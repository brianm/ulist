package org.skife.ulist;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.james.mime4j.field.address.Mailbox;
import org.junit.Test;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TestListServ
{
    @Test
    public void testFoo() throws Exception
    {
        Wiser wiser = new Wiser(35353);
        wiser.start();

        ListServ ls = new ListServ(new InetSocketAddress(InetAddress.getLocalHost(), 25252),
                                   new InetSocketAddress(InetAddress.getLocalHost(), 35353));

        ls.start();
        while (!ls.isReady()) {
            Thread.sleep(100);
        }

        Email email = new SimpleEmail();
        email.setHostName(InetAddress.getLocalHost().getHostName());
        email.setSmtpPort(25252);
        email.setFrom("Brian Wiffle <brianm@example.com>");
        email.addTo("everyone@ulist");
        email.setSubject("a subject");
        email.setMsg("hell world");
        email.send();

        List<WiserMessage> rs = wiser.getMessages();
        assertThat(rs.size(), equalTo(1));

        WiserMessage msg = rs.get(0);
        assertThat(msg.getMimeMessage().getSubject(), equalTo("a subject"));
        assertThat(msg.getEnvelopeSender(), equalTo("brianm@example.com"));
        assertThat(msg.getMimeMessage().getHeader("reply-to").length, equalTo(1));
        assertThat(Mailbox.parse(msg.getMimeMessage().getHeader("reply-to")[0]).getAddress(),
                   equalTo("everyone@ulist"));


        ls.stop();
        wiser.stop();
    }
}
