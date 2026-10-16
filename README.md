# Audiobook Discovery & Listening Platform

A full-stack local demo using **Java 21, Spring Boot 3, React/Vite, Redis cache-aside, and best-effort Kafka playback-progress events**.

## Features
- REST API for catalog search, details, and listening-progress update/retrieval
- Synthetic audiobook catalog (10,000 by default; configurable to 100,000 records)
- In-memory inverted index for exact-token search
- Redis query caching with a five-minute TTL and local-index fallback
- In-memory playback progress with sequence-number protection against stale updates
- React search interface and browser audio player

**Scope:** Original local demonstration. Synthetic titles are not licensed audiobooks. The original local archive contains 256 short, distinct, synthesized MP3 title previews, which are not included in this source-only GitHub upload; the UI will therefore require locally copied audio files for preview playback. The application does not provide complete narrated books or durable progress. Kafka publishing is best-effort.

## Run locally
Prerequisites: JDK 21, Maven 3.9+, Node.js 20+, npm, and Docker Desktop.

```bash
docker compose up -d
cd backend
mvn spring-boot:run
```

In a second terminal:
```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173; API health: http://localhost:8080/api/health.

## Testing and benchmark scope
`benchmark/DomainTests.java` contains 12 standalone Java assertions. `benchmark/Benchmark.java` compares local Java inverted-index versus linear search, **not** API/Redis/Kafka throughput. The previously recorded benchmark found index median slower than linear scan for its mixed synthetic workload; no unmeasured scalability claims are made.

## Limitations
No user authentication, durable progress store, Kafka consumer, production audio service, or complete audiobook chapters. Integrations should be validated separately against running infrastructure.
