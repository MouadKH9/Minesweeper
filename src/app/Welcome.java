package app;

import java.sql.SQLException;
import java.sql.Statement;

import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Welcome extends BorderPane{
	boolean bombed = false;
	public Welcome(String reason,Game game){
		Main.connect();
		Main.root.setId("pauseMenu");
		Main.stage.setMaximized(false);
		if (reason == "b" ) {
			this.bombed = true;
		}
		Label message = new Label();
		message.setId("msg");
		message.setWrapText(true);
		if (bombed) {
			message.setText("You have been bombed! Give it another try:");
		}else if (reason == "won") {
			message.setText("You have won! Why dont you spice things up a bit?");
		}else{
			message.setText("Welcome! Start new game:");
		}
		this.setPadding(new Insets(20));
		this.setTop(message);
		
		VBox group = new VBox(30);
		
		group.setId("grp");
		
		HBox op = new HBox(10);
		
	
		final ComboBox<String> combo = new ComboBox<String>();
		combo.setId("combo");
		combo.getItems().addAll(
			        "Height:9 & Width:9",
			        "Height:16 & Width:16",
			        "Height:20 & Width:20"
			    );
		combo.getSelectionModel().select(Main.DEF_SIZE - 1);
		final ToggleGroup tgroup = new ToggleGroup();
		RadioButton easy = new RadioButton("Easy");
		easy.setSelected(true);
		easy.setToggleGroup(tgroup);
		easy.setUserData("E");
		
		RadioButton medium = new RadioButton("Medium");
		medium.setToggleGroup(tgroup);
		medium.setUserData("M");
		
		RadioButton hard = new RadioButton("Hard");
		hard.setToggleGroup(tgroup);
		hard.setUserData("H");
		
		VBox rds = new VBox(5);
		rds.getChildren().addAll(easy,medium,hard);
		
		op.setAlignment(Pos.CENTER_LEFT);
		op.getChildren().addAll(combo,rds);
		
		tgroup.getToggles().get(Main.DEF_LEVEL-1).setSelected(true);
		final Button button = new Button("Start Game");
		button.setId("wbtn");
		button.setCursor(Cursor.HAND);
		button.setOnAction(new EventHandler<ActionEvent>() {
		     public void handle(ActionEvent e) {
		    	 Transition tran = new Transition() {
		    		 {
		    			 setCycleDuration(Duration.millis(500));
		    		 }
					protected void interpolate(double frac) {
						button.setTranslateX(button.getLayoutX() + frac * 600);
					}
				};
				tran.play();
				tran.setOnFinished(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
							int index = combo.getSelectionModel().getSelectedIndex();
							int levelMod;
							int i;
							if (tgroup.getSelectedToggle().getUserData().toString().equals("E")) {
								levelMod = 8;
								 i = 1;
							}else if (tgroup.getSelectedToggle().getUserData().toString().equals("M")) {
								levelMod = 6;
								i = 2;
							}else{
								levelMod = 4;
								i= 3;
							}
						
						try{
							Statement stmt = Main.c.createStatement();
				    	 switch (index) {
				    	 case 0:
							Main.startGame(9, 9, levelMod );
							stmt.executeUpdate("UPDATE game SET size = 1 WHERE username = '"+ Main.USERNAME +"'");
							break;
				    	 case 1:
							Main.startGame(16, 16, levelMod);
							stmt.executeUpdate("UPDATE game SET size = 2 WHERE username = '"+ Main.USERNAME +"'");
							break;
				    	 case 2:
							Main.startGame(20, 20, levelMod);
							stmt.executeUpdate("UPDATE game SET size = 3 WHERE username = '"+ Main.USERNAME +"'");
							break;	

				    	 default:
							break;
						}
				    	 Main.c.createStatement().executeUpdate("UPDATE game SET level = "+ i +" WHERE username = '"+ Main.USERNAME +"'");
						}catch (SQLException e) {
							e.printStackTrace();
						}
					}
				});
		    }
		    
		});
		
		
		group.getChildren().addAll(op,button);
		
		this.setCenter(group);
	}
	
}
