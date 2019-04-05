package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;

import java.time.LocalDate;
import java.util.Map;

public class LibraryImpl implements Library {

    private BookRepository bookRepository = BookRepository.getInstance();

    @Override
    public void borrowBook(long isbnCode, Member member, LocalDate borrowedAt){
        // get all borrowed books
        Map<Book , LocalDate> borrowedBooks = bookRepository.getBorrowedListBooksByMember(member);
        // check if all borrowed books of member are returned in time
        bookRepository.checkBookReturnedInTime(borrowedBooks,member,borrowedAt);
        // find if book requested is available
        Book book = bookRepository.findBookFromAvailable(isbnCode);
        // save to list borrowed books
        bookRepository.saveBookBorrow(book,borrowedAt,member);
    }

    @Override
    public void returnBook(Book book, Member member) {
        // find borrowed book
        Book bookBorrowed = bookRepository.findBookFromBorrowed(book.getIsbn().getIsbnCode());
        // pay book based on number of date
        member.payBook(bookRepository.findBorrowedBookDate(bookBorrowed),LocalDate.now(),member.getWallet());
        // make the returned book available
        bookRepository.makeBookAvailable(bookBorrowed,member);
    }
}
