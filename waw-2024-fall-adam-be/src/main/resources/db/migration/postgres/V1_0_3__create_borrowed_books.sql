CREATE TYPE borrow_status AS ENUM ('BORROWED', 'RETURNED');

CREATE TABLE  BORROWED_BOOKS (
    borrowed_id SERIAL PRIMARY KEY,
    book_copy_id INTEGER,
    user_id INTEGER NOT NULL,
    status borrow_status NOT NULL,
    borrowed_from DATE NOT NULL,
    return_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    FOREIGN KEY (book_copy_id) REFERENCES BOOKS_COPY(book_copy_id)
);

CREATE CAST (borrow_status AS TEXT) WITH INOUT AS IMPLICIT;

CREATE CAST (VARCHAR AS borrow_status) with inout as IMPLICIT;