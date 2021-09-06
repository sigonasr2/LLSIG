import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Canvas extends JPanel{
	final Color NOTE_COLOR = new Color(196,116,116);
	
	public Canvas(Dimension size) {
		super();
		this.setSize(size);
		this.setMinimumSize(size);
	}
	public void paintComponent(Graphics g) {
		
		final int MIDDLE_X = this.getWidth()/2;
		final int MIDDLE_Y = this.getHeight()/2;
		final int JUDGEMENT_LINE_WIDTH = 64;
		final int JUDGEMENT_LINE_HEIGHT = 4;
		final int NOTE_SIZE = 16;
		
		super.paintComponent(g);
		if (LLSIG.game!=null) {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,this.getWidth(),this.getHeight());
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(LLSIG.game.frameCount),0,16);
			
			g.setColor(Color.GRAY);
			g.fillRect(MIDDLE_X-JUDGEMENT_LINE_WIDTH/2,MIDDLE_Y-JUDGEMENT_LINE_HEIGHT/2,JUDGEMENT_LINE_WIDTH,JUDGEMENT_LINE_HEIGHT);
			
			g.setColor(NOTE_COLOR);
			int noteCounter = 0;
			Lane lane1 = LLSIG.game.lanes.get(0);
			while (lane1.noteExists(noteCounter)) {
				Note n = lane1.getNote(noteCounter);
				int NOTE_Y_OFFSET = (int)((((double)LLSIG.game.musicPlayer.getPlayPosition()-n.getStartFrame())/1000)*60*LLSIG.game.noteSpeed);
				g.fillOval(MIDDLE_X-NOTE_SIZE/2,MIDDLE_Y-NOTE_SIZE/2+NOTE_Y_OFFSET,NOTE_SIZE,NOTE_SIZE);
				noteCounter++;
			}
		}
	}
}
