package main.java.LLSIG;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.Font;

import javax.swing.JPanel;

public class Canvas extends JPanel{
	final Color NOTE_COLOR = new Color(196,116,116);
	
	public Canvas(Dimension size) {
		super();
		this.setSize(size);
		this.setMinimumSize(size);
	}
	public Rectangle2D calculateStringBoundsFont(String msg, Font font) {
		FontRenderContext frc = this.getFontMetrics(font).getFontRenderContext();
		return font.getStringBounds(msg, frc);
	}
	public void paintComponent(Graphics g) {
		
		final int MIDDLE_X = this.getWidth()/2;
		final int MIDDLE_Y = this.getHeight()-(this.getWidth()/2);
		final int JUDGEMENT_LINE_WIDTH = 64;
		final int JUDGEMENT_LINE_HEIGHT = 4;
		final int NOTE_SIZE = 96;
		final int LANE_SPACING_X = 100;
		final int NOTE_DISTANCE = (int)(LLSIG.WINDOW_SIZE.width/2)-NOTE_SIZE;
		final Color[] colorList = new Color[]{Color.BLACK,Color.BLUE,Color.CYAN,Color.DARK_GRAY,Color.GRAY,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};
		
		super.paintComponent(g);
		if (LLSIG.game!=null) {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,this.getWidth(),this.getHeight());
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(LLSIG.game.frameCount),0,16);
			
			for (int i=0;i<9;i++) {
				int LANE_X_OFFSET = (i-5)*LANE_SPACING_X+LANE_SPACING_X/2+JUDGEMENT_LINE_WIDTH/2;
				
				if (LLSIG.keyState[i]) {
					g.setColor(Color.MAGENTA);
				} else {
					g.setColor(Color.GRAY);
				}
				//g.fillRect(MIDDLE_X-JUDGEMENT_LINE_WIDTH/2+LANE_X_OFFSET,MIDDLE_Y-JUDGEMENT_LINE_HEIGHT/2,JUDGEMENT_LINE_WIDTH,JUDGEMENT_LINE_HEIGHT);
				int NOTE_X=(int)(MIDDLE_X-Math.cos(Math.toRadians(22.5*i))*NOTE_DISTANCE-NOTE_SIZE/2);
				int NOTE_Y=(int)(MIDDLE_Y+Math.sin(Math.toRadians(22.5*i))*NOTE_DISTANCE-NOTE_SIZE/2);
				g.fillOval(NOTE_X,NOTE_Y,NOTE_SIZE,NOTE_SIZE);
				g.setColor(NOTE_COLOR);
				
				Lane lane = LLSIG.game.lanes.get(i);
				if (LLSIG.game.PLAYING) {
					if (LLSIG.game.musicPlayer.getPlayPosition()-lane.lastNote<500) {
						switch (lane.lastRating) {
							case PERFECT:
								g.setColor(colorList[(LLSIG.game.frameCount/10)%colorList.length]);
								break;
							case EXCELLENT:
								g.setColor(Color.YELLOW);
								break;
							case GREAT:
								g.setColor(Color.GREEN);
								break;
							case EARLY:
								g.setColor(Color.CYAN);
								break;
							case LATE:
								g.setColor(Color.MAGENTA);
								break;
							case MISS:
								g.setColor(Color.RED);
								break;
							default:
								break;
						}
						Rectangle2D textBounds = calculateStringBoundsFont(lane.lastRating.name(), g.getFont());
						g.drawString(lane.lastRating.name(),(int)(NOTE_X-textBounds.getCenterX()),(int)(NOTE_Y+textBounds.getHeight()));
					}
				}
				int noteCounter = 0;
				while (lane.noteExists(noteCounter)) {
					Note n = lane.getNote(noteCounter);
					if (n.active) {
						double PLAYTIME_RATIO = (1-(((double)n.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition())/LLSIG.game.NOTE_SPEED));
						if (n.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition()<LLSIG.game.NOTE_SPEED) {
							g.fillOval((int)(MIDDLE_X-Math.cos(Math.toRadians(22.5*i))*(PLAYTIME_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2),(int)(MIDDLE_Y+Math.sin(Math.toRadians(22.5*i))*(PLAYTIME_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2),
									(int)(Math.min(PLAYTIME_RATIO/2+0.5,1)*NOTE_SIZE),(int)(Math.min(PLAYTIME_RATIO/2+0.5,1)*NOTE_SIZE));
						}
					}
					noteCounter++;
				}
			}
		}
	}
}
