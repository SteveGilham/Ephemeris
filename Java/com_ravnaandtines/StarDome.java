package com_ravnaandtines;

/**
 *  Class StarDome & associated private classes
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
 *  All rights reserved.  For full licence details see file Main.java
 *
 * @author Mr. Tines
 * @version 1.0 11-Oct-1997
 *
 */
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

import net.sf.functionalj.*;
import net.sf.functionalj.tuple.*;

class RandomFaintStar {

    private final Random noise;
    
    /**
     * Generate a new random star generator
     * @param i seed value
     */
    public RandomFaintStar(final int i) {
        noise = new Random((long)i);

    }

    /**
     * Generate stars with uniform distribution over a 300-unit radius circle
     * @return  the next location
     */
    public Point next() {
        final double r = 300.0 * Math.sqrt(noise.nextDouble());
        final double theta = 2.0 * Math.PI * noise.nextDouble();
        return new Point(
                Transform.round(r * Math.cos(theta)),
                Transform.round(r * Math.sin(theta)));
    }
}

/**
 * Double-valued polar-coordinate Point type
 * @author Mr. Tines
 */
class Doublet extends PairUni<Double> {

    /**
     * Create a new Doublet
     * @param r radial coordinate
     * @param t angular coordinate
     */
    public Doublet(final double r, final double t) {
        super(r, t);
    }
}

/**
 * Double-valued 3D Cartesian Point type
 * @author Mr. Tines
 */
class XYZ extends TripleUni<Double> {

    /**
     * Get the x coordinate
     * @return  first member
     */
    public double x() { //NOPMD
        return getFirst();
    }

    /**
     * Get the y coordinate
     * @return  second member
     */
    public double y() { //NOPMD
        return getSecond();
    }

    /**
     * Get the z coordinate
     * @return  last member
     */
    public double z() { //NOPMD
        return getThird();
    }
}

/**
 * Double-valued alt-azimuth Point type
 * @author Mr. Tines
 */
class AltAz extends PairUni<Double> {

    /**
     * Obvious constructor
     * @param altitude  altitude in radians
     * @param azimuth   azimuth in radians
     */
    public AltAz(final double altitude, final double azimuth) {
        super(altitude, azimuth);
    }

    /**
     * Gets the value of the altitude
     * @return  value in radians
     */
    public double altitude() {
        return getFirst();
    }

    /**
     * Gets the value of the azimuth
     * @return  value in radians
     */
    public double azimuth() {
        return getSecond();
    }
}

/**
 * Behaviours for a star on the dome
 * @author Mr. Tines
 */
class DomeStar {

    private double x, y, magnitude;
    private char[] name, marker;
    private Color hue;
    private double radius;
    private int diam;
    private final Collection<Doublet> polar;
    private static final int N_FAINT = 12;
    final public static Double LARGE = 20000.0;

//"Seed",129,1565,20,"Tree", Color.green
    
    /**
     * Define a new star
     * @param name      its constellation name
     * @param x         x-coordinate on arbitrary grid
     * @param y         y-coordinate ditto
     * @param magnitude brightness ~ number of pixels to cover or -1 for faint star scatter
     * @param marker    name to plot if any
     * @param hue       colour of star
     */
    public DomeStar(final String name, final int x, final int y, final int magnitude, final String marker, final Color hue) {
        this.name = name.toCharArray();
        this.x = x;
        this.y = y;
        this.magnitude = magnitude;
        this.marker = marker.toCharArray();
        this.hue = hue;
        if (magnitude > 0) {
            radius = Math.sqrt((double) magnitude);
            diam = (int) Math.rint(radius);
            radius /= 2;
            polar = new ArrayList<Doublet>();
            polar.add( new Doublet(
              Math.sqrt((double) (x * x + y * y)) / StarDome.RADIAN,
              Math.atan2((double) y, (double) x)));
        } else {
            final RandomFaintStar ran = new RandomFaintStar((x << 16) + (y & 0xFFFF));
            polar = Functions.map(
                    new Function1Impl<Doublet, Integer>() {

                        public Doublet call(final Integer i) {
                            final Point d = ran.next();

                            final double xx = (double) d.x + x;
                            final double yy = (double) d.y + y;
                            return new Doublet(
                                    Math.sqrt(xx * xx + yy * yy) / StarDome.RADIAN,
                                    Math.atan2(yy, xx));
                        }
                    },
                    Functional.range(0, N_FAINT));
        }
    }

