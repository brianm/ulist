package org.skife.ulist;

import org.apache.james.mime4j.field.address.Mailbox;

import java.util.Set;

public interface Storage
{
    public Alias findAlias(String from, String to);

    Alias createAlias(String creator, String name, Iterable<Mailbox> recipients);

    Alias addToAlias(String from, String name, Iterable<Mailbox> newbs);
}
