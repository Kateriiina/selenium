package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "D:\\selenium\\drivers\\chrome drivers\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // 1. Перехід на сайт
            driver.get("https");

            // 2. Попап про місто
            try {
                WebElement popupBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[@id='header']//button[1]/div[contains(text(), 'Так')]")
                ));
                js.executeScript("arguments[0].scrollIntoView(true);", popupBtn);
                js.executeScript("arguments[0].click();", popupBtn);

                // Очікуємо, поки попап зникне
                wait.until(ExpectedConditions.invisibilityOfElementLocated(
                        By.xpath("//*[@id='autocomplete-suggestions']")));
                System.out.println("✅ Попап закрито");
            } catch (TimeoutException e) {
                System.out.println("⚠️ Попап не знайдено — переходимо до авторизації");
            }

            // 3. Меню акаунта
            WebElement customerMenu = wait.until(ExpectedConditions.elementToBeClickable(By.id("customer-menu")));
            customerMenu.click();

            // 4. Кнопка "Авторизуватись"
            List<WebElement> buttons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("button")));
            for (WebElement btn : buttons) {
                if (btn.getAttribute("outerHTML").contains("toggleAuth();")) {
                    btn.click();
                    break;
                }
            }

            // 5. Введення телефону
            WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("auth")));
            phoneInput.sendKeys("");

            // 6. Кнопка "Отримати код"
            WebElement getCodeBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(),'Отримати код')]")));
            js.executeScript("arguments[0].click();", getCodeBtn);

            // 7. Введення коду
            for (int i = 0; i < 6; i++) {
                WebElement codeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input" + i)));
                codeInput.sendKeys(String.valueOf(i + 1));
            }

            // 8. Очікування зникнення модального вікна
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal-root")));

            // 9. Очікування товарів
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id='maincontent']//div[contains(@class, 'product-listing')]")));

            // 10. Додати до кошика
            WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[title='Додати в кошик']")));
            js.executeScript("arguments[0].click();", addToCartBtn);

            // 11. Перехід у кошик
            driver.get("");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Кошик')]")));

            // 12. Попап у кошику
            try {
                WebElement cartPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='product-drawer']/div[2]/div[1]")));
                WebElement body = driver.findElement(By.tagName("body"));
                js.executeScript("arguments[0].click();", body);
                wait.until(ExpectedConditions.invisibilityOf(cartPopup));
            } catch (TimeoutException ignored) {
                System.out.println("Попап не знайдено");
            }

            // 13. Вибір "Самовивіз"
            List<WebElement> pickupTabs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//li[contains(text(),'Самовивіз')]")));
            for (WebElement tab : pickupTabs) {
                if (tab.isDisplayed()) {
                    js.executeScript("arguments[0].click();", tab);
                    break;
                }
            }

            // 14. Клік "До оформлення"
            try {
                WebElement overlay = driver.findElement(By.cssSelector(".no-available-products-popup-backdrop"));
                wait.until(ExpectedConditions.invisibilityOf(overlay));
            } catch (NoSuchElementException ignored) {}

            WebElement proceedBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[text()='До оформлення']")));
            js.executeScript("arguments[0].click();", proceedBtn);

            // Очікування завантаження сторінки оформлення
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//h2[contains(text(),'Оплата') or contains(text(),'Payment')]")));

            // 15. Тултіп про місто
            try {
                WebElement tooltip = driver.findElement(By.xpath("//*[contains(text(),'Змініть місто доставки')]"));
                if (tooltip.isDisplayed()) {
                    WebElement changeBtn = driver.findElement(By.xpath("//button[contains(text(),'Змінити місто')]"));
                    js.executeScript("arguments[0].click();", changeBtn);
                    WebElement suggestion = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//*[@id='autocomplete-suggestions']/li[1]")));
                    suggestion.click();
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(
                            By.xpath("//*[@id='autocomplete-suggestions']")));
                }
            } catch (NoSuchElementException ignored) {}

            // 16. Оплата — готівкою
            WebElement cashOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("paymentMethod_gdwncash_payment")));
            js.executeScript("arguments[0].click();", cashOption);

            // 17. Оформити замовлення
            WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Оформити замовлення')]")));
            js.executeScript("arguments[0].click();", placeOrderBtn);

            // 18. Попап після оформлення
            try {
                WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[@id='react-checkout']/div/div[3]/div/div")));
                WebElement body = driver.findElement(By.tagName("body"));
                js.executeScript("arguments[0].click();", body);
                wait.until(ExpectedConditions.invisibilityOf(popup));
            } catch (TimeoutException | NoSuchElementException ignored) {
                System.out.println("✅ Попап на чекауті відсутній");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
