
## Account

```
curl -X "POST" "http://localhost:9000/accounts" \
	-H "Content-Type: application/json" \
	-d "{\"name\":\"bob\",\"email\":\"bob@example.com\",\"password\":\"password\"}"
```

```
curl -X "POST" "http://localhost:9000/accounts" \
	-H "Content-Type: application/json" \
	-d "{\"name\":\"alice\",\"email\":\"alice@example.com\",\"password\":\"password\"}"
```

## Client 

```
curl -X "POST" "http://localhost:9000/oauth/client" \
	-H "Content-Type: application/json" \
	-d "{\"owner_id\":1,\"grant_type\":\"client_credentials\",\"client_id\":\"bob_client_id\",\"client_secret\":\"bob_client_secret\"}"
```

```
curl -X "POST" "http://localhost:9000/oauth/client" \
	-H "Content-Type: application/json" \
	-d "{\"owner_id\":2,\"grant_type\":\"authorization_code\",\"client_id\":\"alice_client_id\",\"client_secret\":\"alice_client_secret\",\"redirect_uri\":\"http://localhost:3000/callback\"}"
```

```
curl -X "POST" "http://localhost:9000/oauth/client" \
	-H "Content-Type: application/json" \
	-d "{\"owner_id\":2,\"grant_type\":\"password\",\"client_id\":\"alice_client_id2\",\"client_secret\":\"alice_client_secret2\"}"
```

## Authorization Code
```
curl -X "POST" "http://localhost:9000/oauth/code" \
	-H "Content-Type: application/json" \
	-d "{\"account_id\":1,\"oauth_client_id\":2,\"code\":\"bob_code\",\"redirect_uri\":\"http://localhost:3000/callback\"}"
```