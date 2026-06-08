-- =====================================================================
-- 快递驿站滞留件催领系统 - 版本对比审计表 (H2)
-- =====================================================================

CREATE TABLE express_parcel_version_compare (
    pk_version_compare  CHAR(20)        NOT NULL,
    pk_group            CHAR(20),
    pk_org              CHAR(20),
    pk_parcel           CHAR(20)        NOT NULL,
    pk_log              CHAR(20),
    field_name          VARCHAR(100)    NOT NULL,
    field_label         VARCHAR(200),
    old_value           VARCHAR(1000),
    new_value           VARCHAR(1000),
    compare_result      INT             DEFAULT 0,
    change_type         VARCHAR(50),
    operator            CHAR(20),
    operate_time        TIMESTAMP,
    remark              VARCHAR(500),
    creator             CHAR(20),
    creationtime        TIMESTAMP,
    modifier            CHAR(20),
    modifiedtime        TIMESTAMP,
    dr                  INT             DEFAULT 0,
    ts                  CHAR(19)
);

ALTER TABLE express_parcel_version_compare ADD CONSTRAINT pk_express_parcel_version_compare PRIMARY KEY (pk_version_compare);

CREATE INDEX idx_version_compare_parcel ON express_parcel_version_compare(pk_parcel, dr, operate_time DESC);
CREATE INDEX idx_version_compare_log ON express_parcel_version_compare(pk_log, dr);
CREATE INDEX idx_version_compare_result ON express_parcel_version_compare(pk_org, compare_result, dr, operate_time DESC);
CREATE INDEX idx_version_compare_field ON express_parcel_version_compare(pk_parcel, field_name, dr, operate_time DESC);
