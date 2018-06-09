create table users (
  user_name varchar(64) not null,
  user_pass varchar(128) not null,
  constraint users_pk primary key (user_name)
);

create table user_roles (
  user_name varchar(64) not null,
  role_name varchar(64) not null,
  constraint user_roles_pk primary key (user_name, role_name),
  constraint user_roles_users_fk foreign key (user_name) references users (user_name)
);

-- using salt length 8 to be compatible with spring security
-- spring security omits the $iterations$ part, but that can easily be fixed with a database migration
-- ./digest.sh -a PBKDF2WithHmacSHA256 -e UTF-8 -i 185000 -s 8 -k 256 -h org.apache.catalina.realm.SecretKeyCredentialHandler "Correct Horse Battery Staple"
-- credentials = Correct Horse Battery Staple
-- encodedTomcat = 52819f5e13b7509f$185000$7ff1468d6af4a183b0871fe7420d01ce30958b06106690a26f0e3af5cd9e1bc2
-- encodedSpring = 52819f5e13b7509f7ff1468d6af4a183b0871fe7420d01ce30958b06106690a26f0e3af5cd9e1bc2

insert into users (user_name, user_pass)
values ('admin', '52819f5e13b7509f$185000$7ff1468d6af4a183b0871fe7420d01ce30958b06106690a26f0e3af5cd9e1bc2');

insert into user_roles (user_name, role_name)
values ('admin', 'admin');
