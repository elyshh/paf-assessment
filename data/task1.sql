-- Write your Task 1 answers in this file

DROP DATABASE IF EXISTS bedandbreakfast;

CREATE DATABASE bedandbreakfast;

USE bedandbreakfast;

CREATE TABLE users (
    email varchar(128) NOT NULL,
    name varchar(128) NOT NULL,

    primary key(email)
);

CREATE TABLE bookings (
    booking_id char(8) NOT NULL,
    listing_id varchar(20) NOT NULL,
    duration int,
    email varchar(128) NOT NULL,

    PRIMARY KEY (booking_id),
    INDEX (listing_id),
    FOREIGN KEY (email) REFERENCES users(email)
);


CREATE TABLE reviews (
    id int auto_increment,
    listing_id varchar(20),
    date timestamp,
    reviewer_name varchar(64) NOT NULL,
    comments text,

    PRIMARY KEY (id),
    CONSTRAINT fk_listing_id FOREIGN KEY (listing_id) REFERENCES bookings(listing_id)
);

INSERT INTO users (email, name)
    values
        ('fred@gmail.com', 'Fred Flintstone'),
        ('barney@gmail.com', 'Barney Rubble'),
        ('fry@planetexpress.com', 'Philip J Fry'),
        ('hlmer@gmail.com', 'Homer Simpson')
    ;




