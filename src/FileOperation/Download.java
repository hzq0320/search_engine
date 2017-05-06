package FileOperation;
/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月22日 上午9:06:05 
* 类说明 
*/
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

public class Download {

	
	//
	public HttpServletResponse download(String path, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public void downloadLocal(HttpServletResponse response) throws FileNotFoundException {
        // 下载本地文件
        String fileName = "Operator.doc".toString(); // 文件的默认保存名
        // 读到流中
        InputStream inStream = new FileInputStream("c:/Operator.doc");// 文件的存放路径
        // 设置输出的格式
        response.reset();
        response.setContentType("bin");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        try {
            while ((len = inStream.read(b)) > 0)
                response.getOutputStream().write(b, 0, len);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    //从uri中下载数据到path路径
    @SuppressWarnings("deprecation")
	public static boolean downloadNet(String uri,String path) {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        for(int i=0;i<10;i++){
        	try{
        		 URL url = new URL(uri);
        		 URLConnection conn = url.openConnection();
        		 conn.addRequestProperty("cookie", "MicrosoftApplicationsTelemetryDeviceId=3175595b-54b5-fc5b-6867-4baf7beb95cc; MicrosoftApplicationsTelemetryFirstLaunchTime=1485160304834; mtstkn=aomqJ%2Fm7c%2BqRH9IXkOgKarNprLjLcwUUMnru%2FJ8LM3b3geWI2mL4QCtfSj%2F0tgva; destLang=en; dmru_list=da%2Cen; destDia=en-US; srcLang=-; smru_list=; sourceDia=zh-CN; sourceGender=female; MUID=0755F5F8765960EC0836FCA2725966AF; SRCHD=AF=NOFORM; SRCHUSR=DOB=20160722; MUIDB=0755F5F8765960EC0836FCA2725966AF; SRCHUID=V=2&GUID=C8903766D6754356801FD7C07BEB388A; __guid=3935670.275563628409258140.1470450308044.8105; SRCHHPGUSR=CW=1349&CH=617&DPR=1&UTC=480; _EDGE_S=SID=11A86025E03463C72D346A1CE19562FD; KievRPSAuth=FABSARRaTOJILtFsMkpLVWSG6AN6C/svRwNmAAAEgAAACKmXcSe1wRqXEAF08rNySiLeuVYuG5Z2zGrgdVvvM3dd2D44S5ZrU0/Qq%2B01d5NH/cx9Rc%2BWNky9qvJzO9xdLwSfn7UM54lkwVT/Q%2BBk5ikYTxt3Mifz9sp9Sz0P%2BFhYT/n4W2uHQY9B9h7SZFcMaN7ppalkvPudteUwDj%2BRFmu/NtDHtJ8hARLGUOr3gbul4pTV22yMmvLUH/5PUOTZU%2BJOoR7kJsUDEsQnCIRTgbLf/maD%2BunrMHIOvtrdtGgeSuUK7bd7GOv93QItsnNFDzG%2BoCgOAsRvgXKxK5ReM8ATyt3nPAW7MnThWettxpUwGjVPY4G32LHkF%2BxfF8TkdnD7Jb5hDJIw5MDdsgUKxjyEoVvY4VluEWyDCRQA9tucSgza2xPkSsej5R5Kp0/kz2s%3D; PPLState=1; ANON=A=F1C7A20E2AA9B72AED51052DFFFFFFFF&E=1302&W=1; NAP=V=1.9&E=1323&C=-YVvCHaV4-cp_GXBcHVGIYAphOaXufhHuzwfmHx5rJm-YGbe3hXakQ&W=1; WLID=GSNusV0x+gdU5Y5rQwdFIuryar6/Y3d5l+mk8COo/pOxjWPSYM6kLh9Y0v/bzGmE31TnwKUu3ZqMHc9JN9kTu28Uhspd84NOqF6kun5a/f8=; _SS=SID=11A86025E03463C72D346A1CE19562FD; _U=1MV5ZphW2pE4NIjz4EFJNQ45_D5gJyoem0vkdNcPPMHO4pdq8owSbJsoEi48PqhGwMeAZ0mnJ7OZe8UAsJBo7PCOp9Z6KwW85RnOv-FYZFz0bOfdVf1EQO6zEo_KF67Hg65lJReGELr4emEVbuX9EWA; WLS=TS=63624061833&C=04b1ea8d57e65e02&N=%e5%bf%97%e5%bc%ba");
 	             
        		// conn.setDefaultRequestProperty("cookie", "MicrosoftApplicationsTelemetryDeviceId=3175595b-54b5-fc5b-6867-4baf7beb95cc; MicrosoftApplicationsTelemetryFirstLaunchTime=1485160304834; mtstkn=aomqJ%2Fm7c%2BqRH9IXkOgKarNprLjLcwUUMnru%2FJ8LM3b3geWI2mL4QCtfSj%2F0tgva; destLang=en; dmru_list=da%2Cen; destDia=en-US; srcLang=-; smru_list=; sourceDia=zh-CN; sourceGender=female; MUID=0755F5F8765960EC0836FCA2725966AF; SRCHD=AF=NOFORM; SRCHUSR=DOB=20160722; MUIDB=0755F5F8765960EC0836FCA2725966AF; SRCHUID=V=2&GUID=C8903766D6754356801FD7C07BEB388A; __guid=3935670.275563628409258140.1470450308044.8105; SRCHHPGUSR=CW=1349&CH=617&DPR=1&UTC=480; _EDGE_S=SID=11A86025E03463C72D346A1CE19562FD; KievRPSAuth=FABSARRaTOJILtFsMkpLVWSG6AN6C/svRwNmAAAEgAAACKmXcSe1wRqXEAF08rNySiLeuVYuG5Z2zGrgdVvvM3dd2D44S5ZrU0/Qq%2B01d5NH/cx9Rc%2BWNky9qvJzO9xdLwSfn7UM54lkwVT/Q%2BBk5ikYTxt3Mifz9sp9Sz0P%2BFhYT/n4W2uHQY9B9h7SZFcMaN7ppalkvPudteUwDj%2BRFmu/NtDHtJ8hARLGUOr3gbul4pTV22yMmvLUH/5PUOTZU%2BJOoR7kJsUDEsQnCIRTgbLf/maD%2BunrMHIOvtrdtGgeSuUK7bd7GOv93QItsnNFDzG%2BoCgOAsRvgXKxK5ReM8ATyt3nPAW7MnThWettxpUwGjVPY4G32LHkF%2BxfF8TkdnD7Jb5hDJIw5MDdsgUKxjyEoVvY4VluEWyDCRQA9tucSgza2xPkSsej5R5Kp0/kz2s%3D; PPLState=1; ANON=A=F1C7A20E2AA9B72AED51052DFFFFFFFF&E=1302&W=1; NAP=V=1.9&E=1323&C=-YVvCHaV4-cp_GXBcHVGIYAphOaXufhHuzwfmHx5rJm-YGbe3hXakQ&W=1; WLID=GSNusV0x+gdU5Y5rQwdFIuryar6/Y3d5l+mk8COo/pOxjWPSYM6kLh9Y0v/bzGmE31TnwKUu3ZqMHc9JN9kTu28Uhspd84NOqF6kun5a/f8=; _SS=SID=11A86025E03463C72D346A1CE19562FD; _U=1MV5ZphW2pE4NIjz4EFJNQ45_D5gJyoem0vkdNcPPMHO4pdq8owSbJsoEi48PqhGwMeAZ0mnJ7OZe8UAsJBo7PCOp9Z6KwW85RnOv-FYZFz0bOfdVf1EQO6zEo_KF67Hg65lJReGELr4emEVbuX9EWA; WLS=TS=63624061833&C=04b1ea8d57e65e02&N=%e5%bf%97%e5%bc%ba");
 	             InputStream inStream = conn.getInputStream();
 	             FileOutputStream fs = new FileOutputStream(path);
 	             
 	             byte[] buffer = new byte[1204];
 	             int length;
 	             while ((byteread = inStream.read(buffer)) != -1) {
 	                bytesum += byteread;
 	               // System.out.println(bytesum);
 	                fs.write(buffer, 0, byteread);
 	             }
 	             return true;
        	}catch(Exception e){
        		continue;
        	}
        }
        return false;
       
    }
    
    //带cookie下载,返回值表示是否下载成功
    public static boolean downloadNet(String uri,String path,String cookies) {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        for(int i=0;i<10;i++){
        	try{
        		 URL url = new URL(uri);
        		 URLConnection conn = url.openConnection();
        		 conn.addRequestProperty("cookie",cookies);
        		// conn.setDefaultRequestProperty("cookie", "MicrosoftApplicationsTelemetryDeviceId=3175595b-54b5-fc5b-6867-4baf7beb95cc; MicrosoftApplicationsTelemetryFirstLaunchTime=1485160304834; mtstkn=aomqJ%2Fm7c%2BqRH9IXkOgKarNprLjLcwUUMnru%2FJ8LM3b3geWI2mL4QCtfSj%2F0tgva; destLang=en; dmru_list=da%2Cen; destDia=en-US; srcLang=-; smru_list=; sourceDia=zh-CN; sourceGender=female; MUID=0755F5F8765960EC0836FCA2725966AF; SRCHD=AF=NOFORM; SRCHUSR=DOB=20160722; MUIDB=0755F5F8765960EC0836FCA2725966AF; SRCHUID=V=2&GUID=C8903766D6754356801FD7C07BEB388A; __guid=3935670.275563628409258140.1470450308044.8105; SRCHHPGUSR=CW=1349&CH=617&DPR=1&UTC=480; _EDGE_S=SID=11A86025E03463C72D346A1CE19562FD; KievRPSAuth=FABSARRaTOJILtFsMkpLVWSG6AN6C/svRwNmAAAEgAAACKmXcSe1wRqXEAF08rNySiLeuVYuG5Z2zGrgdVvvM3dd2D44S5ZrU0/Qq%2B01d5NH/cx9Rc%2BWNky9qvJzO9xdLwSfn7UM54lkwVT/Q%2BBk5ikYTxt3Mifz9sp9Sz0P%2BFhYT/n4W2uHQY9B9h7SZFcMaN7ppalkvPudteUwDj%2BRFmu/NtDHtJ8hARLGUOr3gbul4pTV22yMmvLUH/5PUOTZU%2BJOoR7kJsUDEsQnCIRTgbLf/maD%2BunrMHIOvtrdtGgeSuUK7bd7GOv93QItsnNFDzG%2BoCgOAsRvgXKxK5ReM8ATyt3nPAW7MnThWettxpUwGjVPY4G32LHkF%2BxfF8TkdnD7Jb5hDJIw5MDdsgUKxjyEoVvY4VluEWyDCRQA9tucSgza2xPkSsej5R5Kp0/kz2s%3D; PPLState=1; ANON=A=F1C7A20E2AA9B72AED51052DFFFFFFFF&E=1302&W=1; NAP=V=1.9&E=1323&C=-YVvCHaV4-cp_GXBcHVGIYAphOaXufhHuzwfmHx5rJm-YGbe3hXakQ&W=1; WLID=GSNusV0x+gdU5Y5rQwdFIuryar6/Y3d5l+mk8COo/pOxjWPSYM6kLh9Y0v/bzGmE31TnwKUu3ZqMHc9JN9kTu28Uhspd84NOqF6kun5a/f8=; _SS=SID=11A86025E03463C72D346A1CE19562FD; _U=1MV5ZphW2pE4NIjz4EFJNQ45_D5gJyoem0vkdNcPPMHO4pdq8owSbJsoEi48PqhGwMeAZ0mnJ7OZe8UAsJBo7PCOp9Z6KwW85RnOv-FYZFz0bOfdVf1EQO6zEo_KF67Hg65lJReGELr4emEVbuX9EWA; WLS=TS=63624061833&C=04b1ea8d57e65e02&N=%e5%bf%97%e5%bc%ba");
 	             InputStream inStream = conn.getInputStream();
 	             FileOutputStream fs = new FileOutputStream(path);
 	             
 	             byte[] buffer = new byte[1204];
 	             int length;
 	             while ((byteread = inStream.read(buffer)) != -1) {
 	                bytesum += byteread;
 	               // System.out.println(bytesum);
 	                fs.write(buffer, 0, byteread);
 	             }
 	             return true;
        	}catch(Exception e){
        		e.printStackTrace();
        		continue;
        	}
        }
        return false;
       
    }
	
    
	public static void main(String[] args) throws MalformedURLException {
		// TODO 自动生成的方法存根

		
		//这样可以下载图片
		/*String s="http://jxgl.hdu.edu.cn/readimagexs.aspx?xh=14071209";
	//	String s="http://www.bing.com/translator/api/language/Speak?locale=zh-CN&gender=male&media=audio/mp3&text=%E4%BD%A0%E5%A5%BD";
		String cookies="ASP.NET_SessionId=kr0efq55zbklu045yhv50o45; route=ae959e1acabd9d993534b51ab78f097b";
		downloadNet(s,"C://Users/须儿胡/Desktop/2.png",cookies);*/
		
		
		/*//下载个人信息
		String s="http://jxgl.hdu.edu.cn/readimagexs.aspx?xh=14071209";
		//	String s="http://www.bing.com/translator/api/language/Speak?locale=zh-CN&gender=male&media=audio/mp3&text=%E4%BD%A0%E5%A5%BD";
		String cookies="route=4376efc7edf61c9fe699e82a2fb7a34f; key_dcp_cas=hWQTYHTSXq6hRdJ2mLnpzMCFGyt11Q7TgJLCYNqp2bgb1WQRMBDy!748587538";
		downloadNet(s,"C://Users/须儿胡/Desktop/1.png",cookies);*/
		
	}

}
