CREATE TABLE `notes` (
  `note_id` int NOT NULL AUTO_INCREMENT,
  `author` varchar(45) NOT NULL,
  `header` varchar(100) NOT NULL,
  `last_modified_date` date DEFAULT NULL,
  PRIMARY KEY (`note_id`),
  KEY `author_of_note_idx` (`author`),
  CONSTRAINT `author_of_note` FOREIGN KEY (`author`) REFERENCES `users` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2079 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `note_filters` (
  `note_id` int NOT NULL,
  `filter` varchar(100) NOT NULL,
  PRIMARY KEY (`note_id`,`filter`),
  CONSTRAINT `note_of_filter` FOREIGN KEY (`note_id`) REFERENCES `notes` (`note_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `note_blocks` (
  `block_id` int NOT NULL AUTO_INCREMENT,
  `note_id` int DEFAULT NULL,
  `header` varchar(45) DEFAULT NULL,
  `block_type` varchar(45) DEFAULT NULL,
  `block_order` int DEFAULT NULL,
  PRIMARY KEY (`block_id`),
  KEY `note_of_block_idx` (`note_id`),
  CONSTRAINT `note_of_block` FOREIGN KEY (`note_id`) REFERENCES `notes` (`note_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `text_blocks` (
  `block_id` int NOT NULL,
  `editor` varchar(45) NOT NULL,
  `content` longtext,
  PRIMARY KEY (`block_id`,`editor`),
  KEY `text_block_editor_idx` (`editor`),
  CONSTRAINT `text_block_editor` FOREIGN KEY (`editor`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  CONSTRAINT `text_block_id` FOREIGN KEY (`block_id`) REFERENCES `note_blocks` (`block_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `survey_blocks` (
  `block_id` int NOT NULL,
  `editor` varchar(45) NOT NULL,
  `survey_map` varchar(10000) DEFAULT NULL,
  PRIMARY KEY (`block_id`,`editor`),
  KEY `editor_of_survey_idx` (`editor`),
  CONSTRAINT `survey_block_editor` FOREIGN KEY (`editor`) REFERENCES `users` (`username`) ON DELETE CASCADE,
  CONSTRAINT `survey_block_id` FOREIGN KEY (`block_id`) REFERENCES `note_blocks` (`block_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `sharenotes` (
  `note_id` int NOT NULL,
  `editor` varchar(45) NOT NULL,
  `share_type` varchar(15) NOT NULL,
  PRIMARY KEY (`editor`,`note_id`),
  KEY `share_note_id_idx` (`note_id`),
  CONSTRAINT `share_note_id` FOREIGN KEY (`note_id`) REFERENCES `notes` (`note_id`) ON DELETE CASCADE,
  CONSTRAINT `this_editor` FOREIGN KEY (`editor`) REFERENCES `users` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;