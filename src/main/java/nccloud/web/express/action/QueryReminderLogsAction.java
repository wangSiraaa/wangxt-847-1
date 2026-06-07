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
import nc.vo.pub.lang.UFDateTime;

import java.util.Map;

public class QueryReminderLogsAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        try {
            IJson json = JsonFactory.create();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = json.fromJson(request.read(), Map.class);

            ClientInfo clientInfo = SessionContext.getInstance().getClientInfo();
            String pkOrg = clientInfo.getPk_org();

            String areaCode = map != null ? (String) map.get("areaCode") : null;
            Integer reminderType = map != null && map.get("reminderType") != null
                    ? (Integer) map.get("reminderType") : null;
            Integer status = map != null && map.get("status") != null
                    ? (Integer) map.get("status") : null;

            UFDateTime startTime = null;
            UFDateTime endTime = null;
            if (map != null && map.get("startTime") != null) {
                startTime = new UFDateTime(map.get("startTime").toString());
            }
            if (map != null && map.get("endTime") != null) {
                endTime = new UFDateTime(map.get("endTime").toString());
            }

            int page = map != null && map.get("page") != null
                    ? ((Number) map.get("page")).intValue() : 1;
            int pageSize = map != null && map.get("pageSize") != null
                    ? ((Number) map.get("pageSize")).intValue() : 20;

            IExpressReminderService service = ServiceLocator.find(IExpressReminderService.class);
            Map<String, Object> result = service.queryReminderLogs(
                    pkOrg, areaCode, reminderType, status, startTime, endTime, page, pageSize);

            return buildSuccess(result);
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
