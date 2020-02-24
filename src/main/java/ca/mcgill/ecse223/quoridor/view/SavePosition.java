package ca.mcgill.ecse223.quoridor.view;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import ca.mcgill.ecse223.quoridor.controller.QuoridorController;
import javax.swing.JLabel;
import javax.swing.DropMode;
import javax.swing.JDesktopPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JPanel;

/**
 * @author Hao Shu
 * @version 11-3-2019
 * 
 *          This is our page to save a game file, it have pop ups for any inputs: if the game is
 *          saved successfully, a window will tell the user that it's a success; if the same file
 *          exists, it will ask if the user wants to overwrite. When the game saving process is over
 *          it will return to the board page.
 * 
 *
 */
public class SavePosition {

  private JFrame frame;
  private JTextField textField;
  private JPanel panel_save_success;
  private JButton button;
  private JLabel label;
  private JPanel panel_save_file;
  private JPanel panel_overwrite_option;
  private JLabel label_exits;
  private JButton button_cancle;
  private JLabel lblNewlabel_exits;
  private JButton button_overwrite_true;
  private JPanel panel_overwrite_success;
  private JButton button_overwrite_confirm;
  private JLabel label_overwrite_success;
  private JLabel label_file_name;
  private String gameFileName;

  /**
   * Launch the application.
   */
      public static void run() {
        try {
          SavePosition window = new SavePosition();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
  }

  /**
   * Create the application.
   */
  public SavePosition() {
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
    frame.setTitle("Save Game");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    panel_save_success = new JPanel();
    panel_save_success.setBounds(0, 0, 471, 241);
    frame.getContentPane().add(panel_save_success);
    panel_save_success.setLayout(null);

    label = new JLabel("Game saved successfully! ");
    label.setBounds(132, 53, 218, 82);
    panel_save_success.add(label);
    label.setFont(new Font("Tahoma", Font.PLAIN, 18));



    button = new JButton(" OK ");
    button.setBounds(188, 162, 104, 45);
    panel_save_success.add(button);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        

        panel_save_success.setVisible(false);
        frame.setVisible(false);
        frame.dispose();
      }
    });
    
    button.setFont(new Font("Tahoma", Font.PLAIN, 17));

    panel_save_file = new JPanel();
    panel_save_file.setBounds(0, 0, 471, 241);
    frame.getContentPane().add(panel_save_file);
    panel_save_file.setLayout(null);

    textField = new JTextField();
    textField.setBounds(171, 99, 215, 38);
    panel_save_file.add(textField);
    textField.setForeground(Color.LIGHT_GRAY);

    label_file_name = new JLabel("File Name:");
    label_file_name.setFont(new Font("Tahoma", Font.PLAIN, 15));
    label_file_name.setBounds(90, 92, 83, 51);
    panel_save_file.add(label_file_name);

    panel_save_file.setVisible(true);
    panel_save_success.setVisible(false);


    JButton btnSave = new JButton("Save");
    btnSave.setBounds(188, 162, 104, 45);
    panel_save_file.add(btnSave);
    btnSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {

        gameFileName = textField.getText();// obtain the file name entered
       
        panel_save_file.setVisible(false);
        if (QuoridorController.checkExistingFile(gameFileName)==false) {
          panel_save_success.setVisible(true);// if the same file exists
        } else {
          panel_overwrite_option.setVisible(true);// successfully saved
        }
        
        try {
        QuoridorController.saveFile(gameFileName);
        QuoridorController.saveGame(gameFileName);
        }catch(Exception e) {
          
        }
      }
    });
    btnSave.setFont(new Font("Tahoma", Font.PLAIN, 17));

    JLabel lblNewLabel = new JLabel("Save the game position in a file");
    lblNewLabel.setBounds(117, 47, 249, 22);
    panel_save_file.add(lblNewLabel);
    lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));

    panel_overwrite_option = new JPanel();
    panel_overwrite_option.setBounds(0, 0, 471, 241);
    frame.getContentPane().add(panel_overwrite_option);
    panel_overwrite_option.setLayout(null);
    panel_overwrite_option.setVisible(false);

    button_cancle = new JButton(" Cancel ");
    button_cancle.setBounds(291, 162, 104, 45);
    panel_overwrite_option.add(button_cancle);
    button_cancle.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        try {
        QuoridorController.overwriteExistingFile(gameFileName, false);
        QuoridorController.overwriteExistingGameFile(gameFileName, false);
        }catch(Exception c) {
          
        }
        frame.setVisible(false);
        frame.dispose();
      }
    });
    button_cancle.setFont(new Font("Tahoma", Font.PLAIN, 17));

    label_exits = new JLabel("The file already exists");
    label_exits.setBounds(144, 39, 184, 71);
    panel_overwrite_option.add(label_exits);
    label_exits.setFont(new Font("Tahoma", Font.PLAIN, 18));

    lblNewlabel_exits = new JLabel("Are you sure you want to overwrite the existing file?");
    lblNewlabel_exits.setBounds(30, 90, 414, 45);
    lblNewlabel_exits.setFont(new Font("Tahoma", Font.PLAIN, 18));
    panel_overwrite_option.add(lblNewlabel_exits);

    button_overwrite_true = new JButton(" OK ");
    button_overwrite_true.setFont(new Font("Tahoma", Font.PLAIN, 17));
    button_overwrite_true.setBounds(86, 162, 104, 45);
    panel_overwrite_option.add(button_overwrite_true);
    button_overwrite_true.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        QuoridorController.overwriteExistingFile(gameFileName, true);
        panel_overwrite_option.setVisible(false);
        panel_overwrite_success.setVisible(true);
      }
    });


    panel_overwrite_success = new JPanel();
    panel_overwrite_success.setBounds(0, 0, 471, 241);
    frame.getContentPane().add(panel_overwrite_success);
    panel_overwrite_success.setVisible(false);


    panel_overwrite_success.setLayout(null);
    button_overwrite_confirm = new JButton(" OK ");
    button_overwrite_confirm.setFont(new Font("Tahoma", Font.PLAIN, 17));
    button_overwrite_confirm.setBounds(188, 162, 103, 44);
    panel_overwrite_success.add(button_overwrite_confirm);

    button_overwrite_confirm.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        frame.setVisible(false);
        frame.dispose();
      }
    });

    label_overwrite_success = new JLabel("File overwritten successfully! ");
    label_overwrite_success.setFont(new Font("Tahoma", Font.PLAIN, 18));
    label_overwrite_success.setBounds(126, 60, 230, 74);
    panel_overwrite_success.add(label_overwrite_success);

  }
}