package pers.traveler.test;

import pers.traveler.constant.PlatformName;
import pers.traveler.device.Device;
import pers.traveler.device.DeviceFactory;
import pers.traveler.entity.UiNode;
import pers.traveler.log.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by mac on 16/5/3.
 */
public class test {
//    public static UiNode searchByID(String nodeID, List<UiNode> nodeList) {
//        for (int i = 0; i < nodeList.size(); i++) {
//            if (nodeList.get(i).getId().equals(nodeID)) {
//                return nodeList.get(i);
//            }
//        }
//        return null;
//    }

//    /**
//     * 计算节点到目标节点的深度
//     *
//     * @param thisNode
//     * @param targetNode
//     * @return
//     */
//    public static int getRelationDepth(UiNode thisNode, UiNode targetNode, int threshold) {
//        int relationDepth = 0;
//        UiNode leftNode;
//
//        leftNode = thisNode.getLeftNode();
//        if (null == leftNode) {
//            return 0;
//        }
//
//        while (null != leftNode) {
//            relationDepth++;
//            if (leftNode.getWindowID().equals(targetNode.getWindowID())) {
//                return relationDepth;
//            }
//
//            if (relationDepth > threshold)
//                return 0;
//            leftNode = leftNode.getLeftNode();
//        }
//        return 0;
//    }

