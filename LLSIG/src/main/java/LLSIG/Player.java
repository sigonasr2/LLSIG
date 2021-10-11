package main.java.LLSIG;
import javazoom.jl.decoder.JavaLayerException;

public class Player {
	JLayerPlayerPausable jlpp;
	String song;
	public Player(String song) {
		this.song=song;
	}
	public void play() {
		new Thread() {
			public void run() {
				try {
					if (jlpp!=null) {jlpp.close();}
					jlpp = new JLayerPlayerPausable(song);
					jlpp.play();
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void play(long frame) {
		new Thread() {
			public void run() {
				try {
					if (jlpp!=null) {jlpp.close();}
					jlpp = new JLayerPlayerPausable(song);
					jlpp.play(frame);
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void pause() {
		jlpp.pause();
	}
	public boolean isPaused() {
		return jlpp.isPaused();
	}
	public void resume() {
		new Thread() {
			public void run() {
				try {
					jlpp.resume();
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void kill() {
		jlpp.close();
	}
	public int getPlayPosition() {
		return jlpp.getPosition();
	}
	public int getFrameIndex() {
		return jlpp.getFrameIndex();
	}
}
