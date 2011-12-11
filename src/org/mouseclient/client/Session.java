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

import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.Vector;

public class Session {
   
   protected String host;
   
   protected int port;
   
   protected char[] password;
   
   protected Socket socket;
   
   protected UUID id;
   
   protected boolean connected = false;
   
   protected String message;
   
   protected String serverPlatform;
   
   protected String serverMacAddress;
   
   protected final static String clientMajorVersion = "2";
   
   protected final static String clientMinorVersion = "1";
   
   protected final static String clientName = "iPhone";
   
   public Session(String host, int port, char[] password) throws Exception {
      this.host = host;
      this.port = port;
      this.password = password;
      
      this.id = UUID.randomUUID();
      
      this.socket = new Socket(host, port);
      
      connect();
      
      Thread reader = new Thread() {
         public void run() {
            try {
               while(true) {
                  final Vector<String> command = getCommand();
                  processCommand(command);
               }
            } catch (IOException e) {
               System.err.println(e.getMessage());
            }
         }
      };
      
      reader.start();
   }
   
   public String getHost() {
      return host;
   }
   
   public String getMessage() {
      return message;
   }
   
   public String getServerPlatform() {
      return serverPlatform;
   }
   
   public String getServerMacAddress() {
      return serverMacAddress;
   }
   
   public Vector<String> getCommand() throws IOException {
      Vector<String> parsed = new Vector<String>();
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      int val;
      
      InputStream is = socket.getInputStream();

      while((val = is.read()) != -1) {
         if (val == 0x1e || val == 0x04) {
            parsed.add(os.toString("UTF-8"));
            os.reset();
            
            if (val == 0x04) break;
         } else {
            os.write(val);
         }
      }
      return parsed;
   }
   
   protected void processCommand(Vector<String> input) {
      String command = input.get(0);
      System.err.println(command);
   }
   
   public void connect() throws IOException {      
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      os.write("CONNECT".getBytes());
      os.write(0x1e);
      for (int i=0; i<password.length; i++) {
         os.write(password[i]);
      }
      os.write(0x1e);
      os.write(id.toString().getBytes());
      os.write(0x1e);
      os.write(clientName.getBytes());
      os.write(0x1e);
      os.write(clientMajorVersion.getBytes());
      os.write(0x1e);
      os.write(clientMinorVersion.getBytes());
      os.write(0x04);
      
      os.writeTo(socket.getOutputStream());   
      
      Vector<String> result = getCommand();
      if (result.get(0).equals("CONNECTED")) {

         if (result.get(1).equals("YES")) {
            connected = true;            
            serverPlatform = result.get(2);
            host = result.get(3);
            message = result.get(4);
            serverMacAddress = result.get(5); 
            
            System.err.println(message);
         } else {
            throw new IOException(result.get(4));
         }
      } else {
         throw new IOException("Unexpected reply - " + result.get(0));
      } 
   }
   
