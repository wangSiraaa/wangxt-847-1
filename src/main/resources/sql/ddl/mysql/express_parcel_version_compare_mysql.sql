-- =====================================================================
-- 快递驿站滞留件催领系统 - 版本对比审计表 (MySQL)
-- =====================================================================

CREATE TABLE express_parcel_version_compare (
    pk_version_compare  CHAR(20)        NOT NULL COMMENT '主键',
    pk_group            CHAR(20)        COMMENT '集团',
    pk_org              CHAR(20)        COMMENT '组织',
    pk_parcel           CHAR(20)        NOT NULL COMMENT '包裹主键',
    pk_log              CHAR(20)        COMMENT '催领记录主键',
    field_name          VARCHAR(100)    NOT NULL COMMENT '字段名',
    field_label         VARCHAR(200)    COMMENT '字段标签',
    old_value           VARCHAR(1000)   COMMENT '旧值',
    new_value           VARCHAR(1000)   COMMENT '新值',
    compare_result      INT             DEFAULT 0 COMMENT '对比结果：0-未变，1-变更，2-新增',
    change_type         VARCHAR(50)     COMMENT '变更类型',
    operator            CHAR(20)        COMMENT '操作人',
    operate_time        DATETIME        COMMENT '操作时间',
    remark              VARCHAR(500)    COMMENT '备注',
    creator             CHAR(20)        COMMENT '创建人',
    creationtime        DATETIME        COMMENT '创建时间',
    modifier            CHAR(20)        COMMENT '修改人',
    modifiedtime        DATETIME        COMMENT '修改时间',
    dr                  INT             DEFAULT 0 COMMENT '删除标记',
    ts                  CHAR(19)        COMMENT '时间戳',
    PRIMARY KEY (pk_version_compare)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='包裹版本对比审计表';

CREATE INDEX idx_version_compare_parcel ON express_parcel_version_compare(pk_parcel, dr, operate_time DESC);
CREATE INDEX idx_version_compare_log ON express_parcel_version_compare(pk_log, dr);
CREATE INDEX idx_version_compare_result ON express_parcel_version_compare(pk_org, compare_result, dr, operate_time DESC);
CREATE INDEX idx_version_compare_field ON express_parcel_version_compare(pk_parcel, field_name, dr, operate_time DESC);
