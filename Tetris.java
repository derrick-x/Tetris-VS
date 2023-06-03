import java.util.ArrayList;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class Tetris {
	static int lastPress = 0;
	static int x = 0;
	static int y = 0;
	static int rotation = 0;
	static int combo = -1;
	static int b2b = -1;
	static int[][] playerBoard = new int[20][10];
	static Point[] preview = new Point[4];
	static ArrayList<Integer> queue = new ArrayList<Integer>();
	static ArrayList<Integer> garbage = new ArrayList<Integer>();
	static int attacks = 0;
	static int pieces = 0;
	static int hold = 0;
	static int clearType = 0;
	static boolean canHold = true;
	static int pcCount = 0;
	static int canTS = 2; //0 = no, 1 = mini only, 2 = yes
	static final int[][] sdpc = {
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{8,0,0,0,0,0,0,0,0,0},
			{8,8,8,0,0,0,0,0,0,0},
			{8,8,8,8,0,0,0,8,8,8},
			{8,8,8,8,0,0,0,0,8,8},
			{8,8,8,8,8,8,0,8,8,8},
	};
	static class ClearInfo{
		int cleared;
		int[][] board;
		boolean isPC;
		boolean isTS;
		public ClearInfo(int c, int[][] b, boolean p, boolean t)
		{cleared = c; board = b; isPC = p; isTS = t;}
	}
	public static void initialize() {
		lastPress = 0;
		x = 0;
		y = 0;
		rotation = 0;
		combo = -1;
		b2b = -1;
		preview = new Point[4];
		queue = new ArrayList<Integer>();
		if (Main.customQueue!=null) {
			for (int i=0; i<Main.customQueue.length(); i++) {
				queue.add(Main.customQueue.charAt(i)-48);
			}
		}
		garbage = new ArrayList<Integer>();
		attacks = 0;
		pieces = 0;
		hold = 0;
		clearType = 0;
		canHold = true;
		canTS = 2;
		Computer.board = new int[20][10];
		Computer.queue = new ArrayList<Integer>();
		Computer.hold = 0;
		Computer.b2b = -1;
		Computer.combo = -1;
		Computer.clearType = 0;
		Computer.clearTime = 0;
		Computer.pieces = 0;
		Computer.attacks = 0; 
		Computer.garbage.clear();
		ArrayList<Integer> newBag = Main.newBag();
		ArrayList<Integer> pcExtra = Main.newBag();
		if (Main.disableBot) {
			if (Main.pcMode>3&&Main.pcMode<11) {
				int extra = 7-((Main.pcMode-4)*10)%7;
				for (int i=0; i<extra; i++) {
					queue.add(pcExtra.get(i));
				}
			}
			if (Main.pcMode==11) {
				for (int y=0; y<20; y++) {
					for (int x=0; x<10; x++) {
						playerBoard[y][x] = sdpc[y][x];
					}
				}
			}
			if (Main.pcMode==2) {
				hold = (int)(Math.random()*7)+1;
				Main.updateHold = true;
			}
		}
		for (int i=0; i<7; i++) {
			queue.add(newBag.get(i));
		}
		for (int i=0; i<4; i++) {
			preview[i] = new Point(Main.positions[queue.get(0)-1][i].x+4, Main.positions[queue.get(0)-1][i].y+1);
		}
		x = 4;
		y = 1;
	}
	public static int[][] keyPress(int keyCode, int[][] board){
		realign();
		boolean success = true;
		if (keyCode==KeyEvent.VK_LEFT) {
			for (int i=0; i<4; i++) {
				preview[i].x--;
				if (preview[i].x<0||playerBoard[preview[i].y][preview[i].x]>0) {
					for (i=i+0; i>=0; i--) {
						preview[i].x++;
					}
					success = false;
					break;
				}
			}
			if (success) {
				FileManager.playSound("move");
				x--;
				if (Main.delay<=0) {
					//keyPress(keyCode, board);
				}
			}
		}
		if (keyCode==KeyEvent.VK_RIGHT) {
			for (int i=0; i<4; i++) {
				preview[i].x++;
				if (preview[i].x>9||playerBoard[preview[i].y][preview[i].x]>0) {
					for (i=i+0; i>=0; i--) {
						preview[i].x--;
					}
					success = false;
					break;
				}
			}
			if (success) {
				FileManager.playSound("move");
				x++;
				if (Main.delay<=0) {
					//keyPress(keyCode, board);
				}
			}
		}
		if (keyCode==KeyEvent.VK_DOWN) {
			for (int i=0; i<4; i++) {
				preview[i].y++;
				if (preview[i].y>19||playerBoard[preview[i].y][preview[i].x]>0) {
					for (i=i+0; i>=0; i--) {
						preview[i].y--;
					}
					success = false;
					break;
				}
			}
			if (success) {
				FileManager.playSound("softdrop");
				y++;
				//keyPress(keyCode, board);
			}
		}
		if (keyCode=='S') {
			for (int i=0; i<4; i++) {
				preview[i].y++;
				if (preview[i].y>19||playerBoard[preview[i].y][preview[i].x]>0) {
					for (i=i+0; i>=0; i--) {
						preview[i].y--;
					}
					success = false;
					break;
				}
			}
			if (success) {
				FileManager.playSound("softdrop");
				y++;
				realign();
			}
		}
		if (keyCode==KeyEvent.VK_UP) {
			canTS = 2;
			if (srsRotate(board, true)) {
				for (int i=0; i<4; i++) {
					preview[i].x-=x;
					preview[i].y-=y;
					int temp = preview[i].x;
					preview[i].x = preview[i].y*-1;
					preview[i].y = temp;
					preview[i].x+=x;
					preview[i].y+=y;
				}
				rotation++;
				FileManager.playSound("rotate");
				realign();
			}
		}
		if (keyCode=='Z') {
			canTS = 2;
			if (srsRotate(board, false)) {
				for (int i=0; i<4; i++) {
					preview[i].x-=x;
					preview[i].y-=y;
					int temp = preview[i].x;
					preview[i].x = preview[i].y;
					preview[i].y = temp*-1;
					preview[i].x+=x;
					preview[i].y+=y;
				}
				rotation--;
				FileManager.playSound("rotate");
			}
		}
		if (keyCode=='A') {
			for (int i=0; i<4; i++) {
				preview[i].x-=x;
				preview[i].y-=y;
				preview[i].x*=-1;
				preview[i].y*=-1;
				preview[i].x+=x;
				preview[i].y+=y;
				if (preview[i].x<0||preview[i].x>9||preview[i].y<0||preview[i].y>19||board[preview[i].y][preview[i].x]>0) {
					for (i+=0; i>=0; i--) {
						preview[i].x-=x;
						preview[i].y-=y;
						preview[i].x*=-1;
						preview[i].y*=-1;
						preview[i].x+=x;
						preview[i].y+=y;
					}
					break;
				}
			}
			rotation+=2;
			if (rotation>3) {
				rotation%=4;
			}
			FileManager.playSound("rotate");
		}
		if (keyCode=='C') {
			if (canHold) {
				Main.updateHold = true;
				canHold = false;
				if (hold==0) {
					hold = queue.remove(0);
					Main.updateQueue = true;
					
				} else {
					queue.add(0, hold);
					hold = queue.remove(1);
				}
				for (int j=0; j<4; j++) {
					preview[j] = new Point(Main.positions[queue.get(0)-1][j].x+4, Main.positions[queue.get(0)-1][j].y+1);
				}
				FileManager.playSound("hold");
				x = 4;
				y = 1;
			}
			realign();
		}
		if (keyCode==' ') {
			Main.updateQueue = true;
			FileManager.playSound("harddrop");
			while (success) {
				for (int i=0; i<4; i++) {
					preview[i].y++;
					if (preview[i].y>19||playerBoard[preview[i].y][preview[i].x]>0) {
						for (i=i+0; i>=0; i--) {
							preview[i].y--;
						}
						for (int j=0; j<4; j++) {
							board[preview[j].y][preview[j].x] = queue.get(0);
						}
						ClearInfo clear = Tetris.checkBoard(board);
						x = 4;
						y = 1;
						rotation = 0;
						clearType = clear.cleared;
						queue.remove(0);
						if (Main.disableBot&&Main.pcMode>0&&clear.isPC) {
							pcCount++;
						}
						for (int j=0; j<4; j++) {
							if (playerBoard[Main.positions[queue.get(0)-1][j].y+1][Main.positions[queue.get(0)-1][j].x+4]>0) {
								Main.win = -1;
								return playerBoard;
							}
							preview[j] = new Point(Main.positions[queue.get(0)-1][j].x+4, Main.positions[queue.get(0)-1][j].y+1);
						}
						if (clearType>0) {
							playerBoard = clear.board;
							combo++;
							if (combo>15) {
								FileManager.playSound("combo_16");
							} else if (combo>0) {
								FileManager.playSound("combo_"+combo);
							}
							if (canTS==0) {
								clear.isTS = false;
							}
							if (clearType==4||clear.isTS) {
								b2b++;
								if (b2b==4) {
									FileManager.playSound("btb_1");
								} else if (b2b==9) {
									FileManager.playSound("btb_2");
								} else if (Math.sqrt(b2b)%1==0&&b2b>1) {
									FileManager.playSound("btb_3");
								}
							} else {
								if (b2b>1) {
									FileManager.playSound("btb_break");
								}
								b2b = -1;
							}
							int thisAttack = 0;
							if (clear.isTS&&canTS==2) {
								FileManager.playSound("clearspin");
								if (clearType==1) {
									thisAttack+=(int)(2+(combo*0.5))+Math.floor(Math.sqrt(b2b));
								} else if (clearType==2) {
									thisAttack+=(int)(4+(combo))+Math.floor(Math.sqrt(b2b));
								} else if (clearType==3) {
								 	thisAttack+=(int)(6+(combo*1.5))+Math.floor(Math.sqrt(b2b));
								} else if (clearType==4) {
									thisAttack+=(int)(8+(combo*2))+Math.floor(Math.sqrt(b2b));
								}
								if (clear.isPC) {
									FileManager.playSound("allclear");
									thisAttack+=10;
								}
							} else {
								FileManager.playSound("clearline");
								if (clearType==1) {
									thisAttack+=(int)(0.5+(combo*0.25));
								} else if (clearType==2) {
									thisAttack+=(int)(1+(combo*0.25));
								} else if (clearType==3) {
									thisAttack+=(int)(2+(combo*0.5));
								} else if (clearType==4) {
									FileManager.playSound("clearquad");
									thisAttack+=(int)(4+(combo))+Math.floor(Math.sqrt(b2b));
								}
								if (clear.isPC) {
									FileManager.playSound("allclear");
									thisAttack+=10;
								}
							}
							if (b2b>0) {
								FileManager.playSound("clearbtb");
							}
							if (clearType==1) {
								Main.dispText = "SINGLE";
							}
							if (clearType==2) {
								Main.dispText = "DOUBLE";
							}
							if (clearType==3) {
								Main.dispText = "TRIPLE";
							}
							if (clearType==4) {
								Main.dispText = "TETRIS";
							}
							if (canTS==1&&clear.isTS) {
								Main.dispText = "MINI T-SPIN "+Main.dispText;
							}
							if (canTS==2&&clear.isTS) {
								Main.dispText = "T-SPIN "+Main.dispText;
								clearType+=4;
							}
							if (clear.isPC) {
								Main.dispText+=" PERFECT CLEAR";
							}
							Main.dispTime = System.currentTimeMillis()+2000;
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
							Main.effects.add(new Main.Effect(thisAttack, true));
							Computer.garbage.add(thisAttack);
						} else {
							if (combo>0) {
								FileManager.playSound("combobreak");
							}
							combo = -1;
							if (garbage.size()>0) {
								try {
									Tetris.addGarbage(garbage , board, true);
									if (garbage.get(0)>0) {
										FileManager.playSound("garbagerise");
									}
								} catch (Exception e) {
									Main.win = -1;
									return playerBoard;
								}
								garbage = new ArrayList<Integer>();
							}
						}
						canHold = true;
						pieces++;
						success = false;
						canTS = 2;
						break;
					}
				}
				if (success) {
					y++;
					canTS = 0;
				}
			}
			Replay.saveTick();
		}
		if (queue.size()<7) {
			ArrayList<Integer> newBag = Main.newBag();
			for (int i=0; i<7; i++) {
				queue.add(newBag.get(i));
			}
		}
		lastPress = keyCode;
		if (keyCode!=' ') {
			
		}
		return board;
	}
	public static ClearInfo checkBoard(int[][] check) {
		int cleared = 0;
		int[][] temp = new int[20][10];
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				temp[y][x] = check[y][x];
			}
		}
		int corners = 0;
		if (queue.get(0)==6) { //T-spin detection
			try {
				if (check[Tetris.y-1][Tetris.x-1]>0) {
					corners++;
				}
			} catch (Exception e) {
				corners++;
			}
			try {
				if (check[Tetris.y-1][Tetris.x+1]>0) {
					corners++;
				}
			} catch (Exception e) {
				corners++;
			}
			try {
				if (check[Tetris.y+1][Tetris.x-1]>0) {
					corners++;
				}
			} catch (Exception e) {
				corners++;
			}
			try {
				if (check[Tetris.y+1][Tetris.x+1]>0) {
					corners++;
				}
			} catch (Exception e) {
				corners++;
			}
		}
		boolean isTS = corners>=3;
		ArrayList<int[]> clear = new ArrayList<int[]>(Arrays.asList(temp));
		for (int y=0; y<clear.size(); y++) {
			boolean filled = true;
			for (int x=0; x<10; x++) {
				if (clear.get(y)[x]==0) {
					filled = false;
				}
			}
			if (filled) {
				clear.remove(y);
				cleared++;
				y--;
			}
		}
		int[][] newBoard = new int[20][10];
		for (int y=0; y<clear.size(); y++) {
			for (int x=0; x<10; x++) {
				newBoard[20-clear.size()+y][x] = clear.get(y)[x];
			}
		}
		boolean isPC = true;
		for (int y=0; y<20; y++) {
			for (int x=0; x<10; x++) {
				if (newBoard[y][x]>0) {
					isPC = false;
				}
			}
		}
		return new ClearInfo(cleared, newBoard, isPC, isTS);
	}
	public static boolean srsRotate(int[][] board, boolean clockwise) {
		if (rotation<0) {
			rotation+=100;
		}
		int rotate = (rotation%4)*2;
		if (clockwise) {
			rotate++;
		}
		/*
		0: 0->3
		1: 0->1
		2: 1->0
		3: 1->2
		4: 2->1
		5: 2->3
		6: 3->2
		7: 3->0
		*/
		ArrayList<Point> preview = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			preview.add(new Point(Tetris.preview[i].x, Tetris.preview[i].y));
		}
		canTS = 2;
		if (clockwise) {
			for (int i=0; i<4; i++) {
				preview.get(i).x-=x;
				preview.get(i).y-=y;
				int temp = preview.get(i).x;
				preview.get(i).x = preview.get(i).y*-1;
				preview.get(i).y = temp;
				preview.get(i).x+=x;
				preview.get(i).y+=y;
			}
		} else {
			for (int i=0; i<4; i++) {
				preview.get(i).x-=x;
				preview.get(i).y-=y;
				int temp = preview.get(i).x;
				preview.get(i).x = preview.get(i).y;
				preview.get(i).y = temp*-1;
				preview.get(i).x+=x;
				preview.get(i).y+=y;
			}
		}
		ArrayList<Point>prev = new ArrayList<Point>(preview);
		for (int i=0; i<4; i++) {
			try {
				if (board[prev.get(i).y][prev.get(i).x]>0) {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
			if (i==3) {
				return true;
			}
		}
		canTS = 1;
		if (rotate==0||rotate==5) {
			//Test 2
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		}
			for (int i=0; i<4; i++) {
				prev.get(i).x+=1;
				prev.get(i).y+=0;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=1;
						Tetris.preview[j].y+=0;
					}
					x+=1;
					y+=0;
					return true;
				}
			}
			//Test 3
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=1;
				prev.get(i).y+=-1;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=1;
						Tetris.preview[j].y+=-1;
					}
					x+=1;
					y+=-1;
					return true;
				}
			}
			canTS = 2;
			//Test 4
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=0;
				prev.get(i).y+=2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=0;
						Tetris.preview[j].y+=2;
					}
					x+=0;
					y+=2;
					return true;
				}
			}
			//Test 5
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=1;
				prev.get(i).y+=2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=1;
						Tetris.preview[j].y+=2;
					}
					x+=1;
					y+=2;
					return true;
				}
			}
			return false;
		}
		if (rotate==1||rotate==4) {
			//Test 2
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=-1;
				prev.get(i).y+=0;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=-1;
						Tetris.preview[j].y+=0;
					}
					x+=-1;
					y+=0;
					return true;
				}
			}
			//Test 3
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=-1;
				prev.get(i).y+=-1;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=-1;
						Tetris.preview[j].y+=-1;
					}
					x+=-1;
					y+=-1;
					return true;
				}
			}
			canTS = 2;
			//Test 4
			prev = new ArrayList<Point>();
			for (int i=0; i<4; i++) {
				prev.add(new Point(preview.get(i).x, preview.get(i).y));
			};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=0;
				prev.get(i).y+=2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=0;
					y+=2;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=0;
						Tetris.preview[j].y+=2;
					}
					return true;
				}
			}
			//Test 5
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=-1;
				prev.get(i).y+=2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=-1;
					y+=2;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=-1;
						Tetris.preview[j].y+=2;
					}
					return true;
				}
			}
			return false;
		}
		if (rotate==2||rotate==3) {
			//Test 2
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=1;
				prev.get(i).y+=0;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=1;
					y+=0;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=1;
						Tetris.preview[j].y+=0;
					}
					return true;
				}
			}
			//Test 3
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=1;
				prev.get(i).y+=1;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=1;
					y+=1;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=1;
						Tetris.preview[j].y+=1;
					}
					return true;
				}
			}
			canTS = 2;
			//Test 4
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=0;
				prev.get(i).y+=-2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=0;
					y+=-2;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=0;
						Tetris.preview[j].y+=-2;
					}
					return true;
				}
			}
			//Test 5
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=1;
				prev.get(i).y+=-2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=1;
					y+=-2;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=1;
						Tetris.preview[j].y+=-2;
					}
					return true;
				}
			}
			return false;
		}
		if (rotate==6||rotate==7) {
			//Test 2
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=-1;
				prev.get(i).y+=0;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=-1;
					y+=0;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=-1;
						Tetris.preview[j].y+=0;
					}
					return true;
				}
			}
			//Test 3
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=-1;
				prev.get(i).y+=1;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=-1;
					y+=1;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=-1;
						Tetris.preview[j].y+=1;
					}
					return true;
				}
			}
			canTS = 2;
			//Test 4
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=0;
				prev.get(i).y+=-2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=0;
					y+=-2;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=0;
						Tetris.preview[j].y+=-2;
					}
					return true;
				}
			}
			//Test 5
			prev = new ArrayList<Point>();
		for (int i=0; i<4; i++) {
			prev.add(new Point(preview.get(i).x, preview.get(i).y));
		};
			for (int i=0; i<4; i++) {
				prev.get(i).x+=-1;
				prev.get(i).y+=-2;
			}
			for (int i=0; i<4; i++) {
				try {
					if (board[prev.get(i).y][prev.get(i).x]>0) {
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					break;
				}
				if (i==3) {
					x+=-1;
					y+=-2;
					for (int j=0; j<4; j++) {
						Tetris.preview[j].x+=-1;
						Tetris.preview[j].y+=-2;
					}
					return true;
				}
			}
			return false;
		}
		return false;
	}
	public static int[][] addGarbage(ArrayList<Integer> amount, int[][] board, boolean team){
		for (int i=0; i<amount.size(); i++) {
			int top = -1;
			for (int y=0; y<20; y++) {
				for (int x=0; x<10; x++) {
					if (top<0) {
						if (board[y][x]>0) {
							top = y;
						}
					}
					if (top>=0) {
						if (top>-1&&top<amount.get(i)) {
							if (team) {
								Main.win = -1;
							} else {
								Main.win = 1;
							}
							return board;
						}
						board[y-amount.get(i)] = board[y];
					}
				}
			}
			int garbageHole = (int)(Math.random()*10);
			int[] garbage = {8, 8, 8, 8, 8, 8, 8, 8, 8, 8};
			garbage[garbageHole] = 0;
			for (int j=0; j<amount.get(i); j++) {
				board[19-j] = garbage;
			}
		}
		return board;
	}
	public static Point[] getShadow(int[][] board, Point[] piece) {
		Point[] shadow = {new Point(piece[0]), new Point(piece[1]), new Point(piece[2]), new Point(piece[3])};
		boolean floor = false;
		while (!floor) {
			for (int i=0; i<4; i++) {
				shadow[i].y++;
				if (shadow[i].y>19||board[shadow[i].y][shadow[i].x]>0) {
					floor = true;
					for (int j=i; j>=0; j--) {
						shadow[j].y--;
					}
					break;
				}
			}
		}
		return shadow;
	}
	public static void realign() {
		Point[] positions = {new Point(Main.positions[queue.get(0)-1][0]), new Point(Main.positions[queue.get(0)-1][1]), new Point(Main.positions[queue.get(0)-1][2]), new Point(Main.positions[queue.get(0)-1][3])};
		rotation+=100;
		if (rotation%4==1) {
			for (int i=0; i<4; i++) {
				int temp = positions[i].x;
				positions[i].x = positions[i].y*-1;
				positions[i].y = temp;
			}
		}
		if (rotation%4==2) {
			for (int i=0; i<4; i++) {
				positions[i].x*=-1;
				positions[i].y*=-1;
			}
		}
		if (rotation%4==3) {
			for (int i=0; i<4; i++) {
				int temp = positions[i].x;
				positions[i].x = positions[i].y;
				positions[i].y = temp*-1;
			}
		}
		x = preview[0].x-positions[0].x;
		y = preview[0].y-positions[0].y;
	}
}