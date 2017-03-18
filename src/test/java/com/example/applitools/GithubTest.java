package com.example.applitools;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Eyes;
import com.applitools.eyes.RectangleSize;

import junit.framework.TestCase;
import net.sourceforge.htmlunit.corejs.javascript.InterfaceAdapter;


interface UIStateChangeable{
	public void FireUIStateChanged(String tag);
}

@RunWith(Parameterized.class)
public class GithubTest extends TestCase implements UIStateChangeable 
{
	private WebDriver driver;
	private Eyes eyes;
	private int width;
	
	@Parameters
    public static Collection<Integer> data() {
        return Arrays.asList(new Integer[] { 1000, 800});
    }
	
	public GithubTest(Integer width) {
		this.width = width;
	}
    
	@Before
	public void setUp() throws Exception {
		initializeEyes();
		driver = new ChromeDriver();
		driver = eyes.open(driver, "GitHub", "Pages", new RectangleSize(this.width, 600));
	}
	
	public void initializeEyes() {
		
		eyes = new Eyes();
		String apiKey = "YOUR_API_KEY";
		
		eyes.setApiKey(apiKey);
		BatchInfo batchInfo = new BatchInfo("Responsive");
		
		eyes.setBatch(batchInfo);
//		eyes.setWaitBeforeScreenshots(3000);
		eyes.setForceFullPageScreenshot(true);
	}
	
	@After
	public void tearDown() throws Exception {
		eyes.close();	
		driver.quit();
	}

	@Test
	public void testResponsiveness() {
		driver.get("https://github.com");
		
		HomePage homePage = new HomePage(driver, this);
		eyes.checkWindow(homePage.getName());
		FeaturesPage personalPage = homePage.goToFeaturesPage();
		eyes.checkWindow(personalPage.getName());
		ExplorePage openSourcePage = personalPage.goToExplorePage();
		eyes.checkWindow(openSourcePage.getName());
	}


	public abstract class BasePage {
		
		protected WebDriver driver;
		private UIStateChangeable uiStateChangedHandler;
		
		protected final By navMenuLocator = By.className("octicon-three-bars");
		protected final By featuresLocator = By.cssSelector("body > div.position-relative.js-header-wrapper > header > div > div > nav > a:nth-child(1)");
		protected final By exploreLocator = By.cssSelector("body > div.position-relative.js-header-wrapper > header > div > div > nav > a:nth-child(2)");

		private String name;


		public BasePage(String pageName, WebDriver driver, UIStateChangeable uiStateChangedHandler) {
			this.driver = driver;
			this.name = pageName;
			this.uiStateChangedHandler = uiStateChangedHandler;
		}

		protected void clickNavButton(By locator) {
			WebElement navMenu = driver.findElement(navMenuLocator);
			if (navMenu.isDisplayed()) {
				navMenu.click();
				this.uiStateChangedHandler.FireUIStateChanged("Navigation Menu");
			}

			driver.findElement(locator).click();
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public FeaturesPage goToFeaturesPage() {
			clickNavButton(featuresLocator);
			return new FeaturesPage(this.driver, this.uiStateChangedHandler);
		}
		
		public ExplorePage goToExplorePage() {
			clickNavButton(exploreLocator);

			return new ExplorePage(this.driver, this.uiStateChangedHandler);
		}
	}

	public class HomePage extends BasePage {
		
		public HomePage(WebDriver driver, UIStateChangeable uiStateChangedHandler) {
			super("Home", driver, uiStateChangedHandler);
		}
	}

	public class FeaturesPage extends BasePage {

		public FeaturesPage(WebDriver driver, UIStateChangeable uiStateChangedHandler) {
			super("Features", driver, uiStateChangedHandler);
		}
	}

	public class ExplorePage extends BasePage {

		public ExplorePage(WebDriver driver, UIStateChangeable uiStateChangedHandler) {
			super("Explore", driver, uiStateChangedHandler);
		}
	}

	public void FireUIStateChanged(String tag) {
		eyes.checkWindow(tag);
	}
}
