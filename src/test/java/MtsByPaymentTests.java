import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*; // Импортируем необходимые классы из Selenium
import org.openqa.selenium.chrome.ChromeDriver; // Импортируем ChromeDriver для работы с браузером Chrome
import org.openqa.selenium.support.ui.ExpectedConditions; // Импортируем условия ожидания для элементов
import org.openqa.selenium.support.ui.WebDriverWait; // Импортируем класс для ожидания

import java.time.Duration; // Импортируем Duration для времени ожидания

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // порядок выполнения тестов
public class MtsByPaymentTests {
    private static WebDriver driver; // Создаем объект WebDriver

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "/Users/Yan/Downloads/chromedriver/chromedriver.exe"); // Указываем путь к драйверу
        driver = new ChromeDriver();
        driver.get("https://www.mts.by"); // Переходим на сайт MTS

        closeCookieConsent();  // Закрываем куки здесь, если это необходимо
    }

    private static void closeCookieConsent() {
        // Закрываем всплывающее окно с куки
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement cookieButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cookie-agree")));
            cookieButton.click(); // Кликаем на кнопку
        } catch (Exception e) { // Игнорируем исключения, если кнопка не найдена

        }
    }

    @Test // Тест для проверки заголовка блока
    public void testBlockTitle() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Ожидание 20 секунд
        WebElement blockTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='pay__wrapper']//h2"))); // Ищем заголовок блока по XPath
        String text = blockTitle.getText(); // Получаем текст заголовка
        Assert.assertNotNull("Блок 'Онлайн пополнение' не найден.", text); // Проверяем, что текст не null
        Assert.assertEquals("Заголовок блока неверен", "Онлайн пополнение\n" + "без комиссии", text); // Сравниваем с ожидаемым текстом
    }


    @Test // Тест для проверки логотипов платежных систем
    public void testPaymentSystemLogos() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Ожидание 30 секунд

        // Массив для хранения alt-атрибутов
        String[] expectedAlts = {
                "Visa",
                "Verified By Visa",
                "MasterCard",
                "MasterCard Secure Code",
                "Белкарт"
        };

        // Проходим по каждому alt-значению и проверяем его наличие
        for (String alt : expectedAlts) {
            try {
                // Используем XPath
                WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='pay__partners']//img[@alt='" + alt + "']")));
                Assert.assertTrue("Логотип " + alt + " не отображается.", logo.isDisplayed()); // Проверяем, отображается ли логотип


                String logoSrc = logo.getAttribute("src"); // Получаем логотипа
                System.out.println("Логотип " + alt + " найден, URL: " + logoSrc);
            } catch (TimeoutException e) {
                System.out.println("Логотип " + alt + " не найден.");
                e.printStackTrace(); // Печатаем стек исключения
            }
        }


    }

    @Test // Тест для проверки кнопки "Продолжить"
    public void testContinueButton() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Ожидание 30 секунд

        JavascriptExecutor js = (JavascriptExecutor) driver; // Создаем объект для выполнения JavaScript


        // Выбор опции из выпадающего списка
        driver.findElement(By.xpath("//option[text()='Услуги связи']")).click(); // Кликаем на опцию "Услуги связи"

        driver.findElement(By.id("connection-phone")).sendKeys("297777777"); // Вводим номер телефона
        driver.findElement(By.id("connection-sum")).sendKeys("100"); // Вводим сумму пополнения
        driver.findElement(By.id("connection-email")).sendKeys("test@example.com"); // Вводим email

        WebElement continueButton = driver.findElement(By.cssSelector("button.button__default")); // Ищем по CSS-селектору
        js.executeScript("arguments[0].click();", continueButton); // Кликаем с помощью JavaScript


    }

    @Test // Тест для проверки ссылки "Подробнее о сервисе"
    public void testLearnMoreLink() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20)); // Ожидание 20 секунд
        WebElement learnMoreLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Подробнее о сервисе")));  // Ищем ссылку "Подробнее о сервисе"
        learnMoreLink.click(); // Кликаем на ссылку
        Assert.assertTrue("Ссылка 'Подробнее о сервисе' не работает.", driver.getCurrentUrl().contains("https://www.mts.by/help/poryadok-oplaty-i-bezopasnost-internet-platezhey/"));  // Проверяем ожидаемую часть
        driver.navigate().back(); // Возвращаемся на предыдущую страницу
    }

    @AfterClass
    public static void tearDown() {
        if (driver != null) {
            driver.quit(); // Закрываем драйвер
        }
    }
}



