//Ahmet Salih Demir
import javafx.application.Application;
	import javafx.geometry.Pos;
	import javafx.scene.*;
	import javafx.stage.*;
	import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
	
	
	public class Main extends Application {
		Stage stage;                                           //Oyun için ana pencere.
		Scene startScene, gameScene, loseScene, nextLevelRoot; //Oyun içerisinde geçiş yapılacak farklı sahneler.
		Game game;                                             //Oyun motorunu çağıran bir game değişkeni.
		private int currentLevel = 1;                          //Level bilgisini 1'e eşitledik.
	    @Override
	    public void start(Stage stage) {
	    	this.stage = stage;
	    	
	    	//START SCENE
	    	VBox startRoot = new VBox(20);
	    	Button start = new Button("Start Game");
	    	start.setPrefWidth(250);
	    	start.setPrefHeight(100);
	    	start.setStyle(
	    	        "-fx-background-color: #7ec8e3;" +  
	    	        "-fx-text-fill: white;" +           
	    	        "-fx-border-color: #1e3a5f;" +      
	    	        "-fx-border-width: 3;" +
	    	        "-fx-background-radius: 8;" +
	    	        "-fx-border-radius: 8;"
	    	);
	    	start.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    	Image image = new Image(getClass().getResource("/background.png").toExternalForm());
	    	BackgroundImage bg = new BackgroundImage(
	    	    image,
	    	    BackgroundRepeat.NO_REPEAT,
	    	    BackgroundRepeat.NO_REPEAT,
	    	    BackgroundPosition.CENTER,
	    	    new BackgroundSize(1500, 700, true, true, true, true)
	    	);
	    	startRoot.setBackground(new Background(bg));
	    	startRoot.getChildren().add(start);
	    	startRoot.setAlignment(Pos.CENTER);
	    	startScene = new Scene(startRoot, 1500, 700);
	    	
	    	
	    	//NEXT LEVEL SCENE
	    	VBox nextRoot = new VBox(20);
	    	Label nextLabel = new Label("YOU WON");
	    	nextLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    	nextLabel.setTextFill(Color.web("#1e3a5f"));
	    	Button next = new Button("Next Level");
	    	next.setPrefWidth(250);
	    	next.setPrefHeight(100);
	    	next.setStyle(
	    			 "-fx-background-color: #7ec8e3;" +  
	    		     "-fx-text-fill: white;" +            
	    		     "-fx-border-color: #1e3a5f;" +     
	    		     "-fx-border-width: 3;" +
	    		     "-fx-background-radius: 8;" +
	    		     "-fx-border-radius: 8;"
	    		);
	    	next.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    	Image image1 = new Image(getClass().getResource("/winbg.png").toExternalForm());
	    	BackgroundImage bg1 = new BackgroundImage(
	    	    image1,
	    	    BackgroundRepeat.NO_REPEAT,
	    	    BackgroundRepeat.NO_REPEAT,
	    	    BackgroundPosition.CENTER,
	    	    new BackgroundSize(1500, 700, true, true, true, true)
	    	);
	    	nextRoot.setBackground(new Background(bg1));
	    	nextRoot.getChildren().add(nextLabel);
	    	nextRoot.getChildren().add(next);
	    	nextRoot.setAlignment(Pos.CENTER);
	    	nextLevelRoot = new Scene(nextRoot, 1500, 700);
	    	
	    	//LOSE SCENE
	    	VBox loseRoot = new VBox(20);
	    	Button retry = new Button("Back to Main Menu");
	    	retry.setPrefWidth(250);
	    	retry.setPrefHeight(100);
	    	retry.setStyle(
	    		    "-fx-background-color: #f4a261;" +
	    		    "-fx-text-fill: #8B4513;" +        
	    		    "-fx-border-color: #e76f51;" +    
	    		    "-fx-border-width: 2;" +
	    		    "-fx-font-size: 20px;" +
	    		    "-fx-font-weight: bold;" +
	    		    "-fx-background-radius: 8;" +
	    		    "-fx-border-radius: 8;"
	    		);
	    	retry.setFont(Font.font("Arial", FontWeight.BOLD, 20));
	    	Image image2 = new Image(getClass().getResource("/gameover.png").toExternalForm());
	    	BackgroundImage bg2 = new BackgroundImage(
	    	    image2,
	    	    BackgroundRepeat.NO_REPEAT,
	    	    BackgroundRepeat.NO_REPEAT,
	    	    BackgroundPosition.CENTER,
	    	    new BackgroundSize(1500, 700, true, true, true, true)
	    	);
	    	loseRoot.setBackground(new Background(bg2));
	    	loseRoot.getChildren().add(retry);
	    	loseRoot.setAlignment(Pos.CENTER);
	    	loseScene = new Scene(loseRoot, 1500, 700);
	    	
	    	//Başlangıç ekranımızdaki butonumuzu oyunun 1. bölümünü başlatması için ayarlıyoruz.
	    	start.setOnAction(e->{
	    		startGame(1);
	    	});
	    	//Kaybetme ekranındaki tuşumuzu başlangıç ekranına götürmesi için ayarlıyoruz.
	    	retry.setOnAction(e->{
	    		stage.setScene(startScene);
	    	});
	    	//Kazanılan bölümün ardından diğer bölümü başlatması için next tuşumuzu ayarlıyoruz.
	    	next.setOnAction(e->{
	    		startGame(currentLevel+1);
	    	});
	    	//İlk ekran olarak bşlangıç ekranımı ayarlıyoruz.
	    	stage.setScene(startScene);
	    	stage.setTitle("Tower Defense Game");
	    	stage.show();
	    }
	    //Belirtilen levele göre oyun döngüsünü başlatan method.
	    public void startGame(int level) {
	    	currentLevel = level;
	    	game = new Game(this, level);
	    	gameScene = new Scene(game.getRoot(), 1500, 700);
	    	stage.setScene(gameScene);
	    }
	    //Levelin kaybedilmesi durumunda kaybetme ekranını getiren method.
	    public void gameOver() {
	    	stage.setScene(loseScene);
	    }
	    //Levelin tamamlanması durumunda eğer leveller bittiyse başlangıç ekranına diğer durumda next level sahnesine geçiren method.
	    public void levelComplete(int nextLevel) {
	    	if(nextLevel > 5) {
	    		stage.setScene(startScene);
	    	}else{
	    		stage.setScene(nextLevelRoot);
	    	}
	    }
	    public static void main(String[] args) {
	        launch(args);
	    }
	}
	
	
	
	
	
	
	
	
	
	
	
	
