package pers.traveler.parser;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pers.traveler.constant.Action;
import pers.traveler.constant.Attribute;
import pers.traveler.entity.Config;
import pers.traveler.entity.UiNode;

import java.util.List;
import java.util.Stack;

/**
 * Created by quqing on 16/5/5.
 */
public class IOSXmlParser extends XmlParser {

    public IOSXmlParser(Config config) {
        super(config);
    }

    @Override
    protected void format(NodeList nodeList, List<String> stringList) {
        String text;
        String attributeInfo;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeMap = node.getAttributes();
                text = node.getTextContent().trim();
                attributeInfo = getAttributeInfo(nodeMap, Attribute.X, Attribute.Y, Attribute.HEIGHT, Attribute.WIDTH);
                stringList.add(text + attributeInfo);
                if (node.hasChildNodes()) {
                    format(node.getChildNodes(), stringList);
                }
            }
        }
    }

    @Override
    protected void parse(NodeList nodeList, Stack<UiNode> taskStack, int depth) {
        UiNode uiNode;
        List<String> clickList = config.getClickList();
        List<String> inputList = config.getInputList();
        List<String> backList = config.getBackList();
        List<String> blackList = config.getBlackList();
        String id, clazz, action, xpath, enabled, visible, valid, x, y, width, height, name, label, value, path, info;

        blackList.addAll(backList);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeMap = node.getAttributes();
                clazz = node.getNodeName();
                enabled = getAttribute(nodeMap, Attribute.ENABLED);
                visible = getAttribute(nodeMap, Attribute.VISIBLE);
                valid = getAttribute(nodeMap, Attribute.VALID);
                xpath = getXPath(node);

                if (null != enabled && enabled.equals("true") && null != visible && visible.equals("true") && null != valid && valid.equals("true")) {
                    xpath = "/" + xpath.substring(xpath.indexOf("/"));
                    xpathList.add(xpath);
                    if (clickList.contains(clazz) || inputList.contains(clazz)) {
                        if (null == blackList || notInBlackList(nodeMap, blackList)) {
                            if (!blackList.contains(xpath)) {
                                x = getAttribute(nodeMap, Attribute.X);
                                y = getAttribute(nodeMap, Attribute.Y);
                                width = getAttribute(nodeMap, Attribute.WIDTH);
                                height = getAttribute(nodeMap, Attribute.HEIGHT);
                                id = md5 + "-" + x + "," + y + "-" + width + "," + height + "-" + xpath;
                                action = inputList.contains(clazz) ? Action.INPUT : Action.CLICK;
                                name = getAttribute(nodeMap, Attribute.NAME);
                                label = getAttribute(nodeMap, Attribute.LABEL);
                                value = getAttribute(nodeMap, Attribute.VALUE);
                                path = getAttribute(nodeMap, Attribute.PATH);
                                info = clazz + "," + name + "," + label + "," + value + "," + path;
                                uiNode = getUiNode(id, md5, action, info, depth);
                                taskStack.push(uiNode);
                            }
                        }
                    }
                }
                if (node.hasChildNodes()) {
                    parse(node.getChildNodes(), taskStack, depth);
                }
            }
        }
    }

    @Override
    protected boolean notInBlackList(NamedNodeMap attributeInfo, List<String> blackList) {
        String label = getAttribute(attributeInfo, Attribute.LABEL);
        String name = getAttribute(attributeInfo, Attribute.NAME);
        String value = getAttribute(attributeInfo, Attribute.VALUE);

        for (String black : blackList) {
            if (!label.isEmpty() && label.contains(black)) {
                return false;
            } else if (!name.isEmpty() && name.contains(black)) {
                return false;
            } else if (!value.isEmpty() && value.contains(black)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param n
     * @return
     */
    @Override
    public String getXPath(Node n) {
        if (null == n)
            return null;

        Node parent;
        Stack<Node> hierarchy = new Stack<Node>();
        StringBuffer buffer = new StringBuffer();

        hierarchy.push(n);

        switch (n.getNodeType()) {
            case Node.ATTRIBUTE_NODE:
                parent = ((Attr) n).getOwnerElement();
                break;
            case Node.ELEMENT_NODE:
                parent = n.getParentNode();
                break;
            case Node.DOCUMENT_NODE:
                parent = n.getParentNode();
                break;
            default:
                throw new IllegalStateException("Unexpected Node type" + n.getNodeType());
        }

        while (null != parent && parent.getNodeType() != Node.DOCUMENT_NODE) {
            hierarchy.push(parent);

            parent = parent.getParentNode();
        }

        Object obj = null;
        while (!hierarchy.isEmpty() && null != (obj = hierarchy.pop())) {
            Node node = (Node) obj;
            boolean handled = false;

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (buffer.length() == 0) {
                    buffer.append(node.getNodeName());
                } else {
                    buffer.append("/");
                    buffer.append(node.getNodeName());

                    if (!handled) {
                        int prev_siblings = 1;
                        Node prev_sibling = node.getPreviousSibling();
                        while (null != prev_sibling) {
                            if (prev_sibling.getNodeType() == node.getNodeType()) {
                                if (prev_sibling.getNodeName().equalsIgnoreCase(
                                        node.getNodeName())) {
                                    prev_siblings++;
                                }
                            }
                            prev_sibling = prev_sibling.getPreviousSibling();
                        }
                        buffer.append("[" + prev_siblings + "]");
                    }
                }
            } else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                buffer.append("/@");
                buffer.append(node.getNodeName());
            }
        }
        return buffer.toString();
    }
}