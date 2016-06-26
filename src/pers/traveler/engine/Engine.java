package pers.traveler.engine;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import pers.traveler.constant.Action;
import pers.traveler.constant.Type;
import pers.traveler.entity.Config;
import pers.traveler.entity.UiNode;
import pers.traveler.log.Log;
import pers.traveler.parser.ConfigProvider;
import pers.traveler.parser.XmlParser;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by quqing on 16/5/6.
 */
public abstract class Engine {
    protected static int counter = 0;
    protected WebDriver driver;
    protected Config config;
    protected XmlParser parser;

    public Engine(WebDriver driver, Config config) {
        this.driver = driver;
        this.config = config;
    }

    /**
     * 返回, 无触发器
     *
     * @throws Exception
     */
    protected abstract String doBackNoTrigger() throws Exception;

    /**
     * 是否包含黑名单
     *
     * @param element
     * @param blackList
     * @return
     */
    protected abstract boolean notInBlackList(WebElement element, List<String> blackList);

    /**
     * 屏幕截图
     */
    protected abstract void screenShot() throws IOException;

    /**
     * 基于dfs的探索性遍历
     *
     * @param taskStack
     * @param depth
     */
    public void dfsSearch(Stack<UiNode> taskStack, int depth) {
        int thisDepth, newTaskCount, repeatCount = 0;
        UiNode thisNode;
        WebElement element;
        Stack<UiNode> children;
        Stack<UiNode> existsTaskStack;
        List<String> triggerList;
        List<UiNode> blackList = new ArrayList<>();
        String xpath, thisWindow, preWindow, thisPageSource, doBackWin;

        // 首次获取窗口内容和窗口标识
        thisPageSource = driver.getPageSource();
        thisWindow = parser.getCurrentWindowID(thisPageSource);
        preWindow = thisWindow;
        triggerList = config.getTriggerList();

        while (!taskStack.isEmpty()) {
            if (repeatCount > config.getAllowSameWinTimes())
                break;
            thisNode = taskStack.pop();
            blackList.add(thisNode);
            Log.logInfo("报告主人,有1个节点任务出栈并加入黑名单 -> 现在还有" + taskStack.size() + "个任务待运行, [" + thisNode.getInfo() + "], " + thisNode.getId());

            try {
                // 截图
                screenShot();

                // 触发器预处理
                if (triggerProcessing(driver.getPageSource(), triggerList)) {
                    screenShot();
                    thisPageSource = driver.getPageSource();
                    thisWindow = parser.getCurrentWindowID(thisPageSource);
                }

                Log.logInfo("当前节点任务所属窗口是否就是当前窗口 -> " + thisNode.getWindowID().equals(thisWindow));

                if (!thisWindow.equals(thisNode.getWindowID())) {
                    // 在任务栈中搜索当前窗口,如果存在,则获取该窗口下所有任务节点
                    existsTaskStack = searchByWindowID(thisWindow, taskStack);

                    // 如果当前窗口已存在任务栈中
                    if (null != existsTaskStack) {
                        repeatCount = 0;
                        Log.logInfo(thisNode.getWindowID() + " >> " + thisWindow + ", 窗口迁移至老窗口,获取到窗口任务列表 -> " + existsTaskStack.size());
                        resetTaskStack(taskStack, existsTaskStack);
                        Log.logInfo("任务栈已更新,现在还有" + taskStack.size() + "个任务待运行......");
                    } else {
                        Log.logInfo(thisNode.getWindowID() + " >> " + thisWindow + ", 窗口迁移至新窗口......");
                        if (preWindow.equals(thisWindow)) {
                            repeatCount = repeatCount + 1;
                        } else {
                            repeatCount = 0;
                        }
                        Log.logInfo("相同窗口执行次数 -> " + repeatCount);
                        preWindow = thisWindow;
                        thisDepth = thisNode.getDepth();

                        // 遍历深度控制,0表示未限制
                        if (depth == 0 || thisDepth < depth) {
                            thisPageSource = driver.getPageSource();
                            thisWindow = parser.getCurrentWindowID(thisPageSource);
                            children = getTaskStack(Type.XML, thisPageSource, thisNode.getDepth() + 1);

                            // 是否获取到新窗口节点任务
                            newTaskCount = null != children ? children.size() : 0;

                            Log.logInfo(newTaskCount + "个新任务准备入栈......");
                            children = removeNodes(blackList, children);
                            children = filterNodes(taskStack, children);
                            children = updateTaskStack(children, thisNode);

                            // 如果有新的节点任务生成,把当前节点任务先压栈,新生成的节点任务出栈
                            if (null != children && children.size() > 0) {
                                Log.logInfo(children.size() + "个新任务允许入栈......");
                                taskStack.push(thisNode);
                                taskStack.addAll(children);
                                Log.logInfo("任务栈已更新, " + children.size() + "个新任务允许入栈, 现在还有" + taskStack.size() + "个任务待运行......");

                                // 更新任务栈后,新任务出栈
                                thisNode = taskStack.pop();
                                blackList.add(thisNode);
                                Log.logInfo("任务栈已更新,现在还有" + taskStack.size() + "个任务待运行......");

                            }

                            if (newTaskCount == 0 || children.size() == 0 && needBack(thisWindow, taskStack)) {
                                Log.logInfo(thisNode.getWindowID() + " >> " + thisWindow + ", 窗口虽然发生迁移,但没有新任务加入, 并且本窗口节点任务已遍历完毕,点击返回......");
                                doBack();
                            }

                        } else {
                            Log.logInfo("[" + thisNode.getInfo() + "], " + thisNode.getWindowID() + " >> " + thisWindow + ", 窗口虽然发生迁移,但已达到最大遍历深度 -> " + thisDepth + ", 点击返回......");
                            doBack();
                        }
                    }
                }

                Log.logInfo("[" + thisNode.getInfo() + "], " + thisNode.getWindowID() + " >> 开始执行节点任务......");

                // 每次迭代懒加载元素对象
                xpath = thisNode.getId().split("-")[3];
                element = driver.findElement(By.xpath(xpath));
                if (thisNode.getAction().equals(Action.CLICK)) {
                    Log.logInfo(Action.CLICK + " -> " + "[info = " + thisNode.getInfo() + "], [depth = " + thisNode.getDepth() + "]" + thisNode.getId());
                    element.click();
                } else if (thisNode.getAction().equals(Action.INPUT)) {
                    Log.logInfo(Action.INPUT + " -> " + "[info = " + thisNode.getInfo() + "], [depth = " + thisNode.getDepth() + "], sendKeys -> 8888, " + thisNode.getId());
                }

                // 任务执行后获取窗口内容和窗口标识
                TimeUnit.SECONDS.sleep(config.getInterval());
                thisPageSource = driver.getPageSource();
                thisWindow = parser.getCurrentWindowID(thisPageSource);

                // 如果同窗口的任务栈已处理完毕,并且还停留在该窗口,返回至上一个窗口
                if (thisNode.getWindowID().equals(thisWindow) && needBack(thisNode.getWindowID(), taskStack)) {
                    Log.logInfo(thisNode.getWindowID() + " == " + thisWindow + ", 窗口未发生迁移,并且本窗口节点任务已遍历完毕,点击返回......");
                    // 获取返回后的窗口内容和窗口标识
                    doBackWin = doBack();
                    thisPageSource = null == doBackWin ? thisPageSource : doBackWin;
                    thisWindow = parser.getCurrentWindowID(thisPageSource);
                }
            } catch (NoSuchElementException e) {
                Log.logError("节点任务 -> [info = " + thisNode.getInfo() + "], NoSuchElementException, 弹出下一个节点任务, " + thisNode.getId());
                continue;
            } catch (org.openqa.selenium.ElementNotVisibleException e) {
                Log.logError("节点任务 -> [info = " + thisNode.getInfo() + "], ElementNotVisibleException, 弹出下一个节点任务, " + thisNode.getId());
                continue;
            } catch (org.openqa.selenium.NoSuchSessionException e) {
                Log.logError("会话丢失,退出 >> 节点任务 -> [info = " + thisNode.getInfo() + "], NoSuchSessionException, 弹出下一个节点任务, " + thisNode.getId());
                break;
            } catch (org.openqa.selenium.SessionNotCreatedException e) {
                Log.logError("会话未创建,退出 >> 节点任务 -> [info = " + thisNode.getInfo() + "], SessionNotCreatedException, 弹出下一个节点任务, " + thisNode.getId());
                break;
            } catch (org.openqa.selenium.NotFoundException e) {
                Log.logError("节点任务 -> [info = " + thisNode.getInfo() + "], NotFoundException, 弹出下一个节点任务, " + thisNode.getId());
                continue;
            } catch (Exception e) {
                Log.logError("节点任务 -> [info = " + thisNode.getInfo() + "], 发生未知异常, 弹出下一个节点任务, " + thisNode.getId());
                Log.logError(e.fillInStackTrace());
                continue;
            }
        }
    }

