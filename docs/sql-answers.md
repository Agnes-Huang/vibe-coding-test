# SQL 编程题答案

## 题目1：统计每门课程选课人数 - GROUP BY

```sql
SELECT
    c.course_id,
    c.course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY enroll_count DESC;
```

说明：
- 使用 `GROUP BY` 对课程维度聚合。
- `LEFT JOIN` 可以保留无人选课课程（`enroll_count = 0`）。

## 题目2：统计选课人数超过50的专业课 - GROUP BY + HAVING

```sql
SELECT
    c.course_id,
    c.course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
JOIN enrollments e ON c.course_id = e.course_id
WHERE c.course_type = '专业课'
GROUP BY c.course_id, c.course_name
HAVING COUNT(e.student_id) > 50
ORDER BY enroll_count ASC;
```

说明：
- `WHERE` 先过滤“专业课”。
- `HAVING` 对分组后的聚合人数进行过滤（大于50）。
