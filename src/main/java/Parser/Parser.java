package Parser;

import Analysis.CheckAge;
import Models.News;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Parser {

    public WebDriver webDriver;

    public ChromeOptions opts;

    public HashMap<String, Integer> speedUpCheck = new HashMap<>();

    public Parser() {
        opts = new ChromeOptions();
        opts.setAcceptInsecureCerts(true);
        opts.setPageLoadStrategy(PageLoadStrategy.NONE);
        opts.setHeadless(true);
        opts.addArguments("--incognito");
        System.setProperty("webdriver.chrome.driver", "C:/chromedriver.exe");
        webDriver = new ChromeDriver(opts);
    } // конструктор по умолчанию

    private void _getDateBirthOfCompany(News company) {
        if (company == null) {
            return;
        }
        if (speedUpCheck.containsKey(company.getUrl_company())) {
            company.addRating(speedUpCheck.get(company.getUrl_company()));
            return;
        }
        webDriver = new ChromeDriver(opts);
        webDriver.get(company.getUrl_company());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(webDriver.getPageSource());
        webDriver.quit();
        Elements table = doc.body().select("#cont_wrap > table > tbody").get(0).children();
        String date = table.get(4).getElementsByTag("td").get(1).text();
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        int tempRating = company.getRating();
        CheckAge.checkAge(company, LocalDate.parse(date, formatDate));
        tempRating = company.getRating() - tempRating;
        speedUpCheck.put(company.getUrl_company(), tempRating);
    }

    public List<News> get_news(String nameCompany) throws InterruptedException {
        List<News> lst = new ArrayList<News>(1200);
        String src = preparedSource(nameCompany);
        if (src == null) {
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
            _getDateBirthOfCompany(news);
            lst.add(news);
        }
        if (lst.size() == 0) {
            return null;
        }
        return lst;
    } // Список со всеми новостями

    private String preparedSource(String nameCompany) throws InterruptedException {
        webDriver.get("https://e-disclosure.ru/poisk-po-soobshheniyam");
        WebDriverWait pausing = new WebDriverWait(webDriver, 10);
        pausing.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        WebElement anotherTextFiled = webDriver.findElement(By.id("textfieldEvent"));
        pausing.until(ExpectedConditions.elementToBeClickable(By.id("textfieldEvent")));
        anotherTextFiled.click();

        WebElement textFieldNameCompany = webDriver.findElement(By.id("textfieldCompany"));
        pausing.until(ExpectedConditions.visibilityOf(textFieldNameCompany));
        textFieldNameCompany.click();
        textFieldNameCompany.sendKeys(nameCompany);
        JavascriptExecutor jse = (JavascriptExecutor) webDriver;

        if (jse.executeScript("return document.getElementById('textfieldCompany').value").equals("")) {
            webDriver.manage().timeouts().pageLoadTimeout(2, TimeUnit.SECONDS);
            webDriver.navigate().refresh();
            webDriver.quit();
            return null;
        }
        WebElement searchButton = webDriver.findElement(By.id("butt"));
        searchButton.click();
        Thread.sleep(2000);
        List<WebElement> pageEl = webDriver.findElements(By.name("pageSize"));
        if (pageEl.size() != 0) {
            Select select = new Select(pageEl.get(0));
            select.selectByValue("2147483647");
            Thread.sleep(2000);
        }

        String source = webDriver.getPageSource();
        webDriver.quit();
        return source;
    } // Работа sil
}
