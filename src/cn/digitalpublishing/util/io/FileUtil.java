package cn.digitalpublishing.util.io;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class FileUtil {

	/*
	 * Description : 创建文件夹
	 * 
	 * @param folderPath
	 * 要创建的文件夹的完整路径 例如 d:/x/y
	 * 
	 */
	public static void newFolder(String folderPath) {
		try {
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			System.out.println(myFilePath);
			if (!myFilePath.exists()&&!myFilePath.isDirectory()) {
				myFilePath.mkdirs();
				System.out.println("目录创建成功！");
			}
		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			//e.printStackTrace();
		}
	}

	/*
	 *  新建文件  
	 *  
	 *  @param  filePathAndName
	 *  文件路径及名称  如c:/fqf.txt  
	 *  
	 *  @param  fileContent
	 *  文件内容  
	 *  
	 */
	public static void newFile(String filePathAndName, String fileContent) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				myFilePath.createNewFile();
			}
			FileWriter resultFile = new FileWriter(myFilePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			String strContent = fileContent;
			myFile.println(strContent);
			resultFile.close();

		} catch (Exception e) {
			System.out.println("新建目录操作出错");
			//e.printStackTrace();

		}
	}

	/*  
	 *  删除文件  
	 *  @param  filePathAndName  String  
	 *  文件路径及名称  如c:/fqf.txt 
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			//e.printStackTrace();

		}

	}

	/*  
	 *  删除文件夹里面的所有文件  
	 *  @param  path  String  文件夹路径  如  c:/fqf  
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件  
				delFolder(path + "/" + tempList[i]);//再删除空文件夹  
			}
		}
	}

	/*  
	 *  删除文件夹  
	 *  @param  filePathAndName  String  文件夹路径及名称  如c:/fqf  
	 *  @param  fileContent  String  
	 *  @return  boolean  
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); //删除完里面所有内容  
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); //删除空文件夹  

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			//e.printStackTrace();

		}
	}

	/*  
	 *  复制单个文件  
	 *  @param  oldPath  String  原文件路径  如：c:/fqf.txt  
	 *  @param  newPath  String  复制后路径  如：f:/fqf.txt  
	 *  @return  boolean  
	 */
	@SuppressWarnings({ "resource", "unused" })
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件存在时  
				InputStream inStream = new FileInputStream(oldPath); //读入原文件  
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[8 * 1024];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数  文件大小  
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			//e.printStackTrace();
		}

	}

	/*  
	 *  复制整个文件夹内容  
	 *  @param  oldPath  String  原文件路径  如：c:/fqf  
	 *  @param  newPath  String  复制后路径  如：f:/fqf/ff  
	 *  @return  boolean  
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); //如果文件夹不存在  则建立新文件夹  
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 8];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {//如果是子文件夹  
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			//e.printStackTrace();

		}
	}

	/*  
	 *  移动文件到指定目录  
	 *  @param  oldPath  String  如：c:/fqf.txt  
	 *  @param  newPath  String  如：d:/fqf.txt  
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);

	}

	/*  
	 *  移动文件到指定目录  
	 *  @param  oldPath  String  如：c:/fqf.txt  
	 *  @param  newPath  String  如：d:/fqf.txt  
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);

	}

	public static String getFix(String fileName) {
		int pos = fileName.lastIndexOf(".");
		if (pos == -1) {
			return null;
		} else {
			return fileName.substring(pos+1, fileName.length());
		}
	}

	public static void copy(File src, File dst) {
		int BUFFER_SIZE = 8 * 1024;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(dst),
					BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
		}
	}
	
	
	
	private static HashMap<String,String> h = new HashMap<String,String>();
	
	static {
		  h.put("","application/octet-stream");
		  h.put("323","text/h323");
		  h.put("acx","application/internet-property-stream");
		  h.put("ai","application/postscript");
		  h.put("aif","audio/x-aiff");
		  h.put("aifc","audio/x-aiff");
		  h.put("aiff","audio/x-aiff");
		  h.put("asf","video/x-ms-asf");
		  h.put("asr","video/x-ms-asf");
		  h.put("asx","video/x-ms-asf");
		  h.put("au","audio/basic");
		  h.put("avi","video/x-msvideo");
		  h.put("axs","application/olescript");
		  h.put("bas","text/plain");
		  h.put("bcpio","application/x-bcpio");
		  h.put("bin","application/octet-stream");
		  h.put("bmp","image/bmp");
		  h.put("c","text/plain");
		  h.put("cat","application/vnd.ms-pkiseccat");
		  h.put("cdf","application/x-cdf");
		  h.put("cer","application/x-x509-ca-cert");
		  h.put("class","application/octet-stream");
		  h.put("clp","application/x-msclip");
		  h.put("cmx","image/x-cmx");
		  h.put("cod","image/cis-cod");
		  h.put("cpio","application/x-cpio");
		  h.put("crd","application/x-mscardfile");
		  h.put("crl","application/pkix-crl");
		  h.put("crt","application/x-x509-ca-cert");
		  h.put("csh","application/x-csh");
		  h.put("css","text/css");
		  h.put("dcr","application/x-director");
		  h.put("der","application/x-x509-ca-cert");
		  h.put("dir","application/x-director");
		  h.put("dll","application/x-msdownload");
		  h.put("dms","application/octet-stream");
		  h.put("doc","application/msword");
		  h.put("dot","application/msword");
		  h.put("dvi","application/x-dvi");
		  h.put("dxr","application/x-director");
		  h.put("eps","application/postscript");
		  h.put("etx","text/x-setext");
		  h.put("evy","application/envoy");
		  h.put("exe","application/octet-stream");
		  h.put("fif","application/fractals");
		  h.put("flr","x-world/x-vrml");
		  h.put("gif","image/gif");
		  h.put("gtar","application/x-gtar");
		  h.put("gz","application/x-gzip");
		  h.put("h","text/plain");
		  h.put("hdf","applicatin/x-hdf");
		  h.put("hlp","application/winhlp");
		  h.put("hqx","application/mac-binhex40");
		  h.put("hta","application/hta");
		  h.put("htc","text/x-component");
		  h.put("htm","text/html");
		  h.put("html","text/html");
		  h.put("htt","text/webviewhtml");
		  h.put("ico","image/x-icon");
		  h.put("ief","image/ief");
		  h.put("iii","application/x-iphone");
		  h.put("ins","application/x-internet-signup");
		  h.put("isp","application/x-internet-signup");
		  h.put("jfif","image/pipeg");
		  h.put("jpe","image/jpeg");
		  h.put("jpeg","image/jpeg");
		  h.put("jpg","image/jpeg");
		  h.put("js","application/x-javascript");
		  h.put("latex","application/x-latex");
		  h.put("lha","application/octet-stream");
		  h.put("lsf","video/x-la-asf");
		  h.put("lsx","video/x-la-asf");
		  h.put("lzh","application/octet-stream");
		  h.put("m13","application/x-msmediaview");
		  h.put("m14","application/x-msmediaview");
		  h.put("m3u","audio/x-mpegurl");
		  h.put("man","application/x-troff-man");
		  h.put("mdb","application/x-msaccess");
		  h.put("me","application/x-troff-me");
		  h.put("mht","message/rfc822");
		  h.put("mhtml","message/rfc822");
		  h.put("mid","audio/mid");
		  h.put("mny","application/x-msmoney");
		  h.put("mov","video/quicktime");
		  h.put("movie","video/x-sgi-movie");
		  h.put("mp2","video/mpeg");
		  h.put("mp3","audio/mpeg");
		  h.put("mpa","video/mpeg");
		  h.put("mpe","video/mpeg");
		  h.put("mpeg","video/mpeg");
		  h.put("mpg","video/mpeg");
		  h.put("mpp","application/vnd.ms-project");
		  h.put("mpv2","video/mpeg");
		  h.put("ms","application/x-troff-ms");
		  h.put("mvb","application/x-msmediaview");
		  h.put("nws","message/rfc822");
		  h.put("oda","application/oda");
		  h.put("p10","application/pkcs10");
		  h.put("p12","application/x-pkcs12");
		  h.put("p7b","application/x-pkcs7-certificates");
		  h.put("p7c","application/x-pkcs7-mime");
		  h.put("p7m","application/x-pkcs7-mime");
		  h.put("p7r","application/x-pkcs7-certreqresp");
		  h.put("p7s","application/x-pkcs7-signature");
		  h.put("pbm","image/x-portable-bitmap");
		  h.put("pdf","application/pdf");
		  h.put("pfx","application/x-pkcs12");
		  h.put("pgm","image/x-portable-graymap");
		  h.put("pko","application/ynd.ms-pkipko");
		  h.put("pma","application/x-perfmon");
		  h.put("pmc","application/x-perfmon");
		  h.put("pml","application/x-perfmon");
		  h.put("pmr","application/x-perfmon");
		  h.put("pmw","application/x-perfmon");
		  h.put("pnm","image/x-portable-anymap");
		  h.put("pot,","application/vnd.ms-powerpoint");
		  h.put("ppm","image/x-portable-pixmap");
		  h.put("pps","application/vnd.ms-powerpoint");
		  h.put("ppt","application/vnd.ms-powerpoint");
		  h.put("prf","application/pics-rules");
		  h.put("ps","application/postscript");
		  h.put("pub","application/x-mspublisher");
		  h.put("qt","video/quicktime");
		  h.put("ra","audio/x-pn-realaudio");
		  h.put("ram","audio/x-pn-realaudio");
		  h.put("ras","image/x-cmu-raster");
		  h.put("rgb","image/x-rgb");
		  h.put("rmi","audio/mid");
		  h.put("roff","application/x-troff");
		  h.put("rtf","application/rtf");
		  h.put("rtx","text/richtext");
		  h.put("scd","application/x-msschedule");
		  h.put("sct","text/scriptlet");
		  h.put("setpay","application/set-payment-initiation");
		  h.put("setreg","application/set-registration-initiation");
		  h.put("sh","application/x-sh");
		  h.put("shar","application/x-shar");
		  h.put("sit","application/x-stuffit");
		  h.put("snd","audio/basic");
		  h.put("spc","application/x-pkcs7-certificates");
		  h.put("spl","application/futuresplash");
		  h.put("src","application/x-wais-source");
		  h.put("sst","application/vnd.ms-pkicertstore");
		  h.put("stl","application/vnd.ms-pkistl");
		  h.put("stm","text/html");
		  h.put("svg","image/svg+xml");
		  h.put("sv4cpio","application/x-sv4cpio");
		  h.put("sv4crc","application/x-sv4crc");
		  h.put("swf","application/x-shockwave-flash");
		  h.put("t","application/x-troff");
		  h.put("tar","application/x-tar");
		  h.put("tcl","application/x-tcl");
		  h.put("tex","application/x-tex");
		  h.put("texi","application/x-texinfo");
		  h.put("texinfo","application/x-texinfo");
		  h.put("tgz","application/x-compressed");
		  h.put("tif","image/tiff");
		  h.put("tiff","image/tiff");
		  h.put("tr","application/x-troff");
		  h.put("trm","application/x-msterminal");
		  h.put("tsv","text/tab-separated-values");
		  h.put("txt","text/plain");
		  h.put("uls","text/iuls");
		  h.put("ustar","application/x-ustar");
		  h.put("vcf","text/x-vcard");
		  h.put("vrml","x-world/x-vrml");
		  h.put("wav","audio/x-wav");
		  h.put("wcm","application/vnd.ms-works");
		  h.put("wdb","application/vnd.ms-works");
		  h.put("wks","application/vnd.ms-works");
		  h.put("wmf","application/x-msmetafile");
		  h.put("wps","application/vnd.ms-works");
		  h.put("wri","application/x-mswrite");
		  h.put("wrl","x-world/x-vrml");
		  h.put("wrz","x-world/x-vrml");
		  h.put("xaf","x-world/x-vrml");
		  h.put("xbm","image/x-xbitmap");
		  h.put("xla","application/vnd.ms-excel");
		  h.put("xlc","application/vnd.ms-excel");
		  h.put("xlm","application/vnd.ms-excel");
		  h.put("xls","application/vnd.ms-excel");
		  h.put("xlt","application/vnd.ms-excel");
		  h.put("xlw","application/vnd.ms-excel");
		  h.put("xof","x-world/x-vrml");
		  h.put("xpm","image/x-xpixmap");
		  h.put("xwd","image/x-xwindowdump");
		  h.put("z","application/x-compress");
		  h.put("zip","application/zip");
	}
	
	public static String getMimeType(String docType){
		  String mime = "";
		 
		  mime = h.get(docType.toLowerCase());
		  if(mime==null){
		   mime = "application/octet-stream";
		  }
		  return mime;
		 }

	public static void compressionFiles(ZipOutputStream zosm, File file,
			String basePath) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			try {
				zosm.setEncoding("gbk");// 指定编码为gbk，否则部署到linux下会出现乱码
				zosm.putNextEntry(new ZipEntry(basePath + "/"));
			} catch (IOException e) {
				//e.printStackTrace();
			}
			basePath = basePath + ((basePath.length() == 0) ? "" : "/");
			for (File f : files) {
				if (f.isDirectory()) {
					
					compressionFiles(zosm, f, basePath + f.getName());
				} else {
					compressionFiles(zosm, f, basePath);
				}
			}
		} else {
			FileInputStream fism = null;
			BufferedInputStream bism = null;
			try {
				byte[] bytes = new byte[1024];
				fism = new FileInputStream(file);
				bism = new BufferedInputStream(fism, 1024);
				if (basePath.length() != 0
						&& !"/".equals(basePath.substring(
								basePath.length() - 1, basePath.length()))
						&& !"\\".equals(basePath.substring(
								basePath.length() - 1, basePath.length()))) {
					basePath = basePath + "/" + file.getName();
				} else {
					basePath = basePath + file.getName();
				}
				zosm.putNextEntry(new ZipEntry(basePath));
				int count;
				while ((count = bism.read(bytes, 0, 1024)) != -1) {
					zosm.write(bytes, 0, count);
				}
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			} finally {
				if (bism != null) {
					try {
						bism.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
				if (fism != null) {
					try {
						fism.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
			}
		}
	}

	
	public static Boolean isExist(String filepath)throws Exception{
		try{
			File f=new File(filepath);
			return f.exists();
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
     * 解压 zip 文件，注意不能解压 rar 文件哦，只能解压 zip 文件 解压 rar 文件 会出现 java.io.IOException: Negative  seek offset 异常 
     * create date:2012-12-18 
     * author:ruixue cheng
     * @param zipfile 
     * @param destDir 
     * @throws IOException 
     */ 
	@SuppressWarnings("rawtypes")
	public static List<String> unZip(String zipfile, String destDir){
		
		List<String> list = new ArrayList<String>();
		
		destDir = destDir.endsWith( "//" ) ? destDir : destDir + "//" ; 

		byte b[] = new byte [1024]; 

		int length; 

		ZipFile zipFile = null;
		OutputStream outputStream = null;
		try{
			zipFile = new ZipFile( new File(zipfile)); 
			Enumeration enumeration = zipFile.getEntries();
			ZipEntry zipEntry = null ; 
			while (enumeration.hasMoreElements()) { 
				zipEntry = (ZipEntry) enumeration.nextElement();
				File loadFile = new File(destDir + zipEntry.getName()); 
				if (zipEntry.isDirectory()){
					//这段都可以不要，因为每次都貌似从最底层开始遍历的 
					loadFile.mkdirs(); 
				}else{
					if(!loadFile.getParentFile().exists()) 
						loadFile.getParentFile().mkdirs(); 
					list.add(zipEntry.getName());
					outputStream = new FileOutputStream(loadFile); 
					InputStream inputStream = zipFile.getInputStream(zipEntry); 
					while ((length = inputStream.read(b)) > 0) 
						outputStream.write(b, 0, length);
				}
			} 
			System. out .println("文件解压成功"); 
	    } catch (IOException e) { 
	    	//e.printStackTrace(); 
	    }finally{
	    	if(outputStream!=null){
	    		try {
	    			outputStream.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
	    	}
	    	if(zipFile!=null){
	    		try {
	    			zipFile.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
	    	}
	    }
	    return list;
	}
	
	  public static void writeFile(String filePath, byte[] b)
	  {
	    FileOutputStream outputStream = null;
	    try {
	      outputStream = new FileOutputStream(new File(filePath));
	      outputStream.write(b);
	      outputStream.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	      File file = new File(filePath);
	      if (file.exists())
	        file.delete();
	    }
	  }
	  
		/**
		 * 生成纯静态的HTML文件
		 * 
		 * @param folderName 文件夹名称
		 * @param ftl FTL文件
		 * @param htmlName HTML文件名
		 * @param map Key-Value数据
		 * @throws IOException
		 * @throws TemplateException
		 */
		public static void generateHTML(String folderName, String ftl, String htmlName, Map<String, Object> map, ServletContext servletContext, String path) throws IOException, TemplateException {
			Configuration cfg = new Configuration();
			cfg.setServletContextForTemplateLoading(servletContext, File.separator + folderName);
			cfg.setEncoding(Locale.getDefault(), "UTF-8");
			Template template = cfg.getTemplate(ftl);
			template.setEncoding("UTF-8");
			File pathFile = new File(path);
			if (!pathFile.exists()) {
				pathFile.mkdir();
			}
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path + htmlName));
			File htmlFile = new File(path + htmlName);
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), "UTF-8"));
			template.process(map, out);
			bufferedWriter.close();
			out.flush();
			out.close();
		}
	  
		public static String clearExt(String fileName){
			Pattern p=Pattern.compile("\\.[^\\.]+$");
			Matcher matcher = p.matcher(fileName);
			return matcher.replaceFirst("");
		}
		
	public static void generateHighQulityThumbnail(String fromFileStr, String saveToFileStr, int formatWidth, int formatHeight) throws Exception {
		BufferedImage srcImage;
		File saveFile = new File(saveToFileStr);
		File fromFile = new File(fromFileStr);
		srcImage = ImageIO.read(fromFile);
		int imageWidth = srcImage.getWidth(null);
		int imageHeight = srcImage.getHeight(null);
		int changeToWidth = 0;
		int changeToHeight = 0;
		if (imageWidth > 0 && imageHeight > 0) {
			if (imageWidth / imageHeight >= formatWidth / formatHeight) {
				if (imageWidth > formatWidth) {
					changeToWidth = formatWidth;
					changeToHeight = (imageHeight * formatWidth) / imageWidth;
				} else {
					changeToWidth = imageWidth;
					changeToHeight = imageHeight;
				}
			} else {
				if (imageHeight > formatHeight) {
					changeToHeight = formatHeight;
					changeToWidth = (imageWidth * formatHeight) / imageHeight;
				} else {
					changeToWidth = imageWidth;
					changeToHeight = imageHeight;
				}
			}
		}
		srcImage = imageZoomOut(srcImage, changeToWidth, changeToHeight);
		ImageIO.write(srcImage, "PNG", saveFile);
	}
	
	private static int width;
	private static int height;
	private static int scaleWidth;
	private static double support = (double) 3.0;
	private static double PI = (double) 3.14159265358978;
	private static double[] contrib;
	private static double[] normContrib;
	private static double[] tmpContrib;
	private static int nDots;
	private static int nHalfDots;
	
	public static BufferedImage imageZoomOut(BufferedImage srcBufferImage, int w, int h) {
		width = srcBufferImage.getWidth();
		height = srcBufferImage.getHeight();
		scaleWidth = w;
		if (DetermineResultSize(w, h) == 1) {
			return srcBufferImage;
		}
		CalContrib();
		BufferedImage pbOut = HorizontalFiltering(srcBufferImage, w);
		BufferedImage pbFinalOut = VerticalFiltering(pbOut, h);
		return pbFinalOut;
	}

	private static int DetermineResultSize(int w, int h) {
		double scaleH, scaleV;
		scaleH = (double) w / (double) width;
		scaleV = (double) h / (double) height;
		if (scaleH >= 1.0 && scaleV >= 1.0) {
			return 1;
		}
		return 0;
	}

	private static double Lanczos(int i, int inWidth, int outWidth, double Support) {
		double x;
		x = (double) i * (double) outWidth / (double) inWidth;
		return Math.sin(x * PI) / (x * PI) * Math.sin(x * PI / Support) / (x * PI / Support);
	}

	private static void CalContrib() {
		nHalfDots = (int) ((double) width * support / (double) scaleWidth);
		nDots = nHalfDots * 2 + 1;
		try {
			contrib = new double[nDots];
			normContrib = new double[nDots];
			tmpContrib = new double[nDots];
		} catch (Exception e) {
			System.out.println("init contrib, normContrib, tmpContrib" + e);
		}
		int center = nHalfDots;
		contrib[center] = 1.0;
		double weight = 0.0;
		int i = 0;
		for (i = 1; i <= center; i++) {
			contrib[center + i] = Lanczos(i, width, scaleWidth, support);
			weight += contrib[center + i];
		}
		for (i = center - 1; i >= 0; i--) {
			contrib[i] = contrib[center * 2 - i];
		}
		weight = weight * 2 + 1.0;
		for (i = 0; i <= center; i++) {
			normContrib[i] = contrib[i] / weight;
		}
		for (i = center + 1; i < nDots; i++) {
			normContrib[i] = normContrib[center * 2 - i];
		}
	}

	private static void CalTempContrib(int start, int stop) {
		double weight = 0;
		int i = 0;
		for (i = start; i <= stop; i++) {
			weight += contrib[i];
		}
		for (i = start; i <= stop; i++) {
			tmpContrib[i] = contrib[i] / weight;
		}
	}

	private static int GetRedValue(int rgbValue) {
		int temp = rgbValue & 0x00ff0000;
		return temp >> 16;
	}

	private static int GetGreenValue(int rgbValue) {
		int temp = rgbValue & 0x0000ff00;
		return temp >> 8;
	}

	private static int GetBlueValue(int rgbValue) {
		return rgbValue & 0x000000ff;
	}

	private static int ComRGB(int redValue, int greenValue, int blueValue) {
		return (redValue << 16) + (greenValue << 8) + blueValue;
	}

	private static int HorizontalFilter(BufferedImage bufImg, int startX, int stopX, int start, int stop, int y, double[] pContrib) {
		double valueRed = 0.0;
		double valueGreen = 0.0;
		double valueBlue = 0.0;
		int valueRGB = 0;
		int i, j;
		for (i = startX, j = start; i <= stopX; i++, j++) {
			valueRGB = bufImg.getRGB(i, y);
			valueRed += GetRedValue(valueRGB) * pContrib[j];
			valueGreen += GetGreenValue(valueRGB) * pContrib[j];
			valueBlue += GetBlueValue(valueRGB) * pContrib[j];
		}
		valueRGB = ComRGB(Clip((int) valueRed), Clip((int) valueGreen), Clip((int) valueBlue));
		return valueRGB;
	}

	private static BufferedImage HorizontalFiltering(BufferedImage bufImage, int iOutW) {
		int dwInW = bufImage.getWidth();
		int dwInH = bufImage.getHeight();
		int value = 0;
		BufferedImage pbOut = new BufferedImage(iOutW, dwInH, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < iOutW; x++) {
			int startX;
			int start;
			int X = (int) (((double) x) * ((double) dwInW) / ((double) iOutW) + 0.5);
			int y = 0;
			startX = X - nHalfDots;
			if (startX < 0) {
				startX = 0;
				start = nHalfDots - X;
			} else {
				start = 0;
			}
			int stop;
			int stopX = X + nHalfDots;
			if (stopX > (dwInW - 1)) {
				stopX = dwInW - 1;
				stop = nHalfDots + (dwInW - 1 - X);
			} else {
				stop = nHalfDots * 2;
			}
			if (start > 0 || stop < nDots - 1) {
				CalTempContrib(start, stop);
				for (y = 0; y < dwInH; y++) {
					value = HorizontalFilter(bufImage, startX, stopX, start, stop, y, tmpContrib);
					pbOut.setRGB(x, y, value);
				}
			} else {
				for (y = 0; y < dwInH; y++) {
					value = HorizontalFilter(bufImage, startX, stopX, start, stop, y, normContrib);
					pbOut.setRGB(x, y, value);
				}
			}
		}
		return pbOut;
	}

	private static int VerticalFilter(BufferedImage pbInImage, int startY, int stopY, int start, int stop, int x, double[] pContrib) {
		double valueRed = 0.0;
		double valueGreen = 0.0;
		double valueBlue = 0.0;
		int valueRGB = 0;
		int i, j;
		for (i = startY, j = start; i <= stopY; i++, j++) {
			valueRGB = pbInImage.getRGB(x, i);
			valueRed += GetRedValue(valueRGB) * pContrib[j];
			valueGreen += GetGreenValue(valueRGB) * pContrib[j];
			valueBlue += GetBlueValue(valueRGB) * pContrib[j];
			// System.out.println(valueRed+"->" + Clip((int)valueRed) + "<-");
			// System.out.println(valueGreen+"->" + Clip((int)valueGreen) + "<-");
			// System.out.println(valueBlue+"->" + Clip((int)valueBlue) + "<-" + "-->");
		}
		valueRGB = ComRGB(Clip((int) valueRed), Clip((int) valueGreen), Clip((int) valueBlue));
		// System.out.println(valueRGB);
		return valueRGB;
	}

	private static BufferedImage VerticalFiltering(BufferedImage pbImage, int iOutH) {
		int iW = pbImage.getWidth();
		int iH = pbImage.getHeight();
		int value = 0;
		BufferedImage pbOut = new BufferedImage(iW, iOutH, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < iOutH; y++) {
			int startY;
			int start;
			int Y = (int) (((double) y) * ((double) iH) / ((double) iOutH) + 0.5);
			startY = Y - nHalfDots;
			if (startY < 0) {
				startY = 0;
				start = nHalfDots - Y;
			} else {
				start = 0;
			}
			int stop;
			int stopY = Y + nHalfDots;
			if (stopY > (int) (iH - 1)) {
				stopY = iH - 1;
				stop = nHalfDots + (iH - 1 - Y);
			} else {
				stop = nHalfDots * 2;
			}
			if (start > 0 || stop < nDots - 1) {
				CalTempContrib(start, stop);
				for (int x = 0; x < iW; x++) {
					value = VerticalFilter(pbImage, startY, stopY, start, stop, x, tmpContrib);
					pbOut.setRGB(x, y, value);
				}
			} else {
				for (int x = 0; x < iW; x++) {
					value = VerticalFilter(pbImage, startY, stopY, start, stop, x, normContrib);
					pbOut.setRGB(x, y, value);
				}
			}
		}
		return pbOut;
	}
	
	private static int Clip(int x) {
		if (x < 0)
			return 0;
		if (x > 255)
			return 255;
		return x;
	}
	
    public static void zoomImage(Integer width, Integer height, String srcPath, String newPath) throws Exception {    
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		if (width == null) { // 根据高度缩放图片  
			op.resize(null, height);
		} else if (height == null) { // 根据宽度缩放图片  
			op.resize(width, null);
		} else {
			op.resize(width, height);
		}
		op.quality(85d);
		op.addImage(newPath);
		// im4java是同时支持ImageMagick和GraphicsMagick的，如果为true则使用GM，如果为false支持IM。
		ConvertCmd convert = new ConvertCmd(true);
		String osName = System.getProperty("os.name").toLowerCase();
		if (0 <= osName.indexOf("win")) {
			convert.setSearchPath("C:\\Program Files\\GraphicsMagick-1.3.21-Q8");
		}
		convert.run(op);   
    }
    
    
	public static void main(String args[]) {
		//MyFile.createPath("c:\\studyTest6\\devil13th\\WebRoot\\s");
		//System.out.println(MyFile.getFix("xxx.a"));
		//System.out.println(FileUtil.getMimeType("JPG"));
		//FileUtil.unZip("K:/cnpereading2/trunk/04_Design/Template/9780335240777.zip", "K:/cnpereading2/trunk/04_Design/Template/9780335240777");
		
		try {
			//generateHighQulityThumbnail("E:/Upload/metadata/Paper.pdf_1.png", "E:/Upload/metadata/Paper.pdf_1_1.png", 1024, 768);
			
			zoomImage(1024, 768, "E:/Upload/metadata/2.png", "E:/Upload/metadata/222.png");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
