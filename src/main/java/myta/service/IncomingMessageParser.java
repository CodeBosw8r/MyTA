package myta.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import myta.exception.MessageParseException;
import myta.message.model.EmailAddress;
import myta.message.model.Header;
import myta.message.model.Message;
import myta.message.model.Recipient;
import myta.message.model.RecipientType;

public class IncomingMessageParser {

    public Message parseMessageRequest(Map<String, Object> requestMap) throws MessageParseException {

        EmailAddress from = this.parseFrom(requestMap);
        List<Recipient> recipients = this.parseRecipients(requestMap);
        String subject = this.parseSubject(requestMap);
        String textBody = this.parseTextBody(requestMap);
        String htmlBody = this.parseHtmlBody(requestMap);
        List<EmailAddress> replyToAddresses = this.parseReplyToAddresses(requestMap);
        List<Header> extraHeaders = this.parseExtraHeaders(requestMap);
        String returnPath = this.parseReturnPath(requestMap);

        if (recipients == null) {

            throw new MessageParseException("No recipients in message");

        }

        if ((textBody == null) && (htmlBody == null)) {

            throw new MessageParseException("Either textBody or htmlBody must be set");

        }

        Message message = new Message();
        message.setFrom(from);
        message.setRecipients(recipients);
        message.setSubject(subject);
        message.setTextBody(textBody);
        message.setHtmlBody(htmlBody);
        message.setReplyToAddresses(replyToAddresses);
        message.setExtraHeaders(extraHeaders);
        message.setReturnPath(returnPath);

        return message;

    }

    public List<Recipient> parseRecipients(Map<String, Object> requestMap) throws MessageParseException {

        List<Recipient> recipients = null;

        if (requestMap != null) {

            Collection<Recipient> recipientsTo = null;
            Collection<Recipient> recipientsCc = null;
            Collection<Recipient> recipientsBcc = null;
            int totalRecipients = 0;

            if (requestMap.containsKey("recipients")) {

                Object recipientsObject = requestMap.get("recipients");

                if (recipientsObject instanceof List) {

                    @SuppressWarnings("unchecked")
                    List<Object> recipientsList = (List<Object>) recipientsObject;

                    for (Object recipientObject : recipientsList) {

                        if (recipientObject instanceof Map) {

                            @SuppressWarnings("unchecked")
                            Map<String, Object> recipientMap = (Map<String, Object>) recipientObject;

                            Recipient parsedRecipient = this.parseRecipient(recipientMap);

                            if (parsedRecipient != null) {

                                if (parsedRecipient.getRecipientType().equals(RecipientType.TO)) {

                                    totalRecipients += 1;

                                    if (recipientsTo == null) {
                                        recipientsTo = new ArrayList<Recipient>(1);
                                    }

                                    recipientsTo.add(parsedRecipient);

                                } else if (parsedRecipient.getRecipientType().equals(RecipientType.CC)) {

                                    totalRecipients += 1;

                                    if (recipientsCc == null) {
                                        recipientsCc = new ArrayList<Recipient>(1);
                                    }

                                    recipientsCc.add(parsedRecipient);

                                } else if (parsedRecipient.getRecipientType().equals(RecipientType.BCC)) {

                                    totalRecipients += 1;

                                    if (recipientsBcc == null) {
                                        recipientsBcc = new ArrayList<Recipient>(1);
                                    }

                                    recipientsBcc.add(parsedRecipient);

                                }

                            } else {

                                throw new MessageParseException("Could not parse recipient");

                            }

                        } else {

                            throw new MessageParseException("Unexpected recipients type, expected map");

                        }

                    }

                } else {

                    throw new MessageParseException("Unexpected recipients type, expected list");

                }

            } else {

                if (requestMap.containsKey("to")) {

                    Object toValue = requestMap.get("to");

                    if (toValue != null) {

                        Collection<EmailAddress> emailAddresses = this.parseRecipientEmailAddresses(toValue);

                        if ((emailAddresses != null) && (emailAddresses.size() > 0)) {

                            totalRecipients += emailAddresses.size();

                            if (recipientsTo == null) {
                                recipientsTo = new ArrayList<Recipient>(emailAddresses.size());
                            }

                            for (EmailAddress emailAddress : emailAddresses) {

                                Recipient recipient = new Recipient(RecipientType.TO, emailAddress);
                                recipientsTo.add(recipient);

                            }

                        }

                    }

                }

                if (requestMap.containsKey("cc")) {

                    Object ccValue = requestMap.get("cc");

                    if (ccValue != null) {

                        Collection<EmailAddress> emailAddresses = this.parseRecipientEmailAddresses(ccValue);

                        if ((emailAddresses != null) && (emailAddresses.size() > 0)) {

                            totalRecipients += emailAddresses.size();

                            if (recipientsCc == null) {
                                recipientsCc = new ArrayList<Recipient>(emailAddresses.size());
                            }

                            for (EmailAddress emailAddress : emailAddresses) {

                                Recipient recipient = new Recipient(RecipientType.CC, emailAddress);
                                recipientsCc.add(recipient);

                            }

                        }

                    }

                }

                if (requestMap.containsKey("bcc")) {

                    Object bccValue = requestMap.get("bcc");

                    if (bccValue != null) {

                        Collection<EmailAddress> emailAddresses = this.parseRecipientEmailAddresses(bccValue);

                        if ((emailAddresses != null) && (emailAddresses.size() > 0)) {

                            totalRecipients += emailAddresses.size();

                            if (recipientsBcc == null) {
                                recipientsBcc = new ArrayList<Recipient>(emailAddresses.size());
                            }

                            for (EmailAddress emailAddress : emailAddresses) {

                                Recipient recipient = new Recipient(RecipientType.BCC, emailAddress);
                                recipientsBcc.add(recipient);

                            }

                        }

                    }

                }

            }

            if (totalRecipients > 0) {

                recipients = new ArrayList<Recipient>(totalRecipients);

                if (recipientsTo != null) {

                    recipients.addAll(recipientsTo);

                }

                if (recipientsCc != null) {

                    recipients.addAll(recipientsCc);

                }

                if (recipientsBcc != null) {

                    recipients.addAll(recipientsBcc);

                }

            }

        }

        return recipients;

    }

