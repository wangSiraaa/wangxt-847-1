package nc.vo.express;

import java.io.Serializable;
import java.util.List;

public class ReminderResultVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private ReminderLogVO reminderLog;
    private List<ParcelVersionCompareVO> versionCompareList;
    private String versionCompareSummary;
    private boolean success;
    private String message;

    public ReminderResultVO() {
    }

    public ReminderResultVO(ReminderLogVO reminderLog, List<ParcelVersionCompareVO> versionCompareList) {
        this.reminderLog = reminderLog;
        this.versionCompareList = versionCompareList;
        this.success = true;
    }

    public ReminderResultVO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ReminderLogVO getReminderLog() {
        return reminderLog;
    }

    public void setReminderLog(ReminderLogVO reminderLog) {
        this.reminderLog = reminderLog;
    }

    public List<ParcelVersionCompareVO> getVersionCompareList() {
        return versionCompareList;
    }

    public void setVersionCompareList(List<ParcelVersionCompareVO> versionCompareList) {
        this.versionCompareList = versionCompareList;
    }

    public String getVersionCompareSummary() {
        return versionCompareSummary;
    }

    public void setVersionCompareSummary(String versionCompareSummary) {
        this.versionCompareSummary = versionCompareSummary;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
