package nc.express.util;

import nc.framework.pub.InvocationInfoProxy;
import nc.vo.express.ParcelVersionCompareVO;
import nc.vo.express.ParcelVO;
import nc.vo.express.ReminderLogVO;
import nc.vo.express.ReminderRuleVO;
import nc.vo.pub.lang.UFDateTime;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParcelVersionComparator {

    private static final Map<String, String> FIELD_LABELS = new LinkedHashMap<>();

    static {
        FIELD_LABELS.put("parcel_status", "包裹状态");
        FIELD_LABELS.put("return_processing", "退回处理状态");
        FIELD_LABELS.put("pickup_code", "取件码");
        FIELD_LABELS.put("pickup_code_expire", "取件码过期时间");
        FIELD_LABELS.put("pickup_time", "取件时间");
        FIELD_LABELS.put("is_vip", "VIP客户");
        FIELD_LABELS.put("is_large", "大件包裹");
        FIELD_LABELS.put("is_remote", "偏远地区");
        FIELD_LABELS.put("weight", "重量");
        FIELD_LABELS.put("area_code", "片区编码");
        FIELD_LABELS.put("remark", "备注");
        FIELD_LABELS.put("reminder_type", "催领类型");
        FIELD_LABELS.put("reminder_count", "催领次数");
        FIELD_LABELS.put("reminder_status", "催领状态");
        FIELD_LABELS.put("rule_name", "匹配规则");
        FIELD_LABELS.put("retention_days", "滞留天数");
        FIELD_LABELS.put("days_overdue", "超期天数");
    }

    public static List<ParcelVersionCompareVO> compareParcelBeforeReminder(
            ParcelVO oldParcel, ReminderRuleVO matchedRule, int daysOverdue) {

        List<ParcelVersionCompareVO> result = new ArrayList<>();
        UFDateTime now = new UFDateTime();
        String userId = InvocationInfoProxy.getInstance().getUserId();
        String groupId = InvocationInfoProxy.getInstance().getGroupId();

        result.add(createCompareVO(oldParcel.getPk_parcel(), null,
                "rule_name", "匹配规则",
                null, matchedRule != null ? matchedRule.getRule_name() : "无",
                "String", now, userId, groupId, oldParcel.getPk_org(),
                ParcelVersionCompareVO.RESULT_NEW, "催领规则匹配"));

        result.add(createCompareVO(oldParcel.getPk_parcel(), null,
                "retention_days", "滞留天数阈值",
                null, matchedRule != null && matchedRule.getRetention_days() != null
                        ? String.valueOf(matchedRule.getRetention_days()) : "3",
                "Integer", now, userId, groupId, oldParcel.getPk_org(),
                ParcelVersionCompareVO.RESULT_NEW, "规则滞留天数"));

        result.add(createCompareVO(oldParcel.getPk_parcel(), null,
                "days_overdue", "实际超期天数",
                null, String.valueOf(daysOverdue),
                "Integer", now, userId, groupId, oldParcel.getPk_org(),
                ParcelVersionCompareVO.RESULT_NEW, "计算超期天数"));

        result.add(createCompareVO(oldParcel.getPk_parcel(), null,
                "parcel_status", "当前包裹状态",
                null, getStatusName(oldParcel.getParcel_status()),
                "String", now, userId, groupId, oldParcel.getPk_org(),
                ParcelVersionCompareVO.RESULT_UNCHANGED, "催领前状态"));

        result.add(createCompareVO(oldParcel.getPk_parcel(), null,
                "return_processing", "退回处理状态",
                null, getReturnStatusName(oldParcel.getReturn_processing()),
                "String", now, userId, groupId, oldParcel.getPk_org(),
                ParcelVersionCompareVO.RESULT_UNCHANGED, "催领前状态"));

        result.add(createCompareVO(oldParcel.getPk_parcel(), null,
                "reminder_type", "催领方式",
                null, matchedRule != null ? getReminderTypeName(matchedRule.getReminder_type()) : "短信",
                "String", now, userId, groupId, oldParcel.getPk_org(),
                ParcelVersionCompareVO.RESULT_NEW, "规则催领方式"));

        return result;
    }

    public static List<ParcelVersionCompareVO> compareParcelAfterReminder(
            ParcelVO oldParcel, ParcelVO newParcel,
            ReminderLogVO reminderLog, ReminderRuleVO matchedRule) {

        List<ParcelVersionCompareVO> result = new ArrayList<>();
        UFDateTime now = new UFDateTime();
        String userId = InvocationInfoProxy.getInstance().getUserId();
        String groupId = InvocationInfoProxy.getInstance().getGroupId();
        String pkOrg = oldParcel.getPk_org();
        String pkLog = reminderLog != null ? reminderLog.getPk_log() : null;

        compareField(result, oldParcel, newParcel, "parcel_status",
                getStatusName(oldParcel.getParcel_status()),
                getStatusName(newParcel.getParcel_status()),
                "String", pkLog, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "return_processing",
                getReturnStatusName(oldParcel.getReturn_processing()),
                getReturnStatusName(newParcel.getReturn_processing()),
                "String", pkLog, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "pickup_code",
                oldParcel.getPickup_code(),
                newParcel.getPickup_code(),
                "String", pkLog, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "pickup_code_expire",
                oldParcel.getPickup_code_expire() != null ? oldParcel.getPickup_code_expire().toString() : null,
                newParcel.getPickup_code_expire() != null ? newParcel.getPickup_code_expire().toString() : null,
                "DateTime", pkLog, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "pickup_time",
                oldParcel.getPickup_time() != null ? oldParcel.getPickup_time().toString() : null,
                newParcel.getPickup_time() != null ? newParcel.getPickup_time().toString() : null,
                "DateTime", pkLog, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "remark",
                oldParcel.getRemark(),
                newParcel.getRemark(),
                "String", pkLog, now, userId, groupId, pkOrg);

        if (reminderLog != null) {
            result.add(createCompareVO(oldParcel.getPk_parcel(), pkLog,
                    "reminder_count", "催领次数",
                    String.valueOf(reminderLog.getReminder_count() - 1),
                    String.valueOf(reminderLog.getReminder_count()),
                    "Integer", now, userId, groupId, pkOrg,
                    ParcelVersionCompareVO.RESULT_CHANGED, "催领后次数更新"));

            result.add(createCompareVO(oldParcel.getPk_parcel(), pkLog,
                    "reminder_status", "催领结果",
                    null, getReminderStatusName(reminderLog.getReminder_status()),
                    "String", now, userId, groupId, pkOrg,
                    ParcelVersionCompareVO.RESULT_NEW, "催领执行结果"));

            if (reminderLog.getReminder_status() == ReminderLogVO.STATUS_FAILED) {
                result.add(createCompareVO(oldParcel.getPk_parcel(), pkLog,
                        "fail_reason", "失败原因",
                        null, reminderLog.getFail_reason(),
                        "String", now, userId, groupId, pkOrg,
                        ParcelVersionCompareVO.RESULT_NEW, "催领失败原因"));
            }
        }

        return result;
    }

    public static List<ParcelVersionCompareVO> compareParcelForResend(
            ParcelVO oldParcel, ParcelVO newParcel, ReminderLogVO reminderLog) {

        List<ParcelVersionCompareVO> result = new ArrayList<>();
        UFDateTime now = new UFDateTime();
        String userId = InvocationInfoProxy.getInstance().getUserId();
        String groupId = InvocationInfoProxy.getInstance().getGroupId();
        String pkOrg = oldParcel.getPk_org();
        String pkLog = reminderLog != null ? reminderLog.getPk_log() : null;

        result.add(createCompareVO(oldParcel.getPk_parcel(), pkLog,
                "operation_type", "操作类型",
                null, "取件码重发",
                "String", now, userId, groupId, pkOrg,
                ParcelVersionCompareVO.RESULT_NEW, "操作类型"));

        compareField(result, oldParcel, newParcel, "pickup_code",
                oldParcel.getPickup_code(),
                newParcel.getPickup_code(),
                "String", pkLog, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "pickup_code_expire",
                oldParcel.getPickup_code_expire() != null ? oldParcel.getPickup_code_expire().toString() : null,
                newParcel.getPickup_code_expire() != null ? newParcel.getPickup_code_expire().toString() : null,
                "DateTime", pkLog, now, userId, groupId, pkOrg);

        if (reminderLog != null) {
            result.add(createCompareVO(oldParcel.getPk_parcel(), pkLog,
                    "reminder_status", "重发结果",
                    null, getReminderStatusName(reminderLog.getReminder_status()),
                    "String", now, userId, groupId, pkOrg,
                    ParcelVersionCompareVO.RESULT_NEW, "取件码重发结果"));
        }

        return result;
    }

    public static List<ParcelVersionCompareVO> compareParcelForStatusChange(
            ParcelVO oldParcel, ParcelVO newParcel, String operationType) {

        List<ParcelVersionCompareVO> result = new ArrayList<>();
        UFDateTime now = new UFDateTime();
        String userId = InvocationInfoProxy.getInstance().getUserId();
        String groupId = InvocationInfoProxy.getInstance().getGroupId();
        String pkOrg = oldParcel.getPk_org();

        result.add(createCompareVO(oldParcel.getPk_parcel(), null,
                "operation_type", "操作类型",
                null, operationType,
                "String", now, userId, groupId, pkOrg,
                ParcelVersionCompareVO.RESULT_NEW, "状态变更操作"));

        compareField(result, oldParcel, newParcel, "parcel_status",
                getStatusName(oldParcel.getParcel_status()),
                getStatusName(newParcel.getParcel_status()),
                "String", null, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "return_processing",
                getReturnStatusName(oldParcel.getReturn_processing()),
                getReturnStatusName(newParcel.getReturn_processing()),
                "String", null, now, userId, groupId, pkOrg);

        compareField(result, oldParcel, newParcel, "pickup_time",
                oldParcel.getPickup_time() != null ? oldParcel.getPickup_time().toString() : null,
                newParcel.getPickup_time() != null ? newParcel.getPickup_time().toString() : null,
                "DateTime", null, now, userId, groupId, pkOrg);

        return result;
    }

    private static void compareField(List<ParcelVersionCompareVO> result,
                                     ParcelVO oldParcel, ParcelVO newParcel,
                                     String fieldName, String oldValue, String newValue,
                                     String valueType, String pkLog,
                                     UFDateTime now, String userId, String groupId, String pkOrg) {
        boolean changed = !areEqual(oldValue, newValue);
        int compareResult = changed ? ParcelVersionCompareVO.RESULT_CHANGED : ParcelVersionCompareVO.RESULT_UNCHANGED;

        result.add(createCompareVO(oldParcel.getPk_parcel(), pkLog,
                fieldName, FIELD_LABELS.getOrDefault(fieldName, fieldName),
                oldValue, newValue, valueType, now, userId, groupId, pkOrg,
                compareResult, changed ? "字段值变更" : "字段值未变更"));
    }

    private static boolean areEqual(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    private static ParcelVersionCompareVO createCompareVO(
            String pkParcel, String pkLog, String fieldName, String fieldLabel,
            String oldValue, String newValue, String valueType,
            UFDateTime compareTime, String operator, String pkGroup, String pkOrg,
            int compareResult, String remark) {

        ParcelVersionCompareVO vo = new ParcelVersionCompareVO();
        vo.setPk_group(pkGroup);
        vo.setPk_org(pkOrg);
        vo.setCreator(operator);
        vo.setCreationtime(compareTime);
        vo.setModifier(operator);
        vo.setModifiedtime(compareTime);
        vo.setDr(0);

        vo.setPk_parcel(pkParcel);
        vo.setPk_log(pkLog);
        vo.setField_name(fieldName);
        vo.setField_label(fieldLabel);
        vo.setOld_value(oldValue);
        vo.setNew_value(newValue);
        vo.setValue_type(valueType);
        vo.setCompare_time(compareTime);
        vo.setCompare_result(compareResult);
        vo.setOperator(operator);
        vo.setRemark(remark);

        return vo;
    }

    private static String getStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case ParcelVO.STATUS_PENDING:
                return "待取件";
            case ParcelVO.STATUS_PICKED:
                return "已取件";
            case ParcelVO.STATUS_RETURNING:
                return "退回中";
            case ParcelVO.STATUS_RETURNED:
                return "已退回";
            default:
                return "未知(" + status + ")";
        }
    }

    private static String getReturnStatusName(Integer status) {
        if (status == null || status == 0) {
            return "未退回";
        } else if (status == 1) {
            return "退回处理中";
        } else if (status == 2) {
            return "已退回";
        }
        return "未知(" + status + ")";
    }

    private static String getReminderTypeName(Integer type) {
        if (type == null) {
            return "未知";
        }
        return ExpressRuleMatcher.getReminderTypeName(type);
    }

    private static String getReminderStatusName(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case ReminderLogVO.STATUS_PENDING:
                return "待发送";
            case ReminderLogVO.STATUS_SENT:
                return "发送成功";
            case ReminderLogVO.STATUS_FAILED:
                return "发送失败";
            case ReminderLogVO.STATUS_CANCELLED:
                return "已取消";
            default:
                return "未知(" + status + ")";
        }
    }

    public static String generateVersionCompareSummary(List<ParcelVersionCompareVO> compareList) {
        if (compareList == null || compareList.isEmpty()) {
            return "无版本对比数据";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("【版本对比审计】共").append(compareList.size()).append("个字段，");

        long changedCount = compareList.stream()
                .filter(v -> v.getCompare_result() == ParcelVersionCompareVO.RESULT_CHANGED)
                .count();
        long newCount = compareList.stream()
                .filter(v -> v.getCompare_result() == ParcelVersionCompareVO.RESULT_NEW)
                .count();
        long unchangedCount = compareList.stream()
                .filter(v -> v.getCompare_result() == ParcelVersionCompareVO.RESULT_UNCHANGED)
                .count();

        sb.append("变更:").append(changedCount).append("个，");
        sb.append("新增:").append(newCount).append("个，");
        sb.append("未变:").append(unchangedCount).append("个。");

        if (changedCount > 0) {
            sb.append("\n变更详情：");
            for (ParcelVersionCompareVO vo : compareList) {
                if (vo.getCompare_result() == ParcelVersionCompareVO.RESULT_CHANGED) {
                    sb.append("\n  - ").append(vo.getField_label())
                            .append(": ").append(vo.getOld_value())
                            .append(" → ").append(vo.getNew_value());
                }
            }
        }

        return sb.toString();
    }
}
