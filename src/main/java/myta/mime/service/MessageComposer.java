package myta.mime.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import myta.message.model.EmailAddress;
import myta.message.model.Header;
import myta.message.model.Recipient;
import myta.message.model.RecipientType;
import myta.mime.model.MimeMessage;

public class MessageComposer {

    public Message composeMimeMessage(Session session, myta.message.model.Message message) throws MessagingException, UnsupportedEncodingException {

        String textBody = message.getTextBody();
        String htmlBody = message.getHtmlBody();
        String messageId = null;

        MimeMessage msg = new MimeMessage(session);

        if ((textBody != null) && (htmlBody != null)) {

            Multipart body = this.createMultipartAlternative(textBody, htmlBody);
            msg.setContent(body);

        } else if (htmlBody != null) {

            // set content
            msg.setContent(htmlBody, "text/html");

        } else {

            // assume text-only
            if (textBody != null) {
                msg.setContent(textBody, "text/plain");
            } else {
                msg.setContent("", "text/plain");
            }

        }

        if (message.getFrom() != null) {

            InternetAddress from = new InternetAddress(message.getFrom().getEmail(), message.getFrom().getName());
            msg.setFrom(from);

        }

        if (message.getSubject() != null) {

            msg.setSubject(message.getSubject());

        }

        if (message.getRecipients() != null) {

            List<RecipientType> recipientTypes = new ArrayList<RecipientType>();
            recipientTypes.add(RecipientType.TO);
            recipientTypes.add(RecipientType.CC);
            recipientTypes.add(RecipientType.BCC);

            for (RecipientType recipientType : recipientTypes) {

                List<InternetAddress> recipients = new ArrayList<InternetAddress>();

                for (Recipient recipient : message.getRecipients()) {

                    if (recipient.getRecipientType().equals(recipientType)) {

                        InternetAddress address = null;

                        address = new InternetAddress(recipient.getEmailAddress().getEmail(), recipient.getEmailAddress().getName());

                        recipients.add(address);

                    }

                }

                if (recipients.size() > 0) {

                    javax.mail.Message.RecipientType addRecipientType = null;

                    if (recipientType.equals(RecipientType.TO)) {

                        addRecipientType = javax.mail.Message.RecipientType.TO;

                    } else if (recipientType.equals(RecipientType.CC)) {

                        addRecipientType = javax.mail.Message.RecipientType.CC;

                    } else if (recipientType.equals(RecipientType.BCC)) {

                        addRecipientType = javax.mail.Message.RecipientType.BCC;

                    }

                    if (addRecipientType != null) {

                        Address[] addresses = new Address[recipients.size()];

                        for (int i = 0; i < recipients.size(); i++) {
                            addresses[i] = recipients.get(i);
                        }

                        msg.addRecipients(addRecipientType, addresses);

                    }

                }

            }

        }

        if ((message.getReplyToAddresses() != null) && (message.getReplyToAddresses().size() > 0)) {

            List<String> addresses = new ArrayList<String>(message.getReplyToAddresses().size());

            for (EmailAddress emailAddress : message.getReplyToAddresses()) {

                addresses.add(emailAddress.toString());

            }

            String replyTo = String.join(", ", addresses);

            if (!replyTo.equals("")) {

                msg.addHeader("Reply-To", replyTo);

            }

        }

        if ((message.getExtraHeaders() != null) && (message.getExtraHeaders().size() > 0)) {

            for (Header extraHeader : message.getExtraHeaders()) {

                if (extraHeader.getName().equals("Message-ID")) {

                    messageId = extraHeader.getValue();

                } else {

                    msg.addHeader(extraHeader.getName(), extraHeader.getValue());

                }

            }

        }

        msg.saveChanges();

        if (messageId != null) {

            msg.setHeader("Message-ID", messageId);

        }

        return msg;

    }

    public Multipart createMultipartAlternative(String textbody, String htmlbody) throws MessagingException {

        Multipart mp = new MimeMultipart("alternative");

        // create and fill the text message part
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(textbody);
        mbp1.setContent(textbody, "text/plain");

        mp.addBodyPart(mbp1);

        // create and fill the html message part
        MimeBodyPart mbp2 = new MimeBodyPart();
        mbp2.setContent(htmlbody, "text/html");

        mp.addBodyPart(mbp2);

        return mp;

    }

    public Multipart createMultipartMixed(Multipart message, MimeBodyPart attachment) throws MessagingException {

        MimeMultipart outer = new MimeMultipart("mixed");

        // wrap multipart in bodypart
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setContent(message);

        // add wrapped multipart
        outer.addBodyPart(mbp);

        // add attachment
        outer.addBodyPart(attachment);

        return outer;

    }

}
