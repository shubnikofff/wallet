create table if not exists public.player
(
    username        varchar(255)               not null,
    balance_version int            default 1   not null,
    balance         numeric(10, 2) default 0.0 not null,
    constraint pk_player primary key (username)
);

insert into player
values ('batman'),
       ('robin'),
       ('joker'),
       ('penguin');

create table if not exists public.transaction
(
    id         uuid                    not null,
    username   varchar(255)            not null,
    direction  varchar(255)            not null,
    amount     numeric(10, 2)          not null,
    created_at timestamp default now() not null,
    constraint pk_transaction primary key (id)
);
