create table contributions (
    Contributer_name varchar2(50) not null ,
    wish_id number(8) not null,
    amount number(8,3) not null,
    constraint wish_contribute_pk primary key (Contributer_name, wish_id),
    constraint Contributer_name_fk foreign key  (Contributer_name) references Users(Username),
    constraint wish_id_fk foreign key (wish_id) references wish_table(wish_id)
);
