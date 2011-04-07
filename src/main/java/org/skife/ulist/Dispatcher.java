package org.skife.ulist;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.message.Message;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Dispatcher
{
    private final Storage storage;
    private final InetSocketAddress outboundAddress;

    public Dispatcher(Storage storage, InetSocketAddress outboundAddress)
    {
        this.storage = storage;
        this.outboundAddress = outboundAddress;
    }

    public void dispatch(Mailbox from, Iterable<Mailbox> to, Message msg) throws RejectException,
                                                                                 TooMuchDataException,
                                                                                 IOException
    {
        Alias alias = storage.find(from, to);

        for (Mailbox member : alias.getMembers()) {
            try {
                Email email = new SimpleEmail();
                email.setHostName(outboundAddress.getAddress().getHostName());
                email.setSmtpPort(outboundAddress.getPort());
                email.setFrom(from.toString());
                email.addTo(member.getAddress());
                email.setSubject(msg.getSubject());
                email.setMsg(msg.getBody().toString());
                email.addReplyTo(alias.getAliasAddressFor(member));
                email.send();
            }
            catch (Exception e) {
                throw new RejectException(e.getMessage());
            }
        }
    }
}
