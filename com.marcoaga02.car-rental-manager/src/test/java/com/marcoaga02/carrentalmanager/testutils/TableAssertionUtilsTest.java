package com.marcoaga02.carrentalmanager.testutils;

import static com.marcoaga02.carrentalmanager.testutils.TableAssertionUtils.rowsOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class TableAssertionUtilsTest {

	@Test
	void testRowsOfWithNullReturnsEmptyList() {
		List<List<String>> rows = rowsOf(null);

		assertThat(rows).isEmpty();
	}

	@Test
	void testRowsOfWithEmptyTableReturnsEmptyList() {
		String[][] tableContents = {};

		List<List<String>> rows = rowsOf(tableContents);

		assertThat(rows).isEmpty();
	}

	@Test
	void testRowsOfWithOneRowConvertsRowPreservingOrderAndContent() {
		String[][] tableContents = { { "a", "b" } };

		List<List<String>> rows = rowsOf(tableContents);

		assertThat(rows).containsExactly(List.of("a", "b"));
	}

	@Test
	void testRowsOfWithMultipleRowsConvertsEachRowPreservingOrderAndContent() {
		String[][] tableContents = { { "a", "b" }, { "c", "d" } };

		List<List<String>> rows = rowsOf(tableContents);

		assertThat(rows).containsExactly(List.of("a", "b"), List.of("c", "d"));
	}

}
