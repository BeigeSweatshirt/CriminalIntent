package android.beige.criminalintent.database;

public class CrimeDbSchema {
    public static final class CrimeTable {
        public static final String NAME = "crimes";

        public static final class Columns {
            public static String UUID = "uuid";
            public static String TITLE = "title";
            public static String DATE = "date";
            public static String SOLVED = "solved";
        }
    }
}
