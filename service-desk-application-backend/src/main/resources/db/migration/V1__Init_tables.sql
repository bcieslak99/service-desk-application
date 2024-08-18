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

insert into support_groups (id, name, description, group_type, group_is_active, manager_id)
VALUES
    ('1a396d8e-3777-4f36-9d51-1aeb03350133', 'Service Desk', 'Pierwsza linia wsparcia - Service Desk.', 'FIRST_LINE', true, null),
    ('8e16b7ce-276b-4eee-a8e3-ec2caf416e9c', 'AD-ADM', 'Grupa odpowiedzialna za Active Directory.', 'SECOND_LINE', true, null),
    ('f84267a2-40ff-4b4b-b0b7-92b811b1a936', 'LAN/WAN/VPN-ADM', 'Grupa odpowiedzialna za działanie sieci.', 'SECOND_LINE', true, null),
    ('5bc2eb58-e2a1-4178-a440-7e52eaa07249', 'Poczta-ADM', 'Grupa odpowiedzialna za działanie skrzynek mailowych.', 'SECOND_LINE', true, null),
    ('624efe20-bc19-4383-b514-a9706dba47e0', 'Lokalne Wsparcie', 'Grupa wsparcia bezpośredniego.', 'SECOND_LINE', true, null),
    ('e2cfa830-4211-42ae-a101-e33f650507dd', 'Bezpieczeństwo IT', 'Grupa odpowiedzialna za bezpieczeństwo IT.', 'SECOND_LINE', true, null);

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
    ticket_type varchar(100) not null,
    primary key (id),
    foreign key (default_group) references support_groups(id)
);

insert into tickets_categories (id, name, description, category_is_active, default_group, ticket_type)
VALUES
    ('16efd80b-0da5-4481-a55c-2caa39b18884', 'Nieprawidłowe działanie VPN', 'Kategoria obejmuje problemy z logowaniem, spowolnionym działaniem itp.', true, 'f84267a2-40ff-4b4b-b0b7-92b811b1a936', 'INCIDENT'),
    ('b91244cc-29f0-492b-8b27-02f1083af5ab', 'Poczta - problem z odbieraniem/wysyłaniem wiadomości', 'Kategoria dotyczy problemów z odbieraniem i wysyłaniem wiadomości.', true, '5bc2eb58-e2a1-4178-a440-7e52eaa07249', 'INCIDENT'),
    ('04221533-e375-41c8-b2ca-6bf020300ce6', 'Podłączenie urządzenia wielofunkcyjnego.', 'Wniosek o usługę - podłączenie urządzenia wielofunkcyjnego.', true, '624efe20-bc19-4383-b514-a9706dba47e0', 'SERVICE_REQUEST');

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
    user_can_see boolean not null,
    primary key (id),
    foreign key (ticket) references tickets(id),
    foreign key (analyst) references users(id)
);

create table notes
(
    id uuid not null unique,
    title varchar(500) not null,
    description varchar(3000) not null,
    created_at timestamp(6) not null,
    last_update_at timestamp(6) not null,
    is_deleted boolean not null,
    owner uuid not null,
    primary key (id),
    foreign key (owner) references users(id)
);

create table task_sets
(
    id uuid not null unique,
    title varchar(200) not null,
    created_at timestamp(6) not null,
    last_activity timestamp(6) not null,
    planned_end_date timestamp(6) not null,
    real_end_date timestamp(6),
    last_notification timestamp(6),
    support_group uuid not null,
    primary key (id),
    foreign key (support_group) references support_groups(id)

);

create table tasks
(
    id uuid not null unique,
    description varchar(2000) not null,
    done boolean not null,
    position int not null,
    tasks_set uuid not null,
    primary key (id),
    foreign key (tasks_set) references task_sets(id)
);

create table task_comments
(
    id uuid not null unique,
    comment varchar(3000) not null,
    created_at timestamp(6) not null,
    author uuid not null,
    task_set uuid not null,
    primary key (id),
    foreign key (author) references users(id),
    foreign key (task_set) references task_sets(id)
);

create table attachments
(
    id uuid not null unique,
    name varchar(300) not null,
    ticket_id uuid not null,
    file_owner uuid not null,
    added_at timestamp(6) not null,
    deleted boolean not null,
    primary key (id),
    foreign key (ticket_id) references tickets(id),
    foreign key (file_owner) references users(id)
)
