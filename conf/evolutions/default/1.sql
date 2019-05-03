# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                            bigint auto_increment not null,
  name                          varchar(255),
  balance                       decimal(15,10),
  currency_code                 varchar(3),
  active                        boolean,
  constraint pk_account primary key (id)
);

create table transfer (
  request_id                    uuid not null,
  source_account_id             bigint,
  target_account_id             bigint,
  amount                        decimal(15,10),
  transfer_date                 timestamp,
  constraint pk_transfer primary key (request_id)
);


# --- !Downs

drop table if exists account;

drop table if exists transfer;