    private String parseSubject(Map<String, Object> requestMap) {

        String subject = this.getStringValue(requestMap, "subject");

        return subject;

    }

    private String parseTextBody(Map<String, Object> requestMap) {

        String subject = this.getStringValue(requestMap, "textBody");

        return subject;

    }

    private String parseHtmlBody(Map<String, Object> requestMap) {

        String subject = this.getStringValue(requestMap, "htmlBody");

        return subject;

    }

    private EmailAddress parseFrom(Map<String, Object> requestMap) throws MessageParseException {

        EmailAddress from = null;

        String fromValue = this.getStringValue(requestMap, "from");

        if (fromValue != null) {

            EmailAddress emailAddress = this.parseEmailAddress(fromValue);

            if (emailAddress != null) {

                from = emailAddress;

            }

        } else {

            throw new MessageParseException("No from field in message");

        }

        return from;

    }

    private String getStringValue(Map<String, Object> requestMap, String key) {

        String value = null;

        if ((requestMap != null) && requestMap.containsKey(key) && (requestMap.get(key) != null)) {

            value = requestMap.get(key).toString();

        }

        return value;

    }

    public List<Header> parseExtraHeaders(Map<String, Object> requestMap) throws MessageParseException {

        List<Header> extraHeaders = null;

        if ((requestMap != null) && requestMap.containsKey("extraHeaders") && (requestMap.get("extraHeaders") != null)) {

            Object extraHeadersObject = requestMap.get("extraHeaders");

            if (extraHeadersObject instanceof List) {

                @SuppressWarnings("unchecked")
                List<Object> extraHeadersList = (List<Object>) extraHeadersObject;

                for (Object extraHeaderObject : extraHeadersList) {

                    if (extraHeaderObject instanceof Map) {

                        @SuppressWarnings("unchecked")
                        Map<String, Object> extraHeaderMap = (Map<String, Object>) extraHeaderObject;

                        if (extraHeaderMap.containsKey("name") && (extraHeaderMap.get("name") != null)) {

                            if (extraHeaderMap.containsKey("value") && (extraHeaderMap.get("value") != null)) {

                                String name = extraHeaderMap.get("name").toString();
                                String value = extraHeaderMap.get("value").toString();

                                Header header = new Header(name, value);

                                if (extraHeaders == null) {

                                    extraHeaders = new ArrayList<Header>(extraHeadersList.size());

                                }

                                extraHeaders.add(header);

                            } else {

                                throw new MessageParseException("Could not parse extra header: value expected");

                            }

                        } else {

                            throw new MessageParseException("Could not parse extra header: name expected");

                        }

                    } else {

                        throw new MessageParseException("Unexpected recipients type, expected map");

                    }

                }

            } else {

                throw new MessageParseException("Unexpected recipients type, expected list");

            }

        }

        if (extraHeaders == null) {

            extraHeaders = new ArrayList<Header>(0);

        }

        return extraHeaders;

    }

    public Header parseHeader(String headerValue) throws MessageParseException {

        return null;

    }

    public String parseReturnPath(Map<String, Object> requestMap) throws MessageParseException {

        String returnPath = null;

        String returnPathParam = this.getStringValue(requestMap, "returnPath");

        if (returnPathParam != null) {

            EmailAddress parsedEmailAddress = this.parseEmailAddress(returnPathParam);

            if (parsedEmailAddress != null) {

                returnPath = parsedEmailAddress.getEmail();

            }

        }

        return returnPath;

    }

