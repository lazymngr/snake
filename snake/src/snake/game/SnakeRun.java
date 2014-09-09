package snake.game;

import javax.swing.SwingUtilities;

public class SnakeRun {

	public static void main(String[] args) {
	    
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new SnakeGame();
			}
		});
		
	}
}
