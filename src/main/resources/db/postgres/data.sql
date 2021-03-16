-- --------------------------------------------------------
-- Data for table author
-- --------------------------------------------------------

INSERT INTO author (id, "name", "surname") 
VALUES 
(1, 'Howard', 'Lovecraft'),
(2, 'J.R.R.', 'Tolkien'),
(3, 'J.K.', 'Rowling');

ALTER SEQUENCE author_id_seq RESTART WITH 4;

-- --------------------------------------------------------
-- Data for table publisher
-- --------------------------------------------------------

INSERT INTO publisher (id, "name") 
VALUES 
(1, 'Mondadori'),
(2, 'Einaudi');

ALTER SEQUENCE publisher_id_seq RESTART WITH 3;

-- --------------------------------------------------------
-- Data for table book
-- --------------------------------------------------------

INSERT INTO book (id, "title", id_author, id_publisher, quantity) 
VALUES 
(1, 'Il colore venuto dallo spazio', 1, 2, 1),
(2, 'Harry Potter e la pietra filosofale', 3, 1, 2),
(3, 'Racconti Incompiuti', 2, 1, 1);

ALTER SEQUENCE book_id_seq RESTART WITH 4;
