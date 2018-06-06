CREATE SEQUENCE UserSequence;
CREATE TABLE "User" (
  id integer PRIMARY KEY DEFAULT NEXTVAL('UserSequence'),
  login varchar(64),
  password varchar(128)
);

CREATE SEQUENCE RoleSequence;
CREATE TABLE Role (
  id integer PRIMARY KEY DEFAULT NEXTVAL('RoleSequence'),
  name varchar(64)
);

CREATE SEQUENCE UserRoleSequence;
CREATE TABLE UserRole (
  id integer PRIMARY KEY DEFAULT NEXTVAL('UserRoleSequence'),
  userId integer,
  roleId integer,
  FOREIGN KEY (userId) REFERENCES "User",
  FOREIGN KEY (roleId) REFERENCES Role
);

-- using salt length 8 to be compatible with spring security
-- spring security omits the $iterations$ part, but that can easily be fixed with a database migration
-- ./digest.sh -a PBKDF2WithHmacSHA256 -e UTF-8 -i 185000 -s 8 -k 256 -h org.apache.catalina.realm.SecretKeyCredentialHandler 123456
-- password is 123456
INSERT INTO "User" (login, password) VALUES ('admin', '81738dfb6925bc48$185000$4cba33c06dd709697f483c7fcc31b61ed64ed36d1287acb56056d11f26bc130d');
INSERT INTO Role (name) VALUES ('admin');
INSERT INTO UserRole (userId, roleId) VALUES (
  (SELECT id FROM "User" WHERE login = 'admin'),
  (SELECT id FROM Role WHERE name = 'admin')
);
