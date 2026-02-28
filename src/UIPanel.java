//Burak Berk Demirbaş

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class UIPanel {
	
	//aslında en başta bu Labelları fied olarak değil de direkt new Label yaparak oluşturup 
	//içine texti yazıp getLive vb methodlarla anlık olarak getirmeyi denedim fakat böyle yapınca güncellenmediğini farkettim o yüzden data field olarak oluşturdum
	//bu yüzden direkt olarak içindeki tüm texti değiştireceğim
	private Label liveLabel;
	private Label moneyLabel;
	private Label nextWave;
    private VBox vbox;
    private ImageView img1, img2 ,img3, img4;
    private Game game;
    
    /*Bu UIPanel Constructerı ile Drag-Drop işelmi için dragAndDrop variableı oluşturuyorum DragAndDrop classındaki drag methodunu kullanmak için  
     *Ayrıca bu constructerın temel mantığı sağ paneldeki 3 farklı label ve 4 farklı (kulelerin ve fiyatının bulunduğu) stackPaneleri birleştirmek (Vbox ile)
     */ 
	public UIPanel(Game game, Pane towerLayer, TowerDragAndDrop dragAndDrop) {
		this.game = game;
	
	/*(öncelikle kuleleri alacağımız dikdörtgen kutuları yapıyorum)
	* aşağıdaki satırlarda önce yeni bir dikdörtgen objesi oluşturdum ve bu dikdörtgenin bazı özelliklerini
	* ayarladım(kenar rengi köşe yumuşaklığı iç dolgu rengi vb)
	* bu dikdörtgenin özelliklerini ayarladıktan sonra bu objenin üstüne bir yazı ve resim ekleyeceğim için 
	* stackpane oluşturuyorum ve bu stack panein üstüne önce dikdörtgeni daha sonra ise vbox ile birleştirmiş
	* olduğum image ve texti ekliyorum
	*/
	
	Rectangle r1 = new Rectangle(50,50,180,105);
	r1.setStroke(Color.YELLOW);
	r1.setFill(Color.LIGHTSALMON);
	r1.setArcWidth(15);
	r1.setArcHeight(15);
	
	VBox vBox1 = new VBox(5);
	Image image1 = new Image("cannon.png");
	img1 = new ImageView(image1);
	img1.setFitHeight(65);
	img1.setFitWidth(55);
	vBox1.getChildren().add(img1);
	vBox1.getChildren().add(new Label("Single Shot Tower - 50$"));
	vBox1.setAlignment(Pos.CENTER);
	
	StackPane stackpane1 = new StackPane();
	stackpane1.getChildren().addAll(r1,vBox1);
	
	//Aynı işlemleri diğer 3 adet tower seçme kutucukları için de tekrarlıyorum
	 
	Rectangle r2 = new Rectangle(50,50,180,105);
	r2.setStroke(Color.YELLOW);
	r2.setFill(Color.LIGHTSALMON);
	r2.setArcWidth(15);
	r2.setArcHeight(15);
	
	VBox vBox2 = new VBox(5);
	Image image2 = new Image("infernoTower.png");
	img2 = new ImageView(image2);
	img2.setFitHeight(55);
	img2.setFitWidth(50);
	vBox2.getChildren().add(img2);
	vBox2.getChildren().add(new Label("Laser Tower - 120$"));
	vBox2.setAlignment(Pos.CENTER);
	
	StackPane stackpane2 = new StackPane();
	stackpane2.getChildren().addAll(r2,vBox2);
	
	
	Rectangle r3 = new Rectangle(50,50,180,105);
	r3.setStroke(Color.YELLOW);
	r3.setFill(Color.LIGHTSALMON);
	r3.setArcWidth(15);
	r3.setArcHeight(15);
	
	VBox vBox3 = new VBox(5);
	Image image3 = new Image("tripleCannon.png");
	img3 = new ImageView(image3);
	img3.setFitHeight(50);
	img3.setFitWidth(50);
	vBox3.getChildren().add(img3);
	vBox3.getChildren().add(new Label("Triple Shot Tower - 150$"));
	vBox3.setAlignment(Pos.CENTER);
	
	StackPane stackpane3 = new StackPane();
	stackpane3.getChildren().addAll(r3,vBox3);
	
	
	Rectangle r4 = new Rectangle(50,50,180,105);
	r4.setStroke(Color.YELLOW);
	r4.setFill(Color.LIGHTSALMON);
	r4.setArcWidth(15);
	r4.setArcHeight(15);
	
	VBox vBox4 = new VBox(5);
	Image image4 = new Image("havan.png");
	img4 = new ImageView(image4);
	img4.setFitHeight(50);
	img4.setFitWidth(50);
	vBox4.getChildren().add(img4);
	vBox4.getChildren().add(new Label("Missile Launcher Tower - 200$"));
	vBox4.setAlignment(Pos.CENTER);

	StackPane stackpane4 = new StackPane();
	stackpane4.getChildren().addAll(r4,vBox4);
	

	/* Daha sonra bu tower kutularının üstünde bulunan Live , Money ve Next Wave sayacını yapıyorum.
	 * 
	 * 
	 */
	liveLabel = new Label("Lives: 0");
	liveLabel.setFont(new Font("Arial",18));
	
	moneyLabel = new Label("Money: 100$");
	moneyLabel.setFont(new Font("Arial",18));
	
	nextWave = new Label("Next Wave: 0s");
	nextWave.setFont(new Font("Arial",18));

	/*Yukarıda yapmış olduğum tüm nodeları VBox içine sırayla atıyorum ve bunların ortalanması
	 * için setAlignment ve nodeların arasındaki boşlukları ayarlamak için setSpacing methodlarını kullanıyorum
	 */
	
	vbox = new VBox();
	vbox.setAlignment(Pos.CENTER);
	vbox.setSpacing(10);
	vbox.getChildren().addAll(liveLabel,moneyLabel,nextWave,stackpane1,stackpane2,stackpane3,stackpane4);
	vbox.setPadding(new Insets(20));
	
	dragAndDrop.drag(img1);
	dragAndDrop.drag(img2);
	dragAndDrop.drag(img3);
	dragAndDrop.drag(img4);
	
	}
	
	/*Yukarıdaki constructer ile oluşturmuş olduğum vBoxu ( sağ Panelin tamamını ) public bir getNode methodu ile başka classlardan erişime açık hale getiriyorum
	* daha sonra bunu map ile bir borderPane üzerinde birleştireceğiz ( Game classında )
	*/
	public Pane getNode() {  
		return vbox;
	}
	
	private int live;
	private int money;
	private int time;
	
	/* UIPaneldeki can sayısını güncellemek için kullanıyorum (Level Manager classında EnemyEscaped methodu çalışırsa ui.updateLives ile anlık can değeri geliyor
	 * ve bu method sayesinde güncelliyoruz. (Direkt Labelın içindeki texti yeniden yerleştiriyoruz.
	 */
	public void updateLives(int lives) {
		this.live=lives;
		liveLabel.setText("Lives: "+live);
	}
	
	//UIPaneldeki para sayısını güncellemek için kullanıyorum (LevelManager classında EnemyKilled çalıştığı zaman para artıyor ve bu methodu çağırarak göstergeyi güncelliyorum
	//Yine aynı şekilde direkt olarak Labelın içindeki texti yeniden yerleştiriyorum
	public void updateMoney(int money) {
		this.money=money;
		moneyLabel.setText("Money: "+this.money+"$");
	}
	//UIPaneldeki bir sonraki wavee kalan süreyi gösteren Labelı günceller Bunu da LevelManager classında StartNextWave methodu ve peşinden çağırdığı waveTimer
	//metodları ile yapar. Yine aynı şekilde Labelın içindeki texti yeniden yerleştirir 
	public void updateTime(int time) { 
		this.time=time;
		nextWave.setText("Next Wave: "+this.time+"s");
	}
	public ImageView getTowerIcon1() { //Kule 1 için görseli return eder
	    return img1;
	}
	public ImageView getTowerIcon2() { //Kule 2 için görseli return eder
	    return img2;
	}
	public ImageView getTowerIcon3() { //Kule 3 için görseli return eder
	    return img3;
	}
	public ImageView getTowerIcon4() { //Kule 4 için görseli return eder
	    return img4;
	}
}