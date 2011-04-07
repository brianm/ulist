package org.skife.ulist;

import org.apache.james.mime4j.field.address.Mailbox;

import java.util.Set;

public class RealStorage implements Storage
{
    public Alias findAlias(String from, String to)
    {
        throw new UnsupportedOperationException("Not Yet Implemented!");
    }

    public Alias createAlias(String creator, String name, Iterable<Mailbox> recipients)
    {
        throw new UnsupportedOperationException("Not Yet Implemented!");
    }

    public Alias addToAlias(String from, String name, Iterable<Mailbox> newbs)
    {
        throw new UnsupportedOperationException("Not Yet Implemented!");
    }
}
