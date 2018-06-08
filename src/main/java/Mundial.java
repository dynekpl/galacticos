import org.openqa.selenium.*;
import org.openqa.selenium.Point;
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
import java.util.*;
import java.util.List;

public class Mundial {

  private static WebDriver driver;

  public static final String URL = "https://tickets.fifa.com/requestSummary";

  public static String teamId;
  public static String matchId;
  public static String time;

  private static boolean isSetupDone = false;
  private static boolean isTicket = false;

  private static void setUp() throws IOException {
    readInputValues();
    setupBrowser();

    driver.get(URL);
    logMessage("Otwieram przeglądarkę");
    logMessage("Masz 2 minuty na zalogowanie");

    maximizeBrowser();
    waitForBuyButton();
    //minimizeBrowser();

    isSetupDone = true;
  }

  private static void setupBrowser() {
//    System.setProperty("webdriver.chrome.driver", "./src/main/resources/chromedriver.exe");
    System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
    driver = new ChromeDriver();
//    System.setProperty("webdriver.firefox.marionette", "./src/main/resources/geckodriver.exe");
//    driver = new FirefoxDriver();
  }

  private static void readInputValues() throws IOException {
    BufferedReader br = null;

    br = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("Podaj trzyliterowy kod drużyny: ");
    teamId = br.readLine();

    System.out.print("Podaj numer meczu: ");
    matchId = br.readLine();

//    System.out.print("Podaj czas odświeżania (w sekundach): ");
//    time = br.readLine();

    br.close();
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    if (!isSetupDone) {
      setUp();
    }
    while (!isTicket) {
      lookForTickets();

//      long timeout = Long.valueOf(time) * 1000;
//      Thread.sleep(timeout);
    }
  }

  private static void lookForTickets() throws InterruptedException {
    driver.navigate().refresh();
    logMessage("Odświeżam stronę");

    By linkToProductNavBar = By.id("linkToProductNavBar");
    pauseInSeconds(1);
    new WebDriverWait(driver, 60).until(ExpectedConditions.invisibilityOfElementLocated(By.id("ngdialog1")));
    new WebDriverWait(driver, 60).until(ExpectedConditions.elementToBeClickable(linkToProductNavBar));
    WebElement buyTicketsButton = driver.findElement(linkToProductNavBar);
    buyTicketsButton.click();

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

  private static void pauseInSeconds(int sec) {
    try {
      Thread.sleep(sec * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void waitForBuyButton() {
    By linkToProductNavBar = By.id("linkToProductNavBar");
    new WebDriverWait(driver, 3600).until(ExpectedConditions.visibilityOfElementLocated(linkToProductNavBar));
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

  private static void checkAvailability(WebElement productBox) throws InterruptedException {
    List<WebElement> wrapperCategories = productBox.findElements(By.id("wrapperCategory"));

    Collections.reverse(wrapperCategories);

    for (WebElement wc : wrapperCategories) {
      WebElement categoryItem = wc.findElement(By.className("categoryItem"));
      List<WebElement> divs = categoryItem.findElements(By.tagName("div"));

      String availText = divs.get(2).getText();
      if (!availText.equals(" ")) {
        logMessage("BILETY!!!");
        selectTickets(categoryItem);
        alert();
        isTicket = true;
        break;
      }
    }
    if (!isTicket) {
      logMessage("Nie ma biletów :(");
    }
  }

  private static void selectTickets(WebElement categoryItem) {
    logMessage("Wybieram bilety");

    JavascriptExecutor js = (JavascriptExecutor) Mundial.driver;
    js.executeScript("arguments[0].scrollIntoView();", categoryItem);
    pauseInSeconds(1);
    new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(categoryItem));
    categoryItem.click();
    List<WebElement> selects = driver.findElements(By.tagName("select"));
    Select ticketsToSelect = new Select(selects.get(3));
    ticketsToSelect.selectByValue("number:2");

    By addToShoppingBasket = By.xpath("//div[contains(text(),'Add to your Shopping Basket')]");
    WebElement addToBasket = driver.findElement(addToShoppingBasket);
    js.executeScript("arguments[0].scrollIntoView();", addToBasket);
    pauseInSeconds(1);
    new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(addToShoppingBasket));
    addToBasket.click();
  }

  private static void alert() throws InterruptedException {
    //maximizeBrowser();
    openNewTab();

    ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
    String newTab = tabs.get(tabs.size() - 1);
    driver.switchTo().window(newTab);
    driver.get("http://www.wavsource.com/snds_2018-01-14_3453803176249356/sfx/call_to_arms.wav");
  }

  private static void maximizeBrowser() {
    driver.manage().window().maximize();
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
    rb.keyRelease(KeyEvent.VK_CONTROL);
    rb.keyRelease(KeyEvent.VK_T);
  }

  private static void minimizeBrowser() {
//    Robot rb = null;
//    try {
//      rb = new Robot();
//    } catch (AWTException e) {
//      e.printStackTrace();
//    }
//    rb.keyPress(KeyEvent.VK_ALT);
//    rb.keyPress(KeyEvent.VK_SPACE);
//    rb.keyRelease(KeyEvent.VK_ALT);
//    rb.keyRelease(KeyEvent.VK_SPACE);
//    rb.keyPress(KeyEvent.VK_M);
//    rb.keyRelease(KeyEvent.VK_M);
//    rb.keyPress(KeyEvent.VK_ENTER);
//    rb.keyRelease(KeyEvent.VK_ENTER);
    driver.manage().window().setPosition(new Point(0, -2000));
  }

  private static void logMessage(String s) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    String formattedDate = dateFormat.format(date);
    System.out.println(formattedDate + " | " + s);
  }


}
