-- ================================================
-- 快递驿站催领系统 总控执行脚本
-- 适用数据库：Oracle 11g+ / 达梦 8+
-- 执行顺序：建表 → 初始化规则 → 验证数据
-- ================================================

-- ================================================
-- 第一步：创建数据表
-- ================================================
@ddl/express_parcel.sql
@ddl/express_reminder_rule.sql
@ddl/express_reminder_log.sql
@ddl/express_parcel_version_compare.sql

-- ================================================
-- 第二步：初始化默认催领规则
-- ================================================
@dml/express_init_rules.sql

-- ================================================
-- 第三步：验证数据完整性
-- ================================================
PROMPT ================================================
PROMPT 数据验证结果：
PROMPT ================================================

SELECT 'express_parcel 表记录数：' || COUNT(1) AS info
FROM express_parcel WHERE dr = 0;

SELECT 'express_reminder_rule 表记录数：' || COUNT(1) AS info
FROM express_reminder_rule WHERE dr = 0;

SELECT 'express_reminder_log 表记录数：' || COUNT(1) AS info
FROM express_reminder_log WHERE dr = 0;

SELECT 'express_parcel_version_compare 表记录数：' || COUNT(1) AS info
FROM express_parcel_version_compare WHERE dr = 0;

SELECT '初始化规则（按优先级）：' AS info FROM dual;
SELECT rule_name || ' (优先级:' || priority || ', 类型:' || reminder_type || ', 滞留天数:' || retention_days || ')' AS rule_info
FROM express_reminder_rule
WHERE dr = 0
ORDER BY priority DESC;

PROMPT ================================================
PROMPT 数据库初始化完成！
PROMPT ================================================
