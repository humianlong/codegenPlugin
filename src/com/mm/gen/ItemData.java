package com.mm.gen;

public class ItemData {

    private Boolean check;

    private String templateName;

    private String type;

    public ItemData() {
    }

    public ItemData(Boolean check, String templateName, String type) {
        this.check = check;
        this.templateName = templateName;
        this.type = type;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
