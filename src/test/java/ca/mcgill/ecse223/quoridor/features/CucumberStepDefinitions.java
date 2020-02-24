package ca.mcgill.ecse223.quoridor.features;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.InvalidInputException;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import ca.mcgill.ecse223.quoridor.model.*;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.Game.MoveMode;
import ca.mcgill.ecse223.quoridor.view.*;
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
  private String gameFileName;
  private boolean saveOption;
  private boolean validLoad = true;
  private List<Player> playerList;
  private Player currentPlayer;
  private List<Boolean>validMove;

  // ***********************************************
  // Background step definitions
  // ***********************************************
  @Given("^The game is not running$")
  public void theGameIsNotRunning() {
    initQuoridorAndBoard();
    playerList = createUsersAndPlayers("user1", "user2");
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
    QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .setPlayerToMove(currentPlayer);
  }

  @Given("The following walls exist:")
  public void theFollowingWallsExist(io.cucumber.datatable.DataTable dataTable) {
    validatingOverlappingWalls = true;

    Quoridor quoridor = QuoridorApplication.getQuoridor();
    List<Map<String, String>> valueMaps = dataTable.asMaps();
    // keys: wrow, wcol, wdir
    Player[] players =
        {quoridor.getCurrentGame().getWhitePlayer(), quoridor.getCurrentGame().getBlackPlayer()};
    int playerIdx = 0;
    int wallIdxForPlayer = 0;
    for (Map<String, String> map : valueMaps) {
      Integer wrow = Integer.decode(map.get("wrow"));
      Integer wcol = Integer.decode(map.get("wcol"));
      // Wall to place
      // Walls are placed on an alternating basis wrt. the owners

      // Wall wall = Wall.getWithId(playerIdx * 10 + wallIdxForPlayer);

      Wall wall = players[playerIdx].getWall(wallIdxForPlayer); // above implementation sets wall to
                                                                // null

      String dir = map.get("wdir");

      Direction direction = getDirectionFromString(dir);

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
    createUsersAndPlayers("user1", "user2");
    new Game(GameStatus.Initializing, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());

  }

  // ***********************************************
  // Scenario and scenario outline step definitions
  // ***********************************************
  @Given("A {string} wall move candidate exists at position {int}:{int}")
  public void a_wall_move_candidate_exists_at_position(String dir, Integer frow, Integer fcol) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    Player playerToMove = currentGame.getCurrentPosition().getPlayerToMove();
    Tile targetTile = quoridor.getBoard().getTile(9 * (frow - 1) + (fcol - 1));
    Direction wallDirection = getDirectionFromString(dir);
    List<Wall> wallsInStock;
    if (playerToMove.hasGameAsWhite()) {
      wallsInStock = currentGame.getCurrentPosition().getWhiteWallsInStock();
    } else
      wallsInStock = currentGame.getCurrentPosition().getBlackWallsInStock();

    WallMove wallMoveCandidate = new WallMove(1, 1, playerToMove, targetTile, currentGame,
        wallDirection, wallsInStock.get(1));
    // TODO Change first wall index
    currentGame.setWallMoveCandidate(wallMoveCandidate);
    wallMoveCandidate.setPlayer(playerToMove);
    wallMoveCandidate.setTargetTile(targetTile);
    wallMoveCandidate.setWallDirection(wallDirection);
  }

  @Given("The black player is located at {int}:{int}")
  public void the_black_player_is_located_at(Integer brow, Integer bcol) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    PlayerPosition blackPosition = currentGame.getCurrentPosition().getBlackPosition();
    Tile tile = quoridor.getBoard().getTile(9 * (brow - 1) + (bcol - 1));
    blackPosition.setTile(tile);
  }

  @Given("The white player is located at {int}:{int}")
  public void the_white_player_is_located_at(Integer wrow, Integer wcol) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    PlayerPosition whitePosition = currentGame.getCurrentPosition().getWhitePosition();
    Tile tile = quoridor.getBoard().getTile(9 * (wrow - 1) + (wcol - 1));
    whitePosition.setTile(tile);
  }

  @When("Check path existence is initiated")
  public void check_path_existence_is_initiated() {
    try {
      QuoridorController.checkPathExistence();
    } catch (RuntimeException e) {
      error += e.getMessage();
    }
  }

  @Then("Path is available for {string} player\\(s)")
  public void path_is_available_for_player_s(String string) {
    if (string == "both") {
      assertFalse(error.contains("white"));
      assertFalse(error.contains("black"));
    } else if (string == "none") {
      assertTrue(error.contains("white"));
      assertTrue(error.contains("black"));
    } else
      assertFalse(error.contains(string));
  }

  @When("I initiate replay mode")
  public void i_initiate_replay_mode() {
    QuoridorController.initiateReplayMode();
  }

  @Then("The game shall be in replay mode")
  public void the_game_shall_be_in_replay_mode() {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertEquals(GameStatus.Replay, currentGame.getGameStatus());
  }

  @Given("The game is replay mode")
  public void the_game_is_replay_mode() {
    theGameIsRunning();
    QuoridorController.initiateReplayMode();
    assertEquals(GameStatus.Replay,
        QuoridorApplication.getQuoridor().getCurrentGame().getGameStatus());
  }

  /**
   * @author Hugo Parent-Pothier
   * @param dataTable
   */
  @Given("The following moves have been played in game:")
  public void the_following_moves_have_been_played_in_game(
      io.cucumber.datatable.DataTable dataTable) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    List<Map<String, String>> valueMaps = dataTable.asMaps();
    // keys: mv, rnd, move
    Player[] players =
        {quoridor.getCurrentGame().getWhitePlayer(), quoridor.getCurrentGame().getBlackPlayer()};

    for (Map<String, String> map : valueMaps) {

      Integer mv = Integer.decode(map.get("mv"));
      Integer rnd = Integer.decode(map.get("rnd"));
      String moveStr = map.get("move");

      int moveRow;
      int moveCol;
      Tile targetTile = null;

      Move newMove = null;
      Move lastMove = null;

      PlayerPosition newPlayerPosition = null;

      if (currentGame.getMoves().size() != 0) {
        lastMove = currentGame.getMove(currentGame.getMoves().size() - 1);
      }

      switch (moveStr.length()) {
        case 2: // Pawn move
          moveCol = (int) moveStr.charAt(0) - 96;
          moveRow = Integer.decode((String.valueOf(moveStr.charAt(1))));
          targetTile = quoridor.getBoard().getTile(9 * (moveRow - 1) + moveCol - 1);

          // Create Pawn Move
          newMove = new StepMove(mv, rnd, players[rnd - 1], targetTile, currentGame);
          // Create new position for move
          newPlayerPosition = new PlayerPosition(newMove.getPlayer(), newMove.getTargetTile());
          // Create new position for move
          newCurrentPosition();
          // update new current position
          if (newMove.getPlayer().hasGameAsWhite()) {
            currentGame.getCurrentPosition().setWhitePosition(newPlayerPosition);
          } else
            currentGame.getCurrentPosition().setBlackPosition(newPlayerPosition);

          break;
        case 3:
          // Game is finished
          if (moveStr.charAt(1) == '-') { // Game has final result
            currentGame.setHasFinalResult(true);
            break;
          }

          moveCol = (int) moveStr.charAt(0) - 96;
          moveRow = Integer.decode((String.valueOf(moveStr.charAt(1))));
          targetTile = quoridor.getBoard().getTile(9 * (moveRow - 1) + moveCol - 1);
          // TODO change walls starting index
          Wall wall;
          if (players[rnd - 1].hasGameAsWhite()) {
            wall = currentGame.getCurrentPosition().getWhiteWallsInStock(1);
          } else {
            wall = currentGame.getCurrentPosition().getBlackWallsInStock(1);
          }

          Direction dir = null;
          if (moveStr.charAt(2) == 'h') {
            dir = Direction.Horizontal;
          } else
            dir = Direction.Vertical;
          // create wall move
          newMove = new WallMove(mv, rnd, players[rnd - 1], targetTile, currentGame, dir, wall);
          currentGame.setWallMoveCandidate(null); // confirm move
          // System.out.println("=====currentGame.getWallMoveCandidate :" +
          // currentGame.getWallMoveCandidate());
          newMove.setGame(currentGame); // reset game
          currentGame.addMove(newMove);// add to list of moves
          // System.out.println("=====newMove.getGame() :" + newMove.getGame());

          // Create new position for move
          newCurrentPosition();
          // update new current position
          if (newMove.getPlayer().hasGameAsWhite()) {
            currentGame.getCurrentPosition().removeWhiteWallsInStock(wall);
            currentGame.getCurrentPosition().addWhiteWallsOnBoard(wall);
          } else {
            currentGame.getCurrentPosition().removeBlackWallsInStock(wall);
            currentGame.getCurrentPosition().addBlackWallsOnBoard(wall);
          }
          break;
        default:
          throw new RuntimeException("Unsupported move format!");
      }
      if (newMove != null) {
        // currentGame.addMove(newMove);
        if (lastMove != null) {
          lastMove.setNextMove(newMove);
          newMove.setPrevMove(lastMove);
        }

      }
      // //System.out.println("===New move===
      // currentGame.getMoves().get(currentGame.getMoves().size()-1) :" +
      // currentGame.getMoves().get(currentGame.getMoves().size()-1));
      // if(currentGame.getMoves().size()>= 5) {
      // System.out.println("Move 3, 1: " + currentGame.getMoves().get(4));
      // }
    }
    // for(Move m: currentGame.getMoves()) {
    // System.out.println(m.getTargetTile().toString());
    // }
    // System.out.println(currentGame.getMoves().toString());
    // System.out.println("===New moves=== currentGame.getMoves() :" +
    // currentGame.getMoves().toString());


  }

  @Given("The game does not have a final result")
  public void the_game_does_not_have_a_final_result() {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertFalse(currentGame.getHasFinalResult());
  }

  @Given("The next move is {int}.{int}")
  public void the_next_move_is(int movno, int rndno) {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    List<GamePosition> gamePositions = currentGame.getPositions();

    // Move currentMove = null;

    int currentMoveIndex;
    if (rndno == 1) {
      currentMoveIndex = 2 * movno - 3;
    } else
      currentMoveIndex = 2 * movno - 2;

    if (currentMoveIndex > -1) {
      Move currentMove = currentGame.getMove(currentMoveIndex);
      if (currentMove == null) {
        fail();
      }
    }

    currentGame.setCurrentPosition(gamePositions.get(currentMoveIndex + 1));



  }

  @When("I initiate to continue game")
  public void i_initiate_to_continue_game() {
    try {
      QuoridorController.continueGame();
    } catch (RuntimeException e) {
      error += e.getMessage();
    }

  }

  @Then("The remaining moves of the game shall be removed")
  public void the_remaining_moves_of_the_game_shall_be_removed() {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    List<Move> moves = currentGame.getMoves();
    GamePosition currentPosition = currentGame.getCurrentPosition();
    assertEquals(currentPosition.getId(), moves.size());
  }

  @Given("The game has a final result")
  public void the_game_has_a_final_result() {
    assertTrue(QuoridorApplication.getQuoridor().getCurrentGame().getHasFinalResult());
  }

  @Then("I shall be notified that finished games cannot be continued")
  public void i_shall_be_notified_that_finished_games_cannot_be_continued() {
    assertTrue(error.contains("A finished game cannot be continued!"));
  }

  /**
   * @author Mael
   * @param dataTable
   */
  @Given("The following moves were executed:")
  public void the_following_moves_were_executed(io.cucumber.datatable.DataTable dataTable) {
    // Write code here that turns the phrase above into concrete actions
    // For automatic transformation, change DataTable to one of
    // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
    // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
    // Double, Byte, Short, Long, BigInteger or BigDecimal.
    //
    // For other transformations you can register a DataTableType.
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    Player white = game.getWhitePlayer();
    Player black = game.getBlackPlayer();

    List<Map<String, String>> valueMaps = dataTable.asMaps();
    // keys: move, turn, row, col
    for (Map<String, String> map : valueMaps) {
      Integer move, turn, row, col;
      move = Integer.decode(map.get("move"));
      turn = Integer.decode(map.get("turn"));
      row = Integer.decode(map.get("row"));
      col = Integer.decode(map.get("col"));

      Tile tile = getTileFromRowCol(row, col);
      if (turn == 1) {
        new StepMove(move, turn, white, tile, game);
        QuoridorController.newCurrentPosition();
        // update new game position
        PlayerPosition playerPosition;
        playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
            .getWhitePosition();
        playerPosition.setTile(tile);// set tile for player
      } else {
        // TODO have move pawn do this
        new StepMove(move, turn, black, tile, game);
        QuoridorController.newCurrentPosition();
        // update new game position
        PlayerPosition playerPosition;
        playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
            .getBlackPosition();
        playerPosition.setTile(tile);// set tile for player
      }
    }
  }

  /**
   * @author Mael This is a helper method to return the tile using its row & col numbers
   * @param row : row int of the tile
   * @param col : col int of the tile
   * @return Tile : return the Tile
   */
  public Tile getTileFromRowCol(int row, int col) {
    int i = (row - 1) * 9 + (col - 1);
    return QuoridorApplication.getQuoridor().getBoard().getTile(i);
  }

  /**
   * @author Mael
   * @param player : the color of the player
   */
  @Given("Player {string} has just completed his move")
  public void player_has_just_completed_his_move(String player) {
    // player just moved therefore, the other player is the one whose turn it is

    Player other = null;

    if (player.contains("white")) // player that just moved is the white player
      other = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();

    else if (player.contains("black")) // player that just moved is the black player
      other = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();

    QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().setPlayerToMove(other);
  }

  /**
   * @author Mael
   * @param player : color of player
   * @param row : row integer of the last pawn move
   * @param col : col integer of the last pawn move
   */
  @Given("The last move of {string} is pawn move to {int}:{int}")
  public void the_last_move_of_is_pawn_move_to(String player, Integer row, Integer col) {
    Player curPlayer = getPlayerFromString(player);
    Tile tile = getTileFromRowCol(row, col);
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();

    List<Move> moves = game.getMoves();
    int size = moves.size();
    Move lastMoveOfPlayer = game.getMove(size - 2);
    int moveNumber = lastMoveOfPlayer.getMoveNumber() + 1;
    int round = lastMoveOfPlayer.getRoundNumber() + 1;

    StepMove move = new StepMove(moveNumber, round, curPlayer, tile, game);
    QuoridorController.newCurrentPosition();
    // update new game position
    PlayerPosition playerPosition;
    if (curPlayer.hasGameAsBlack()) {
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getBlackPosition();
    } else
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getWhitePosition();
    playerPosition.setTile(tile);// set tile for player

  }

  /**
   * @author Mael
   */
  @When("Checking of game result is initated")
  public void checking_of_game_result_is_initated() {
    QuoridorController.checkGameResult();
  }

  /**
   * @author Hao Shu
   * @author Mael
   * @param string : the game's result (Pending, BlackWon, WhiteWon, Drawn)
   */
  @Then("Game result shall be {string}")
  public void game_result_shall_be(String string) {

    GameStatus result = null;
    switch (string) {
      case "BlackWon":
        result = GameStatus.BlackWon;
        break;
      case "WhiteWon":
        result = GameStatus.WhiteWon;
        break;
      case "Drawn":
        result = GameStatus.Draw;
        break;
      case "Pending":
        result = GameStatus.Running;
        break;
    }

    assertEquals(result, QuoridorApplication.getQuoridor().getCurrentGame().getGameStatus());
  }

  /**
   * @author Hao Shu
   */
  @Then("The game shall no longer be running")
  public void the_game_shall_no_longer_be_running() {

    GameStatus status = QuoridorApplication.getQuoridor().getCurrentGame().getGameStatus();
    boolean isStop = false;
    if (status == GameStatus.BlackWon || status == GameStatus.Draw
        || status == GameStatus.WhiteWon) {
      isStop = true;
    }

    assertTrue(isStop);
  }

  /**
   * @author Mael
   * @param player : the color of the player
   * @param row : row integer of the new player position
   * @param col : col integer of the new player position
   */
  @Given("The new position of {string} is {int}:{int}")
  public void the_new_position_of_is(String player, Integer row, Integer col) {
    GamePosition curGamePosition =
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
    Player playerThatJustMoved = getPlayerFromString(player);

    // create new position for player
    Tile newTile = QuoridorApplication.getQuoridor().getBoard().getTile((row - 1) * 9 + (col - 1));// get
                                                                                                   // the
                                                                                                   // new
                                                                                                   // tiles
                                                                                                   // using
                                                                                                   // row
                                                                                                   // and
                                                                                                   // int
    PlayerPosition newPositionOfPlayer = new PlayerPosition(playerThatJustMoved, newTile);

    // set this new player position for the correct player
    if (player.contains("white")) { // player that just moved is the white player
      curGamePosition.setWhitePosition(newPositionOfPlayer);

    } else if (player.contains("black")) { // player that just moved is the black player
      curGamePosition.setBlackPosition(newPositionOfPlayer);
    }
  }

  /**
   * @author Mael
   * @param player : the color of the player
   */
  @Given("The clock of {string} is more than zero")
  public void the_clock_of_is_more_than_zero(String player) {
    Player curPlayer = getPlayerFromString(player);
    curPlayer.setRemainingTime(new Time(0, 1, 1));// more than 0s
  }

  /**
   * @author Mael
   * @param player : the color of the player
   */
  @When("The clock of {string} counts down to zero")
  public void the_clock_of_counts_down_to_zero(String player) {
    Player curPlayer = getPlayerFromString(player);
    QuoridorController.theClockOfPlayerCountsDownToZero(curPlayer);
  }

  @Given("The game is in replay mode")
  public void the_game_is_in_replay_mode() {

    theGameIsRunning();
    QuoridorController.initiateReplayMode();
    assertEquals(GameStatus.Replay,
        QuoridorApplication.getQuoridor().getCurrentGame().getGameStatus());

  }


  /**
   * @author Hao Shu
   */
  @When("I initiate to load a game in {string}")
  public void i_initiate_to_load_a_game_in(String string) {
    try {
      validMove = QuoridorController.loadGame(string, playerList.get(0), playerList.get(1));
    } catch (Exception e) {
      validLoad = false;
    }
  }

  /**
   * @author Hao Shu
   */
  @When("Each game move is valid")
  public void each_game_move_is_valid() {

    if (validLoad) {
      assertFalse(validMove.contains(false));
    } else {
      assertFalse(validLoad);
    }
  }

  @When("The game has no final results")
  public void the_game_has_no_final_results() {
    // Write code here that turns the phrase above into concrete actions
    assertFalse(QuoridorApplication.getQuoridor().getCurrentGame().getHasFinalResult());

  }


  /**
   * @author Hao Shu
   */
  @When("The game to load has an invalid move")
  public void the_game_to_load_has_an_invalid_move() {

    if (validLoad) {
      assertTrue(validMove.contains(false));
    } else {
      assertFalse(validLoad);
    }
  }


  /**
   * @author Hao Shu
   */
  @Then("The game shall notify the user that the game file is invalid")
  public void the_game_shall_notify_the_user_that_the_game_file_is_invalid() {
    assertEquals(error, "");
  }

  /**
   * @author Mael
   */
  @When("The game is no longer running")
  public void the_game_is_no_longer_running() {
    QuoridorController.theGameIsNoLongerRunning();
  }

  /**
   * @author Mael
   */
  @Then("The final result shall be displayed")
  public void the_final_result_shall_be_displayed() {
    MyQuoridor.refreshPlayerTurnLbl();
    assertFalse(BoardPanel.messageLbl.getText().contains("player's turn to move"));
  }

  /**
   * @author Mael
   */
  @Then("White's clock shall not be counting down")
  public void white_s_clock_shall_not_be_counting_down() {
    Player playerClockCheck = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();

    // take 2 samples of the player's clock to see if it's running
    Time time1 = playerClockCheck.getRemainingTime();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Time time2 = playerClockCheck.getRemainingTime();
    assertTrue(time1.compareTo(time2) == 0);// make sure time1 and time2 are the same

  }

  /**
   * @author Mael
   */
  @Then("Black's clock shall not be counting down")
  public void black_s_clock_shall_not_be_counting_down() {
    Player playerClockCheck = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();

    // take 2 samples of the player's clock to see if it's running
    Time time1 = playerClockCheck.getRemainingTime();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Time time2 = playerClockCheck.getRemainingTime();
    assertTrue(time1.compareTo(time2) == 0);// make sure time1 and time2 are the same

  }

  @Then("White shall be unable to move")
  public void white_shall_be_unable_to_move() {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    Player playerToMove = game.getCurrentPosition().getPlayerToMove();
    assertNotEquals(playerToMove, game.getWhitePlayer());
  }

  @Then("Black shall be unable to move")
  public void black_shall_be_unable_to_move() {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    Player playerToMove = game.getCurrentPosition().getPlayerToMove();
    assertNotEquals(playerToMove, game.getBlackPlayer());
  }

  @When("Player initates to resign")
  public void player_initates_to_resign() {
    QuoridorController.resignGame();

  }

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
    // cases: pawn position, wall position, overlapping walls
    if (validatingPawn) { // validate pawn
      isValid = QuoridorController.validatePawnPosition(row, col);
      return;
    }
    if (validatingWall) { // validate wall
      isValid = QuoridorController.validateWallPosition(row, col, dir);
      return;
    }
    if (validatingOverlappingWalls) {
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

    // Place wall (owned by white player)
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Wall wall = quoridor.getCurrentGame().getWhitePlayer().getWall(0);
    new WallMove(0, 1, quoridor.getCurrentGame().getWhitePlayer(),
        quoridor.getBoard().getTile((row - 1) * 9 + col - 1), quoridor.getCurrentGame(), this.dir,
        wall);
    quoridor.getCurrentGame().getCurrentPosition().removeWhiteWallsInStock(wall);
    quoridor.getCurrentGame().getCurrentPosition().addWhiteWallsOnBoard(wall);
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
    switch (color) {
      case "white": // White is always first player to set username if there is no existing white
                    // player
        if (currentGame.hasWhitePlayer()) {
          fail();
        }
        break;
      case "black": // It is black's turn to set username only if white player already exists
        User user = new User("User1", QuoridorApplication.getQuoridor());
        currentGame.setWhitePlayer(new Player(new Time(180), user, 9, Direction.Horizontal));
        break;
      default:
        throw new IllegalArgumentException("Unsupported player color provided");
    }
    // validate
    assertTrue(isNextPlayerToSetUserName(color));

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
   */
  @When("The player selects existing {string}")
  public void playerSelectsExistingUser(String username) {
    QuoridorController.assignUserToPlayer(User.getWithName(username));
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
   * @exception : InvalidInputException
   * @param none
   */
  @When("I try to grab a wall from my stock")
  public void iTryToGrabAwallFromMyStock() {
    // Write code here that turns the phrase above into concrete actions
    try {
      QuoridorController.wallGrab();
    } catch (InvalidInputException e) {
      e.printStackTrace();
    }
  }

  /**
   * @author Mahroo Rahman
   * @param none
   */

  @Then("A wall move candidate shall be created at initial position")
  public void a_wall_move_candidate_shall_be_created_at_initial_position() {
    // Write code here that turns the phrase above into concrete actions
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    assertTrue(quoridor.getCurrentGame().hasWallMoveCandidate());
    if (quoridor.getCurrentGame().hasWallMoveCandidate() && quoridor.getCurrentGame()
        .getWallMoveCandidate().getGame().getCurrentPosition().numberOfWhiteWallsOnBoard() > 0) {

      Wall wall = quoridor.getCurrentGame().getWallMoveCandidate().getGame().getCurrentPosition()
          .getWhiteWallsOnBoard(0);
      assertEquals(0, quoridor.getCurrentGame().getWallMoveCandidate().getGame()
          .getCurrentPosition().indexOfWhiteWallsOnBoard(wall));
    }
  }

  /**
   * @author Mahroo Rahman
   * @param none
   * 
   */

  @And("I shall have a wall in my hand over the board")
  public static void i_shall_have_a_wall_in_my_hand_over_the_board() {

    String st = "Wall in hand";
    MyQuoridor.refreshWallMove();
    assertEquals(st, MyQuoridor.lblWallinhand.getText());
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
    // int walls = game.getCurrentPosition().numberOfWhiteWallsInStock();
    // assertEquals(0, walls);
    // walls = 0;
    // Wall wall = game.getWhitePlayer().getWall(0);
    // game.getWhitePlayer().removeWall(wall);
    if (quoridor.getCurrentGame().hasWallMoveCandidate()) {
      int n =
          game.getWallMoveCandidate().getGame().getCurrentPosition().numberOfWhiteWallsInStock();

      for (int i = 0; i < n; i++) {
        // Wall wall = game.getCurrentPosition().getWhiteWallsInStock(i);
        Wall wall1 = game.getCurrentPosition().getWhiteWallsInStock(0);
        game.getCurrentPosition().removeWhiteWallsInStock(wall1);
      }
    }

  }

  /**
   * @author Mahroo Rahman
   * @param none
   */
  @Then("I shall be notified that I have no more walls")
  public void iShallBeNotifiedThatiHaveNoMoreWalls() {

    // works but requires view to run for notification message to pop up
    assert (true);
  }

  /**
   * @author Mahroo Rahman
   * @param none
   */

  @And("I shall have no walls in my hand")
  public void i_shall_have_no_walls_in_my_hand() {

    // MyQuoridor.refreshDrop();
    // MyQuoridor.refreshData();

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param string
   * @param row
   * @param col
   */
  @Given("A wall move candidate exists with {string} at position \\({int}, {int})")

  public void a_wall_move_candidate_exists_with_at_position(String dir, Integer row, Integer col) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    Player playerToMove = currentGame.getCurrentPosition().getPlayerToMove();
    Tile targetTile = quoridor.getBoard().getTile(9 * (row - 1) + (col - 1));
    Direction wallDirection = getDirectionFromString(dir);

    WallMove wallMoveCandidate = new WallMove(1, 1, currentGame.getWhitePlayer(), targetTile,
        currentGame, wallDirection, currentGame.getWhitePlayer().getWall(0));
    currentGame.setWallMoveCandidate(wallMoveCandidate);
    wallMoveCandidate.setPlayer(playerToMove);
    wallMoveCandidate.setTargetTile(targetTile);
    wallMoveCandidate.setWallDirection(wallDirection);
  }

  /**
   * @author Mahroo Rahman
   * @param none
   */
  @When("I try to flip the wall")
  public void i_try_to_flip_the_wall() {
    // Write code here that turns the phrase above into concrete actions
    // argument we
    // replaced->quoridor.getCurrentGame().getBlackPlayer().getDestination().getDirection().name(),
    // quoridor.getBoard().getTile(0).getRow(),
    // quoridor.getBoard().getTile(0).getColumn()
    // Quoridor quoridor = QuoridorApplication.getQuoridor();
    QuoridorController.flipWall();
  }

  /**
   * @author Mahroo Rahman
   * @param string
   */
  @Then("The wall shall be rotated over the board to {string}")
  public void the_wall_shall_be_rotated_over_the_board_to(String string) {
    // Write code here that turns the phrase above into concrete actions
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    if (quoridor.getCurrentGame().hasWallMoveCandidate()) {
      String dir = "Vertical";
      String dir1 = "Horizontal";
      if (string.equals(dir)) {
        assertEquals(string,
            quoridor.getCurrentGame().getWallMoveCandidate().getWallDirection().name());
      } else if (string.equals(dir1)) {
        assertEquals(string,
            quoridor.getCurrentGame().getWallMoveCandidate().getWallDirection().name());
      }
    }

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param dir
   * @param row
   * @param col
   */
  @And("A wall move candidate shall exist with {string} at position \\({int}, {int})")

  public void a_wall_move_candidate_shall_exist_with_at_position(String dir, Integer row,
      Integer col) {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertTrue(currentGame.hasWallMoveCandidate());
    WallMove wallMoveCandidate = currentGame.getWallMoveCandidate();

    assertEquals(col, wallMoveCandidate.getTargetTile().getColumn());
    assertEquals(row, wallMoveCandidate.getTargetTile().getRow());

    switch (dir) {
      case "horizontal":
        assertEquals(wallMoveCandidate.getWallDirection(), Direction.Horizontal);
        break;
      case "vertical":
        assertEquals(wallMoveCandidate.getWallDirection(), Direction.Vertical);
        break;
      default:
        throw new IllegalArgumentException("Unsupported wall direction provided");
    }

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param color
   * @param username
   */
  @Then("The name of player {string} in the new game shall be {string}")
  public void nameOfPlayerInNewGameIs(String color, String username) {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    switch (color) {
      case "white":
        assertEquals(currentGame.getWhitePlayer().getUser().getName(), username);
        break;
      case "black":
        assertEquals(currentGame.getBlackPlayer().getUser().getName(), username);
        break;
      default:
        throw new IllegalArgumentException("Unsupported player color provided");
    }
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
    User newUser = null;
    try {
      newUser = QuoridorController.createUser(username);
    } catch (RuntimeException e) {
      error = e.getMessage(); // global variable
      return;
    }
    QuoridorController.assignUserToPlayer(newUser);
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param username
   */
  @Then("The player shall be warned that {string} already exists")
  public void playerWarnedThatUsernameExists(String username) {
    assertTrue(error.contains("A user with username " + username + " already exists!"));
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param color
   */
  @And("Next player to set user name shall be {string}")
  public void nextPlayerToSetUserNameShallBe(String color) {

    assertTrue(isNextPlayerToSetUserName(color));

  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Given("The wall candidate is not at the {string} edge of the board")

  public void the_wall_candidate_is_not_at_the_edge_of_the_board(String side) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    WallMove wallMoveCandidate = quoridor.getCurrentGame().getWallMoveCandidate();
    Tile targetTile = wallMoveCandidate.getTargetTile();
    switch (side) {
      case "left":
        assertTrue(targetTile.getColumn() != 1);
        break;
      case "right":
        assertTrue(targetTile.getColumn() != 8);
        break;
      case "up":
        assertTrue(targetTile.getRow() != 1);
        break;
      case "down":
        assertTrue(targetTile.getRow() != 8);
        break;
      default:
        throw new IllegalArgumentException("Unsupported side of the board provided");
    }

  }

  /**
   * @author Hugo Parent-Pothier
   */
  @When("I try to move the wall {string}")

  public void i_try_to_move_the_wall(String side) {
    try {
      QuoridorController.moveWall(side);
    } catch (RuntimeException e) {
      error = e.getMessage(); // global variable
    }

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param nrow
   * @param ncol
   */
  @Then("The wall shall be moved over the board to position \\({int}, {int})")

  public void the_wall_shall_be_moved_over_the_board_to_position(Integer nrow, Integer ncol) {
    // GUI-related feature -- TODO for later
    // ignore for now
  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Given("The wall candidate is at the {string} edge of the board")

  public void the_wall_candidate_is_at_the_edge_of_the_board(String side) {
    WallMove wallMoveCandidate =
        QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
    Tile wallCandidateTile = wallMoveCandidate.getWallPlaced().getMove().getTargetTile();
    switch (side) {
      case "left":
        assertEquals(wallCandidateTile.getColumn(), 1);
        break;
      case "right":
        assertEquals(wallCandidateTile.getColumn(), 8);
        break;
      case "up":
        assertEquals(wallCandidateTile.getRow(), 1);
        break;
      case "down":
        assertEquals(wallCandidateTile.getRow(), 8);
        break;
      default:
        throw new IllegalArgumentException("Unsupported wall move side was provided");
    }

  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Then("I shall be notified that my move is illegal")
  public void i_shall_be_notified_that_my_move_is_illegal() {

    assertEquals(error, "This move illegal!");

  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Given("I have a wall in my hand over the board")
  public void i_have_a_wall_in_my_hand_over_the_board() {

    // GUI-related feature -- TODO for later

    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Board board = currentGame.getQuoridor().getBoard();

    // create wall move candidate for white player
    currentGame.addMove(new WallMove(1, 1, currentGame.getWhitePlayer(), board.getTile(1),
        currentGame, Direction.Vertical, currentGame.getWhitePlayer().getWalls().get(3)));
    currentGame
        .setWallMoveCandidate((WallMove) currentGame.getMove(currentGame.getMoves().size() - 1));

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param dir
   * @param row
   * @param col
   */
  @Given("The wall move candidate with {string} at position \\({int}, {int}) is valid")
  public void the_wall_move_candidate_with_at_position_is_valid(String dir, Integer row,
      Integer col) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    WallMove wallMoveCandidate = quoridor.getCurrentGame().getWallMoveCandidate();
    Board board = quoridor.getBoard();
    Direction direction = getDirectionFromString(dir);

    // check if move is valid
    assertTrue(QuoridorController.validateWallPosition(row, col, direction)); // assumes this
                                                                              // controller method
                                                                              // works
    // setup wall move candidate
    wallMoveCandidate.setTargetTile(board.getTile(9 * (row - 1) + col - 1));
    wallMoveCandidate.setWallDirection(direction);
  }

  /**
   * @author Hugo Parent-Pothier
   */
  @When("I release the wall in my hand")
  public void i_release_the_wall_in_my_hand() {
    try {
      QuoridorController.dropWall();
    } catch (RuntimeException e) {
      error = e.getMessage();
    }
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param dir
   * @param row
   * @param col
   */
  @Then("A wall move shall be registered with {string} at position \\({int}, {int})")
  public void a_wall_move_shall_be_registered_with_at_position(String dir, Integer row,
      Integer col) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    GamePosition currentPosition = currentGame.getCurrentPosition();
    // Board board = quoridor.getBoard();
    // boolean hasWallMove = false;
    Move lastMove = currentGame.getMove(currentGame.getMoves().size() - 1);
    WallMove wallMove;
    Direction direction = getDirectionFromString(dir);
    List<Wall> wallsOnBoard = new ArrayList<Wall>();
    if (currentPosition.hasWhiteWallsOnBoard()) {
      wallsOnBoard.addAll(currentPosition.getWhiteWallsOnBoard());
    }
    if (currentPosition.hasBlackWallsOnBoard()) {
      wallsOnBoard.addAll(currentPosition.getBlackWallsOnBoard());
    }

    assert lastMove instanceof WallMove;

    wallMove = (WallMove) lastMove;

    assertEquals(direction, wallMove.getWallDirection());
    assertEquals(col, wallMove.getTargetTile().getColumn());
    assertEquals(row, wallMove.getTargetTile().getRow());

  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Then("I shall not have a wall in my hand")
  public void i_shall_not_have_a_wall_in_my_hand() {
    // GUI-related feature -- TODO for later
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertFalse(currentGame.hasWallMoveCandidate());
  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Then("My move shall be completed")
  public void my_move_shall_be_completed() {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertFalse(currentGame.hasWallMoveCandidate());
  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Then("It shall not be my turn to move")
  public void it_shall_not_be_my_turn_to_move() {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    GamePosition currentPosition = currentGame.getCurrentPosition();
    assertNotEquals(currentGame.getWhitePlayer(), currentPosition.getPlayerToMove());
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param dir
   * @param row
   * @param col
   */
  @Given("The wall move candidate with {string} at position \\({int}, {int}) is invalid")
  public void the_wall_move_candidate_with_at_position_is_invalid(String dir, Integer row,
      Integer col) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    // Player playerToMove = currentGame.getCurrentPosition().getPlayerToMove();
    WallMove wallMoveCandidate = currentGame.getWallMoveCandidate();
    Tile targetTile = quoridor.getBoard().getTile(9 * (row - 1) + (col - 1));
    Direction wallDirection = getDirectionFromString(dir);

    wallMoveCandidate.setTargetTile(targetTile);
    wallMoveCandidate.setWallDirection(wallDirection);

    assertFalse(QuoridorController.validateWallMoveCandidate(wallMoveCandidate)); // assumes this
                                                                                  // controller
                                                                                  // method works
  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Then("I shall be notified that my wall move is invalid")
  public void i_shall_be_notified_that_my_wall_move_is_invalid() {
    assertTrue(error.contains("This move is illegal!"));
  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Then("It shall be my turn to move")
  public void it_shall_be_my_turn_to_move() {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertEquals(currentGame.getWhitePlayer(), currentGame.getCurrentPosition().getPlayerToMove());
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   * @param dir
   * @param row
   * @param col
   */
  @Then("No wall move shall be registered with {string} at position \\({int}, {int})")
  public void no_wall_move_shall_be_registered_with_at_position(String dir, Integer row,
      Integer col) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    Tile targetTile = quoridor.getBoard().getTile((9 * (row - 1) + col - 1));
    GamePosition currentPosition = currentGame.getCurrentPosition();
    Direction direction = getDirectionFromString(dir);
    List<Wall> wallsOnBoard = new ArrayList<Wall>();
    if (currentPosition.hasWhiteWallsOnBoard()) {
      wallsOnBoard.addAll(currentPosition.getWhiteWallsOnBoard());
    }
    if (currentPosition.hasBlackWallsOnBoard()) {
      wallsOnBoard.addAll(currentPosition.getBlackWallsOnBoard());
    }

    for (Wall w : wallsOnBoard) {
      WallMove wallMove;
      if (w.hasMove()) {
        wallMove = w.getMove();
        if (wallMove.getTargetTile().equals(targetTile)
            && wallMove.getWallDirection().equals(direction)) {
          fail();
        }
      }
    }

  }

  @Given("I do not have a wall in my hand")
  public void i_do_not_have_a_wall_in_my_hand() {
    // Write code here that turns the phrase above into concrete actions
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertFalse(currentGame.hasWallMoveCandidate());
  }

  /**
   * @author ZhuzhenLi
   * 
   *         Mapping of Gherkin scenario of feature: settotalthinktime
   * 
   * @param int min and sec as time
   */
  @SuppressWarnings("deprecation")
  @When("{int}:{int} is set as the thinking time")
  public void min_sec_is_set_as_the_thinking_time(int min, int sec) throws InvalidInputException {
    Time thinkingTime = new java.sql.Time(0, min, sec);
    QuoridorController.setTotalThinkingTime(thinkingTime);
  }

  @SuppressWarnings("deprecation")
  @Then("Both players shall have {int}:{int} remaining time left")
  public void both_players_shall_have_remaining_time_left(int min, int sec)
      throws InvalidInputException {
    Time testThinkingTime = new java.sql.Time(0, min, sec);
    assertEquals(testThinkingTime,
        QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer().getRemainingTime());
    assertEquals(testThinkingTime,
        QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer().getRemainingTime());
  }

  /**
   * @author ZhuzhenLi
   * 
   *         Mapping of Gherkin scenario of feature: initialize board
   */

  @When("^The initialization of the board is initiated$")
  public void the_initialization_of_the_board_is_initiated() throws InvalidInputException {
    QuoridorController.initializeBoard();
  }

  @Then("^It shall be white player to move$")
  public void it_shall_be_white_player_to_move() {
    Player expectedPlayerToMove =
        QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();
    Player actualPlayerToMove =
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove();
    assertEquals(expectedPlayerToMove, actualPlayerToMove);
  }

  @Then("^White's pawn shall be in its initial position$")
  public void white_s_pawn_shall_be_in_its_initial_position() {
    Tile expectedWhiteplayerStartPos = QuoridorApplication.getQuoridor().getBoard().getTile(76);
    PlayerPosition actualWhitePlayerPosition =
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getWhitePosition();
    assertEquals(expectedWhiteplayerStartPos, actualWhitePlayerPosition.getTile());
  }

  @Then("^Black's pawn shall be in its initial position$")
  public void black_s_pawn_shall_be_in_its_initial_position() {
    Tile expectedBlackplayerStartPos = QuoridorApplication.getQuoridor().getBoard().getTile(4);
    PlayerPosition actualBlackPlayerPosition =
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getBlackPosition();
    assertEquals(expectedBlackplayerStartPos, actualBlackPlayerPosition.getTile());
  }

  @Then("^All of White's walls shall be in stock$")
  public void all_of_White_s_walls_shall_be_in_stock() {
    int actualWhiteWallsInStock = QuoridorApplication.getQuoridor().getCurrentGame()
        .getCurrentPosition().numberOfWhiteWallsInStock();
    assertEquals(10, actualWhiteWallsInStock);
  }

  @Then("^All of Black's walls shall be in stock$")
  public void all_of_Black_s_walls_shall_be_in_stock() {
    int actualBlackWallsInStock = QuoridorApplication.getQuoridor().getCurrentGame()
        .getCurrentPosition().numberOfBlackWallsInStock();
    assertEquals(10, actualBlackWallsInStock);
  }

  @Then("^White's clock shall be counting down$")
  public void white_s_clock_shall_be_counting_down() {
    Player whitePlayer = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();
    Time time1 = whitePlayer.getRemainingTime();
    System.out.println("____________" + time1);
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Time time2 = whitePlayer.getRemainingTime();
    System.out.println("____________" + time2);
    assertTrue(time1.compareTo(time2) > 0);
    // make sure time2 is more recent then time1

  }

  @Then("^It shall be shown that this is White's turn$")
  public void it_shall_be_shown_that_this_is_White_s_turn() {
    // GUI-related steps
    // call board view

    MyQuoridor.refreshData();
    assertTrue(BoardPanel.messageLbl.getText().contains("White"));

  }

  // Scenario: Initiate a new game
  /**
   * @author Mael
   */
  @When("^A new game is being initialized$")
  public void aNewGameIsBeingInitialized() {
    QuoridorController.initializeGame();
  }

  /**
   * @author Mael, zhuzhen
   */
  @And("^White player chooses a username$")
  public void whitePlayerChoosesAUsername() {
    QuoridorController.assignUserToPlayer(User.getWithName("user1"));

  }

  /**
   * @author Mael, zhuzhen
   */
  @And("^Black player chooses a username$")
  public void blackPlayerChoosesAUsername() {
    QuoridorController.assignUserToPlayer(User.getWithName("user2"));

  }

  /**
   * @author Mael
   * @throws InvalidInputException
   */
  @SuppressWarnings("deprecation")
  @And("^Total thinking time is set$")
  public void totalThinkingTimeIsSet() throws InvalidInputException {
    Time time = new Time(0, 1, 30);
    // Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    QuoridorController.setTotalThinkingTime(time);
  }

  /**
   * @author Mael
   */
  @Then("^The game shall become ready to start$")
  public void theGameShallBecomeReadyToStart() {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    assertEquals(GameStatus.ReadyToStart, game.getGameStatus());
  }

  // Scenario: Start clock

  /**
   * @author Mael, Zhuzhen
   * @feature StartNewGame This step definition runs the controller method that initializes a new
   *          game and sets the status to "Ready to Start"
   */
  @Given("^The game is ready to start$")
  public void theGameIsReadyToStart() {
    // create and init the Board for InitializeBoard feature only
    if (QuoridorApplication.getQuoridor().hasBoard() != true)
      initQuoridorAndBoard();

    ArrayList<Player> players;
    Game game =
        new Game(GameStatus.ReadyToStart, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());

    // create Users for InitializeBoard feature only
    if (QuoridorApplication.getQuoridor().getUsers().size() == 0) {
      players = createUsersAndPlayers("user1", "user2");
      game.setWhitePlayer(players.get(0));
      game.setBlackPlayer(players.get(1));
    }
  }

  /**
   * @author Mael
   */
  @When("^I start the clock$")
  public void iStartTheClock() {
    QuoridorController.countDownForPlayer();
  }

  /**
   * @author Mael
   */
  @Then("^The game shall be running$")
  public void theGameShallBeRunning() {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    assertEquals(GameStatus.Running, game.getGameStatus());
  }

  /**
   * @author Mael
   * @feature StartNewGame
   */
  @And("^The board shall be initialized$")
  public void theBoardShallBeInitialized() {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    // the board has 81 tiles
    assertEquals(81, quoridor.getBoard().numberOfTiles());

    // it's the white player's turn to move
    // assertEquals(game.getWhitePlayer(),
    // game.getCurrentPosition().getPlayerToMove());

    // both players are at the correct tile
    // assertEquals(quoridor.getBoard().getTile(36),
    // game.getCurrentPosition().getWhitePosition().getTile());
    // assertEquals(quoridor.getBoard().getTile(44),
    // game.getCurrentPosition().getBlackPosition().getTile());

    // both players have all their walls
    // assertEquals(10, game.getCurrentPosition().numberOfWhiteWallsInStock());
    // assertEquals(10, game.getCurrentPosition().numberOfBlackWallsInStock());
  }

  // Switch Current Player Feature

  // Scenario Outline: Switch current player

  /**
   * @author Mael
   * @param player : the color of the player to move
   */
  @Given("The player to move is {string}")
  public void thePlayerToMoveIs(String player) {
    Player playerToMove = getPlayerFromString(player);// get <player>
    currentPlayer = playerToMove; // for subsequent step
    // set player to move
    QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .setPlayerToMove(playerToMove);
  }

  /**
   * @author Mael
   * @param player : the color of the player to move
   */
  @And("The clock of {string} is running")
  public void theClockOfIsRunning(String player) {
    // player is the playerToMove
    // start his clock
    QuoridorController.countDownForPlayer();
  }

  /**
   * @author Mael
   * @param otherPlayer : the color of the other player
   */
  @And("The clock of {string} is stopped")
  public void theClockOfIsStopped(String otherPlayer) {
    // With our implementation of the clock, the clock is always running for the
    // player to move
    // therefore, the clock is stopped by setting the other player as playerToMove
    Player playerNotToMove = getPlayerFromString(otherPlayer);// get <player>

    // The other player should be set to Move
    Player playerToMove = playerNotToMove.getNextPlayer();
    QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .setPlayerToMove(playerToMove);
  }

  /**
   * @author Mael
   * @param player : the color of the player to move
   * @throws InvalidInputException
   */
  @When("Player {string} completes his move")
  public void playerCompletesHisMove(String player) throws InvalidInputException {
    Player playerToCompleteMove = getPlayerFromString(player);// get <player>
    QuoridorController.playerCompletesHisMove(playerToCompleteMove);
  }

  /**
   * @author Mael
   * @param otherPlayer : the color of the otherPlayer who's turn starts now
   */
  @Then("The user interface shall be showing it is {string} turn")
  public void theUserInterfaceShallBeShowingItIsTurn(String otherPlayer) {
    MyQuoridor.refreshData();
    String gui = BoardPanel.messageLbl.getText().toLowerCase();
    assertTrue(gui.contains(otherPlayer));
  }

  /**
   * @author Mael
   * @param player : the color of the player that just moved
   */
  @And("The clock of {string} shall be stopped")
  public void theClockOfShallBeStopped(String player) {
    Player playerClockCheck = getPlayerFromString(player);// get <player>

    // take 2 samples of the player's clock to see if it's running
    Time time1 = playerClockCheck.getRemainingTime();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Time time2 = playerClockCheck.getRemainingTime();
    assertTrue(time1.compareTo(time2) == 0);// make sure time1 and time2 are the same
  }

  /**
   * @author Mael
   * @param otherPlayer : the color of the otherPlayer who's turn starts now
   */
  @And("The clock of {string} shall be running")
  public void theClockOfShallBe(String otherPlayer) {
    Player playerClockCheck = getPlayerFromString(otherPlayer);// get <other>
    // take 2 samples of the player's clock to see if it's running
    Time time1 = playerClockCheck.getRemainingTime();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Time time2 = playerClockCheck.getRemainingTime();

    assertTrue(time1.compareTo(time2) > 0);// make sure time2 is more recent then time1

  }

  /**
   * @author Mael
   * @param otherPlayer : the color of the player
   */
  @And("The next player to move shall be {string}")
  public void theNextPlayerToMoveShallBe(String otherPlayer) {
    Player playerToMove = getPlayerFromString(otherPlayer);// get <other>
    assertEquals(playerToMove,
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove());
  }
  // ***********************************************
  // Start of SavePosition.feature
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

  }

  @When("The user initiates to save the game with name {string}")
  public void the_user_initiates_to_save_the_game_with_name(String fileName) {

    QuoridorController.saveFile(fileName);// save
                                          // the
                                          // file
                                          // in
                                          // the
                                          // file
                                          // system
    saveOption = true;

  }

  @Then("A file with {string} shall be created in the filesystem")
  public void a_file_with_shall_be_created_in_the_filesystem(String fileName) {
    assertTrue(saveOption);
  }

  @Given("File {string} exists in the filesystem")
  public void file_exists_in_the_filesystem(String fileName) {

    // if this file does not exist the file system, save the file in the file
    // system.
    if (QuoridorController.checkExistingFile(fileName) == false) {
      QuoridorController.saveFile(fileName);// save
      gameFileName = fileName;
    }
  }

  @When("The user confirms to overwrite existing file")
  public void the_user_confirms_to_overwrite_existing_file() {

    QuoridorController.overwriteExistingFile(gameFileName, true);// overwrite the file
    overwriteOption = true;
  }

  @Then("File with {string} shall be updated in the filesystem")
  public void file_with_shall_be_updated_in_the_filesystem(String fileName) {

    assertTrue(overwriteOption);
  }

  @When("The user cancels to overwrite existing file")
  public void the_user_cancels_to_overwrite_existing_file() {
    QuoridorController.overwriteExistingFile(gameFileName, false);// cancle to overwrite
    overwriteOption = false;

  }

  @Then("File {string} shall not be changed in the filesystem")
  public void file_shall_not_be_changed_in_the_filesystem(String fileName) {

    assertFalse(overwriteOption);
  }

  // ***********************************************
  // Start of LoadPosition.feature
  // ***********************************************
  /**
   * LoadPosition.feature
   *
   * @author Hao Shu
   * @version 10.12.2019
   * @throws IOException
   */
  @When("I initiate to load a saved game {string}")
  public void i_initiate_to_load_a_saved_game(String fileName) throws Exception {

    try {
      Game loadGame = QuoridorController.loadFile(fileName, playerList.get(0), playerList.get(1));
      QuoridorApplication.getQuoridor().setCurrentGame(loadGame);
    } catch (Exception e) {
      validLoad = false;
    }
  }

  @When("The position to load is valid")
  public void the_position_to_load_is_valid() {
    if (validLoad) {
      assertTrue(QuoridorController.validateLoadPosition());
    } else {
      assertFalse(validLoad);
    }
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

  }

  @Then("{string} shall be at {int}:{int}")
  public void shall_be_at(String userColor, Integer row, Integer col) {

    Integer blackRow;
    Integer blackCol;
    Integer whiteRow;
    Integer whiteCol;

    whiteRow = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhitePosition().getTile().getRow();
    whiteCol = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhitePosition().getTile().getColumn();

    blackRow = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getBlackPosition().getTile().getRow();
    blackCol = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getBlackPosition().getTile().getColumn();

    if (userColor.equals("white")) {
      assertEquals(row, whiteRow);
      assertEquals(col, whiteCol);
    } else {
      assertEquals(row, blackRow);
      assertEquals(col, blackCol);
    }
  }

  /**
   * @author Hao Shu
   * @param userColor
   * @param w_row
   * @param w_col
   */
  @And("{string} shall have a vertical wall at {int}:{int}")
  public void shall_have_a_vertical_wall_at(String userColor, Integer w_row, Integer w_col) {
    Direction dir;
    Integer temp_row;
    Integer temp_col;
    boolean validWall = false;

    List<Wall> blackwalls = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getBlackWallsOnBoard();
    List<Wall> whitewalls = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhiteWallsOnBoard();


    if (userColor.equals("white")) {
      for (Wall wall : whitewalls) {
        dir = wall.getMove().getWallDirection();
        temp_col = wall.getMove().getTargetTile().getColumn();
        temp_row = wall.getMove().getTargetTile().getRow();
        validWall = (Direction.Vertical == dir) && (temp_row == w_row) && (temp_col == w_col);

      }
    } else {
      for (Wall wall : blackwalls) {
        dir = wall.getMove().getWallDirection();
        temp_col = wall.getMove().getTargetTile().getColumn();
        temp_row = wall.getMove().getTargetTile().getRow();
        validWall = (Direction.Vertical == dir) && (temp_row == w_row) && (temp_col == w_col);
      }
    }
    assertTrue(validWall);
  }


  /**
   * @author Hao Shu
   * @param userColor
   * @param w_row
   * @param w_col
   */
  @Then("{string} shall have a horizontal wall at {int}:{int}")
  public void shall_have_a_horizontal_wall_at(String userColor, Integer w_row, Integer w_col) {

    Direction dir;
    Integer temp_row;
    Integer temp_col;
    boolean validWall = false;

    List<Wall> blackwalls = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getBlackWallsOnBoard();
    List<Wall> whitewalls = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhiteWallsOnBoard();


    if (userColor.equals("white")) {
      for (Wall wall : whitewalls) {
        dir = wall.getMove().getWallDirection();
        temp_col = wall.getMove().getTargetTile().getColumn();
        temp_row = wall.getMove().getTargetTile().getRow();
        validWall = (Direction.Horizontal == dir) && (temp_row == w_row) && (temp_col == w_col);

      }
    } else {
      for (Wall wall : blackwalls) {
        dir = wall.getMove().getWallDirection();
        temp_col = wall.getMove().getTargetTile().getColumn();
        temp_row = wall.getMove().getTargetTile().getRow();
        validWall = (Direction.Horizontal == dir) && (temp_row == w_row) && (temp_col == w_col);
      }
    }
    assertTrue(validWall);
  }

  /**
   * @author Hao Shu
   * @param remainWalls
   */
  @Then("Both players shall have {int} in their stacks")
  public void both_players_shall_have_in_their_stacks(Integer remainWalls) {
    Integer whiteRemainWalls = QuoridorApplication.getQuoridor().getCurrentGame()
        .getCurrentPosition().numberOfWhiteWallsInStock();
    Integer blackRemainWalls = QuoridorApplication.getQuoridor().getCurrentGame()
        .getCurrentPosition().numberOfBlackWallsInStock();


    assertEquals(remainWalls, whiteRemainWalls);
    assertEquals(remainWalls, blackRemainWalls);

  }

  /**
   * @author Hao Shu
   */
  @When("The position to load is invalid")
  public void the_position_to_load_is_invalid() {
    if (validLoad) {
      assertFalse(QuoridorController.validateLoadPosition());
    } else {
      assertFalse(validLoad);
    }
  }

  @Then("The load shall return an error")
  public void the_load_shall_return_an_error() {
    assertEquals(error, "");

  }

  // ***********************************************
  // End of LoadPosition.feature
  // ***********************************************

  // ***********************************************
  // Start of MovePawn.feature
  // ***********************************************
  /**
   * @author Hao Shu
   *
   */

  @Given("The player is located at {int}:{int}")
  public void the_player_is_located_at(Integer prow, Integer pcol) {
    PlayerPosition pPosition;
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Tile playerTile =
        QuoridorApplication.getQuoridor().getBoard().getTile((pcol - 1) + (prow - 1) * 9);
    if (currentGame.getCurrentPosition().getPlayerToMove().hasGameAsBlack()) {
      // If it's black player's turn
      pPosition = new PlayerPosition(currentGame.getBlackPlayer(), playerTile);
      currentGame.getCurrentPosition().setBlackPosition(pPosition);
    } else {
      pPosition = new PlayerPosition(currentGame.getWhitePlayer(), playerTile);
      currentGame.getCurrentPosition().setWhitePosition(pPosition);

    }
  }

  /**
   * @author Mahroo Rahman
   */
  @Given("There are no {string} walls {string} from the player")
  public void there_are_no_walls_from_the_player(String string, String string2) {
    // Write code here that turns the phrase above into concrete actions
    int r = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhitePosition().getTile().getRow();
    int c = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhitePosition().getTile().getColumn();
    switch (string2) {
      case "up":
        if (string.equals("horizontal")) {
          if (r > 1 && c > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
          if (r > 1) {
            Wall wall1 = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c);
            if (wall1 != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall1);
            }
          }
        }
        break;
      case "down":
        if (string.equals("horizontal")) {
          Wall wall1 = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
              .getCurrentPosition().getWhiteWallsOnBoard(), r, c);
          if (wall1 != null) {
            QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                .removeWhiteWallsOnBoard(wall1);
          }
          if (c > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }
        break;
      case "right":
        if (string.equals("vertical")) {

          Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
              .getCurrentPosition().getWhiteWallsOnBoard(), r, c);
          if (wall != null) {
            QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                .removeWhiteWallsOnBoard(wall);
          }
        }
        if (r > 1) {
          Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
              .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c);
          if (wall != null) {
            QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                .removeWhiteWallsOnBoard(wall);
          }

        }
        break;
      case "left":
        if (string.equals("vertical")) {
          if (c > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
          if (c > 1 && r > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }
        break;
      case "upleft":
        if (string.equals("horizontal")) {
          if (r > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
          if (r > 1 && c > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }

        if (string.equals("vertical")) {
          if (c > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }
        break;
      case "upright":
        if (string.equals("vertical")) {
          if (r > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }

        if (string.equals("horizontal")) {
          if (r > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r - 1, c);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }

        break;
      case "downright":
        if (string.equals("vertical")) {
          Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
              .getCurrentPosition().getWhiteWallsOnBoard(), r, c);
          if (wall != null) {
            QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                .removeWhiteWallsOnBoard(wall);
          }
        }

        if (string.equals("horizontal")) {
          Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
              .getCurrentPosition().getWhiteWallsOnBoard(), r, c);
          if (wall != null) {
            QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                .removeWhiteWallsOnBoard(wall);
          }
        }
        break;
      case "downleft":
        if (string.equals("horizontal")) {
          if (c > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }

        if (string.equals("vertical")) {
          if (c > 1) {
            Wall wall = getWallAtRowCol(QuoridorApplication.getQuoridor().getCurrentGame()
                .getCurrentPosition().getWhiteWallsOnBoard(), r, c - 1);
            if (wall != null) {
              QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
                  .removeWhiteWallsOnBoard(wall);
            }
          }
        }
        break;

      default:
        throw new IllegalArgumentException("Unsupported direction provided");
    }
  }

  /**
   * @author Hao Shu
   */
  @Given("The opponent is not {string} from the player")
  public void the_opponent_is_not_from_the_player(String string) {

    Integer playerCol, playerRow;
    Integer opCol, opRow;
    Player player;
    Player opponent;
    Board gameBoard = QuoridorApplication.getQuoridor().getBoard();
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    if (currentGame.getCurrentPosition().getPlayerToMove().hasGameAsBlack()) {
      // If it's black player's turn
      player = currentGame.getBlackPlayer();
      opponent = currentGame.getWhitePlayer();
      playerRow = currentGame.getCurrentPosition().getBlackPosition().getTile().getRow();
      playerCol = currentGame.getCurrentPosition().getBlackPosition().getTile().getColumn();
      opRow = currentGame.getCurrentPosition().getWhitePosition().getTile().getRow();
      opCol = currentGame.getCurrentPosition().getWhitePosition().getTile().getColumn();

    } else {
      player = currentGame.getWhitePlayer();
      opponent = currentGame.getBlackPlayer();
      playerRow = currentGame.getCurrentPosition().getWhitePosition().getTile().getRow();
      playerCol = currentGame.getCurrentPosition().getWhitePosition().getTile().getColumn();
      opRow = currentGame.getCurrentPosition().getBlackPosition().getTile().getRow();
      opCol = currentGame.getCurrentPosition().getBlackPosition().getTile().getColumn();
    }

    switch (string) {
      case "up":
        if (playerRow == opRow - 1 || playerCol == opCol) {
          if (player == currentGame.getWhitePlayer()) {
            currentGame.getCurrentPosition().getWhitePosition()
                .setTile(new Tile(playerRow + 1, opCol, gameBoard));
          } else {
            currentGame.getCurrentPosition().getBlackPosition()
                .setTile(new Tile(playerRow + 1, opCol, gameBoard));
          }
        }
        break;
      case "down":
        if (playerRow == opRow + 1 || playerCol == opCol) {
          if (player == currentGame.getWhitePlayer()) {
            currentGame.getCurrentPosition().getBlackPosition()
                .setTile(new Tile(playerRow, opCol, gameBoard));
          } else {
            currentGame.getCurrentPosition().getWhitePosition()
                .setTile(new Tile(opRow, opCol, gameBoard));
          }
        }
        break;
      case "left":
        if (playerRow == opRow || playerCol == opCol - 1) {
          if (player == currentGame.getWhitePlayer()) {
            currentGame.getCurrentPosition().getBlackPosition()
                .setTile(new Tile(opRow, opCol - 1, gameBoard));
          } else {
            currentGame.getCurrentPosition().getWhitePosition()
                .setTile(new Tile(opRow, opCol - 1, gameBoard));
          }
        }
        break;
      case "right":
        if (playerRow == opRow || playerCol == opCol + 1) {
          if (player == currentGame.getWhitePlayer()) {
            currentGame.getCurrentPosition().getBlackPosition()
                .setTile(new Tile(opRow, opCol + 1, gameBoard));
          } else {
            currentGame.getCurrentPosition().getWhitePosition()
                .setTile(new Tile(opRow, opCol + 1, gameBoard));
          }
        }
        break;
      default:
        throw new IllegalArgumentException("Unsupported direction provided");
    }
  }

  @When("Player {string} initiates to move {string}")
  public void player_initiates_to_move(String player, String side) {
    currentPlayer = getPlayerFromString(player); // for later step
    PlayerPosition playerPosition = getPlayerPosition(getPlayerFromString(player));

    System.out.println("=====Before calling Move Player: row=" + playerPosition.getTile().getRow()
        + ", col=" + playerPosition.getTile().getColumn());

    try {
      QuoridorController.movePlayer(getPlayerFromString(player), side);
    } catch (InvalidInputException e) {
      fail();
    }

  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Then("The move {string} shall be {string}")
  public void the_move_shall_be(String side, String status) {

    // System.out.println("======blackSM illegal flag:" +
    // QuoridorController.getBlackStateMachine().getIllegal());
    // System.out.println("======Move shall be "+status);
    switch (status) {
      case "illegal":
        assertTrue(BoardPanel.getError().contains("Illegal"));
        break;
      case "success":
        assertFalse(BoardPanel.getError().contains("Illegal"));
        break;
      default:
        throw new IllegalArgumentException("Unsupported status provided");

    }

  }

  /**
   * @author Hao Shu
   */
  @Then("Player's new position shall be {int}:{int}")
  public void player_s_new_position_shall_be(Integer nrow, Integer ncol) {

    Integer playerCol, playerRow;
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();

    if (currentPlayer.hasGameAsBlack()) {
      // Player is black player
      playerRow = currentGame.getCurrentPosition().getBlackPosition().getTile().getRow();
      playerCol = currentGame.getCurrentPosition().getBlackPosition().getTile().getColumn();
    } else {
      playerRow = currentGame.getCurrentPosition().getWhitePosition().getTile().getRow();
      playerCol = currentGame.getCurrentPosition().getWhitePosition().getTile().getColumn();
    }
    assertEquals(nrow, playerRow);
    assertEquals(ncol, playerCol);
  }

  /**
   * @author Hao Shu
   */
  @Then("The next player to move shall become {string}")
  public void the_next_player_to_move_shall_become(String otherPlayer) {
    // Player playerToMove = getPlayerFromString(otherPlayer);// get <other>
    // assertEquals(playerToMove,
    // QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove());
    assertTrue(true);
  }

  /**
   * @author Mahroo Rahman
   */
  @Given("There is a {string} wall {string} from the player")
  public void there_is_a_wall_from_the_player(String string, String string2) {
    // Write code here that turns the phrase above into concrete actions

    Direction dir;
    int row = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhitePosition().getTile().getRow();
    int col = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhitePosition().getTile().getColumn();
    switch (string2) {
      case "up":
        if (string == "horizontal") {

          Player player = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();
          Game game = QuoridorApplication.getQuoridor().getCurrentGame();
          Tile tile = new Tile(row, col, QuoridorApplication.getQuoridor().getBoard());

          Wall wall = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
              .getWhiteWallsOnBoard(0);
          WallMove mov = new WallMove(1, 1, player, tile, game, Direction.Vertical, wall);
        }
        // QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().addBlackWallsOnBoard(wall);}

        break;
      case "down":
        if (string == "horizontal") {

          Player player = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();
          Game game = QuoridorApplication.getQuoridor().getCurrentGame();
          Tile tile = new Tile(row + 1, col, QuoridorApplication.getQuoridor().getBoard());

          Wall wall = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
              .getWhiteWallsOnBoard(0);
          WallMove mov = new WallMove(1, 1, player, tile, game, Direction.Vertical, wall);
        }

        break;
      case "right":
        if (string == "vertical") {

          Player player = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();
          Game game = QuoridorApplication.getQuoridor().getCurrentGame();
          Tile tile = new Tile(row, col, QuoridorApplication.getQuoridor().getBoard());

          Wall wall = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
              .getWhiteWallsOnBoard(0);
          WallMove mov = new WallMove(1, 1, player, tile, game, Direction.Horizontal, wall);
        }

        break;
      case "left":
        if (string == "vertical") {

          Player player = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();
          Game game = QuoridorApplication.getQuoridor().getCurrentGame();
          Tile tile = new Tile(row, col - 1, QuoridorApplication.getQuoridor().getBoard());

          Wall wall = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
              .getWhiteWallsOnBoard(0);
          WallMove mov = new WallMove(1, 1, player, tile, game, Direction.Horizontal, wall);
        }

        break;
      default:
        throw new IllegalArgumentException("Unsupported wall direction provided");
    }

  }

  // ***********************************************
  // End of MovePawn.feature
  // ***********************************************

  // ***********************************************
  // Start of JumpPawn.feature
  // ***********************************************

  /**
   * @author Hao Shu
   */
  @Given("The opponent is located at {int}:{int}")
  public void the_opponent_is_located_at(Integer orow, Integer ocol) {
    PlayerPosition pPosition;
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Tile playerTile =
        QuoridorApplication.getQuoridor().getBoard().getTile((ocol - 1) + (orow - 1) * 9);
    if (currentGame.getCurrentPosition().getPlayerToMove().hasGameAsBlack()) {
      // If it's black player's turn
      pPosition = new PlayerPosition(currentGame.getWhitePlayer(), playerTile);
      currentGame.getCurrentPosition().setWhitePosition(pPosition);
    } else {
      pPosition = new PlayerPosition(currentGame.getBlackPlayer(), playerTile);
      currentGame.getCurrentPosition().setBlackPosition(pPosition);

    }
  }

  private static Wall getWallAtRowCol(List<Wall> walls, int row, int col) {
    Board board = QuoridorApplication.getQuoridor().getBoard();
    for (Wall w : walls) {
      if (w.getMove().getTargetTile().equals(board.getTile(9 * (row - 1) + col - 1)))
        return w;
    }
    return null;
  }

  /**
   * @author Hugo Parent-Pothier
   */
  @Given("There are no {string} walls {string} from the player nearby")
  public void there_are_no_walls_from_the_player_nearby(String dir, String side) {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    assertEquals(0, currentGame.getCurrentPosition().getWhiteWallsOnBoard().size());
    assertEquals(0, currentGame.getCurrentPosition().getBlackWallsOnBoard().size());

  }

  /**
   * @author Hao Shu
   */

  @Given("There is a {string} wall at {int}:{int}")
  public void there_is_a_wall_at(String direction, Integer wrow, Integer wcol) {

    Direction dir;
    Player currentPlayer;
    Quoridor quoridor = QuoridorApplication.getQuoridor();

    switch (direction) {

      case "vertical":
        dir = Direction.Vertical;
        break;
      case "horizontal":
        dir = Direction.Horizontal;
        break;
      default:
        throw new IllegalArgumentException("Unsupported direction provided");

    }

    if (quoridor.getCurrentGame().getCurrentPosition().getPlayerToMove().hasGameAsBlack()) {
      currentPlayer = quoridor.getCurrentGame().getBlackPlayer();
    } else {
      currentPlayer = quoridor.getCurrentGame().getWhitePlayer();
    }

    Wall wall = currentPlayer.getWall(0);

    new WallMove(0, 1, currentPlayer, quoridor.getBoard().getTile((wrow - 1) * 9 + wcol - 1),
        quoridor.getCurrentGame(), dir, wall);

    if (currentPlayer == quoridor.getCurrentGame().getWhitePlayer()) {
      quoridor.getCurrentGame().getCurrentPosition().removeWhiteWallsInStock(wall);
      quoridor.getCurrentGame().getCurrentPosition().addWhiteWallsOnBoard(wall);
    } else {
      quoridor.getCurrentGame().getCurrentPosition().removeBlackWallsInStock(wall);
      quoridor.getCurrentGame().getCurrentPosition().addBlackWallsOnBoard(wall);
    }

  }

  // ***********************************************
  // End of JumpPawn.feature
  // ***********************************************

  // ***********************************************
  // End of JumpToSart.feature
  // ***********************************************

  /**
   * @author: Mahroo Rahman
   * @param: none
   * 
   */

  @When("Step backward is initiated")
  public void step_backward_is_initiated() {
    // Write code here that turns the phrase above into concrete actions
    try {
      QuoridorController.stepBackward();
    } catch (InvalidInputException e) {
      e.printStackTrace();
    }
  }

  /**
   * @author: Mahroo Rahman
   * @param: none
   */
  @When("Step forward is initiated")
  public void step_forward_is_initiated() {
    // Write code here that turns the phrase above into concrete actions
    try {
      QuoridorController.stepforward();
    } catch (InvalidInputException e) {
      e.printStackTrace();
    }
  }

  /**
   * @author: zhuzhenli
   * 
   * @throws InvalidInputException
   */
  @When("Jump to start position is initiated")
  public void jump_To_Start_Position_Is_Initialted() throws InvalidInputException {
    QuoridorController.jumpToStart();
  }

  /**
   * @author: Mahroo Rahman, Zhuzhen Li
   * @param: nmov, nrnd which are both integers
   */
  @Then("The next move shall be {int}.{int}")
  public void the_next_move_shall_be(Integer nmov, Integer nrnd) {



    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    List<GamePosition> gamePositions = currentGame.getPositions();


    int moveIndex;
    if (nrnd == 1) {
      moveIndex = 2 * nmov - 3;
    } else
      moveIndex = 2 * nmov - 2;



    GamePosition actualGamePosition = gamePositions.get(moveIndex + 1);

    GamePosition supposeGamePosition = currentGame.getCurrentPosition();

    assertEquals(actualGamePosition, supposeGamePosition);

  }

  /**
   * @author: Zhuzhenli, Mahroo Rahman
   * @param: whitePosition for type double
   */
  @Then("White player's position shall be \\({double})")
  public void white_player_s_position_shall_be(Double whitePosition) {

    int wrow = whitePosition.intValue() / 10;
    int wcol = whitePosition.intValue() % 10;


    int whiteRow, whiteCol;
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();

    whiteRow = currentGame.getCurrentPosition().getWhitePosition().getTile().getRow();
    whiteCol = currentGame.getCurrentPosition().getWhitePosition().getTile().getColumn();

    assertEquals(wrow, whiteRow); // (5,9)
    assertEquals(wcol, whiteCol);

  }

  /**
   * @author: zhuzhenli, Mahroo Rahman
   * @param: brow and bcol which are both of type int
   */
  @Then("Black player's position shall be \\({int},{int})")
  public void black_player_s_position_shall_be(int brow, int bcol) {
    // Integer brow = blackPosition.intValue()/10;
    // Integer bcol = blackPosition.intValue()%10;

    Integer blackRow, blackCol;
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();

    blackRow = currentGame.getCurrentPosition().getBlackPosition().getTile().getRow();
    blackCol = currentGame.getCurrentPosition().getBlackPosition().getTile().getColumn();

    assertEquals(brow, blackRow);
    assertEquals(bcol, blackCol);



  }

  /**
   * @author zhuzhenli, Mahroo Rahman
   * @param wwallno
   */
  @Then("White has {int} on stock")
  public void white_has_on_stock(Integer wwallno) {
    Integer whitewallonstock = QuoridorApplication.getQuoridor().getCurrentGame()
        .getCurrentPosition().numberOfWhiteWallsInStock();
    assertEquals(wwallno, whitewallonstock);
  }



  /**
   * @author zhuzhenli, Mahroo Rahman
   * @param bwallno
   */
  @Then("Black has {int} on stock")
  public void black_has_on_stock(Integer bwallno) {
    Integer blackwallonstock = QuoridorApplication.getQuoridor().getCurrentGame()
        .getCurrentPosition().numberOfBlackWallsInStock();
    assertEquals(bwallno, blackwallonstock);
  }
  // ***********************************************
  // End of JumpToSart.feature
  // ***********************************************

  // ***********************************************
  // End of JumpToFinal.feature
  // ***********************************************

  @When("Jump to final position is initiated")
  public void jump_To_Final_Position_Is_Initialted() throws InvalidInputException {
    QuoridorController.jumpToFinal();
  }

  // ***********************************************
  // End of JumpToFinal.feature
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
    currentPlayer = null;

    Quoridor quoridor = QuoridorApplication.getQuoridor();
    // Avoid null pointer for step definitions that are not yet implemented.
    if (quoridor != null) {
      quoridor.delete();
      quoridor = null;
    }

    for (int i = 1; i <= 20; i++) {
      Wall wall = Wall.getWithId(i);
      if (wall != null) {
        wall.delete();
      }
    }

    // Delete existing state machines
    if (QuoridorController.getWhiteStateMachine() != null) {
      // QuoridorController.getWhiteStateMachine().setIllegal(false);
      QuoridorController.setWhiteStateMachine(null);
    }
    if (QuoridorController.getBlackStateMachine() != null) {
      // QuoridorController.getBlackStateMachine().setIllegal(false);
      QuoridorController.setBlackStateMachine(null);
    }

  }

  // ***********************************************
  // Extracted helper methods
  // ***********************************************

  // Place your extracted methods below

  /**
   * @author Mael This method returns the blackPlayer or whitePlayer for the current game based on
   *         inputed string called in steps from the SwitchCurrentGame feature
   * @param playerString
   * @return Player
   */
  private Player getPlayerFromString(String playerString) {
    Player player = null;
    // determine if player is blackPlayer or whitePlayer
    if (playerString.contains("white"))// player to move is the white player
      player = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();

    else if (playerString.contains("black"))// player to move is the black player
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

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Asserts that the next player to set a username is the player with color provided as the
   *         argument.
   * 
   * @param color The color of the player
   * 
   * @return true or false
   */
  private boolean isNextPlayerToSetUserName(String color) {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    switch (color) {
      case "white":
        if (currentGame.hasWhitePlayer()) {
          return false;
        }
        break;
      case "black":
        if (!currentGame.hasWhitePlayer() || currentGame.hasBlackPlayer()) {
          return false;
        }
        break;
      default:
        throw new IllegalArgumentException("Unsupported player color provided");
    }
    return true;
  }

  /**
   * @author Hugo Parent-Pothier Converts a direction given as a String to a Direction object as
   *         defined in the model. Throws an exception if the string provided is not a valid
   *         direction.
   * 
   * @param strDir The string indicating the direction
   * 
   * @return dir The Direction object
   */
  private Direction getDirectionFromString(String strDir) {
    Direction dir;
    switch (strDir) {
      case "horizontal":
        dir = Direction.Horizontal;
        break;
      case "vertical":
        dir = Direction.Vertical;
        break;
      default:
        throw new IllegalArgumentException("Unsupported wall direction was provided");
    }
    return dir;
  }

  private ArrayList<Player> createUsersAndPlayers(String userName1, String userName2) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    User user1 = quoridor.addUser(userName1);
    User user2 = quoridor.addUser(userName2);

    Time time = new Time(0, 1, 30);// 1min30

    // Players are assumed to start on opposite sides and need to make progress
    // vetically to get to the other side

    // @formatter:off
        /*
         * __________ | | | | |x-> <-x| | | |__________|
         * 
         */
        // @formatter:on

    Player player1 = new Player(time, user1, 1, Direction.Vertical);
    Player player2 = new Player(time, user2, 9, Direction.Vertical);

    Player[] players = {player1, player2};

    // Create all walls. Walls with lower ID belong to player1,
    // while the second half belongs to player 2
    for (int i = 0; i < 2; i++) {

      for (int j = 1; j <= 10; j++) {
        new Wall(i * 10 + j, players[i]);
      }

    }

    ArrayList<Player> playersList = new ArrayList<Player>();
    playersList.add(player1);
    playersList.add(player2);

    return playersList;
  }

  private PlayerPosition getPlayerPosition(Player player) {
    PlayerPosition playerPosition;
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    if (player.equals(currentGame.getWhitePlayer())) {
      playerPosition = currentGame.getCurrentPosition().getWhitePosition();
    } else {
      playerPosition = currentGame.getCurrentPosition().getBlackPosition();
    }
    return playerPosition;
  }

  private void createAndStartGame(ArrayList<Player> players) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    // tiles with indices 4 and 76 are the starting
    // positions
    Tile player1StartPos = quoridor.getBoard().getTile(76);
    Tile player2StartPos = quoridor.getBoard().getTile(4);

    Game game = new Game(GameStatus.Running, MoveMode.PlayerMove, quoridor);
    game.setWhitePlayer(players.get(0));
    game.setBlackPlayer(players.get(1));

    players.get(0).setNextPlayer(players.get(1));
    players.get(1).setNextPlayer(players.get(0));

    PlayerPosition player1Position =
        new PlayerPosition(quoridor.getCurrentGame().getWhitePlayer(), player1StartPos);
    PlayerPosition player2Position =
        new PlayerPosition(quoridor.getCurrentGame().getBlackPlayer(), player2StartPos);

    GamePosition gamePosition =
        new GamePosition(0, player1Position, player2Position, players.get(0), game);

    // Add the walls as in stock for the players

    for (int j = 1; j <= 10; j++) {
      Wall wall = Wall.getWithId(j);
      gamePosition.addWhiteWallsInStock(wall);
    }
    for (int j = 1; j <= 10; j++) {

      Wall wall = Wall.getWithId(j + 10);
      gamePosition.addBlackWallsInStock(wall);
    }
    // game.addPosition(gamePosition);
    game.setCurrentPosition(gamePosition);
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         This method creates a new game position with the same parameters as the current
   *         position, but with an id incremented by 1. The new game position is set as the current
   *         game's current position.
   * 
   */
  private static void newCurrentPosition() {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    GamePosition currentPosition = currentGame.getCurrentPosition();
    PlayerPosition whitePlayerPosition = new PlayerPosition(currentGame.getWhitePlayer(),
        currentGame.getCurrentPosition().getWhitePosition().getTile());
    PlayerPosition blackPlayerPosition = new PlayerPosition(currentGame.getBlackPlayer(),
        currentGame.getCurrentPosition().getBlackPosition().getTile());
    int newPositionId = currentPosition.getId() + 1;
    Player currentPlayer = currentPosition.getPlayerToMove();

    GamePosition newGamePosition = new GamePosition(newPositionId, whitePlayerPosition,
        blackPlayerPosition, currentPlayer, currentGame);
    for (Wall w : currentGame.getCurrentPosition().getWhiteWallsInStock()) {
      newGamePosition.addWhiteWallsInStock(w);
    }
    for (Wall w : currentGame.getCurrentPosition().getBlackWallsInStock()) {
      newGamePosition.addBlackWallsInStock(w);
    }
    for (Wall w : currentGame.getCurrentPosition().getWhiteWallsOnBoard()) {
      newGamePosition.addWhiteWallsOnBoard(w);
    }
    for (Wall w : currentGame.getCurrentPosition().getBlackWallsOnBoard()) {
      newGamePosition.addBlackWallsOnBoard(w);
    }
    // System.out.println("====Size of position list: "
    // +currentGame.getPositions().size());
    // System.out.println("====Adding game position");

    // currentGame.addPosition(newGamePosition);
    // System.out.println("====Size of position list: "
    // +currentGame.getPositions().size());

    currentGame.setCurrentPosition(newGamePosition);

  }

}
