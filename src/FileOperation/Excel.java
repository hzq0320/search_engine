package FileOperation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Excel {
	
	//将excel内容读出到String链表中
	public static void read(String path,ArrayList<String> l){
		try{  
			File file=new File(path); 
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));  
            int sum_sheet=wb.getNumberOfSheets();
            for(int si=0;si<sum_sheet;si++){
            	XSSFSheet sheet=wb.getSheetAt(si);
            	for(int ri=sheet.getFirstRowNum();ri<=sheet.getLastRowNum();ri++){
            		XSSFRow row=sheet.getRow(ri);
            		int sum_cell=row.getRowNum();
            		String s="";
            		for(int ci=row.getFirstCellNum();ci<row.getLastCellNum();ci++){
            			XSSFCell cell=row.getCell(ci);           			
            			if(cell.toString().length()>2&&(cell.toString().substring(cell.toString().length()-2,cell.toString().length()).equals(".0"))){
            				s+=cell.toString().substring(0,cell.toString().length()-2)+"\t";
            			}
            			else{
            				s+=""+cell+"\t";
            			}
            		}
            		l.add(s);
            	}
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	//读入大数据
	public static ArrayList<String> readfrom(String path){
		ArrayList<String> l=new ArrayList<String> ();
		try{  
			File file=new File(path); 
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));  
            int sum_sheet=wb.getNumberOfSheets();
           // System.out.println(sum_sheet);
            for(int si=0;si<sum_sheet;si++){
            	XSSFSheet sheet=wb.getSheetAt(si);
            	for(int ri=sheet.getFirstRowNum();ri<=sheet.getLastRowNum();ri++){
            		XSSFRow row=sheet.getRow(ri);
            		int sum_cell=row.getRowNum();
            		String s="";
            		for(int ci=row.getFirstCellNum();ci<row.getLastCellNum();ci++){
            			XSSFCell cell=row.getCell(ci);           			
            			if(cell.toString().length()>2&&(cell.toString().substring(cell.toString().length()-2,cell.toString().length()).equals(".0"))){
            				s+=cell.toString().substring(0,cell.toString().length()-2)+"\t";
            			}
            			else{
            				s+=cell.toString()+"\t";
            			}
            		}
            		//System.out.println(s);
            		l.add(s);
            	}
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return l;
	}
	
	//读入大数据(从File文件)
	public static ArrayList<String> readfrom(File file){
		ArrayList<String> l=new ArrayList<String> ();
		try{  
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));  
            int sum_sheet=wb.getNumberOfSheets();
           // System.out.println(sum_sheet);
            for(int si=0;si<sum_sheet;si++){
            	XSSFSheet sheet=wb.getSheetAt(si);
            	for(int ri=sheet.getFirstRowNum();ri<=sheet.getLastRowNum();ri++){
            		XSSFRow row=sheet.getRow(ri);
            		int sum_cell=row.getRowNum();
            		String s="";
            		for(int ci=row.getFirstCellNum();ci<row.getLastCellNum();ci++){
            			XSSFCell cell=row.getCell(ci);           			
            			if(cell.toString().length()>2&&(cell.toString().substring(cell.toString().length()-2,cell.toString().length()).equals(".0"))){
            				s+=cell.toString().substring(0,cell.toString().length()-2)+"\t";
            			}
            			else{
            				s+=cell.toString()+"\t";
            			}
            		}
            		//System.out.println(s);
            		l.add(s);
            	}
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return l;
	}
		
	
	//加第一行属性行，导入大数据
	public static void writeto(String excelpath,ArrayList<String> l,String first){
		l.add(first);
		//先新建一个文件
		try{
			Workbook wb=new SXSSFWorkbook(100);		//新建一个excel
			Sheet sheet =wb.createSheet("第一个sheet");		//新建一个sheet
			
			CellStyle style = wb.createCellStyle();  	//此行与下一行为设定格式
			
			for(int i=0;i<l.size();i++){
				System.out.println(i+"\t"+l.size()+">>");
				Row	row=sheet.createRow(i); //在现有行号后追加数据
				String s=l.get(i);
				String s1[]=s.split("\t");
				for(int j=0;j<s1.length;j++){
					Cell cell=row.createCell(j);		//该行第一个单元格
					cell.setCellValue(s1[j]);		//该行第一个单元格的值
					cell.setCellStyle(style);		//该行第一个单元格的类型
				}
			}
			
			FileOutputStream os= null;
	        os = new FileOutputStream(excelpath);		//如果path不存在文件会自动创建
	        wb.write(os);
	        os.flush();
	        os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("写入成功!");			
	}
	
	//导入大数据
	public static void writeto(String excelpath,ArrayList<String> l){
		//先新建一个文件
		try{
			Workbook wb=new SXSSFWorkbook(100);		//新建一个excel
			Sheet sheet =wb.createSheet("第一个sheet");		//新建一个sheet
			
			CellStyle style = wb.createCellStyle();  	//此行与下一行为设定格式
			
			for(int i=0;i<l.size();i++){
				System.out.println(i+"\t"+l.size()+">>");
				Row	row=sheet.createRow(i); //在现有行号后追加数据
				String s=l.get(i);
				String s1[]=s.split("\t");
				for(int j=0;j<s1.length;j++){
					Cell cell=row.createCell(j);		//该行第一个单元格
					cell.setCellValue(s1[j]);		//该行第一个单元格的值
					cell.setCellStyle(style);		//该行第一个单元格的类型
				}
			}
			
			FileOutputStream os= null;
	        os = new FileOutputStream(excelpath);		//如果path不存在文件会自动创建
	        wb.write(os);
	        os.flush();
	        os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("写入成功!");			
	}
	
	//主函数
	public static void main(String[] args) {
		
		/*String path="D://天猫评价/test/2.xlsx";
		ArrayList<String> l=new ArrayList<String> ();
		for(int i=0;i<500000;i++){
			l.add("i\t"+i);
		}
		writeto(path,l);
		ArrayList re=readfrom(path);
		System.out.println(re.size());
		for(int i=0;i<re.size();i++){
			System.out.println(re.get(i));
		}*/
		
		/*long start=Time.Time.gettime13();
		String path="D://天猫评价/texts/04_20/text_1492678066320.xlsx";
		ArrayList<String> l=readfrom(path);
		System.out.println("用时"+(Time.Time.gettime13()-start)+"毫秒");
		System.out.println(l.size());*/
		
	}

}
