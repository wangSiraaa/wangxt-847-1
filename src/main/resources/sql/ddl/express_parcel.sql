-- =====================================================================
-- 快递驿站滞留件催领系统 - 包裹主表
-- 兼容数据库：Oracle 11g+ / 达梦 8+
-- 创建日期：2026-06-08
-- =====================================================================

-- 包裹主表
CREATE TABLE express_parcel (
    pk_parcel           CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    express_no          VARCHAR2(100)   NOT NULL,
    receiver_name       VARCHAR2(50)    NOT NULL,
    receiver_phone      VARCHAR2(20)    NOT NULL,
    pickup_code         VARCHAR2(20),
    pickup_code_expire  DATE,
    inbound_time        DATE            NOT NULL,
    parcel_status       INT             DEFAULT 0,
    is_vip              INT             DEFAULT 0,
    is_large            INT             DEFAULT 0,
    is_remote           INT             DEFAULT 0,
    weight              DECIMAL(28,8),
    area_code           VARCHAR2(50),
    return_processing   INT             DEFAULT 0,
    remark              VARCHAR2(500),
    creator             CHAR(20),
    creationtime        DATE,
    modifier            CHAR(20),
    modifiedtime        DATE,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19)
);

-- 主键约束
ALTER TABLE express_parcel ADD CONSTRAINT pk_express_parcel PRIMARY KEY (pk_parcel);

-- 索引：快递单号查询
CREATE INDEX idx_express_parcel_no ON express_parcel(express_no, pk_org, dr);

-- 索引：组织+状态查询超期包裹
CREATE INDEX idx_express_parcel_org ON express_parcel(pk_org, parcel_status, dr, inbound_time);

-- 索引：退回处理状态
CREATE INDEX idx_express_parcel_return ON express_parcel(return_processing, dr);

-- 索引：取件码过期查询
CREATE INDEX idx_express_parcel_code ON express_parcel(pickup_code_expire, dr);

-- 备注：
-- parcel_status: 0-待取件, 1-已取件, 2-退回中, 3-已退回
-- is_vip: 0-否, 1-是
-- is_large: 0-否, 1-是
-- is_remote: 0-否, 1-是
-- return_processing: 0-否, 1-是
-- dr: 0-未删除, 1-已删除
