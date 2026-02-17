CREATE DATABASE IF NOT EXISTS hero_story DEFAULT CHARACTER SET utf8mb4;
USE hero_story;

CREATE TABLE IF NOT EXISTS t_user (
    user_id INT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(64) NOT NULL,
    password VARCHAR(128) NOT NULL,
    hero_avatar VARCHAR(64) NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_user_name (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
