import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Mundial {

  private static WebDriver driver;

  public static final String URL = "https://tickets.fifa.com/requestSummary";

  public static final String MATCH_ID = "14";

  private static boolean isSetupDone = false;
  private static boolean isTicket = false;

  private static void setUp() {
//    System.setProperty("webdriver.chrome.driver", "./src/test/resources/drivers/chromedriver.exe");
//    driver = new ChromeDriver();
    System.setProperty("webdriver.firefox.marionette", "./src/main/resources/geckodriver.exe");
    driver = new FirefoxDriver();
  }

  public static void main(String[] args) {
    if (!isSetupDone) {
      setUp();
    }
    lookForTickets();
  }

  private static void lookForTickets() {
    driver.navigate().to(URL);

    By linkToProductNavBar = By.id("linkToProductNavBar");
    new WebDriverWait(driver, 120).until(ExpectedConditions.elementToBeClickable(linkToProductNavBar));
    driver.findElement(linkToProductNavBar).click();

    By clearFiltersButton = By.xpath("//button[@title='Clear filters']");
    new WebDriverWait(driver,10).until(ExpectedConditions.visibilityOfElementLocated(clearFiltersButton));

    List<WebElement> selects = driver.findElements(By.tagName("select"));
    Select filterSelect = new Select(selects.get(0));
    filterSelect.selectByValue("TE");

    selects = driver.findElements(By.tagName("select"));
    Select teamSelect = new Select(selects.get(1));
    teamSelect.selectByValue("ENG");

    List<WebElement> elements = driver.findElements(By.xpath("//div[@class='productBox']"));
    System.out.println("!!!" + elements.size());
  }


}
