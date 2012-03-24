/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com_ravnaandtines;

import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import static java.lang.Math.*;
import java.util.Collection;

import net.sf.functionalj.*;
import net.sf.functionalj.util.*;
import net.sf.functionalj.tuple.Pair;

/**
 *
 * @author Alan
 */
final class Utils {

    private Utils() {
    }

    /**
     * Draws a line if both ends are visible
     * @param g     graphics context
     * @param from  start of line
     * @param to    end of line
     */
    public static void drawLine(final Graphics g, final Point from, final Point to) {
        if (from.x > 0 && from.y > 0 && to.x > 0 && to.y > 0) {
            g.drawLine(from.x, from.y, to.x, to.y);
        }
    }

    /**
     * Draws a set of characters if the key point is visible
     * @param g         graphics context
     * @param at        start point
     * @param text      The text as a char[]
     * @param length    how much of the text
     */
    public static void drawChars(final Graphics g, final Point at, final char[] text, final int length) {
        if (at.x > 0 && at.y > 0) {
            g.drawChars(text, 0, length, at.x, at.y);
        }
    }

    /**
     * Draws a string at the nominated location
     * @param g     graphics context
     * @param text  string to draw
     * @param at    zero point
     * @param dx    x offset
     * @param dy    y offset
     */
    public static void label(final Graphics g, final String text, final Point at, final int dx, final int dy) {
        final char[] c = text.toCharArray();
        g.setColor(Color.lightGray);
        g.drawChars(c, 0, c.length, at.x + dx, at.y + dy);
    }

    /**
     * Draws a circle if the upper-left corner is visible
     * @param g     graphics context
     * @param at    centre of circle
     * @param size  diameter
     */
    public static void drawCircle(final Graphics g, final Point at, final int size) {
        if (at.x > 0 && at.y > 0) {
            final int half = size / 2;
            g.fillOval(at.x - half, at.y - half, size, size);
        }
    }
}

class View { //NOPMD
    public int side, halfSide;          // dimensions in pixels of the canvas
    public double look;                 // bearing of horizon view
    public boolean up;                  // true if whole sky, else horizon
    public double bearing, offset;      // position of observer relative to Magasta's pool
    public double tiltVector;           // the tilt of the dome at this instant
}

final class Transform { //NOPMD -- "too many methods" -- fine grained methods

    private Transform() {
    }

    /**
     * Sensible wrapping of Math.round
     * @param v double value in
     * @return  integer out
     */
    public static int round(final double v) {
        return (int) Math.round(v);
    }

    /**
     * Turns an alt-az location into a screen location
     * @param altitude  altitude in radians
     * @param azimuth   azimuth in radians
     * @param view      transform description
     * @param combine   add or subtract operation
     * @return screen location in pixels
     */
    public static Point project(final double altitude, final double azimuth, final View view, final Function2<Integer, Integer, Integer> combine) {
        final int bit = round(view.halfSide * altitude * cos(azimuth));
        return new Point(
                combine.call(view.side / 2, bit),
                view.side / 2 - round(view.halfSide * altitude * sin(azimuth)));
    }

    /**
     * Turns a 3D Cartesian location into an alt-az pair
     * @param view  transform description
     * @param x     x coordinate (arbitrary units)
     * @param y     y coordinate (arbitrary units)
     * @param z     z coordinate (arbitrary units)
     * @return      alt-az (in radians)
     */
    public static Point getAltAz(final View view, final double x, final double y, final double z) {
        final double halfPi = PI / 2.0;
        if (view.up) {
            final double azimuth = atan2(x, y);
            final double altitude = atan2(sqrt(x * x + y * y), z) / halfPi;
            return project(altitude, azimuth, view, Operators.add);
        } else {
            // look along line with angle = look as pole
            // rotate this to x axis (need to frig signs)
            final double c = cos(view.look);
            final double s = sin(view.look);
            final double tx = x * c + y * s;
            final double ty = x * s - y * c;
            final double tz = z;
            // now use the x-axis as if it were z, z as y, y as x
            final double azimuth = atan2(ty, tz) + halfPi;
            final double altitude = atan2(sqrt(ty * ty + tz * tz), tx) / halfPi;
            if (tx < 0) {
                return new Point(-1, -1);
            } else {
                return project(altitude, azimuth, view, Operators.subtract);
            }
        }
    }

