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
 * along with MouseClient; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright 2011 Nick Glass
 */
package org.mouseclient.client;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.lang.reflect.*;
import java.awt.Image;
import java.awt.Toolkit;

import android.util.Log;
import apple.dts.samplecode.osxadapter.OSXAdapter;

public class MouseClient implements SessionCallback {
	
   public final static String TAG = MouseClient.class.toString();
	
   public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));
    
   private static Image icon;
    
	public static void main (String [] args) throws ClassNotFoundException {
		Log.version(TAG, "Launching " + Version.getLongApplicationName() + " ...");

		icon = Toolkit.getDefaultToolkit().getImage
		   (MouseClient.class.getResource("/org/mouseclient/resources/images/app.png"));
		
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception ex) {
         Log.e(TAG, "Look and Feel Error : " + ex.getMessage());
      }
      
      if (MAC_OS_X) {
         // ensure property is set to place menus at top of screen
         System.setProperty("com.apple.mrj.application.apple.menu.about.name", Version.APPLICATION_NAME);
         System.setProperty("apple.laf.useScreenMenuBar", "true");
         
         // try setting dock icon on mac
         try {
            Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
            Object macOSXApplication = applicationClass.getConstructor((Class[])null).newInstance((Object[])null);
            Method setDockIconImage = applicationClass.getDeclaredMethod("setDockIconImage", java.awt.Image.class);
            setDockIconImage.invoke(macOSXApplication, icon);
         } catch (ClassNotFoundException cnfe) {
            Log.e(TAG, "This version of Mac OS X does not support the Apple EAWT. (" + cnfe + ")");
         } catch (Exception ex) {  // Likely a NoSuchMethodException or an IllegalAccessException loading/invoking eawt.Application methods
            Log.e(TAG, "Mac OS X Adapter could not talk to EAWT:", ex);
         }
      }

		for (int i = 0; i < args.length - 1; i+=2) {
		   String arg   = args[i];
		   String value = args[i+1];
		   
		   if (arg.equals("-loglevel")) {
		      try {
		         int level = Integer.parseInt(value);
		         Log.i(TAG, "Setting -loglevel to " + value);
		         android.util.Log.setLogLevel(level); 
		      } catch (NumberFormatException e) {
		         Log.e(TAG, "Illegal value for -loglevel : " + value);
		      }
		   }
		}
		
		System.setProperty("http.agent", MouseClient.class.getSimpleName());
		
		new MouseClient();
	}
    
	private MouseClient() {
		
		if (MAC_OS_X) {			
            try {
                // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
                // use as delegates for various com.apple.eawt.ApplicationListener methods
                OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[])null));
                //OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
                //OSXAdapter.setPreferencesHandler(this, getClass().getDeclaredMethod("preferences", (Class[])null));
            } catch (Exception e) {
                Log.e(TAG, "Error while loading the OSXAdapter:", e);
            }		
		}
		
		final MouseClient mc = this;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MouseService.startService();
				
				ServerSelector serverSelector = new ServerSelector(mc, null);
				serverSelector.setIconImage(icon);
				serverSelector.setVisible(true);
			}
		});
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Mac Callbacks
	//
	public boolean quit() {

	   System.exit(0);
	   return true;
	}

	// TODO
	public boolean about() {
	   return true;
	}

	// TODO
	public boolean preferences() {
	   return false;
	}

   @Override
   public void newSession(ServerDetails l, Session s) throws Exception {
      MouseDialog mouseDialog = new MouseDialog(s, null);
      mouseDialog.setIconImage(icon);
      mouseDialog.setVisible(true);
   }

}
