package pers.traveler.robot;

import pers.traveler.constant.Package;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by quqing on 16/5/18.
 * 机器人工厂
 */
public class RobotFactory {
    public static Robot build(String robotType, String configFile) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = Class.forName(Package.ROBOT.replaceAll("#type#", robotType));
        return (Robot) clazz.getConstructor(String.class).newInstance(configFile);
    }
}
