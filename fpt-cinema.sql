-- ============================================================
-- FPT Cinema - MySQL Schema (chuyển từ SQL Server)
-- Yêu cầu: MySQL 8.0.16+ (để CHECK constraint có hiệu lực)
-- Engine: InnoDB (mặc định, bắt buộc để dùng FOREIGN KEY)
-- Charset: utf8mb4 (hỗ trợ đầy đủ tiếng Việt + emoji)
-- Lưu ý: created_at/updated_at dùng CURRENT_TIMESTAMP -> hãy đặt
--        time_zone của server = '+00:00' (UTC) để dữ liệu nhất quán.
--        updated_at TỰ ĐỘNG cập nhật khi UPDATE (ON UPDATE CURRENT_TIMESTAMP).
-- ============================================================



-- ----------------------------------------------------------
-- TABLES
-- ----------------------------------------------------------

CREATE TABLE `roles` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `role_name` VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE `permissions` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `permission_code` VARCHAR(100) UNIQUE NOT NULL,
  `permission_name` VARCHAR(150) NOT NULL,
  `description` VARCHAR(500)
);

-- Permission gán theo ROLE
CREATE TABLE `role_permissions` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `role_id` INT NOT NULL,
  `permission_id` INT NOT NULL
);

-- Permission gán theo từng ACCOUNT
-- is_granted = 1 -> cấp thêm quyền; 0 -> thu hồi quyền mà role đang có
CREATE TABLE `user_permissions` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `permission_id` INT NOT NULL,
  `is_granted` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `users` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `role_id` INT NOT NULL,
  `full_name` VARCHAR(150) NOT NULL,
  `email` VARCHAR(150) UNIQUE NOT NULL,
  `phone` VARCHAR(20),
  `password_hash` VARCHAR(255) NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
      CHECK (`status` IN ('ACTIVE','INACTIVE','LOCKED')),
  `reward_points` INT NOT NULL DEFAULT 0,
  `membership_level` VARCHAR(20) NOT NULL DEFAULT 'BRONZE'
      CHECK (`membership_level` IN ('BRONZE','SILVER','GOLD','DIAMOND')),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `revoked_tokens` (
  `token_id` VARCHAR(100) PRIMARY KEY,
  `expires_at` DATETIME(6) NOT NULL,
  `revoked_at` DATETIME(6) NOT NULL
);

CREATE TABLE `movies` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `genre` VARCHAR(20) NOT NULL
      CHECK (`genre` IN ('ACTION','COMEDY','HORROR','ROMANCE','ANIMATION','SCI_FI','DRAMA','THRILLER')),
  `duration_minutes` INT NOT NULL,
  `age_rating` VARCHAR(10) NOT NULL
      CHECK (`age_rating` IN ('P','K','T13','T16','T18')),
  `release_date` DATE,
  `poster_url` VARCHAR(500),
  `trailer_url` VARCHAR(500),
  `description` VARCHAR(1000),
  `status` VARCHAR(20) NOT NULL DEFAULT 'NOW_SHOWING'
      CHECK (`status` IN ('NOW_SHOWING','COMING_SOON','ENDED','HIDDEN')),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `rooms` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `room_name` VARCHAR(100) UNIQUE NOT NULL,
  `room_type` VARCHAR(20) NOT NULL DEFAULT 'STANDARD'
      CHECK (`room_type` IN ('STANDARD','VIP','IMAX','FOUR_DX','DOLBY')),
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
      CHECK (`status` IN ('ACTIVE','MAINTENANCE','CLOSED')),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `seats` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `room_id` INT NOT NULL,
  `seat_row` VARCHAR(10) NOT NULL,
  `seat_number` INT NOT NULL,
  `seat_type` VARCHAR(20) NOT NULL DEFAULT 'NORMAL'
      CHECK (`seat_type` IN ('NORMAL','VIP','COUPLE','PREMIUM')),
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
      CHECK (`status` IN ('ACTIVE','LOCKED','BROKEN'))
);

