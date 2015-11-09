# --- !Ups
create table "account" (
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  "email" VARCHAR(500) NOT NULL,
  "password" VARCHAR(100) NOT NULL,
  "created_at" DATETIME NOT NULL,
  CONSTRAINT account_mail_address_key unique ("email")
);

create table "oauth_client"
(
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY,
  "owner_id" BIGINT NOT NULL,
  "grant_type" VARCHAR(20) NOT NULL,
  "client_id" VARCHAR(100) NOT NULL,
  "client_secret" VARCHAR(100) NOT NULL,
  "scope" VARCHAR(2000),
  "redirect_uri" VARCHAR(2000),
  "created_at" TIMESTAMP NOT NULL,
  CONSTRAINT oauth_client_owner_id_fkey foreign key ("owner_id")
    REFERENCES "account" ("id") ON DELETE CASCADE,
  CONSTRAINT oauth_client_client_id_key unique ("client_id")
);

create table "oauth_access_token"
(
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY,
  "account_id" BIGINT NOT NULL,
  "oauth_client_id" BIGINT NOT NULL,
  "access_token" VARCHAR(100) NOT NULL,
  "refresh_token" VARCHAR(100) NOT NULL,
  "created_at" TIMESTAMP NOT NULL,
  CONSTRAINT oauth_access_token_account_id_fkey FOREIGN KEY ("account_id")
    REFERENCES "account" ("id") ON DELETE CASCADE,
  CONSTRAINT oauth_access_token_oauth_client_id_fkey FOREIGN KEY ("oauth_client_id")
    REFERENCES "oauth_client" ("id") ON DELETE CASCADE
);

create table "oauth_authorization_code"
(
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY,
  "account_id" BIGINT NOT NULL,
  "oauth_client_id" BIGINT NOT NULL,
  "code" VARCHAR(100) NOT NULL,
  "redirect_uri" VARCHAR(2000) NOT NULL,
  "created_at" TIMESTAMP NOT NULL,
  CONSTRAINT oauth_authorization_code_account_id_fkey FOREIGN KEY ("account_id")
    REFERENCES "account" ("id") ON DELETE CASCADE,
  CONSTRAINT oauth_authorization_code_oauth_client_id_fkey FOREIGN KEY ("oauth_client_id")
    REFERENCES "oauth_client" ("id") ON DELETE CASCADE
);


# --- !Downs

drop table "account";
drop table "oauth_client";
drop table "oauth_access_token";
drop table "oauth_authorization_code";