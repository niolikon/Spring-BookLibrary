--
-- Database: booklibrary
--

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
CREATE INDEX author_fullname ON author (name, surname);

ALTER TABLE author ADD CONSTRAINT author_unicity
UNIQUE(name, surname);

-- --------------------------------------------------------
-- Structure of table publisher
-- --------------------------------------------------------

CREATE TABLE publisher (
  id 		INTEGER IDENTITY PRIMARY KEY,
  name 		VARCHAR(100)
);
CREATE INDEX publisher_name ON publisher (name);

ALTER TABLE publisher ADD CONSTRAINT publisher_unicity
UNIQUE(name);

-- --------------------------------------------------------
-- Structure of table book
-- --------------------------------------------------------

CREATE TABLE book (
  id 			INTEGER IDENTITY PRIMARY KEY,
  title 		VARCHAR(100),
  author_id 	INTEGER NOT NULL,
  publisher_id 	INTEGER NOT NULL,
  quantity 		INTEGER
);

CREATE INDEX book_title ON book (title);

ALTER TABLE book ADD CONSTRAINT book_author_fk 
FOREIGN KEY (author_id) REFERENCES author (id) 
ON DELETE CASCADE;

ALTER TABLE book ADD CONSTRAINT book_publisher_fk 
FOREIGN KEY (publisher_id) REFERENCES publisher (id)
ON DELETE CASCADE;

ALTER TABLE book ADD CONSTRAINT book_unicity
UNIQUE(title,author_id,publisher_id);
