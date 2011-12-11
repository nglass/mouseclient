/*
 * This file is part of MouseClient.
 *
 * MouseClient is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * any later version.
 *
 * MouseClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TunesRemote SE; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package org.mouseclient.client;

import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import android.util.Log;

public class CloseOnLastWindow {
   public final static String TAG = CloseOnLastWindow.class.toString();
   
   // count open windows. exit when last is closed
	private static AtomicInteger OPEN_WINDOWS = new AtomicInteger(0);

	public static void registerWindow() {
		OPEN_WINDOWS.incrementAndGet();	
	}

	public static void unregisterWindow() {
		int windows = OPEN_WINDOWS.decrementAndGet();

		if (windows <= 0) {
			Log.i(TAG, "Last window closed exiting...");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					System.exit(0);
				}
			});
		}	
	}
}
