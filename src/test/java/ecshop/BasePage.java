package ecshop;

import org.apache.xml.serializer.utils.Utils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import bsh.org.objectweb.asm.Constants;
//封装BasePage
//设计BasePage两个构造方法，后一个等待标题显示。
public class BasePage {
	public WebDriver driver;
	public String url; 
	public BasePage(WebDriver driver){ 
		this.driver= driver; 
		PageFactory.initElements(
				new AjaxElementLocatorFactory(driver, Constants.EXPLICIT_WAIT) , this);
		}
	public BasePage(WebDriver driver, String title) {//等待网页标题出现预期值
		this.driver= driver;
		//显式等待,标题包含预期值
		Utils.explicitWaitTitle(title);
		PageFactory.initElements(
				new AjaxElementLocatorFactory(driver, Constants.EXPLICIT_WAIT) , this);}
	
//	设计BasePage打开页面的通用方法	
	
	//打开页面
	/**
	 * 注意在子类的网页中如果有直接打开网页的需求，那么需要在构造方法中给url赋值
	 */
	public void get() {
		driver.get(url);
		driver.manage().window().maximize();}
	//设计BasePage，获得网页标题
	public String getTitle() {
		return driver.getTitle();}
	//设计BasePage获得网址
	public String getURL() {
		return driver.getCurrentUrl();}
	//设计BasePage，获得网页源码
	public String getPageSource() {
		return driver.getPageSource();}
	}

