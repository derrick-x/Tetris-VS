import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

public class FileManager {
	static final File[] musicList = new File("music").listFiles();
	static Clip music;
	static int currMusic = -1;
	public static void playSound(String name) {
		if (!Main.sound) {
			return;
		}
		File file = new File("soundeffects\\"+name+".wav");
		if (!Main.watchReplay) {
			Replay.sounds.add(name);
		}
		try {
			Clip sound = AudioSystem.getClip();
			sound.open(AudioSystem.getAudioInputStream(file));
			sound.start();
		} catch (Exception e) {
			System.out.println("Exception");
		}
	}
	public static void playMusic(String name) {
		music.stop();
		try {
			music = AudioSystem.getClip();
			music.open(AudioSystem.getAudioInputStream(new File(name+".wav")));
			music.start();
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
	}
	public static void doMusic() {
		if (musicList.length==0||!Main.music) {
			return;
		}
		if (music!=null&&music.isActive()) {
			return;
		}
		if (currMusic==-1) {
			currMusic = (int)(Math.random()*musicList.length);
		} else {
			currMusic+=(int)(Math.random()*(musicList.length-1))+1; //Ensures same music is not played twice
			currMusic%=musicList.length;
		}
		File file = musicList[currMusic];
		try {
			music = AudioSystem.getClip();
			music.open(AudioSystem.getAudioInputStream(file));
			music.start();
		} catch (Exception e) {
			System.out.println("Exception");
		}
	}
	public static Image getImage(String name) {
		return Toolkit.getDefaultToolkit().getImage("images\\"+name+".jpg");
	}
}
