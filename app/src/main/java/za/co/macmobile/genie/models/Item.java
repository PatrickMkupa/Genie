package za.co.macmobile.genie.models;

import android.graphics.drawable.Drawable;

public class Item {
    private  String name;
    private String resource;
    private boolean isSelected;

    public Item( String resource,String name) {
        this.name = name;
        this.resource = resource;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
