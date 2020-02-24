package ca.mcgill.ecse223.quoridor.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Font;
import javax.swing.SwingConstants;
import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import ca.mcgill.ecse223.quoridor.model.Game;
import ca.mcgill.ecse223.quoridor.model.Move;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.Player;
import ca.mcgill.ecse223.quoridor.model.Tile;
import ca.mcgill.ecse223.quoridor.model.WallMove;

public class MyQuoridor {

	private static JFrame frame;

	// Frame size
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static int frameWidth = screenSize.width;
	private static int frameHeight = screenSize.height;

	// Board
	private static BoardPanel boardPanel = new BoardPanel();
	private static int boardHOffset = (frameWidth - BoardPanel.getBoardWidth()) / 2;
	private static int boardVOffset = 1;

	private static int boardWidth = BoardPanel.getBoardWidth();

	// Control Panels
	private static MoveControlPanel controlPanel = new MoveControlPanel();

	public static MoveControlPanel getControlPanel() {
		return controlPanel;
	}

	private static GameNotRunningControlPanel gameNotRunningControlPanel = new GameNotRunningControlPanel();

	private static ReplayModeControlPanel replayModeControlPanel = new ReplayModeControlPanel();

	private static int controlPanelHOffset = boardHOffset;
	private static int controlPanelVOffset = boardVOffset + BoardPanel.getBoardHeight();

	public static GameNotRunningControlPanel getGameNotRunningControlPanel() {
		return gameNotRunningControlPanel;
	}

	public static ReplayModeControlPanel getReplayModeControlPanel() {
		return replayModeControlPanel;
	}


	// Player info labels
	private static int playerLblWidth = 117;

	private final JLabel lblWhitePlayer = new JLabel("White Player");
	private final JLabel lblWhiteUser = new JLabel("user1");
	private final JLabel lblWhitePlayerTimeHeader = new JLabel("Time Remaining");
	private final static JLabel lblWhitePlayerTime = new JLabel();

	private final JLabel lblBlackPlayer = new JLabel("Black Player");
	private final JLabel lblBlackUser = new JLabel("user2");
	private final JLabel lblBlackPlayerTimeHeader = new JLabel("Time Remaining");
	private final static JLabel lblBlackPlayerTime = new JLabel();
	public static ArrayList<Player> players;
	public final static JLabel lblWallinhand = new JLabel("");

	/**
	 * Getters and Setters
	 */

	public static JFrame getFrame() {
		return frame;
	}

	public static int getFrameWidth() {
		return frameWidth;
	}

	public static int getFrameHeight() {
		return frameHeight;
	}

	/**
	 * Launch the application.
	 */

