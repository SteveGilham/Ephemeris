package com_ravnaandtines;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.*;

/** 
 *  Class Animation
 *
 *  A double-buffered Canvas
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
 *  All rights reserved.  For full licence details see file Main.java
 *
 * @author Mr. Tines
 * @version 1.0 21-Jul-1997
 *
 */
public class Animation extends JPanel {

    private Dimension imageSize;
    private Image offscreen;
    protected Animated movingImage;

    private Animation() {
        super();
    }

    /**
     * The constructor sets everything up
     * @param m Animated thing to display with double buffering
     */
    public Animation(final Animated m) {
        super();
        movingImage = m;
        imageSize = new Dimension(0, 0);
    }

    /**
     * The double buffered action
     * @param g Graphics context to draw to
     */
    @Override public void update(final Graphics g) {
        paint(g);
    }
    
    @Override public void paint(final Graphics g) {
        super.paint(g);
        final Dimension d = getSize();
        
        if ((d.width != imageSize.width) || (d.height != imageSize.height)) {
            imageSize = d;
            if (offscreen != null) {
                offscreen.flush();
            }
            
            offscreen = createImage(d.width, d.height);
        }
        
        if (offscreen == null) {
            g.setColor(getBackground());
            g.fillRect(0, 0, imageSize.width, imageSize.height);
            return;
        }
        
        movingImage.setSize(imageSize);
        offscreen.flush();

        final Graphics off = offscreen.getGraphics();
        off.setColor(getBackground());
        off.fillRect(0, 0, imageSize.width, imageSize.height);
        movingImage.paint(off);
        g.drawImage(offscreen, 0, 0, this);
    }
}

/* end of file Animation.java */

