create table if not exists user_data
(
    chat_id        serial primary key,
    first_name    varchar(50) not null,
    last_name     varchar(50) not null,
    user_name     varchar(50) not null,
    registered_at timestamp   not null
);

create table if not exists notifier
(
    id  serial primary key,
    notification varchar(100) not null
);

insert into notifier(id, notification)
values (1, 'It''s time to do the exercises! You''ve been looking at the screen for 1 hour');
