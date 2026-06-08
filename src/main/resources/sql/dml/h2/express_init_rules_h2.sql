-- =====================================================================
-- 初始化催领规则数据 (H2 版本)
-- 使用 MERGE INTO 实现幂等插入，重复执行不会报错
-- 兼容 H2 2.x
-- =====================================================================

-- 规则1: VIP客户优先电话催领 (优先级100)
MERGE INTO express_reminder_rule t
USING (
    SELECT 
        'RULE_VIP_001' AS pk_rule,
        '0001' AS pk_group,
        '0001' AS pk_org,
        'VIP_REMIND' AS rule_code,
        'VIP客户催领' AS rule_name,
        100 AS priority,
        1 AS enabled,
        1 AS cond_vip,
        CAST(NULL AS INT) AS cond_large,
        CAST(NULL AS INT) AS cond_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        2 AS reminder_type,
        1 AS retention_days,
        1 AS reminder_interval,
        3 AS max_reminder_count,
        'VIP客户滞留件优先电话催领' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, priority, enabled,
    cond_vip, cond_large, cond_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_interval, max_reminder_count, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.priority, s.enabled,
    s.cond_vip, s.cond_large, s.cond_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_interval, s.max_reminder_count, s.remark,
    s.creator, s.creationtime, s.modifier, s.modifiedtime, s.dr, s.ts
);

-- 规则2: 大件包裹上门通知 (优先级90)
MERGE INTO express_reminder_rule t
USING (
    SELECT 
        'RULE_LARGE_001' AS pk_rule,
        '0001' AS pk_group,
        '0001' AS pk_org,
        'LARGE_PARCEL' AS rule_code,
        '大件包裹催领' AS rule_name,
        90 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS cond_vip,
        1 AS cond_large,
        CAST(NULL AS INT) AS cond_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        4 AS reminder_type,
        1 AS retention_days,
        1 AS reminder_interval,
        3 AS max_reminder_count,
        '大件包裹安排上门通知' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, priority, enabled,
    cond_vip, cond_large, cond_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_interval, max_reminder_count, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.priority, s.enabled,
    s.cond_vip, s.cond_large, s.cond_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_interval, s.max_reminder_count, s.remark,
    s.creator, s.creationtime, s.modifier, s.modifiedtime, s.dr, s.ts
);

-- 规则3: 偏远地区APP推送 (优先级80)
MERGE INTO express_reminder_rule t
USING (
    SELECT 
        'RULE_REMOTE_001' AS pk_rule,
        '0001' AS pk_group,
        '0001' AS pk_org,
        'REMOTE_AREA' AS rule_code,
        '偏远地区催领' AS rule_name,
        80 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS cond_vip,
        CAST(NULL AS INT) AS cond_large,
        1 AS cond_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        3 AS reminder_type,
        2 AS retention_days,
        2 AS reminder_interval,
        4 AS max_reminder_count,
        '偏远地区使用APP推送通知' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, priority, enabled,
    cond_vip, cond_large, cond_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_interval, max_reminder_count, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.priority, s.enabled,
    s.cond_vip, s.cond_large, s.cond_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_interval, s.max_reminder_count, s.remark,
    s.creator, s.creationtime, s.modifier, s.modifiedtime, s.dr, s.ts
);

-- 规则4: 重物包裹优先处理 (优先级70)
MERGE INTO express_reminder_rule t
USING (
    SELECT 
        'RULE_HEAVY_001' AS pk_rule,
        '0001' AS pk_group,
        '0001' AS pk_org,
        'HEAVY_WEIGHT' AS rule_code,
        '重物包裹催领' AS rule_name,
        70 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS cond_vip,
        CAST(NULL AS INT) AS cond_large,
        CAST(NULL AS INT) AS cond_remote,
        10.0 AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        2 AS reminder_type,
        1 AS retention_days,
        1 AS reminder_interval,
        3 AS max_reminder_count,
        '超过10kg的包裹电话通知' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, priority, enabled,
    cond_vip, cond_large, cond_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_interval, max_reminder_count, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.priority, s.enabled,
    s.cond_vip, s.cond_large, s.cond_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_interval, s.max_reminder_count, s.remark,
    s.creator, s.creationtime, s.modifier, s.modifiedtime, s.dr, s.ts
);

-- 规则5: 片区A01短信催领 (优先级60)
MERGE INTO express_reminder_rule t
USING (
    SELECT 
        'RULE_AREA_A01' AS pk_rule,
        '0001' AS pk_group,
        '0001' AS pk_org,
        'AREA_A01' AS rule_code,
        'A01片区催领' AS rule_name,
        60 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS cond_vip,
        CAST(NULL AS INT) AS cond_large,
        CAST(NULL AS INT) AS cond_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        'A01' AS area_code,
        1 AS reminder_type,
        2 AS retention_days,
        1 AS reminder_interval,
        5 AS max_reminder_count,
        'A01片区使用短信催领' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, priority, enabled,
    cond_vip, cond_large, cond_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_interval, max_reminder_count, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.priority, s.enabled,
    s.cond_vip, s.cond_large, s.cond_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_interval, s.max_reminder_count, s.remark,
    s.creator, s.creationtime, s.modifier, s.modifiedtime, s.dr, s.ts
);

-- 规则6: 默认规则-短信催领 (优先级1)
MERGE INTO express_reminder_rule t
USING (
    SELECT 
        'RULE_DEFAULT_001' AS pk_rule,
        '0001' AS pk_group,
        '0001' AS pk_org,
        'DEFAULT_REMIND' AS rule_code,
        '默认催领规则' AS rule_name,
        1 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS cond_vip,
        CAST(NULL AS INT) AS cond_large,
        CAST(NULL AS INT) AS cond_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        1 AS reminder_type,
        3 AS retention_days,
        1 AS reminder_interval,
        5 AS max_reminder_count,
        '普通包裹默认短信催领' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, priority, enabled,
    cond_vip, cond_large, cond_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_interval, max_reminder_count, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.priority, s.enabled,
    s.cond_vip, s.cond_large, s.cond_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_interval, s.max_reminder_count, s.remark,
    s.creator, s.creationtime, s.modifier, s.modifiedtime, s.dr, s.ts
);
