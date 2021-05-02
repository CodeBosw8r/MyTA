package myta.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import myta.exception.MessageParseException;
import myta.message.model.EmailAddress;
import myta.message.model.Message;
import myta.message.model.Recipient;
import myta.message.model.RecipientType;

public class IncomingMessageParser {

    public Message parseMessageRequest(Map<String, Object> requestMap) throws MessageParseException {

        return null;

    }

    public Collection<Recipient> parseRecipients(Map<String, Object> requestMap) throws MessageParseException {

        Collection<Recipient> recipients = null;

        if (requestMap != null) {

            if (requestMap.containsKey("recipients")) {

            } else {

                Collection<Recipient> recipientsTo = null;
                Collection<Recipient> recipientsCc = null;
                Collection<Recipient> recipientsBcc = null;
                int totalRecipients = 0;

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

        }

        return null;

    }

    public Collection<EmailAddress> parseRecipientEmailAddresses(Object recipientValue) throws MessageParseException {

        Collection<EmailAddress> emailAddresses = null;

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

                    Collection<EmailAddress> parsedAddresses = this.parseRecipientEmailAddresses(recipientPart);

                    if (parsedAddresses != null) {

                        emailAddresses = parsedAddresses;

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

}
