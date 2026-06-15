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

## n8n chatbot integration

Configure the published n8n webhook before starting the backend:

```powershell
$env:N8N_CHAT_WEBHOOK_URL="https://duyhoang.app.n8n.cloud/webhook/chat-api"
$env:N8N_CHAT_WEBHOOK_SECRET="<your-new-webhook-secret>"
```

The n8n workflow must return:

```json
{
  "answer": "Chatbot response",
  "intent": "GENERAL_CHAT"
}
```

The `Basic LLM Chain` prompt must use the movie context sent by the backend:

```text
You are the FPT Cinema assistant.
Answer in Vietnamese using only the supplied system data.
For a movie-list question, include movies whose status is NOW_SHOWING.
If the data does not contain the answer, say that the information is unavailable.

User question:
{{ $json.body.message }}

Movie data:
{{ JSON.stringify($json.body.context.movies) }}
```

Chat endpoints require a valid JWT:

```http
POST /api/chat/conversations
POST /api/chat/conversations/{conversationId}/messages
GET /api/chat/conversations/{conversationId}/messages
PUT /api/chat/conversations/{conversationId}/close
```
