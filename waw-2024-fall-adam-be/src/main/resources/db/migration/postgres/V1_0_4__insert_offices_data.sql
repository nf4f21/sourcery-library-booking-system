INSERT INTO addresses (city, street, building_number, apartment_number, country, zip_code)
VALUES ('Kaunas', 'A. Juozapaviciaus pr.', '11d', null, 'Lithuania', 'LT-45252'),
       ('Vilnius', 'Zalgirio g.', '112', null, 'Lithuania', 'LT-09300'),
       ('London', 'Liverpool St', '34-37', null, 'United Kingdom', 'EC2M 7PP'),
       ('Chicago', 'N Orleans', '350', 'Suite 7500-S', 'United States', '60654'),
       ('Toronto', 'University Avenue', '100', 'Suite 500', 'Canada', 'M5J1V6');

INSERT INTO offices (name, address_id)
VALUES ('Kaunas', 1),
       ('Vilnius', 2),
       ('London', 3),
       ('Chicago', 4),
       ('Toronto', 5);