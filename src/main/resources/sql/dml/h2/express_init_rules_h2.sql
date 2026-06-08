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
        'VIP客户滞留件优先电话催领' AS rule_desc,
        100 AS priority,
        1 AS enabled,
        1 AS condition_vip,
        CAST(NULL AS INT) AS condition_large,
        CAST(NULL AS INT) AS condition_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        2 AS reminder_type,
        1 AS retention_days,
        '【VIP服务】尊敬的{receiver_name}，您的包裹{express_no}已到达驿站，请尽快凭取件码{pickup_code}取件，VIP专线：400-888-8888' AS reminder_content,
        '初始化' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, rule_desc, priority, enabled,
    condition_vip, condition_large, condition_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_content, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.rule_desc, s.priority, s.enabled,
    s.condition_vip, s.condition_large, s.condition_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_content, s.remark,
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
        '大件包裹安排上门通知' AS rule_desc,
        90 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS condition_vip,
        1 AS condition_large,
        CAST(NULL AS INT) AS condition_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        4 AS reminder_type,
        2 AS retention_days,
        '【大件提醒】尊敬的{receiver_name}，您的大件包裹{express_no}已到达，我们将安排送货上门，请保持电话畅通' AS reminder_content,
        '初始化' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, rule_desc, priority, enabled,
    condition_vip, condition_large, condition_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_content, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.rule_desc, s.priority, s.enabled,
    s.condition_vip, s.condition_large, s.condition_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_content, s.remark,
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
        '偏远地区使用APP推送通知' AS rule_desc,
        80 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS condition_vip,
        CAST(NULL AS INT) AS condition_large,
        1 AS condition_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        3 AS reminder_type,
        3 AS retention_days,
        '【偏远提醒】尊敬的{receiver_name}，您的包裹{express_no}已到达驿站，由于您所在区域较远，请尽快安排取件' AS reminder_content,
        '初始化' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, rule_desc, priority, enabled,
    condition_vip, condition_large, condition_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_content, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.rule_desc, s.priority, s.enabled,
    s.condition_vip, s.condition_large, s.condition_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_content, s.remark,
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
        '超过10kg的包裹电话通知' AS rule_desc,
        70 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS condition_vip,
        CAST(NULL AS INT) AS condition_large,
        CAST(NULL AS INT) AS condition_remote,
        10.0 AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        2 AS reminder_type,
        2 AS retention_days,
        '【重物提醒】尊敬的{receiver_name}，您的包裹{express_no}重达{weight}kg，建议安排搬运工具前来取件' AS reminder_content,
        '初始化' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, rule_desc, priority, enabled,
    condition_vip, condition_large, condition_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_content, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.rule_desc, s.priority, s.enabled,
    s.condition_vip, s.condition_large, s.condition_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_content, s.remark,
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
        'A01片区使用短信催领' AS rule_desc,
        60 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS condition_vip,
        CAST(NULL AS INT) AS condition_large,
        CAST(NULL AS INT) AS condition_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        'A01' AS area_code,
        1 AS reminder_type,
        3 AS retention_days,
        '【A01片区】尊敬的{receiver_name}，您的包裹{express_no}已到达A01驿站，取件码{pickup_code}，请3日内取件' AS reminder_content,
        '初始化' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, rule_desc, priority, enabled,
    condition_vip, condition_large, condition_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_content, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.rule_desc, s.priority, s.enabled,
    s.condition_vip, s.condition_large, s.condition_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_content, s.remark,
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
        '普通包裹默认短信催领' AS rule_desc,
        1 AS priority,
        1 AS enabled,
        CAST(NULL AS INT) AS condition_vip,
        CAST(NULL AS INT) AS condition_large,
        CAST(NULL AS INT) AS condition_remote,
        CAST(NULL AS DECIMAL(28,8)) AS min_weight,
        CAST(NULL AS DECIMAL(28,8)) AS max_weight,
        CAST(NULL AS VARCHAR(50)) AS area_code,
        1 AS reminder_type,
        3 AS retention_days,
        '【取件提醒】尊敬的{receiver_name}，您的包裹{express_no}已到达驿站，取件码{pickup_code}，请及时取件' AS reminder_content,
        '初始化' AS remark,
        'system' AS creator,
        CURRENT_TIMESTAMP AS creationtime,
        'system' AS modifier,
        CURRENT_TIMESTAMP AS modifiedtime,
        0 AS dr,
        FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm:ss') AS ts
) s
ON (t.pk_rule = s.pk_rule)
WHEN NOT MATCHED THEN INSERT (
    pk_rule, pk_group, pk_org, rule_code, rule_name, rule_desc, priority, enabled,
    condition_vip, condition_large, condition_remote, min_weight, max_weight, area_code,
    reminder_type, retention_days, reminder_content, remark,
    creator, creationtime, modifier, modifiedtime, dr, ts
) VALUES (
    s.pk_rule, s.pk_group, s.pk_org, s.rule_code, s.rule_name, s.rule_desc, s.priority, s.enabled,
    s.condition_vip, s.condition_large, s.condition_remote, s.min_weight, s.max_weight, s.area_code,
    s.reminder_type, s.retention_days, s.reminder_content, s.remark,
    s.creator, s.creationtime, s.modifier, s.modifiedtime, s.dr, s.ts
);
