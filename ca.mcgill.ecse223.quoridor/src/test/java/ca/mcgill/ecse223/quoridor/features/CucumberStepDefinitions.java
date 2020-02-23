package ca.mcgill.ecse223.quoridor.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.mcgill.ecse223.quoridor.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.InvalidInputException;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import ca.mcgill.ecse223.quoridor.model.*;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.Game.MoveMode;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;

public class CucumberStepDefinitions {

	private String error = "";
	// for position validation
	private int col;
	private int row;
	private Direction dir;
	private boolean validatingOverlappingWalls;
	private boolean validatingPawn;
	private boolean validatingWall;
	private boolean isValid = false;
	private boolean overwriteOption;

	// ***********************************************
	// Background step definitions
	// ***********************************************
	@Given("^The game is not running$")
	public void theGameIsNotRunning() {
		initQuoridorAndBoard();
		createUsersAndPlayers("user1", "user2");
	}

	@Given("^The game is running$")
	public void theGameIsRunning() {
		initQuoridorAndBoard();
		ArrayList<Player> createUsersAndPlayers = createUsersAndPlayers("user1", "user2");
		createAndStartGame(createUsersAndPlayers);
	}

	@And("^It is my turn to move$")
	public void itIsMyTurnToMove() throws Throwable {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Player currentPlayer = quoridor.getCurrentGame().getWhitePlayer();
		QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().setPlayerToMove(currentPlayer);
	}

	@Given("The following walls exist:")
	public void theFollowingWallsExist(io.cucumber.datatable.DataTable dataTable) {
		validatingOverlappingWalls = true;
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		List<Map<String, String>> valueMaps = dataTable.asMaps();
		// keys: wrow, wcol, wdir
		Player[] players = { quoridor.getCurrentGame().getWhitePlayer(), quoridor.getCurrentGame().getBlackPlayer() };
		int playerIdx = 0;
		int wallIdxForPlayer = 0;
		for (Map<String, String> map : valueMaps) {
			Integer wrow = Integer.decode(map.get("wrow"));
			Integer wcol = Integer.decode(map.get("wcol"));
			// Wall to place
			// Walls are placed on an alternating basis wrt. the owners
			// Wall wall = Wall.getWithId(playerIdx * 10 + wallIdxForPlayer);
			Wall wall = players[playerIdx].getWall(wallIdxForPlayer); // above implementation sets wall to null

			String dir = map.get("wdir");

			Direction direction;
			switch (dir) {
			case "horizontal":
				direction = Direction.Horizontal;
				break;
			case "vertical":
				direction = Direction.Vertical;
				break;
			default:
				throw new IllegalArgumentException("Unsupported wall direction was provided");
			}
			new WallMove(0, 1, players[playerIdx], quoridor.getBoard().getTile((wrow - 1) * 9 + wcol - 1),
					quoridor.getCurrentGame(), direction, wall);
			if (playerIdx == 0) {
				quoridor.getCurrentGame().getCurrentPosition().removeWhiteWallsInStock(wall);
				quoridor.getCurrentGame().getCurrentPosition().addWhiteWallsOnBoard(wall);
			} else {
				quoridor.getCurrentGame().getCurrentPosition().removeBlackWallsInStock(wall);
				quoridor.getCurrentGame().getCurrentPosition().addBlackWallsOnBoard(wall);
			}
			wallIdxForPlayer = wallIdxForPlayer + playerIdx;
			playerIdx++;
			playerIdx = playerIdx % 2;
		}
		System.out.println();

	}

	@Given("^A new game is initializing$")
	public void aNewGameIsInitializing() throws Throwable {
		initQuoridorAndBoard();
		ArrayList<Player> players = createUsersAndPlayers("user1", "user2");
		new Game(GameStatus.Initializing, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());
	}

	// ***********************************************
	// Scenario and scenario outline step definitions
	// ***********************************************

