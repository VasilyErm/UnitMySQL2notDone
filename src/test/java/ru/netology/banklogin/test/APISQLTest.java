package ru.netology.banklogin.test;

import org.junit.jupiter.api.Test;
import ru.netology.banklogin.data.APIHelper;
import ru.netology.banklogin.data.DataHelper;
import ru.netology.banklogin.data.SQLHelper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class APISQLTest {

    @Test
    public void validTransferFromFirstCardToSecond() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        APIHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = SQLHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCode());
        var tokenInfo = APIHelper.sendQueryToVerify(verificationInfo, 200);
        var cardsBalances = APIHelper.sendQueryToGetCardBalances(tokenInfo.getToken(), 200);
        var firstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var secondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var transferInfo = new APIHelper.APITransferInfo(DataHelper.getFirstCardInfo().getNumber(),
                DataHelper.getSecondCardInfo().getNumber(),String amount);
        APIHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardsBalances = APIHelper.sendQueryToGetCardBalances(tokenInfo.getToken(), 200);
        var actualFirstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualSecondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        assertAll(() -> assertEquals(firstCardBalance - amount, actualFirstCardBalance),
                () -> assertEquals(secondCardBalance + amount, actualSecondCardBalance));
    }
}
