package org.skife.ulist;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.sun.mail.smtp.SMTPMessage;
import org.apache.commons.mail.EmailException;
import org.apache.james.mime4j.field.address.Mailbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Set;

public class Dispatcher
{
    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private final Deliverator deliverator;
    private final Storage storage;
    private final String aliasDomain;

    public Dispatcher(Deliverator deliverator,
                      Storage storage,
                      String aliasDomain)
    {
        this.deliverator = deliverator;
        this.storage = storage;
        this.aliasDomain = aliasDomain;
    }

    public void dispatch(Mailbox from, Iterable<Mailbox> recipients, SMTPMessage msg) throws RejectException,
                                                                                             TooMuchDataException,
                                                                                             IOException
    {

        final Mailbox alias_mbox = Iterables.find(recipients, new Predicate<Mailbox>()
        {
            public boolean apply(Mailbox mailbox)
            {
                return aliasDomain.equalsIgnoreCase(mailbox.getDomain());
            }
        });

        if (alias_mbox != null) {
            // one of the recipients is an alias!
            final Alias alias = storage.findAlias(from.getAddress(), alias_mbox.getLocalPart());
            if (alias != null) {
                // sent to an existing alias!

                for (Mailbox alias_member : alias.getMembers()) {
                    try {
                        if (!alias_member.equals(from)) {
                            deliverator.deliver(from.getAddress(), alias_member.getAddress(), msg);
                        }
                    }
                    catch (Exception e) {
                        log.warn("unable to deliver mail", e);
                    }
                }


                Set<Mailbox> newbs = Sets.difference(Sets.newHashSet(recipients), Sets.newHashSet(alias.getMembers()));
                storage.addToAlias(from.getAddress(), alias_mbox.getLocalPart(),
                                   Iterables.transform(newbs, new Function<Mailbox, String>()
                                   {
                                       public String apply(Mailbox mailbox)
                                       {
                                           return mailbox.getAddress().toLowerCase();
                                       }
                                   }));

            }
            else {
                // we need to make an alias!

                Iterable<Mailbox> filtered = Iterables.filter(recipients, new Predicate<Mailbox>()
                {
                    public boolean apply(Mailbox mailbox)
                    {
                        return !(alias_mbox.getLocalPart().equals(mailbox.getLocalPart())
                                 && alias_mbox.getDomain().equals(mailbox.getDomain()));
                    }
                });

                Alias new_alias = storage.createAlias(from.getAddress(),
                                                      alias_mbox.getLocalPart(),
                                                      Iterables.transform(filtered, new Function<Mailbox, String>()
                                                      {

                                                          public String apply(Mailbox mailbox)
                                                          {
                                                              return mailbox.getAddress().toLowerCase();
                                                          }
                                                      }));

                // TODO need to message the creator about creation of the alias
            }
        }
        else {
            throw new UnsupportedOperationException("Not Yet Implemented!");
        }

    }
}
