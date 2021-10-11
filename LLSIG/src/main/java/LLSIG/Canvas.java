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
		
		g.setFont(LLSIG.gameFont);
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
			g.drawString(Integer.toString(LLSIG.game.musicPlayer.getPlayPosition()),0,32);
			g.drawString(Integer.toString(LLSIG.game.musicPlayer.getFrameIndex()),0,64);
			if (LLSIG.game.BPM_MEASURE) {
				g.drawString("Average BPM: "+LLSIG.approximateBPM(),MIDDLE_X-128,MIDDLE_Y+64);
			} else 
			if (LLSIG.game.METRONOME) {
				g.drawString("Offset: "+LLSIG.testOffset,MIDDLE_X-128,MIDDLE_Y+64);
			} else {
				g.setColor(LLSIG.game.musicPlayer.getPlayPosition()-LLSIG.LAST_PERFECT<500?Color.WHITE:Color.DARK_GRAY);g.drawString("PERFECT: "+LLSIG.PERFECT_COUNT,MIDDLE_X-128,MIDDLE_Y-96);
				g.setColor(LLSIG.game.musicPlayer.getPlayPosition()-LLSIG.LAST_EXCELLENT<500?Color.WHITE:Color.DARK_GRAY);g.drawString("EXCELLENT: "+LLSIG.EXCELLENT_COUNT,MIDDLE_X-128,MIDDLE_Y-64);
				g.setColor(LLSIG.game.musicPlayer.getPlayPosition()-LLSIG.LAST_GREAT<500?Color.WHITE:Color.DARK_GRAY);g.drawString("GREAT: "+LLSIG.GREAT_COUNT,MIDDLE_X-128,MIDDLE_Y-32);
				g.setColor(LLSIG.game.musicPlayer.getPlayPosition()-LLSIG.LAST_EARLY<500?Color.WHITE:Color.DARK_GRAY);g.drawString("EARLY: "+LLSIG.EARLY_COUNT,MIDDLE_X-128,MIDDLE_Y);
				g.setColor(LLSIG.game.musicPlayer.getPlayPosition()-LLSIG.LAST_LATE<500?Color.WHITE:Color.DARK_GRAY);g.drawString("LATE: "+LLSIG.LATE_COUNT,MIDDLE_X-128,MIDDLE_Y+32);
				g.setColor(LLSIG.game.musicPlayer.getPlayPosition()-LLSIG.LAST_MISS<500?Color.WHITE:Color.DARK_GRAY);g.drawString("MISS: "+LLSIG.MISS_COUNT,MIDDLE_X-128,MIDDLE_Y+64);
			}
			g.setColor(Color.WHITE); 
			String comboString = "x"+LLSIG.COMBO+" combo";
			Rectangle2D bounds = calculateStringBoundsFont(comboString, g.getFont());
			g.drawString(comboString,(int)(MIDDLE_X-bounds.getCenterX()),MIDDLE_Y+164);
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
				g.setColor(NOTE_COLOR);
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
