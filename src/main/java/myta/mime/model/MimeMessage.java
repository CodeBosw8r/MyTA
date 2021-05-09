package myta.mime.model;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;

public class MimeMessage extends jakarta.mail.internet.MimeMessage {
    Session            session;
    private static int id = 0;

    public MimeMessage(Session session) {
        super(session);
        this.session = session;
    }

    @Override
    protected void updateMessageID() throws MessagingException {

        String messageId = null;

        if (this.getAllHeaders() != null) {

            String[] header = null;

            try {
                header = this.getHeader("Message-ID");
            } catch (Exception e) {
            }

            if ((header != null) && (header.length > 0)) {

                messageId = header[0];

            }

        }

        if (messageId == null) {

            messageId = "<" + getUniqueMessageIDValue(session) + ">";

        }

        setHeader("Message-ID", messageId);
    }

    public static String getUniqueMessageIDValue(Session ssn) {
        String suffix = null;

        InternetAddress addr = InternetAddress.getLocalAddress(ssn);
        if (addr != null)
            suffix = addr.getAddress();
        else {
            suffix = "myta@localhost"; // worst-case default
        }

        StringBuffer s = new StringBuffer();

        // Unique string is <hashcode>.<id>.<currentTime>.JavaMail.<suffix>
        s.append(s.hashCode()).append('.').append(getUniqueId()).append('.').append(System.currentTimeMillis()).append('.').append("MyTA.").append(suffix);

        return s.toString();

    }

    private static synchronized int getUniqueId() {
        return id++;
    }

}