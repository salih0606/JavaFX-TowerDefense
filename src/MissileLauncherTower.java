import java.util.List;
import javafx.scene.layout.Pane;

public class MissileLauncherTower extends Tower {
    
    public MissileLauncherTower(String imageName, double towerX, double towerY, double range, double cost, double cooldown, double cd, List<Enemy> enemies, Pane gamePane) {
        super(imageName, towerX, towerY, range, cost, cooldown, cd, enemies);
        this.gamePane = gamePane;
    }

    @Override
    public Enemy findTarget() {
        for (Enemy e : enemies) {
            if (!e.isDead()) {
                double dx = e.getX() - getX();
                double dy = e.getY() - getY();
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist <= range) {
                    return e;
                }
            }
        }
        return null;
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
    public void shoot(Enemy target) {
        // Parametreler sırasıyla: X, Y, Hasar, Hız, Patlama Yarıçapı (100 piksel iyi bir alan), Layer, Asıl Hedef, Tüm Düşmanlar
        MissileBullet bullet = new MissileBullet(getX(), getY(), 20, 400, 100, gamePane, target, this.enemies);
    }
}