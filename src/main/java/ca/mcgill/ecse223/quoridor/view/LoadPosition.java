package ca.mcgill.ecse223.quoridor.view;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextField;
import ca.mcgill.ecse223.quoridor.application.QuoridorApplication;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import ca.mcgill.ecse223.quoridor.model.Player;
import ca.mcgill.ecse223.quoridor.model.Quoridor;
import ca.mcgill.ecse223.quoridor.model.Wall;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JPanel;


/**
 * @author Hao Shu
 * @version 11-3-2019
 * 
 *          This is our page to load a game file, it have pop ups for any inputs: if the game is
 *          loaded successfully, a window will tell the user that it's a success; if the position to
 *          load is invalid, it will open a window and show the message. After the load process is
 *          over it will return to the board page.
 * 
 *
 */
public class LoadPosition {

  private JFrame frame;
  private JTextField textField;
  private JPanel panel_load_success;
  private JButton button;
  private JLabel label;
  private JPanel panel_load_file;
  private JPanel panel_invalid;
  private JLabel label_invalid;
  private JButton button_1;
  private JLabel label_fileName;
  private boolean loadValid = true;
  private static Player whitePlayer;
  private static Player blackPlayer;
  public static ArrayList<Player> players;
  private static boolean isPos;

  /**
   * Launch the application.
   */
  public static void run(boolean isPosition) {

    try {
      LoadPosition window = new LoadPosition();
      window.frame.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }

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

    QuoridorController.initQuoridorAndBoard();

    players = QuoridorController.createUsersAndPlayers("a", "b");
    whitePlayer = players.get(0);
    blackPlayer = players.get(1);
    if (isPosition) {
      isPos = true;
    } else {
      isPos = false;
    }

  }

  /**
   * Create the application.
   */
  public LoadPosition() {

    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 489, 288);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);
    frame.setTitle("Load Game");

    panel_load_success = new JPanel();
    panel_load_success.setBounds(0, 0, 471, 241);
    frame.getContentPane().add(panel_load_success);
    panel_load_success.setLayout(null);

    label = new JLabel("Game loaded successfully! ");
    label.setBounds(132, 53, 218, 82);
    panel_load_success.add(label);
    label.setFont(new Font("Tahoma", Font.PLAIN, 18));



    button = new JButton(" OK ");
    button.setBounds(188, 162, 104, 45);
    panel_load_success.add(button);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        panel_load_success.setVisible(false);
        frame.setVisible(false);

        if (isPos) {
          MyQuoridor.run();
        } else {

          QuoridorController.stopGame();
          BoardPanel.messageLbl.setText("Stopped");
          MyQuoridor.getGameNotRunningControlPanel().setVisible(true);


        }
        MyQuoridor.refreshData();
        MyQuoridor.refreshLoad();
      }
    });
    button.setFont(new Font("Tahoma", Font.PLAIN, 17));

    panel_load_file = new JPanel();
    panel_load_file.setBounds(0, 0, 471, 241);
    frame.getContentPane().add(panel_load_file);
    panel_load_file.setLayout(null);

    textField = new JTextField();
    textField.setBounds(171, 99, 215, 38);
    panel_load_file.add(textField);
    textField.setForeground(Color.LIGHT_GRAY);


    panel_load_file.setVisible(true);
    panel_load_success.setVisible(false);


    JButton btnLoad = new JButton("Load ");
    btnLoad.setBounds(188, 162, 104, 45);
    panel_load_file.add(btnLoad);
    btnLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {

        if (isPos) {
          String fileName = textField.getText() + ".dat";
          panel_load_file.setVisible(false);

          try {
            QuoridorController.loadFile(fileName, whitePlayer, blackPlayer);

          } catch (Exception e) {
            panel_invalid.setVisible(true);

            loadValid = false;
          }

        } else {

          String fileName = textField.getText() + ".mov";
          panel_load_file.setVisible(false);

          try {
            QuoridorController.loadGame(fileName, whitePlayer, blackPlayer);
            System.out.print(QuoridorController.validateLoadPosition());
          } catch (Exception e) {
            panel_invalid.setVisible(true);

            loadValid = false;
          }
        }
        if (loadValid == false) {
          QuoridorApplication.getQuoridor().getBoard().delete();
          panel_invalid.setVisible(true);// if the file is valid, show the error message

        } else if (QuoridorController.validateLoadPosition()) {

          panel_load_success.setVisible(true);// if the position is valid, show the success message
        } else {
          panel_invalid.setVisible(true);// if the position is valid, show the error message
          QuoridorApplication.getQuoridor().getBoard().delete();
        }
      }
    });
    btnLoad.setFont(new Font("Tahoma", Font.PLAIN, 17));

    JLabel lblNewLabel = new JLabel("Load the game from an existing game file");
    lblNewLabel.setBounds(41, 47, 398, 22);
    panel_load_file.add(lblNewLabel);
    lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));

    label_fileName = new JLabel("File Name:");
    label_fileName.setFont(new Font("Tahoma", Font.PLAIN, 15));
    label_fileName.setBounds(75, 91, 83, 51);
    panel_load_file.add(label_fileName);

    panel_invalid = new JPanel();
    panel_invalid.setBounds(0, 0, 471, 241);
    frame.getContentPane().add(panel_invalid);
    panel_invalid.setLayout(null);
    panel_invalid.setVisible(false);

    button_1 = new JButton(" OK ");
    button_1.setBounds(189, 162, 104, 45);
    panel_invalid.add(button_1);
    button_1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
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

        frame.setVisible(false);
        StartNewGame.main(null);;

      }
    });
    button_1.setFont(new Font("Tahoma", Font.PLAIN, 17));

    label_invalid = new JLabel("The position to load is invalid ");
    label_invalid.setBounds(123, 52, 284, 82);
    panel_invalid.add(label_invalid);
    label_invalid.setFont(new Font("Tahoma", Font.PLAIN, 18));

  }
}
