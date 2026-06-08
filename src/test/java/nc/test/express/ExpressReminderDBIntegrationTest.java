package nc.test.express;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.sql.*;
import java.util.*;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ExpressReminderDBIntegrationTest {

    private static final String DB_PROPERTIES_FILE = "src/test/resources/db.properties";
    private static Connection conn;
    private static String dbType = "MYSQL";
    private static String pkOrg = "TEST_ORG_001";
    private static String pkGroup = "TEST_GROUP_001";
    private static String userId = "TEST_USER_001";

    @BeforeClass
    public static void setUpClass() throws Exception {
        Properties props = new Properties();
        File propsFile = new File(DB_PROPERTIES_FILE);
        if (propsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propsFile)) {
                props.load(fis);
            }
        } else {
            System.out.println("================================================");
            System.out.println("未找到 db.properties，使用默认连接参数");
            System.out.println("请在 src/test/resources/db.properties 中配置：");
            System.out.println("  jdbc.url=jdbc:oracle:thin:@//host:port/sid");
            System.out.println("  jdbc.username=username");
            System.out.println("  jdbc.password=password");
            System.out.println("  jdbc.driver=oracle.jdbc.OracleDriver");
            System.out.println("或达梦数据库：");
            System.out.println("  jdbc.url=jdbc:dm://host:port");
            System.out.println("  jdbc.driver=dm.jdbc.driver.DmDriver");
            System.out.println("================================================");
            props.setProperty("jdbc.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=Oracle");
            props.setProperty("jdbc.username", "sa");
            props.setProperty("jdbc.password", "");
            props.setProperty("jdbc.driver", "org.h2.Driver");
        }

        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");
        String driver = props.getProperty("jdbc.driver");

        System.out.println("连接数据库: " + url);
        System.out.println("驱动类: " + driver);
        System.out.println("用户名: " + username);

        Class.forName(driver);
        conn = DriverManager.getConnection(url, username, password);
        conn.setAutoCommit(false);

        String dbProduct = conn.getMetaData().getDatabaseProductName().toUpperCase();
        if (dbProduct.contains("ORACLE")) {
            dbType = "ORACLE";
        } else if (dbProduct.contains("DM")) {
            dbType = "DM";
        } else if (dbProduct.contains("MYSQL")) {
            dbType = "MYSQL";
        } else if (dbProduct.contains("H2")) {
            dbType = "H2";
        }
        System.out.println("数据库类型: " + dbType + " (" + dbProduct + ")");
        System.out.println("数据库连接成功！");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (conn != null && !conn.isClosed()) {
            conn.rollback();
            conn.close();
            System.out.println("数据库连接已关闭");
        }
    }

    @Before
    public void setUp() throws Exception {
        conn.setAutoCommit(false);
    }

    @After
    public void tearDown() throws Exception {
        conn.rollback();
    }

    private String sysdate() {
        if ("MYSQL".equals(dbType) || "H2".equals(dbType)) {
            return "NOW()";
        }
        return "SYSDATE";
    }

    private String toCharTs(String column) {
        if ("MYSQL".equals(dbType) || "H2".equals(dbType)) {
            return "DATE_FORMAT(" + column + ", '%Y-%m-%d %H:%i:%s')";
        }
        return "TO_CHAR(" + column + ", 'YYYY-MM-DD HH24:MI:SS')";
    }

    private String dateAddDays(String column, int days) {
        if ("MYSQL".equals(dbType) || "H2".equals(dbType)) {
            return "DATE_ADD(" + column + ", INTERVAL " + days + " DAY)";
        }
        return column + " + " + days;
    }

    private String dateAddDaysExpr(String column, String daysExpr) {
        if ("MYSQL".equals(dbType) || "H2".equals(dbType)) {
            return "DATE_ADD(" + column + ", INTERVAL " + daysExpr + " DAY)";
        }
        return column + " + " + daysExpr;
    }

    private String rownumLimit(int limit) {
        if ("MYSQL".equals(dbType) || "H2".equals(dbType)) {
            return "LIMIT " + limit;
        }
        return "AND ROWNUM <= " + limit;
    }

    @Test
    public void test_01_CreateTables() throws Exception {
        System.out.println("\n========== 测试1: 创建数据表 ==========");

        String ddlPath = "src/main/resources/sql/ddl/";
        if ("MYSQL".equals(dbType)) {
            ddlPath += "mysql/";
        }

        String parcelFile = "MYSQL".equals(dbType) 
            ? ddlPath + "express_parcel_mysql.sql" 
            : ddlPath + "express_parcel.sql";
        String ruleFile = "MYSQL".equals(dbType)
            ? ddlPath + "express_reminder_rule_mysql.sql"
            : ddlPath + "express_reminder_rule.sql";
        String logFile = "MYSQL".equals(dbType)
            ? ddlPath + "express_reminder_log_mysql.sql"
            : ddlPath + "express_reminder_log.sql";

        executeSQLFromFile(parcelFile);
        System.out.println("✓ express_parcel 表创建成功");

        executeSQLFromFile(ruleFile);
        System.out.println("✓ express_reminder_rule 表创建成功");

        executeSQLFromFile(logFile);
        System.out.println("✓ express_reminder_log 表创建成功");

        assertTrue("express_parcel 表应该存在", tableExists("EXPRESS_PARCEL"));
        assertTrue("express_reminder_rule 表应该存在", tableExists("EXPRESS_REMINDER_RULE"));
        assertTrue("express_reminder_log 表应该存在", tableExists("EXPRESS_REMINDER_LOG"));

        System.out.println("✓ 所有数据表创建验证通过");
    }

    @Test
    public void test_02_InitRules() throws Exception {
        System.out.println("\n========== 测试2: 初始化催领规则 ==========");

        String initFile;
        if ("MYSQL".equals(dbType)) {
            initFile = "src/main/resources/sql/dml/mysql/express_init_rules_mysql.sql";
        } else {
            initFile = "src/main/resources/sql/dml/express_init_rules.sql";
        }

        executeSQLFromFile(initFile);
        conn.commit();

        String countSql = "SELECT COUNT(1) FROM express_reminder_rule WHERE pk_org = ? AND dr = 0";
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("✓ 已初始化 " + count + " 条催领规则");
                    assertEquals("应该初始化6条规则", 6, count);
                }
            }
        }

        String checkSql = "SELECT rule_name, priority, reminder_type FROM express_reminder_rule "
                        + "WHERE pk_org = ? AND dr = 0 ORDER BY priority DESC";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println("  - " + rs.getString("rule_name") 
                            + " (优先级:" + rs.getInt("priority") 
                            + ", 类型:" + rs.getInt("reminder_type") + ")");
                }
            }
        }

        System.out.println("✓ 催领规则初始化验证通过");
    }

    @Test
    public void test_03_ParcelInbound() throws Exception {
        System.out.println("\n========== 测试3: 包裹入库 ==========");

        String[] pks = insertTestParcels();

        String checkSql = "SELECT pk_parcel, express_no, receiver_name, pickup_code, "
                        + "pickup_code_expire, parcel_status, inbound_time "
                        + "FROM express_parcel WHERE pk_org = ? AND dr = 0 ORDER BY inbound_time DESC";

        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    String pk = rs.getString("pk_parcel");
                    String expressNo = rs.getString("express_no");
                    String receiver = rs.getString("receiver_name");
                    String pickupCode = rs.getString("pickup_code");
                    Timestamp expire = rs.getTimestamp("pickup_code_expire");
                    int status = rs.getInt("parcel_status");

                    System.out.println("  包裹[" + count + "]: " + expressNo 
                            + " | 收件人:" + receiver
                            + " | 取件码:" + pickupCode
                            + " | 状态:" + status);

                    assertNotNull("取件码不应为空", pickupCode);
                    assertEquals("取件码应为6位", 6, pickupCode.length());
                    assertNotNull("取件码过期时间不应为空", expire);
                    assertEquals("状态应为待取件(0)", 0, status);
                }
                assertEquals("应该入库4个测试包裹", 4, count);
            }
        }

        System.out.println("✓ 包裹入库验证通过");
    }

    @Test
    public void test_04_GenerateOverdueReminders() throws Exception {
        System.out.println("\n========== 测试4: 超期生成催领记录 ==========");

        String findOverdueSql = 
            "SELECT p.pk_parcel, p.express_no, p.receiver_name, p.is_vip, p.is_large, "
          + "p.is_remote, p.weight, p.area_code, p.inbound_time, r.pk_rule, r.rule_name, "
          + "r.reminder_type, r.retention_days "
          + "FROM express_parcel p, express_reminder_rule r "
          + "WHERE p.pk_org = ? AND p.dr = 0 AND r.dr = 0 AND r.pk_org = p.pk_org "
          + "AND p.parcel_status = 0 AND p.return_processing = 0 "
          + "AND EXISTS ("
          + "    SELECT 1 FROM express_reminder_rule r2 "
          + "    WHERE r2.pk_rule = r.pk_rule "
          + "    AND (r2.cond_vip IS NULL OR r2.cond_vip = p.is_vip) "
          + "    AND (r2.cond_large IS NULL OR r2.cond_large = p.is_large) "
          + "    AND (r2.cond_remote IS NULL OR r2.cond_remote = p.is_remote) "
          + "    AND (r2.area_code IS NULL OR r2.area_code = p.area_code) "
          + "    AND (r2.min_weight IS NULL OR p.weight >= r2.min_weight) "
          + "    AND (r2.max_weight IS NULL OR p.weight <= r2.max_weight) "
          + "    AND " + dateAddDaysExpr("p.inbound_time", "r2.retention_days") + " < " + sysdate() + " "
          + "    AND r2.enabled = 1 "
          + ") "
          + "AND NOT EXISTS ("
          + "    SELECT 1 FROM express_reminder_log l "
          + "    WHERE l.pk_parcel = p.pk_parcel AND l.dr = 0 "
          + "    AND l.pk_rule = r.pk_rule "
          + ") "
          + "ORDER BY r.priority DESC";

        List<Map<String, Object>> overdueParcels = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(findOverdueSql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("pk_parcel", rs.getString("pk_parcel"));
                    row.put("express_no", rs.getString("express_no"));
                    row.put("receiver_name", rs.getString("receiver_name"));
                    row.put("pk_rule", rs.getString("pk_rule"));
                    row.put("rule_name", rs.getString("rule_name"));
                    row.put("reminder_type", rs.getInt("reminder_type"));
                    row.put("retention_days", rs.getInt("retention_days"));
                    row.put("area_code", rs.getString("area_code"));
                    overdueParcels.add(row);
                }
            }
        }

        System.out.println("找到 " + overdueParcels.size() + " 个超期待催领包裹");
        for (Map<String, Object> p : overdueParcels) {
            System.out.println("  - " + p.get("express_no") + " (" + p.get("receiver_name") 
                    + ") → " + p.get("rule_name"));
        }

        Set<String> processedParcels = new HashSet<>();
        List<String> generatedLogPKs = new ArrayList<>();

        for (Map<String, Object> parcel : overdueParcels) {
            String pkParcel = (String) parcel.get("pk_parcel");
            if (processedParcels.contains(pkParcel)) {
                continue;
            }
            processedParcels.add(pkParcel);

            String countSql = "SELECT COUNT(1) FROM express_reminder_log "
                            + "WHERE pk_parcel = ? AND dr = 0 AND reminder_status IN (0,1)";
            int currentCount = 0;
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                ps.setString(1, pkParcel);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        currentCount = rs.getInt(1);
                    }
                }
            }

            String pkLog = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            String insertSql = 
                "INSERT INTO express_reminder_log ("
              + "pk_log, pk_group, pk_org, creator, creationtime, modifier, modifiedtime, dr, ts, "
              + "pk_parcel, pk_rule, reminder_type, reminder_time, reminder_status, "
              + "reminder_content, pickup_code, reminder_count, operator, area_code"
              + ") VALUES (?, ?, ?, ?, " + sysdate() + ", ?, " + sysdate() + ", 0, " + toCharTs(sysdate()) + ", "
              + "?, ?, ?, " + sysdate() + ", 1, ?, ?, ?, ?, ?)";

            String content = "【快递催领】尊敬的" + parcel.get("receiver_name") + "，您的快递(" 
                           + parcel.get("express_no") + ")已到达驿站，请尽快凭取件码领取。";

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, pkLog);
                ps.setString(2, pkGroup);
                ps.setString(3, pkOrg);
                ps.setString(4, userId);
                ps.setString(5, userId);
                ps.setString(6, pkParcel);
                ps.setString(7, (String) parcel.get("pk_rule"));
                ps.setInt(8, (Integer) parcel.get("reminder_type"));
                ps.setString(9, content);
                ps.setString(10, "123456");
                ps.setInt(11, currentCount + 1);
                ps.setString(12, userId);
                ps.setString(13, (String) parcel.get("area_code"));
                ps.executeUpdate();
            }

            generatedLogPKs.add(pkLog);
            System.out.println("  ✓ 生成催领记录: " + pkLog + " → 类型=" + parcel.get("reminder_type"));
        }

        conn.commit();

        assertEquals("应该生成2条催领记录（VIP和普通件，退回中不生成）", 
                2, generatedLogPKs.size());

        String verifySql = "SELECT l.pk_log, p.express_no, p.receiver_name, "
                         + "l.reminder_type, l.reminder_count, l.area_code "
                         + "FROM express_reminder_log l "
                         + "INNER JOIN express_parcel p ON l.pk_parcel = p.pk_parcel "
                         + "WHERE l.pk_org = ? AND l.dr = 0";

        try (PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println("  催领记录[" + count + "]: " 
                            + rs.getString("express_no") + " → " 
                            + "类型:" + rs.getInt("reminder_type") 
                            + ", 次数:" + rs.getInt("reminder_count")
                            + ", 片区:" + rs.getString("area_code"));
                    
                    assertNotNull("片区编码不应为空", rs.getString("area_code"));
                }
            }
        }

        System.out.println("✓ 超期催领生成验证通过");
    }

    @Test
    public void test_05_ReturnProcessingBlock() throws Exception {
        System.out.println("\n========== 测试5: 退回处理中不再催领 ==========");

        String findReturningSql = 
            "SELECT pk_parcel, express_no, receiver_name FROM express_parcel "
          + "WHERE pk_org = ? AND dr = 0 AND return_processing = 1";

        String returningPk = null;
        String returningExpressNo = null;
        try (PreparedStatement ps = conn.prepareStatement(findReturningSql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    returningPk = rs.getString("pk_parcel");
                    returningExpressNo = rs.getString("express_no");
                    System.out.println("找到退回处理中的包裹: " + returningExpressNo);
                }
            }
        }

        assertNotNull("应该存在退回处理中的测试包裹", returningPk);

        String checkReminderSql = 
            "SELECT COUNT(1) FROM express_reminder_log "
          + "WHERE pk_parcel = ? AND dr = 0";

        try (PreparedStatement ps = conn.prepareStatement(checkReminderSql)) {
            ps.setString(1, returningPk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertEquals("退回处理中的包裹不应有催领记录", 0, count);
                    System.out.println("✓ 退回处理中的包裹确实没有催领记录");
                }
            }
        }

        String attemptGenerateSql = 
            "SELECT COUNT(1) FROM express_parcel p "
          + "WHERE p.pk_parcel = ? AND p.dr = 0 "
          + "AND p.parcel_status = 0 AND p.return_processing = 0 "
          + "AND EXISTS ("
          + "    SELECT 1 FROM express_reminder_rule r "
          + "    WHERE r.pk_org = p.pk_org AND r.dr = 0 AND r.enabled = 1 "
          + "    AND (r.cond_vip IS NULL OR r.cond_vip = p.is_vip) "
          + "    AND (r.cond_large IS NULL OR r.cond_large = p.is_large) "
          + "    AND (r.cond_remote IS NULL OR r.cond_remote = p.is_remote) "
          + "    AND " + dateAddDaysExpr("p.inbound_time", "r.retention_days") + " < " + sysdate() + " "
          + ")";

        try (PreparedStatement ps = conn.prepareStatement(attemptGenerateSql)) {
            ps.setString(1, returningPk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    assertEquals("退回处理中的包裹应该被过滤掉", 0, count);
                    System.out.println("✓ 退回处理中的包裹已被正确过滤，不会生成催领");
                }
            }
        }

        System.out.println("✓ 退回处理阻塞催领验证通过");
    }

    @Test
    public void test_06_ResendPickupCode() throws Exception {
        System.out.println("\n========== 测试6: 取件码重发 ==========");

        String findExpiredSql = 
            "SELECT pk_parcel, express_no, receiver_name, pickup_code, receiver_phone "
          + "FROM express_parcel "
          + "WHERE pk_org = ? AND dr = 0 "
          + "AND parcel_status = 0 AND return_processing = 0 "
          + "AND pickup_code_expire < " + sysdate() + " "
          + rownumLimit(1);

        String expiredPk = null;
        String oldPickupCode = null;
        String receiverPhone = null;
        try (PreparedStatement ps = conn.prepareStatement(findExpiredSql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    expiredPk = rs.getString("pk_parcel");
                    oldPickupCode = rs.getString("pickup_code");
                    receiverPhone = rs.getString("receiver_phone");
                    System.out.println("找到取件码过期的包裹: " + rs.getString("express_no") 
                            + ", 原取件码: " + oldPickupCode);
                }
            }
        }

        assertNotNull("应该存在取件码过期的测试包裹", expiredPk);

        String newPickupCode = String.format("%06d", new Random().nextInt(999999));
        String updateParcelSql = 
            "UPDATE express_parcel SET pickup_code = ?, pickup_code_expire = " + dateAddDays(sysdate(), 1) + ", "
          + "modifier = ?, modifiedtime = " + sysdate() + " "
          + "WHERE pk_parcel = ?";

        try (PreparedStatement ps = conn.prepareStatement(updateParcelSql)) {
            ps.setString(1, newPickupCode);
            ps.setString(2, userId);
            ps.setString(3, expiredPk);
            int rows = ps.executeUpdate();
            assertEquals("应该更新1条包裹记录", 1, rows);
            System.out.println("✓ 更新包裹取件码: " + oldPickupCode + " → " + newPickupCode);
        }

        String countSql = "SELECT COUNT(1) FROM express_reminder_log WHERE pk_parcel = ? AND dr = 0";
        int currentCount = 0;
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setString(1, expiredPk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentCount = rs.getInt(1);
                }
            }
        }

        String pkLog = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String areaCode = "A01";
        String content = "【取件码重发】您的取件码已更新为：" + newPickupCode 
                       + "，请凭此码取件，24小时内有效。";

        String insertLogSql = 
            "INSERT INTO express_reminder_log ("
          + "pk_log, pk_group, pk_org, creator, creationtime, modifier, modifiedtime, dr, ts, "
          + "pk_parcel, reminder_type, reminder_time, reminder_status, "
          + "reminder_content, pickup_code, reminder_count, operator, area_code, remark"
          + ") VALUES (?, ?, ?, ?, " + sysdate() + ", ?, " + sysdate() + ", 0, " + toCharTs(sysdate()) + ", "
          + "?, 1, " + sysdate() + ", 1, ?, ?, ?, ?, '取件码过期重发')";

        try (PreparedStatement ps = conn.prepareStatement(insertLogSql)) {
            ps.setString(1, pkLog);
            ps.setString(2, pkGroup);
            ps.setString(3, pkOrg);
            ps.setString(4, userId);
            ps.setString(5, userId);
            ps.setString(6, expiredPk);
            ps.setString(7, content);
            ps.setString(8, newPickupCode);
            ps.setInt(9, currentCount + 1);
            ps.setString(10, userId);
            ps.setString(11, areaCode);
            ps.executeUpdate();
            System.out.println("✓ 生成取件码重发记录，发送到: " + receiverPhone);
        }

        conn.commit();

        String verifySql = 
            "SELECT p.pickup_code, p.pickup_code_expire, l.reminder_content, l.pickup_code as log_code, l.area_code "
          + "FROM express_parcel p, express_reminder_log l "
          + "WHERE p.pk_parcel = ? AND l.pk_parcel = p.pk_parcel AND l.remark = '取件码过期重发' "
          + "ORDER BY l.creationtime DESC";

        try (PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setString(1, expiredPk);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String updatedCode = rs.getString("pickup_code");
                    Timestamp updatedExpire = rs.getTimestamp("pickup_code_expire");
                    String logCode = rs.getString("log_code");
                    String logContent = rs.getString("reminder_content");
                    String logArea = rs.getString("area_code");

                    assertEquals("包裹取件码应已更新", newPickupCode, updatedCode);
                    assertEquals("催领记录取件码应一致", newPickupCode, logCode);
                    assertNotNull("取件码过期时间应已更新", updatedExpire);
                    assertTrue("催领内容应包含新取件码", logContent.contains(newPickupCode));
                    assertNotNull("片区编码不应为空", logArea);

                    System.out.println("✓ 验证: 取件码已更新为 " + updatedCode);
                    System.out.println("✓ 验证: 催领内容 - " + logContent);
                } else {
                    fail("未找到取件码重发记录");
                }
            }
        }

        System.out.println("✓ 取件码重发验证通过");
    }

    @Test
    public void test_07_QueryReminderLogsWithPagination() throws Exception {
        System.out.println("\n========== 测试7: 片区主管查询催领记录（分页） ==========");

        String countSql = "SELECT COUNT(1) FROM express_reminder_log WHERE pk_org = ? AND dr = 0";
        int totalCount = 0;
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }
        }
        System.out.println("总催领记录数: " + totalCount);
        assertTrue("应该有催领记录", totalCount > 0);

        int pageSize = 2;
        int pageNum = 1;
        int offset = (pageNum - 1) * pageSize;

        String querySql = generatePaginationSql(
            "SELECT l.pk_log, l.reminder_time, l.reminder_type, l.reminder_status, "
          + "l.reminder_count, l.area_code, l.pickup_code, "
          + "p.express_no, p.receiver_name, p.receiver_phone "
          + "FROM express_reminder_log l "
          + "INNER JOIN express_parcel p ON l.pk_parcel = p.pk_parcel "
          + "WHERE l.pk_org = ? AND l.dr = 0 "
          + "ORDER BY l.reminder_time DESC, l.pk_log DESC",
            offset, pageSize
        );

        System.out.println("分页查询 SQL（第一页，每页" + pageSize + "条）:");
        System.out.println("  " + querySql.replace("\n", "\n  "));

        try (PreparedStatement ps = conn.prepareStatement(querySql)) {
            ps.setString(1, pkOrg);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                System.out.println("\n查询结果:");
                while (rs.next()) {
                    count++;
                    System.out.println("  记录[" + count + "]: "
                            + rs.getString("express_no")
                            + " | 收件人:" + rs.getString("receiver_name")
                            + " | 类型:" + rs.getInt("reminder_type")
                            + " | 状态:" + rs.getInt("reminder_status")
                            + " | 片区:" + rs.getString("area_code"));
                }
                assertTrue("第一页应该有记录", count > 0);
                assertTrue("每页记录数不应超过" + pageSize, count <= pageSize);
            }
        }

        String areaCode = "A01";
        String areaQuerySql = generatePaginationSql(
            "SELECT l.pk_log, l.reminder_time, l.reminder_type, "
          + "p.express_no, p.receiver_name "
          + "FROM express_reminder_log l "
          + "INNER JOIN express_parcel p ON l.pk_parcel = p.pk_parcel "
          + "WHERE l.pk_org = ? AND l.area_code = ? AND l.dr = 0 "
          + "ORDER BY l.reminder_time DESC",
            0, 10
        );

        try (PreparedStatement ps = conn.prepareStatement(areaQuerySql)) {
            ps.setString(1, pkOrg);
            ps.setString(2, areaCode);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                System.out.println("\n片区[" + areaCode + "]催领记录:");
                while (rs.next()) {
                    count++;
                    System.out.println("  - " + rs.getString("express_no") 
                            + " (" + rs.getString("receiver_name") + ")");
                }
                System.out.println("  共 " + count + " 条记录");
            }
        }

        System.out.println("✓ 片区主管分页查询验证通过");
    }

    @Test
    public void test_08_VerifyDataIntegrity() throws Exception {
        System.out.println("\n========== 测试8: 数据完整性验证 ==========");

        String[] tables = {"EXPRESS_PARCEL", "EXPRESS_REMINDER_RULE", "EXPRESS_REMINDER_LOG"};
        for (String table : tables) {
            String sql = "SELECT COUNT(1) FROM " + table + " WHERE dr = 0";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("  " + table + ": " + count + " 条有效记录");
                    assertTrue(table + " 应该有数据", count > 0);
                }
            }
        }

        String integritySql = 
            "SELECT COUNT(1) FROM express_reminder_log l "
          + "WHERE l.dr = 0 "
          + "AND NOT EXISTS (SELECT 1 FROM express_parcel p WHERE p.pk_parcel = l.pk_parcel)";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(integritySql)) {
            if (rs.next()) {
                int orphanCount = rs.getInt(1);
                assertEquals("不应存在孤立的催领记录", 0, orphanCount);
                System.out.println("  ✓ 催领记录关联完整性正常");
            }
        }

        String areaCodeSql = 
            "SELECT COUNT(1) FROM express_reminder_log l "
          + "WHERE l.dr = 0 AND (l.area_code IS NULL OR l.area_code = '')";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(areaCodeSql)) {
            if (rs.next()) {
                int nullAreaCount = rs.getInt(1);
                assertEquals("催领记录片区编码不应为空", 0, nullAreaCount);
                System.out.println("  ✓ 催领记录片区编码完整性正常");
            }
        }

        System.out.println("✓ 数据完整性验证通过");
    }

    private String[] insertTestParcels() throws Exception {
        String[] pks = new String[4];

        UFDateTime now = new UFDateTime();

        pks[0] = insertParcel(
            "SF_TEST_001", "VIP客户", "13800000001",
            1, 0, 0, 2.5, "A01",
            now.addDays(-5), false, false
        );
        System.out.println("  ✓ 入库VIP超期包裹");

        pks[1] = insertParcel(
            "SF_TEST_002", "普通用户", "13800000002",
            0, 0, 0, 3.0, "A02",
            now.addDays(-5), false, false
        );
        System.out.println("  ✓ 入库普通超期包裹");

        pks[2] = insertParcel(
            "SF_TEST_003", "退回用户", "13800000003",
            0, 0, 0, 1.5, "A01",
            now.addDays(-10), false, true
        );
        System.out.println("  ✓ 入库退回处理中包裹（超期但不应催领）");

        pks[3] = insertParcel(
            "SF_TEST_004", "取件码过期用户", "13800000004",
            0, 0, 0, 1.0, "A01",
            now.addDays(-1), true, false
        );
        System.out.println("  ✓ 入库取件码过期包裹");

        conn.commit();
        return pks;
    }

    private String insertParcel(String expressNo, String receiverName, String receiverPhone,
                                int isVip, int isLarge, int isRemote, double weight, String areaCode,
                                UFDateTime inboundTime, boolean expirePickupCode, boolean returnProcessing) 
            throws Exception {

        String pkParcel = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        String pickupCode = String.format("%06d", new Random().nextInt(999999));

        UFDateTime expireTime;
        if (expirePickupCode) {
            expireTime = inboundTime.addHours(-1);
        } else {
            expireTime = inboundTime.addHours(24);
        }

        int status = returnProcessing ? 2 : 0;

        String sql = 
            "INSERT INTO express_parcel ("
          + "pk_parcel, pk_group, pk_org, creator, creationtime, modifier, modifiedtime, dr, ts, "
          + "express_no, receiver_name, receiver_phone, pickup_code, pickup_code_expire, "
          + "inbound_time, parcel_status, is_vip, is_large, is_remote, weight, area_code, "
          + "return_processing"
          + ") VALUES (?, ?, ?, ?, " + sysdate() + ", ?, " + sysdate() + ", 0, " + toCharTs(sysdate()) + ", "
          + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pkParcel);
            ps.setString(2, pkGroup);
            ps.setString(3, pkOrg);
            ps.setString(4, userId);
            ps.setString(5, userId);
            ps.setString(6, expressNo);
            ps.setString(7, receiverName);
            ps.setString(8, receiverPhone);
            ps.setString(9, pickupCode);
            ps.setTimestamp(10, new Timestamp(expireTime.toMillis()));
            ps.setTimestamp(11, new Timestamp(inboundTime.toMillis()));
            ps.setInt(12, status);
            ps.setInt(13, isVip);
            ps.setInt(14, isLarge);
            ps.setInt(15, isRemote);
            ps.setDouble(16, weight);
            ps.setString(17, areaCode);
            ps.setInt(18, returnProcessing ? 1 : 0);
            ps.executeUpdate();
        }

        return pkParcel;
    }

    private void executeSQLFromFile(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("SQL文件不存在: " + filePath);
        }

        System.out.println("  执行SQL文件: " + file.getName());

        StringBuilder sqlBuilder = new StringBuilder();
        boolean inDelimiter = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                if (line.toUpperCase().startsWith("DELIMITER")) {
                    inDelimiter = !inDelimiter;
                    continue;
                }
                sqlBuilder.append(line).append(" ");
                if (line.endsWith(";") && !inDelimiter) {
                    String sql = sqlBuilder.toString().replace(";", "").trim();
                    if (!sql.isEmpty()) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            System.out.println("  警告: SQL执行失败（可能表已存在）: " + e.getMessage());
                        }
                    }
                    sqlBuilder = new StringBuilder();
                }
            }
        }
    }

    private boolean tableExists(String tableName) throws Exception {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private String generatePaginationSql(String baseSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder();
        
        String dbProduct = conn.getMetaData().getDatabaseProductName().toUpperCase();
        
        if (dbProduct.contains("ORACLE") || dbProduct.contains("DM")) {
            sql.append("SELECT * FROM (");
            sql.append("  SELECT t.*, ROWNUM rn FROM (");
            sql.append(baseSql);
            sql.append("  ) t WHERE ROWNUM <= ").append(offset + limit);
            sql.append(") WHERE rn > ").append(offset);
        } else if (dbProduct.contains("H2") || dbProduct.contains("MYSQL")) {
            sql.append(baseSql);
            sql.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
        } else {
            sql.append(baseSql);
            sql.append(" LIMIT ").append(limit).append(" OFFSET ").append(offset);
        }
        
        return sql.toString();
    }

    private static class UFDateTime {
        private long time;
        public UFDateTime() { this.time = System.currentTimeMillis(); }
        public UFDateTime(long time) { this.time = time; }
        public UFDateTime addDays(int days) {
            return new UFDateTime(this.time + (long)days * 24L * 60L * 60L * 1000L);
        }
        public UFDateTime addHours(int hours) {
            return new UFDateTime(this.time + (long)hours * 60L * 60L * 1000L);
        }
        public long toMillis() { return time; }
    }
}
