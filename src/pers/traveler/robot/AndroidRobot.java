package pers.traveler.robot;

import io.appium.java_client.android.AndroidDriver;
import pers.quq.filedb.core.FileFilterImpl;
import pers.quq.filedb.core.Filter;
import pers.traveler.constant.PlatformName;
import pers.traveler.constant.Type;
import pers.traveler.device.DeviceFactory;
import pers.traveler.engine.EngineFactory;
import pers.traveler.entity.UiNode;
import pers.traveler.log.Log;
import pers.traveler.tools.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

/**
 * Created by quqing on 16/5/11.
 */
public class AndroidRobot extends Robot {
    public AndroidRobot(String configFile) {
        super(configFile);
    }

    @Override
    protected String getRemoveApp() {
        return config.getAppPackage();
    }

    @Override
    protected void catchAppException() {
        String path;
        String exceptionInfo;
        Filter filter = new FileFilterImpl();

        try {
            path = "output" + File.separator + date + File.separator + time + File.separator + deviceID + File.separator + "logs" + File.separator;
            exceptionInfo = filter.grep(path + "app.log", pers.traveler.constant.Filter.CRASH);
            exceptionInfo = exceptionInfo + System.getProperty("line.separator") + filter.grep(path + "app.log", pers.traveler.constant.Filter.SYSTEM_ERR);
            FileUtil.writeAll(path + "app_err.log", exceptionInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void working() {
        String homePageSource;
        List<String> guideFlow;
        Stack<UiNode> taskStack;

        try {
            device = DeviceFactory.createDevice(PlatformName.Android, config.getUdid());
            homePageSource = beforeTravel();
            guideFlow = config.getGuideFlow();

            String pack = device.getAppPackage(config.getApp());
            Log.logInfo("########################### app environment ###########################");
            Log.logInfo("product = " + device.getProductModel());
            Log.logInfo("api level = " + device.getApiLevel());
            Log.logInfo("platform version = " + device.getPlatformVersion());
            Log.logInfo("core version = " + device.getCoreVersion());
            Log.logInfo("system version = " + device.getSysVersion());
            Log.logInfo("version = " + device.getVersion());
            Log.logInfo("app = " + pack);
            Log.logInfo("app activity = " + device.getAppActivity(config.getApp()));
            Log.logInfo("app current activity = " + device.getCurrentActivity());
            Log.logInfo("app is alive = " + device.appIsAlive(pack));
            Log.logInfo("language = " + device.getLanguage());
            Log.logInfo("resolution = " + device.getResolution());
            Log.logInfo("uiautomator pid = " + device.getUiautomatorProcess());
            Log.logInfo("########################################################################");

            engine = EngineFactory.build(PlatformName.Android, driver, config);

            if (((AndroidDriver) driver).isAppInstalled(getRemoveApp()))
                logManager.run(config);

            if (null != guideFlow && guideFlow.size() > 0)
                homePageSource = engine.guideRuleProcessing(guideFlow, config.getInterval());

            if (runMode == 1) {
                taskStack = engine.getTaskStack(Type.XML, homePageSource, 1);

                Log.logInfo("########################### 开始执行探索性遍历测试 ###########################");
                engine.dfsSearch(taskStack, config.getDepth());
            }

            afterTravel();
        } catch (ClassNotFoundException e) {
            Log.logError(e.fillInStackTrace());
        } catch (NoSuchMethodException e) {
            Log.logError(e.fillInStackTrace());
        } catch (IllegalAccessException e) {
            Log.logError(e.fillInStackTrace());
        } catch (InvocationTargetException e) {
            Log.logError(e.fillInStackTrace());
        } catch (InstantiationException e) {
            Log.logError(e.fillInStackTrace());
        }
    }
}
