# --- !Ups

create table "User" ("id" BIGINT AUTO_INCREMENT, "name" VARCHAR NOT NULL);

# --- !Downs

drop table "User";