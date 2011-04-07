package org.skife.ulist;

public interface Storage
{
    public Alias findAlias(String from, String to);

    Alias createAlias(String creator, String name, Iterable<String> recipients);

    Alias addToAlias(String from, String name, Iterable<String> newbs);
}
