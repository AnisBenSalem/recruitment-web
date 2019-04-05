package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.Library;

import java.time.LocalDate;

/**
 * A member is a person who can borrow and return books to a {@link Library}
 * A member can be either a student or a resident
 */
public abstract class Member {
    /**
     * An initial sum of money the member has
     */
    private float wallet;

    /**
     * The member should pay their books when they are returned to the library
     *
     * @param startDate the date of borrowing the book
     *
     * @param endDate the returning book date
     *
     * @param wallet  wallet balance of the member
     */
    public abstract float payBook(LocalDate startDate, LocalDate endDate, float wallet);

    /**
     * The number of days to return a book by member
     */
    public abstract int getDelayOfReturnBook();

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }
}
