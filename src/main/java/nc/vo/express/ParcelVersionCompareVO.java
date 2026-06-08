package nc.vo.express;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class ParcelVersionCompareVO extends SuperVO {
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "express_parcel_version_compare";
    public static final String PK_COMPARE = "pk_compare";
    public static final String PK_PARCEL = "pk_parcel";
    public static final String PK_LOG = "pk_log";
    public static final String FIELD_NAME = "field_name";
    public static final String FIELD_LABEL = "field_label";
    public static final String OLD_VALUE = "old_value";
    public static final String NEW_VALUE = "new_value";
    public static final String VALUE_TYPE = "value_type";
    public static final String COMPARE_TIME = "compare_time";
    public static final String COMPARE_RESULT = "compare_result";
    public static final String OPERATOR = "operator";
    public static final String REMARK = "remark";

    public static final int RESULT_UNCHANGED = 0;
    public static final int RESULT_CHANGED = 1;
    public static final int RESULT_NEW = 2;

    private String pk_compare;
    private String pk_parcel;
    private String pk_log;
    private String field_name;
    private String field_label;
    private String old_value;
    private String new_value;
    private String value_type;
    private UFDateTime compare_time;
    private Integer compare_result;
    private String operator;
    private String remark;

    private String pk_group;
    private String pk_org;
    private String creator;
    private UFDateTime creationtime;
    private String modifier;
    private UFDateTime modifiedtime;
    private Integer dr = 0;
    private UFDateTime ts;

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getPKFieldName() {
        return PK_COMPARE;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public IVOMeta getMetaData() {
        return VOMetaFactory.getInstance().getVOMeta("express.parcelversioncompare");
    }

    public String getPk_compare() {
        return pk_compare;
    }

    public void setPk_compare(String pk_compare) {
        this.pk_compare = pk_compare;
    }

    public String getPk_parcel() {
        return pk_parcel;
    }

    public void setPk_parcel(String pk_parcel) {
        this.pk_parcel = pk_parcel;
    }

    public String getPk_log() {
        return pk_log;
    }

    public void setPk_log(String pk_log) {
        this.pk_log = pk_log;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getField_label() {
        return field_label;
    }

    public void setField_label(String field_label) {
        this.field_label = field_label;
    }

    public String getOld_value() {
        return old_value;
    }

    public void setOld_value(String old_value) {
        this.old_value = old_value;
    }

    public String getNew_value() {
        return new_value;
    }

    public void setNew_value(String new_value) {
        this.new_value = new_value;
    }

    public String getValue_type() {
        return value_type;
    }

    public void setValue_type(String value_type) {
        this.value_type = value_type;
    }

    public UFDateTime getCompare_time() {
        return compare_time;
    }

    public void setCompare_time(UFDateTime compare_time) {
        this.compare_time = compare_time;
    }

    public Integer getCompare_result() {
        return compare_result;
    }

    public void setCompare_result(Integer compare_result) {
        this.compare_result = compare_result;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPk_group() {
        return pk_group;
    }

    public void setPk_group(String pk_group) {
        this.pk_group = pk_group;
    }

    public String getPk_org() {
        return pk_org;
    }

    public void setPk_org(String pk_org) {
        this.pk_org = pk_org;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public UFDateTime getCreationtime() {
        return creationtime;
    }

    public void setCreationtime(UFDateTime creationtime) {
        this.creationtime = creationtime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public UFDateTime getModifiedtime() {
        return modifiedtime;
    }

    public void setModifiedtime(UFDateTime modifiedtime) {
        this.modifiedtime = modifiedtime;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public UFDateTime getTs() {
        return ts;
    }

    public void setTs(UFDateTime ts) {
        this.ts = ts;
    }
}
