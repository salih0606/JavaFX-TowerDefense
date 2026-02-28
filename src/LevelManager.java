//Burak Berk Demirbaş
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.*;

public class LevelManager {

	public UIPanel ui;
	public Main main; //bu class salihin yapacağı main class olmalı
	private WaveManager waveManager;
	private Game game;
	
	private boolean gameEnded = false;
	
	private int currentLevel;
	private int lives = 5;
	private int money = 1000;
	private int currentWave = 0;
	private int totalWaveInCurrentLevel;
	
	//LevelManager classı içinden UIPanele erişmek için bu methodu kullanıyorum
	public void bindUI(UIPanel UI) {
		this.ui=UI;
	}
	
    //Level manager classı içinden Main classa erişmek için bu methodu kullanıyorum
	public void bindMain(Main main) {
		this.main=main;
	}
	
	//Level manager classı içinden Game classına erişmek için bu methodu kullanıyorum
	public void bindGame(Game game) {
		this.game = game;
	}
	
	 private Timeline waveCountdownTimer;
	

	 public void startLevel(int level) { //Bu methodu game classındaki constructer çağırıyor o anki level değerini parametre olarak alarak
		
		this.gameEnded = false;
		this.currentLevel=level;
		this.lives=5;
		this.currentWave=0;
		this.totalWaveInCurrentLevel= (level<3) ? level+2 : 5; //hangi levelda kaç adet wave var bunu belirliyorum
		
		
		this.waveManager = new WaveManager("level"+level+".txt"); //WaveManager constructerını değişken dosya adları ile kullanıyorum.
		//WaveManager constructerına verdiğim String fileName ile o leveldeki text dosyalarındaki enemy sayısını, enemyler arası süreyi, ve wave öncesi delayı bir arrayda topluyoruz
		
		ui.updateLives(lives); //Canı 5 olarak göstermek için 
		ui.updateMoney(money); //Parayı 100 olarak göstermek için
		
		//ilk wavei başlatıyorum
		startNextWave();
	}
 
	public void startNextWave() { //Bir sonraki wavein başlangıcı hazırlanır eğer wtüm waveler bittiyse levelComplete methodunu çağırarak içindeki level parametresini 1 arttırıyor ve sonraki levele geçiyoruz
		if(gameEnded) return; 
		
		if(currentWave>=waveManager.getTotalWaves()) { //Bu statementta o leveldeki tüm waveler bittiyse leveli tamamlıyoruz
			//main.levelComplete(currentLevel+1);
			return;
		} 
		
		WaveData wave = waveManager.getWave(currentWave); //burada textteki o anki wavein bilgilerini çekiyoruz
		int delay = wave.delayBetweenWave;
		ui.updateTime(delay); //sonraki wavein kaç saniye sonra geleceği ekranda gözükür
		
		//Bu bilgilere göre bir geri sayım başlıyor ve geri sayım sonunda startWave methodu çağırılıyor geri sayım süresini WaveData classından alıyoruz
		waveTimer(delay,()->{
			if(!gameEnded)startWave(wave);
		});
	}
	
	public void startWave(WaveData wave) {
		game.spawnEnemies(wave);//game.spawnEnemies gibi bir kod ile düşmanları spawn ettireceğiz Game classındaki wpawnEnemies methodunu çalıştırıyorum
		currentWave++; 
		startNextWave();
	}
	
	private void  waveTimer(int delay, Runnable onFinished) { //Runnable onFinished geri sayım bittiğinde ne yapacağını gösterir. startNextWave methodunun içinde bir geri sayım var ve bu bittikten sonra Runnable onFinished startWave methodunu çalıştırır
		if(waveCountdownTimer != null) {
			waveCountdownTimer.stop();
		}
		
		final int[] remaining = {delay}; //Burada normalde direkt int kullanmıştım fakat lambda ifadelerinde bunun gibi primitive değişkenler değiştirilemiyormuş
		
		//normalde remaining[0]--; yerine direkt delay yazıp-- yapacaktım fakat lambda ifadeleri içinde değiştirilemiyor değeri
		waveCountdownTimer = new Timeline(new KeyFrame(Duration.seconds(1),e->{ //Her 1 saniyede bir çalışan yeni bir TimeLine oluşturdum
			remaining[0]--; 
			ui.updateTime(remaining[0]); //UIPanel classındaki updateTime methodunu çağırarak süre sayacını UI üzerinden güncelliyorum
			if(remaining[0]<=0) { //Zamanlayıcı 0 a indiğinde -1 -2 şeklinde daha geriye gitmemesi için durduruyorum
				waveCountdownTimer.stop();
				onFinished.run();
			}
		})
	);
		waveCountdownTimer.setCycleCount(delay);
		waveCountdownTimer.play();
	}
	
	
	/* Bu methodu Enemy classının içinden her framede düşmanların pozisyonunu kontrol ederek kullanacğız eğer
	 * bir düşman çıkışa (Son cellin ortasına ulaştı ise levelManager.enemyEscaped(); ve bu methodun içinde canımızı azaltacağız, UI güncellenecek UIPanel classındaki methodu çağırarak ve can 0 a düşerse oyunu bitireceğiz
	 */
	
	public void enemyEscaped() {
		if(gameEnded) return;
		
		this.lives--;
		ui.updateLives(lives);
		
		if(lives<=0) {
			gameEnded = true;
			if(waveCountdownTimer != null) waveCountdownTimer.stop();
			main.gameOver(); //Burası salihin main classda yapacağı method olacak
			
		}
	}
	
	// Bu methodu da Enemy classının içinden düşman canı 0 a düşerse çağıracağız.
	//bu method ile de düşman ölünce parayı arttırıyor ve UIPanel classındaki updateMoney methodunu kullanarak parayı güncelliyoruz.
	public void enemyKilled() {
		if(gameEnded) return;
		
		this.money+=10;
		ui.updateMoney(money);
	}
	
	// Kule satıldığında para eklemek için
    public void addMoney(int amount) {
        this.money += amount;
        ui.updateMoney(this.money);
    }
    
    public int getMoney() {
		return this.money;
	}
	
	public void spendMoney(int amount) {
		this.money -= amount;
		ui.updateMoney(this.money);
	}
	// Bütün dalgalar gönderildi mi diye Game'in sorabileceği metot
		public boolean areAllWavesStarted() {
			return currentWave >= waveManager.getTotalWaves();
		}
		
		// Gerçek zafer anında Game'in çağıracağı metot
		public void levelWon() {
			if(!gameEnded) {
				gameEnded = true;
				if(waveCountdownTimer != null) waveCountdownTimer.stop();
				main.levelComplete(currentLevel + 1);
			}
		}
	
	
}