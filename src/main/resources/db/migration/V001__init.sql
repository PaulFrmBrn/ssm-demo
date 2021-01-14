create table orders
(
    id      serial primary key,
    status  text not null,
    comment text not null
);