CREATE TABLE IF NOT EXISTS BOOKS (
     book_id SERIAL PRIMARY KEY,
     cover_image BYTEA NOT NULL,
     title VARCHAR(255) NOT NULL,
     author VARCHAR(255) NOT NULL,
     description VARCHAR(5000) NOT NULL,
     format VARCHAR(50) NOT NULL,
     number_of_pages INTEGER NOT NULL,
     publication_date DATE NOT NULL,
     publisher VARCHAR(255) NOT NULL,
     isbn VARCHAR(255) NOT NULL,
     edition_language VARCHAR(50) NOT NULL,
     series VARCHAR(50),
     category VARCHAR(50)
);