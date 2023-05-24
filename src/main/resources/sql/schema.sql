create table "user"
(
    id       serial
        primary key,
    token    varchar(24) not null
        unique,
    telegram bigint
        unique
);

alter table "user"
    owner to postgres;

create table "limit"
(
    id         serial
        primary key,
    url        varchar(200) not null,
    limit_time bigint       not null,
    user_id    integer      not null
        references "user"
);

alter table "limit"
    owner to postgres;
