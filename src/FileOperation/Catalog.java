package FileOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

/** 
* @author 作者 E-mail: zhiqianghu0320@gmail.com
* @version 创建时间：2017年4月1日 下午8:29:34 
* 类说明 :文件目录的一些简单操作,包括判断文件是否存在,文件夹是否存在,获取系统根目录,当前路径下深度搜索文件或目录,删除文件,删除文件夹,复制文件等,
*/
public class Catalog {

	// 判断文件是否存在
	// 存在返回true,否则创建新的文件并返回true,只有文件不存在且创建新文件失败时才返回false
    public static boolean fileExists(String filepath) {
    	File file=new File(filepath);
        if(file.exists()){
            return true;
        }else{
            try{
            	//创建其父目录
            	int dirEnd=filepath.lastIndexOf("/");
            	dirExists(filepath.substring(0,dirEnd));
                
            	file.createNewFile();
                return true;
            }catch (IOException e) {
            	e.printStackTrace();
                return false;
            }
        }
    }
    
    // 判断文件夹是否存在
 	// File存在且是文件夹则返回true,否则创建新的文件夹并返回true,只有File存在但不是文件夹时才返回false
    public static boolean dirExists(String dirpath) {
    	File file=new File(dirpath);
     	if(file.exists()){
     		if(file.isDirectory()){
     			return true;
            }else{
                return false;
            }
        }else{
        	/*file.mkdirs可以创建多层文件夹,file.mkdir只能创建一层文件夹
        	例如：当前在D://CSDN文件夹下为空,当File file=new File("D://CSDN/FileOperation/Catalog/")时,
        	file.mkdirs()会依次创建FileOperation及其子目录Catalog,file.mkdir()运行不出错但是未成功创建多层目录
        	当File file=new File("D://CSDN/FileOperation/")时,二者效果相同
        	*/
            file.mkdirs();
            return true;
        }
    }

    //得到系统根目录,保存到链表中并返回
  	public static ArrayList<String> getRoots(){
  		ArrayList<String> roots=new ArrayList<String> ();
  		for (File f : FileSystemView.getFileSystemView().getHomeDirectory().listFiles()) {
  			//获取"我的电脑"文件对象
  		    if (f.getName().equals("::{20D04FE0-3AEA-1069-A2D8-08002B30309D}")) {
  		        for (File sf : f.listFiles()) {
  		        	if(sf.getPath().substring(sf.getPath().length()-1,sf.getPath().length()).equals("\\")){
  		        		roots.add(sf.getPath());
  		        	}
  		        }
  		    }
  		}
  		return roots;
  	}
    
  	//此方法指定访问的深度,否则可能访问不完
  	//显示当前目录下所有文件或文件夹,文件夹绝对路径保存到dirs中,文件绝对路径保存到files中
  	//当前是第first层,进一个子目录加一层,在第end层就退出了(不读取当前层)
  	public static void getDirsAndFiles(String nowPath,ArrayList<String> dirs,ArrayList<String> files,int start,int end){
  		if(start>=end){
  			return ;
  		}
  		try{
  			File file=new File(nowPath);
  			File[] allFiles = file.listFiles();
  			for(int i=0;i<allFiles.length;i++){
  				if(allFiles[i].isDirectory())
  				{
  					dirs.add(allFiles[i].getAbsolutePath());
  					getDirsAndFiles(allFiles[i].getAbsolutePath(),dirs,files,start+1,end);
  				}
  				
  				if(allFiles[i].isFile()){
  					files.add(allFiles[i].getAbsolutePath());
  				}
  			}
  		}catch(Exception e){
  			e.printStackTrace();
  			return ;
  		}
  	}
  	
  	//此方法指定访问的深度,否则可能访问不完
  	//显示当前目录下所有文件,文件绝对路径保存到files中
  	public static ArrayList<String> getFiles(String nowPath){
  		ArrayList<String> files=new ArrayList<String> ();
  		try{
  			File file=new File(nowPath);
  			File[] allFiles = file.listFiles();
  			for(int i=0;i<allFiles.length;i++){
  				if(allFiles[i].isFile()){
  					files.add(allFiles[i].getAbsolutePath());
  				}
  			}
  			return files;
  		}catch(Exception e){
  			e.printStackTrace();
  			return null;
  		}
  	}
  	
