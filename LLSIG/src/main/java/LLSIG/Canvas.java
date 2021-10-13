package main.java.LLSIG;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JPanel;
import java.awt.Point;
import javafx.scene.media.AudioSpectrumListener;

public class Canvas extends JPanel implements AudioSpectrumListener{
	final Color NOTE_COLOR = new Color(196,116,116);
	final Color HOLD_NOTE_COLOR = new Color(64,64,64,160);
	ArrayList<SpectrographBar> spectrograph = new ArrayList<SpectrographBar>();
	
	public Canvas(Dimension size) {
		super();
		this.setSize(size);
		this.setMinimumSize(size);
	}

	public void update() {
		for (int i=0;i<spectrograph.size();i++) {
			SpectrographBar sb = spectrograph.get(i);
			sb.update();
		}
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

			final int SPECTROBAR_SIZE = spectrograph.size()>0?this.getWidth()/spectrograph.size():0;

			for (int i=0;i<spectrograph.size();i++) {
				SpectrographBar sb = spectrograph.get(i);
				sb.draw(g,SPECTROBAR_SIZE*i,SPECTROBAR_SIZE,this.getHeight(),this.getWidth()-SPECTROBAR_SIZE*i);
			}
			g.setColor(Color.WHITE);
			g.drawString(Double.toString(LLSIG.game.musicPlayer.getPlayPosition()),0,32);

			if (LLSIG.game.EDITOR) {
				final int MARGIN_X = 32;
				final int BEAT_RANGE = 6;
				final int BEAT_SPACING = 164;
				final int NOTE_Y=(int)(MIDDLE_Y);
				for (int y=-BEAT_RANGE;y<BEAT_RANGE;y++) {
					g.setColor(Color.GRAY);
					g.fillRect(MARGIN_X,(int)(NOTE_Y+(y-LLSIG.EDITOR_CURSOR_BEAT%1)*BEAT_SPACING)+NOTE_SIZE/2,this.getWidth()-MARGIN_X*2,4);
					g.setColor(Color.DARK_GRAY);
					for (int yy=1;yy<=LLSIG.EDITOR_BEAT_DIVISIONS;yy++) {
						g.fillRect(MARGIN_X*2,(int)(NOTE_Y+(y-LLSIG.EDITOR_CURSOR_BEAT%1)*BEAT_SPACING)+(BEAT_SPACING/LLSIG.EDITOR_BEAT_DIVISIONS)*yy+NOTE_SIZE/2,this.getWidth()-MARGIN_X*4,2);
					}
				}
				for (int i=0;i<9;i++) {
					if (LLSIG.keyState[i]) {
						g.setColor(Color.MAGENTA);
					} else {
						g.setColor(Color.GRAY);
					}
					int NOTE_X=(int)(((this.getWidth()-MARGIN_X)/9)*i+MARGIN_X);
					g.fillOval(NOTE_X,NOTE_Y,NOTE_SIZE,NOTE_SIZE);
					g.setColor(NOTE_COLOR);
					Lane lane = LLSIG.game.lanes.get(i);
					List<Note> notes = lane.noteChart.stream().filter((note)->Math.abs(LLSIG.EDITOR_CURSOR_BEAT-note.beatSnapStart)<BEAT_RANGE||Math.abs(LLSIG.EDITOR_CURSOR_BEAT-note.beatSnapEnd)<BEAT_RANGE).collect(Collectors.toList());
					for (Note n : notes) {
						final int START_Y = (int)(NOTE_Y+(n.beatSnapStart-LLSIG.EDITOR_CURSOR_BEAT)*BEAT_SPACING);
						final int END_Y = (int)(NOTE_Y+(n.beatSnapEnd-LLSIG.EDITOR_CURSOR_BEAT)*BEAT_SPACING);
						if (n.getNoteType()==NoteType.HOLD) {
							Color prevCol = g.getColor();
							g.setColor(HOLD_NOTE_COLOR);
							g.fillOval(NOTE_X,END_Y,NOTE_SIZE,NOTE_SIZE);
							g.setColor(prevCol);
							g.fillRect(NOTE_X,START_Y,NOTE_SIZE,END_Y-START_Y);
						}
						g.fillOval(NOTE_X,START_Y,NOTE_SIZE,NOTE_SIZE);
					}
				}
			} else {
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
							double PLAYTIME_END_RATIO = (1-(((double)n.getEndFrame()-LLSIG.game.musicPlayer.getPlayPosition())/LLSIG.game.NOTE_SPEED));
							if (n.getNoteType()==NoteType.HOLD) {
								if (n.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition()>=LLSIG.game.NOTE_SPEED) {
									//Use the center to draw the connecting note.
									g.fillOval((int)(MIDDLE_X-Math.cos(Math.toRadians(22.5*i))*(0*NOTE_DISTANCE)-NOTE_SIZE/2),(int)(MIDDLE_Y+Math.sin(Math.toRadians(22.5*i))*(0*NOTE_DISTANCE)-NOTE_SIZE/2),
									(int)(Math.min(0/2+0.5,1)*NOTE_SIZE),(int)(Math.min(0/2+0.5,1)*NOTE_SIZE));
								} else {
									Color prevCol = g.getColor();
									g.setColor(HOLD_NOTE_COLOR);
									Point CORNER1 = new Point((int)(MIDDLE_X-Math.cos(Math.toRadians(22.5*i))*(PLAYTIME_END_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2),(int)(MIDDLE_Y+Math.sin(Math.toRadians(22.5*i))*(PLAYTIME_END_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2));
									Point CORNER2 = new Point((int)(MIDDLE_X-Math.cos(Math.toRadians(22.5*i))*(PLAYTIME_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2),(int)(MIDDLE_Y+Math.sin(Math.toRadians(22.5*i))*(PLAYTIME_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2));
									g.fillPolygon(new int[]{CORNER1.x,CORNER1.x+NOTE_SIZE,CORNER2.x+NOTE_SIZE,CORNER2.x}, new int[]{CORNER1.y+NOTE_SIZE/2,CORNER1.y+NOTE_SIZE/2,CORNER2.y+NOTE_SIZE/2,CORNER2.y+NOTE_SIZE/2}, 4);
									g.setColor(prevCol);
									g.fillOval((int)(MIDDLE_X-Math.cos(Math.toRadians(22.5*i))*(PLAYTIME_END_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2),(int)(MIDDLE_Y+Math.sin(Math.toRadians(22.5*i))*(PLAYTIME_END_RATIO*NOTE_DISTANCE)-NOTE_SIZE/2),
											(int)(Math.min(PLAYTIME_END_RATIO/2+0.5,1)*NOTE_SIZE),(int)(Math.min(PLAYTIME_END_RATIO/2+0.5,1)*NOTE_SIZE));
								}
							}
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
	@Override
	public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
		if (spectrograph.size()!=magnitudes.length) {
			spectrograph.clear();
			for (float f : magnitudes) {
				spectrograph.add(new SpectrographBar(f));
			}
		} else {
			for (int i=0;i<magnitudes.length;i++) {
				spectrograph.get(i).update(magnitudes[i]);
			}
		}
	}
}
