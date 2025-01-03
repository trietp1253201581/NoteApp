CREATE TABLE `users` (
  `name` varchar(45) DEFAULT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `birthday` date DEFAULT NULL,
  `school` varchar(45) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `is_locked` varchar(10) DEFAULT 'false',
  `admin` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`username`),
  KEY `admin_manage_idx` (`admin`),
  CONSTRAINT `admin_manage` FOREIGN KEY (`admin`) REFERENCES `admins` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `admins` (
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci