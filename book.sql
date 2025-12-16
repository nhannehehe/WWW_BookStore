-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1:3306
-- Thời gian đã tạo: Th12 16, 2025 lúc 02:40 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `book`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `cart`
--

CREATE TABLE `cart` (
  `id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `cart`
--

INSERT INTO `cart` (`id`, `quantity`, `product_id`, `user_id`) VALUES
(4, 1, 18, 4);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `category`
--

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  `image_name` varchar(1000) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `category`
--

INSERT INTO `category` (`id`, `image_name`, `is_active`, `name`) VALUES
(1, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935244866957_thanh_ly.jpg', b'1', 'Science Fiction'),
(2, 'https://cdn0.fahasa.com/media/catalog/product/i/m/image_244718_1_52.jpg', b'1', 'English Learning'),
(3, 'https://cdn0.fahasa.com/media/catalog/product/3/0/30-giay-khoa-hoc_khoa-hoc-du-lieu.jpg', b'1', 'Science'),
(4, 'https://cdn0.fahasa.com/media/catalog/product/9/7/9786040392435.jpg', b'1', 'Technology'),
(5, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935280916470.jpg', b'1', 'Education');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `order_address`
--

CREATE TABLE `order_address` (
  `id` int(11) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `mobile_no` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `order_address`
--

INSERT INTO `order_address` (`id`, `address`, `city`, `email`, `first_name`, `last_name`, `mobile_no`, `pincode`, `state`) VALUES
(1, 'Address', 'HCMCity', 'baothong15082003@gmail.com', 'Dang', 'Thong', '1211221121', '111111', 'GoVap'),
(2, 'Address', 'HCMCity', 'baothong15082003@gmail.com', 'Dang', 'Thong', '1211221121', '111111', 'GoVap'),
(3, 'Address', 'HCMCity', 'baothong15082003@gmail.com', 'Dang', 'Thong', '1211221121', '111111', 'GoVap'),
(4, 'Admin Address', 'AdminCity', 'admin@admin.com', 'Admin', 'Admin', '0000000000', '000000', 'Admin State'),
(5, 'Admin Address', 'AdminCity', 'admin@admin.com', 'Admin', 'Admin', '0000000000', '000000', 'Admin State'),
(6, 'Admin Address', 'AdminCity', 'admin@admin.com', 'Admin', 'Thong', '0000000000', '000000', 'Admin State'),
(7, 'Admin Address', 'AdminCity', 'admin@admin.com', 'Admin', 'Thong', '0000000000', '000000', 'Admin State'),
(8, 'Tan Hoa, Tan Chau, Tay Ninh', 'Tay Ninh', 'nguyentanphat100102it@gmail.com', 'Nguyen', 'Phat', '0369809077', '840000', 'Tay Ninh'),
(9, 'Tan Hoa, Tan Chau, Tay Ninh', 'Tay Ninh', 'phatntp719@gmail.com', 'Nguyen', 'Phat', '0369809077', '840000', 'Tay Ninh'),
(10, 'Tan Hoa, Tan Chau, Tay Ninh', 'Tay Ninh', 'phatntp719@gmail.com', 'Nguyen', 'Phat', '0369809077', '840000', 'Tay Ninh'),
(11, 'Tan Hoa, Tan Chau, Tay Ninh', 'Tay Ninh', 'nguyentanphat100102it@gmail.com', 'Nguyen', 'Phat', '0369809077', '840000', 'Tay Ninh'),
(12, 'Tan Hoa, Tan Chau, Tay Ninh', 'Tay Ninh', 'nguyentanphat100102it@gmail.com', 'Nguyen', 'Phat', '0369809077', '840000', 'Tay Ninh');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product`
--

CREATE TABLE `product` (
  `id` int(11) NOT NULL,
  `description` varchar(5000) DEFAULT NULL,
  `discount` int(11) NOT NULL,
  `discount_price` double DEFAULT NULL,
  `image` varchar(1000) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `stock` int(11) NOT NULL,
  `title` varchar(500) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `product`
--

INSERT INTO `product` (`id`, `description`, `discount`, `discount_price`, `image`, `is_active`, `price`, `stock`, `title`, `category_id`) VALUES
(6, 'The term \"artificial intelligence\" was proposed much earlier than we know - in the summer of 1956, by computer scientist John McCarthy at the Dartmouth Conference.', 10, 539100, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935244866957_thanh_ly.jpg', b'1', 599000, 49, 'AI 2041 - For The Future', 4),
(7, 'Data science is a new artificial ecosystem in the modern information age, ranging from crime detection to disease prediction. But did you know it\'s not just about the massive amount of information collected by computers, smartphones, and credit cards?', 5, 332500, 'https://cdn0.fahasa.com/media/catalog/product/3/0/30-giay-khoa-hoc_khoa-hoc-du-lieu.jpg', b'1', 350000, 22, 'Data Science', 3),
(8, 'Dive into the fascinating world of quantum mechanics with this easy-to-understand book.', 10, 269100, 'https://cdn0.fahasa.com/media/catalog/product/3/0/30-giay-khoa-hoc_khoa-hoc-du-lieu.jpg', b'1', 299000, 20, 'Quantum Physics Explained', 3),
(9, 'Understand the fundamentals of artificial intelligence and its applications in real-world problems.', 12, 351120, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935244866957_thanh_ly.jpg', b'1', 399000, 35, 'Learning AI Basics', 4),
(10, 'A comprehensive guide to start learning English effectively, with plenty of examples and exercises.', 15, 170000, 'https://cdn0.fahasa.com/media/catalog/product/i/m/image_244718_1_52.jpg', b'1', 200000, 49, 'English for Beginners', 2),
(11, 'This book covers advanced concepts in electronics technology for engineering students.', 18, 451000, 'https://cdn0.fahasa.com/media/catalog/product/9/7/9786040392435.jpg', b'1', 550000, 30, 'Advanced Electronics', 4),
(12, 'An excellent resource for educators to improve their teaching methods.', 5, 285000, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935280916470.jpg', b'1', 300000, 38, 'Teaching Strategies', 5),
(13, 'Explore the mysteries of the cosmos with this thrilling science book.', 8, 386400, 'https://cdn0.fahasa.com/media/catalog/product/3/0/30-giay-khoa-hoc_khoa-hoc-du-lieu.jpg', b'1', 420000, 25, 'Physics of the Universe', 3),
(14, 'Discover how AI is transforming industries and creating new possibilities.', 10, 441000, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935244866957_thanh_ly.jpg', b'1', 490000, 20, 'Innovating with AI', 4),
(15, 'Improve your English communication skills for daily conversations.', 12, 158400, 'https://cdn0.fahasa.com/media/catalog/product/i/m/image_244718_1_52.jpg', b'1', 180000, 45, 'Practical English Skills', 2),
(16, 'A beginner-friendly introduction to designing electrical circuits.', 20, 280000, 'https://cdn0.fahasa.com/media/catalog/product/9/7/9786040392435.jpg', b'1', 350000, 29, 'Basic Circuits Design', 4),
(17, 'A look at modern trends and challenges in education today.', 10, 243000, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935280916470.jpg', b'1', 270000, 53, 'Modern Education Trends', 5),
(18, 'Destination B1 - Grammar And Vocabulary With Answer Key', 0, 270000, 'https://cdn0.fahasa.com/media/catalog/product/8/9/8935246945001.jpg', b'1', 270000, 997, 'Destination B1 - Grammar And Vocabulary With Answer Key', 2),
(19, 'The Witches', 0, 191700, 'https://cdn0.fahasa.com/media/catalog/product/9/7/9780241677667.jpg', b'1', 191700, 5, 'The Witches', 1),
(20, 'Maya And The Robot', 0, 156600, 'https://cdn0.fahasa.com/media/catalog/product/9/7/9781984814654.jpg', b'1', 156600, 1, 'Maya And The Robot', 1);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `product_order`
--

CREATE TABLE `product_order` (
  `id` int(11) NOT NULL,
  `order_date` datetime(6) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `payment_type` varchar(255) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `order_address_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `product_order`
--

INSERT INTO `product_order` (`id`, `order_date`, `order_id`, `payment_type`, `price`, `quantity`, `status`, `order_address_id`, `product_id`, `user_id`) VALUES
(8, '2025-12-02 14:27:06.000000', 'baab6e55-5ae0-46c2-b1d8-34f54276e645', 'ONLINE', 156600, 4, 'Delivered', 8, 20, 5),
(9, '2025-12-06 20:39:31.000000', 'f929219c-d1d0-4eeb-982a-7bf409d6699f', 'COD', 539100, 1, 'Cancelled', 9, 6, 6),
(10, '2025-12-06 20:40:17.000000', '12fb5713-54fb-43ca-b935-bae9260677b8', 'COD', 332500, 1, 'In Progress', 10, 7, 6),
(11, '2025-12-08 22:28:32.000000', 'fb5ffc1a-d8f7-45da-bff5-c545043fc9c2', 'COD', 285000, 1, 'In Progress', 11, 12, 5),
(12, '2025-12-08 22:53:12.000000', 'a4bcfe0d-d4e6-430b-acf8-d8ec66a4bdff', 'COD', 243000, 1, 'In Progress', 12, 17, 5);

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `user_dtls`
--

CREATE TABLE `user_dtls` (
  `id` int(11) NOT NULL,
  `account_non_locked` bit(1) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `failed_attempt` int(11) DEFAULT NULL,
  `is_enable` bit(1) DEFAULT NULL,
  `lock_time` datetime(6) DEFAULT NULL,
  `mobile_number` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `pincode` varchar(255) DEFAULT NULL,
  `profile_image` varchar(255) DEFAULT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Đang đổ dữ liệu cho bảng `user_dtls`
--

INSERT INTO `user_dtls` (`id`, `account_non_locked`, `address`, `city`, `email`, `failed_attempt`, `is_enable`, `lock_time`, `mobile_number`, `name`, `password`, `pincode`, `profile_image`, `reset_token`, `role`, `state`) VALUES
(5, b'1', 'Tan Hoa, Tan Chau, Tay Ninh', 'Tay Ninh', 'nguyentanphat100102it@gmail.com', 0, b'1', NULL, '0369809077', 'Nguyen Tan Phat', '$2a$10$aJ9/oEFMaWojjHL6UUJWX.rwuzYWf5L6Hr6Vcw8.ltOmiFq6pRRuO', '840000', 'anh-anime-4k-chat-luong-cao_044844344.jpg', NULL, 'ROLE_USER', 'Tay Ninh'),
(6, b'1', 'Tan Hoa, Tan Chau, Tay Ninh', 'Tay Ninh', 'phatntp719@gmail.com', 1, b'1', NULL, '0369809077', 'Nguyen Tan Phat', '$2a$10$.pwvzldniBQ0Heh9X6HBSewOrFsw45P3.mL7SjaCeYcKLHDAkt2m6', '840000', 'nền 1.jpg', NULL, 'ROLE_ADMIN', 'Tay Ninh');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK3d704slv66tw6x5hmbm6p2x3u` (`product_id`),
  ADD KEY `FK9x4wn098i53ikun1ynxet2ynj` (`user_id`);

--
-- Chỉ mục cho bảng `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `order_address`
--
ALTER TABLE `order_address`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1mtsbur82frn64de7balymq9s` (`category_id`);

--
-- Chỉ mục cho bảng `product_order`
--
ALTER TABLE `product_order`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_qcdbxaeuc7c5gahwh0dutg04r` (`order_address_id`),
  ADD KEY `FKh73acsd9s5wp6l0e55td6jr1m` (`product_id`),
  ADD KEY `FK4f2ycr32kigtux5ag3tv0xu5m` (`user_id`);

--
-- Chỉ mục cho bảng `user_dtls`
--
ALTER TABLE `user_dtls`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `cart`
--
ALTER TABLE `cart`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT cho bảng `category`
--
ALTER TABLE `category`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT cho bảng `order_address`
--
ALTER TABLE `order_address`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT cho bảng `product`
--
ALTER TABLE `product`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT cho bảng `product_order`
--
ALTER TABLE `product_order`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT cho bảng `user_dtls`
--
ALTER TABLE `user_dtls`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `FK3d704slv66tw6x5hmbm6p2x3u` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  ADD CONSTRAINT `FK9x4wn098i53ikun1ynxet2ynj` FOREIGN KEY (`user_id`) REFERENCES `user_dtls` (`id`);

--
-- Các ràng buộc cho bảng `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `FK1mtsbur82frn64de7balymq9s` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`);

--
-- Các ràng buộc cho bảng `product_order`
--
ALTER TABLE `product_order`
  ADD CONSTRAINT `FK4f2ycr32kigtux5ag3tv0xu5m` FOREIGN KEY (`user_id`) REFERENCES `user_dtls` (`id`),
  ADD CONSTRAINT `FK8frxalwc79tpxo7hgqp3hsjck` FOREIGN KEY (`order_address_id`) REFERENCES `order_address` (`id`),
  ADD CONSTRAINT `FKh73acsd9s5wp6l0e55td6jr1m` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
