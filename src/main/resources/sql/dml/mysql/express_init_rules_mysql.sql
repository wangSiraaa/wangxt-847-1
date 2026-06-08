INSERT IGNORE INTO express_reminder_rule 
(pk_rule, pk_group, pk_org, rule_code, rule_name, cond_vip, cond_large, cond_remote, cond_min_weight, cond_area_code,
 reminder_type, retention_days, max_reminder_count, priority, enabled, remark,
 creator, creationtime, modifier, modifiedtime, dr, ts)
VALUES
('RULE_VIP_001', 'TEST_GROUP_001', 'TEST_ORG_001', 'VIP_REMINDER', 'VIP客户催领规则', 1, NULL, NULL, NULL, NULL,
 2, 1, 3, 100, 1, 'VIP客户优先电话催领', 'TEST_USER_001', NOW(), 'TEST_USER_001', NOW(), 0, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')),

('RULE_LARGE_001', 'TEST_GROUP_001', 'TEST_ORG_001', 'LARGE_REMINDER', '大件包裹催领规则', NULL, 1, NULL, NULL, NULL,
 4, 1, 3, 90, 1, '大件包裹上门通知', 'TEST_USER_001', NOW(), 'TEST_USER_001', NOW(), 0, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')),

('RULE_REMOTE_001', 'TEST_GROUP_001', 'TEST_ORG_001', 'REMOTE_REMINDER', '偏远地区催领规则', NULL, NULL, 1, NULL, NULL,
 2, 2, 3, 80, 1, '偏远地区优先电话催领', 'TEST_USER_001', NOW(), 'TEST_USER_001', NOW(), 0, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')),

('RULE_HEAVY_001', 'TEST_GROUP_001', 'TEST_ORG_001', 'HEAVY_REMINDER', '重物包裹催领规则', NULL, NULL, NULL, 10.00, NULL,
 4, 1, 3, 70, 1, '10kg以上重物上门通知', 'TEST_USER_001', NOW(), 'TEST_USER_001', NOW(), 0, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')),

('RULE_AREA_A01', 'TEST_GROUP_001', 'TEST_ORG_001', 'AREA_A01_REMINDER', '片区A01催领规则', NULL, NULL, NULL, NULL, 'A01',
 3, 2, 3, 60, 1, '片区A01使用APP推送', 'TEST_USER_001', NOW(), 'TEST_USER_001', NOW(), 0, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')),

('RULE_DEFAULT_001', 'TEST_GROUP_001', 'TEST_ORG_001', 'DEFAULT_REMINDER', '默认催领规则', NULL, NULL, NULL, NULL, NULL,
 1, 3, 5, 1, 1, '默认短信催领，3天超期', 'TEST_USER_001', NOW(), 'TEST_USER_001', NOW(), 0, DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s'));