CREATE TABLE `showtimes` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `movie_id` INT NOT NULL,
  `room_id` INT NOT NULL,
  `start_time` DATETIME NOT NULL,
  `base_price` DECIMAL(12,2) NOT NULL,
  `cleaning_buffer_minutes` INT NOT NULL DEFAULT 15,
  `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN'
      CHECK (`status` IN ('OPEN','SOLD_OUT','CANCELLED','FINISHED')),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `products` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(150) NOT NULL,
  `product_type` VARCHAR(20) NOT NULL
      CHECK (`product_type` IN ('FOOD','BEVERAGE','COMBO')),
  `price` DECIMAL(12,2) NOT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `promotions` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `code` VARCHAR(80) UNIQUE,
  `name` VARCHAR(150) NOT NULL,
  `promotion_type` VARCHAR(20) NOT NULL
      CHECK (`promotion_type` IN ('PERCENTAGE','FIXED_AMOUNT')),
  `discount_value` DECIMAL(12,2) NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE `bookings` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `booking_code` VARCHAR(50) UNIQUE NOT NULL,
  `customer_id` INT NOT NULL,
  `staff_id` INT,
  `showtime_id` INT NOT NULL,
  `promotion_id` INT,
  `channel` VARCHAR(20) NOT NULL
      CHECK (`channel` IN ('ONLINE','COUNTER')),
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING'
      CHECK (`status` IN ('PENDING','CONFIRMED','CANCELLED','EXPIRED','REFUNDED')),
  `subtotal` DECIMAL(14,2) NOT NULL DEFAULT 0,
  `discount_amount` DECIMAL(14,2) NOT NULL DEFAULT 0,
  `final_amount` DECIMAL(14,2) NOT NULL,
  `expires_at` DATETIME,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `tickets` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `booking_id` INT NOT NULL,
  `showtime_id` INT NOT NULL,
  `seat_id` INT NOT NULL,
  `ticket_code` VARCHAR(80) UNIQUE NOT NULL,
  `price` DECIMAL(12,2) NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'RESERVED'
      CHECK (`status` IN ('RESERVED','BOOKED','CHECKED_IN','USED','CANCELLED','REFUNDED')),
  `checked_in_at` DATETIME,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `booking_products` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `booking_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `unit_price` DECIMAL(12,2) NOT NULL DEFAULT 0,
  `total_price` DECIMAL(12,2) NOT NULL
);

CREATE TABLE `payments` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `booking_id` INT NOT NULL,
  `payment_code` VARCHAR(80) UNIQUE NOT NULL,
  `method` VARCHAR(20) NOT NULL
      CHECK (`method` IN ('CASH','VNPAY','E_WALLET','BANK_TRANSFER')),
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING'
      CHECK (`status` IN ('PENDING','PAID','FAILED','REFUNDED')),
  `amount` DECIMAL(14,2) NOT NULL,
  `refund_amount` DECIMAL(14,2) NOT NULL DEFAULT 0,
  `paid_at` DATETIME,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE `reviews` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `customer_id` INT NOT NULL,
  `movie_id` INT NOT NULL,
  `booking_id` INT NOT NULL,
  `rating` INT NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
  `comment` VARCHAR(1000),
  `status` VARCHAR(20) NOT NULL DEFAULT 'VISIBLE'
      CHECK (`status` IN ('VISIBLE','HIDDEN','PENDING','REJECTED')),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ===== AI Chatbot: log theo phiên hội thoại =====
