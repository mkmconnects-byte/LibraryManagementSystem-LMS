public class Book {
    public String id;
    public String title;
    public String isbn;
    public String author;
    public int copies;
    public int available;
    public double price;

    public Book(String id, String title, String isbn, String author, int copies, int available, double price) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.copies = copies;
        this.available = available;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book ID: " + id +
                ", Title: " + title +
                ", ISBN: " + isbn +
                ", Author: " + author +
                ", Copies: " + copies +
                ", Available: " + available +
                ", Price: " + price;
    }
}