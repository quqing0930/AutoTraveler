package pers.traveler.device;

import pers.traveler.constant.Package;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by quqing on 16/5/18.
 * 设备工厂
 */
public class DeviceFactory {
    public static Device createDevice(String deviceType, String udid) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = Class.forName(Package.DEVICE.replaceAll("#type#", deviceType));
        return (Device) clazz.getConstructor(String.class).newInstance(udid);
    }
}