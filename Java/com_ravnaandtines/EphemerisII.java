package com_ravnaandtines;

import java.applet.*;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.concurrent.*;

/** 
 *  Class EphemerisII
 *
 *  This is a simple Gloranthan planetarium
 *  It is implemented as a stand-alone Application, which has been
 *  derived from Applet, along the lines of the framework in Main.java
 *
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
 *  All rights reserved.  For full licence details see file Main.java
 *
 * @author Mr. Tines
 * @version 1.0 11-Oct-1997
 *
 */
class E2HitAnimate extends Animation implements MouseMotionListener {

    public E2HitAnimate(final Animated m, final JTextField hitfield) {
        super(m);
        this.hitfield = hitfield;
        this.addMouseMotionListener(this);
    }
    private final JTextField hitfield;

    public void mouseDragged(final MouseEvent e) {
        //no-op
    }

    public void mouseMoved(final MouseEvent e) {
        ((EphemerisIIAnimation) movingImage).hit(e, hitfield);
    }
}

public class EphemerisII extends JApplet {

    private E2HitAnimate engine;
    private EphemerisIIAnimation ac;
    private EphemerisIIControlPanel controls;
    private boolean master = false;
    private static EphemerisIIFrame window;
    public static JCheckBox cbsunpath, cbsouthpath, cbnames, cbframe, cblight, cbobscure;
    public static JCheckBox cbring;
    ////private final java.util.Timer timer = new java.util.Timer();
    private final ScheduledExecutorService scheduler =
       Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> taskHandle;


    /**
     * Main initialisation
     * @see Applet#init
     */
    @Override
    public void init() {
        final JTextField hitfield = new JTextField(25);
        hitfield.setEditable(false);
        ac = new EphemerisIIAnimation();
        engine = new E2HitAnimate(ac, hitfield);
        ac.setAngles(0.0, 0.0);

        final JPanel statusbar = new JPanel();
        final JTabbedPane tab = new JTabbedPane();

        controls = new EphemerisIIControlPanel(statusbar, hitfield);
        tab.add(controls, "Navigation", 0);

        final JPanel show = new JPanel();
        tab.add("Display options", show);

        final E2WizBox wizbox = new E2WizBox();
        tab.add("Wizard Mode", wizbox);

        final JTextArea ta = new JTextArea(Licence.LICENSE_TEXT, 20, 20);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        tab.add("License", new JScrollPane(ta));

        show.setLayout(new BoxLayout(show, BoxLayout.Y_AXIS));

        cbsunpath = new JCheckBox("Show sunpath");
        cbsunpath.setSelected(false);
        show.add(cbsunpath);

        cbsouthpath = new JCheckBox("Show southpath");
        cbsouthpath.setSelected(false);
        show.add(cbsouthpath);

        cbnames = new JCheckBox("Show names");
        cbnames.setSelected(true);
        show.add(cbnames);

        cbframe = new JCheckBox("Show Buserian frame");
        cbframe.setSelected(false);
        show.add(cbframe);

        cblight = new JCheckBox("Show Lightfore path");
        cblight.setSelected(false);
        show.add(cblight);

        cbobscure = new JCheckBox("Show stars during day");
        cbobscure.setSelected(true);
        show.add(cbobscure);

        cbring = new JCheckBox("Real size objects", false);
        show.add(cbring);        
        
        tab.setSelectedComponent(controls);

        window = new EphemerisIIFrame("Gloranthan Ephemeris", master);
        final Dimension d = setFrameSize();
        engine.setSize(d.width / 2, d.height / 2);
        window.setLayout(new BorderLayout());
        window.add("Center", engine);
        window.add("East", tab);
        window.add("South", statusbar);
        ac.associate(window);
        ac.associate(controls);
    }

    private static Dimension setFrameSize() throws HeadlessException {
        final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        window.setSize(d.width / 2, d.height / 2);
        window.setVisible(true);
        return d;
    }

    /**
     * Main (re-)start; timer is
     * fired up at this point.
     */
    @Override
    public void start() {
        ////final java.util.TimerTask task = new java.util.TimerTask() {
        final Runnable task = new Runnable() {

            public void run() {
                ac.setSize(engine.getSize());
                controls.tick();
                engine.repaint();
            }
        };
        taskHandle = scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);

        ////timer.scheduleAtFixedRate(task, 0, 1000);
    }

    /**
     * Main graceful suppression (iconise,
     * leave page or whatever).  Called before
     * destroy()
     */
    @Override
    public void stop() {
        window.setVisible(false);
        ////timer.cancel();
        taskHandle.cancel(false);
    }

    /**
     * Main final termination and tidy
     */
    @Override
    public void destroy() {
        //No-op
    }

    /**
     * Output to screen
     * @param g Graphic to which to draw
     */
    @Override
    public void paint(final Graphics g) {
        engine.repaint();
    }

    /**
     * Return parameter details
     * @see Applet.getParameterInfo
     */
    @Override
    public String[][] getParameterInfo() {
        final String[][] t = {{"None"}, {"N/A"}, {"This applet is purely GUI driven"}};
        return t; // NOPMD
    }

    /**
     * Applicationizer function
     */
    public static void main(final String[] args) {
        final EphemerisII self = new EphemerisII();
        self.master = true;
        self.init();
        self.start();
    }
}

/* end of file EphemerisII.java */

