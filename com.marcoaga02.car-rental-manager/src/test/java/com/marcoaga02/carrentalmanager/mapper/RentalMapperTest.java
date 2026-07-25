package com.marcoaga02.carrentalmanager.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import org.junit.Before;
import org.junit.Test;

import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.model.Customer;
import com.marcoaga02.carrentalmanager.model.Rental;
import com.marcoaga02.carrentalmanager.viewmodel.RentalViewModel;

public class RentalMapperTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);

	private static final String A_TAX_ID_CODE = "aTaxIdCode";
	private static final String A_FIRSTNAME = "aFirstname";
	private static final String A_LASTNAME = "aLastname";

	private static final LocalDate A_START_DATE = LocalDate.of(2026, Month.JULY, 25);
	private static final Integer A_NUMBER_OF_DAYS = 6;

	private static final LocalDate EXPECTED_END_DATE = LocalDate.of(2026, Month.JULY, 31);
	private static final BigDecimal EXPECTED_TOTAL_AMOUNT = new BigDecimal("61.2"); // 10.2 * 6
	private static final String EXPECTED_CUSTOMER_FULLNAME = "aFirstname aLastname";
	private static final String EXPECTED_CAR_DESCRIPTION = "aBrand aModel [aCarPlate]";

	private RentalMapper rentalMapper;

	@Before
	public void setUp() {
		rentalMapper = new RentalMapper();
	}

	@Test
	public void testToViewModelWhenInputIsNullReturnNull() {
		assertThat(rentalMapper.toViewModel(null)).isNull();
	}

	@Test
	public void testToViewModelWhenInputIsValidReturnTheCorrectMapping() {
		Car car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);
		Customer customer = new Customer(A_TAX_ID_CODE, A_FIRSTNAME, A_LASTNAME);
		Rental rental = new Rental(car, customer, A_START_DATE, A_NUMBER_OF_DAYS);

		RentalViewModel result = rentalMapper.toViewModel(rental);

		assertThat(result.getId()).isNull();
		assertThat(result.getStartDate()).isEqualTo(A_START_DATE);
		assertThat(result.getEndDate()).isEqualTo(EXPECTED_END_DATE);
		assertThat(result.getRentalDays()).isEqualTo(A_NUMBER_OF_DAYS);
		assertThat(result.getCustomerFullname()).isEqualTo(EXPECTED_CUSTOMER_FULLNAME);
		assertThat(result.getCarDescription()).isEqualTo(EXPECTED_CAR_DESCRIPTION);
		assertThat(result.getTotalAmount()).isEqualByComparingTo(EXPECTED_TOTAL_AMOUNT);
	}

}
