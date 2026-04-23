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
}