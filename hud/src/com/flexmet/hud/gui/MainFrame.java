package com.flexmet.hud.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;

import com.flexmet.thrift.FastPathService;
import com.flexmet.thrift.HudFastPathEvent;
import com.flexmet.thrift.HudFastPathService;
import com.flexmet.thrift.HudPIQLQuery;
import com.flexmet.thrift.HudPIQLResponse;
import com.flexmet.thrift.HudPIQLService;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.JTextArea;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextPane textPane;
	private JTextArea textArea;
	/**
	 * Launch the application.
	 */
	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 582, 397);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(16, 301, 187, 28);
		contentPane.add(textField);
		textField.setColumns(10);
		
		
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBounds(306, 68, 270, 301);
		contentPane.add(textPane);
		
		JButton btnNewButton = new JButton("Send FastPath");
		btnNewButton.setBounds(16, 341, 161, 28);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TSocket fpSocket = new TSocket(Main.masterServer, HudFastPathService.hudFastPathPort);
				try {
					fpSocket.open();
				} catch (TTransportException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				TBinaryProtocol fpProtocol = new TBinaryProtocol(fpSocket);
				HudFastPathService.Client fpClient = new HudFastPathService.Client(fpProtocol);
				HudFastPathEvent event = new HudFastPathEvent();
				event.setFpData(textField.getText());
				HudFastPathEvent response;
				try {
					 
					 response = fpClient.executeFastPath(event);
					 textPane.setText(response.getFpData());
					 System.out.println("Response: "+response.getFpData());
				} catch (TException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					fpSocket.close();
				}
			}
		});
		contentPane.add(btnNewButton);
		
		JLabel lblNewLabel = new JLabel("Flexmet HUD");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(233, 6, 112, 42);
		contentPane.add(lblNewLabel);
		
		JLabel lblFastpathCommand = new JLabel("FastPath Command:");
		lblFastpathCommand.setBounds(16, 273, 139, 16);
		contentPane.add(lblFastpathCommand);
		
		JLabel lblRealTimeQuery = new JLabel("Historical query");
		lblRealTimeQuery.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		lblRealTimeQuery.setBounds(16, 86, 81, 16);
		contentPane.add(lblRealTimeQuery);
		
		JLabel lblPiqlQuery = new JLabel("PIQL Query:");
		lblPiqlQuery.setBounds(16, 72, 91, 16);
		contentPane.add(lblPiqlQuery);
		
		JButton btnNewButton_1 = new JButton("FIRE DA PIQL");
		btnNewButton_1.setBounds(16, 211, 161, 28);
		contentPane.add(btnNewButton_1);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				TSocket piqlSocket = new TSocket(Main.masterServer, HudPIQLService.hudPIQLPort);
				try {
					piqlSocket.open();
				} catch (TTransportException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				TBinaryProtocol piqlProtocol = new TBinaryProtocol(piqlSocket);
				HudPIQLService.Client piqlClient = new HudPIQLService.Client(piqlProtocol);
				HudPIQLQuery query = new HudPIQLQuery();
				query.setQuery(textArea.getText());
				HudPIQLResponse response;
				try {
					 
					 response = piqlClient.executePIQLQuery(query);
					 textPane.setText(response.getResponse());
					 System.out.println("Response: "+response.getResponse());
				} catch (TException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					piqlSocket.close();
				}
			}
		});
		JLabel label = new JLabel("Real time query");
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		label.setBounds(16, 283, 81, 16);
		contentPane.add(label);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setBounds(16, 106, 177, 93);
		contentPane.add(textArea);
	}
}
