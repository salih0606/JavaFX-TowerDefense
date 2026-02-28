import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.geometry.Point2D;

public class Bullet extends Circle {
    private AnimationTimer timer;
    private double damage, speed;
    private Pane layer; // Mermilerin çizileceği katman (towerLayer olacak)
    private Enemy enemy;

    public Bullet(double startX, double startY, double damage, double speed, Pane layer, Enemy enemy) {
        super(startX, startY, 5); // Merminin büyüklüğü (yarıçapı)
        this.setFill(Color.BLACK);
        this.damage = damage;
        
        // Eğer hız çok düşük gelirse otomatik hızlandırıyoruz ki havada asılı kalmasın :)
        this.speed = (speed < 10) ? 300 : speed; 
        
        this.layer = layer;
        this.enemy = enemy;
        
        this.layer.getChildren().add(this);
        startMovement();
    }

    private void startMovement() {
        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                // Gerçek geçen süreyi (delta time) hesaplıyoruz
                double dt = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                move(dt);
            }
        };
        timer.start();
    }

    public void move(double dt) {
        // Hedef öldüyse veya yoldan çıkıp kaybolduysa mermiyi imha et
        if (enemy.isDead() || enemy.reachedEnd()) {
            destroy();
            return;
        }
        
        Point2D enemyScenePos = enemy.getView().localToScene(65, 65); // Düşmanın merkezi
        Point2D enemyLocalPos = layer.sceneToLocal(enemyScenePos);
        // Düşmanın ANLIK merkezini al (Hedef takibi - Homing)
        double targetX = enemy.getX();
        double targetY = enemy.getY();
        
        double dx = targetX - getCenterX();
        double dy = targetY - getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Çarpışma kontrolü (Mesafe yeterince kısaysa vurmuş sayılır)
        if (distance < speed * dt + 10) { 
            enemy.takeDamage(damage);
            destroy();
        } else {
            // Hedefe doğru uçuş matematiksel hesaplaması
            setCenterX(getCenterX() + (dx / distance) * speed * dt);
            setCenterY(getCenterY() + (dy / distance) * speed * dt);
        }
    }
    

    private void destroy() {
        timer.stop();
        layer.getChildren().remove(this);
    }
}