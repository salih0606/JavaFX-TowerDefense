import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class LaserBullet {
    private Line laser;

    public LaserBullet(double startX, double startY, double damage, double speed, Pane layer, Enemy target) {
        // Lazer anında vurur, hedefe ulaşılamıyorsa veya öldüyse hiç çizme
        if (target.isDead()) return;

        // Başlangıç noktası kule, bitiş noktası düşman
        laser = new Line(startX, startY, target.getX(), target.getY());
        laser.setStroke(Color.RED);
        laser.setStrokeWidth(3);
        
        layer.getChildren().add(laser);

        // Lazer yavaş gitmez, anında hasar verir!
        target.takeDamage(damage);

        // 100 milisaniye (0.1 saniye) ekranda kalıp görsel efekt yaratsın, sonra silinsin
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(e -> layer.getChildren().remove(laser));
        pause.play();
    }
}