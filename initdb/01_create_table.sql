CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255) NULL,
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL DEFAULT 2,
    due_date DATE NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT chk_todos_title_not_blank
        CHECK (CHAR_LENGTH(TRIM(REPLACE(title, '　', ''))) > 0),
    CONSTRAINT chk_todos_priority
        CHECK (priority IN (1, 2, 3)),
    CONSTRAINT chk_todos_category
        CHECK (category IN ('デザイン', 'マーケティング', 'プログラミング', '資格', '就職活動') )
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;

CREATE TABLE operation_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    action VARCHAR(10) NOT NULL,
    todo_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT chk_operation_logs_action
        CHECK (action IN ('登録', '編集', '削除'))
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_unicode_ci;
