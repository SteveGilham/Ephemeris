package com_ravnaandtines;

/** 
 *  Class EphemerisIIControlPanel 
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
 *  All rights reserved.  For full licence details see file Main.java
 *
 * @author Mr. Tines
 * @version 1.0 11-Oct-1997
 *
 */
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Image;

import net.sf.functionalj.tuple.*;

//====CLASS E2TimePicker========================================================
class E2TimePicker extends JPanel implements ChangeListener {

    // private JButton ok;
    final private JSpinner scalebar;
    //final private JRadioButton cbmin,  cbhr,  cbday,  cbyr;
    final private String[] intervals = {
        "Minutes",
        "Hours",
        "Days",
        "Years"
    };
    final private JComboBox picker = new JComboBox(intervals);
    private double delta;

    public E2TimePicker(final double last) {
        super();
        setLayout(new GridLayout(1, 0));
        delta = last;

        scalebar = new JSpinner(new SpinnerNumberModel(0, 0, 60, 1));
        add(scalebar);
        scalebar.addChangeListener(this);
        add(picker);

        if (last < 1.0 / 24.0) {
            picker.setSelectedIndex(0);
        } else if (last < 1 && last >= 1.0 / 24.0) {
            picker.setSelectedIndex(1);
        } else if (last < 294 && last >= 1) {
            picker.setSelectedIndex(2);
        } else {
            picker.setSelectedIndex(3);
        }

        picker.addItemListener(new ItemListener() {

            public void itemStateChanged(final ItemEvent e) {
                init();
            }
        });
        init();
    }

    public double get() {
        return delta;
    }
    
    @Override
    public void setEnabled(final boolean state)
    {
        super.setEnabled(state);
        picker.setEnabled(state);
        scalebar.setEnabled(state);
    }

    private void init() {
        int val;
        switch (picker.getSelectedIndex()) {
            case 0:
                val = (int) Math.rint(1440 * delta);
                scalebar.setModel(new SpinnerNumberModel(Math.min(val, 60), 0, 60, 1));
                break;

            case 1:
                val = (int) Math.rint(24 * delta);
                scalebar.setModel(new SpinnerNumberModel(Math.min(val, 24), 0, 24, 1));
                break;

            case 2:
                val = (int) Math.rint(24);
                scalebar.setModel(new SpinnerNumberModel(Math.min(val, 294), 0, 294, 1));
                break;

            default:
                val = (int) Math.rint(delta / 29.4);
                scalebar.setModel(new SpinnerNumberModel(Math.min(val, 100), 0, 100, 1));
                break;
        }
    }

    public void stateChanged(final ChangeEvent evt) {
        switch (picker.getSelectedIndex()) {

            case 0:
                delta = ((Integer) scalebar.getValue()) / 1440.0;
                break;
            case 1:
                delta = ((Integer) scalebar.getValue()) / 24.0;
                break;
            case 2:
                delta = ((Integer) scalebar.getValue());
                break;
            default:
                delta = ((Integer) scalebar.getValue()) * 294.0;
                break;
        }
    }
}

//====CLASS E2LocatorPanel======================================================
class E2LocatorPanel extends Component implements MouseListener {

    public double r = 0.0, theta = 0.0;
    // square represents inner world half-side 5,750 km
    // dome radius is 20,000 km
    private static final double D_RAD = 20.0;
    private static final double INNER = 5.75;
    private static final double FRACTION = INNER / D_RAD;
    private int ix, iy, iw, ih;
    private final EphemerisIIControlPanel scaler;
    private final Image map;

    /**
     * default constructor
     */
    public E2LocatorPanel(final EphemerisIIControlPanel owner) {
        super();
        scaler = owner;

        // image is 194 <> by 168 up/down
        // centre is at 90.5, 97.5 (0 to n-1) so has 102.5 to the right
        // assume 103 pixels map to the half-size
        map = Toolkit.getDefaultToolkit().createImage(new InnerWorld());
        //setBackground(Color.lightGray);
        addMouseListener(this);
    }

