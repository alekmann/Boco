package no.boco.backend.user;

import no.boco.backend.rental.Rental;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * The MailService class provides methods for sending e-mails from the company account.
 */
@Service
public class MailService {
    private static final String WEBSITE = "http://localhost:8080";
    private final String EMAIL = "noreply.boco@gmail.com";
    private final String PSSWRD = "idatt2106";

    /**
     * Standard setup for sending an e-mail from the company account.
     * @return the session object to be used while constructing an e-mail.
     */
    private Session getSession(){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PSSWRD);
            }
        });
    }

    /**
     * Standardized template for sending an activation e-mail to a User from the company account.
     * Will provide a link the reciever can visit to activate their user account.
     * @param user the User to send the e-mail to.
     */
    public void sendActivationEmail(User user){
        String recipient = user.getEmail();
        String subject = "Activate your Boco profile";

        String link = "http://localhost:8080/activate/" + user.getActivation();
        String content = "<p>Hello " + user.getFirstName() + " " + user.getLastName() + "!" + "</p>" +
                "<p>Please activate your Boco profile by visiting the following link:</p>" +
                "<a href=" + link + ">" + link + "</a>";

        sendEmail(recipient, subject, content);
    }

    /**
     * Send an e-mail from the company account.
     * @param recipient the e-mail address to send an e-mail to.
     * @param subject the subject field in the e-mail.
     * @param htmlContent the content of the e-mail. Will be parsed as html.
     */
    public void sendEmail(String recipient, String subject, String htmlContent){
        Session session = getSession();
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(EMAIL, false));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject(subject);
            msg.setSentDate(new java.util.Date());
            msg.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(msg);
        }catch(MessagingException e){
            e.printStackTrace();
        }
    }

    /**
     * Sends a notification to with information about a rental request
     * @param rental rental object to notify about
     */
    public void sendRentalRequest(Rental rental) {
        String subject = "Du har mottat en leieforespørsel";
        String html = String.format("<h3>Leieforespørsel %s</h3>" +
               "<p>Du har mottatt en leieforespørsel på <a href='%s'>%s</a> fra %s til %s.</p>" +
                "<p>Besøk <a href='%s'>Boco sine nettsider</a> for å svare!</p>",
                rental.getPost().getTitle(),
                WEBSITE + "/posts/" + rental.getPost().getPostId(),
                rental.getPost().getTitle(),
                rental.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("d. MMM yy")),
                rental.getToDate().toLocalDate().format(DateTimeFormatter.ofPattern("d. MMM yy")),
                WEBSITE
                );
        sendEmail(rental.getPost().getUser().getEmail(), subject, html);
    }

    /**
     * Sends notification with the answer of for a rental request
     * @param rental rental request
     */
    public void sendRentalRequestAnswer(Rental rental) {
        String subject = "Du har fått svar på din leieforespørsel";
        String html = String.format("<h3>Leieforespørsel %s</h3>" +
                        "<p>Du har fått %s din leieforespørsel på <a href='%s'>%s</a> fra %s til %s.</p>",
                rental.getPost().getTitle(),
                rental.isApproved() ? "godkjent" : "avslått",
                WEBSITE + "/posts/" + rental.getPost().getPostId(),
                rental.getPost().getTitle(),
                rental.getFromDate().toLocalDate().format(DateTimeFormatter.ofPattern("d. MMM yyyy")),
                rental.getToDate().toLocalDate().format(DateTimeFormatter.ofPattern("d. MMM yyyy"))
        );
        sendEmail(rental.getCustomer().getEmail(), subject, html);
    }
}
