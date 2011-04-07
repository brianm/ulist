package org.skife.ulist;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.james.mime4j.field.address.Mailbox;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class InMemoryStorage implements Storage
{
    private final Map<UUID, Set<String>> aliases = Maps.newHashMap();
    private final Map<Map.Entry<String, String>, UUID> names = Maps.newHashMap();

    public synchronized Alias findAlias(String fromEmailAddress, String aliasName)
    {
        UUID uuid = names.get(Maps.immutableEntry(fromEmailAddress, aliasName));
        if (uuid == null) {
            return null;
        }
        Set<String> members = aliases.get(uuid);
        return new Alias(uuid, Iterables.transform(members, new Function<String, Mailbox>()
        {
            public Mailbox apply(String s)
            {
                return Mailbox.parse(s);
            }
        }));
    }

    public synchronized Alias createAlias(String creator, String name, Iterable<Mailbox> recipients)
    {
        UUID uuid = UUID.randomUUID();
        Set<String> members = Sets.newHashSet();
        Iterables.addAll(members, Iterables.transform(recipients, new Function<Mailbox, String>()
        {
            public String apply(Mailbox mailbox)
            {
                return mailbox.getAddress();
            }
        }));
        members.add(creator);
        aliases.put(uuid, members);

        for (String member : members) {
            if (names.containsKey(Maps.immutableEntry(member, name))) {
                // user already has an alias with that name, make a new name!
                int i = 0;
                while (names.containsKey(Maps.immutableEntry(member, name + "-" + i))) { i += 1;}
                names.put(Maps.immutableEntry(member, name + "-" + i), uuid);
            }
            else {
                // woot, just add it!
                names.put(Maps.immutableEntry(member, name), uuid);
            }
        }

        return findAlias(creator, name);
    }

    public synchronized Alias addToAlias(String from, String aliasName, Iterable<Mailbox> newbs)
    {
        UUID uuid = names.get(Maps.immutableEntry(from, aliasName));
        Iterables.addAll(aliases.get(uuid), Iterables.transform(newbs, new Function<Mailbox, String>() {
            public String apply(Mailbox mailbox)
            {
                return mailbox.getAddress();
            }
        }));

        return findAlias(from, aliasName);
    }
}
