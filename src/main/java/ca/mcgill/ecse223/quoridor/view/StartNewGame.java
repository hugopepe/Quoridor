package ca.mcgill.ecse223.quoridor.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Font;

import ca.mcgill.ecse223.quoridor.controller.QuoridorController;

import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author Mael
 * This is our first view page, its the welcome screen with the 2 options a player should have:
 * Create a new game or load a previous one. 
 * Each JButton  opens up the corresponding page/frame and closes this one
 *
 */
public class StartNewGame {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartNewGame window = new StartNewGame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StartNewGame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblWelcome = new JLabel("Welcome to Quoridor");
		lblWelcome.setBounds(99, 46, 214, 25);
		lblWelcome.setVerticalAlignment(SwingConstants.TOP);
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 20));
		frame.getContentPane().add(lblWelcome);
		
		JButton btnStartNewGame = new JButton("Start a New Game");
		btnStartNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//initialize a NewGame
				QuoridorController.initializeGame();

				//send to Provide Username View
				ProvideSelectUsername.run(); 
				
				//close this view
				frame.setVisible(false);
				frame.dispose(); 
				
			}
		});
		btnStartNewGame.setBounds(127, 87, 161, 29);
		frame.getContentPane().add(btnStartNewGame);
		
		JLabel lblOr = new JLabel("-OR-");
		lblOr.setBounds(192, 132, 34, 20);
		lblOr.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblOr);
		
		JButton btnLoadAPosition = new JButton("Load a Position File");
		btnLoadAPosition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			  LoadPosition.run(true); 
				
				frame.setVisible(false);
				frame.dispose(); 
			}
		});
		btnLoadAPosition.setBounds(127, 158, 169, 29);
		btnLoadAPosition.setVerticalAlignment(SwingConstants.BOTTOM);
		frame.getContentPane().add(btnLoadAPosition);
	}

}
