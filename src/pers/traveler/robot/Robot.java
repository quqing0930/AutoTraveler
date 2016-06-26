package pers.traveler.robot;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import pers.traveler.constant.CmdConfig;
import pers.traveler.constant.PlatformName;
import pers.traveler.device.Device;
import pers.traveler.engine.Engine;
import pers.traveler.entity.Config;
import pers.traveler.log.Log;
import pers.traveler.log.LogManager;
import pers.traveler.parser.ConfigProvider;
import pers.traveler.review.PicToAvi;
import pers.traveler.review.core.MovieSaveException;
import pers.traveler.tools.CmdUtil;
import pers.traveler.tools.DateUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Create by quqing on 16/5/8.
 */
public abstract class Robot {
    protected byte runMode;
    protected String date;
    protected String time;
    protected String deviceID;
    protected String configFile;
    protected Engine engine;
    protected WebDriver driver;
    protected Config config;
    protected Device device;
    protected LogManager logManager;

    public Robot(String configFile) {
        this.configFile = configFile;
        logManager = new LogManager();
        config = new ConfigProvider().getConfig(configFile);
    }

    /**
     * 遍历内调方法
     */
    protected abstract void working();

    /**
     * 获取removeApp
     *
     * @return
     */
    protected abstract String getRemoveApp();

    /**
     * 从日志提取错误信息
     */
    protected abstract void catchAppException();

    protected String beforeTravel() {
        byte mode;
        int port;
        String cmd;
        long timeout = 0;
        String host;
        Map<String, String> capabilityMap;

        date = device.getDate();
        time = device.getTime();
        runMode = config.getRunMode();
        deviceID = null == config.getUdid() ? UUID.randomUUID().toString() : config.getUdid();

        try {
            Log.logInfo("date = " + date);
            Log.logInfo("time = " + time);
            System.setProperty("date", date);
            System.setProperty("time", time);
            System.setProperty("deviceID", deviceID);

            mode = config.getMode();
            port = config.getPort();
            host = config.getHost();
            cmd = config.getRunServer();
            timeout = config.getTimeout();

            if (null != cmd && !cmd.isEmpty()) {
                cmd = cmd.replaceAll("#port#", Integer.toString(port)).replaceAll("#udid#", config.getUdid());
                StartAppiumServer startAppiumServer = new StartAppiumServer(cmd);
                startAppiumServer.start();

                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            capabilityMap = config.getCapabilityMap();

            Log.logInfo("########################### DesiredCapabilities ###########################");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            for (Map.Entry<String, String> entry : capabilityMap.entrySet()) {
                Log.logInfo(entry.getKey() + " = " + entry.getValue());
                capabilities.setCapability(entry.getKey(), entry.getValue());
            }
            Log.logInfo("###########################################################################");

            Log.logInfo("Appium Server -> http://" + host + ":" + port + "/wd/hub");
            if (mode == PlatformName.ANDROID) {
                driver = new AndroidDriver(new URL("http://" + host + ":" + port + "/wd/hub"), capabilities);
            } else if (mode == PlatformName.IOS) {
                driver = new IOSDriver(new URL("http://" + host + ":" + port + "/wd/hub"), capabilities);
            }

            // 创建图片文件夹
            CmdUtil.run(CmdConfig.CREATE_DIR.replaceAll("#dir#", config.getScreenshot()));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);

        return driver.getPageSource();
    }

    /**
     * 遍历内调方法, 在规定时间内运行
     *
     * @param duration
     * @throws InterruptedException
     */
    protected void working(long duration) throws InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            long startTime = System.currentTimeMillis();
            long endTime;
            long runTime;
            String showTime;

            @Override
            public void run() {
                endTime = System.currentTimeMillis();
                runTime = endTime - startTime;
                showTime = DateUtil.formatTime(runTime);
                Log.logInfo("遍历测试持续时间 -> " + showTime);
                if (runTime / 60000 >= duration) {
                    timer.cancel();
                    afterTravel();
                    System.exit(0);
                }
            }
        }, 0, 10000);

        working();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 遍历外调方法
     */
    public void travel() {
        try {
            Log.logInfo("############################ Config parameters ############################");
            Log.logInfo("mode=" + config.getMode());
            Log.logInfo("runMode=" + runMode);
            Log.logInfo("depth=" + config.getDepth());
            Log.logInfo("port=" + config.getPort());
            Log.logInfo("reverse=" + config.getReverse());
            Log.logInfo("filter=" + config.getFilter());
            Log.logInfo("port=" + config.getPort());
            Log.logInfo("duration=" + config.getDuration());
            Log.logInfo("interval=" + config.getInterval());
            Log.logInfo("timeout=" + config.getTimeout());
            Log.logInfo("host=" + config.getHost());
            Log.logInfo("app=" + config.getApp());
            Log.logInfo("udid=" + config.getUdid());
            Log.logInfo("tips=" + config.getTips());
            Log.logInfo("screenshot=" + config.getScreenshot());
            Log.logInfo("appPackage=" + config.getAppPackage());
            Log.logInfo("identifyDefault=" + config.getIdentifyDefault());
            Log.logInfo("guideFlow=" + config.getGuideFlow());
            Log.logInfo("clickList=" + config.getClickList());
            Log.logInfo("inputList=" + config.getInputList());
            Log.logInfo("backList=" + config.getBackList());
            Log.logInfo("notbackList=" + config.getNotBackList());
            Log.logInfo("blackList=" + config.getBlackList());
            Log.logInfo("triggerList=" + config.getTriggerList());
            Log.logInfo("identifySpecialList=" + config.getIdentifySpecialList());
            Log.logInfo("#########################################################################");
            if (config.getDuration() == 0) {
                working();
            } else {
                working(config.getDuration());
            }
            Log.logInfo("########################### 探索性遍历测试执行完毕 ###########################");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public long getDuration() {
        return config.getDuration();
    }

    /**
     * 测试套件执行后关闭driver。
     */
    public void afterTravel() {
        int fps = 1;
        int mWidth = 1440;
        int mHeight = 860;
        String suffix = "png";
        String pngDir = config.getScreenshot();
        String aviFileName = pngDir + File.separator + "review.avi";
        String removeApp = getRemoveApp();

        try {
            TimeUnit.SECONDS.sleep(config.getInterval());

            logManager.stop(device, config.getUdid());

            if (config.getMode() == PlatformName.ANDROID || config.getMode() == PlatformName.IOS) {
                ((AppiumDriver) driver).closeApp();
                ((AppiumDriver) driver).removeApp(removeApp);
            }
            driver.quit();

        } catch (InterruptedException e) {
            Log.logError(e.fillInStackTrace());
        } finally {
            try {
                Log.logInfo("开始统计异常信息...");
                catchAppException();
                Log.logInfo("统计异常信息完毕!");

                TimeUnit.SECONDS.sleep(10);
                Log.logInfo("开始创建视频......");
                PicToAvi.convertPicToAvi(pngDir, suffix, aviFileName, fps, mWidth, mHeight);
                Log.logInfo("创建视频完毕!");
                CmdUtil.run(CmdConfig.KILL_APP_PROCESS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (MovieSaveException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}