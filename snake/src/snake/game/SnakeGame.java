package snake.game;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SnakeGame {
	
	private int rows, columns, score;
	private long deltaTime;
	private boolean running, snakeTurnBlocked;
	private Random random;
	private Thread thread;
	
	private JFrame frame;
	private JPanel panel;
	private GridLayout grid;
	private ImageIcon whiteicon, greenicon;
	private JLabel gameoverLabel;
	
	private Cell field[][], apple;
	private ArrayList<Cell> snake;
	private enum dir{UP, DOWN, LEFT, RIGHT}
	private dir snakeDirection;

	public SnakeGame(){
		
		deltaTime = 200;
		running = true;
		snakeTurnBlocked = false;
		rows = 20;
		columns = 20;
		snakeDirection = dir.UP;
		score = 0;
		random = new Random();
		
		//loading images
		whiteicon = new ImageIcon("assets/cell_white.gif");
		greenicon = new ImageIcon("assets/cell_green.gif");
		
		//creating frame
		frame = new JFrame();
		frame.setTitle("Snake");
		frame.setSize(400, 400);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//creating panel and grid layout
		panel = new JPanel();
		grid = new GridLayout(rows, columns);
		grid.setHgap(4);
		grid.setVgap(4);
		panel.setLayout(grid);
		
		//creating game over label
		gameoverLabel = new JLabel("<html><p align=center>Game Over!</html>");
		gameoverLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		//creating field
		field = new Cell[rows][columns];
		for(int i = 0; i < field.length; i++){ //rows
			for(int j = 0; j < field[i].length; j++){ //columns
				field[i][j] = new Cell(i, j, whiteicon);
				panel.add(field[i][j]);
			}
		}
		
		//packing frame
		frame.add(panel);
		frame.pack();
		
		//creating snake and apple
		createSnake(rows /2, columns /2);
		createApple();
		
		//snake control
		frame.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_W){
					if((snakeDirection == dir.LEFT || snakeDirection == dir.RIGHT) && !snakeTurnBlocked){
						snakeDirection = dir.UP;
						snakeTurnBlocked = true;
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_A){
					if((snakeDirection == dir.UP || snakeDirection == dir.DOWN) && !snakeTurnBlocked){
						snakeDirection = dir.LEFT;
						snakeTurnBlocked = true;
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_S){
					if((snakeDirection == dir.LEFT || snakeDirection == dir.RIGHT) && !snakeTurnBlocked){
						snakeDirection = dir.DOWN;
						snakeTurnBlocked = true;
					}
				}
				if(e.getKeyCode() == KeyEvent.VK_D){
					if((snakeDirection == dir.UP || snakeDirection == dir.DOWN) && !snakeTurnBlocked){
						snakeDirection = dir.RIGHT;
						snakeTurnBlocked = true;
					}
				}
			}
		});
		
		//updating thread
		thread = new Thread(){
			public void run(){
				while(running){
					try{
						Thread.sleep(deltaTime);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					update();
				}
			}
		};
		thread.start();
	}
	
	//snake's head is the last element
	public void createSnake(int r, int c){
		snake = new ArrayList<Cell>();
		snake.add(field[r][c + 2]);
		snake.add(field[r][c + 1]);
		snake.add(field[r][c]);
	}
	
	public void createApple(){
		int r = random.nextInt(rows);
		int c = random.nextInt(columns);
		if(!snake.contains(field[r][c])){
			apple = field[r][c];
		}else{
			createApple();
		}
	}
	
	//true == green, false == white
	public void setCellGreen(int r, int c, boolean color){
		if(color){
			field[r][c].setIcon(greenicon);
		}else{
			field[r][c].setIcon(whiteicon);
		}
	}

	public void update(){
		
		//calculating cell for snake's next step
		int r, c;
		switch(snakeDirection){
			case UP:
				r = snake.get(snake.size() - 1).getRow() - 1;
				c = snake.get(snake.size() - 1).getColumn();
				break;
			case RIGHT:
				r = snake.get(snake.size() - 1).getRow();
				c = snake.get(snake.size() - 1).getColumn() + 1;
				break;
			case DOWN:
				r = snake.get(snake.size() - 1).getRow() + 1;
				c = snake.get(snake.size() - 1).getColumn();
				break;
			case LEFT:
				r = snake.get(snake.size() - 1).getRow();
				c = snake.get(snake.size() - 1).getColumn() - 1;
				break;
			default:
				r = snake.get(snake.size() - 1).getRow();
				c = snake.get(snake.size() - 1).getColumn();
				break;
		}
		
		//checking for collision with boundaries
		if(r >= 0 && r < rows && c >= 0 && c < columns){
			
			//moving snake
			if(field[r][c] == apple){ //eating apple
				createApple();
				score++;
				
			}else if(snake.contains(field[r][c])){ //cutting off tail
				int tail = snake.indexOf(field[r][c]) + 1;
				for(int i = 0; i < tail; i++){
					snake.remove(i);
				}
				snake.remove(0);
				score = score - tail;
				
			}else{ //normal moving
				snake.remove(0);
			}
			snake.add(field[r][c]);
			snakeTurnBlocked = false;
			
		}else{
			gameOver();
		}

		//updating cells color
		for(int i = 0; i < field.length; i++){ //rows
			for(int j = 0; j < field[i].length; j++){ //columns
				if(snake.contains(field[i][j]) || field[i][j] == apple){
					setCellGreen(i, j, true);
				}else{
					setCellGreen(i, j, false);
				}
			}
		}
	}
	
	public void gameOver(){
		running = false;
		gameoverLabel.setText(String.format("<html><p align=center>Game Over!<br/>Your score: %d</html>", score));
		frame.remove(panel);
		frame.add(gameoverLabel);
		frame.revalidate();
		frame.repaint();
	}
	
}
