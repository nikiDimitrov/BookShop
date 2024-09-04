create table if not exists books
(
    id       uuid not null primary key,
    author   varchar(255),
    name     varchar(255),
    price    double precision not null,
    quantity integer          not null,
    year     integer          not null
);

create table if not exists categories
(
    id   uuid not null primary key,
    name varchar(255)
);

create table if not exists  books_categories
(
    book_id uuid not null
        constraint fkmsuoucvyyccli3f6u59co41cq
            references books,
    categories_id uuid not null
        constraint fkass1i2uq846riwn5tihbgsi0o
            references categories
);

create table if not exists users
(
    id       uuid  not null primary key,
    email    varchar(255),
    password varchar(255),
    role     varchar(255)
        constraint users_role_check
            check ((role)::text = ANY
                   ((ARRAY ['ADMIN'::character varying, 'CLIENT'::character varying, 'EMPLOYEE'::character varying])::text[])),
    username varchar(255) not null
);

create table if not exists orders
(
    id          uuid  not null primary key,
    status      varchar(255) not null,
    total_price double precision not null,
    user_id     uuid not null
        constraint fk32ql8ubntj5uh44ph9659tiih
            references users
);

create table if not exists order_items
(
    id       uuid not null primary key,
    quantity integer not null,
    book_id  uuid
        constraint fki4ptndslo2pyfp9r1x0eulh9g
            references books,
    order_id uuid
        constraint fkbioxgbv59vetrxe0ejfubep1w
            references orders
);
;

