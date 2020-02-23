package ca.mcgill.ecse223.quoridor.controller;

import java.sql.Time;
import java.util.ArrayList;
import ca.mcgill.ecse223.quoridor.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.model.*;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.Game.MoveMode;
import java.util.List;

public class QuoridorController {

	/**
	 * @author Joanna Koo
	 * @return
	 * @throws java.lang.UnsupportedOperationException
	 */
	public static void moveWall() throws java.lang.UnsupportedOperationException {
		throw new java.lang.UnsupportedOperationException();
	}

	/**
	 * @author Joanna Koo
	 * @return
	 * @throws java.lang.UnsupportedOperationException
	 */
	public static boolean dropWall() throws java.lang.UnsupportedOperationException {
		throw new java.lang.UnsupportedOperationException();

	}

	/**
	 * @author Mael
	 * @feature StartNewGame This controller method initializes a new game by
	 *          creating 2 users 2 players and creating a new game with them. This
	 *          new game has the status "Initializing" at first then "Ready to
	 *          Start"
	 */
	public static void initializeGame() {
		// must delete any potential previous games before initializing a new one
		tearDown();

		// create 2 users and 2 players
		// initialize a new game
		new Game(GameStatus.Initializing, MoveMode.PlayerMove,
				QuoridorApplication.getQuoridor());

		// all initializing steps are done, game is ready to start
		QuoridorApplication.getQuoridor().getCurrentGame().setGameStatus(GameStatus.ReadyToStart);

		// throw new UnsupportedOperationException();
	}

	/**
	 * @author Mael
	 * @feature StartNewGame This controller method creates a new game by creating 2
	 *          users 2 players and initializing the board. This new game has the
	 *          status "Ready to Start"
	 */
	public static void readyToStartGame() {
		// must delete any potential previous games before initializing a new one
		tearDown();

		// board must be created
		initQuoridorAndBoard();

		// create 2 users and 2 players
		ArrayList<Player> players = createUsersAndPlayers("userA", "userB");

		// game is ready to start
		new Game(GameStatus.ReadyToStart, MoveMode.PlayerMove,
				QuoridorApplication.getQuoridor());

		// initialize board by creating walls for each player and placing them at the
		// correct tile
		initializeBoard();

		// throw new UnsupportedOperationException();
	}

	/**
	 * @author Mael
	 * @feature StartNewGame This controller method starts the clock for the white
	 *          player
	 */
	public static void whiteStartClock() {
		// Time clock = new Time(System.currentTimeMillis());

		throw new UnsupportedOperationException();
	}

	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer This controller method sets the player's clock
	 *          to running
	 * @param player
	 */
	public static void playerStartClock(Player player) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer This controller method sets the player's clock
	 *          to stopped
	 * @param player
	 */
	public static void playerStopClock(Player player) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer This controller method waits for the player to
	 *          complete his move then stops his clock Then it's the other player's
	 *          turn
	 * @param player
	 */
	public static void playerCompletesHisMove(Player player) {
		// determine who is the other player
		Player otherPlayer = null;
		Player whitePlayer = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();
		Player blackPlayer = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();

		if (player == whitePlayer)
			otherPlayer = blackPlayer;
		else if (player == blackPlayer)
			otherPlayer = whitePlayer;

		// When Player "<player>" completes his move

		// Then The user interface shall be showing it is "<other>" turn
		// GUI

		// And The clock of "<player>" shall be stopped
		playerStopClock(player);

		// And The clock of "<other>" shall be running
		playerStartClock(otherPlayer);

		// And The next player to move shall be "<other>"
		QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().setPlayerToMove(otherPlayer);

		throw new UnsupportedOperationException();

	}

