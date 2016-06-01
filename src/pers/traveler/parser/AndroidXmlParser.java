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
public class AndroidXmlParser extends XmlParser {

    public AndroidXmlParser(Config config) {
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
                attributeInfo = getAttributeInfo(nodeMap, Attribute.BOUNDS);
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
        List<String> blackList = config.getBlackList();
        String id, clazz, action, clickable, enabled, xpath, bounds, location, size, text, content_desc, resourceId, info;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap nodeMap = node.getAttributes();
                clazz = getAttribute(nodeMap, Attribute.CLAZZ);
                clickable = getAttribute(nodeMap, Attribute.CLICKABLE);
                enabled = getAttribute(nodeMap, Attribute.ENABLED);

                xpath = getXPath(node);
                if (null != clickable && clickable.equals("true") && null != enabled && enabled.equals("true")) {
                    if (clickList.contains(clazz) || inputList.contains(clazz)) {
                        if (null == blackList || notInBlackList(nodeMap, blackList)) {
                            bounds = getAttribute(nodeMap, Attribute.BOUNDS);
                            location = bounds.substring(bounds.indexOf("[") + 1, bounds.indexOf("]"));
                            size = bounds.substring(location.length() + 3, bounds.length() - 1);
                            id = md5 + "-" + location + "-" + size + "-" + xpath;
                            action = inputList.contains(clazz) ? Action.INPUT : Action.CLICK;
                            text = getAttribute(nodeMap, Attribute.TEXT);
                            content_desc = getAttribute(nodeMap, Attribute.CONTENT_DESC);
                            resourceId = getAttribute(nodeMap, Attribute.RESOURCE_ID);
                            info = clazz + "," + text + "," + content_desc + "," + resourceId;
                            uiNode = getUiNode(id, md5, action, info, depth);
                            taskStack.push(uiNode);
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
        String text = getAttribute(attributeInfo, Attribute.TEXT);
        String content_desc = getAttribute(attributeInfo, Attribute.CONTENT_DESC);
        String resourceId = getAttribute(attributeInfo, Attribute.RESOURCE_ID);

        for (int i = 0; i < blackList.size(); i++) {
            if (!text.isEmpty() && text.contains(blackList.get(i))) {
                return false;
            } else if (!content_desc.isEmpty() && content_desc.contains(blackList.get(i))) {
                return false;
            } else if (!resourceId.isEmpty() && blackList.get(i).equals(resourceId)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getXPath(Node n) {
        String clazz1;
        String clazz2;
        NamedNodeMap nodeMap;

        if (null == n)
            return null;

        Node parent = null;
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
                break;
            default:
                throw new IllegalStateException("Unexpected Node type" + n.getNodeType());
        }

        while (null != parent && parent.getNodeType() != Node.DOCUMENT_NODE) {
            hierarchy.push(parent);
            parent = parent.getParentNode();
        }

        Object obj;
        while (!hierarchy.isEmpty() && null != (obj = hierarchy.pop())) {
            Node node = (Node) obj;
            boolean handled = false;

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodeMap = node.getAttributes();
                clazz1 = getAttribute(nodeMap, "class");

                if (buffer.length() == 0) {
                    buffer.append(clazz1);
                } else {
                    buffer.append("/");
                    buffer.append(clazz1);

                    if (!handled) {
                        int prev_siblings = 1;
                        Node prev_sibling = node.getPreviousSibling();
                        while (null != prev_sibling) {
                            if (prev_sibling.getNodeType() == node.getNodeType()) {
                                nodeMap = prev_sibling.getAttributes();
                                clazz2 = getAttribute(nodeMap, "class");
                                if (clazz2.equalsIgnoreCase(clazz1)) {
                                    prev_siblings++;
                                }
                            }
                            prev_sibling = prev_sibling.getPreviousSibling();
                        }
                        buffer.append("[" + prev_siblings + "]");
                    }
                }
            } else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                nodeMap = node.getAttributes();
                clazz1 = getAttribute(nodeMap, "class");
                buffer.append("/@");
                buffer.append(clazz1);
            }
        }
        return buffer.toString().replaceAll("null", "/");
    }
}