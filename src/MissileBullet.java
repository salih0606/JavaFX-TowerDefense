import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.List;

public class MissileBullet extends Circle {
    private AnimationTimer timer;
    private double damage, speed, explosionRad;
    private Pane layer;
    private Enemy target;           // Füzenin kilitlendiği asıl hedef
    private List<Enemy> allEnemies; // Patlayınca etraftakileri bulmak için tüm liste

    public MissileBullet(double startX, double startY, double damage, double speed, double explosionRad, Pane layer, Enemy target, List<Enemy> allEnemies) {
        super(startX, startY, 8); // Füze normal mermiden biraz daha büyük olsun
        this.setFill(Color.DARKORANGE); // Rengi de turuncu olsun ki fark edilsin
        this.damage = damage;
        this.speed = (speed < 10) ? 200 : speed; 
        this.explosionRad = explosionRad;
        this.layer = layer;
        this.target = target;
        this.allEnemies = allEnemies;
        
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
                double dt = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;
                move(dt);
            }
        };
        timer.start();
    }

    public void move(double dt) {
        // Eğer hedef biz ona ulaşamadan öldüyse veya haritadan çıktıysa, füzeyi boşa harcama, olduğu yerde patlat!
        if (target.isDead() || target.reachedEnd()) {
            explode();
            destroy();
            return;
        }

        double dx = target.getX() - getCenterX();
        double dy = target.getY() - getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Çarpışma anı
        if (distance < speed * dt + 10) {
            explode(); // Hedefe çarptı, patla!
            destroy();
        } else {
            setCenterX(getCenterX() + (dx / distance) * speed * dt);
            setCenterY(getCenterY() + (dy / distance) * speed * dt);
        }
    }

    private void explode() {
        // Etki alanındaki (explosionRad) tüm düşmanları tara ve hasar ver
        for (Enemy e : allEnemies) {
            if (e.isDead()) continue;
            
            double dx = e.getX() - getCenterX();
            double dy = e.getY() - getCenterY();
            // Düşmanın patlama merkezine (füzeye) olan uzaklığı
            double dist = Math.sqrt(dx * dx + dy * dy);

            // Eğer düşman patlama çapının içindeyse hasarı yer
            if (dist <= explosionRad) {
                e.takeDamage(damage);
            }
        }
    }

    private void destroy() {
        timer.stop();
        layer.getChildren().remove(this);
    }
}