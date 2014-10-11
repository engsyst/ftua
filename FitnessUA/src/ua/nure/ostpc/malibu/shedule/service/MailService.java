package ua.nure.ostpc.malibu.shedule.service;

import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
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

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.EmployeeDAO;

public class MailService {
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_AUTH_USER = "kotlyarovanton2@gmail.com";
	private static final String SMTP_AUTH_PORT = "587";
	private static final String SMTP_AUTH_PWD = "mazeratti9595";
	private static final String fileName = "shedule.xls";
	private static final String filePath = "C:/T.xls";

	private EmployeeDAO employeeDAO;
	private static final Logger log = Logger.getLogger(MailService.class);

	public MailService(EmployeeDAO employeeDAO) {
		this.employeeDAO = employeeDAO;
	}

	public void sendMail() {
		List<String> emailList = employeeDAO.getEmailListForSubscribers();
		if (emailList != null) {
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.host", SMTP_HOST_NAME);
			props.put("mail.smtp.port", SMTP_AUTH_PORT);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.ssl.trust", SMTP_HOST_NAME);
			Authenticator auth = new SMTPAuthenticator();
			Session session = Session.getDefaultInstance(props, auth);
			session.setDebug(false);

			Transport transport = null;
			try {
				Message message = new MimeMessage(session);
				transport = session.getTransport();
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setText("Schedule");
				Multipart multipart = new MimeMultipart();
				multipart.addBodyPart(messageBodyPart);
				if (fileName != null) {
					messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(filePath);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(fileName);
					multipart.addBodyPart(messageBodyPart);
				}
				message.setContent(multipart);
				message.setFrom(new InternetAddress("kotlyarovanton2@gmail.com"));
				for (String email : emailList) {
					message.addRecipient(Message.RecipientType.TO,
							new InternetAddress(email));
				}
				transport.connect();
				transport.sendMessage(message,
						message.getRecipients(Message.RecipientType.TO));
				if (log.isDebugEnabled()) {
					log.debug("Mails sended.");
				}
			} catch (Exception e) {
				log.error("Can not send schedule.", e);
			} finally {
				try {
					if (transport != null) {
						transport.close();
					}
				} catch (MessagingException e) {
					log.error("Can not close message transport.", e);
				}
			}
		}
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}
}
