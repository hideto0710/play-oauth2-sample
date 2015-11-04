# play-oauth2-sample
The OAuth 2.0 server-side implementation sample with Play Framework.

## TODOs

- [x] Client Credentials Grant
- [x] Resource Owner Password Credentials Grant
- [x] Authorization Code Grant
- [ ] Add Refresh Token.
- [ ] Add scope to OAuthAuthorizationCode.
- [ ] Add sample view.

## Prepare Data
[preinsert.md]("./preinsert.,d")

## POST /oauth/access_token
### Client Credentials

```
{
  "client_id": "bob_client_id",
  "client_secret": "bob_client_secret",
  "grant_type": "client_credentials"
}
```

### Authorization Code Grant

```
{
  "client_id": "alice_client_id",
  "client_secret": "alice_client_secret",
  "redirect_uri": "http://localhost:3000/callback",
  "code": "bob_code",
  "grant_type": "authorization_code"
}
```

### Password

```
{
  "client_id": "bob_client_id",
  "client_secret": "bob_client_secret",
  "username": "bob@gmail.com",
  "password": "password",
  "grant_type": "password"
}
```