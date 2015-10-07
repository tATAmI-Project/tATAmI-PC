/*******************************************************************************
 * Copyright (C) 2013 Andrei Olaru, Marius-Tudor Benea, Nguyen Thi Thuy Nga, Amal El Fallah Seghrouchni, Cedric Herpson.
 * 
 * This file is part of tATAmI-PC.
 * 
 * tATAmI-PC is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * tATAmI-PC is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with tATAmI-PC.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package testing.tudor.utils;

import jade.core.AID;
import jade.core.ContainerID;
import jade.lang.acl.ACLMessage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.mobile.MobileAgent;

public class AgentView extends JFrame implements ActionListener {

	private static final long serialVersionUID = -1015067807163184520L;

	private MobileAgent myAgent;

	private JTextField containerName;
	private JTextField agentName;
	private JTextField messageTxt;

	private JButton moveBt;
	private JButton cloneBt;
	private JButton killBt;

	public JButton proposeBt;
	public JButton acceptBt;
	public JButton rejectBt;

	private TextArea screen;

	public AgentView(MobileAgent agent) {
		myAgent = agent;
		setTitle(myAgent.getLocalName());

		setLocation(160, 70);
		setSize(1024, 768);
		setBackground(Color.lightGray);
		setLayout(new BorderLayout());

		fillWindow();

		setVisible(true);

		addListeners();
	}

	public void addListeners() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent we) {
				if (myAgent != null)
					myAgent.doDelete();
			}
		});
	}

	public void fillWindow() {
		moveBt = new JButton("Move");
		moveBt.addActionListener(this);

		cloneBt = new JButton("Clone");
		cloneBt.addActionListener(this);

		killBt = new JButton("Kill");
		killBt.addActionListener(this);

		// The labels:
		JLabel lbl1 = new JLabel("Container name for move/clone:  ");
		JLabel lbl2 = new JLabel("Agent name for messages/clone:  ");
		JLabel lbl3 = new JLabel("The content of the messages:   ");

		JPanel lblsP = new JPanel(new BorderLayout());
		lblsP.add(lbl1, BorderLayout.NORTH);
		lblsP.add(lbl3, BorderLayout.CENTER);
		lblsP.add(lbl2, BorderLayout.SOUTH);

		// The text fields:
		containerName = new JTextField();
		agentName = new JTextField("agent1");
		messageTxt = new JTextField();

		JPanel txtsP = new JPanel(new BorderLayout());
		txtsP.add(containerName, BorderLayout.NORTH);
		txtsP.add(messageTxt, BorderLayout.CENTER);
		txtsP.add(agentName, BorderLayout.SOUTH);

		// The upper left part (containing the text panels):
		JPanel txtP = new JPanel(new BorderLayout());
		txtP.add(lblsP, BorderLayout.WEST);
		txtP.add(txtsP, BorderLayout.CENTER);
		txtP.add(new JLabel("  "), BorderLayout.EAST);

		// Move and clone buttons:
		JPanel mcBP = new JPanel(new BorderLayout());
		mcBP.add(moveBt, BorderLayout.NORTH);
		mcBP.add(cloneBt, BorderLayout.SOUTH);

		// Move, clone and kill buttons:
		JPanel mckBP = new JPanel(new BorderLayout());
		mckBP.add(mcBP, BorderLayout.NORTH);
		mckBP.add(killBt, BorderLayout.SOUTH);

		// Upper part:
		JPanel uppP = new JPanel(new BorderLayout());
		uppP.add(txtP, BorderLayout.CENTER);
		uppP.add(mckBP, BorderLayout.EAST);

		proposeBt = new JButton("Propose");
		proposeBt.addActionListener(this);

		acceptBt = new JButton("Accept");
		acceptBt.addActionListener(this);

		rejectBt = new JButton("Reject");
		rejectBt.addActionListener(this);

		// Panel containing the buttons in the left side of the window:
		JPanel ssP = new JPanel(new BorderLayout());
		ssP.add(proposeBt, BorderLayout.NORTH);
		ssP.add(acceptBt, BorderLayout.SOUTH);

		JPanel sssP = new JPanel(new BorderLayout());
		sssP.add(ssP, BorderLayout.NORTH);
		sssP.add(rejectBt, BorderLayout.SOUTH);

		JPanel sssP2 = new JPanel(new BorderLayout());
		sssP2.add(sssP, BorderLayout.NORTH);

		// The screen:
		screen = new TextArea();
		screen.setEditable(false);
		screen.setFocusable(true);
		screen.setBackground(Color.WHITE);

		// Central side of the window:
		JPanel cent = new JPanel(new BorderLayout());
		cent.add(sssP2, BorderLayout.WEST);
		cent.add(screen, BorderLayout.CENTER);

		add(BorderLayout.NORTH, uppP);
		add(BorderLayout.CENTER, cent);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(moveBt)) {
			myAgent
					.doMove(new ContainerID(containerName.getText().trim(),
							null));
		} else if (e.getSource().equals(cloneBt)) {
			myAgent.doClone(new ContainerID(containerName.getText().trim(),
					null), agentName.getText().trim());
		} else if (e.getSource().equals(killBt)) {
			setTitle("Ex " + getTitle() + " - who moved to a better place ...");

			myAgent.doDelete();

			moveBt.setEnabled(false);
			cloneBt.setEnabled(false);
			killBt.setEnabled(false);

			containerName.setEditable(false);
			messageTxt.setEditable(false);
			agentName.setEditable(false);

			proposeBt.setEnabled(false);
			acceptBt.setEnabled(false);
			rejectBt.setEnabled(false);

			myAgent = null;

			appendScreen("\n*************************************");
			appendScreen("Allright, then! I\'m outta\' here ...");
			appendScreen("*************************************\n");
		} else if (e.getSource().equals(proposeBt)) {
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			
			msg.addReceiver(new AID(agentName.getText().trim(),AID.ISLOCALNAME));
			msg.setContent("I'm proposing you something!");
			
			myAgent.send(msg);
		} else if (e.getSource().equals(acceptBt)) {
		} else if (e.getSource().equals(rejectBt)) {
		}
	}

	public void setScreen(String screen) {
		this.screen.setText(screen + "\n");
	}

	public void appendScreen(String screen) {
		this.screen.append(screen + "\n");
	}

	public MobileAgent getMyAgent() {
		return myAgent;
	}

	public void setMyAgent(MobileAgent myAgent) {
		this.myAgent = myAgent;
	}
}
