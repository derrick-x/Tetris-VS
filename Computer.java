import java.util.*;
import java.awt.Point;

public class Computer {
	static int[][] board = new int[20][10];
	//0 = empty, 1 = I, 2 = J, 3 = L, 4 = O, 5 = S, 6 = T, 7 = Z, 8 = garbage
	static ArrayList<Integer> queue = new ArrayList<Integer>();
	static int hold = 0;
	static int b2b = -1;
	static int combo = -1;
	static int clearType = 0;
	static long clearTime = 0;
	static int pieces = 0;
	static int attacks = 0;
	static ArrayList<Integer> garbage = new ArrayList<Integer>();
	public static void tetris(){
		if (Main.disableBot) {
			return;
		}
		Replay.saveTick();
		Point[] bestPlacement = null;
		int bestScore = Integer.MIN_VALUE;
		int bestPiece = 0;
		int piece1 = queue.get(0);
		int piece2 = 0;
		if (hold>0) {
			piece2 = hold;
		} else {
			piece2 = queue.get(1);
		}
		//Test first piece
		for (int r=0; r<4; r++) {
			if (piece1==1) {
				int left = Main.Idomains[r*2];
				int right = Main.Idomains[r*2+1];
				Point[] positions = rotate(Main.positions[0], r);
				for (int x=left; x<=right; x++) {
					Point[] landSpot = landPiece(positions, x);
					if (scorePlacement(landSpot, piece1)>bestScore||(scorePlacement(landSpot, piece1)==bestScore&&Math.random()<0.03)) {
						bestScore = scorePlacement(landSpot, piece1);
						bestPlacement = landSpot;
						bestPiece = piece1;
					}
				}
			} else if (piece1==4) {
				int left = 0;
				int right = 8;
				for (int x=left; x<=right; x++) {
					Point[] landSpot = landPiece(Main.positions[piece1-1], x);
					if (scorePlacement(landSpot, piece1)>bestScore) {
						bestScore = scorePlacement(landSpot, piece1);
						bestPlacement = landSpot;
						bestPiece = piece1;
					}
				}
			} else {
				int left = Main.domains[r*2];
				int right = Main.domains[r*2+1];
				Point[] positions = rotate(Main.positions[piece1-1], r);
				for (int x=left; x<=right; x++) {
					Point[] landSpot = landPiece(positions, x);
					if (scorePlacement(landSpot, piece1)>bestScore) {
						bestScore = scorePlacement(landSpot, piece1);
						bestPlacement = landSpot;
						bestPiece = piece1;
					}
				}
			}
		}
		//Test second piece
		for (int r=0; r<4; r++) {
			if (piece2==1) {
				int left = Main.Idomains[r*2];
				int right = Main.Idomains[r*2+1];
				Point[] positions = rotate(Main.positions[0], r);
				for (int x=left; x<=right; x++) {
					Point[] landSpot = landPiece(positions, x);
					if (scorePlacement(landSpot, piece2)>bestScore) {
						bestScore = scorePlacement(landSpot, piece2);
						bestPlacement = landSpot;
						bestPiece = piece2;
					}
				}
			} else if (piece2==4) {
				int left = 0;
				int right = 8;
				for (int x=left; x<=right; x++) {
					Point[] landSpot = landPiece(Main.positions[piece2-1], x);
					if (scorePlacement(landSpot, piece2)>bestScore||(scorePlacement(landSpot, piece2)==bestScore&&Math.random()<0.03)) {
						bestScore = scorePlacement(landSpot, piece2);
						bestPlacement = landSpot;
						bestPiece = piece2;
					}
				}
			} else {
				int left = Main.domains[r*2];
				int right = Main.domains[r*2+1];
				Point[] positions = rotate(Main.positions[piece2-1], r);
				for (int x=left; x<=right; x++) {
					Point[] landSpot = landPiece(positions, x);
					if (scorePlacement(landSpot, piece2)>bestScore||(scorePlacement(landSpot, piece2)==bestScore&&Math.random()<0.03)) {
						bestScore = scorePlacement(landSpot, piece2);
						bestPlacement = landSpot;
						bestPiece = piece2;
					}
				}
			}
		}
		if (bestPlacement[0]==null) {
			Main.win = 1;
		}
		for (int i=0; i<4; i++) {
			board[bestPlacement[i].y][bestPlacement[i].x] = bestPiece;
		}
		if (bestPiece==queue.get(0)) {
			queue.remove(0);
		} else {
			if (bestPiece==hold) {
				hold = queue.remove(0);
			} else {
				queue.remove(1);
				hold = queue.remove(0);
			}
		}
		if (Tetris.checkBoard(board).cleared>0) {
			clearType = Tetris.checkBoard(board).cleared;
			combo++;
			clearTime = System.currentTimeMillis();
			if (clearType==4) {
				b2b++;
			} else {
				b2b = -1;
			}
			int thisAttack = 0;
			if (clearType==1) {
				thisAttack+=(int)(0.5+(combo*0.25));
			} else if (clearType==2) {
				thisAttack+=(int)(1+(combo*0.25));
			} else if (clearType==3) {
				thisAttack+=(int)(2+(combo*0.25));
			} else if (clearType==4) {
				thisAttack+=(int)(4+(combo*0.25))+Math.floor(Math.sqrt(b2b));
			}
			int cancel = thisAttack;
			while (cancel>0&&garbage.size()>0) {
				if (garbage.get(0)>cancel) {
					garbage.set(0, garbage.get(0)-cancel);
					cancel = 0;
				} else {
					cancel-=garbage.remove(0);
				}
			}
			attacks+=thisAttack;
			if (thisAttack>5) {
				FileManager.playSound("garbage_in_large");
			} else if (thisAttack>2) {
				FileManager.playSound("garbage_in_medium");
			} else if (thisAttack>0){
				FileManager.playSound("garbage_in_small");
			}
			Main.effects.add(new Main.Effect(thisAttack, false));
			Tetris.garbage.add(thisAttack);
		} else {
			combo = -1;
			if (garbage.size()>0) {
				try {
					Tetris.addGarbage(garbage, board, false);
				} catch (Exception e) {
					Main.win = 1;
					return;
				}
				garbage = new ArrayList<Integer>();
			}
		}
		board = Tetris.checkBoard(board).board;
		pieces++;
	}
	public static int scorePlacement(Point[] landSpot, int piece) {
		int score = 0;
		int[][] preview = new int[20][10];
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				preview[y][x] = board[y][x];
			}
		}
		for (int i=0; i<4; i++) {
			try {
				preview[landSpot[i].y][landSpot[i].x] = 8;
			} catch (Exception e) {
				return Integer.MIN_VALUE;
			}
		}
		Tetris.ClearInfo test = Tetris.checkBoard(preview);
		int linesCleared = test.cleared;
		//Find highest tile increase
		int highest = 20;
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				if (preview[y][x]>0&&preview[y][x]!=8) {
					highest = y;
					break;
				}
			}
			if (highest<20) {
				break;
			}
		}
		//Scan for holes
		boolean holes = false;
		for (int y=1; y<20; y++) {
			for (int x=0; x<10; x++) {
				if (preview[y][x]==0) {
					if (preview[y-1][x]>0) {
						holes = true;
						score-=100;
					}
				}
			}
		}
		score+=highest*5+(int)Math.pow(highest, 2);
		//Check if piece covers holes
		ArrayList<Integer> topHoles = new ArrayList<Integer>();
		for (int y=0; y<19; y++) {
			for (int x=0; x<10; x++) {
				if (preview[y][x]>0&&preview[y+1][x]==0) {
					topHoles.add(x);
					if (preview[y][x]==8) {
						score-=100;
					}
				}
			}
		}
		//Reward placing piece lower
		for (int i=0; i<4; i++) {
			score+=landSpot[i].y*5;
		}
		boolean do90stack = true;
		//Reward if vertical I placed in 3+ deep trench
		if (piece==1&&landSpot[0].x==landSpot[1].x&&!topHoles.contains(landSpot[0].x)) {
			int reward = 0;
			for (int i=0; i<4; i++) {
				boolean left = true;
				boolean right = true;
				try {
					left = board[landSpot[i].y][landSpot[i].x-1]>0;
				} catch (Exception e) {
					
				}
				try {
					right = board[landSpot[i].y][landSpot[i].x+1]>0;
				} catch (Exception e) {
					
				}
				if (left&&right) {
					reward++;
				}
			}
			if (reward>2) {
				score+=(reward-2)*200;
			}
		}
		//Reward if vertical L/J placed in 2+ deep trench
		if (piece==2||piece==3) {
			HashSet<Integer> yPos = new HashSet<Integer>();
			for (int i=0; i<4; i++) {
				yPos.add(landSpot[i].y);
			}
			if (yPos.size()==3) {
				HashMap<Integer, Point> stickPoints = new HashMap<Integer, Point>();
				for (int i=0; i<4; i++) {
					if (stickPoints.containsKey(landSpot[i].y)) {
						stickPoints.remove(landSpot[i].y);
					} else {
						stickPoints.put(landSpot[i].y, landSpot[i]);
					}
				}
				int inWell = 0;
				for (Point p : stickPoints.values()) {
					try {
						if (board[p.y][p.x-1]>0) {
							inWell++;
						}
					} catch (Exception e) {
						
					}
					try {
						if (board[p.y][p.x+1]>0) {
							inWell++;
						}
					} catch (Exception e) {
						
					}
				}
				if (inWell==4) {
					score+=300;
				}
			}
		}
		//Scan for multiple trenches
		int[] trenches = {20, 20, 20, 20, 20, 20, 20, 20, 20, 20};
		for (int x=1; x<9; x++) {
			int depth = 0;
			for (int y=19; y>=0; y--) {
				if (preview[y][x]==0&&preview[y][x-1]>0&&preview[y][x+1]>0) {
					depth++;
				} else {
					depth = 0;
				}
				if (depth>1) {
					do90stack = false;
					trenches[x] = y;
				}
			}
		}
		int depth = 0;
		for (int y=19; y>=0; y--) {
			if (preview[y][0]==0&&preview[y][1]>0) {
				depth++;
			} else {
				depth = 0;
			}
			if (depth>1) {
				score-=depth*5;
				do90stack = false;
				trenches[0] = y;
			}
		}
		depth = 0;
		for (int y=19; y>=0&&do90stack; y--) {
			if (preview[y][9]==0&&preview[y][8]>0) {
				depth++;
			} else {
				depth = 0;
			}
			if (depth>1) {
				score-=depth*5;
				trenches[9] = y;
			}
		}
		//Try to maintain 9-0 stacking, if safe board
		do90stack = do90stack&&!holes;
		for (int i=0; i<4&&do90stack; i++) {
			if (landSpot[i].x==9) {
				score-=50;
			}
		}
		//Check line clear type
		if (linesCleared==1&&highest>15&&!holes&&combo<1) {
			score-=1;
		} else if (linesCleared==4) {
			score+=500;
		}
		if ((piece==5||piece==7)&&score>0) { //Prioritize using S and Z pieces
			score+=100;
		}
		return score;
	}
	public static Point[] landPiece(Point[] positions, int xPos) {
		boolean resolved = false;
		boolean outside = true;
		for (int yPos=-4; yPos<21; yPos++) {
			outside = false;
			for (int t=0; t<4; t++) {
				int x = positions[t].x+xPos;
				int y = positions[t].y+yPos;
				if (y<0) {
					outside = true;
					continue;
				}
				if (y==20) {
					yPos--;
					resolved = true;
					break;
				}
				if (board[y][x]>0) {
					resolved = true;
					yPos--;
					break;
				}
			}
			if (resolved) {
				if (outside) {
					return null;
				} else {
					Point[] landSpot = {new Point(), new Point(), new Point(), new Point()};
					for (int t=0; t<4; t++) {
						landSpot[t].x = positions[t].x+xPos;
						landSpot[t].y = positions[t].y+yPos;
					}
					return landSpot;
				}
			}
		}
		System.out.println("Something went wrong");
		return null;
	}
	public static Point[] rotate(Point[] positions, int rotate) {
		Point[] rotated = {new Point(), new Point(), new Point(), new Point()};
		switch (rotate) {
		case 0:
			for (int i=0; i<4; i++) {
				rotated[i].x = positions[i].x;
				rotated[i].y = positions[i].y;
			}
			break;
		case 3:
			for (int i=0; i<4; i++) {
				rotated[i].x = positions[i].y;
				rotated[i].y = positions[i].x*-1;
			}
			break;
		case 2:
			for (int i=0; i<4; i++) {
				rotated[i].x = positions[i].x*-1;
				rotated[i].y = positions[i].y*-1;
			}
			break;
		case 1:
			for (int i=0; i<4; i++) {
				rotated[i].x = positions[i].y*-1;
				rotated[i].y = positions[i].x;
			}
			break;
		default:
			break;
		}
		return rotated;
	}
}
