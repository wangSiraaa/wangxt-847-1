package nc.vo.express;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pubapp.pattern.model.meta.entity.vo.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class ReminderLogVO extends SuperVO {
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "express_reminder_log";
    public static final String PK_LOG = "pk_log";
    public static final String PK_PARCEL = "pk_parcel";
    public static final String PK_RULE = "pk_rule";
    public static final String REMINDER_TYPE = "reminder_type";
    public static final String REMINDER_TIME = "reminder_time";
    public static final String REMINDER_STATUS = "reminder_status";
    public static final String REMINDER_CONTENT = "reminder_content";
    public static final String PICKUP_CODE = "pickup_code";
    public static final String FAIL_REASON = "fail_reason";
    public static final String REMINDER_COUNT = "reminder_count";
    public static final String OPERATOR = "operator";
    public static final String AREA_CODE = "area_code";
    public static final String REMARK = "remark";

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_FAILED = 2;
    public static final int STATUS_CANCELLED = 3;

    private String pk_log;
    private String pk_parcel;
    private String pk_rule;
    private Integer reminder_type;
    private UFDateTime reminder_time;
    private Integer reminder_status;
    private String reminder_content;
    private String pickup_code;
    private String fail_reason;
    private Integer reminder_count;
    private String operator;
    private String area_code;
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
        return PK_LOG;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public IVOMeta getMetaData() {
        return VOMetaFactory.getInstance().getVOMeta("express.reminderlog");
    }

    public String getPk_log() {
        return pk_log;
    }

    public void setPk_log(String pk_log) {
        this.pk_log = pk_log;
    }

    public String getPk_parcel() {
        return pk_parcel;
    }

    public void setPk_parcel(String pk_parcel) {
        this.pk_parcel = pk_parcel;
    }

    public String getPk_rule() {
        return pk_rule;
    }

    public void setPk_rule(String pk_rule) {
        this.pk_rule = pk_rule;
    }

    public Integer getReminder_type() {
        return reminder_type;
    }

    public void setReminder_type(Integer reminder_type) {
        this.reminder_type = reminder_type;
    }

    public UFDateTime getReminder_time() {
        return reminder_time;
    }

    public void setReminder_time(UFDateTime reminder_time) {
        this.reminder_time = reminder_time;
    }

    public Integer getReminder_status() {
        return reminder_status;
    }

    public void setReminder_status(Integer reminder_status) {
        this.reminder_status = reminder_status;
    }

    public String getReminder_content() {
        return reminder_content;
    }

    public void setReminder_content(String reminder_content) {
        this.reminder_content = reminder_content;
    }

    public String getPickup_code() {
        return pickup_code;
    }

    public void setPickup_code(String pickup_code) {
        this.pickup_code = pickup_code;
    }

    public String getFail_reason() {
        return fail_reason;
    }

    public void setFail_reason(String fail_reason) {
        this.fail_reason = fail_reason;
    }

    public Integer getReminder_count() {
        return reminder_count;
    }

    public void setReminder_count(Integer reminder_count) {
        this.reminder_count = reminder_count;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
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
