package org.skife.ulist;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.Field;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.SMTPServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class ListServ
{

    private static final Logger log = LoggerFactory.getLogger(ListServ.class);

    private final SMTPServer server;

    public ListServ(InetSocketAddress listenAddress, final InetSocketAddress outboundAddress)
    {
        BasicConfigurator.configure();

        final Storage storage = new Storage();

        server = new SMTPServer(new MessageHandlerFactory()
        {
            public MessageHandler create(MessageContext ctx)
            {
                return new MessageHandler()
                {
                    private Mailbox to;
                    private Mailbox from;

                    public void from(String s) throws RejectException
                    {
                        from = Mailbox.parse(s);
                    }

                    public void recipient(String s) throws RejectException
                    {
                        to = Mailbox.parse(s);
                    }

                    public void data(InputStream inputStream) throws RejectException, TooMuchDataException, IOException
                    {
                        Message msg = new Message(inputStream);
                        for (Field field : msg.getHeader().getFields()) {
                            log.debug("{} : {}", field.getName(), field.getBody());
                        }

                        Alias alias = storage.find(from, to);

                        for (Mailbox member : alias.getMembers()) {
                            try {
                                Email email = new SimpleEmail();
                                email.setHostName(outboundAddress.getAddress().getHostName());
                                email.setSmtpPort(outboundAddress.getPort());
                                email.setFrom(from.toString());
                                email.addTo(member.getAddress());
                                email.setSubject(msg.getSubject());
                                email.setMsg(msg.getBody().toString());
                                email.addReplyTo(to.getAddress());
                                email.send();
                            }
                            catch (Exception e) {
                                throw new RejectException(e.getMessage());
                            }
                        }
                    }

                    public void done()
                    {
                        System.out.println("done!");
                    }
                };
            }
        });
        server.setBindAddress(listenAddress.getAddress());
        server.setPort(listenAddress.getPort());
    }

    public void start() {
        server.start();
    }

    public void stop()
    {
        server.stop();
    }

    public boolean isReady()
    {
        return server.isRunning();
    }
}
