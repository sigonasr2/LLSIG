package main.java.LLSIG;

public class Note {
	NoteType type;
	int start,end;
	boolean active=true; //Set to false when the note has been scored.
	double beatSnapStart,beatSnapEnd = -1;
	public Note(NoteType type,int start,int end) {
		this.type=type;
		this.start=start;
		this.end=end;
	}
	public Note(NoteType type,int start) {
		this(type,start,-1);
	}
	public NoteType getNoteType() {
		return type;
	}
	public void setNoteType(NoteType type) {
		this.type = type;
	}
	public int getStartFrame() {
		return start;
	}
	public void setStartFrame(int start) {
		this.start = start;
	}
	public int getEndFrame() {
		return end;
	}
	public void setEndFrame(int end) {
		this.end = end;
	}
	public void setBeatSnap(double value) {
		this.beatSnapStart=value;
	}
	public double getBeatSnap() {
		return beatSnapStart;
	}
	public void setBeatSnapEnd(double value) {
		this.beatSnapEnd=value;
	}
	public double getBeatSnapEnd() {
		return beatSnapEnd;
	}
	@Override
	public String toString() {
		return "Note [type=" + type + ", start=" + start + ", end=" + end + "]";
	}
}
