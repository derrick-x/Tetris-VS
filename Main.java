import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Main extends Canvas{
	static HashMap<String, Image> images = new HashMap<String, Image>();
	static boolean debug = false;
	static double botPPS = 1;
	static boolean MAX_PPS = false;
	static boolean disableBot = false;
	static boolean watchReplay = false;
	static boolean continuous = false;
	static int firstTo = 2;
	static boolean saveReplays = true;
	static boolean dynamicPPS = false;	
	static boolean music = true;
	static boolean sound = true;
	static int pcMode = 0;
	static int mouseX = 0;
	static int mouseY = 0;
	static final int arr = 0;
	static final int das = 105;
	static int delay = das;
	static int dasKey = 0;
	static int win = 0; 
	static int cScore = 0;
	static int pScore = 0;
 	static long botLastPlace = System.currentTimeMillis()+3000;
	static long startTime;
	static boolean updateQueue = false;
	static boolean updateHold = false;
	static long finish = 0;
	static int round = 1;
	static int gameStage = 0;
	static int selected = 0;
	static final int options = 10;
	static ArrayList<Integer> bag = new ArrayList<Integer>();
	static final int[] Idomains = {1,7,0,9,2,8,0,9};
	static final int[] domains = {1,8,0,8,1,8,1,9};
	static final Color[] colors = {Color.black, Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green, Color.magenta, Color.red, Color.gray};
	static int[][] lastBoard = new int[20][10];
	static int[][] computer = new int[20][10];
	static Point[] lastShadow = {new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0)};
	static ArrayList<Effect> effects = new ArrayList<>();
	static final String[] pcModes = {"Off", "Standard", "DPC spam", "Box PC", "1st PC", "2nd PC", "3rd PC", "4th PC", "5th PC", "6th PC", "7th PC", "SDPC", "Hachispin PC", "MS2 PC"};
	static String dispText = "";
	static Long dispTime = (long) 0;
	static Canvas canvas;
	static boolean[] arrowKeys = new boolean[4];
	static ArrayList<Double> avgAPM = new ArrayList<Double>();
	static ArrayList<Double> avgPPS = new ArrayList<Double>();
	static String customQueue = null;
	static BufferedImage image = new BufferedImage(1600, 800, BufferedImage.TYPE_3BYTE_BGR);
	static final Point[][] positions = {
			{new Point(2, 0),new Point(-1, 0),new Point(0, 0),new Point(1, 0)},
			{new Point(-1, -1),new Point(-1, 0),new Point(0, 0),new Point(1, 0)},
			{new Point(-1, 0),new Point(0, 0),new Point(1, 0),new Point(1, -1)},
			{new Point(0, -1),new Point(0, 0),new Point(1, 0),new Point(1, -1)},
			{new Point(-1, 0),new Point(0, 0),new Point(0, -1),new Point(1, -1)},
			{new Point(-1, 0),new Point(0, 0),new Point(0, -1),new Point(1, 0)},
			{new Point(-1, -1),new Point(0, -1),new Point(0, 0),new Point(1, 0)}};
	
	public static void main(String[] args) throws InterruptedException {
		JFrame frame = new JFrame();
		canvas = new Main();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setSize(1600, 800);
		frame.add(canvas);
		canvas.setBackground(Color.black);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
		canvas.setFocusable(false);
		canvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (gameStage==0) {
					gameStage = 1;
				}
				canvas.repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
		});
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar()==' ') {
					//_Tetris.addGarbage(new ArrayList<Integer>(Arrays.asList(1)), _Computer.board);
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (gameStage==0) {
					canvas.repaint();
					if (e.getKeyCode()==KeyEvent.VK_UP) {
						selected--;
						if (selected<0) {
							selected = options;
						}
					}
					if (e.getKeyCode()==KeyEvent.VK_DOWN) {
						selected++;
						if (selected>options) {
								selected = 0;
						}
					}
					if (e.getKeyCode()==KeyEvent.VK_LEFT) {
						if (selected==0) {
							botPPS-=0.1;
						}
						if (selected==1) {
							MAX_PPS = !MAX_PPS;
						}
						if (selected==2) {
							disableBot = !disableBot;
						}
						if (selected==3) {
							watchReplay = !watchReplay;
						}
						if (selected==4) {
							continuous = !continuous;
						}
						if (selected==5) {
							firstTo--;
						}
						if (selected==6) {
							saveReplays = !saveReplays;
						}
						if (selected==7) {
							dynamicPPS = !dynamicPPS;
						}
						if (selected==8) {
							music = !music;
						}
						if (selected==9) {
							sound = !sound;
						}
						if (selected==10) {
							pcMode--;
							if (pcMode<0) {
								pcMode = pcModes.length-1;
							}
						}
					}
					if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
						if (selected==0) {
							botPPS+=0.1;
						}
						if (selected==1) {
							MAX_PPS = !MAX_PPS;
						}
						if (selected==2) {
							disableBot = !disableBot;
						}
						if (selected==3) {
							watchReplay = !watchReplay;
						}
						if (selected==4) {
							continuous = !continuous;
						}
						if (selected==5) {
							firstTo++;
						}
						if (selected==6) {
							saveReplays = !saveReplays;
						}
						if (selected==7) {
							dynamicPPS = !dynamicPPS;
						}
						if (selected==8) {
							music = !music;
						}
						if (selected==9) {
							sound = !sound;
						}
						if (selected==10) {
							pcMode++;
							if (pcMode==pcModes.length) {
								pcMode = 0;
							}
						}
					}
					return;
				}
				if (System.currentTimeMillis()<startTime) {
					return;
				}
				if (e.getKeyCode()==37||e.getKeyCode()==39||e.getKeyCode()==40) {
					arrowKeys[e.getKeyCode()-37] = true;
				} else {
					Tetris.keyPress(e.getKeyCode(), Tetris.playerBoard);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode()==37||e.getKeyCode()==39||e.getKeyCode()==40) {
					arrowKeys[e.getKeyCode()-37] = false;
					if (dasKey==e.getKeyCode()) {
						delay = das;
						dasKey = 0;
					}
				}
			}
		});
		botLastPlace = System.currentTimeMillis()+3000;
		for (int i=1; i<=7; i++) {
			bag.add(i);
		}
		while (gameStage==0) {
			try {
				drawDisplay();
			} catch (Exception e) {}
			canvas.paint(canvas.getGraphics());
		}
		canvas.repaint();
		image = new BufferedImage(1600, 800, BufferedImage.TYPE_3BYTE_BGR);
		runGame();
		if (win==1) {
			FileManager.playSound("elim");
			if (dynamicPPS) {
				botPPS*=1.25;
			}
		} else if (win==-1){
			FileManager.playSound("topout");
			if (dynamicPPS) {
				botPPS*=0.8;
			}
		}
		while (!watchReplay&&(continuous||(cScore<firstTo&&pScore<firstTo))) {
			avgAPM.add((double)Tetris.attacks*60000.0/(System.currentTimeMillis()-startTime));
			avgPPS.add((double)Tetris.pieces*1000.0/(System.currentTimeMillis()-startTime));
			win = 0;
			round++;
			lastBoard = new int[20][10];
			Tetris.playerBoard = new int[20][10];
			canvas.repaint();
			runGame();
			if (win==1) {
				FileManager.playSound("elim");
				if (dynamicPPS) {
					botPPS*=1.25;
				}
			} else if (win==-1){
				FileManager.playSound("topout");
				if (dynamicPPS) {
					botPPS*=0.8;
				}
			}
		}
		canvas.setVisible(false);
		System.out.println("\nFinal score: "+pScore+"-"+cScore);
		double totalAPM = 0;
		for (int i=0; i<avgAPM.size(); i++) {	
			totalAPM+=avgAPM.get(i);
		}
		System.out.println("Average apm: "+totalAPM/avgAPM.size());
		double totalPPS = 0;
		for (int i=0; i<avgPPS.size(); i++) {
			totalPPS+=avgPPS.get(i);
		}
		System.out.println("Average pps: "+totalPPS/avgPPS.size());
		if (pScore>cScore) {
			FileManager.playMusic("win");
		} else {
			FileManager.playMusic("lose");
		}
		Thread.sleep(1000);
		while (FileManager.music.isActive()) {}
		System.exit(0);
	}
	public static void runGame() {
		if (watchReplay) {
			startTime = System.currentTimeMillis();
		} else {
			startTime = System.currentTimeMillis()+3000;
		}
		Tetris.initialize();
		long lastTick = System.currentTimeMillis();
		if (watchReplay) {
			try {
				Replay.loadIn();
			} catch (Exception e) {
				System.out.println("Replay failed to load");
				e.printStackTrace();
				return;
			}
			while (true) {
				drawDisplay();
				canvas.paint(Replay.tick(canvas.getGraphics()));
			}
		}
		while (true) {
			FileManager.doMusic();
			for (int i=0; i<4; i++) {
				if (Tetris.queue.get(0)!=1&&(Tetris.preview[i].x-Tetris.x>1||Tetris.preview[i].x-Tetris.x<-1||Tetris.preview[i].y-Tetris.y>1||Tetris.preview[i].y-Tetris.y<-1)) {
					//FileManager.playSound("combo_16");
				}
			}
			long timePassed = System.currentTimeMillis()-lastTick;
			lastTick = System.currentTimeMillis();
			if (disableBot) {
				if (pcMode>10) {
					if (Tetris.pieces==6) {
						break;
					}
				} else if (pcMode>3) {
					if (Tetris.pieces==10) {
						break;
					}
				} else if (pcMode==3) {
					if (Tetris.pieces>4) {
						break;
					}
				} else if (pcMode==2) {
					if (Tetris.pieces==15) {
						break;
					}
				}
			}
			if (win==1) {
				pScore++;
				try {
					drawDisplay();
					image = new BufferedImage(1600, 800, BufferedImage.TYPE_3BYTE_BGR);
					canvas.paint(canvas.getGraphics());
				} catch (ConcurrentModificationException e) {
					
				}
				System.out.println("Player won");
				long time = (System.currentTimeMillis()-startTime)/1000;
				if (time==0) {
					time = 1;
				}
				int apm = (Tetris.attacks*600)/(int)time;
				int pps = (Tetris.pieces*100)/(int)time;
				int bapm = (Computer.attacks*600)/(int)time; 
 				System.out.println("Your apm: "+apm/10.0);
				System.out.println("Your pps: "+pps/100.0);
				System.out.println("Bot apm:  "+bapm/10.0);
				System.out.println("Bot pps:  "+(int)(botPPS*100)/100.0);
				System.out.println("time:     "+time);
				try {
					Replay.export();
				} catch (Exception e) {
					System.out.println("Replay export failed.");
					e.printStackTrace();
				}
				return;
			}
			if (win==-1) {
				cScore++;
				try {
					drawDisplay();
					image = new BufferedImage(1600, 800, BufferedImage.TYPE_3BYTE_BGR);
					canvas.paint(canvas.getGraphics());
				} catch (ConcurrentModificationException e) {
					
				}
				System.out.println("Computer won");
				long time = (System.currentTimeMillis()-startTime)/1000;
				int apm = (Tetris.attacks*600)/(int)time;
				int pps = (Tetris.pieces*100)/(int)time;
				int bapm = (Computer.attacks*600)/(int)time;
				System.out.println("Your apm: "+apm/10.0);
				System.out.println("Your pps: "+pps/100.0);
				System.out.println("Bot apm:  "+bapm/10.0);
				System.out.println("Bot pps:  "+(int)(botPPS*100)/100.0);
				System.out.println("time:     "+time);
				try {
					Replay.export();
				} catch (Exception e) {
					System.out.println("Replay export failed.");
					e.printStackTrace();
				}
				return;
			}
			debug = false;
			if (Computer.queue.size()<7) {
				ArrayList<Integer> newBag = newBag();
				for (int i=0; i<7; i++) {
					Computer.queue.add(newBag.get(i));
				}
			}
			if (arrowKeys[0]) {
				if (dasKey==37) {
					delay-=timePassed;
					if (delay<=0) {
						Tetris.keyPress(37, Tetris.playerBoard);
					}
				} else {
					Tetris.keyPress(37, Tetris.playerBoard);
					dasKey = 37;
				}
			} else if (arrowKeys[2]) {
				if (arrowKeys[2]) {
					if (dasKey==39) {
						delay-=timePassed;
						if (delay<=0) {
							Tetris.keyPress(39, Tetris.playerBoard);
						}
					} else {
						Tetris.keyPress(39, Tetris.playerBoard);
						dasKey = 39;
					}
				}
			}
			if (arrowKeys[3]) {
				Tetris.keyPress(40, Tetris.playerBoard);
			}
			if (System.currentTimeMillis()>startTime&&(MAX_PPS||botLastPlace<System.currentTimeMillis()-(1000/botPPS))) {
				try {
					Computer.tetris();
					botLastPlace = System.currentTimeMillis();
				} catch (Exception e) {
					win = 1;
					continue;
				}
			}
			try {
				mouseX = canvas.getMousePosition().x;
				mouseY = canvas.getMousePosition().y;
			} catch (NullPointerException e) {
				
			}
			try {
				drawDisplay();
				canvas.paint(canvas.getGraphics());
			} catch (ConcurrentModificationException e) {
				
			}
		}
	}
	public static void drawDisplay() {
		Graphics g = image.getGraphics();
		if (gameStage==0) {
			g.setColor(Color.black);
			g.fillRect(0, 0, 1600, 800);
			g.setColor(Color.white);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
			g.drawString("OPTIONS", 50, 25);
			g.drawString("Click anywhere to start", 600, 25);
			if (selected==0) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			botPPS = Math.round(botPPS * 10)/10.0;
			g.drawString("Bot speed: "+botPPS, 50, 50);
			if (selected==1) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Max speed: "+MAX_PPS, 50, 75);
			if (selected==2) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Single player: "+disableBot, 50, 100);
			if (selected==3) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Watch replay: "+watchReplay, 50, 125);
			if (selected==4) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Endless 1v1: "+continuous, 50, 150);
			if (selected==5) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("First to: "+firstTo, 50, 175);
			if (selected==6) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Save replays: "+saveReplays, 50, 200);
			if (selected==7) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Adjusting bot speed: "+watchReplay, 50, 225);
			if (selected==8) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Enable music: "+music, 50, 250);
			if (selected==9) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("Enable SFX: "+sound, 50, 275);
			if (selected==10) {
				g.setColor(colors[selected%7+1]);
			} else {
				g.setColor(Color.white);
			}
			g.drawString("PC mode: "+pcModes[pcMode], 50, 300);
			return;
		}
		if (watchReplay) {
			if (Replay.replay==null) {
				return;
			}
			g = Replay.tick(g);
			return;
		}
		g.setColor(Color.white);
		//g.drawString(_Tetris.x+" "+_Tetris.y, 50, 50);
		g.drawRect(900, 0, 300, 600);
		g.drawRect(100, 0, 300, 600);
		if (System.currentTimeMillis()<startTime) {
			for (int i=0; i<5; i++) {
				if (Tetris.queue.size()<6) {
					break;
				}
				for (int p=0; p<4; p++) {
					g.setColor(colors[Tetris.queue.get(i)]);
					g.fillRect(450+positions[Tetris.queue.get(i)-1][p].x*30, (1+i)*100+positions[Tetris.queue.get(i)-1][p].y*30, 30, 30);
				}
			}
		} else {
			g.setColor(Color.black);
			g.fillRect(401, 0, 498, 800);
			for (int i=0; i<5; i++) {
				if (Tetris.queue.size()<6) {
					break;
				}
				for (int p=0; p<4; p++) {
					g.setColor(colors[Tetris.queue.get(i+1)]);
			 		g.fillRect(450+positions[Tetris.queue.get(i+1)-1][p].x*30, (1+i)*100+positions[Tetris.queue.get(i+1)-1][p].y*30, 30, 30);
			 	}
			}		
		}
		for (int i=0; i<effects.size(); i++) {
			g = effects.get(i).drawSelf(g);
		}
		
		g.setColor(Color.white);
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
		if (Tetris.combo>0) {
			g.drawString("Combo x"+Tetris.combo, 600, 200);
		}
		if (Tetris.b2b>0) {
			g.drawString("Back-to-back x"+Tetris.b2b, 600, 230);
		}
		//Debug
		//	g.drawString(Tetris.x+" "+Tetris.y, 600, 300);
		if (disableBot&&pcMode>0) {
			g.drawString("Total PCs: "+Tetris.pcCount, 600, 260);
		}
		
		if (System.currentTimeMillis()<dispTime) {
			if (dispText.equals("TETRIS")) {
				g.setColor(Color.cyan);
			}
			if (dispText.contains("T-SPIN")) {
				g.setColor(Color.magenta);
			}
			g.drawString(dispText, 600, 150);
		}
		if (System.currentTimeMillis()<startTime) {
			g.setColor(Color.black);
			g.fillRect(600, 0, 298, 800);
			g.setColor(Color.white);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 50));
			g.drawString((1-(System.currentTimeMillis()-startTime)/1000)+"", 650, 50);
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
			if (!disableBot) {
				g.drawString(pScore+"-"+cScore, 650, 300);
			}
			updateQueue = true;
			return;
		}
		//Player Tetris
		try {
			Point[] shadow = Tetris.getShadow(Tetris.playerBoard, Tetris.preview);
			for (int i=0; i<4; i++) {
				g.setColor(colors[Tetris.playerBoard[lastShadow[i].y][lastShadow[i].x]]);
				for (int p=0; p<4; p++) {
					if (lastShadow[i].equals(Tetris.preview[p])) {
						g.setColor(colors[Tetris.queue.get(0)]);
					 }
				}	
				g.fillRect(lastShadow[i].x*30+100, lastShadow[i].y*30, 30, 30);
				g.setColor(Color.darkGray);
				if (shadow[i].equals(Tetris.preview[0])) {
					continue;
				}
				if (shadow[i].equals(Tetris.preview[1])) {
					continue;
				}
				if (shadow[i].equals(Tetris.preview[2])) {
					continue;
				}
				if (shadow[i].equals(Tetris.preview[3])) {
					continue;
				}
				g.fillRect(shadow[i].x*30+100, shadow[i].y*30, 30, 30);
				lastShadow[i] = new Point(shadow[i]);
			 }
		 } catch (Exception e) {
			 
	 	}
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				if (y==Tetris.preview[0].y&&x==Tetris.preview[0].x) {
					continue;
				}
				if (y==Tetris.preview[1].y&&x==Tetris.preview[1].x) {
					continue;
				}
				if (y==Tetris.preview[2].y&&x==Tetris.preview[2].x) {
					continue;
				}
				if (y==Tetris.preview[3].y&&x==Tetris.preview[3].x) {
					continue;
				}
				if (Tetris.playerBoard[y][x]==lastBoard[y][x]) {
					continue;
				}
				g.setColor(colors[Tetris.playerBoard[y][x]]);
				g.fillRect(x*30+100, y*30, 30, 30);
			}
		}
		for (int p=0; p<4; p++) {
			if (Tetris.queue.get(0)==lastBoard[Tetris.preview[p].y][Tetris.preview[p].x]) {
				continue;
			}
			g.setColor(colors[Tetris.queue.get(0)]);
			g.fillRect(100+Tetris.preview[p].x*30, Tetris.preview[p].y*30, 30, 30);
		}
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				lastBoard[y][x] = Tetris.playerBoard[y][x];
			}
		}
		for (int p=0; p<4; p++) {
			lastBoard[Tetris.preview[p].y][Tetris.preview[p].x] = Tetris.queue.get(0);
		}
		if (updateHold) {
			g.setColor(Color.black);
			g.fillRect(0, 601, 200, 200);
			for (int p=0; p<4; p++) {
				if (Tetris.hold==0) {
					break;
				}
				g.setColor(colors[Tetris.hold]);
				g.fillRect(100+positions[Tetris.hold-1][p].x*30, 700+positions[Tetris.hold-1][p].y*30, 30, 30);
			}
		}
		int pGarbage = 0;
		for (int l : Tetris.garbage) {
			pGarbage+=l;
		}
		g.setColor(Color.red);
		g.fillRect(405, 600-pGarbage*30, 4, pGarbage*30);
		int cGarbage = 0;
		for (int l : Computer.garbage) {
			cGarbage+=l;
		}
		g.setColor(Color.red);
		g.fillRect(895, 600-cGarbage*30, 4, cGarbage*30);
		//Computer Tetris
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				if (Computer.board[y][x]==computer[y][x]) {
					continue;
				}
				computer[y][x] = Computer.board[y][x];
				g.setColor(colors[Computer.board[y][x]]);
				g.fillRect(x*30+900, y*30, 30, 30);
			}
		}
		g.setColor(Color.white);
	}
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	public static ArrayList<Integer> newBag(){
		ArrayList<Integer> temp = new ArrayList<Integer>(bag);
		ArrayList<Integer> newBag = new ArrayList<Integer>();
		while (temp.size()>0) {
			newBag.add(temp.remove((int)(Math.random()*temp.size())));
		}
		return newBag;
	}
	static class Effect{
		int size;
		float time;
		boolean team;
		public Effect(int s, boolean t) {
			size = s; team = t; time = Math.max(1, size-3);
		}
		public Graphics drawSelf(Graphics g) {
			if (team) {
				g.setColor(Color.getHSBColor((float)0.5, 1, Math.min(1, time)));
			} else {
				g.setColor(Color.getHSBColor(0,  1, Math.min(1, time)));
			}
			g.setFont(new Font(Font.MONOSPACED, Font.BOLD, (int)(Math.sqrt(size)*20)));
			if (team) {
				g.drawString(size+"", 550, 300);
			} else {
				g.drawString(size+"", 800, 300);
			}
			time-=0.05;
			if (time<=0) {
				Main.effects.remove(this);
			}
			return g;
		}
	}
}