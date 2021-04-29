package myta.message.model;

public class Recipient {

    private RecipientType recipientType;

    private EmailAddress  emailAddress;

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

}
