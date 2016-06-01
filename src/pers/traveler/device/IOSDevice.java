package pers.traveler.device;

import pers.traveler.constant.CmdConfig;
import pers.traveler.tools.CmdUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quqing on 16/4/30.
 * IOS设备信息
 */
public class IOSDevice extends DeviceAdapter {

    public IOSDevice() {
        super();
    }

    public IOSDevice(String udid) {
        super(udid);
    }

    @Override
    public List<String> getLogCatPID() {
        List<String> pidList = null;
        String sysLogPID = CmdUtil.run(CmdConfig.LOG_SYS_PID);

        if (null != sysLogPID) {
            pidList = new ArrayList<>();
            sysLogPID = stringFilter.columns(sysLogPID, 1);
            if (sysLogPID.indexOf(System.getProperty("line.separator")) != -1) {
                pidList = Arrays.asList(sysLogPID.split(System.getProperty("line.separator")));
            } else {
                pidList.add(sysLogPID);
            }

        }
        return pidList;
    }
}