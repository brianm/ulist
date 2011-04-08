package org.skife.ulist;

import com.sun.mail.smtp.SMTPMessage;
import org.apache.commons.mail.EmailException;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.io.IOException;

public interface Deliverator
{
    public void deliver(String from, String recipient, SMTPMessage msg) throws EmailException, MessagingException, IOException;
}
