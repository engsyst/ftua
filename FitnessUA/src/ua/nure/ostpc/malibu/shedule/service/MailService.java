package ua.nure.ostpc.malibu.shedule.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

public class MailService {
	private static MailSettings ms;
	private static final Logger log = Logger.getLogger(MailService.class);
	
	public static void configure(String propertyFileName) throws MailException {
		try {
			ms = new MailSettings(propertyFileName);
		} catch (FileNotFoundException e) {
			log.error("Can not read mail service configuration", e);
			throw new MailException("Can not read mail service configuration", e);
		} catch (IOException e) {
			log.error("Can not read mail service configuration", e);
			throw new MailException("Can not read mail service configuration", e);
		}
	}
	
	public static void sendMail(String theme, String text, byte[] attach, String attachName, String[] emailList) throws MailException {
		if (ms == null) {
			log.error("MailService not cofigured");
			throw new MailException("MailService not cofigured");
		}
		if (emailList == null) {
			log.error("e-mail list can not be null");
			throw new MailException("MailService not cofigured");
		}
		
		Session session = ms.getSession();
		session.setDebug(true);
		
		Transport transport = null;
		try {
			Message message = new MimeMessage(session);
			Multipart multipart = new MimeMultipart();
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setHeader("Content-Type:", "text/plain; charset=UTF-8");
			messageBodyPart.setText(text);
			multipart.addBodyPart(messageBodyPart);
			
			if (attach != null) {
				messageBodyPart = new MimeBodyPart();
				messageBodyPart.setFileName(attachName);
				DataSource source = new ByteArrayDataSource(attach, ms.getMimeType());
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setHeader("Content-Disposition: ", "attachment; charset=UTF-8; " + Charset.forName("UTF-8").encode(attachName));
//				messageBodyPart.setFileName(Charset.forName("UTF-8").encode(attachName).toString());
				multipart.addBodyPart(messageBodyPart);
			}
			message.setContent(multipart);

			message.setSubject(theme);
			message.setFrom(new InternetAddress(ms.getSmtpInetAddr()));

			for (String email : emailList) {
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(email));
			}

			transport = session.getTransport();
			transport.connect();
			transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
			
			if (log.isDebugEnabled()) {
				log.debug("Mails sended.");
			}
		} catch (Exception e) {
			log.error("Can not send e-mail.", e);
			throw new MailException("Невозможно отправить почту", e);
		} finally {
			try {
				if (transport != null) {
					transport.close();
				}
			} catch (MessagingException e) {
				log.error("Can not close message transport.", e);
				throw new MailException("Невозможно отправить почту", e);
			}
		}
	}

}