  	//删除文件
  	public static boolean delete(String fileName){
        File file = new File(fileName);
        if(!file.exists()){
            return true;
        }else{
            if(file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }
  	
  	//删除文件
  	public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        //如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if(file.exists()&&file.isFile()){
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

  	//删除目录及目录下的文件
    public static boolean deleteDirectory(String dirPath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if(!dirPath.endsWith(File.separator)){
        	dirPath = dirPath + File.separator;
        }
        File dirFile = new File(dirPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if((!dirFile.exists())||(!dirFile.isDirectory())){
            return false;
        }
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                if(!deleteFile(files[i].getAbsolutePath())){
                	return false;
                }
            }
            // 删除子目录
            else if(files[i].isDirectory()){
                if(!deleteDirectory(files[i].getAbsolutePath())){
                	return false;
                }
            }
        }
        // 删除当前目录
        if(dirFile.delete()){
            return true;
        }else{
            return false;
        }
    }

    
    //重命名文件(必须在同一目录下,所以需要一个path)
  	//返回false表示旧文件名和新文件名不在一个目录下或者旧文件名的文件不存在或者目录下已经有一个文件和新文件名相同
  	//当然也可以以int或byte类型作为返回值便于判断错误类型
  	public static boolean renameFile(String oldname,String newname){ 
  		//新的文件名和以前文件名不同时,才有必要进行重命名 
  		if(!oldname.equals(newname)){
          	int oldDirEnd=oldname.lastIndexOf("/");
          	int newDirEnd=newname.lastIndexOf("/");
          	//必须在同一个文件夹下才能重命名,否则就可能要创建目录了
          	if(!oldname.substring(0,oldDirEnd).equals(newname.substring(0,newDirEnd))){
          		return false;
          	}
              File oldfile=new File(oldname);
              //需要重命名的文件不存在
              if(!oldfile.exists()){
                  return false;
              }
              File newfile=new File(newname);
              //若在该目录下已经有一个文件和新文件名相同，则不允许重命名
              if(newfile.exists()) 
                  return false; 
              else{ 
                  oldfile.renameTo(newfile); 
                  return true;
              } 
          }else{
              return true;
          }
      }
  	
  	
  	//复制filePath的文件到dirPath目录
  	//只有复制成功且通道正常关闭才返回true
  	@SuppressWarnings("finally")
	public static boolean copy(String filePath,String dirPath) {
  		File oldfile=new File(filePath);
  		File newfile=new File(dirPath);
  		
  		int fileNameStart=filePath.lastIndexOf("/");
  		newfile=new File(dirPath+filePath.substring(fileNameStart,filePath.length()));
  		
  		FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        boolean result=false;
        try{
            fi = new FileInputStream(oldfile);
            fo = new FileOutputStream(newfile);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
            result=true;
        }catch(IOException e){
            e.printStackTrace();
        }finally{
        	try{
                fi.close();
                in.close();
                fo.close();
                out.close();
            }catch(IOException e){
            	result=false;
            	e.printStackTrace();
            }
        	return result;
        }
  	}
  	
	public static void main(String[] args) {
		
		/*//测试该路径文件夹是否存在
		String dirpath="D://CSDN/FileOperation/Catalog/";
		if(dirExists(dirpath)==true){
			System.out.println("This folder exists.");
		}*/
		
		/*//测试该路径文件是否存在
		String filepath="D://CSDN/FileOperation/Catalog/MyTest.txt";
		if(fileExists(filepath)==true){
			System.out.println("This file exists.");
		}*/
		
		
		/*//测试获取系统根目录
		ArrayList<String> roots=new ArrayList<String> ();
		getRoots(roots);
		for(int i=0;i<roots.size();i++){
			System.out.println(roots.get(i));
		}*/
		
		
		/*//先创建一个文件(同时创建其各层父目录)
		String filepath="D://CSDN/FileOperation/Catalog/MyTest.txt";
		if(fileExists(filepath)==true){
			System.out.println("This file exists.");
		}
		//测试获取目录下所有文件夹及文件绝对路径
		ArrayList<String> dirs=new ArrayList<String> ();
		ArrayList<String> files=new ArrayList<String> ();
		String nowpath="D://CSDN/";
		getDirsAndFiles(nowpath,dirs,files,1,4);
		System.out.println(dirs.toString());
		System.out.println(files.toString());*/
		
		/*//测试删除目录下所有目录及其文件,并删除该目录
		String filepath="D://CSDN/FileOperation";
		delete(filepath);*/
		
		/*//测试重命名文件
		String oldname="D://CSDN/FileOperation/Catalog/MyTest.txt";
		String newname="D://CSDN/FileOperation/Catalog/MyTest2.txt";
		if(renameFile(oldname,newname)==true){
			System.out.println("Rename success.");
		}*/
		
		//测试复制文件
		String filePath="D://CSDN/test.txt";
		String dirPath="D://CSDN/Dir/";
		if(copy(filePath,dirPath)==true){
			System.out.println("Copy success.");
		}
	}

}
