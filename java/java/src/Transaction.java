public class Transaction {
    public String date;
    public String bookId;
    public String studentId;
    public int type; // 1 = issue, 2 = return

    public Transaction(String date, String bookId, String studentId, int type) {
        this.date = date;
        this.bookId = bookId;
        this.studentId = studentId;
        this.type = type;
    }

    @Override
    public String toString() {
        String action;
        if (type == 1) {
            action = "Issue";
        } else if (type == 2) {
            action = "Return";
        } else {
            action = "Unknown";
        }

        return "Date: " + date +
                ", Book ID: " + bookId +
                ", Student ID: " + studentId +
                ", Type: " + action;
    }
}