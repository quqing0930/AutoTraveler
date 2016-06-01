package pers.traveler.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Reporter;
import pers.traveler.tools.DateUtil;

import java.io.File;

public class Log {
    private static Logger logger;

    private static String filePath = "src" + File.separator + "log4j.properties";

    static {
        logger = Logger.getLogger("sys_log");
        PropertyConfigurator.configure(new File(filePath).getAbsolutePath());
    }

    /**
     * 记录Info级别日志。
     *
     * @param message the message object.
     */
    public static void logInfo(Object message) {
        logger.info("[INFO] " + message);
        Reporter.log(DateUtil.getSimpleDateFormat() + " : " + "[INFO] " + message);
    }

    /**
     * 记录测试步骤信息。
     *
     * @param message the message object.
     */
    public static void logStep(Object message) {
        logger.info("[STEP] " + message);
        Reporter.log(DateUtil.getSimpleDateFormat() + " : " + "[STEP] " + message);
    }

    /**
     * 记录测试流日志。
     *
     * @param message the message object.
     */
    public static void logFlow(Object message) {
        logger.info("[FLOW] " + message);
        Reporter.log(DateUtil.getSimpleDateFormat() + " : " + "[FLOW] " + message);
    }

    /**
     * 记录Error级别日志。
     *
     * @param message the message object.
     */
    public static void logError(Object message, Throwable throwable) {
//        logger.error("[ERROR]   " + message, throwable);
        logger.error("[ERROR]   " + message, throwable);
        Reporter.log(DateUtil.getSimpleDateFormat() + " : " + "[ERROR]   " + message);
    }

    public static void logError(Object message) {
        logger.error("[ERROR]   " + message);
        Reporter.log(DateUtil.getSimpleDateFormat() + " : " + "[ERROR]   " + message);
    }

    /**
     * 记录Warn级别日志。
     *
     * @param message the message object.
     */
    public static void logWarn(Object message) {
        logger.warn("[WARN] " + message);
        Reporter.log(DateUtil.getSimpleDateFormat() + " : " + "[WARN] " + message);
    }
}