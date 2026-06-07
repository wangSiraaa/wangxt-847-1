package nc.impl.bgplugin;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.express.IExpressReminderService;
import nc.itf.bg.IBackgroundWorkPlugin;
import nc.vo.bg.BgTaskVO;
import nc.vo.pub.BusinessException;

import java.util.Map;

public class ExpressOverdueReminderPlugin implements IBackgroundWorkPlugin {

    @Override
    public void execute(BgTaskVO taskVO, Map<String, String> paramMap) throws BusinessException {
        try {
            String pkOrg = paramMap != null ? paramMap.get("pkOrg") : null;
            if (pkOrg == null || pkOrg.isEmpty()) {
                pkOrg = InvocationInfoProxy.getInstance().getPkOrg();
            }
            if (pkOrg == null || pkOrg.isEmpty()) {
                throw new BusinessException("组织主键不能为空");
            }

            IExpressReminderService service = NCLocator.getInstance().lookup(IExpressReminderService.class);
            service.generateOverdueReminders(pkOrg);

        } catch (Exception e) {
            throw new BusinessException("自动催领任务执行失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getDescription() {
        return "快递驿站滞留件超期自动催领任务";
    }

    @Override
    public String getPluginId() {
        return "express_overdue_reminder";
    }

    @Override
    public String getPluginName() {
        return "快递驿站超期自动催领";
    }
}
