package main.java.LLSIG;
import java.util.ArrayList;
import java.util.List;

public class Lane{
	List<Note> noteChart;
	List<Note> addQueue = new ArrayList<Note>();
	int currentNoteIndex = 0;
	TimingRating lastRating = TimingRating.MISS;
	double lastNote = -1;
	Note lastNoteAdded;
	boolean keyPressed=false;
	public Lane(List<Note> noteChart) {
		super();
		this.noteChart = noteChart;
	}
	public boolean endOfChart() {
		return currentNoteIndex==noteChart.size()-1;
	} 
	public void clearOutInactiveNotes() {
		noteChart.removeIf(note->note.deleted||(!note.active&&!note.active2));
	}
	public void clearOutDeletedNotes() {
		if (noteChart.removeIf(note->note.deleted)) {
			System.out.println("Deleted note from "+this);
		}
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
			if (n.active||n.active2) {return n;}
		}
		return null;
	}
	public Note getNote() {
		for (int i=0;i<noteChart.size();i++)
		{
			Note n = getNote(i);
			if (n!=null&&(n.active||n.active2)) {return n;}
		}
		return null;
	}
	public void addNote(Note n) {
		addNote(n,false);
	}
	public void addNote(Note n,boolean performReorderingOfList) {
		addQueue.add(n);
	}
	
	public void addFromQueue() {
		for (Note n : addQueue) {
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
				System.out.println("Note added: "+n);
		}
		addQueue.clear();
	}
	public void markMissedNotes() {
		if (LLSIG.game.PLAYING) {
			noteChart.forEach((note)->{
				double diff = note.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition();
				double diff2 = note.getEndFrame()-LLSIG.game.musicPlayer.getPlayPosition();
				if (note.getNoteType()==NoteType.HOLD&&note.active2&&!note.active&&diff2<-LLSIG.BAD_TIMING_WINDOW) {
					note.active2=false;
					lastRating = TimingRating.MISS;
					lastNote = LLSIG.game.musicPlayer.getPlayPosition();
					LLSIG.COMBO=0;
					LLSIG.MISS_COUNT++;
					LLSIG.LAST_MISS=LLSIG.game.musicPlayer.getPlayPosition();
				}
				if (note.active&&diff<-LLSIG.BAD_TIMING_WINDOW) {
					note.active=false;
					if (note.getNoteType()==NoteType.HOLD) {
						note.active2=false; //Count as a double miss, since we missed the first part.
						LLSIG.MISS_COUNT++;
					}
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
