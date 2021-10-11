package main.java.LLSIG;

public class BeatTiming {
	long offset;
	int bpm;
	boolean active;
	BeatTiming(long offset,int bpm) {
		this.offset=offset;
		this.bpm=bpm;
		this.active=true;
	}
}
