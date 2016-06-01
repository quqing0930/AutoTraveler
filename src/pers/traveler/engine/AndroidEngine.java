package pers.traveler.engine;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pers.traveler.constant.Attribute;
import pers.traveler.constant.CmdConfig;
import pers.traveler.entity.Config;
import pers.traveler.parser.AndroidXmlParser;
import pers.traveler.tools.CmdUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by quqing on 16/5/6.
 */
public class AndroidEngine extends Engine {
    public AndroidEngine(WebDriver driver, Config config) {
        super(driver, config);
        parser = new AndroidXmlParser(config);
    }

    @Override
    protected boolean notInBlackList(WebElement element, List<String> blackList) {
        String text = element.getText();
        String name = element.getAttribute(Attribute.NAME);
        String resourceId = element.getAttribute(Attribute.RESOURCEID);

        for (String black : blackList) {
            if (!text.isEmpty() && text.contains(black)) {
                return false;
            } else if (!name.isEmpty() && name.contains(black)) {
                return false;
            } else if (!resourceId.isEmpty() && black.equals(resourceId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void screenShot() {
        CmdUtil.run(CmdConfig.SCREEN_CAP.replaceAll("#udid#", config.getUdid()));
        CmdUtil.run(CmdConfig.PULL_SCREENSHOT.replaceAll("#udid#", config.getUdid()).replaceAll("#path2png#", config.getScreenshot() + File.separator + counter++ + ".png"));
    }

    @Override
    protected String doBackNoTrigger() throws Exception {
        if (!doNotBack(driver.getPageSource())) {
            ((AndroidDriver) driver).pressKeyCode(AndroidKeyCode.BACK);
            TimeUnit.SECONDS.sleep(config.getInterval());
            screenShot();
            return driver.getPageSource();
        }
        return null;
    }
}