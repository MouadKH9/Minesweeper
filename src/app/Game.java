package app;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Game extends BorderPane{
	public BorderPane panel;
	int x ;	
	int y ;
	public static int number;
	int seconds = 0;
	int minutes = 0;
	StackPane root = Main.root;
	public int levelMod;
	public Label rem;
	private Label time = new Label(00+":"+00);
	public boolean paused = false;
	
	
	@SuppressWarnings("static-access")
	public Game(int X,int Y,int LevelMod){
		this.x = X;
		this.y = Y;
		this.levelMod= LevelMod;
		this.number = (x*y)/levelMod;
		root.setId(null);
		panel = new BorderPane();
		Board board =  new Board(x,y,this.number,this);
		rem = new Label(number+"");
		panel.setPadding(new Insets(10, 0, 10, 0));
		rem.setStyle("-fx-font-size:22px;-fx-text-fill:black;");
		time.setStyle("-fx-font-size:22px;-fx-text-fill:black;");
		ImageView clock = new ImageView(new Image(this.getClass().getResource("images/clock.png").toString()));
		clock.setFitHeight(20);
		clock.setFitWidth(20);
		time.setGraphic(clock);
		time.setContentDisplay(ContentDisplay.RIGHT);
		
		ImageView bombIcon = new ImageView(new Image(this.getClass().getResource("images/bomb-i.png").toString()));
		bombIcon.setFitHeight(20);
		bombIcon.setFitWidth(20);
		rem.setGraphic(bombIcon);
		rem.setContentDisplay(ContentDisplay.LEFT);
		
		BorderPane.setMargin(rem, new Insets(0, 0, 0, 10));
		BorderPane.setMargin(time, new Insets(0, 10, 0, 0));
		panel.setLeft(rem);
		
		panel.setRight(time);
		panel.setId("panel");
		
		HBox box = new HBox(0);
		BorderPane.setMargin(box, new Insets(10));
		
		Button pause = new Button();
		ImageView Ipause = new ImageView(new Image(this.getClass().getResource("images/pause.png").toString()));
		Ipause.setFitHeight(30);
		Ipause.setFitWidth(30);
		pause.setGraphic(Ipause);
		pause.setBackground(null);
		pause.setCursor(Cursor.HAND);
		
		pause.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				pause();
			}
		});	
		
		Button about = new Button();
		ImageView Iabout = new ImageView(new Image(this.getClass().getResource("images/about.png").toString()));
		Iabout.setFitHeight(30);
		Iabout.setFitWidth(30);
		about.setGraphic(Iabout);
		about.setBackground(null);
		about.setCursor(Cursor.HAND);
		
		about.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				about();
			}
		});	
		
		Button help = new Button();
		ImageView Ihelp = new ImageView(new Image(this.getClass().getResource("images/help.png").toString()));
		Ihelp.setFitHeight(30);
		Ihelp.setFitWidth(30);
		help.setGraphic(Ihelp);
		help.setBackground(null);
		help.setCursor(Cursor.HAND);
		help.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				help();
			}
		});	
		
		box.getChildren().addAll(about,pause,help);
		BorderPane.setAlignment(time, Pos.CENTER_RIGHT);
		BorderPane.setAlignment(rem, Pos.CENTER_LEFT);
		BorderPane.setAlignment(box, Pos.CENTER);
		box.setAlignment(Pos.CENTER);
		panel.setCenter(box);
		help.setId("button");
		pause.setId("button");
		about.setId("button");
		
		this.setTop(panel);
		this.setCenter(board);
		
		if (y>12) {
			Main.stage.setMaximized(true);
			panel.setMaxHeight(100);
			board.fixWidth();
		}else{
			Main.stage.setWidth(55 * x);
			Main.stage.setHeight(50 * y + panel.getHeight() + 10);
		}
		
		 Service<Void> service = new Service<Void>() {
	            @Override
	            protected Task<Void> createTask() {
	                return new Task<Void>() {           
	                	@Override
	                    
	                    protected Void call() throws Exception {
	                        //Background work                       
	                        final CountDownLatch latch = new CountDownLatch(1);
	                        Platform.runLater(new Runnable() {                          
	                            @Override
	                            public void run() {
	                                try{
	                                	
	                                	Timer timer = new Timer();
	                                	
	                                	timer.scheduleAtFixedRate(new TimerTask() {
	                                		  @Override
	                                		  public void run() {
	                                			  if (!paused) {
	                                				  seconds++;
		                                			  if (seconds==60) {
														seconds = 0;
														minutes++;
		                                			  }
												}
	                                		  }
	                                		}, 1000, 1000);
	                                }finally{
	                                    latch.countDown();
	                                }
	                            }
	                        });
	                        latch.await();                      
	                        return null;
	                    }
	                };       
	            }
	        };
	        service.start();
	        Transition t = new Transition() {
	        	{
	                setCycleDuration(Duration.seconds(1000));
	            }
				@Override
				protected void interpolate(double frac) {
					if (!paused) {
						time.setText(String.format("%02d", minutes)+":"+String.format("%02d", seconds));
					}
				}
			};
			t.play();
	}
	
	public void pause(){
		paused = true;
		final BorderPane pauseMenu = new BorderPane();
		
		pauseMenu.setId("pauseMenu");
		//Label making
		Label title = new Label("Paused");
		title.setStyle("-fx-text-fill:#2ecc71;-fx-font-size:30px;");
		pauseMenu.setTop(title);
		BorderPane.setAlignment(title, Pos.CENTER);
		BorderPane.setMargin(title, new Insets(20));
		//buttons making
		VBox list = new VBox(10);
		
		final Button resume = new Button("Resume");
		resume.setId("pbtn");
		resume.setCursor(Cursor.HAND);
		resume.setPrefWidth(200);
		
		final Button quitMenu = new Button("Quit To Menu");
		quitMenu.setId("pbtn");
		quitMenu.setCursor(Cursor.HAND);
		quitMenu.setPrefWidth(200);
		quitMenu.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation");
				alert.setContentText("Are you sure you wanna quit this game?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					Main.endGame("quit");
				}
			}
		});
		
		
		final Button quit = new Button("Quit");
		quit.setId("pbtn");
		quit.setCursor(Cursor.HAND);
		quit.setPrefWidth(200);
		quit.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation");
				alert.setContentText("Are you sure you wanna quit?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
				    Main.exit();
				}
			}
		});
		
		list.getChildren().addAll(resume,quitMenu,quit);
		list.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(list, Pos.CENTER);
		pauseMenu.setCenter(list);
		
		root.getChildren().addAll(pauseMenu);
		
		resume.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Button[] btns = {resume,quitMenu,quit};
				unpause(pauseMenu,btns);
			}
		});
		
		FadeTransition ft = new FadeTransition(Duration.millis(500), pauseMenu);
		ft.setFromValue(0);
		ft.setToValue(1);
		ft.play();
	}
	
	public  void unpause(final BorderPane pauseMenu,Button[] list){
		SequentialTransition seq =new SequentialTransition();
		 for (int i = 0; i < list.length; i++) {
			 final Button t = list[i];
			 final Animation animation = new Transition() {
			     {
			         setCycleDuration(Duration.millis(400));
			     }
			 
			     protected void interpolate(double frac) {
			         t.setTranslateX(frac*900);
			     }
			
		 };
		 	seq.getChildren().add(animation);
		}
		 FadeTransition ft = new FadeTransition(Duration.millis(500), pauseMenu);
		 ft.setFromValue(1);
		 ft.setToValue(0);
		 seq.getChildren().add(ft);
		 seq.play();
		 seq.setOnFinished(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				paused = false;
				root.getChildren().remove(pauseMenu);
				
			}
		});
	}
	
	public void help(){
		paused = true;
		final BorderPane pauseMenu = new BorderPane();
		pauseMenu.setId("pauseMenu");
		
		Label title = new Label("Need some help? ");
		title.setStyle("-fx-text-fill:#2ecc71;-fx-font-size:30px;");
		pauseMenu.setTop(title);
		BorderPane.setAlignment(title, Pos.CENTER_LEFT);
		pauseMenu.setPadding(new Insets(30));
		
		String helptext = "The game is pretty simple! the numbers in each block indicates the number of bombs surrounding the block\n"
				+ " The objective is locate and mark all the bombs! you can mark each bomb by right clicking the block you think is a bomb!";
		
		Text text =new Text(helptext);
		text.setStyle("-fx-font-size:18px;");
		text.setFill(Color.WHITE);
		text.setTextAlignment(TextAlignment.JUSTIFY);
		text.setWrappingWidth(root.getWidth() - 50);
		BorderPane.setMargin(title, new Insets(50));
		
		final Button resume = new Button("Close");
		resume.setId("pbtn");
		resume.setCursor(Cursor.HAND);
		resume.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Button[] btns = {resume};
				unpause(pauseMenu,btns);
			}
		});
		
		VBox b =new VBox(10);
		b.getChildren().addAll(text,resume);
		
		pauseMenu.setTop(title);
		pauseMenu.setCenter(b);
		root.getChildren().add(pauseMenu);
	}

	public void about(){
		paused = true;
		final BorderPane pauseMenu = new BorderPane();
		pauseMenu.setId("pauseMenu");
		
		Label title = new Label("About");
		title.setStyle("-fx-text-fill:#2ecc71;-fx-font-size:30px;");
		pauseMenu.setTop(title);
		BorderPane.setAlignment(title, Pos.CENTER_LEFT);
		pauseMenu.setPadding(new Insets(30));
		
		String abouttext = "Designed by Mouad\nProgrammed by Mouad\nBasically I've done everything so just go ahead and continue playing.";
		Text text =new Text(abouttext);
		text.setStyle("-fx-font-size:18px;");
		text.setFill(Color.WHITE);
		text.setTextAlignment(TextAlignment.JUSTIFY);
		text.setWrappingWidth(root.getWidth() - 50);
		BorderPane.setMargin(title, new Insets(50));
		
		final Button resume = new Button("Close");
		resume.setId("pbtn");
		resume.setCursor(Cursor.HAND);
		resume.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				Button[] btns = {resume};
				unpause(pauseMenu,btns);
			}
		});
		
		VBox b =new VBox(10);
		b.getChildren().addAll(text,resume);
		
		pauseMenu.setTop(title);
		pauseMenu.setCenter(b);
		root.getChildren().add(pauseMenu);
	}
}
