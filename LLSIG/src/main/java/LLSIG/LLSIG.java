package main.java.LLSIG;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import javazoom.jl.decoder.JavaLayerException;
import main.java.sig.utils.FileUtils;

public class LLSIG implements KeyListener{
	Player musicPlayer;
	JFrame window;
	Thread gameLoop;
	ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
	int frameCount;
	public static LLSIG game;
	int noteSpeed = 4;
	List<Lane> lanes = new ArrayList<Lane>();
	
	String song = "MiChi - ONE-315959669";
	
	final static Dimension WINDOW_SIZE = new Dimension(1024,800);
	
	public boolean EDITMODE = true;
	
	LLSIG(JFrame f) {
		this.window = f;
		this.musicPlayer = new Player("music/"+song+".mp3");
		musicPlayer.play();
		
		for (int i=0;i<9;i++) {
			lanes.add(new Lane(new ArrayList<Note>()));
		}
		
		LoadSongData("MiChi - ONE-315959669",lanes);
		
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
			case KeyEvent.VK_P:{if (musicPlayer.isPaused()) {musicPlayer.resume();} else {musicPlayer.pause();}}break;
			case KeyEvent.VK_Q:{musicPlayer.pause();SaveSongData("music/"+song+".sig",lanes);}break;
		}
		if (lane!=-1) {
			LLSIG.game.lanes.get(lane).addNote(new Note(NoteType.NORMAL,musicPlayer.getPlayPosition()));
		}
		//System.out.println("Pressed "+e.getKeyChar()+" on frame "+musicPlayer.getPlayPosition());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}