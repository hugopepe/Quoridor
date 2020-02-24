package ca.mcgill.ecse223.quoridor.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;


public class Pawn extends JPanel {
	

    public Color color;
    int size;
    
    public Pawn( int size, Color color) {
		super();
		this.setBounds(0, 0, size, size);
		this.color = color;
		this.size = size;
		
	}

//
//	public void paintComponent(Graphics g) {
//    	  super.paintComponent(g);
//    	  g.setColor(color);
//    	  g.fillOval(xPos,yPos,width,height);
//    	  g.setColor(Color.BLACK);
//    	  g.drawOval(xPos,yPos,width,height);
//    	 
//    	  
//    }
    
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;

        
		Rectangle pawn = new Rectangle(0, 0, size, size); 

        g2.setColor(color);
        g2.fillOval(0,0,size,size);
        g2.draw(pawn); 

    }
    
    
	

}
