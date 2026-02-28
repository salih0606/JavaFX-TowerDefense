import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class LaserTower extends Tower {
    
    // Hangi düşmana hangi lazer çizgisinin bağlı olduğunu tutan liste (Harika bir OOP pratiğidir)
    private Map<Enemy, Line> activeLasers; 
    private double damagePerSecond = 30.0; // Çoklu vurduğu için saniyelik hasarı biraz kıstım, istersen artırabilirsin :)

    public LaserTower(String imageName, double towerX, double towerY, double range, double cost, double cooldown, double cd, List<Enemy> enemies, Pane gamePane) {
        super(imageName, towerX, towerY, range, cost, cooldown, cd, enemies);
        this.gamePane = gamePane;
        this.activeLasers = new HashMap<>(); // HashMap'imizi başlatıyoruz
    }

    @Override
    public Enemy findTarget() {
        // Artık tek bir hedef bulup dönmüyoruz, update içinde menzildeki HERKESİ tarayacağız.
        // O yüzden burayı boş (null) döndürüyoruz, eski sisteme ihtiyacımız kalmadı.
        return null; 
    }

    @Override
    public void update(double dt) {
        // 1. Bütün düşman listesini tara
        for (Enemy e : enemies) {
            
            // Eğer düşman ölüyse veya haritadan çıktıysa, ve bizde lazeri varsa kopar
            if (e.isDead() || e.reachedEnd()) {
                removeLaser(e);
                continue;
            }

            // Düşmanla aramızdaki mesafeyi ölç
            double dx = e.getX() - getX();
            double dy = e.getY() - getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            // Düşman menzilin içindeyse!
            if (dist <= range) {
                
                // Eğer bu düşmana henüz lazer bağlamadıysak, yeni bir lazer yarat
                if (!activeLasers.containsKey(e)) {
                    Line newLaser = new Line(getX(), getY(), e.getX(), e.getY());
                    newLaser.setStroke(Color.RED);
                    newLaser.setStrokeWidth(3); // Çoklu lazer olacağı için biraz incelttim
                    gamePane.getChildren().add(newLaser); // Ekrana ekle
                    activeLasers.put(e, newLaser); // Hafızaya kaydet (Bu düşman = Bu çizgi)
                } 
                // Eğer zaten lazer bağlıysa, sadece ucunu düşmanın yeni konumuna güncelle
                else {
                    Line existingLaser = activeLasers.get(e);
                    existingLaser.setEndX(e.getX());
                    existingLaser.setEndY(e.getY());
                }
                
                // Ve sürekli hasarı ver
                e.takeDamage(damagePerSecond * dt);
                
            } 
            // Düşman menzilden çıktıysa ama lazeri hala takılıysa, lazeri kopar
            else {
                removeLaser(e);
            }
        }
        
        // 2. Güvenlik Temizliği: Eğer düşman listesi bir şekilde sıfırlandıysa (wave bittiyse vb.)
        // havada asılı kalan lazer çizgisi kalmasın diye kontrol ediyoruz.
        Iterator<Map.Entry<Enemy, Line>> it = activeLasers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Enemy, Line> entry = it.next();
            if (!enemies.contains(entry.getKey())) {
                gamePane.getChildren().remove(entry.getValue());
                it.remove();
            }
        }
    }

    // Lazer çizgisini ekrandan ve hafızadan silen yardımcı metot
    private void removeLaser(Enemy e) {
        if (activeLasers.containsKey(e)) {
            gamePane.getChildren().remove(activeLasers.get(e));
            activeLasers.remove(e);
        }
    }

    @Override
    public void shoot(Enemy target) {
        // Lazer kulesi klasik mermi fırlatmadığı için boş bırakıyoruz.
    }
}