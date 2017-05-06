package Mail;
/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月9日 下午2:07:23 
* 类说明 
*/

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
import javax.mail.internet.MimeUtility;

public class SendMail {
	
	private String myMailAdd="hzq0320@163.com";
	private String myMailPassword="hzq970320";
	
	public SendMail(String mailadd,String password){
		myMailAdd=mailadd;
		myMailPassword=password;
	}
	
	//发送邮件：邮件标题、邮件正文、邮件接受者
	public void sendTo(String mailTitle,String mailContent,String recMailAdd) throws MessagingException{
		//配置文件
		Properties props = new Properties();
		props.put("mail.smtp.host","smtp.163.com");//发件人用来发邮件的电子信箱服务器
		props.put("mail.smtp.auth","true"); //通过验证
		
		Session session=Session.getInstance(props);
		//session.setDebug(true);//显示调试信息
		
		MimeMessage message=new MimeMessage(session);
		//给消息对象设置发件人/收件人/主题/发信时间
		//发件人的地址
		InternetAddress from=new InternetAddress(myMailAdd);
		message.setFrom(from);
		//收件人的地址
		InternetAddress to=new InternetAddress(recMailAdd);
		message.setRecipient(Message.RecipientType.TO,to);
		//邮件标题
		message.setSubject(mailTitle);
		message.setSentDate(new Date());
		//设置内容格式和编码方式
		message.setContent(mailContent,"text/html;charset=utf-8");
		
		message.saveChanges();
		
		//发送邮件
	    Transport transport=session.getTransport();
	    transport.connect(myMailAdd,myMailPassword);
	  //发送邮件,第二个参数是所有已设好的收件人地址 (说明可以存储多个收件人地址)
	    transport.sendMessage(message,message.getAllRecipients());
	    transport.close();
	}
	
	
	//带附件发送邮件
	public void sendWithFilesTo(String mailTitle,String mailContent,String recMailAdd,ArrayList<String> filePaths) throws MessagingException, UnsupportedEncodingException{
		//配置文件
		Properties props = new Properties();
		props.put("mail.smtp.host","smtp.163.com");//发件人用来发邮件的电子信箱服务器
		props.put("mail.smtp.auth","true"); //通过验证
		
		Session session=Session.getInstance(props);
		//session.setDebug(true);//显示调试信息
		
		MimeMessage message=new MimeMessage(session);
		//给消息对象设置发件人/收件人/主题/发信时间
		//发件人的地址
		InternetAddress from=new InternetAddress(myMailAdd);
		message.setFrom(from);
		//收件人的地址
		InternetAddress to=new InternetAddress(recMailAdd);
		message.setRecipient(Message.RecipientType.TO,to);
		//邮件标题
		message.setSubject(mailTitle);
		message.setSentDate(new Date());
		
		//新建一个存放信件内容的BodyPart对象
		BodyPart mdp=new MimeBodyPart();
		//设置内容格式和编码方式
		mdp.setContent(mailContent,"text/html;charset=utf-8");
		//新建一个MimeMultipart对象用来存放BodyPart对象(事实上可以存放多个)
	    Multipart mm=new MimeMultipart();
	    mm.addBodyPart(mdp);//将BodyPart加入到MimeMultipart对象中(可以加入多个BodyPart)
	    if(filePaths!=null){
	    	for(int i=0;i<filePaths.size();i++){
	    		BodyPart attachmentBodyPart = new MimeBodyPart();
	    		DataSource source = new FileDataSource(filePaths.get(i));
	            attachmentBodyPart.setDataHandler(new DataHandler(source));
	             
	            // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
	         
	            //此处用于获取文件名
	            File tempFile =new File(filePaths.get(i));  
	            String fileName = tempFile.getName();  
	             
	            //MimeUtility.encodeWord可以避免文件名乱码
	            attachmentBodyPart.setFileName(MimeUtility.encodeWord(fileName));
	            mm.addBodyPart(attachmentBodyPart);
	    	}
	    }
	    message.setContent(mm);//把mm作为消息对象的内容
		message.saveChanges();
		
		//发送邮件
	    Transport transport=session.getTransport();
	    transport.connect(myMailAdd,myMailPassword);
	  //发送邮件,第二个参数是所有已设好的收件人地址 (说明可以存储多个收件人地址)
	    transport.sendMessage(message,message.getAllRecipients());
	    transport.close();
	}

	public static void main(String [] args){
		
		ArrayList<String> filePaths=new ArrayList<String> ();
		filePaths.add("D://一些账号.txt");
		//设置用户名和密码
		SendMail send=new SendMail("hzq0320@163.com","hzq970320");
		try {
			//send.sendWithFilesTo("下午好","你好啊","1975798751@qq.com",filePaths);
			send.sendTo("早上好", "你好啊", "1975798751@qq.com");
			System.out.println("发送成功");
		} catch (MessagingException  e){
			e.printStackTrace();
		}
		
	}
	

}

