import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class LibraryFxApp extends Application {

    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        outputArea = new TextArea();
        outputArea.setPrefHeight(400);

        Button btnLoadBooks = new Button("Load Books");
        Button btnLoadStudents = new Button("Load Students");
        Button btnLoadTransactions = new Button("Load Transactions");
        Button btnIssuedReport = new Button("Books Issued Report");
        Button btnAveragePrice = new Button("Average Book Price");
        Button btnSearchTitle = new Button("Search by Title");

        btnLoadBooks.setOnAction(e -> {
            books = loadBooks();
            outputArea.setText("Books loaded successfully.\n");
            for (Book book : books) {
                outputArea.appendText(book.toString() + "\n");
            }
        });

        btnLoadStudents.setOnAction(e -> {
            students = loadStudents();
            outputArea.setText("Students loaded successfully.\n");
            for (Student student : students) {
                outputArea.appendText(student.toString() + "\n");
            }
        });

        btnLoadTransactions.setOnAction(e -> {
            transactions = loadTransactions();
            outputArea.setText("Transactions loaded successfully.\n");
            for (Transaction transaction : transactions) {
                outputArea.appendText(transaction.toString() + "\n");
            }
        });

        btnIssuedReport.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Books Issued Report");
            dialog.setHeaderText("Enter date");
            dialog.setContentText("Date (DD/MM/YYYY):");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(date -> showBooksIssuedReport(date));
        });

        btnAveragePrice.setOnAction(e -> showAverageBookPrice());

        btnSearchTitle.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Search Book");
            dialog.setHeaderText("Enter title or prefix");
            dialog.setContentText("Title or prefix*: ");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(this::showSearchResults);
        });

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        HBox topBar = new HBox(15, btnLoadBooks, btnLoadStudents, btnLoadTransactions);
        topBar.setAlignment(Pos.CENTER);
        topBar.getStyleClass().add("top-bar");

        HBox bottomBar = new HBox(15, btnIssuedReport, btnAveragePrice, btnSearchTitle);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.getStyleClass().add("bottom-bar");

        ImageView imageView = new ImageView(new Image("file:book.jpg"));
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(true);

        Label centerTitle = new Label("Library Management System");
        centerTitle.getStyleClass().add("main-title");

        VBox centerBox = new VBox(20, centerTitle, imageView);
        centerBox.setAlignment(Pos.CENTER);

        outputArea.setPrefHeight(180);

        VBox bottomSection = new VBox(15, bottomBar, outputArea);
        bottomSection.setAlignment(Pos.CENTER);

        root.setTop(topBar);
        root.setCenter(centerBox);
        root.setBottom(bottomSection);

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add("style.css");

        primaryStage.setTitle("Library Management System - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ArrayList<Book> loadBooks() {
        ArrayList<Book> books = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("../data/book.csv"));
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 7) continue;

                String id = parts[0];
                String title = parts[1];
                String isbn = parts[2].replace("-", "").trim();
                String author = parts[3];
                int copies = Integer.parseInt(parts[4]);
                int available = Integer.parseInt(parts[5]);
                double price = Double.parseDouble(parts[6]);

                if (!Validator.validBookId(id)) continue;
                if (!Validator.validIsbn13(isbn)) continue;
                if (!Validator.validCopies(copies)) continue;
                if (!Validator.validAvailability(available, copies)) continue;

                books.add(new Book(id, title, isbn, author, copies, available, price));
            }

            br.close();
        } catch (Exception e) {
            outputArea.setText("Error loading books.\n" + e.getMessage());
        }

        return books;
    }

    public ArrayList<Student> loadStudents() {
        ArrayList<Student> students = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("../data/student.csv"));
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 2) continue;

                String id = parts[0];
                String name = parts[1];

                if (!Validator.validStudentId(id)) continue;

                students.add(new Student(id, name));
            }

            br.close();
        } catch (Exception e) {
            outputArea.setText("Error loading students.\n" + e.getMessage());
        }

        return students;
    }

    public ArrayList<Transaction> loadTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("../data/transactions.csv"));
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 4) continue;

                String date = parts[0];
                String bookId = parts[1];
                String studentId = parts[2];
                int type = Integer.parseInt(parts[3]);

                if (!Validator.validDate(date)) continue;
                if (!Validator.validBookId(bookId)) continue;
                if (!Validator.validStudentId(studentId)) continue;
                if (!Validator.validTransactionType(type)) continue;

                transactions.add(new Transaction(date, bookId, studentId, type));
            }

            br.close();
        } catch (Exception e) {
            outputArea.setText("Error loading transactions.\n" + e.getMessage());
        }

        return transactions;
    }

    public void showBooksIssuedReport(String date) {
        outputArea.setText("BOOKS ISSUED REPORT FOR " + date + "\n\n");

        boolean found = false;

        for (Transaction transaction : transactions) {
            if (transaction.date.equals(date) && transaction.type == 1) {
                Book book = findBookById(transaction.bookId);
                found = true;

                if (book != null) {
                    outputArea.appendText("Book ID: " + book.id + ", Title: " + book.title + "\n");
                } else {
                    outputArea.appendText("Book ID: " + transaction.bookId + ", Title not found\n");
                }
            }
        }

        if (!found) {
            outputArea.appendText("No issued books found for this date.\n");
        }
    }

    public void showAverageBookPrice() {
        outputArea.setText("AVERAGE BOOK PRICE\n\n");

        if (books.size() == 0) {
            outputArea.appendText("No books available.\n");
            return;
        }

        double total = 0;
        for (Book book : books) {
            total += book.price;
        }

        double average = total / books.size();
        outputArea.appendText("Average Book Price: " + average + "\n");
    }

    public void showSearchResults(String search) {
        outputArea.setText("SEARCH RESULTS\n\n");

        boolean found = false;
        ArrayList<String> results = new ArrayList<>();

        if (search.endsWith("*")) {
            String prefix = search.substring(0, search.length() - 1).toLowerCase();

            for (Book book : books) {
                if (book.title.toLowerCase().startsWith(prefix)) {
                    String line = "Title: " + book.title + ", Available: " + book.available;
                    outputArea.appendText(line + "\n");
                    results.add(line);
                    found = true;
                }
            }
        } else {
            for (Book book : books) {
                if (book.title.equalsIgnoreCase(search)) {
                    String line = "Title: " + book.title + ", Available: " + book.available;
                    outputArea.appendText(line + "\n");
                    results.add(line);
                    found = true;
                }
            }
        }

        if (!found) {
            outputArea.appendText("No matching books found.\n");
        } else {
            exportSearchResults(results);
        }
    }

    public void exportSearchResults(ArrayList<String> results) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("../data/search_results.txt"));
            for (String line : results) {
                writer.println(line);
            }
            writer.close();
            outputArea.appendText("\nSearch results exported to data/search_results.txt\n");
        } catch (Exception e) {
            outputArea.appendText("\nError exporting search results.\n");
        }
    }

    public Book findBookById(String id) {
        for (Book book : books) {
            if (book.id.equalsIgnoreCase(id)) {
                return book;
            }
        }
        return null;
    }
}