package pers.traveler.device;

import pers.quq.filedb.core.StringFilterImpl;
import pers.traveler.constant.CmdConfig;
import pers.traveler.constant.PlatformName;
import pers.traveler.tools.CmdUtil;
import pers.traveler.log.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quqing on 16/5/18.
 * 设备信息适配器
 */
public class DeviceAdapter implements Device {
    protected String udid;
    protected StringFilterImpl stringFilter = new StringFilterImpl();

    public DeviceAdapter() {
    }

    public DeviceAdapter(String udid) {
        this.udid = udid;
    }

    @Override
    public List<String> getUdid(int appPlatform) {
        String cmd;
        List<String> udidList = new ArrayList<>();
        BufferedReader br = null;

        try {
            cmd = PlatformName.ANDROID == appPlatform ? CmdConfig.UUID_ANDROID : CmdConfig.UUID_IOS;
            Process p = CmdUtil.init(cmd);
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty())
                    udidList.add(line.trim());
            }

            if (PlatformName.IOS == appPlatform && udidList.size() > 0) {
                for (int i = 0; i < udidList.size(); i++) {
                    if (udidList.get(i).contains("-"))
                        udidList.remove(udidList.get(i));
                }
            }
        } catch (Exception e) {
            udidList.clear();
            udidList = null;
            Log.logError(e.fillInStackTrace());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    Log.logError(e.fillInStackTrace());
                }
            }
        }
        return udidList;
    }

    @Override
    public String getAppInfo(String apk, String filter) {
        return null;
    }

    @Override
    public boolean appIsAlive(String process) {
        return true;
    }

    @Override
    public boolean killUiautomator() {
        return true;
    }

    @Override
    public String getAppPackage(String apk) {
        return null;
    }

    @Override
    public String getAppActivity(String apk) {
        return null;
    }

    @Override
    public String getCurrentActivity() {
        return null;
    }

    @Override
    public String getProductModel() {

        return null;
    }

    @Override
    public String getPlatformVersion() {

        return null;
    }

    @Override
    public String getResolution() {

        return null;
    }

    @Override
    public String getSysVersion() {

        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getCoreVersion() {
        return null;
    }

    @Override
    public String getApiLevel() {
        return null;
    }

    @Override
    public String getLanguage() {
        return null;
    }

    @Override
    public String getUiautomatorProcess() {
        return null;
    }

    @Override
    public List<String> getLogCatPID() {
        return null;
    }

    @Override
    public String getDate() {
        String cmd;
        if (CmdUtil.isWindows()) {
            cmd = CmdConfig.WIN_DATE;
        } else {
            cmd = CmdConfig.LINUX_DATE;
        }
        return CmdUtil.run(cmd);
    }

    @Override
    public String getTime() {
        String cmd;
        if (CmdUtil.isWindows()) {
            cmd = CmdConfig.WIN_TIME;
        } else {
            cmd = CmdConfig.LINUX_TIME;
        }
        return CmdUtil.run(cmd);
    }
}
