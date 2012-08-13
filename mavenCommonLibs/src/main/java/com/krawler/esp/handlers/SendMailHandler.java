/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.krawler.esp.handlers;

import java.io.UnsupportedEncodingException;
import javax.mail.*;
import javax.mail.internet.*;

import com.krawler.esp.utils.ConfigReader;

import java.io.File;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SendMailHandler {

	private static String getSMTPPath() {
		return ConfigReader.getinstance().get("SMTPPath");
	}
	
	private static String getSMTPPort() {
		return ConfigReader.getinstance().get("SMTPPort");
	}

	public static void postMail(String recipients[], String subject,
			String htmlMsg, String plainMsg, String from) throws MessagingException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", getSMTPPath());
		props.put("mail.smtp.port", getSMTPPort());

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		MimeMessage msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i].trim().replace(" ", "+"));
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);

		Multipart multipart = new MimeMultipart("alternative");

		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(plainMsg, "text/plain");
		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlMsg, "text/html");
		multipart.addBodyPart(messageBodyPart);

		msg.setContent(multipart);
		Transport.send(msg);
	}
    
      public static void postMail(String recipients[], String subject,
			String htmlMsg, String plainMsg, String fromAddress, String fromName) throws MessagingException, UnsupportedEncodingException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", getSMTPPath());
		props.put("mail.smtp.port", getSMTPPort());

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		MimeMessage msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(fromAddress,fromName);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i].trim().replace(" ", "+"));
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);

		Multipart multipart = new MimeMultipart("alternative");

		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(plainMsg, "text/plain");
		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlMsg, "text/html");
		multipart.addBodyPart(messageBodyPart);

		msg.setContent(multipart);
		Transport.send(msg);
	}
    public static void postMail(String recipients[], String subject,
			String htmlMsg, String plainMsg, String from, String attachments[]) throws MessagingException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", getSMTPPath());
		props.put("mail.smtp.port", getSMTPPort());

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create a message
		MimeMessage msg = new MimeMessage(session);
		MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(msg, true);
		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
            String address=recipients[i].trim().replace(" ", "+");
			addressTo[i] = new InternetAddress(address);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);

//		Multipart multipart = new MimeMultipart("alternative");
//
//		BodyPart messageBodyPart = new MimeBodyPart();
//		messageBodyPart.setContent(plainMsg, "text/plain");
//		multipart.addBodyPart(messageBodyPart);
//
//		messageBodyPart = new MimeBodyPart();
//		messageBodyPart.setContent(htmlMsg, "text/html");
//		multipart.addBodyPart(messageBodyPart);
		mimeMessageHelper.setText(htmlMsg, true);

                for (int i = 0; i < attachments.length; i+=2) {
//                  messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachments[i]);
                    mimeMessageHelper.addAttachment(attachments[i+1], source);
//                  messageBodyPart.setDataHandler(new DataHandler(source));
//                  messageBodyPart.setFileName(attachments[i+1]);
//                  multipart.addBodyPart(messageBodyPart);
                }

//		msg.setContent(multipart);
		Transport.send(msg);
	}

    public static void postCampaignMail(String recipients[], String subject,
			String htmlMsg, String plainMsg, String fromAddress, String replyAddress[], String fromName) throws MessagingException, UnsupportedEncodingException {
		boolean debug = false;
		Properties props = new Properties();
		props.put("mail.smtp.host", getSMTPPath());
		props.put("mail.smtp.port", getSMTPPort());
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);
		MimeMessage msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(fromAddress,fromName);
		msg.setFrom(addressFrom);
		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i].trim().replace(" ", "+"));
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);
		msg.setSubject(subject);
        InternetAddress[] replyTo = new InternetAddress[replyAddress.length];
        for (int i = 0; i < recipients.length; i++) {
			replyTo[i] = new InternetAddress(replyAddress[i].trim().replace(" ", "+"));
		}
        msg.setReplyTo(replyTo);
		Multipart multipart = new MimeMultipart("alternative");
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(plainMsg, "text/plain");
		multipart.addBodyPart(messageBodyPart);
		messageBodyPart = new PreencodedMimeBodyPart("base64");
        messageBodyPart.setHeader("charset", "utf-8");
		messageBodyPart.setContent(htmlMsg, "text/html");
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);
		Transport.send(msg);
	}
}
