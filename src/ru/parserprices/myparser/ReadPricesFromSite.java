package ru.parserprices.myparser;


import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.IOException;

public class ReadPricesFromSite {

    public static void main(String[] args) throws IOException {

        // создаем новый экземпляр html unit driver
        // Обратите внимание, что последующий код не закладывается на
        // конкретную, имплементацию, а только на интерфейс WebDriver.
        WebDriver driver = new HtmlUnitDriver();

        // Открываем Google
        driver.get("http://www.google.com");

        // Находим по имени поле для ввода
        WebElement element = driver.findElement(By.name("q"));

        // Вводим ключевое слово для поиска
        element.sendKeys("гладиолус");

        // Отправляем форму в которой находится элемент element.
        // WebDriver сам найдет, в какой он форме.
        element.submit();

        // Выводим в консоль заголовок страницы
        System.out.println("Page title is: " + driver.getTitle());


    }
}
