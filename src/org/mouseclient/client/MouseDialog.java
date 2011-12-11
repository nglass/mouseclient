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

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JDialog;

public class MouseDialog extends JDialog implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

   private static final long serialVersionUID = 1709636827900249038L;
   
   protected String title;
   
   protected String releaseString;
   
   protected Session session;
   
   protected Robot robot;
   
   protected boolean grabbedMouse = false;
   
   protected Cursor blankCursor;
   
   public MouseDialog(Session session, Frame rootContainer) throws AWTException {
      super(rootContainer, false);
      
      this.session = session;
      
      this.robot = new Robot();
      
      this.title = new String(Version.APPLICATION_NAME + " - " + session.getHost());
      
      this.releaseString = new String("Press \"Ctrl + ~\" to release mouse");
      
      setTitle(this.title);      
      setSize(350, 200);
      validate();
      
      addKeyListener(this);
      addMouseListener(this);
      addMouseMotionListener(this);
      addMouseWheelListener(this);
      
      setFocusTraversalKeysEnabled(false);
      
      CloseOnLastWindow.registerWindow();

      // Transparent 16 x 16 pixel cursor image.
      BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

      // Create a new blank cursor.
      this.blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
          cursorImg, new Point(0, 0), "blank cursor");
      
      //Set Up Window Exit
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            close();
         }
      });
      
      center();
   }
   
   private void close() {
      this.setVisible(false);
      CloseOnLastWindow.unregisterWindow();
      this.dispose();
   }
   
   private void center() {
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      Point center = ge.getCenterPoint();
      Rectangle bounds = ge.getMaximumWindowBounds();
      int w = Math.min(getWidth(), bounds.width);
      int h = Math.min(getHeight(), bounds.height);
      int x = center.x - w / 2, y = center.y - h / 2;
      setBounds(x, y, w, h);
      validate();
   }

   public void centerMouse () {
      //recenter mouse
      robot.mouseMove(getX() + getWidth()/2, getY() + getHeight()/2);
   }
   
   @Override
   public void keyTyped(KeyEvent e) {}

   @Override
   public void keyPressed(KeyEvent e) {
      if (grabbedMouse) {
         if (e.getKeyCode() == KeyEvent.VK_BACK_QUOTE && e.isControlDown()) {
            grabbedMouse = false;
            setCursor(Cursor.getDefaultCursor());
            setTitle(this.title);
         } else {
            try {
               session.key(e);
            } catch (IOException e1) {
               e1.printStackTrace();
            }
         }
      }
   }

   @Override
   public void keyReleased(KeyEvent e) {}

   @Override
   public void mouseClicked(MouseEvent e) {}

   public void mouseButton(MouseEvent e, boolean pressed) {
      if (grabbedMouse) {
         int button = 0;
         
         switch (e.getButton()) {
         case MouseEvent.BUTTON1:
            button = 1;
            break;
         case MouseEvent.BUTTON2:
            button = 2;
            break;
         case MouseEvent.BUTTON3:
            button = 3;
            break;
         default:
            break;
         }
         if (button != 0) {
            try {
               session.mouseButton(button, pressed);
            } catch (IOException e1) {
               e1.printStackTrace();
            }
         }
      }
   }
   
   @Override
   public void mousePressed(MouseEvent e) {
      if (grabbedMouse) {
         mouseButton(e, true);
      } else {
         grabbedMouse = true;
         centerMouse();
         setCursor(blankCursor);
         setTitle(this.releaseString);
      }
   }

   @Override
   public void mouseReleased(MouseEvent e) {
      mouseButton(e, false);
   }

   @Override
   public void mouseEntered(MouseEvent e) {}

   @Override
   public void mouseExited(MouseEvent e) {}

   
   public void mouseDraggedOrMoved(MouseEvent e) {
      if (grabbedMouse) {
         int dx, dy;
         
         int centerx = getWidth() / 2;
         int centery = getHeight() / 2;
         
         dx = e.getX() - centerx;
         dy = e.getY() - centery;
         
         if (dx != 0 || dy != 0) {
            try {
               session.mouseMoved(dx, dy);
            } catch (IOException e1) {
               e1.printStackTrace();
            }
            //recenter mouse
            centerMouse();
         }
      }
   }
   
   @Override
   public void mouseDragged(MouseEvent e) {
      mouseDraggedOrMoved(e);
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      mouseDraggedOrMoved(e);
   }

   @Override
   public void mouseWheelMoved(MouseWheelEvent e) {
      if (grabbedMouse) {
         int dy = e.getWheelRotation();
         
         try {
            if (e.isControlDown()) {
               session.zoom(dy);
            } else {
               session.scroll(0,  dy);
            }
         } catch (IOException e1) {
            e1.printStackTrace();
         }
      } 
   }
}
