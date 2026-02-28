//burak berk demirbaş
import java.util.List;

import javafx.scene.layout.Pane;

public class TowerFactory {
    public static Tower createTowerByType(String type, double x, double y, double range, double cost, double cooldown, double cd, List<Enemy> enemies,Game game,Pane gamePane) {
        switch (type) {
            case "CANNON":
                return new SingleShotTower("cannon.png", x, y, range, cost, cooldown, cd, enemies,gamePane, game);
            case "LASER":
                return new LaserTower("infernoTower.png", x, y, range, cost, cooldown, cd, enemies,gamePane);
            case "TRIPLE":
                return new TripleShotTower("tripleCannon.png", x, y, range, cost, cooldown, cd, enemies,gamePane);
            case "MISSILE":
                return new MissileLauncherTower("havan.png", x, y, range, cost, cooldown, cd, enemies,gamePane);
            default:
                return null;
        }
    }
}