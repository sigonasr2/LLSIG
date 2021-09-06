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

import javax.swing.JFrame;

import javazoom.jl.decoder.JavaLayerException;

public class LLSIG implements KeyListener{
	Player musicPlayer;
	JFrame window;
	Thread gameLoop;
	ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
	int frameCount;
	public static LLSIG game;
	int noteSpeed = 4;
	List<Lane> lanes = new ArrayList<Lane>();
	
	LLSIG(JFrame f) {
		this.window = f;
		this.musicPlayer = new Player("music/MiChi - ONE-315959669.mp3");
		musicPlayer.play();
		
		lanes.add(new Lane(Arrays.asList(new Note[] {
				new Note(NoteType.NORMAL,1000),
				new Note(NoteType.NORMAL,2000),
				new Note(NoteType.NORMAL,3000),
				new Note(NoteType.NORMAL,4000),
		})));
		
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
		stpe.scheduleAtFixedRate(gameLoop, 0, 16666666l, TimeUnit.NANOSECONDS);
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setSize(640, 640);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new LLSIG(f);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Pressed "+e.getKeyChar()+" on frame "+musicPlayer.getPlayPosition());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