   public void key(KeyEvent e) throws IOException {
      boolean add = false;
      String keycode, utf8;
      
      switch(e.getKeyCode()) {
      case KeyEvent.VK_SHIFT:
      case KeyEvent.VK_CONTROL:
      case KeyEvent.VK_ALT:
      case KeyEvent.VK_META:
         return;
      
      case KeyEvent.VK_TAB:
         keycode = "-1"; utf8 = "TAB";
         break;
      case KeyEvent.VK_ESCAPE:
         keycode = "-1"; utf8 = "ESCAPE";
         break;
      case KeyEvent.VK_DELETE:
         keycode = "-1"; utf8 = "DELETE";
         break;
         
      case KeyEvent.VK_HOME:
         keycode = "-1"; utf8 = "HOME";
         break;
      case KeyEvent.VK_END:
         keycode = "-1"; utf8 = "END";
         break;
      case KeyEvent.VK_PAGE_UP:
         keycode = "-1"; utf8 = "PGUP";
         break;
      case KeyEvent.VK_PAGE_DOWN:
         keycode = "-1"; utf8 = "PGDN";
         break;
         
      case KeyEvent.VK_UP:
         keycode = "-1"; utf8 = "UP";
         break;
      case KeyEvent.VK_DOWN:
         keycode = "-1"; utf8 = "DOWN";
         break;
      case KeyEvent.VK_LEFT:
         keycode = "-1"; utf8 = "LEFT";
         break;
      case KeyEvent.VK_RIGHT:
         keycode = "-1"; utf8 = "RIGHT";
         break;
         
      case KeyEvent.VK_F1:
         keycode = "-1"; utf8 = "F1";
         break;
      case KeyEvent.VK_F2:
         keycode = "-1"; utf8 = "F2";
         break;
      case KeyEvent.VK_F3:
         keycode = "-1"; utf8 = "F3";
         break;
      case KeyEvent.VK_F4:
         keycode = "-1"; utf8 = "F4";
         break;
      case KeyEvent.VK_F5:
         keycode = "-1"; utf8 = "F5";
         break;
      case KeyEvent.VK_F6:
         keycode = "-1"; utf8 = "F6";
         break;
      case KeyEvent.VK_F7:
         keycode = "-1"; utf8 = "F7";
         break;
      case KeyEvent.VK_F8:
         keycode = "-1"; utf8 = "F8";
         break;
      case KeyEvent.VK_F9:
         keycode = "-1"; utf8 = "F9";
         break;
      case KeyEvent.VK_F10:
         keycode = "-1"; utf8 = "F10";
         break;
      case KeyEvent.VK_F11:
         keycode = "-1"; utf8 = "F11";
         break;
      case KeyEvent.VK_F12:
         keycode = "-1"; utf8 = "F12";
         break;

      default:
         keycode = Integer.toString(e.getKeyCode());
         utf8 = Character.toString(e.getKeyChar());
      }
      
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      os.write("KEY".getBytes());
      os.write(0x1e);
      os.write(keycode.getBytes()); // keycode
      os.write(0x1e);
      os.write(utf8.getBytes()); //utf8
      os.write(0x1e);
      if (e.isControlDown()) {
         if (add) os.write("+".getBytes());
         os.write("CTRL".getBytes());
         add = true;
      }
      if (e.isMetaDown()) {
         if (add) os.write("+".getBytes());
         os.write("OPT".getBytes());
         add = true;
      }
      if (e.isAltDown()) {
         if (add) os.write("+".getBytes());
         os.write("ALT".getBytes());
         add = true;
      }
      if (e.isShiftDown()) {
         if (add) os.write("+".getBytes());
         os.write("SHIFT".getBytes());
         add = true;
      }
      os.write(0x04);
      
      os.writeTo(socket.getOutputStream());   
   }
   
   public void mouseButton(int button, boolean pressed) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      os.write("CLICK".getBytes());
      os.write(0x1e);
      if (button == 1) {
         os.write("L".getBytes());
      } else if (button == 2) {
         os.write("M".getBytes());
      } else if (button == 3) {
         os.write("R".getBytes());
      }
      os.write(0x1e);
      if (pressed) {
         os.write("D".getBytes());
      } else {
         os.write("U".getBytes());
      }
      os.write(0x1e);
      os.write(0x04);
      
      os.writeTo(socket.getOutputStream());
   }
   
   public void mouseMoved(int dx, int dy) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      os.write("MOVE".getBytes());
      os.write(0x1e);
      os.write(Integer.toString(dx).getBytes()); // x
      os.write(0x1e);
      os.write(Integer.toString(dy).getBytes()); // y
      os.write(0x1e);
      os.write("0".getBytes()); // ?
      os.write(0x04);
      
      os.writeTo(socket.getOutputStream());
   }
   
   public void scroll(int dx, int dy) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      os.write("SCROLL".getBytes());
      os.write(0x1e);
      os.write(Integer.toString(dx).getBytes()); // x
      os.write(0x1e);
      os.write(Integer.toString(dy).getBytes()); // y
      os.write(0x1e);
      os.write(0x04);
      
      os.writeTo(socket.getOutputStream());
   }
   
   public void zoom(int dz) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      
      os.write("ZOOM".getBytes());
      os.write(0x1e);
      os.write(Integer.toString(dz).getBytes()); // zoom
      os.write(0x04);
      
      os.writeTo(socket.getOutputStream());
   }
}