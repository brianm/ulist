package org.skife.ulist;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.SMTPServer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

public class TestSubEthaStuff
{
    @Test
    public void testFoo() throws Exception
    {

        BasicConfigurator.configure();
        SMTPServer server = new SMTPServer(new MessageHandlerFactory()
        {
            public MessageHandler create(MessageContext ctx)
            {
                return new MessageHandler()
                {

                    public void from(String s) throws RejectException
                    {
                        System.out.printf("from: %s\n", s);
                    }

                    public void recipient(String s) throws RejectException
                    {
                        System.out.printf("to: %s\n", s);
                    }

                    public void data(InputStream inputStream) throws RejectException, TooMuchDataException, IOException
                    {
                        Message msg = new Message(inputStream);
                        for (Field field : msg.getHeader().getFields()) {
                            System.out.println(field.getName() + " || " + field.getBody());
                        }




                    }

                    public void done()
                    {
                        System.out.println("done!");
                    }
                };
            }
        });
        server.setBindAddress(InetAddress.getLocalHost());
        server.setPort(25252);

        server.start();

        while (!server.isRunning()) {
            Thread.sleep(500);
        }


        Email email = new SimpleEmail();
        email.setHostName(InetAddress.getLocalHost().getHostName());
        email.setSmtpPort(25252);
        email.setFrom("brianm@example.com");
        email.addTo("brianm@example.org");
        email.setSubject("a subject");
        email.setMsg("hell world");
        email.send();
    }
}
