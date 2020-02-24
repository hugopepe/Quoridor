package ca.mcgill.ecse223.quoridor.view;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.InvalidInputException;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import ca.mcgill.ecse223.quoridor.model.Player;
import ca.mcgill.ecse223.quoridor.model.Game.MoveMode;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class MoveControlPanel extends JPanel {
	// Control Panel
	private int panelWidth = BoardPanel.getBoardWidth();
	private int panelHeight = MyQuoridor.getFrameHeight() - BoardPanel.getBoardHeight();

	// Arrow buttons
	private static int arrowButtonWidth = 69;
	private static int arrowButtonHeight = 35;
	
	//Diagonal jump buttons
	private static int diagJumpBtnWidth = 35;
	private static int diagJumpBtnHeight = 35;

	// Grab/Drop buttons
	private static int grabDropButtonWidth = 92;
	private static int grabDropButtonHeight = 29;

	/**
	 * Create the panel.
	 */
	public MoveControlPanel() {
		setBackground(new Color(204, 153, 0));
		setBounds(0, 0, panelWidth, panelHeight);
		setLayout(null);

		JButton btnUp = new JButton("UP");
		btnUp.setBounds((panelWidth - arrowButtonWidth) / 2, 6, arrowButtonWidth, arrowButtonHeight);
		btnUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goSide("up");

			}
		});
		add(btnUp);

		JButton btnDown = new JButton("DOWN");
		btnDown.setBounds((panelWidth - arrowButtonWidth) / 2, 73, arrowButtonWidth, arrowButtonHeight);
		btnDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goSide("down");
			}
		});
		add(btnDown);

		JButton btnRight = new JButton("RIGHT");
		btnRight.setBounds((panelWidth - arrowButtonWidth) / 2 + arrowButtonWidth, 39, arrowButtonWidth, arrowButtonHeight);
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goSide("right");				
			}
		});
		add(btnRight);

		JButton btnLeft = new JButton("LEFT");
		btnLeft.setBounds((panelWidth - arrowButtonWidth) / 2 - arrowButtonWidth, 39, arrowButtonWidth, arrowButtonHeight);
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				goSide("left");
			}
		});
		add(btnLeft);
		
		JButton btnUL = new JButton("UL");
		btnUL.setBounds((panelWidth - arrowButtonWidth) / 2 - diagJumpBtnWidth, 6, diagJumpBtnWidth, diagJumpBtnHeight);
		btnUL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goSide("upleft");
			}
		});
		add(btnUL);
		
		JButton btnUR = new JButton("UR");
		btnUR.setBounds(panelWidth / 2 + diagJumpBtnWidth, 6, diagJumpBtnWidth, diagJumpBtnHeight);
		btnUR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goSide("upright");
			}
		});
		add(btnUR);
		
		JButton btnDR = new JButton("DR");
		btnDR.setBounds(panelWidth / 2 + diagJumpBtnWidth, 73, diagJumpBtnWidth, diagJumpBtnHeight);
		btnDR.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goSide("downright");
			}
		});
		add(btnDR);
		
		JButton btnDL = new JButton("DL");
		btnDL.setBounds((panelWidth - arrowButtonWidth) / 2 - diagJumpBtnWidth, 73, diagJumpBtnWidth, diagJumpBtnHeight);
		btnDL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goSide("downleft");
			}
		});
		add(btnDL);
		
		

		JButton btnGrabWall = new JButton("Grab Wall");
		btnGrabWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BoardPanel.clearError();
				try {
					QuoridorController.wallGrab();
				} catch (InvalidInputException e1) {
					BoardPanel.displayError("There are no walls to grab");// print exception as error message on view
				}
				MyQuoridor.refreshWallMove();
			}
		});
		btnGrabWall.setBounds((panelWidth - grabDropButtonWidth) / 2, 120, grabDropButtonWidth, grabDropButtonHeight);
		add(btnGrabWall);

		JButton btnDropWall = new JButton("Drop Wall");
		btnDropWall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// dropWall
				try {
					QuoridorController.dropWall();
				} catch (Exception e) {
					BoardPanel.displayError("Invalid move, try again.");// print exception as error message on view
					return;
				}
				BoardPanel.clearError();
				MyQuoridor.refreshData();// update visuals
				MyQuoridor.refreshDrop();
			}
		});

		btnDropWall.setBounds((panelWidth - grabDropButtonWidth) / 2, 145, grabDropButtonWidth, grabDropButtonHeight);
		add(btnDropWall);

		JButton btnRotate = new JButton("ROTATE");
		btnRotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnRotate.setBounds((panelWidth - arrowButtonWidth) / 2, 39, 69, 35);
		btnRotate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				QuoridorController.flipWall();
				MyQuoridor.refreshWallMove();

			}
		});
		add(btnRotate);

		ActionMap actionMap = getActionMap();
		int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap inputMap = getInputMap(condition);

		for (KeyDirection direction : KeyDirection.values()) {
			inputMap.put(direction.getKeyStroke(), direction.getText());
			actionMap.put(direction.getText(), new MyArrowBinding(direction.getText()));
		}

	}

	
	static void goSide(String side) {
		BoardPanel.clearError();
		if (QuoridorApplication.getQuoridor().getCurrentGame().getMoveMode() == MoveMode.WallMove) {
			try {
				QuoridorController.moveWall(side);
			} catch(IllegalArgumentException e) {
				BoardPanel.displayError(e.getMessage());
			}
			MyQuoridor.refreshWallMove();
			
		}

		// if we are playermove mode, arrows should move player
		else if (QuoridorApplication.getQuoridor().getCurrentGame().getMoveMode() == MoveMode.PlayerMove) {
			// move player up
			Player player = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove();

			try {
				QuoridorController.movePlayer(player, side);
			} catch (InvalidInputException e1) {
				BoardPanel.displayError(e1.getMessage());
				return;
			}
			//BoardPanel.clearError();
			QuoridorController.checkGameResult();
			MyQuoridor.refreshData();// update visuals
		}
	}


}

class MyArrowBinding extends AbstractAction {
	public MyArrowBinding(String text) {
		super(text);
		putValue(ACTION_COMMAND_KEY, text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		MoveControlPanel.goSide(actionCommand);
	}

}

enum KeyDirection {
	UP("up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0)), 
	DOWN("down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0)),
	LEFT("left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0)),
	RIGHT("right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));

	KeyDirection(String text, KeyStroke keyStroke) {
		this.text = text;
		this.keyStroke = keyStroke;
	}

	private String text;
	private KeyStroke keyStroke;

	public String getText() {
		return text;
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}
}