CREATE TABLE `ai_conversations` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `user_id` INT,                                   -- NULL = khách chưa đăng nhập
  `channel` VARCHAR(50) NOT NULL DEFAULT 'WEB',
  `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN'
      CHECK (`status` IN ('OPEN','CLOSED','ESCALATED')),
  `started_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ended_at` DATETIME
);

CREATE TABLE `ai_messages` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `conversation_id` INT NOT NULL,
  `sender` VARCHAR(20) NOT NULL
      CHECK (`sender` IN ('USER','BOT')),
  `content` TEXT NOT NULL,
  `intent_type` VARCHAR(30)
      CHECK (`intent_type` IN ('MOVIE_LIST','SHOWTIME_LIST','PROMOTION_LIST','PRODUCT_LIST','BOOKING_LOOKUP','TICKET_LOOKUP','GENERAL_CHAT')),
  `confidence` DECIMAL(5,4),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------
-- INDEXES
-- ----------------------------------------------------------

CREATE UNIQUE INDEX `IX_seats_room_row_number`
  ON `seats` (`room_id`, `seat_row`, `seat_number`);

CREATE UNIQUE INDEX `IX_role_permissions_role_permission`
  ON `role_permissions` (`role_id`, `permission_id`);

CREATE UNIQUE INDEX `IX_user_permissions_user_permission`
  ON `user_permissions` (`user_id`, `permission_id`);

CREATE INDEX `IX_revoked_tokens_expires_at`
  ON `revoked_tokens` (`expires_at`);

CREATE INDEX `IX_showtimes_movie_start_time`
  ON `showtimes` (`movie_id`, `start_time`);

CREATE INDEX `IX_showtimes_room_start_time`
  ON `showtimes` (`room_id`, `start_time`);

CREATE INDEX `IX_bookings_customer_status_created`
  ON `bookings` (`customer_id`, `status`, `created_at`);

CREATE INDEX `IX_bookings_showtime_status`
  ON `bookings` (`showtime_id`, `status`);

CREATE UNIQUE INDEX `IX_tickets_showtime_seat`
  ON `tickets` (`showtime_id`, `seat_id`);

CREATE UNIQUE INDEX `IX_reviews_customer_booking`
  ON `reviews` (`customer_id`, `booking_id`);

CREATE INDEX `IX_ai_conversations_user`
  ON `ai_conversations` (`user_id`);

CREATE INDEX `IX_ai_messages_conversation_created`
  ON `ai_messages` (`conversation_id`, `created_at`);

-- ----------------------------------------------------------
-- FOREIGN KEYS
-- ----------------------------------------------------------

ALTER TABLE `users`
  ADD CONSTRAINT `FK_users_roles`
  FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);

ALTER TABLE `role_permissions`
  ADD CONSTRAINT `FK_role_permissions_roles`
  FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`);

ALTER TABLE `role_permissions`
  ADD CONSTRAINT `FK_role_permissions_permissions`
  FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`);

ALTER TABLE `user_permissions`
  ADD CONSTRAINT `FK_user_permissions_users`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `user_permissions`
  ADD CONSTRAINT `FK_user_permissions_permissions`
  FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`);

ALTER TABLE `seats`
  ADD CONSTRAINT `FK_seats_rooms`
  FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`);

ALTER TABLE `showtimes`
  ADD CONSTRAINT `FK_showtimes_movies`
  FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`);

ALTER TABLE `showtimes`
  ADD CONSTRAINT `FK_showtimes_rooms`
  FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`);

ALTER TABLE `bookings`
  ADD CONSTRAINT `FK_bookings_customers`
  FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`);

ALTER TABLE `bookings`
  ADD CONSTRAINT `FK_bookings_staff`
  FOREIGN KEY (`staff_id`) REFERENCES `users` (`id`);

ALTER TABLE `bookings`
  ADD CONSTRAINT `FK_bookings_showtimes`
  FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`);

ALTER TABLE `bookings`
  ADD CONSTRAINT `FK_bookings_promotions`
  FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`);

ALTER TABLE `tickets`
  ADD CONSTRAINT `FK_tickets_bookings`
  FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `tickets`
  ADD CONSTRAINT `FK_tickets_showtimes`
  FOREIGN KEY (`showtime_id`) REFERENCES `showtimes` (`id`);

ALTER TABLE `tickets`
  ADD CONSTRAINT `FK_tickets_seats`
  FOREIGN KEY (`seat_id`) REFERENCES `seats` (`id`);

ALTER TABLE `booking_products`
  ADD CONSTRAINT `FK_booking_products_bookings`
  FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `booking_products`
  ADD CONSTRAINT `FK_booking_products_products`
  FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

ALTER TABLE `payments`
  ADD CONSTRAINT `FK_payments_bookings`
  FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `reviews`
  ADD CONSTRAINT `FK_reviews_customers`
  FOREIGN KEY (`customer_id`) REFERENCES `users` (`id`);

ALTER TABLE `reviews`
  ADD CONSTRAINT `FK_reviews_movies`
  FOREIGN KEY (`movie_id`) REFERENCES `movies` (`id`);

ALTER TABLE `reviews`
  ADD CONSTRAINT `FK_reviews_bookings`
  FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`);

ALTER TABLE `ai_conversations`
  ADD CONSTRAINT `FK_ai_conversations_users`
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

ALTER TABLE `ai_messages`
  ADD CONSTRAINT `FK_ai_messages_conversation`
  FOREIGN KEY (`conversation_id`) REFERENCES `ai_conversations` (`id`);
