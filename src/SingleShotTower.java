import java.util.List;
import javafx.scene.layout.Pane;

public class SingleShotTower extends Tower {
    
    public SingleShotTower(String imageName, double towerX, double towerY, double range, double cost, double cooldown, double cd, List<Enemy> enemies, Pane gamePane, Game game) {
        // ARTIK NULL DEĞİL, enemies listesini gönderiyoruz!
        super(imageName, towerX, towerY, range, cost, cooldown, cd, enemies); 
        this.gamePane = gamePane;
        this.game = game;
    }

    @Override
    public Enemy findTarget() {
        for (Enemy e : enemies) {
            if (!e.isDead()) {
                // Konum hesaplaması tam merkeze göre
                double dx = e.getX() - getX();
                double dy = e.getY() - getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist <= range) {
                    return e; // Menzildeki ilk düşmanı bul ve DÖN!
                }
            }
        }
        return null; // Kimseyi bulamazsan null dön.
    }

    @Override
    public void update(double dt) {
        cooldown -= dt;
        Enemy target = findTarget();

        if (target != null && cooldown <= 0) {
            shoot(target);
            cooldown = cd;
        }
    }

    @Override
    public void shoot(Enemy enemy) {
        // Bullet sınıfını birazdan düzelteceğiz, şimdilik böyle kalsın.
        Bullet bullet = new Bullet(getX(), getY(), 34, 3, gamePane, enemy);
        System.out.println("Ateş edildi! Konum: " + getX() + ", " + getY());
    }
}