package sportahome;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import sportahome.CalendarHelper;
import sportahome.NoFoundAppartmentsException;

public class TestHelper{

    protected FirefoxDriver driver;

    public TestHelper(FirefoxDriver driver){
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    /**
     * Selects a city, dates to stay and submits the initial page 
     *
     * @param  randomDate  If we want to select a randomDate for the stay
     */
    public void exploreCity(boolean randomDate){
        // Select a random city 
        Select cityDropdown = new Select(driver.findElement(By.cssSelector("select.city-selector--city.ga-home-select-city")));
        int randomCity = generateRandom(0, 10);
        cityDropdown.selectByIndex(randomCity);

        // Select and open the calendar    
        WebElement dateIn = WaitUntilElementClickable(driver, By.xpath("//input[@placeholder='Date In']"), 10);
        dateIn.click();

        // Find the calendar and select a day
        WebElement cal = WaitUntilElementClickable(driver, By.cssSelector("div.pika-lendar"), 10);
        
        // Generate random date in case need. Otherwise use today
        LocalDate date = LocalDate.now();
        CalendarHelper calHelp = new CalendarHelper(driver);
        if (randomDate){
            date = calHelp.generateRandomDate();
        }
        // Select the date in the "from" calendar. For now letting default TO to be used
        calHelp.selectDateInCalendar(cal, date);

        // Select to continue the flow by clicking "Explore"
        WebElement explore = driver.findElement(By.cssSelector("a.button.city-selector--cta.ga-home-explore"));
        explore.click();
        
    }

    /**
     * Selects a random appt from the returned list (filters applies if passed) and opens it
     *
     * @param  filters  List of possible filters to add when selecting a home: only studios, shared 
     * @return the number of homes found with the criteria
     */
    public int selectHome (List<String> filters) throws NoFoundAppartmentsException{

        // In case filters are passed, use them
        if (filters.size() > 0){
            WebElement filterSelect = WaitUntilElementClickable(driver, By.cssSelector("button.button.button--secondary.button--small.ga-search-form-more-filters"), 10); 
            filterSelect.click();
            for (String filter : filters){
                this.driver.findElement(By.cssSelector("input#studios-id.checkbox__input.ga-search-form-"+filter)).click();
            }
            WebElement filterApply = WaitUntilElementClickable(driver, By.cssSelector("button.button.button--secondary.button--small.ga-search-form-more-filters"), 10); 
            filterApply.click();
        }

        // Get all the appts
        WebElement apptWidget = driver.findElement(By.cssSelector("div.l-list")); 
        List<WebElement> appts = apptWidget.findElements(By.cssSelector("div.l-list__item"));
        if (appts.size() == 0){
            System.out.println("There are no appts");
            throw new NoFoundAppartmentsException("No appts found");
        }

        // Select random appt
        int randomAppt = generateRandom(1, appts.size());
        int selectedAppt = 1;
        for (WebElement appt : appts) {
            if (randomAppt == selectedAppt){
                appt.click();
                break;
            }
            selectedAppt+=1;
        }
        return appts.size();
    }

    /**
     * On the appt view, select the dates to finally book and submit
     *
     * @param  randomDate  If we want to select a randomDate for the stay
     * @return the number of homes found with the criteria
     */
    public void bookHome(boolean randomDate){
        this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        switchToTab();

        WebElement dateIn = WaitUntilElementClickable(driver, By.xpath("//input[@placeholder='Date in']"), 10);
        dateIn.click();
        
        // Find the calendar and select a day 
        WebElement cal = WaitUntilElementClickable(driver, By.cssSelector("div.pika-lendar"), 10);
        
        // Generate random date in case need. Otherwise use today
        LocalDate date = LocalDate.now();
        CalendarHelper calHelp = new CalendarHelper(driver);
        if (randomDate){
            date = calHelp.generateRandomDate();
        }
        // Select the date in the "from" calendar. For now letting default TO to be used
        calHelp.selectDateInCalendar(cal, date);;
        
        // Select to book it and proceed to pay
        WebElement book = WaitUntilElementClickable(driver, By.cssSelector("span.button--book-now__text"), 30);
        book.click();
    }

///////////////////////////////////////////////////////////////// Some more helper methods ///////////////////////////////////////////////////////

    /**
    * On the appt view, select the dates to finally book and submit
    *
    * @param min Minimum value
    * @param max Max value 
    * @return generated random numnber
    */
    private static int generateRandom(int min, int max){
        Random rn = new Random();
        int random =  rn.nextInt(max + 1 - min) + min;
        return random;
    }
    
    /**
    * Method to handle the switch to the new tab when opening an appt
    */

    private void switchToTab(){
        // Get all Open Tabs
        String currentPageHandle = this.driver.getWindowHandle(); 
        ArrayList<String> tabHandles = new ArrayList<String>(this.driver.getWindowHandles());
        String pageTitle = "Rooms";
        boolean myNewTabFound = false;

        // Go through tabs and select the new oen with ege so
        for(String eachHandle : tabHandles){
            this.driver.switchTo().window(eachHandle);
            if(this.driver.getTitle().equalsIgnoreCase(pageTitle)){
                //Close the current tab
                this.driver.close();
                //Switch focus to Old tab
                this.driver.switchTo().window(currentPageHandle);
                myNewTabFound = true;           
            }
        }
    }

    /**
    * Method to make sure that element is clickable when trying to click it
    *
    * @param driver the current driver object
    * @param elementLocator locator used
    * @param int time to wait before timeout in seconds
    * @return WebElement ready to be used
    */
    public static WebElement WaitUntilElementClickable(FirefoxDriver driver, By elementLocator, int timeout){
        WebElement el = null;
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            el = wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
        }
        catch (NoSuchElementException e) 
        {
            System.out.println("Element with locator: '" + elementLocator + "' was not found in current context page.");
            throw e;
        }
        return el;
    }
}