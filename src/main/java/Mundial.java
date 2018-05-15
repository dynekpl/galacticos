import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class Mundial {

  private static WebDriver driver;

  public static final String URL = "https://tickets.fifa.com/requestSummary";

//  public static final String TEAM_ID = "ENG";
//  public static final String MATCH_ID = "14";

  public static String teamId;
  public static String matchId;
  public static String time;

  private static boolean isSetupDone = false;
  private static boolean isTicket = false;

  private static void setUp() throws IOException {
    BufferedReader br = null;

    br = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("Podaj trzyliterowy kod drużyny: ");
    teamId = br.readLine();

    System.out.print("Podaj numer meczu: ");
    matchId = br.readLine();

    System.out.print("Podaj czas odświeżania (w sekundach): ");
    time = br.readLine();

    br.close();

//    System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromedriver.exe");
    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    driver = new ChromeDriver();
//    System.setProperty("webdriver.firefox.marionette", "./src/main/resources/geckodriver.exe");
//    driver = new FirefoxDriver();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    if (!isSetupDone) {
      setUp();
      logMessage("Otwieram przeglądarkę");
      logMessage("Masz 2 minuty na zalogowanie");
    }
    while (!isTicket) {
      lookForTickets();
      long timeout = Long.valueOf(time) * 1000;
      Thread.sleep(timeout);
    }
  }

  private static void lookForTickets() {
    driver.navigate().to(URL);

    logMessage("Wchodze na stronę");

    By linkToProductNavBar = By.id("linkToProductNavBar");
    new WebDriverWait(driver, 120).until(ExpectedConditions.elementToBeClickable(linkToProductNavBar));
    driver.findElement(linkToProductNavBar).click();

    By clearFiltersButton = By.xpath("//button[@title='Clear filters']");
    new WebDriverWait(driver, 10).until(ExpectedConditions.visibilityOfElementLocated(clearFiltersButton));

    logMessage("Szukam biletów");
    List<WebElement> selects = driver.findElements(By.tagName("select"));
    Select filterSelect = new Select(selects.get(0));
    filterSelect.selectByValue("TE");

    selects = driver.findElements(By.tagName("select"));
    Select teamSelect = new Select(selects.get(1));
    teamSelect.selectByValue(teamId);

    WebElement productBox = findProductBoxForGivenMatch();
    checkAvailability(productBox);
  }

  private static void checkAvailability(WebElement productBox) {
    List<WebElement> wrapperCategories = productBox.findElements(By.id("wrapperCategory"));

    //TODO odwrócić listę żeby szukanie zaczynało sie od najtańszej kategorii

    for (WebElement wc : wrapperCategories) {
      WebElement categoryItem = wc.findElement(By.className("categoryItem"));
      List<WebElement> divs = categoryItem.findElements(By.tagName("div"));

      String availText = divs.get(2).getText();
      if (!availText.equals(" ")) {
        alert();
        isTicket = true;
        break;
      }
    }
    if (!isTicket) {
      logMessage("Nie ma biletów :(");
    }
  }

  private static void alert() {
    logMessage("BILETY!!!");
    openNewTab();

    ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
    driver.switchTo().window(tabs.get(tabs.size() - 1));

    driver.get("http://www.wavsource.com/snds_2018-01-14_3453803176249356/sfx/call_to_arms.wav");
  }

  private static void openNewTab() {
    //    Actions actions = new Actions(driver);
//    actions.keyDown(Keys.CONTROL).sendKeys("t").keyUp(Keys.CONTROL).build().perform();
    Robot rb = null;
    try {
      rb = new Robot();
    } catch (AWTException e) {
      e.printStackTrace();
    }
    rb.keyPress(KeyEvent.VK_CONTROL);
    rb.keyPress(KeyEvent.VK_T);
  }

  private static WebElement findProductBoxForGivenMatch() {
    List<WebElement> productBoxes = driver.findElements(By.xpath("//div[@class='productBox']"));
    for (WebElement pb : productBoxes) {
      WebElement element = pb.findElement(By.className("matchDescription"));
      String description = element.getText();
      if (description.substring(6, 8).equals(matchId)) {
        //System.out.println("!" + description);
        return pb;
      }
    }
    return null;
  }

  private static void logMessage(String s) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    String formattedDate = dateFormat.format(date);
    System.out.println(formattedDate + " | " + s);
  }


}
