DROP TABLE book IF EXISTS;
DROP TABLE author IF EXISTS;
DROP TABLE publisher IF EXISTS;

-- --------------------------------------------------------
-- Structure of table author
-- --------------------------------------------------------

CREATE TABLE author (
  id 		INTEGER IDENTITY PRIMARY KEY,
  name 		VARCHAR(100),
  surname 	VARCHAR(100)
);
CREATE INDEX author_fullname ON author (name,surname);

-- --------------------------------------------------------
-- Structure of table publisher
-- --------------------------------------------------------

CREATE TABLE publisher (
  id 		INTEGER IDENTITY PRIMARY KEY,
  name 		VARCHAR(100)
);
CREATE INDEX publisher_name ON publisher (name);

-- --------------------------------------------------------
-- Structure of table book
-- --------------------------------------------------------

CREATE TABLE book (
  id 			INTEGER IDENTITY PRIMARY KEY,
  title 		VARCHAR(100),
  id_author 	INTEGER NOT NULL,
  id_publisher 	INTEGER NOT NULL,
  quantity 		INTEGER
);
ALTER TABLE book ADD CONSTRAINT fk_book_author FOREIGN KEY (id_author) REFERENCES author (id);
ALTER TABLE book ADD CONSTRAINT fk_book_publisher FOREIGN KEY (id_publisher) REFERENCES publisher (id);
CREATE INDEX book_title ON book (title);
