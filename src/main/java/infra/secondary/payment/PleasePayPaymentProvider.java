package infra.secondary.payment;

import java.time.YearMonth;

import hexagon.secondary.port.ForManagingCreditCardPayments;

public class PleasePayPaymentProvider implements ForManagingCreditCardPayments {

	@Override
	public void pay(String creditCardNumber, YearMonth expire,
			String securityCode, float totalAmount) {
		// always succeed
	}

}
