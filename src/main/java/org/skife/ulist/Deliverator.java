package org.skife.ulist;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.message.Message;

import java.net.InetSocketAddress;

public class Deliverator
{

    private final InetSocketAddress outboundAddress;

    public Deliverator(InetSocketAddress outboundAddress) {
        this.outboundAddress = outboundAddress;
    }

    public void deliver(Mailbox from, Mailbox recipient, Message msg) throws EmailException
    {
        Email email = new SimpleEmail();
        email.setHostName(outboundAddress.getAddress().getHostName());
        email.setSmtpPort(outboundAddress.getPort());

        email.setFrom(from.getAddress());
        email.setSubject(msg.getSubject());
        email.setMsg(msg.getBody().toString());
        email.addTo(recipient.getAddress());
        email.send();

    }
}
