
create table Friend_requests (
    Username varchar2(50) not null ,
    Friendname varchar2(50) not null,
    
    constraint pk_friend_requests primary key (Username, Friendname),
    constraint fk_req_user1 foreign key  (Username) references Users(Username),
    constraint fk_req_user2 foreign key  (Friendname) references Users(Username)
);
