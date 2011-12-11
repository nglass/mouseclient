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

/**
 * 
 */

public class ServerDetails {
   private String serviceName;
   private String address;
   private int port;

   public ServerDetails() {
      this.serviceName = new String();
      this.address = new String();
      this.port = 0;
   }
   
   public ServerDetails(String serviceName, String address, int port) {
      this.serviceName = serviceName;
      this.address = address;
      this.port = port;
   }

   public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
   }
   
   public String getServiceName() {
      return serviceName;
   }

   public String getAddress() {
      return address;
   }
   
   public void setAddress(String address) {
      this.address = address;
   }

   public int getPort() {
      return port;
   }
   
   public void setPort(int port) {
      this.port = port;
   }

   // this is how the object will appear in the list
   @Override
   public String toString() {
      return serviceName + "@" + address;
   }

   @Override
   public boolean equals(Object aThat) {
      if (this == aThat)
         return true;
      if (!(aThat instanceof ServerDetails))
         return false;
      ServerDetails that = (ServerDetails) aThat;
      return this.serviceName.equals(that.serviceName) && 
             this.address.equals(that.address);
   }
}