public class Transaction {
    public String date;
    public String bookId;
    public String studentId;
    public int type; //

    public Transaction(String date, String bookId, String studentId, int type){
        this.date = date;
        this.bookId = bookId;
        this.studentId = studentId;
        this.type = type;
    }
}
