package Main;

public class News {
    private String date;
    private String company;
    private String url_company;
    private String article;
    private String url_article;

    public News(String date, String company, String url_company, String article, String url_article) {
        this.date = date;
        this.company = company;
        this.url_company = url_company;
        this.article = article;
        this.url_article = url_article;
    } // Конструктор с параметрами

    @Override
    public String toString() {
        return date + " | " + company + " | " + article;
    } // Удобный вид

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUrl_company() {
        return url_company;
    }

    public void setUrl_company(String url_company) {
        this.url_company = url_company;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getUrl_article() {
        return url_article;
    }

    public void setUrl_article(String url_article) {
        this.url_article = url_article;
    }
} // Класс и методы для работы с новостями
