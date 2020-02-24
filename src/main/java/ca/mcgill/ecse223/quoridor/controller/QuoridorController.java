package ca.mcgill.ecse223.quoridor.controller;

import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.InvalidInputException;
import ca.mcgill.ecse223.quoridor.controller.PawnBehavior.MoveDirection;
import ca.mcgill.ecse223.quoridor.model.*;
import ca.mcgill.ecse223.quoridor.model.Game.GameStatus;
import ca.mcgill.ecse223.quoridor.model.Game.MoveMode;
import ca.mcgill.ecse223.quoridor.view.MyQuoridor;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;

public class QuoridorController {

  private static PawnBehavior whiteStateMachine, blackStateMachine;

  public static PawnBehavior getWhiteStateMachine() {
    return whiteStateMachine;
  }

  public static void setWhiteStateMachine(PawnBehavior whiteStateMachine) {
    QuoridorController.whiteStateMachine = whiteStateMachine;
  }

  public static PawnBehavior getBlackStateMachine() {
    return blackStateMachine;
  }

  public static void setBlackStateMachine(PawnBehavior blackStateMachine) {
    QuoridorController.blackStateMachine = blackStateMachine;
  }

  /**
   * @author Mahroo Rahman
   * 
   *         When a player is using replay mode, this method causes the player to move his/her
   *         position one step backwards.
   * @throws InvalidInputException if there is no game, player, quoridor or inavlid game position
   * @return void
   */
  public static void stepBackward() throws InvalidInputException {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    if (quoridor == null) {
      throw new InvalidInputException("The quoridor does not exist.");
    }

    Game game = quoridor.getCurrentGame();
    if (game == null) {
      throw new InvalidInputException("The game does not exist.");
    }


    Player player1 = game.getWhitePlayer();
    Player player2 = game.getBlackPlayer();

    if (player1 == null) {
      throw new InvalidInputException("The game does not have a white player.");
    }

    if (player2 == null) {
      throw new InvalidInputException("The game does not have a black player.");
    }

    List<GamePosition> positions = game.getPositions();
    GamePosition current_position = game.getCurrentPosition();

    int index_of_game_position = positions.indexOf(current_position);
    if (current_position == null) {
      throw new InvalidInputException("Not a valid game position");
    }
    if (index_of_game_position > 0) {

      GamePosition next_position = positions.get(index_of_game_position - 1);


      game.setCurrentPosition(next_position);

    } else {
      game.setCurrentPosition(current_position);
    }

  }

  /**
   * @author Mahroo Rahman
   * 
   *         When a player is using replay mode, this method allows the players to change their
   *         position one step forward.
   * @throws InvalidInputException if there is no quoridor,player,game or the game position is
   *         invalid
   * @return void
   */
  public static void stepforward() throws InvalidInputException {
    Quoridor quoridor = QuoridorApplication.getQuoridor();

    if (quoridor == null) {
      throw new InvalidInputException("The quoridor does not exist.");
    }

    Game game = quoridor.getCurrentGame();
    if (game == null) {
      throw new InvalidInputException("The game does not exist.");
    }



    Player player1 = game.getWhitePlayer();
    Player player2 = game.getBlackPlayer();

    if (player1 == null) {
      throw new InvalidInputException("The game does not have a white player.");
    }

    if (player2 == null) {
      throw new InvalidInputException("The game does not have a black player.");
    }

    List<GamePosition> positions = game.getPositions();
    GamePosition current_position = game.getCurrentPosition();
    int index_of_game_position = positions.indexOf(current_position);
    if (current_position == null) {
      throw new InvalidInputException("Not a valid game position");
    }
    // positions.get(positions.size() - 1);
    int last_index = positions.size() - 1;


    if (index_of_game_position != last_index) {
      GamePosition next_position = positions.get(index_of_game_position + 1);
      game.setCurrentPosition(next_position);
    } else {
      game.setCurrentPosition(current_position);
    }

  }



  /**
   * @author zhuzhenLi
   * 
   *         When a player using replay mode, scroll fast to the very beginning of the game
   * @throws InvalidInputException
   */
  public static void jumpToStart() throws InvalidInputException {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    if (quoridor == null) {
      throw new InvalidInputException("The quoridor does not exist.");
    }

    Game game = quoridor.getCurrentGame();
    if (game == null) {
      throw new InvalidInputException("The game does not exist.");
    }

    // if (game.getGameStatus() != GameStatus.Replay) {
    // throw new InvalidInputException("The game should be in replay mode now.");
    // }

    Player player1 = game.getWhitePlayer();
    Player player2 = game.getBlackPlayer();

    if (player1 == null) {
      throw new InvalidInputException("The game does not have a white player.");
    }

    if (player2 == null) {
      throw new InvalidInputException("The game does not have a black player.");
    }
    // set player 1 and playe2 starting positions
    // Tile player1StartPos = quoridor.getBoard().getTile(76); // to match with the test
    // Tile player2StartPos = quoridor.getBoard().getTile(4);


    List<GamePosition> gamePositions = game.getPositions();
    GamePosition startGamePosition = gamePositions.get(0);

    // Add the walls as in stock for both players
    for (int j = 0; j < 10; j++) {
      Wall wall = player1.getWall(j);
      startGamePosition.addWhiteWallsInStock(wall);
    }

    for (int j = 0; j < 10; j++) {
      Wall wall = player2.getWall(j);
      startGamePosition.addBlackWallsInStock(wall);
    }



    // Check that 10 walls are on stock (no walls on board)
    if (startGamePosition.getBlackWallsInStock().size() != 10
        || startGamePosition.getWhiteWallsInStock().size() != 10) {
      throw new InvalidInputException("There should be 10 walls on stock for each player.");
    }

    game.setCurrentPosition(startGamePosition);

    // set white player to move
    game.getCurrentPosition().setPlayerToMove(player1);

    // // set next move as 1 1
    // Move newMove = new StepMove(1, 1, player1, player1StartPos, game);
    // game.addMove(newMove);

  }

  /**
   * @author zhuzhenLi
   * @throws InvalidInputException
   */
  public static void jumpToFinal() throws InvalidInputException {

    Quoridor quoridor = QuoridorApplication.getQuoridor();
    if (quoridor == null) {
      throw new InvalidInputException("The quoridor does not exist.");
    }

    Game game = quoridor.getCurrentGame();
    if (game == null) {
      throw new InvalidInputException("The game does not exist.");
    }

    // int whitewallonstock = game.getCurrentPosition().numberOfWhiteWallsInStock();
    // int blackwallonstock = game.getCurrentPosition().numberOfBlackWallsInStock();

    // System.out.println("~~~~~~~~~~~~~~~~~~~~whitewallonstock is "+whitewallonstock);
    // System.out.println("~~~~~~~~~~~~~~~~~~~~blackwallonstock is "+blackwallonstock);
    //

    // if (game.getGameStatus() != GameStatus.Replay) {
    // throw new InvalidInputException("The game should be in replay mode now.");
    // }

    Player player1 = game.getWhitePlayer();
    Player player2 = game.getBlackPlayer();

    if (player1 == null) {
      throw new InvalidInputException("The game does not have a white player.");
    }

    if (player2 == null) {
      throw new InvalidInputException("The game does not have a black player.");
    }

    List<GamePosition> gamePositions = game.getPositions();
    // System.out.println("\n----------gamePositions.size()-----"+gamePositions.size());

    GamePosition lastGamePostion = gamePositions.get(gamePositions.size() - 1);


    // set game position and the first player
    GamePosition gamePosition = gamePositions.get(gamePositions.size() - 1);
    // 0 (1595)
    // 1 (5895)
    // 2 (5852)
    // 3 (5752)
    // 4 (5753)
    // 5 (5753)
    // 6 (5753)
    // 7 (5753)
    // 8 (5762)

    // System.out.println("============player1FinalPos.getColumn()=========="+gamePosition.getWhitePosition().getTile().getColumn());
    // System.out.println("============player1FinalPos.getRow()=========="+gamePosition.getWhitePosition().getTile().getRow());
    // System.out.println("============player2FinalPos.getColumn()=========="+gamePosition.getBlackPosition().getTile().getColumn());
    // System.out.println("============player2FinalPos.getRow()=========="+gamePosition.getBlackPosition().getTile().getRow());

    List<Wall> whiteStockWalls = lastGamePostion.getWhiteWallsInStock();
    List<Wall> balckStockWalls = lastGamePostion.getBlackWallsInStock();

    for (Wall wwall : whiteStockWalls) {
      gamePosition.addWhiteWallsInStock(wwall);
    }

    for (Wall bwall : balckStockWalls) {
      gamePosition.addBlackWallsInStock(bwall);
    }

    game.setCurrentPosition(gamePosition);

    // // set next move
    // int movelength = game.getMoves().size();
    // Move lastMove = game.getMove(movelength - 1);
    // int nextRound = 0;
    // int nextMove = 0;
    // if (lastMove.getRoundNumber() == 1) {
    // nextRound = 2;
    // nextMove = lastMove.getMoveNumber();
    // } else {
    // nextRound = 1;
    // nextMove = lastMove.getMoveNumber() + 1;
    // }
    // Move newMove = new StepMove(nextMove, nextRound, player1, player1FinalPos, game);
    // game.addMove(newMove);

  }

  /**
   * @author zhuzhenLi
   * @author Hugo Parent-Pothier
   * @param moveDirection
   * @return boolean
   * 
   *         Check if there is a wall in the way of a pawn for a given moveDirection
   */
  public static boolean wallInTheWay(Tile currentTile, MoveDirection moveDirection) {
    boolean result = false;
    int currentPawnCol = currentTile.getColumn();
    int currentPawnRow = currentTile.getRow();

    List<Wall> walls = getWallsOnBoard();
    Wall wallToCheck = null;
    switch (moveDirection) {

      case West:
        // Check vertical wall to left
        wallToCheck = getWallAtRowCol(walls, currentPawnRow, currentPawnCol - 1);
        if (wallToCheck != null) {
          if (wallToCheck.getMove().getWallDirection().equals(Direction.Vertical)) {
            return true;
          }
        }
        // Check vertical wall to left (one row higher)
        if (currentPawnRow > 1) {
          wallToCheck = getWallAtRowCol(walls, currentPawnRow - 1, currentPawnCol - 1);
          if (wallToCheck != null) {
            if (wallToCheck.getMove().getWallDirection().equals(Direction.Vertical)) {
              return true;

            }
          }
        }
        break;

      case East:

        // Check vertical wall to right
        wallToCheck = getWallAtRowCol(walls, currentPawnRow, currentPawnCol);
        if (wallToCheck != null) {
          if (wallToCheck.getMove().getWallDirection().equals(Direction.Vertical)) {
            result = true;
            break;
          }
        }

        // Check vertical wall to right (one row higher)
        if (currentPawnRow > 1) {
          wallToCheck = getWallAtRowCol(walls, currentPawnRow - 1, currentPawnCol);
          if (wallToCheck != null) {
            if (wallToCheck.getMove().getWallDirection().equals(Direction.Vertical)) {
              result = true;
              break;
            }
          }
        }
        break;

      case North:

        // Check horizontal wall above
        wallToCheck = getWallAtRowCol(walls, currentPawnRow - 1, currentPawnCol);
        if (wallToCheck != null) {
          if (wallToCheck.getMove().getWallDirection().equals(Direction.Horizontal)) {
            result = true;
            break;
          }
        }

        // Check horizontal wall above (one column left)
        if (currentPawnCol > 1) {
          wallToCheck = getWallAtRowCol(walls, currentPawnRow - 1, currentPawnCol - 1);
          if (wallToCheck != null) {
            if (wallToCheck.getMove().getWallDirection().equals(Direction.Horizontal)) {
              result = true;
              break;
            }
          }
        }
        break;

      case South:
        // Check horizontal wall below
        wallToCheck = getWallAtRowCol(walls, currentPawnRow, currentPawnCol);
        if (wallToCheck != null) {
          if (wallToCheck.getMove().getWallDirection().equals(Direction.Horizontal)) {
            result = true;
            break;
          }
        }
        // Check horizontal wall below (one column left)
        if (currentPawnCol > 1) {
          wallToCheck = getWallAtRowCol(walls, currentPawnRow, currentPawnCol - 1);
          if (wallToCheck != null) {
            if (wallToCheck.getMove().getWallDirection().equals(Direction.Horizontal)) {
              result = true;
              break;
            }
          }
        }
        break;

      default:
        throw new IllegalArgumentException("Unsupported move direction provided");

    }
    // System.out.println("====Running wall in the way : result = " + result);
    return result;

  }

  /**
   * @author zhuzhenLi
   * @author Hugo Parent-Pothier
   * 
   * @param moveDirection
   * @return boolean Checks if there is a pawn placed next to the current pawn in the moveDirection.
   * 
   * @throw IllegalArgumentException "Unsupported move direction provided" as default case for
   *        switch
   * 
   */
  public static boolean pawnInTheWay(MoveDirection moveDirection) {
    // System.out.println("===Running pawn in the way:");
    boolean result = false;

    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Player currentPlayer = currentGame.getCurrentPosition().getPlayerToMove();
    int currentPawnRow, currentPawnCol;
    int targetPawnRow = -1, targetPawnCol = -1;

    if (currentPlayer.equals(currentGame.getWhitePlayer())) {
      currentPawnCol = currentGame.getCurrentPosition().getWhitePosition().getTile().getColumn();
      currentPawnRow = currentGame.getCurrentPosition().getWhitePosition().getTile().getRow();
    } else {
      currentPawnCol = currentGame.getCurrentPosition().getBlackPosition().getTile().getColumn();
      currentPawnRow = currentGame.getCurrentPosition().getBlackPosition().getTile().getRow();
    }

    switch (moveDirection) {
      case West:
        targetPawnCol = currentPawnCol - 1;
        targetPawnRow = currentPawnRow;
        break;
      case East:
        targetPawnCol = currentPawnCol + 1;
        targetPawnRow = currentPawnRow;
        break;
      case North:
        targetPawnRow = currentPawnRow - 1;
        targetPawnCol = currentPawnCol;
        break;
      case South:
        targetPawnRow = currentPawnRow + 1;
        targetPawnCol = currentPawnCol;
        break;
      default:
        throw new IllegalArgumentException("Unsupported move direction provided");
    }
    if (currentPlayer.equals(currentGame.getWhitePlayer())) {
      result = (targetPawnCol == currentGame.getCurrentPosition().getBlackPosition().getTile()
          .getColumn())
          && (targetPawnRow == currentGame.getCurrentPosition().getBlackPosition().getTile()
              .getRow());
    } else {
      result = (targetPawnCol == currentGame.getCurrentPosition().getWhitePosition().getTile()
          .getColumn())
          && (targetPawnRow == currentGame.getCurrentPosition().getWhitePosition().getTile()
              .getRow());
    }
    // System.out.println(result);

    return result;

  }

