package utils;
public class Constants {//常量类
	//IE驱动文件
	public static final String IE_DRIVER = System.getProperty("user.dir")+"\\driver\\IEDriverServer.exe";
	//Chrome驱动文件
	public static final String CHROME_DRIVER = System.getProperty("user.dir")+"\\driver\\chromedriver.exe";
	//隐式等待默认超时时间
	public static final long IMPLICITLY_WAIT=60;
	//显示等待默认超时时间
		public static final int EXPLICIT_WAIT=60;
	//默认下载文件的路径
	public static final String DOWNLOAD_PATH="D:\\download";
	//截图文件路径
	public static final String SCREENSHOT = System.getProperty("user.dir")+ "\\screenshots";
	//测试数据路径
		public static final String DATA_PATH = System.getProperty("user.dir")+ "\\data";
		
	//ecshop高级搜索的网址
		public static final String ECSHOP_ADVANCED_SEARCH_URL = "";
		
}
