package fr.d2factory.libraryapp.book;

import fr.d2factory.libraryapp.communs.Constant;
import fr.d2factory.libraryapp.exceptions.BookNotFoundException;
import fr.d2factory.libraryapp.exceptions.HasLateBooksException;
import fr.d2factory.libraryapp.member.Member;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.d2factory.libraryapp.communs.Constant.MEMBER_RECHEAD_DATE_LIMIT;
import static java.time.temporal.ChronoUnit.DAYS;


/**
 * The book repository emulates a database via 2 HashMaps
 */
@Getter
public class BookRepository {
    private Map<ISBN, Book> availableBooks;
    private Map<Member, Map<Book, LocalDate>> borrowedBooks;

    private static BookRepository bookRepositoryInstance;

    private BookRepository() {
        availableBooks = new HashMap<>();
        borrowedBooks = new HashMap<>();
    }

    public static BookRepository getInstance() {
        if (bookRepositoryInstance == null)
            bookRepositoryInstance = new BookRepository();
        return bookRepositoryInstance;
    }

    public void addBooks(List<Book> books) {
        books.forEach(book->availableBooks.put(book.getIsbn(),book));
    }

    public Book findBookFromAvailable(long isbnCode) {
        List<Book> books = new ArrayList<>(availableBooks.values());
        return books.stream().filter(book -> isbnCode == book.isbn.isbnCode)
                .findAny()
                .orElseThrow(() -> new BookNotFoundException(Constant.BOOK_REQUIRED_NOT_FOUNT));
    }

    public Book findBookFromBorrowed(long isbnCode) {
        return borrowedBooks.entrySet()
                .stream()
                .flatMap(e -> e.getValue().entrySet().stream())
                .filter(book -> isbnCode == book.getKey().isbn.isbnCode)
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow(() -> new BookNotFoundException(Constant.BOOK_REQUIRED_NOT_FOUNT));
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt, Member member) {
        Map<Book, LocalDate> borrowed = new HashMap<>();
        borrowed.put(book, borrowedAt);
        borrowedBooks.put(member, borrowed);
        //remove from available
        availableBooks.remove(book.isbn, book);
    }

    public LocalDate findBorrowedBookDate(Book book) {
        return borrowedBooks.entrySet()
                .stream()
                .flatMap(e -> e.getValue().entrySet().stream())
                .filter(bookBorrowed -> book.equals(bookBorrowed.getKey()))
                .map(Map.Entry::getValue)
                .findAny()
                .orElseThrow(() -> new BookNotFoundException(Constant.BOOK_REQUIRED_NOT_FOUNT));
    }

    public void makeBookAvailable(Book book, Member member) {
        Map<Book, LocalDate> borrowedBook = extractBooksFromBorrowed(book);
        // remove from borrowed
        for (Map.Entry<Member, Map<Book, LocalDate>> entry : borrowedBooks.entrySet()) {
            if (entry.getKey() == member && entry.getValue() == borrowedBook) {
                borrowedBooks.remove(entry);
            }
        }
        // add to available books
        saveAvailable(book);
    }

    public Map<Book, LocalDate> getBorrowedListBooksByMember(Member member) {
        Map<Book, LocalDate> listOfBooks = new HashMap<>();
        for (Map.Entry<Member, Map<Book, LocalDate>> entry : borrowedBooks.entrySet()) {
            if (entry.getKey() == member) {
                for (Map.Entry<Book, LocalDate> borrowed : entry.getValue().entrySet()) {
                    listOfBooks.put(borrowed.getKey(), borrowed.getValue());
                }
            }
        }
        return listOfBooks;
    }

    public void checkBookReturnedInTime(Map<Book, LocalDate> books, Member member, LocalDate borrowedAt) {
        long numberOfDaysBorrowed;
        for (Map.Entry<Book, LocalDate> b : books.entrySet()) {
            numberOfDaysBorrowed = DAYS.between( b.getValue() ,borrowedAt);
            if (numberOfDaysBorrowed >= member.getDelayOfReturnBook())
                throw new HasLateBooksException(MEMBER_RECHEAD_DATE_LIMIT);
        }
    }

    private Map<Book, LocalDate> extractBooksFromBorrowed(Book book) {
        return borrowedBooks.entrySet()
                .stream()
                .flatMap(e -> e.getValue().entrySet().stream())
                .filter(bookBorrowed -> book.equals(bookBorrowed.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void saveAvailable(Book book) {
        availableBooks.put(book.isbn, book);
    }


}
