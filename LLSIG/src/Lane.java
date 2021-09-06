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
	public boolean noteExists() {
		return noteExists(0);
	}
	public boolean noteExists(int noteOffset) {
		return currentNoteIndex+noteOffset<noteChart.size();
	}
	public Note getNote(int noteOffset) throws IndexOutOfBoundsException {
		return noteChart.get(currentNoteIndex+noteOffset);
	}
	public Note getNote() {
		return getNote(0);
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
