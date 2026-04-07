-- MillX Database Schema

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

--
-- Database: `millx_db`
--
CREATE DATABASE IF NOT EXISTS `millx_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `millx_db`;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` text DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `reset_otp` varchar(10) DEFAULT NULL,
  `reset_otp_expires_at` datetime DEFAULT NULL,
  `role` enum('user','admin') NOT NULL DEFAULT 'user',
  `status` int(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `user_tokens`
--

CREATE TABLE `user_tokens` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_token_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `machines`
--

CREATE TABLE `machines` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `status` enum('Running','Stopped','Maintenance') NOT NULL DEFAULT 'Stopped',
  `min_capacity` VARCHAR(50) DEFAULT '0',
  `max_capacity` VARCHAR(50) DEFAULT '0',
  `unit` VARCHAR(20) DEFAULT 'KG/HR',
  `description` TEXT,
  `image` VARCHAR(255) DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stock_quantity` int(11) NOT NULL DEFAULT 0,
  `image` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('Pending','Processing','Completed','Cancelled') NOT NULL DEFAULT 'Pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `stocks`
--

CREATE TABLE `stocks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 0,
  `unit` varchar(20) DEFAULT 'kg',
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `feedbacks`
--

CREATE TABLE `feedbacks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_feedback_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

COMMIT;

--
-- Dumping data for table `users`
-- (Password is '123456' hashed with BCRYPT)
--
INSERT INTO `users` (`id`, `name`, `email`, `phone`, `password`, `role`, `status`, `created_at`) VALUES
(1, 'Admin User', 'admin@millx.com', '9876543210', '$2y$10$SfM5ns.9hH7.3b4.6.8.0.P.8.8.8.8.8.8.8.8.8.8.8.8.8.8', 'admin', 1, '2024-01-01 10:00:00'),
(2, 'Test User', 'user@millx.com', '9876543211', '$2y$10$SfM5ns.9hH7.3b4.6.8.0.P.8.8.8.8.8.8.8.8.8.8.8.8.8.8', 'user', 1, '2024-01-01 10:00:00');

--
-- Dumping data for table `machines`
--
INSERT INTO `machines` (`id`, `name`, `status`, `last_updated`) VALUES
(1, 'Rice Grinder Pro', 'Running', '2024-01-01 10:00:00'),
(2, 'Wheat Flour Machine', 'Stopped', '2024-01-01 10:00:00'),
(3, 'Spice Grinder 3000', 'Maintenance', '2024-01-01 10:00:00');

--
-- Dumping data for table `products`
--
INSERT INTO `products` (`id`, `name`, `description`, `price`, `stock_quantity`, `image`, `created_at`) VALUES
(1, 'Rice Grinding', 'High quality rice grinding service', 10.00, 100, NULL, '2024-01-01 10:00:00'),
(2, 'Wheat Grinding', 'Fine wheat flour milling', 12.00, 100, NULL, '2024-01-01 10:00:00'),
(3, 'Chilli Grinding', 'Spicy chilli powder making', 25.00, 100, NULL, '2024-01-01 10:00:00'),
(4, 'Ragi Flour', 'Healthy ragi flour', 40.00, 50, NULL, '2024-01-01 10:00:00');

--
-- Dumping data for table `stocks`
--
INSERT INTO `stocks` (`id`, `item_name`, `quantity`, `unit`, `updated_at`) VALUES
(1, 'Rice', 500, 'kg', '2024-01-01 10:00:00'),
(2, 'Wheat', 350, 'kg', '2024-01-01 10:00:00'),
(3, 'Ragi', 100, 'kg', '2024-01-01 10:00:00'),
(4, 'Chilli', 50, 'kg', '2024-01-01 10:00:00'),
(5, 'Turmeric', 25, 'kg', '2024-01-01 10:00:00');

