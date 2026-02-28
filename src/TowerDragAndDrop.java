//BURAK BERK DEMİRBAŞ
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TowerDragAndDrop {
    private Map map;
    private Pane towerLayer;
    private Game game;
	private List<Enemy> enemies ;
	private Pane gamePane;
	private ImageView draggingImage;
	private Circle rangeCircle;
	private Point2D offset;

    public TowerDragAndDrop(Map map, Pane towerLayer, Game game, List<Enemy> enemies, Pane gamePane) {
    	//drag and drop işlemi yapılacak objeyi başlatıyoruz. Bu işlemi Game classında yapıyorum
    	this.map = map;
        this.towerLayer = towerLayer;
        this.game = game;
        this.enemies = enemies;
        this.gamePane = gamePane;
    }

    public void drag(ImageView icon) {//belirli bir ikon için drag and drop işlemini başlatıyorum ( Aslında tek method ile alltaki methodların eventHandlerlarını başlatıyorum)
    	//icon.setOnMousePressed(this::handleMousePressed); yazımı ile icon.setOnMousePressed(e -> handleMousePressed(e)); eş değermiş 
    	icon.setOnMousePressed(e -> handleMousePressed(e));
    	icon.setOnMouseDragged(e -> handleMouseDragged(e));
    	icon.setOnMouseReleased(e -> handleMouseReleased(e));
    }
    
    private void handleMousePressed(MouseEvent e) {
        ImageView icon = (ImageView) e.getSource();

        draggingImage = new ImageView(icon.getImage());//geçici bir draggingImage oluşturuyorum. İkon ile aynı image aynı boyut 
        draggingImage.setFitWidth(icon.getFitWidth());
        draggingImage.setFitHeight(icon.getFitHeight());
        draggingImage.setOpacity(1);
        towerLayer.getChildren().add(draggingImage);

        rangeCircle = new Circle(110);//Range(circle) oluşturuyorum 
        rangeCircle.setFill(Color.rgb(0, 255, 0, 0.2));
        rangeCircle.setStroke(Color.GREEN);
        rangeCircle.setMouseTransparent(true);
        towerLayer.getChildren().add(rangeCircle);

        offset = new Point2D(e.getX(), e.getY());//ilk tıkladığımız yeri kaydederiz nereden tuttuysak oradan sürükleyebilmek için
        e.consume();
    }
    
    private void handleMouseDragged(MouseEvent dragEvent) {//sürükleme işlemi sırasında ikonu ve rangei o anki mouse konumuna göregüncellemek için ve rangei tam olarak ikonun ortasına hizalamak için bu  methodu kullanıyorum
        Point2D scenePoint = new Point2D(dragEvent.getSceneX(), dragEvent.getSceneY());
        Point2D localPoint = towerLayer.sceneToLocal(scenePoint);//üstte aldığımız scene üzerndeki konumu towerLayerın kendi koordinat sistemine göre dönüştürür 

        draggingImage.setLayoutX(localPoint.getX() - offset.getX());//üstte aldığımız bilgilere göre imageı yerleştirme işlemi
        draggingImage.setLayoutY(localPoint.getY() - offset.getY());
        
        //imageın tam merkezine yerleştirmek için bu işlemleri yapıyorum
        rangeCircle.setCenterX(draggingImage.getLayoutX() + draggingImage.getFitWidth() / 2);
        rangeCircle.setCenterY(draggingImage.getLayoutY() + draggingImage.getFitHeight() / 2);

        dragEvent.consume();
    }
    
    private void handleMouseReleased(MouseEvent releaseEvent) {
        Point2D scenePoint = new Point2D(releaseEvent.getSceneX(), releaseEvent.getSceneY());//fare konumunu alıyorum
        
        //Mape göre hangi koordinata geliyor bunu anlayıp cellsizea bölüyorum ki hangi satıra, sütuna bırakma işlemi yapılmış bunu anlayalım
        int cellSize = 40;
        int col = (int) ((scenePoint.getX() - map.calculateTopLeftX()) / cellSize);
        int row = (int) ((scenePoint.getY() - map.calculateTopLeftY()) / cellSize);
        Cell cell = null;

        try {
            cell = map.getCell(row, col);

            if (!cell.isPath() && !cell.hasTower()) {
                String type = getTowerType(draggingImage.getImage());
                
                
                // Başlangıç değerleri (Boş kalmasın diye)
                double range = 110, cost = 50, cooldown = 1.0, cd = 1.0;

                // UIPanel'de yazdığınız fiyatlara ve özelliklere göre kuleleri ayırıyoruz
                switch (type) {
                    case "CANNON":
                        cost = 50; cd = 1.0; cooldown = 1.0; 
                        break;
                    case "LASER":
                        cost = 120; cd = 0.1; cooldown = 0.1; 
                        break;
                    case "TRIPLE":
                        cost = 150; cd = 1.5; cooldown = 1.5; 
                        break;
                    case "MISSILE":
                        cost = 200; cd = 2.0; cooldown = 2.0; 
                        break;
                }

            

                // 1. ÖNCE PARAYI KONTROL ET!
                if (game.getMoney() >= cost) {
                    
                    double absoluteX = map.calculateTopLeftX() + (col * cellSize) + (cellSize / 2.0);
                    double absoluteY = map.calculateTopLeftY() + (row * cellSize) + (cellSize / 2.0);

                 // TowerFactory.createTowerByType satırındaki son parametreyi 'towerLayer' yap
                    Tower tower = TowerFactory.createTowerByType(type, absoluteX, absoluteY, range, cost, cooldown, cd, enemies, game, towerLayer);
                    if (tower != null) {
                        // 2. PARAYI KES!
                        game.spendMoney((int)cost);
                        System.out.println("Kule satın alındı! Kalan para: " + game.getMoney());

                        towerLayer.getChildren().add(tower.getView());
                        cell.setTower(tower);
                        game.addTower(tower);
                        
                        // Kuleyi taşınabilir yap
                        makeTowerDraggable(tower, cell); 
                    }
                } else {
                    // PARASI YETMİYORSA (İstersen buraya UI'da kırmızı bir uyarı da ekleyebilirsin)
                    System.out.println("YETERSİZ BAKİYE! Bu kule için " + cost + "$ gerekiyor.");
                }
            }
            }

         catch (ArrayIndexOutOfBoundsException a) {//Burada normalde bu hatayı verdiği zaman catchleyip kulenin satılma işlemini yapmak lazım yani parayı arttıracağız
            System.out.println("Kule harita dışına bırakıldı.");
        } finally {//işlem bittikten sonra rangei görünmez yap 
            rangeCircle.setOpacity(0);
            towerLayer.getChildren().remove(rangeCircle);
            towerLayer.getChildren().remove(draggingImage);//? kaldırmak mermi sıkma işlemini etkiler mi yoksa ayrıca bir range oluşturuluyor mu bak !

            if (cell != null) {
            	//hataları denemek için koyuldu 
                System.out.println("scene: " + scenePoint);
                System.out.println("mapTopLeft: " + map.calculateTopLeftX() + ", " + map.calculateTopLeftY());
                System.out.println("row: " + row + ", col: " + col);
                System.out.println("isPath: " + cell.isPath() + ", hasTower: " + cell.hasTower());
                System.out.println("cell instance: " + cell);
                System.out.println("map.getCell again: " + map.getCell(row, col));
            }
        }
        
        releaseEvent.consume();
    }
    
    private String getTowerType(Image image) {//sürüklenen imageı parametre olarak alır ve imageın urlsini eşleyerek hangi kule türü olduğunu tespit eder ve type olarak kaydeder
        String url = image.getUrl();
        if (url.contains("cannon.png")) return "CANNON";
        if (url.contains("infernoTower.png")) return "LASER";
        if (url.contains("tripleCannon.png")) return "TRIPLE";
        if (url.contains("havan.png")) return "MISSILE";
        return "UNKNOWN";
      //handleMouseReleased methodunun içinde hesapladığım cellin centerX ve centerY değerlerini kullanarak mapin sol üst köşesinden centerX e kadar ilerleyip yerleştirdiğimiz (drop işlemini yaptığımız) cellin neresine koyarsak koyalım ikonu merkeze yerleştirmek
    }

    private void placeIconAtCenter(Node icon, double centerX, double centerY) {
        icon.setLayoutX(map.calculateTopLeftX() + centerX - ((ImageView)icon).getFitWidth() / 2);
        icon.setLayoutY(map.calculateTopLeftY() + centerY - ((ImageView)icon).getFitHeight() / 2);
    }
 // Mevcut kuleleri taşımak ve satmak için yazdığımız efsane metot
    private void makeTowerDraggable(Tower tower, Cell originalCell) {
        javafx.scene.Node view = tower.getView();
        final Cell[] currentCell = {originalCell}; // Kule yer değiştirdikçe bu da güncellenecek

        view.setOnMousePressed(e -> {
            view.setOpacity(0.5); // Sürüklerken kuleyi yarı saydam yap (hayalet efekti)
            
            // Menzil halkasını gösterelim
            rangeCircle = new Circle(70); // Standart menzili 70 alıyoruz
            rangeCircle.setFill(Color.rgb(0, 255, 0, 0.2));
            rangeCircle.setStroke(Color.GREEN);
            rangeCircle.setMouseTransparent(true);
            rangeCircle.setCenterX(tower.getX());
            rangeCircle.setCenterY(tower.getY());
            towerLayer.getChildren().add(rangeCircle);

            offset = new Point2D(e.getX(), e.getY()); // Tıklanan yeri kaydet
            e.consume();
        });

        view.setOnMouseDragged(e -> {
            // Kuleyi farenin olduğu yere taşı
            Point2D scenePoint = new Point2D(e.getSceneX(), e.getSceneY());
            Point2D localPoint = towerLayer.sceneToLocal(scenePoint);

            view.setLayoutX(localPoint.getX() - offset.getX());
            view.setLayoutY(localPoint.getY() - offset.getY());

            // Menzil halkasını da onunla birlikte kaydır (+20 kulenin tam merkezi için)
            rangeCircle.setCenterX(view.getLayoutX() + 20);
            rangeCircle.setCenterY(view.getLayoutY() + 20);
            e.consume();
        });

        view.setOnMouseReleased(e -> {
            view.setOpacity(1.0); // Şeffaflığı düzelt
            towerLayer.getChildren().remove(rangeCircle); // Halkayı sil

            // Bırakılan yeni yeri hesapla
            Point2D scenePoint = new Point2D(e.getSceneX(), e.getSceneY());
            int cellSize = 40;
            int col = (int) ((scenePoint.getX() - map.calculateTopLeftX()) / cellSize);
            int row = (int) ((scenePoint.getY() - map.calculateTopLeftY()) / cellSize);

            try {
                Cell targetCell = map.getCell(row, col);

                // Kendi olduğu yere mi bırakıldı? Hiçbir şey yapma
                if (targetCell == currentCell[0]) {
                    snapBack(tower);
                } 
                // Geçerli ve boş bir yere bırakıldıysa TAŞI
                else if (!targetCell.isPath() && !targetCell.hasTower()) {
                    currentCell[0].setTower(null); // Eski yeri boşalt
                    
                    // Yeni koordinatları hesapla
                    double absoluteX = map.calculateTopLeftX() + (col * cellSize) + (cellSize / 2.0);
                    double absoluteY = map.calculateTopLeftY() + (row * cellSize) + (cellSize / 2.0);

                    // Kulenin beynini ve görselini yeni yerine geçir
                    tower.setX(absoluteX);
                    tower.setY(absoluteY);
                    view.setLayoutX(absoluteX - 20);
                    view.setLayoutY(absoluteY - 20);

                    // Yeni hücreyi doldur
                    targetCell.setTower(tower);
                    currentCell[0] = targetCell;
                } 
                // Geçersiz bir yere (Yola veya başka kulenin üstüne) bırakıldıysa GERİ ZIPLA
                else {
                    snapBack(tower);
                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                // EĞER HARİTANIN DIŞINA ATILDIYSA -> KULEYİ SAT!
                currentCell[0].setTower(null); 
                towerLayer.getChildren().remove(view); 
                game.removeTower(tower); 
                
                // Kulenin kendi fiyatının %50'sini iade et!
                int refund = (int) (tower.getCost() / 2);
                game.addMoney(refund); 
                System.out.println( " kulesi satıldı! +" + refund + "$");
            }
            e.consume();
        });
    }

    // Geçersiz hamlelerde kuleyi eski orijinal yerine geri oturtan yardımcı metot
    private void snapBack(Tower tower) {
        tower.getView().setLayoutX(tower.getX() - 20);
        tower.getView().setLayoutY(tower.getY() - 20);
    }
}
    









