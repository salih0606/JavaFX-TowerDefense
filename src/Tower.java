import java.util.List;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;

public abstract class Tower {
    protected double range, cost, towerX, towerY, cooldown, cd;
    protected ImageView view;
    protected Game game;
    protected Pane gamePane;
    protected List<Enemy> enemies;

    public Tower(String imageName, double towerX, double towerY, double range, double cost, double cooldown, double cd, List<Enemy> enemies) {
        this.towerX = towerX;
        this.towerY = towerY;
        this.range = range;
        this.cost = cost;
        this.cooldown = cooldown;
        this.cd = cd;
        // DİKKAT: Artık enemies listesini doğrudan burada atıyoruz!
        this.enemies = enemies; 

        try {
            String path = getClass().getResource("/" + imageName).toExternalForm();
            this.view = new ImageView(new Image(path));
        } catch (Exception e) {
            System.err.println("Görsel yüklenemedi: " + imageName);
            this.view = new ImageView(); 
        }
        this.view.setFitWidth(45);  
        this.view.setFitHeight(45); 
        // Görselin tam ortasını, kulenin X ve Y'sine hizalıyoruz.
        this.view.setLayoutX(towerX - 20); 
        this.view.setLayoutY(towerY - 20); 
    }

    public abstract Enemy findTarget();
    public abstract void shoot(Enemy enemy); // Parametre adını tekil yaptık
    public abstract void update(double deltaTime);

    public double getX() { return towerX; }
    public double getY() { return towerY; }
    public void setX(double x) { towerX = x; }
    public void setY(double y) { towerY = y; }
    
    public double getCost() {
        return cost;
    }
    
    public Node getView() {
        return view;
    }
}