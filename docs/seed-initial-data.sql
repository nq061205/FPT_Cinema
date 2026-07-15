-- FPT Cinema initial catalog seed
-- Idempotent: safe to run again. It does not create fake bookings/payments/reviews.
-- Run against the same MySQL database as the backend.

START TRANSACTION;

-- Add the combo products that are missing from the initial catalog.
INSERT INTO products (name, product_type, price, is_active)
SELECT 'Combo Solo', 'COMBO', 99000, TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Combo Solo');

INSERT INTO products (name, product_type, price, is_active)
SELECT 'Combo Couple', 'COMBO', 179000, TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Combo Couple');

INSERT INTO products (name, product_type, price, is_active)
SELECT 'Combo Family', 'COMBO', 229000, TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Combo Family');

INSERT INTO products (name, product_type, price, is_active)
SELECT 'Combo Kids', 'COMBO', 79000, TRUE
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Combo Kids');

-- Give each active promotion to each customer once.
INSERT INTO user_promotions (user_id, promotion_id, status, assigned_at)
SELECT u.id, p.id, 'AVAILABLE', CURRENT_TIMESTAMP
FROM users u
JOIN roles role ON role.id = u.role_id
JOIN promotions p ON p.is_active = TRUE
WHERE LOWER(role.role_name) = 'customer'
  AND NOT EXISTS (
      SELECT 1
      FROM user_promotions up
      WHERE up.user_id = u.id
        AND up.promotion_id = p.id
  );

-- Complete the seat maps for the main active rooms. Existing seats are preserved.
-- Rooms 21/22 are intentionally excluded: room 21 is a test room and room 22 is CLOSED.
INSERT INTO seats (room_id, seat_row, seat_number, seat_type, status)
SELECT r.id,
       seat_rows.seat_row,
       seat_numbers.seat_number,
       CASE
           WHEN r.room_type = 'VIP' THEN
               CASE WHEN seat_rows.seat_row IN ('E', 'F') THEN 'COUPLE' ELSE 'VIP' END
           WHEN r.room_type IN ('IMAX', 'DOLBY') THEN
               CASE
                   WHEN seat_rows.seat_row = 'F' THEN 'PREMIUM'
                   WHEN seat_rows.seat_row IN ('C', 'D', 'E') THEN 'VIP'
                   ELSE 'NORMAL'
               END
           WHEN r.room_type = 'FOUR_DX' THEN 'PREMIUM'
           ELSE
               CASE WHEN seat_rows.seat_row IN ('E', 'F') THEN 'VIP' ELSE 'NORMAL' END
       END,
       'ACTIVE'
FROM rooms r
CROSS JOIN (
    SELECT 'A' AS seat_row UNION ALL SELECT 'B' UNION ALL SELECT 'C'
    UNION ALL SELECT 'D' UNION ALL SELECT 'E' UNION ALL SELECT 'F'
) seat_rows
CROSS JOIN (
    SELECT 1 AS seat_number UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8
) seat_numbers
WHERE r.id IN (1, 2, 3, 4, 5, 6, 7, 8)
  AND r.status = 'ACTIVE'
  AND NOT EXISTS (
      SELECT 1
      FROM seats existing_seat
      WHERE existing_seat.room_id = r.id
        AND existing_seat.seat_row = seat_rows.seat_row
        AND existing_seat.seat_number = seat_numbers.seat_number
  );

-- A two-show-per-room slate for the next seven days. Only NOW_SHOWING movies
-- and ACTIVE rooms are eligible. The overlap guard also protects user-created
-- showtimes if this script is run again later.
CREATE TEMPORARY TABLE seed_showtime_template (
    movie_id INT NOT NULL,
    room_id INT NOT NULL,
    start_time TIME NOT NULL,
    base_price DECIMAL(12, 2) NOT NULL,
    PRIMARY KEY (movie_id, room_id, start_time)
);

INSERT INTO seed_showtime_template (movie_id, room_id, start_time, base_price) VALUES
    (3, 1, '09:00:00',  75000),
    (2, 1, '14:00:00',  75000),
    (4, 2, '10:00:00',  80000),
    (16, 2, '15:00:00', 80000),
    (2, 3, '09:30:00',  80000),
    (3, 3, '16:00:00',  80000),
    (16, 4, '10:30:00', 100000),
    (4, 4, '18:30:00', 100000),
    (3, 5, '11:00:00', 100000),
    (2, 5, '19:00:00', 100000),
    (2, 6, '10:00:00', 120000),
    (4, 6, '18:00:00', 120000),
    (4, 7, '09:00:00', 130000),
    (16, 7, '16:30:00', 130000),
    (16, 8, '11:30:00', 110000),
    (3, 8, '19:30:00', 110000);

CREATE TEMPORARY TABLE seed_day_offsets (day_offset INT PRIMARY KEY);
INSERT INTO seed_day_offsets (day_offset) VALUES (1), (2), (3), (4), (5), (6), (7);

INSERT INTO showtimes (
    movie_id,
    room_id,
    start_time,
    base_price,
    cleaning_buffer_minutes,
    status
)
SELECT t.movie_id,
       t.room_id,
       TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY), t.start_time),
       CASE
           WHEN DAYOFWEEK(DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY)) IN (1, 7)
               THEN t.base_price + 20000
           ELSE t.base_price
       END,
       15,
       'OPEN'
FROM seed_showtime_template t
JOIN seed_day_offsets d
JOIN movies m ON m.id = t.movie_id AND m.status = 'NOW_SHOWING'
JOIN rooms r ON r.id = t.room_id AND r.status = 'ACTIVE'
WHERE (
    SELECT COUNT(*)
    FROM showtimes daily_showtime
    WHERE daily_showtime.room_id = t.room_id
      AND DATE(daily_showtime.start_time) = DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY)
      AND daily_showtime.status NOT IN ('CANCELLED', 'FINISHED')
) < 8
  AND NOT EXISTS (
      SELECT 1
      FROM showtimes existing_showtime
      JOIN movies existing_movie ON existing_movie.id = existing_showtime.movie_id
      WHERE existing_showtime.room_id = t.room_id
        AND existing_showtime.status NOT IN ('CANCELLED', 'FINISHED')
        AND existing_showtime.start_time < DATE_ADD(
            TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY), t.start_time),
            INTERVAL (m.duration_minutes + 15) MINUTE
        )
        AND DATE_ADD(
            existing_showtime.start_time,
            INTERVAL (existing_movie.duration_minutes + existing_showtime.cleaning_buffer_minutes) MINUTE
        ) > TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY), t.start_time)
  )
  AND NOT EXISTS (
      SELECT 1
      FROM showtimes same_showtime
      WHERE same_showtime.movie_id = t.movie_id
        AND same_showtime.room_id = t.room_id
        AND same_showtime.start_time = TIMESTAMP(
            DATE_ADD(CURDATE(), INTERVAL d.day_offset DAY),
            t.start_time
        )
  );

DROP TEMPORARY TABLE seed_day_offsets;
DROP TEMPORARY TABLE seed_showtime_template;

COMMIT;
