/*
 * This file is part of Fim - File Integrity Manager
 *
 * Copyright (C) 2015  Etienne Vrignaud
 *
 * Fim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Fim.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fim.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class DuplicateSetTest
{
	private DuplicateSet a1;
	private DuplicateSet a2;
	private DuplicateSet b;

	@Before
	public void setup()
	{
		a1 = new DuplicateSet(Arrays.asList(
				new FileState("file_1", 1L, new FileTime(1L), new FileHash("1", "11", "111"), null),
				new FileState("file_2", 2L, new FileTime(2L), new FileHash("2", "22", "222"), null)
		));

		a2 = new DuplicateSet(Arrays.asList(
				new FileState("file_1", 1L, new FileTime(1L), new FileHash("1", "11", "111"), null),
				new FileState("file_2", 2L, new FileTime(2L), new FileHash("2", "22", "222"), null)
		));

		b = new DuplicateSet(Arrays.asList(
				new FileState("file_1", 1L, new FileTime(1L), new FileHash("1", "11", "111"), null),
				new FileState("file_3", 3L, new FileTime(3L), new FileHash("3", "33", "333"), null)
		));
	}

	@Test
	public void equalsIsWorking()
	{
		assertThat(a1).isNotEqualTo(null);

		assertThat(a1).isNotEqualTo("dummy_string");

		assertThat(a1).isEqualTo(a2);
		assertThat(a2).isEqualTo(a1);

		assertThat(a1).isNotEqualTo(b);
		assertThat(b).isNotEqualTo(a1);
	}

	@Test
	public void hashcodeIsWorking()
	{
		assertThat(a1.hashCode()).isEqualTo(a2.hashCode());

		assertThat(a1.hashCode()).isNotEqualTo(b.hashCode());
	}

	@Test
	public void toStringIsWorking()
	{
		assertThat(a1.toString().contains("duplicatedFiles")).isTrue();
	}
}