  /**
   * @throws IllegalArgumentException "This move is illegal!"
   */
  public static void illegalPawnMove() throws IllegalArgumentException {
    throw new IllegalArgumentException("This move is illegal!");
  }

  /**
   * @author zhuzhenLi, Mael
   * 
   *         This method makes a player move one tile in the specified direction. A step move is
   *         also created and added to the list of moves.
   * 
   * @param moveDirection : The direction to step at (West, North, South, East)
   * @return nothing since the method is void
   * @throws IllegalArgumentException "Unsupported move direction provided" when we check for cases
   *         of direction and the direction provided is unsupported
   * @throws IllegalArgumentException"No such tile index" if the index of the tile does not match
   * 
   */
  public static void stepMove(MoveDirection moveDirection) {
    System.out.println("step: " + moveDirection.toString());

    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();

    Player currentPlayer = currentGame.getCurrentPosition().getPlayerToMove();

    PlayerPosition playerPosition;
    if (currentPlayer.hasGameAsBlack())
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getBlackPosition();
    else
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getWhitePosition();

    int currentTileIndex = getIndexFromTile(playerPosition.getTile());

    int newTileIndex;
    switch (moveDirection) {
      case North:
        newTileIndex = currentTileIndex - 9; // up
        break;
      case South:
        newTileIndex = currentTileIndex + 9; // down
        break;
      case West:
        newTileIndex = currentTileIndex - 1; // left
        break;
      case East:
        newTileIndex = currentTileIndex + 1; // right
        break;
      default:
        throw new IllegalArgumentException("Unsupported move direction provided");
    }

    // set the pawn's new tile
    Tile newTile = QuoridorApplication.getQuoridor().getBoard().getTile(newTileIndex);

    // createMove(newTile, "step");

    List<Move> currentMoveList = currentGame.getMoves();// list of moves
    int moveListSize = currentMoveList.size();
    Move newMove = null;

    // no moves in the list
    if (moveListSize == 0) {
      // create the new move

      newMove = new StepMove(1, 1, currentPlayer, newTile, currentGame);
      currentGame.addMove(newMove);
    }
    // other moves in the list
    else {

      // to get the move & round number
      Move lastMove = currentMoveList.get(currentMoveList.size() - 1); // last move
      int nextMoveNumber = lastMove.getMoveNumber() + (lastMove.getRoundNumber() - 1);
      int nextMoveRound = (lastMove.getRoundNumber() % 2) + 1;

      // create the new move
      newMove = new StepMove(nextMoveNumber, nextMoveRound, currentPlayer, newTile, currentGame);
      currentGame.addMove(newMove);

      // update previous/next pointers
      lastMove.setNextMove(newMove);
      newMove.setPrevMove(lastMove);
    }

    // create new game position for move and set it to current position (duplicate
    // everything except id)
    newCurrentPosition();

    // update new game position
    if (currentPlayer.hasGameAsBlack()) {
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getBlackPosition();
    } else
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getWhitePosition();
    playerPosition.setTile(newTile);// set tile for player

    // Update Player to move
    try {
      playerCompletesHisMove(currentPlayer);
    } catch (InvalidInputException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

  }

  /**
   * @author zhuzhenLi
   * @param moveDirection make a forward jump move for pawn in moveDirection
   * @return nothing since method is void
   * @throw new IllegalArgumentException"Unsupported move direction provided" when the direction
   *        provided is east, west, north or south in default case for the switch
   * 
   */
  public static void jumpMove(MoveDirection moveDirection) {
    System.out.println("====JUMPING " + moveDirection.toString());
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Player currentPlayer = currentGame.getCurrentPosition().getPlayerToMove();

    PlayerPosition playerPosition;
    if (currentPlayer.equals(currentGame.getWhitePlayer())) {
      playerPosition = currentGame.getCurrentPosition().getWhitePosition();
    } else {
      playerPosition = currentGame.getCurrentPosition().getBlackPosition();
    }
    int currentPawnCol = playerPosition.getTile().getColumn();
    int currentPawnRow = playerPosition.getTile().getRow();
    int newPawnRow, newPawnCol;

    switch (moveDirection) {
      case West:
        newPawnCol = currentPawnCol - 2;
        newPawnRow = currentPawnRow;
        break;
      case East:
        newPawnCol = currentPawnCol + 2;
        newPawnRow = currentPawnRow;
        break;
      case North:
        newPawnRow = currentPawnRow - 2;
        newPawnCol = currentPawnCol;
        break;
      case South:
        newPawnRow = currentPawnRow + 2;
        newPawnCol = currentPawnCol;
        break;
      default:
        throw new IllegalArgumentException("Unsupported move direction provided");
    }

    int newTileIndex = (newPawnRow - 1) * 9 + newPawnCol - 1;

    // set the pawn's new tile
    Tile newTile = currentGame.getQuoridor().getBoard().getTile(newTileIndex);

    List<Move> currentMoveList = currentGame.getMoves();// list of moves
    int moveListSize = currentMoveList.size();
    Move newMove = null;

    // no moves in the list
    if (moveListSize == 0) {
      // create the new move
      newMove = new JumpMove(1, 1, currentPlayer, newTile, currentGame);
      currentGame.addMove(newMove);
    }
    // other moves in the list
    else {

      // to get the move & round number
      Move lastMove = currentMoveList.get(currentMoveList.size() - 1); // last move
      int nextMoveNumber = lastMove.getMoveNumber() + (lastMove.getRoundNumber() - 1);
      int nextMoveRound = (lastMove.getRoundNumber() % 2) + 1;

      // create the new move
      newMove = new JumpMove(nextMoveNumber, nextMoveRound, currentPlayer, newTile, currentGame);
      currentGame.addMove(newMove);

      // update previous/next pointers
      lastMove.setNextMove(newMove);
      newMove.setPrevMove(lastMove);
    }

    // create new game position for move and set it to current position (duplicate
    // everything except id)
    newCurrentPosition();

    // update new game position
    if (currentPlayer.hasGameAsBlack()) {
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getBlackPosition();
    } else
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getWhitePosition();
    playerPosition.setTile(newTile);// set tile for player

    // Update Player to move
    try {
      playerCompletesHisMove(currentPlayer);
    } catch (InvalidInputException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

  }

  /**
   * @author zhuzhenLi
   * @author Hugo Parent-Pothier
   * @param moveDirection1, moveDirection2 make a forward diagonal jump move for pawn in
   *        moveDirection1, moveDirection2
   * @return void
   * @throw IllegalArgumentException "Unsupported move direction provided" when the move is not
   *        west, east, north or south
   * 
   */
  public static void diagonalJumpMove(MoveDirection vDirection, MoveDirection hDirection) {
    System.out.println("====JUMPING DIAGONALLY: " + vDirection + ",  " + hDirection);
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Player currentPlayer = currentGame.getCurrentPosition().getPlayerToMove();

    PlayerPosition playerPosition = null;
    try {
      playerPosition = getPlayerPosition(currentPlayer);
    } catch (InvalidInputException e1) {
      e1.printStackTrace();
    }

    int currentPawnCol = playerPosition.getTile().getColumn();
    int currentPawnRow = playerPosition.getTile().getRow();
    int newPawnRow, newPawnCol;

    switch (vDirection) {
      case North:
        System.out.println("currentPawnRow: " + currentPawnRow);

        newPawnRow = currentPawnRow - 1;
        System.out.println("newPawnRow: " + newPawnRow);
        break;
      case South:
        newPawnRow = currentPawnRow + 1;

        break;
      default:
        throw new IllegalArgumentException("Unsupported vertical direction provided");
    }

    switch (hDirection) {
      case West:
        System.out.println("currentPawnCol: " + currentPawnCol);
        newPawnCol = currentPawnCol - 1;
        System.out.println("newPawnCol: " + newPawnCol);
        break;
      case East:
        newPawnCol = currentPawnCol + 1;
        break;
      default:
        throw new IllegalArgumentException("Unsupported horizontal direction provided");
    }

    int index = (newPawnRow - 1) * 9 + newPawnCol - 1;

    // set the pawn's new tile
    Tile newTile = currentGame.getQuoridor().getBoard().getTile(index);

    List<Move> currentMoveList = currentGame.getMoves();
    int moveListSize = currentMoveList.size();

    Move newMove = null;

    // no moves in the list
    if (moveListSize == 0) {
      // create the new move
      newMove = new JumpMove(1, 1, currentPlayer, newTile, currentGame);
      currentGame.addMove(newMove);
    }
    // other moves in the list
    else {

      // to get the move & round number
      Move lastMove = currentMoveList.get(currentMoveList.size() - 1); // last move
      int nextMoveNumber = lastMove.getMoveNumber() + (lastMove.getRoundNumber() - 1);
      int nextMoveRound = (lastMove.getRoundNumber() % 2) + 1;

      // create the new move
      newMove = new JumpMove(nextMoveNumber, nextMoveRound, currentPlayer, newTile, currentGame);
      currentGame.addMove(newMove);

      // update previous/next pointers
      lastMove.setNextMove(newMove);
      newMove.setPrevMove(lastMove);
    }

    // create new game position for move and set it to current position (duplicate
    // everything except id)
    newCurrentPosition();

    // update new game position
    if (currentPlayer.hasGameAsBlack()) {
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getBlackPosition();
    } else
      playerPosition = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getWhitePosition();
    playerPosition.setTile(newTile);// set tile for player

    // Update Player to move
    try {
      playerCompletesHisMove(currentPlayer);
    } catch (InvalidInputException e) {
      throw new IllegalArgumentException(e.getMessage());
    }

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Moves a wall over the board by changing the target tile of the wall move candidate. A
   *         wall can only move to a tile adjacent to its current tile. Throws an exception if the
   *         move is not allowed. (Associated to move wall feature)
   * 
   * @param side The direction of the move (left, right, up, down)
   * 
   * @throws IllegalArgumentException
   * @return void
   * 
   */
  public static void moveWall(String side) throws IllegalArgumentException {

    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    WallMove wallMoveCandidate = currentGame.getWallMoveCandidate();
    Tile targetTile = wallMoveCandidate.getTargetTile();
    Board board = quoridor.getBoard();
    int targetTileIndex = 9 * (targetTile.getRow() - 1) + targetTile.getColumn() - 1;

    // set moveMode to WallMove
    currentGame.setMoveMode(MoveMode.WallMove);

    switch (side) {

      case "left":
        // Check if wall can move towards specified side
        if (targetTile.getColumn() == 1) {
          throw new IllegalArgumentException("This move illegal!");
        }
        // Update wallMove candidate, and move Wall
        wallMoveCandidate.setTargetTile(board.getTile(targetTileIndex - 1)); // (col -1)
        // wallToMove.setMove(wallMoveCandidate);
        break;
      case "right":
        // Check if wall can move towards specified side
        if (targetTile.getColumn() == 8) {
          throw new IllegalArgumentException("This move illegal!");
        }
        // Update wallMove candidate
        wallMoveCandidate.setTargetTile(board.getTile(targetTileIndex + 1));
        // wallToMove.setMove(wallMoveCandidate);
        break;
      case "up":
        // Check if wall can move towards specified side
        if (targetTile.getRow() == 1) {
          throw new IllegalArgumentException("This move illegal!");
        }
        // Update wallMove candidate
        wallMoveCandidate.setTargetTile(board.getTile(targetTileIndex - 9));
        // wallToMove.setMove(wallMoveCandidate);
        break;
      case "down":
        // Check if wall can move towards specified side
        if (targetTile.getRow() == 8) {
          throw new IllegalArgumentException("This move illegal!");
        }
        // Update wallMove candidate
        wallMoveCandidate.setTargetTile(board.getTile(targetTileIndex + 9));
        // wallToMove.setMove(wallMoveCandidate);
        break;
      default:
        throw new IllegalArgumentException("Unsupported wall move side provided");
    }
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Confirms a wall move by converting a wall move candidate to an actual wall move. Throws
   *         an exception if the wall move candidate is invalid. (Associated to DropWall Gherkin
   *         feature)
   * 
   * @throws IllegalArgumentException
   * @param none
   * @return void
   */
  public static void dropWall() throws IllegalArgumentException {

    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    WallMove wallMoveCandidate = currentGame.getWallMoveCandidate();
    Wall wall = wallMoveCandidate.getWallPlaced();
    GamePosition currentPosition = currentGame.getCurrentPosition();
    Player currentPlayer = currentGame.getCurrentPosition().getPlayerToMove();

    String error = "";
    // Check if move is valid
    try {
      checkPathExistence();
    } catch (Exception e) {
      error += e.getMessage();
    }
    if (!validateWallMoveCandidate(wallMoveCandidate)) {
      error += "This move is illegal!";
    }

    if (error.trim().length() > 0)
      throw new RuntimeException(error);

    // Add wall on board (in new game position)
    currentPosition = currentGame.getCurrentPosition();
    if (currentPlayer.equals(currentGame.getWhitePlayer())) {
      currentPosition.addWhiteWallsOnBoard(wall);
    } else {
      currentPosition.addBlackWallsOnBoard(wall);
    }

    // Set wall move
    wall.setMove(wallMoveCandidate);

    // Remove wallMoveCandidate (confirm move and drop wall)
    currentGame.setWallMoveCandidate(null);

    // Update Player to move
    try {
      playerCompletesHisMove(currentPlayer);
    } catch (InvalidInputException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * @author Mael This controller method initializes a new game by creating a new game with the
   *         status "Initializing"
   */
  public static void initializeGame() {
    // must delete any potential previous games before initializing a new one

    // tearDown();

    // initialize a new game
    new Game(GameStatus.Initializing, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());

  }

  /**
   * @author Mael
   * @author Hugo Parent-Pothier
   * 
   * @feature SwitchCurrentPlayer
   * 
   *          This controller method is to be called at the end of a player's move. It sets the
   *          current player to move to the other player and sets the move mode to player move.
   * 
   * @param player
   * @throws InvalidInputException
   * 
   */
  public static void playerCompletesHisMove(Player player) throws InvalidInputException {
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Player currentPlayer = currentGame.getCurrentPosition().getPlayerToMove();
    if (!currentPlayer.equals(player)) {
      throw new IllegalArgumentException("The player is not the player to move!");
    }

    currentGame.getCurrentPosition().setPlayerToMove(currentPlayer.getNextPlayer());
    QuoridorApplication.getQuoridor().getCurrentGame().setMoveMode(MoveMode.PlayerMove);
  }

  /**
   * @author Mael This helper method returns the correct PlayerPosition of a player according to his
   *         color (getBlackPosition, getWhitePosition)
   * 
   * @param player : the player for who we want the PlayerPosition
   * @return PlayerPosition : return the position of the player
   * @throws InvalidInputException
   */
  public static PlayerPosition getPlayerPosition(Player player) throws InvalidInputException

  {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();

    Player whitePlayer = QuoridorApplication.getQuoridor().getCurrentGame().getWhitePlayer();
    Player blackPlayer = QuoridorApplication.getQuoridor().getCurrentGame().getBlackPlayer();

    if (player.equals(whitePlayer))
      return game.getCurrentPosition().getWhitePosition();
    else if (player.equals(blackPlayer))
      return game.getCurrentPosition().getBlackPosition();

    else
      throw new InvalidInputException("This player is not in the current game");
  }

  /**
   * @author ZhuzhenLi
   * 
   *         This method sets total thinking time for both white and black players of the board game
   *
   * @param totalthinkingTime: The total thinking time for each player
   * 
   * @throws InvalidInputException
   * @return void
   * 
   */
  public static void setTotalThinkingTime(Time totalthinkingTime) throws InvalidInputException {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();

    String error = "";

    if (totalthinkingTime == null) {
      error = "The thinkingTime does not exist.";
    } else if (!isValidThinkingTime(totalthinkingTime)) {
      error = "Total Thinking Time is not valid. ";
    }

    if (game == null) {
      error = "The game does not exist.";
    }

    if (error.length() > 0) {
      throw new InvalidInputException(error.trim());
    }

    Player whitePlayer = game.getWhitePlayer();
    Player blackPlayer = game.getBlackPlayer();

    // set white player thinking time
    if (whitePlayer == null) {
      throw new InvalidInputException("The whitePlayer does not exist.");
    }

    try {
      whitePlayer.setRemainingTime(totalthinkingTime);
    } catch (RuntimeException e) {
      throw new InvalidInputException(e.getMessage());
    }

    // set black player thinking time
    if (blackPlayer == null) {
      throw new InvalidInputException("The blackPlayer does not exist.");
    }

    try {
      blackPlayer.setRemainingTime(totalthinkingTime);
    } catch (RuntimeException e) {
      throw new InvalidInputException(e.getMessage());
    }

    // change game status for feature <startNewGame>
    game.setGameStatus(GameStatus.ReadyToStart);

  }

  /**
   * @author ZhuzhenLi
   * 
   *         This helper method determinate weather a given totalthinkingtime is valid or not
   * 
   * @param totalthinkingTime: The total thinking time to be checked if valid or not
   * 
   * @return boolean
   */
  @SuppressWarnings("deprecation")
  private static boolean isValidThinkingTime(Time totalthinkingTime) {

    int hours = totalthinkingTime.getHours();
    int minutes = totalthinkingTime.getMinutes();
    int seconds = totalthinkingTime.getSeconds();

    if (hours == 0 && minutes == 0 && seconds == 0) {
      return false; // check if totalthinkingTime is 0
    }
    if (hours < 0 || minutes < 0 || seconds < 0) {
      return false; // check if totalthinkingTime is negative
    }

    return true;
  }

  /**
   * @author ZhuzhenLi
   * 
   *         This method initializes a Quoridor board in its initial position; sets the pawn of both
   *         white and black player at their initial position; initializes the stock of walls for
   *         both white and black players; sets white as next player to move; starts counting down
   *         clock for the white player;
   * 
   * @param none
   * @throws InvalidInputException
   * @return void
   * 
   */
  public static void initializeBoard() throws InvalidInputException {

    Quoridor quoridor = QuoridorApplication.getQuoridor();
    if (quoridor == null) {
      throw new InvalidInputException("The quoridor does not exist.");
    }

    // create walls for each player of the current running game
    Game game = quoridor.getCurrentGame();

    Player player1 = game.getWhitePlayer();
    Player player2 = game.getBlackPlayer();

    if (player1 == null) {
      throw new InvalidInputException("The game does not have a white player.");
    }

    if (player2 == null) {
      throw new InvalidInputException("The game does not have a black player.");
    }
    // set player 1 and playe2 starting positions
    Tile player1StartPos = quoridor.getBoard().getTile(76);
    Tile player2StartPos = quoridor.getBoard().getTile(4);

    PlayerPosition player1Position = new PlayerPosition(player1, player1StartPos);
    PlayerPosition player2Position = new PlayerPosition(player2, player2StartPos);

    // set game position and the first player
    GamePosition gamePosition =
        new GamePosition(0, player1Position, player2Position, player1, game);

    // Add the walls as in stock for both players
    for (int j = 0; j < 10; j++) {
      Wall wall = player1.getWall(j);
      gamePosition.addWhiteWallsInStock(wall);
    }

    for (int j = 0; j < 10; j++) {
      Wall wall = player2.getWall(j);
      gamePosition.addBlackWallsInStock(wall);
    }

    // Check initial position of both pawns
    if (gamePosition.getWhitePosition() != player1Position
        || gamePosition.getBlackPosition() != player2Position) {
      throw new InvalidInputException("Incorrect initial position of both pawns.");
    }

    // Check that 10 walls are on stock (no walls on board)
    if (gamePosition.getBlackWallsInStock().size() != 10
        || gamePosition.getWhiteWallsInStock().size() != 10) {
      throw new InvalidInputException("There should be 10 walls on stock for each player.");
    }

    game.setCurrentPosition(gamePosition);
    // List<GamePosition> gamePositions = game.getPositions();
    // GamePosition firstgamePosition = gamePositions.get(gamePositions.size()-1);
    //
    //
    // System.out.println("============firstgamePosition=========="+firstgamePosition.getWhitePosition().getTile().getColumn());
    // System.out.println("============firstgamePosition=========="+firstgamePosition.getWhitePosition().getTile().getRow());
    // System.out.println("============firstgamePosition=========="+firstgamePosition.getBlackPosition().getTile().getColumn());
    // System.out.println("============firstgamePosition=========="+firstgamePosition.getBlackPosition().getTile().getRow());
    //

    // set white player to move
    game.getCurrentPosition().setPlayerToMove(player1);

    // start count down clock for white player (player to move)
    countDownForPlayer();

  }

  /**
   * @author Mael
   * 
   *         This controller method periodically (every second) decrements the playerToMove's
   *         remainingTime This is how we implemented our clock, you call it once at the start of a
   *         game and it keeps running
   * 
   * @return void
   */
  public static void countDownForPlayer() {
    // When the clock starts, the game is running
    QuoridorApplication.getQuoridor().getCurrentGame().setGameStatus(GameStatus.Running);

    // used by ScheduledExecutorService to decrement thinking time each second
    Runnable decrementClock = new Runnable() {
      public void run() {
        if (QuoridorApplication.getQuoridor().getCurrentGame()
            .getGameStatus() == GameStatus.Running) {
          // get playerToMove's remaining thinking time
          Player player = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
              .getPlayerToMove();
          Time time = player.getRemainingTime();

          // get the total remaining seconds
          int totalSecs = timeToSeconds(time);

          // remove 1 seconds from thinking time and set it as the new thinking time for
          // the player
          if (totalSecs != 0) {
            totalSecs--;
            time = secondsToTime(totalSecs);
            player.setRemainingTime(time);
            MyQuoridor.refreshTimer();
          } else
            theClockOfPlayerCountsDownToZero(player);
        }
      }
    };

    // call decrementClock runnable every second
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(decrementClock, 0, 1, TimeUnit.SECONDS);
  }

  /**
   * @author Mael
   * 
   *         This helper method converts Time into seconds as an int this is used by
   *         countDownForPlayer() to decrement the thinking time (needs an int)
   * 
   * @param time : This is the time value that needs to be converted into seconds
   * @return int
   */
  public static int timeToSeconds(Time time) {
    // the getMinutes() and getSeconds() functions for Time are all deprecated so we
    // use Calendar
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(time);
    int hours = calendar.get(Calendar.HOUR);
    int minutes = calendar.get(Calendar.MINUTE);
    int seconds = calendar.get(Calendar.SECOND);

    //
    int timeInSeconds = hours * 3600 + minutes * 60 + seconds;

    return timeInSeconds;
  }

  /**
   * @author Mael
   * 
   *         This helper method converts seconds(int) into a Time this is used by
   *         countDownForPlayer() to update a player's thinking time (needs Time)
   * 
   * @param totalSeconds : This is the seconds(int) that need to be converted into a Time
   * @return int
   */
  @SuppressWarnings("deprecation")
  public static Time secondsToTime(int totalSeconds) {
    // get individual minutes and seconds from the totalSeconds
    int minutes = totalSeconds / 60;
    int seconds = totalSeconds % 60;

    // create a Time value using the minutes and seconds
    Time time = new java.sql.Time(0, minutes, seconds);

    return time;
  }

  /**
   * @author Mael
   * @author Hugo Parent-Pothier
   * 
   *         This controller method moves the player to the corresponding tile based on the side of
   *         the move (left, right, up, down). If the move is illegal, an error message gets
   *         displayed in the view.
   * 
   * @param player The player who should be moved
   * @param side The side to move (left, right, down, up)
   * @throws InvalidInputException
   */
  public static void movePlayer(Player player, String side) throws InvalidInputException {

    PawnBehavior currentStateMachine;
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    Player playerToMove = currentGame.getCurrentPosition().getPlayerToMove();

    // Check if player is player to move
    if (!player.equals(playerToMove)) {
      throw new InvalidInputException("Player is not the player to move!");
    }

    // if no state machines have ever been created, create one for both players
    if (whiteStateMachine == null) {

      // System.out.println("=========new whiteSM");
      whiteStateMachine = new PawnBehavior(currentGame.getWhitePlayer());
    }

    if (blackStateMachine == null) {
      // System.out.println("=========new blackSM");
      blackStateMachine = new PawnBehavior(currentGame.getBlackPlayer());
    }

    // get the current player's state machine
    if (playerToMove.hasGameAsBlack()) { // this is black player
      currentStateMachine = blackStateMachine;
    } else {// this is a white player
      currentStateMachine = whiteStateMachine;
    }
    // For debugging
    System.out.println("Initial State of black player: "
        + blackStateMachine.getPawnSMMovingVerticalVertical().toString() + ", "
        + blackStateMachine.getPawnSMMovingHorizontalHorizontal().toString());
    System.out.println("Initial position of black player:  row : "
        + currentGame.getCurrentPosition().getBlackPosition().getTile().getRow() + ", col : "
        + currentGame.getCurrentPosition().getBlackPosition().getTile().getColumn());

    System.out.println("Initial State of white player: "
        + whiteStateMachine.getPawnSMMovingVerticalVertical().toString() + ", "
        + whiteStateMachine.getPawnSMMovingHorizontalHorizontal().toString());
    System.out.println("Initial position of white player:  row : "
        + currentGame.getCurrentPosition().getWhitePosition().getTile().getRow() + ", col : "
        + currentGame.getCurrentPosition().getWhitePosition().getTile().getColumn());

    // Call move player
    switch (side) {
      case "up":
        System.out.println("=====moveUp");
        currentStateMachine.moveUp();
        break;

      case "down":
        System.out.println("=====moveDown");
        currentStateMachine.moveDown();
        break;

      case "right":
        System.out.println("=====moveRight");
        currentStateMachine.moveRight();
        break;

      case "left":
        System.out.println("=====moveLeft");
        currentStateMachine.moveLeft();
        break;

      case "upleft":
        System.out.println("=====moveUpLeft");
        currentStateMachine.moveUpLeft();
        break;
      case "upright":
        System.out.println("=====moveUpRight");
        currentStateMachine.moveUpRight();
        break;
      case "downleft":
        System.out.println("=====moveDownLeft");
        currentStateMachine.moveDownLeft();
        break;
      case "downright":
        System.out.println("=====moveDownRight");
        currentStateMachine.moveDownRight();
        break;
      default:
        throw new InvalidInputException("Illegal side provided!");

    }
  }

  /**
   * @author Mael This controller method stops a game thats running by setting the player to move to
   *         neither the white or black player This is called by the step definition tests and sets
   *         the game status automatically to black won
   */
  public static void theGameIsNoLongerRunning() {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game game = quoridor.getCurrentGame();
    game.setGameStatus(GameStatus.BlackWon);// for test

    // create a new empty player that we'll set as player to move to stop this game
    // this essentially stops the clock of both players
    // and stops them from moving
    @SuppressWarnings("deprecation")
    Time remainingTime = new Time(1, 1, 1);
    User user = new User("empty", quoridor);
    Player emptyPlayer = new Player(remainingTime, user, 9, Direction.Vertical);
    game.getCurrentPosition().setPlayerToMove(emptyPlayer);

  }

  /**
   * @author Mael This controller method stops a game thats running by setting the game status to
   *         stopped
   */
  public static void stopGame() {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game game = quoridor.getCurrentGame();
    game.setGameStatus(GameStatus.Stopped);
  }

  /**
   * @author Mael This controller method resumes a game that wasn't running by setting the game
   *         status back to running
   */
  public static void resumeGame() {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game game = quoridor.getCurrentGame();
    game.setGameStatus(GameStatus.Running);
  }



  /**
   * @author Mael This controller method sets the correct game status based on which player's clock
   *         has ran out
   * @param player : the player who's time has exceeded
   */
  @SuppressWarnings("deprecation")
  public static void theClockOfPlayerCountsDownToZero(Player player) {
    player.setRemainingTime(new Time(0, 0, 0));

    // Then Game result shall be "<result>"
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    if (player.hasGameAsWhite()) {
      game.setGameStatus(GameStatus.BlackWon);
    } else {
      game.setGameStatus(GameStatus.WhiteWon);
    }
    MyQuoridor.refreshPlayerTurnLbl();
  }

  /**
   * @author Mael This is a helper to return the index of an inputed tile using it's row and col
   *         ints
   * @param tile
   * @return i which is the index position on the tile which is an int
   */
  public static int getIndexFromTile(Tile tile) {
    int row = tile.getRow();
    int col = tile.getColumn();
    int i = (row - 1) * 9 + (col - 1);
    return i;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Creates a user with the username given as the argument. Throws an exception if the
   *         username is not valid or if a user with the same username already exists. (Associated
   *         to ProvideSelectUsername Gherkin feature)
   * 
   * @param username The name of the new user
   * @throws IllegalArgumentException
   * @return user of type User
   */
  public static User createUser(String username) throws IllegalArgumentException {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    User user = null;
    try {
      checkValidUsername(username, quoridor);
      user = new User(username, quoridor);
    } catch (RuntimeException e) {
      throw new IllegalArgumentException(e);

    }
    return user;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Assigns the player with the given username to the given player. Applies to an
   *         unassigned player, giving priority to the white player. Throws an exception if one of
   *         the parameters are invalid or if the assignment is not allowed. (Associated to
   *         ProvideSelectUsername Gherkin feature)
   * 
   * @param user The user to assign to the player.
   * @throws IllegalArgumentException
   * 
   */
  public static void assignUserToPlayer(User user) throws IllegalArgumentException { // Assigns
                                                                                     // white player
                                                                                     // first
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();

    if (!currentGame.hasWhitePlayer()) {
      // create white player
      try {
        currentGame.setWhitePlayer(new Player(new Time(180), user, 1, Direction.Vertical));
      } catch (RuntimeException e) {
        throw new IllegalArgumentException(e);
      }
    }

    else if (!currentGame.hasBlackPlayer()) {
      try {
        // create black player
        currentGame.setBlackPlayer(new Player(new Time(180), user, 9, Direction.Vertical));
      } catch (RuntimeException e) {
        throw new IllegalArgumentException(e);
      }
      // create black player
      currentGame.setBlackPlayer(new Player(new Time(180), user, 9, Direction.Vertical));

      // Set next player for both players (both players now exist)
      Player whitePlayer = currentGame.getWhitePlayer();
      Player blackPlayer = currentGame.getBlackPlayer();
      whitePlayer.setNextPlayer(blackPlayer);
      blackPlayer.setNextPlayer(whitePlayer);

    } else
      throw new IllegalArgumentException("The two players have already been assigned");

  }

  /**
   * @author Mahroo Rahman
   * @throws InvalidInputException
   * @returns void
   */
  public static void wallGrab() throws InvalidInputException {
    if (QuoridorApplication.getQuoridor().getCurrentGame().hasWallMoveCandidate()) {
      System.out.println("==================ALREADY HAS WALLMOVECANDIDATE");

      return;
    }
    // set game move mode to wall move
    QuoridorApplication.getQuoridor().getCurrentGame().setMoveMode(MoveMode.WallMove);

    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    Player player = currentGame.getCurrentPosition().getPlayerToMove();

    int numofwallsinstock;
    if (player.getGameAsBlack() == null)
      numofwallsinstock = currentGame.getCurrentPosition().numberOfWhiteWallsInStock();
    else
      numofwallsinstock = currentGame.getCurrentPosition().numberOfBlackWallsInStock();

    List<Move> currentMoveList = currentGame.getMoves();
    int numOfMoves = currentGame.getMoves().size();
    Move newMove = null;

    Tile tile = quoridor.getBoard().getTile(40);// place wall move candidate "center" tile

    if (numofwallsinstock == 0) {
      System.out.println("==================NO WALLS IN STOCK");
      throw new InvalidInputException("The number of walls in the stock is 0");
    }

    else {
      System.out.println("==================WALLS IN STOCK");
      Wall wall;
      // getting the wall from 0th position of the linked list
      if (player.getGameAsBlack() == null) {

        wall = quoridor.getCurrentGame().getCurrentPosition().getWhiteWallsInStock(0);
      }

      else {
        wall = currentGame.getCurrentPosition().getBlackWallsInStock(0);
      }

      // get the first wall from stock
      if (wall != null) {
        System.out.println("==================WALL != NULL");
        // need to create a wall move candidate

        // no moves in the list
        if (numOfMoves == 0) {
          System.out.println("==================numOfMoves == 0");

          // create the new move
          System.out.println("===size of list of moves: " + currentGame.getMoves().size());
          System.out.println("===Creating new wall move");
          newMove = new WallMove(1, 1, player, tile, currentGame, Direction.Horizontal, wall);
          currentGame.addMove(newMove);
          System.out.println("===size of list of moves: " + currentGame.getMoves().size());

        }
        // other moves in the list
        else {

          // to get the move & round number
          Move lastMove = currentMoveList.get(numOfMoves - 1);
          int nextMoveNumber = lastMove.getMoveNumber() + (lastMove.getRoundNumber() - 1);
          int nextMoveRound = (lastMove.getRoundNumber() % 2) + 1;

          // create the new move
          System.out.println("===size of list of moves: " + currentGame.getMoves().size());
          System.out.println("===Creating new wall move");
          newMove = new WallMove(nextMoveNumber, nextMoveRound, player, tile, currentGame,
              Direction.Horizontal, wall);
          currentGame.addMove(newMove);
          System.out.println("===size of list of moves: " + currentGame.getMoves().size());

          // update previous/next pointers
          lastMove.setNextMove(newMove);
          newMove.setPrevMove(lastMove);
        }

        // created the wall move candidate
        currentGame.setWallMoveCandidate((WallMove) newMove);
        System.out.println("==================WALLMOVECANDIDATE");

        // create new game position for move and set it to current position (duplicate
        // everything except id)
        newCurrentPosition();

        // removed the wall from the stock
        if (player.getGameAsBlack() == null) {
          quoridor.getCurrentGame().getCurrentPosition().removeWhiteWallsInStock(wall);
        } else {
          quoridor.getCurrentGame().getCurrentPosition().removeBlackWallsInStock(wall);
        }

      }
    }
  }

  /**
   * @author Mahroo Rahman
   * @param none
   * 
   *        The wall covers two tiles so either two rows and one column or one row and two columns
   *        of the board so we keep a row and column as pivot over which the wall flips. The user
   *        passes the current direction of the wall, the row number and the column number. The
   *        controller changes the direction by 90 degrees. So, if the current direction is
   *        horizontal it will become vertical and if vertical then becomes horizontal. the row and
   *        column remains same.
   * @throws nothing
   * @returns void
   */

  public static void flipWall() {
    // throw new UnsupportedOperationException();
    // quoridor.getCurrentGame().getWallMoveCandidate().getTargetTile().getColumn();
    // quoridor.getCurrentGame().getWallMoveCandidate().getTargetTile().getRow();
    // quoridor.getCurrentGame().getWallMoveCandidate().setTargetTile(aNewTargetTile);
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    if (quoridor.getCurrentGame().hasWallMoveCandidate()) {
      Direction direction = quoridor.getCurrentGame().getWallMoveCandidate().getWallDirection();

      if (direction.equals(Direction.Horizontal)) {
        quoridor.getCurrentGame().getWallMoveCandidate().setWallDirection(Direction.Vertical);
      } else if (direction.equals(Direction.Vertical)) {
        quoridor.getCurrentGame().getWallMoveCandidate().setWallDirection(Direction.Horizontal);
      }
    }

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Asserts that a given pawn position is valid on the board, but does not check if
   *         multiple pawns are on the same tile. Returns true if valid, and false if invalid.
   *         (Associated to ValidatePosition Gherkin feature)
   * 
   * @param row The row position to validate.
   * @param col The column position to validate.
   * 
   * @return isValid
   */
  public static boolean validatePawnPosition(int row, int col) {
    if (row < 1 || row > 9 || col < 1 || col > 9) {
      return false;
    } else
      return true;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Asserts that a given wall position is valid on the board, but does not check for
   *         overlapping walls. Returns true if valid, and false if invalid. (Associated to
   *         ValidatePosition Gherkin feature)
   * 
   * @param row The row position to validate.
   * @param col The column position to validate.
   * @param dir The Wall direction to validate.
   * 
   * @return isValid
   */
  public static boolean validateWallPosition(int row, int col, Direction dir) {
    if (col < 1 || col > 8 || row < 1 || row > 8) {
      return false;
    } else
      return true;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Asserts that given wall move candidate has a valid position on the board. Returns true
   *         if valid, and false if invalid. (Associated to drop wall Gherkin feature)
   * 
   * @param wallMoveCandidate The wall move candidate to validate.
   * 
   * @return The validity of the wall candidate.
   */
  public static boolean validateWallMoveCandidate(WallMove wallMoveCandidate) {

    List<Wall> walls = getWallsOnBoard();
    int col = wallMoveCandidate.getTargetTile().getColumn();
    int row = wallMoveCandidate.getTargetTile().getRow();
    Direction dir = wallMoveCandidate.getWallDirection();
    Wall wallToPlace = wallMoveCandidate.getWallPlaced();
    // Check tile on board
    if (col < 1 || col > 8 || row < 1 || row > 8) {
      return false;
    }
    // Check Same coord
    if (getWallAtRowCol(walls, row, col) != null) {
      return false;
    }
    // Check overlapping
    if (dir == Direction.Vertical) {
      if (hasVerticalWallAbove(walls, wallToPlace.getMove().getTargetTile())
          || hasVerticalWallBelow(walls, wallToPlace.getMove().getTargetTile())) {
        return false;
      }
    }
    if (dir == Direction.Horizontal) {
      if (hasHorizontalWallToLeft(walls, wallToPlace.getMove().getTargetTile())
          || hasHorizontalWallToRight(walls, wallToPlace.getMove().getTargetTile())) {
        return false;
      }
    }

    return true;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Asserts that no walls on the board are overlapping in the current game position.
   *         Returns true if no walls are overlapping, returns false otherwise. (Associated to
   *         validate position Gherkin feature)
   * 
   * @return isValid
   */
  public static boolean validateOverlappingWalls() { /*
                                                      * Assumes any wall on board has corresponding
                                                      * wall move
                                                      */

    // from left to right
    GamePosition currentPosition =
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
    List<Wall> wallsOnBoard = new ArrayList<Wall>();
    if (currentPosition.hasWhiteWallsOnBoard()) {
      wallsOnBoard.addAll(currentPosition.getWhiteWallsOnBoard());
    }
    if (currentPosition.hasBlackWallsOnBoard()) {
      wallsOnBoard.addAll(currentPosition.getBlackWallsOnBoard());
    }

    List<Wall> wallsByRow;
    List<Wall> wallsByCol;

    Wall currentWall;

    for (int col = 1; col <= 8; col++) {
      wallsByCol = getWallsByCol(wallsOnBoard, col);
      for (int row = 1; row <= 8; row++) {

        // check same coord
        wallsByRow = getWallsByRow(wallsByCol, row);
        if (wallsByRow.size() == 0) {
          continue; // no wall at this coord
        }
        if (wallsByRow.size() > 1) {
          return false; // Many walls on same coordinate
        }

        currentWall = wallsByRow.get(0);

        // check vertical overlap
        if (currentWall.getMove().getWallDirection().equals(Direction.Vertical)) {
          if (hasVerticalWallBelow(wallsByCol, currentWall.getMove().getTargetTile())) {
            return false;
          }
        }
        // check horizontal overlap
        else {
          if (hasHorizontalWallToRight(getWallsByRow(wallsOnBoard, row),
              currentWall.getMove().getTargetTile())) {
            return false;
          }
        }
      }

    }
    return true;
  }

  /**
   * This method checks if a game file with a given file name exists in the file system.
   *
   *
   * @author Hao Shu
   * @param fileName name of the file to check
   * @return true if the file exists, false if not exist
   *
   *
   */
  public static boolean checkExistingFile(String fileName) {

    File gameFile = new File("src/../" + fileName + ".dat"); // initialized the game file to check

    return gameFile.exists();

  }

  /**
   * 
   * This method enables user to delete a saved game with a given file name in the file system.
   *
   * @author Hao Shu
   * @param fileName name of the file to delete
   * @return void
   * 
   *
   */

  public static void deleteFile(String fileName) {

    File gameFile = new File("src/../" + fileName + ".dat"); // initialized the game file to delete

    if (gameFile.delete() == false) {
      System.out.print("Failed to delete the game file");
    }

  }

  /**
   * This method enables user to save game position with a file name in the file system.
   *
   * @author Hao Shu
   * @param fileName name of the file to save
   * @return true if the file is successfully saved, false if it is not saved
   * @throws UnsupportedOperationException
   */

  public static boolean saveFile(String fileName) {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    List<Wall> whiteWall = game.getCurrentPosition().getWhiteWallsOnBoard();
    List<Wall> blackWall = game.getCurrentPosition().getBlackWallsOnBoard();

    boolean saveStatus = false;
    int whiteRow;
    int whiteCol;
    char whiteColChar;
    int blackRow;
    int blackCol;
    char blackColChar;

    Integer whiteWallRow = null;
    Integer whiteWallCol = null;
    char whiteWallColChar = 0;
    Integer blackWallRow = null;
    Integer blackWallCol = null;
    char blackWallColChar = 0;
    Direction whiteDir = null;
    Direction blackDir = null;

    String whiteWallInfo = null;
    String blackWallInfo = null;

    // white player's position
    whiteRow = game.getCurrentPosition().getWhitePosition().getTile().getRow();
    whiteCol = game.getCurrentPosition().getWhitePosition().getTile().getColumn();

    whiteColChar = numToAlphabet(whiteCol);// Convert the column to letters

    // black player's position
    blackRow = game.getCurrentPosition().getBlackPosition().getTile().getRow();
    blackCol = game.getCurrentPosition().getBlackPosition().getTile().getColumn();

    blackColChar = numToAlphabet(blackCol);// Convert the column to letters

    for (Wall wall : whiteWall) {
      whiteDir = wall.getMove().getWallDirection();
      whiteWallCol = wall.getMove().getTargetTile().getColumn();
      whiteWallRow = wall.getMove().getTargetTile().getRow();
      whiteWallColChar = numToAlphabet(whiteWallCol);// Convert the column to letters

      whiteWallInfo += "," + String.valueOf(whiteWallColChar) + Integer.toString(whiteWallRow)
          + dirAbbreviation(whiteDir);

    }


    for (Wall wall : blackWall) {
      blackDir = wall.getMove().getWallDirection();
      blackWallCol = wall.getMove().getTargetTile().getColumn();
      blackWallRow = wall.getMove().getTargetTile().getRow();
      blackWallColChar = numToAlphabet(blackWallCol);// Convert the column to letters

      blackWallInfo += "," + String.valueOf(blackWallColChar) + Integer.toString(blackWallRow)
          + dirAbbreviation(blackDir);


    }

    if (whiteWallInfo != null && blackWallInfo != null) {
      whiteWallInfo = whiteWallInfo.replace("null", "");
      blackWallInfo = blackWallInfo.replace("null", "");
    } else if (whiteWallInfo == null) {
      whiteWallInfo = "";
      blackWallInfo = "";
    } else if (blackWallInfo == null) {
      blackWallInfo = "";
      blackWallInfo = "";
    }
    if (game.getCurrentPosition().hasWhiteWallsOnBoard() == false) {

    }
    File gameFile = new File("src/../" + fileName + ".dat"); // initialized the game file to save
    try {
      if (gameFile.exists() == false) {
        gameFile.createNewFile();
        FileWriter fw = new FileWriter(gameFile);
        BufferedWriter bw = new BufferedWriter(fw);

        if (game.getCurrentPosition().getPlayerToMove() == game.getBlackPlayer()) {

          bw.write("B: " + blackColChar + blackRow + blackWallInfo + "\r\n" + "W: " + whiteColChar
              + whiteRow + whiteWallInfo);
          bw.close();
        } else {

          bw.write("W: " + whiteColChar + whiteRow + whiteWallInfo + "\r\n" + "B: " + blackColChar
              + blackRow + blackWallInfo);
          bw.close();

        }
        System.out.println("File written Successfully");
        saveStatus = true;
      } else {

        System.out.println("File already exists");
        saveStatus = false;
      }
    } catch (IOException e) {

      System.out.println("Save game failed");
    }
    return saveStatus;

  }

  /**
   * This method enables user to to save the current game in a text file even if the game has not
   * yet been finished so that the user can continue or review it later.
   *
   * @author Hao Shu
   * @param fileName name of the file to save
   * @return true if the file is successfully saved, false if it is not saved
   * @throws UnsupportedOperationException
   */

  public static boolean saveGame(String fileName) {
    Game game = QuoridorApplication.getQuoridor().getCurrentGame();


    boolean saveStatus = false;
    int whiteRow;
    int whiteCol;
    char whiteColChar;
    int blackRow;
    int blackCol;
    char blackColChar;

    Integer roundNum = 1;
    Integer whiteWallRow = null;
    Integer whiteWallCol = null;
    char whiteWallColChar = 0;
    Integer blackWallRow = null;
    Integer blackWallCol = null;
    char blackWallColChar = 0;
    Direction whiteDir = null;
    Direction blackDir = null;

    String roundResult = null;


    for (int i = 1; i < game.getPositions().size(); i += 2) {

      if (game.getMove(game.getMoves().size() - 1) instanceof StepMove) { // this move was a step
                                                                          // move therefore it
        whiteRow = game.getPosition(i).getWhitePosition().getTile().getRow();
        whiteCol = game.getPosition(i).getWhitePosition().getTile().getColumn();
        whiteColChar = numToAlphabet(whiteCol);// Convert the column to letters

        blackRow = game.getPosition(i).getBlackPosition().getTile().getRow();
        blackCol = game.getCurrentPosition().getBlackPosition().getTile().getColumn();

        blackColChar = numToAlphabet(blackCol);// Convert the column to letters

        roundResult += Integer.toString(roundNum) + ". " + String.valueOf(whiteColChar)
            + Integer.toString(whiteRow) + " " + String.valueOf(blackColChar)
            + Integer.toString(blackRow) + "\r\n";

      }


      else if (game.getMove(game.getMoves().size() - 1) instanceof WallMove) { // this move was a
                                                                               // wall move
                                                                               // therefore it
        Integer wallIndex = 0;
        Wall whiteWall = game.getPosition(i).getWhiteWallsOnBoard(wallIndex);
        Wall blackWall = game.getPosition(i).getBlackWallsOnBoard(wallIndex);

        whiteDir = whiteWall.getMove().getWallDirection();
        whiteWallCol = whiteWall.getMove().getTargetTile().getColumn();
        whiteWallRow = whiteWall.getMove().getTargetTile().getRow();
        whiteWallColChar = numToAlphabet(whiteWallCol);// Convert the column to letters

        blackDir = blackWall.getMove().getWallDirection();
        blackWallCol = blackWall.getMove().getTargetTile().getColumn();
        blackWallRow = blackWall.getMove().getTargetTile().getRow();
        blackWallColChar = numToAlphabet(blackWallCol);// Convert the column to letters

        roundResult += Integer.toString(roundNum) + ". " + String.valueOf(whiteWallColChar)
            + Integer.toString(whiteWallRow) + whiteDir + " " + String.valueOf(blackWallColChar)
            + Integer.toString(blackWallRow) + blackDir + "\r\n";
      }

      roundResult = roundResult.replace("null", "");

      roundNum++;
    }
    // black player's position
    File gameFile = new File("src/../" + fileName + ".mov"); // initialized the game file to save
    try {
      if (gameFile.exists() == false) {
        gameFile.createNewFile();
        FileWriter fw = new FileWriter(gameFile);
        BufferedWriter bw = new BufferedWriter(fw);


        bw.write(roundResult);
        bw.close();
        System.out.println("File written Successfully");
        saveStatus = true;
      } else {

        System.out.println("File already exists");
        saveStatus = false;
      }
    } catch (IOException e) {

      System.out.println("Save game failed");

    }
    return saveStatus;

  }

  /**
   * This method enables user to overwrite a existing game position file with a new game position
   * file when they have the same file name in the file system.
   *
   * If the input is true, the file will be overwritten. If the input is false, the file will not be
   * overwritten.
   *
   * @author Hao Shu
   * @param overwriteOption
   * @return void
   * @throws UnsupportedOperationException
   */

  public static void overwriteExistingFile(String fileName, boolean overwriteOption) {

    Game game = QuoridorApplication.getQuoridor().getCurrentGame();
    List<Wall> whiteWall = game.getCurrentPosition().getWhiteWallsOnBoard();
    List<Wall> blackWall = game.getCurrentPosition().getBlackWallsOnBoard();

    boolean saveStatus = false;
    int whiteRow;
    int whiteCol;
    char whiteColChar;
    int blackRow;
    int blackCol;
    char blackColChar;

    Integer whiteWallRow = null;
    Integer whiteWallCol = null;
    char whiteWallColChar = 0;
    Integer blackWallRow = null;
    Integer blackWallCol = null;
    char blackWallColChar = 0;
    Direction whiteDir = null;
    Direction blackDir = null;

    String whiteWallInfo = null;
    String blackWallInfo = null;

    // white player's position
    whiteRow = game.getCurrentPosition().getWhitePosition().getTile().getRow();
    whiteCol = game.getCurrentPosition().getWhitePosition().getTile().getColumn();

    whiteColChar = numToAlphabet(whiteCol);// Convert the column to letters

    // black player's position
    blackRow = game.getCurrentPosition().getBlackPosition().getTile().getRow();
    blackCol = game.getCurrentPosition().getBlackPosition().getTile().getColumn();

    blackColChar = numToAlphabet(blackCol);// Convert the column to letters

    for (Wall wall : whiteWall) {
      whiteDir = wall.getMove().getWallDirection();
      whiteWallCol = wall.getMove().getTargetTile().getColumn();
      whiteWallRow = wall.getMove().getTargetTile().getRow();
      whiteWallColChar = numToAlphabet(whiteWallCol);// Convert the column to letters

      whiteWallInfo += "," + String.valueOf(whiteWallColChar) + Integer.toString(whiteWallRow)
          + dirAbbreviation(whiteDir);

    }


    for (Wall wall : blackWall) {
      blackDir = wall.getMove().getWallDirection();
      blackWallCol = wall.getMove().getTargetTile().getColumn();
      blackWallRow = wall.getMove().getTargetTile().getRow();
      blackWallColChar = numToAlphabet(blackWallCol);// Convert the column to letters

      blackWallInfo += "," + String.valueOf(blackWallColChar) + Integer.toString(blackWallRow)
          + dirAbbreviation(blackDir);


    }

    if (whiteWallInfo != null && blackWallInfo != null) {
      whiteWallInfo = whiteWallInfo.replace("null", "");
      blackWallInfo = blackWallInfo.replace("null", "");
    } else if (whiteWallInfo == null) {
      whiteWallInfo = "";
    } else if (blackWallInfo == null) {
      blackWallInfo = "";
    }
    if (game.getCurrentPosition().hasWhiteWallsOnBoard() == false) {

    }
    File gameFile = new File("src/../" + fileName + ".dat"); // initialized the game file to save
    try {
      if (overwriteOption) {
        gameFile.createNewFile();
        FileWriter fw = new FileWriter(gameFile);
        BufferedWriter bw = new BufferedWriter(fw);

        if (game.getCurrentPosition().getPlayerToMove() == game.getBlackPlayer()) {

          bw.write("B: " + blackColChar + blackRow + blackWallInfo + "\r\n" + "W: " + whiteColChar
              + whiteRow + whiteWallInfo);
          bw.close();
        } else {

          bw.write("W: " + whiteColChar + whiteRow + whiteWallInfo + "\r\n" + "B: " + blackColChar
              + blackRow + blackWallInfo);
          bw.close();

        }
        System.out.println("File overwritten Successfully");
      } else {

        System.out.println("Overwrite cancelled");

      }
    } catch (IOException e) {

      System.out.println("overwrite game failed");

    }

  }

  /**
   * This method enables user to overwrite a existing game file with a new game file when they have
   * the same file name in the file system.
   *
   * If the input is true, the file will be overwritten. If the input is false, the file will not be
   * overwritten.
   *
   * @author Hao Shu
   * @param overwriteOption
   * @return void
   * @throws UnsupportedOperationException
   */

  public static void overwriteExistingGameFile(String fileName, boolean overwriteOption) {

    Game game = QuoridorApplication.getQuoridor().getCurrentGame();


    boolean saveStatus = false;
    int whiteRow;
    int whiteCol;
    char whiteColChar;
    int blackRow;
    int blackCol;
    char blackColChar;

    Integer roundNum = 1;
    Integer whiteWallRow = null;
    Integer whiteWallCol = null;
    char whiteWallColChar = 0;
    Integer blackWallRow = null;
    Integer blackWallCol = null;
    char blackWallColChar = 0;
    Direction whiteDir = null;
    Direction blackDir = null;

    String roundResult = null;


    for (int i = 1; i < game.getPositions().size(); i += 2) {

      if (game.getMoveMode() == MoveMode.PlayerMove) {



        whiteRow = game.getPosition(i).getWhitePosition().getTile().getRow();
        whiteCol = game.getPosition(i).getWhitePosition().getTile().getColumn();
        whiteColChar = numToAlphabet(whiteCol);// Convert the column to letters

        blackRow = game.getPosition(i).getBlackPosition().getTile().getRow();
        blackCol = game.getCurrentPosition().getBlackPosition().getTile().getColumn();

        blackColChar = numToAlphabet(blackCol);// Convert the column to letters


        roundResult += Integer.toString(roundNum) + ". " + String.valueOf(whiteColChar)
            + Integer.toString(whiteRow) + " " + String.valueOf(blackColChar)
            + Integer.toString(blackRow) + "\r\n";

      }


      if (game.getMoveMode() == MoveMode.WallMove) {
        Integer wallIndex = 0;
        Wall whiteWall = game.getPosition(i).getWhiteWallsOnBoard(wallIndex);
        Wall blackWall = game.getPosition(i).getBlackWallsOnBoard(wallIndex);

        whiteDir = whiteWall.getMove().getWallDirection();
        whiteWallCol = whiteWall.getMove().getTargetTile().getColumn();
        whiteWallRow = whiteWall.getMove().getTargetTile().getRow();
        whiteWallColChar = numToAlphabet(whiteWallCol);// Convert the column to letters

        blackDir = blackWall.getMove().getWallDirection();
        blackWallCol = blackWall.getMove().getTargetTile().getColumn();
        blackWallRow = blackWall.getMove().getTargetTile().getRow();
        blackWallColChar = numToAlphabet(blackWallCol);// Convert the column to letters

        roundResult += Integer.toString(roundNum) + ". " + String.valueOf(whiteWallColChar)
            + Integer.toString(whiteWallRow) + whiteDir + " " + String.valueOf(blackWallColChar)
            + Integer.toString(blackWallRow) + blackDir + "\r\n";
      }

      roundResult = roundResult.replace("null", "");
      roundNum++;

    }
    // black player's position
    File gameFile = new File("src/../" + fileName + ".mov"); // initialized the game file to save
    try {
      if (overwriteOption) {
        gameFile.createNewFile();
        FileWriter fw = new FileWriter(gameFile);
        BufferedWriter bw = new BufferedWriter(fw);


        bw.write(roundResult);
        bw.close();
        System.out.println("File overwritten Successfully");
      } else {

        System.out.println("Overwrite cancelled");

      }
    } catch (IOException e) {

      System.out.println("overwrite game failed");
    }

  }

  /**
   * This method enables user to load a saved game with a given file name in the file system.
   *
   * @author Hao Shu
   * @param fileName name of the file to load
   * @param whitePlayer
   * @param blackPlayer
   * @return the loaded game
   * @throws IOException
   */

  public static Game loadFile(String fileName, Player whitePlayer, Player blackPlayer)
      throws Exception {
    FileInputStream inputstream = null;
    try {
      inputstream = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {

      throw (new Exception("File not found"));
    }

    Scanner scanner = new Scanner(inputstream);
    String line1 = scanner.nextLine();
    String line2 = scanner.nextLine();
    String checkLine = null;
    String delims = "[ :,]+";
    int moveNumber;
    int roundNumber;

    List<Wall> whiteWalls = new ArrayList<Wall>();
    List<Wall> blackWalls = new ArrayList<Wall>();
    Direction dir = null;

    Player playerToMove = null;
    Player nextPlayer = null;
    PlayerPosition blackposition = null;
    PlayerPosition whiteposition = null;
    Tile playerTile = null;
    Game game =
        new Game(GameStatus.Initializing, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());

    // check player's turn
    if (line1.charAt(0) == 'B') {
      playerToMove = blackPlayer;
      nextPlayer = whitePlayer;

    }
    if (line1.charAt(0) == 'W') {
      playerToMove = whitePlayer;
      nextPlayer = blackPlayer;
    }

    playerToMove.setNextPlayer(nextPlayer);
    nextPlayer.setNextPlayer(playerToMove);

    // fetch data from two lines
    for (int i = 1; i <= 2; i++) {
      if (i == 1) {
        checkLine = line1; // the line to check is the first line
      } else {
        checkLine = line2;// the line to check is the second line
      }

      String[] split = checkLine.split(delims); // parse into smaller strings

      if (split[0].equals("W")) { // check if is white player
        int whiteWallIndex = 0;
        for (int j = 1; j < split.length; j++) {
          moveNumber = i;
          roundNumber = j;
          String[] info = split[j].split("");// split into the inforamtion for each component
          try {
            // set the player tile
            playerTile = QuoridorApplication.getQuoridor().getBoard()
                .getTile((Integer.parseInt(info[1]) - 1) * 9 + (alphabetToNum(info[0]) - 1));
          } catch (Exception e) {

            throw (new Exception("out of bound"));
          }
          if (info.length == 2) {
            // set the player position
            whiteposition = new PlayerPosition(whitePlayer, playerTile);
          }
          if (info.length == 3) {
            dir = dirExplain(info[2]);// set the direction of the wall
            Wall wall = whitePlayer.getWall(whiteWallIndex);
            whiteWallIndex++;
            new WallMove(moveNumber, roundNumber, whitePlayer, playerTile,
                QuoridorApplication.getQuoridor().getCurrentGame(), dir, wall);
            game.setWallMoveCandidate(null);
            whiteWalls.add(wall);
          }

        }

      }
      if (split[0].equals("B")) { // if this line store black player's data
        int blackWallIndex = 0;
        for (int j = 1; j < split.length; j++) { // start from the second argument in the string and
                                                 // loop to the end
          moveNumber = i;
          roundNumber = j;

          String[] info = split[j].split(""); // split string by each component
          try {
            // set the player tile
            playerTile = QuoridorApplication.getQuoridor().getBoard()
                .getTile((alphabetToNum(info[0]) - 1) + (Integer.parseInt(info[1]) - 1) * 9);
          } catch (Exception e) {

            throw (new Exception("out of bound"));
          }
          if (info.length == 2) { // check if is a spawn
            // set the player position
            blackposition = new PlayerPosition(blackPlayer, playerTile);
          }
          if (info.length == 3) { // check if is a wall

            dir = dirExplain(info[2]);
            Wall wall = blackPlayer.getWall(blackWallIndex);
            blackWallIndex++;
            new WallMove(moveNumber, roundNumber, blackPlayer, playerTile,
                QuoridorApplication.getQuoridor().getCurrentGame(), dir, wall);
            game.setWallMoveCandidate(null);
            // add wall on the board
            blackWalls.add(wall);

          }

        }

      }

    }

    // create a initializing game

    game.setBlackPlayer(blackPlayer);
    game.setWhitePlayer(whitePlayer);
    // create current gamePosition
    GamePosition gamePosition =
        new GamePosition(0, whiteposition, blackposition, playerToMove, game);

    // set player positions
    gamePosition.setBlackPosition(blackposition);
    gamePosition.setWhitePosition(whiteposition);
    game.setCurrentPosition(gamePosition);

    // add walls to the board
    for (Wall wall : blackWalls) {
      game.getCurrentPosition().addBlackWallsOnBoard(wall);
    }
    for (Wall wall : whiteWalls) {
      game.getCurrentPosition().addWhiteWallsOnBoard(wall);

    }

    // add walls into stock, except the wall on board
    for (int j = whiteWalls.size() + 1; j <= 10; j++) {
      Wall wall = Wall.getWithId(j);
      gamePosition.addWhiteWallsInStock(wall);
    }
    for (int j = blackWalls.size() + 1; j <= 10; j++) {
      Wall wall = Wall.getWithId(j + 10);
      gamePosition.addBlackWallsInStock(wall);
    }


    game.setCurrentPosition(gamePosition);
    game.setGameStatus(GameStatus.Running);
    return game;

  }


  /**
   * @author Mael This controller method checks if the game is over and sets the game status
   *         consequently (BlackWon, WhiteWon, Draw)
   */
  public static void checkGameResult() {

    Game game = QuoridorApplication.getQuoridor().getCurrentGame();

    // check if player reached target area
    Player white = game.getWhitePlayer();
    Player black = game.getBlackPlayer();

    Tile curWhiteTile = game.getCurrentPosition().getWhitePosition().getTile();
    Tile curBlackTile = game.getCurrentPosition().getBlackPosition().getTile();

    Destination whiteDestination = white.getDestination();
    Destination blackDestination = black.getDestination();
    if (whiteDestination.getDirection() == Direction.Horizontal) { // both players move horizontally
      if (whiteDestination.getTargetNumber() == curWhiteTile.getColumn()) {// white player reached
                                                                           // destination
        game.setGameStatus(GameStatus.WhiteWon);
        game.setHasFinalResult(true);
        return;
      } else if (blackDestination.getTargetNumber() == curBlackTile.getColumn()) {// black player
                                                                                  // reached
                                                                                  // destination
        game.setGameStatus(GameStatus.BlackWon);
        game.setHasFinalResult(true);
        return;
      } else {
        game.setGameStatus(GameStatus.Running);
        game.setHasFinalResult(false);
      }
    }

    else if (whiteDestination.getDirection() == Direction.Vertical) {// both player move vertically
      if (whiteDestination.getTargetNumber() == curWhiteTile.getRow()) {// white player reached
                                                                        // destination
        game.setGameStatus(GameStatus.WhiteWon);
        game.setHasFinalResult(true);
        return;
      } else if (blackDestination.getTargetNumber() == curBlackTile.getRow()) {// black player
                                                                               // reached
                                                                               // destination
        game.setGameStatus(GameStatus.BlackWon);
        game.setHasFinalResult(true);
        return;
      } else {
        game.setGameStatus(GameStatus.Running);
        game.setHasFinalResult(false);
      }
    }

    // check player repeats for the third time
    // player that just moved is not the current player to move
    Player playerThatJustMoved;
    if (game.getCurrentPosition().getPlayerToMove().hasGameAsBlack())
      playerThatJustMoved = game.getWhitePlayer();
    else
      playerThatJustMoved = game.getBlackPlayer();

    // get the tiles we'll compare to see if the player repeats for the 3rd time
    List<GamePosition> positions = game.getPositions();
    if (positions.size() < 10)// not enough moves have been completed to test for drawn game
      return;
    Tile[] tiles = new Tile[6];

    // get last 6 times tile changed for playerThatJustMoved
    if (playerThatJustMoved.hasGameAsWhite()) {
      for (int i = 0; i < 5; i++) {
        int index = positions.size() - 1 - 2 * i;
        GamePosition gamePos = game.getPosition(index);
        if (game.getMove(index - 1) instanceof StepMove) // this move was a step move therefore it
                                                         // is considered when looking if a player
                                                         // is repeating moves
          tiles[i] = gamePos.getWhitePosition().getTile();
      }
      tiles[5] = game.getPosition(0).getWhitePosition().getTile();// get start game position
    } else {
      for (int i = 0; i < 5; i++) {
        int index = positions.size() - 1 - 2 * i;
        GamePosition gamePos = game.getPosition(index);

        if (game.getMove(index - 1) instanceof StepMove)
          tiles[i] = gamePos.getBlackPosition().getTile();
      }
      tiles[5] = game.getPosition(0).getBlackPosition().getTile();
    }

    if (tiles[0] == tiles[2] && tiles[0] == tiles[4]) {
      if (tiles[1] == tiles[3] && tiles[1] == tiles[5]) {
        game.setGameStatus(GameStatus.Draw);
        game.setHasFinalResult(true);
      } else {
        game.setGameStatus(GameStatus.Running);
        game.setHasFinalResult(false);
      }
    }
  }

  /**
   * This method enables user to load a previously played game recorded in an algebraic notation in
   * a text file
   * 
   * @author Hao Shu
   * @param fileName
   * @param whitePlayer
   * @param blackPlayer
   * @return a boolean list indicating if the invalid move exists
   * @throws Exception
   */
  public static List<Boolean> loadGame(String fileName, Player whitePlayer, Player blackPlayer)
      throws Exception {
    // use scanner read the file

    FileInputStream inputstream = null;
    try {
      inputstream = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {

      throw (new Exception("File not found"));
    }


    Scanner scanner = new Scanner(inputstream);
    String checkLine = null;
    String delims = "[ :,]+";
    List<Wall> whiteWalls = new ArrayList<Wall>();
    List<Wall> blackWalls = new ArrayList<Wall>();
    Direction dir = null;

    Player playerToMove = null;
    Tile wallTile = null;
    Tile playerTile = null;
    Move newMove = null;

    int playerCol;
    int playerRow;
    int curPlayerCol = 0;
    int curPlayerRow = 0;
    int whiteWallIndex = 0;
    int blackWallIndex = 0;
    int posId = 0;


    int movNum = 1;
    int roundNum;
    List<Boolean> validMove = new ArrayList<Boolean>();


    PlayerPosition newPlayerPosition;
    PlayerPosition whitePosition =
        new PlayerPosition(whitePlayer, QuoridorApplication.getQuoridor().getBoard().getTile(4));
    PlayerPosition blackPosition =
        new PlayerPosition(blackPlayer, QuoridorApplication.getQuoridor().getBoard().getTile(76));
    Game game =
        new Game(GameStatus.Initializing, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());
    GamePosition gamePosition =
        new GamePosition(0, whitePosition, blackPosition, whitePlayer, game);

    // set player positions
    gamePosition.setBlackPosition(blackPosition);
    gamePosition.setWhitePosition(whitePosition);
    game.setCurrentPosition(gamePosition);
    game.setHasFinalResult(false);

    game.setBlackPlayer(blackPlayer);
    game.setWhitePlayer(whitePlayer);

    for (int j = 0; j < 10; j++) {
      Wall wall = Wall.getWithId(j);
      gamePosition.addWhiteWallsInStock(wall);
    }
    for (int j = 0; j < 10; j++) {
      Wall wall = Wall.getWithId(j + 10);
      gamePosition.addBlackWallsInStock(wall);
    }

    // create current gamePosition

    while (scanner.hasNextLine()) {
      checkLine = scanner.nextLine();
      roundNum = checkLine.charAt(0);

      String[] split = checkLine.split(delims); // parse into smaller strings

      for (int i = 1; i < split.length; i++) {

        String[] info = split[i].split("");// split into the inforamtion for each component
        if (info.length == 2) {// info for players

          playerTile = QuoridorApplication.getQuoridor().getBoard()
              .getTile((alphabetToNum(info[0]) - 1) + (Integer.parseInt(info[1]) - 1) * 9);

          playerRow = Integer.parseInt(info[1]);
          playerCol = alphabetToNum(info[0]);

          if (i == 1) {// White player
            curPlayerRow = game.getCurrentPosition().getWhitePosition().getTile().getRow();
            curPlayerCol = game.getCurrentPosition().getWhitePosition().getTile().getColumn();
            playerToMove = whitePlayer;
            whitePosition = new PlayerPosition(playerToMove, playerTile);
            gamePosition.setWhitePosition(whitePosition);
            gamePosition.setId(posId);
            QuoridorApplication.getQuoridor().getCurrentGame().addPosition(gamePosition);
            QuoridorApplication.getQuoridor().getCurrentGame().setCurrentPosition(gamePosition);
            gamePosition.setPlayerToMove(blackPlayer);

          }
          if (i == 2) {// Black players
            curPlayerRow = game.getCurrentPosition().getBlackPosition().getTile().getRow();
            curPlayerCol = game.getCurrentPosition().getBlackPosition().getTile().getColumn();
            playerToMove = blackPlayer;
            blackPosition = new PlayerPosition(playerToMove, playerTile);
            gamePosition.setBlackPosition(blackPosition);

            gamePosition.setId(posId);
            QuoridorApplication.getQuoridor().getCurrentGame().addPosition(gamePosition);
            QuoridorApplication.getQuoridor().getCurrentGame().setCurrentPosition(gamePosition);
            gamePosition.setPlayerToMove(whitePlayer);
          }
          newMove = new StepMove(movNum, roundNum, playerToMove, playerTile, game);


          game.addMove(newMove);



          posId++;
          validMove.add(validateLoadPosition());

        }
        if (info.length == 3) {// info for wall

          dir = dirExplain(info[2]);// set the direction of the wall

          wallTile = QuoridorApplication.getQuoridor().getBoard()
              .getTile((alphabetToNum(info[0]) - 1) + (Integer.parseInt(info[1]) - 1) * 9);

          if (i == 1) {// if whiteWall
            Wall wall = whitePlayer.getWall(whiteWallIndex);
            whiteWallIndex++;
            newMove = new WallMove(movNum, roundNum, whitePlayer, wallTile,
                QuoridorApplication.getQuoridor().getCurrentGame(), dir, wall);
            gamePosition.addWhiteWallsOnBoard(wall);
            gamePosition.removeWhiteWallsInStock(wall);
            gamePosition.setId(posId);
            QuoridorApplication.getQuoridor().getCurrentGame().addPosition(gamePosition);
            QuoridorApplication.getQuoridor().getCurrentGame().setCurrentPosition(gamePosition);
            gamePosition.setPlayerToMove(blackPlayer);



          }
          if (i == 2) {// if blackWall
            Wall wall = blackPlayer.getWall(blackWallIndex);
            blackWallIndex++;
            newMove = new WallMove(movNum, roundNum, blackPlayer, wallTile,
                QuoridorApplication.getQuoridor().getCurrentGame(), dir, wall);
            gamePosition.addBlackWallsOnBoard(wall);
            gamePosition.removeBlackWallsInStock(wall);
            gamePosition.setId(posId);
            QuoridorApplication.getQuoridor().getCurrentGame().addPosition(gamePosition);
            QuoridorApplication.getQuoridor().getCurrentGame().setCurrentPosition(gamePosition);
            gamePosition.setPlayerToMove(whitePlayer);
          }
          game.addMove(newMove);

          posId++;
        }
        movNum++;
      }

    }

    validMove.add(validateLoadPosition());
    game.setGameStatus(GameStatus.ReadyToStart);

    if (game.getCurrentPosition().getBlackPosition().getTile().getRow() == 1
        || game.getCurrentPosition().getWhitePosition().getTile().getRow() == 9) {
      game.setHasFinalResult(true);
      game.setGameStatus(GameStatus.Replay);
    }
    return validMove;


  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         This method changes the status of the current game to ReplayMode. (Related to
   *         EnterReplayMode feature)
   * @param none
   * @return void
   */
  public static void initiateReplayMode() {
    if (!QuoridorApplication.getQuoridor().hasCurrentGame()) {
      new Game(GameStatus.Initializing, MoveMode.PlayerMove, QuoridorApplication.getQuoridor());
    }
    QuoridorApplication.getQuoridor().getCurrentGame().setGameStatus(GameStatus.Replay);
  }

  /**
   * This method checks if the position to load is valid
   *
   * @author Hao Shu
   * @return false if the position is invalid, true if the position to load is valid
   */
  public static boolean validateLoadPosition() {
    int blackRow;
    int blackCol;
    int blackWallRow;
    int blackWallCol;
    int whiteRow;
    int whiteCol;
    int whiteWallRow;
    int whiteWallCol;

    Direction blackWallDir;
    Direction whiteWallDir;
    List<Wall> whiteWalls = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getWhiteWallsOnBoard();
    List<Wall> blackWalls = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getBlackWallsOnBoard();
    List<Wall> wallsOnBoard = new ArrayList<Wall>();

    wallsOnBoard.addAll(blackWalls);
    wallsOnBoard.addAll(whiteWalls);

    blackRow = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getBlackPosition().getTile().getRow();
    blackCol = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
        .getBlackPosition().getTile().getColumn();

    try {
      // validate black walls position
      for (int i = 0; i < blackWalls.size(); i++) {

        blackWallRow = blackWalls.get(i).getMove().getTargetTile().getRow();
        blackWallCol = blackWalls.get(i).getMove().getTargetTile().getColumn();
        blackWallDir = blackWalls.get(i).getMove().getWallDirection();

        if (QuoridorController.validateWallPosition(blackWallRow, blackWallCol,
            blackWallDir) == false) {

          throw (new Exception("Wall out of bound"));
        }
      }

      whiteRow = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getWhitePosition().getTile().getRow();
      whiteCol = QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition()
          .getWhitePosition().getTile().getColumn();

      // validate white walls position
      for (int i = 0; i < whiteWalls.size(); i++) {
        whiteWallRow = whiteWalls.get(i).getMove().getTargetTile().getRow();
        whiteWallCol = whiteWalls.get(i).getMove().getTargetTile().getColumn();
        whiteWallDir = whiteWalls.get(i).getMove().getWallDirection();

        if (QuoridorController.validateWallPosition(whiteWallRow, whiteWallCol,
            whiteWallDir) == false) {

          throw (new Exception("Wall out of bound"));
        }
      }

      // check overlapping walls

      if (validateOverlappingWalls() == false) {
        throw (new Exception("Walls Overlapping!"));
      } else if (blackRow == whiteRow && blackCol == whiteCol) {
        throw (new Exception("Invalid Pawn Position!"));
      } else if (QuoridorController.validatePawnPosition(whiteRow, whiteCol)
          && QuoridorController.validatePawnPosition(blackRow, blackCol)) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }

    return true;

  }

  /**
   * This method will set the game status once a player resigns the game, black player will win if
   * the white player resigns, and vice versa
   * 
   * 
   * @author Hao Shu
   * @param none
   * @return void
   */
  public static void resignGame() {

    System.out.println(
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove());
    if (QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition().getPlayerToMove()
        .hasGameAsWhite()) {// when white player decide to resign
      QuoridorApplication.getQuoridor().getCurrentGame().setGameStatus(GameStatus.BlackWon);

    } else {
      QuoridorApplication.getQuoridor().getCurrentGame().setGameStatus(GameStatus.WhiteWon);
    }
    QuoridorApplication.getQuoridor().getCurrentGame().setHasFinalResult(true);
  }

  /**
   * This helper method creates 2 users and 2 players
   * 
   * @param userName1 : first username
   * @param userName2 : 2nd username
   * @return ArrayList<Player>
   */
  public static ArrayList<Player> createUsersAndPlayers(String userName1, String userName2) {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    User user1 = quoridor.addUser(userName1);
    User user2 = quoridor.addUser(userName2);

    int thinkingTime = 180;

    // Players are assumed to start on opposite sides and need to make progress
    // vertically to get to the other side
    // @formatter:off
        /*
         * __________ | | | | |x-> <-x| | | |__________|
         * 
         */
        // @formatter:on

    Player player1 = new Player(new Time(thinkingTime), user1, 1, Direction.Vertical);
    Player player2 = new Player(new Time(thinkingTime), user2, 9, Direction.Vertical);

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

  /**
   * This helper method creates the board and it's tiles
   */
  public static void initQuoridorAndBoard() {
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

  // ***********************************************
  // Private methods
  // ***********************************************

  /**
   * @author Hao Shu
   * 
   *         This method converts a input lower case letter to a number For example, it will return
   *         1 if input = 'a', return 2 if input = 'b'.
   * 
   * @param s the letter to convert
   * @return the corresponding number
   */
  private static int alphabetToNum(String s) {

    char[] c = s.toCharArray();
    int asciiValue = (int) c[0];

    int num = asciiValue - 96;
    return num;

  }

  /**
   * @author Hao Shu
   * 
   *         This method converts a input number to a lower case letter For example, it will return
   *         'a' if input = 1, return 'b' if input = 2.
   * 
   * @param num the number to convert
   * @return the corresponding letter
   */
  private static char numToAlphabet(int num) {

    char[] charList = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};

    return charList[num - 1];

  }

  /**
   * @author Hao Shu
   * 
   *         This method gives abbreviation to the direction
   * 
   * @param dir the direction to abbreviate
   * @return "h" if the direction is horizontal, "z" if the direction is vertical
   */
  private static String dirAbbreviation(Direction dir) {
    if (dir == Direction.Horizontal) {
      return "h";
    } else {
      return "v";
    }

  }

  /**
   * @author Hao Shu
   * 
   *         This method gives the direction according to the abbreviation
   * 
   * @param s the abbreviation to convert
   * @return Horizontal if the input is "h", Vertical if the input is "v"
   */
  private static Direction dirExplain(String s) {

    char[] c = s.toCharArray();
    if (c[0] == 'h') {
      return Direction.Horizontal;
    } else {
      return Direction.Vertical;
    }

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Throws an error if the username has an invalid format.
   * @throws IllegalArgumentException
   * @param username A username
   * @param quoridor A quoridor application
   * 
   */
  private static void checkValidUsername(String username, Quoridor quoridor)
      throws IllegalArgumentException {
    String error = "";
    // check empty or null
    if (username == null || username.equals("")) {
      error += "The username cannot be empty!";
    }
    for (User u : QuoridorApplication.getQuoridor().getUsers()) {
      if (u.getName().contentEquals(username)) {
        error += "A user with username " + username + " already exists!";
      }
    }
    if (error.length() > 0) {
      throw new IllegalArgumentException(error);
    }
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Returns the list of all walls placed on the board.
   * 
   * @return walls The walls on the board
   */
  private static List<Wall> getWallsOnBoard() {
    GamePosition currentPosition =
        QuoridorApplication.getQuoridor().getCurrentGame().getCurrentPosition();
    List<Wall> walls = new ArrayList<Wall>();
    if (currentPosition.hasWhiteWallsOnBoard()) {
      walls.addAll(currentPosition.getWhiteWallsOnBoard());
    }
    if (currentPosition.hasBlackWallsInStock()) {
      walls.addAll(currentPosition.getBlackWallsOnBoard());
    }
    return walls;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Returns the list of walls placed on the specified row of the board. (Associated to
   *         ValidatePosition Gherkin feature)
   * 
   * @param row A row on the board.
   * 
   * @return walls A list of walls
   */
  private static List<Wall> getWallsByRow(List<Wall> walls, int row) {
    List<Wall> wallsOnRow = new ArrayList<Wall>();
    for (Wall w : walls) {
      if (w.getMove().getTargetTile().getRow() == row) {
        wallsOnRow.add(w);
      }
    }
    return wallsOnRow;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Returns the list of walls placed on the specified column of the board. (Associated to
   *         ValidatePosition Gherkin feature)
   * 
   * @param col A column on the board.
   * 
   * @return walls A list of walls
   */
  private static List<Wall> getWallsByCol(List<Wall> walls, int col) {
    List<Wall> wallsOnCol = new ArrayList<Wall>();
    for (Wall w : walls) {
      if (w.getMove().getTargetTile().getColumn() == col) {
        wallsOnCol.add(w);
      }
    }
    return wallsOnCol;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Returns the wall placed on at the specified coordinates on the board. Returns null if
   *         there is no wall at those coordinates. (Associated to ValidatePosition Gherkin feature)
   * 
   * @param row A row on the board.
   * @param col A column on the board.
   * 
   * @return wall A wall
   */
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
   * 
   *         Returns true if there is a vertical wall below the target wall. Returns false
   *         otherwise. (Associated to ValidatePosition Gherkin feature)
   * 
   * @param walls The list of walls on the board.
   * @param wall The target wall.
   * 
   * @return boolean true of false
   */
  private static boolean hasVerticalWallBelow(List<Wall> walls, Tile tile) {
    Board board = QuoridorApplication.getQuoridor().getBoard();
    int tileIndex = board.indexOfTile(tile);
    Tile tileBelow;
    // Tile index 0 is at (row, col) = (0,0). tileIndex = (row-1)*9+(col-1)
    if (tileIndex >= 9 * 7) { // Lowest possible wall position. There cannot be any walls below.
      return false;
    }
    tileBelow = board.getTile(tileIndex + 9);
    for (Wall w : walls) {
      if (w.getMove().getTargetTile().equals(tileBelow)
          && w.getMove().getWallDirection().equals(Direction.Vertical)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Returns true if there is a vertical wall above the target wall. Returns false
   *         otherwise. (Associated to ValidatePosition Gherkin feature)
   * 
   * @param walls The list of walls on the board.
   * @param wall The target wall.
   * 
   * @return true of false
   */
  private static boolean hasVerticalWallAbove(List<Wall> walls, Tile tile) {
    Board board = QuoridorApplication.getQuoridor().getBoard();
    int tileIndex = board.indexOfTile(tile);
    Tile tileAbove;
    // Tile index 0 is at (row, col) = (0,0). tileIndex = (row-1)*9+(col-1)
    if (tileIndex < 9) { // Highest possible wall position. There cannot be any walls above.
      return false;
    }
    tileAbove = board.getTile(tileIndex - 9);
    for (Wall w : walls) {
      if (w.getMove().getTargetTile().equals(tileAbove)
          && w.getMove().getWallDirection().equals(Direction.Vertical)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Returns true if there is a horizontal wall to the right the target wall. Returns false
   *         otherwise. (Associated to ValidatePosition Gherkin feature)
   * 
   * @param walls The list of walls on the board.
   * @param wall The target wall.
   * 
   * @return true of false
   */
  private static boolean hasHorizontalWallToRight(List<Wall> walls, Tile tile) {
    Board board = QuoridorApplication.getQuoridor().getBoard();
    int tileIndex = board.indexOfTile(tile);
    Tile tileToRight;
    // Tile index 0 is at (row, col) = (0,0). tileIndex = (row-1)*9+(col-1)
    if (tileIndex % 9 == 7) { // Rightmost wall position.There cannot be any walls to the right.
      return false;
    }
    tileToRight = board.getTile(tileIndex + 1);
    for (Wall w : walls) {
      if (w.getMove().getTargetTile().equals(tileToRight)
          && w.getMove().getWallDirection().equals(Direction.Horizontal)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Returns true if there is a horizontal wall to the left of the target wall. Returns
   *         false otherwise. (Associated to ValidatePosition Gherkin feature)
   * 
   * @param walls The list of walls on the board.
   * @param wall The target wall.
   * 
   * @return true of false
   */
  private static boolean hasHorizontalWallToLeft(List<Wall> walls, Tile tile) {
    // Tile tileOfWall = wall.getMove().getTargetTile();
    Board board = QuoridorApplication.getQuoridor().getBoard();
    int tileIndex = board.indexOfTile(tile);
    Tile tileToLeft;
    // Tile index 0 is at (row, col) = (0,0). tileIndex = (row-1)*9+(col-1)
    if (tileIndex % 9 == 0) { // Leftmost wall position.There cannot be any walls to the left.
      return false;
    }
    tileToLeft = board.getTile(tileIndex - 1);
    for (Wall w : walls) {
      if (w.getMove().getTargetTile().equals(tileToLeft)
          && w.getMove().getWallDirection().equals(Direction.Horizontal)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         This method creates a new game position with the same parameters as the current
   *         position, but with an id incremented by 1. The new game position is set as the current
   *         game's current position.
   * 
   */
  public static void newCurrentPosition() {
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
    System.out.println("====Size of position list: " + currentGame.getPositions().size());
    System.out.println("====Adding game position");

    // currentGame.addPosition(newGamePosition);
    System.out.println("====Size of position list: " + currentGame.getPositions().size());

    currentGame.setCurrentPosition(newGamePosition);

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         This method returns if there is a wall in the way of a jump move from a given starting
   *         tile. It is assumed that the starting jump tile is at least one jump away from the edge
   *         of the board. (The method should not be used otherwise)
   * 
   * @param currentTile The starting tile of the jump.
   * @param moveDirection The direction of the jump.
   * @return result True is there is a wall in the way, false otherwise.
   */
  public static boolean wallInTheWayForJump(Tile currentTile, MoveDirection moveDirection) {

    // Check wall in the way one tile away
    if (wallInTheWay(currentTile, moveDirection)) {
      return true;
    }

    boolean result = false;
    int currentPawnCol = currentTile.getColumn();
    int currentPawnRow = currentTile.getRow();
    Tile tileInMoveDir = null;
    Board board = QuoridorApplication.getQuoridor().getBoard();

    // Check wall in the way two tiles away
    switch (moveDirection) {

      case West:
        // Check in wall in the way one column left
        tileInMoveDir = board.getTile(9 * (currentPawnRow - 1) + (currentPawnCol - 2));
        if (wallInTheWay(tileInMoveDir, moveDirection)) {
          result = true;
        }
        break;

      case East:

        // Check in wall in the way one column right
        tileInMoveDir = board.getTile(9 * (currentPawnRow - 1) + (currentPawnCol));
        if (wallInTheWay(tileInMoveDir, moveDirection)) {
          result = true;
        }
        break;

      case North:
        // Check in wall in the way one row up
        tileInMoveDir = board.getTile(9 * (currentPawnRow - 2) + (currentPawnCol - 1));
        if (wallInTheWay(tileInMoveDir, moveDirection)) {
          result = true;
        }
        break;

      case South:
        // Check in wall in the way one column down
        tileInMoveDir = board.getTile(9 * (currentPawnRow) + (currentPawnCol - 2));
        if (wallInTheWay(tileInMoveDir, moveDirection)) {
          result = true;
        }
        break;

      default:
        throw new IllegalArgumentException("Unsupported move direction provided");
    }
    // System.out.println("====Wall in the way for jump: result = " + result);
    return result;

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         This method returns if diagonal jump with the specified parameters is legal. (Related
   *         to JumpPawn feature)
   * 
   * @param player The player to jump.
   * @param vDir The vertical direction of the diagonal jump.
   * @param hDir The horizontal direction of the diagonal jump.
   * @return True if the move is legal, false otherwise.
   */
  public static boolean legalDiagonalJump(Player player, MoveDirection vDir, MoveDirection hDir) {
    Tile currentTile = null;
    Tile otherPawnTile = null;
    MoveDirection dirToOtherPawn = null;
    MoveDirection dirFromOtherPawn = null;

    try {
      currentTile = QuoridorController.getPlayerPosition(player).getTile();
      otherPawnTile = QuoridorController.getPlayerPosition(player.getNextPlayer()).getTile();

    } catch (InvalidInputException e) {
      e.printStackTrace();
    }

    // Check pawn in the way
    if (pawnInTheWay(vDir)) {
      dirToOtherPawn = vDir;
      dirFromOtherPawn = hDir;
    } else if (pawnInTheWay(hDir)) {
      dirToOtherPawn = hDir;
      dirFromOtherPawn = vDir;
    } else {
      return false;
    }

    // Check wall in the way to other pawn's position
    if (wallInTheWay(currentTile, dirToOtherPawn)) {
      return false;
    }

    // Check edge behind pawn to jump or wall behind pawn to jump
    switch (dirToOtherPawn) {
      case North:
        if (!(otherPawnTile.getRow() == 1 || wallInTheWay(otherPawnTile, dirToOtherPawn))) {
          return false;
        }
        break;
      case South:
        if (!(otherPawnTile.getRow() == 9 || wallInTheWay(otherPawnTile, dirToOtherPawn))) {
          return false;
        }
        break;
      case West:
        if (!(otherPawnTile.getColumn() == 1 || wallInTheWay(otherPawnTile, dirToOtherPawn))) {
          return false;
        }
        break;
      case East:
        if (!(otherPawnTile.getColumn() == 9 || wallInTheWay(otherPawnTile, dirToOtherPawn))) {
          return false;
        }
        break;
      default:
        throw new RuntimeException("Unsupported move direction provided!");
    }

    // check wall in the way from other pawn's position
    if (wallInTheWay(otherPawnTile, dirFromOtherPawn)) {
      return false;
    }
    // All tests passed so diagonal move is valid
    return true;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         This method is used to continue a game from the current position in replay
   *         mode.(Related to feature EnterReplayMode)
   * 
   */
  public static void continueGame() throws RuntimeException {// The index of a move in the game's
                                                             // moves list should be
                                                             // one less than the id
    // of the corresponding game position, which should be the same as its index in
    // the position list

    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
    if (currentGame.getHasFinalResult()) {
      throw new RuntimeException("A finished game cannot be continued!");
    }
    GamePosition currentPosition = currentGame.getCurrentPosition();
    System.out.println("====Current Position Id" + currentPosition.getId());
    // delete moves and game position after currentPosition
    List<Move> moves = currentGame.getMoves();
    int moveIndex;
    // System.out.println("====moves: " + moves.toString());
    Move move = null;
    for (moveIndex = moves.size() - 1; moveIndex > currentPosition.getId() - 1; moveIndex--) {
      move = moves.get(moveIndex);
      // System.out.println("====moveIndex: " + moveIndex + " , moves.get(moveIndex):
      // " + moves.get(moveIndex));
      System.out.println("====Removing move from list ::: size: " + currentGame.getMoves().size());
      System.out.println(
          "====Removing position from list ::: size: " + currentGame.getPositions().size());
      move.setGame(currentGame);
      System.out.println("====moves.get(moveIndex): " + moves.get(moveIndex));
      move.delete();
      currentGame.removeMove(move);
      currentGame.getPosition(moveIndex + 1).delete();
      // currentGame.removePosition(gamePositions.get(moveIndex));
      System.out.println("====Moves size: " + currentGame.getMoves().size());
      System.out.println("====Positions size: " + currentGame.getPositions().size());

    }
    // set player to move in current position
    if (moves.size() == 0) {// game start
      currentPosition.setPlayerToMove(currentGame.getWhitePlayer());
    } else if (moves.get(moves.size() - 1).getRoundNumber() == 1) {
      currentPosition.setPlayerToMove(currentGame.getBlackPlayer());
    } else
      currentPosition.setPlayerToMove(currentGame.getWhitePlayer());

    currentGame.setGameStatus(GameStatus.Running);

  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         This method checks if a path exists from the players' positions to their respective
   *         destinations. When a path is exists for both players, the method returns true.
   *         Otherwise it throws an exception specifying which paths are blocked. (Related to
   *         CheckIfPathExists feature)
   * 
   * @return True if a path exists.
   * @throws RuntimeException Contains error messages for the paths blocked for each players.
   */
  public static boolean checkPathExistence() throws RuntimeException {
    Quoridor quoridor = QuoridorApplication.getQuoridor();
    Game currentGame = quoridor.getCurrentGame();
    Player whitePlayer = currentGame.getWhitePlayer();
    Player blackPlayer = currentGame.getBlackPlayer();
    String error = "";
    // create graph
    DefaultUndirectedGraph<Tile, DefaultEdge> boardGraph =
        new DefaultUndirectedGraph<Tile, DefaultEdge>(DefaultEdge.class);
    Tile[] vertices = new Tile[81];

    // populate graph
    List<Tile> tiles = QuoridorApplication.getQuoridor().getBoard().getTiles();
    int index = 0;
    for (Tile t : tiles) {
      vertices[index] = t;
      boardGraph.addVertex(vertices[index]);
      index++;
    }
    // Add horizontal edges
    for (int i = 1; i <= 9; i++) { // rows
      for (int j = 1; j <= 8; j++) { // columns
        if (!(verticalWallAt(i, j) || verticalWallAt(i - 1, j))) { // don't add edge if vertical
                                                                   // wall between
                                                                   // tiles
          boardGraph.addEdge(vertices[9 * (i - 1) + (j - 1)], vertices[9 * (i - 1) + (j)]);

        }
      }
    }
    // Add vertical edges
    for (int j = 1; j <= 9; j++) { // columns
      for (int i = 1; i <= 8; i++) { // rows
        if (!(horizontalWallAt(i, j) || horizontalWallAt(i, j - 1))) { // don't add edge if
                                                                       // horizontal wall
                                                                       // between tiles
          boardGraph.addEdge(vertices[9 * (i - 1) + (j - 1)], vertices[9 * (i) + (j - 1)]);
        }
      }
    }

    // check path for white (throw exception for white if no path)
    if (!PathToDestination(whitePlayer, boardGraph)) {
      error += "The path is not available for white player! ";
    }

    // check path for black (throw exception for black if no path)
    if (!PathToDestination(blackPlayer, boardGraph)) {
      error += "The path is not available for black player!";
    }

    if (!error.trim().isEmpty()) {
      throw new RuntimeException(error);
    }

    return true;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Helper method for checkPathExistence(). Runs shortest path algorithm for the board
   *         graph. (Related to CheckIfPathExists feature)
   * 
   * @param player The player for which the path has to be checked.
   * @param boardGraph A graph of the current board with tiles at vertices, and adjacencies between
   *        tiles as edges.
   * @return True if a path exists, false otherwise.
   */
  private static boolean PathToDestination(Player player,
      DefaultUndirectedGraph<Tile, DefaultEdge> boardGraph) {
    Board board = QuoridorApplication.getQuoridor().getBoard();
    Destination destination = player.getDestination();
    Tile playerTile = null;
    try {
      playerTile = getPlayerPosition(player).getTile();
    } catch (InvalidInputException e) {
      e.printStackTrace();
    }
    // Path algorithm
    DijkstraShortestPath<Tile, DefaultEdge> pathAlgo =
        new DijkstraShortestPath<Tile, DefaultEdge>(boardGraph);
    if (destination.getDirection().equals(Direction.Vertical)) {
      int row = destination.getTargetNumber();
      for (int col = 1; col <= 9; col++) { // iterate through target row, return true if a path is
                                           // found
        if (pathAlgo.getPath(playerTile, board.getTile(9 * (row - 1) + col - 1)) != null) {
          return true;
        }
      }
    } else {
      int col = destination.getTargetNumber();
      for (int row = 1; row <= 9; row++) { // iterate through target column, return true if a path
                                           // is found
        if (pathAlgo.getPath(playerTile, board.getTile(9 * (row - 1) + col - 1)) != null) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Helper method for checkPathExistence(), to build the board graph. Returns if there is a
   *         vertical wall on the board at the specified row and column. (Related to
   *         CheckIfPathExists feature)
   * 
   * @param row A row on the board.
   * @param col A column on the board
   * @return True if there is a wall, false otherwise.
   */
  private static boolean verticalWallAt(int row, int col) {
    if (row < 1 || row > 8 || col < 1 || col > 8) { // not possible to have a wall there
      return false;
    }
    List<Wall> wallsOnBoard = getWallsOnBoard();
    WallMove curWallMove =
        QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
    if (curWallMove != null)// if game has a wall move candidate add to the list
      wallsOnBoard.add(curWallMove.getWallPlaced());
    Wall wall = getWallAtRowCol(wallsOnBoard, row, col);
    if (wall != null && wall.getMove().getWallDirection().equals(Direction.Vertical)) {
      return true;
    } else
      return false;
  }

  /**
   * @author Hugo Parent-Pothier
   * 
   *         Helper method for checkPathExistence(), to build the board graph. Returns if there is a
   *         horizontal wall on the board at the specified row and column. (Related to
   *         CheckIfPathExists feature)
   * 
   * @param row A row on the board.
   * @param col A column on the board
   * @return True if there is a wall, false otherwise.
   */
  private static boolean horizontalWallAt(int row, int col) {
    if (row < 1 || row > 8 || col < 1 || col > 8) { // not possible to have a wall there
      return false;
    }
    List<Wall> wallsOnBoard = getWallsOnBoard();
    WallMove curWallMove =
        QuoridorApplication.getQuoridor().getCurrentGame().getWallMoveCandidate();
    if (curWallMove != null)
      wallsOnBoard.add(curWallMove.getWallPlaced());
    Wall wall = getWallAtRowCol(wallsOnBoard, row, col);
    if (wall != null && wall.getMove().getWallDirection().equals(Direction.Horizontal)) {
      return true;
    } else
      return false;
  }



}
