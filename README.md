# fuzzy-octo-eureka
offsite test

# System Requirements
1. Provide API for requesting downloads, observing progress, and fetching data
2. Build a daemon process to continuously fetch questions and answers from StackOverflow API

# API
API is acting as a controller for the background daemon process, possible downloadable targets are predefined on server-side, could be some sort of config (omitted for the offsite test)

All endpoints should be authenticated and rate limited (omitted for the offsite test)

```
POST /api/v1/downloads - start/stop download
request (body)
{
    "action": "start" | "stop",
    "download_id": "1234", // stop only
}
response
{
    "action": "start",
    "result": "starting" | "already started" | "error",
    "download_id": "1234",
    "detail": {},
}
```

```
GET /api/v1/progress?download_id - fetch progress by id
request (query param)
{
    "download_id": "1234",
}
response
{
    "download_id": "1234",
    "progress": 0.5,
    "status": "init" | "running" | "completed" | "error",
    "detail": {},
}
```

```
GET /api/v1/data?download_id - fetch data by id
request (query param)
{
    "download_id": "1234",
}
response
{
    "download_id": "1234",
    "status": "init" | "running" | "completed" | "error",
    "detail": {},
    "data": [ // empty array for "init" and "error"
        {
            "question_id": 1234,
            "question_title": "How to do something?",
            "question_body": "I want to do something, how can I do it?",
            "answers": [
                {
                    "answer_id": 1234,
                    "answer_body": "You can do it by doing this...",
                },
                ...
            ],
        },
        ...
    ],
}
```

# Daemon process

