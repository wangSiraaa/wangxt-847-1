-- =====================================================================
-- 快递驿站包裹主表 (H2 版本)
-- 兼容 H2 2.x + MySQL 兼容模式
-- =====================================================================

CREATE TABLE IF NOT EXISTS express_parcel (
    pk_parcel           CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    express_no          VARCHAR(100)    NOT NULL,
    receiver_name       VARCHAR(50)     NOT NULL,
    receiver_phone      VARCHAR(20)     NOT NULL,
    pickup_code         VARCHAR(20),
    pickup_code_expire  TIMESTAMP,
    inbound_time        TIMESTAMP       NOT NULL,
    parcel_status       INT             DEFAULT 0,
    is_vip              INT             DEFAULT 0,
    is_large            INT             DEFAULT 0,
    is_remote           INT             DEFAULT 0,
    weight              DECIMAL(28,8),
    area_code           VARCHAR(50),
    return_processing   INT             DEFAULT 0,
    remark              VARCHAR(500),
    creator             CHAR(20),
    creationtime        TIMESTAMP,
    modifier            CHAR(20),
    modifiedtime        TIMESTAMP,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19),
    CONSTRAINT pk_express_parcel PRIMARY KEY (pk_parcel)
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_express_parcel_no ON express_parcel(express_no);
CREATE INDEX IF NOT EXISTS idx_express_parcel_org_status ON express_parcel(pk_org, parcel_status);
CREATE INDEX IF NOT EXISTS idx_express_parcel_return ON express_parcel(return_processing);
CREATE INDEX IF NOT EXISTS idx_express_parcel_expire ON express_parcel(pickup_code_expire);

-- 添加注释
COMMENT ON TABLE express_parcel IS '快递驿站包裹主表';
COMMENT ON COLUMN express_parcel.pk_parcel IS '主键';
COMMENT ON COLUMN express_parcel.pk_group IS '集团主键';
COMMENT ON COLUMN express_parcel.pk_org IS '组织主键';
COMMENT ON COLUMN express_parcel.express_no IS '快递单号';
COMMENT ON COLUMN express_parcel.receiver_name IS '收件人姓名';
COMMENT ON COLUMN express_parcel.receiver_phone IS '收件人电话';
COMMENT ON COLUMN express_parcel.pickup_code IS '取件码';
COMMENT ON COLUMN express_parcel.pickup_code_expire IS '取件码过期时间';
COMMENT ON COLUMN express_parcel.inbound_time IS '入库时间';
COMMENT ON COLUMN express_parcel.parcel_status IS '包裹状态:0-待取件,1-已取件,2-退回中,3-已退回';
COMMENT ON COLUMN express_parcel.is_vip IS '是否VIP:0-否,1-是';
COMMENT ON COLUMN express_parcel.is_large IS '是否大件:0-否,1-是';
COMMENT ON COLUMN express_parcel.is_remote IS '是否偏远地区:0-否,1-是';
COMMENT ON COLUMN express_parcel.weight IS '重量(kg)';
COMMENT ON COLUMN express_parcel.area_code IS '片区编码';
COMMENT ON COLUMN express_parcel.return_processing IS '退回处理标志:0-否,1-是(阻塞催领)';
COMMENT ON COLUMN express_parcel.remark IS '备注';
COMMENT ON COLUMN express_parcel.creator IS '创建人';
COMMENT ON COLUMN express_parcel.creationtime IS '创建时间';
COMMENT ON COLUMN express_parcel.modifier IS '修改人';
COMMENT ON COLUMN express_parcel.modifiedtime IS '修改时间';
COMMENT ON COLUMN express_parcel.dr IS '删除标志:0-未删除,1-已删除';
COMMENT ON COLUMN express_parcel.ts IS '时间戳';
