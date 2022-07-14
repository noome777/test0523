package application;

//UI�� ����� ���� 3���� import ����
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BlockGame {
	
	static class MyFrame extends JFrame{
	
	//constant --> ����� ������ ���� �빮��, ����� �̿��Ѵ�.
	static int BALL_WIDTH = 20;
	static int BALL_HEIGHT = 20;
	static int BLOCK_ROWS = 5;
	static int BLOCK_COLUMNS = 10;
	static int BLOCK_WIDTH = 39;
	static int BLOCK_HEIGHT = 20;
	static int BLOCK_GAP = 3;
	static int BAR_WIDTH = 80;
	static int BAR_HEIGHT = 20;
	static int CANVAS_WIDTH =400 + (BLOCK_GAP * BLOCK_COLUMNS) + BLOCK_GAP;
	static int CANVAS_HEIGHT = 600;
	
	
	
	
	//variable
	static MyPanel myPanel = null;
	static int score = 0;
	static Timer timer = null;
	static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLUMNS];
	static Bar bar = new Bar();
	static Ball ball = new Ball();
	static int barXTarget = bar.x; //TargetX Value �� ������ �ϱ� ���� ���̴�.
	static int dir = 0; //0 : Up-Right, 1 : Down-Right, 2:Up-Left, 3: Down-Left;
	static int ballSpeed = 5;
	
	
	
	
	
	static class Ball{
		int x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;//ĵ������ �߰����� ���� ������ ���̸�ŭ ���ָ� ���� ȭ�� �߰����� ���� �����ϰ� �ȴ�.
		int y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2; //���� ����
		int width = BALL_WIDTH;
		int height = BALL_HEIGHT;
		
		Point getCenter() {
			return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT / 2));
		}
		Point getBottomCenter() {
			return new Point(x +(BALL_WIDTH / 2), y + (BALL_HEIGHT));
		}
		Point getTopCenter() {
			return new Point(x +(BALL_WIDTH / 2), y);
		}
		Point getLeftCenter() {
			return new Point(x, y + (BALL_HEIGHT / 2));
		}
		Point getRightCenter() {
			return new Point(x +(BALL_WIDTH), y + (BALL_HEIGHT / 2));
		}
	}
	
	
	static class Bar{
		int x = CANVAS_WIDTH / 2 - BAR_WIDTH / 2;
		int y =  CANVAS_HEIGHT - 100; //600���� 100������ ���ش�
		int width = BAR_WIDTH;
		int height = BAR_HEIGHT;
		
	}
	
	
	static class Block{
		int x = 0;//����� ���� ���� ���� ��ġ�� �ٸ��� ������ ���߿� FOR������ �ʱ�ȭ ���ش�.
		int y = 0;
		int width = BLOCK_WIDTH;
		int height = BLOCK_HEIGHT;
		int color = 0; //0 : white, 1: yellow, 2: blue, 3: mazanta, 4: red -->���ڰ� Ŀ������ ���� ������ ������
		boolean isHidden = false;// ó������ ����� ������� �ʰ� ��������. ������, �浹 �Ŀ��� ����� �������.
	}
	
	
	
	static class MyPanel extends JPanel{ //CANVAS FOR DRAW!
		public MyPanel() {
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
			this.setBackground(Color.BLACK);
		}
		
		@Override //JPanel���� �������� �������̵� �޼ҵ��̰�, ���� ����� �� �ƴ϶� �̹� JPanel���� �����Ǿ��ִ� �Լ��̴�.
		public void paint(Graphics g) {
			super.paint(g);//super Ŭ������ paint��ü�� �Ѱ��༭ ���� ���õǾ��ٴ� ���� �˷��ش�.
			Graphics2D g2d = (Graphics2D)g; //�̰� ���� �������ִ� Ŭ������
			
			drawUI( g2d );
		}
		
		private void drawUI(Graphics2D g2d) {
			//draw Blocks�� �׷�����
			for(int i = 0; i < BLOCK_ROWS; i++) {
				for(int j = 0; j < BLOCK_COLUMNS; j++) {
					if(blocks[i][j].isHidden) {
						continue;
					}
					if(blocks[i][j].color == 0) {
						g2d.setColor(Color.YELLOW);
					} 
					else if (blocks[i][j].color == 1) {
						g2d.setColor(Color.GREEN);
					} 
					else if (blocks[i][j].color == 2) {
						g2d.setColor(Color.BLUE);
					} 
					else if (blocks[i][j].color == 3) {
						g2d.setColor(Color.MAGENTA);
					} 
					else if (blocks[i][j].color == 4) {
						g2d.setColor(Color.RED);
					}
					
					g2d.fillRect(blocks[i][j].x, blocks[i][j].y, blocks[i][j].width, blocks[i][j].height);// fillRect�� �׸� �׸��� �Լ�
					
					
					}
				
					//draw score�� �׷�����
					g2d.setColor(Color.WHITE);
					g2d.setFont((new Font("TimesRoman", Font.BOLD, 20)));
					g2d.drawString("score : " + score, CANVAS_WIDTH /2 - 30, 20);
					
					//draw ball
					g2d.setColor(Color.WHITE);
					g2d.fillOval(ball.x, ball.y , BALL_WIDTH, BALL_HEIGHT);
					
					//draw bar
					g2d.setColor(Color.WHITE);
					g2d.fillRect(bar.x, bar.y, BAR_WIDTH, BAR_HEIGHT);
					
				}
			}
		}




	
	
	
	
		public MyFrame(String title) {
			super(title);
			this.setVisible(true); //true�� ��������� ȭ�鿡 ���̰� �ȴ�. ���� this�� JFame�� ����.
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
			this.setLocation(400, 300); //ó���� ���� â�� ��� �� ����� �Űܼ� UI â�� �� �� �ְ� �������ش�.
			this.setLayout(new BorderLayout());
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//�̰� ����� â�� ����� ������. �� �׷��� �������Ḧ �ϰ� ��.
			
			
			//Init vars--> ���� ������ �ʱ�ȭ���ִ� �Լ�.
			initData();
			
			myPanel = new MyPanel();//ĵ���� ������ �ϴ� �����г��� ��������
			this.add("Center", myPanel); //�г��� ��������� ��ü �����ӿ� ���� �־��༭ ��ġ������ ���ش�?? -->public Component add(String name, Component comp)
			
			setKeyListener();//�ٰ� ������ ���� ���� ���ߵǴϱ� Ű���� �����ʸ� �������ش�.
			startTimer();
			
		}
		
		public void initData() {
			for(int i = 0; i < BLOCK_ROWS; i++) {
				for(int j = 0; j < BLOCK_COLUMNS; j++) {
					blocks[i][j] = new Block(); //������ ��ü�迭�� ������ ���� ���̰� ��ü�� ������� ���� �ƴϹǷ�, ���� �ϳ��ϳ� ��ü�� �����ؼ� ������ ���� �־���ߵ�.
					blocks[i][j].x = BLOCK_WIDTH * j + BLOCK_GAP * j; //����� 50���� ��������µ� ���� ����� ��ǥ���� ����� ��ġ�� ��������
					blocks[i][j].y = 100 + i * BLOCK_HEIGHT + BLOCK_GAP * i + BLOCK_GAP * i ; //��� ������ ����ϹǷ� 100�� �ش�.
					blocks[i][j].width = BLOCK_WIDTH;
					blocks[i][j].height = BLOCK_HEIGHT;
					blocks[i][j].color = 4 - i; //0 : white, 1: yellow, 2: blue, 3: mazanta, 4: red ���� �ؿ� �ִ� �ֵ��� �Ͼ������
					blocks[i][j].isHidden = false;
					
				}
			}
		}
		
		//Ű�������̽� �ʿ��� �̹� ������ �ִ� �Լ� ���۸� �ؼ� �̿����ش�.
		//���� ������ Ű�� �ٰ� �����̰� ���ش�
		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {
				

				@Override
				public void keyPressed(KeyEvent e) {//Key Event�� e��� ��ü������ ���� ������ �ȴ�.
					if(e.getKeyCode() == KeyEvent.VK_LEFT){
						System.out.println("Pressed Left Key");
						 barXTarget -= 20;
						 if(bar.x < barXTarget) {// ����ؼ� Ű�� ������ �� --> �ʹ� ���� ���� ������
							 barXTarget = bar.x;
						 }
						
					} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
						System.out.println("Pressed Right Key");
						 barXTarget += 20;
						 if(bar.x > barXTarget) {// ����ؼ� Ű�� ������ �� --> �ʹ� ���� ���� ������
							 barXTarget = bar.x; //Ű�� �������Ѽ� Ű�� �и��� ���� ������
						 }
					}
				}
			});
		}
		
		
		
		
		public void startTimer() {
			timer = new Timer(20, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { //Timer Event�� e��� ��ü������ ���� ������ �ȴ�.
					movement(); //������ó��
					checkCollision(); //���� ���� �浹ó�� wall.bar
					checkCollisionBlock(); //50���� ��ϰ��� �浹ó�� blocks
					myPanel.repaint(); //redraw�ϰ� ���ش�.
				}
			});
			timer.start();
		}
		
		
		
		
		public void movement() {//������ó�� 
			if(bar.x < barXTarget) {
				bar.x += 5;
			} else if(bar.x > barXTarget) {
				bar.x -= 5;
			}
			
			if(dir == 0) {// 0:up-right
				ball.x += ballSpeed;
				ball.y -= ballSpeed;
			} else if(dir == 1) {// 1: down-right
				ball.x += ballSpeed;
				ball.y += ballSpeed;
			} else if(dir == 2) {// 2: up-left 
				ball.x -= ballSpeed;
				ball.y -= ballSpeed;
			} else if(dir == 3) {// 3: down-left
				ball.x -= ballSpeed;
				ball.y += ballSpeed;
			}
		}
		public boolean duplRect(Rectangle rect1, Rectangle rect2) {
			return rect1.intersects(rect2); //check two Rect in Duplicated!
		}
		public void checkCollision(){ //���� ���� �浹ó��
			if(dir == 0) {// 0:up-right
				//wall
				if(ball.y < 0) {// wall upper
					dir = 1;
					
				}
				if(ball.x > CANVAS_WIDTH-BALL_WIDTH) {// wall right
					dir = 2;
				}
				//bar - none
			
			} else if(dir == 1) {// 1: down-right
				//wall
				if(ball.y > CANVAS_HEIGHT-BALL_HEIGHT-BALL_HEIGHT) {//wall bottom �� �ε����� ���
					dir = 0;
					
				}
				if(ball.x > CANVAS_WIDTH-BALL_WIDTH) {//wall right
					dir = 3;
				}
				//bar
				if(ball.getBottomCenter().y >= bar.y) {
					if( duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
								new Rectangle(bar.x, bar.y, bar.width, bar.height)) ) {
						dir = 0;
					}
				}
			} else if(dir == 2) {// 2: up-left 
				//wall
				if(ball.y < 0) {// wall upper
					dir = 3;
					
				}
				if(ball.x < 0) {// wall left
					dir = 0;
				}
				//bar - none
			} else if(dir == 3) {// 3: down-left
				//wall
				if(ball.y > CANVAS_HEIGHT-BALL_HEIGHT-BALL_HEIGHT) {// wall bottom
					dir = 2;
					
				}
				if(ball.x < 0) {// wall left
					dir = 1;
				}
				//bar
				if(ball.getBottomCenter().y >= bar.y) {
					if( duplRect(new Rectangle(ball.x, ball.width, ball.width, ball.height),
								new Rectangle(bar.x, bar.y, bar.width, bar.height)) ) {
						dir = 2;
					}
				}
			}
		}
		public void checkCollisionBlock(){ //50���� ��ϰ��� �浹ó��
			//0 : Up-Right, 1 : Down-Right, 2:Up-Left, 3: Down-Left;
			
			for(int i=0; i<BLOCK_ROWS; i++){
				for(int j=0; j<BLOCK_COLUMNS; j++) {
					Block block = blocks[i][j];
					if(block.isHidden == false) {
						if(dir == 0) {//0 : Up-Right
							if( duplRect(new Rectangle(ball.x, ball.width, ball.width, ball.height),
									     new Rectangle(block.x, block.y, block.width, block.height)) ) {
								if(ball.x > block.x + 2 &&
										ball.getRightCenter().x <= block.x + block.width -2) {
									//block botton collision
								} else {
									//block left collision
									dir = 2;
								}
								block.isHidden = true;
							}
						}
								
						} else if(dir == 1) {//1 : Down-Right
							if( duplRect(new Rectangle(ball.x, ball.width, ball.width, ball.height),
								     new Rectangle(block.x, block.y, block.width, block.height)) ) {
								if(ball.x > block.x + 2 &&
										ball.getRightCenter().x <= block.x + block.width -2) {
									//block top collision
									dir = 0;
								} else {
									//block left collision
									dir = 3;
								}
								block.isHidden = true;
							}
						} else if(dir == 2) {//2: Up-Left
								if( duplRect(new Rectangle(ball.x, ball.width, ball.width, ball.height),
									     new Rectangle(block.x, block.y, block.width, block.height)) ) {
									if(ball.x > block.x + 2 &&
											ball.getRightCenter().x <= block.x + block.width -2) {
										//block bottom collision
										dir = 3;
									} else {
										//block right collision
										dir = 0;
									}
									block.isHidden = true;
								}
						} else if(dir == 3) {//3: Down-Left
								if( duplRect(new Rectangle(ball.x, ball.width, ball.width, ball.height),
									     new Rectangle(block.x, block.y, block.width, block.height)) ) {
									if(ball.x > block.x + 2 &&
											ball.getRightCenter().x <= block.x + block.width -2) {
										//block top collision
										dir = 2;
									} else {
										//block right collision
										dir = 1;
									}
									block.isHidden = true;
							}
						}
					}
				}
			}
		}
	

	
	//���θ޼ҵ忡�� STATIC���� ȣ���ϱ� ������ ������ ��� ��� STATIC�� �ٿ��ִ� ��.
	public static void main(String[] args) {

		new MyFrame("BLOCK GAME");
		
		
		
		
	}
}

