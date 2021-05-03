package myta.message.model;

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

    private static final long  serialVersionUID = 3528943017570062648L;

    private EmailAddress       from;

    private List<Recipient>    recipients;

    private String             subject;

    private String             htmlBody;

    private String             textBody;

    private List<EmailAddress> replyToAddresses;

    private List<Header>       extraHeaders;

    private String             returnPath;

    public EmailAddress getFrom() {
        return from;
    }

    public void setFrom(EmailAddress from) {
        this.from = from;
    }

    public List<Recipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<Recipient> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public List<EmailAddress> getReplyToAddresses() {
        return replyToAddresses;
    }

    public void setReplyToAddresses(List<EmailAddress> replyToAddresses) {
        this.replyToAddresses = replyToAddresses;
    }

    public List<Header> getExtraHeaders() {
        return extraHeaders;
    }

    public void setExtraHeaders(List<Header> extraHeaders) {
        this.extraHeaders = extraHeaders;
    }

    public String getReturnPath() {
        return returnPath;
    }

    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }

}
