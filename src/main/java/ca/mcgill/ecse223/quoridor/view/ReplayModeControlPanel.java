package ca.mcgill.ecse223.quoridor.view;

import javax.swing.JPanel;

import ca.mcgill.ecse223.quoridor.controller.InvalidInputException;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class ReplayModeControlPanel extends JPanel {
	// Control Panel
	private int panelWidth = BoardPanel.getBoardWidth();
	private int panelHeight = MyQuoridor.getFrameHeight() - BoardPanel.getBoardHeight();

	// jump buttons
	private static int buttonWidth = 130;
	private static int buttonHeigtht = 29;

	/**
	 * Create the panel.
	 */
	public ReplayModeControlPanel() {
		setBackground(new Color(204, 255, 113));
		setBounds(0, 0, panelWidth, panelHeight);
		setLayout(null);

		JButton btnContinueGame = new JButton("Continue Game");
		btnContinueGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					QuoridorController.continueGame();
				}catch(Exception e1) {
					BoardPanel.displayError(e1.getMessage());
					return;
				}
				MyQuoridor.getGameNotRunningControlPanel().setVisible(false);
				MyQuoridor.getControlPanel().setVisible(true);
				MyQuoridor.getReplayModeControlPanel().setVisible(false);
			}
		});
		btnContinueGame.setBounds(256, 230, 176, 36);
		btnContinueGame.setBounds((panelWidth - buttonWidth) / 2, 11, buttonWidth, buttonHeigtht);
		add(btnContinueGame);

		JButton btnJumpToStart = new JButton("Jump to Start");
		btnJumpToStart.setBounds(9, 30 + buttonHeigtht, buttonWidth, buttonHeigtht);
		btnJumpToStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					QuoridorController.jumpToStart();
					BoardPanel.clearError();
				} catch (InvalidInputException e) {

					e.printStackTrace();
				}
				MyQuoridor.refreshData();// update visuals
				MyQuoridor.refreshLoadWall();

			}
		});
		add(btnJumpToStart);
		
		/**
		 * @author Mahroo Rahman
		 * when the button step backwards is pressed in replay
		 * mode by first clicking on stop game and then click 
		 * replay mode and then click step backwards,
		 * it should cause the player who made his/her last
		 * move one step to its previous position and continues to moves backwards for each player
		 */
		

		JButton btnStepBackwards = new JButton("Step Backwards");
		btnStepBackwards.setBounds(btnJumpToStart.getX() + buttonWidth + 9, 30 + buttonHeigtht, buttonWidth,
				buttonHeigtht);
		btnStepBackwards.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					QuoridorController.stepBackward();
					BoardPanel.clearError();
				} catch (InvalidInputException e) {

					e.printStackTrace();
				}
				MyQuoridor.refreshData();// update visuals
				MyQuoridor.refreshLoadWall();

			}
		});
		add(btnStepBackwards);
		
		/**
		 * @author Mahroo Rahman
		 * when the button step forwards is pressed in replay
		 * mode by first clicking on stop game and then click 
		 * replay mode and then click step forwards,
		 * it should cause the player who made his/her last
		 * move one step to its forward position and continues to move forwards for each player
		 */

		JButton btnStepForward = new JButton("Step Forward");
		btnStepForward.setBounds(btnStepBackwards.getX() + buttonWidth + 9, 30 + buttonHeigtht, buttonWidth,
				buttonHeigtht);
		btnStepForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					QuoridorController.stepforward();
					BoardPanel.clearError();
				} catch (InvalidInputException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				MyQuoridor.refreshData();// update visuals
				MyQuoridor.refreshLoadWall();
			}
		});
		add(btnStepForward);

		JButton btnJumpToFinal = new JButton("Jump to Final");
		btnJumpToFinal.setBounds(btnStepForward.getX() + buttonWidth + 9, 30 + buttonHeigtht, buttonWidth,
				buttonHeigtht);
		btnJumpToFinal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					QuoridorController.jumpToFinal();
					BoardPanel.clearError();

				} catch (InvalidInputException e) {

					e.printStackTrace();
				}
				MyQuoridor.refreshData();// update visuals
				MyQuoridor.refreshLoadWall();
			}
		});
		add(btnJumpToFinal);

	}

}
