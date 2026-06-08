-- =====================================================================
-- 快递驿站滞留件催领系统 - 版本对比审计表
-- 兼容数据库：Oracle 11g+ / 达梦 8+
-- 创建日期：2026-06-08
-- =====================================================================

-- 版本对比审计表
CREATE TABLE express_parcel_version_compare (
    pk_version_compare  CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    pk_parcel           CHAR(20)        NOT NULL,
    pk_log              CHAR(20),
    field_name          VARCHAR2(100)   NOT NULL,
    field_label         VARCHAR2(200),
    old_value           VARCHAR2(1000),
    new_value           VARCHAR2(1000),
    compare_result      INT             DEFAULT 0,
    change_type         VARCHAR2(50),
    operator            CHAR(20),
    operate_time        DATE,
    remark              VARCHAR2(500),
    creator             CHAR(20),
    creationtime        DATE,
    modifier            CHAR(20),
    modifiedtime        DATE,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19)
);

-- 主键约束
ALTER TABLE express_parcel_version_compare ADD CONSTRAINT pk_express_parcel_version_compare PRIMARY KEY (pk_version_compare);

-- 索引：按包裹查询版本对比记录
CREATE INDEX idx_version_compare_parcel ON express_parcel_version_compare(pk_parcel, dr, operate_time DESC);

-- 索引：按催领记录查询版本对比记录
CREATE INDEX idx_version_compare_log ON express_parcel_version_compare(pk_log, dr);

-- 索引：按对比结果查询
CREATE INDEX idx_version_compare_result ON express_parcel_version_compare(pk_org, compare_result, dr, operate_time DESC);

-- 索引：按字段名查询变更历史
CREATE INDEX idx_version_compare_field ON express_parcel_version_compare(pk_parcel, field_name, dr, operate_time DESC);

-- 备注：
-- compare_result: 0-未变化, 1-已变更, 2-新增字段
-- change_type: 字段变化分类，如"状态变更"、"取件码更新"、"规则匹配"等
