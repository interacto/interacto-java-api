/*
 * Interacto
 * Copyright (C) 2020 Arnaud Blouin
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
package io.github.interacto.interaction;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TestThreadService {
	ThreadService mementoThreadService;

	@BeforeEach
	void setUp() {
		mementoThreadService = ThreadService.getInstance();
	}

	@AfterEach
	void tearDown() {
		ThreadService.setInstance(mementoThreadService);
	}

	@Test
	void testGetSet() {
		final ThreadService mockService = Mockito.mock(ThreadService.class);
		ThreadService.setInstance(mockService);
		assertSame(mockService, ThreadService.getInstance());
	}

	@Test
	void testCurrentThread() {
		assertSame(Thread.currentThread(), ThreadService.getInstance().currentThread());
	}

	@Nested
	class SleepTest {
		ExecutorService executor;
		CountDownLatch counter;

		@BeforeEach
		void setUp() {
			executor = Executors.newWorkStealingPool();
			counter = new CountDownLatch(1);
		}

		@Test
		void testSleep() throws InterruptedException {
			executor.submit(() -> {
				try {
					ThreadService.getInstance().sleep(2000);
					counter.countDown();
				}catch(final InterruptedException ignore) {
				}
			});

			Thread.sleep(500);
			assertEquals(1, counter.getCount());
		}

		@AfterEach
		void tearDown() throws InterruptedException {
			executor.shutdownNow();
			executor.awaitTermination(2, TimeUnit.SECONDS);
		}
	}
}
