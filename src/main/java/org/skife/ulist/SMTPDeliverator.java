package org.skife.ulist;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.message.Body;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.message.Multipart;
import org.apache.james.mime4j.message.SingleBody;

import java.net.InetSocketAddress;

public class SMTPDeliverator implements Deliverator
{

    private final InetSocketAddress outboundAddress;

    public SMTPDeliverator(InetSocketAddress outboundAddress)
    {
        this.outboundAddress = outboundAddress;
    }

    public void deliver(Mailbox from, Mailbox recipient, Message msg) throws EmailException
    {
        Email email = new SimpleEmail();
        email.setHostName(outboundAddress.getAddress().getHostName());
        email.setSmtpPort(outboundAddress.getPort());

        email.setFrom(from.getAddress());
        email.setSubject(msg.getSubject());


        Body body = msg.getBody();
        if (body instanceof Message) {

        }
        else if (body instanceof Multipart)
        {

        }
        else if (body instanceof SingleBody)

        email.setMsg(msg.getBody().toString());
        email.addTo(recipient.getAddress());
        email.send();

    }
}
