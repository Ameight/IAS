package Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ArticleTable {

    private final SimpleIntegerProperty position;
    private final SimpleStringProperty nameCompany;
    private final SimpleStringProperty description;

    public ArticleTable(int position, String nameCompany, String description) {
        this.position = new SimpleIntegerProperty(position);
        this.nameCompany = new SimpleStringProperty(nameCompany);
        this.description = new SimpleStringProperty(description);
    }

    public int getPosition() {
        return this.position .get();
    }

    public void setPosition(int nameCompany) {
        this.position .set(nameCompany);
    }

    public String getNameCompany() {
        return this.nameCompany.get();
    }

    public void setNameCompany(String nameCompany) {
        this.nameCompany.set(nameCompany);
    }

    public String getDescription() {
        return this.description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

} // Описание таблицы

