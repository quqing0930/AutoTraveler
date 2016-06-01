package pers.traveler.engine;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import pers.traveler.constant.Attribute;
import pers.traveler.constant.CmdConfig;
import pers.traveler.entity.Config;
import pers.traveler.parser.IOSXmlParser;
import pers.traveler.tools.CmdUtil;

import java.io.File;
import java.util.List;

/**
 * Created by quqing on 16/5/6.
 */
public class IOSEngine extends Engine {

    public IOSEngine(WebDriver driver, Config config) {
        super(driver, config);
        parser = new IOSXmlParser(config);
    }

    @Override
    protected boolean notInBlackList(WebElement element, List<String> blackList) {
        String text = element.getText();
        String name = element.getAttribute(Attribute.NAME);
        String value = element.getAttribute(Attribute.VALUE);
        String label = element.getAttribute(Attribute.LABEL);

        for (int i = 0; i < blackList.size(); i++) {
            if (!text.isEmpty() && text.contains(blackList.get(i))) {
                return false;
            } else if (!name.isEmpty() && name.contains(blackList.get(i))) {
                return false;
            } else if (!value.isEmpty() && value.contains(blackList.get(i))) {
                return false;
            } else if (!label.isEmpty() && label.contains(blackList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void screenShot() {
        CmdUtil.run(CmdConfig.SCREEN_IOS_CAP.replaceAll("#udid#", config.getUdid()).replaceAll("#path2png#", config.getScreenshot() + File.separator + counter++ + ".png"));
    }

    @Override
    protected String doBackNoTrigger() throws Exception {
        List<String> xpathList;
        parser.getNodesFromWindow(driver.getPageSource(), 0);
        xpathList = parser.getXpathList();

        if (!doNotBack(driver.getPageSource())) {
            for (String back : config.getBackList()) {
                try {
                    if (xpathList.contains(back)) {
                        driver.findElement(By.xpath(back)).click();
                        screenShot();
                        return driver.getPageSource();
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
        }
        return null;
    }
}