    /**
     * overrides superclass method to draw dome and hit point
     */
    @Override
    public void paint(final Graphics g) {
        final Dimension d = getSize();
        final int lwi = d.width;
        final int lhi = d.height;
        int x = lwi;
        int buf = 0;
        if (lhi < lwi) {
            x = lhi;
            buf = (lwi - lhi) / 2;
        }
        double px, py;
        int spx, spy;


        // now put in the image
        // the centre is at buf+x/2, x/2 and x/2 maps to 103 pixels
        final double factor = (double) x / 206.0;
        ix = buf + x / 2 - (int) Math.round(91.0 * factor);
        iy = x / 2 - (int) Math.round(98.0 * factor);
        iw = (int) Math.round(194.0 * factor);
        ih = (int) Math.round(168.0 * factor);
        g.drawImage(map, ix, iy, iw, ih, this);

        g.setColor(scaler.getBackground());

        for (int i = 1; i < 4; ++i) {
            g.draw3DRect(ix - i, iy - i, iw + 2 * i, ih + 2 * i, true);
            if ((i == ix) || (i == iy) || (ix + iw + i == x) || (iy + ih + i == x)) {
                break;
            }
        }

        // Viewpoint
        px = ((double) (x - 2)) / 2.0 * (1.0 + r * Math.cos(theta) / FRACTION);
        py = ((double) (x - 2)) / 2.0 * (1.0 - r * Math.sin(theta) / FRACTION);
        spx = (int) Math.round(px);
        spy = (int) Math.round(py);
        g.setColor(Color.yellow);
        g.drawOval(buf + spx - 2, spy - 2, 5, 5);
        g.setColor(Color.darkGray);
        g.drawOval(buf + spx - 1, spy - 1, 3, 3);
    }

    /**
     * Performs event handling as per Java 1.0 for hits
     * @return boolean state
     */
    public void mouseClicked(final MouseEvent e) {
        //ignore;
    }

    public void mousePressed(final MouseEvent e) {
        //ignore;
    }

    public void mouseEntered(final MouseEvent e) {
        //ignore;
    }

    public void mouseExited(final MouseEvent e) {
        //ignore;
    }

    public void mouseReleased(final MouseEvent e) {

        if ((e.getX() < ix) || (e.getX() > ix + iw)
                || (e.getY() < iy) || (e.getY() > iy + ih)) {
            return;
        }

        final Dimension d = getSize();
        int x = d.width;
        int buf = 0;
        if (x > d.height) {
            x = d.height;
            buf = (d.width - d.height) / 2;
        }
        final double centre = ((double) (x - 2)) / 2.0;
        final double px = (e.getX() - buf) - centre;
        final double py = centre - e.getY();
        final double tr = Math.sqrt(px * px + py * py) / centre;
        r = tr * FRACTION;
        theta = Math.atan2(py, px);
        repaint();
    }
}

//====CLASS E2LookPanel=========================================================
class E2LookPanel extends Component implements MouseListener, MouseMotionListener {

    public boolean up = true;
    public double bearing = 0.0;
    private boolean scan = false;
    private double feed = 0.0;

