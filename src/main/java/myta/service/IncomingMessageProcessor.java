package myta.service;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final Engine        engine;

    private final AtomicInteger numMailsSent;

    public IncomingMessageProcessor(Engine engine) {

        this.engine = engine;
        this.numMailsSent = new AtomicInteger();

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
                this.numMailsSent.incrementAndGet();

            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public int getNumMailsSent() {
        return this.numMailsSent.get();
    }

}
