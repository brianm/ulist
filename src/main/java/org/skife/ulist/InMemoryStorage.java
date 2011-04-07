package org.skife.ulist;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.apache.james.mime4j.field.address.Mailbox;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.immutableEntry;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.UUID.randomUUID;

public class InMemoryStorage implements Storage
{
    private final Map<UUID, Set<String>> aliases = newHashMap();
    private final Map<Map.Entry<String, String>, UUID> names = newHashMap();

    public synchronized Alias findAlias(String fromEmailAddress, String aliasName)
    {
        UUID uuid = names.get(immutableEntry(fromEmailAddress, aliasName));
        if (uuid == null) {
            return null;
        }
        Set<String> members = aliases.get(uuid);
        return new Alias(uuid, transform(members, new Function<String, Mailbox>()
        {
            public Mailbox apply(String s)
            {
                return Mailbox.parse(s);
            }
        }));
    }

    public synchronized Alias createAlias(String creator, String name, Iterable<String> recipients)
    {
        UUID uuid = randomUUID();
        Set<String> members = newHashSet();
        Iterables.addAll(members, recipients);
        members.add(creator);
        aliases.put(uuid, members);

        for (String member : members) {
            if (names.containsKey(immutableEntry(member, name))) {
                // user already has an alias with that name, make a new name!
                int i = 0;
                while (names.containsKey(immutableEntry(member, name + "-" + i))) { i += 1;}
                names.put(immutableEntry(member, name + "-" + i), uuid);
            }
            else {
                // woot, just add it!
                names.put(immutableEntry(member, name), uuid);
            }
        }

        return findAlias(creator, name);
    }

    public synchronized Alias addToAlias(String from, String aliasName, Iterable<String> newbs)
    {
        UUID uuid = names.get(immutableEntry(from, aliasName));
        addAll(aliases.get(uuid), newbs);
        return findAlias(from, aliasName);
    }
}
