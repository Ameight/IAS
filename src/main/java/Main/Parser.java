package Main;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Parser {

    public WebDriver webDrover;

    public Parser(){
        ChromeOptions opts = new ChromeOptions();
        opts.setAcceptInsecureCerts(true);
        opts.setPageLoadStrategy(PageLoadStrategy.NONE);
        opts.setHeadless(true);
        opts.addArguments("--incognito");
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver.exe");
        webDrover = new ChromeDriver(opts);
    } // конструктор по умолчанию

    private void _getDateBirthOfCompany(){

    }

    public List<News> get_news(String nameCompany) throws InterruptedException {
        List<News> lst = new ArrayList<News>(1200);
        String src = preparedSource(nameCompany);
        if(src == null){
            return null;
        } //
        Document doc = Jsoup.parse(src);
        Elements els = doc.body().select("#cont_wrap > table > tbody").get(0).children();

        for (Element el : els) {
            String date = el.getElementsByTag("td").get(0).text();
            String company = el.getElementsByTag("td").get(1).getElementsByTag("a").get(0).text();
            String url_company = el.getElementsByTag("td").get(1).getElementsByTag("a").get(0).attr("href");
            String article = el.getElementsByTag("td").get(1).getElementsByTag("a").get(1).text();
            String url_article = el.getElementsByTag("td").get(1).getElementsByTag("a").get(1).attr("href");
            News news = new News(date, company, url_company, article, url_article);
            lst.add(news);
            System.out.println(url_company);
        }
        return lst;
    } // Список со всеми новостями

    private String preparedSource(String nameCompany) throws InterruptedException {
        webDrover.get("https://e-disclosure.ru/poisk-po-soobshheniyam");
        Thread.sleep(5000);
        WebElement anotherTextFiled = webDrover.findElement(By.id("textfieldEvent"));
        anotherTextFiled.click();
        Thread.sleep(100);
        WebElement textFieldNameCompany = webDrover.findElement(By.id("textfieldCompany"));

        textFieldNameCompany.click();
        Thread.sleep(200);
        textFieldNameCompany.sendKeys(nameCompany);
        Thread.sleep(200);
        textFieldNameCompany.sendKeys(Keys.RETURN);
        Thread.sleep(2000);
        textFieldNameCompany.click();
        Thread.sleep(200);
        JavascriptExecutor jse = (JavascriptExecutor) webDrover;
        if(jse.executeScript("return document.getElementById('textfieldCompany').value").equals("")){
            webDrover.manage().timeouts().pageLoadTimeout(2, TimeUnit.SECONDS);
            webDrover.navigate().refresh();
            webDrover.quit();
            return null;
        }
        WebElement searchButton = webDrover.findElement(By.id("butt"));
        searchButton.click();
        Thread.sleep(3000);
        List<WebElement> pageEl = webDrover.findElements(By.name("pageSize"));
        if(pageEl.size() != 0){
            Select select = new Select(pageEl.get(0));
            select.selectByValue("2147483647");
            Thread.sleep(2000);
        }

        String source = webDrover.getPageSource();
        webDrover.quit();
        return source;
    } // Работа sil
}
