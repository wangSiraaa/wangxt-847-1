package nc.bs.express;

import nc.bs.dao.DAOException;
import nc.express.rule.ExpressRuleMatcher;
import nc.express.util.ExpressUtils;
import nc.express.util.ParcelVersionComparator;
import nc.framework.pub.InvocationInfoProxy;
import nc.itf.express.IExpressReminderService;
import nc.vo.am.common.util.StringUtils;
import nc.vo.express.ParcelVersionCompareVO;
import nc.vo.express.ParcelVO;
import nc.vo.express.ReminderLogVO;
import nc.vo.express.ReminderResultVO;
import nc.vo.express.ReminderRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressReminderServiceImpl implements IExpressReminderService {

    private ExpressReminderDAO dao = new ExpressReminderDAO();
    private ExpressRuleMatcher ruleMatcher = new ExpressRuleMatcher();

    private static final int PICKUP_CODE_EXPIRE_HOURS = 24;
    private static final int DEFAULT_RETENTION_DAYS = 3;

    @Override
    public ParcelVO inbound(ParcelVO parcelVO) throws BusinessException {
        validateInboundData(parcelVO);

        ParcelVO existing = dao.findParcelByExpressNo(parcelVO.getExpress_no(), parcelVO.getPk_org());
        if (existing != null && existing.getParcel_status() == ParcelVO.STATUS_PENDING) {
            throw new BusinessException("该快递单号已存在且未领取");
        }

        String userId = InvocationInfoProxy.getInstance().getUserId();
        String groupId = InvocationInfoProxy.getInstance().getGroupId();
        UFDateTime now = new UFDateTime();

        parcelVO.setPk_group(groupId);
        parcelVO.setCreator(userId);
        parcelVO.setCreationtime(now);
        parcelVO.setModifier(userId);
        parcelVO.setModifiedtime(now);
        parcelVO.setDr(0);
        parcelVO.setParcel_status(ParcelVO.STATUS_PENDING);
        parcelVO.setReturn_processing(0);
        parcelVO.setInbound_time(now);
        parcelVO.setPickup_code(ExpressUtils.generatePickupCode());
        parcelVO.setPickup_code_expire(getExpireTime(now));

        try {
            return dao.insertParcel(parcelVO);
        } catch (DAOException e) {
            throw new BusinessException("包裹入库失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ReminderRuleVO calculateRule(ParcelVO parcelVO) throws BusinessException {
        try {
            List<ReminderRuleVO> rules = dao.findAllEnabledRules(parcelVO.getPk_org());
            return ruleMatcher.matchRule(parcelVO, rules);
        } catch (DAOException e) {
            throw new BusinessException("规则计算失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ReminderLogVO> generateReminders(String pkOrg, String[] pkParcels) throws BusinessException {
        if (pkParcels == null || pkParcels.length == 0) {
            throw new BusinessException("请选择需要催领的包裹");
        }

        List<ReminderLogVO> result = new ArrayList<>();
        try {
            List<ParcelVO> parcels = dao.findParcelsByPKs(pkParcels);
            List<ReminderRuleVO> rules = dao.findAllEnabledRules(pkOrg);

            for (ParcelVO parcel : parcels) {
                ReminderLogVO log = generateReminderForParcel(parcel, rules);
                if (log != null) {
                    result.add(log);
                }
            }
        } catch (DAOException e) {
            throw new BusinessException("生成催领记录失败：" + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<ReminderLogVO> generateOverdueReminders(String pkOrg) throws BusinessException {
        List<ReminderLogVO> result = new ArrayList<>();
        try {
            List<ParcelVO> overdueParcels = dao.findOverdueParcels(pkOrg);
            List<ReminderRuleVO> rules = dao.findAllEnabledRules(pkOrg);

            for (ParcelVO parcel : overdueParcels) {
                ReminderLogVO log = generateReminderForParcel(parcel, rules);
                if (log != null) {
                    result.add(log);
                }
            }
        } catch (DAOException e) {
            throw new BusinessException("批量生成催领记录失败：" + e.getMessage(), e);
        }
        return result;
    }

    private ReminderLogVO generateReminderForParcel(ParcelVO parcel, List<ReminderRuleVO> rules)
            throws DAOException, BusinessException {

        ReminderResultVO result = generateReminderForParcelWithVersion(parcel, rules);
        return result != null ? result.getReminderLog() : null;
    }

    private ReminderResultVO generateReminderForParcelWithVersion(ParcelVO parcel, List<ReminderRuleVO> rules)
            throws DAOException, BusinessException {

        if (parcel.getReturn_processing() != null && parcel.getReturn_processing() == 1) {
            return null;
        }

        if (parcel.getParcel_status() != ParcelVO.STATUS_PENDING) {
            return null;
        }

        ParcelVO oldParcel = (ParcelVO) parcel.clone();

        ReminderRuleVO rule = ruleMatcher.matchRule(parcel, rules);
        if (rule == null) {
            throw new BusinessException("未找到匹配的催领规则，包裹单号：" + parcel.getExpress_no());
        }

        int currentCount = dao.getReminderCountForParcel(parcel.getPk_parcel());
        if (rule.getMax_reminder_count() != null && currentCount >= rule.getMax_reminder_count()) {
            return null;
        }

        int daysOverdue = ExpressUtils.calculateDaysOverdue(
                parcel.getInbound_time().getMillis(),
                rule.getRetention_days() != null ? rule.getRetention_days() : DEFAULT_RETENTION_DAYS);

        if (daysOverdue < 0) {
            return null;
        }

        List<ParcelVersionCompareVO> beforeCompare = ParcelVersionComparator.compareParcelBeforeReminder(
                parcel, rule, daysOverdue);

        String userId = InvocationInfoProxy.getInstance().getUserId();
        String groupId = InvocationInfoProxy.getInstance().getGroupId();
        UFDateTime now = new UFDateTime();

        ReminderLogVO log = new ReminderLogVO();
        log.setPk_group(groupId);
        log.setPk_org(parcel.getPk_org());
        log.setCreator(userId);
        log.setCreationtime(now);
        log.setModifier(userId);
        log.setModifiedtime(now);
        log.setDr(0);

        log.setPk_parcel(parcel.getPk_parcel());
        log.setPk_rule(rule.getPk_rule());
        log.setReminder_type(rule.getReminder_type());
        log.setReminder_time(now);
        log.setReminder_status(ReminderLogVO.STATUS_SENT);
        log.setPickup_code(parcel.getPickup_code());
        log.setReminder_count(currentCount + 1);
        log.setOperator(userId);
        log.setArea_code(parcel.getArea_code());
        log.setReminder_content(ExpressUtils.generateReminderContent(
                parcel.getReceiver_name(),
                parcel.getPickup_code(),
                parcel.getExpress_no(),
                daysOverdue));

        sendReminder(parcel, rule, log);

        ReminderLogVO savedLog = dao.insertReminderLog(log);

        ParcelVO newParcel = dao.findParcelByPK(parcel.getPk_parcel());
        if (newParcel == null) {
            newParcel = oldParcel;
        }

        List<ParcelVersionCompareVO> afterCompare = ParcelVersionComparator.compareParcelAfterReminder(
                oldParcel, newParcel, savedLog, rule);

        List<ParcelVersionCompareVO> allCompare = new ArrayList<>();
        allCompare.addAll(beforeCompare);
        allCompare.addAll(afterCompare);

        for (ParcelVersionCompareVO compareVO : allCompare) {
            compareVO.setPk_log(savedLog.getPk_log());
            dao.insertVersionCompare(compareVO);
        }

        ReminderResultVO result = new ReminderResultVO(savedLog, allCompare);
        result.setVersionCompareSummary(ParcelVersionComparator.generateVersionCompareSummary(allCompare));

        return result;
    }

    private void sendReminder(ParcelVO parcel, ReminderRuleVO rule, ReminderLogVO log) {
        try {
            int type = rule.getReminder_type();
            switch (type) {
                case ReminderRuleVO.TYPE_SMS:
                    sendSMS(parcel.getReceiver_phone(), log.getReminder_content());
                    break;
                case ReminderRuleVO.TYPE_PHONE:
                    makePhoneCall(parcel.getReceiver_phone());
                    break;
                case ReminderRuleVO.TYPE_APP:
                    sendAppPush(parcel.getReceiver_phone(), log.getReminder_content());
                    break;
                case ReminderRuleVO.TYPE_DOOR:
                    arrangeDoorVisit(parcel, log);
                    break;
                default:
                    break;
            }
            log.setReminder_status(ReminderLogVO.STATUS_SENT);
        } catch (Exception e) {
            log.setReminder_status(ReminderLogVO.STATUS_FAILED);
            log.setFail_reason(e.getMessage());
        }
    }

    @Override
    public ReminderLogVO resendPickupCode(String pkParcel) throws BusinessException {
        try {
            ParcelVO parcel = dao.findParcelByPK(pkParcel);
            if (parcel == null) {
                throw new BusinessException("包裹不存在");
            }

            if (parcel.getReturn_processing() != null && parcel.getReturn_processing() == 1) {
                throw new BusinessException("退回处理中的包裹不能重发取件码");
            }

            if (parcel.getParcel_status() != ParcelVO.STATUS_PENDING) {
                throw new BusinessException("包裹已取件或已退回，无需重发取件码");
            }

            UFDateTime now = new UFDateTime();
            String userId = InvocationInfoProxy.getInstance().getUserId();

            String newPickupCode = ExpressUtils.generatePickupCode();
            parcel.setPickup_code(newPickupCode);
            parcel.setPickup_code_expire(getExpireTime(now));
            parcel.setModifier(userId);
            parcel.setModifiedtime(now);
            dao.updateParcel(parcel);

            ReminderLogVO log = new ReminderLogVO();
            log.setPk_group(parcel.getPk_group());
            log.setPk_org(parcel.getPk_org());
            log.setCreator(userId);
            log.setCreationtime(now);
            log.setModifier(userId);
            log.setModifiedtime(now);
            log.setDr(0);

            log.setPk_parcel(parcel.getPk_parcel());
            log.setReminder_type(ReminderRuleVO.TYPE_SMS);
            log.setReminder_time(now);
            log.setReminder_status(ReminderLogVO.STATUS_SENT);
            log.setPickup_code(newPickupCode);
            log.setReminder_count(dao.getReminderCountForParcel(pkParcel) + 1);
            log.setOperator(userId);
            log.setArea_code(parcel.getArea_code());
            log.setRemark("取件码过期重发");
            log.setReminder_content(ExpressUtils.generatePickupCodeResendContent(
                    parcel.getReceiver_name(),
                    newPickupCode,
                    parcel.getExpress_no()));

            sendSMS(parcel.getReceiver_phone(), log.getReminder_content());

            return dao.insertReminderLog(log);
        } catch (DAOException e) {
            throw new BusinessException("重发取件码失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ParcelVO updateReturnStatus(String pkParcel, Integer returnStatus) throws BusinessException {
        try {
            ParcelVO parcel = dao.findParcelByPK(pkParcel);
            if (parcel == null) {
                throw new BusinessException("包裹不存在");
            }

            String userId = InvocationInfoProxy.getInstance().getUserId();
            UFDateTime now = new UFDateTime();

            parcel.setReturn_processing(returnStatus);
            parcel.setModifier(userId);
            parcel.setModifiedtime(now);

            if (returnStatus == 1) {
                parcel.setParcel_status(ParcelVO.STATUS_RETURNING);
            } else if (returnStatus == 2) {
                parcel.setParcel_status(ParcelVO.STATUS_RETURNED);
            }

            return dao.updateParcel(parcel);
        } catch (DAOException e) {
            throw new BusinessException("更新退回状态失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ParcelVO updateParcelStatus(String pkParcel, Integer status) throws BusinessException {
        try {
            ParcelVO parcel = dao.findParcelByPK(pkParcel);
            if (parcel == null) {
                throw new BusinessException("包裹不存在");
            }

            String userId = InvocationInfoProxy.getInstance().getUserId();
            UFDateTime now = new UFDateTime();

            parcel.setParcel_status(status);
            parcel.setModifier(userId);
            parcel.setModifiedtime(now);

            if (status == ParcelVO.STATUS_PICKED) {
                parcel.setPickup_time(now);
            }

            return dao.updateParcel(parcel);
        } catch (DAOException e) {
            throw new BusinessException("更新包裹状态失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> queryReminderLogs(String pkOrg, String areaCode,
                                                 Integer reminderType, Integer status,
                                                 UFDateTime startTime, UFDateTime endTime,
                                                 int page, int pageSize) throws BusinessException {
        try {
            int pageStart = (page - 1) * pageSize;
            List<ReminderLogVO> logs = dao.findReminderLogsByCondition(
                    pkOrg, areaCode, reminderType, status, startTime, endTime, pageStart, pageSize);

            int total = dao.countReminderLogsByCondition(
                    pkOrg, areaCode, reminderType, status, startTime, endTime);

            Map<String, Object> result = new HashMap<>();
            result.put("list", logs);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", (int) Math.ceil((double) total / pageSize));

            return result;
        } catch (DAOException e) {
            throw new BusinessException("查询催领记录失败：" + e.getMessage(), e);
        }
    }

    @Override
    public ParcelVO findParcelByPK(String pkParcel) throws BusinessException {
        try {
            return dao.findParcelByPK(pkParcel);
        } catch (DAOException e) {
            throw new BusinessException("查询包裹失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ReminderLogVO> findReminderLogsByParcel(String pkParcel) throws BusinessException {
        try {
            return dao.findReminderLogsByParcel(pkParcel);
        } catch (DAOException e) {
            throw new BusinessException("查询催领记录失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ReminderResultVO> generateRemindersWithVersion(String pkOrg, String[] pkParcels) throws BusinessException {
        if (pkParcels == null || pkParcels.length == 0) {
            throw new BusinessException("请选择需要催领的包裹");
        }

        List<ReminderResultVO> result = new ArrayList<>();
        try {
            List<ParcelVO> parcels = dao.findParcelsByPKs(pkParcels);
            List<ReminderRuleVO> rules = dao.findAllEnabledRules(pkOrg);

            for (ParcelVO parcel : parcels) {
                ReminderResultVO reminderResult = generateReminderForParcelWithVersion(parcel, rules);
                if (reminderResult != null) {
                    result.add(reminderResult);
                }
            }
        } catch (DAOException e) {
            throw new BusinessException("生成催领记录失败：" + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<ReminderResultVO> generateOverdueRemindersWithVersion(String pkOrg) throws BusinessException {
        List<ReminderResultVO> result = new ArrayList<>();
        try {
            List<ParcelVO> overdueParcels = dao.findOverdueParcels(pkOrg);
            List<ReminderRuleVO> rules = dao.findAllEnabledRules(pkOrg);

            for (ParcelVO parcel : overdueParcels) {
                ReminderResultVO reminderResult = generateReminderForParcelWithVersion(parcel, rules);
                if (reminderResult != null) {
                    result.add(reminderResult);
                }
            }
        } catch (DAOException e) {
            throw new BusinessException("批量生成催领记录失败：" + e.getMessage(), e);
        }
        return result;
    }

    @Override
    public ReminderResultVO resendPickupCodeWithVersion(String pkParcel) throws BusinessException {
        try {
            ParcelVO oldParcel = dao.findParcelByPK(pkParcel);
            if (oldParcel == null) {
                throw new BusinessException("包裹不存在");
            }

            if (oldParcel.getReturn_processing() != null && oldParcel.getReturn_processing() == 1) {
                throw new BusinessException("退回处理中的包裹不能重发取件码");
            }

            if (oldParcel.getParcel_status() != ParcelVO.STATUS_PENDING) {
                throw new BusinessException("包裹已取件或已退回，无需重发取件码");
            }

            UFDateTime now = new UFDateTime();
            String userId = InvocationInfoProxy.getInstance().getUserId();

            String newPickupCode = ExpressUtils.generatePickupCode();
            ParcelVO newParcel = (ParcelVO) oldParcel.clone();
            newParcel.setPickup_code(newPickupCode);
            newParcel.setPickup_code_expire(getExpireTime(now));
            newParcel.setModifier(userId);
            newParcel.setModifiedtime(now);
            dao.updateParcel(newParcel);

            ReminderLogVO log = new ReminderLogVO();
            log.setPk_group(oldParcel.getPk_group());
            log.setPk_org(oldParcel.getPk_org());
            log.setCreator(userId);
            log.setCreationtime(now);
            log.setModifier(userId);
            log.setModifiedtime(now);
            log.setDr(0);

            log.setPk_parcel(oldParcel.getPk_parcel());
            log.setReminder_type(ReminderRuleVO.TYPE_SMS);
            log.setReminder_time(now);
            log.setReminder_status(ReminderLogVO.STATUS_SENT);
            log.setPickup_code(newPickupCode);
            log.setReminder_count(dao.getReminderCountForParcel(pkParcel) + 1);
            log.setOperator(userId);
            log.setArea_code(oldParcel.getArea_code());
            log.setRemark("取件码过期重发");
            log.setReminder_content(ExpressUtils.generatePickupCodeResendContent(
                    oldParcel.getReceiver_name(),
                    newPickupCode,
                    oldParcel.getExpress_no()));

            sendSMS(oldParcel.getReceiver_phone(), log.getReminder_content());

            ReminderLogVO savedLog = dao.insertReminderLog(log);

            List<ParcelVersionCompareVO> compareList = ParcelVersionComparator.compareParcelForResend(
                    oldParcel, newParcel, savedLog);

            for (ParcelVersionCompareVO compareVO : compareList) {
                compareVO.setPk_log(savedLog.getPk_log());
                dao.insertVersionCompare(compareVO);
            }

            ReminderResultVO result = new ReminderResultVO(savedLog, compareList);
            result.setVersionCompareSummary(ParcelVersionComparator.generateVersionCompareSummary(compareList));

            return result;
        } catch (DAOException e) {
            throw new BusinessException("重发取件码失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ParcelVersionCompareVO> findVersionCompareByParcel(String pkParcel) throws BusinessException {
        try {
            return dao.findVersionCompareByParcel(pkParcel);
        } catch (DAOException e) {
            throw new BusinessException("查询版本对比记录失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<ParcelVersionCompareVO> findVersionCompareByLog(String pkLog) throws BusinessException {
        try {
            return dao.findVersionCompareByLog(pkLog);
        } catch (DAOException e) {
            throw new BusinessException("查询版本对比记录失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> queryVersionCompareLogs(String pkOrg, String pkParcel,
                                                       Integer compareResult,
                                                       UFDateTime startTime, UFDateTime endTime,
                                                       int page, int pageSize) throws BusinessException {
        try {
            int pageStart = (page - 1) * pageSize;
            List<ParcelVersionCompareVO> logs = dao.findVersionCompareByCondition(
                    pkOrg, pkParcel, compareResult, startTime, endTime, pageStart, pageSize);

            int total = dao.countVersionCompareByCondition(
                    pkOrg, pkParcel, compareResult, startTime, endTime);

            Map<String, Object> result = new HashMap<>();
            result.put("list", logs);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", (int) Math.ceil((double) total / pageSize));

            return result;
        } catch (DAOException e) {
            throw new BusinessException("查询版本对比记录失败：" + e.getMessage(), e);
        }
    }

    private void validateInboundData(ParcelVO parcelVO) throws BusinessException {
        if (StringUtils.isEmpty(parcelVO.getExpress_no())) {
            throw new BusinessException("快递单号不能为空");
        }
        if (StringUtils.isEmpty(parcelVO.getReceiver_name())) {
            throw new BusinessException("收件人姓名不能为空");
        }
        if (StringUtils.isEmpty(parcelVO.getReceiver_phone())) {
            throw new BusinessException("收件人电话不能为空");
        }
        if (StringUtils.isEmpty(parcelVO.getPk_org())) {
            throw new BusinessException("组织不能为空");
        }
    }

    private UFDateTime getExpireTime(UFDateTime now) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getMillis());
        cal.add(Calendar.HOUR, PICKUP_CODE_EXPIRE_HOURS);
        return new UFDateTime(cal.getTimeInMillis());
    }

    private void sendSMS(String phone, String content) {
        // TODO: 集成短信网关
        System.out.println("【短信】发送到 " + phone + ": " + content);
    }

    private void makePhoneCall(String phone) {
        // TODO: 集成电话系统
        System.out.println("【电话】拨打 " + phone);
    }

    private void sendAppPush(String phone, String content) {
        // TODO: 集成APP推送服务
        System.out.println("【APP推送】发送到 " + phone + ": " + content);
    }

    private void arrangeDoorVisit(ParcelVO parcel, ReminderLogVO log) {
        // TODO: 安排上门通知
        System.out.println("【上门通知】安排人员上门，收件人：" + parcel.getReceiver_name());
    }
}
