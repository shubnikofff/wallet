create table if not exists public.player
(
    id              uuid                       not null,
    username        varchar(255) unique        not null,
    balance_version int            default 1   not null,
    balance         numeric(10, 2) default 0.0 not null,
    constraint pk_player primary key (id)
);

insert into player
values (gen_random_uuid(), 'batman'),
       (gen_random_uuid(), 'robin'),
       (gen_random_uuid(), 'joker'),
       (gen_random_uuid(), 'penguin');

create table if not exists public.transaction
(
    id         uuid                    not null,
    username   varchar(255)            not null,
    direction  varchar(255)            not null,
    amount     numeric(10, 2)          not null,
    created_at timestamp default now() not null,
    constraint pk_transaction primary key (id)
);
