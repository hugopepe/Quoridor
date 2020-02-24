/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.29.0.4181.a593105a9 modeling language!*/

package ca.mcgill.ecse223.quoridor.controller;
import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.model.*;
import ca.mcgill.ecse223.quoridor.view.*;

// line 6 "../../../../../PawnStateMachine.ump"
public class PawnBehavior
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum MoveDirection { East, South, West, North }

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //PawnBehavior Attributes
  private boolean illegal;

  //PawnBehavior State Machines
  public enum PawnSM { moving }
  public enum PawnSMMovingVertical { Null, vertical }
  public enum PawnSMMovingVerticalVertical { Null, start, atMiddleV, atDownBorder, atUpBorder }
  public enum PawnSMMovingHorizontal { Null, horizontal }
  public enum PawnSMMovingHorizontalHorizontal { Null, start, atLeftBorder, atRightBorder, atMiddleH }
  private PawnSM pawnSM;
  private PawnSMMovingVertical pawnSMMovingVertical;
  private PawnSMMovingVerticalVertical pawnSMMovingVerticalVertical;
  private PawnSMMovingHorizontal pawnSMMovingHorizontal;
  private PawnSMMovingHorizontalHorizontal pawnSMMovingHorizontalHorizontal;

  //PawnBehavior Associations
  private Game currentGame;
  private Player player;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public PawnBehavior(Player aPlayer)
  {
    illegal = false;
    if (!setPlayer(aPlayer))
    {
      throw new RuntimeException("Unable to create PawnBehavior due to aPlayer");
    }
    setPawnSMMovingVertical(PawnSMMovingVertical.Null);
    setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.Null);
    setPawnSMMovingHorizontal(PawnSMMovingHorizontal.Null);
    setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.Null);
    setPawnSM(PawnSM.moving);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setIllegal(boolean aIllegal)
  {
    boolean wasSet = false;
    illegal = aIllegal;
    wasSet = true;
    return wasSet;
  }

  public boolean getIllegal()
  {
    return illegal;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isIllegal()
  {
    return illegal;
  }

  public String getPawnSMFullName()
  {
    String answer = pawnSM.toString();
    if (pawnSMMovingVertical != PawnSMMovingVertical.Null) { answer += "." + pawnSMMovingVertical.toString(); }
    if (pawnSMMovingVerticalVertical != PawnSMMovingVerticalVertical.Null) { answer += "." + pawnSMMovingVerticalVertical.toString(); }
    if (pawnSMMovingHorizontal != PawnSMMovingHorizontal.Null) { answer += "." + pawnSMMovingHorizontal.toString(); }
    if (pawnSMMovingHorizontalHorizontal != PawnSMMovingHorizontalHorizontal.Null) { answer += "." + pawnSMMovingHorizontalHorizontal.toString(); }
    return answer;
  }

  public PawnSM getPawnSM()
  {
    return pawnSM;
  }

  public PawnSMMovingVertical getPawnSMMovingVertical()
  {
    return pawnSMMovingVertical;
  }

  public PawnSMMovingVerticalVertical getPawnSMMovingVerticalVertical()
  {
    return pawnSMMovingVerticalVertical;
  }

  public PawnSMMovingHorizontal getPawnSMMovingHorizontal()
  {
    return pawnSMMovingHorizontal;
  }

  public PawnSMMovingHorizontalHorizontal getPawnSMMovingHorizontalHorizontal()
  {
    return pawnSMMovingHorizontalHorizontal;
  }

  private boolean __autotransition16__()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    switch (aPawnSMMovingVerticalVertical)
    {
      case start:
        if (isAtDownBorder())
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private boolean __autotransition17__()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    switch (aPawnSMMovingVerticalVertical)
    {
      case start:
        if (isAtUpBorder())
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private boolean __autotransition18__()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    switch (aPawnSMMovingVerticalVertical)
    {
      case start:
        if (isAtMiddleV())
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveUp()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    switch (aPawnSMMovingVerticalVertical)
    {
      case atMiddleV:
        if (stepAwayFromUpBorder()&&isLegalStep(MoveDirection.North))
        {
          exitPawnSMMovingVerticalVertical();
        // line 36 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.North);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromUpBorder())&&isLegalStep(MoveDirection.North))
        {
          exitPawnSMMovingVerticalVertical();
        // line 37 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.North);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (jumpAwayFromUpBorder()&&isLegalJump(MoveDirection.North))
        {
          exitPawnSMMovingVerticalVertical();
        // line 42 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.North);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(jumpAwayFromUpBorder())&&!(stepAwayFromUpBorder())&&isLegalJump(MoveDirection.North))
        {
          exitPawnSMMovingVerticalVertical();
        // line 43 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.North);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.North))&&!(isLegalJump(MoveDirection.North)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 58 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atDownBorder:
        if (isLegalStep(MoveDirection.North))
        {
          exitPawnSMMovingVerticalVertical();
        // line 71 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.North);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (isLegalJump(MoveDirection.North))
        {
          exitPawnSMMovingVerticalVertical();
        // line 74 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.North);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.North))&&!(isLegalJump(MoveDirection.North)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 81 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      case atUpBorder:
        exitPawnSMMovingVerticalVertical();
        // line 105 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveDown()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    switch (aPawnSMMovingVerticalVertical)
    {
      case atMiddleV:
        if (stepAwayFromDownBorder()&&isLegalStep(MoveDirection.South))
        {
          exitPawnSMMovingVerticalVertical();
        // line 38 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.South);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromDownBorder())&&isLegalStep(MoveDirection.South))
        {
          exitPawnSMMovingVerticalVertical();
        // line 39 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.South);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (jumpAwayFromDownBorder()&&isLegalJump(MoveDirection.South))
        {
          exitPawnSMMovingVerticalVertical();
        // line 44 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.South);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(jumpAwayFromDownBorder())&&!(stepAwayFromDownBorder())&&isLegalJump(MoveDirection.South))
        {
          exitPawnSMMovingVerticalVertical();
        // line 45 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.South);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.South))&&!(isLegalJump(MoveDirection.South)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 59 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atDownBorder:
        exitPawnSMMovingVerticalVertical();
        // line 82 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
        wasEventProcessed = true;
        break;
      case atUpBorder:
        if (isLegalStep(MoveDirection.South))
        {
          exitPawnSMMovingVerticalVertical();
        // line 93 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.South);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (isLegalJump(MoveDirection.South))
        {
          exitPawnSMMovingVerticalVertical();
        // line 96 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.South);
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.South))&&!(isLegalJump(MoveDirection.South)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 104 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveUpLeft()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingVerticalVertical)
    {
      case atMiddleV:
        if (stepAwayFromUpBorder()&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.West))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromUpBorder())&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.West))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.West)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 61 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atDownBorder:
        if (isLegalDiagonalJump(MoveDirection.North,MoveDirection.West))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.West)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 84 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atUpBorder:
        exitPawnSMMovingVerticalVertical();
        // line 109 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case atLeftBorder:
        exitPawnSMMovingHorizontalHorizontal();
        // line 147 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
        wasEventProcessed = true;
        break;
      case atRightBorder:
        if (isLegalDiagonalJump(MoveDirection.North,MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 161 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.North, MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.West)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 168 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      case atMiddleH:
        if (stepAwayFromLeftBorder()&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 190 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.North, MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromLeftBorder())&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 191 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.North,MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.West)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 203 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveUpRight()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingVerticalVertical)
    {
      case atMiddleV:
        if (stepAwayFromUpBorder()&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.East))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromUpBorder())&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.East))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.East)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 62 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atDownBorder:
        if (isLegalDiagonalJump(MoveDirection.North,MoveDirection.East))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.East)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 85 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atUpBorder:
        exitPawnSMMovingVerticalVertical();
        // line 110 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case atLeftBorder:
        if (isLegalDiagonalJump(MoveDirection.North,MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 138 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.North, MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.East)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 144 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      case atRightBorder:
        exitPawnSMMovingHorizontalHorizontal();
        // line 171 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
        wasEventProcessed = true;
        break;
      case atMiddleH:
        if (stepAwayFromRightBorder()&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 192 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.North,MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromRightBorder())&&isLegalDiagonalJump(MoveDirection.North,MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 193 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.North,MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.North,MoveDirection.East)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 204 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveDownLeft()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingVerticalVertical)
    {
      case atMiddleV:
        if (stepAwayFromDownBorder()&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.West))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromDownBorder())&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.West))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.West)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 63 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atDownBorder:
        exitPawnSMMovingVerticalVertical();
        // line 86 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
        wasEventProcessed = true;
        break;
      case atUpBorder:
        if (isLegalDiagonalJump(MoveDirection.South,MoveDirection.West))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.West)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 107 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case atLeftBorder:
        exitPawnSMMovingHorizontalHorizontal();
        // line 146 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
        wasEventProcessed = true;
        break;
      case atRightBorder:
        if (isLegalDiagonalJump(MoveDirection.South,MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 160 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.South, MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.West)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 169 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      case atMiddleH:
        if (stepAwayFromLeftBorder()&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 194 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.South,MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromLeftBorder())&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 195 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.South,MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.West)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 205 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveDownRight()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical = pawnSMMovingVerticalVertical;
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingVerticalVertical)
    {
      case atMiddleV:
        if (stepAwayFromDownBorder()&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.East))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromDownBorder())&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.East))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.East)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 64 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        break;
      case atDownBorder:
        exitPawnSMMovingVerticalVertical();
        // line 87 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atDownBorder);
        wasEventProcessed = true;
        break;
      case atUpBorder:
        if (isLegalDiagonalJump(MoveDirection.South,MoveDirection.East))
        {
          exitPawnSMMovingVerticalVertical();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atMiddleV);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.East)))
        {
          exitPawnSMMovingVerticalVertical();
        // line 108 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.atUpBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case atLeftBorder:
        if (isLegalDiagonalJump(MoveDirection.South,MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 137 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.South, MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.East)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 145 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      case atRightBorder:
        exitPawnSMMovingHorizontalHorizontal();
        // line 170 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
        wasEventProcessed = true;
        break;
      case atMiddleH:
        if (stepAwayFromRightBorder()&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 196 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.South,MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromRightBorder())&&isLegalDiagonalJump(MoveDirection.South,MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 197 "../../../../../PawnStateMachine.ump"
          diagonalJump(MoveDirection.South,MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalDiagonalJump(MoveDirection.South,MoveDirection.East)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 206 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private boolean __autotransition19__()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case start:
        if (isAtLeftBorder())
        {
          exitPawnSMMovingHorizontalHorizontal();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private boolean __autotransition20__()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case start:
        if (isAtRightBorder())
        {
          exitPawnSMMovingHorizontalHorizontal();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private boolean __autotransition21__()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case start:
        if (isAtMiddleH())
        {
          exitPawnSMMovingHorizontalHorizontal();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveRight()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case atLeftBorder:
        if (isLegalStep(MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 131 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (isLegalJump(MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 134 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.East))&&!(isLegalJump(MoveDirection.East)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 141 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      case atRightBorder:
        exitPawnSMMovingHorizontalHorizontal();
        // line 166 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
        wasEventProcessed = true;
        break;
      case atMiddleH:
        if (stepAwayFromRightBorder()&&isLegalStep(MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 178 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromRightBorder())&&isLegalStep(MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 179 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (jumpAwayFromRightBorder()&&isLegalJump(MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 184 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(jumpAwayFromRightBorder())&&!(stepAwayFromRightBorder())&&isLegalJump(MoveDirection.East))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 185 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.East);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.East))&&!(isLegalJump(MoveDirection.East)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 200 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean moveLeft()
  {
    boolean wasEventProcessed = false;
    
    PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal = pawnSMMovingHorizontalHorizontal;
    switch (aPawnSMMovingHorizontalHorizontal)
    {
      case atLeftBorder:
        exitPawnSMMovingHorizontalHorizontal();
        // line 142 "../../../../../PawnStateMachine.ump"
        illegalMove();
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
        wasEventProcessed = true;
        break;
      case atRightBorder:
        if (isLegalStep(MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 154 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (isLegalJump(MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 157 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.West))&&!(isLegalJump(MoveDirection.West)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 165 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atRightBorder);
          wasEventProcessed = true;
          break;
        }
        break;
      case atMiddleH:
        if (stepAwayFromLeftBorder()&&isLegalStep(MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 180 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(stepAwayFromLeftBorder())&&isLegalStep(MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 181 "../../../../../PawnStateMachine.ump"
          step(MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (jumpAwayFromLeftBorder()&&isLegalJump(MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 186 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atLeftBorder);
          wasEventProcessed = true;
          break;
        }
        if (!(jumpAwayFromLeftBorder())&&!(stepAwayFromLeftBorder())&&isLegalJump(MoveDirection.West))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 187 "../../../../../PawnStateMachine.ump"
          jump(MoveDirection.West);
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        if (!(isLegalStep(MoveDirection.West))&&!(isLegalJump(MoveDirection.West)))
        {
          exitPawnSMMovingHorizontalHorizontal();
        // line 201 "../../../../../PawnStateMachine.ump"
          illegalMove();
          setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.atMiddleH);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void exitPawnSM()
  {
    switch(pawnSM)
    {
      case moving:
        exitPawnSMMovingVertical();
        exitPawnSMMovingHorizontal();
        break;
    }
  }

  private void setPawnSM(PawnSM aPawnSM)
  {
    pawnSM = aPawnSM;

    // entry actions and do activities
    switch(pawnSM)
    {
      case moving:
        if (pawnSMMovingVertical == PawnSMMovingVertical.Null) { setPawnSMMovingVertical(PawnSMMovingVertical.vertical); }
        if (pawnSMMovingHorizontal == PawnSMMovingHorizontal.Null) { setPawnSMMovingHorizontal(PawnSMMovingHorizontal.horizontal); }
        break;
    }
  }

  private void exitPawnSMMovingVertical()
  {
    switch(pawnSMMovingVertical)
    {
      case vertical:
        exitPawnSMMovingVerticalVertical();
        setPawnSMMovingVertical(PawnSMMovingVertical.Null);
        break;
    }
  }

  private void setPawnSMMovingVertical(PawnSMMovingVertical aPawnSMMovingVertical)
  {
    pawnSMMovingVertical = aPawnSMMovingVertical;
    if (pawnSM != PawnSM.moving && aPawnSMMovingVertical != PawnSMMovingVertical.Null) { setPawnSM(PawnSM.moving); }

    // entry actions and do activities
    switch(pawnSMMovingVertical)
    {
      case vertical:
        if (pawnSMMovingVerticalVertical == PawnSMMovingVerticalVertical.Null) { setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.start); }
        break;
    }
  }

  private void exitPawnSMMovingVerticalVertical()
  {
    switch(pawnSMMovingVerticalVertical)
    {
      case start:
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.Null);
        break;
      case atMiddleV:
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.Null);
        break;
      case atDownBorder:
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.Null);
        break;
      case atUpBorder:
        setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical.Null);
        break;
    }
  }

  private void setPawnSMMovingVerticalVertical(PawnSMMovingVerticalVertical aPawnSMMovingVerticalVertical)
  {
    pawnSMMovingVerticalVertical = aPawnSMMovingVerticalVertical;
    if (pawnSMMovingVertical != PawnSMMovingVertical.vertical && aPawnSMMovingVerticalVertical != PawnSMMovingVerticalVertical.Null) { setPawnSMMovingVertical(PawnSMMovingVertical.vertical); }

    // entry actions and do activities
    switch(pawnSMMovingVerticalVertical)
    {
      case start:
        __autotransition16__();
        __autotransition17__();
        __autotransition18__();
        break;
    }
  }

  private void exitPawnSMMovingHorizontal()
  {
    switch(pawnSMMovingHorizontal)
    {
      case horizontal:
        exitPawnSMMovingHorizontalHorizontal();
        setPawnSMMovingHorizontal(PawnSMMovingHorizontal.Null);
        break;
    }
  }

  private void setPawnSMMovingHorizontal(PawnSMMovingHorizontal aPawnSMMovingHorizontal)
  {
    pawnSMMovingHorizontal = aPawnSMMovingHorizontal;
    if (pawnSM != PawnSM.moving && aPawnSMMovingHorizontal != PawnSMMovingHorizontal.Null) { setPawnSM(PawnSM.moving); }

    // entry actions and do activities
    switch(pawnSMMovingHorizontal)
    {
      case horizontal:
        if (pawnSMMovingHorizontalHorizontal == PawnSMMovingHorizontalHorizontal.Null) { setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.start); }
        break;
    }
  }

  private void exitPawnSMMovingHorizontalHorizontal()
  {
    switch(pawnSMMovingHorizontalHorizontal)
    {
      case start:
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.Null);
        break;
      case atLeftBorder:
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.Null);
        break;
      case atRightBorder:
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.Null);
        break;
      case atMiddleH:
        setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal.Null);
        break;
    }
  }

  private void setPawnSMMovingHorizontalHorizontal(PawnSMMovingHorizontalHorizontal aPawnSMMovingHorizontalHorizontal)
  {
    pawnSMMovingHorizontalHorizontal = aPawnSMMovingHorizontalHorizontal;
    if (pawnSMMovingHorizontal != PawnSMMovingHorizontal.horizontal && aPawnSMMovingHorizontalHorizontal != PawnSMMovingHorizontalHorizontal.Null) { setPawnSMMovingHorizontal(PawnSMMovingHorizontal.horizontal); }

    // entry actions and do activities
    switch(pawnSMMovingHorizontalHorizontal)
    {
      case start:
        __autotransition19__();
        __autotransition20__();
        __autotransition21__();
        break;
    }
  }
  /* Code from template association_GetOne */
  public Game getCurrentGame()
  {
    return currentGame;
  }

  public boolean hasCurrentGame()
  {
    boolean has = currentGame != null;
    return has;
  }
  /* Code from template association_GetOne */
  public Player getPlayer()
  {
    return player;
  }
  /* Code from template association_SetUnidirectionalOptionalOne */
  public boolean setCurrentGame(Game aNewCurrentGame)
  {
    boolean wasSet = false;
    currentGame = aNewCurrentGame;
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setPlayer(Player aNewPlayer)
  {
    boolean wasSet = false;
    if (aNewPlayer != null)
    {
      player = aNewPlayer;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    currentGame = null;
    player = null;
  }


  /**
   * 
   * HELPER METHODS
   * 
   * Returns the current row number of the pawn
   */
  // line 223 "../../../../../PawnStateMachine.ump"
   private int getCurrentPawnRow(){
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		int currentPawnRow;
		if (player.equals(currentGame.getWhitePlayer())) {
			currentPawnRow = currentGame.getCurrentPosition().getWhitePosition().getTile().getRow();
		} else
			currentPawnRow = currentGame.getCurrentPosition().getBlackPosition().getTile().getRow();

		return currentPawnRow;
  }


  /**
   * Returns the current column number of the pawn
   */
  // line 236 "../../../../../PawnStateMachine.ump"
   private int getCurrentPawnColumn(){
    Game currentGame = QuoridorApplication.getQuoridor().getCurrentGame();
		int currentPawnCol;

		if (player.equals(currentGame.getWhitePlayer())) {
			currentPawnCol = currentGame.getCurrentPosition().getWhitePosition().getTile().getColumn();
		} else
			currentPawnCol = currentGame.getCurrentPosition().getBlackPosition().getTile().getColumn();
		return currentPawnCol;
  }


  /**
   * 
   * GUARDS
   * 
   * Returns if the right border is one step right
   */
  // line 254 "../../../../../PawnStateMachine.ump"
   private boolean stepAwayFromRightBorder(){
    return getCurrentPawnColumn()== 8;
  }


  /**
   * Returns if the left border is one step left
   */
  // line 259 "../../../../../PawnStateMachine.ump"
   private boolean stepAwayFromLeftBorder(){
    return getCurrentPawnColumn()== 2;
  }


  /**
   * Returns if the upper border is one step up
   */
  // line 264 "../../../../../PawnStateMachine.ump"
   private boolean stepAwayFromUpBorder(){
    return getCurrentPawnRow() == 2;
  }


  /**
   * Returns if the lower border is one step down
   */
  // line 269 "../../../../../PawnStateMachine.ump"
   private boolean stepAwayFromDownBorder(){
    return getCurrentPawnRow() == 8;
  }


  /**
   * Returns if the right border is one jump up
   */
  // line 274 "../../../../../PawnStateMachine.ump"
   private boolean jumpAwayFromRightBorder(){
    return getCurrentPawnColumn()== 7;
  }


  /**
   * Returns if the left border is one jump left
   */
  // line 279 "../../../../../PawnStateMachine.ump"
   private boolean jumpAwayFromLeftBorder(){
    return getCurrentPawnColumn()== 3;
  }


  /**
   * Returns if the upper border is one jump up
   */
  // line 284 "../../../../../PawnStateMachine.ump"
   private boolean jumpAwayFromUpBorder(){
    return getCurrentPawnRow() == 3;
  }


  /**
   * Returns if the lower border is one jump down
   */
  // line 289 "../../../../../PawnStateMachine.ump"
   private boolean jumpAwayFromDownBorder(){
    return getCurrentPawnRow() == 7;
  }


  /**
   * Returns if a border is one step in the given direction
   */
  // line 294 "../../../../../PawnStateMachine.ump"
   private boolean stepAwayFromBorder(MoveDirection dir){
    switch (dir) {
			case North:
				return stepAwayFromUpBorder();
			case South:
				return stepAwayFromDownBorder();
			case East:
				return stepAwayFromRightBorder();
			case West:
				return stepAwayFromLeftBorder();
			default:
				throw new RuntimeException("Unsupposted move direction provided");
		}
  }


  /**
   * Returns if it is legal to step in the given direction
   */
  // line 310 "../../../../../PawnStateMachine.ump"
   private boolean isLegalStep(MoveDirection dir){
    Tile currentTile = null;
		try {
			currentTile = QuoridorController.getPlayerPosition(player).getTile();
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}    	
		if(QuoridorController.wallInTheWay(currentTile, dir) || QuoridorController.pawnInTheWay(dir)) { 
	    		return false; 	
	    } else return true;
  }


  /**
   * Returns if it is legal to jump in the given direction
   */
  // line 322 "../../../../../PawnStateMachine.ump"
   private boolean isLegalJump(MoveDirection dir){
    Tile currentTile = null;
		try {
			currentTile = QuoridorController.getPlayerPosition(player).getTile();
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}    	
		if(QuoridorController.wallInTheWayForJump(currentTile, dir) || !QuoridorController.pawnInTheWay(dir) || stepAwayFromBorder(dir))  { 
    		return false; 	
    	} else return true;
  }


  /**
   * Returns if it is legal to jump diagonally in the given direction
   */
  // line 335 "../../../../../PawnStateMachine.ump"
   private boolean isLegalDiagonalJump(MoveDirection vDir, MoveDirection hDir){
    //System.out.println("====legalDiagJump: " + QuoridorController.legalDiagonalJump(player, vDir, hDir));
 
		return QuoridorController.legalDiagonalJump(player, vDir, hDir);
  }


  /**
   * Returns if the pawn is at left the leftmost column on the board
   */
  // line 342 "../../../../../PawnStateMachine.ump"
   private boolean isAtLeftBorder(){
    if (getCurrentPawnColumn() == 1) {
    		return true;
    	} else return false;
  }


  /**
   * Returns if the pawn is at left the rightmost column on the board
   */
  // line 349 "../../../../../PawnStateMachine.ump"
   private boolean isAtRightBorder(){
    if (getCurrentPawnColumn() == 9) {
    		return true;
    	} else return false;
  }


  /**
   * Returns if the pawn is anywhere between the leftmost and the rightmost columns
   */
  // line 356 "../../../../../PawnStateMachine.ump"
   private boolean isAtMiddleH(){
    if (getCurrentPawnColumn() > 1 && getCurrentPawnColumn() < 9) {    	
    		return true;
    	} else return false;
  }


  /**
   * Returns if the pawn is at the lowest row on the board
   */
  // line 363 "../../../../../PawnStateMachine.ump"
   private boolean isAtDownBorder(){
    if (getCurrentPawnRow() == 9) {
    		return true;
    	} else return false;
  }


  /**
   * Returns if the pawn is at the highest row on the board
   */
  // line 370 "../../../../../PawnStateMachine.ump"
   private boolean isAtUpBorder(){
    if (getCurrentPawnRow() == 1) {
    		return true;
    	} else return false;
  }


  /**
   * Returns if the pawn is anywhere between the lowest and the highest rows
   */
  // line 376 "../../../../../PawnStateMachine.ump"
   private boolean isAtMiddleV(){
    if (getCurrentPawnRow() > 1 && getCurrentPawnRow() < 9) {
    		return true;
    	} else return false;
  }


  /**
   * 
   * ACTIONS
   * 
   * Action to be called when an illegal move is attempted
   */
  // line 389 "../../../../../PawnStateMachine.ump"
   private void illegalMove(){
    //System.out.println("===Illegal!");
		BoardPanel.displayError("Illegal Move!");
		illegal = true; //For testing in step definitions
  }


  /**
   * Action to be called when a step move is legal
   */
  // line 398 "../../../../../PawnStateMachine.ump"
   private void step(MoveDirection moveDirection){
    QuoridorController.stepMove(moveDirection);
    	BoardPanel.clearError();
    	illegal = false; // For testing in step definitions
  }

  // line 405 "../../../../../PawnStateMachine.ump"
   private void jump(MoveDirection moveDirection){
    QuoridorController.jumpMove(moveDirection);
    	BoardPanel.clearError();
    	illegal = false; // For testing in step definitions
  }

  // line 412 "../../../../../PawnStateMachine.ump"
   private void diagonalJump(MoveDirection vDirection, MoveDirection hDirection){
    //System.out.println("====diagJump: " + vDirection + " " + hDirection);
		QuoridorController.diagonalJumpMove(vDirection,hDirection);
		BoardPanel.clearError();
		illegal = false; // For testing in step definitions
  }


  public String toString()
  {
    return super.toString() + "["+
            "illegal" + ":" + getIllegal()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "currentGame = "+(getCurrentGame()!=null?Integer.toHexString(System.identityHashCode(getCurrentGame())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "player = "+(getPlayer()!=null?Integer.toHexString(System.identityHashCode(getPlayer())):"null");
  }
}