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

public class Version {

	public static String APPLICATION_NAME = "MouseClient";
	
	private static String version = null;
	
	public static String getVersion(){
		if (version == null) {
			String implementation = Version.class.getPackage().getImplementationVersion();
			if (implementation == null) {
				version = "DEVELOPMENT BUILD";
			} else {
				version = "Revision " + implementation;
			}
		}
		
		return version;
	}
	
	public static String getLongApplicationName(){
		return APPLICATION_NAME + " " + getVersion();
	}
	
	public static String getUserAgentId(){
		return APPLICATION_NAME + "/" + getVersion();
	}
}
