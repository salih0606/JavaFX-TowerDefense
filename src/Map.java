//Ahmet Salih Demir
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.io.*;
import java.util.*;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Map {
	private Cell[][] grid;               //Haritadaki tüm hücreleri tutan 2D dizi.
	private int rows;                    //Satır sayısı.
	private int cols;                    //Sütun sayısı
	private GridPane view;               //Haritanın genel görüntüsü.
	List<Cell> path = new ArrayList<>(); //Düşmanların ilerleyeceği path hücrelerini tutan liste.

	public Map(int level) {
		view = new GridPane();//Grid panemizi başlatıyoruz.
		try {
			// YENİ: Paket içi veri akışı (InputStream) ile okuma sistemi.
            // Başındaki "/" işareti paketin (src/exe) kök dizininden okumasını sağlar.
			InputStream is = getClass().getResourceAsStream("/level" + level + ".txt");
			
			if (is == null) {
				System.err.println("Harita dosyası bulunamadı: level" + level + ".txt");
				return; // Çökmeyi engeller
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
			
			//Birinci satırı okuyoruz burada sütun bilgisi var.
			line = reader.readLine();
			//Okuduğumuz değeri sütuna eşitliyoruz
			cols = Integer.parseInt(line.split(":")[1]);
			
			//İkinci satırı okuyoruz.
			line = reader.readLine();
			//Okuduğumuz değeri satıra eşitliyoruz.
			rows = Integer.parseInt(line.split(":")[1]);
			
			//Elde ettiğimiz satır sütun bilgileriyle gridimizi oluşturuyoruz.
			grid = new Cell[rows][cols];
			
			//Hücrelerin geliş animasyonu için boş bir time line açıyoruz.
			Timeline timeLine = new Timeline();
			 
			//Her hücre bir önceki hücreden 15 milisaniye sonra oluşmaya başlıyor.
			int delayPerCell = 15;
			//Bir hücrenin oluşma süresi 20 milisaniye
			int animationDuration = 20;
			
			
			for(int r = 0; r<rows; r++) {
				for(int c = 0; c<cols; c++) {
					//Gridpanemizi hücre hücre oluşturuyoruz ve path olma ve kula bulundurma durumunu yanlış olarak ayarlıyoruz.
					grid[r][c] = new Cell(r, c, false, false);
					
					//Hücreler animasyon ile oluşurken boyut olarak 0 dan ve opaklık olarak yoktan oluşmaları için bu satırları ekliyoruz.
					grid[r][c].getView().setScaleX(0);
					grid[r][c].getView().setScaleY(0);
					grid[r][c].getView().setOpacity(0);
					
					//Genel map görünümümüze hücreleri tek tek ekliyoruz.
					view.add(grid[r][c].getView(), c, r);
					
					//Hücrelerin animasyon zamanlaması için index oluşturduk.
					int index = r *cols + c;
					//Aldığımız index ve hücreler arası oluşma süresiyle animasyona start time değerini oluşturuyoruz.
					Duration startTime = Duration.millis(index * delayPerCell);
					//key valueler ile opaklığı ve büyüklükleri 0 dan 1 e gtimesi için ayarlıyoruz.
					KeyValue kvOpacity = new KeyValue(grid[r][c].getView().opacityProperty(), 1);
					KeyValue kvX = new KeyValue(grid[r][c].getView().scaleXProperty(), 1);
			        KeyValue kvY = new KeyValue(grid[r][c].getView().scaleYProperty(), 1);
			        
			        //Key frame vererek animation timerimzie aralıkları veriyoruz.
			        KeyFrame kf = new KeyFrame(startTime.add(Duration.millis(animationDuration)), kvX, kvY, kvOpacity);

			        timeLine.getKeyFrames().add(kf);
					
				}
			}
			
			timeLine.play();
			
			//Wave data satırına kadar satırları okuyoruz.
			while((line = reader.readLine()) != null && !line.startsWith("WAVE_DATA:")) {
				//Satır içeriklerini virgül ile ayırarak 0 kısımlarını path için satır değeri ve 1kısımlarını path için sütun değeri olarak ayarlıyoruz.
				String[] parts = line.split(",");
				int r = Integer.parseInt(parts[0]);
				int c = Integer.parseInt(parts[1]);
				//Belirlediğimiz hücreleri set path methodumuz da true olarak ayarladık.
				grid[r][c].setPath(true);
				//Path hücrelerini tutan listemize belirli hücreleri ekledik.
				path.add(grid[r][c]);
			}
			reader.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("view.getWidth(): " + view.getWidth());
		System.out.println("calculateTopLeftX: " + calculateTopLeftX());
		
	}
	//Map görüntüsünü döndüren method.
	public GridPane getView() {
		view.setAlignment(Pos.CENTER);
		view.setPadding(new Insets(50));
		return view;
	}
	//Map içerisindeki belirli bir hücreyi döndüren method.
	public Cell getCell(int row, int col) {
		return grid[row][col];
		
	}
	//Path listesini döndüren method.
	public List<Cell> getPath(){
		return path;
	}
	//Kontrol satırlarımız için yazdığımız hesplama methodları.
	public double calculateTopLeftX() {
		return (view.getWidth() - cols * 40) / 2;
	}
	public double calculateTopLeftY() {
		return (view.getHeight() - rows * 40) / 2;
	}
}