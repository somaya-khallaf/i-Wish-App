create table users (
    username varchar2(50) primary key,
    full_name varchar2(100),
    password varchar2(100) not null,
    gender varchar2(10),
    phone varchar2(15),
    balance number(10,2),
    dob date
);