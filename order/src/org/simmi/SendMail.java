package org.simmi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	
	public static void message2( String to, String uid, String content ) {
		String from = "sigmar@matis.is";
		//String to = pnt.Pantað_Af+"@matis.is";
		String subject = uid;
		String message = content;
		
		SubMail sendMail = new SubMail(from, to, subject, message);
		sendMail.send();
	}
	
	public static void message( String to, String uid, String content ) {
		String from = "vote@matis.is";
		//String to = pnt.Pantað_Af+"@matis.is";
		//String subject = "áminning, kosningar í Starfsmannaráð Matís 2011"; //"kosning í Starfsmannaráð Matís 2011 / Matis employee's council election 2011";
		//String message = "Við vildum minna aftur á kosninguna í Starfsmannaráð Matís 2011\n\nÞín kosningaslóð/Election link: http://130.208.252.230:8888/Vote.html?uid=" 
		//		+ uid + "\n\nKosningu líkur kl 16:00. Þriðjudaginn 18.10.2011" 
		//		+ "\n\n"+content;
		
		String subject = "kosning öryggistrúnaðarmanna Matís 2013 / Matis security council election 2013";
		String message = "Sæl\n\nÞá er komið að kosningu í öryggistrúnaðarmanna Matís ohf. Vinsamlega notið tengilinn í þessum pósti til að velja þá tvo einstaklinga sem þið viljið sjá starfa að öryggismálum hjá Matís."
		+ "\n\nÞín kosningaslóð/Election link: http://130.208.252.7:8888/Vote.html?uid="+uid
		+ "\n\nKosningu líkur kl 16:00. Föstudaginn 14.03.2013"
		+ "\n\n"+content;
		 
		SubMail sendMail = new SubMail(from, to, subject, message);
		sendMail.send();
	}
	
	private static class SMTPAuthenticator extends javax.mail.Authenticator {
	    public PasswordAuthentication getPasswordAuthentication() {
	        String username = "sigmar";
	        String password = "mouse.311dick";
	        return new PasswordAuthentication(username, password);
	    }
	};

	public static class SubMail {
		private String from;
		private String to;
		private String subject;
		private String text;
		
		public SubMail(String from, String to, String subject, String text){
			this.from = from;
			this.to = to;
			this.subject = subject;
			this.text = text;
		}
		
		public void send(){
			Properties props = new Properties();
			props.put("mail.smtp.host", "postur.matis.is");
			props.put("mail.smtp.port", "25");
			//props.put("mail.smtp.auth", "true");
			//props.put("mail.smtp.auth.ntlm.domain", "MATIS");
			
			Authenticator auth = new SMTPAuthenticator();
			Session mailSession = Session.getDefaultInstance(props);//, auth);
			Message simpleMessage = new MimeMessage(mailSession);
			
			InternetAddress fromAddress = null;
			InternetAddress toAddress = null;
			try {
				fromAddress = new InternetAddress(from);
				toAddress = new InternetAddress(to);
			} catch (AddressException e) {
				e.printStackTrace();
			}
			
			try {
				simpleMessage.setFrom(fromAddress);
				simpleMessage.setRecipient(RecipientType.TO, toAddress);
				simpleMessage.setSubject(subject);
				simpleMessage.setText(text);
				
				Transport.send(simpleMessage);			
			} catch (MessagingException e) {
				e.printStackTrace();
			}		
		}
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			char[] cbuf = new char[1024];
			StringBuilder sb = new StringBuilder();
			Reader	fr = new InputStreamReader( SendMail.class.getResourceAsStream("/oryggi.txt") );
			int r = fr.read(cbuf);
			while( r > 0 ) {
				sb.append( cbuf, 0, r );
				r = fr.read(cbuf);
			}
			fr.close();
			
			/*String[] bull = {"8243040612232853973",
					"3032366729773481819",
					"5975987280778136444",
					"432737564548472298",
					"5456942221999096097",
					"3311023510920647968",
					"8719707240337189579",
					"2797414000838240457",
					"2044937091303351491",
					"3499097865561408788",
					"8097919572331296481",
					"2618348423619995970",
					"247690798092834781",
					"4919488340587129834",
					"2141204974859309799",
					"5929771037638051710",
					"4622778440827285588",
					"816558893251035831",
					"8734052522180105075",
					"6309034699706951952",
					"4227154924089505962",
					"6068574952696212333",
					"8560657665512396257",
					"8871591716536767798",
					"6549227508791347111",
					"6623265450318362221",
					"7760068989956594156",
					"6166136487623648967",
					"5503166760822750194",
					"4408239496213731951",
					"2245488318968013358",
					"7682204002095924274",
					"1078130137536902820",
					"6264392102107545848",
					"5434528781918765444",
					"9101458503875819127",
					"4666993386630914774",
					"5587276286812160356",
					"5666499453947364747",
					"7709363980229879560",
					"5620377932448783008",
					"1867959909973995721",
					"8479397565986625848",
					"746712447560201329"};*/
			//Set<String> check = new HashSet<String>( Arrays.asList(bull) );
			
			fr = new InputStreamReader( SendMail.class.getResourceAsStream("/left.txt") );
			BufferedReader	br = new BufferedReader( fr );
			String line = br.readLine();
			while( line != null ) {
				String[] split = line.split("\t");
				if( split.length > 2 ) {
					if( split[1].contains("@") /*&& !check.contains(split[split.length-1])*/ ) {
						//message( "sigmar@matis.is", split[split.length-1], sb.toString() );
						//message( "jon.h.arnarson@matis.is", split[split.length-1], sb.toString() );
						message( split[1], split[split.length-1], sb.toString() );
						//System.out.println( split[1] + " " + split[split.length-1] );
						//break;
					} else System.out.println( split[1] + " " + split[split.length-1] );
				}
				
				line = br.readLine();
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
