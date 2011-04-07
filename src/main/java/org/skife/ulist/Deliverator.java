package org.skife.ulist;

import org.apache.commons.mail.EmailException;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.message.Message;

public interface Deliverator
{
    public void deliver(Mailbox from, Mailbox recipient, Message msg) throws EmailException;
}
