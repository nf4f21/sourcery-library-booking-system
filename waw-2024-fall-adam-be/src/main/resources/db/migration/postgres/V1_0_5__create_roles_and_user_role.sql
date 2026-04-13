CREATE TABLE IF NOT EXISTS ROLES
(
    role_id SERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS USER_ROLE
(
    user_id INTEGER,
    role_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES USERS (user_id),
    FOREIGN KEY (role_id) REFERENCES ROLES (role_id),
    UNIQUE (user_id, role_id)
);

INSERT INTO ROLES (name)
VALUES ('ADMIN'),
       ('USER');