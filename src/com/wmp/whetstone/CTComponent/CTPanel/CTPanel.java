package com.wmp.whetstone.CTComponent.CTPanel;

public abstract class CTPanel {
    private String name = "CTPanel";
    private String ID = "CTPanel";
    private boolean visible = true;

    public CTPanel() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public abstract void refresh() throws Exception;


}
