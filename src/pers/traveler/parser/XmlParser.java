package pers.traveler.parser;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pers.traveler.entity.Config;
import pers.traveler.entity.UiNode;
import pers.traveler.log.Log;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by quqing on 16/5/5.
 */
public abstract class XmlParser {
    protected String md5;
    protected Config config;
    protected List<String> xpathList = new ArrayList<>();

    public XmlParser(Config config) {
        this.config = config;
    }

    /**
     * 获取节点的XPath
     *
     * @param node
     * @return
     */
    public abstract String getXPath(Node node);

    /**
     * 解析出节点元素列表
     *
     * @param nodeList
     * @param taskStack
     */
    protected abstract void parse(NodeList nodeList, Stack<UiNode> taskStack, int depth);

    /**
     * 格式化元素节点的特征信息
     *
     * @param nodeList
     * @param stringList
     */
    protected abstract void format(NodeList nodeList, List<String> stringList);

    /**
     * 按节点的文本内容判断是否在黑名单
     *
     * @param attributeInfo
     * @param blackList
     * @return
     */
    protected abstract boolean notInBlackList(NamedNodeMap attributeInfo, List<String> blackList);

    /**
     * 获取窗口的唯一标识符,自定义元素节点的采样个数
     *
     * @param pageSource
     * @param elements
     * @return
     */
    public String getWindowIdentify(String pageSource, int elements) {
        String formatPageSource = "";
        Document doc;
        DocumentBuilder dBuilder;
        DocumentBuilderFactory dbFactory;
        List<String> stringList = new ArrayList<>();
        InputSource is = new InputSource(new StringReader(pageSource));
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
            if (doc.hasChildNodes()) {
                format(doc.getChildNodes(), stringList);
            }
            for (int i = 0; i < elements; i++)
                if (i == stringList.size()) {
                    break;
                } else {
                    formatPageSource = formatPageSource + stringList.get(i) + ";";
                }
            return new ConfigProvider().getMD5ByString(formatPageSource);
        } catch (ParserConfigurationException e) {
            Log.logError(e.fillInStackTrace());
        } catch (SAXException e) {
            Log.logError(e.fillInStackTrace());
        } catch (IOException e) {
            Log.logError(e.fillInStackTrace());
        } catch (Exception e) {
            Log.logError(e.fillInStackTrace());
        }
        return null;
    }

    /**
     * 获取当前窗口的所有可点击节点
     *
     * @param pageSource
     */
    public Stack<UiNode> getNodesFromWindow(String pageSource, int depth) {
        InputSource is = new InputSource(new StringReader(pageSource));
        Stack<UiNode> taskStack = new Stack<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            md5 = getCurrentWindowID(pageSource);
            if (doc.hasChildNodes()) {
                parse(doc.getChildNodes(), taskStack, depth);
            }
            return taskStack;
        } catch (Exception e) {
            Log.logError(e.fillInStackTrace());
        }
        return null;
    }

    /**
     * 获取当前窗口的唯一标识符
     *
     * @param pageSource
     * @return
     */
    public String getCurrentWindowID(String pageSource) {
        String[] keys;
        int elements;
        List<String> identifySpecialList = config.getIdentifySpecialList();
        if (null == identifySpecialList) {
            return getWindowIdentify(pageSource, config.getIdentifyDefault());
        } else {
            for (String identifySpecial : identifySpecialList) {
                keys = identifySpecial.split(">>")[0].split(",");
                for (int i = 0; i < keys.length; i++) {
                    if (!pageSource.contains(keys[i])) {
                        break;
                    }
                    if (i == keys.length - 1) {
                        elements = Integer.parseInt(identifySpecial.split(">>")[1]);
                        return getWindowIdentify(pageSource, elements);
                    }
                }
            }
            return getWindowIdentify(pageSource, config.getIdentifyDefault());
        }
    }

    /**
     * 格式化xml
     *
     * @param xmlString
     * @param fileName
     */
    public void xmlFormatter(String xmlString, String fileName) {
        Document doc;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlString)));
            DOMSource src = new DOMSource(doc);
            StreamResult sr = new StreamResult(new File(fileName));
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.METHOD, "xml");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(src, sr);

        } catch (Exception e) {
            Log.logError(e.fillInStackTrace());
        }
    }

    /**
     * 获取窗口的xpath列表
     *
     * @return
     */
    public List<String> getXpathList() {
        return this.xpathList;
    }

    /**
     * 获取节点
     *
     * @param id
     * @param windowID
     * @return
     */
    protected UiNode getUiNode(String id, String windowID, String action, String info, int depth) {
        UiNode node = new UiNode();

        node.setId(id);
        node.setWindowID(windowID);
        node.setDepth(depth);
        node.setAction(action);
        node.setInfo(info);

        return node;
    }

    /**
     * 获取节点属性值信息
     *
     * @param nodeMap
     * @param except
     * @return
     */
    protected String getAttributeInfo(NamedNodeMap nodeMap, String... except) {
        StringWriter sw;
        PrintWriter pw;
        sw = new StringWriter();
        pw = new PrintWriter(sw);
        for (int j = 0; j < nodeMap.getLength(); j++) {
            if (!Arrays.asList(except).contains(nodeMap.item(j).getNodeName()))
                pw.print(nodeMap.item(j).getNodeValue().trim());
        }
        return sw.toString();
    }

    /**
     * 根据属性名获取属性值
     *
     * @param nodeMap
     * @param attributeName
     * @return the value of attribute
     */
    protected String getAttribute(NamedNodeMap nodeMap, String attributeName) {
        Node attrNode;
        attrNode = nodeMap.getNamedItem(attributeName);
        if (null == attrNode)
            return null;
        return attrNode.getNodeValue();
    }
}