-- =====================================================================
-- 快递驿站滞留件催领系统 - 催领规则表
-- 兼容数据库：Oracle 11g+ / 达梦 8+
-- 创建日期：2026-06-08
-- =====================================================================

-- 催领规则表
CREATE TABLE express_reminder_rule (
    pk_rule             CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    rule_name           VARCHAR2(100)   NOT NULL,
    cond_vip            INT,
    cond_large          INT,
    cond_remote         INT,
    cond_weight_min     DECIMAL(28,8),
    cond_area           VARCHAR2(50),
    reminder_type       INT             NOT NULL,
    retention_days      INT             DEFAULT 3,
    reminder_interval   INT             DEFAULT 1,
    max_reminder_count  INT             DEFAULT 5,
    priority            INT             DEFAULT 1,
    enabled             INT             DEFAULT 1,
    remark              VARCHAR2(500),
    creator             CHAR(20),
    creationtime        DATE,
    modifier            CHAR(20),
    modifiedtime        DATE,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19)
);

-- 主键约束
ALTER TABLE express_reminder_rule ADD CONSTRAINT pk_express_reminder_rule PRIMARY KEY (pk_rule);

-- 索引：组织+启用状态查询
CREATE INDEX idx_reminder_rule_org ON express_reminder_rule(pk_org, enabled, dr, priority DESC);

-- 备注：
-- reminder_type: 1-短信, 2-电话, 3-APP推送, 4-上门通知
-- cond_vip: NULL-不限制, 1-仅VIP
-- cond_large: NULL-不限制, 1-仅大件
-- cond_remote: NULL-不限制, 1-仅偏远
-- enabled: 0-禁用, 1-启用
