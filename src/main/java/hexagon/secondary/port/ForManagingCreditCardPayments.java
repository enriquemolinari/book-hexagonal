package hexagon.secondary.port;

import java.time.YearMonth;

public interface ForManagingCreditCardPayments {
	void pay(String creditCardNumber, YearMonth expire, String securityCode,
			float totalAmount);
}
