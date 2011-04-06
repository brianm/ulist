package org.skife.ulist;

import org.apache.james.mime4j.field.address.Mailbox;

public class Storage
{
    public Alias find(Mailbox from, Mailbox to) {
        return new Alias();
    }
}
