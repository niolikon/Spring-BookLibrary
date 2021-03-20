--
-- Database: booklibrary
--

DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS publisher;

-- --------------------------------------------------------
-- Structure of table author
-- --------------------------------------------------------

CREATE TABLE author (
  id 		SERIAL PRIMARY KEY,
  name 		VARCHAR(100),
  surname 	VARCHAR(100)
);
CREATE INDEX author_name ON author (name);
CREATE INDEX author_surname ON author (surname);

ALTER TABLE author ADD CONSTRAINT author_unicity
UNIQUE(name,surname);

-- --------------------------------------------------------
-- Structure of table publisher
-- --------------------------------------------------------

CREATE TABLE publisher (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100)
);
CREATE INDEX publisher_name ON publisher (name);

ALTER TABLE publisher ADD CONSTRAINT publisher_unicity
UNIQUE(name);

-- --------------------------------------------------------
-- Structure of table book
-- --------------------------------------------------------

CREATE TABLE book (
  id SERIAL PRIMARY KEY,
  title VARCHAR(100),
  author_id SERIAL,
  publisher_id SERIAL,
  quantity int8
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
