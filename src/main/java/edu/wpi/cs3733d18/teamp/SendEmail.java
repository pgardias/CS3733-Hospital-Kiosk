package edu.wpi.cs3733d18.teamp;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmail {

    // Attributes
    private String to;
    private boolean isText;

    // Constructor
    public SendEmail(String to) {

        this.to = to;
    }

    /**
     * Sends a message to a specified email
     * @param messageText The message to be emailed, each line is an index
     */
    public void sendEmail(ArrayList<String> messageText) {
        // Recipient's email ID needs to be mentioned.
        //String to = "kqsavell@wpi.edu";

        // Sender's email ID needs to be mentioned
        String from = "teamphatphoenix@gmail.com";

        // Assuming you are sending email from localhost
        String host = "smtp-mail.outlook.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.user", from);
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "thephattestphoenix");
            }
        };

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, authenticator);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Brigham & Women's: Your hospital route");

            // Send the actual message
            String emailText = genDirections(messageText);
            captureDisplay(); // Get path/map image
            //message.setText(emailText);
            //message.setContent("<img src=\"screenshot.jpg\"><div>"+emailText+"</div>", "text/html");

            // This mail has 2 part, the BODY and the embedded image
            MimeMultipart multipart = new MimeMultipart("related");
            // first part (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<img src=\"cid:image\"><div align=\"center\">"+emailText+"</div>";
            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);
            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(
                    "screenshot.png");

            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            // add image to the multipart
            multipart.addBodyPart(messageBodyPart);

            // put everything together
            message.setContent(multipart);

            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    /**
     * Sends a message to a specified phone number via email
     * @param messageText The message to be emailed, each line is an index
     */
    public void sendText(ArrayList<String> messageText) {
        // Recipient's email ID needs to be mentioned.
        //String to = "kqsavell@wpi.edu";

        // Sender's email ID needs to be mentioned
        String from = "teamphatphoenix@gmail.com";

        // Assuming you are sending email from localhost
        String host = "smtp-mail.outlook.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.user", from);
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "thephattestphoenix");
            }
        };

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties, authenticator);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Brigham & Women's: Your hospital route");

            // Send the actual message
            String emailText = "";
            for (String s : messageText) {
                emailText = emailText.concat(s + "\n");
            }

//            captureDisplay(); // Get path/map image
//
//            // Create the message part
//            BodyPart messageBodyPart = new MimeBodyPart();
//
//            // Now set the actual message
//            messageBodyPart.setText(emailText);
//
//            // Create a multipar message
//            Multipart multipart = new MimeMultipart();
//
//            // Set text message part
//            multipart.addBodyPart(messageBodyPart);
//
//            // Part two is attachment
//            messageBodyPart = new MimeBodyPart();
//            String filename = "screenshot.png";
//            DataSource source = new FileDataSource("screenshot.png");
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(filename);
//            multipart.addBodyPart(messageBodyPart);
//
//            // Send the complete message parts
//            message.setContent(multipart);

            message.setText(emailText);
            // Send message
            Transport.send(message);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    /**
     * Takes a screenshot of the screen and saves it as an image file
     */
    private void captureDisplay() {
        try {
            /*WritableImage writableImage = new WritableImage(1920, 1080);
            BufferedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);*/
            File file = new File("emailMap.png");
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
            ImageIO.write(image, "png", new File("screenshot.png"));
            //ImageIO.write(renderedImage, "png", file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (AWTException awte) {
            awte.printStackTrace();
        }
    }

    /**
     * Generates the message to send via email
     * @param message The arraylist to convert into a string
     */
    private String genDirections(ArrayList<String> message) {
        String rtnMessage = "";
        for (String s: message) {
            rtnMessage = rtnMessage.concat(s+"<p></p>");
        }
        rtnMessage = rtnMessage.concat("You have arrived at your destination!" + "<p></p>");
        return rtnMessage;
    }

    public String getTo() {
        return to;
    }
}
