package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\selenium\\drivers\\chrome drivers\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Basic Auth + Перехід на сторінку "Вино"
        driver.get("https://dev:dev123@gwn-hyva-dev.perspective.net.ua/ua/napoi/vino/");

        // Чекаємо, поки сторінка повністю завантажиться
        Thread.sleep(2000);

        // Вибираємо сортування "Ціна: від найвищої"
        WebElement sortSelect = driver.findElement(By.cssSelector("select[data-role='sorter']"));

        // Використовуємо JavaScript для вибору потрібного option (бо option hidden)
        js.executeScript(
                "arguments[0].value='price&product_list_dir=desc'; arguments[0].dispatchEvent(new Event('change'));",
                sortSelect
        );

        // Чекаємо, поки сторінка перезавантажиться після сортування
        Thread.sleep(3000);

        // Знаходимо першу кнопку "Додати в кошик" і клікаємо
        List<WebElement> addToCartButtons = driver.findElements(By.cssSelector("button[title='Додати в кошик']"));
        if (!addToCartButtons.isEmpty()) {
            addToCartButtons.get(0).click();
        } else {
            System.out.println("Кнопка 'Додати в кошик' не знайдена");
            driver.quit();
            return;
        }

        // Чекаємо, щоб товар точно додався
        Thread.sleep(2000);

        // Переходимо на сторінку кошика
        driver.get("https://gwn-hyva-dev.perspective.net.ua/checkout/cart/index/");

        // Чекаємо завантаження кошика
        Thread.sleep(3000);

        // Знаходимо кнопку "До оформлення" і натискаємо її
        WebElement proceedButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[text()='До оформлення']"))
        );
        proceedButton.click();

        // Чекаємо кілька секунд, щоб побачити наступну сторінку
        Thread.sleep(3000);

        driver.quit();
    }
}
