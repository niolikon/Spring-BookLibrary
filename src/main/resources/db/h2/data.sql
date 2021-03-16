-- --------------------------------------------------------
-- Data for table author
-- --------------------------------------------------------

INSERT INTO author (id, name, surname) 
VALUES 
(1, 'Howard', 'Lovecraft');

INSERT INTO author (id, name, surname) 
VALUES 
(2, 'J.R.R.', 'Tolkien');

INSERT INTO author (id, name, surname) 
VALUES 
(3, 'J.K.', 'Rowling');

-- --------------------------------------------------------
-- Data for table publisher
-- --------------------------------------------------------

INSERT INTO publisher (id, name) 
VALUES 
(1, 'Mondadori');

INSERT INTO publisher (id, name) 
VALUES 
(2, 'Einaudi');

-- --------------------------------------------------------
-- Data for table book
-- --------------------------------------------------------

INSERT INTO book (id, title, id_author, id_publisher, quantity) 
VALUES 
(1, 'Il colore venuto dallo spazio', 1, 2, 1);

INSERT INTO book (id, title, id_author, id_publisher, quantity)
VALUES 
(2, 'Harry Potter e la pietra filosofale', 3, 1, 2);

INSERT INTO book (id, title, id_author, id_publisher, quantity)
VALUES 
(3, 'Racconti Incompiuti', 2, 1, 1);
