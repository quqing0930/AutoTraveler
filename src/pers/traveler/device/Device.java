package pers.traveler.device;

import java.util.List;

/**
 * Created by quqing on 16/4/29.
 * 设备信息的接口
 */
public interface Device {
    List<String> getUdid(int appPlatform);

    String getAppInfo(String apk, String filter);

    boolean appIsAlive(String process);

    boolean killUiautomator();

    String getAppPackage(String apk);

    String getAppActivity(String apk);

    String getCurrentActivity();

    String getProductModel();

    String getPlatformVersion();

    String getResolution();

    String getSysVersion();

    String getVersion();

    String getCoreVersion();

    String getApiLevel();

    String getLanguage();

    String getUiautomatorProcess();

    List<String> getLogCatPID();

    String getDate();

    String getTime();
}
