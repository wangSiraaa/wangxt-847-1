package nccloud.web.express.action;

import nccloud.framework.core.exception.ExceptionUtils;
import nccloud.framework.core.json.IJson;
import nccloud.framework.service.ServiceLocator;
import nccloud.framework.web.action.itf.ICommonAction;
import nccloud.framework.web.container.IRequest;
import nccloud.framework.web.json.JsonFactory;
import nc.itf.express.IExpressReminderService;
import nc.vo.express.ReminderLogVO;

import java.util.Map;

public class ResendPickupCodeAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        try {
            IJson json = JsonFactory.create();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = json.fromJson(request.read(), Map.class);

            String pkParcel = map != null ? (String) map.get("pkParcel") : null;
            if (pkParcel == null || pkParcel.isEmpty()) {
                throw new Exception("包裹主键不能为空");
            }

            IExpressReminderService service = ServiceLocator.find(IExpressReminderService.class);
            ReminderLogVO log = service.resendPickupCode(pkParcel);

            return buildSuccess(log);
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
