package main.java.LLSIG;
import java.util.List;

public class Lane{
	List<Note> noteChart;
	int currentNoteIndex = 0;
	TimingRating lastRating = TimingRating.MISS;
	double lastNote = -1;
	public Lane(List<Note> noteChart) {
		super();
		this.noteChart = noteChart;
	}
	public boolean endOfChart() {
		return currentNoteIndex==noteChart.size()-1;
	} 
	public void clearOutInactiveNotes() {
		noteChart.removeIf(note->!note.active);
	}
	public boolean noteExists() {
		return getNote()!=null;
	}
	public boolean noteExists(int noteOffset) {
		return getNote(noteOffset)!=null;
	}
	public Note getNote(int noteOffset) throws IndexOutOfBoundsException {
		for (int i=noteOffset;i<noteChart.size();i++)
		{
			Note n = noteChart.get(i);
			if (n.active) {return n;}
		}
		return null;
	}
	public Note getNote() {
		for (int i=0;i<noteChart.size();i++)
		{
			Note n = getNote(i);
			if (n.active) {return n;}
		}
		return null;
	}
	public void addNote(Note n) {
		addNote(n,false);
	}
	public void addNote(Note n,boolean performReorderingOfList) {
		if (performReorderingOfList) {
			boolean added=false;
			for (int i=0;i<noteChart.size();i++) {
				Note nn = noteChart.get(i);
				if (nn.start>n.start) {
					noteChart.add(i,n);
					added=true;
					break;
				}
			}
			if (!added) {
				noteChart.add(n);
			}
		} else {
			noteChart.add(n);
		}
		System.out.println("Note added: "+n);
	}
	public void markMissedNotes() {
		if (LLSIG.game.PLAYING) {
			noteChart.forEach((note)->{
				double diff = note.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition();
				if (diff<-LLSIG.BAD_TIMING_WINDOW) {
					note.active=false;
					lastRating = TimingRating.MISS;
					lastNote = LLSIG.game.musicPlayer.getPlayPosition();
					LLSIG.COMBO=0;
					LLSIG.MISS_COUNT++;
					LLSIG.LAST_MISS=LLSIG.game.musicPlayer.getPlayPosition();
				}
			});
		}
	}
}
