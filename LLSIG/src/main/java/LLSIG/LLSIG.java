package main.java.LLSIG;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import main.java.sig.utils.FileUtils;

public class LLSIG implements KeyListener{
	Player musicPlayer;
	JFrame window;
	Thread gameLoop;
	ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
	int frameCount;
	public static LLSIG game;
	int NOTE_SPEED = 850; //The note speed determines how early you see the note. So lowering this number increases the speed.
	List<Lane> lanes = new ArrayList<Lane>();
	
	String song = "MiChi - ONE-315959669";
	
	final static Dimension WINDOW_SIZE = new Dimension(1280,1050);
	
	public boolean EDITMODE = true;
	public boolean PLAYING = false; //Whether or not a song is loaded and playing.

	public static boolean[] lanePress = new boolean[9]; //A lane is being requested to being pressed.
	public static boolean[] keyState = new boolean[9]; //Whether or not the key is pressed down.

	final static int PERFECT_TIMING_WINDOW = 20;
	final static int EXCELLENT_TIMING_WINDOW = 50;
	final static int GREAT_TIMING_WINDOW = 100;
	final static int BAD_TIMING_WINDOW = 150;
	
	LLSIG(JFrame f) {
		this.window = f;

		for (int i=0;i<9;i++) {
			lanes.add(new Lane(new ArrayList<Note>()));
		}

		PLAYING = new File("music/"+song+".mp3").exists();
		if (PLAYING)  {
			this.musicPlayer = new Player("music/"+song+".mp3");
			musicPlayer.play();
			
			LoadSongData("MiChi - ONE-315959669",lanes);
		}
		Canvas canvas = new Canvas(f.getSize());
		window.add(canvas);
		window.setVisible(true);
		window.addKeyListener(this);
		gameLoop = new Thread() {
			public void run() {
				frameCount++;
				window.repaint();
			}
		};
		
		//SaveSongData("MiChi - ONE-315959669",lanes);
		stpe.scheduleAtFixedRate(gameLoop, 0, 16666666l, TimeUnit.NANOSECONDS);
	}
	
	private void LoadSongData(String song,List<Lane> lanes) {
		try {
			String[] data = FileUtils.readFromFile("music/"+song+".sig");
			for (String line : data) {
				String[] split = line.split(Pattern.quote(","));
				int lane = Integer.parseInt(split[0]);
				NoteType noteType = NoteType.valueOf(split[1]);
				int offset = Integer.parseInt(split[2]);
				int offset2 = -1;
				while (lanes.size()<lane) {
					lanes.add(new Lane(new ArrayList<Note>()));
				}
				if (noteType==NoteType.HOLD) {
					offset2 = Integer.parseInt(split[2]);
					lanes.get(lane-1).addNote(new Note(noteType,offset,offset2));
				} else {
					lanes.get(lane-1).addNote(new Note(noteType,offset));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void SaveSongData(String song,List<Lane> lanes) {
		List<String> data = new ArrayList<String>();
		for (int lane=0;lane<lanes.size();lane++) {
			Lane l = lanes.get(lane);
			int noteCount=0;
			while (l.noteExists(noteCount)) {
				Note n = l.getNote(noteCount++);
				data.add(new StringBuilder().append(lane+1).append(",")
						.append(n.getNoteType().name()).append(",")
						.append(n.getStartFrame()).append(",")
						.append(n.getEndFrame())
						.toString());
			}
		}
		FileUtils.writeToFile(data.toArray(new String[data.size()]),"music/"+song+".sig");
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setSize(WINDOW_SIZE);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new LLSIG(f);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int lane = -1;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_A:{lane=0;}break;
			case KeyEvent.VK_S:{lane=1;}break;
			case KeyEvent.VK_D:{lane=2;}break;
			case KeyEvent.VK_F:{lane=3;}break;
			case KeyEvent.VK_SPACE:{lane=4;}break;
			case KeyEvent.VK_J:{lane=5;}break;
			case KeyEvent.VK_K:{lane=6;}break;
			case KeyEvent.VK_L:{lane=7;}break;
			case KeyEvent.VK_SEMICOLON:{lane=8;}break;
			case KeyEvent.VK_P:{if (LLSIG.game.PLAYING&&musicPlayer.isPaused()) {musicPlayer.resume();} else {musicPlayer.pause();}}break;
			case KeyEvent.VK_Q:{if (LLSIG.game.PLAYING) {musicPlayer.pause();SaveSongData(song,lanes);}}break;
		}
		if (LLSIG.game.PLAYING&&lane!=-1) {
			LLSIG.game.lanes.get(lane).addNote(new Note(NoteType.NORMAL,musicPlayer.getPlayPosition()));
		}
		if (lane!=-1) {
			if (PLAYING) {
				Lane l = lanes.get(lane);
				if (l.noteExists()) {
					Note n = l.getNote();
					int diff = n.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition();
					if (Math.abs(diff)<=PERFECT_TIMING_WINDOW) {l.lastRating=TimingRating.PERFECT;} else
					if (Math.abs(diff)<=EXCELLENT_TIMING_WINDOW) {l.lastRating=TimingRating.EXCELLENT;} else
					if (Math.abs(diff)<=GREAT_TIMING_WINDOW) {l.lastRating=TimingRating.GREAT;} else
					if (Math.abs(diff)<=BAD_TIMING_WINDOW) {l.lastRating=Math.signum(diff)>0?TimingRating.EARLY:TimingRating.LATE;}
					l.lastNote=LLSIG.game.musicPlayer.getPlayPosition();
					n.active=false;
				}
			}
			keyState[lane]=true;
		}
		//System.out.println("Pressed "+e.getKeyChar()+" on frame "+musicPlayer.getPlayPosition());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int lane = -1;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_A:{lane=0;}break;
			case KeyEvent.VK_S:{lane=1;}break;
			case KeyEvent.VK_D:{lane=2;}break;
			case KeyEvent.VK_F:{lane=3;}break;
			case KeyEvent.VK_SPACE:{lane=4;}break;
			case KeyEvent.VK_J:{lane=5;}break;
			case KeyEvent.VK_K:{lane=6;}break;
			case KeyEvent.VK_L:{lane=7;}break;
			case KeyEvent.VK_SEMICOLON:{lane=8;}break;
		}
		if (lane!=-1) {
			keyState[lane]=false;
		}
	}
}
