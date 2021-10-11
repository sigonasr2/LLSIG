package main.java.LLSIG;

public class BeatTiming {
	double offset;
	int bpm;
	boolean active;
	BeatTiming(double offset,int bpm) {
		this.offset=offset;
		this.bpm=bpm;
		this.active=true;
	}
}
