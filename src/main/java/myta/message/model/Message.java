package myta.message.model;

import java.util.List;

public class Message {

    private List<Recipient>    recipients;

    private String             subject;

    private String             htmlBody;

    private String             textBody;

    private List<EmailAddress> replyTo;

    private List<Header>       headers;

    private String             returnPath;

}
