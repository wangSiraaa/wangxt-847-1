package nc.bs.express;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.express.rule.ExpressRuleMatcher;
import nc.impl.pubapp.pattern.database.SqlBuilderUtil;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.SQLParameter;
import nc.vo.express.ParcelVO;
import nc.vo.express.ReminderLogVO;
import nc.vo.express.ReminderRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nccloud.util.PaginationUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public class ExpressReminderDAO extends BaseDAO {

    public ParcelVO insertParcel(ParcelVO vo) throws DAOException {
        vo.setStatus(nc.vo.pub.VOStatus.NEW);
        return (ParcelVO) this.insertVO(vo);
    }

    public ParcelVO updateParcel(ParcelVO vo) throws DAOException {
        vo.setStatus(nc.vo.pub.VOStatus.UPDATED);
        this.updateVO(vo);
        return vo;
    }

    public ParcelVO findParcelByPK(String pkParcel) throws DAOException {
        return (ParcelVO) this.retrieveByPK(ParcelVO.class, pkParcel);
    }

    public ParcelVO findParcelByExpressNo(String expressNo, String pkOrg) throws DAOException {
        String condition = "express_no = ? and pk_org = ? and dr = 0";
        Object[] params = new Object[]{expressNo, pkOrg};
        Collection<ParcelVO> result = this.retrieveByClause(ParcelVO.class, condition, params);
        if (result != null && !result.isEmpty()) {
            return result.iterator().next();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<ParcelVO> findOverdueParcels(String pkOrg) throws DAOException {
        String sql = "SELECT p.* FROM express_parcel p "
                   + "INNER JOIN express_reminder_rule r ON 1=1 "
                   + "WHERE p.dr = 0 AND r.dr = 0 AND r.enabled = 1 "
                   + "AND p.parcel_status = 0 "
                   + "AND (p.return_processing IS NULL OR p.return_processing = 0) "
                   + "AND p.pk_org = ? "
                   + "AND NOT EXISTS ("
                   + "    SELECT 1 FROM express_reminder_log l "
                   + "    WHERE l.pk_parcel = p.pk_parcel "
                   + "    AND l.dr = 0 "
                   + "    AND l.reminder_status IN (0,1) "
                   + ") "
                   + "AND EXISTS ("
                   + "    SELECT 1 FROM express_reminder_rule r2 "
                   + "    WHERE r2.dr = 0 AND r2.enabled = 1 "
                   + "    AND (r2.cond_vip IS NULL OR r2.cond_vip = p.is_vip) "
                   + "    AND (r2.cond_large IS NULL OR r2.cond_large = p.is_large) "
                   + "    AND (r2.cond_remote IS NULL OR r2.cond_remote = p.is_remote) "
                   + "    AND (r2.area_code IS NULL OR r2.area_code = p.area_code) "
                   + "    AND (r2.min_weight IS NULL OR p.weight >= r2.min_weight) "
                   + "    AND (r2.max_weight IS NULL OR p.weight <= r2.max_weight) "
                   + "    AND TRUNC(SYSDATE) - TRUNC(p.inbound_time) >= r2.retention_days "
                   + ") "
                   + "ORDER BY p.inbound_time ASC";

        Object[] params = new Object[]{pkOrg};
        return (List<ParcelVO>) this.executeQuery(sql, params,
                new BeanListProcessor(ParcelVO.class));
    }

    @SuppressWarnings("unchecked")
    public List<ParcelVO> findParcelsWithExpiredPickupCode(String pkOrg) throws DAOException {
        String sql = "SELECT * FROM express_parcel "
                   + "WHERE dr = 0 AND parcel_status = 0 "
                   + "AND pk_org = ? "
                   + "AND pickup_code_expire < SYSDATE "
                   + "AND (return_processing IS NULL OR return_processing = 0) "
                   + "ORDER BY pickup_code_expire ASC";

        Object[] params = new Object[]{pkOrg};
        return (List<ParcelVO>) this.executeQuery(sql, params,
                new BeanListProcessor(ParcelVO.class));
    }

    @SuppressWarnings("unchecked")
    public List<ReminderRuleVO> findAllEnabledRules(String pkOrg) throws DAOException {
        String condition = "dr = 0 AND enabled = 1 AND (pk_org = ? OR pk_org IS NULL) "
                         + "ORDER BY priority DESC";
        Object[] params = new Object[]{pkOrg};
        return (List<ReminderRuleVO>) this.retrieveByClause(ReminderRuleVO.class, condition, params);
    }

    public ReminderRuleVO findRuleByPK(String pkRule) throws DAOException {
        return (ReminderRuleVO) this.retrieveByPK(ReminderRuleVO.class, pkRule);
    }

    public ReminderLogVO insertReminderLog(ReminderLogVO vo) throws DAOException {
        vo.setStatus(nc.vo.pub.VOStatus.NEW);
        return (ReminderLogVO) this.insertVO(vo);
    }

    public ReminderLogVO updateReminderLog(ReminderLogVO vo) throws DAOException {
        vo.setStatus(nc.vo.pub.VOStatus.UPDATED);
        this.updateVO(vo);
        return vo;
    }

    @SuppressWarnings("unchecked")
    public List<ReminderLogVO> findReminderLogsByParcel(String pkParcel) throws DAOException {
        String condition = "pk_parcel = ? AND dr = 0 ORDER BY reminder_time DESC";
        Object[] params = new Object[]{pkParcel};
        return (List<ReminderLogVO>) this.retrieveByClause(ReminderLogVO.class, condition, params);
    }

    @SuppressWarnings("unchecked")
    public List<ReminderLogVO> findReminderLogsByCondition(String pkOrg, String areaCode,
                                                           Integer reminderType, Integer status,
                                                           UFDateTime startTime, UFDateTime endTime,
                                                           int pageStart, int pageSize) throws DAOException {
        StringBuilder sql = new StringBuilder();
        SQLParameter sqlParam = new SQLParameter();

        sql.append("SELECT l.* FROM express_reminder_log l ");
        sql.append("WHERE l.dr = 0 ");
        sql.append("AND l.pk_org = ? ");
        sqlParam.addParam(pkOrg);

        if (areaCode != null && !areaCode.isEmpty()) {
            sql.append("AND l.area_code = ? ");
            sqlParam.addParam(areaCode);
        }
        if (reminderType != null) {
            sql.append("AND l.reminder_type = ? ");
            sqlParam.addParam(reminderType);
        }
        if (status != null) {
            sql.append("AND l.reminder_status = ? ");
            sqlParam.addParam(status);
        }
        if (startTime != null) {
            sql.append("AND l.reminder_time >= ? ");
            sqlParam.addParam(startTime);
        }
        if (endTime != null) {
            sql.append("AND l.reminder_time <= ? ");
            sqlParam.addParam(endTime);
        }

        sql.append("ORDER BY l.reminder_time DESC, l.pk_log DESC");

        int offset = Math.max(0, pageStart);
        int safePageSize = Math.max(1, Math.min(pageSize, 500));

        String paginationSql = PaginationUtil.generatePaginationSql(
                sql.toString(), offset, safePageSize, sqlParam);

        return (List<ReminderLogVO>) this.executeQuery(paginationSql, sqlParam,
                new BeanListProcessor(ReminderLogVO.class));
    }

    public int countReminderLogsByCondition(String pkOrg, String areaCode,
                                            Integer reminderType, Integer status,
                                            UFDateTime startTime, UFDateTime endTime) throws DAOException {
        StringBuilder sql = new StringBuilder();
        SQLParameter sqlParam = new SQLParameter();

        sql.append("SELECT COUNT(1) FROM express_reminder_log l ");
        sql.append("WHERE l.dr = 0 ");
        sql.append("AND l.pk_org = ? ");
        sqlParam.addParam(pkOrg);

        if (areaCode != null && !areaCode.isEmpty()) {
            sql.append("AND l.area_code = ? ");
            sqlParam.addParam(areaCode);
        }
        if (reminderType != null) {
            sql.append("AND l.reminder_type = ? ");
            sqlParam.addParam(reminderType);
        }
        if (status != null) {
            sql.append("AND l.reminder_status = ? ");
            sqlParam.addParam(status);
        }
        if (startTime != null) {
            sql.append("AND l.reminder_time >= ? ");
            sqlParam.addParam(startTime);
        }
        if (endTime != null) {
            sql.append("AND l.reminder_time <= ? ");
            sqlParam.addParam(endTime);
        }

        Object result = this.executeQuery(sql.toString(), sqlParam,
                new ColumnProcessor());
        return result != null ? ((Number) result).intValue() : 0;
    }

    public int getReminderCountForParcel(String pkParcel) throws DAOException {
        String sql = "SELECT COUNT(1) FROM express_reminder_log "
                   + "WHERE pk_parcel = ? AND dr = 0 AND reminder_status IN (0,1)";
        Object[] params = new Object[]{pkParcel};
        Object result = this.executeQuery(sql, params, new ColumnProcessor());
        return result != null ? ((Number) result).intValue() : 0;
    }

    @SuppressWarnings("unchecked")
    public List<ParcelVO> findParcelsByPKs(String[] pkParcels) throws DAOException {
        String condition = SqlBuilderUtil.buildSqlForIn(ParcelVO.PK_PARCEL, pkParcels)
                         + " AND dr = 0";
        return (List<ParcelVO>) this.retrieveByClause(ParcelVO.class, condition);
    }

    public boolean isReturnProcessing(String pkParcel) throws DAOException, BusinessException {
        ParcelVO vo = findParcelByPK(pkParcel);
        if (vo == null) {
            throw new BusinessException("包裹不存在");
        }
        return vo.getReturn_processing() != null && vo.getReturn_processing() == 1;
    }
}
