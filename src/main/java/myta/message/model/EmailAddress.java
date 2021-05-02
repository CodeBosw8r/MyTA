package myta.message.model;

import java.io.Serializable;

public class EmailAddress implements Serializable {

    private static final long serialVersionUID = 8136581631661777702L;

    private String            email;

    private String            name;

    public EmailAddress(String email, String name) {

        this.email = email;
        this.name = name;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {

        return this.name != null ? (this.name + " <" + this.email + ">") : this.email;

    }

}
