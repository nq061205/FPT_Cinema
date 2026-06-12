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
