package service;

import org.apache.commons.io.IOUtils;
import utils.Constants;
import utils.EmailUtil;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by sasaradovanovic on 1/14/18.
 */
public class EmailService {


    private Properties props;

    private String template;

    private String activationTemplate;


    public EmailService() {
        props = new Properties();
        props.put("mail.smtp.host", Constants.Email.SMTP_HOST);
        props.put("mail.smtp.socketFactory.port", Constants.Email.SSL_PORT);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", Constants.Email.SSL_PORT);
        ClassLoader classLoader = getClass().getClassLoader();
        template = getFileWithUtil("email-templates/test.html");
        activationTemplate = getFileWithUtil("email-templates/activation.html");
    }


    public void sendActivationEmail (String receptient, String hash, String username) throws IOException, URISyntaxException {

        String activationUrl = Constants.App.URL + "/#/activate?hash=" + hash + "&username=" + username;
        System.out.println("---------------------------------------");
        String mailContent = activationTemplate.replace("{{activationLink}}", activationUrl);
        sendEmail(receptient, "Activate your account", mailContent);

    }


    private void sendEmail (String receptient, String title, String content) throws URISyntaxException, IOException {
        final String fromEmail = Constants.Email.EMAIL_ADDRESS; //requires valid gmail id
        final String password = Constants.Email.EMAIL_PASSWORD; // correct password for gmail id
        final String toEmail = receptient; // can be any email id

        System.out.println("SSLEmail Start");

        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getDefaultInstance(props, auth);
        System.out.println("Session created");
        EmailUtil.sendEmail(session, toEmail, title, content);
    }


    private String getFileWithUtil(String fileName) {

        String result = "";

        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
