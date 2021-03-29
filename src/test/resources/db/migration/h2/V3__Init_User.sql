--
-- Database: booklibrary
--

DROP TABLE role IF EXISTS;
DROP TABLE user IF EXISTS;
DROP TABLE userrole IF EXISTS;

-- --------------------------------------------------------
-- Structure of table role
-- --------------------------------------------------------

CREATE TABLE role (
  id 		INTEGER IDENTITY PRIMARY KEY,
  name 		VARCHAR(20)
);
CREATE INDEX role_name ON role (name);

ALTER TABLE role ADD CONSTRAINT role_unicity
UNIQUE(name);

-- --------------------------------------------------------
-- Structure of table user
-- --------------------------------------------------------

CREATE TABLE enduser (
  id 		INTEGER IDENTITY PRIMARY KEY,
  name 		VARCHAR(50),
  surname 	VARCHAR(50),
  username 	VARCHAR(20),
  password 	VARCHAR(200)
);
CREATE INDEX user_fullname ON enduser (name, surname);
CREATE INDEX user_login ON enduser (username, password);

ALTER TABLE enduser ADD CONSTRAINT enduser_unicity
UNIQUE(username);

-- --------------------------------------------------------
-- Structure of table enduserrole
-- --------------------------------------------------------

CREATE TABLE enduserrole (
  id 			INTEGER IDENTITY PRIMARY KEY,
  enduser_id 	INTEGER NOT NULL,
  role_id 		INTEGER NOT NULL
);

ALTER TABLE enduserrole ADD CONSTRAINT enduserrole_user_fk 
FOREIGN KEY (enduser_id) REFERENCES enduser (id) 
ON DELETE CASCADE;

ALTER TABLE enduserrole ADD CONSTRAINT enduserrole_role_fk 
FOREIGN KEY (role_id) REFERENCES role (id)
ON DELETE CASCADE;

ALTER TABLE enduserrole ADD CONSTRAINT enduserrole_unicity
UNIQUE(enduser_id,role_id);
