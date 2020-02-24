package ca.mcgill.ecse223.quoridor.view;

import java.sql.Time;

import javax.swing.JFrame;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.InvalidInputException;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import ca.mcgill.ecse223.quoridor.model.Wall;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SetTotalThinkingTimeAndInitializeBoard {

	private JFrame frame;
	private JPanel contentPane;
	private JTextField min;
	private JTextField sec;
	

	/*
	 * @author zhuzhenLi
	 * GUI for set total thinking time and initialized board
	 */
	
	/**
	 * Launch the application.
	 */
	public static void run() {

		try {
			for (int j = 1; j <= 10; j++)
				new Wall(j, QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer());
			for (int j = 1; j <= 10; j++)
				new Wall(10+j, QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer());
			
			SetTotalThinkingTimeAndInitializeBoard window = new SetTotalThinkingTimeAndInitializeBoard();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create the application.
	 * @throws InvalidInputException 
	 */
	public SetTotalThinkingTimeAndInitializeBoard() throws InvalidInputException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()throws InvalidInputException {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		frame.setTitle("Set Total Thinking Time");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		// set total thinking time panel 
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 450, 278);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblSetTotalThinkingTime = new JLabel("Set the total thinking time (minutes and seconds) for both players");
		lblSetTotalThinkingTime.setBounds(18, 54, 415, 16);
		panel.add(lblSetTotalThinkingTime);
		
		
		
		JLabel lblMinutes = new JLabel("Minutes:");
		lblMinutes.setBounds(141, 147, 54, 16);
		panel.add(lblMinutes);
		
		min = new JTextField();
		min.setBounds(207, 142, 130, 26);
		panel.add(min);
		min.setColumns(10);
		
		JLabel lblSeconds = new JLabel("Seconds:");
		lblSeconds.setBounds(139, 111, 56, 16);
		panel.add(lblSeconds);
		
		sec = new JTextField();
		sec.setBounds(207, 106, 130, 26);
		panel.add(sec);
		sec.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		btnOk.setBounds(200, 205, 75, 30);
		panel.add(btnOk);
		
		JLabel inputError = new JLabel("Invalid Inputs Given");
		inputError.setBounds(170, 180, 130, 20);
		inputError.setForeground(Color.RED);
		panel.add(inputError);
		inputError.setVisible(false);
		
		
		
		// initialize board panel
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 450, 278);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		panel_1.setVisible(false);
		
		
		
		JButton btnInitializeBoard = new JButton("Initialize Board");
		btnInitializeBoard.setBounds(120, 100, 220, 30);
		panel_1.add(btnInitializeBoard);
		
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String min_input = min.getText();
				String sec_input = sec.getText();
				int input_min = -1;
				int input_sec = -1;
				try {
					input_min =  Integer.parseInt(min_input);
				}catch (NumberFormatException e1) {
					
				}
				try {
					input_sec =  Integer.parseInt(sec_input);
				}catch(NumberFormatException e2){
					
				}
				
				// call controller method 
				if(min_input.isEmpty() || sec_input.isEmpty() || input_min <0 || input_sec<0 ) {
					inputError.setVisible(true);
				}else if (input_min ==0 && input_sec==0) {
					inputError.setVisible(true);
				}else {
					inputError.setVisible(false);
					panel.setVisible(false);
					panel_1.setVisible(true);
					frame.setTitle("Initialize Board");
					
					
					@SuppressWarnings("deprecation")
					Time input_totalthinkingTime = new java.sql.Time(0,input_min, input_sec);
					try {
						QuoridorController.setTotalThinkingTime(input_totalthinkingTime);
					} catch (InvalidInputException e1) {
						inputError.setVisible(true);
					}
				}
				
			}
			
		});
		
		btnInitializeBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					QuoridorController.initializeBoard();
				} catch (InvalidInputException e1) {
				}

				// go to board view on GUI
				
				MyQuoridor.run();
				frame.setVisible(false);
				frame.dispose();
				
			}
		});
	}
}
