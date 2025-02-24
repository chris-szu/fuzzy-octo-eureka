package com.example.daemon.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PreDestroy;

@Service
public class DaemonService {
    private static final Logger logger = LoggerFactory.getLogger(DaemonService.class);

    @Value("${daemon.processing.interval-ms:1000}")
    private long processingInterval;

    private volatile boolean isRunning = false;
    private Thread workerThread;

    public synchronized boolean startDaemon() {
        if (isRunning) {
            logger.info("Daemon is already running");
            return false;
        }

        isRunning = true;
        workerThread = new Thread(this::processLoop);
        workerThread.setName("Daemon-Worker");
        workerThread.start();
        logger.info("Daemon service started");
        return true;
    }

    public synchronized boolean stopDaemon() {
        if (!isRunning) {
            logger.info("Daemon is not running");
            return false;
        }

        logger.info("Stopping daemon service...");
        isRunning = false;
        if (workerThread != null) {
            workerThread.interrupt();
            try {
                workerThread.join(5000); // Wait up to 5 seconds for clean shutdown
                logger.info("Daemon service stopped");
                return true;
            } catch (InterruptedException e) {
                logger.warn("Shutdown interrupted", e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void processLoop() {
        while (isRunning) {
            try {
                // Example processing task
                processSomething();

                Thread.sleep(processingInterval);
            } catch (InterruptedException e) {
                logger.info("Daemon thread interrupted", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error in daemon process", e);
            }
        }
    }

    private void processSomething() {
        // Add your actual processing logic here
        logger.info("Processing task at: {}", System.currentTimeMillis());
    }

    @PreDestroy
    public void shutdown() {
        stopDaemon();
    }
}
