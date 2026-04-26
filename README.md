## Queryable Event-Sourced Realtime Communication Platform
`Spring Boot` `React (Vite)` `WebSockets` `Apache Kafka` `Redis` `Elasticsearch` `MySQL` `MongoDB`

- Built bidirectional real-time chat with presence, typing indicators, delivery states, infinite scroll
- Implemented event sourcing with append-only event log for time-travel reconstruction and audit trails
- Published domain events to Kafka; independent consumers for search indexing, task extraction, analytics
- Used Redis for presence tracking, JWT blacklisting, message cache, and token bucket rate limiting
- Indexed messages into Elasticsearch via Kafka for fuzzy search, user/date filtering, file/link search
- Built `@task` pattern parser converting messages into structured tasks with assignees and due dates
- Built LLM-powered features using OpenAI APIs integrated with Spring Boot and React
- Implemented prompt engineering and response handling for AI-assisted workflows
- Explored RAG with vector search for contextual responses within conversation history
