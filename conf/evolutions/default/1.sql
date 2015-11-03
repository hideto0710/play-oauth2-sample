# --- !Ups

create table "account" (
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY,
  "name" VARCHAR(100) NOT NULL,
  "email" VARCHAR(500) NOT NULL,
  "password" VARCHAR(100) NOT NULL,
  "created_at" DATETIME NOT NULL
);

create table "oauth_client"
(
  "id" BIGINT AUTO_INCREMENT PRIMARY KEY,
  "owner_id" BIGINT NOT NULL,
  "grant_type" VARCHAR(20) NOT NULL,
  "client_id" VARCHAR(100) NOT NULL,
  "client_secret" VARCHAR(100) NOT NULL,
  "redirect_uri" VARCHAR(2000),
  "created_at" TIMESTAMP NOT NULL
);

# --- !Downs

drop table "account";
drop table "oauth_client";