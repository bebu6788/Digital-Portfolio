--
-- PostgreSQL database dump
--
SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET default_tablespace = '';

SET default_with_oids = false;
---
--- drop tables
---
DROP TABLE IF EXISTS ;

--
-- Name: users; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE users (
        accountID serial primary key,
        firstName VARCHAR(50) NOT NULL,
        lastName VARCHAR(50) NOT NULL,
        memberSince DATE NOT NULL,
        physical VARCHAR(42)[],
        ebook VARCHAR(42)[],
	audiobook VARCHAR(42)[],
        Password VARCHAR(100) NOT NULL,
        Email VARCHAR(100) NOT NULL,
	books VARCHAR(20)[],
	favAuthors VARCHAR(40)[],
	favGenres VARCHAR(40)[]
);

DROP TABLE IF EXISTS ;

--
-- Name: library; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE library(
        isbn VARCHAR(20) NOT NULL, 
        author VARCHAR(45),
        title VARCHAR(100)
);

INSERT INTO users (firstname,lastname,membersince,physical,ebook,audiobook,password,email,books,favAuthors,favGenres
) VALUES ('John','Doe',to_date('20220412','yyyymmdd'),ARRAY ['9780553898194','9780553900347'],ARRAY ['9780345335906'],ARRAY ['9781444741223'],'dontplaintext','test@test.com',ARRAY ['9780553898194','9780345335906','9780553900347','9781444741223'],ARRAY['Philip K Dick', 'Orson Scott Card'], ARRAY['Science Fiction', 'Fantasy']
);

INSERT INTO library(isbn,title,author) VALUES ('9780553898194','Snow Crash','Neal Stephenson') , ('9780345335906','The Wizard of Oz','Lyman Frank Baum'
) , ('9780553900347','Foundation and Empire','Isaac Asimov'), ('9781444741223','11.22.63','Stephen King');

--
-- Name: users; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--



