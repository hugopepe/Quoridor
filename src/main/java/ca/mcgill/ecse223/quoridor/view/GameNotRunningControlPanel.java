package ca.mcgill.ecse223.quoridor.view;

import javax.swing.JPanel;


import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class GameNotRunningControlPanel extends JPanel {
	// Control Panel
	private int panelWidth = BoardPanel.getBoardWidth();
	private int panelHeight = MyQuoridor.getFrameHeight() - BoardPanel.getBoardHeight();

	//both buttons
	private static int buttonWidth = 165;
	private static int buttonHeight = 29;

	//resume button
	private static JButton btnResume;


	public static JButton getBtnResume() {
		return btnResume;
	}


	/**
	 * Create the panel.
	 */
	public GameNotRunningControlPanel() {
		setBackground(new Color(204, 255, 153));
		setBounds(0, 0, panelWidth, panelHeight);
		setLayout(null);
	
		JButton btnEnterReplayMode = new JButton("Enter Replay Mode");
		btnEnterReplayMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				MyQuoridor.getGameNotRunningControlPanel().setVisible(false);
				MyQuoridor.getControlPanel().setVisible(false);
				MyQuoridor.getReplayModeControlPanel().setVisible(true);
				QuoridorController.initiateReplayMode();
			}
		});
		btnEnterReplayMode.setBounds((panelWidth - buttonWidth) / 2, 15, buttonWidth, buttonHeight);
		add(btnEnterReplayMode);

		btnResume = new JButton("Resume Game");
		btnResume.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QuoridorController.resumeGame();
				MyQuoridor.getGameNotRunningControlPanel().setVisible(false);
				MyQuoridor.getControlPanel().setVisible(true);
				MyQuoridor.refreshData();
			}
		});
		btnResume.setBounds((panelWidth - buttonWidth) / 2, btnEnterReplayMode.getY()+10+buttonHeight, buttonWidth, buttonHeight);
		add(btnResume);
		
		JButton btnLoadGame = new JButton("Load Game");
		btnLoadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  LoadPosition.run(false);
			}
		});
		btnLoadGame.setBounds((panelWidth - buttonWidth) / 2,btnResume.getY()+10+buttonHeight , buttonWidth, buttonHeight);
		add(btnLoadGame);
		
		

		
	}



}



