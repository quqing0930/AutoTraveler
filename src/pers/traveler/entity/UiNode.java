package pers.traveler.entity;

import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Created by quqing on 16/5/4.
 */
public class UiNode {
    private boolean hasVisited = false;

    private int depth;

    private String id;

    private String windowID;

    private String action;

    private String info;

    private UiNode leftNode = null;

    private WebElement element;

    private List<UiNode> rightNode = null;

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getWindowID() {
        return windowID;
    }

    public void setWindowID(String windowID) {
        this.windowID = windowID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UiNode getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(UiNode leftNode) {
        this.leftNode = leftNode;
    }

    public List<UiNode> getRightNode() {
        return rightNode;
    }

    public void setRightNode(List<UiNode> rightNode) {
        this.rightNode = rightNode;
    }

    public boolean isHasVisited() {
        return hasVisited;
    }

    public void setHasVisited(boolean hasVisited) {
        this.hasVisited = hasVisited;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public WebElement getElement() {
        return element;
    }

    public void setElement(WebElement element) {
        this.element = element;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}