    /**
     * default constructor
     */
    public E2LookPanel() {
        super();
        //setBackground(Color.lightGray);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * overrides superclass method to draw horizon and look-at
     */
    @Override
    public void paint(final Graphics g) {
        final Dimension d = getSize();
        final int lwi = d.width;
        final int lhi = d.height;
        int buf = 0;
        int x = lwi;
        if (lhi < lwi) {
            x = lhi;
            buf = (lwi - lhi) / 2;
        }
        final int halfx = x / 2;
        final int rad = x / 3;
        final int inner = rad / 2;
        final int cx = buf + halfx;
        final int cy = halfx;
        g.setColor(getParent().getBackground());
        g.fillOval(cx - rad, cy - rad, 2 * rad, 2 * rad);
        g.setColor(Color.green);
        g.drawOval(cx - rad, cy - rad, 2 * rad, 2 * rad);
        g.drawOval(cx - (rad - 1), cy - (rad - 1), 2 * (rad - 1), 2 * (rad - 1));

        g.setColor(getParent().getBackground().brighter());
        g.drawArc(cx - (rad + 1), cy - (rad + 1), 2 * (rad + 1), 2 * (rad + 1), 45, 180);
        g.drawArc(cx - (rad + 2), cy - (rad + 2), 2 * (rad + 2), 2 * (rad + 2), 45, 180);
        g.setColor(getParent().getBackground().darker());
        g.drawArc(cx - (rad + 1), cy - (rad + 1), 2 * (rad + 1), 2 * (rad + 1), 45, -180);
        g.drawArc(cx - (rad + 2), cy - (rad + 2), 2 * (rad + 2), 2 * (rad + 2), 45, -180);

        g.setColor(Color.blue);
        g.drawOval(cx - inner, cy - inner, 2 * inner, 2 * inner);
        g.drawLine(cx - rad, halfx, cx - inner, halfx);
        g.drawLine(cx + inner, halfx, cx + rad, halfx);
        g.drawLine(buf + halfx, cy - rad, buf + halfx, cy - inner);
        g.drawLine(buf + halfx, cy + inner, buf + halfx, cy + rad);

        // Viewpoint
        g.setColor(Color.red);
        if (up) {
            g.fillOval(buf + halfx - 2, halfx - 2, 5, 5);
        } else {
            final double px = ((double) rad) * Math.sin(bearing);
            final double py = ((double) rad) * Math.cos(bearing);
            final int spx = cx + (int) Math.round(px);
            final int spy = cy - (int) Math.round(py);
            g.fillOval(spx - 2, spy - 2, 5, 5);
            g.drawLine(cx, cy, spx, spy);
        }

        if (scan) {
            g.setColor(Color.darkGray);
            final double px = ((double) rad) * Math.sin(feed);
            final double py = ((double) rad) * Math.cos(feed);
            final int spx = cx + (int) Math.round(px);
            final int spy = cy - (int) Math.round(py);
            g.fillOval(spx - 2, spy - 2, 5, 5);
            g.drawLine(cx, cy, spx, spy);

            feed *= 180.0 / Math.PI;
            if (feed < 0) {
                feed += 360.0;
            }
            String f = Double.toString(feed);
            if (f.length() > 7) {
                f = f.substring(0, 7);
            }
            g.drawChars(f.toCharArray(), 0, f.length(), cx, cy);
        }
        scan = false;
    }

    /**
     * Performs event handling as per Java 1.0 for hits
     * @return boolean state
     */
    public void mouseClicked(final MouseEvent e) {
        doEvent(e, false, false, true, false);
    }

    public void mousePressed(final MouseEvent e) {
        //TODO: implement this java.awt.event.MouseListener method;
    }

    public void mouseReleased(final MouseEvent e) {
        //TODO: implement this java.awt.event.MouseListener method;
    }

    public void mouseEntered(final MouseEvent e) {
        //TODO: implement this java.awt.event.MouseListener method;
    }

    public void mouseExited(final MouseEvent e) {
        doEvent(e, false, false, false, true);
    }

    public void mouseDragged(final MouseEvent e) {
        doEvent(e, false, true, false, false);
    }

    public void mouseMoved(final MouseEvent e) {
        doEvent(e, true, false, false, false);
    }

    public boolean doEvent(final MouseEvent event,
            final boolean move, final boolean drag, final boolean clik, final boolean exit) {
        final Dimension d = getSize();
        final int lwi = d.width;
        final int lhi = d.height;
        int buf = 0;
        int x = lwi;
        if (lhi < lwi) {
            x = lhi;
            buf = (lwi - lhi) / 2;
        }
        final int halfx = x / 2;
        final int rad = x / 3;
        final int inner = rad / 2;
        final int cx = buf + halfx;
        final int cy = halfx;

        final double px = event.getX() - cx;
        final double py = cy - event.getY();
        final double tr = Math.sqrt(px * px + py * py);

        scan = false;

        if (tr > rad || exit) {
            ;
        } else if (!clik) {
            if (tr > inner) {
                scan = true;
                feed = Math.atan2(px, py);
            }
        } else {
            up = (tr < inner);
            if (!up) {
                bearing = Math.atan2(px, py);
            }
        }
        repaint();
        return true;
    }
}

//====INTERFACE E2TimeOfDay=====================================================
interface E2TimeOfDay {

    void setTimeOfDay(int i);
}

//====CLASS E2SelfButton========================================================
class E2SelfButton extends JButton implements ActionListener {

    final private E2TimeOfDay core;
    final private int type;

    public E2SelfButton(final String label, final E2TimeOfDay target, final int i) {
        super(label);
        core = target;
        type = i;
        addActionListener(this);
    }

