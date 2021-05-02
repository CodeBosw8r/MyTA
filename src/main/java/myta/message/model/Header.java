package myta.message.model;

import java.io.Serializable;

public class Header implements Serializable {

    private static final long serialVersionUID = 5299391230670261560L;

    private String            name;

    private String            value;

    public Header(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
