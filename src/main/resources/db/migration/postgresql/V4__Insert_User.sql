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
(1, 'Admin', 'Admin', 'admin', '$2y$10$1zyH1wPffdquP5Cr0bo9Uem7j9odP1Ur5lRYTp/ty8mPbxfzOlWUa');

INSERT INTO enduser (id, name, surname, username, password) 
VALUES 
(2, 'Simone', 'Muscas', 'simone', '$2y$10$kF9QRWleVe.ylCGnDkKtTOLHUOCyKuMXcUToNh1PMdqsBiHk/Ioni');

INSERT INTO enduser (id, name, surname, username, password) 
VALUES 
(3, 'Test', 'Test', 'test', '$2y$10$wHU6Hx.U5gUSRn/QX0gsh.Zql9eiKTt9dsZWLhsEho13AUnt/gxcC');

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

INSERT INTO enduserrole (id, enduser_id, role_id) 
VALUES 
(4, 3, 1);

INSERT INTO enduserrole (id, enduser_id, role_id) 
VALUES 
(5, 3, 2);
