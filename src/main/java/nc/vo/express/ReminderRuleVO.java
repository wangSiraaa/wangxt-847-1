package nc.vo.express;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class ReminderRuleVO extends SuperVO {
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "express_reminder_rule";
    public static final String PK_RULE = "pk_rule";
    public static final String RULE_CODE = "rule_code";
    public static final String RULE_NAME = "rule_name";
    public static final String COND_VIP = "cond_vip";
    public static final String COND_LARGE = "cond_large";
    public static final String COND_REMOTE = "cond_remote";
    public static final String MIN_WEIGHT = "min_weight";
    public static final String MAX_WEIGHT = "max_weight";
    public static final String AREA_CODE = "area_code";
    public static final String REMINDER_TYPE = "reminder_type";
    public static final String RETENTION_DAYS = "retention_days";
    public static final String REMINDER_INTERVAL = "reminder_interval";
    public static final String MAX_REMINDER_COUNT = "max_reminder_count";
    public static final String PRIORITY = "priority";
    public static final String REMARK = "remark";
    public static final String ENABLED = "enabled";

    public static final int TYPE_SMS = 1;
    public static final int TYPE_PHONE = 2;
    public static final int TYPE_APP = 3;
    public static final int TYPE_DOOR = 4;

    private String pk_rule;
    private String rule_code;
    private String rule_name;
    private Integer cond_vip;
    private Integer cond_large;
    private Integer cond_remote;
    private UFDouble min_weight;
    private UFDouble max_weight;
    private String area_code;
    private Integer reminder_type;
    private Integer retention_days;
    private Integer reminder_interval;
    private Integer max_reminder_count;
    private Integer priority;
    private String remark;
    private Integer enabled;

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
        return PK_RULE;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public IVOMeta getMetaData() {
        return VOMetaFactory.getInstance().getVOMeta("express.reminderrule");
    }

    public String getPk_rule() {
        return pk_rule;
    }

    public void setPk_rule(String pk_rule) {
        this.pk_rule = pk_rule;
    }

    public String getRule_code() {
        return rule_code;
    }

    public void setRule_code(String rule_code) {
        this.rule_code = rule_code;
    }

    public String getRule_name() {
        return rule_name;
    }

    public void setRule_name(String rule_name) {
        this.rule_name = rule_name;
    }

    public Integer getCond_vip() {
        return cond_vip;
    }

    public void setCond_vip(Integer cond_vip) {
        this.cond_vip = cond_vip;
    }

    public Integer getCond_large() {
        return cond_large;
    }

    public void setCond_large(Integer cond_large) {
        this.cond_large = cond_large;
    }

    public Integer getCond_remote() {
        return cond_remote;
    }

    public void setCond_remote(Integer cond_remote) {
        this.cond_remote = cond_remote;
    }

    public UFDouble getMin_weight() {
        return min_weight;
    }

    public void setMin_weight(UFDouble min_weight) {
        this.min_weight = min_weight;
    }

    public UFDouble getMax_weight() {
        return max_weight;
    }

    public void setMax_weight(UFDouble max_weight) {
        this.max_weight = max_weight;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public Integer getReminder_type() {
        return reminder_type;
    }

    public void setReminder_type(Integer reminder_type) {
        this.reminder_type = reminder_type;
    }

    public Integer getRetention_days() {
        return retention_days;
    }

    public void setRetention_days(Integer retention_days) {
        this.retention_days = retention_days;
    }

    public Integer getReminder_interval() {
        return reminder_interval;
    }

    public void setReminder_interval(Integer reminder_interval) {
        this.reminder_interval = reminder_interval;
    }

    public Integer getMax_reminder_count() {
        return max_reminder_count;
    }

    public void setMax_reminder_count(Integer max_reminder_count) {
        this.max_reminder_count = max_reminder_count;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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
