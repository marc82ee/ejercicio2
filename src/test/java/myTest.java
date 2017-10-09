package sportahome;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import sportahome.TestHelper;

 
public class myTest {
	public static FirefoxDriver driver;
	protected static TestHelper spothome;

	@BeforeClass
    public static void openBrowser(){
		 driver = new FirefoxDriver();
		 driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		 driver.get("https://www.spotahome.com");
		 spothome = new TestHelper(driver);	 
		} 
 
	@Test
    public void select_appartment_random_city(){
 
		System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());

		 // 1. Start the flow by selecting city and the period
		spothome.exploreCity(true);
		 
		 // 2. Select a random a appt, from the ones available in the city  
		List<String> searchFilter = new ArrayList<String>();
		try{
		 	spothome.selectHome(searchFilter);
		} 
		catch (NoFoundAppartmentsException e){
			 System.out.println("No appts were found" + e.getText());
		}

		 // 3. Select to book the appt for some specific dates
		spothome.bookHome(true);

		 // Verify the results at the end of the flow
		String bodyText = driver.findElement(By.tagName("body")).getText();
		String title = driver.getTitle();
		Assert.assertTrue("Booking title not found,some problem in the page?", title.contains("Make your reservation"));
		Assert.assertTrue("Booking text not found,some problem in the page?", bodyText.contains("Total to Pay"));
         
	    System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
	}
	
	@Test
    public void select_appartment_random_city_with_filters(){
 
		System.out.println("Starting test " + new Object(){}.getClass().getEnclosingMethod().getName());

		// 1. Start the flow by selecting city and the period
		spothome.exploreCity(true);
		 
		// 2. Select a random a appt, from the ones available in the city
		List<String> searchFilter = Arrays.asList("studios"); 
		try{
			spothome.selectHome(searchFilter);
		} 
		catch (NoFoundAppartmentsException e){
			  System.out.println("No appts were found" + e.getText());
		}
 
		// 3. Select to book the appt for some specific dates
		spothome.bookHome(true);
 
		// Verify the results at the end of the flow
		String bodyText = driver.findElement(By.tagName("body")).getText();
		String title = driver.getTitle();
		Assert.assertTrue("Booking title not found,some problem in the page?", title.contains("Make your reservation"));
		Assert.assertTrue("Booking text not found,some problem in the page?", bodyText.contains("Total to Pay"));
		  
		System.out.println("Ending test " + new Object(){}.getClass().getEnclosingMethod().getName());
    }
 
	@AfterClass
	 public static void closeBrowser(){
		 driver.quit();
	}
}