import javafx.scene.layout.Pane;

public class TripleBullet {
    
    // Bu sınıf aslında kendi başına bir cisim değil, 3 tane Bullet üreten bir "Yönetici"
    public TripleBullet(double startX, double startY, double damage, double speed, Pane layer, Enemy target) {
        if (target.isDead()) return;

        // Görsel olarak 3 farklı mermi gibi görünmesi için koordinatları biraz (15 piksel) kaydırıyoruz
        new Bullet(startX - 15, startY, damage, speed, layer, target); // Sol mermi
        new Bullet(startX + 15, startY, damage, speed, layer, target); // Sağ mermi
        new Bullet(startX, startY - 15, damage, speed, layer, target); // Üst mermi
    }
}