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

import java.io.Closeable;
import java.io.IOException;

import javax.jmdns.JmDNS;
import javax.jmdns.JmmDNS;
import javax.jmdns.NetworkTopologyEvent;
import javax.jmdns.NetworkTopologyListener;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

import android.util.Log;

public class MouseService extends Thread implements ServiceListener, NetworkTopologyListener, Closeable {

   public final static String TAG = MouseService.class.toString();

   public final static String MOBILEREMOTE_TYPE = "_mobileremote._tcp.local.";

   private JmmDNS jmmdns = null;

   private final DefaultListModel serviceList = new DefaultListModel();

   private static MouseService instance = null;
   
   private MouseService() {}

   @Override
   public void run() {
      jmmdns = JmmDNS.Factory.getInstance();
      jmmdns.addNetworkTopologyListener(this);
   }

   @Override
   public void close() {
      try {
         if (jmmdns != null) {
            jmmdns.close();
         }
      } catch (IOException e) {
         Log.e(TAG, "Exception shutting down MouseService", e);
      }
   }

   public void updateService(String serviceName, ServiceInfo serviceInfo) {
      final String address = serviceInfo.getHostAddresses()[0];
      final int port = serviceInfo.getPort();

      ServerDetails ent = new ServerDetails(serviceName, address, port);

      if (serviceList.contains(ent)) {
         serviceList.setElementAt(ent, serviceList.indexOf(ent));
      } else {
         serviceList.addElement(ent);
      }
   }

   @Override
   public void serviceAdded(ServiceEvent event) {
      Log.i(TAG, "serviceAdded(event=" + event.toString() + ")");

      // Force resolution of new service
      final String serviceName = event.getName();
      final ServiceInfo info = event.getDNS().getServiceInfo(event.getType(), event.getName());

      if (info != null) {
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               updateService(serviceName, info);
            }
         });
      }
   }

   @Override
   public void serviceRemoved(ServiceEvent event) {
      Log.i(TAG, "serviceRemoved(event=" + event.toString() + ")");
      
      // remove entry
      final String serviceName = event.getName();
      final ServerDetails ent = new ServerDetails(serviceName, null, 0);
      
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            serviceList.removeElement(ent);
         }
      });
   }

   @Override
   public void serviceResolved(ServiceEvent event) {
      Log.i(TAG, "serviceResolved(event=" + event.toString() + ")");

      final String serviceName = event.getName();
      final ServiceInfo serviceInfo = event.getInfo();

      updateService(serviceName, serviceInfo);
   }

   public static void startService() {
      if (instance == null) {
         instance = new MouseService();
         instance.start();

         Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
               if (instance != null) {
                  instance.close();
               }
               Log.i(TAG, "MouseService Stopped");
            }
         });
      }
   }

   public static DefaultListModel getServiceList() {
      if (instance != null) {
         return instance.serviceList;
      }
      return new DefaultListModel();
   }
   
   @Override
   public void inetAddressAdded(NetworkTopologyEvent event) {
      Log.i(TAG, "inetAddressAdded(event=" + event.toString() + ")");
      JmDNS mdns = event.getDNS();

      // Start listening for DACP servers on this interface
      mdns.addServiceListener(MOBILEREMOTE_TYPE, this);
   }

   @Override
   public void inetAddressRemoved(NetworkTopologyEvent event) {
      Log.i(TAG, "inetAddressRemoved(event=" + event.toString() + ")");
   }
}
