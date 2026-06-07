package nc.itf.express;

import nc.vo.express.ParcelVO;
import nc.vo.express.ReminderLogVO;
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
}
