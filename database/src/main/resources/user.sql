CREATE TABLE users (
                       id int PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL
);

INSERT INTO users (username, email) VALUES ('John Doe', 'john.doe@example.com');
INSERT INTO users (username, email) VALUES ('Jane Doe', 'jane.doe@example.com');
INSERT INTO users (username, email) VALUES ('Alice', 'alice@example.com');
INSERT INTO users (username, email) VALUES ('Bob', 'bob@example.com');
