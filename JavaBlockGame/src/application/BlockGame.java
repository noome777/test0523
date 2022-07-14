package application;

//UI를 만들기 위해 3개를 import 해줌
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class BlockGame {
	
	static class MyFrame extends JFrame{
	
	//constant --> 상수를 지정할 때는 대문자, 언더바 이용한다.
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
	static int barXTarget = bar.x; //TargetX Value 는 보관을 하기 위한 것이다.
	static int dir = 0; //0 : Up-Right, 1 : Down-Right, 2:Up-Left, 3: Down-Left;
	static int ballSpeed = 5;
	
	
	
	
	
	static class Ball{
		int x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;//캔버스의 중간에서 공의 반지름 길이만큼 빼주면 완전 화면 중간에서 공이 시작하게 된다.
		int y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2; //위와 같음
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
		int y =  CANVAS_HEIGHT - 100; //600에서 100정도를 빼준다
		int width = BAR_WIDTH;
		int height = BAR_HEIGHT;
		
	}
	
	
	static class Block{
		int x = 0;//블록의 개수 많고 각각 위치가 다르기 때문에 나중에 FOR문으로 초기화 해준다.
		int y = 0;
		int width = BLOCK_WIDTH;
		int height = BLOCK_HEIGHT;
		int color = 0; //0 : white, 1: yellow, 2: blue, 3: mazanta, 4: red -->숫자가 커질수록 깰때 점수가 높아짐
		boolean isHidden = false;// 처음에는 블록이 사라지지 않게 설정해줌. 하지만, 충돌 후에는 블록이 사라진다.
	}
	
	
	
	static class MyPanel extends JPanel{ //CANVAS FOR DRAW!
		public MyPanel() {
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
			this.setBackground(Color.BLACK);
		}
		
		@Override //JPanel에서 가져오는 오버라이드 메소드이고, 직접 만드는 게 아니라 이미 JPanel에서 지정되어있는 함수이다.
		public void paint(Graphics g) {
			super.paint(g);//super 클래스에 paint객체를 넘겨줘서 내가 세팅되었다는 것을 알려준다.
			Graphics2D g2d = (Graphics2D)g; //이것 또한 지원해주는 클래스임
			
			drawUI( g2d );
		}
		
		private void drawUI(Graphics2D g2d) {
			//draw Blocks를 그려보기
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
					
					g2d.fillRect(blocks[i][j].x, blocks[i][j].y, blocks[i][j].width, blocks[i][j].height);// fillRect는 네모를 그리는 함수
					
					
					}
				
					//draw score를 그려보기
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
			this.setVisible(true); //true로 설정해줘야 화면에 보이게 된다. 여깃 this는 JFame을 뜻함.
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
			this.setLocation(400, 300); //처음에 게임 창을 띄울 때 가운데로 옮겨서 UI 창이 뜰 수 있게 설정해준다.
			this.setLayout(new BorderLayout());
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//이걸 해줘야 창이 제대로 닫힌다. 안 그러면 강제종료를 하게 됨.
			
			
			//Init vars--> 변수 값들을 초기화해주는 함수.
			initData();
			
			myPanel = new MyPanel();//캔버스 역할을 하는 마이패널을 생성해줌
			this.add("Center", myPanel); //패녈을 만들었으니 전체 프레임에 집어 넣어줘서 위치지정을 해준다?? -->public Component add(String name, Component comp)
			
			setKeyListener();//바가 오른쪽 왼쪽 말들 들어야되니까 키보드 리스너를 선언해준다.
			startTimer();
			
		}
		
		public void initData() {
			for(int i = 0; i < BLOCK_ROWS; i++) {
				for(int j = 0; j < BLOCK_COLUMNS; j++) {
					blocks[i][j] = new Block(); //위에서 객체배열의 공간만 만든 것이고 객체가 만들어진 것은 아니므로, 공간 하나하나 객체를 생성해서 실제로 집어 넣어줘야됨.
					blocks[i][j].x = BLOCK_WIDTH * j + BLOCK_GAP * j; //블록이 50개가 만들어지는데 각각 블록의 좌표마다 블록의 위치를 잡아줘야함
					blocks[i][j].y = 100 + i * BLOCK_HEIGHT + BLOCK_GAP * i + BLOCK_GAP * i ; //상단 여백을 줘야하므로 100을 준다.
					blocks[i][j].width = BLOCK_WIDTH;
					blocks[i][j].height = BLOCK_HEIGHT;
					blocks[i][j].color = 4 - i; //0 : white, 1: yellow, 2: blue, 3: mazanta, 4: red 제일 밑에 있는 애들이 하얀색으로
					blocks[i][j].isHidden = false;
					
				}
			}
		}
		
		//키인터페이스 쪽에서 이미 가지고 있는 함수 구글링 해서 이용해준다.
		//왼쪽 오른쪽 키로 바가 움직이게 해준다
		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {
				

				@Override
				public void keyPressed(KeyEvent e) {//Key Event가 e라는 객체변수를 통해 들어오게 된다.
					if(e.getKeyCode() == KeyEvent.VK_LEFT){
						System.out.println("Pressed Left Key");
						 barXTarget -= 20;
						 if(bar.x < barXTarget) {// 계속해서 키를 눌렀을 때 --> 너무 많은 값이 가버림
							 barXTarget = bar.x;
						 }
						
					} else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
						System.out.println("Pressed Right Key");
						 barXTarget += 20;
						 if(bar.x > barXTarget) {// 계속해서 키를 눌렀을 때 --> 너무 많은 값이 가버림
							 barXTarget = bar.x; //키를 고정시켜서 키가 밀리는 것을 방지함
						 }
					}
				}
			});
		}
		
		
		
		
		public void startTimer() {
			timer = new Timer(20, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) { //Timer Event가 e라는 객체변수를 통해 들어오게 된다.
					movement(); //움직임처리
					checkCollision(); //벽과 바의 충돌처리 wall.bar
					checkCollisionBlock(); //50개의 블록과의 충돌처리 blocks
					myPanel.repaint(); //redraw하게 해준다.
				}
			});
			timer.start();
		}
		
		
		
		
		public void movement() {//움직임처리 
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
		public void checkCollision(){ //벽과 바의 충돌처리
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
				if(ball.y > CANVAS_HEIGHT-BALL_HEIGHT-BALL_HEIGHT) {//wall bottom 에 부딪히는 경우
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
		public void checkCollisionBlock(){ //50개의 블록과의 충돌처리
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
	

	
	//메인메소드에서 STATIC으로 호출하기 때문에 변수와 상수 모두 STATIC을 붙여주는 것.
	public static void main(String[] args) {

		new MyFrame("BLOCK GAME");
		
		
		
		
	}
}

