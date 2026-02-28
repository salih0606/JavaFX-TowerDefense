//Ahmet Salih Demir
import java.util.*;

import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Game{
	private BorderPane root;             //Ana sahne yapımız.
	private Main main;                   
	private LevelManager levelmanager;   //Seviyeler arası kontrol için levelManager
	private UIPanel uiPanel;             //Kulelerin ana yapısını oyuna eklemek için UIPanel
	private Map map;                     //Düşman hareketlerinde kullanılacak Map.
	private TowerDragAndDrop dragAndDrop;//Kule yerleştirme sistemi için dragAndDrop.
	private boolean isSpawning = false; // Düşman üretiliyor mu?
	private int level;                               //Anlık level bilgisi.
	private List<Tower> towers = new ArrayList<>();  //Sahneye eklemek için kule listesi.
	private List<Bullet> bullets = new ArrayList<>();//Sahneye eklemek için mermi listesi.
	private WaveManager wave;                        //Düşman dalga yönetimi için dalga yöneticisi.
	private List<Enemy> enemies = new ArrayList<>(); //Sahneye eklemek için düşman listesi.
	private List<Cell> path;                         //Düşmanların ilerleyeceği pathi belirten hücre listesi.
	StackPane gameArea;                              //Oyun arayüzünde farklı paneleri içerisinde barındıracak olan genel pane.
	Pane enemyLayer;                                 //Düşmanların bulunduğu katman.
	Pane bulletLayer;                                //Mermilerin bulunduğu katman,
	Pane towerLayer;                                 //Kulelerin bulunduğu katman.
	
	public Game(Main main, int level) {
		this.main = main;
		this.level = level;
		
		//Oyun katmanlarını oluşturuyoruz.
		gameArea = new StackPane();
		enemyLayer = new Pane();
		bulletLayer = new Pane();
		towerLayer = new Pane();
		
		enemyLayer.setPickOnBounds(false);
		bulletLayer.setPickOnBounds(false);
		towerLayer.setPickOnBounds(false);
		
		//Harita ve path bilgisini alıyoruz.
		map = new Map(level);
		path = map.getPath();
		
		//Düşman spawnı için level dosyalarına göre dalga bilgisini alıyoruz.
		wave = new WaveManager("level"+level+".txt");
		
		//Katmanları genel panemize ekliyoruz.
		gameArea.getChildren().addAll(map.getView(), enemyLayer,towerLayer, bulletLayer);
		
		//Sürükle bırak sistemini initialize ediyoruz.
		dragAndDrop = new TowerDragAndDrop(map, towerLayer, this, enemies, gameArea);
		
		//UIPanel classımızdaki değişkenlerele oluşturuluyor.
		uiPanel = new UIPanel(this, towerLayer, dragAndDrop);
		
		
		
		//Oyun arayüzünü ve UIPaneli daha genel ve nihai oyun ekranımıza ekliyoruz.
		root = new BorderPane();
		root.setCenter(gameArea);
		root.setRight(uiPanel.getNode());
		
		//Bölüm geçiş kontrollerini gerekli değikenlere bağlıyoruz.
		levelmanager = new LevelManager();
		levelmanager.bindUI(uiPanel);
		levelmanager.bindMain(main);
		levelmanager.bindGame(this);
		levelmanager.startLevel(level);
		
		//Ana oyun döngüsü için bir animation timer başlatıyoruz.
		AnimationTimer gameLoop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				update();//Her frame için update methodumuzu çağırıyoruz.
			}
		};
		
		gameLoop.start();
	}
	//Bizim oyun elementlerimiz ile update methodumuzu dolduruyoruz.
	private void update() {
	    // 1. O ANKİ düşmanların bir kopyasını (fotoğrafını) alıyoruz.
	    // Böylece döngü dönerken Timeline yeni düşman eklerse veya oyun biterse sistem çökmez!
	    List<Enemy> currentEnemies = new ArrayList<>(enemies);
	    List<Enemy> enemiesToRemove = new ArrayList<>();

	    // Orijinal listeyi değil, aldığımız kopyayı geziyoruz
	    for (Enemy enemy : currentEnemies) {
	        enemy.move(path);
	        
	        if (enemy.isDead()) {
	        	new DeathParticleEffect(enemy.getX(), enemy.getY(), enemyLayer);
	            enemiesToRemove.add(enemy);
	            levelmanager.enemyKilled(); // Para gelsin
	        } 
	        else if (enemy.reachedEnd()) {
	            enemiesToRemove.add(enemy);
	            levelmanager.enemyEscaped(); // Can gitsin
	        }
	    }

	    // 2. Not aldığımız düşmanları asıl listeden ve ekrandan SİMDİ sil
	    for (Enemy deadEnemy : enemiesToRemove) {
	        enemyLayer.getChildren().remove(deadEnemy.getView());
	        enemies.remove(deadEnemy);
	    }

	    // 3. Kuleleri güncelle (Aynı güvenlik önlemini kulelere de yapıyoruz)
	    List<Tower> currentTowers = new ArrayList<>(towers);
	    for (Tower tower : currentTowers) {
	        tower.update(0.016);
	    }
	    if (levelmanager.areAllWavesStarted() && !isSpawning && enemies.isEmpty()) {
	        levelmanager.levelWon(); // Zafer!
	    }
	}
	
	//Oyun ekranını döndüren methodumuz.
	public Pane getRoot() {
		return root;
	}
	//Düşman spawnını kontrol eden genel methodumuz(Time line ile aralıklı spanw).
	public void spawnEnemies(WaveData waveData) {
		isSpawning = true;
		//Time line boş şekilde oluşturuluyor ve birazdan sırayla düşman oluşturmak için keyframeler eklenecek.
		Timeline spawner = new Timeline();
		int count = waveData.enemyCount;
		double interval = waveData.betweenTime;
		
		
		for(int i = 0; i < count; i++) {
			//Key frame time line içerisine yerleştirilen bir zaman parçası gibi düşünülebilir.
			//Düşmanların belirli aralıklarla spawn edilmesini sağlayan yapı.
			KeyFrame kf = new KeyFrame(Duration.seconds(interval * i), e->{
				Cell startCell = path.get(0);//Düşmanlar için başlangıç hücremizi ayarlıyoruz.
				Rectangle startRect = startCell.getView();//Başlangıc hücremizi bir dikdötrgene bağlıyoruz.
				Point2D scenePoint = startRect.localToScene(20, 20);//İlk hücrenin ortasında spawn için 20 ye 20 den başlıyoruz.
				Point2D localInEnemyLayer = enemyLayer.sceneToLocal(scenePoint);//Burada da local to scene ve scene to local kullanarak düşmanların ekran üzerinde doğru yollaraı takip etmesini sağlıyoruz.
				Enemy enemy = wave.spawnEnemy(localInEnemyLayer.getX(), localInEnemyLayer.getY());//Wave manager içsrisindeki spawn enemy methodumuzu global şekle çevirdiğimiz x y değerlerine göre spawn ediyoruz.
				enemy.setPath(path);//Düşmanlar için path atıyoruz.
				enemies.add(enemy);//Oluşturulan düşmanları genel düşman listemize ekliyoruz.
				enemyLayer.getChildren().add(enemy.getView());//Düşmanları görünür kılıyoruz.
				
			});
			//Time line yi key frame parçalarıyla çalıştırıyoruz.
			spawner.getKeyFrames().add(kf);
		}
		spawner.setOnFinished(e -> isSpawning = false);
		spawner.play();
	}
	//Map döndüren method.
	public Map getMap() {
		return this.map;
	}
	//belirtilen kuleyi genel kule listemize ekliyoruz.
	public void addTower(Tower tower) { 
	    towers.add(tower);
	   
	}
	// Kule satıldığında onu oyun döngüsünden silmek için
    public void removeTower(Tower tower) {
        towers.remove(tower);
    }

    // LevelManager'a para aktarmak için köprü metot
    public void addMoney(int amount) {
        levelmanager.addMoney(amount);
    }
    public int getMoney() {
		return levelmanager.getMoney();
	}
	
	public void spendMoney(int amount) {
		levelmanager.spendMoney(amount);
	}
	
	
}






















