-- --------------------------------------------------------
-- Data for table author
-- --------------------------------------------------------

INSERT INTO author (name, surname) 
VALUES 
('Howard', 'Lovecraft'),
('J.R.R.', 'Tolkien'),
('J.K.', 'Rowling');

-- --------------------------------------------------------
-- Data for table publisher
-- --------------------------------------------------------

INSERT INTO publisher (name) 
VALUES 
('Mondadori'),
('Einaudi');

-- --------------------------------------------------------
-- Data for table book
-- --------------------------------------------------------

INSERT INTO book (title, author_id, publisher_id, quantity) 
VALUES 
('Il colore venuto dallo spazio', 1, 2, 1),
('Harry Potter e la pietra filosofale', 3, 1, 2),
('Racconti Incompiuti', 2, 1, 1);