    /**
     * Define a new star with default colouring by magnitude
     * @param name      its constellation name
     * @param x         x-coordinate on arbitrary grid
     * @param y         y-coordinate ditto
     * @param magnitude brightness ~ number of pixels to cover or -1 for faint star scatter
     * @param marker    name to plot if any
     */
    public DomeStar(final String name, final int x, final int y, final int magnitude, final String marker) {
        this(name, x, y, magnitude, marker, (magnitude < 0) ? Color.cyan : Color.white);
    }

    /**
     * 
     * @param spin      dome rotation angle
     * @param th        star angular coordinate on sky
     * @param radius    star radial coordinate from pole
     * @param view      transform
     * @param g         graphics context
     * @param drawMe    how to draw the star (always return Boolean.TRUE)
     * @return          { ifHidden, where }
     */
    private Pair<Boolean, Point> draw(
            final double spin,
            final double th,
            final double radius,
            final View view,
            final Graphics g,
            final Function2<Boolean, Graphics, Point> drawMe) {
        // get resultant 3D position
        // i) spin the dome
        // ii) generate 3D coordinate if untilted
        // iii) apply tilt about x axis
        // iv) apply offset viewing position
        final XYZ xyz = tilt(toCartesian(radius, -th - spin), view);
        if (xyz.z() >= 0) {
            final java.awt.Point nxy = Transform.getAltAz(view, xyz.x(), xyz.y(), xyz.z());
            if (nxy.x > 0)
            {
                drawMe.call(g, nxy);
                return new Pair<Boolean, Point>(Boolean.FALSE, nxy);
            }
        }

        //Below horizon
        return new Pair<Boolean, Point>(Boolean.TRUE, null);
    }

    /**
     * Draw as a point
     * @param g     graphics context
     * @param nxy   screen location
     */
    private void drawDot(final Graphics g, final Point nxy) {
        g.drawLine(nxy.x, nxy.y, nxy.x, nxy.y);
    }

    /**
     * Draw as a disk
     * @param g     graphics context
     * @param nxy   screen location
     */
    private void drawBig(final Graphics g, final java.awt.Point pxy) {
        g.fillOval(pxy.x, pxy.y, diam, diam);
        g.drawLine(pxy.x + diam / 2, pxy.y + diam / 2, pxy.x + diam / 2, pxy.y + diam / 2);
    }

    /**
     * Transform star spherical location on unit sphere to Cartesian
     * @param declination     declination
     * @param rightAscension  right ascension
     * @return                Cartesian location
     */
    private XYZ toCartesian(final double declination, final double rightAscension) {
        final XYZ c = new XYZ();
        // ii) generate 3D coordinate if untilted
        c.setFirst(Math.cos(rightAscension) * Math.sin(declination));
        c.setSecond(Math.sin(rightAscension) * Math.sin(declination));
        c.setThird(Math.cos(declination));

        return c;
    }

    /**
     * Convert from dome coordinates to observer sky coordinates
     * @param initial   Dome position as Cartesian
     * @param view      Transform to apply
     * @return          observer relative position
     */
    private XYZ tilt(final XYZ initial, final View view) {
        final XYZ c = new XYZ();
        c.setFirst(initial.x() * Math.cos(view.tiltVector)
                + initial.z() * Math.sin(view.tiltVector)
                - view.offset * Math.cos(view.bearing));
        c.setSecond(initial.y());
        c.setThird(-initial.x() * Math.sin(view.tiltVector)
                + initial.z() * Math.cos(view.tiltVector)
                - view.offset * Math.sin(view.bearing));

        return c;
    }

    /**
     * How to draw a normal star -- wraps drawBig
     */
    private final Function2<Boolean, Graphics, Point> drawMajor =
            new Function2Impl<Boolean, Graphics, Point>() {

                        public Boolean call(final Graphics g, final Point xy) {
                            drawBig(g, xy);
                            return Boolean.TRUE;
                        }
                    };

