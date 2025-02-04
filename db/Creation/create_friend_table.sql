create table friend(
    Username varchar2(50) not null ,
    Friendname varchar2(50) not null,
    
    constraint pk_create_friend primary key (Username, Friendname),
    constraint fk_friend_user1 foreign key  (Username) references Users(Username),
    constraint fk_friend_user2 foreign key  (Friendname) references Users(Username)
);


