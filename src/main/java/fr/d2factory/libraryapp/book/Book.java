package fr.d2factory.libraryapp.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * A simple representation of a book
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    String title;
    String author;
    ISBN isbn;
}
