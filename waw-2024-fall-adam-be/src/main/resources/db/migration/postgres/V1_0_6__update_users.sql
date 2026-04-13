ALTER TABLE USERS
    DROP COLUMN IF EXISTS username,
    ADD COLUMN first_name VARCHAR(255) NOT NULL,
    ADD COLUMN last_name VARCHAR(255) NOT NULL,
    ADD COLUMN phone_number VARCHAR(20) NOT NULL,
    ADD COLUMN default_office_id INTEGER NOT NULL,
    ADD CONSTRAINT fk_default_office_id
        FOREIGN KEY (default_office_id) REFERENCES OFFICES (office_id);
