package com.marcoaga02.carrentalmanager.testutils;

import static com.marcoaga02.carrentalmanager.testutils.TableAssertionUtils.rowsOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class TableAssertionUtilsTest {

	@Test
	void testRowsOfConvertsEachRowPreservingOrderAndContent() {
		String[][] tableContents = { { "a", "b" }, { "c", "d" } };

		List<List<String>> rows = rowsOf(tableContents);

		assertThat(rows).containsExactly(List.of("a", "b"), List.of("c", "d"));
	}

	@Test
	void testRowsOfWithEmptyTableReturnsEmptyList() {
		String[][] tableContents = {};

		List<List<String>> rows = rowsOf(tableContents);

		assertThat(rows).isEmpty();
	}

}
