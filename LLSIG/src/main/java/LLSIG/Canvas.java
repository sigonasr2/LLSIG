package main.java.LLSIG;
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
		final int MIDDLE_Y = this.getHeight()/8;
		final int NOTE_DISTANCE = 128;
		final int JUDGEMENT_LINE_WIDTH = 64;
		final int JUDGEMENT_LINE_HEIGHT = 4;
		final int NOTE_SIZE = 16;
		final int LANE_SPACING_X = 100;
		
		super.paintComponent(g);
		if (LLSIG.game!=null) {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,this.getWidth(),this.getHeight());
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(LLSIG.game.frameCount),0,16);
			
			for (int i=0;i<9;i++) {
				int LANE_X_OFFSET = (i-5)*LANE_SPACING_X+LANE_SPACING_X/2+JUDGEMENT_LINE_WIDTH/2;
				
				g.setColor(Color.GRAY);
				//g.fillRect(MIDDLE_X-JUDGEMENT_LINE_WIDTH/2+LANE_X_OFFSET,MIDDLE_Y-JUDGEMENT_LINE_HEIGHT/2,JUDGEMENT_LINE_WIDTH,JUDGEMENT_LINE_HEIGHT);
				g.fillOval((int)(MIDDLE_X-Math.cos(Math.toRadians(22.5*i))-16),(int)(MIDDLE_Y+Math.sin(Math.toRadians(22.5*i))-16),32,32);
				g.setColor(NOTE_COLOR);
				
				Lane lane = LLSIG.game.lanes.get(i);
				int noteCounter = 0;
				while (lane.noteExists(noteCounter)) {
					Note n = lane.getNote(noteCounter);
					int NOTE_Y_OFFSET = (int)((((double)LLSIG.game.musicPlayer.getPlayPosition()-n.getStartFrame())/1000)*60*LLSIG.game.noteSpeed);
					g.fillOval(MIDDLE_X-NOTE_SIZE/2+LANE_X_OFFSET,MIDDLE_Y-NOTE_SIZE/2+NOTE_Y_OFFSET,NOTE_SIZE,NOTE_SIZE);
					noteCounter++;
				}
			}
		}
	}
}