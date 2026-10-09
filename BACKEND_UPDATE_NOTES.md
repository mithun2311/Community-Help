# Community Help Backend Integration Update

This update builds on the supplied local backend and preserves the existing Spring Boot package structure. RAG and frontend implementation remain separate.

## Integrated in this update

- Safer JWT parsing and stateless Spring Security with configurable CORS origins.
- Password hashing, normalized email addresses, generic invalid-login responses, validation, and conflict responses.
- Bootstrap administrator configuration through `ADMIN_EMAILS`; the configured account receives the administrator role on registration or login.
- Individual/NGO account type and a verification workflow: users submit a verification note; administrators approve or reject it.
- Helper profile with skills/categories, availability, and maximum service radius.
- Explainable request matching filtered by verification, helper availability, skill/category, approximate distance, expiry, urgency, and freshness.
- Help-request acceptance uses a database row lock and checks helper verification, availability, category skills, and approximate distance against the helper's configured radius.
- Idempotency-Key support for help-request creation, automatic expiry of open requests, and expiry checks during listing/acceptance.
- Help-request lifecycle with the opposite participant required to confirm a completion request.
- Approximate location before acceptance and exact location for authorized participants after acceptance; live tracking is stopped for terminal states.
- REST chat plus STOMP WebSocket chat and live tracking updates at `/ws`; JWT is checked at STOMP CONNECT and chat/tracking subscriptions are limited to request participants.
- Safety check-ins, scheduled timeout escalation, trusted contacts, SOS deduplication, and optional email notifications after transaction commit.
- Request image URL metadata and persisted tags with ownership/state restrictions; actual file upload/storage is still a separate integration.
- Report moderation endpoints for administrators and DTO responses for chat messages, chats, reports, ratings, profiles, and trust summaries to reduce accidental entity-data exposure.
- Per-help-request SHA-256 hash-chained audit trail and verification endpoint.
- H2 test profile for context tests.

## Environment configuration

Required for the normal PostgreSQL profile:

- `DB_PASSWORD`: Neon/PostgreSQL password.
- `JWT_SECRET`: Base64-encoded key material of at least 32 bytes.
- `ADMIN_EMAILS`: comma-separated email addresses that are allowed to become administrators. Set this before registering/logging in to those accounts.

Optional email notifications:

- `EMAIL_NOTIFICATIONS_ENABLED=true`
- `ALERT_EMAIL_RECIPIENT=...`
- `MAIL_HOST=...`
- `MAIL_PORT=587`
- `MAIL_USERNAME=...`
- `MAIL_PASSWORD=...`

Email notification delivery is disabled by default. Trusted contacts can have an optional email address; phone numbers are stored, but SMS delivery requires an SMS provider and is not implemented in this package. Configure `app.cors.allowed-origins` for the actual frontend origin(s) before deployment.

## Important API contract changes

- `PUT /api/help-requests/{id}/accept` now requires JSON containing the helper's current `latitude` and `longitude`, so the server can enforce the configured service radius.
- `POST /api/auth/register` optionally accepts `accountType` (`INDIVIDUAL` or `NGO`) and `organizationName` for NGO accounts.
- New verification endpoints: `GET /api/profile/verification`, `POST /api/profile/verification`.
- New helper profile endpoints: `GET /api/profile/helper`, `PUT /api/profile/helper`.
- New admin endpoints: `GET /api/admin/users`, `PUT /api/admin/users/{id}/verification?status=VERIFIED|REJECTED`, `GET /api/admin/reports`, `PUT /api/admin/reports/{id}/status`.
- WebSocket endpoint: `/ws`; send a STOMP CONNECT native header `Authorization: Bearer <JWT>`, subscribe to `/topic/chats/{chatId}`, and send to `/app/chats/{chatId}/send`. Assigned request participants may subscribe to `/topic/requests/{requestId}/tracking` for live helper location updates.

## Build and validation status

The source was statically checked for duplicate top-level class names, balanced delimiters, and valid `pom.xml` XML. Maven could not be run in this environment because the Maven Wrapper could not download the configured Maven distribution. These checks do **not** prove that the Java project compiles or that runtime integration works. Run `.[0m` `mvnw.cmd test` on the development machine before replacing the existing backend.

## Still outstanding before calling the frozen project scope fully complete

- Actual request image upload/storage (this update stores validated HTTPS image URLs only).
- More complete organization/NGO verification evidence and audit history.
- SMS delivery through a configured provider.
- Retention cleanup for raw tracking history and notification history.
- Comprehensive API, security, and high-contention acceptance tests, including the planned parallel-acceptance test.
- End-to-end tests of the WebSocket JWT handshake, chat subscription authorization, SMTP configuration, and scheduled escalation.
- RAG remains separate by design.
