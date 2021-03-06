package com.protection.plpt.plpt.mpkz.mpkz.method.email;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by sergey on 6/4/16.
 */
public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private String user;
    private String password;
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients,
                                      final String fileName) throws Exception {
        try {
            final MimeMessage message = new MimeMessage(session);
//            DataHandler handler = new DataHandler(new FileDataSource(fileName));
            message.setSender(new InternetAddress(sender));
            message.setText(body);
            _multipart = new MimeMultipart();
            addAttachment(fileName, subject, body);
            message.setFileName(getShortName(fileName));

            message.setContent(_multipart);
            message.setSubject(subject);
//            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            } else {
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            }
            new AsyncTask() {

                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Transport.send(message);
                        new File(fileName).delete();
                    } catch (MessagingException e) {
                        Log.i("gmail sender", "error sending email : " + e.getMessage());
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();

        } catch (Exception e) {
            Log.e("123", e.getMessage());
        }
    }

    private String getShortName(String fileName) {
        String res = fileName;
        while (res.contains("/")) {
            res = res.substring(res.indexOf("/") + 1);
        }
        return res;
    }

    private Multipart _multipart;

    public void addAttachment(String filename, String subject, String bodyText) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(getShortName(filename));

        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(bodyText, "text/plain");
        textPart.setText(bodyText);

        _multipart.addBodyPart(messageBodyPart);
        _multipart.addBodyPart(textPart);


//        BodyPart messageBodyPart2 = new MimeBodyPart();
//        messageBodyPart2.setText(subject);

//        _multipart.addBodyPart(messageBodyPart2);
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null) {
                return "application/octet-stream";
            } else {
                return type;
            }
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}
