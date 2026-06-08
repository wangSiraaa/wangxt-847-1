-- =====================================================================
-- 快递驿站催领流水表 (H2 版本)
-- 兼容 H2 2.x + MySQL 兼容模式
-- =====================================================================

CREATE TABLE IF NOT EXISTS express_reminder_log (
    pk_log              CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    pk_parcel           CHAR(20)        NOT NULL,
    pk_rule             CHAR(20),
    receiver_name       VARCHAR(50)     NOT NULL,
    receiver_phone      VARCHAR(20)     NOT NULL,
    express_no          VARCHAR(100)    NOT NULL,
    reminder_type       INT             NOT NULL,
    reminder_content    VARCHAR(1000)   NOT NULL,
    reminder_time       TIMESTAMP       NOT NULL,
    reminder_status     INT             DEFAULT 0,
    send_time           TIMESTAMP,
    fail_reason         VARCHAR(500),
    pickup_code         VARCHAR(20),
    area_code           VARCHAR(50),
    remark              VARCHAR(500),
    creator             CHAR(20),
    creationtime        TIMESTAMP,
    modifier            CHAR(20),
    modifiedtime        TIMESTAMP,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19),
    CONSTRAINT pk_express_reminder_log PRIMARY KEY (pk_log)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_reminder_log_parcel ON express_reminder_log(pk_parcel);
CREATE INDEX IF NOT EXISTS idx_reminder_log_time ON express_reminder_log(reminder_time);
CREATE INDEX IF NOT EXISTS idx_reminder_log_status ON express_reminder_log(reminder_status);
CREATE INDEX IF NOT EXISTS idx_reminder_log_area ON express_reminder_log(area_code);

-- 添加注释
COMMENT ON TABLE express_reminder_log IS '快递驿站催领流水表';
COMMENT ON COLUMN express_reminder_log.pk_log IS '主键';
COMMENT ON COLUMN express_reminder_log.pk_group IS '集团主键';
COMMENT ON COLUMN express_reminder_log.pk_org IS '组织主键';
COMMENT ON COLUMN express_reminder_log.pk_parcel IS '包裹主键(关联express_parcel.pk_parcel)';
COMMENT ON COLUMN express_reminder_log.pk_rule IS '规则主键(关联express_reminder_rule.pk_rule)';
COMMENT ON COLUMN express_reminder_log.receiver_name IS '收件人姓名';
COMMENT ON COLUMN express_reminder_log.receiver_phone IS '收件人电话';
COMMENT ON COLUMN express_reminder_log.express_no IS '快递单号';
COMMENT ON COLUMN express_reminder_log.reminder_type IS '催领方式:1-短信,2-电话,3-APP推送,4-上门通知';
COMMENT ON COLUMN express_reminder_log.reminder_content IS '催领内容';
COMMENT ON COLUMN express_reminder_log.reminder_time IS '催领时间';
COMMENT ON COLUMN express_reminder_log.reminder_status IS '催领状态:0-待发送,1-已发送,2-发送失败,3-已取消';
COMMENT ON COLUMN express_reminder_log.send_time IS '实际发送时间';
COMMENT ON COLUMN express_reminder_log.fail_reason IS '失败原因';
COMMENT ON COLUMN express_reminder_log.pickup_code IS '取件码(冗余)';
COMMENT ON COLUMN express_reminder_log.area_code IS '片区编码(冗余,用于查询优化)';
COMMENT ON COLUMN express_reminder_log.remark IS '备注';
COMMENT ON COLUMN express_reminder_log.creator IS '创建人';
COMMENT ON COLUMN express_reminder_log.creationtime IS '创建时间';
COMMENT ON COLUMN express_reminder_log.modifier IS '修改人';
COMMENT ON COLUMN express_reminder_log.modifiedtime IS '修改时间';
COMMENT ON COLUMN express_reminder_log.dr IS '删除标志:0-未删除,1-已删除';
COMMENT ON COLUMN express_reminder_log.ts IS '时间戳';
