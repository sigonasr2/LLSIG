package main.java.LLSIG;
import java.util.ArrayList;
import java.util.List;

public class Lane{
	List<Note> noteChart;
	int currentNoteIndex = 0;
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
			Note n = getNote(i);
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
}
