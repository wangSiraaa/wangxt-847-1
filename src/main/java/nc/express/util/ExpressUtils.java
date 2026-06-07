package nc.express.util;

import java.security.SecureRandom;
import java.util.Random;

public class ExpressUtils {

    private static final Random RANDOM = new SecureRandom();
    private static final String PICKUP_CODE_CHARS = "0123456789";
    private static final int PICKUP_CODE_LENGTH = 6;

    public static String generatePickupCode() {
        StringBuilder sb = new StringBuilder(PICKUP_CODE_LENGTH);
        for (int i = 0; i < PICKUP_CODE_LENGTH; i++) {
            sb.append(PICKUP_CODE_CHARS.charAt(RANDOM.nextInt(PICKUP_CODE_CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateReminderContent(String receiverName, String pickupCode,
                                                 String expressNo, int daysOverdue) {
        StringBuilder sb = new StringBuilder();
        sb.append("【快递驿站】尊敬的").append(receiverName).append("，");
        sb.append("您的快递（单号：").append(expressNo).append("）");
        if (daysOverdue > 0) {
            sb.append("已滞留").append(daysOverdue).append("天，");
        }
        sb.append("请尽快凭取件码 ").append(pickupCode).append(" 到驿站领取。");
        sb.append("如有疑问请联系驿站。");
        return sb.toString();
    }

    public static String generatePickupCodeResendContent(String receiverName, String newPickupCode,
                                                        String expressNo) {
        StringBuilder sb = new StringBuilder();
        sb.append("【快递驿站】尊敬的").append(receiverName).append("，");
        sb.append("您的快递（单号：").append(expressNo).append("）取件码已更新。");
        sb.append("新取件码：").append(newPickupCode).append("，");
        sb.append("请凭新取件码到驿站领取。");
        return sb.toString();
    }

    public static int calculateDaysOverdue(long inboundTimeMillis, int retentionDays) {
        long now = System.currentTimeMillis();
        long diff = now - inboundTimeMillis;
        long days = diff / (24 * 60 * 60 * 1000);
        int daysOverdue = (int) days - retentionDays;
        return Math.max(0, daysOverdue);
    }

    public static boolean isPickupCodeExpired(long expireTimeMillis) {
        return System.currentTimeMillis() > expireTimeMillis;
    }
}