    /**
     * How to draw a faint star -- wraps drawDot
     */
    private final Function2<Boolean, Graphics, Point> drawMinor =
                    new Function2Impl<Boolean, Graphics, Point>() {

                        public Boolean call(final Graphics g, final Point xy) {
                            drawDot(g, xy);
                            return Boolean.TRUE;
                        }
                    };
                    
    /**
     * A no-op drawing function -- used when just computing the location
     */
    private final Function2<Boolean, Graphics, Point> noop =
                new Function2Impl<Boolean, Graphics, Point>() {

                    public Boolean call(final Graphics g, final Point xy) {
                        return Boolean.TRUE;
                    }
                };

    /**
     * Draws this star
     * @param g             Graphics to draw to
     * @param spin          giving rotation angle about pole star
     * @param slide         tilt of the dome
     * @param radius        pixels from overhead to horizon
     * @param x0            pixel location of overhead (x coord)
     * @param y0            pixel location of overhead (y coord)
     * @param names         boolean if names are to be plotted
     * @param radialOffset  fraction offset of observer from M's pool to dome
         * @param bearing   angle of radius vector from M's pool to observer
     * @param projection    style of projection of dome
     */
    public void draw(final Graphics g, final double spin,
            final boolean names, final View view) {
        g.setColor(hue);
        Pair<Boolean, Point> act = null;

        final Function2<Boolean, Graphics, Point> doDraw =
                (magnitude > 0)
                ? drawMajor
                : drawMinor;

        boolean named = false;
        for (Doublet star : polar) {
            act = draw(spin, star.getSecond(), star.getFirst(), view, g,
                    doDraw);
            if (act.getFirst()) {
                continue;
            }
            named = names;
        }

        if (named && marker.length > 0) {
            g.setColor(Color.cyan);
            g.drawChars(marker, 0, marker.length, act.getSecond().x, act.getSecond().y);
        }

    }

    /**
     * Locates this star
     * @param ex    Cursor x position
     * @param ey    Cursor y position
     * @param view  Transform
     * @param spin  dome rotation about axis
     * @return { how far from the mouse, if not faint }
     */
    public Pair<Double, Boolean> locate(final int ex, final int ey, final View view,
            final double spin) {

        Pair<Boolean, Point> act = null;
        double range = LARGE;

        for (Doublet star : polar) {
            act = draw(spin, star.getSecond(), star.getFirst(), view, null, noop);
            if (act.getFirst()) {
                continue;
            }

            final double x0 = act.getSecond().x - ex;
            final double y0 = act.getSecond().y - ey;
            final double crit = (magnitude > 0)
                    ? diam + 5.0 : 25.0;
            final double margin = Math.sqrt(x0 * x0 + y0 * y0);
            if (margin < crit && margin < range) {
                range = margin;
            }
        }
        return new Pair<Double, Boolean>(range,
                Boolean.valueOf(magnitude > 0));
    }

    /**
     * Formats the star as name, location magnitude and any label
     * @return
     */
    @Override
    public String toString() {
        return String.format("\"%s\" %d %d %d \"%s\"",
                new String(name),
                Math.round(x),
                Math.round(y),
                Math.round(magnitude),
                new String(marker));
    }
}

/**
 * All the stars collectively
 * @author Mr. Tines
 */
class StarDome {

    private static final String CITY = "City";
    private static final String EMPTY = "";
    private static final String FIELDS = "Fields";
    private static final String FOREST = "Forest";
    private static final String LOVE_STARS = "Love Stars";
    private static final String RIVER = "River";
    private static final String WAR_STARS = "War Stars";
    private final List<DomeStar> host = new ArrayList<DomeStar>(100);
    /**
     * The star "Youth", at x,y = 113,2540 is on the eastern horizon
     * at sunset on the equinox.  The equinox happens at midday that day;
     * the tilt angle can be neglected (<~0.1 degree) for this model
     * so the scale to 90 degrees from the zenith is 2542.5 units
     */
    public final static double RADIAN = 1618.614;
    /**
     * The rotation of "Youth" from the x axis is
     */
    private final static double SKEW = 0.04445887;

