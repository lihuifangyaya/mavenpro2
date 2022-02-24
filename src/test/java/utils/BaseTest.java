package utils;

import org.junit.runners.Parameterized.Parameters;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest{
	public WebDriver driver;
		@BeforeMethod
		@Parameters({"browser"})
		public void beforeMethod(String b) {
			driver = Utils.openBrowser(b);}
		@AfterMethod
		public void afterMethod() {
			driver.quit();}}
}
