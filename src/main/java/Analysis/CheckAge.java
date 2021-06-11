package Analysis;

import Main.News;

import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class CheckAge {

    // Больше трех лет - надежно - 100
    // Меньше трех лет, но больше 2 - желтый - 50
    // Меньше двух лет - плохо - 0

    public static void checkAge(News company, LocalDate date) {
        LocalDate old = LocalDate.now().minusYears(27);
        LocalDate average = LocalDate.now().minusYears(20);
        if(date.isBefore(old)){
            company.addRating(100);
            return;
        }
        if(date.isAfter(old) && date.isBefore(average)){
            company.addRating(50);
            return;
        }
        if(date.isAfter(average)){
            company.addRating(0);
        }
    }

}
