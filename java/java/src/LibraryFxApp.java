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
import javafx.scene.layout.HBox;

public class LibraryFxApp extends Application {

    public ArrayList<Book> books = new ArrayList<>();
    public ArrayList<Student> students = new ArrayList<>();
    public ArrayList<Transaction> transactions = new ArrayList<>();

    private TextArea outputArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPromptText("Output will appear here...");

        // ── Buttons ──
        Button btnLoadBooks = new Button("\u2709  Load Books");
        Button btnLoadStudents = new Button("\u263A  Load Students");
        Button btnLoadTransactions = new Button("\u21C4  Load Transactions");
        Button btnIssuedReport = new Button("\u2630  Issued Report");
        Button btnAveragePrice = new Button("\u2211  Avg Price");
        Button btnSearchTitle = new Button("\u2315  Search Title");

        // outline style for action buttons
        btnIssuedReport.getStyleClass().add("btn-outline");
        btnAveragePrice.getStyleClass().add("btn-outline");
        btnSearchTitle.getStyleClass().add("btn-outline");

        // ── Button handlers ──
        btnLoadBooks.setOnAction(e -> {
            books = loadBooks();
            outputArea.setText("Books loaded successfully.\n");
            for (Book book : books) {
                outputArea.appendText(book.toString() + "\n");
            }
            updateStats();
        });

        btnLoadStudents.setOnAction(e -> {
            students = loadStudents();
            outputArea.setText("Students loaded successfully.\n");
            for (Student student : students) {
                outputArea.appendText(student.toString() + "\n");
            }
            updateStats();
        });

        btnLoadTransactions.setOnAction(e -> {
            transactions = loadTransactions();
            outputArea.setText("Transactions loaded successfully.\n");
            for (Transaction transaction : transactions) {
                outputArea.appendText(transaction.toString() + "\n");
            }
            updateStats();
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
            dialog.setContentText("Title or prefix: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(this::showSearchResults);
        });

        // ═══════════════════════════════════════════
        // LAYOUT
        // ═══════════════════════════════════════════

        // ── Header ──
        Label titleLabel = new Label("Library Management System");
        titleLabel.getStyleClass().add("main-title");

        Label subtitleLabel = new Label("Manage books, students and transactions");
        subtitleLabel.getStyleClass().add("sub-title");

        VBox titleBox = new VBox(2, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        HBox headerBar = new HBox(titleBox);
        headerBar.getStyleClass().add("header-bar");
        headerBar.setAlignment(Pos.CENTER_LEFT);

        // ── Stat Cards ──
        statBooks = new Label("0");
        statStudents = new Label("0");
        statTransactions = new Label("0");

        statBooks.getStyleClass().add("stat-value");
        statStudents.getStyleClass().add("stat-value");
        statTransactions.getStyleClass().add("stat-value");

        VBox cardBooks = buildStatCard("Books", statBooks);
        VBox cardStudents = buildStatCard("Students", statStudents);
        VBox cardTransactions = buildStatCard("Transactions", statTransactions);

        HBox statsRow = new HBox(16, cardBooks, cardStudents, cardTransactions);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.setPadding(new Insets(0, 28, 0, 28));

        // ── Data Section ──
        Label dataLabel = new Label("DATA");
        dataLabel.getStyleClass().add("section-label");

        HBox dataButtons = new HBox(12, btnLoadBooks, btnLoadStudents, btnLoadTransactions);
        dataButtons.setAlignment(Pos.CENTER_LEFT);

        VBox dataSection = new VBox(8, dataLabel, dataButtons);
        dataSection.setPadding(new Insets(0, 28, 0, 28));

        // ── Actions Section ──
        Label actionsLabel = new Label("ACTIONS");
        actionsLabel.getStyleClass().add("section-label");

        HBox actionButtons = new HBox(12, btnIssuedReport, btnAveragePrice, btnSearchTitle);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        VBox actionsSection = new VBox(8, actionsLabel, actionButtons);
        actionsSection.setPadding(new Insets(0, 28, 0, 28));

        // ── Output Console ──
        Label outputLabel = new Label("OUTPUT");
        outputLabel.getStyleClass().add("output-label");

        outputArea.setPrefHeight(200);

        VBox outputSection = new VBox(8, outputLabel, outputArea);
        outputSection.setPadding(new Insets(0, 28, 20, 28));
        VBox.setVgrow(outputArea, javafx.scene.layout.Priority.ALWAYS);

        // ── Root Assembly ──
        VBox root = new VBox(20,
                headerBar,
                statsRow,
                dataSection,
                actionsSection,
                outputSection);
        VBox.setVgrow(outputSection, javafx.scene.layout.Priority.ALWAYS);

        Scene scene = new Scene(root, 780, 660);
        scene.getStylesheets().add("style.css");

        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // ── Stat card helper ──
    private Label statBooks;
    private Label statStudents;
    private Label statTransactions;

    private VBox buildStatCard(String label, Label valueLabel) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-label");
        VBox card = new VBox(4, lbl, valueLabel);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(180);
        return card;
    }

    private void updateStats() {
        statBooks.setText(String.valueOf(books.size()));
        statStudents.setText(String.valueOf(students.size()));
        statTransactions.setText(String.valueOf(transactions.size()));
    }

    public ArrayList<Book> loadBooks() {
        ArrayList<Book> books = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("../../data/book.csv"));
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 7)
                    continue;

                String id = parts[0];
                String title = parts[1];
                String isbn = parts[2].replace("-", "").trim();
                String author = parts[3];
                int copies = Integer.parseInt(parts[4]);
                int available = Integer.parseInt(parts[5]);
                double price = Double.parseDouble(parts[6]);

                if (!Validator.validBookId(id))
                    continue;
                if (!Validator.validIsbn13(isbn))
                    continue;
                if (!Validator.validCopies(copies))
                    continue;
                if (!Validator.validAvailability(available, copies))
                    continue;

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
            BufferedReader br = new BufferedReader(new FileReader("../../data/student.csv"));
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 2)
                    continue;

                String id = parts[0];
                String name = parts[1];

                if (!Validator.validStudentId(id))
                    continue;

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
            BufferedReader br = new BufferedReader(new FileReader("../../data/transactions.csv"));
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length != 4)
                    continue;

                String date = parts[0];
                String bookId = parts[1];
                String studentId = parts[2];
                int type = Integer.parseInt(parts[3]);

                if (!Validator.validDate(date))
                    continue;
                if (!Validator.validBookId(bookId))
                    continue;
                if (!Validator.validStudentId(studentId))
                    continue;
                if (!Validator.validTransactionType(type))
                    continue;

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