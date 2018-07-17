import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class Intercity {

  private static WebDriver driver;

  public static final String URL = "https://www.intercity.pl/pl/site/dla-pasazera/informacje/wyszukiwarka-polaczen.html?src=";

  private static void setUp() {
//    System.setProperty("webdriver.chrome.driver", "./src/test/resources/drivers/chromedriver.exe");
//    driver = new ChromeDriver();
    System.setProperty("webdriver.firefox.marionette", "./src/main/resources/geckodriver.exe");
    driver = new FirefoxDriver();
  }

  public static void main(String[] args) {
    setUp();
    driver.navigate().to(URL);

    By searchButtonXPath = By.xpath("//button[@name='search']");
    new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(searchButtonXPath));

    WebElement from = driver.findElement(By.id("ic-seek-z"));
    from.sendKeys("Gdynia Główna");

    WebElement to = driver.findElement(By.id("ic-seek-do"));
    to.sendKeys("Kraków Główna");

    WebElement time = driver.findElement(By.id("ic-seek-time"));
    time.clear();
    time.sendKeys("16:00");

    WebElement date = driver.findElement(By.id("inputID3"));
    date.clear();
    date.sendKeys("2018-06-03");

    List<WebElement> elements = driver.findElements(By.xpath("//span[@class='checkbox-custom-btn']"));
    elements.get(0).click();

    driver.findElement(searchButtonXPath).click();
  }
}
