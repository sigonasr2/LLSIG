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
			LLSIG.updateMultipleNoteMarkers();
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
				for (Lane l : LLSIG.game.lanes) {
					if (l!=this) {
						for (Note nn : l.noteChart) {
							if (nn.start==n.start) {
								if (!n.multiple&&!nn.multiple) {LLSIG.lastHold=!LLSIG.lastHold;}
								nn.multiple=true;
								n.multiple=true;
								n.multiple_col=nn.multiple_col=LLSIG.lastHold;
							}
							if (n.getNoteType()==NoteType.HOLD) {
								if (nn.getNoteType()==NoteType.HOLD) {
									if (n.start==nn.end) {
										if (!n.multiple&&!nn.multiple2) {LLSIG.lastHold=!LLSIG.lastHold;}
										nn.multiple2=true;
										n.multiple=true;
										n.multiple_col=nn.multiple2_col=LLSIG.lastHold;
									}
									if (n.end==nn.start) {
										if (!n.multiple2&&!nn.multiple) {LLSIG.lastHold=!LLSIG.lastHold;}
										nn.multiple=true;
										n.multiple2=true;
										n.multiple2_col=nn.multiple_col=LLSIG.lastHold;
									} else
									if (n.end==nn.end) {
										if (!n.multiple2&&!nn.multiple2) {LLSIG.lastHold=!LLSIG.lastHold;}
										nn.multiple2=true;
										n.multiple2=true;
										n.multiple2_col=nn.multiple2_col=LLSIG.lastHold;
									}
								} else {
									if (n.end==nn.start) {
										if (!n.multiple2&&!nn.multiple) {LLSIG.lastHold=!LLSIG.lastHold;}
										nn.multiple=true;
										n.multiple2=true;
										n.multiple2_col=nn.multiple_col=LLSIG.lastHold;
									}
								}
							}
						}
					}
				}
				if (!added) {
					noteChart.add(n);
				}
				System.out.println("Note added: "+n);
		}
		if (addQueue.size()>0) {
			LLSIG.updateMultipleNoteMarkers();
		}
		addQueue.clear();
	}
	public void markMissedNotes(int laneNumber) {
		if (LLSIG.game.PLAYING) {
			int noteCounter=LLSIG.noteCounter[laneNumber];
			while (noteExists(noteCounter)) {
				Note note = getNote(noteCounter);
				double diff = note.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition();
				double diff2 = note.getEndFrame()-LLSIG.game.musicPlayer.getPlayPosition();
				if (note.getNoteType()==NoteType.HOLD&&note.active2&&!note.active&&diff2<-LLSIG.BAD_TIMING_WINDOW) {
					LLSIG.noteCounter[laneNumber]++;
					note.active2=false;
					lastRating = TimingRating.MISS;
					lastNote = LLSIG.game.musicPlayer.getPlayPosition();
					LLSIG.COMBO=0;
					LLSIG.MISS_COUNT++;
					LLSIG.LAST_MISS=LLSIG.game.musicPlayer.getPlayPosition();
				}
				if (note.active&&diff<-LLSIG.BAD_TIMING_WINDOW) {
					LLSIG.noteCounter[laneNumber]++;
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
				if ((note.active||note.active2)&&diff>LLSIG.game.NOTE_SPEED) {
					break;
				}
				noteCounter++;
			}
		}
	}
}
