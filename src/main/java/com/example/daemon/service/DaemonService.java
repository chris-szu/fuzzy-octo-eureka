package com.example.daemon.service;

import com.example.daemon.dto.StartDaemonResult;
import com.example.daemon.dto.StopDaemonResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class DaemonService {
    private static final Logger logger = LoggerFactory.getLogger(DaemonService.class);

    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final AtomicBoolean isSchedulerEnabled = new AtomicBoolean(false);

    public DaemonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.stackexchange.com/2.3")
                .build();
    }

    // todo make it configurable
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes in milliseconds
    public void scheduledProcessLoop() {
        if (isSchedulerEnabled.get()) {
            try {
                fetchQuestions();
                fetchAnswers();
            } catch (Exception e) {
                logger.error("Error in scheduled process", e);
            }
        }
    }

    public synchronized StartDaemonResult startDaemon() {
        if (isSchedulerEnabled.get()) {
            logger.info("Daemon is already running");
            return StartDaemonResult.ALREADY_STARTED;
        }

        isSchedulerEnabled.set(true);
        logger.info("Daemon service started");
        return StartDaemonResult.STARTING;
    }

    public synchronized StopDaemonResult stopDaemon() {
        if (!isSchedulerEnabled.get()) {
            logger.info("Daemon is not running");
            return StopDaemonResult.ALREADY_STOPPED;
        }

        logger.info("Stopping daemon service...");
        isSchedulerEnabled.set(false);
        logger.info("Daemon service stopped");
        return StopDaemonResult.STOPPING;
    }

    private void fetchQuestions() {
        logger.info("Processing task at: {}", System.currentTimeMillis());
        webClient.get()
                .uri("/questions?site=stackoverflow") // todo should fetch items in the last hour only
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(jsonResponse -> {
                    try {
                        var jsonArray = objectMapper.readTree(jsonResponse).get("items");
                        if (jsonArray.isArray()) {
                            for (var item : jsonArray) {
                                String singleLine = item.toString();
                                // todo catch exception separately and retry later
                                Files.writeString(
                                        Path.of("questions.jsonl"), // todo use a configurable path
                                        singleLine + System.lineSeparator(),
                                        StandardOpenOption.CREATE,
                                        StandardOpenOption.APPEND
                                );
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error processing response", e);
                    }
                });
    }

    private void fetchAnswers() {
        try {
            List<String> questionIds = new ArrayList<>();
            // Read questions from the JSONL file
            Files.readAllLines(Path.of("questions.jsonl")).forEach(line -> {
                try {
                    var questionNode = objectMapper.readTree(line);
                    questionIds.add(questionNode.get("question_id").asText());
                } catch (Exception e) {
                    logger.error("Error parsing question line: {}", line, e);
                }
            });

            // Batch process questions
            for (int i = 0; i < questionIds.size(); i += 100) {
                int end = Math.min(i + 100, questionIds.size());
                String ids = String.join(";", questionIds.subList(i, end));

                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/questions/{ids}/answers")
                                .queryParam("site", "stackoverflow")
                                .queryParam("filter", "withbody") // includes answer body
                                .build(ids))
                        .retrieve()
                        .bodyToMono(String.class)
                        .subscribe(jsonResponse -> {
                            try {
                                var jsonArray = objectMapper.readTree(jsonResponse).get("items");
                                if (jsonArray.isArray()) {
                                    for (var item : jsonArray) {
                                        // Create a new wrapper object
                                        var wrapper = objectMapper.createObjectNode();
                                        wrapper.put("parent_id", item.get("question_id").asText());
                                        wrapper.set("answer", item);

                                        String singleLine = wrapper.toString();
                                        Files.writeString(
                                                Path.of("answers.jsonl"),
                                                singleLine + System.lineSeparator(),
                                                StandardOpenOption.CREATE,
                                                StandardOpenOption.APPEND
                                        );
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("Error processing answers response", e);
                            }
                        });
            }
        } catch (Exception e) {
            logger.error("Error in fetchAnswers", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        stopDaemon();
    }
}
