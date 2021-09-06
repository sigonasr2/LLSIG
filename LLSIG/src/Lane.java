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
	public void consumeNote() {
		currentNoteIndex = Math.min(currentNoteIndex+1,noteChart.size()-1);
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
}
