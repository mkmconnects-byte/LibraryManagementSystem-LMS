public class Validator {

    public static boolean validStudentId(String id) {
        return id != null && id.matches("\\d{8}");
    }

    public static boolean validBookId(String id) {
        return id != null && id.matches("[A-Za-z]{2}\\d{2}");
    }

    public static boolean validCopies(int copies) {
        return copies >= 0 && copies <= 2;
    }

    public static boolean validAvailability(int available, int copies) {
        return available >= 0 && available <= copies;
    }

    public static boolean validTransactionType(int type) {
        return type == 1 || type == 2;
    }

    public static boolean validDate(String date) {
        if (date == null || !date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return false;
        }

        String[] parts = date.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (month < 1 || month > 12) {
            return false;
        }

        if (day < 1 || day > 31) {
            return false;
        }

        return year > 0;
    }

    public static boolean validIsbn13(String isbn) {
        if (isbn == null || !isbn.matches("\\d{13}")) {
            return false;
        }

        int total = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Integer.parseInt(String.valueOf(isbn.charAt(i)));
            if (i % 2 == 0) {
                total += digit;
            } else {
                total += digit * 3;
            }
        }

        int checkDigit = (10 - (total % 10)) % 10;
        int actualDigit = Integer.parseInt(String.valueOf(isbn.charAt(12)));

        return checkDigit == actualDigit;
    }
}