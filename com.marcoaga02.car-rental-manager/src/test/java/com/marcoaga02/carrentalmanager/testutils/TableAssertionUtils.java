package com.marcoaga02.carrentalmanager.testutils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TableAssertionUtils {

	private TableAssertionUtils() {
	}

	public static List<List<String>> rowsOf(String[][] tableContents) {
		return Arrays.stream(tableContents).map(Arrays::asList).collect(Collectors.toList());
	}

}
