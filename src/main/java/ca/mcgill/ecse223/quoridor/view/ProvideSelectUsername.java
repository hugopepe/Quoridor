package ca.mcgill.ecse223.quoridor.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import ca.mcgill.ecse223.quoridor.model.*;


import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import javax.swing.SwingConstants;
/**
 * @author Hugo Parent-Pothier
 * 
 * The GUI component to select or create usernames for players during the initialization of the game.
 *
 */
public class ProvideSelectUsername extends JFrame {
	
	//Model elements
	private Quoridor quoridor = QuoridorApplication.getQuoridor();
	private User user;
	private List<User> users = quoridor.getUsers();
	
	//GUI components
	private JPanel contentPane;
	private JComboBox<String> availableUsers; 
	private JTextField textField;
	private JLabel errorMessage;
	private JLabel lblSelectExistingUser;
	private JLabel lblCreateNewUser;
	
	private String playerColor;
	List<String> availableUsernames = new ArrayList<String>();
	
	/**
	 * Launch the application.
	 */

	public static void run() {
		//Create default users
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		quoridor.addUser("user1");
		quoridor.addUser("user2");
		
		try {
			ProvideSelectUsername frame = new ProvideSelectUsername();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		


	/**
	 * Create the frame.
	 */
	public ProvideSelectUsername() throws IllegalArgumentException {
		
		
		setResizable(false);
		
		if(!quoridor.getCurrentGame().hasWhitePlayer()) {
			playerColor = "white";
		} else if(!quoridor.getCurrentGame().hasWhitePlayer()) {
			playerColor = "black";
		} else throw new IllegalArgumentException("The game already has players!"); 
		
		setTitle("Provide or Select Username");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
	
		lblSelectExistingUser = new JLabel("Select a username for " + playerColor + " player");
		lblSelectExistingUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblSelectExistingUser.setBounds(88, 38, 273, 27);
		contentPane.add(lblSelectExistingUser);
		
		JLabel lblor = new JLabel("-OR-");
		lblor.setHorizontalAlignment(SwingConstants.CENTER);
		lblor.setBounds(204, 133, 39, 16);
		contentPane.add(lblor);
		
		lblCreateNewUser = new JLabel("Create a new username for " + playerColor + " player");
		lblCreateNewUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblCreateNewUser.setBounds(78, 173, 294, 16);
		contentPane.add(lblCreateNewUser);
		
		textField = new JTextField();
		textField.setBounds(75, 192, 300, 26);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnSelectUsername = new JButton("Select Username");
		btnSelectUsername.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				errorMessage.setText("");
				try {
					QuoridorController.assignUserToPlayer(User.getWithName(String.valueOf(availableUsers.getSelectedItem())));
					
				} catch(RuntimeException ex) {
					errorMessage.setText("You have to select a user");
					refreshData();
					return;
				}
				
				if(quoridor.getCurrentGame().hasWhitePlayer()) {
					if(quoridor.getCurrentGame().hasBlackPlayer()) {
						//The usernames have been set for both players
						goToSetThinkingTime();
					} else {
						playerColor = "black";
						refreshData();
					}	
				} else refreshData();
			}
		});
		
		btnSelectUsername.setBounds(151, 92, 147, 29);
		contentPane.add(btnSelectUsername);
		
		JButton btnCreateUsername = new JButton("Create Username");
		btnCreateUsername.setBounds(151, 218, 147, 29);
		contentPane.add(btnCreateUsername);
		btnCreateUsername.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				errorMessage.setText("");
				try {
					user = QuoridorController.createUser(textField.getText());
				} catch (RuntimeException ex) {
					errorMessage.setText("The username provided is not allowed");
					refreshData();
					return;
				}
			
				//If successful
				QuoridorController.assignUserToPlayer(user);
				if(quoridor.getCurrentGame().hasWhitePlayer()) {
					if(quoridor.getCurrentGame().hasBlackPlayer()) {
						//The usernames have been set for both players
						goToSetThinkingTime();
					} else {
						playerColor = "black";
						refreshData();
					}	
				} else refreshData();
			}
		});
		
		
		availableUsers = new JComboBox<String>();
		
		availableUsers.setBounds(78, 64, 294, 27);
		for(User u: users) {
			availableUsers.addItem(u.getName());
		}
		contentPane.add(availableUsers);
		availableUsers.setSelectedIndex(-1);
		
		
		errorMessage = new JLabel();
		errorMessage.setHorizontalAlignment(SwingConstants.CENTER);
		errorMessage.setForeground(Color.RED);
		errorMessage.setBounds(78, 245, 294, 16);
		contentPane.add(errorMessage);

	}
	
	/**
	 * Close the current window and open SetThinkingTime. (The next step in the initialization)
	 */
	private void goToSetThinkingTime() {
		QuoridorController.initQuoridorAndBoard();
		SetTotalThinkingTimeAndInitializeBoard.run();
		this.setVisible(false);
		this.dispose();
		
	}
	
	/**
	 * Refresh the displayed elements.
	 */
	private void refreshData() {
		lblSelectExistingUser.setText("Select a username for " + playerColor + " player");
		lblCreateNewUser.setText("Create a new username for " + playerColor + " player");
		textField.setText("");
		if(quoridor.getCurrentGame().hasWhitePlayer()) { //white player user has been set, so update the available usernames
			availableUsers.removeAllItems();
			for(User u: users) {
				if(!quoridor.getCurrentGame().getWhitePlayer().getUser().equals(u)) {
					availableUsers.addItem(u.getName());
				}
			}
		}
		availableUsers.setSelectedIndex(-1);
		textField.setText("");
	}
	
}
