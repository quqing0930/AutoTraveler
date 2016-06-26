package pers.traveler.robot;

import pers.traveler.log.Log;
import pers.traveler.tools.CmdUtil;

/**
 * Created by quqing on 16/6/14.
 */
public class StartAppiumServer extends Thread {
    private String command;

    /**
     * @param command
     */
    public StartAppiumServer(String command) {
        this.command = command;
    }

    /**
     * 启动Appium服务。
     */
    public void run() {
        //启动appium服务
        Log.logInfo("start appium >> " + command);
        CmdUtil.run(command);

    }
}
