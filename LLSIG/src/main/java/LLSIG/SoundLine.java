package main.java.LLSIG;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioSystem;

public class SoundLine implements LineListener{
	Clip clip;
	AudioInputStream stream;
	
	SoundLine(String sound) {
		try {
			this.clip = AudioSystem.getClip();
			this.stream = AudioSystem.getAudioInputStream(new File(sound).getAbsoluteFile());
			clip.addLineListener(this);
			clip.open(stream);
			clip.start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(LineEvent event) {
		if (event.getType()==Type.STOP) {
			clip.close();
			try {
				stream.close();
				//System.out.println("Freed resources for sound.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