    public void actionPerformed(final ActionEvent e) {
        core.setTimeOfDay(type);
    }
}

//====CLASS EphemerisIIControlPanel=============================================
public class EphemerisIIControlPanel extends JPanel implements E2TimeOfDay,
        ChangeListener {

    /**
     * List of cults whose holy days are stored here
     */
    final private String[] cults = {
        "Voria", "Gorgorma", "Uleria", "Flamal", "Triolina", "Dormal", "Bagog", "[Summer Solstice]", "Invisible God", "Red Goddess", "Yelmalio", "Pamalt", "Babeester Gor", "Asrelia", "Earth Goddesses", "Lodril", "Maran Gor", "Lokarnos", "Kyger Litor, 7 Mothers", "Argan Argar", "Zorak Zoran", "Waha the Butcher", "Xiola Umbar", "Valind", "Magasta", "Subere", "Godunya", "Ty Kora Tek", "[Winter Solstice]", "Donandar", "Humakt", "Storm Bull", "Tsankth", "Orlanth", "Unholy Trio", "Issaries", "Lhankor Mhy"
    };
    /**
     * The day of the year (from 1 to 294 on which these fall)
     */
    final private int[] dates = {
        1, 3, 7, 23, 28, 37, 43,
        68, 80, 97, 110,
        116, 127, 134, 136, 138,
        139, 152, 175,
        177, 183, 189, 190, 193,
        196, 197, 201, 213, 215, 235,
        242, 258, 261, 263, 281, 286,
        287
    };
    /**
     * UI components
     */
    public JTextField calendar;
    final private JList holyDays;
    final private JSpinner minbar, hrbar, dowbar;
    final private JSpinner wkbar, yrbar;
    private static final String[] DAY_OF_WEEK = {"Freezeday", "Waterday", "Clayday", "Windsday",
        "Fireday", "Wildday", "Godsday"
    };
    private static final String[] WEEK_OF_SEASON = {"Disorder", "Harmony", "Death", "Fertility", "Stasis",
        "Movement", "Illusion", "Truth", "Luck", "Fate"
    };
    private static final String[] SEASON_OF_YEAR = {"Sea", "Fire", "Earth", "Dark", "Storm"};
    private int seconds = 0;
    /**
     * Current date
     */
    public int year = 1600, week = 0, day = 0;
    public int season = 8;
    /**
     * hour is in days from midnight
     * yrpart is in years from spring equinox, to nearest day before
     * harmonic is dome tilt in radians, + to North.
     */
    public double hour = 0.75, yrpart = 0.0;
    private Pair<Double, TripleUni<Double>> domeConfiguration =
            E2Param.getTilts(0, 0, 0.0);

    public Pair<Double, TripleUni<Double>> getDomeConfiguration() {
        return domeConfiguration;
    }

    public double getDayLength() {
        return domeConfiguration.getFirst();
    }
    /**
     * More UI components
     */
    private boolean ticking = false;
    final private JButton ticker;
    final private JCheckBox cbreverse;
    private double delta, freeDelta = 1.0 / 48.0;
    private final double delta0 = 293.0 / 294.0;
    final private E2LocatorPanel locator;
    final private E2LookPanel lookat;
    final private String[] calendarTypes = {"Astronometric", "Western", "Eastern", "Pamaltelan", "Orlanthi", "Pelorian"};
    final private JComboBox calendarList = new JComboBox(calendarTypes);
    final private String[] intervalTypes = {"1 second",
        "1 minute",
        "10 minutes",
        "1 hour",
        "1 day",
        "1 sidereal day",
        "1 solar day",
        "Ad lib."};
    final private JComboBox intervalList = new JComboBox(intervalTypes);

    public void getSolarDelta(final boolean next) {
        double dayLength = getDayLength();
        double dawn = (1.0 - dayLength) / 2.0; // fraction of day after midnight
        final boolean timeOfDay = (hour >= dawn) && ((1.0 - hour) > dawn);
        double phase = 0;
        if (timeOfDay) {
            phase = (hour - dawn) / dayLength;
        } else if (hour > 0.5) {
            phase = (hour - (1.0 - dawn)) / (1.0 - dayLength);
        } else {
            phase = (hour + dawn) / (1.0 - dayLength);
        }

        dayLength = E2Param.getTilts(
                week,
                next ? this.day + 1 : this.day - 1,
                hour).getFirst();
        dawn = (1.0 - dayLength) / 2.0;

        double newhour = hour;
        if (timeOfDay) {
            newhour = phase * dayLength + dawn;
        } else if (hour > 0.5) {
            newhour = phase * (1.0 - dayLength) + (1.0 - dawn);
        } else {
            newhour = phase * (1.0 - dayLength) - dawn;
        }

        if (next) {
            delta = 1.0 + newhour - hour;
        } else {
            delta = newhour - (hour + 1.0);
        }
    }

    /**
     * Steps the current ephemeris time by one appropriate unit of time
     */
    public void tick() {
        if (!ticking) {
            return;
        }

        switch (intervalList.getSelectedIndex()) {
            case 0:
            default:
                delta = 1.0 / 86400.0;
                break;
            case 1:
                delta = 1.0 / 1440.0;
                break;
            case 2:
                delta = 1.0 / 144.0;
                break;
            case 3:
                delta = 1.0 / 24.0;
                break;
            case 4:
                delta = 1.0;
                break;
            case 5:
                getSolarDelta(!cbreverse.isSelected());
                break;
            case 6:
                delta = delta0;
                break;
            case 7:
                delta = freeDelta;
                break;
        }

        if (cbreverse.isSelected() && intervalList.getSelectedIndex() != 5) {
            hour -= delta;
        } else {
            hour += delta;
        }
        setTime();
    }

    public void setTime() {
        if (Math.abs(hour) >= 1.0) {
            final double ddays = Math.floor(Math.abs(hour));
            final int idays = (int) Math.rint(ddays);
            if (hour > 0) {
                day += idays;
                hour -= idays;
                while (day >= 294) {
                    day -= 294;
                    ++year;
                }
            } else {
                day -= idays;
                hour += idays;
                while (day < 0) {
                    day += 294;
                    --year;
                }
            }
        }

        if (hour >= 0.999995) {
            hour -= 1;
            day += 1;
        } else if (hour < 0) {
            day -= 1;
            hour += 1;
        }
        final double hval = 24.0 * hour;
        int hr = (int) (24 * hour);
        if (hval - hr > 0.9999) {
            hr += 1;
        }

        hrbar.setValue(hr);
        final int min = (int) (hour * 1440.0 - 60 * hr);
        seconds = (int) (hour * 86400.0 - 3600 * hr - 60 * min);
        minbar.setValue(min);
        while (day > 6) {
            day -= 7;
            week += 1;
        }
        while (day < 0) {
            day += 7;
            week -= 1;
        }
        dowbar.setValue(day);

        while (week > 41) {
            year += 1;
            week -= 42;
        }
        while (week < 0) {
            year -= 1;
            week += 42;
        }
        wkbar.setValue(week);
        yrbar.setValue(year);
        final double saveHour = hour;
        setLabel();
        hour = saveHour;
    }

    /**
     * Sets the labels to follow the slider values; computes tilt of dome
     */
    private void setLabel() {
        year = (Integer) yrbar.getValue();
        day = (Integer) dowbar.getValue();

        //int min = minbar.getValue();

        //int hhr = hrbar.getValue();

        hour = ((Integer) hrbar.getValue()) / 24.0;
        hour += ((Integer) minbar.getValue()) / 1440.0;
        hour += ((Integer) seconds) / 86400.0;

        week = (Integer) wkbar.getValue();

        if (week > 41) {
            week = 41;
        }
        wkbar.setValue(week);

        /*double dday =*/ setHarmonic();
        setLocal();
    }

    public final void setLocal() {
        final double dawn = (1.0 - getDayLength()) / 2.0; // fraction of day after midnight
        final double dusk = (1.0 + getDayLength()) / 2.0; // fraction of day after midnight
        double part, h24;
        int hr, min, sec;
        String hh, mm, ss, text;

        switch (calendarList.getSelectedIndex()) {
            default:
            case 0: {
                h24 = hour * 24.0;
                hr = (int) h24;
                min = (int) ((h24 - hr) * 60.0);
                sec = (int) (((h24 - hr) * 60.0 - min) * 60.0);
                hh = Integer.toString(hr);
                if (hr < 10) {
                    hh = "0" + hh;
                }
                mm = Integer.toString(min);
                if (min < 10) {
                    mm = "0" + mm;
                }
                ss = Integer.toString(sec);
                if (sec < 10) {
                    ss = "0" + ss;
                }

                text = hh + ":" + mm + ":" + ss + "    " + DAY_OF_WEEK[day] + "    ";

                if (week < 5 * season) {
                    text += WEEK_OF_SEASON[week % season] + " week, "
                            + SEASON_OF_YEAR[week / season] + " season " + year + "ST    ";
                } else {
                    final int w = week - 5 * season + 1;
                    text += "Sacred Week " + w + " " + year + "ST    ";
                }
                final double dday = (7.0 * week + day) + hour;
                String dds = Double.toString(dday);
                if (dds.length() > 7) {
                    dds = dds.substring(0, 7);
                }

                /*String hd = "" + E2Param.getTilt(baseHarmonic);
                if (hd.length() > 7) {
                hd = hd.substring(0, 7);
                }*/
                text += "Day " + dds /*+ " tilt " + hd*/ + "    ";

                final double dd = getDayLength() * 24.0;
                hr = (int) dd;
                min = (int) ((dd - hr) * 60.0);
                text += "day len/24h = " + hr + "h" + min + "m";
            }
            break;

            case 1: // assumes 64 minute hours
            {
                if (hour < dawn) {
                    part = 8.0 * ((hour / dawn) + 1.0);
                    hr = (int) part;
                    min = (int) ((part - hr) * 64.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Night hour " + hr + ":" + mm;
                } else if (hour < dusk) {
                    part = 16.0 * (hour - dawn) / (dusk - dawn);
                    hr = (int) part;
                    min = (int) ((part - hr) * 64.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Day hour " + hr + ":" + mm;
                } else {
                    part = 8.0 * (hour - dusk) / (1.0 - dusk);
                    hr = (int) part;
                    min = (int) ((part - hr) * 64.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Night hour " + hr + ":" + mm;
                }
                final int dd = day + 7 * week;
                text += "    Day " + dd + " " + year + "ST";
            }
            break;
            case 2: {
                h24 = hour * 24.0;
                hr = (int) h24;
                min = (int) ((h24 - hr) * 60.0);
                sec = (int) (((h24 - hr) * 60.0 - min) * 60.0);
                hh = Integer.toString(hr);
                if (hr < 10) {
                    hh = "0" + hh;
                }
                mm = Integer.toString(min);
                if (min < 10) {
                    mm = "0" + mm;
                }
                ss = Integer.toString(sec);
                if (sec < 10) {
                    ss = "0" + ss;
                }

                text = hh + ":" + mm + ":" + ss + "    ";
                final int num = day + 1;

                text += Integer.toString(num) + "-day    The week of ";
                switch (week) {
                    case 0:
                        text += "Wise Passivity";
                        break;
                    case 1:
                        text += "Tranquil Composure";
                        break;
                    case 2:
                        text += "Lucid Stillness";
                        break;
                    case 3:
                        text += "Taciturn Solemnity";
                        break;
                    case 4:
                        text += "Fortunate Incapacity";
                        break;
                    case 5:
                        text += "Profound Solitude";
                        break;
                    case 6:
                        text += "Futile Annihilation";
                        break;

                    case 7:
                        text += "Erudite Obfuscation";
                        break;
                    case 8:
                        text += "Concealed Truths";
                        break;
                    case 9:
                        text += "Privy Trust";
                        break;
                    case 10:
                        text += "Inner Knowledge";
                        break;
                    case 11:
                        text += "Constrained Discretion";
                        break;
                    case 12:
                        text += "Esoteric Reality";
                        break;
                    case 13:
                        text += "Lurking Ambuscade";
                        break;

                    case 14:
                        text += "Naked Essence";
                        break;
                    case 15:
                        text += "the Fervid Soul";
                        break;
                    case 16:
                        text += "Cheery Exhilaration";
                        break;
                    case 17:
                        text += "Vitality";
                        break;
                    case 18:
                        text += "Absolute Innascibility";
                        break;
                    case 19:
                        text += "Pleasant Torpor";
                        break;
                    case 20:
                        text += "the Journey's End";
                        break;

                    case 21:
                        text += "Practiced Sagacity";
                        break;
                    case 22:
                        text += "Adroit Readiness";
                        break;
                    case 23:
                        text += "Conscious Insight";
                        break;
                    case 24:
                        text += "Ingenious Success";
                        break;
                    case 25:
                        text += "Exquisite Sensation";
                        break;
                    case 26:
                        text += "Poignant Memory";
                        break;
                    case 27:
                        text += "Dull Oblivion";
                        break;

                    case 28:
                        text += "Assured Credence";
                        break;
                    case 29:
                        text += "Seeking Comprehension";
                        break;
                    case 30:
                        text += "Intelligent Incredulity";
                        break;
                    case 31:
                        text += "Sufficient Omniscience";
                        break;
                    case 32:
                        text += "Hesitant Cognizance";
                        break;
                    case 33:
                        text += "Mature Nescience";
                        break;
                    case 34:
                        text += "Mindless Dolour";
                        break;

                    case 35:
                        text += "Exuberant Creation";
                        break;
                    case 36:
                        text += "Portentous Gloom";
                        break;
                    case 37:
                        text += "the Unpathed Waters";
                        break;
                    case 38:
                        text += "the Living Glebe";
                        break;
                    case 39:
                        text += "Effulgent Radiance";
                        break;
                    case 40:
                        text += "Novel Tempestuousness";
                        break;
                    case 41:
                        text += "Universal Ruin";
                        break;
                }
                text += "    The month of ";
                switch (week / 7) {
                    case 0:
                        text += "Silence";
                        break;
                    case 1:
                        text += "Secrets";
                        break;
                    case 2:
                        text += "Being";
                        break;
                    case 3:
                        text += "Experience";
                        break;
                    case 4:
                        text += "Thought";
                        break;
                    case 5:
                        text += "Spirit";
                        break;
                }
                text += "    " + year + "ST";
            }
            break;
            case 3: {
                final double del = (dusk - dawn) / 4.0;
                final double del2 = (1.0 - dusk) / 2.0;
                if (hour < (dawn / 2)) {
                    text = "Late Night";
                } else if (hour < dawn) {
                    text = "Dawning";
                } else if (hour < dawn + del) {
                    text = "Early Morning";
                } else if (hour < dawn + 2 * del) {
                    text = "Early Day";
                } else if (hour < dawn + 3 * del) {
                    text = "Early Eve";
                } else if (hour < dusk) {
                    text = "Late Eve";
                } else if (hour < dusk + del2) {
                    text = "Gloaming";
                } else {
                    text = "Early Night";
                }
                final int dd = day + 7 * week;
                text += "    Day " + dd + " " + year + "ST";
            }
            break;
            case 4: {
                if (hour < dawn) {
                    part = 6.0 * ((hour / dawn) + 1.0);
                    hr = (int) part;
                    min = (int) ((part - hr) * 60.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Night hour " + hr + ":" + mm;
                } else if (hour < dusk) {
                    part = 12.0 * (hour - dawn) / (dusk - dawn);
                    hr = (int) part;
                    min = (int) ((part - hr) * 60.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Day hour " + hr + ":" + mm;
                } else {
                    part = 6.0 * (hour - dusk) / (1.0 - dusk);
                    hr = (int) part;
                    min = (int) ((part - hr) * 60.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Night hour " + hr + ":" + mm;
                }
                text += "    " + DAY_OF_WEEK[day] + "    ";

                if (week < 5 * season) {
                    text += WEEK_OF_SEASON[week % season] + " week, "
                            + SEASON_OF_YEAR[week / season] + " season " + year + "ST";

                } else {
                    final int w = week - 5 * season + 1;
                    text += "Sacred Week " + w + " " + year + "ST";
                }
            }
            break;
            case 5: {
                if (hour < dawn) {
                    part = 5.0 * ((hour / dawn) + 1.0);
                    hr = (int) part;
                    min = (int) ((part - hr) * 100.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Night hour " + hr + "." + mm;
                } else if (hour < dusk) {
                    part = 15.0 * (hour - dawn) / (dusk - dawn);
                    hr = (int) part;
                    min = (int) ((part - hr) * 100.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Day hour " + hr + "." + mm;
                } else {
                    part = 5.0 * (hour - dusk) / (1.0 - dusk);
                    hr = (int) part;
                    min = (int) ((part - hr) * 100.0);
                    mm = Integer.toString(min);
                    if (min < 10) {
                        mm = "0" + mm;
                    }
                    text = "Night hour " + hr + "." + mm;
                }
                final int dd = day + 7 * week;
                text += "    Day " + dd + " " + year + "ST";
            }
            break;
        }
        calendar.setText(text);
    }

    private double setHarmonic() {
        double dday = (7.0 * week + day);
        yrpart = dday / 294.0;
        domeConfiguration = E2Param.getTilts(week, day, hour);
        dday += hour;
        return dday;
    }

    /**
     * Spin angle of the dome from Spring equinox
     * @return double spin angle in radians
     */
    public double spin() {
        return Math.PI * 2.0
                * ((hour - 0.75) // daily motion
                + yrpart);	// annual motion
    }

    /**
     * Tilt of the dome from zero, + -> North
     * @return double tilt angle in radians
     */
    public double slide(final E2Param.TiltType index) {
        return (Double) domeConfiguration.getSecond().get(index.ordinal()) * Math.PI / 180.0;
    }

    /**
     * Constructor - places UI components in a Panel
     */
    public EphemerisIIControlPanel(final JPanel aux, final JTextField hitfield) {
        super();
        setLayout(new GridLayout(0, 2, 10, 2));

        // Time and day reporting/ holyday selection  (top left)
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(0, 1));
        //p.setBackground(Color.lightGray);

        calendarList.setSelectedIndex(0);
        calendarList.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                setLocal();
            }
        });
        final JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(0, 1));

        p2.add(new JLabel("<html><u>Calendar:</u></html>"));
        p2.add(calendarList);
        p2.add(new JLabel(""));
        p2.add(new JLabel("<html><u>Jump to Holy Day of</u></html>"));
        p.add(p2);

        //aux.setLayout(new GridLayout(1, 0));
        calendar = new JTextField();
        calendar.setEditable(false);
        aux.add(calendar);
        aux.add(hitfield);

        calendar.setText("Freezeday 00:00:00     Disorder week Sea season 1600ST");

        holyDays = new JList(cults);//TODO (4, false);
        holyDays.setVisibleRowCount(4);
        holyDays.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        holyDays.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(final ListSelectionEvent e) {
                handleList();
            }
        });

        p.add(new JScrollPane(holyDays));
        add(p);

        // Time control slider  (top right)
        p = new JPanel();
        p.setLayout(new GridLayout(0, 2));
        p.add(new JLabel("<html><u>Current Time:</u></html>"));
        p.add(new JLabel(""));
        p.add(new JLabel("Minutes"));
        minbar = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        minbar.addChangeListener(this);
        p.add(minbar);
        p.add(new JLabel("Hours"));
        hrbar = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        hrbar.addChangeListener(this);
        p.add(hrbar);
        p.add(new JLabel("Day of week"));
        dowbar = new JSpinner(new SpinnerNumberModel(0, 0, 6, 1));
        dowbar.addChangeListener(this);
        p.add(dowbar);
        p.add(new JLabel("Week"));
        wkbar = new JSpinner(new SpinnerNumberModel(0, 0, 41, 1));
        wkbar.addChangeListener(this);
        p.add(wkbar);
        p.add(new JLabel("Year"));
        yrbar = new JSpinner(new SpinnerNumberModel(1600, -11000, 2500, 1));
        yrbar.addChangeListener(this);
        p.add(yrbar);
        add(p);
        setLabel();

        // Step interval selection (2nd row left)
        p = new JPanel();
        p.setLayout(new GridLayout(0, 1));
        //p.setBackground(Color.lightGray);

        p.add(new JLabel(""));
        p.add(new JLabel("<html><u>Time step:</u></html>"));

        p.add(intervalList);
        final E2TimePicker pick = new E2TimePicker(freeDelta);

        intervalList.addItemListener(
                new ItemListener() {

                    public void itemStateChanged(final ItemEvent e) {
                        pick.setEnabled(intervalList.getSelectedIndex() == 7);
                    }
                });

        p.add(pick);
        pick.setEnabled(intervalList.getSelectedIndex() == 7);

        cbreverse = new JCheckBox("Reverse", false);
        p.add(cbreverse);
        ticker = new JButton("Start");
        ticker.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                ticking = !ticking;
                freeDelta = pick.get();
                if (ticking) {
                    ticker.setText("Stop");
                } else {
                    ticker.setText("Start");
                }
            }
        });

