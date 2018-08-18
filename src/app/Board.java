package app;

import java.util.Random;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

public class Board extends VBox {
	int width = 700;
	public  Block[][] blocks;
	int marks ;
	Game game;
	int X;
	int Y;
	int[] zeros ;
	
	
	public Board(int x,int y,int number,Game Game){
		this.setId("board");
		this.game = Game;
		this.X = x;
		this.Y = y;
		int[] mines = getRandomArray(number,0,x*y);
		blocks = new Block[y][x];
		for (int jy = 0; jy < blocks.length; jy++) {
			HBox line = new HBox();
			for (int ix = 0; ix < blocks[jy].length; ix++) {
				Block block = new Block(ix,jy,game,this);
				line.getChildren().add(block);
				blocks[jy][ix] = block;
			}
			this.getChildren().add(line);
		}
		
		
		for (int i = 0; i < mines.length; i++) {
			int l = mines[i]/y;
			int col = mines[i]%y;
			bombPlant(l,col);
		}
		int b = 0;
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				if (blocks[i][j].c == 0 && !blocks[i][j].isBomb){
					b++;
				}
			}
		}
		zeros = new int[b];
		b = 0;
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				if (blocks[i][j].c == 0 && !blocks[i][j].isBomb) {
					zeros[b] = i*x + j;
					b++;
				}
			}
		}
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				switch (blocks[i][j].c) {
					case 0:
						blocks[i][j].s.setText("");
					break;
					
					case 1:
						blocks[i][j].s.setStyle("-fx-font-size:25px;-fx-text-fill:blue;-fx-font-weight:bolder; ");
					break;
					
					case 2:
						blocks[i][j].s.setStyle("-fx-font-size:25px;-fx-text-fill:green;-fx-font-weight:bolder; ");
					break;
				
					case 3:
						blocks[i][j].s.setStyle("-fx-font-size:25px;-fx-text-fill:red;-fx-font-weight:bolder; ");
					break;
					
					case 4:
						blocks[i][j].s.setStyle("-fx-font-size:25px;-fx-text-fill:#000080;-fx-font-weight:bolder; ");
					break;
	
					default:
						blocks[i][j].s.setStyle("-fx-font-size:25px;-fx-text-fill:#800000;-fx-font-weight:bolder; ");
					break;
					}
			}
		}
	}	
	
	/**
	 * Generates random numbers
	 * @param num number of generated numbers
	 * @param start where to start
	 * @param end where to stop
	 * 
	 * @return Array of generated numbers
	 */
	public static int[] getRandomArray(int num,int start,int end){
		
		int[] array = new int[num] ;
		Random generator = new Random();
		int rand;
		for (int i = 0; i < array.length; i++) {
			rand = start  +  generator.nextInt(end);
			for (int j = 0; j < i; j++) {
				while (rand == array[j]) {
					rand = start + generator.nextInt(end);
				}
			}
			array[i] = rand ;
		}
		
		return array;
	}
	
	/**
	 * Plants bomb in specified location
	 * @param y horizontal index of the block
	 * @param x vertical index of the block
	 */
	public void bombPlant(int x,int y){
		blocks[y][x].setIsBomb(true);
		blocks[y][x].getChildren().removeAll(blocks[y][x].s,blocks[y][x].btn);
		ImageView bomb = new ImageView(new Image(this.getClass().getResource("images/bomb.png").toString()));
		bomb.setFitHeight(20);
		bomb.setFitWidth(20);
		blocks[y][x].getChildren().addAll(bomb,blocks[y][x].btn);
		try {
			plus(y-1,x-1);
		}catch (Exception e) {
		}
		try {
			plus(y-1,x+1);
		}catch (Exception e) {
		}
		try {
			plus(y-1,x);
		}catch (Exception e) {
		}
		try {
			plus(y,x-1);
		}catch (Exception e) {
		}
		try {
			plus(y,x+1);
		}catch (Exception e) {
		}
		try {
			plus(y+1,x-1);
		}catch (Exception e) {
		}
		try {
			plus(y+1,x+1);
		}catch (Exception e) {
		}
		try {
			plus(y+1,x);
		}catch (Exception e) {
		}
	}
	
	/**
	 * Adds the counter of the block by one, and sets the label's text to the count.
	 * @param y horizontal index of the block
	 * @param x vertical index of the block
	 */
	public void plus(int y,int x){
		blocks[y][x].c++;
		if (!blocks[y][x].isBomb) {
			blocks[y][x].s.setText(""+blocks[y][x].c);
		}
	}
	
	/**
	 * Add the number a to the existing number of marked blocks.
	 * @param a either 1 or -1
	 */
	public void setMarks(int a){
		marks += a;
		game.rem.setText(Game.number-marks+"");
		boolean won = true;
		if (marks == Game.number) {
				for (int i = 0; i < Y; i++) {
					for (int j = 0; j < X; j++) {
						if (blocks[i][j].marked && !blocks[i][j].isBomb) {
							won= false;
						}
					}
				}
				if (won){
					Main.gameWon(game);
				}
		}
	}
	
	public void fixWidth(){
		Rectangle2D screen = Screen.getPrimary().getVisualBounds();
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[i].length; j++) {
				blocks[i][j].btn.setPrefWidth(screen.getWidth() / X);
				blocks[i][j].btn.setPrefHeight((screen.getHeight() - game.panel.getHeight()) / Y);
			}
		}
	}
}
