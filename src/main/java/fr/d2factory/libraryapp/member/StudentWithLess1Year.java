package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.communs.Constant;
import fr.d2factory.libraryapp.exceptions.InsufficientWalletBalanceException;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class StudentWithLess1Year extends Member {

    @Override
    public float payBook(LocalDate startDate, LocalDate endDate, float wallet) {
        long nbrDays = DAYS.between(startDate,endDate);
        float amountToPay ;
        if(nbrDays <= Constant.STUDENT_FIRST_YEAR_INITIAL_FREE_DAYS)
            amountToPay = 0;
        else if(nbrDays <= Constant.STUDENT_MAX_LIMIT_DAYS)
            amountToPay =  Constant.FEES_PER_DAY * (nbrDays - Constant.STUDENT_FIRST_YEAR_INITIAL_FREE_DAYS);
        else
            amountToPay =  (Constant.FEES_PER_DAY * (Constant.STUDENT_MAX_LIMIT_DAYS - Constant.STUDENT_FIRST_YEAR_INITIAL_FREE_DAYS)) +
                    ( Constant.STUDENT_MAJOR_FEES * (nbrDays - Constant.STUDENT_MAX_LIMIT_DAYS));
        if(amountToPay > wallet){
            throw new InsufficientWalletBalanceException(Constant.INSUFFICIENT_BALANCE);
        }
        return amountToPay;
    }

    @Override
    public int getDelayOfReturnBook() {
        return Constant.STUDENT_MAX_LIMIT_DAYS;
    }
}
