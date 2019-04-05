package fr.d2factory.libraryapp.exceptions;


public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message){
        super(message);
    }
}
