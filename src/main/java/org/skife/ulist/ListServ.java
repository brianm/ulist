package org.skife.ulist;

import com.google.common.collect.Lists;
import com.sun.mail.smtp.SMTPMessage;
import org.apache.james.mime4j.field.address.Mailbox;
import org.apache.james.mime4j.message.Message;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.server.SMTPServer;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;

public class ListServ
{
    private final SMTPServer server;
    private final Dispatcher dispatcher;

    public ListServ(InetSocketAddress listenAddress,
                    Deliverator deliverator,
                    Storage storage,
                    String aliasDomain)
    {
        BasicConfigurator.configure();

        dispatcher = new Dispatcher(deliverator, storage, aliasDomain);

        server = new SMTPServer(new MessageHandlerFactory()
        {
            public MessageHandler create(MessageContext ctx)
            {
                return new MessageHandler()
                {
                    private List<Mailbox> to = Lists.newArrayList();
                    private Mailbox from;

                    public void from(String s) throws RejectException
                    {
                        from = Mailbox.parse(s);
                    }

                    public void recipient(String s) throws RejectException
                    {
                        to.add(Mailbox.parse(s));
                    }

                    public void data(InputStream inputStream) throws RejectException, TooMuchDataException, IOException
                    {
                        Session session = Session.getDefaultInstance(new Properties());
                        SMTPMessage msg = null;
                        try {
                            msg = new SMTPMessage(session, inputStream);

                            dispatcher.dispatch(from, to, msg);
                        }
                        catch (MessagingException e) {

                        }
                    }

                    public void done()
                    {
                    }
                };
            }
        });
        server.setBindAddress(listenAddress.getAddress());
        server.setPort(listenAddress.getPort());
    }

    public void start()
    {
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
