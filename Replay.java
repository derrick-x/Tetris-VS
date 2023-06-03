import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import java.io.*;
import java.time.temporal.ChronoUnit;

public class Replay {
	static ArrayList<String> sounds = new ArrayList<>();
	static int[][] prevBoards = new int[20][20];
	static int index = 0;
	static boolean paused = false;
	static long currTime = 0;
	static long rtOffset = System.currentTimeMillis();
	static long[] times; //Only used during replay watching
	static boolean[] activeKeys = new boolean[4];
	static class Frame implements Comparable<Frame>{
		long time;
		int[][] boards = new int[20][20];
		int[] pqueue = new int[6];
		int[] cqueue = new int[6];
		ArrayList<String> sounds = new ArrayList<String>();
		int phold;
		int chold;
		int garbage;
		public Frame(long t, int[][] b, int[]pq, int[]cq, int ph, int ch, ArrayList<String> s) {
			time = t; boards = b; pqueue = pq; cqueue = cq; phold = ph; chold = ch;sounds = s;
		}
		public Frame(String input) {
			String[] args = input.split(" ");
			time = Long.parseLong(args[0]);
			for (int y=0; y<20; y++) {
				for (int x=0; x<10; x++) {
					boards[y][x] = args[1].charAt(y*10+x)-48;
				}
			}
			for (int y=0; y<20; y++) {
				for (int x=0; x<10; x++) {
					boards[y][x+10] = args[2].charAt(y*10+x)-48;
				}
			}
			for (int i=0; i<6; i++) {
				pqueue[i] = args[3].charAt(i)-48;
			}
			for (int i=0; i<6; i++) {
				cqueue[i] = args[4].charAt(i)-48;
			}
			phold = Integer.parseInt(args[5]);
			chold = Integer.parseInt(args[6]);
			for (int i=7; i<args.length; i++) {
				sounds.add(args[i]);
			}
		}
		public int compareTo(Frame f) {
			return (int)(time-f.time);
		}
	}
	static ArrayList<Frame> replay = new ArrayList<>();
	public static void export() throws IOException{	
		if (!Main.saveReplays) {
			return;
		}
		String time = java.time.LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
		time = time.replace(':', '-');
		FileWriter output = new FileWriter("replays\\replay "+time+".out");
		while (replay.size()>0) {
			Frame frame = replay.remove(0);
			StringBuilder line = new StringBuilder();
			line.append(frame.time+" ");
			for (int y=0; y<20; y++) {
				for (int x=0; x<10; x++) {
					line.append(frame.boards[y][x]);
				}
			}
			line.append(" ");
			for (int y=0; y<20; y++) {
				for (int x=10; x<20; x++) {
					line.append(frame.boards[y][x]);
				}
			}
			line.append(" ");
			for (int i=0; i<6; i++) {
				line.append(frame.pqueue[i]);
			}
			line.append(" ");
			for (int i=0; i<6; i++) {
				line.append(frame.cqueue[i]);
			}
			line.append(" "+frame.phold);
			line.append(" "+frame.phold);
			for (int i=0; i<frame.sounds.size(); i++) {
				line.append(" "+frame.sounds.get(i)); 
			}
			output.write(line.toString()+"\n");
		}
		output.close();
	}
	public static void loadIn() throws IOException{
		Scanner scan = new Scanner(new File("replay.in"));
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			replay.add(new Frame(line));
		}
		times = new long[replay.size()];
		for (int i=0; i<replay.size(); i++) {
			times[i] = replay.get(i).time;
		}
	}
	public static void saveTick() {
		int[][] save = new int[20][20];
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				save[y][x] = Tetris.playerBoard[y][x];
			}
		}
		for (int y=0; y<20; y++) {
			for (int x=10; x<20; x++) {
				save[y][x] = Computer.board[y][x-10];
			}
		}
		int[] pqueue = new int[6];
		for (int i=0; i<6; i++) {
			pqueue[i] = Tetris.queue.get(i);
		}
		int[] cqueue = new int[6];
		for (int i=0; i<6; i++) {
			try {
				cqueue[i] = Computer.queue.get(i);
			} catch (Exception e) {
				cqueue[i] = 0;
			}
		}
		replay.add(new Frame(System.currentTimeMillis()-Main.startTime, save, pqueue, cqueue, Tetris.hold, Computer.hold, new ArrayList<String>(sounds)));
		sounds.clear();
	}
	public static Graphics tick(Graphics g) {
		if (replay.size()==0) {
			return g;
		}
		if (Main.arrowKeys[0]&&!activeKeys[0]) {
			rtOffset+=5000;
			if (rtOffset>System.currentTimeMillis()) {
				rtOffset = System.currentTimeMillis();
			}
		}
		if (Main.arrowKeys[2]&&!activeKeys[2]) {
			rtOffset-=5000;
		}
		if (Main.arrowKeys[3]&&!activeKeys[3]) {
			paused = !paused;
		}
		for (int i=0; i<4; i++) {
			activeKeys[i] = Main.arrowKeys[i];
		}
		if (paused) {
			rtOffset = System.currentTimeMillis()-currTime;
		} else {
			currTime = System.currentTimeMillis()-rtOffset;
		}
		int newIndex = Arrays.binarySearch(times, currTime);
		if (newIndex<0) {
			newIndex*=-1;
			newIndex--;
		}
		if (index!=newIndex) {
			index = newIndex;
			g.setColor(Color.black);
			g.fillRect(410, 0, 480, 700);
			g.fillRect(50, 610, 200, 150);
		} else {
			return g;
		}
		if (index>=replay.size()) {
			System.exit(0);
		}
		g.setColor(Color.white);
		g.drawRect(900, 0, 300, 600);
		g.drawRect(100, 0, 300, 600);
		g.drawString("Time: "+(currTime/1000.0), 600, 500);
		if (replay.size()==0) {
			System.exit(0);
		}
		HashSet<String> sounds = new HashSet<String>(); //Avoid duplicate sounds
		for (int i=0; i<replay.get(index).sounds.size(); i++) {
			sounds.add(replay.get(index).sounds.get(i));
		}
		for (String s : sounds) {
			FileManager.playSound(s);
		}
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				if (prevBoards[y][x]!=replay.get(index).boards[y][x]) {
					g.setColor(Main.colors[replay.get(index).boards[y][x]]);
					g.fillRect(x*30+100, y*30, 30, 30);
					prevBoards[y][x] = replay.get(index).boards[y][x];
				}
				if (prevBoards[y][x+10]!=replay.get(index).boards[y][x+10]) {
					g.setColor(Main.colors[replay.get(index).boards[y][x+10]]);
					g.fillRect(x*30+900, y*30, 30, 30);
					prevBoards[y][x+10] = replay.get(index).boards[y][x+10];
				}
				
			}
		}
		for (int i=0; i<6; i++) {
			for (int p=0; p<4; p++) {
				g.setColor(Main.colors[replay.get(index).pqueue[i]]);
				g.fillRect(450+Main.positions[replay.get(index).pqueue[i]-1][p].x*30, (1+i)*100+Main.positions[replay.get(index).pqueue[i]-1][p].y*30, 30, 30);
			}
		}
		for (int p=0; p<4; p++) {
			if (replay.get(index).phold==0) {
				break;
			}
			g.setColor(Main.colors[replay.get(index).phold]);
			g.fillRect(100+Main.positions[replay.get(index).phold-1][p].x*30, 700+Main.positions[replay.get(index).phold-1][p].y*30, 30, 30);
		}
		return g;
	}
}
