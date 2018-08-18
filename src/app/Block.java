package app;

import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Block extends StackPane{
	int index;
	boolean isBomb;
	boolean marked;
	int c = 0;
	int ix;
	int iy;
	public Board board;
	public Button btn;
	public Label s = new Label();
	public Block[][] blocks ;
	
	public Block(int IX,int IY,final Game game,final Board Board){
		this.setId("block");
		this.ix= IX;
		this.iy= IY;
		this.board = Board;
		this.blocks = Board.blocks;
		s.setText(""+c);
		btn = new Button("");
		btn.setPrefSize(50, 50);
		btn.setId("btn");
		this.getChildren().addAll(s,btn);
		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {	
		    	if (e.getButton() == MouseButton.PRIMARY) {
	    		 if(isBomb()){
		    		 for (int i = 0; i < blocks.length; i++) {
						for (int j = 0; j < blocks[i].length; j++) {
							blocks[i][j].btn.setVisible(false);
						}
					}
		    		 final ImageView image = new ImageView(new Image(Block.class.getResource("images/boom.gif").toString()));
		    		 image.setFitHeight(Main.root.getHeight());
		    		 image.setFitWidth(Main.root.getWidth());
		    		 Main.root.getChildren().addAll(image);
		    		 Transition trans = new Transition() {
		    			 {
		    		         setCycleDuration(Duration.millis(500));
		    		     }
						protected void interpolate(double frac) {
							if (frac > 0.9) {
								Main.root.getChildren().remove(image);
							}
						}
					};
					trans.play();
		    		game.panel.getChildren().clear();
		    		HBox hb =new HBox(50);
		    		Label jn = new Label("Oops! You're dead.");
		    		jn.setStyle("-fx-font-size:18px;-fx-text-fill:black;");
		    		ImageView km = new ImageView(new Image(this.getClass().getResource("images/bomb2.png").toString()));
		    		km.setFitHeight(30);
		    		km.setFitWidth(35);
		    		hb.getChildren().addAll(jn,km);
		    		game.panel.setCenter(hb);
		    		board.setOnMouseClicked(new EventHandler<MouseEvent>() {
						public void handle(MouseEvent event) {
							Main.endGame("b");
						}
					});
		    	 }else{
		    		 if (!marked) {
		    			 btn.setVisible(false);
			    		 if (c == 0) {
			    			 int xx = iy * game.y + ix;
			    			 int[] z = Board.zeros;
			    			 if(xx < ((game.x * game.y)/2)) {
								for (int i = 0; i < (z.length/2); i++) {
									int yyy = z[i] / game.y;
									int xxx = z[i] % game.y;
									blocks[yyy][xxx].btn.setVisible(false);
								}
			    			 }else{
								for (int i = (z.length/2); i < z.length; i++) {
									int yyy = z[i] / game.y;
									int xxx = z[i] % game.y;
									blocks[yyy][xxx].btn.setVisible(false);
								}
							}
						}
					}
		    	 }
	    	 }else{ 
				if (!marked) {
					if (Board.marks < Game.number) {
						btn.setStyle("-fx-background-color:#f5675f;-fx-text-fill:white;");
						marked = true;
						Board.setMarks(1);
					}
				}else{
					btn.setStyle("-fx-background-color:#7bacea;");
					marked = false;
					Board.setMarks(-1);
				}
			}
     	}	
     });
	btn.setPrefWidth(Main.stage.getWidth()/blocks[1].length);
	}
	
	/**
	 * @return if the block is a bomb or not
	 */
	public  boolean isBomb(){
		return this.isBomb;
	}
	
	/**
	 * Sets the value of the boolean isBomb
	 * @param f is it a Bomb ? 
	 */
	public void setIsBomb(boolean f){
		this.isBomb = f;
	}
	
}