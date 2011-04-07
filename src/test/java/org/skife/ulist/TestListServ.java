package org.skife.ulist;

import com.google.common.collect.Lists;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.james.mime4j.field.address.Mailbox;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

public class TestListServ
{
    private Wiser wiser;
    private ListServ ls;
    private InMemoryStorage storage;

    @Before
    public void setUp() throws Exception
    {
        wiser = new Wiser(35353);
        wiser.start();

        storage = new InMemoryStorage();

        ls = new ListServ(new InetSocketAddress(InetAddress.getLocalHost(), 25252),
                          new SMTPDeliverator(new InetSocketAddress(InetAddress.getLocalHost(), 35353)),
                          storage,
                          "ulist");

        ls.start();
        while (!ls.isReady()) {
            Thread.sleep(100);
        }
    }

    @After
    public void tearDown() throws Exception
    {
        ls.stop();
        wiser.stop();
    }

    @Test
    public void testCreateMailingListForNewUser() throws Exception
    {
        Email email = new SimpleEmail();
        email.setHostName(InetAddress.getLocalHost().getHostName());
        email.setSmtpPort(25252);
        email.setFrom("brianm@example.com");
        email.setSubject("hi");
        email.setMsg("hello world");

        email.addTo("kate@example.com");
        email.addTo("new-list@ulist");

        email.send();

        Alias alias = storage.findAlias("brianm@example.com", "new-list");
        assertThat(alias, notNullValue());
        assertThat(alias.getMembers(), hasItem(Mailbox.parse("kate@example.com")));
        assertThat(alias.getMembers(), hasItem(Mailbox.parse("brianm@example.com")));
        assertThat(alias.getMembers(), not(hasItem(Mailbox.parse("new-list@ulist"))));
    }

    @Test
    public void testEmailsSentToExistingAlias() throws Exception
    {
        storage.createAlias("brianm@example.com", "existing", Lists.newArrayList("kate@example.com",
                                                                                 "sam@example.com"));

        Email email = new SimpleEmail();
        email.setHostName(InetAddress.getLocalHost().getHostName());
        email.setSmtpPort(25252);
        email.setFrom("brianm@example.com");
        email.setSubject("hi");
        email.setMsg("hello world");

        email.addTo("existing@ulist");

        email.send();

        List<WiserMessage> msgs = wiser.getMessages();
        assertThat(msgs.size(), equalTo(2));
    }

    @Test
    public void testAddSomeoneToAlias() throws Exception
    {
        storage.createAlias("brianm@example.com", "existing", Lists.newArrayList("kate@example.com",
                                                                                 "sam@example.com"));

        Email email = new SimpleEmail();
        email.setHostName(InetAddress.getLocalHost().getHostName());
        email.setSmtpPort(25252);
        email.setFrom("brianm@example.com");
        email.setSubject("hi");
        email.setMsg("hello world");

        email.addTo("existing@ulist");
        email.addTo("jon@example.com");

        email.send();

        List<WiserMessage> msgs = wiser.getMessages();
        assertThat(msgs.size(), equalTo(2));

        Alias alias = storage.findAlias("brianm@example.com", "existing");
        assertThat(alias, notNullValue());
        assertThat(alias.getMembers(), hasItem(Mailbox.parse("kate@example.com")));
        assertThat(alias.getMembers(), hasItem(Mailbox.parse("brianm@example.com")));
        assertThat(alias.getMembers(), hasItem(Mailbox.parse("jon@example.com")));
    }

    @Test
    public void testSendingFancyEmail() throws Exception
    {
        storage.createAlias("brianm@example.com", "existing", Lists.newArrayList("kate@example.com"));

        HtmlEmail email = new HtmlEmail();
        email.setHostName(InetAddress.getLocalHost().getHostName());
        email.setSmtpPort(25252);
        email.setFrom("brianm@example.com");
        email.setSubject("hi");
        email.addPart("hello world", "text/plain");
        email.addPart("<h1>hello world</h1>", "text/html");

        email.addTo("existing@ulist");
        email.send();

        List<WiserMessage> msgs = wiser.getMessages();
        assertThat(msgs.size(), equalTo(1));
        WiserMessage msg = msgs.get(0);
        System.out.println(new String(msg.getData()));
    }
}
