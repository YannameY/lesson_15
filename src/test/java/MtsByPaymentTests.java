import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MtsByPaymentTests {
    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://www.mts.by");
        closeCookieConsent();
    }

    private static void closeCookieConsent() {
        try {
            WebElement cookieButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("cookie-agree")));
            cookieButton.click();
        } catch (Exception e) {

        }
    }

    @Test
    public void testBlockTitle() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement blockTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='pay__wrapper']//h2")));
        String text = blockTitle.getText();
        Assert.assertNotNull("Блок 'Онлайн пополнение' не найден.", text);
        Assert.assertEquals("Заголовок блока неверен", "Онлайн пополнение\nбез комиссии", text);
    }

    @Test
    public void testPaymentSystemLogos() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        String[] expectedAlts = {"Visa", "Verified By Visa", "MasterCard", "MasterCard Secure Code", "Белкарт"};

        for (String alt : expectedAlts) {
            checkLogo(wait, alt);
        }
    }

    private void checkLogo(WebDriverWait wait, String alt) {
        try {
            WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='pay__partners']//img[@alt='" + alt + "']")));
            Assert.assertTrue("Логотип " + alt + " не отображается.", logo.isDisplayed());
            String logoSrc = logo.getAttribute("src");
            System.out.println("Логотип " + alt + " найден, URL: " + logoSrc);
        } catch (Exception e) {
            System.out.println("Логотип " + alt + " не найден.");
        }
    }

    @Test
    public void testContinueButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.findElement(By.xpath("//option[text()='Услуги связи']")).click();
        driver.findElement(By.id("connection-phone")).sendKeys("297777777");
        driver.findElement(By.id("connection-sum")).sendKeys("100");
        driver.findElement(By.id("connection-email")).sendKeys("test@example.com");

        WebElement continueButton = driver.findElement(By.cssSelector("button.button__default"));
        continueButton.click();
    }

    @Test
    public void testLearnMoreLink() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement learnMoreLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Подробнее о сервисе")));
        learnMoreLink.click();
        Assert.assertTrue("Ссылка 'Подробнее о сервисе' не работает.", driver.getCurrentUrl().contains("https://www.mts.by/help/poryadok-oplaty-i-bezopasnost-internet-platezhey/"));
        driver.navigate().back();
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
