package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.communs.Constant;
import fr.d2factory.libraryapp.exceptions.InsufficientWalletBalanceException;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class StudentWithMore1Year extends Member {

    @Override
    public float payBook(LocalDate startDate, LocalDate endDate, float wallet) {
        long nbrDays = DAYS.between(startDate,endDate);
        float amountToPay ;
        if(nbrDays <= Constant.STUDENT_MAX_LIMIT_DAYS)
            amountToPay =  Constant.FEES_PER_DAY * nbrDays;
        else
            amountToPay =  (Constant.FEES_PER_DAY * Constant.STUDENT_MAX_LIMIT_DAYS +
                    ((Constant.FEES_PER_DAY + Constant.STUDENT_MAJOR_FEES) * (nbrDays - Constant.STUDENT_MAX_LIMIT_DAYS)));
        if(amountToPay > wallet){
            throw new InsufficientWalletBalanceException("Insufficient balance to pay ");
        }
        return amountToPay;
    }

    @Override
    public int getDelayOfReturnBook() {
        return Constant.STUDENT_MAX_LIMIT_DAYS;
    }
}
