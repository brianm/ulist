package org.skife.ulist;

import com.sun.mail.smtp.SMTPMessage;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class SMTPDeliverator implements Deliverator
{

    private final InetSocketAddress outboundAddress;

    public SMTPDeliverator(InetSocketAddress outboundAddress)
    {
        this.outboundAddress = outboundAddress;
    }

    public void deliver(String from, String recipient, SMTPMessage msg) throws EmailException, MessagingException, IOException
    {
        Email email = new SimpleEmail();

        email.setHostName(outboundAddress.getAddress().getHostName());
        email.setSmtpPort(outboundAddress.getPort());

        email.setFrom(from);
        email.setSubject(msg.getSubject());

        email.setContent(msg.getContent(), msg.getContentType());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        msg.writeTo(out);

        email.setMsg(new String(out.toByteArray()));
        email.addTo(recipient);
        email.send();

    }
}
