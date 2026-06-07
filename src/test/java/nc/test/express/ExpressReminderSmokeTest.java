package nc.test.express;

import nc.bs.express.ExpressReminderDAO;
import nc.bs.express.ExpressReminderServiceImpl;
import nc.express.rule.ExpressRuleMatcher;
import nc.itf.express.IExpressReminderService;
import nc.vo.express.ParcelVO;
import nc.vo.express.ReminderLogVO;
import nc.vo.express.ReminderRuleVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExpressReminderSmokeTest {

    @Mock
    private ExpressReminderDAO dao;

    @InjectMocks
    private ExpressReminderServiceImpl service;

    private List<ReminderRuleVO> testRules;

    @Before
    public void setUp() throws Exception {
        testRules = new ArrayList<>();

        ReminderRuleVO vipRule = new ReminderRuleVO();
        vipRule.setPk_rule("rule_vip");
        vipRule.setRule_name("VIP客户规则");
        vipRule.setCond_vip(1);
        vipRule.setReminder_type(ReminderRuleVO.TYPE_PHONE);
        vipRule.setRetention_days(1);
        vipRule.setMax_reminder_count(3);
        vipRule.setPriority(10);
        vipRule.setEnabled(1);
        testRules.add(vipRule);

        ReminderRuleVO largeRule = new ReminderRuleVO();
        largeRule.setPk_rule("rule_large");
        largeRule.setRule_name("大件包裹规则");
        largeRule.setCond_large(1);
        largeRule.setReminder_type(ReminderRuleVO.TYPE_DOOR);
        largeRule.setRetention_days(1);
        largeRule.setMax_reminder_count(3);
        largeRule.setPriority(9);
        largeRule.setEnabled(1);
        testRules.add(largeRule);

        ReminderRuleVO defaultRule = new ReminderRuleVO();
        defaultRule.setPk_rule("rule_default");
        defaultRule.setRule_name("默认规则");
        defaultRule.setReminder_type(ReminderRuleVO.TYPE_SMS);
        defaultRule.setRetention_days(3);
        defaultRule.setMax_reminder_count(5);
        defaultRule.setPriority(1);
        defaultRule.setEnabled(1);
        testRules.add(defaultRule);

        when(dao.findAllEnabledRules(anyString())).thenReturn(testRules);
    }

    @Test
    public void testRuleMatching_VIP() throws Exception {
        ParcelVO vipParcel = new ParcelVO();
        vipParcel.setIs_vip(1);
        vipParcel.setWeight(new UFDouble(1));

        ExpressRuleMatcher matcher = new ExpressRuleMatcher();
        ReminderRuleVO matchedRule = matcher.matchRule(vipParcel, testRules);

        assertNotNull("VIP包裹应该匹配到VIP规则", matchedRule);
        assertEquals("VIP客户规则", matchedRule.getRule_name());
        assertEquals(ReminderRuleVO.TYPE_PHONE, matchedRule.getReminder_type().intValue());
        assertEquals(1, matchedRule.getRetention_days().intValue());
    }

    @Test
    public void testRuleMatching_Large() throws Exception {
        ParcelVO largeParcel = new ParcelVO();
        largeParcel.setIs_large(1);
        largeParcel.setWeight(new UFDouble(15));

        ExpressRuleMatcher matcher = new ExpressRuleMatcher();
        ReminderRuleVO matchedRule = matcher.matchRule(largeParcel, testRules);

        assertNotNull("大件包裹应该匹配到大件规则", matchedRule);
        assertEquals("大件包裹规则", matchedRule.getRule_name());
        assertEquals(ReminderRuleVO.TYPE_DOOR, matchedRule.getReminder_type().intValue());
    }

    @Test
    public void testRuleMatching_Default() throws Exception {
        ParcelVO normalParcel = new ParcelVO();
        normalParcel.setIs_vip(0);
        normalParcel.setIs_large(0);
        normalParcel.setWeight(new UFDouble(2));

        ExpressRuleMatcher matcher = new ExpressRuleMatcher();
        ReminderRuleVO matchedRule = matcher.matchRule(normalParcel, testRules);

        assertNotNull("普通包裹应该匹配到默认规则", matchedRule);
        assertEquals("默认规则", matchedRule.getRule_name());
        assertEquals(ReminderRuleVO.TYPE_SMS, matchedRule.getReminder_type().intValue());
        assertEquals(3, matchedRule.getRetention_days().intValue());
    }

    @Test
    public void testGenerateOverdueReminders_Smoke() throws Exception {
        String pkOrg = "test_org_001";

        List<ParcelVO> overdueParcels = new ArrayList<>();

        ParcelVO parcel1 = new ParcelVO();
        parcel1.setPk_parcel("parcel_001");
        parcel1.setExpress_no("SF1234567890001");
        parcel1.setReceiver_name("张三");
        parcel1.setReceiver_phone("13800138001");
        parcel1.setPk_org(pkOrg);
        parcel1.setIs_vip(1);
        parcel1.setInbound_time(new UFDateTime(System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L));
        parcel1.setParcel_status(ParcelVO.STATUS_PENDING);
        parcel1.setReturn_processing(0);
        parcel1.setDr(0);
        overdueParcels.add(parcel1);

        ParcelVO parcel2 = new ParcelVO();
        parcel2.setPk_parcel("parcel_002");
        parcel2.setExpress_no("SF1234567890002");
        parcel2.setReceiver_name("李四");
        parcel2.setReceiver_phone("13800138002");
        parcel2.setPk_org(pkOrg);
        parcel2.setIs_vip(0);
        parcel2.setIs_large(1);
        parcel2.setWeight(new UFDouble(20));
        parcel2.setInbound_time(new UFDateTime(System.currentTimeMillis() - 3L * 24L * 60L * 60L * 1000L));
        parcel2.setParcel_status(ParcelVO.STATUS_PENDING);
        parcel2.setReturn_processing(0);
        parcel2.setDr(0);
        overdueParcels.add(parcel2);

        ParcelVO parcel3 = new ParcelVO();
        parcel3.setPk_parcel("parcel_003");
        parcel3.setExpress_no("SF1234567890003");
        parcel3.setReceiver_name("王五");
        parcel3.setReceiver_phone("13800138003");
        parcel3.setPk_org(pkOrg);
        parcel3.setInbound_time(new UFDateTime(System.currentTimeMillis() - 5L * 24L * 60L * 60L * 1000L));
        parcel3.setParcel_status(ParcelVO.STATUS_PENDING);
        parcel3.setReturn_processing(1);
        parcel3.setDr(0);
        overdueParcels.add(parcel3);

        when(dao.findOverdueParcels(pkOrg)).thenReturn(overdueParcels);
        when(dao.countReminderLogsByCondition(anyString(), anyString(), anyInt())).thenReturn(0);
        when(dao.insertReminderLog(any(ReminderLogVO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IExpressReminderService reminderService = service;
        List<ReminderLogVO> logs = reminderService.generateOverdueReminders(pkOrg);

        assertNotNull("催领记录列表不应为空", logs);
        assertEquals("应该生成2条催领记录（退回处理中的不生成）", 2, logs.size());

        ReminderLogVO log1 = logs.stream()
                .filter(l -> l.getPk_parcel().equals("parcel_001"))
                .findFirst().orElse(null);
        assertNotNull("VIP包裹应该生成催领记录", log1);
        assertEquals(ReminderRuleVO.TYPE_PHONE, log1.getReminder_type().intValue());
        assertEquals(1, log1.getReminder_count().intValue());

        ReminderLogVO log2 = logs.stream()
                .filter(l -> l.getPk_parcel().equals("parcel_002"))
                .findFirst().orElse(null);
        assertNotNull("大件包裹应该生成催领记录", log2);
        assertEquals(ReminderRuleVO.TYPE_DOOR, log2.getReminder_type().intValue());
        assertEquals(1, log2.getReminder_count().intValue());

        boolean hasReturnProcessing = logs.stream()
                .anyMatch(l -> l.getPk_parcel().equals("parcel_003"));
        assertFalse("退回处理中的包裹不应该生成催领记录", hasReturnProcessing);

        verify(dao, times(1)).findOverdueParcels(pkOrg);
        verify(dao, times(2)).insertReminderLog(any(ReminderLogVO.class));
    }

    @Test
    public void testReturnProcessingBlockReminder() throws Exception {
        ParcelVO returningParcel = new ParcelVO();
        returningParcel.setPk_parcel("parcel_returning");
        returningParcel.setExpress_no("SF9999999999999");
        returningParcel.setReceiver_name("赵六");
        returningParcel.setReceiver_phone("13800138999");
        returningParcel.setReturn_processing(1);
        returningParcel.setInbound_time(new UFDateTime(System.currentTimeMillis() - 10L * 24L * 60L * 60L * 1000L));
        returningParcel.setParcel_status(ParcelVO.STATUS_RETURNING);

        when(dao.countReminderLogsByCondition(anyString(), anyString(), anyInt())).thenReturn(0);

        java.lang.reflect.Method method = ExpressReminderServiceImpl.class.getDeclaredMethod(
                "generateReminderForParcel", ParcelVO.class, List.class);
        method.setAccessible(true);
        Object result = method.invoke(service, returningParcel, testRules);

        assertNull("退回处理中的包裹应该返回null，不生成催领", result);
    }

    @Test
    public void testPickupCodeExpireResend() throws Exception {
        String pkParcel = "parcel_expired";

        ParcelVO expiredParcel = new ParcelVO();
        expiredParcel.setPk_parcel(pkParcel);
        expiredParcel.setExpress_no("SF1111111111111");
        expiredParcel.setReceiver_name("孙七");
        expiredParcel.setReceiver_phone("13800138111");
        expiredParcel.setPk_org("test_org_001");
        expiredParcel.setPickup_code("123456");
        expiredParcel.setPickup_code_expire(new UFDateTime(System.currentTimeMillis() - 2L * 24L * 60L * 60L * 1000L));
        expiredParcel.setParcel_status(ParcelVO.STATUS_PENDING);
        expiredParcel.setReturn_processing(0);
        expiredParcel.setDr(0);

        when(dao.findParcelByPK(pkParcel)).thenReturn(expiredParcel);
        when(dao.updateParcel(any(ParcelVO.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(dao.insertReminderLog(any(ReminderLogVO.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IExpressReminderService reminderService = service;
        ReminderLogVO log = reminderService.resendPickupCode(pkParcel);

        assertNotNull("取件码过期重发应该生成催领记录", log);
        assertEquals(ReminderLogVO.STATUS_SENT, log.getReminder_status().intValue());
        assertTrue("催领内容应该包含取件码", log.getReminder_content().contains("取件码"));

        verify(dao, times(1)).updateParcel(any(ParcelVO.class));
        verify(dao, times(1)).insertReminderLog(any(ReminderLogVO.class));
    }

    @Test
    public void testInboundSuccess() throws Exception {
        ParcelVO newParcel = new ParcelVO();
        newParcel.setPk_org("test_org_001");
        newParcel.setExpress_no("SF2222222222222");
        newParcel.setReceiver_name("周八");
        newParcel.setReceiver_phone("13800138222");
        newParcel.setIs_vip(0);
        newParcel.setWeight(new UFDouble(3));

        when(dao.findParcelByExpressNo("SF2222222222222", "test_org_001")).thenReturn(null);
        when(dao.insertParcel(any(ParcelVO.class))).thenAnswer(invocation -> {
            ParcelVO vo = invocation.getArgument(0);
            vo.setPk_parcel("pk_generated_001");
            return vo;
        });

        IExpressReminderService reminderService = service;
        ParcelVO result = reminderService.inbound(newParcel);

        assertNotNull("入库应该返回包裹信息", result);
        assertNotNull("应该自动生成取件码", result.getPickup_code());
        assertEquals(6, result.getPickup_code().length());
        assertNotNull("应该设置取件码过期时间", result.getPickup_code_expire());
        assertEquals(ParcelVO.STATUS_PENDING, result.getParcel_status().intValue());
        assertEquals(0, result.getReturn_processing().intValue());
        assertEquals("test_org_001", result.getPk_org());
    }
}
