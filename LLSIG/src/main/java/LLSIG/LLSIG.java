package main.java.LLSIG;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.nio.file.Paths;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import main.java.sig.utils.FileUtils;

import javafx.application.Platform;

public class LLSIG implements KeyListener,MouseWheelListener{
	Player musicPlayer;
	JFrame window;
	Thread gameLoop;
	ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1);
	int frameCount;
	public static LLSIG game;
	public static Font gameFont = new Font("Century Gothic",Font.BOLD,32);
	public static int bpm = 120;
	public static double offset = 0;
	public static double testOffset = 0;
	public static double beatDelay = ((1/((double)bpm/60))*1000);
	public static boolean lastHold = false; //A toggle. Decides primary/secondary color marking.
	
	public static List<Long> beats = new ArrayList<Long>();
	
	int NOTE_SPEED = 750; //The note speed determines how early you see the note. So lowering this number increases the speed.
	List<Lane> lanes = new ArrayList<Lane>();
	List<BeatTiming> timings = new ArrayList<BeatTiming>();
	
	String song = "MiChi-ONE";
	
	final static Dimension WINDOW_SIZE = new Dimension(1280,1050);
	
	public boolean EDITMODE = false;
	public boolean METRONOME = false;
	public boolean BPM_MEASURE = false;
	public boolean PLAYING = true; //Whether or not a song is loaded and playing.
	public boolean EDITOR = false; //Whether or not we are in beatmap editing mode.
	public boolean HOLDING_CTRL_KEY = false;

	public static double EDITOR_CURSOR_BEAT = 0;
	public static double PREVIOUS_CURSOR_BEAT = 0;
	public static int EDITOR_BEAT_DIVISIONS = 4;

	public static int beatNumber = 0;

	public static boolean[] lanePress = new boolean[9]; //A lane is being requested to being pressed.
	public static boolean[] keyState = new boolean[9]; //Whether or not the key is pressed down.

	final static int PERFECT_TIMING_WINDOW = 20;
	final static int EXCELLENT_TIMING_WINDOW = 50;
	final static int GREAT_TIMING_WINDOW = 100;
	final static int BAD_TIMING_WINDOW = 150;
	
	public static int PERFECT_COUNT = 0;
	public static int EXCELLENT_COUNT = 0;
	public static int GREAT_COUNT = 0;
	public static int EARLY_COUNT = 0;
	public static int LATE_COUNT = 0;
	public static int MISS_COUNT = 0;
	public static double LAST_PERFECT = 0;
	public static double LAST_EXCELLENT = 0;
	public static double LAST_GREAT = 0;
	public static double LAST_EARLY = 0;
	public static double LAST_LATE = 0;
	public static double LAST_MISS = 0;
	public static int COMBO = 0;

	public final static int NOTE_RECORD_BEAT_SNAP_MULTIPLE = 4; //How many beat divisions max we can snap to.
	
	public final static long TIMEPERTICK = 16666667l;
    public static double DRAWTIME=0;
	
	public static Clip metronome_click1,metronome_click2,clap;
	
	LLSIG(JFrame f) {
		
		Platform.startup(()->{});
		
		this.window = f;
		
		AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new File("se/metronome_click1.wav").getAbsoluteFile());
			try {
				metronome_click1 = AudioSystem.getClip();
				metronome_click1.open(audioInputStream);
				audioInputStream = AudioSystem.getAudioInputStream(new File("se/metronome_click2.wav").getAbsoluteFile());
				metronome_click2 = AudioSystem.getClip();
				metronome_click2.open(audioInputStream);
				audioInputStream = AudioSystem.getAudioInputStream(new File("se/clap.wav").getAbsoluteFile());
				clap = AudioSystem.getClip();
				clap.open(audioInputStream);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i=0;i<9;i++) {
			lanes.add(new Lane(new ArrayList<Note>()));
		}

		PLAYING = new File("music/"+song+".mp3").exists();
		if (PLAYING)  {
			this.musicPlayer = new Player(Paths.get("music/"+song+".mp3").toUri().toString());
			if (!EDITOR) {
				musicPlayer.play();
			}
			
			LoadSongData(song,lanes);
		}
		Canvas canvas = new Canvas(f.getSize());
		musicPlayer.setAudioSpectrumListener(canvas);
		window.add(canvas);
		window.setVisible(true);
		window.addKeyListener(this);
		window.addMouseWheelListener(this);
		
		new Thread() {
            public void run(){
                while (true) {
                    long startTime = System.nanoTime();
                    frameCount++;
					canvas.update();
					if (PLAYING&&EDITOR) {
						for (int i=0;i<9;i++) {
							Lane l =lanes.get(i);
							l.clearOutDeletedNotes();
						}
						if (!musicPlayer.isPaused()) {
							EDITOR_CURSOR_BEAT = (musicPlayer.getPlayPosition()-offset)/beatDelay;
							for (int i=0;i<9;i++) {
								Lane lane = LLSIG.game.lanes.get(i);
								if (lane.noteExists()) {
									Note n = lane.getNote();
									if (n.getNoteType()==NoteType.HOLD&&n.active2&&n.getBeatSnapEnd()<=EDITOR_CURSOR_BEAT) {
										clap.setFramePosition(0);
										clap.start();
										n.active2=false;
									} else
									if (n.active&&n.getBeatSnap()<=EDITOR_CURSOR_BEAT) {
										clap.setFramePosition(0);
										clap.start();
										n.active=false;
									}
								}
							}
						}
					} else
    				if (PLAYING) {
						for (int i=0;i<9;i++) {
							Lane l =lanes.get(i);
							l.markMissedNotes();
							/*if (!EDITMODE) {
								l.clearOutInactiveNotes();
							}*/
							l.clearOutDeletedNotes();
							l.addFromQueue();
						}
    					for (BeatTiming bt : timings) {
    						if (bt.active&&musicPlayer.getPlayPosition()>=bt.offset&&bt.offset>offset) {
    							bt.active=false;
    							bpm=bt.bpm;
    							offset=bt.offset;
    							beatDelay = ((1/((double)bpm/60))*1000);
								beatNumber=0;
    							System.out.println("BPM is "+bpm+". Delay is "+beatDelay);
    						}
    					}
    					if (METRONOME) {
    						if (beatNumber*beatDelay+offset<musicPlayer.getPlayPosition()) {
    							beatNumber++;
    							if (beatNumber%4==0) {
    								metronome_click1.setFramePosition(0);
    								metronome_click1.start();
    							} else {
    								metronome_click2.setFramePosition(0);
    								metronome_click2.start();
    							}
    						}
    					}
    				}
                    window.repaint();
                    long endTime = System.nanoTime();
                    long diff = endTime-startTime;
                    if (diff>TIMEPERTICK) { //Took longer than 1/60th of a second. No sleep.
                        System.err.println("Frame Drawing took longer than "+TIMEPERTICK+"ns to calculate ("+diff+"ns total)!");
                    } else {
                        try {
                            long sleepTime = TIMEPERTICK - diff;
                            long millis = (sleepTime)/1000000;
                            int nanos = (int)(sleepTime-(((sleepTime)/1000000)*1000000));
                            //System.out.println("FRAME DRAWING: Sleeping for ("+millis+"ms,"+nanos+"ns) - "+(diff)+"ns");
                            DRAWTIME = (double)diff/1000000;
                            f.setTitle("Game Loop: "+DRAWTIME+"ms");
                            Thread.sleep(millis,nanos);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
	}
	
	private void LoadSongData(String song,List<Lane> lanes) {
		lanes.clear();
		for (int i=0;i<9;i++) {
			lanes.add(new Lane(new ArrayList<Note>()));
		}
		timings.clear();
		try {
			String[] data = FileUtils.readFromFile("music/"+song+".sig");
			for (String line : data) {
				String[] split = line.split(Pattern.quote(","));
				if (split[0].length()>0) {
					if (split[0].equals("B")) {
						offset=Double.parseDouble(split[1]);
						bpm=Integer.parseInt(split[2]);
						beatDelay = ((1/((double)bpm/60))*1000);
						timings.add(new BeatTiming(offset,bpm));
					} else {
						int lane = Integer.parseInt(split[0]);
						NoteType noteType = NoteType.valueOf(split[1]);
						int offset = (int)Math.round(Double.parseDouble(split[2])*beatDelay+LLSIG.offset);
						int offset2 = -1;
						while (lanes.size()<lane) {
							lanes.add(new Lane(new ArrayList<Note>()));
						}
						if (noteType==NoteType.HOLD) {
							offset2 = (int)Math.round(Double.parseDouble(split[3])*beatDelay+LLSIG.offset);
							Note n = new Note(noteType,offset,offset2);
							n.beatSnapStart = Double.parseDouble(split[2]);
							n.beatSnapEnd = Double.parseDouble(split[3]);
							lanes.get(lane-1).addNote(n);
						} else {
							Note n = new Note(noteType,offset);
							n.beatSnapStart = Double.parseDouble(split[2]);
							lanes.get(lane-1).addNote(n);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void SaveSongData(String song,List<Lane> lanes) {
		List<String> data = new ArrayList<String>();
		for (int i=0;i<timings.size();i++) {
			BeatTiming bt = timings.get(i);
			data.add(new StringBuilder().append("B").append(",")
					.append(bt.offset).append(",")
					.append(bt.bpm)
					.toString());
		}
		for (int lane=0;lane<lanes.size();lane++) {
			Lane l = lanes.get(lane);
			int noteCount=0;
			for (Note n : l.noteChart) {
				n.active=true;
			}
			while (l.noteExists(noteCount)) {
				Note n = l.getNote(noteCount++);
				data.add(new StringBuilder().append(lane+1).append(",")
						.append(n.getNoteType().name()).append(",")
						.append(n.getBeatSnap()).append(",")
						.append(n.getBeatSnapEnd())
						.toString());
			}
		}
		FileUtils.writeToFile(data.toArray(new String[data.size()]),"music/"+song+".sig");
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setSize(WINDOW_SIZE);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new LLSIG(f);
	}
	
	public static double approximateBPM() {
		long totalDiff = 0;
		if (beats.size()>=2) {
			for (int i=1;i<beats.size();i++) {
				totalDiff+=beats.get(i)-beats.get(i-1);
			}
			long averageDiff = totalDiff/(beats.size()-1);
			return (1/((double)averageDiff/1000000000l)*60);
		} else {
			return 0;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int lane = -1;
		if (EDITOR) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_A:{lane=0;}break;
				case KeyEvent.VK_S:{lane=1;}break;
				case KeyEvent.VK_D:{lane=2;}break;
				case KeyEvent.VK_F:{lane=3;}break;
				case KeyEvent.VK_SPACE:{lane=4;}break;
				case KeyEvent.VK_J:{lane=5;}break;
				case KeyEvent.VK_K:{lane=6;}break;
				case KeyEvent.VK_L:{lane=7;}break;
				case KeyEvent.VK_SEMICOLON:{lane=8;}break;
				case KeyEvent.VK_P:{if (LLSIG.game.PLAYING&&musicPlayer.isPaused()) {
					musicPlayer.seek((long)(Math.floor(EDITOR_CURSOR_BEAT*beatDelay)+offset));
					PREVIOUS_CURSOR_BEAT=EDITOR_CURSOR_BEAT;
					LLSIG.game.lanes.forEach((l)->{
							l.noteChart.forEach((note)->{
								note.active = note.getBeatSnap()>EDITOR_CURSOR_BEAT;
								note.active2 = note.getBeatSnapEnd()>EDITOR_CURSOR_BEAT;
						});
					});
					musicPlayer.resume();
				} else {musicPlayer.pause();EDITOR_CURSOR_BEAT=PREVIOUS_CURSOR_BEAT;}}break;
				case KeyEvent.VK_PAGE_UP:{EDITOR_CURSOR_BEAT=Math.max(EDITOR_CURSOR_BEAT-4,0);}break;
				case KeyEvent.VK_PAGE_DOWN:{EDITOR_CURSOR_BEAT+=4;}break;
				case KeyEvent.VK_Q:{if (LLSIG.game.PLAYING) {musicPlayer.pause();SaveSongData(song,lanes);}}break;
				case KeyEvent.VK_DOWN:{EDITOR_CURSOR_BEAT+=1d/EDITOR_BEAT_DIVISIONS;}break;
				case KeyEvent.VK_RIGHT:{EDITOR_BEAT_DIVISIONS=Math.max(EDITOR_BEAT_DIVISIONS*2,1);EDITOR_CURSOR_BEAT=Math.floor(EDITOR_CURSOR_BEAT*EDITOR_BEAT_DIVISIONS)/EDITOR_BEAT_DIVISIONS;}break;
				case KeyEvent.VK_UP:{EDITOR_CURSOR_BEAT=Math.max(EDITOR_CURSOR_BEAT-(1d/EDITOR_BEAT_DIVISIONS),0);}break;
				case KeyEvent.VK_LEFT:{EDITOR_BEAT_DIVISIONS=(int)Math.max(Math.floor(EDITOR_BEAT_DIVISIONS/2),0);EDITOR_CURSOR_BEAT=Math.floor(EDITOR_CURSOR_BEAT*EDITOR_BEAT_DIVISIONS)/EDITOR_BEAT_DIVISIONS;}break;
				case KeyEvent.VK_DELETE:{
					LLSIG.game.lanes.forEach((l)->{
							l.noteChart.forEach((note)->{
								if (note.getBeatSnap()==EDITOR_CURSOR_BEAT||(note.getNoteType()==NoteType.HOLD&&note.getBeatSnap()>=EDITOR_CURSOR_BEAT&&note.getBeatSnapEnd()<=EDITOR_CURSOR_BEAT)) {
							note.markForDeletion();
						}
						});
					});
				}break;
				case KeyEvent.VK_CONTROL:{HOLDING_CTRL_KEY=true;}break;
			}
		} else {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_A:{
					if (BPM_MEASURE) {
						beats.add(System.nanoTime());
					}
					lane=0;
				}break;
				case KeyEvent.VK_BACK_SLASH:{
					if (METRONOME) {
						testOffset=musicPlayer.getPlayPosition();
						beatNumber=Math.max(0,beatNumber-12);
					}
					musicPlayer.seek((long)(Math.floor(musicPlayer.getPlayPosition()-(beatDelay*12))));
					LLSIG.game.lanes.forEach((l)->{
						l.noteChart.forEach((note)->{if (note.start>musicPlayer.getPlayPosition()) {note.markForDeletion();System.out.println("Marked "+note);}});
					});
				}break;
				case KeyEvent.VK_S:{lane=1;}break;
				case KeyEvent.VK_D:{lane=2;}break;
				case KeyEvent.VK_F:{lane=3;}break;
				case KeyEvent.VK_SPACE:{lane=4;}break;
				case KeyEvent.VK_J:{lane=5;}break;
				case KeyEvent.VK_K:{lane=6;}break;
				case KeyEvent.VK_L:{lane=7;}break;
				case KeyEvent.VK_SEMICOLON:{lane=8;}break;
				case KeyEvent.VK_P:{if (LLSIG.game.PLAYING&&musicPlayer.isPaused()) {musicPlayer.resume();} else {musicPlayer.pause();}}break;
				case KeyEvent.VK_Q:{if (LLSIG.game.PLAYING) {musicPlayer.pause();SaveSongData(song,lanes);}}break;
				case KeyEvent.VK_DOWN:{EDITOR_CURSOR_BEAT+=1d/EDITOR_BEAT_DIVISIONS;}break;
				case KeyEvent.VK_RIGHT:{EDITOR_BEAT_DIVISIONS=Math.max(EDITOR_BEAT_DIVISIONS*2,1);EDITOR_CURSOR_BEAT=Math.floor(EDITOR_CURSOR_BEAT*EDITOR_BEAT_DIVISIONS)/EDITOR_BEAT_DIVISIONS;}break;
				case KeyEvent.VK_UP:{EDITOR_CURSOR_BEAT=Math.max(EDITOR_CURSOR_BEAT-(1d/EDITOR_BEAT_DIVISIONS),0);}break;
				case KeyEvent.VK_LEFT:{EDITOR_BEAT_DIVISIONS=(int)Math.max(Math.floor(EDITOR_BEAT_DIVISIONS/2),0);EDITOR_CURSOR_BEAT=Math.floor(EDITOR_CURSOR_BEAT*EDITOR_BEAT_DIVISIONS)/EDITOR_BEAT_DIVISIONS;}break;
			}
		}
		if (lane!=-1) {
			if (EDITOR) {
				Lane l = LLSIG.game.lanes.get(lane);
				if (!l.keyPressed) {
					List<Note> matchingNotes = l.noteChart.stream().filter((note)->note.getBeatSnap()==EDITOR_CURSOR_BEAT||(note.getNoteType()==NoteType.HOLD&&note.getBeatSnap()<=EDITOR_CURSOR_BEAT&&note.getBeatSnapEnd()>=EDITOR_CURSOR_BEAT)).collect(Collectors.toList());
					boolean replace=true;
					for (Note n : matchingNotes) {
						if (n.getNoteType()!=NoteType.HOLD) {replace=false;} //We don't replace the note if the position was exactly matching as we may have wanted to remove the note completely.
						n.markForDeletion();
					}
					if (replace) {
						Note n = new Note(NoteType.NORMAL,beatDelay*EDITOR_CURSOR_BEAT);
						n.active=false;
						//System.out.println(Math.round(((musicPlayer.getPlayPosition()-offset)/beatDelay)*NOTE_RECORD_BEAT_SNAP_MULTIPLE)/(double)NOTE_RECORD_BEAT_SNAP_MULTIPLE);
						n.setBeatSnap(EDITOR_CURSOR_BEAT);
						LLSIG.game.lanes.get(lane).addNote(n,true);
						LLSIG.game.lanes.get(lane).lastNoteAdded=n;
						l.keyPressed=true;
						clap.setFramePosition(0);
						clap.start();
					}
				}
			} else
			if (PLAYING&&EDITMODE) {
				if (!LLSIG.game.lanes.get(lane).keyPressed) {
					Note previousN = LLSIG.game.lanes.get(lane).lastNoteAdded;
					double snapBeat = Math.round(((musicPlayer.getPlayPosition()-offset)/beatDelay)*NOTE_RECORD_BEAT_SNAP_MULTIPLE)/(double)NOTE_RECORD_BEAT_SNAP_MULTIPLE;
					boolean allowed=true;
					if (previousN!=null) {
						if (previousN.getBeatSnap()==snapBeat) {
							allowed=false;
						}
					}

					if (allowed) {
						Note n = new Note(NoteType.NORMAL,musicPlayer.getPlayPosition());
						n.active=false;
						//System.out.println(Math.round(((musicPlayer.getPlayPosition()-offset)/beatDelay)*NOTE_RECORD_BEAT_SNAP_MULTIPLE)/(double)NOTE_RECORD_BEAT_SNAP_MULTIPLE);
						n.setBeatSnap(snapBeat);
						LLSIG.game.lanes.get(lane).addNote(n);
						LLSIG.game.lanes.get(lane).lastNoteAdded=n;
					}
				}
			} else
			if (PLAYING&&!EDITMODE&&!EDITOR) {
				Lane l = lanes.get(lane);
				if (l.noteExists()) {
					Note n = l.getNote();
					double diff = n.getStartFrame()-LLSIG.game.musicPlayer.getPlayPosition();
					if (n.active&&diff<=BAD_TIMING_WINDOW) {
						judgeNote(l, diff, false);
						n.active=false;
					}
				}
			}
			keyState[lane]=true;
		}
		//System.out.println("Pressed "+e.getKeyChar()+" on frame "+musicPlayer.getPlayPosition());
	}

	private void judgeNote(Lane l, double diff, boolean allowMiss) {
		if (Math.abs(diff)<=PERFECT_TIMING_WINDOW) {l.lastRating=TimingRating.PERFECT;COMBO++;PERFECT_COUNT++;LAST_PERFECT=LLSIG.game.musicPlayer.getPlayPosition();} else
		if (Math.abs(diff)<=EXCELLENT_TIMING_WINDOW) {l.lastRating=TimingRating.EXCELLENT;COMBO++;EXCELLENT_COUNT++;LAST_EXCELLENT=LLSIG.game.musicPlayer.getPlayPosition();} else
		if (Math.abs(diff)<=GREAT_TIMING_WINDOW) {l.lastRating=TimingRating.GREAT;COMBO++;GREAT_COUNT++;LAST_GREAT=LLSIG.game.musicPlayer.getPlayPosition();} else
		if (Math.abs(diff)<=BAD_TIMING_WINDOW) {
			if (Math.signum(diff)>0) {
				l.lastRating=TimingRating.EARLY;
				EARLY_COUNT++;
				LAST_EARLY=LLSIG.game.musicPlayer.getPlayPosition();
			} else {
				l.lastRating=TimingRating.LATE;
				LATE_COUNT++;
				LAST_LATE=LLSIG.game.musicPlayer.getPlayPosition();
			}
				COMBO=0;
		} else
		if (allowMiss) {
			l.lastRating=TimingRating.MISS;
			MISS_COUNT++;
			LAST_MISS=LLSIG.game.musicPlayer.getPlayPosition();
			COMBO=0;
		}
		l.lastNote=LLSIG.game.musicPlayer.getPlayPosition();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int lane = -1;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_A:{lane=0;}break;
			case KeyEvent.VK_S:{lane=1;}break;
			case KeyEvent.VK_D:{lane=2;}break;
			case KeyEvent.VK_F:{lane=3;}break;
			case KeyEvent.VK_SPACE:{lane=4;}break;
			case KeyEvent.VK_J:{lane=5;}break;
			case KeyEvent.VK_K:{lane=6;}break;
			case KeyEvent.VK_L:{lane=7;}break;
			case KeyEvent.VK_SEMICOLON:{lane=8;}break;
			case KeyEvent.VK_CONTROL:{HOLDING_CTRL_KEY=false;}break;
		}
		if (lane!=-1) {
			if (EDITOR) {
				if (LLSIG.game.lanes.get(lane).keyPressed) {
					Note lastNote = LLSIG.game.lanes.get(lane).lastNoteAdded;
					if (lastNote!=null) {
						if (EDITOR_CURSOR_BEAT-lastNote.getBeatSnap()>=1) {
							List<Note> matchingNotes = LLSIG.game.lanes.get(lane).noteChart.stream().filter((note)->(note!=lastNote&&note.getBeatSnap()<=EDITOR_CURSOR_BEAT&&note.getBeatSnap()>=lastNote.getBeatSnap())||(note.getNoteType()==NoteType.HOLD&&((EDITOR_CURSOR_BEAT>=note.getBeatSnap()&&EDITOR_CURSOR_BEAT<=note.getBeatSnapEnd())||(lastNote.getBeatSnap()>=note.getBeatSnap()&&lastNote.getBeatSnap()<=note.getBeatSnapEnd())))).collect(Collectors.toList());
							for (Note n : matchingNotes) {
								n.markForDeletion();
							}
							double noteBeat = Math.round(EDITOR_CURSOR_BEAT*beatDelay);
							//Create a hold note to this position.
							lastNote.setEndFrame(noteBeat);
							lastNote.setNoteType(NoteType.HOLD);
							lastNote.setBeatSnapEnd(EDITOR_CURSOR_BEAT);
							lastNote.active2=false;
							clap.setFramePosition(0);
							clap.start();
						}
					}
					LLSIG.game.lanes.get(lane).keyPressed=false;
				}
			}
			if (PLAYING&&EDITMODE) {
				if (LLSIG.game.lanes.get(lane).keyPressed) {
					double noteBeat = Math.round(((musicPlayer.getPlayPosition()-offset)/beatDelay)*NOTE_RECORD_BEAT_SNAP_MULTIPLE)/(double)NOTE_RECORD_BEAT_SNAP_MULTIPLE;
					Note lastNote = LLSIG.game.lanes.get(lane).lastNoteAdded;
					if (lastNote!=null) {
						if (noteBeat-lastNote.getBeatSnap()>=1) {
							lastNote.setNoteType(NoteType.HOLD);
							lastNote.setBeatSnapEnd(noteBeat);
							lastNote.active2=false;
							//LLSIG.game.lanes.get(lane).lastNoteAdded=null;
						}
					}
					LLSIG.game.lanes.get(lane).keyPressed=false;
				}
			}
			if (PLAYING&&!EDITMODE&&!EDITOR) {
				keyState[lane]=false;
				Lane l = lanes.get(lane);
				if (l.noteExists()) {
					Note n = l.getNote();
					double diff2 = n.getEndFrame()-LLSIG.game.musicPlayer.getPlayPosition();
					if (n.getNoteType()==NoteType.HOLD&&n.active2&&!n.active) {
						judgeNote(l, diff2, true);
						n.active2=false;
					}
				}
			}
			keyState[lane]=false;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (EDITOR) {
			if (e.getWheelRotation()!=0) {
				if (Math.abs(e.getWheelRotation())<0) {
					//Rotated up.
					EDITOR_CURSOR_BEAT=Math.max(EDITOR_CURSOR_BEAT-(1d/EDITOR_BEAT_DIVISIONS),0);
				} else {
					//Rotated down.
					EDITOR_CURSOR_BEAT+=1d/EDITOR_BEAT_DIVISIONS;
				}
			}
		}
	}
}