        p.add(ticker);
        p.add(new JLabel(""));
        add(p);

        // Display selection (2nd row right)
        p = new JPanel();
        p.setLayout(new GridLayout(0, 1, 20, 3));
        p.add(new JSeparator());
        p.add(new JLabel("<html><u>Set time to:</u></html>"));
        //p.setBackground(Color.lightGray);
        p.add(new E2SelfButton("Midnight 00h", this, 0));
        p.add(new E2SelfButton("Dawn", this, 1));
        p.add(new E2SelfButton("Noon", this, 2));
        p.add(new E2SelfButton("Dusk", this, 3));
        p.add(new E2SelfButton("Midnight 24h", this, 4));
        p.add(new JLabel(""));
        add(p);

        locator = new E2LocatorPanel(this);
        add(locator);

        lookat = new E2LookPanel();
        add(lookat);

        setSize(300, 500);
    }

    public void setTimeOfDay(final int i) {
        switch (i) {
            case 0:
                hour = 0.0;
                break;
            case 1:
                hour = 0.25;
                setHarmonic();
                hour = (1.0 - getDayLength()) / 2.0; // fraction of day after midnight
                break;
            default:
            case 2:
                hour = 0.5;
                break;
            case 3:
                hour = 0.75;
                setHarmonic();
                hour = (1.0 + getDayLength()) / 2.0; // fraction of day after midnight
                break;
            case 4:
                hour = 86399.0 / 86400.0;
                break;
        }
        setTime();
    }

    /**
     * Gets radial position of viewer
     * @return double fraction of distance from viewpoint to dome
     */
    public double getOffset() {
        if (null == locator) {
            return 0;
        }
        return locator.r;
    }

    /**
     * Gets angular position of viewer
     * @return double angle of viewer anticlockwise from west
     */
    public double getBearing() {
        if (null == locator) {
            return 0;
        }
        return (Math.PI / 2.0) - locator.theta;
    }

    /**
     * Sets holy day list visibility suitably
     */
    public void adjust() {
        //TODO holyDays.makeVisible(holyDays.getSelectedIndex());
    }

    public boolean getUp() {
        return lookat.up;
    }

    public double getLook() {
        return lookat.bearing;
    }

    public void stateChanged(final ChangeEvent evt) {
        if (ticking) {
            return;
        }
        seconds = 0;
        setLabel();
    }

    /**
     * Performs event handling as per Java 1.0 for sliders
     * @return boolean state
     */
    public void handleList() {
        if (ticking || holyDays.getSelectedIndex() < 0) {
            return;
        }

        final int day = dates[holyDays.getSelectedIndex()] - 1;
        dowbar.setValue(day % 7);
        wkbar.setValue(day / 7);
        setLabel();
        holyDays.clearSelection();
    }
}

/* end of file ControlPanel.java */
