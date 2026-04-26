import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

public class LibraryFxApp extends Application {

    public ArrayList<Book> books = new ArrayList<>();
    public ArrayList<Student> students = new ArrayList<>();
    public ArrayList<Transaction> transactions = new ArrayList<>();

    private StackPane outputStack;
    private TableView<Book> bookTable;
    private TableView<Student> studentTable;
    private TableView<Transaction> transactionTable;
    private TableView<Book> searchTable;
    private TableView<Book> issuedReportTable;
    private TextArea messageArea;
    private Label outputTitleLabel;

    private ArrayList<String> lastSearchResults = new ArrayList<>();

    private Label statBooks;
    private Label statStudents;
    private Label statTransactions;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        bookTable = new TableView<>();
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bookTable.setPlaceholder(new Label("No books loaded"));

        TableColumn<Book, String> colBookId = new TableColumn<>("ID");
        colBookId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().id));
        colBookId.setMinWidth(60);

        TableColumn<Book, String> colTitle = new TableColumn<>("Title");
        colTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title));
        colTitle.setMinWidth(160);

        TableColumn<Book, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isbn));
        colIsbn.setMinWidth(120);

        TableColumn<Book, String> colAuthor = new TableColumn<>("Author");
        colAuthor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().author));
        colAuthor.setMinWidth(120);

        TableColumn<Book, Number> colCopies = new TableColumn<>("Copies");
        colCopies.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().copies));
        colCopies.setMinWidth(60);

        TableColumn<Book, Number> colAvail = new TableColumn<>("Available");
        colAvail.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().available));
        colAvail.setMinWidth(70);

        TableColumn<Book, Number> colPrice = new TableColumn<>("Price");
        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().price));
        colPrice.setMinWidth(70);

        bookTable.getColumns().addAll(colBookId, colTitle, colIsbn, colAuthor, colCopies, colAvail, colPrice);

        studentTable = new TableView<>();
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        studentTable.setPlaceholder(new Label("No students loaded"));

        TableColumn<Student, String> colStudId = new TableColumn<>("Student ID");
        colStudId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().id));
        colStudId.setMinWidth(120);

        TableColumn<Student, String> colStudName = new TableColumn<>("Name");
        colStudName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colStudName.setMinWidth(200);

        studentTable.getColumns().addAll(colStudId, colStudName);

        transactionTable = new TableView<>();
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPlaceholder(new Label("No transactions loaded"));

        TableColumn<Transaction, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date));
        colDate.setMinWidth(100);

        TableColumn<Transaction, String> colTBookId = new TableColumn<>("Book ID");
        colTBookId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().bookId));
        colTBookId.setMinWidth(80);

        TableColumn<Transaction, String> colTStudId = new TableColumn<>("Student ID");
        colTStudId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().studentId));
        colTStudId.setMinWidth(100);

        TableColumn<Transaction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(c -> {
            int t = c.getValue().type;
            String label = (t == 1) ? "Issue" : (t == 2) ? "Return" : "Unknown";
            return new SimpleStringProperty(label);
        });
        colType.setMinWidth(80);

        transactionTable.getColumns().addAll(colDate, colTBookId, colTStudId, colType);

        searchTable = new TableView<>();
        searchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        searchTable.setPlaceholder(new Label("No results"));

        TableColumn<Book, String> colSrchId = new TableColumn<>("ID");
        colSrchId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().id));
        colSrchId.setMinWidth(60);

        TableColumn<Book, String> colSrchTitle = new TableColumn<>("Title");
        colSrchTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title));
        colSrchTitle.setMinWidth(200);

        TableColumn<Book, String> colSrchAuthor = new TableColumn<>("Author");
        colSrchAuthor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().author));
        colSrchAuthor.setMinWidth(140);

        TableColumn<Book, Number> colSrchAvail = new TableColumn<>("Available");
        colSrchAvail.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().available));
        colSrchAvail.setMinWidth(80);

        searchTable.getColumns().addAll(colSrchId, colSrchTitle, colSrchAuthor, colSrchAvail);

        issuedReportTable = new TableView<>();
        issuedReportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        issuedReportTable.setPlaceholder(new Label("No issued books for this date"));

        TableColumn<Book, String> colIrId = new TableColumn<>("Book ID");
        colIrId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().id));
        colIrId.setMinWidth(80);

        TableColumn<Book, String> colIrTitle = new TableColumn<>("Title");
        colIrTitle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().title));
        colIrTitle.setMinWidth(200);

        TableColumn<Book, String> colIrAuthor = new TableColumn<>("Author");
        colIrAuthor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().author));
        colIrAuthor.setMinWidth(140);

        TableColumn<Book, String> colIrIsbn = new TableColumn<>("ISBN");
        colIrIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isbn));
        colIrIsbn.setMinWidth(120);

        issuedReportTable.getColumns().addAll(colIrId, colIrTitle, colIrAuthor, colIrIsbn);

        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        messageArea.getStyleClass().add("message-area");

        outputStack = new StackPane(bookTable, studentTable, transactionTable, searchTable, issuedReportTable, messageArea);
        outputStack.getStyleClass().add("output-stack");
        VBox.setVgrow(outputStack, Priority.ALWAYS);
        showView(messageArea);

        outputTitleLabel = new Label("OUTPUT");
        outputTitleLabel.getStyleClass().add("output-label");

        Button btnLoadBooks = new Button("\u2709  Load Books");
        Button btnLoadStudents = new Button("\u263A  Load Students");
        Button btnLoadTransactions = new Button("\u21C4  Load Transactions");
        Button btnIssuedReport = new Button("\u2630  Issued Report");
        Button btnAveragePrice = new Button("\u2211  Avg Price");
        Button btnSearchTitle = new Button("\u2315  Search Title");
        Button btnExport = new Button("\u2913  Export Results");

        btnIssuedReport.getStyleClass().add("btn-outline");
        btnAveragePrice.getStyleClass().add("btn-outline");
        btnSearchTitle.getStyleClass().add("btn-outline");
        btnExport.getStyleClass().add("btn-outline");

        btnLoadBooks.setOnAction(e -> {
            books = loadBooks();
            bookTable.getItems().setAll(books);
            showView(bookTable);
            outputTitleLabel.setText("BOOKS  ·  " + books.size() + " records loaded");
            updateStats();
        });

        btnLoadStudents.setOnAction(e -> {
            students = loadStudents();
            studentTable.getItems().setAll(students);
            showView(studentTable);
            outputTitleLabel.setText("STUDENTS  ·  " + students.size() + " records loaded");
            updateStats();
        });

        btnLoadTransactions.setOnAction(e -> {
            transactions = loadTransactions();
            transactionTable.getItems().setAll(transactions);
            showView(transactionTable);
            outputTitleLabel.setText("TRANSACTIONS  ·  " + transactions.size() + " records loaded");
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

        btnExport.setOnAction(e -> {
            if (lastSearchResults.isEmpty()) {
                messageArea.setText("Nothing to export. Run a search first.");
                showView(messageArea);
                outputTitleLabel.setText("EXPORT");
            } else {
                exportSearchResults(lastSearchResults);
                messageArea.setText("Exported " + lastSearchResults.size()
                        + " result(s) to data/search_results.txt");
                showView(messageArea);
                outputTitleLabel.setText("EXPORT  ·  " + lastSearchResults.size() + " results saved");
            }
        });

        Label titleLabel = new Label("Library Management System");
        titleLabel.getStyleClass().add("main-title");

        Label subtitleLabel = new Label("Manage books, students and transactions");
        subtitleLabel.getStyleClass().add("sub-title");

        VBox titleBox = new VBox(2, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label headerIcon = new Label("\uD83D\uDCDA");
        headerIcon.getStyleClass().add("header-icon");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerBar = new HBox(titleBox, spacer, headerIcon);
        headerBar.getStyleClass().add("header-bar");
        headerBar.setAlignment(Pos.CENTER_LEFT);

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

        Label dataLabel = new Label("DATA");
        dataLabel.getStyleClass().add("section-label");

        HBox dataButtons = new HBox(12, btnLoadBooks, btnLoadStudents, btnLoadTransactions);
        dataButtons.setAlignment(Pos.CENTER_LEFT);

        VBox dataSection = new VBox(8, dataLabel, dataButtons);
        dataSection.setPadding(new Insets(0, 28, 0, 28));

        Label actionsLabel = new Label("ACTIONS");
        actionsLabel.getStyleClass().add("section-label");

        HBox actionButtons = new HBox(12, btnIssuedReport, btnAveragePrice, btnSearchTitle, btnExport);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        VBox actionsSection = new VBox(8, actionsLabel, actionButtons);
        actionsSection.setPadding(new Insets(0, 28, 0, 28));

        VBox outputSection = new VBox(8, outputTitleLabel, outputStack);
        outputSection.setPadding(new Insets(0, 28, 20, 28));
        VBox.setVgrow(outputSection, Priority.ALWAYS);

        VBox root = new VBox(20,
                headerBar,
                statsRow,
                dataSection,
                actionsSection,
                outputSection);
        VBox.setVgrow(outputSection, Priority.ALWAYS);

        Scene scene = new Scene(root, 960, 720);
        scene.getStylesheets().add("style.css");

        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showView(javafx.scene.Node target) {
        for (javafx.scene.Node child : outputStack.getChildren()) {
            child.setVisible(child == target);
            child.setManaged(child == target);
        }
    }

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
            messageArea.setText("Error loading books.\n" + e.getMessage());
            showView(messageArea);
            outputTitleLabel.setText("ERROR");
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
            messageArea.setText("Error loading students.\n" + e.getMessage());
            showView(messageArea);
            outputTitleLabel.setText("ERROR");
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
            messageArea.setText("Error loading transactions.\n" + e.getMessage());
            showView(messageArea);
            outputTitleLabel.setText("ERROR");
        }

        return transactions;
    }

    public void showBooksIssuedReport(String date) {
        ArrayList<Book> issuedBooks = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.date.equals(date) && transaction.type == 1) {
                Book book = findBookById(transaction.bookId);
                if (book != null) {
                    issuedBooks.add(book);
                }
            }
        }

        issuedReportTable.getItems().setAll(issuedBooks);
        showView(issuedReportTable);
        outputTitleLabel.setText("ISSUED REPORT  ·  " + date + "  ·  " + issuedBooks.size() + " books");

        if (issuedBooks.isEmpty()) {
            messageArea.setText("No issued books found for " + date + ".");
            showView(messageArea);
            outputTitleLabel.setText("ISSUED REPORT  ·  " + date);
        }
    }

    public void showAverageBookPrice() {
        if (books.size() == 0) {
            messageArea.setText("No books available. Load books first.");
            showView(messageArea);
            outputTitleLabel.setText("AVERAGE PRICE");
            return;
        }

        double total = 0;
        for (Book book : books) {
            total += book.price;
        }

        double average = total / books.size();
        messageArea.setText(String.format(
                "Average Book Price\n\n" +
                "  Total books  :  %d\n" +
                "  Sum of prices:  %.2f\n" +
                "  Average price:  %.2f", books.size(), total, average));
        showView(messageArea);
        outputTitleLabel.setText("AVERAGE PRICE  ·  " + String.format("%.2f", average));
    }

    public void showSearchResults(String search) {
        ArrayList<Book> matchedBooks = new ArrayList<>();
        ArrayList<String> resultLines = new ArrayList<>();

        if (search.endsWith("*")) {
            String prefix = search.substring(0, search.length() - 1).toLowerCase();

            for (Book book : books) {
                if (book.title.toLowerCase().startsWith(prefix)) {
                    matchedBooks.add(book);
                    resultLines.add("Title: " + book.title + ", Available: " + book.available);
                }
            }
        } else {
            for (Book book : books) {
                if (book.title.equalsIgnoreCase(search)) {
                    matchedBooks.add(book);
                    resultLines.add("Title: " + book.title + ", Available: " + book.available);
                }
            }
        }

        lastSearchResults = resultLines;

        searchTable.getItems().setAll(matchedBooks);
        showView(searchTable);
        outputTitleLabel.setText("SEARCH RESULTS  ·  \"" + search + "\"  ·  " + matchedBooks.size() + " found");

        if (matchedBooks.isEmpty()) {
            messageArea.setText("No matching books found for \"" + search + "\".");
            showView(messageArea);
            outputTitleLabel.setText("SEARCH RESULTS  ·  \"" + search + "\"");
        } else {
            exportSearchResults(resultLines);
        }
    }

    public void exportSearchResults(ArrayList<String> results) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("../../data/search_results.txt"));
            for (String line : results) {
                writer.println(line);
            }
            writer.close();
        } catch (Exception e) {
            messageArea.setText("Error exporting search results.\n" + e.getMessage());
            showView(messageArea);
            outputTitleLabel.setText("EXPORT ERROR");
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