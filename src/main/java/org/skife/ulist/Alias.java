package org.skife.ulist;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.james.mime4j.field.address.Mailbox;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Alias
{
    private final UUID name;
    private final Iterable<Mailbox> members;

    public Alias(UUID name, Iterable<Mailbox> members) {
        this.name = name;
        this.members = members;
    }

    public Iterable<Mailbox> getMembers()
    {
        return members;
    }

    public UUID getName()
    {
        return name;
    }
}
