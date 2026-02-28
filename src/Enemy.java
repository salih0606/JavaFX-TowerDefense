//Ahmet Salih Demir
import javafx.scene.shape.*;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;


import java.util.*;
public class Enemy {
	private double health = 100;              //Düşmanlara atadığımız can değeri.
	private double speed;                     //Düşmanların hareet hızı.
	private int CPI;                          //CurrentPathIndex - düşmanların hedeflediği path hücresi.
	private double x, y;                      //Düşmnaların ekran üzerindeki konumunu temsil eden birimler.
	private Group view;                       //Düşmanların toplam görünümünü temsil eden group.
	private ImageView enemyView;              //Düşmanın health barsız görünümü.
	private List<Cell> path;                  //Düşmanlara ilerleyecekleri yolu gösterecek path.(Grid üzerinde Cell listesi)
	private Rectangle healthBar;              //Can barımızın hasar aldıkça azalacak olan iç kısmı.
	private Rectangle healthBarFrame;         //Can barımızın çerçevesi,
	private boolean healthBarVisible = false;//Can barının hasar alana kadar görünmemesi için atanan değer.
	
	public Enemy(double health, double speed, double x, double y) {
		this.health = health;
		this.speed = speed;
		this.CPI = 1;
		
		//Can barı ve çerçevesini oluşturuyoruz.
		healthBarFrame = new Rectangle(30, 5);
		healthBarFrame.setFill(Color.TRANSPARENT);
		healthBarFrame.setStroke(Color.BLACK);
		healthBarFrame.setStrokeWidth(1);
		
		healthBar = new Rectangle(30, 5, Color.LIMEGREEN);
		
		//Can barını oluşturmak için çerçeve ve içini birleştiriyoruz.
		Group healthBarGroup = new Group(healthBar,healthBarFrame);
		healthBarGroup.setVisible(true);//Grünürlüğü burada da hasar alana kadar false olması için ayarlıyoruz.
		
		//Can barı bulunmayan düşman görselini oluşturuyoruz.
		enemyView = new ImageView(getClass().getResource("/enemy.png").toExternalForm());
		enemyView.setFitWidth(130);
		enemyView.setFitHeight(130);
		
		//Can barı ve düşman görselinin birleştiği nihai düşman görünümünü bir grup içerisinde oluşturuyoruz.
		Group group = new Group(enemyView, healthBarGroup);
		//can barını düşman üzerinde hizalıyoruz.
		healthBarGroup.setLayoutX(+50);
		healthBarGroup.setLayoutY(+50);
		
		//Görümüm view değişkenine atadık.
		this.view = group;
		
		//Düşman için başlangıç pozisyonunu atıyoruz.
		//Düşmanlar için başlangç pozisyonumuzda büyük sıkıntılar yaşamıştık ilk olarak setTranslate(x/y) ile başadık ancak sonradan büyük değişikler yaşandı.
		//Bu başlangıç konumu kısmı özellikle düşmanları hareket ettirdiğimiz move methodumuz için büyük önem arzediyor.
	    this.x = x;
	    this.y = y;
	    view.setLayoutX(x);
		view.setLayoutY(y);
	}
	//Düşmanların üzerinde hareket edeceği pathi burada atıyoruz.
	public void setPath (List<Cell> path) {
		this.path = path;
	}
	//Genel düşman hareketi kontrol eden mehodumuz.
	public void move(List<Cell> path) {
		if(CPI >= path.size()) return;//Path bittiğinde düşman hareketleri durması için alınan return.
		
		Cell target = path.get(CPI);
		
		//Hücrelerin sahne üzerindeki konumlarını alan kodlar.
		//Bu kısım düşmnaların merkeze yerleştirdiğimiz mapin  doğru konumunu bulmasına yardımcı oluyor.
		//Kodu ilk yazdığımızda düşmanlar mapin oluşturuduğu ekranın sol üstünde spawn oluyrlar ve orada path şeklinde hareket ediyorlardı.
		//localToScene ve sceneToLocal ile doğru yolda spawn ve hareketi sağladık.
		Point2D targetPos = target.getView().localToScene(0,0);
		Point2D localTargetPos = view.getParent().sceneToLocal(targetPos);
		
		//Bu kısım ise düşmanların cel hücrelerinin merkezleri arasında hareket etmesini sağlıyor.
		double targetX = localTargetPos.getX() + 20;
		double targetY = localTargetPos.getY() + 20;
		
		//Hedef hücre ile anlık x y konumu farkını alıyoruz.
		double dx = targetX - x;
		double dy = targetY - y;
		//Bu kısımda da hücreler arası mesafe hesaplanıyor,
		double distance = Math.sqrt(dx * dx + dy * dy);
		
		if(distance < speed) {
			//Eğer düşman hedef hücreye ulaştıysa sonraki hedefi ayarlıyoruz.
			x = targetX;
			y = targetY;
			CPI++;
		}else {
			//Burada ise düşmanların x / y değerlerini hedef hücreye göre değiştirerek onları hareket ettiriyoruz.
			x += speed * dx / distance;
			y += speed * dy / distance;
		}
		 //Eklediğmiz düşman görselini ortalamk için hücrelerin yarı boyu kadar pikseli görüntü konumunda çıkarıyoruz.
		view.setLayoutX(x-65);
		view.setLayoutY(y-80);
		
	}
	//Düşmanların hasar almasını ve canlarındaki azalmayı atayan method.
	public void takeDamage(double amount) {
		health-= amount;
		//Kodun bu kısmında can barını hasar aldıktan sonra görünür kılmayı amaçlayan if statementi ekliyoruz.
		if(!healthBarVisible) {
			healthBar.setVisible(true);
			healthBarFrame.setVisible(true);
			healthBarVisible = true;
		}
		//Can barının alınan hasara göre uzunluğunu güncelleyen kod.
		double ratio = Math.max(0, health/100);
		healthBar.setWidth(30 * ratio);
	}
	//Canın 0 veya altına düşmasiyle düşmanın ölüp ölmediğini kontrol eden methodumuz.
	public boolean isDead() {
		return health <=0;
	}
	//Diğer işlemler için düşmanın path sonuna ulaşıp ulaşmadığını kontrol eden methodumuz.
	public boolean reachedEnd() {
		return CPI >= path.size();
	}
	//Düşmanların x konumunu döndüren method.
	public double getX() {//doğukan
		return x;
	}
	//Düşmanların y konumunu döndüren methodumuz.
	public double getY() {//doğukan
		return y;
	}
	//Düşmanların genel görüntüsünü döndüren method.
	public Group getView() {
		return view;
	}
	
	
}
