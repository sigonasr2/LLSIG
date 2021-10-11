package main.java.LLSIG;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class Player {
	MediaPlayer jlpp;
	Media song;
	public Player(String song) {
		this.song=new Media(song);
		jlpp = new MediaPlayer(this.song);
	}
	public void play() {
		new Thread() {
			public void run() {
				jlpp.stop();
				jlpp.play();
			}
		}.start();
	}
	public void play(long frame) {
		new Thread() {
			public void run() {
				jlpp.stop();
				jlpp.play();
				jlpp.seek(new Duration(frame));
			}
		}.start();
	}
	public void pause() {
		jlpp.pause();
	}
	public boolean isPaused() {
		return jlpp.getStatus()==Status.PAUSED;
	}
	public void resume() {
		new Thread() {
			public void run() {
				jlpp.play();
			}
		}.start();
	}
	public void kill() {
		jlpp.dispose();
	}
	public double getPlayPosition() {
		return jlpp.getCurrentTime().toMillis();
	}
}
