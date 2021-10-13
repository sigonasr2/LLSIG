package main.java.LLSIG;

import java.awt.Graphics;
import java.awt.Color;

public class SpectrographBar {
    final float NO_UPDATE = 999f;
    float current_magnitude = 0.0f;
    float target_magnitude = 0.0f;
    final double CHANGE_SPD = 1d;
    SpectrographBar() {
        this(0f);
    }
    SpectrographBar(float magnitude) {
        update(magnitude);
    }
    public void update() {
        update(NO_UPDATE);
    }
    public void update(float value) {
    	if (current_magnitude<target_magnitude) {
    		current_magnitude=(float)Math.min(0,current_magnitude+CHANGE_SPD);
    	} else {
    		current_magnitude=(float)Math.max(-60f,current_magnitude-CHANGE_SPD);
    	}
        if (value!=NO_UPDATE) {
            target_magnitude=value;
        }
    }
    public void draw(Graphics g,int x,int w,int h,int x2) {
        final Color targetCol = Color.CYAN;
    	if (current_magnitude!=NO_UPDATE) {
    		//System.out.println(current_magnitude);
	        int colVal = (int)(((60d-Math.abs(current_magnitude))/60d)*255);
            double r_ratio = targetCol.getRed()/255d;
            double g_ratio = targetCol.getGreen()/255d;
            double b_ratio = targetCol.getBlue()/255d;
	        g.setColor(new Color((int)(colVal*r_ratio),(int)(colVal*g_ratio),(int)(colVal*b_ratio),Math.min(255,colVal*2)));
	        g.fillRect(x,0,w,h);
	        g.fillRect(x2,0,w,h);
    	}
    }
}
