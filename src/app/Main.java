package app;
	

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;


public class Main extends Application {
	
	public static int DEF_LEVEL;
	public static int DEF_SIZE;
	public static String USERNAME;
	public static String dburl;
	public static Connection c = null;
	
	static boolean first;
	static Scene scene;
	static Stage stage;
	public static StackPane root = new StackPane();
	static File file;
	
	@SuppressWarnings("static-access")
	public void start(Stage Stage) {
		try {
			this.stage = Stage;
			Welcome start = new Welcome("s",null);
			root.getChildren().add(start);
			root.setId("pauseMenu");
			scene = new Scene(root,500,300);
			scene.getStylesheets().add(getClass().getResource("res/application.css").toExternalForm());
			stage.setScene(scene);
			stage.show();
			stage.setResizable(false);
			stage.setTitle("MineSweeper");
			stage.getIcons().add(new Image(Main.class.getResource("images/icon.png").toString()));
			scene.widthProperty().addListener(new ChangeListener<Number>() {
			    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
			    	Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2); 
			        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);  
			    }
			});
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            public void handle(WindowEvent we) {
	            	Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirmation");
					alert.setContentText("Are you sure you wanna quit?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK){
					    Main.exit();
					}else{
						we.consume();
					}
	            }
	        });
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void endGame(String reason){
		root.setOnMouseClicked(null);
		root.getChildren().clear();
		root.getChildren().add(new Welcome(reason,null));
	}
	
	public static void startGame(int x,int y,int levelMod) {
		//x = 30 and y = 16
		root.getChildren().clear();
		Game game = new Game(x,y,levelMod);
		
		
		root.getChildren().add(game);
	}
	
	public static void gameWon(Game game){
		String option  = "e_1";
		if (game.levelMod == 8 && game.x==9) {
			option = "e_1";
		}else if(game.levelMod == 6 && game.x==9) {
			option = "m_1";
		}else if(game.levelMod == 4 && game.x==9) {
			option = "h_1";
		}else if (game.levelMod == 8 && game.x==16) {
			option = "e_2";
		}else if(game.levelMod == 6 && game.x==16) {
			option = "m_2";
		}else if(game.levelMod == 4 && game.x==16) {
			option = "h_2";
		}else if (game.levelMod == 8 && game.x==20) {
			option = "e_3";
		}else if(game.levelMod == 6 && game.x==20) {
			option = "m_3";
		}else if(game.levelMod == 4 && game.x==20) {
			option = "h_3";
		}
		int hs = 0;
		try {
			Statement stmt = Main.c.createStatement();
			ResultSet rs1 = stmt.executeQuery("SELECT * FROM highscores;");
			while (rs1.next()) {
				hs = rs1.getInt(option);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int minutes = game.minutes;
		int seconds = game.seconds;
		
		root.getChildren().clear();
		final StackPane panroot = new StackPane();
		BorderPane pan = new BorderPane();
		pan.setId("pan");
		
		Label lab = new Label("You have won!");
		ImageView happy =new ImageView(new Image(Main.class.getResource("images/happy.png").toString()));
		happy.setFitHeight(30);
		happy.setFitWidth(30);
		lab.setGraphic(happy);
		lab.setContentDisplay(ContentDisplay.RIGHT);
		lab.setId("lab");
		
		Label t1 = new Label("Your time: ");
		t1.setStyle("-fx-text-fill:#f1c40f;");
		Label utime = new Label(minutes + " minutes and " + seconds+ " seconds");
		Label t2 = new Label("Highscore: ");
		t2.setStyle("-fx-text-fill:#f1c40f;");
		Label highscore = new Label((int) hs/60 + " minutes and " + hs%60 + " seconds");
		
		
		VBox center = new VBox(10);
		center.getChildren().addAll(lab,new HBox(t1,utime),new HBox(t2,highscore));
		center.setAlignment(Pos.CENTER);
		
		pan.setCenter(center);
		pan.setPadding(new Insets(20));
		
		ImageView fire = new ImageView(new Image(Main.class.getResource("images/win.gif").toString()));
		ImageView fire1 = new ImageView(new Image(Main.class.getResource("images/win2.gif").toString()));
		ImageView fire2 = new ImageView(new Image(Main.class.getResource("images/win.gif").toString()));
		ImageView fire3 = new ImageView(new Image(Main.class.getResource("images/win2.gif").toString()));
		
		BorderPane left = new BorderPane();
		BorderPane right = new BorderPane();
		
		left.setTop(fire);
		left.setBottom(fire1);
		right.setTop(fire3);
		right.setBottom(fire2);
		
		stage.setWidth(700);
		stage.setHeight(600);
		pan.setLeft(left);
		pan.setRight(right);
		pan.setStyle("-fx-background-color:black;");
		
		panroot.getChildren().add(pan);
		if (minutes * 60 + seconds < hs && hs != 0) {
			try {
				Statement st = c.createStatement();
				st.executeUpdate("UPDATE highscores SET " + option + " = "+ minutes * 60 + seconds +";");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			final VBox X = new VBox(10);
			Label nice = new Label("You have beaten your highscore!");
			nice.setStyle("-fx-font-size:30px;");
			ImageView balloons = new ImageView(new Image(Main.class.getResource("images/balloons.png").toString()));
			balloons.setFitHeight(40);
			balloons.setFitWidth(40);
			nice.setGraphic(balloons);
			nice.setContentDisplay(ContentDisplay.RIGHT);
			
			Button dismiss = new Button("Dismiss");
			dismiss.setCursor(Cursor.HAND);
			dismiss.setId("dis");
			dismiss.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {
					FadeTransition ft = new FadeTransition();
					ft.setNode(X);
					ft.setFromValue(1);
					ft.setToValue(0);
					ft.setDuration(Duration.millis(300));
					ft.setOnFinished(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							panroot.getChildren().remove(X);
						}
					});
					ft.play();
				}
			});
			
			X.setMaxSize(500, 180);
			X.setAlignment(Pos.CENTER);
			X.setStyle("--fx-border-radius: 10 10 10 10;-fx-background-radius: 10 10 10 10;-fx-background-color:linear-gradient(to bottom right, #16a085, #27ae60);");
			X.getChildren().addAll(nice,dismiss);
			panroot.getChildren().add(X);
		}else if (hs == 0) {
			try {
				Statement st = c.createStatement();
				st.executeUpdate("UPDATE highscores SET " + option + " = "+ minutes * 60 + seconds +";");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			center.getChildren().remove(2);
		}
		
		root.getChildren().add(panroot);
		
		pan.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				root.setOnMouseClicked(null);
				endGame("won");
				
			}
			
		});
	}
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void connect(){
		try {
			dburl = System.getProperty("user.home")+"/minesweeper.db";
			File db = new File(dburl);
			boolean exists = true;
			if (!db.exists()) {
				exists = false;
			}
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+dburl);
			Statement stmt = c.createStatement();
			if (!exists) {
				
				setDb();
				
			}
			c.setAutoCommit(true);
		  	ResultSet rs = stmt.executeQuery( "SELECT * FROM game;" );
	  		USERNAME = rs.getString("username");
	  		DEF_SIZE = rs.getInt("size");
	  		DEF_LEVEL = rs.getInt("level");
		  	rs.close();
	    } catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );	
	    }
	}
	
	public static void setDb() throws SQLException{
			String sql = 	"CREATE TABLE `game` ("+
					"`username`	TEXT NOT NULL,"+
					"`level`	INTEGER NOT NULL DEFAULT 1,"+
					"`size`	INTEGER NOT NULL DEFAULT 1);";
			Statement stmt = c.createStatement();
			stmt.executeUpdate(sql);
			sql = 	"CREATE TABLE `highscores` ("+
					"`e_1`	INTEGER NOT NULL DEFAULT 0,"+
					"`e_2`	INTEGER NOT NULL DEFAULT 0,"+
					"`e_3`	INTEGER NOT NULL DEFAULT 0,"+
					"`m_1`	INTEGER NOT NULL DEFAULT 0,"+
					"`m_2`	INTEGER NOT NULL DEFAULT 0,"+
					"`m_3`	INTEGER NOT NULL DEFAULT 0,"+
					"`h_1`	INTEGER NOT NULL DEFAULT 0,"+
					"`h_2`	INTEGER NOT NULL DEFAULT 0,"+
					"`h_3`	INTEGER NOT NULL DEFAULT 0);";
			stmt.executeUpdate(sql);
			sql = 	"INSERT INTO highscores (e_1) VALUES (0);";
			stmt.executeUpdate(sql);
			boolean ok = false;
			while (!ok) {
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Username");
			dialog.setContentText("Please enter your name:");
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
				    USERNAME = result.get();
				    ok = true;
				}
			}
			sql = "INSERT INTO game (username) " +
			"VALUES ('"+ USERNAME +"');"; 
			stmt.executeUpdate(sql);
	}
	
	public static void exit(){
		try {
			c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Platform.exit();
        System.exit(0);
	}
	
}

