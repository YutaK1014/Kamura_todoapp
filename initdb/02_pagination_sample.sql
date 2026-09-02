INSERT INTO todos (title, detail, category, priority, due_date, completed)
SELECT sample.title, 'pagination-test', CONVERT(0xE38387E382B6E382A4E383B3 USING utf8mb4), 2, sample.due_date, FALSE
FROM (
    SELECT 'todo-06' AS title, '2026-09-06' AS due_date
    UNION ALL SELECT 'todo-07', '2026-09-07'
    UNION ALL SELECT 'todo-08', '2026-09-08'
    UNION ALL SELECT 'todo-09', '2026-09-09'
    UNION ALL SELECT 'todo-10', '2026-09-10'
    UNION ALL SELECT 'todo-11', '2026-09-11'
    UNION ALL SELECT 'todo-12', '2026-09-12'
    UNION ALL SELECT 'todo-13', '2026-09-13'
    UNION ALL SELECT 'todo-14', '2026-09-14'
    UNION ALL SELECT 'todo-15', '2026-09-15'
) AS sample
WHERE NOT EXISTS (
    SELECT 1 FROM todos existing WHERE existing.title = sample.title
);