    /**
     * 返回
     *
     * @throws Exception
     */
    protected String doBack() throws Exception {
        triggerProcessing(driver.getPageSource(), config.getTriggerList(), Action.GESTURE);
        triggerProcessing(driver.getPageSource(), config.getTriggerList(), Action.DELAY);
        triggerProcessing(driver.getPageSource(), config.getTriggerList(), Action.CLICK);
        return doBackNoTrigger();
    }

    /**
     * 当前窗口不需要返回的判断
     *
     * @param pageSource
     * @return
     */
    protected boolean doNotBack(String pageSource) {
        List<String> notBackList = config.getNotBackList();
        for (String notBackItem : notBackList) {
            if (doNotBack(pageSource, notBackItem))
                return true;
        }
        return false;
    }

    /**
     * 当前窗口不需要返回的判断
     *
     * @param pageSource
     * @param notBackItem
     * @return
     */
    protected boolean doNotBack(String pageSource, String notBackItem) {
        String[] notBackKeys;
        if (null != notBackItem) {
            if (notBackItem.indexOf(",") == -1) {
                notBackKeys = new String[]{notBackItem};
            } else {
                notBackKeys = notBackItem.split(",");
            }

            for (String notBackKey : notBackKeys) {
                if (!pageSource.contains(notBackKey.trim()))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 重置任务栈
     *
     * @param taskStack
     * @param existsTaskStack
     */
    protected void resetTaskStack(Stack<UiNode> taskStack, Stack<UiNode> existsTaskStack) {
        taskStack.removeAll(existsTaskStack);
        taskStack.addAll(existsTaskStack);
    }

    /**
     * 获取任务栈
     *
     * @param type
     * @param source
     * @param depth
     * @return
     */
    public Stack<UiNode> getTaskStack(byte type, String source, int depth) {
        List<UiNode> tempForReverse;
        Stack<UiNode> taskStack = null;

        if (type == Type.XML) {
            taskStack = getTaskStackByXml(source, depth);
        } else if (type == Type.DRIVER) {
            taskStack = getTaskStackByDriver(source, depth);
        }

        if (null != taskStack && config.getReverse() == 1) {
            tempForReverse = new ArrayList<>();
            for (int i = taskStack.size() - 1; i >= 0; i--) {
                tempForReverse.add(taskStack.get(i));
            }
            taskStack.clear();
            taskStack.addAll(tempForReverse);
        }

        return taskStack;
    }

    /**
     * 从xml获取任务栈
     *
     * @param pageSource
     * @param depth
     * @return
     */
    protected Stack<UiNode> getTaskStackByXml(String pageSource, int depth) {
        Stack<UiNode> taskStack;
        taskStack = parser.getNodesFromWindow(pageSource, depth);

        return taskStack;
    }

    /**
     * 从driver获取任务栈
     *
     * @param windowID
     * @param depth
     * @return
     */
    protected Stack<UiNode> getTaskStackByDriver(String windowID, int depth) {
        String className;
        UiNode node;
        List<WebElement> elementList = null;
        List<String> blackList, inputList, clickList;
        Stack<UiNode> taskStack = new Stack<>();
        blackList = config.getBlackList();
        clickList = config.getClickList();
        inputList = config.getInputList();

        if (driver instanceof AndroidDriver) {
            elementList = ((AppiumDriver) driver).findElements(By.xpath("//*[@clickable='true' and @enabled='true']"));
        } else if (driver instanceof IOSDriver) {
            elementList = ((AppiumDriver) driver).findElements(By.xpath("//*[@visible='true' and @enabled='true' and @valid='true']"));
        }

        for (WebElement element : elementList) {
            try {
                className = element.getTagName();
                if (null != className) {
                    if (clickList.contains(className) || inputList.contains(className)) {
                        if (null == blackList || notInBlackList(element, blackList)) {
                            node = new UiNode();
                            node.setId(windowID + "-" + element.getLocation().toString() + "-" + element.getSize().toString());
                            node.setWindowID(windowID);
                            node.setElement(element);
                            node.setDepth(depth);
                            node.setAction(clickList.contains(className) ? Action.CLICK : Action.INPUT);
                            taskStack.push(node);
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                continue;
            }
        }

        return taskStack;
    }

    /**
     * 更新左节点
     *
     * @param taskStack
     * @param leftNode
     * @return
     */
    protected Stack<UiNode> updateTaskStack(Stack<UiNode> taskStack, UiNode leftNode) {
        UiNode uiNode;
        for (int i = 0; i < taskStack.size(); i++) {
            uiNode = taskStack.get(i);
            uiNode.setLeftNode(leftNode);
        }
        return taskStack;
    }

    /**
     * 引导规则处理
     *
     * @param guideFlow
     * @param interval
     * @return
     */
    public String guideRuleProcessing(List<String> guideFlow, long interval) {
        String actionType;
        String locationType;
        String location;
        String inputContent;

        try {
//            acceptAlert(config.getTips());
            for (int i = 0; i < guideFlow.size(); i++) {
                try {
                    actionType = guideFlow.get(i).split(">>")[0];
                    if (actionType.equalsIgnoreCase(Action.SLIDE)) {
                        int times;
                        times = Integer.parseInt(guideFlow.get(i).split(">>")[1]);
                        swipeRightToLeft(times);
                    } else if (actionType.equalsIgnoreCase(Action.TIPS)) {
                        String str = guideFlow.get(i);
                        int index = str.indexOf(">>") + 2;
                        acceptAlert(str.substring(index));
                    } else if (actionType.equalsIgnoreCase(Action.TAP)) {
                        tapByCoordinate(guideFlow.get(i));
                    } else if (actionType.equalsIgnoreCase(Action.SCROLL_TO)) {
                        Log.logInfo("scroll to [" + guideFlow.get(i).split(">>")[1] + "]");
                        ((AppiumDriver) driver).scrollTo(guideFlow.get(i).split(">>")[1]);
                    } else if (actionType.equalsIgnoreCase(Action.SCROLL_TO_EXACT)) {
                        Log.logInfo("scroll to exact [" + guideFlow.get(i).split(">>")[1] + "]");
                        ((AppiumDriver) driver).scrollToExact(guideFlow.get(i).split(">>")[1]);
                    } else if (actionType.equalsIgnoreCase(Action.WAIT)) {
                        Log.logInfo("wait for [" + guideFlow.get(i).split(">>")[1] + "]");
                        TimeUnit.SECONDS.sleep(Long.parseLong(guideFlow.get(i).split(">>")[1]));
                    } else if (actionType.equalsIgnoreCase(Action.RE_LOGIN)) {
                        Log.logInfo("do re-login");
                        ((AppiumDriver) driver).resetApp();
                    } else if (actionType.equalsIgnoreCase(Action.INCLUDE)) {
                        Log.logInfo("execute module >> " + guideFlow.get(i).split(">>")[1]);
                        guideRuleProcessing(new ConfigProvider().getModule(guideFlow.get(i).split(">>")[1]), interval);
                    } else {
                        locationType = guideFlow.get(i).split(">>")[1].split("::")[0];
                        if (actionType.equalsIgnoreCase(pers.traveler.constant.Action.CLICK)) {
                            location = guideFlow.get(i).split(">>")[1].split("::")[1];
                            Log.logInfo(Action.CLICK + " -> " + location);
                            if (locationType.equalsIgnoreCase(Action.ID)) {
                                driver.findElement(By.id(location)).click();
                            } else if (locationType.equalsIgnoreCase(Action.NAME)) {
                                driver.findElement(By.name(location)).click();
                            } else if (locationType.equalsIgnoreCase(Action.XPATH)) {
                                driver.findElement(By.xpath(location)).click();
                            }
                        } else if (actionType.equalsIgnoreCase(Action.INPUT)) {
                            location = guideFlow.get(i).split(">>")[1].split("::")[1].split("\\|")[0];
                            inputContent = guideFlow.get(i).split("\\|")[1];
                            Log.logInfo(Action.INPUT + " -> " + location + ", sendKeys -> " + inputContent);
                            if (locationType.equalsIgnoreCase(Action.ID)) {
                                driver.findElement(By.id(location)).click();
                                driver.findElement(By.id(location)).sendKeys(inputContent);
                            } else if (locationType.equalsIgnoreCase(Action.NAME)) {
                                driver.findElement(By.name(location)).click();
                                driver.findElement(By.name(location)).sendKeys(inputContent);
                            } else if (locationType.equalsIgnoreCase(Action.XPATH)) {
                                driver.findElement(By.xpath(location)).click();
                                driver.findElement(By.xpath(location)).sendKeys(inputContent);
                            }
                        } else if (actionType.equalsIgnoreCase(Action.GESTURE)) {
                            List<WebElement> gestureContainer;
                            location = guideFlow.get(i).split(">>")[1].split("::")[1];
                            Log.logInfo(Action.GESTURE + " -> " + location);
                            gestureContainer = getGesturepwdWebElements(location, new int[]{1, 2, 3, 6, 5, 4, 7, 8, 9});
                            GesturePWD(gestureContainer);
                        } else if (actionType.equalsIgnoreCase(Action.ASSERT)) {
                            String[] strings;
                            String pageSource = driver.getPageSource();
//                        location = guideFlow.get(i).split(">>")[1].split("::")[1].split("\\|")[0];
                            inputContent = guideFlow.get(i).split("\\|")[1];
                            if (inputContent.indexOf(",") == -1) {
                                strings = new String[]{inputContent};
                            } else {
                                strings = inputContent.split(",");
                            }
                            Assert.assertTrue(isContain(pageSource, strings));
                        }
                        screenShot();
                    }
                    TimeUnit.SECONDS.sleep(interval);
                } catch (InterruptedException e) {
                    Log.logError(e.fillInStackTrace());
                } catch (NoSuchElementException e) {
                    Log.logError(e.fillInStackTrace());
                    continue;
                } catch (AssertionError e) {
                    Log.logError(e.fillInStackTrace());
                    continue;
                }
            }
            TimeUnit.SECONDS.sleep(interval);
            return driver.getPageSource();
        } catch (InterruptedException e) {
            Log.logError(e.fillInStackTrace());
        } catch (IOException e) {
            Log.logError(e.fillInStackTrace());
        }
        return null;
    }

    private boolean isContain(String source, String[] strings) {
        for (String str : strings) {
            if (!source.contains(str)) {
                Log.logError("failure, page not contains -> " + str);
                return false;
            }
        }
        Log.logInfo("success, page contains -> " + Arrays.asList(strings));
        return true;
    }

    private void tapByCoordinate(String stepConfig) {
        String tempCoordinate;
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        tempCoordinate = Double.toString(width * Double.parseDouble(stepConfig.split(">>")[1].split(",")[0].split(":")[1]));
        tempCoordinate = formatCoordinate(tempCoordinate);
        int x = Integer.parseInt(tempCoordinate);
        tempCoordinate = Double.toString(height * Double.parseDouble(stepConfig.split(">>")[1].split(",")[1].split(":")[1]));
        tempCoordinate = formatCoordinate(tempCoordinate);
        int y = Integer.parseInt(tempCoordinate);
        int fingers = Integer.parseInt(stepConfig.split(">>")[1].split(",")[2].split(":")[1]);
        int touchCount = Integer.parseInt(stepConfig.split(">>")[1].split(",")[3].split(":")[1]);
        int duration = Integer.parseInt(stepConfig.split(">>")[1].split(",")[4].split(":")[1]);
        Log.logInfo("width=" + width + ", height=" + height);
        for (int i = 0; i < touchCount; i++) {
            Log.logInfo("轻触屏幕 -> x=" + x + ", y=" + y + ",fingers=" + fingers + ",touchCount=" + touchCount + ", duration=" + duration);
            ((AppiumDriver) driver).tap(fingers, x, y, duration);
        }
    }

    private String formatCoordinate(String coordinate) {
        if (coordinate.indexOf(".") != -1) {
            coordinate = coordinate.substring(0, coordinate.indexOf("."));
        }
        return coordinate;
    }

    /**
     * 模拟轻触操作。
     *
     * @param width      x坐标.
     * @param height     y坐标.
     * @param tapCount   轻触手指数.
     * @param touchCount 轻触次数.
     * @param duration   持续时间
     */
    public void tapByCoordinate(double width, double height, double tapCount, double touchCount, double duration) {
        Map tap = new HashMap();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        tap.put("tapCount", tapCount);
        tap.put("touchCount", touchCount);
        tap.put("duration", duration);
        tap.put("x", width);
        tap.put("y", height);
        js.executeScript("mobile: tap", tap);
    }

    /**
     * swipe down
     *
     * @param during wait for page loading
     * @author quqing
     */
    public void swipeToDown(int during, int startXDenominator, int startYDenominator, int endXDenominator, int endYDenominator) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        ((AppiumDriver) driver).swipe(width / startXDenominator, height / startYDenominator, width / endXDenominator, height / endYDenominator, during);
    }

    /**
     * swipe up
     *
     * @param during wait for page loading
     * @author quqing
     */
    public void swipeToUp(int during) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        ((AppiumDriver) driver).swipe(width / 2, height * 3 / 4, width / 2, height / 6, during);
    }

    /**
     * 在指定时间内等待，直到文本出现在页面上。
     *
     * @param timeoutInSeconds 设置等待时间,单位:秒.
     * @param TargetText       等待出现的文本,可以设置多个.
     * @return boolean
     */
    protected boolean waitForText(int timeoutInSeconds, String... TargetText) {
        int i;
        Boolean flag = false;
        String pageSource = null;
        long currentTime = System.currentTimeMillis();
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(config.getInterval());
                if (driver != null)
                    pageSource = driver.getPageSource();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (null != pageSource) {
                for (i = 0; i < TargetText.length; i++) {
                    if (!pageSource.contains(TargetText[i])) {
                        flag = false;
                        break;
                    }
                }
                if (i == TargetText.length)
                    flag = true;
            }

            if (System.currentTimeMillis() - currentTime >= timeoutInSeconds * 1000 || flag) {
                break;
            }
        }

        return flag;
    }

    /**
     * 弹出框处理
     */
    protected void acceptAlert(String tips) throws InterruptedException {
        if (null != tips) {
            String[] keys;
            String condition = tips.split(">>")[0];
            if (condition.indexOf("|") != -1) {
                keys = tips.split(">>")[0].split("\\|");
            } else {
                keys = new String[]{condition};
            }
            if (waitForText(16, keys)) {
                driver.findElement(By.xpath(tips.split(">>")[1])).click();
            }
        }
//        TimeUnit.SECONDS.sleep(config.getInterval());
    }

    /**
     * 模拟向左滑屏。
     */
    protected boolean swipeRightToLeft(int times) {
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        try {
            for (int i = 0; i < times; i++) {
                ((AppiumDriver) driver).swipe(width * 9 / 10, height / 2, width * 1 / 10, height / 2, 1000);
                screenShot();
                TimeUnit.SECONDS.sleep(config.getInterval());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected List<WebElement> getGesturepwdWebElements(String location, int[] password) {
        List<WebElement> webElements = new ArrayList<WebElement>();
        List<WebElement> GestureContainer;
        GestureContainer = driver.findElements(By.xpath(location));
        for (int i = 0; i < password.length; i++) {
            if (password[i] == 1) {
                webElements.add(GestureContainer.get(0));
            } else if (password[i] == 2) {
                webElements.add(GestureContainer.get(1));
            } else if (password[i] == 3) {
                webElements.add(GestureContainer.get(2));
            } else if (password[i] == 4) {
                webElements.add(GestureContainer.get(3));
            } else if (password[i] == 5) {
                webElements.add(GestureContainer.get(4));
            } else if (password[i] == 6) {
                webElements.add(GestureContainer.get(5));
            } else if (password[i] == 7) {
                webElements.add(GestureContainer.get(6));
            } else if (password[i] == 8) {
                webElements.add(GestureContainer.get(7));
            } else if (password[i] == 9) {
                webElements.add(GestureContainer.get(8));
            }
        }
        return webElements;
    }

    /**
     * 模拟手势密码。
     *
     * @param webElements 手势密码元素对象.
     */
    protected void GesturePWD(List<WebElement> webElements) {
        int x = webElements.get(0).getSize().width / 2;
        int y = webElements.get(0).getSize().height / 2;
        TouchAction touchAction = new TouchAction((AppiumDriver) driver);
        touchAction.press(webElements.get(0).getLocation().x + x, webElements.get(0).getLocation().y + y);
        for (int i = 1; i < webElements.size(); i++) {
            touchAction.waitAction(500);
            touchAction.moveTo(webElements.get(i).getLocation().x - webElements.get(i - 1).getLocation().x, webElements.get(i).getLocation().y - webElements.get(i - 1).getLocation().y);
        }
        touchAction.release();
        touchAction.perform();
    }

    /**
     * 任务栈中删除黑名单
     *
     * @param blackList
     * @param taskStack
     * @return
     */
    protected Stack<UiNode> removeNodes(List<UiNode> blackList, Stack<UiNode> taskStack) {
        if (null != taskStack) {
            List<UiNode> removeList = new ArrayList<>();
            Stack<UiNode> blackStack = new Stack<>();
            blackStack.addAll(blackList);

            for (int i = 0; i < taskStack.size(); i++) {
                if (exist(taskStack.get(i), blackStack)) {
                    removeList.add(taskStack.get(i));
                }
            }

            taskStack.removeAll(removeList);
        }
        return taskStack;
    }

    /**
     * 按过滤级别过滤新任务栈,返回过滤后的任务栈
     *
     * @param taskStack
     * @param childrenStack
     * @return
     */
    protected Stack<UiNode> filterNodes(Stack<UiNode> taskStack, Stack<UiNode> childrenStack) {
        List<UiNode> needList = new ArrayList<>();
        if (childrenStack != null && !childrenStack.isEmpty()) {
            for (UiNode child : childrenStack) {
                if (!exist(child, taskStack))
                    needList.add(child);
            }
            if (needList.size() > 0) {
                childrenStack.clear();
                childrenStack.addAll(needList);
            }
        }
        return childrenStack;
    }

    /**
     * 按过滤级别判断元素是否已存在
     *
     * @param child
     * @param taskStack
     * @return
     */
    protected boolean exist(UiNode child, Stack<UiNode> taskStack) {
        String mark;
        String childMark;
        int filter = config.getFilter();

        if (filter == 1) {
            for (int i = 0; i < taskStack.size(); i++) {
                if (taskStack.get(i).getInfo().equals(child.getInfo()))
                    return true;
            }
            return false;
        } else if (filter == 2) {
            childMark = child.getWindowID() + child.getInfo();
            for (int i = 0; i < taskStack.size(); i++) {
                mark = taskStack.get(i).getWindowID() + taskStack.get(i).getInfo();
                if (mark.equals(childMark))
                    return true;
            }
            return false;
        } else {
            for (int i = 0; i < taskStack.size(); i++) {
                if (taskStack.get(i).getId().equals(child.getId()))
                    return true;
            }
            return false;
        }
    }

    /**
     * 判断是否存在同窗口的节点
     *
     * @param windowID
     * @param nodeStack
     * @return
     */
    protected boolean needBack(String windowID, Stack<UiNode> nodeStack) {
        for (int i = 0; i < nodeStack.size(); i++) {
            if (windowID.equals(nodeStack.get(i).getWindowID())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据窗口ID在任务栈中搜索节点,并返回结果
     *
     * @param windowID
     * @param nodeStack
     * @return
     */
    protected Stack<UiNode> searchByWindowID(String windowID, Stack<UiNode> nodeStack) {
        Stack<UiNode> searchStack = new Stack<>();
        for (int i = 0; i < nodeStack.size(); i++) {
            if (nodeStack.get(i).getWindowID().equals(windowID)) {
                searchStack.push(nodeStack.get(i));
            }
        }
        searchStack = searchStack.size() > 0 ? searchStack : null;
        return searchStack;
    }

    /**
     * 触发器处理 页面内容如果满足触发条件,执行特定操作
     *
     * @param pageSource
     * @param triggerList
     */
    protected boolean triggerProcessing(String pageSource, List<String> triggerList) {
        String key;
        String action;
        List<String> keyList;
        if (null != triggerList && triggerList.size() > 0) {
            for (String trigger : triggerList) {
                keyList = new ArrayList<>();
                key = trigger.split(">>")[0];
                action = trigger.split(">>")[1];

                if (key.indexOf("|") == -1) {
                    keyList.add(key);
                } else {
                    keyList = Arrays.asList(key.split("\\|"));
                }
                if (needTrigger(pageSource, keyList)) {
                    try {
                        if (action.equalsIgnoreCase(Action.BACK)) {
                            doBackNoTrigger();
                        } else if (action.indexOf("->") != -1 && action.split("->")[0].equalsIgnoreCase(Action.GESTURE)) {
                            action = action.split("->")[1];
                            GesturePWD(getGesturepwdWebElements(action, new int[]{1, 2, 3, 6, 5, 4, 7, 8, 9}));
                        } else if (action.indexOf("->") != -1 && action.split("->")[0].equalsIgnoreCase(Action.DELAY)) {
                            break;
                        } else {
                            driver.findElement(By.xpath(action)).click();
                        }
                        TimeUnit.SECONDS.sleep(config.getInterval());
                        return true;
                    } catch (NoSuchElementException e) {
                        Log.logError(e.fillInStackTrace());
                        continue;
                    } catch (Exception e) {
                        Log.logError(e.fillInStackTrace());
                    }
                }
            }
        }
        return false;
    }

    /**
     * 触发器处理 页面内容如果满足触发条件,执行特定类型的操作
     *
     * @param pageSource
     * @param triggerList
     * @param type
     */
    protected boolean triggerProcessing(String pageSource, List<String> triggerList, String type) {
        String key;
        String action;
        List<String> keyList;
        if (null != triggerList && triggerList.size() > 0) {
            for (String trigger : triggerList) {
                keyList = new ArrayList<>();
                key = trigger.split(">>")[0];
                action = trigger.split(">>")[1];

                if (key.indexOf("|") == -1) {
                    keyList.add(key);
                } else {
                    keyList = Arrays.asList(key.split("\\|"));
                }
                if (needTrigger(pageSource, keyList)) {
                    try {
                        if (action.equalsIgnoreCase(Action.BACK)) {
                            if (type.equalsIgnoreCase(Action.BACK)) {
                                doBackNoTrigger();
                                return true;
                            }
                        } else if (action.indexOf("->") != -1 && action.split("->")[0].equalsIgnoreCase(Action.GESTURE)) {
                            if (type.equalsIgnoreCase(Action.GESTURE)) {
                                action = action.split("->")[1];
                                GesturePWD(getGesturepwdWebElements(action, new int[]{1, 2, 3, 6, 5, 4, 7, 8, 9}));
                                return true;
                            }
                        } else if (action.indexOf("->") != -1 && action.split("->")[0].equalsIgnoreCase(Action.DELAY)) {
                            if (type.equalsIgnoreCase(Action.DELAY)) {
                                action = action.split("->")[1];
                                TimeUnit.SECONDS.sleep(Long.parseLong(action));
                                return true;
                            }
                        } else {
                            if (type.equalsIgnoreCase(Action.CLICK)) {
                                driver.findElement(By.xpath(action)).click();
                                return true;
                            }
                        }
                    } catch (NoSuchElementException e) {
                        Log.logError(e.fillInStackTrace());
                        continue;
                    } catch (Exception e) {
                        Log.logError(e.fillInStackTrace());
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否满足触发器条件,条件必须都满足
     *
     * @param pageSource
     * @param keys
     * @return
     */
    protected boolean needTrigger(String pageSource, List<String> keys) {
        for (String key : keys) {
            if (!pageSource.contains(key))
                return false;
        }
        return true;
    }
}