    public static void main(String[] args) {
        try {

            Device device = DeviceFactory.createDevice(PlatformName.Android, "eccb30368643");
//            device.getLogCatPID();
            LogManager logManager = new LogManager();
            logManager.stop(device, "eccb30368643");
//            Screenshot.forAndroid("eccb30368643", "/Users/mac/Desktop/2.png");
            Stack<UiNode> test = new Stack<>();
            Stack<UiNode> test1 = new Stack<>();
            UiNode node1 = new UiNode();
            UiNode node2_1 = new UiNode();
            UiNode node2_2 = new UiNode();
            UiNode node3 = new UiNode();
            UiNode node4 = new UiNode();
            UiNode node5 = new UiNode();
            UiNode node6 = new UiNode();
            UiNode node7 = new UiNode();
            UiNode node8 = new UiNode();
            UiNode temp = null;

            List<UiNode> children1 = new ArrayList<>();
            children1.add(node2_1);
            List<UiNode> children2 = new ArrayList<>();
            children2.add(node3);
            children2.add(node4);
            List<UiNode> children3 = new ArrayList<>();
            children3.add(node5);

            List<UiNode> tempList;
            List<UiNode> needRemoveList = new ArrayList<>();

            node1.setWindowID("a");
            node1.setId("a-1");
            node1.setRightNode(children1);

            node2_1.setWindowID("b");
            node2_1.setRightNode(children2);
            node2_1.setLeftNode(node1);
            node2_1.setId("b-node2_1");

            node2_2.setWindowID("b");
            node2_2.setLeftNode(node1);
            node2_2.setId("b-node2_2");

            node3.setWindowID("c");
            node3.setRightNode(children3);
            node3.setLeftNode(node2_1);
            node3.setId("c-node3");

            node4.setWindowID("c");
            node4.setLeftNode(node2_1);
            node4.setId("c-node4");

            node5.setWindowID("d");
            node5.setLeftNode(node3);
            node5.setId("d-node5");

            node6.setWindowID("x");
            node6.setLeftNode(node3);
            node6.setId("x-node6");

            node7.setWindowID("y");
            node7.setLeftNode(node3);
            node7.setId("y-node7");

            node8.setWindowID("z");
            node8.setLeftNode(node3);
            node8.setId("d-node8");

            test.push(node1);
            test.push(node2_1);
            test.push(node2_2);
            test.push(node3);
            test.push(node4);
            test.push(node5);

            test1.push(node6);
            test1.push(node7);
            test1.push(node8);

            System.out.println("##############初始状态###############");
            List<UiNode> reserve = new ArrayList<>();
            for (int i = 0; i < test.size(); i++) {
                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
            }

//            System.out.println("##############反转状态###############");
            for (int i = test.size() - 1; i >= 0; i--) {
                reserve.add(test.get(i));
            }

            System.out.println("##############反转状态###############");
            test.clear();
            test.addAll(reserve);
            for (int i = 0; i < test.size(); i++) {
                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
            }

            List<UiNode> blackList = new ArrayList<>();
            blackList.add(node3);
            blackList.add(node4);

//            UiNode node = searchByID("c-node3", blackList);
//            System.out.println(node.getWindowID() + " -> " + node.getId());
//
//            int depth = getRelationDepth(node, node5, 9);
//            System.out.println(depth);

            System.out.println("##############搜索到的节点###############");
//            Stack<UiNode> uiNodes = searchByWindowID("b", test);
//            test = removeNodes(blackList, test);
            for (int i = 0; i < test.size(); i++) {
                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
            }

            System.out.println("##############删除后状态###############");
            test.removeAll(blackList);
            for (int i = 0; i < test.size(); i++) {
                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
            }

//            System.out.println("##############入栈后状态###############");
//            test.addAll(uiNodes);
//            for (int i = 0; i < test.size(); i++) {
//                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
//            }

            System.out.println("##############我是分隔符###############");
            test.pop();
            for (int i = 0; i < test.size(); i++) {
                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
            }

            System.out.println("##############我是分隔符###############");
            test.pop();
            for (int i = 0; i < test.size(); i++) {
                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
            }

//            // node5发生跳窗,跳窗至按窗口ID:b, node5级联往上查询父窗口是否存在b
//            System.out.println("##############删除被找到的老窗口节点###############");
//            UiNode findNode = new UiNode();
//            searchByWindowID(node5, findNode, "b");
//            System.out.println("windID -> " + findNode.getWindowID() + ", ID -> " + findNode.getId());
//
//            temp = searchByID(findNode.getId(),test);
//
//            test = removeNode(findNode.getId(),test);
//
//            System.out.println("##############显示删除后的状态###############");
//            for (int i = 0; i < test.size(); i++) {
//                System.out.println(test.get(i).getWindowID());
//            }
//
//            System.out.println("##############需要删除的级联节点信息###############");
//            getChildrenFromFather(temp, needRemoveList);
//
//            System.out.println("找到需要删除的节点: " + needRemoveList.size());
//            for (int i = 0; i < needRemoveList.size(); i++) {
//                System.out.println(needRemoveList.get(i).getWindowID() + " -> " + needRemoveList.get(i).getId());
//            }
//
//            System.out.println("##############删除后的节点信息###############");
//            test.removeAll(needRemoveList);
//            for (int i = 0; i < test.size(); i++) {
//                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
//            }
//
//            test.push(temp);
//            System.out.println("#############################");
//            for (int i = 0; i < test.size(); i++) {
//                System.out.println(test.get(i).getWindowID() + " -> " + test.get(i).getId());
//            }
//
//            System.out.println("#############################");
//            test.pop();
//            for (int i = 0; i < test.size(); i++) {
//                System.out.println(test.get(i).getWindowID());
//            }
//            System.out.println("#############################");
//            test.pop();
//            for (int i = 0; i < test.size(); i++) {
//                System.out.println(test.get(i).getWindowID());
//            }
//            System.out.println("#############################");
//            test.pop();
//            for (int i = 0; i < test.size(); i++) {
//                System.out.println(test.get(i).getWindowID());
//            }
//            System.out.println("#############################");

//            WebDriver driver = null;
//            ConfigProvider configProvider = new ConfigProvider();
//            Config constant = configProvider.getConfig("/Users/mac/Documents/intelliJ-idea/AutoTraveler/confige/android.xml");
//            Engine engine = new AndroidEngine(driver, constant);
//            String pageSource = FileUtil.readAll("/Users/mac/Documents/intelliJ-idea/AutoTraveler/dump.xml");
//
//            System.out.println(engine.getCurrentWindowID(pageSource));
//
//            System.out.println("mode=" + constant.getMode());
//            System.out.println("depth=" + constant.getDepth());
//            System.out.println("port=" + constant.getPort());
//            System.out.println("duration=" + constant.getDuration());
//            System.out.println("interval=" + constant.getInterval());
//            System.out.println("timeout=" + constant.getTimeout());
//            System.out.println("host=" + constant.getHost());
//            System.out.println("app=" + constant.getApp());
//            System.out.println("udid=" + constant.getUdid());
//            System.out.println("appPackage=" + constant.getAppPackage());
//            System.out.println("identifyDefault=" + constant.getIdentifyDefault());
//            System.out.println("guideFlow=" + constant.getGuideFlow());
//            System.out.println("clickList=" + constant.getClickList());
//            System.out.println("inputList=" + constant.getInputList());
//            System.out.println("backList=" + constant.getBackList());
//            System.out.println("blackList=" + constant.getBlackList());
//            System.out.println("triggerList=" + constant.getTriggerList());
//            System.out.println("identifySpecialList=" + constant.getIdentifySpecialList());
//            System.out.println("capabilityMap=" + constant.getCapabilityMap());
//            System.out.println("############################################");

//            Robot testRobot = new AndroidRobot("/Users/mac/Documents/intelliJ-idea/AutoTraveler/confige/android.xml");
//            testRobot.travel();

//            Engine engine = new AndroidEngine(driver);
//            engine.guideRuleProcessing(constant.getGuideFlow());

////            System.out.println(configProvider.getCurrentActivity());
//            System.out.println(configProvider.getMD5ByFile("/Users/mac/Documents/intelliJ-idea/AutoTraveler/testA.xml"));
//            System.out.println(configProvider.getMD5ByString("<?xml version=\"1.0\" encoding=\"UTF-8\"?><hierarchy rotation=\"0\"><android.widget.FrameLayout index=\"0\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"\" instance=\"0\"><android.widget.LinearLayout index=\"0\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"\" instance=\"0\"><android.widget.FrameLayout index=\"0\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"android:id/content\" instance=\"1\"><android.widget.FrameLayout index=\"0\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"com.pingan.yzt:id/rootview\" instance=\"2\"><android.widget.LinearLayout index=\"0\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"\" instance=\"1\"><android.view.View index=\"0\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,978]\" resource-id=\"\" instance=\"0\"/><android.widget.Button NAF=\"true\" index=\"1\" text=\"\" class=\"android.widget.Button\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[167,978][553,1074]\" resource-id=\"com.pingan.yzt:id/btnnext\" instance=\"0\"/><android.widget.LinearLayout index=\"2\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1184][720,1280]\" resource-id=\"com.pingan.yzt:id/pointLayout\" instance=\"2\"><android.view.View index=\"0\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"false\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[321,1184][337,1200]\" resource-id=\"\" instance=\"1\"/><android.view.View index=\"1\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"false\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[367,1184][383,1200]\" resource-id=\"\" instance=\"2\"/><android.view.View index=\"2\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[413,1184][429,1200]\" resource-id=\"\" instance=\"3\"/></android.widget.LinearLayout></android.widget.LinearLayout><android.support.v4.view.ViewPager index=\"1\" text=\"\" class=\"android.support.v4.view.ViewPager\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"true\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"com.pingan.yzt:id/viewpager\" instance=\"0\"><android.widget.RelativeLayout index=\"2\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"com.pingan.yzt:id/rootview\" instance=\"0\"><android.widget.ImageView index=\"0\" text=\"\" class=\"android.widget.ImageView\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,180][720,359]\" resource-id=\"com.pingan.yzt:id/iv_image\" instance=\"0\"/><android.widget.ImageView index=\"1\" text=\"\" class=\"android.widget.ImageView\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,453][720,1280]\" resource-id=\"com.pingan.yzt:id/iv_image_banner\" instance=\"1\"/></android.widget.RelativeLayout></android.support.v4.view.ViewPager></android.widget.FrameLayout></android.widget.FrameLayout></android.widget.LinearLayout></android.widget.FrameLayout></hierarchy>"));
//
//            FileUtil.writeAll("/Users/mac/Documents/intelliJ-idea/AutoTraveler/testB.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><hierarchy rotation=\"0\"><android.widget.FrameLayout index=\"0\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"\" instance=\"0\"><android.widget.LinearLayout index=\"0\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"\" instance=\"0\"><android.widget.FrameLayout index=\"0\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"android:id/content\" instance=\"1\"><android.widget.FrameLayout index=\"0\" text=\"\" class=\"android.widget.FrameLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"com.pingan.yzt:id/rootview\" instance=\"2\"><android.widget.LinearLayout index=\"0\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"\" instance=\"1\"><android.view.View index=\"0\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,978]\" resource-id=\"\" instance=\"0\"/><android.widget.Button NAF=\"true\" index=\"1\" text=\"\" class=\"android.widget.Button\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"true\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[167,978][553,1074]\" resource-id=\"com.pingan.yzt:id/btnnext\" instance=\"0\"/><android.widget.LinearLayout index=\"2\" text=\"\" class=\"android.widget.LinearLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,1184][720,1280]\" resource-id=\"com.pingan.yzt:id/pointLayout\" instance=\"2\"><android.view.View index=\"0\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"false\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[321,1184][337,1200]\" resource-id=\"\" instance=\"1\"/><android.view.View index=\"1\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"false\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[367,1184][383,1200]\" resource-id=\"\" instance=\"2\"/><android.view.View index=\"2\" text=\"\" class=\"android.view.View\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[413,1184][429,1200]\" resource-id=\"\" instance=\"3\"/></android.widget.LinearLayout></android.widget.LinearLayout><android.support.v4.view.ViewPager index=\"1\" text=\"\" class=\"android.support.v4.view.ViewPager\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"true\" focused=\"false\" scrollable=\"true\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"com.pingan.yzt:id/viewpager\" instance=\"0\"><android.widget.RelativeLayout index=\"2\" text=\"\" class=\"android.widget.RelativeLayout\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,0][720,1280]\" resource-id=\"com.pingan.yzt:id/rootview\" instance=\"0\"><android.widget.ImageView index=\"0\" text=\"\" class=\"android.widget.ImageView\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,180][720,359]\" resource-id=\"com.pingan.yzt:id/iv_image\" instance=\"0\"/><android.widget.ImageView index=\"1\" text=\"\" class=\"android.widget.ImageView\" package=\"com.pingan.yzt\" content-desc=\"\" checkable=\"false\" checked=\"false\" clickable=\"false\" enabled=\"true\" focusable=\"false\" focused=\"false\" scrollable=\"false\" long-clickable=\"false\" password=\"false\" selected=\"false\" bounds=\"[0,453][720,1280]\" resource-id=\"com.pingan.yzt:id/iv_image_banner\" instance=\"1\"/></android.widget.RelativeLayout></android.support.v4.view.ViewPager></android.widget.FrameLayout></android.widget.FrameLayout></android.widget.LinearLayout></android.widget.FrameLayout></hierarchy>");
//            String content = FileUtil.readAll("/Users/mac/Documents/intelliJ-idea/AutoTraveler/testB.xml");
//            System.out.println(configProvider.getMD5ByString(content));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
