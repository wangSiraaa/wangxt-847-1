package nc.itf.express;

import nc.vo.express.ParcelVersionCompareVO;
import nc.vo.express.ParcelVO;
import nc.vo.express.ReminderLogVO;
import nc.vo.express.ReminderResultVO;
import nc.vo.express.ReminderRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

import java.util.List;
import java.util.Map;

public interface IExpressReminderService {

    ParcelVO inbound(ParcelVO parcelVO) throws BusinessException;

    ReminderRuleVO calculateRule(ParcelVO parcelVO) throws BusinessException;

    List<ReminderLogVO> generateReminders(String pkOrg, String[] pkParcels) throws BusinessException;

    List<ReminderLogVO> generateOverdueReminders(String pkOrg) throws BusinessException;

    ReminderLogVO resendPickupCode(String pkParcel) throws BusinessException;

    ParcelVO updateReturnStatus(String pkParcel, Integer returnStatus) throws BusinessException;

    ParcelVO updateParcelStatus(String pkParcel, Integer status) throws BusinessException;

    Map<String, Object> queryReminderLogs(String pkOrg, String areaCode,
                                          Integer reminderType, Integer status,
                                          UFDateTime startTime, UFDateTime endTime,
                                          int page, int pageSize) throws BusinessException;

    ParcelVO findParcelByPK(String pkParcel) throws BusinessException;

    List<ReminderLogVO> findReminderLogsByParcel(String pkParcel) throws BusinessException;

    List<ReminderResultVO> generateRemindersWithVersion(String pkOrg, String[] pkParcels) throws BusinessException;

    List<ReminderResultVO> generateOverdueRemindersWithVersion(String pkOrg) throws BusinessException;

    ReminderResultVO resendPickupCodeWithVersion(String pkParcel) throws BusinessException;

    List<ParcelVersionCompareVO> findVersionCompareByParcel(String pkParcel) throws BusinessException;

    List<ParcelVersionCompareVO> findVersionCompareByLog(String pkLog) throws BusinessException;

    Map<String, Object> queryVersionCompareLogs(String pkOrg, String pkParcel,
                                                 Integer compareResult,
                                                 UFDateTime startTime, UFDateTime endTime,
                                                 int page, int pageSize) throws BusinessException;
}
