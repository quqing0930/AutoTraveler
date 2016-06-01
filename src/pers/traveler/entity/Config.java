package pers.traveler.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by quqing on 16/5/8.
 */
public class Config {
    private byte mode;
    private byte reverse;
    private byte depth;
    private byte filter;
    private byte allowSameWinTimes;
    private int port;
    private int identifyDefault;
    private long duration;
    private long interval;
    private long timeout;
    private String host;
    private String app;
    private String udid;
    private String tips;
    private String bundleId;
    private String logCmd;
    private String screenshot;
    private String appPackage;
    private List<String> guideFlow;
    private List<String> clickList;
    private List<String> inputList;
    private List<String> blackList;
    private List<String> backList;
    private List<String> notBackList;
    private List<String> triggerList;
    private List<String> identifySpecialList;
    private Map<String, String> capabilityMap;

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public byte getDepth() {
        return depth;
    }

    public void setDepth(byte depth) {
        this.depth = depth;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Map<String, String> getCapabilityMap() {
        return capabilityMap;
    }

    public void setCapabilityMap(Map<String, String> capabilityMap) {
        this.capabilityMap = capabilityMap;
    }

    public List<String> getGuideFlow() {
        return guideFlow;
    }

    public void setGuideFlow(List<String> guideFlow) {
        this.guideFlow = guideFlow;
    }

    public List<String> getClickList() {
        return clickList;
    }

    public void setClickList(List<String> clickList) {
        this.clickList = clickList;
    }

    public List<String> getInputList() {
        return inputList;
    }

    public void setInputList(List<String> inputList) {
        this.inputList = inputList;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public List<String> getBackList() {
        return backList;
    }

    public void setBackList(List<String> backList) {
        this.backList = backList;
    }

    public List<String> getTriggerList() {
        return triggerList;
    }

    public void setTriggerList(List<String> triggerList) {
        this.triggerList = triggerList;
    }

    public int getIdentifyDefault() {
        return identifyDefault;
    }

    public void setIdentifyDefault(int identifyDefault) {
        this.identifyDefault = identifyDefault;
    }

    public List<String> getIdentifySpecialList() {
        return identifySpecialList;
    }

    public void setIdentifySpecialList(List<String> identifySpecialList) {
        this.identifySpecialList = identifySpecialList;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getLogCmd() {
        return logCmd;
    }

    public void setLogCmd(String logCmd) {
        this.logCmd = logCmd;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public byte getFilter() {
        return filter;
    }

    public void setFilter(byte filter) {
        this.filter = filter;
    }

    public List<String> getNotBackList() {
        return notBackList;
    }

    public void setNotBackList(List<String> notBackList) {
        this.notBackList = notBackList;
    }

    public byte getReverse() {
        return reverse;
    }

    public void setReverse(byte reverse) {
        this.reverse = reverse;
    }

    public byte getAllowSameWinTimes() {
        return allowSameWinTimes;
    }

    public void setAllowSameWinTimes(byte allowSameWinTimes) {
        this.allowSameWinTimes = allowSameWinTimes;
    }
}