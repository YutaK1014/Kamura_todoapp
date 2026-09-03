CREATE TABLE IF NOT EXISTS player_status (
    id TINYINT NOT NULL,
    total_points INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT chk_player_status_id CHECK (id = 1),
    CONSTRAINT chk_player_status_points CHECK (total_points >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO player_status (id, total_points) VALUES (1, 0)
ON DUPLICATE KEY UPDATE id = id;
