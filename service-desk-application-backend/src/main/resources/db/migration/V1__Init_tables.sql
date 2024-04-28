create table users
(
    user_id uuid not null unique,
    name varchar(500) not null,
    surname varchar(500) not null,
    mail varchar(1000) not null unique,
    phone_number varchar(30),
    password varchar(2000) not null,
    account_is_active boolean not null,
    access_as_employee_is_permitted boolean not null,
    user_is_administrator boolean not null,
    account_created_at timestamp(6) not null,
    last_password_update timestamp(6) not null,
    primary key (user_id)
);