	/**
	 * @author Hugo Parent-Pothier
	 */
	@Given("A game position is supplied with pawn coordinate {int}:{int}")
	public void a_game_position_is_supplied_with_pawn_coordinate(Integer row, Integer col) {
		validatingPawn = true;
		// not in model
		this.row = row;
		this.col = col;
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 */
	@When("Validation of the position is initiated")
	public void validation_of_the_position_is_initiated() {
		//cases: pawn position, wall position, overlapping walls
		if(validatingPawn) { //validate pawn
			System.out.println("Validating pawn position: "+ row + col);
			isValid = QuoridorController.validatePawnPosition(row, col);
			return;
		}
		if(validatingWall) { //validate wall
			System.out.println("Validating wall position: "+ row + col + dir);
			isValid = QuoridorController.validateWallPosition(row, col, dir);
			return;
		}
		if(validatingOverlappingWalls) {
			isValid = QuoridorController.validateOverlappingWalls();
			return;
		}
		return;	
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param string
	 */
	@Then("The position shall be {string}")
	public void the_position_shall_be(String string) {
		if (isValid) {
			assertEquals(string, "ok");
		} else {
			assertEquals(string, "error");
		}
	}
	
	/**
	 * @author Hugo Parent-Pothier
	 */
	@Given("A game position is supplied with wall coordinate {int}:{int}-{string}")
	public void a_game_position_is_supplied_with_wall_coordinate(int row, int col, String dir) {
		validatingWall = true;
		this.row = row;
		this.col = col;
		if (dir.equals("horizontal")) {
			this.dir = Direction.Horizontal;
		} else
			this.dir = Direction.Vertical;
		
	    //Place wall (owned by white player)
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Wall wall = quoridor.getCurrentGame().getWhitePlayer().getWall(0); 
		new WallMove(0, 1, quoridor.getCurrentGame().getWhitePlayer(), quoridor.getBoard().getTile((row - 1) * 9 + col - 1),
		quoridor.getCurrentGame(), this.dir, wall);
		quoridor.getCurrentGame().getCurrentPosition().removeWhiteWallsInStock(wall);
		quoridor.getCurrentGame().getCurrentPosition().addWhiteWallsOnBoard(wall);


		
	/*	
		Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		GamePosition currentPosition = currentGame.getCurrentPosition();

		Wall wall = currentPosition.getWhiteWallsInStock(0);
		currentPosition.removeWhiteWallsInStock(wall);
		currentPosition.addWhiteWallsOnBoard(wall);
		currentGame.addMove(new WallMove(currentGame.numberOfMoves(), currentGame., Player aPlayer, Tile aTargetTile, Game aGame, Direction aWallDirection, Wall aWallPlaced));
	*/
	}

	/**
	 * @author Hugo Parent-Pothier
	 */
	@Then("The position shall be valid")
	public void the_position_shall_be_valid() {
		assertTrue(isValid);
	}

	/**
	 * @author Hugo Parent-Pothier
	 */
	@Then("The position shall be invalid")
	public void the_position_shall_be_invalid() {
		assertFalse(isValid);
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param color
	 */
	@Given("Next player to set user name is {string}")
	public void nextPlayerToSetUserName(String color) {
		Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		GamePosition currentPosition = currentGame.getCurrentPosition();
		Player whitePlayer = currentGame.getWhitePlayer();
		Player blackPlayer = currentGame.getBlackPlayer();
		if(color.equals("white")) {
			currentPosition.setPlayerToMove(whitePlayer);
			return;
		}
		if(color.equals("black")) {
			currentPosition.setPlayerToMove(blackPlayer);
			return;
		}
		fail(); //should not reach this point
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param username
	 * @throws InvalidInputException
	 */
	@And("There is existing user {string}")
	public void thereIsExistingUser(String username) {
		QuoridorApplication.getQuoridor().addUser(username);
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param username
	 * @throws InvalidInputException
	 */
	@When("The player selects existing {string}")
	public void playerSelectsExistingUser(String username) {
		GamePosition currentPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		QuoridorController.assignUserToPlayer(User.getWithName(username), currentPosition.getPlayerToMove());
	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */
	@Given("I have more walls on stock")
	public void i_have_more_walls_on_stock() {
		// Write code here that turns the phrase above into concrete actions
		Quoridor quoridor = QuoridorApplication.getQuoridor();

		assertNotEquals(0, quoridor.getCurrentGame().getCurrentPosition().numberOfWhiteWallsInStock());

	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */
	@When("I try to grab a wall from my stock")
	public void iTryToGrabAwallFromMyStock() {
		// Write code here that turns the phrase above into concrete actions

		QuoridorController.CreateWall();

	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */

	@Then("A wall move candidate shall be created at initial position")
	public void a_wall_move_candidate_shall_be_created_at_initial_position() {
		// Write code here that turns the phrase above into concrete actions
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		// boolean has =true;
		assertTrue(quoridor.getCurrentGame().hasWallMoveCandidate());
		if (quoridor.getCurrentGame().hasWallMoveCandidate()) {

			Wall wall = quoridor.getCurrentGame().getWallMoveCandidate().getGame().getCurrentPosition()
					.getWhiteWallsOnBoard(0);
			assertEquals(0, quoridor.getCurrentGame().getWallMoveCandidate().getGame().getCurrentPosition()
					.indexOfWhiteWallsOnBoard(wall));
		}

	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */

	@And("I shall have a wall in my hand over the board")
	public static void i_shall_have_a_wall_in_my_hand_over_the_board() {
		throw new cucumber.api.PendingException();

	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */
	@And("The wall in my hand shall disappear from my stock")
	public static void the_wall_in_my_hand_shall_disappear_from_my_stock() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		assertEquals(9, quoridor.getCurrentGame().getWallMoveCandidate().getGame().getCurrentPosition()
				.numberOfWhiteWallsInStock());
	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */
	@Given("I have no more walls on stock")
	public static void i_have_no_more_walls_on_stock() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Game game = quoridor.getCurrentGame();
		//int walls = game.getCurrentPosition().numberOfWhiteWallsInStock();
		// assertEquals(0, walls);
		//walls = 0;
		//Wall wall = game.getWhitePlayer().getWall(0);
		//game.getWhitePlayer().removeWall(wall);
		for(int i =0; i<game.getWallMoveCandidate().getGame().getCurrentPosition()
				.numberOfWhiteWallsInStock(); i++) 
		{
			Wall wall = game.getWhitePlayer().getWall(i);
			game.getWhitePlayer().removeWall(wall);
		}
	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */
	@Then("I shall be notified that I have no more walls")
	public void iShallBeNotifiedThatiHaveNoMoreWalls() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		assertEquals(0, quoridor.getCurrentGame().getWallMoveCandidate().getGame().getCurrentPosition()
				.numberOfWhiteWallsInStock(), "I have no more walls");
	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */

	@And("I shall have no walls in my hand")
	public void i_shall_have_no_walls_in_my_hand() {
		throw new cucumber.api.PendingException();
		// Quoridor quoridor = QuoridorApplication.getQuoridor();
		// assertEquals(0,
		// quoridor.getCurrentGame().getWallMoveCandidate().getGame().getCurrentPosition().numberOfWhiteWallsOnBoard());
	}

	/**
	 * @author Mahroo Rahman
	 * @param string, int1, int2
	 */
	@Given("A wall move candidate exists with {string} at position \\({int}, {int})")
	public void a_wall_move_candidate_exists_with_at_position(String string, Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		WallMove wallmovecandidate = quoridor.getCurrentGame().getWallMoveCandidate();

		/*
		 * String direction =
		 * quoridor.getCurrentGame().getBlackPlayer().getDestination().getDirection().
		 * name(); direction = string; int row =
		 * quoridor.getBoard().getTile(0).getRow(); row = int1; int column =
		 * quoridor.getBoard().getTile(0).getColumn(); column = int2;
		 */
		Board board = quoridor.getBoard();
		//Tile tile = quoridor.getCurrentGame().getCurrentPosition().getWhitePosition().getTile();
		Tile tile = new Tile(int1, int2,board);
		quoridor.getCurrentGame().getCurrentPosition().getWhitePosition().setTile(tile);
		String si = "vertical";
		String s2 = "horizontal";
		

		if (string.equals(si)) {
			wallmovecandidate.getPlayer().getDestination().setDirection(Direction.Vertical);
		} else if (string.equals(s2)) {
			wallmovecandidate.getPlayer().getDestination().setDirection(Direction.Horizontal);
		}

	}

	/**
	 * @author Mahroo Rahman
	 * @param none
	 */
	@When("I try to flip the wall")
	public void i_try_to_flip_the_wall() {
		// Write code here that turns the phrase above into concrete actions
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		QuoridorController.flipWall(quoridor.getCurrentGame().getBlackPlayer().getDestination().getDirection().name(),
				quoridor.getBoard().getTile(0).getRow(), quoridor.getBoard().getTile(0).getColumn());
	}

	/**
	 * @author Mahroo Rahman
	 * @param string
	 */
	@Then("The wall shall be rotated over the board to {string}")
	public void the_wall_shall_be_rotated_over_the_board_to(String string) {
		// Write code here that turns the phrase above into concrete actions
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		assertEquals(string, quoridor.getCurrentGame().getBlackPlayer().getDestination().getDirection().name());
	}

	/**
	 * @author Mahroo Rahman
	 * @param string, int1, int2
	 */
	@And("A wall move candidate shall exist with {string} at position \\({int}, {int})")
	public void a_wall_move_candidate_shall_exist_with_at_position(String string, Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		assertEquals(string, quoridor.getCurrentGame().getBlackPlayer().getDestination().getDirection().name());
		assertEquals(int1, quoridor.getBoard().getTile(0).getRow());
		assertEquals(int2, quoridor.getBoard().getTile(0).getColumn());
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 */
	@Then("The name of player {string} in the new game shall be {string}")
	public void nameOfPlayerInNewGameIs(String color, String username) {
		Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		if (color.equals("white")) {
			assertEquals(currentGame.getWhitePlayer().getUser().getName(), username);
			return;
		}
		if (color.equals("black")) {
			assertEquals(currentGame.getBlackPlayer().getUser().getName(), username);
			return;
		}
		fail(); // should not reach this point
	}

	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param username
	 */
	@And("There is no existing user {string}")
	public void thereIsNoExistingUser(String username) {
		List<User> users = QuoridorApplication.getQuoridor().getUsers();
		for (User u : users) {
			assertNotEquals(username, u.getName());
		}
	}
	
	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param username
	 */
	@When("The player provides new user name: {string}")
	public void thePlayerProvidesNewUserName(String username) {
		
		GamePosition currentPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
		try {
			QuoridorController.createUser(username);
		} catch (RuntimeException e) {
			error = e.getMessage(); // global variable
		}
		QuoridorController.assignUserToPlayer(User.getWithName(username), currentPosition.getPlayerToMove());
	}
	
	
	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param username
	 */
	@Then("The player shall be warned that {string} already exists")
	public void playerWarnedThatUsernameExists(String username) {
		assertEquals(error, "A user with username" + username + " already exists!");
	}

	
	
	/**
	 * @author Hugo Parent-Pothier
	 * 
	 * @param color
	 */
	@And("Next player to set user name shall be {string}")
	public void nextPlayerToSetUserNameShallBe(String color) { 
		Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		GamePosition currentPosition = currentGame.getCurrentPosition();
		if(color.equals("white")) {
			assertEquals(currentPosition.getPlayerToMove(), currentGame.getWhitePlayer());
		}
		if(color.equals("black")) {
			assertEquals(currentPosition.getPlayerToMove(), currentGame.getBlackPlayer());
		}
		fail(); //should not reach this point
	}
	
	

	@Given("The player is located at {int}:{int}")
	public void the_player_is_located_at(Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("There are no {string} walls {string} from the player")
	public void there_are_no_walls_from_the_player(String string, String string2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("The opponent is not {string} from the player")
	public void the_opponent_is_not_from_the_player(String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@When("Player {string} initiates to move {string}")
	public void player_initiates_to_move(String string, String string2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("The move {string} shall be {string}")
	public void the_move_shall_be(String string, String string2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("Player's new position shall be {int}:{int}")
	public void player_s_new_position_shall_be(Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("The next player to move shall become {string}")
	public void the_next_player_to_move_shall_become(String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("There is a {string} wall {string} from the player")
	public void there_is_a_wall_from_the_player(String string, String string2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("My opponent is not {string} from the player")
	public void my_opponent_is_not_from_the_player(String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("The wall candidate is not at the {string} edge of the board")
	public void the_wall_candidate_is_not_at_the_edge_of_the_board(String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@When("I try to move the wall {string}")
	public void i_try_to_move_the_wall(String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("The wall shall be moved over the board to position \\({int}, {int})")
	public void the_wall_shall_be_moved_over_the_board_to_position(Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("The wall candidate is at the {string} edge of the board")
	public void the_wall_candidate_is_at_the_edge_of_the_board(String string) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("I shall be notified that my move is illegal")
	public void i_shall_be_notified_that_my_move_is_illegal() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	

	@Given("I have a wall in my hand over the board")
	public void i_have_a_wall_in_my_hand_over_the_board() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("The wall move candidate with {string} at position \\({int}, {int}) is valid")
	public void the_wall_move_candidate_with_at_position_is_valid(String string, Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@When("I release the wall in my hand")
	public void i_release_the_wall_in_my_hand() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("A wall move shall be registered with {string} at position \\({int}, {int})")
	public void a_wall_move_shall_be_registered_with_at_position(String string, Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("I shall not have a wall in my hand")
	public void i_shall_not_have_a_wall_in_my_hand() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("My move shall be completed")
	public void my_move_shall_be_completed() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("It shall not be my turn to move")
	public void it_shall_not_be_my_turn_to_move() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("The wall move candidate with {string} at position \\({int}, {int}) is invalid")
	public void the_wall_move_candidate_with_at_position_is_invalid(String string, Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("I shall be notified that my wall move is invalid")
	public void i_shall_be_notified_that_my_wall_move_is_invalid() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("It shall be my turn to move")
	public void it_shall_be_my_turn_to_move() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Then("No wall move shall be registered with {string} at position \\({int}, {int})")
	public void no_wall_move_shall_be_registered_with_at_position(String string, Integer int1, Integer int2) {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	@Given("I do not have a wall in my hand")
	public void i_do_not_have_a_wall_in_my_hand() {
		// Write code here that turns the phrase above into concrete actions
		throw new cucumber.api.PendingException();
	}

	

	

	@SuppressWarnings("deprecation")
	@When("{int}:{int} is set as the thinking time")
	public void min_sec_is_set_as_the_thinking_time(int min, int sec) throws InvalidInputException {
		Time thinkingTime = new java.sql.Time(0, min, sec);
		QuoridorController.setTotalThinkingTime(thinkingTime, QuoridorApplication.getQuoridor().getCurrentGame());
	}

	@SuppressWarnings("deprecation")
	@Then("Both players shall have {int}:{int} remaining time left")
	public void both_players_shall_have_remaining_time_left(int min, int sec) throws InvalidInputException {
		Time testThinkingTime = new java.sql.Time(0, min, sec);
		assertEquals(testThinkingTime,
				QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getRemainingTime());
		assertEquals(testThinkingTime,
				QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer().getRemainingTime());
	}
	

	/**
	 * @author ZhuzhenLi 
	 * 
	 * Mapping of Gherkin scenario of feature: initialize board
	 */

	@When("The initialization of the board is initiated")
	public void the_initialization_of_the_board_is_initiated() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
//		ArrayList<Player> createUsersAndPlayers = createUsersAndPlayers("user1", "user2");
//		Game game = new Game(GameStatus.Running, MoveMode.PlayerMove, createUsersAndPlayers.get(0), createUsersAndPlayers.get(1), quoridor);
//		quoridor.setCurrentGame(game);
		QuoridorController.initializeBoard(quoridor);
	}

	@Then("It shall be white player to move")
	public void it_shall_be_white_player_to_move() {
		Player expectedPlayerToMove = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();
		Player actualPlayerToMove = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
				.getPlayerToMove();
		assertEquals(expectedPlayerToMove, actualPlayerToMove);
	}

	@Then("White's pawn shall be in its initial position")
	public void white_s_pawn_shall_be_in_its_initial_position() {
		Tile expectedWhiteplayerStartPos = QuoridorApplication.getQuoridor().getBoard().getTile(36);
		PlayerPosition actualWhitePlayerPosition = QuoridorApplication.getQuoridor().getCurrentGame()
				.getCurrentPosition().getWhitePosition();
		assertEquals(expectedWhiteplayerStartPos, actualWhitePlayerPosition.getTile());
	}

	@Then("Black's pawn shall be in its initial position")
	public void black_s_pawn_shall_be_in_its_initial_position() {
		Tile expectedBlackplayerStartPos = QuoridorApplication.getQuoridor().getBoard().getTile(44);
		PlayerPosition actualBlackPlayerPosition = QuoridorApplication.getQuoridor().getCurrentGame()
				.getCurrentPosition().getBlackPosition();
		assertEquals(expectedBlackplayerStartPos, actualBlackPlayerPosition.getTile());
	}

	@Then("All of White's walls shall be in stock")
	public void all_of_White_s_walls_shall_be_in_stock() {
		int actualWhiteWallsInStock = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate()
				.getGame().getCurrentPosition().numberOfWhiteWallsInStock();
		assertEquals(10, actualWhiteWallsInStock);
	}

	@Then("All of Black's walls shall be in stock")
	public void all_of_Black_s_walls_shall_be_in_stock() {
		int actualBlackWallsInStock = QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate()
				.getGame().getCurrentPosition().numberOfBlackWallsInStock();
		assertEquals(10, actualBlackWallsInStock);
	}

	@Then("White's clock shall be counting down")
	public void white_s_clock_shall_be_counting_down() {
		Time time1 = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getRemainingTime();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Time time2 = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getRemainingTime();
		assertTrue(time1.compareTo(time2) < 0);
	}

	@Then("It shall be shown that this is White's turn")
	public void it_shall_be_shown_that_this_is_White_s_turn() {
		// GUI-related steps
		throw new cucumber.api.PendingException();
	}




//Start New Game Feature
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition runs the controller method that initializes
	 */
	@When ("^A new game is being initialized$")
	public void aNewGameIsBeingInitialized(){
		
		QuoridorController.initializeGame();
					
	}
	
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition makes sure the white player chose a username
	 */
	@And ("^White player chooses a username$")
	public void whitePlayerChoosesAUsername() {
				
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		String username =  game.getWhitePlayer().getUser().getName();
		assertTrue(username != null && !username.isEmpty());//white player chose a name
		
	}
	
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition makes sure the black player chose a username
	 */
	@And ("^Black player chooses a username$")
	public void blackPlayerChoosesAUsername() {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		String username =  game.getBlackPlayer().getUser().getName();
		assertTrue(username != null && !username.isEmpty());//black player chose a name
	}

	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition makes sure the total thinking time
	 * for both players was set 
	 */
	@And ("^Total thinking time is set$")
	public void totalThinkingTimeIsSet() {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		Time time1 = game.getWhitePlayer().getRemainingTime();
		Time time2 = game.getBlackPlayer().getRemainingTime();
		assertTrue(time1 != null && time2 != null);//both players have thinking time set
	}
	
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition makes sure the game is ready to start
	 */
	@Then ("^The game shall become ready to start$")
	public void theGameShallBecomeReadyToStart() {
		Game game = QuoridorApplication.getQuoridor().getCurrentGame();
		//game.setGameStatus(GameStatus.ReadyToStart);
		assertEquals(GameStatus.ReadyToStart, game.getGameStatus());
	}
	
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition runs the controller method that initializes
	 * a new game and sets the status to "Ready to Start"
	 */
	@Given ("^The game is ready to start$")
	public void theGameIsReadyToStart() {
		QuoridorController.readyToStartGame();
	}
	
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition runs the controller method that starts the clock for the white player
	 */
	@When ("^I start the clock$")
	public void iStartTheClock() {
		QuoridorController.whiteStartClock();
	}
	
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition sets the current game's status to "Running"
	 */
	@Then ("^The game shall be running$")
	public void theGameShallBeRunning() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Game game = quoridor.getCurrentGame();	//get current game	
		game.setGameStatus(GameStatus.Running);//game status changed to running
	}
	
	/**
	 * @author Mael
	 * @feature StartNewGame
	 * This step definition makes sure the board has been initialized by
	 * making sure the board has 81 tiles
	 * it's the white player's turn to move
	 * both players have their walls and are at the correct tile
	 */
	@And ("^The board shall be initialized$")
	public void theBoardShallBeInitialized() {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		Game game = quoridor.getCurrentGame();
		//the board has 81 tiles
		assertEquals(81, quoridor.getBoard().numberOfTiles());
		
		//it's the white player's turn to move
		assertEquals(game.getWhitePlayer(), game.getCurrentPosition().getPlayerToMove());
		
		//both players are at the correct tile
		assertEquals(quoridor.getBoard().getTile(36), game.getCurrentPosition().getWhitePosition().getTile());
		assertEquals(quoridor.getBoard().getTile(44), game.getCurrentPosition().getBlackPosition().getTile());
		
		//both players have all their walls
		assertEquals(10, game.getCurrentPosition().numberOfWhiteWallsInStock());
		assertEquals(10, game.getCurrentPosition().numberOfBlackWallsInStock());

	}
	
//Switch Current Player Feature
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This step definition sets the player to move to <player>
	 * @param player
	 */
	@Given ("The player to move is {string}")
	public void thePlayerToMoveIs(String player) {//assuming we receive the player as "BlackPlayer" or "WhitePlayer"
		Player playerToMove = getPlayerFromString(player);//get <player>
		
		//set player to move
		QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().setPlayerToMove(playerToMove);
	}
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This step definition calls the controller method that sets the clock of <player> to running
	 * @param player
	 */
	@And ("The clock of {string} is running")
	public void theClockOfIsRunning(String player) {
		Player playerClockStart = getPlayerFromString(player);//get <player>
		QuoridorController.playerStartClock(playerClockStart);//starts clock for the player
	}
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This step definition calls the controller method that sets the clock of <other> to stopped
	 * @param player
	 */
	@And ("The clock of {string} is stopped")
	public void theClockOfIsStopped(String otherPlayer) {
		Player playerClockStop = getPlayerFromString(otherPlayer);//get <other>
		QuoridorController.playerStopClock(playerClockStop);//stops the clock for the other player
	}
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This step definition runs the controller method that makes a player complete his move
	 * then switches the turns
	 * @param player
	 */
	@When ("Player {string} completes his move")
	public void playerCompletesHisMove(String player) {
		
		Player playerToCompleteMove = getPlayerFromString(player);//get <player>
		QuoridorController.playerCompletesHisMove(playerToCompleteMove);
	}
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This is the step definition that makes sure the interface is showing it's <other> turn
	 * @param player
	 */
	@Then ("The user interface shall be showing it is {word} turn")
	public void theUserInterfaceShallBeShowingItIsTurn(Player player){
		// GUI-related feature -- TODO for later
	}
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This step definition makes sure the clock of <player> is stopped by taking 2 time samples
	 * and making sure they're the same 
	 * @param player
	 */
	@And ("The clock of {string} shall be stopped")
	public void theClockOfShallBeStopped(String player) {
		Player playerClockCheck = getPlayerFromString(player);//get <player>
		
		//take 2 samples of the player's clock to see if it's running
		Time time1  = playerClockCheck.getRemainingTime();
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Time time2  = playerClockCheck.getRemainingTime();

		assertTrue(time1.compareTo(time2) == 0 );// make sure time1 and time2 are the same
		
	}
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This step definition makes sure the clock of <other> is running by taking 2 time samples
	 * and making sure the 2nd is the most recent
	 * @param otherPlayer
	 */
	@And ("The clock of {string} shall be running")
	public void theClockOfShallBe(String otherPlayer) {
		Player playerClockCheck = getPlayerFromString(otherPlayer);//get <other>
		//take 2 samples of the player's clock to see if it's running
		Time time1  = playerClockCheck.getRemainingTime();
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Time time2  = playerClockCheck.getRemainingTime();

		assertTrue(time1.compareTo(time2) < 0 );// make sure time2 is more recent then time1
	
	}
	
	/**
	 * @author Mael
	 * @feature SwitchCurrentPlayer
	 * This step definition makes sure that the next player to move is <other>
	 * @param otherPlayer
	 */
	@And ("The next player to move shall be {string}")
	public void theNextPlayerToMoveShallBe (String otherPlayer) {
		Player playerToMove = getPlayerFromString(otherPlayer);//get <other>
		assertEquals(playerToMove, QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove());
	}
    
	// ***********************************************
    // Start of LoadPosition.feature
    // ***********************************************
	
	
	/**
	   * SavePosition.feature
	   * 
	   * @author Hao Shu
	   * @version 10.12.2019
	   */

	  //
	  //
	  @Given("No file {string} exists in the filesystem")

	  public void no_file_exists_in_the_filesystem(String fileName) {
	    
	    // if this file exists the file system, delete the existing file
	    if (QuoridorController.checkExistingFile(fileName)) {
	      QuoridorController.deleteFile(fileName);
	    }
	    throw new cucumber.api.PendingException();
	  }

	  @When("The user initiates to save the game with name {string}")
	  public void the_user_initiates_to_save_the_game_with_name(String fileName) {

	    QuoridorController.saveFile(QuoridorApplication.getQuoridor().getCurrentGame(),fileName);// save the file in the file system
	    isValid = true;
	    throw new cucumber.api.PendingException();
	  }

	   @Then("A file with {string} shall be created in the filesystem")
	   public void a_file_with_shall_be_created_in_the_filesystem(String fileName) {
	     assertTrue(isValid);
	   throw new cucumber.api.PendingException();
	   }
	  
	  @Given("File {string} exists in the filesystem")
	  public void file_exists_in_the_filesystem(String fileName) {

	    // if this file does not exist the file system, save the file in the file system.
	    if (QuoridorController.checkExistingFile(fileName) == false) {
	      QuoridorController.saveFile(QuoridorApplication.getQuoridor().getCurrentGame(),fileName);// save the file in the file system
	    }
	    throw new cucumber.api.PendingException();
	  }                                  

	  @When("The user confirms to overwrite existing file")
	  public void the_user_confirms_to_overwrite_existing_file() {

	    QuoridorController.overwriteExistingFile(true);// overwrite the file
	    overwriteOption = true;
	    throw new cucumber.api.PendingException();
	  }

	   @Then("File with {string} shall be updated in the filesystem")
	   public void file_with_shall_be_updated_in_the_filesystem(String fileName) {
	   
	     assertTrue(overwriteOption);
	   throw new cucumber.api.PendingException();
	   }
	  
	  @When("The user cancels to overwrite existing file")
	  public void the_user_cancels_to_overwrite_existing_file() {
	    QuoridorController.overwriteExistingFile(false);// cancle to overwrite
	    overwriteOption = false;
	    throw new cucumber.api.PendingException();
	    
	  }

	   @Then("File {string} shall not be changed in the filesystem")
	   public void file_shall_not_be_changed_in_the_filesystem(String fileName) {
	  
	     assertFalse(overwriteOption);
	   throw new cucumber.api.PendingException();
	   }
	  
	   

	      // ***********************************************
	      // Start of LoadPosition.feature
	      // ***********************************************
	  /**
	   * LoadPosition.feature
	   * 
	   * @author Hao Shu
	   * @version 10.12.2019
	   */
	  @When("I initiate to load a saved game {string}")
	  public void i_initiate_to_load_a_saved_game(String fileName) {
	    QuoridorController.loadFile(fileName);
	    throw new cucumber.api.PendingException();
	  }

	  @When("The position to load is valid")
	  public void the_position_to_load_is_valid() {
	    assertTrue(QuoridorController.validateLoadPosition());
	    throw new cucumber.api.PendingException();
	  }

	  @Then("It shall be {string}'s turn")
	  public void it_shall_be_s_turn(String userColor) {
	    String currentColor = "";
	    if (QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove()
	        .hasGameAsWhite()) {
	      currentColor = "white";
	    } else if (QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	        .getPlayerToMove().hasGameAsBlack()) {
	      currentColor = "black";
	    }
	    assertEquals(userColor, currentColor);
	    throw new cucumber.api.PendingException();
	  }

	  @Then("{string} shall be at {int}:{int}")
	  public void shall_be_at(String userColor, Integer row, Integer col) {

	    Integer temp_row;
	    Integer temp_col;
	    if (userColor == "white") {
	      temp_row = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getWhitePosition().getTile().getRow();
	      temp_col = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getWhitePosition().getTile().getColumn();
	    } else {
	      temp_row = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getBlackPosition().getTile().getRow();
	      temp_col = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getBlackPosition().getTile().getColumn();
	    }
	    assertEquals(row, temp_row);
	    assertEquals(col, temp_col);

	    throw new cucumber.api.PendingException();
	  }

	  @Then("{string} shall have a vertical wall at {int}:{int}")
	  public void shall_have_a_vertical_wall_at(String userColor, Integer w_row, Integer w_col) {

	    Direction dir;
	    Integer temp_row;
	    Integer temp_col;
	    if (userColor == "white") {

	      temp_row = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getWhiteWallsOnBoard(0).getMove().getTargetTile().getRow();
	      temp_col = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getWhiteWallsOnBoard(0).getMove().getTargetTile().getColumn();
	      dir = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getWall(0).getMove()
	          .getWallDirection();
	    } else {

	      temp_row = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getBlackWallsOnBoard(0).getMove().getTargetTile().getRow();
	      temp_col = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getBlackWallsOnBoard(0).getMove().getTargetTile().getColumn();
	      dir = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer().getWall(0).getMove()
	          .getWallDirection();
	    }
	    assertEquals(w_row, temp_row);
	    assertEquals(w_col, temp_col);
	    assertEquals(Direction.Vertical, dir);

	    throw new cucumber.api.PendingException();
	  }

	  @Then("{string} shall have a horizontal wall at {int}:{int}")
	  public void shall_have_a_horizontal_wall_at(String userColor, Integer w_row, Integer w_col) {

	    Direction dir;
	    Integer temp_row;
	    Integer temp_col;
	    if (userColor == "white") {

	      temp_row = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getWhiteWallsOnBoard(0).getMove().getTargetTile().getRow();
	      temp_col = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getWhiteWallsOnBoard(0).getMove().getTargetTile().getColumn();
	      dir = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getWall(0).getMove()
	          .getWallDirection();
	    } else {

	      temp_row = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getBlackWallsOnBoard(0).getMove().getTargetTile().getRow();
	      temp_col = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
	          .getBlackWallsOnBoard(0).getMove().getTargetTile().getColumn();
	      dir = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer().getWall(0).getMove()
	          .getWallDirection();
	    }
	    assertEquals(w_row, temp_row);
	    assertEquals(w_col, temp_col);
	    assertEquals(Direction.Horizontal, dir);

	    throw new cucumber.api.PendingException();
	  }

	  @Then("Both players shall have {int} in their stacks")
	  public void both_players_shall_have_in_their_stacks(Integer remainWalls) {
	    Integer whiteRemainWalls =
	        QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getWalls().size();
	    Integer blackRemainWalls =
	        QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer().getWalls().size();

	    assertEquals(remainWalls, whiteRemainWalls);
	    assertEquals(remainWalls, blackRemainWalls);

	    throw new cucumber.api.PendingException();
	  }

	  @When("The position to load is invalid")
	  public void the_position_to_load_is_invalid() {
	    assertFalse(QuoridorController.validateLoadPosition());
	    throw new cucumber.api.PendingException();
	  }

	  @Then("The load shall return an error")
	  public void the_load_shall_return_an_error() {
	    assertEquals(error, "The position to load is invalid!");
	    throw new cucumber.api.PendingException();
	  }

	  // ***********************************************
	  // End of LoadPosition.feature
	  // ***********************************************

	// ***********************************************
	// Clean up
	// ***********************************************

	// After each scenario, the test model is discarded
	@After
	public void tearDown() {
		// reset error message and flags              
		error = "";
		validatingOverlappingWalls = false;
		validatingPawn = false;
		validatingWall = false;

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

	// ***********************************************
	// Extracted helper methods
	// ***********************************************

	// Place your extracted methods below
	
	/**
	 * @author Mael
	 * This method returns the blackPlayer or whitePlayer for the current game based on inputed string
	 * called in steps from the SwitchCurrentGame feature
	 * @param playerString
	 * @return
	 */
	private Player getPlayerFromString(String playerString) {
		Player player = null;
		//determine if player is blackPlayer or whitePlayer		
		if(playerString.contains("white"))//player to move is the white player
			player = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();
			
		else if(playerString.contains("black"))//player to move is the black player
			player = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();

		return player;
	}

	private void initQuoridorAndBoard() {
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

	private ArrayList<Player> createUsersAndPlayers(String userName1, String userName2) {
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

	private void createAndStartGame(ArrayList<Player> players) {
		Quoridor quoridor = QuoridorApplication.getQuoridor();
		// There are total 36 tiles in the first four rows and
		// indexing starts from 0 -> tiles with indices 36 and 36+8=44 are the starting
		// positions
		Tile player1StartPos = quoridor.getBoard().getTile(36);
		Tile player2StartPos = quoridor.getBoard().getTile(44);

		
		Game game = new Game(GameStatus.Running, MoveMode.PlayerMove, quoridor);
		game.setWhitePlayer(players.get(0));
		game.setBlackPlayer(players.get(1));


		PlayerPosition player1Position = new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(),
				player1StartPos);
		PlayerPosition player2Position = new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(),
				player2StartPos);

		GamePosition gamePosition = new GamePosition(0, player1Position, player2Position, players.get(0), game);

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

}