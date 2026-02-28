import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Random;

public class DeathParticleEffect {
    
    private static final Random random = new Random(); // Rastgele yönler için

    public DeathParticleEffect(double startX, double startY, Pane layer) {
        
        // Düşman başına kaç tane parçacık fırlasın? (10-15 arası iyidir)
        int particleCount = 10 + random.nextInt(6); 

        for (int i = 0; i < particleCount; i++) {
            createParticle(startX, startY, layer);
        }
    }

    private void createParticle(double x, double y, Pane layer) {
        // 1. Parçacığı oluştur (Ufak kırmızı daire)
        // Yarıçapı rastgele 2 ile 4 piksel arası olsun (çeşitlilik iyidir)
        double radius = 2 + random.nextDouble() * 2; 
        Circle particle = new Circle(x, y, radius);
        
        // Rengi kırmızı yap (İstersen turuncu-kırmızı arası rastgele tonlar da yapabiliriz)
        particle.setFill(Color.RED); 
        
        // Ekrana (enemyLayer) ekle
        layer.getChildren().add(particle);

        // 2. Rastgele hareket hesapla (Fizik motoru gibi)
        // 360 derece rastgele bir açı seç
        double angle = random.nextDouble() * 2 * Math.PI;
        // Fırlama hızını/mesafesini rastgele belirle (50-100 piksel arası)
        double distance = 50 + random.nextDouble() * 50; 

        // Açıyı X ve Y eksenindeki hareket miktarına (vektöre) çevir
        double targetX = Math.cos(angle) * distance;
        double targetY = Math.sin(angle) * distance;

        // 3. HAREKET ANİMASYONU (Translate Transition)
        // Parçacık merkezden dışarıya doğru fırlasın
        TranslateTransition tt = new TranslateTransition(Duration.millis(600), particle);
        tt.setByX(targetX);
        tt.setByY(targetY);
        // Hareketi baştan sona yavaşlat (Hızlı başlar, yavaşlar - fırlama hissi)
        tt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        // 4. SOLMA ANİMASYONU (Fade Transition)
        // Parçacık fırlarken hızlıca görünmez olsun (solma)
        FadeTransition ft = new FadeTransition(Duration.millis(600), particle);
        ft.setFromValue(1.0); // Tam görünür
        ft.setToValue(0.0);   // Tam görünmez

        // 5. ANİMASYONLARI BİRLEŞTİR VE ÇALIŞTIR (Parallel Transition)
        // Hem hareket etsin hem solsun
        ParallelTransition pat = new ParallelTransition(particle, tt, ft);
        
        // 6. KENDİNİ TEMİZLEME
        // Animasyon bittiğinde parçacığı ekrandan (Pane) sil ki oyun kasmasın
        pat.setOnFinished(e -> layer.getChildren().remove(particle));

        // Parçacığı ateşle!
        pat.play();
    }
}