package myta.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import myta.config.model.SmtpConfiguration;
import myta.core.Engine;
import myta.message.model.Message;
import myta.mime.service.MessageComposer;

public class IncomingMessageProcessor {

    private final Engine engine;

    private static int   idCounter = 0;

    public IncomingMessageProcessor(Engine engine) {

        this.engine = engine;

    }

    public void processIncomingMessage(Message message) {

        MessageComposer messageComposer = new MessageComposer();

        SmtpConfiguration smtpConfiguration = this.engine.getDefaultRelayConfiguration();

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpConfiguration.getHost());

        boolean debug = false;

        Session session = Session.getInstance(props);

        javax.mail.Message mimeMessage = null;

        try {
            mimeMessage = messageComposer.composeMimeMessage(session, message);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (mimeMessage != null) {

            InternetAddress returnAddress = null;

            if (message.getReturnPath() != null) {

                try {
                    returnAddress = new InternetAddress(message.getReturnPath());
                } catch (AddressException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            if (returnAddress != null) {
                props.put("mail.smtp.from", returnAddress.getAddress());
            }

            if (debug) {
                session.setDebug(true);
            }

            // send the message
            try {
                Transport.send(mimeMessage);
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public static String getUniqueMessageIDValue(Session session) {

        String suffix = null;

        InternetAddress addr = InternetAddress.getLocalAddress(session);
        if (addr != null)
            suffix = addr.getAddress();
        else {
            suffix = "myta@localhost"; // worst-case default
        }

        StringBuffer s = new StringBuffer();

        // Unique string is <hashcode>.<id>.<currentTime>.JavaMail.<suffix>
        s.append(s.hashCode()).append('.').append(idCounter++).append(System.currentTimeMillis()).append('.').append("MyTA.").append(suffix);
        return s.toString();

    }
}
