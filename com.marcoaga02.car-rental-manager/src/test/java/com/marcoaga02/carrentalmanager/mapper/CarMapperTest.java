package com.marcoaga02.carrentalmanager.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.marcoaga02.carrentalmanager.model.Car;
import com.marcoaga02.carrentalmanager.viewmodel.CarViewModel;

public class CarMapperTest {

	private static final String A_CAR_PLATE = "aCarPlate";
	private static final String A_BRAND = "aBrand";
	private static final String A_MODEL = "aModel";
	private static final BigDecimal A_DAILY_RATE = BigDecimal.valueOf(10.2);
	private static final Long AN_ID = 10L;

	private CarMapper carMapper;

	@Before
	public void setUp() {
		carMapper = new CarMapper();
	}

	@Test
	public void testToViewModelWhenInputIsNullReturnNull() {
		assertThat(carMapper.toViewModel(null)).isNull();
	}

	@Test
	public void testToViewModelWhenInputIsValidReturnTheCorrectMapping() {
		Car car = new Car(A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

		CarViewModel result = carMapper.toViewModel(car);

		assertThat(result.getId()).isNull();
		assertThat(result.getCarPlate()).isEqualTo(A_CAR_PLATE);
		assertThat(result.getBrand()).isEqualTo(A_BRAND);
		assertThat(result.getModel()).isEqualTo(A_MODEL);
		assertThat(result.getDailyRate()).isEqualTo(A_DAILY_RATE);
	}

	@Test
	public void testToEntityWhenInputIsNullReturnNull() {
		assertThat(carMapper.toEntity(null)).isNull();
	}

	@Test
	public void testToEntityWhenInputIsValidReturnTheCorrectMapping() {
		CarViewModel carViewModel = new CarViewModel(AN_ID, A_CAR_PLATE, A_BRAND, A_MODEL, A_DAILY_RATE);

		Car result = carMapper.toEntity(carViewModel);

		assertThat(result.getId()).isNull();
		assertThat(result.getCarPlate()).isEqualTo(A_CAR_PLATE);
		assertThat(result.getBrand()).isEqualTo(A_BRAND);
		assertThat(result.getModel()).isEqualTo(A_MODEL);
		assertThat(result.getDailyRate()).isEqualTo(A_DAILY_RATE);
	}

}
