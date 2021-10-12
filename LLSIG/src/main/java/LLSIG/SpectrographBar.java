package main.java.LLSIG;

import java.awt.Graphics;
import java.awt.Color;

public class SpectrographBar {
    final float NO_UPDATE = 999f;
    float current_magnitude = 0.0f;
    final double CHANGE_SPD = 1.05d;
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
        if (value==NO_UPDATE) {
            if (current_magnitude>-59.7f) {
                current_magnitude=(float)(CHANGE_SPD/(-60-current_magnitude)+current_magnitude);
            } else {
                current_magnitude=-60f;
            }
        } else {
            current_magnitude=value;
        }
    }
    public void draw(Graphics g,int x,int w,int h) {
    	if (current_magnitude!=NO_UPDATE) {
    		//System.out.println(current_magnitude);
	        int colVal = (int)(((60d-Math.abs(current_magnitude))/60d)*255);
	        g.setColor(new Color(colVal,colVal,colVal,colVal));
	        g.fillRect(x,0,w,h);
    	}
    }
}