    /**
     * Gets a point on the Sun-path, parameterised by angle
     * @param view  transform description
     * @param angle position parameter of path
     * @return      alt-az (in radians)
     */
    public static Point sunpath(final View view, final double angle) {
        // i) 3D coordinate is
        final double y = -cos(angle) - view.offset * sin(view.bearing);
        final double x = sin(angle) * sin(view.tiltVector) - view.offset * cos(view.bearing);
        final double z = sin(angle) * cos(view.tiltVector);
        // ii) extract altitude and azimuth
        return getAltAz(view, x, y, z);
    }

    /**
     * Gets a point on the South-path, parameterised by angle
     * @param view  transform description
     * @param angle position parameter of path
     * @param dm    bearing of dodging mouth
     * @param em    bearing of east mouth
     * @return      alt-az (in radians)
     */
    public static Point southpath(final View view, final double angle, final double dm, final double em) {
        final double northpoint = (dm + PI - em) / 2.0; // where bisector is northmost
        final double subtend = northpoint - dm;         // angle from bisector

        final double ty = cos(subtend);
        final double tx = -sin(subtend) * cos(angle);
        final double tz = sin(subtend) * sin(angle);

        final double y = ty * sin(northpoint) - tx * cos(northpoint) - view.offset * sin(view.bearing);
        final double x = ty * cos(northpoint) + tx * sin(northpoint) - view.offset * cos(view.bearing);

        return getAltAz(view, y, x, tz);
    }

    /**
     * Gets a point on the Lightfore-path, parameterised by angle
     * @param view  transform description
     * @param angle position parameter of path
     * @param twist dome rotation at that parameter
     * @return      alt-az (in radians)
     */
    public static Point spinpath(final View view, final double angle, final double twist) {
        // i) raw 3D coordinate is
        double tx = -cos(angle);
        double ty = 0.0;
        double tz = sin(angle);

        // ii) unapply tilt about x axis
        double y = ty;
        double x = tx * cos(view.tiltVector) - tz * sin(view.tiltVector);
        double z = tx * sin(view.tiltVector) + tz * cos(view.tiltVector);

        // iii) spin
        x = tx * cos(twist) + ty * sin(twist);
        y = -tx * sin(twist) + ty * cos(twist);
        z = tz;

        // iv) reapply tilt about x axis
        ty = y;
        tx = x * cos(view.tiltVector) + z * sin(view.tiltVector);
        tz = -x * sin(view.tiltVector) + z * cos(view.tiltVector);

        // v) apply offset viewing position
        tx -= view.offset * cos(view.bearing);
        ty -= view.offset * sin(view.bearing);
        return getAltAz(view, tx, ty, tz);
    }
    
    /**
     * A parameter increment
     */
    private static final double DELTA = PI / 50;

    /**
     * Draw a path given a function describing it
     * @param g     graphics context
     * @param hue   path colour
     * @param path  parameter to alt-az function
     * @return      the first point drawn
     */
    private static Point drawPath(final Graphics g, final Color hue, final Function1<Point, Integer> path) {
        final Collection<Point> points = Functions.map(path, Functional.range(0, 51));
        final Collection<Point> second = ListUtils.tail(points);
        g.setColor(hue);
        Functions.each(
                new Function1Impl<Object, Pair<Point, Point>>() {

                    public Object call(final Pair<Point, Point> line) {
                        Utils.drawLine(g, line.getFirst(), line.getSecond());
                        return null;
                    }
                },
                Functional.zip(points, second));
        return ListUtils.first(points);
    }

    /**
     * Draws the Sun-path
     * @param g     graphics context
     * @param view  transform description
     */
    public static void drawSunpath(final Graphics g, final View view) {
        drawPath(g, Color.yellow,
                new Function1Impl<Point, Integer>() {

                    public Point call(final Integer i) {
                        return sunpath(view, i * DELTA);
                    }
                });
    }

