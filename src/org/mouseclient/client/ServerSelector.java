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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ServerSelector extends JDialog implements ListDataListener, ListSelectionListener {

   private static final long serialVersionUID = 6966040866384041847L;

   private JList addressList;

	protected int width;

	protected int height;
	
	protected Frame rootContainer;
	
	protected DefaultListModel model;
	
	protected JTextField addressField, portField;
	
	protected JPasswordField passwordField;
	
	protected JLabel addressLabel, portLabel, passwordLabel;
	
	private void close() {
		this.setVisible(false);
		CloseOnLastWindow.unregisterWindow();
		this.dispose();
	}
	
	public ServerSelector(final SessionCallback callback, final Frame rootContainer) {
		super(rootContainer, true);
		setTitle("Select Mouse Server");
		final JDialog frame = this;
		
		this.rootContainer = rootContainer;
		// -- size
		this.width = 300;
		this.height = 300;
		
		this.addressLabel = new JLabel("Address:");
		this.portLabel = new JLabel("Port:");
		this.passwordLabel = new JLabel("Password:");
		
		this.addressField = new JTextField();
		this.portField = new JTextField();
		this.passwordField = new JPasswordField();
		
		model = MouseService.getServiceList();
		model.addListDataListener(this);
		
		addressList = new JList(model);	   
		addressList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addressList.setSelectedIndex(-1);
		addressList.setVisibleRowCount(5);
		addressList.addListSelectionListener(this);
		JScrollPane listScrollPane = new JScrollPane(addressList);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));

		JButton okButton = new JButton("OK");
		frame.getRootPane().setDefaultButton(okButton);
		buttonPane.add(okButton);		

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (addressList.getSelectedValue() instanceof ServerDetails) {

				   ServerDetails l = (ServerDetails) addressList.getSelectedValue();
					
					// Log into session
					Session session = null;
					try {
						session = new Session(addressField.getText(), 
						                      Integer.parseInt(portField.getText()),
						                      passwordField.getPassword());
						
						System.err.println("Successfully connected!");
					} catch (Exception exception) {
						JOptionPane.showMessageDialog
							(frame, "Error: Could not start session\n" + exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						session = null;
					}
					
					// start gui
					if (session != null) {
					   frame.setVisible(false);
					   
						try {
                     callback.newSession(l, session);
                     close();
                  } catch (Exception e1) {
                     // TODO Auto-generated catch block
                     e1.printStackTrace();
                     frame.setVisible(true);
                  }
					}
				}
			}
		});


		JButton cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		//Set Up Window Exit
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		GridBagConstraints scrollGBC = new GridBagConstraints(0, 0, 2, 1, 100, 100, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);

		GridBagConstraints addresslGBC = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);
		
		GridBagConstraints addressfGBC = new GridBagConstraints(1, 1, 1, 1, 100, 0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);
		
		GridBagConstraints portlGBC = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);
      
      GridBagConstraints portfGBC = new GridBagConstraints(1, 2, 1, 1, 100, 0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);
		
      GridBagConstraints passwordlGBC = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);
      
      GridBagConstraints passwordfGBC = new GridBagConstraints(1, 3, 1, 1, 100, 0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 1, 1);
      
		GridBagConstraints buttonsGBC = new GridBagConstraints(0, 4, 2, 1, 100, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 1, 1);
		
		frame.getContentPane().setLayout(new GridBagLayout());
		frame.getContentPane().add(listScrollPane,scrollGBC);	
		frame.getContentPane().add(addressLabel, addresslGBC);
		frame.getContentPane().add(addressField, addressfGBC);
		frame.getContentPane().add(portLabel, portlGBC);
      frame.getContentPane().add(portField, portfGBC);
      frame.getContentPane().add(passwordLabel, passwordlGBC);
      frame.getContentPane().add(passwordField, passwordfGBC);
		frame.getContentPane().add(buttonPane,buttonsGBC);
		
		//Size, Title and setVisible				 
		frame.pack();
		CloseOnLastWindow.registerWindow();
		
		center();
	}
	
	// -- center the dialog
	protected void center() {
		if (rootContainer != null) {
			Rectangle rcRect = rootContainer.getBounds();
			int x = rcRect.x + (rcRect.width / 2) - (width / 2), y = rcRect.y + (rcRect.height / 2) - (height / 2);
			setBounds(x, y, width, height);
		} else {
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((screen.width - width) / 2, (screen.height - height) / 2, width, height);
		}
		validate();
	}

   @Override
   public void valueChanged(ListSelectionEvent e) {
      ServerDetails serverDetails = (ServerDetails) addressList.getSelectedValue();
      addressField.setText(serverDetails.getAddress());
      portField.setText(Integer.toString(serverDetails.getPort()));
      passwordField.setText("");
   }

   @Override
   public void intervalAdded(ListDataEvent e) {}

   @Override
   public void intervalRemoved(ListDataEvent e) {}

   public void initSelection() {
      // If user has not modified selection then
      // Automatically highlight last Session if found
      if (addressList.getSelectedIndex() == -1 && addressField.getText().length() == 0) {
         addressList.setSelectedIndex(0);
      }
   }
   
   @Override
   public void contentsChanged(ListDataEvent e) {
      initSelection(); 
   }
}
