package main.java.LLSIG;
import javafx.scene.media.AudioSpectrumListener;
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
		jlpp.stop();
		jlpp.play();
	}
	public void play(long frame) {
		jlpp.play();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.seek(frame);
	}
	public void pause() {
		jlpp.pause();
	}
	public boolean isPaused() {
		return jlpp.getStatus()==Status.PAUSED;
	}
	public void resume() {
		jlpp.play();
	}
	public void kill() {
		jlpp.dispose();
	}
	public double getPlayPosition() {
		return jlpp.getCurrentTime().toMillis();
	}
	public void seek(long frame) {
		jlpp.seek(new Duration(frame));
	}
	public void setAudioSpectrumListener(AudioSpectrumListener listener) {
		jlpp.setAudioSpectrumListener(listener);
	}
}
