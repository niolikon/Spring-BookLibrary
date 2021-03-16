--
-- Database: booklibrary
--

-- --------------------------------------------------------
-- Structure of table author
-- --------------------------------------------------------

CREATE SEQUENCE author_id_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  MAXVALUE 2147483647
  CACHE 1;
  
CREATE TABLE IF NOT EXISTS author (
  id int8 NOT NULL DEFAULT nextval('author_id_seq'),
  name VARCHAR(100),
  surname varchar(100),
  PRIMARY KEY (id)
);

-- --------------------------------------------------------
-- Structure of table publisher
-- --------------------------------------------------------

CREATE SEQUENCE publisher_id_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  MAXVALUE 2147483647
  CACHE 1;

CREATE TABLE IF NOT EXISTS publisher (
  id int8 NOT NULL DEFAULT nextval('publisher_id_seq'),
  name VARCHAR(100),
  PRIMARY KEY (id)
);

-- --------------------------------------------------------
-- Structure of table book
-- --------------------------------------------------------

CREATE SEQUENCE book_id_seq
  START WITH 1
  INCREMENT BY 1
  NO MINVALUE
  MAXVALUE 2147483647
  CACHE 1;

CREATE TABLE IF NOT EXISTS book (
  id int8 NOT NULL DEFAULT nextval('book_id_seq'),
  title VARCHAR(100),
  id_author integer,
  id_publisher integer,
  quantity int8,
  PRIMARY KEY (id)
);

-- --------------------------------------------------------
-- Indexes for table author
-- --------------------------------------------------------

ALTER TABLE IF EXISTS author 
ADD CONSTRAINT author_unicity
UNIQUE (name,surname);
  
-- --------------------------------------------------------
-- Indexes for table publisher
-- --------------------------------------------------------

ALTER TABLE IF EXISTS publisher 
ADD CONSTRAINT publisher_unicity
UNIQUE (name);

-- --------------------------------------------------------
-- Indexes for table book
-- --------------------------------------------------------

ALTER TABLE IF EXISTS book 
ADD CONSTRAINT book_unicity
UNIQUE (title,id_author,id_publisher);
  
-- --------------------------------------------------------
-- Foreign keys for table book
-- --------------------------------------------------------

ALTER TABLE IF EXISTS book
  ADD CONSTRAINT author_foreign_key FOREIGN KEY (id_author) REFERENCES author,
  ADD CONSTRAINT publisher_foreign_key FOREIGN KEY (id_publisher) REFERENCES publisher;
    