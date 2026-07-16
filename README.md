# FPT_Cinema

## JWT authentication

Login:

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "manager@cinema.test",
  "password": "your-password"
}
```

Call protected APIs with the returned access token:

```http
Authorization: Bearer <accessToken>
```

`GET /api/auth/me` returns the authenticated user's role and effective
permissions.

`GET /api/user` returns the user list and is restricted to the `ADMIN` and
`MANAGER` roles. The response never includes password hashes.

Effective permissions are calculated from `role_permissions`, then overridden
by `user_permissions`. `is_granted = true` adds a permission and
`is_granted = false` removes it.

All endpoints except `/api/auth/login` and `/api/user/create` require a valid
JWT. Use method security for fine-grained authorization:

```java
@PreAuthorize("hasAuthority('MOVIE_UPDATE')")
@PreAuthorize("hasRole('ADMIN')")
```

For local development, the application generates a temporary JWT key when
`JWT_SECRET` is missing. Tokens then become invalid whenever the application
restarts.

Set a stable production secret before starting the application:

```powershell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

## Gemini chatbot integration

Create an API key in Google AI Studio, then configure it as an environment variable before starting the backend:

```powershell
$env:GEMINI_API_KEY="<your-gemini-api-key>"
```

Optional model override (the default is `gemini-3.5-flash`):

```powershell
$env:GEMINI_API_MODEL="gemini-3.5-flash"
```

The backend sends the conversation history and current movie data from the database to Gemini. The API key stays on the server and is never sent to the frontend.

## Initial catalog data

The repeatable catalog seed is in [`docs/seed-initial-data.sql`](docs/seed-initial-data.sql). It fills the main room seat maps, adds combo products, assigns active promotions to customers, and creates a seven-day showtime slate for currently showing movies. It intentionally does not create fake bookings, tickets, payments, or reviews.

Run it once against the configured MySQL database, then restart the backend so showtime end-status jobs are registered.

Chat endpoints require a valid JWT:

```http
POST /api/chat/conversations
POST /api/chat/conversations/{conversationId}/messages
GET /api/chat/conversations/{conversationId}/messages
PUT /api/chat/conversations/{conversationId}/close
```
