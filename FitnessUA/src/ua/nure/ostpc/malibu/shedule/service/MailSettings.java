package ua.nure.ostpc.malibu.shedule.service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class MailSettings {
	private static final String SMTP_INET_ADDR_KEY = "mail.smtp.inet.addr";
	private static final String SMTP_AUTH_USER_KEY = "mail.smtp.auth.user";
	private static final String SMTP_AUTH_PWD_KEY = "mail.smtp.auth.pwd";
	private static final String MIME_TYPE_KEY = "mime.type";
	
	private String smtpInetAddr;
	private String smtpAuthUser;
	private String smtpAuthPwd;
	private SMTPAuthenticator authenticator;
	private Session session;
	private String mimeType;

	class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(smtpAuthUser, smtpAuthPwd);
		}
	}

	public MailSettings(String fName) throws FileNotFoundException, IOException {
		Properties settings = new Properties();
		settings.load(new FileReader(fName));
		
		smtpInetAddr = settings.getProperty(SMTP_INET_ADDR_KEY);
		settings.remove(SMTP_INET_ADDR_KEY);
		smtpAuthUser = settings.getProperty(SMTP_AUTH_USER_KEY);
		settings.remove(SMTP_AUTH_USER_KEY);
		smtpAuthPwd = settings.getProperty(SMTP_AUTH_PWD_KEY);
		settings.remove(SMTP_AUTH_PWD_KEY);
		mimeType = settings.getProperty(MIME_TYPE_KEY);
		settings.remove(MIME_TYPE_KEY);
		
		authenticator = new SMTPAuthenticator();
		session = Session.getDefaultInstance(settings, authenticator);
	}

	public Session getSession() {
		return session;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getSmtpInetAddr() {
		return smtpInetAddr;
	}
}
