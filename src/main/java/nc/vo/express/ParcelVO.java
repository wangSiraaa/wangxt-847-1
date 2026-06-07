package nc.vo.express;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class ParcelVO extends SuperVO {
    private static final long serialVersionUID = 1L;

    public static final String TABLE_NAME = "express_parcel";
    public static final String PK_PARCEL = "pk_parcel";
    public static final String EXPRESS_NO = "express_no";
    public static final String RECEIVER_NAME = "receiver_name";
    public static final String RECEIVER_PHONE = "receiver_phone";
    public static final String PICKUP_CODE = "pickup_code";
    public static final String PICKUP_CODE_EXPIRE = "pickup_code_expire";
    public static final String INBOUND_TIME = "inbound_time";
    public static final String PICKUP_TIME = "pickup_time";
    public static final String PARCEL_STATUS = "parcel_status";
    public static final String IS_VIP = "is_vip";
    public static final String IS_LARGE = "is_large";
    public static final String IS_REMOTE = "is_remote";
    public static final String WEIGHT = "weight";
    public static final String AREA_CODE = "area_code";
    public static final String REMARK = "remark";
    public static final String RETURN_PROCESSING = "return_processing";

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_PICKED = 1;
    public static final int STATUS_RETURNING = 2;
    public static final int STATUS_RETURNED = 3;

    private String pk_parcel;
    private String express_no;
    private String receiver_name;
    private String receiver_phone;
    private String pickup_code;
    private UFDateTime pickup_code_expire;
    private UFDateTime inbound_time;
    private UFDateTime pickup_time;
    private Integer parcel_status;
    private Integer is_vip;
    private Integer is_large;
    private Integer is_remote;
    private UFDouble weight;
    private String area_code;
    private String remark;
    private Integer return_processing;

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
        return PK_PARCEL;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public IVOMeta getMetaData() {
        return VOMetaFactory.getInstance().getVOMeta("express.parcel");
    }

    public String getPk_parcel() {
        return pk_parcel;
    }

    public void setPk_parcel(String pk_parcel) {
        this.pk_parcel = pk_parcel;
    }

    public String getExpress_no() {
        return express_no;
    }

    public void setExpress_no(String express_no) {
        this.express_no = express_no;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiver_phone() {
        return receiver_phone;
    }

    public void setReceiver_phone(String receiver_phone) {
        this.receiver_phone = receiver_phone;
    }

    public String getPickup_code() {
        return pickup_code;
    }

    public void setPickup_code(String pickup_code) {
        this.pickup_code = pickup_code;
    }

    public UFDateTime getPickup_code_expire() {
        return pickup_code_expire;
    }

    public void setPickup_code_expire(UFDateTime pickup_code_expire) {
        this.pickup_code_expire = pickup_code_expire;
    }

    public UFDateTime getInbound_time() {
        return inbound_time;
    }

    public void setInbound_time(UFDateTime inbound_time) {
        this.inbound_time = inbound_time;
    }

    public UFDateTime getPickup_time() {
        return pickup_time;
    }

    public void setPickup_time(UFDateTime pickup_time) {
        this.pickup_time = pickup_time;
    }

    public Integer getParcel_status() {
        return parcel_status;
    }

    public void setParcel_status(Integer parcel_status) {
        this.parcel_status = parcel_status;
    }

    public Integer getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(Integer is_vip) {
        this.is_vip = is_vip;
    }

    public Integer getIs_large() {
        return is_large;
    }

    public void setIs_large(Integer is_large) {
        this.is_large = is_large;
    }

    public Integer getIs_remote() {
        return is_remote;
    }

    public void setIs_remote(Integer is_remote) {
        this.is_remote = is_remote;
    }

    public UFDouble getWeight() {
        return weight;
    }

    public void setWeight(UFDouble weight) {
        this.weight = weight;
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

    public Integer getReturn_processing() {
        return return_processing;
    }

    public void setReturn_processing(Integer return_processing) {
        this.return_processing = return_processing;
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
