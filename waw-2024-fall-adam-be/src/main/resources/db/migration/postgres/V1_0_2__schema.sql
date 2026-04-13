CREATE TABLE IF NOT EXISTS ADDRESSES
(
    address_id       SERIAL PRIMARY KEY,
    city             VARCHAR(255) NOT NULL,
    street           VARCHAR(255) NOT NULL,
    building_number  VARCHAR(20)  NOT NULL,
    apartment_number VARCHAR(20),
    country          VARCHAR(255)  NOT NULL,
    zip_code         VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS OFFICES
(
    office_id  SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    address_id INTEGER UNIQUE REFERENCES ADDRESSES (address_id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS BOOKS_COPY
(
    book_copy_id SERIAL PRIMARY KEY,
    is_available BOOLEAN NOT NULL,
    book_id      INTEGER NOT NULL,
    office_id    INTEGER NOT NULL,
    FOREIGN KEY (book_id) REFERENCES BOOKS (book_id),
    FOREIGN KEY (office_id) REFERENCES OFFICES (office_id)
);
