package myta.message.model;

import java.io.Serializable;

public class Recipient implements Serializable {

    private static final long serialVersionUID = 4906944885986379385L;

    private RecipientType     recipientType;

    private EmailAddress      emailAddress;

    public Recipient(RecipientType recipientType, String email) {
        super();
        this.recipientType = recipientType;
        this.emailAddress = new EmailAddress(email);
    }

    public Recipient(RecipientType recipientType, EmailAddress emailAddress) {
        super();
        this.recipientType = recipientType;
        this.emailAddress = emailAddress;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {

        return this.recipientType.toString() + ": " + this.emailAddress.toString();

    }

}
