package ca.mcgill.ecse223.quoridor.view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import java.awt.Font;
import java.util.List;

import javax.swing.SwingConstants;

import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.model.GamePosition;
import ca.mcgill.ecse223.quoridor.model.Wall;

public class BoardPanel extends JLayeredPane {

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static int tileSize = (int) screenSize.getHeight() / 20;
	private static int tileSpace = tileSize / 5;

	private static int wallLength = 2 * tileSize + tileSpace;
	private static int wallWidth = tileSpace;

	// Move number info
	private static int moveNumber = 0; // game not started
	private static JLabel moveHeaderLbl = new JLabel("Last move number: " + moveNumber);
	


	private static int moveHeight = 20;
	private static int moveWidth = 200;


	// Message
	private static String message = "White player's turn to move";
	private static int messageHeight = 25;
	private static int messageWidth = 500;
	
	
	// Error
	private static JLabel errorLbl = new JLabel("");
	private static String error = "error";

	public static String getError() {
		return error;
	}

	private static int errorHeight = 16;
	private static int errorWidth = 300;

	// Player's turn label
	public static JLabel messageLbl;

	// Board size
	private static int boardWidth = 9 * tileSize + (12 * tileSpace) + 2 * wallLength;
	private static int boardHeight = 9 * tileSize + (11 * tileSpace) + messageHeight + moveHeight + errorHeight;

	// Tiles
	private static TileInView tiles[] = new TileInView[81];

	public static TileInView[] getTiles() {
		return tiles;
	}

	public static void setTiles(TileInView[] tiles) {
		BoardPanel.tiles = tiles;
	}

	private static int firstTileX = wallLength + 2 * tileSpace;
	private static int firstTileY = wallWidth + tileSpace;
	private static Color tileColor = new Color(204, 153, 0);

	// Walls
	private static Canvas walls[] = new Canvas[20];
	private static Color defaultWhiteWallColor = Color.GRAY;
	private static Color defaultBlackWallColor = Color.BLACK;

	public static Canvas[] getWalls() {
		return walls;
	}

	public static void setWalls(Canvas[] walls) {
		BoardPanel.walls = walls;
	}

	// Pawns
	private static int pawnSize = tileSize * 2 / 3;
	private static Pawn whitePawn = new Pawn(pawnSize, Color.WHITE);

	public static Pawn getWhitePawn() {
		return whitePawn;
	}

	public static void setWhitePawn(Pawn whitePawn) {
		BoardPanel.whitePawn = whitePawn;
	}

	public static Pawn getBlackPawn() {
		return blackPawn;
	}

	public static void setBlackPawn(Pawn blackPawn) {
		BoardPanel.blackPawn = blackPawn;
	}

	private static Pawn blackPawn = new Pawn(pawnSize, Color.BLACK);

	/**
	 * Getters and setters
	 */
	public static int getBoardWidth() {
		return boardWidth;
	}

	public static int getBoardHeight() {
		return boardHeight;
	}

	public static int getMoveNumber() {
		return moveNumber;
	}

	public static void setMoveNumber(int moveNumber) {
		BoardPanel.moveNumber = moveNumber;
	}
	
	public static JLabel getErrorLbl() {
		return errorLbl;
	}

	public static void setErrorLbl(JLabel errorLbl) {
		BoardPanel.errorLbl = errorLbl;
	}

	public static void setError(String error) {
		BoardPanel.error = error;
	}

	public static int getWallLength() {
		return wallLength;
	}

	public static Color getWhiteWallColor() {
		return defaultWhiteWallColor;
	}

	public static Color getBlackWallColor() {
		return defaultBlackWallColor;
	}

	public static void setWallLength(int wallLength) {
		BoardPanel.wallLength = wallLength;
	}

	public static int getWallWidth() {
		return wallWidth;
	}

	public static void setWallWidth(int wallWidth) {
		BoardPanel.wallWidth = wallWidth;
	}
	
	public static JLabel getRoundHeaderLbl() {
		return moveHeaderLbl;
	}