    /**
     * Draws all the stars according to the Dome configuration
     * and canvas size.
     * @param g     Graphics to draw to
     * @param angle double rotation of dome from dusk on spring equinox
     * @param names boolean if names are to be plotted
     * @param view  View geometry of the projection etc.
     */
    public void draw(final Graphics g, final double angle,
            final boolean names, final View view) {
        for (DomeStar star : host) {
            star.draw(g, SKEW - angle, names, view);
        }
    }

    /**
     * Locates the star nearest the indicated position
     * @param ex    cursor x coordinate
     * @param ey    cursor y coordinate 
     * @param view  geometry of the projection
     * @param angle rotation of dome from dusk on spring equinox
     * @return      name (toString) of closest star
     */
    public String locate(final int ex, final int ey, final View view,
            final double angle) {

        double r1 = DomeStar.LARGE / 2.0;
        double r2 = r1;

        DomeStar candidate = null;
        DomeStar alternate = null;
        int i = -1;
        int j = -1;

        int index = 0;
        for (DomeStar star : host) {
            final Pair<Double, Boolean> rr = star.locate(ex, ey, view, SKEW - angle);
            if (rr.getSecond() && rr.getFirst() < r1) {
                candidate = star;
                r1 = rr.getFirst();
                i = index;

            }
            if (!rr.getSecond() && rr.getFirst() < r2) {
                alternate = star;
                r2 = rr.getFirst();
                j = index;

            }
            ++index;
        }

        return null == candidate
                ? (null == alternate ? EMPTY
                : Integer.toString(j) + " " + alternate.toString())
                : Integer.toString(i) + " " + candidate.toString();
    }

