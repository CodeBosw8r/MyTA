package myta.service;

import java.io.UnsupportedEncodingException;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import myta.config.model.SmtpConfiguration;
import myta.core.Engine;
import myta.dkim.model.DkimKey;
import myta.message.model.Message;
import myta.mime.service.MessageComposer;
import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.DkimMessage;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;

public class IncomingMessageProcessor {

    private final Engine        engine;

    private final AtomicInteger numMailsSent;

    public IncomingMessageProcessor(Engine engine) {

        this.engine = engine;
        this.numMailsSent = new AtomicInteger();

    }

    public void processIncomingMessage(Message message) {

        MessageComposer messageComposer = new MessageComposer();

        List<SmtpConfiguration> relayServers = this.engine.getRelayServers();

        int luckyNumber = 0;

        if (relayServers.size() > 1) {

            luckyNumber = new Random().nextInt(relayServers.size());

        }

        SmtpConfiguration smtpConfiguration = relayServers.get(luckyNumber);

        Properties props = new Properties();
        props.put("mail.smtp.host", smtpConfiguration.getHost());

        boolean debug = false;

        Session session = Session.getInstance(props);

        MimeMessage mimeMessage = null;

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

            String from = message.getFrom().getEmail();

            DkimKey dkimKey = this.determineDkimKey(from, engine.getDkimKeyMapping());

            if (dkimKey != null) {

                MimeMessage dkimSignedMessage = null;

                try {
                    dkimSignedMessage = dkimSignMessage(mimeMessage, from, dkimKey);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (dkimSignedMessage != null) {

                    mimeMessage = dkimSignedMessage;

                }

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

    private MimeMessage dkimSignMessage(MimeMessage message, String from, DkimKey dkimKey) throws Exception {

        String signingDomain = dkimKey.getDomain();
        String selector = dkimKey.getSelector();
        RSAPrivateKey privateKey = dkimKey.getPrivateKey();

        DkimSigner dkimSigner = new DkimSigner(signingDomain, selector, privateKey);
        dkimSigner.setIdentity(from);
        dkimSigner.setHeaderCanonicalization(Canonicalization.SIMPLE);
        dkimSigner.setBodyCanonicalization(Canonicalization.RELAXED);
        dkimSigner.setSigningAlgorithm(SigningAlgorithm.SHA256_WITH_RSA);
        dkimSigner.setLengthParam(true);
        dkimSigner.setCopyHeaderFields(false);
        return new DkimMessage(message, dkimSigner);
    }

    public DkimKey determineDkimKey(String from, Map<String, DkimKey> dkimKeyMapping) {

        DkimKey dkimKey = null;

        if ((from != null) && (dkimKeyMapping != null)) {

            if (dkimKeyMapping.containsKey(from)) {

                dkimKey = dkimKeyMapping.get(from);

            }

            if (dkimKey == null) {

                if (from.contains("@")) {

                    String key = "*" + from.substring(from.indexOf("@"));

                    if (dkimKeyMapping.containsKey(key)) {

                        dkimKey = dkimKeyMapping.get(key);

                    }

                }

            }

        }

        return dkimKey;

    }

    public int getNumMailsSent() {
        return this.numMailsSent.get();
    }

}
