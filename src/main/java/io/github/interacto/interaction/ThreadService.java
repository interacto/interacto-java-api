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

/**
 * Replace the use of static Thread's methods by a service that one can mock easily.
 */
public class ThreadService {
	private static ThreadService instance = new ThreadService();

	public static ThreadService getInstance() {
		return instance;
	}

	public static void setInstance(final ThreadService instance) {
		ThreadService.instance = instance;
	}

	public void sleep(final long millis) throws InterruptedException {
		Thread.sleep(millis);
	}

	public Thread currentThread() {
		return Thread.currentThread();
	}
}
