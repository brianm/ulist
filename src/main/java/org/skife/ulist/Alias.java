package org.skife.ulist;

import com.google.common.collect.Lists;
import org.apache.james.mime4j.field.address.Mailbox;

public class Alias
{
    public Iterable<? extends Mailbox> getMembers()
    {
        return Lists.newArrayList(new Mailbox("brianm", "example.net"));
    }
}