    /**
     * Draws the South path
     * @param g     graphics context
     * @param view  transform description
     * @param dayno date for path description
     * @param hour  time of day on that date
     */
    public static void drawSouthpath(final Graphics g, final View view, final long dayno, final double hour) {
        final double em = Transform.eastMouth(dayno, hour);
        final double dm = Transform.dodgeMouth(dayno, hour);
        drawPath(g, Color.red,
                new Function1Impl<Point, Integer>() {

                    public Point call(final Integer i) {
                        return southpath(view, i * DELTA, dm, em);
                    }
                });
    }

    /**
     * Draws the Lightfore-path
     * @param g         graphics context
     * @param view      transform description
     * @param dayLength current length of day
     * @param hourAngle measure of time through the night
     */
    public static void drawLightforePath(final Graphics g, final View view,
            final double dayLength, final double hourAngle) {
        // current position is hA; each step represents (1.0-daylength)/50
        // of a turn of the sky; or a twist in radians of
        final double k1 = (1.0 - dayLength) * 2.0 * DELTA;

        // we are a fraction (1.0-daylength)*hourAngle/PI through the
        // night; so that we have a twist since rising of
        final double t0 = -2.0 * (1.0 - dayLength) * hourAngle - PI / 2.0;

        final Point blot = drawPath(g, Color.lightGray,
                new Function1Impl<Point, Integer>() {

                    public Point call(final Integer i) {
                        return spinpath(view, i * DELTA, t0 + i * k1);
                    }
                });
        Utils.drawCircle(g, blot, 4);
    }

    // A possible interpretation of the south path...
    // Regular - but should be a hell of a thing to reverse engineer
    // by buseri-style observations, with such sporadic sampling
/*	private double eastMouth(long dayno, double hour)
    {
    double c17 = PI*2.0* ((double)((dayno+5)%17)+hour)/17.0;
    double c43 = PI*2.0* ((double)((dayno+29)%43)+hour)/43.0;
    double c73 = PI*2.0* ((double)((dayno+59)%73)+hour)/73.0;
    return (cos(c17)+cos(c43)+cos(c73)+3.0)/20.0;
    }
    private double dodgeMouth(long dayno, double hour)
    {
    double c29 = PI*2.0* ((double)((dayno+11)%29)+hour)/29.0;
    double c71 = PI*2.0* ((double)((dayno+47)%71)+hour)/71.0;
    double c113= PI*2.0* ((double)((dayno+97)%113)+hour)/113.0;
    return (cos(c29)+cos(c71)+cos(c113)+3.0)/6.0;
    }
     */
    
    /**
     * Ad-hoc position of the East Mouth
     * @param dayno date
     * @param hour  time of day
     * @return  bearing in radians from North
     */
    public static double eastMouth(final long dayno, final double hour) {
        /* East mouth goes from about 10deg N to 30 deg S*/
        /* Period of Major oscillation 4 weeks for Tolat/Shargash */
        /* who rises about 9deg N of east */
        final double baseDate = E2Param.shargashRise - 7.0;
        final double cycle = PI * ((dayno % 28) + hour - baseDate) / 14.0;

        //formula of abf@cs.ucc.ie
        return PI / 18 * (sin(2 * cycle) + sin(cycle) - 0.85);
    }

    /**
     * Ad-hoc position of the Dodging Mouth
     * @param dayno date
     * @param hour  time of day
     * @return  bearing in radians from North
     */
    public static double dodgeMouth(final long dayno, final double hour) {
        /* West mouth goes from about 40N to 45S */

        //formula of abf@cs.ucc.ie
        final double cycle = 2.0 * PI * ((dayno % 81) + hour) / 81.0;
        double angle = cos(cycle) + cos(2 * cycle)
                + cos(3 * cycle) + cos(5 * cycle) - 1.0;

        final double nmax = 2.0 * PI / 9.0;
        final double smax = -PI / 4.0;

        angle *= (smax - nmax) / -6.0;
        angle += (nmax + smax) / 2.0;

        return angle;
    }
}
