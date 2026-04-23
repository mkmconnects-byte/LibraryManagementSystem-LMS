import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.PrintWriter;

public class LibraryConsoleBackup {

    public static void main(String[] args) {
        ArrayList<Book> books = loadBooks();
        ArrayList<Student> students = loadStudents();
        ArrayList<Transaction> transactions = loadTransactions();

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("\nJAVA LIBRARY REPORT MENU");
            System.out.println("1. View books");
            System.out.println("2. View students");
            System.out.println("3. View transactions");
            System.out.println("4. Books issued report by date");
            System.out.println("5. Average book price");
            System.out.println("6. Search book by title");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            int choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                displayBooks(books);
            } else if (choice == 2) {
                displayStudents(students);
            } else if (choice == 3) {
                displayTransactions(transactions);
            } else if (choice == 4) {
                System.out.print("Enter date (DD/MM/YYYY): ");
                String date = input.nextLine();
                booksIssuedReportByDate(date, transactions, books);
            } else if (choice == 5) {
                averageBookPrice(books);
            } else if (choice == 6) {
                System.out.print("Enter title or prefix with * at end: ");
                String title = input.nextLine();
                searchBookByTitle(title, books);
            } else if (choice == 7) {
                System.out.println("Exiting program.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        input.close();
    }

    public static ArrayList<Book> loadBooks() {
        ArrayList<Book> books = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/book.csv"));
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 7) {
                    System.out.println("Invalid book record format: " + line);
                    continue;
                }

                String id = parts[0];
                String title = parts[1];
                String isbn = parts[2];
                String author = parts[3];

                int copies;
                int available;
                double price;

                try {
                    copies = Integer.parseInt(parts[4]);
                    available = Integer.parseInt(parts[5]);
                    price = Double.parseDouble(parts[6]);
                } catch (Exception e) {
                    System.out.println("Invalid number format in book record: " + line);
                    continue;
                }

                if (!Validator.validBookId(id)) {
                    System.out.println("Invalid Book ID: " + id);
                    continue;
                }

                if (!Validator.validIsbn13(isbn)) {
                    System.out.println("Invalid ISBN-13: " + isbn);
                    continue;
                }

                if (!Validator.validCopies(copies)) {
                    System.out.println("Invalid copies count: " + copies);
                    continue;
                }

                if (!Validator.validAvailability(available, copies)) {
                    System.out.println("Invalid availability: " + available);
                    continue;
                }

                Book book = new Book(id, title, isbn, author, copies, available, price);
                books.add(book);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error loading books.");
            e.printStackTrace();
        }

        return books;
    }

    public static ArrayList<Student> loadStudents() {
        ArrayList<Student> students = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/student.csv"));
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 2) {
                    System.out.println("Invalid student record format: " + line);
                    continue;
                }

                String id = parts[0];
                String name = parts[1];

                if (!Validator.validStudentId(id)) {
                    System.out.println("Invalid Student ID: " + id);
                    continue;
                }

                Student student = new Student(id, name);
                students.add(student);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error loading students.");
            e.printStackTrace();
        }

        return students;
    }

    public static ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/transactions.csv"));
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 4) {
                    System.out.println("Invalid transaction record format: " + line);
                    continue;
                }

                String date = parts[0];
                String bookId = parts[1];
                String studentId = parts[2];

                int type;
                try {
                    type = Integer.parseInt(parts[3]);
                } catch (Exception e) {
                    System.out.println("Invalid transaction type: " + line);
                    continue;
                }

                if (!Validator.validDate(date)) {
                    System.out.println("Invalid date: " + date);
                    continue;
                }

                if (!Validator.validBookId(bookId)) {
                    System.out.println("Invalid book ID in transaction: " + bookId);
                    continue;
                }

                if (!Validator.validStudentId(studentId)) {
                    System.out.println("Invalid student ID in transaction: " + studentId);
                    continue;
                }

                if (!Validator.validTransactionType(type)) {
                    System.out.println("Invalid transaction type value: " + type);
                    continue;
                }

                Transaction transaction = new Transaction(date, bookId, studentId, type);
                transactions.add(transaction);
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error loading transactions.");
            e.printStackTrace();
        }

        return transactions;
    }

    public static void displayBooks(ArrayList<Book> books) {
        System.out.println("\nBOOK LIST");
        System.out.println("---------");

        for (Book book : books) {
            System.out.println(book);
        }
    }

    public static void displayStudents(ArrayList<Student> students) {
        System.out.println("\nSTUDENT LIST");
        System.out.println("------------");

        for (Student student : students) {
            System.out.println(student);
        }
    }

    public static void displayTransactions(ArrayList<Transaction> transactions) {
        System.out.println("\nTRANSACTION LIST");
        System.out.println("----------------");

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    public static void booksIssuedReportByDate(String date, ArrayList<Transaction> transactions, ArrayList<Book> books) {
        System.out.println("\nBOOKS ISSUED REPORT FOR " + date);
        System.out.println("-----------------------------");

        boolean found = false;

        for (Transaction transaction : transactions) {
            if (transaction.date.equals(date) && transaction.type == 1) {
                found = true;
                Book book = findBookById(transaction.bookId, books);

                if (book != null) {
                    System.out.println("Book ID: " + book.id + ", Title: " + book.title);
                } else {
                    System.out.println("Book ID: " + transaction.bookId + ", Title not found");
                }
            }
        }

        if (!found) {
            System.out.println("No issued books found for this date.");
        }
    }

    public static void averageBookPrice(ArrayList<Book> books) {
        if (books.size() == 0) {
            System.out.println("No books available.");
            return;
        }

        double total = 0;

        for (Book book : books) {
            total += book.price;
        }

        double average = total / books.size();
        System.out.println("\nAverage Book Price: " + average);
    }

    public static void searchBookByTitle(String search, ArrayList<Book> books) {
        System.out.println("\nSEARCH RESULTS");
        System.out.println("--------------");

        boolean found = false;
        ArrayList<String> results = new ArrayList<>();

        if (search.endsWith("*")) {
            String prefix = search.substring(0, search.length() - 1).toLowerCase();

            for (Book book : books) {
                if (book.title.toLowerCase().startsWith(prefix)) {
                    found = true;
                    String line = "Title: " + book.title + ", Available: " + book.available;
                    results.add(line);
                    System.out.println(line);
                }
            }
        } else {
            for (Book book : books) {
                if (book.title.equalsIgnoreCase(search)) {
                    found = true;
                    String line = "Title: " + book.title + ", Available: " + book.available;
                    results.add(line);
                    System.out.println(line);
                }
            }
        }

        if (!found) {
            System.out.println("No matching books found.");
        } else {
            exportSearchResults(results);
        }
    }

    public static void exportSearchResults(ArrayList<String> results) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("data/search_results.txt"));

            for (String line : results) {
                writer.println(line);
            }

            writer.close();
            System.out.println("Search results exported to data/search_results.txt");

        } catch (Exception e) {
            System.out.println("Error exporting search results.");
            e.printStackTrace();
        }
    }

    public static Book findBookById(String id, ArrayList<Book> books) {
        for (Book book : books) {
            if (book.id.equalsIgnoreCase(id)) {
                return book;
            }
        }
        return null;
    }
}