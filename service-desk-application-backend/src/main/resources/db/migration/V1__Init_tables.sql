create table users
(
    id uuid not null unique,
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
    primary key (id)
);

create table support_groups
(
    id uuid not null unique,
    name varchar(300) not null unique,
    description varchar(2000) not null,
    group_type varchar(100) not null,
    group_is_active boolean not null,
    manager_id uuid,
    primary key (id),
    foreign key (manager_id) references users(id)
);

create table group_members
(
    group_id uuid not null,
    user_id uuid not null,
    foreign key (group_id) references support_groups(id),
    foreign key (user_id) references users(id)
);

create table tickets_categories
(
    id uuid not null unique,
    name varchar(500),
    description varchar(2000),
    category_is_active boolean not null,
    default_group uuid not null,
    primary key (id),
    foreign key (default_group) references support_groups(id)
);

create table tickets
(
    id uuid not null unique,
    type varchar(100) not null,
    description varchar(3000) not null,
    customer uuid not null,
    reporter uuid not null,
    assignee_analyst uuid,
    support_group uuid not null,
    open_date timestamp(6) not null,
    resolve_date timestamp(6),
    close_date timestamp(6),
    category uuid not null,
    status varchar(100) not null,
    primary key (id),
    foreign key (customer) references users(id),
    foreign key (reporter) references users(id),
    foreign key (assignee_analyst) references users(id),
    foreign key (support_group) references support_groups(id),
    foreign key (category) references tickets_categories(id)
);

create table tickets_activities
(
    id uuid not null unique,
    ticket_activity_type varchar(100) not null,
    description varchar(2000),
    activity_date timestamp(6),
    ticket uuid not null,
    analyst uuid,
    primary key (id),
    foreign key (ticket) references tickets(id),
    foreign key (analyst) references users(id)
);
