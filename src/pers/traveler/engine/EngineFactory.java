package pers.traveler.engine;

import org.openqa.selenium.WebDriver;
import pers.traveler.constant.Package;
import pers.traveler.entity.Config;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by quqing on 16/5/18.
 * 引擎工厂
 */
public class EngineFactory {
    public static Engine build(String engineType, WebDriver driver, Config config) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = Class.forName(Package.ENGINE.replaceAll("#type#", engineType));
        return (Engine) clazz.getConstructor(WebDriver.class, Config.class).newInstance(driver, config);
    }
}