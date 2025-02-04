create table wish_table(

wish_id number(8) primary key,
product_id number(8) not null,
owner_name varchar2(100),
status varchar2(100),
constraint product_id_fk foreign key (product_id) REFERENCES product_table(product_id),
constraint user_wish_fk foreign key (owner_name) references users(username)
);


CREATE SEQUENCE wish_id_seq
START WITH 1
INCREMENT BY 1
NOCYCLE;


CREATE OR REPLACE TRIGGER wish_id_trigger
BEFORE INSERT ON wish_table
FOR EACH ROW
BEGIN
    :NEW.wish_id := wish_id_seq.NEXTVAL;
END;