	public static void run() {
		refreshData();

		try {
			MyQuoridor window = new MyQuoridor();
			MyQuoridor.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public MyQuoridor() {

		initialize();

		frame.getContentPane().setLayout(null);

		// Board
		frame.getContentPane().add(boardPanel);
		boardPanel.setLocation(boardHOffset, boardVOffset);
		boardPanel.setLayout(null);

		// Move control Panel
		frame.getContentPane().add(getControlPanel());
		getControlPanel().setLocation(controlPanelHOffset, controlPanelVOffset);
		getControlPanel().setLayout(null);
		// controlPanel.setVisible(false);

		// Game not running control Panel
		frame.getContentPane().add(gameNotRunningControlPanel);
		gameNotRunningControlPanel.setLocation(controlPanelHOffset, controlPanelVOffset);
		gameNotRunningControlPanel.setLayout(null);
		gameNotRunningControlPanel.setVisible(false);

		// Replay mode control Panel
		frame.getContentPane().add(replayModeControlPanel);
		replayModeControlPanel.setLocation(controlPanelHOffset, controlPanelVOffset);
		replayModeControlPanel.setLayout(null);
		replayModeControlPanel.setVisible(false);

		// White Player label
		lblWhitePlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblWhitePlayer.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblWhitePlayer.setBounds((frameWidth - boardWidth) / 4 - playerLblWidth / 2, 226, playerLblWidth, 37);
		frame.getContentPane().add(lblWhitePlayer);

		// White player username
		lblWhiteUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblWhiteUser.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 14));
		lblWhiteUser.setBounds((frameWidth - boardWidth) / 4 - playerLblWidth / 2, 263, playerLblWidth, 24);
		frame.getContentPane().add(lblWhiteUser);

		// White player timer header
		lblWhitePlayerTimeHeader.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblWhitePlayerTimeHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblWhitePlayerTimeHeader.setBounds((frameWidth - boardWidth) / 4 - playerLblWidth / 2, 418, playerLblWidth, 16);
		frame.getContentPane().add(lblWhitePlayerTimeHeader);

		// White player timer
		lblWhitePlayerTime.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblWhitePlayerTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblWhitePlayerTime.setBounds((frameWidth - boardWidth) / 4 - playerLblWidth / 2, 446, playerLblWidth, 16);
		frame.getContentPane().add(lblWhitePlayerTime);

		// Black player label
		lblBlackPlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlackPlayer.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblBlackPlayer.setBounds(boardWidth + (frameWidth - boardWidth / 2 - playerLblWidth) / 2, 226, playerLblWidth,
				37);
		frame.getContentPane().add(lblBlackPlayer);

		// Black player username
		lblBlackUser.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlackUser.setFont(new Font("Lucida Grande", Font.BOLD | Font.ITALIC, 14));
		lblBlackUser.setBounds(boardWidth + (frameWidth - boardWidth / 2 - playerLblWidth) / 2, 263, playerLblWidth,
				24);
		frame.getContentPane().add(lblBlackUser);

		// Black player timer header
		lblBlackPlayerTimeHeader.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblBlackPlayerTimeHeader.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlackPlayerTimeHeader.setBounds(boardWidth + (frameWidth - boardWidth / 2 - playerLblWidth) / 2, 418,
				playerLblWidth, 16);
		frame.getContentPane().add(lblBlackPlayerTimeHeader);

		// Black player timer
		lblBlackPlayerTime.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblBlackPlayerTime.setHorizontalAlignment(SwingConstants.CENTER);
		lblBlackPlayerTime.setBounds(boardWidth + (frameWidth - boardWidth / 2 - playerLblWidth) / 2, 446,
				playerLblWidth, 16);
		frame.getContentPane().add(lblBlackPlayerTime);

		// TODO Maybe incorporate this to message label? Or just use a String since its
		// not going to be
		// visible.
		lblWallinhand.setBounds(26, 160, 69, 20);
		lblWallinhand.setVisible(false);// used to pass stepdefinitions tests
		frame.getContentPane().add(lblWallinhand);

		// Save button
		JButton btnSavePosition = new JButton("Save Position");
		btnSavePosition.setBounds(6, 6, 117, 29);
		frame.getContentPane().add(btnSavePosition);
		btnSavePosition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// send to SavePosition
				SavePosition.run();

			}
		});
		
		//Stop Game button
		JButton btnStopGame = new JButton("Stop Game");
		btnStopGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				QuoridorController.stopGame();
				BoardPanel.messageLbl.setText("Stopped");
				getGameNotRunningControlPanel().setVisible(true);
				getControlPanel().setVisible(false);
				getReplayModeControlPanel().setVisible(false);
				
			}
		});
		btnStopGame.setBounds(6, 51, 117, 29);
		frame.getContentPane().add(btnStopGame);

		//Stop Game button
		JButton btnResignGame = new JButton("Resign Game");
		btnResignGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				QuoridorController.resignGame();
				refreshPlayerTurnLbl();
				getGameNotRunningControlPanel().setVisible(true);
				getControlPanel().setVisible(false);
				getReplayModeControlPanel().setVisible(false);
				
			}
		});
		btnResignGame.setBounds(6, 96, 117, 29);
		frame.getContentPane().add(btnResignGame);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(0, 0, screenSize.width, screenSize.height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * @author Mael This refresh method is automatically called by our clock in the controller 
	 * it looks at the current player's remaining time and updates the corresponding JLabel
	 */	
	public static void refreshTimer() {

		// refresh time for white player
		lblWhitePlayerTime
				.setText(QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getRemainingTime() + "");

		// refresh time for black player
		lblBlackPlayerTime
				.setText(QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer().getRemainingTime() + "");

	}

	/**
	 * This refreshes the label that shows whose turn it is
	 */
	public static void refreshPlayerTurnLbl() {
		// refresh player to move label
		// refresh player to move label
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		boolean gameOver = false;
		if (game.getGameStatus() == GameStatus.BlackWon) {
			BoardPanel.messageLbl.setText("Black Player Wins");
			gameOver=true;
		}
		else if (game.getGameStatus() == GameStatus.WhiteWon) {
			BoardPanel.messageLbl.setText("White Player Wins");
			gameOver=true;
		}
		else if (game.getGameStatus() == GameStatus.Draw) {
			BoardPanel.messageLbl.setText("Game Drawn");
			gameOver=true;
		}
		else {
			String s = getPlayerColor(game.getCurrentPosition().getPlayerToMove());
			BoardPanel.messageLbl.setText(s + " player's turn to move");
		}

		//game over
		if(gameOver) {
			getGameNotRunningControlPanel().setVisible(true);
			getGameNotRunningControlPanel().getBtnResume().setVisible(false);
			getControlPanel().setVisible(false);
			getReplayModeControlPanel().setVisible(false);
		}
	}

	/**
	 * This is our main refresh method
	 */
	public static void refreshData() {
		// refresh player to move label
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		boolean gameOver = false;
		if (game.getGameStatus() == GameStatus.BlackWon) {
			BoardPanel.messageLbl.setText("Black Player Wins");
			gameOver=true;
		}
		else if (game.getGameStatus() == GameStatus.WhiteWon) {
			BoardPanel.messageLbl.setText("White Player Wins");
			gameOver=true;
		}
		else if (game.getGameStatus() == GameStatus.Draw) {
			BoardPanel.messageLbl.setText("Game Drawn");
			gameOver=true;
		}
		else {
			String s = getPlayerColor(game.getCurrentPosition().getPlayerToMove());
			BoardPanel.messageLbl.setText(s + " player's turn to move");
		}

		//game over
		if(gameOver) {
			getGameNotRunningControlPanel().setVisible(true);
			getGameNotRunningControlPanel().getBtnResume().setVisible(false);
			getControlPanel().setVisible(false);
			getReplayModeControlPanel().setVisible(false);
		}
		else {//reset in case game not over
			getGameNotRunningControlPanel().getBtnResume().setVisible(true);
		}
		
		// refresh white pawn
		int whiteCol = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getWhitePosition()
				.getTile().getColumn();
		int whiteRow = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getWhitePosition()
				.getTile().getRow();
		int whiteTileIndex = (whiteRow - 1) * 9 + (whiteCol - 1);
		boardPanel.movePawn(BoardPanel.getWhitePawn(), BoardPanel.getTiles()[whiteTileIndex]);
		// refresh black pawn
		int blackCol = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getBlackPosition()
				.getTile().getColumn();
		int blackRow = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getBlackPosition()
				.getTile().getRow();
		int blackTileIndex = (blackRow - 1) * 9 + (blackCol - 1);
		boardPanel.movePawn(BoardPanel.getBlackPawn(), BoardPanel.getTiles()[blackTileIndex]);

		// refresh move number
		List<Move> moves = QuoridorApplication.getQuoridor().getCurrentGame().getMoves();
		int currentPositionIndex = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getId();
		if (currentPositionIndex > 0) {
			int moveNumber = moves.get(currentPositionIndex - 1).getMoveNumber(); 
			BoardPanel.setMoveNumber(moveNumber);
		} else {
			BoardPanel.setMoveNumber(0);	
		}
		BoardPanel.getRoundHeaderLbl().setText("Last move number: " + BoardPanel.getMoveNumber());
		// TODO add refresh walls (For load position)

	}

	public static void refreshWallMove() {
		lblWallinhand.setText("Wall in hand");// used to pass step definitions tests

		if (QuoridorApplication.getQuoridor().getCurrentGame().hasWallMoveCandidate()) {
			String orientation = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate()
					.getWallDirection().name();

			int wallId = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate().getWallPlaced()
					.getId();
			Tile t = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate().getTargetTile();

			boardPanel.moveWall(wallId, orientation, t.getColumn(), t.getRow());
			boardPanel.setWallColor(wallId, Color.BLUE);
			boardPanel.bringWallForeground(wallId);
		}
		
	}

	public static void refreshDrop() {
		lblWallinhand.setText("No Wall in hand");// used to pass step definitions tests
		Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		WallMove lastMove = (WallMove) currentGame.getMove(currentGame.getMoves().size() - 1);
		int wallId = lastMove.getWallPlaced().getId();
		// wall is placed reset its color to default color
		if (wallId < 10) // white wall
			boardPanel.setWallColor(wallId, BoardPanel.getWhiteWallColor());
		else
			boardPanel.setWallColor(wallId, BoardPanel.getBlackWallColor());

	}
	
	public static void refreshLoad() {


      Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
      for (int i = 0; i < currentGame.getCurrentPosition().getWhiteWallsOnBoard().size(); i++) {
        int whiteWallId = currentGame.getCurrentPosition().getWhiteWallsOnBoard(i).getId();
        String whiteDir = currentGame.getCurrentPosition().getWhiteWallsOnBoard(i).getMove()
            .getWallDirection().name();
        Tile whiteTile = currentGame.getCurrentPosition().getWhiteWallsOnBoard(i).getMove().getTargetTile();
        

        boardPanel.moveWall(whiteWallId, whiteDir, whiteTile.getColumn(), whiteTile.getRow());
        boardPanel.bringWallForeground(whiteWallId);
        boardPanel.setWallColor(whiteWallId, BoardPanel.getWhiteWallColor());

      }
      for (int i = 0; i < currentGame.getCurrentPosition().getBlackWallsOnBoard().size(); i++) {
        int blackWallId = currentGame.getCurrentPosition().getBlackWallsOnBoard(i).getId();
        String blackDir = currentGame.getCurrentPosition().getBlackWallsOnBoard(i).getMove()
            .getWallDirection().name();
        Tile blackTile = currentGame.getCurrentPosition().getBlackWallsOnBoard(i).getMove().getTargetTile();

        
        boardPanel.moveWall(blackWallId, blackDir, blackTile.getColumn(), blackTile.getRow());
        boardPanel.bringWallForeground(blackWallId);
        boardPanel.setWallColor(blackWallId, BoardPanel.getBlackWallColor());
      }


    }



	public static void refreshLoadWall() {

		Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		for (int i = 0; i < currentGame.getCurrentPosition().getWhiteWallsOnBoard().size(); i++) {
			int whiteWallId = currentGame.getCurrentPosition().getWhiteWallsOnBoard(i).getId();
			String whiteDir = currentGame.getCurrentPosition().getWhiteWallsOnBoard(i).getMove().getWallDirection()
					.name();
			Tile whiteTile = currentGame.getCurrentPosition().getWhiteWallsOnBoard(i).getMove().getTargetTile();

			boardPanel.moveWall(whiteWallId, whiteDir, whiteTile.getColumn(), whiteTile.getRow());

			boardPanel.bringWallForeground(whiteWallId);
			boardPanel.setWallColor(whiteWallId, BoardPanel.getWhiteWallColor());
			System.out.print(whiteWallId);
		}
		for (int i = 0; i < currentGame.getCurrentPosition().getBlackWallsOnBoard().size(); i++) {
			int blackWallId = currentGame.getCurrentPosition().getBlackWallsOnBoard(i).getId();
			String blackDir = currentGame.getCurrentPosition().getBlackWallsOnBoard(i).getMove().getWallDirection()
					.name();
			Tile blackTile = currentGame.getCurrentPosition().getBlackWallsOnBoard(i).getMove().getTargetTile();

			boardPanel.moveWall(blackWallId, blackDir, blackTile.getColumn(), blackTile.getRow());
			boardPanel.bringWallForeground(blackWallId);
			boardPanel.setWallColor(blackWallId, BoardPanel.getBlackWallColor());
			System.out.print(blackWallId);
		}
		BoardPanel.placeWallsInStock();//To place walls in back in stock in replay mode
	}

	/**
	 * @author Mael This helper method returns the color of a player as a string
	 * @param player : the player object
	 * @return String : color ie. white or black
	 */
	public static String getPlayerColor(Player player) {
		if (QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove()
				.getGameAsWhite() != null)
			return "White";
		else
			return "Black";
	}
}
