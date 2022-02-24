package utils;
import static org.testng.Assert.*;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
public class utils{
	public static WebDriver driver;
/** 启动浏览器
  * @parambrowser
 * @return WebDriver
 */
public static WebDriver openBrowser(String browser) {
	try {
		if (browser.equalsIgnoreCase("firefox")) {
			FirefoxProfile profile = new FirefoxProfile();
			//设置不要禁用混合内容
			profile.setPreference("security.mixed_content.block_active_content", false);
			profile.setPreference("security.mixed_content.block_display_content", true);
			//设置自动下载
			//1.不显示下载管理器
			profile.setPreference("browser.download.manager.showWhenStarting", false);
			//2.指定自动下载的文件类型
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/octet-stream,application/vnd.msexcel,text/csv,application/zip");
			//3.默认下载文件夹，0是桌面、1是默认系统用户的”下载“，2是自定义文件夹
			profile.setPreference("browser.download.folderList", 2);
			//4.设置自定义文件夹
			profile.setPreference("browser.download.dir", Constants.DOWNLOAD_PATH);
			//启动
			driver = new FirefoxDriver(profile);
		}
			
		else if (browser.equalsIgnoreCase("ie")){
			DesiredCapabilities ieCapabilities= 
					DesiredCapabilities.internetExplorer();
			ieCapabilities.setCapability("nativeEvents", true); 
			ieCapabilities.setCapability("unexpectedAlertBehaviour", "accept");
			ieCapabilities.setCapability("ignoreProtectedModeSettings", true);
			ieCapabilities.setCapability("disable-popup-blocking", true);
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			ieCapabilities.setCapability("requireWindowFocus", false);
			ieCapabilities.setCapability("enablePersistentHover", false);
			System.setProperty("webdriver.ie.driver", Constants.IE_DRIVER);
			//启动IE
			driver = new InternetExplorerDriver(ieCapabilities);
			
		}
		else if (browser.equalsIgnoreCase("chrome")) {
			//delete warning --ignore-certificate-errors
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability("chrome.switches", Arrays.asList("--incognito"));
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--test-type");
			options.addArguments("enable-automation");
			options.addArguments("--disable-infobars");
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			System.setProperty("webdriver.chrome.driver", Constants.CHROME_DRIVER);
			//启动Chrome
			driver = new ChromeDriver(capabilities);
		}
		else{
		Log.error("Invalid browser type:"+browser);
		}
		Log.info("Browser is started,Typeis "+browser);
		//浏览器最大化
		driver.manage().window().maximize();
		//设置隐式等待超时时间
		driver.manage().timeouts().implicitlyWait(Constants.IMPLICITLY_WAIT, TimeUnit.SECONDS);
		return driver;
		} catch (Exception e) {
		Log.error("Unable to Open Browser.");
		Log.error(e.getMessage());
		fail(e.getMessage());
		return null;
		}
		
	}
//元素截图
public static void takeScreenshot(String sTestCaseName){
	DateFormat dateformat= new SimpleDateFormat("yyyy-MM-ddhh_mm_ss");
	Date date= new Date();
	File file= ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	try {
		FileUtils.copyFile(file, new File(Constants.SCREENSHOT+ sTestCaseName+"/"
	+sTestCaseName+ " # " + dateformat.format(date)+ ".png"));
		} catch (Exception e) {
			Log.error("Issue in Taking Screenshot");}}
//执行js
public static Object executeJS(String js,Object... arg1) {
	try {
		Log.info(“Execute JS:"+js);
		Log.info(“JS arg1:"+arg1);
	return ((JavascriptExecutor) driver).executeScript(js,arg1);
	} catch (Exception e) {
		e.printStackTrace();Log.error(e.getMessage());return null; }}

/*** 
 * 等待固定时间（毫秒数）
 * */
public static void sleep(long ms){
	try {
		Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Log.error("The sleep thread is interrupted.");
			fail(e.printStackTrace());}}
/**等待网页加载完毕，一直等待，无终止条件,
一般用于有网页跳转的方法后调用，比如按钮或链接的点击
*/
public static void waitForPageLoad(){
	String js= "return document.readyState;";
	while (!"complete".equals((String)executeJS(js))) 
	{
		Log.info("Page is loading……");
		sleep(1000);
		}Log.info("The page is loaded.");}


//等待网页加载完毕，30秒作为最长等待时间
public static void waitForPageLoad30(){
	String js= "return document.readyState;";
	boolean flag = false;//初始值false表示网页没有加载完
	//最多等待30秒
	for (int i=1;i<=30;i++){
		sleep(1000);
		if("complete".equals((String)executeJS(js))) {
			flag=true;
			Log.info("Page is loaded in "+i+" seconds.");
			break;}}
		if (!flag){ fail();
		Log.warn("Page is not loaded in 30 seconds.");}}

//自定义显式等待，等待网页标题包含预期标题
public static void explicitWaitTitle(final String title){
	WebDriverWait wait = new WebDriverWait(driver,Constants.EXPLICIT_WAIT);
	try{ wait.until(new ExpectedCondition<Boolean>() {
		@Override
		public Boolean apply(WebDriver d) {
			return
			d.getTitle().toLowerCase().contains(title.toLowerCase()); }});
	}catch(TimeoutException te) {
		System.out.println(title);
		System.out.println(driver.getTitle());
		throw new IllegalStateException("当前不是预期页面，当前页面title是：" + driver.getTitle());}}


//封装获取页面元素状态（是否可见、是否可用）的方法,true代表可见，false代表不可见
public static boolean getElementStatus(WebElement element){
	if(!element.isDisplayed()){
		Log.error("The element is not displayed:"+element.toString());
		takeScreenshot(“Utils-getElementStatus");
				} else if (!element.isEnabled()){
				Log.error("The element is disabled:"+element.toString());
				takeScreenshot(“Utils-getElementStatus");
		}
return element.isDisplayed()&&element.isEnabled();}



//封装选择下拉框的选项方法
public static void selectDropDown(WebElement element,String flag, String data){
	if (getElementStatus(element)){
		Select select= new Select(element);
	Log.info("Select option in dropdown list :"+flag);
	if(flag.equalsIgnoreCase("byvalue")){
		select.selectByValue(data);
		}else if(flag.equalsIgnoreCase("byindex")){
			select.selectByIndex(Integer.parseInt(data));
		}else{select.selectByVisibleText(data);
		}
	Log.info(data +" is selectedin dropdown list:"+element.toString());
	}else{Log.error("dropdown list is disabled or not displayed.");}
	}
	Log.info(data +" is selectedin dropdown list:"+element.toString());
	}
else{
		Log.error("dropdown list is disabled or not displayed.");
		fail ("dropdown list is disabled or not displayed.");
		}
}
	
	
//点击页面元素的click方法如下
	
	public static void click(WebElement element){
		try {
			if(getElementStatus(element)){
				//1、判断该页面元素是否存在和可用
				element.click();
				//2、点击元素
				Log.info(element.toString()+" is clicked.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			Log.error("Method [elementClick] "+e.getMessage());
			fail(e.getMessage());}}
			}
		
		
/*** 点击页面元素,并等待网页加载完毕
	 * * @paramelement
	* */
		public static void clickAndWait(WebElement element){
			click(element);
			waitForPageLoad();
			//等待网页加载}
	}
//页面输入通常使用sendKeys方法，向指定的页面元素输入数据
		public static void inputValue(WebElement element,String value){
			try {
					if(getElementStatus(element)){
						//1、判断该页面元素是否存在和可用
						if(value!=null){//数据是否不等于空
							element.clear();
							element.sendKeys(value);
							Log.info(“Test data: "+value + " is input to element:“+element.toString());
						}else{Log.error("Test data: "+value + " is null.");}
						}
			} catch (Exception e) {
					Log.error("Method [inputValue] "+e.getMessage());
					fail(e.getMessage());}}
			}
		}
//
		
		
		
//封装基类断言	
/*** 判断页面元素是否出现
* * @param by
* * @return true出现，false未出现
* */
		public static boolean isElementPresent(By by) {
			try {driver.findElement(by);
			Log.info("Matching elements are found.");
			return true;
			} catch (NoSuchElementException e) {
				Log.error("No matching elements are found.");
				return false;
				}
			}
		
/*** 
 * 断言页面元素出现
 * * @param by
 * */
		public static void assertElementPresent(By by){
			assertTrue(isElementPresent(by));
			}
		
/*** 
* 断言页面元素未出现或消失
* @paramby
* */
		public static void assertElementNotPresent(By by){
			assertFalse(isElementPresent(by));
			}
		
/*** 判断是否弹出对话框
 * * @return,true代表出现,false代表未出现
 * */
		public static boolean isAlertPresent() {
			try {
				driver.switchTo().alert();
				Log.info("The alert dialog can be found.");
				return true;
			} catch (NoAlertPresentException e) {
				Log.error("The alert dialog cannot be found.");
				return false;
				fail(e.getMessage());}}		
/*** 断言弹出框出现
 * * @paramby*/
		public static void assertAlertPresent(){
			assertTrue(isAlertPresent());}		
		
/*** 断言弹出框未出现或消失
* */
		public static void assertAlertNotPresent(){
			assertFalse(isAlertPresent());}
		
/**获得弹出框中内容，并且关闭弹出框，默认点击“确定”
*	如果点击取消或否，先给acceptNextAlert赋值为false，在调用方法
*	@return 获得 弹框中得文本内容
*/
		public static boolean acceptNextAlert= true;
		public static String closeAlertAndGetItsText() {
			try {Alert alert= driver.switchTo().alert();
			String alertText= alert.getText();
			if (acceptNextAlert) {Log.info("Accept the dialog");
			alert.accept();
			} else {Log.info("Dismiss the dialog");
			alert.dismiss();
			}
			Log.info("The text in the dialog is:"+alertText);
			return alertText;
			} 
			finally {
				acceptNextAlert= true;}}
//页面断言弹出框中内容等于预期值，并且点击“确定”或“是”来关闭弹出框
/*** 断言弹出框内容正确，并且点击“确定”或“是”来关闭弹出框
 * */
		public static void assertAlertText(String expText){
			String actText= closeAlertAndGetItsText();
			try{
				assertEquals(actText,expText);
			}catch(AssertionError e){
				e.printStackTrace();
				Log.error(e.getMessage());
				fail(e.getMessage());}}
//页面断言弹出框中内容等于预期值，并且点击“取消”或“否”来关闭弹出框
/*** 断言弹出框内容正确，并且点击“取消”或“否”来关闭弹出框
 * */
		public static void assertAlertTextAndDismiss(String expText){
			acceptNextAlert= false;
			assertAlertText(expText);}
	
//断言弹出框内容包含预期文本，点击“确定”或“是”
		public static void assertAlertContainsText(String... expTexts){
			String actText= closeAlertAndGetItsText();
			for (String expText:expTexts){
				try{
					assertTrue(actText.contains(expText));
				} catch (AssertionError e){
					e.printStackTrace();
					Log.error("Actual alert text ["+actText+"] does not contains expected text ["+expText+"].");
					fail(e.getMessage());}}}
/*** 断言弹出框内容包含预期文本，并且点击“取消”或“否”来关闭弹出框
 * */
		public static void assertAlertContainsTextAndDismiss(String... expTexts){
			acceptNextAlert= false;
			assertAlertContainsText(expTexts);}
		
//页面断言文本等于预期值,即断言元素中的文本等于预期值
		public static void assertText(WebElement element,String expText){
			if(getElementStatus(element)){
				if(expText!=null){
					try{assertEquals(element.getText(),expText);
					}catch(AssertionError e){
						e.printStackTrace();
						Log.error(e.getMessage());
						fail(e.getMessage());}
					}else{
						fail("Test data: "+expText+ " is null.");
						Log.error("Test data: "+expText+ " is null.");}}}
		
//页面断言文本包含预期文本，断言元素的文本包含指定的那些预期文本字符串
		public static void assertContainsText(WebElement element,String... expTexts){
			if(getElementStatus(element)){
				for (String expText:expTexts){
					try{
						assertTrue(element.getText().contains(expText));
						}catch(AssertionError e){
							e.printStackTrace();
							Log.error("Text["+element.getText()+"] does not contains expected text ["+expText+"]");
							fail(e.getMessage());}}}}
		
	}



