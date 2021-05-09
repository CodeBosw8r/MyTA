package myta.dkim.model;

import java.security.interfaces.RSAPrivateKey;

public class DkimKey {

    private String        domain;

    private String        selector;

    private RSAPrivateKey privateKey;

    public DkimKey(String domain, String selector, RSAPrivateKey privateKey) {
        super();
        this.domain = domain;
        this.selector = selector;
        this.privateKey = privateKey;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

}
