alter table users
    add role varchar(255) default 'USER' not null;