package fr.d2factory.libraryapp.library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.exceptions.BookNotFoundException;
import fr.d2factory.libraryapp.exceptions.HasLateBooksException;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.StudentWithLess1Year;
import fr.d2factory.libraryapp.member.StudentWithMore1Year;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.core.IsNot.not;

public class LibraryTest {
    private Library library;
    private BookRepository bookRepository;

    @Before
    public void setup() throws IOException {
        //instantiate the library and the repository
        bookRepository = BookRepository.getInstance();
        library = new LibraryImpl();
        //Fill the list of books from books json file
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("books.json")).getFile());
        ObjectMapper mapper = new ObjectMapper();
        List<Book> books = mapper.readValue(file, new TypeReference<List<Book>>() {});
        //add books to the library
        bookRepository.addBooks(books);
    }

    @Test
    public void member_can_borrow_a_book_if_book_is_available() throws BookNotFoundException {

        //Given
        Resident resident = new Resident();
        resident.setWallet(200);

        //When
        Book book = bookRepository.findBookFromAvailable(3326456467846L);
        library.borrowBook(3326456467846L, resident, LocalDate.of(2018, 1, 1));

        //Asserts
        Assert.assertThat(bookRepository.getAvailableBooks() , not(IsMapContaining.hasEntry(book.getIsbn(), book)));
        Assert.assertThat(bookRepository.getBorrowedBooks() , IsMapContaining.hasKey(resident));
    }

    @Test
    public void borrowed_book_is_no_longer_available() throws BookNotFoundException{

        //Given
        Resident resident = new Resident();
        resident.setWallet(200);

        //When
        Book book = bookRepository.findBookFromAvailable(3326456467846L);
        library.borrowBook(3326456467846L, resident, LocalDate.of(2018, 1, 1));

        //Assert
        Assert.assertThat(bookRepository.getAvailableBooks() , not(IsMapContaining.hasEntry(book.getIsbn(), book)));
    }

    @Test
    public void residents_are_taxed_10cents_for_each_day_they_keep_a_book() {
        //Given
        Resident resident = new Resident();
        resident.setWallet(200);
        LocalDate today = LocalDate.now();

        //When
        library.borrowBook(3326456467846L, resident, today);

        //Asserts
        Assert.assertEquals(0.10,resident.payBook(today,today.plusDays(1),resident.getWallet())  , 0.01);

    }

    @Test
    public void students_pay_10_cents_the_first_30days() {
        //Given
        StudentWithMore1Year student = new StudentWithMore1Year();
        student.setWallet(400);
        LocalDate today = LocalDate.now();

        //When
        library.borrowBook(3326456467846L, student, today);

        //Asserts
        Assert.assertEquals(3.0,student.payBook(today,today.plusDays(30),student.getWallet()) ,0.01);
    }

    @Test
    public void students_in_1st_year_are_not_taxed_for_the_first_15days() {
        //Given
        StudentWithLess1Year student = new StudentWithLess1Year();
        student.setWallet(0);
        LocalDate today = LocalDate.now();

        //When
        library.borrowBook(3326456467846L, student, today);

        //Asserts
        Assert.assertEquals(0 ,student.payBook(today,today.plusDays(15),student.getWallet()) , 0.01);
    }

    @Test
    public void students_pay_15cents_for_each_day_they_keep_a_book_after_the_initial_30days() {
        //Given
        StudentWithLess1Year student = new StudentWithLess1Year();
        student.setWallet(500);
        LocalDate today = LocalDate.now();

        //When
        library.borrowBook(3326456467846L, student, today);

        //Asserts
        Assert.assertEquals(0.15,student.payBook(today,today.plusDays(31),student.getWallet()) -
                student.payBook(today,today.plusDays(30),student.getWallet()) , 0.01);

    }

    @Test
    public void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days() {
        //Given
        Resident resident = new Resident();
        resident.setWallet(1000);
        LocalDate today = LocalDate.now();

        //When
        library.borrowBook(3326456467846L, resident, today);

        //Asserts
        Assert.assertEquals(0.2 ,resident.payBook(today,today.plusDays(61),resident.getWallet()) -
                resident.payBook(today,today.plusDays(60),resident.getWallet())  , 0.01);
    }

    @Test(expected = HasLateBooksException.class)
    public void members_cannot_borrow_book_if_they_have_late_books(){
        //Given
        Resident resident = new Resident();
        resident.setWallet(1000);
        LocalDate today = LocalDate.now();

        //When
        //borrow a book 4 moths ago
        library.borrowBook(3326456467846L, resident, today.minusMonths(4));
        //trying to borrow a book today
        library.borrowBook(46578964513L, resident, today);
    }
}
