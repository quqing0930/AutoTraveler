package pers.traveler.log;

import pers.traveler.constant.CmdConfig;
import pers.traveler.device.AndroidDevice;
import pers.traveler.device.Device;
import pers.traveler.entity.Config;
import pers.traveler.tools.CmdUtil;

import java.util.List;

/**
 * Created by quqing on 16/5/19.
 */
public class LogManager {
    private LogScan logScan;

    public void run(Config config) {
        logScan = new LogScan(config.getLogCmd().replaceAll("#udid#", config.getUdid()));
        logScan.start();
    }

    public void stop(Device device, String udid) {
        String killCmd;
        List<String> pidList = device.getLogCatPID();
        killCmd = device instanceof AndroidDevice ? CmdConfig.KILL_APP_PROCESS : CmdConfig.KILL_SYS_PROCESS;
        if (null != pidList) {
            for (String pid : pidList) {
                killCmd = killCmd.replaceAll("#udid#", udid).replaceAll("#pid#", pid);
                CmdUtil.run(killCmd);
            }
        }
    }
}
