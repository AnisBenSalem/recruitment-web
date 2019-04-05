package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.communs.Constant;
import fr.d2factory.libraryapp.exceptions.InsufficientWalletBalanceException;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@NoArgsConstructor
public class Resident extends Member {

    @Override
    public float payBook(LocalDate startDate, LocalDate endDate, float wallet) {
        long nbrDays = DAYS.between(startDate,endDate);
        float amountToPay;
        if(nbrDays <= Constant.RESIDENT_MAX_LIMIT_DAYS)
            amountToPay = Constant.FEES_PER_DAY * nbrDays;
        else
            amountToPay = (Constant.FEES_PER_DAY * Constant.RESIDENT_MAX_LIMIT_DAYS
                    + ((Constant.RESIDENT_MAJOR_FEES) * (nbrDays - Constant.RESIDENT_MAX_LIMIT_DAYS)));
        if(amountToPay > wallet){
            throw new InsufficientWalletBalanceException("Insuficient balance to pay");
        }
        return amountToPay;
    }

    @Override
    public int getDelayOfReturnBook() {
        return Constant.RESIDENT_MAX_LIMIT_DAYS;
    }

}
