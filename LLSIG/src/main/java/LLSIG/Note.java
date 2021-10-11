package main.java.LLSIG;

public class Note {
	NoteType type;
	double start,end;
	boolean active=true; //Set to false when the note has been scored.
	double beatSnapStart,beatSnapEnd = -1;
	public Note(NoteType type,double start,double end) {
		this.type=type;
		this.start=start;
		this.end=end;
	}
	public Note(NoteType type,double start) {
		this(type,start,-1);
	}
	public NoteType getNoteType() {
		return type;
	}
	public void setNoteType(NoteType type) {
		this.type = type;
	}
	public double getStartFrame() {
		return start;
	}
	public void setStartFrame(double start) {
		this.start = start;
	}
	public double getEndFrame() {
		return end;
	}
	public void setEndFrame(double end) {
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
