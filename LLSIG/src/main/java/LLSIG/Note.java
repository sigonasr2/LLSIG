package main.java.LLSIG;

public class Note {
	NoteType type;
	double start,end;
	boolean active=true; //Set to false when the note has been scored.
	boolean active2=false; //Set to false when the end section of the note has been scored.
	double beatSnapStart,beatSnapEnd = -1;
	boolean deleted=false; //Set this marker to delete it on the next frame (when using the editor)
	boolean multiple=false; //Whether or not to display an indicator showing this is a multi-press note.
	boolean multiple2=false; //Whether or not to display an indicator showing this is a multi-press note for the ending of the hold.
	boolean multiple_col=false; //If true, use the secondary color.
	boolean multiple2_col=false; //If true, use the secondary color.
	public Note(NoteType type,double start,double end) {
		this.type=type;
		this.start=start;
		this.end=end;
		if (type==NoteType.HOLD) {
			this.active2=true;
		}
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
	public void markForDeletion() {
		deleted=true;
	}
	@Override
	public String toString() {
		return "Note [type=" + type + ", start=" + start + ", end=" + end + "]";
	}
}
