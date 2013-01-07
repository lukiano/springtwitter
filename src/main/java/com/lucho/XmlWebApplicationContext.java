package com.lucho;

/**
 */
public class XmlWebApplicationContext extends org.springframework.web.context.support.XmlWebApplicationContext {

    private final XmlWebApplicationContext parent;

    public XmlWebApplicationContext(final XmlWebApplicationContext parent) {
        this.parent = parent;
        this.setParent(parent);
    }

    protected void onClose() {
        this.parent.close();
    }
}
