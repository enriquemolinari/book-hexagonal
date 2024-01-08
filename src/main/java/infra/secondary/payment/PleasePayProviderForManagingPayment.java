package infra.secondary.payment;

import hexagon.secondary.port.ForManagingPayments;

import java.time.YearMonth;

public class PleasePayProviderForManagingPayment implements ForManagingPayments {

    @Override
    public void pay(String creditCardNumber, YearMonth expire,
                    String securityCode, float totalAmount) {
        // always succeed
    }

}