	/**
	 * Create the panel.
	 */
	public BoardPanel() {
		setBackground(new Color(255, 248, 220));
		// System.out.println(screenSize.getHeight());
		setBounds(0, 0, boardWidth, boardHeight);
		setLayout(null);

		// place message
		messageLbl = new JLabel(message);
		messageLbl.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		messageLbl.setBounds((boardWidth - messageWidth) / 2, boardHeight - messageHeight - moveHeight, messageWidth,
				messageHeight);
		messageLbl.setHorizontalAlignment(JLabel.CENTER);
		add(messageLbl);

		// place round message
		moveHeaderLbl.setFont(new Font("Lucida Grande", Font.BOLD, 16));
		moveHeaderLbl.setBounds((boardWidth - moveWidth) / 2, boardHeight - moveHeight, moveWidth, moveHeight);
		moveHeaderLbl.setHorizontalAlignment(JLabel.CENTER);
		add(moveHeaderLbl);

		// Place error message
		errorLbl.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		errorLbl.setForeground(Color.RED);
		errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
		errorLbl.setBounds((boardWidth - errorWidth) / 2, boardHeight - moveHeight - messageHeight - errorHeight,
				errorWidth, errorHeight);
		add(errorLbl);

		// place tiles
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				TileInView tile = tiles[9 * (col) + row] = new TileInView(firstTileX + row * (tileSize + tileSpace),
						firstTileY + col * (tileSize + tileSpace), tileSize, tileColor);
				add(tile);
				// tile.setBounds(firstTileX + row * (tileSize+tileSpace), firstTileY + col *
				// (tileSize+tileSpace), tileSize, tileSize);
				// tile.setBackground(tileColor);
				tile.setVisible(true);

			}
		}

		// place white walls
		for (int col = 0; col < 10; col++) {
			Canvas wall = walls[col] = new Canvas();
			add(wall);
			wall.setBounds(tileSpace, col * (tileSize + tileSpace) + tileSpace, wallLength, wallWidth);
			wall.setBackground(defaultWhiteWallColor);

		}

		// place black walls
		for (int col = 0; col < 10; col++) {
			Canvas wall = walls[col + 10] = new Canvas();
			add(wall);
			wall.setBounds(wallLength + 9 * tileSize + (11 * tileSpace), col * (tileSize + tileSpace) + tileSpace,
					wallLength, wallWidth);
			wall.setBackground(defaultBlackWallColor);
		}

		// place pawns
		tiles[36].add(whitePawn);
		whitePawn.setBounds(tileSize / 2 - pawnSize / 2, tileSize / 2 - pawnSize / 2, pawnSize, pawnSize);
		tiles[44].add(blackPawn);
		blackPawn.setBounds(tileSize / 2 - pawnSize / 2, tileSize / 2 - pawnSize / 2, pawnSize, pawnSize);

	}



	public void moveWall(int wallID, String dir, int col, int row) {

		Canvas wall = BoardPanel.getWalls()[wallID-1];
		if (dir.equals("Horizontal")) {
			wall.setBounds(firstTileX + (col - 1) * (tileSize + wallWidth),
					firstTileY + (row * (tileSize + wallWidth)) - wallWidth, wallLength, wallWidth);
		}
		if (dir.equals("Vertical")) {
			wall.setBounds(firstTileX + (col * (tileSize + wallWidth)) - wallWidth,
					firstTileY + (row - 1) * (tileSize + wallWidth), wallWidth, wallLength);
		}
	}

	public void setWallColor(int wallID, Color color) {
		Canvas wall = BoardPanel.getWalls()[wallID-1];
		wall.setBackground(color);
	}

	public void bringWallForeground(int wallID) {
		moveToFront((Component) BoardPanel.getWalls()[wallID-1]);
	}

	// To place a pawn on a tile
	public void movePawn(Pawn pawn, TileInView tileInView) {
		TileInView currentTile = (TileInView) pawn.getParent();
		currentTile.remove(pawn);
		tileInView.add(pawn);
		pawn.setVisible(true);
		currentTile.repaint();
		tileInView.repaint();
	}

	// Called when error occurs
	public static void displayError(String errorMessage) {
		error = errorMessage;
		errorLbl.setText(error);
		//System.out.println("Displaying error in BoardPanel: " + error);	
	}

	// Called to clear the error field
	public static void clearError() {
		error = "";
		errorLbl.setText(error);
	}
	
	public static void placeWallsInStock() {
		GamePosition currentPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		List<Wall> whiteWallsInStock = currentPosition.getWhiteWallsInStock();
		List<Wall> blackWallsInStock = currentPosition.getBlackWallsInStock();
		for(Wall w: whiteWallsInStock) {
			int wallIndex = w.getId()-1;
			walls[wallIndex].setBounds(tileSpace, (wallIndex) * (tileSize + tileSpace) + tileSpace, wallLength, wallWidth);
			walls[wallIndex].setBackground(defaultWhiteWallColor);
		}
		for(Wall w: blackWallsInStock) {
			int wallIndex = w.getId()-1;
			walls[wallIndex].setBounds(wallLength + 9 * tileSize + (11 * tileSpace), (wallIndex - 10) * (tileSize + tileSpace) + tileSpace,
							wallLength, wallWidth);
			walls[wallIndex].setBackground(defaultBlackWallColor);	
		}
		
	}

}
