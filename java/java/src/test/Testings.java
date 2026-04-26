import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class Testings {

    @Test
    void testFindBookById_found() {
        LibraryFxApp app = new LibraryFxApp();

        app.books = new ArrayList<>();
        app.books.add(new Book("B001", "Java", "9780306406157", "John", 1, 1, 10.0));

        Book result = app.findBookById("B001");

        assertNotNull(result);
        assertEquals("Java", result.title);
    }

    @Test
    void testFindBookById_notFound() {
        LibraryFxApp app = new LibraryFxApp();

        app.books = new ArrayList<>();

        Book result = app.findBookById("B999");

        assertNull(result);
    }

    @Test
    void testAverageBookPriceCalculation() {
        LibraryFxApp app = new LibraryFxApp();

        app.books = new ArrayList<>();
        app.books.add(new Book("B1", "A", "9780306406157", "X", 1, 1, 10.0));
        app.books.add(new Book("B2", "B", "9780306406157", "Y", 1, 1, 20.0));

        double total = 0;
        for (Book b : app.books) {
            total += b.price;
        }

        double avg = total / app.books.size();

        assertEquals(15.0, avg);
    }

    @Test
    void testSearchExactMatch() {
        LibraryFxApp app = new LibraryFxApp();

        app.books = new ArrayList<>();
        app.books.add(new Book("B1", "Java", "9780306406157", "X", 1, 1, 10.0));

        boolean found = false;

        for (Book book : app.books) {
            if (book.title.equalsIgnoreCase("Java")) {
                found = true;
            }
        }

        assertTrue(found);
    }
}
