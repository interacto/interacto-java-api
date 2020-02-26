/*
 * Interacto
 * Copyright (C) 2019 Arnaud Blouin
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.interacto.command.library;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestModifyValue {
	static class ModifyValueImpl extends ModifyValue<String> {
		boolean mustMatch = false;
		final AtomicInteger cptApply = new AtomicInteger();

		@Override
		protected void applyValue() {
			cptApply.incrementAndGet();
		}

		@Override
		protected boolean isValueMatchesProperty() {
			return mustMatch;
		}
	}

	ModifyValueImpl cmd;

	@BeforeEach
	void setUp() {
		cmd = new ModifyValueImpl();
	}

	@Test
	void testCannotDo() {
		assertFalse(cmd.canDo());
	}

	@Test
	void testCannotDoMatch() {
		cmd.setValue("foo");
		assertFalse(cmd.canDo());
	}

	@Test
	void testSetValue() {
		cmd.setValue("bar");
		assertEquals("bar", cmd.value);
	}

	@Test
	void testFlush() {
		cmd.setValue("yo");
		cmd.flush();
		assertNull(cmd.value);
	}

	@Test
	void testCanDo() {
		cmd.mustMatch = true;
		cmd.setValue("foo");
		assertTrue(cmd.canDo());
	}

	@Test
	void testDo() {
		cmd.mustMatch = true;
		cmd.setValue("foo");
		cmd.doIt();
		assertEquals(1, cmd.cptApply.get());
	}

	@Test
	void testHadEffects() {
		cmd.mustMatch = true;
		cmd.setValue("bar");
		cmd.doIt();
		cmd.done();
		assertTrue(cmd.hadEffect());
	}
}
