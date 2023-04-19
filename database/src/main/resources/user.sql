CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL

);

INSERT INTO users (username, email, password) VALUES ('John Doe', 'john.doe@example.com', 'AAAAAAAAAAAAAAAAAAAAAA==:nUK02A5yTIHzgABO3YhXFE16v4mLm8m88s5LgOIxV4c=');
INSERT INTO users (username, email, password) VALUES ('Jane Doe', 'jane.doe@example.com', 'AAAAAAAAAAAAAAAAAAAAAA==:nUK02A5yTIHzgABO3YhXFE16v4mLm8m88s5LgOIxV4c=');
INSERT INTO users (username, email, password) VALUES ('Alice', 'alice@example.com', 'AAAAAAAAAAAAAAAAAAAAAA==:nUK02A5yTIHzgABO3YhXFE16v4mLm8m88s5LgOIxV4c=');
INSERT INTO users (username, email, password) VALUES ('Bob', 'bob@example.com', 'AAAAAAAAAAAAAAAAAAAAAA==:nUK02A5yTIHzgABO3YhXFE16v4mLm8m88s5LgOIxV4c=');
