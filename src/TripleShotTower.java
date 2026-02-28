import java.util.List;
import javafx.scene.layout.Pane;

public class TripleShotTower extends Tower {
    
    public TripleShotTower(String imageName, double towerX, double towerY, double range, double cost, double cooldown, double cd, List<Enemy> enemies, Pane gamePane) {
        super(imageName, towerX, towerY, range, cost, cooldown, cd, enemies);
        this.gamePane = gamePane;
    }

    @Override
    public Enemy findTarget() {
        // Tek bir hedef bulmuyoruz, update içinde 3 kişiyi birden tarayacağız!
        return null; 
    }

    @Override
    public void update(double dt) {
        cooldown -= dt;

        // Ateş etme süresi geldiyse
        if (cooldown <= 0) {
            int shotCount = 0; // Kaç kişiye ateş ettiğimizi sayacağız
            
            // Menzildeki bütün düşmanları tara
            for (Enemy e : enemies) {
                if (!e.isDead() && !e.reachedEnd()) {
                    double dx = e.getX() - getX();
                    double dy = e.getY() - getY();
                    double dist = Math.sqrt(dx * dx + dy * dy);

                    // Eğer düşman menzildeyse ona bir mermi yolla
                    if (dist <= range) {
                        shoot(e); 
                        shotCount++; // Ateş edilen kişi sayısını artır
                        
                        // 3 farklı düşmana mermi yolladıysak dur (Kulemiz 3'lü atış yapıyor sonuçta)
                        if (shotCount == 3) {
                            break; 
                        }
                    }
                }
            }
            
            // Eğer en az 1 kişiye bile ateş ettiysek, bekleme süresini başa sar
            if (shotCount > 0) {
                cooldown = cd;
            }
        }
    }

    @Override
    public void shoot(Enemy target) {
        // Doğrudan hedefteki farklı düşmanlara normal mermi fırlatıyoruz! (TripleBullet sınıfına gerek kalmadı)
        // Hızı 600, hasarı 15 olarak ayarladım, oyunun dengesine göre değiştirebilirsin.
        new Bullet(getX(), getY(), 15, 600, gamePane, target);
    }
}