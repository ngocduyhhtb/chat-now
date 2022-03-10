package com.nduy.realtimechatapp.Model;

public class UserAction {
    private String actionName;
    private int iconID;

    public UserAction(String actionName, int iconID) {
        this.actionName = actionName;
        this.iconID = iconID;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public int getIconID() {
        return iconID;
    }

    public void setIconID(int iconID) {
        this.iconID = iconID;
    }
}
