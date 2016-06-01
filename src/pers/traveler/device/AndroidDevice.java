package pers.traveler.device;

import pers.traveler.constant.CmdConfig;
import pers.traveler.tools.CmdUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quqing on 16/4/29.
 * Android设备信息
 */
public class AndroidDevice extends DeviceAdapter {
    public AndroidDevice() {
        super();
    }

    public AndroidDevice(String udid) {
        super(udid);
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    @Override
    public String getAppInfo(String apk, String filter) {
        String cmd = CmdConfig.APP_INFO.replaceAll("#apk#", apk);
        String appPackage = CmdUtil.run(cmd);
        appPackage = stringFilter.grep(appPackage, filter);
        appPackage = appPackage.split("'")[1];
        return appPackage.trim();
    }

    @Override
    public boolean appIsAlive(String process) {
        String cmd = CmdConfig.APP_PROCESS_EXISTS.replaceAll("#udid#", udid).replaceAll("#package#", process);
        String info = CmdUtil.run(cmd);
        info = stringFilter.grep(info, process);
        if (null == info || info.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean killUiautomator() {
        String pid = getUiautomatorProcess();
        CmdUtil.run(CmdConfig.KILL_APP_PROCESS.replaceAll("#udid#", udid).replaceAll("#pid#", pid));
        pid = getUiautomatorProcess();
        if (null == pid || pid.isEmpty())
            return true;
        return false;
    }

    @Override
    public String getAppPackage(String apk) {
        return getAppInfo(apk, "package");
    }

    @Override
    public String getAppActivity(String apk) {
        return getAppInfo(apk, "activity");
    }

    @Override
    public String getCurrentActivity() {
        String cmd = CmdConfig.APP_CURRENT_ACTIVITY.replaceAll("#udid#", udid);
        String currentActivity = CmdUtil.run(cmd);
        currentActivity = stringFilter.grep(currentActivity, "mFocusedActivity");
        currentActivity = currentActivity.substring(currentActivity.indexOf("/") + 1);
        currentActivity = currentActivity.split("}")[0];
        return currentActivity;
    }

    @Override
    public String getProductModel() {

        return CmdUtil.run(CmdConfig.PRODUCT.replaceAll("#udid#", udid));
    }

    @Override
    public String getPlatformVersion() {

        return CmdUtil.run(CmdConfig.RELEASE_VERSION.replaceAll("#udid#", udid));
    }

    @Override
    public String getResolution() {

        return CmdUtil.run(CmdConfig.RESOLUTION.replaceAll("#udid#", udid));
    }

    @Override
    public String getSysVersion() {

        return CmdUtil.run(CmdConfig.SYS_VERSION.replaceAll("#udid#", udid));
    }

    @Override
    public String getVersion() {
        return CmdUtil.run(CmdConfig.VERSION.replaceAll("#udid#", udid));
    }

    @Override
    public String getCoreVersion() {
        String coreVersion = CmdUtil.run(CmdConfig.CORE_VERSION.replaceAll("#udid#", udid));
        return stringFilter.columns(coreVersion, 3);
    }

    @Override
    public String getApiLevel() {
        return CmdUtil.run(CmdConfig.API_LEVEL.replaceAll("#udid#", udid));
    }

    @Override
    public String getLanguage() {
        return CmdUtil.run(CmdConfig.LANGUAGE.replaceAll("#udid#", udid));
    }

    @Override
    public String getUiautomatorProcess() {
        String uiautomatorProcess = CmdUtil.run(CmdConfig.FIND_UIAUTOMATOR_PROCESS.replaceAll("#udid#", udid));
        if (null != uiautomatorProcess) {
            uiautomatorProcess = stringFilter.grep(uiautomatorProcess, "uiautomator");
            uiautomatorProcess = stringFilter.columns(uiautomatorProcess, 2);
        }
        return uiautomatorProcess;
    }

    @Override
    public List<String> getLogCatPID() {
        List<String> pidList = null;
        String logCatPID = CmdUtil.run(CmdConfig.LOG_CAT_PID.replaceAll("#udid#", udid));

        if (null != logCatPID) {
            pidList = new ArrayList<>();
            logCatPID = stringFilter.columns(logCatPID, 2);
            if (logCatPID.indexOf(System.getProperty("line.separator")) != -1) {
                pidList = Arrays.asList(logCatPID.split(System.getProperty("line.separator")));
            } else {
                pidList.add(logCatPID);
            }

        }
        return pidList;
    }
}