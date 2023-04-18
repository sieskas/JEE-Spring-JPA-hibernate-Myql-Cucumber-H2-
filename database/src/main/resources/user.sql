CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO users (username, email) VALUES ('John Doe', 'john.doe@example.com');
INSERT INTO users (username, email) VALUES ('Jane Doe', 'jane.doe@example.com');
INSERT INTO users (username, email) VALUES ('Alice', 'alice@example.com');
INSERT INTO users (username, email) VALUES ('Bob', 'bob@example.com');
