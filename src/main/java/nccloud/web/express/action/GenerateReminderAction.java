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
            List<ReminderLogVO> logs;

            if (pkParcels != null && pkParcels.length > 0) {
                logs = service.generateReminders(pkOrg, pkParcels);
            } else {
                logs = service.generateOverdueReminders(pkOrg);
            }

            return buildSuccess(logs);
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
