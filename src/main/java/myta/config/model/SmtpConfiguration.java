package myta.config.model;

public class SmtpConfiguration {

    private String host;

    public SmtpConfiguration(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
