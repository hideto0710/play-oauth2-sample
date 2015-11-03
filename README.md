# play-oauth2-sample
The OAuth 2.0 server-side implementation sample with Play Framework.

## Client credentials

### Request

```
{
  "client_id": "bob_client_id",
  "client_secret": "bob_client_secret",
  "grant_type": "client_credentials"
}
```

### OAuth Flow

1. validateClient(...): Future[Boolean]
1. findClientUser(...): Future[Option[Account]]
1. getStoredAccessToken(...): Future[Option[AccessToken]]
1. createAccessToken(...): Future[AccessToken]

## Password

### Request

```
{
  "client_id": "bob_client_id",
  "client_secret": "bob_client_secret",
  "username": "bob@gmail.com",
  "password": "password",
  "grant_type": "password"
}
```

### OAuth Flow

1. validateClient(...): Future[Boolean]
1. findUser(...): : Future[Option[Account]]
1. getStoredAccessToken(...): Future[Option[AccessToken]]
1. createAccessToken(...): Future[AccessToken]