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
import nc.vo.express.ParcelVO;

import java.util.Map;

public class ParcelInboundAction implements ICommonAction {

    @Override
    public Object doAction(IRequest request) {
        try {
            IJson json = JsonFactory.create();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = json.fromJson(request.read(), Map.class);

            ClientInfo clientInfo = SessionContext.getInstance().getClientInfo();
            String pkOrg = clientInfo.getPk_org();

            ParcelVO parcel = new ParcelVO();
            parcel.setPk_org(pkOrg);
            parcel.setExpress_no(map != null ? (String) map.get("expressNo") : null);
            parcel.setReceiver_name(map != null ? (String) map.get("receiverName") : null);
            parcel.setReceiver_phone(map != null ? (String) map.get("receiverPhone") : null);

            if (map != null && map.get("isVip") != null) {
                parcel.setIs_vip((Integer) map.get("isVip"));
            }
            if (map != null && map.get("isLarge") != null) {
                parcel.setIs_large((Integer) map.get("isLarge"));
            }
            if (map != null && map.get("isRemote") != null) {
                parcel.setIs_remote((Integer) map.get("isRemote"));
            }
            if (map != null && map.get("weight") != null) {
                parcel.setWeight(new nc.vo.pub.lang.UFDouble(map.get("weight").toString()));
            }
            if (map != null && map.get("areaCode") != null) {
                parcel.setArea_code((String) map.get("areaCode"));
            }
            if (map != null && map.get("remark") != null) {
                parcel.setRemark((String) map.get("remark"));
            }

            IExpressReminderService service = ServiceLocator.find(IExpressReminderService.class);
            ParcelVO result = service.inbound(parcel);

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
