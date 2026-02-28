//Ahmet Salih Demir
import javafx.scene.shape.*;
import javafx.scene.image.Image;
import javafx.scene.paint.*;

public class Cell {
	private boolean isPath; //Hücremizin path olup olmadığını belirlemek için kullanılacak değiken.
	private int row; // Hücremizin gridpane üzerindeki satır konumu.
	private int col; // Hücremisisn gridpane üzerindeki sütun konumu.
	private Rectangle view; //Her hücreyi temsil eden dikdörtgen görseli.
	private Tower tower;  // Hğcrede kule bulunma durumunu kontrol etmek için bir kule objesi.
	
	public Cell(int row, int col, boolean isPath, boolean hasTower) {
		this.col = col;
		this.row = row;
		this.isPath = isPath;
		
		// YENİ: Paket içi okuma mantığı (getResourceAsStream) ve başlarına "/" eklendi
		Image image1 = new Image(getClass().getResourceAsStream("/grass4.png"));
		Image image2 = new Image(getClass().getResourceAsStream("/grass5.png"));
        
		view = new Rectangle(40,40);
		view.setStroke(Color.TRANSPARENT);
		view.setArcHeight(8);
		view.setArcWidth(8);
		int a = (int)(Math.random() * 2);
		boolean b = false;
		if(a == 1)
			b=true;
		view.setFill(b  ? new ImagePattern(image1) : new ImagePattern(image2));
	}
	
	public void setPath(boolean isPath) {
		this.isPath = isPath;
		// YENİ: Paket içi okuma mantığı
		Image image3 = new Image(getClass().getResourceAsStream("/path.png"));
		view.setFill(new ImagePattern(image3));
	}
	
	public Rectangle getView() {
		return view;
	}

	public boolean isPath() {
		return isPath;
	}

	public boolean hasTower() {
		return this.tower != null;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Tower getTower() { 
		return tower;
	}

	public void setTower(Tower tower) { 
		System.out.println("setTower çağrıldı: " + tower);
		this.tower=tower;
	}
}