	/**
	 * @author ZhuzhenLi
	 * 
	 *         This method sets total thinking time for both white and black players
	 *         of the board game
	 * 
	 * @param totalthinkingTime: The total thinking time for each player game The
	 *                           current game of Quoridor Application
	 * 
	 * @throws UnsupportedOperationException
	 * @return null
	 */
	public static void setTotalThinkingTime(Time totalthinkingTime, Game game) throws UnsupportedOperationException {
		// throw new UnsupportedOperationException();

		if (totalthinkingTime == null) {
			throw new UnsupportedOperationException("The thinkingTime does not exist.");
		}

		if (game == null) {
			throw new UnsupportedOperationException("The game does not exist.");
		}

		Player whitePlayer = game.getWhitePlayer();
		Player blackPlayer = game.getBlackPlayer();

		// set white player thinking time
		if (whitePlayer == null) {
			throw new UnsupportedOperationException("The whitePlayer does not exist.");
		}

		try {
			whitePlayer.setRemainingTime(totalthinkingTime);
		} catch (RuntimeException e) {
			throw new UnsupportedOperationException(e.getMessage());
		}

		// set black player thinking time
		if (blackPlayer == null) {
			throw new UnsupportedOperationException("The blackPlayer does not exist.");
		}

		try {
			blackPlayer.setRemainingTime(totalthinkingTime);
		} catch (RuntimeException e) {
			throw new UnsupportedOperationException(e.getMessage());
		}

	}

