package nc.express.rule;

import nc.vo.express.ParcelVO;
import nc.vo.express.ReminderRuleVO;
import nc.vo.pub.lang.UFDouble;

import java.util.List;

public class ExpressRuleMatcher {

    public ReminderRuleVO matchRule(ParcelVO parcel, List<ReminderRuleVO> rules) {
        if (rules == null || rules.isEmpty()) {
            return null;
        }

        for (ReminderRuleVO rule : rules) {
            if (isRuleMatch(parcel, rule)) {
                return rule;
            }
        }

        return getDefaultRule(rules);
    }

    private boolean isRuleMatch(ParcelVO parcel, ReminderRuleVO rule) {
        if (rule.getEnabled() == null || rule.getEnabled() != 1) {
            return false;
        }

        if (rule.getCond_vip() != null) {
            int vip = parcel.getIs_vip() != null ? parcel.getIs_vip() : 0;
            if (rule.getCond_vip() == 1 && vip != 1) {
                return false;
            }
        }

        if (rule.getCond_large() != null) {
            int large = parcel.getIs_large() != null ? parcel.getIs_large() : 0;
            if (rule.getCond_large() == 1 && large != 1) {
                return false;
            }
        }

        if (rule.getCond_remote() != null) {
            int remote = parcel.getIs_remote() != null ? parcel.getIs_remote() : 0;
            if (rule.getCond_remote() == 1 && remote != 1) {
                return false;
            }
        }

        if (rule.getCond_weight_min() != null && parcel.getWeight() != null) {
            if (parcel.getWeight().compareTo(new UFDouble(rule.getCond_weight_min())) < 0) {
                return false;
            }
        }

        if (rule.getCond_area() != null && !rule.getCond_area().isEmpty()) {
            String area = parcel.getArea_code();
            if (area == null || !area.equals(rule.getCond_area())) {
                return false;
            }
        }

        return true;
    }

    private ReminderRuleVO getDefaultRule(List<ReminderRuleVO> rules) {
        for (ReminderRuleVO rule : rules) {
            if (isDefaultRule(rule)) {
                return rule;
            }
        }
        return rules.get(rules.size() - 1);
    }

    private boolean isDefaultRule(ReminderRuleVO rule) {
        return rule.getCond_vip() == null
                && rule.getCond_large() == null
                && rule.getCond_remote() == null
                && rule.getCond_weight_min() == null
                && (rule.getCond_area() == null || rule.getCond_area().isEmpty());
    }

    public static String getReminderTypeName(int type) {
        switch (type) {
            case ReminderRuleVO.TYPE_SMS:
                return "短信";
            case ReminderRuleVO.TYPE_PHONE:
                return "电话";
            case ReminderRuleVO.TYPE_APP:
                return "APP推送";
            case ReminderRuleVO.TYPE_DOOR:
                return "上门通知";
            default:
                return "未知";
        }
    }
}
