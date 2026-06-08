-- =====================================================================
-- 快递驿站滞留件催领系统 - 催领流水表
-- 兼容数据库：Oracle 11g+ / 达梦 8+
-- 创建日期：2026-06-08
-- =====================================================================

-- 催领流水表
CREATE TABLE express_reminder_log (
    pk_log              CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    pk_parcel           CHAR(20)        NOT NULL,
    pk_rule             CHAR(20),
    reminder_type       INT             NOT NULL,
    reminder_time       DATE            NOT NULL,
    reminder_status     INT             DEFAULT 0,
    reminder_content    VARCHAR2(1000),
    pickup_code         VARCHAR2(20),
    fail_reason         VARCHAR2(500),
    reminder_count      INT             DEFAULT 1,
    operator            CHAR(20),
    area_code           VARCHAR2(50),
    creator             CHAR(20),
    creationtime        DATE,
    modifier            CHAR(20),
    modifiedtime        DATE,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19)
);

-- 主键约束
ALTER TABLE express_reminder_log ADD CONSTRAINT pk_express_reminder_log PRIMARY KEY (pk_log);

-- 外键关联（可选，根据需要开启）
-- ALTER TABLE express_reminder_log ADD CONSTRAINT fk_reminder_log_parcel
--     FOREIGN KEY (pk_parcel) REFERENCES express_parcel(pk_parcel);
-- ALTER TABLE express_reminder_log ADD CONSTRAINT fk_reminder_log_rule
--     FOREIGN KEY (pk_rule) REFERENCES express_reminder_rule(pk_rule);

-- 索引：包裹查询催领记录
CREATE INDEX idx_reminder_log_parcel ON express_reminder_log(pk_parcel, dr, reminder_time DESC);

-- 索引：组织+片区查询（主管视角）
CREATE INDEX idx_reminder_log_area ON express_reminder_log(pk_org, area_code, dr, reminder_time DESC);

-- 索引：催领状态查询
CREATE INDEX idx_reminder_log_status ON express_reminder_log(pk_org, reminder_status, dr);

-- 索引：催领类型统计
CREATE INDEX idx_reminder_log_type ON express_reminder_log(pk_org, reminder_type, dr, reminder_time DESC);

-- 备注：
-- reminder_type: 1-短信, 2-电话, 3-APP推送, 4-上门通知
-- reminder_status: 0-待发送, 1-已发送, 2-发送失败, 3-已取消
