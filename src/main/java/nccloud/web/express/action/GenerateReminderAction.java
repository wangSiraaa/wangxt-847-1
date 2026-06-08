package nccloud.web.express.action;

import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.core.json.IJson;
import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.container.SessionContext;
import nccloud.framework.web.container.ClientInfo;
import nccloud.framework.web.json.JsonFactory;
import nc.itf.express.IExpressReminderService;
import nc.vo.express.ReminderLogVO;
import nc.vo.express.ReminderResultVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateReminderAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        try {
            IJson json = JsonFactory.create();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = json.fromJson(request.read(), Map.class);

            ClientInfo clientInfo = SessionContext.getInstance().getClientInfo();
            String pkOrg = clientInfo.getPk_org();

            String[] pkParcels = null;
            if (map != null && map.get("pkParcels") != null) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) map.get("pkParcels");
                pkParcels = list.toArray(new String[0]);
            }

            IExpressReminderService service = ServiceLocator.find(IExpressReminderService.class);
            List<ReminderResultVO> results;

            if (pkParcels != null && pkParcels.length > 0) {
                results = service.generateRemindersWithVersion(pkOrg, pkParcels);
            } else {
                results = service.generateOverdueRemindersWithVersion(pkOrg);
            }

            List<ReminderLogVO> logs = new ArrayList<>();
            List<Map<String, Object>> versionCompareList = new ArrayList<>();
            for (ReminderResultVO result : results) {
                logs.add(result.getReminderLog());
                Map<String, Object> compareInfo = new HashMap<>();
                compareInfo.put("pk_log", result.getReminderLog().getPk_log());
                compareInfo.put("pk_parcel", result.getReminderLog().getPk_parcel());
                compareInfo.put("compareCount", result.getVersionCompareList().size());
                compareInfo.put("changedCount", result.getChangedFieldCount());
                compareInfo.put("unchangedCount", result.getUnchangedFieldCount());
                compareInfo.put("newFieldCount", result.getNewFieldCount());
                compareInfo.put("summary", result.getVersionCompareSummary());
                versionCompareList.add(compareInfo);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("logs", logs);
            response.put("versionCompareSummary", versionCompareList);
            response.put("total", logs.size());

            return buildSuccess(response);
        } catch (Exception e) {
            ExceptionUtils.wrapException(e);
            return buildError(e.getMessage());
        }
    }

    private Map<String, Object> buildSuccess(Object data) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return result;
    }

    private Map<String, Object> buildError(String message) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", false);
        java.util.Map<String, Object> error = new java.util.HashMap<>();
        error.put("message", message);
        result.put("error", error);
        return result;
    }
}
