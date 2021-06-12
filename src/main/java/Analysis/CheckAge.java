package Analysis;

import Models.News;

import java.time.LocalDate;

public class CheckAge {

    public static void checkAge(News company, LocalDate date) {
        LocalDate old = LocalDate.now().minusYears(27);
        LocalDate average = LocalDate.now().minusYears(20);
        if (date.isBefore(old)) {
            company.addRating(100);
            return;
        }
        if (date.isAfter(old) && date.isBefore(average)) {
            company.addRating(50);
            return;
        }
        if (date.isAfter(average)) {
            company.addRating(0);
        }
    }
}
