package sportahome;

import java.time.LocalDate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebElement;

public class CalendarHelper extends TestHelper{

    public CalendarHelper(FirefoxDriver driver){
        super(driver);
    }

    /**
     * Generates a randomDate in a specific range
     *
     * @return LocalDate object with a random value
     */
    public LocalDate generateRandomDate(){
        LocalDate startDate = LocalDate.now();//start date
        long start = startDate.toEpochDay();

        LocalDate endDate = LocalDate.of(2017,12,30); //end date
        long end = endDate.toEpochDay();

        // Generate random date in the given range
        long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
        System.out.println(LocalDate.ofEpochDay(randomEpochDay)); // random date between the range
        return LocalDate.ofEpochDay(randomEpochDay);
    } 

    /**
     * Select an specific date in the calendar and click it
     *
     * @param WebElement representing the calendar
     * @param date to select in the calendar
     */
    public void selectDateInCalendar(WebElement cal, LocalDate date){
        List<WebElement> cells = loadCorrectMonth(cal,date);
        for (WebElement cell : cells) { 
            int day = Integer.parseInt(cell.getAttribute("data-pika-day"));
            if (day == date.getDayOfMonth()){
                cell.click();
                break;
            }
        }
    }

    /**
     * Find the correct month to select, depending on the date
     *
     * @param WebElement representing the calendar
     * @param date to select in the calendar
     * @return cells containing the list of days in the month
     */
    public List<WebElement> loadCorrectMonth(WebElement cal, LocalDate date){
        List<WebElement> cells = cal.findElements(By.cssSelector("button.pika-button.pika-day"));
        int currentMonth = Integer.parseInt(cells.get(0).getAttribute("data-pika-month"))+1; // January is month 0
        int month = date.getMonthValue();

        // Current solution only works for consecutive months, without taking into account upcoming years!
        if (month > currentMonth){
            for (int m = currentMonth; m <= 12; m++){
                WebElement next = driver.findElement(By.cssSelector("button.pika-next"));
                next.click();
                cal = driver.findElement(By.cssSelector("div.pika-lendar"));
                cells = cal.findElements(By.cssSelector("button.pika-button.pika-day"));
                currentMonth = Integer.parseInt(cells.get(0).getAttribute("data-pika-month"))+1; 
                if (month == currentMonth){
                    break;
                }
            }
        }
        return cells;
    }
}