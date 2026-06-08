-- =====================================================================
-- 快递驿站滞留件催领系统 - 初始化默认催领规则数据
-- 兼容数据库：Oracle 11g+ / 达梦 8+
-- 执行顺序：先执行 DDL 建表，再执行本脚本初始化规则
-- =====================================================================

-- 清理旧数据（可选）
-- DELETE FROM express_reminder_rule WHERE pk_org IS NULL OR pk_org = 'GLOBAL';

-- =====================================================================
-- 集团级默认规则（所有组织通用，可被组织级规则覆盖）
-- =====================================================================

-- 规则1：VIP客户专属规则 - 滞留1天后电话催领，最多3次
MERGE INTO express_reminder_rule t
USING (
    SELECT 'RULE_VIP_001' AS pk_rule,
           '00000000000000000000' AS pk_group,
           NULL AS pk_org,
           'VIP_REMIND' AS rule_code,
           'VIP客户优先催领规则' AS rule_name,
           1 AS cond_vip,
           NULL AS cond_large,
           NULL AS cond_remote,
           NULL AS min_weight,
           NULL AS max_weight,
           NULL AS area_code,
           2 AS reminder_type,
           1 AS retention_days,
           1 AS reminder_interval,
           3 AS max_reminder_count,
           100 AS priority,
           1 AS enabled,
           'VIP客户滞留1天后自动电话催领' AS remark,
           'SYS_INIT' AS creator,
           SYSDATE AS creationtime,
           'SYS_INIT' AS modifier,
           SYSDATE AS modifiedtime,
           0 AS dr,
           TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS ts
    FROM dual
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN
    INSERT (pk_rule, pk_group, pk_org, rule_code, rule_name, cond_vip, cond_large, cond_remote,
            min_weight, max_weight, area_code, reminder_type, retention_days, reminder_interval,
            max_reminder_count, priority, enabled, remark, creator, creationtime,
            modifier, modifiedtime, dr, ts)
    VALUES (s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.cond_vip, s.cond_large, s.cond_remote,
            s.min_weight, s.max_weight, s.area_code, s.reminder_type, s.retention_days, s.reminder_interval,
            s.max_reminder_count, s.priority, s.enabled, s.remark, s.creator, s.creationtime,
            s.modifier, s.modifiedtime, s.dr, s.ts);

-- 规则2：大件包裹规则 - 滞留1天后上门通知，最多3次
MERGE INTO express_reminder_rule t
USING (
    SELECT 'RULE_LARGE_001' AS pk_rule,
           '00000000000000000000' AS pk_group,
           NULL AS pk_org,
           'LARGE_PARCEL' AS rule_code,
           '大件包裹上门催领规则' AS rule_name,
           NULL AS cond_vip,
           1 AS cond_large,
           NULL AS cond_remote,
           NULL AS min_weight,
           NULL AS max_weight,
           NULL AS area_code,
           4 AS reminder_type,
           1 AS retention_days,
           1 AS reminder_interval,
           3 AS max_reminder_count,
           90 AS priority,
           1 AS enabled,
           '大件包裹滞留1天后自动上门通知' AS remark,
           'SYS_INIT' AS creator,
           SYSDATE AS creationtime,
           'SYS_INIT' AS modifier,
           SYSDATE AS modifiedtime,
           0 AS dr,
           TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS ts
    FROM dual
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN
    INSERT (pk_rule, pk_group, pk_org, rule_code, rule_name, cond_vip, cond_large, cond_remote,
            min_weight, max_weight, area_code, reminder_type, retention_days, reminder_interval,
            max_reminder_count, priority, enabled, remark, creator, creationtime,
            modifier, modifiedtime, dr, ts)
    VALUES (s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.cond_vip, s.cond_large, s.cond_remote,
            s.min_weight, s.max_weight, s.area_code, s.reminder_type, s.retention_days, s.reminder_interval,
            s.max_reminder_count, s.priority, s.enabled, s.remark, s.creator, s.creationtime,
            s.modifier, s.modifiedtime, s.dr, s.ts);

-- 规则3：偏远地区规则 - 滞留2天后APP推送，最多4次
MERGE INTO express_reminder_rule t
USING (
    SELECT 'RULE_REMOTE_001' AS pk_rule,
           '00000000000000000000' AS pk_group,
           NULL AS pk_org,
           'REMOTE_AREA' AS rule_code,
           '偏远地区APP催领规则' AS rule_name,
           NULL AS cond_vip,
           NULL AS cond_large,
           1 AS cond_remote,
           NULL AS min_weight,
           NULL AS max_weight,
           NULL AS area_code,
           3 AS reminder_type,
           2 AS retention_days,
           2 AS reminder_interval,
           4 AS max_reminder_count,
           80 AS priority,
           1 AS enabled,
           '偏远地区滞留2天后自动APP推送' AS remark,
           'SYS_INIT' AS creator,
           SYSDATE AS creationtime,
           'SYS_INIT' AS modifier,
           SYSDATE AS modifiedtime,
           0 AS dr,
           TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS ts
    FROM dual
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN
    INSERT (pk_rule, pk_group, pk_org, rule_code, rule_name, cond_vip, cond_large, cond_remote,
            min_weight, max_weight, area_code, reminder_type, retention_days, reminder_interval,
            max_reminder_count, priority, enabled, remark, creator, creationtime,
            modifier, modifiedtime, dr, ts)
    VALUES (s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.cond_vip, s.cond_large, s.cond_remote,
            s.min_weight, s.max_weight, s.area_code, s.reminder_type, s.retention_days, s.reminder_interval,
            s.max_reminder_count, s.priority, s.enabled, s.remark, s.creator, s.creationtime,
            s.modifier, s.modifiedtime, s.dr, s.ts);

-- 规则4：重物规则 - 重量超过10kg的包裹，滞留1天后电话催领
MERGE INTO express_reminder_rule t
USING (
    SELECT 'RULE_HEAVY_001' AS pk_rule,
           '00000000000000000000' AS pk_group,
           NULL AS pk_org,
           'HEAVY_WEIGHT' AS rule_code,
           '重物包裹优先催领规则' AS rule_name,
           NULL AS cond_vip,
           NULL AS cond_large,
           NULL AS cond_remote,
           10 AS min_weight,
           NULL AS max_weight,
           NULL AS area_code,
           2 AS reminder_type,
           1 AS retention_days,
           1 AS reminder_interval,
           3 AS max_reminder_count,
           70 AS priority,
           1 AS enabled,
           '超过10kg的重物包裹滞留1天后电话催领' AS remark,
           'SYS_INIT' AS creator,
           SYSDATE AS creationtime,
           'SYS_INIT' AS modifier,
           SYSDATE AS modifiedtime,
           0 AS dr,
           TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS ts
    FROM dual
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN
    INSERT (pk_rule, pk_group, pk_org, rule_code, rule_name, cond_vip, cond_large, cond_remote,
            min_weight, max_weight, area_code, reminder_type, retention_days, reminder_interval,
            max_reminder_count, priority, enabled, remark, creator, creationtime,
            modifier, modifiedtime, dr, ts)
    VALUES (s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.cond_vip, s.cond_large, s.cond_remote,
            s.min_weight, s.max_weight, s.area_code, s.reminder_type, s.retention_days, s.reminder_interval,
            s.max_reminder_count, s.priority, s.enabled, s.remark, s.creator, s.creationtime,
            s.modifier, s.modifiedtime, s.dr, s.ts);

-- 规则5：片区A专属规则 - 片区A01的包裹滞留2天后短信催领
MERGE INTO express_reminder_rule t
USING (
    SELECT 'RULE_AREA_A01' AS pk_rule,
           '00000000000000000000' AS pk_group,
           NULL AS pk_org,
           'AREA_A01' AS rule_code,
           '片区A01专属催领规则' AS rule_name,
           NULL AS cond_vip,
           NULL AS cond_large,
           NULL AS cond_remote,
           NULL AS min_weight,
           NULL AS max_weight,
           'A01' AS area_code,
           1 AS reminder_type,
           2 AS retention_days,
           1 AS reminder_interval,
           5 AS max_reminder_count,
           60 AS priority,
           1 AS enabled,
           '片区A01的包裹滞留2天后短信催领' AS remark,
           'SYS_INIT' AS creator,
           SYSDATE AS creationtime,
           'SYS_INIT' AS modifier,
           SYSDATE AS modifiedtime,
           0 AS dr,
           TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS ts
    FROM dual
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN
    INSERT (pk_rule, pk_group, pk_org, rule_code, rule_name, cond_vip, cond_large, cond_remote,
            min_weight, max_weight, area_code, reminder_type, retention_days, reminder_interval,
            max_reminder_count, priority, enabled, remark, creator, creationtime,
            modifier, modifiedtime, dr, ts)
    VALUES (s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.cond_vip, s.cond_large, s.cond_remote,
            s.min_weight, s.max_weight, s.area_code, s.reminder_type, s.retention_days, s.reminder_interval,
            s.max_reminder_count, s.priority, s.enabled, s.remark, s.creator, s.creationtime,
            s.modifier, s.modifiedtime, s.dr, s.ts);

-- 规则6：默认规则 - 不匹配任何特殊规则时使用，滞留3天后短信催领，最多5次
MERGE INTO express_reminder_rule t
USING (
    SELECT 'RULE_DEFAULT_001' AS pk_rule,
           '00000000000000000000' AS pk_group,
           NULL AS pk_org,
           'DEFAULT_REMIND' AS rule_code,
           '默认短信催领规则' AS rule_name,
           NULL AS cond_vip,
           NULL AS cond_large,
           NULL AS cond_remote,
           NULL AS min_weight,
           NULL AS max_weight,
           NULL AS area_code,
           1 AS reminder_type,
           3 AS retention_days,
           1 AS reminder_interval,
           5 AS max_reminder_count,
           1 AS priority,
           1 AS enabled,
           '普通包裹滞留3天后自动短信催领' AS remark,
           'SYS_INIT' AS creator,
           SYSDATE AS creationtime,
           'SYS_INIT' AS modifier,
           SYSDATE AS modifiedtime,
           0 AS dr,
           TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS ts
    FROM dual
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN
    INSERT (pk_rule, pk_group, pk_org, rule_code, rule_name, cond_vip, cond_large, cond_remote,
            min_weight, max_weight, area_code, reminder_type, retention_days, reminder_interval,
            max_reminder_count, priority, enabled, remark, creator, creationtime,
            modifier, modifiedtime, dr, ts)
    VALUES (s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.cond_vip, s.cond_large, s.cond_remote,
            s.min_weight, s.max_weight, s.area_code, s.reminder_type, s.retention_days, s.reminder_interval,
            s.max_reminder_count, s.priority, s.enabled, s.remark, s.creator, s.creationtime,
            s.modifier, s.modifiedtime, s.dr, s.ts);

COMMIT;

-- 查询已初始化的规则
SELECT pk_rule, rule_code, rule_name, cond_vip, cond_large, cond_remote, min_weight, max_weight, area_code,
       reminder_type, retention_days, priority, enabled
FROM express_reminder_rule
WHERE dr = 0
ORDER BY priority DESC;
