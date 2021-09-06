import javazoom.jl.decoder.JavaLayerException;

public class Player {
	JLayerPlayerPausable jlpp;
	String song;
	public Player(String song) {
		this.song=song;
		try {
			jlpp = new JLayerPlayerPausable(song);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	public void play() {
		new Thread() {
			public void run() {
				try {
					jlpp.play();
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	public void pause() {
		jlpp.pause();
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
}
