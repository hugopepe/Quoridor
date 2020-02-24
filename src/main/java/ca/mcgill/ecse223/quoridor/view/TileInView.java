package ca.mcgill.ecse223.quoridor.view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import java.awt.Rectangle;

public class TileInView extends JPanel {
	
	
//	private static Pawn whitePawn = new Pawn(15,15, Color.WHITE);
//	private static Pawn blackPawn = new Pawn(10,10, Color.BLACK);
//	
	private int xPos; 
	private int yPos;
	private int size;
	

	
	public TileInView(int xPos, int yPos, int size, Color backGroundColor) {
		super();
		this.xPos = xPos;
		this.yPos = yPos;
		this.size = size;
		
	
		
		this.setBounds(xPos, yPos, size, size);
		this.setBackground(backGroundColor);
		this.setLayout(null);
	}


	
	
	
//	public placePawn() {
//		pawn.setBounds(0, 0, 10, 10);
//		pawn.set
//		
//	}
//
//	public removePawn()
//	
	
}