    public List<EmailAddress> parseRecipientEmailAddresses(Object recipientValue) throws MessageParseException {

        List<EmailAddress> emailAddresses = null;

        if (recipientValue instanceof String) {

            EmailAddress emailAddress = this.parseEmailAddress(recipientValue.toString());

            if (emailAddresses == null) {

                emailAddresses = new ArrayList<EmailAddress>(1);

            }

            emailAddresses.add(emailAddress);

        } else if (recipientValue instanceof List<?>) {

            @SuppressWarnings("unchecked")
            List<Object> recipientList = (List<Object>) recipientValue;

            for (Object recipientPart : recipientList) {

                if (recipientPart != null) {

                    List<EmailAddress> parsedAddresses = this.parseRecipientEmailAddresses(recipientPart);

                    if (parsedAddresses != null) {

                        if (emailAddresses == null) {

                            emailAddresses = new ArrayList<EmailAddress>(recipientList.size());

                        }

                        emailAddresses.addAll(parsedAddresses);

                    }

                }

            }

        }

        return emailAddresses;

    }

    public Recipient parseRecipient(Map<String, Object> recipientMap) throws MessageParseException {

        Recipient recipient = null;

        if (recipientMap != null) {

            String type = null;
            String email = null;

            RecipientType recipientType = null;
            EmailAddress emailAddress = null;

            if (recipientMap.containsKey("type") && (recipientMap.get("type") != null)) {

                type = recipientMap.get("type").toString();

            }

            if (recipientMap.containsKey("email") && (recipientMap.get("email") != null)) {

                email = recipientMap.get("email").toString();

            }

            if (type != null) {

                recipientType = this.parseRecipientType(type);

            }

            if (email != null) {

                emailAddress = this.parseEmailAddress(email);

            }

            if ((recipientType != null) && (emailAddress != null)) {

                recipient = new Recipient(recipientType, emailAddress);

            } else {

                throw new MessageParseException("Email and recipient type missing in recipient");

            }

        } else {

            throw new MessageParseException("No recipient map null pointer");

        }

        return recipient;

    }

    public EmailAddress parseEmailAddress(String email) throws MessageParseException {

        EmailAddress emailAddress = null;

        if ((email != null) && (!email.equals(""))) {

            InternetAddress internetAddress = null;

            try {

                internetAddress = new InternetAddress(email);

            } catch (AddressException e) {

                throw new MessageParseException("Cannot parse email address " + email + ": " + e.getMessage());

            }

            if (internetAddress != null) {

                String address = internetAddress.getAddress();
                String personal = null;

                if ((internetAddress.getPersonal() != null) && !internetAddress.getPersonal().equals("")) {
                    personal = internetAddress.getPersonal();
                }

                emailAddress = new EmailAddress(address, personal);

            }

        } else {

            throw new MessageParseException("Empty email address");

        }

        return emailAddress;

    }

    public RecipientType parseRecipientType(String type) throws MessageParseException {

        RecipientType recipientType = null;

        if ((type != null) && (!type.equals(""))) {

            if (type.equals("to")) {

                recipientType = RecipientType.TO;

            } else if (type.equals("cc")) {

                recipientType = RecipientType.CC;

            } else if (type.equals("bcc")) {

                recipientType = RecipientType.BCC;

            } else {

                throw new MessageParseException("Unknown recipient type: " + recipientType);

            }

        } else {

            throw new MessageParseException("Empty recipient type");

        }

        return recipientType;

    }

    public List<EmailAddress> parseReplyToAddresses(Map<String, Object> requestMap) throws MessageParseException {

        List<EmailAddress> replyToAddresses = null;

        if (requestMap != null) {

            if (requestMap.containsKey("replyTo")) {

                Object replyToValue = requestMap.get("replyTo");

                if (replyToValue != null) {

                    if (replyToValue instanceof String) {

                        EmailAddress parsedEmailAddress = this.parseEmailAddress(replyToValue.toString());

                        if (parsedEmailAddress != null) {

                            replyToAddresses = new ArrayList<EmailAddress>(1);
                            replyToAddresses.add(parsedEmailAddress);

                        }

                    } else if (replyToValue instanceof List) {

                        @SuppressWarnings("unchecked")
                        List<Object> replyToList = (List<Object>) replyToValue;

                        if (replyToList.size() > 0) {

                            for (Object replyToListItem : replyToList) {

                                if ((replyToListItem != null) && (replyToListItem instanceof String)) {

                                    EmailAddress parsedEmailAddress = this.parseEmailAddress(replyToListItem.toString());

                                    if (parsedEmailAddress != null) {

                                        if (replyToAddresses == null) {

                                            replyToAddresses = new ArrayList<EmailAddress>(replyToList.size());

                                        }

                                        replyToAddresses.add(parsedEmailAddress);

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

        if (replyToAddresses == null) {
            replyToAddresses = new ArrayList<EmailAddress>(0);
        }

        return replyToAddresses;

    }

}
