-- =====================================================================
-- 快递驿站催领规则表 (H2 版本)
-- 兼容 H2 2.x + MySQL 兼容模式
-- =====================================================================

CREATE TABLE IF NOT EXISTS express_reminder_rule (
    pk_rule             CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    rule_code           VARCHAR(50)     NOT NULL,
    rule_name           VARCHAR(100)    NOT NULL,
    cond_vip            INT,
    cond_large          INT,
    cond_remote         INT,
    min_weight          DECIMAL(28,8),
    max_weight          DECIMAL(28,8),
    area_code           VARCHAR(50),
    reminder_type       INT             NOT NULL,
    retention_days      INT             DEFAULT 3,
    reminder_interval   INT             DEFAULT 1,
    max_reminder_count  INT             DEFAULT 5,
    priority            INT             DEFAULT 1,
    enabled             INT             DEFAULT 1,
    remark              VARCHAR(500),
    creator             CHAR(20),
    creationtime        TIMESTAMP,
    modifier            CHAR(20),
    modifiedtime        TIMESTAMP,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19),
    CONSTRAINT pk_express_reminder_rule PRIMARY KEY (pk_rule)
);

-- 唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS uk_express_reminder_rule_code 
    ON express_reminder_rule(pk_org, rule_code, dr);

-- 索引：组织+启用状态查询
CREATE INDEX IF NOT EXISTS idx_express_reminder_rule_org 
    ON express_reminder_rule(pk_org, enabled, dr, priority DESC);

-- 添加注释
COMMENT ON TABLE express_reminder_rule IS '快递驿站催领规则表';
COMMENT ON COLUMN express_reminder_rule.pk_rule IS '主键';
COMMENT ON COLUMN express_reminder_rule.pk_group IS '集团主键';
COMMENT ON COLUMN express_reminder_rule.pk_org IS '组织主键';
COMMENT ON COLUMN express_reminder_rule.rule_code IS '规则编码';
COMMENT ON COLUMN express_reminder_rule.rule_name IS '规则名称';
COMMENT ON COLUMN express_reminder_rule.cond_vip IS 'VIP条件:null-不限制,1-仅VIP';
COMMENT ON COLUMN express_reminder_rule.cond_large IS '大件条件:null-不限制,1-仅大件';
COMMENT ON COLUMN express_reminder_rule.cond_remote IS '偏远条件:null-不限制,1-仅偏远';
COMMENT ON COLUMN express_reminder_rule.min_weight IS '最小重量(kg)';
COMMENT ON COLUMN express_reminder_rule.max_weight IS '最大重量(kg)';
COMMENT ON COLUMN express_reminder_rule.area_code IS '片区编码';
COMMENT ON COLUMN express_reminder_rule.reminder_type IS '催领方式:1-短信,2-电话,3-APP推送,4-上门通知';
COMMENT ON COLUMN express_reminder_rule.retention_days IS '滞留天数(超过此天数开始催领)';
COMMENT ON COLUMN express_reminder_rule.reminder_interval IS '催领间隔(天)';
COMMENT ON COLUMN express_reminder_rule.max_reminder_count IS '最大催领次数';
COMMENT ON COLUMN express_reminder_rule.priority IS '优先级(数值越大优先级越高)';
COMMENT ON COLUMN express_reminder_rule.enabled IS '是否启用:0-禁用,1-启用';
COMMENT ON COLUMN express_reminder_rule.remark IS '备注';
COMMENT ON COLUMN express_reminder_rule.creator IS '创建人';
COMMENT ON COLUMN express_reminder_rule.creationtime IS '创建时间';
COMMENT ON COLUMN express_reminder_rule.modifier IS '修改人';
COMMENT ON COLUMN express_reminder_rule.modifiedtime IS '修改时间';
COMMENT ON COLUMN express_reminder_rule.dr IS '删除标志:0-未删除,1-已删除';
COMMENT ON COLUMN express_reminder_rule.ts IS '时间戳';
