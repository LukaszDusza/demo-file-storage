CREATE TABLE files (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       file_name VARCHAR(255) NOT NULL,
                       checksum VARCHAR(255) NOT NULL,
                       size BIGINT NOT NULL
);
