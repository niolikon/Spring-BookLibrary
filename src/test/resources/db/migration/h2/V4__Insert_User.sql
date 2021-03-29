-- --------------------------------------------------------
-- Data for table role
-- --------------------------------------------------------

INSERT INTO role (id, name) 
VALUES 
(1, 'admin');

INSERT INTO role (id, name) 
VALUES 
(2, 'user');

-- --------------------------------------------------------
-- Data for table enduser
-- --------------------------------------------------------

INSERT INTO enduser (id, name, surname, username, password) 
VALUES 
(1, 'Admin', 'Admin', 'admin', 'admin');

INSERT INTO enduser (id, name, surname, username, password) 
VALUES 
(2, 'Simone', 'Muscas', 'simone', 'simone');

-- --------------------------------------------------------
-- Data for table enduserrole
-- --------------------------------------------------------

INSERT INTO enduserrole (id, enduser_id, role_id) 
VALUES 
(1, 1, 1);

INSERT INTO enduserrole (id, enduser_id, role_id) 
VALUES 
(2, 1, 2);

INSERT INTO enduserrole (id, enduser_id, role_id) 
VALUES 
(3, 2, 2);