    /**
     * Default constructor - assumes that the stars all rotate with the
     * dome at the current moment.
     * TODO -- make this account for the separate domes.
     * TODO -- data driven?
     */
    public StarDome() {
        host.add(new DomeStar("Pole Star", 1, 1, 50, "Pole Star"));
        
        // Arraz angle -66.4768 degrees = 293.5232 = 239.7282 days from E
        host.add(new DomeStar("Arraz", -425, 185, 35, "Arraz"));
        host.add(new DomeStar("Ourania", 355, -325, 20, EMPTY));
        host.add(new DomeStar("Evandal", 446, -1285, 20, "Oropum"));
        host.add(new DomeStar("Everina", 655, -760, 20, "Rice"));
        host.add(new DomeStar("Conspirator", 1451, -969, 20, "Whisperers"));
        host.add(new DomeStar("Maw", 910, -70, 20, EMPTY));
        host.add(new DomeStar("Eye", 1120, -189, 20, EMPTY));
        host.add(new DomeStar("Neck", 895, 96, 20, EMPTY));
        host.add(new DomeStar("Chest", 805, 305, 20, "Star Dragon"));
        host.add(new DomeStar("Wing", 835, 576, 20, EMPTY));
        host.add(new DomeStar("Belly", 565, 665, 20, EMPTY));
        host.add(new DomeStar("Tail", 459, 935, 20, EMPTY));
        host.add(new DomeStar("Stinger", -6, 1070, 20, EMPTY));
        host.add(new DomeStar("Tail", 219, 1085, 20, EMPTY));
        host.add(new DomeStar("Seed", 129, 1565, 20, "Tree", Color.green));
        host.add(new DomeStar("Erkonus", -1445, 544, 20, "Erkonus"));
        host.add(new DomeStar("Dove", -1355, -611, 20, "Dove"));
        host.add(new DomeStar("Harp", -574, -850, 20, "Harp"));
        host.add(new DomeStar("Steward", -185, -850, 20, "Steward"));
        host.add(new DomeStar("(Tongue)", -49, -1600, 5, EMPTY));
        host.add(new DomeStar("Eye", 71, -2005, 20, "Lorion"));
        host.add(new DomeStar("Tail", 0, -2500, 20, EMPTY));
        host.add(new DomeStar("Leg", 866, -2185, 20, "Thunderer"));
        host.add(new DomeStar("Leg", 761, -2350, 20, EMPTY));
        host.add(new DomeStar("Arm", 926, -2380, 20, EMPTY));
        host.add(new DomeStar("Arm", 1166, -2154, 20, EMPTY));
        host.add(new DomeStar("Varnaga", 1241, -1614, 20, "Crocodile"));
        host.add(new DomeStar("Vergenari", 1961, -1479, 20, "Sow"));
        host.add(new DomeStar("Bull", 2140, -819, 20, "Bull"));
        host.add(new DomeStar("Oasis", 2140, 51, 20, "Oasis"));
        host.add(new DomeStar("Thasus", 1780, 666, 20, "Thasus"));
        host.add(new DomeStar("Pincer", 1359, 1506, 20, EMPTY));
        host.add(new DomeStar("Womb", 1269, 1716, 20, "Scorpion"));
        host.add(new DomeStar("Jewel Flower", 474, 1910, 20, "Flowers"));
        
        // Youth = 2.54 degrees = 2.080 days from East
        host.add(new DomeStar("Youth", 113, 2540, 20, "Youth"));
        host.add(new DomeStar("(Shafesora)", -1911, 1354, 5, "Marsh"));
        host.add(new DomeStar("Lion", -2285, 544, 20, "Lion"));
        host.add(new DomeStar("Swan", -1955, -206, 20, "Swan"));
        host.add(new DomeStar("Bow", -1459, -1331, 20, EMPTY));
        host.add(new DomeStar("Heart", -1339, -1571, 20, "Hunter"));
        host.add(new DomeStar("Hip", -1459, -1661, 20, EMPTY));
        host.add(new DomeStar("Knee", -1609, -1601, 20, EMPTY));
        host.add(new DomeStar("Pot", -814, -1795, 20, "Pot"));
        host.add(new DomeStar("Fan", -1024, -2336, 20, "Fan"));
        host.add(new DomeStar("Eye", -94, -1930, 20, EMPTY));
        host.add(new DomeStar("Marker", -94, -2290, 20, EMPTY));
//      host.add(new DomeStar("Kalikos",-378,-2785,20,"Kalikos", Color.red));
        host.add(new DomeStar("Chorus", -260, 410, 10, "Chorus"));
        host.add(new DomeStar(EMPTY, -380, 320, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -470, 290, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -545, 65, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -440, 95, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -380, -10, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -485, -40, 10, EMPTY));
        host.add(new DomeStar("Cook", -110, -265, 10, "Cook"));
        host.add(new DomeStar(EMPTY, -80, -340, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -155, -340, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -170, -400, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -245, -415, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -140, -445, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 430, -400, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 280, -400, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 190, -340, 10, EMPTY));
        host.add(new DomeStar("Officers", 190, 110, 10, "Officers"));
        host.add(new DomeStar(EMPTY, 160, 245, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 130, 440, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 325, 50, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 401, -1015, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 596, -1450, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 701, -1435, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 446, -1540, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 386, -1345, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 566, -1150, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 535, -775, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 805, -700, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 715, -520, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 610, -535, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 565, -475, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 475, -535, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 445, -685, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 385, -595, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 295, -535, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1180, -909, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1301, -999, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1225, -744, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1330, -849, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1600, -969, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 865, 186, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 820, 440, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 970, 621, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 970, 726, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 939, 861, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 774, 1055, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1030, 726, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 564, 830, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 460, 800, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 159, 995, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -81, 1655, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 54, 1715, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 204, 1790, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 264, 1655, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 114, 1925, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 159, 2015, 10, EMPTY));
        host.add(new DomeStar("Yoke", -590, 905, 10, "Yoke"));
        host.add(new DomeStar(EMPTY, -546, 1010, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -486, 995, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -545, 845, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -500, 860, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1370, 469, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1490, 394, 10, EMPTY));
        host.add(new DomeStar("Hawk", -860, -101, 10, "Hawk"));
        host.add(new DomeStar(EMPTY, -830, -146, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -800, -220, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -920, -146, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -890, -221, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -845, -280, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1355, -356, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1475, -716, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1310, -701, 10, EMPTY));
        host.add(new DomeStar("Quail", -1399, -896, 10, "Quail"));
        host.add(new DomeStar(EMPTY, -1294, -986, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1414, -1001, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1309, -1076, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -709, -895, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -649, -955, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -334, -970, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -289, -955, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -214, -940, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 56, -1390, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 251, -1345, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 41, -1225, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 10, -1000, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 145, -1000, 10, EMPTY));
        host.add(new DomeStar("Fishes", 26, -1480, 10, "Fishes"));
        host.add(new DomeStar(EMPTY, 101, -1600, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 56, -1750, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -64, -1735, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 131, -2170, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -109, -2155, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 851, -2305, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 956, -2259, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1046, -2214, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1316, -1689, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1286, -1539, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1346, -1479, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1091, -1584, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1181, -1389, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1946, -1659, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 2126, -1374, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 2155, -939, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 2156, -1029, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 2215, -714, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 2260, -804, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1990, 531, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1554, 846, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1659, 1056, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1389, 1821, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1334, 2048, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1134, 2091, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 1149, 1656, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 999, 1716, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 909, 1896, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 609, 1970, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 489, 2135, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 98, 2675, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 23, 2435, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -51, 2345, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 84, 2390, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 189, 2465, 10, EMPTY));
        host.add(new DomeStar(EMPTY, 248, 2375, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -456, 2045, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -366, 1940, 10, EMPTY));
        host.add(new DomeStar("Willows", -351, 1745, 10, "Willows"));
        host.add(new DomeStar(EMPTY, -456, 1655, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -276, 1670, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -366, 1580, 10, EMPTY));
        host.add(new DomeStar("Plough", -1101, 1864, 10, "Plough"));
        host.add(new DomeStar(EMPTY, -1116, 1759, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1056, 1759, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -981, 1819, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -996, 1759, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1071, 1699, 10, EMPTY));
        host.add(new DomeStar("Veridna", -1656, 2384, 10, "Veridna"));
        host.add(new DomeStar(EMPTY, -1461, 2474, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1611, 2504, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1716, 2474, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1701, 2594, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1686, 2699, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2495, 574, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2390, 424, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2225, 454, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2165, 394, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2210, -71, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2120, -116, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2000, -41, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1880, -161, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1865, -206, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2030, -341, 10, EMPTY));
        host.add(new DomeStar("Hag", -2615, -417, 10, "Hag"));
        host.add(new DomeStar(EMPTY, -2705, -462, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2885, -447, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2825, -627, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2570, -762, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2614, -852, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2540, -822, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2464, -777, 10, EMPTY));
        host.add(new DomeStar("Borna", -2674, -927, 10, "Borna"));
        host.add(new DomeStar(EMPTY, -2404, -852, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2254, -1016, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2224, -1121, 10, EMPTY));
        host.add(new DomeStar("Deer", -2254, -1211, 10, "Deer"));
        host.add(new DomeStar(EMPTY, -2119, -1256, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2119, -1316, 10, EMPTY));
        host.add(new DomeStar("Firestick", -2614, -1362, 10, "Firestick"));
        host.add(new DomeStar(EMPTY, -2614, -1437, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2599, -1512, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2584, -1572, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2599, -1617, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2494, -1602, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1399, -1481, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1594, -1391, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1684, -1436, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1774, -1511, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -2029, -1676, 10, EMPTY));
        host.add(new DomeStar("Groundhog", -2029, -1766, 10, "Groundhog"));
        host.add(new DomeStar(EMPTY, -1909, -2051, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -1774, -2036, 10, EMPTY));
        host.add(new DomeStar("Rabbits", -1744, -2126, 10, "Rabbits"));
        host.add(new DomeStar(EMPTY, -1744, -2216, 10, EMPTY));
        host.add(new DomeStar("Raven", -589, -2125, 10, "Raven"));
        host.add(new DomeStar(EMPTY, -574, -2215, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -649, -2230, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -679, -2305, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -529, -2260, 10, EMPTY));
        host.add(new DomeStar(EMPTY, -603, -2320, 10, EMPTY));

        host.add(new DomeStar(LOVE_STARS, -1835, 1647, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -1881, 1459, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -1967, 1552, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -2000, 1678, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -2013, 1859, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -2166, 1714, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -2222, 1981, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -2239, 1771, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -2403, 1862, -1, EMPTY));

        host.add(new DomeStar(WAR_STARS, -1991, 1003, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2091, 1069, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2125, 1125, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2231, 1021, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2289, 1201, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2437, 1398, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2484, 1173, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2511, 1279, -1, EMPTY));
        host.add(new DomeStar(WAR_STARS, -2708, 1325, -1, EMPTY));

        host.add(new DomeStar(RIVER, 53, 3035, -1, EMPTY));
        host.add(new DomeStar(RIVER, 83, 2765, -1, EMPTY));
        host.add(new DomeStar(RIVER, 98, 2450, -1, EMPTY));
        host.add(new DomeStar(RIVER, 99, 2225, -1, EMPTY));
        host.add(new DomeStar(RIVER, 99, 2045, -1, EMPTY));
        host.add(new DomeStar(RIVER, 129, 1790, -1, EMPTY));
        host.add(new DomeStar(RIVER, 189, 1520, -1, EMPTY));
        host.add(new DomeStar(RIVER, 219, 1340, -1, EMPTY));
        host.add(new DomeStar(RIVER, 354, 1100, -1, EMPTY));
        host.add(new DomeStar(RIVER, 534, 875, -1, EMPTY));
        host.add(new DomeStar(RIVER, 715, 650, -1, EMPTY));
        host.add(new DomeStar(RIVER, 880, 486, -1, EMPTY));
        host.add(new DomeStar(RIVER, 955, 261, -1, EMPTY));
        host.add(new DomeStar(RIVER, 1015, -115, -1, EMPTY));
        host.add(new DomeStar(RIVER, 910, -325, -1, EMPTY));
        host.add(new DomeStar(RIVER, 715, -610, -1, EMPTY));
        host.add(new DomeStar(RIVER, 415, -850, -1, EMPTY));
        host.add(new DomeStar(RIVER, 281, -1120, -1, EMPTY));
        host.add(new DomeStar(RIVER, 146, -1375, -1, EMPTY));
        host.add(new DomeStar(RIVER, 26, -1645, -1, EMPTY));
        host.add(new DomeStar(RIVER, -34, -1960, -1, EMPTY));
        host.add(new DomeStar(RIVER, -34, -2215, -1, EMPTY));
        host.add(new DomeStar(RIVER, -63, -2455, -1, EMPTY));
        host.add(new DomeStar(RIVER, -78, -2755, -1, EMPTY));
        host.add(new DomeStar(RIVER, -78, -2980, -1, EMPTY));
        host.add(new DomeStar(LOVE_STARS, -1923, 1619, 3, LOVE_STARS));
        host.add(new DomeStar(WAR_STARS, -2238, 1167, 3, WAR_STARS));
        host.add(new DomeStar(FOREST, -2770, 723, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2305, 453, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1915, 169, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1645, -176, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1390, -461, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1180, -806, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1164, -1286, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1284, -1691, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1389, -1991, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1614, -2321, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2800, 243, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2845, -191, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2785, -762, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2679, -1181, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2469, -1676, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2139, -2006, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1764, -1766, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2350, 79, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2335, -446, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2200, -866, -2, EMPTY));
        host.add(new DomeStar(FOREST, -2034, -1271, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1614, -1256, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1795, -896, -2, EMPTY));
        host.add(new DomeStar(FOREST, -1765, -536, -2, EMPTY));
        host.add(new DomeStar(FIELDS, -282, 2810, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -1361, 2539, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -1406, 1714, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -671, 2120, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -326, 1715, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -1436, 934, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -1315, 49, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -700, 80, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -1046, 1054, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -596, 1505, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -715, 665, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -730, -550, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -564, -1075, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -324, -1675, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -849, -2785, -3, EMPTY));
        host.add(new DomeStar(FIELDS, -594, -2200, -3, EMPTY));
        host.add(new DomeStar(CITY, -205, 228, -4, EMPTY));
        host.add(new DomeStar(CITY, 18, 259, -4, EMPTY));
        host.add(new DomeStar(CITY, 215, 150, -4, EMPTY));
        host.add(new DomeStar(CITY, 247, -40, -4, EMPTY));
        host.add(new DomeStar(CITY, 139, -203, -4, EMPTY));
        host.add(new DomeStar(CITY, -39, -276, -4, EMPTY));
        host.add(new DomeStar(CITY, -228, -153, -4, EMPTY));
        host.add(new DomeStar(CITY, -281, 30, -4, EMPTY));
    }
}

/* end of file StarDome.java */


