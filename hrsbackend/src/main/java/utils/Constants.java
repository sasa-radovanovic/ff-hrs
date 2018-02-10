package utils;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by sasaradovanovic on 12/29/17.
 */
public class Constants {

    public static class App {
        public static final String URL = "http://localhost:8087";
    }

    public static class Database {
        public static final String DB_NAME = "hrs";
        public static final String DB_HOST = "127.0.0.1";
        public static final int DB_PORT = 27017;
    }

    public enum Roles {
        ROLE_USER, ROLE_TEAM_ADMIN, ROLE_ADMIN
    }


    public static class Email {
        public static final String SMTP_HOST = "smtp.gmail.com";
        public static final String SSL_PORT = "465";
        public static final String EMAIL_ADDRESS = "ff.holidayrequestsystem@gmail.com";
        public static final String EMAIL_PASSWORD= "holidayrequestsystem";
    }


    public static class TimeConstants {
        public static final Format formatter = new SimpleDateFormat("dd/MM/yyyy");
        public static final DateFormat simpleDateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        public static Date fromLocalDate (LocalDate localDate) {
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        public static final DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

}