	/**
	 * @author ZhuzhenLi
	 * 
	 *         This method initializes a Quoridor board in its initial position;
	 *         sets the pawn of both white and black player at their initial
	 *         position; initializes the stock of walls for both white and black
	 *         players; sets white as next player to move; starts counting down
	 *         clock for the white player;
	 * 
	 * @param quoridor The Quoridor Application which initialize Board for
	 * @throws UnsupportedOperationException
	 * @return null
	 */
	public static void initializeBoard(Quoridor quoridor) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
		/*
		 * if (quoridor == null) { throw new
		 * UnsupportedOperationException("The quoridor does not exist."); }
		 * 
		 * // add tiles for board Board board = new Board(quoridor); try { for (int i =
		 * 1; i <= 9; i++) { // rows for (int j = 1; j <= 9; j++) { // columns
		 * board.addTile(i, j); } } }catch(RuntimeException e) { throw new
		 * UnsupportedOperationException(e.getMessage()); }
		 * 
		 * 
		 * // create walls for each player of the current running game Game game =
		 * quoridor.getCurrentGame();
		 * 
		 * Player player1 = game.getWhitePlayer(); Player player2 =
		 * game.getBlackPlayer();
		 * 
		 * if (player1 == null) { throw new
		 * UnsupportedOperationException("The game does not have a white player."); } if
		 * (player2 == null) { throw new
		 * UnsupportedOperationException("The game does not have a black player."); }
		 * Player[] players = { player1, player2 }; // Create all walls. Walls with
		 * lower ID belong to player1, the second half belongs to player2 for (int i =
		 * 0; i < 2; i++) { for (int j = 0; j < 10; j++) { new Wall(i * 10 + j,
		 * players[i]); } }
		 * 
		 * // set player 1 and playe2 starting positions // There are total 36 tiles in
		 * the first four rows and // indexing starts from 0 -> tiles with indices 36
		 * and 36+8=44 are the starting positions Tile player1StartPos =
		 * board.getTile(36); Tile player2StartPos = board.getTile(44);
		 * 
		 * PlayerPosition player1Position = new PlayerPosition(player1,
		 * player1StartPos); PlayerPosition player2Position = new
		 * PlayerPosition(player2, player2StartPos);
		 * 
		 * // set game position and the first player GamePosition gamePosition = new
		 * GamePosition(0, player1Position, player2Position, player1, game);
		 * 
		 * 
		 * // Add the walls as in stock for both players for (int j = 0; j < 10; j++) {
		 * Wall wall = Wall.getWithId(j); gamePosition.addWhiteWallsInStock(wall); } for
		 * (int j = 0; j < 10; j++) { Wall wall = Wall.getWithId(j + 10);
		 * gamePosition.addBlackWallsInStock(wall); }
		 * 
		 * game.setCurrentPosition(gamePosition);
		 * 
		 * 
		 * // set white player to move
		 * game.getCurrentPosition().setPlayerToMove(player1);
		 * 
		 * // start count down clock for white player
		 */

	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Creates a user with the username given as the argument. Throws an
	 *         exception if the username is not valid or if a user with the same
	 *         username already exists. (Associated to ProvideSelectUsername Gherkin
	 *         feature)
	 * 
	 * @param username The name of the user
	 * @throws InvalidInputException
	 */
	public static User createUser(String username) throws IllegalArgumentException {
		Quoridor quoridor = QuoridorApplication.getQuoridor(); 
		String error = "";
		User user = null;
		try { 
			checkValidUsername(username, quoridor); 
			user = new User(username, quoridor);
		} 
		catch(RuntimeException e) {
			error += e.getMessage();
			if(error.length()>0) {
				throw new IllegalArgumentException(error);
			}
		}
		return user;
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Assigns the player with the given username to the given player.
	 *         Throws an exception if one of the parameters are invalid or if the
	 *         assignment is not allowed. (Associated to ProvideSelectUsername
	 *         Gherkin feature)
	 * 
	 * @param username
	 * @param player
	 *
	 */
	public static void assignUserToPlayer(User user, Player player) {
		Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		GamePosition currentPosition = currentGame.getCurrentPosition();
		player.setUser(user);
		if(currentGame.getWhitePlayer().equals(player)) { //update next player to move
			currentPosition.setPlayerToMove(currentGame.getBlackPlayer());
		}
		else currentPosition.setPlayerToMove(currentGame.getWhitePlayer());
		
		return;
	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 * 
	 *             creates a wall for the player to grab
	 * @throws UnsupportedOperationException
	 * @returns null
	 */
	public static void CreateWall() {
		throw new UnsupportedOperationException();
	}

	// public static Wall wall =
	// QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove().getWall(1);
	/**
	 * @author Mahroo Rahman
	 * @param direction, row and column
	 * 
	 *                   The wall covers two tiles so either two rows and one column
	 *                   or one row and two columns of the board so we keep a row
	 *                   and column as pivot over which the wall flips. The user
	 *                   passes the current direction of the wall, the row number
	 *                   and the column number. The controller changes the direction
	 *                   by 90 degrees. So, if the current direction is horizontal
	 *                   it will become vertical and if vertical then becomes
	 *                   horizontal. the row and column remains same.
	 * @throws UnsupportedOperationException
	 * @returns null
	 */

	public static void flipWall(String direction, int row, int column) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Asserts that a given pawn position is valid on the board. Returns
	 *         true if valid, and false if invalid. (Associated to ValidatePosition
	 *         Gherkin feature)
	 * 
	 * @param row The row position to validate.
	 * @param col The column position to validate.
	 * 
	 * @return isValid
	 */
	public static boolean validatePawnPosition(int row, int col) {
		if(row < 1 || row > 9 || col < 1 || col > 9) {
			return false;
		}
		else return true;
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Asserts that given wall position is valid on the board. Returns true
	 *         if valid, and false if invalid. (Associated to ValidatePosition
	 *         Gherkin feature)
	 * 
	 * @param row The row position to validate.
	 * @param col The column position to validate.
	 * @param dir Wall direction to validate.
	 * 
	 * @return isValid
	 */
	public static boolean validateWallPosition(int row, int col, Direction dir) {
		if(col < 1 || col > 8 || row < 1 || row > 8) {
			return false;
		}
		else return true;
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Asserts that no walls on the board are overlapping. Returns true if
	 *         no walls are overlapping, returns false otherwise.
	 * 
	 * @return isValid
	 */
	public static boolean validateOverlappingWalls() { /* Assumes any wall on board has corresponding wall move */
		
		
		//from left to right
		GamePosition currentPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		List<Wall> wallsOnBoard  = new ArrayList<Wall>();
		wallsOnBoard.addAll(currentPosition.getBlackWallsOnBoard());
		wallsOnBoard.addAll(currentPosition.getWhiteWallsOnBoard());
			
		List<Wall> wallsByRow;
		List<Wall> wallsByCol;
		
		Wall currentWall;
		
		for(int col = 1; col <= 8 ; col++) {
			wallsByCol = getWallsByCol(wallsOnBoard, col);
			for(int row = 1; row <= 8; row++) {
				
				//check same coord
				wallsByRow = getWallsByRow(wallsByCol, row);
				if(wallsByRow.size() == 0) { 
					continue; //no wall at this coord
				}
				if(wallsByRow.size() > 1) {
					return false; //Many walls on same coordinate
				}
				
				currentWall = wallsByRow.get(0);

				//check vertical overlap
				if(currentWall.getMove().getWallDirection().equals(Direction.Vertical)) {
					if(hasVerticalWallAbove(wallsByCol, currentWall)) {
						return false;
					}
				}
				//check horizontal overlap
				else {
					if(hasHorizontalWallToRight(getWallsByRow(wallsOnBoard, row), currentWall)) {
						return false;
					}
				}	
			}
			
		}
		return true;
	}

	/**
	 * This method checks if a game file with a given file name exists in the file
	 * system.
	 * 
	 * 
	 * @author Hao Shu
	 * @param fileName name of the file to check
	 * @return true if the file exists, false if not exist
	 * 
	 * 
	 */
	public static boolean checkExistingFile(String fileName) {

		throw new UnsupportedOperationException();
	}

	/**
	 * This method enables user to delete a saved game with a given file name in the
	 * file system.
	 * 
	 * @author Hao Shu
	 * @param fileName name of the file to delete
	 * @return void
	 * 
	 */

	public static void deleteFile(String fileName) {

		throw new UnsupportedOperationException();
	}

	/**
     * This method enables user to save game with a file name in the file system.
     * 
     * @author Hao Shu
     * @param game current game data to save
     * @param fileName name of the file to save
     * @return void
     */

    public static void saveFile(Game game, String fileName) {

        throw new UnsupportedOperationException();
    }


	/**
	 * This method enables user to overwrite a existing game file with a new game
	 * file when they have the same file name in the file system.
	 * 
	 * If the input is true, the file will be overwritten. If the input is false,
	 * the file will not be overwritten.
	 * 
	 * @author Hao Shu
	 * @param overwriteOption
	 * @return void
	 */

	public static void overwriteExistingFile(boolean overwriteOption) {

		throw new UnsupportedOperationException();
	}

	/**
	 * This method enables user to load a saved game with a given file name in the
	 * file system.
	 * 
	 * @author Hao Shu
	 * @param fileName name of the file to load
	 * @return void
	 */

	public static void loadFile(String fileName) {

		throw new UnsupportedOperationException();
	}

	/**
	 * This method checks if the position to load is valid
	 * 
	 * @author Hao Shu
	 * @param void
	 * @return false if the position is invalid, true if the position to load is
	 *         valid
	 */
	public static boolean validateLoadPosition() {

		throw new UnsupportedOperationException();
	}

	// ***********************************************
	// Private methods
	// ***********************************************
	
	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Throw an error if the username has an invalid format.
	 * 
	 * @param username A username
	 * @param quoridor A quoridor app
	 * 
	 */
	private static void checkValidUsername(String username, Quoridor quoridor) throws IllegalArgumentException {
		String error = "";
		//check empty or null
		if(username == null || username.equals("")) {
			error += "The username cannot be empty!";
		}	
		for(User u: QuoridorApplication.getQuoridor().getUsers()) {
			if(u.getName().contentEquals(username)) {
				error+= "A user with username" + username + " already exists!";
			}
		}
		
		if(error.length() > 0) {
			throw new IllegalArgumentException(error);
		}
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Returns a list of walls placed the specified row of the board.
	 *         (Associated to ValidatePosition Gherkin feature)
	 * 
	 * @param row A row on the board.
	 * 
	 * @return walls A list of walls
	 */
	private static List<Wall> getWallsByRow(List<Wall> walls, int row) {
		List<Wall> wallsOnRow = new ArrayList<Wall>();
		for(Wall w: walls) {
			if(w.getMove().getTargetTile().getRow() == row) {
				wallsOnRow.add(w);
			}
		}
		return wallsOnRow;
 	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Returns a list of walls placed the specified column of the board.
	 *         (Associated to ValidatePosition Gherkin feature)
	 * 
	 * @param col A column on the board.
	 * 
	 * @return walls A list of walls
	 */
	private static List<Wall> getWallsByCol(List<Wall> walls, int col) {
		List<Wall> wallsOnCol = new ArrayList<Wall>();
		for(Wall w: walls) {
			if(w.getMove().getTargetTile().getColumn() == col) {
				wallsOnCol.add(w);
			}
		}
		return wallsOnCol;
 	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Returns a list of walls placed the specified column of the board.
	 *         (Associated to ValidatePosition Gherkin feature)
	 * 
	 * @param row A row on the board.
	 * @param col A column on the board.
	 * 
	 * @return wall A wall
	 */
	private static Wall getWallByRowCol(List<Wall> walls, int row, int col) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Returns true if there is a vertical wall above the target wall. Returns false otherwise.
	 * 
	 * @param walls The list of walls on the board.
	 * @param wall The target wall.
	 * 
	 * @return boolean true of false
	 */
	private static boolean hasVerticalWallAbove(List<Wall> walls, Wall wall) {
		Tile tileOfWall = wall.getMove().getTargetTile();
		Board board = QuoridorApplication.getQuoridor().getBoard();
		int tileIndex = board.indexOfTile(tileOfWall);
		Tile tileAbove = null;
		//Tile index 0 is at (row, col) = (0,0). tileIndex = (row-1)*9+(col-1) 
		if(tileIndex >= 9*7) { //Upmost wall position. There cannot be any walls above.
			return false;
		}
		tileAbove = board.getTile(tileIndex + 9);
		for(Wall w: walls) {
			if(w.getMove().getTargetTile().equals(tileAbove) && w.getMove().getWallDirection().equals(Direction.Vertical)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @author Hugo Parent-Pothier
	 * 
	 *         Returns true if there is a horizontal wall to the right the target wall. Returns false otherwise.
	 * 
	 * @param walls The list of walls on the board.
	 * @param wall The target wall.
	 * 
	 * @return boolean true of false
	 */
	private static boolean hasHorizontalWallToRight(List<Wall> walls, Wall wall) {
		Tile tileOfWall = wall.getMove().getTargetTile();
		Board board = QuoridorApplication.getQuoridor().getBoard();
		int tileIndex = board.indexOfTile(tileOfWall);
		Tile tileToRight;
		//Tile index 0 is at (row, col) = (0,0). tileIndex = (row-1)*9+(col-1) 
		if(tileIndex % 9 == 7) { //Rightmost wall position.There cannot be any walls to the right.
			return false;
		}
		tileToRight = board.getTile(tileIndex + 1);
		for(Wall w: walls) {
			if(w.getMove().getTargetTile().equals(tileToRight) && w.getMove().getWallDirection().equals(Direction.Horizontal)) {
				return true;
			}
		}
		return false;
	}
	
	

	private static ArrayList<Player> createUsersAndPlayers(String userName1, String userName2) {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		User user1 = quoridor.addUser(userName1);
		User user2 = quoridor.addUser(userName2);

		int thinkingTime = 180;

		// Players are assumed to start on opposite sides and need to make progress
		// horizontally to get to the other side
		// @formatter:off
		/*
		 * __________ | | | | |x-> <-x| | | |__________|
		 * 
		 */
		// @formatter:on
		Player player1 = new Player(new Time(thinkingTime), user1, 9, Direction.Horizontal);
		Player player2 = new Player(new Time(thinkingTime), user2, 1, Direction.Horizontal);

		Player[] players = { player1, player2 };

		// Create all walls. Walls with lower ID belong to player1,
		// while the second half belongs to player 2
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 10; j++) {
				new Wall(i * 10 + j, players[i]);
			}
		}

		ArrayList<Player> playersList = new ArrayList<Player>();
		playersList.add(player1);
		playersList.add(player2);

		return playersList;
	}

	private static void initQuoridorAndBoard() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Board board = new Board(quoridor);
		// Creating tiles by rows, i.e., the column index changes with every tile
		// creation
		for (int i = 1; i <= 9; i++) { // rows
			for (int j = 1; j <= 9; j++) { // columns
				board.addTile(i, j);
			}
		}
	}

	private static void initializeBoard() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Game game = quoridor.getCurrentGame();

		// There are total 36 tiles in the first four rows and
		// indexing starts from 0 -> tiles with indices 36 and 36+8=44 are the starting
		// positions
		Tile player1StartPos = quoridor.getBoard().getTile(36);
		Tile player2StartPos = quoridor.getBoard().getTile(44);
		PlayerPosition player1Position = new PlayerPosition(game.getWhitePlayer(), player1StartPos);
		PlayerPosition player2Position = new PlayerPosition(game.getBlackPlayer(), player2StartPos);

		GamePosition gamePosition = new GamePosition(0, player1Position, player2Position, game.getWhitePlayer(), game);

		// Add the walls as in stock for the players
		for (int j = 0; j < 10; j++) {
			Wall wall = Wall.getWithId(j);
			gamePosition.addWhiteWallsInStock(wall);
		}
		for (int j = 0; j < 10; j++) {
			Wall wall = Wall.getWithId(j + 10);
			gamePosition.addBlackWallsInStock(wall);
		}

		game.setCurrentPosition(gamePosition);

	}

	private static void tearDown() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		// Avoid null pointer for step definitions that are not yet implemented.
		if (quoridor != null) {
			quoridor.delete();
			quoridor = null;
		}
		for (int i = 0; i < 20; i++) {
			Wall wall = Wall.getWithId(i);
			if (wall != null) {
				wall.delete();
			}
		}
	}

}