package snake.game;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Cell extends JLabel{
	static final long serialVersionUID = 1L;
	
	private int row, comlumn;
	
	public Cell(int r, int c, ImageIcon icon) {
		row = r;
		comlumn = c;
		setIcon(icon);
		setSize(10, 10);
	}

	public int getRow(){
		return row;
	}
	
	public int getColumn(){
		return comlumn;
	}

}
