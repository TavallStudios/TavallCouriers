# Tavall Couriers Java Tools Contract

Tavall Couriers is a Tavall-owned Java consumer. Tavall DI is the universal first-party composition/lifecycle baseline across Gemini, internal API, and Spring web modules.

The persistence-facing modules consume Tavall Database Postgres as the canonical shared database/JPA infrastructure boundary. The Spring runtime consumes Tavall Logging and Tavall Concurrency for application diagnostics and asynchronous work.

Spring remains the HTTP/security/view framework; Gemini, PDFBox, and ZXing remain provider/domain libraries. Existing Spring JDBC/JPA, HikariCP, and direct PostgreSQL infrastructure are migration seams where they duplicate Tavall Database mechanics. Courier domain models/repositories remain product-owned.

Use Tavall Registry, Cache, EventBus, Reflection, and Scheduler when those concerns exist rather than introducing project-local equivalents.

Do not add first-party ServiceLoader composition, service locators, executor frameworks, logging wrappers, registry/cache/event frameworks, reflection scanners, scheduled executors, or database infrastructure when a Tavall tool owns the concern.

Exact Java 25 verification, dependency-lock refresh, PostgreSQL integration tests, AI/provider tests, and Spring runtime acceptance are required before promotion.