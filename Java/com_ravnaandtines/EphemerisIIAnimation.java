package com_ravnaandtines;

/**
 *  Class EphemerisIIAnimation
 *
 *  Coded & copyright Mr. Tines <tines@windsong.demon.co.uk> 1997
 *  All rights reserved.  For full licence details see file Main.java
 *
 * @author Mr. Tines
 * @version 1.0 11-Oct-1997
 *
 */
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;

public class EphemerisIIAnimation implements Animated {

    final private View view = new View();
    private Dimension size;
    private double spinAngle;
    final private StarDome sky = new StarDome();
    private EphemerisIIControlPanel cf;
    private boolean fontData = false;
    private Font defFont,  nFont;
    final private char[] cardinal = {'N', 'E', 'W', 'S'};
    private int nfw[],  nfh;
    private Graphics gc;
    private EphemerisIIFrame frame;
    private boolean start = true;

    /**
     * Allows the animation to find out what the date it
     * should display is
     * @param x EphemerisIIControlPanel giving the time
     */
    public void associate(final EphemerisIIControlPanel x) {
        cf = x;
    }

    public void associate(final EphemerisIIFrame x) {
        frame = x;
    }

    /**
     * Sets the current Dome configuration by two angles in radians
     * @param spin double gives the rotation about the pole
     * @param tilt double gives the tilt of the dome
     */
    public void setAngles(final double spin, final double tilt) {
        spinAngle = spin;
        view.tiltVector = tilt;
    }

    private Point primitive(final double pA, final double pB) {
        // This is a thing that is distant pA from the axis,
        // with a given bearing pB from the north

        // 3D axis-relative coordinate is
        final double sx = Math.sin(pA) * Math.sin(pB);
        final double sy = Math.sin(pA) * Math.cos(pB);
        final double sz = Math.cos(pA);

        // Apply tilt
        double ty = sy;
        double tx = sx * Math.cos(view.tiltVector) + sz * Math.sin(view.tiltVector);
        final double tz = -sx * Math.sin(view.tiltVector) + sz * Math.cos(view.tiltVector);

        // apply offset viewing position
        tx -= view.offset * Math.cos(view.bearing);
        ty -= view.offset * Math.sin(view.bearing);

        return Transform.getAltAz(view, tx, ty, tz);
    }



    public boolean hit(final MouseEvent e, final JTextField t) {
        final int dx = e.getX() - (view.side / 2);
        final int dy = e.getY() - (view.side / 2);
        if (!view.up && dy > 0) {
            return false;
        }
        final int r2 = dx * dx + dy * dy;
        if (r2 > view.halfSide * view.halfSide) {
            return false;
        }

        // Now reverse engineer location
        // TODO split
        setAngles(cf.spin(), cf.slide(E2Param.TiltType.POLARIS));
        t.setText(sky.locate(e.getX(), e.getY(), view, spinAngle));
        return true;
    }

    private void rescale(double[] v) {
        // normalise
        final double l = Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

        // so (0, yuthu, 0) + k*v = 1 at intersection
        // with the dome means that
        // 1 = (kvx)^2 + (kvz^2) + (kvy+yuthu)^2
        //   = (k^2)(vx^2+vz^2+vy^2) + 2k(vy.yuthu) + yuthu^2
        // k^2 +2k(vy.yuthu) -(1-yuthu^2) = 0
        // k = -(vy.yuthu) + _/(vy^2yuthu^2)+(1-yuthu^2)

        final double b = v[1] * yuthu / l;
        final double k = Math.sqrt(b * b + 1.0 - yuthu * yuthu) - b;

        // so position on the sky is
        v[0] = k * v[0] / l;
        v[1] = yuthu + k * v[1] / l;
        v[2] = k * v[2] / l;
    }
    private double yuthu;

    private void drawFrame(final Graphics g) {
        if (E2Param.yuthu) {
            yuthu = Math.sin(Math.PI / 20.0);
        } else {
            yuthu = (3.6 / E2Param.domeRadius); // default case
        }

        Point df1 = null, dt1, df2 = null, dt2, df3 = null, dt3;
        g.setColor(Color.lightGray);

        // yuthu-centric coordinates of pole
        //double polex = 0.0;
        double poley = Math.sin(view.tiltVector) - yuthu;
        double polez = Math.cos(view.tiltVector);
        final double yudist = Math.sqrt(/*polex*polex+*/poley * poley + polez * polez);
        double[] v = new double[3];

        // normalise
        poley /= yudist;
        polez /= yudist;

        // Northward unit vector
        //double nx = 0;
        final double ny = polez;
        final double nz = -poley;

        //Eastward unit vector
        // ex = 1, ey=0, ez=0

        // the first ring of the frame is 18 degrees radius
        // about the pole from yuthu, due north of centre,
        // the next, 63, the last 90
        final double s1 = Math.sin(Math.PI / 10.0);
        final double s2 = Math.sin(7.0 * Math.PI / 20.0);
        final double s3 = Math.sin(Math.PI / 2.0);

        final double c1 = Math.cos(Math.PI / 10.0);
        final double c2 = Math.cos(7.0 * Math.PI / 20.0);
        final double c3 = Math.cos(Math.PI / 2.0);
        final double offx = view.offset * Math.cos(view.bearing);
        final double offy = view.offset * Math.sin(view.bearing);

        char[] number = new char[3];

        for (int i = 0; i < 65; ++i) {

            final double angle = i * Math.PI / 32.0;
            final double s = Math.sin(angle);
            final double c = Math.cos(angle);

            // first ring vector
            v[0] = /*polex*c1*/ +s1 * (c/* *ex + s * nx */);
            v[1] = poley * c1 + s1 * (/* c*ey + */s * ny);
            v[2] = polez * c1 + s1 * (/* c*ez + */s * nz);

            rescale(v);
            dt1 =Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
            if (0 < i) {
                Utils.drawLine(g, df1, dt1);
            } //0<i
            df1 = dt1;
            // draw line from axis to horizon and number wedges
            if (i % 8 == 0) {
                Point df = null, dt;
                for (int j = 0; j < 16; ++j) {
                    final double sn = Math.sin(j / 8.0);
                    final double cn = Math.cos(j / 8.0);
                    v[0] = sn * c;
                    v[1] = poley * cn + sn * s * ny;
                    v[2] = polez * cn + sn * s * nz;
                    rescale(v);
                    dt = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
                    if (0 < j) {
                        Utils.drawLine(g, df, dt);
                    }
                    df = dt;
                }// next j
            } // i%8 == 0
            if (i % 8 == 4) // this gets to be rape&paste at its finest
            {
                Point dt;
                final double sn = Math.sin(Math.PI / 20.0);
                final double cn = Math.cos(Math.PI / 20.0);
                v[0] = -sn * c;
                v[1] = poley * cn - sn * s * ny;
                v[2] = polez * cn - sn * s * nz;
                rescale(v);
                dt = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
                number[0] = (char) ('1' + i / 8);
                Utils.drawChars(g, dt, number, 1);
            }

            // second ring vector
            v[0] = /*polex*c2*/ +s2 * (c/* *ex + s * nx */);
            v[1] = poley * c2 + s2 * (/* c*ey + */s * ny);
            v[2] = polez * c2 + s2 * (/* c*ez + */s * nz);
            rescale(v);
            dt2 = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
            if (0 < i) {
                Utils.drawLine(g, df2, dt2);
            }
            df2 = dt2;
            // draw line from axis to horizon and number wedges
            if (i % 4 == 0) {
                final double delta = (2.0 - Math.PI / 10.0) / 16.0;
                Point df = null, dt;
                for (int j = 0; j < 16; ++j) {
                    final double sn = Math.sin(j * delta + Math.PI / 10.0);
                    final double cn = Math.cos(j * delta + Math.PI / 10.0);
                    v[0] = sn * c;
                    v[1] = poley * cn + sn * s * ny;
                    v[2] = polez * cn + sn * s * nz;
                    rescale(v);
                    dt = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
                    if (0 < j) {
                        Utils.drawLine(g, df, dt);
                    }
                    df = dt;
                } // next j
            }//i%4 == 0
            if (i % 4 == 2) {
                Point dt;
                final double sn = Math.sin(Math.PI / 5.0);
                final double cn = Math.cos(Math.PI / 5.0);
                v[0] = -sn * c;
                v[1] = poley * cn - sn * s * ny;
                v[2] = polez * cn - sn * s * nz;
                rescale(v);
                dt = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
                final int house = (i / 4) + 9;
                number[0] = (char) ('0' + house / 10);
                if (house < 10) {
                    number[0] = ' ';
                }
                number[1] = (char) ('0' + house % 10);
                Utils.drawChars(g, dt, number, 2);
            }

            // third ring vector
            v[0] = /*polex*c3*/ +s3 * (c/* *ex + s * nx */);
            v[1] = poley * c3 + s3 * (/* c*ey + */s * ny);
            v[2] = polez * c3 + s3 * (/* c*ez + */s * nz);
            rescale(v);
            dt3 = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
            if (0 < i) {
                        Utils.drawLine(g, df3, dt3);
            }
            df3 = dt3;
            // draw line from axis to horizon and number wedges
            if (i % 2 == 0) {
                final double delta = (2.0 - 7.0 * Math.PI / 20.0) / 16.0;
                Point df = null, dt;
                for (int j = 0; j < 16; ++j) {
                    final double sn = Math.sin(j * delta + 7.0 * Math.PI / 20.0);
                    final double cn = Math.cos(j * delta + 7.0 * Math.PI / 20.0);
                    v[0] = sn * c;
                    v[1] = poley * cn + sn * s * ny;
                    v[2] = polez * cn + sn * s * nz;
                    rescale(v);
                    dt = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
                    if (0 < j) {
                        Utils.drawLine(g, df, dt);
                    }
                    df = dt;
                }
            }
            if (i % 2 == 1) {
                Point dt;
                double sn = Math.sin(17.0 * Math.PI / 40.0);
                double cn = Math.cos(17.0 * Math.PI / 40.0);
                v[0] = -sn * c;
                v[1] = poley * cn - sn * s * ny;
                v[2] = polez * cn - sn * s * nz;
                rescale(v);
                dt = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
                int house = (i / 2) + 25;
                number[0] = (char) ('0' + house / 10);
                number[1] = (char) ('0' + house % 10);
                Utils.drawChars(g, dt, number, 2);

                sn = Math.sin(1.0 + Math.PI / 4.0);
                cn = Math.cos(1.0 + Math.PI / 4.0);
                v[0] = -sn * c;
                v[1] = poley * cn - sn * s * ny;
                v[2] = polez * cn - sn * s * nz;
                rescale(v);
                dt = Transform.getAltAz(view, v[1] - offx, v[0] - offy, v[2]);
                house = (i / 2) + 57;
                number[0] = (char) ('0' + house / 10);
                number[1] = (char) ('0' + house % 10);
                Utils.drawChars(g, dt, number, 2);

            } // i%2 == 1
        }// next i
    } // end drawFrame

    /**
     * This draws the clock face and the times.
     * @param g Graphics to draw to
     */
    public synchronized void paint(final Graphics g) {
        cf.setTime();
        gc = g;
        // get a large size font for cardinal points
        if (!fontData) {
            defFont = g.getFont();
            final String fname = defFont.getName();
            final int fsize = defFont.getSize() * 2;
            nFont = new Font(fname, Font.BOLD, fsize);
            g.setFont(nFont);
            final java.awt.FontMetrics fm = g.getFontMetrics(nFont);
            nfh = fm.getHeight();
            nfw = new int[4];
            for (int nf = 0; nf < 4; ++nf) {
                nfw[nf] = fm.charWidth(cardinal[nf]);
            }
            g.setFont(defFont);
            fontData = true;
        }
        // draw the black background
        g.setColor(Color.black);
        g.fillRect(0, 0, size.width, size.height);

        if (null == frame) {
            return;
        }
        // tweak the list box on the control panel
        // to ensure the default selection is visible

        if (start) {
            cf.adjust();
            start = false;
        }

        // get type of display
        view.up = cf.getUp();
        view.look = -cf.getLook();

        // Get crucial size information for display
        if (view.up) {
            view.side = (size.width < size.height) ? size.width : size.height;
        } else {
            view.side = (size.width < 2 * size.height) ? size.width : 2 * size.height;
        }
        view.halfSide = (int) (0.45 * (double) view.side);
        final int margin = view.side / 2 - view.halfSide;
        int x1 = margin;
        int d1 = 2 * view.halfSide;

        // Get the current Dome orientation
        // TODO split
        setAngles(cf.spin(), cf.slide(E2Param.TiltType.POLARIS));
        int x, y;

        // Assume the Sun is not up
        boolean day = false;

        // Day length is 15/25 Dara Happan hours with 9 degrees tilt
        // and offset from 12.5 is proportional to tilt.
        // So as a fraction of a day +0.1 per 9 degrees or +1 for 90deg
        final double dayLength = cf.getDayLength();
        final double dawn = (1.0 - dayLength) / 2.0; // fraction of day after midnight


        // compute day/night state and colour dome appropriately
        int blue = 0;
        final double twilength = 0.1 * dayLength;

        if (cf.hour < (dawn - twilength)) {
            ;
        } else if (cf.hour < dawn) {
            blue = (int) (255.0 * (1.0 + (cf.hour - dawn) / twilength));
        } else if ((1.0 - cf.hour) > dawn) {
            blue = 255;
            day = true;
        } else if ((1.0 - cf.hour) > (dawn - twilength)) {
            blue = (int) (255.0 * (1.0 + (1.0 - cf.hour - dawn) / twilength));
        }
        if (blue > 0) {
            g.setColor(new Color(0, 0, blue));
            if (view.up) {
                g.fillOval(x1, x1, d1, d1);
            } else {
                g.fillArc(x1, x1, d1, d1, 0, 180);
            }
        }

        // are stars to be shown? - should really fade these in
        final boolean stars = (blue < 127) || EphemerisII.cbobscure.isSelected();

        g.setColor(Color.green);
        if (view.up) {
            // draw horizon and cardinal points
            for (int i = 0; i < 5; ++i) {
                g.drawOval(x1, x1, d1, d1);
                --x1;
                d1 += 2;
            }
            g.setFont(nFont);
            g.setColor(Color.white);
            x = (view.side - nfw[0]) / 2;
            y = margin - nfh / 4;
            g.drawChars(cardinal, 0, 1, x, y);

            g.setColor(Color.yellow);
            x = margin - 3 * nfw[1] / 2;
            y = (view.side + nfh) / 2;
            g.drawChars(cardinal, 1, 1, x, y);

            g.setColor(Color.red);
            x = (view.side - margin + nfw[2] / 2);
            y = (view.side + nfh) / 2;
            g.drawChars(cardinal, 2, 1, x, y);

            g.setColor(Color.green);
            x = (view.side - nfw[3]) / 2;
            y = (view.side - margin + 3 * nfh / 4);
            g.drawChars(cardinal, 3, 1, x, y);

            // reset to small default font
            g.setFont(defFont);
        } else {
            for (int i = 0; i < 5; ++i) {
                g.drawArc(x1, x1, d1, d1, 0, 180);
                g.drawLine(x1, x1 + d1 / 2, x1 + d1, x1 + d1 / 2);
                --x1;
                d1 += 2;
            }
            //   private char [] cardinal = {'N','E','W','S'};
            final int span = 2 * d1;
            final int lookOffset = -(int) Math.round(span * view.look / (Math.PI * 2.0));
            int base = lookOffset - span;
            int go;
            g.setFont(nFont);
            y = x1 + d1 / 2 + nfh + 2;

            for (go = 0; go < 3; ++go, base += span) {
                if (Math.abs(base) <= d1 / 2 + nfw[0]) {
                    g.setColor(Color.white);
                    x = x1 + d1 / 2 - (base) - nfw[0] / 2;
                    g.drawChars(cardinal, 0, 1, x, y);
                }
                if (Math.abs(base - d1 / 2) <= d1 / 2 + nfw[1]) {
                    g.setColor(Color.yellow);
                    x = x1 + d1 / 2 - (base - d1 / 2) - nfw[1] / 2;
                    g.drawChars(cardinal, 1, 1, x, y);
                }
                if (Math.abs(base + d1 / 2) <= d1 / 2 + nfw[2]) {
                    g.setColor(Color.red);
                    x = x1 + d1 / 2 - (base + d1 / 2) - nfw[2] / 2;
                    g.drawChars(cardinal, 2, 1, x, y);
                }
                if (Math.abs(base + d1) <= d1 / 2 + nfw[3]) {
                    g.setColor(Color.green);
                    x = x1 + d1 / 2 - (base + d1) - nfw[3] / 2;
                    g.drawChars(cardinal, 3, 1, x, y);
                }
            } // next go

            g.setFont(defFont);
        }

        // drawing type
        //projection = cf.getProj();
        view.offset = cf.getOffset() * 20.0 / E2Param.domeRadius;
        view.bearing = -cf.getBearing();

        // draw star dome as required
        final boolean names = EphemerisII.cbnames.isSelected();
        if (stars) {
            sky.draw(g, spinAngle, names, view);
        }


        // planets and such
        // Sunpath
        if (EphemerisII.cbsunpath.isSelected()) {
            Transform.drawSunpath(g, view);
        }
        double hourAngle, h;
        Point xy;
        // Yelm & Lightfore
        g.setColor(Color.yellow);
        if (day)//Yelm
        {
            hourAngle = Math.PI * (cf.hour - dawn) / dayLength;
            xy = Transform.sunpath(view, hourAngle);
            if (xy.x > 0 && xy.y > 0) {
                final int sunsize = EphemerisII.cbring.isSelected() ? Math.max(1, (int) Math.round(view.halfSide / 180.0))
                        : 12;
                g.fillOval(xy.x - sunsize / 2, xy.y - sunsize / 2, sunsize, sunsize);
                g.drawLine(xy.x, xy.y, xy.x, xy.y);
                if (names) {
                    Utils.label(gc, "Yelm", xy, 12, 0);
                }
            }
        } else {
            h = cf.hour;
            if (h > 0.5) {
                hourAngle = (h - (1.0 - dawn)) / (1.0 - dayLength);
            } else {
                hourAngle = (h + dawn) / (1.0 - dayLength);
            }
            hourAngle *= Math.PI;
            xy = Transform.sunpath(view, hourAngle);
            if (xy.x > 0 && xy.y > 0) {
                g.fillOval(xy.x - 2, xy.y - 2, 4, 4);
                if (names) {
                    Utils.label(gc, "Lightfore", xy, 4, 0);
                }
            }

            // lightfore path
            if (EphemerisII.cblight.isSelected()) {
                Transform.drawLightforePath(g, view, dayLength, hourAngle);
            }
        } // sun
        double cycle;
        //Definition: 1 AU is 1/10 of the distance from the horizon to the Pole
        //Star on the equitilt days. I.e., it is 9 degrees = pi/20

        // Theya begins to rise exactly 5 Dara Happan hours (Theyalan: 4 hrs 48 min)
        // before Dawn, in all seasons. She travels upwards at a constant speed of
        // 1/2 AU per Dara Happan hour (Dara Happan: 4.5 degrees/hour, Theyalan:
        // 4.6875 degrees/hour), and is no longer visible once the Sun has risen.
        // pi/40 per DHhour or 25*pi/40 = 5pi/8 per day = (5/16)cycle per day

//       if((cf.hour >= dawn-0.20002) && (cf.hour <= dawn))
//       {
//         //cycle = (5/16)*(cf.hour-dawn+0.2)
//           cycle = (5*(cf.hour-dawn)+1.0)*Math.PI/8;
//           g.setColor(Color.white);
//		       xy = sunpath(cycle);
//           if(xy.x > 0 && xy.y > 0)
//           {
//             g.fillOval(xy.x-2,xy.y-2,4,4);
//		         if(names) label("Theya", xy.x+4, xy.y);
//           }
//       }

        //Assuming she sets at dusk and flips half way
        if (!day || EphemerisII.cbobscure.isSelected()) {
            final double fliptime = 0.4; //= ((dawn-0.2)+(1-dawn))/2.0;
            double cf_hour = cf.hour;
            if (cf_hour > 0.99999) {
                cf_hour -= 1.0;
            }
            if (cf_hour > fliptime) {
                cf_hour = fliptime * 2.0 - cf_hour;
            }
            cycle = (5 * (cf_hour - dawn) + 1.0) * Math.PI / 8;
            g.setColor(Color.white);
            xy = Transform.sunpath(view, cycle);
            if (xy.x > 0 && xy.y > 0 && cf_hour >= (dawn - 0.2)) {
                g.fillOval(xy.x - 2, xy.y - 2, 4, 4);
                if (names) {
                    Utils.label(gc, "Theya", xy, 4, 0);
                }
            }
        }

        //RAUSA, the Dusk Star
        //Rausa travels at the same speed as Theya. However, she always sets at
        //midnight, regardless of the season. When she is visible, even at
        //midwinter, she is always seen only when she is falling. Thus, on
        //Midwinter's night her height at Dusk (when she becomes visible) can be
        //calculated based on how far she could fall during 1/2 the night.
//       if((cf.hour > 1.0-dawn) || (cf.hour < 0.00001))
//       {
//           double cf_hour = cf.hour; if (cf_hour < 0.5) cf_hour +=1.0;
//         //cycle = (5/16)(cf.hour-1)+0.5;
//           cycle = (1+ (5*(cf_hour-1)/8)) * Math.PI;
//           g.setColor(Color.red);
//		       xy = sunpath(cycle);
//           if(xy.x > 0 && xy.y > 0)
//           {
//             g.fillOval(xy.x-2,xy.y-2,4,4);
//		         if(names) label("Rausa", xy.x+4, xy.y);
//           }
//       }

        // Assuming she rises at dawn and flips half-way
        if (!day || EphemerisII.cbobscure.isSelected()) {
            final double fliptime = dawn + (1.0 - dawn) / 2.0;
            double cf_hour = cf.hour;
            if (cf_hour < 0.00001) {
                cf_hour += 1.0;
            }
            if (cf_hour < fliptime) {
                cf_hour = fliptime * 2.0 - cf_hour;
            }
            cycle = (1 + (5 * (cf_hour - 1) / 8)) * Math.PI;
            g.setColor(Color.red);
            xy = Transform.sunpath(view, cycle);
            if (xy.x > 0 && xy.y > 0 && cf_hour <= 1.00001) {
                g.fillOval(xy.x - 2, xy.y - 2, 4, 4);
                if (names) {
                    Utils.label(gc, "Rausa", xy, 4, 0);
                }
            }
        }

        // Kalikos - as per Elder secrets
        // Assume jumper rate, max at midnight
        if (stars) {
            double upAngle = -1;
            if (cf.hour < dawn) {
                upAngle = (dawn - cf.hour) * 5 * Math.PI / 8;
            } else if (cf.hour > (1.0 - dawn)) {
                upAngle = (cf.hour - 1.0 + dawn) * 5 * Math.PI / 8;
            }
            if (upAngle > 0) {
                // i) 3D coordinate is
                final double qy = -Math.cos(upAngle) * Math.cos(0.2) - view.offset * Math.sin(view.bearing);
                final double qx = Math.cos(upAngle) * Math.sin(0.2) - view.offset * Math.cos(view.bearing);
                final double qz = Math.sin(upAngle);
                // ii) extract altitude and azimuth
                xy = Transform.getAltAz(view, -qy, qx, qz);
                g.setColor(Color.white);
                g.fillOval(xy.x - 2, xy.y - 2, 4, 4);
                if (names) {
                    Utils.label(gc, "Kalikos", xy, 4, 0);
                }
            }
        }
        // entekos/moskalf - white; 31up, 31down
        // sets about dusk at autmn equinox yr 5
        // Autumn equinox is 135.5 days after midnight, dusk 135.75

        final long dayno = cf.year * 294 + cf.week * 7 + cf.day;

        final long entekosOffset = 5 * 294 + 135;
        cycle = 0.5 + (((double) ((dayno - entekosOffset) % 62)) + cf.hour - 0.75) / 62.0;
        if (cycle > 1.0) {
            cycle -= 1.0;
        }
        if (stars && 0 <= cycle && cycle <= 0.5) {
            g.setColor(Color.lightGray);
            xy = Transform.sunpath(view, cycle * 2.0 * Math.PI);
            if (xy.x > 0 && xy.y > 0) {
                g.fillOval(xy.x - 3, xy.y - 3, 6, 6);
                if (names) {
                    Utils.label(gc, "Entekos", xy, 6, 0);
                }
            }
        }

        // wagon/lokarnos 98up, 98 down
        //Lokarnos/Wagon rises at dusk on Freezeday of Disorder Week in all
        //seasons. He takes 7 days to cross the sky, and then spends another 7 days
        //in the Underworld. This is the case until sometime in the 900s, when his
        //speed begins to change.
        // in Year 1, rising is at midnight Fire/Movement/Earth (24:00h, that is)

        // if dayno before slowing...
        final long wagonOffset = 152;
        cycle = (((double) ((dayno - wagonOffset) % 14)) + cf.hour) / 14.0;
        if (stars && 0 <= cycle && cycle <= 0.5) {
            g.setColor(Color.lightGray);
            xy = Transform.sunpath(view, cycle * 2.0 * Math.PI);
            if (xy.x > 0 && xy.y > 0) {
                g.fillOval(xy.x - 4, xy.y - 4, 8, 8);
                if (names) {
                    Utils.label(gc, "Lokarnos", xy, 8, 0);
                }
            }
        }


        // uleria/mastakos 8hr up, 0 down; rises at 6pm at autumn equinox
        if (!E2Param.uleria) {
            cycle = (cf.hour - 0.75);  // cycle zero at 18:00
        } else // perhaps 1/3 of a sidereal day as period.
        {
            cycle = cf.week * 7 + cf.day + cf.hour - 135.75;
            cycle *= 294.0 / 293.0; // now sidereal days;
            cycle -= Math.floor(cycle);
        }
        if (cycle < 0) {
            cycle += 1.0;
        }
        cycle *= 3.0;
        while (cycle > 1.0) {
            cycle -= 1.0;
        }
        if (cycle < 0) {
            cycle += 1.0;
        }
        cycle *= Math.PI;

        if (stars) {
            g.setColor(blue > 127 ? Color.cyan : Color.blue);
            xy = Transform.sunpath(view, cycle);
            if (xy.x > 0 && xy.y > 0) {
                g.fillOval(xy.x - 2, xy.y - 2, 4, 4);
                if (names) {
                    Utils.label(gc, "Uleria", xy, 4, 0);
                }
            }
        }

        // White orbiter goes round 29 times in 28 days
        if (cf.year > 1725) {
            drawOrbiter(g);
        }


        // South path and south path planets
        double dm, em;
        Point p;
        if (EphemerisII.cbsouthpath.isSelected()) {
            Transform.drawSouthpath(g, view, dayno, cf.hour);
        }

        // Shargash 14d/14d - visible at day
        cycle = (((double) (dayno % 28)) + cf.hour - E2Param.shargashRise) / 28.0;
        if (cycle < 0) {
            cycle += 1.0;
        }
        if (0 <= cycle && cycle <= 0.5) {
            // mouth location at rising and setting
            em = Transform.eastMouth(dayno, cf.hour - 28 * cycle);
            dm = Transform.dodgeMouth(14 + dayno, cf.hour - 28 * cycle);
            p = Transform.southpath(view, cycle * 2.0 * Math.PI, dm, em);
            if (p.x > 0 && p.y > 0) {
                g.setColor(Color.red);
                g.fillOval(p.x - 4, p.y - 4, 8, 8);
                if (names) {
                    Utils.label(gc, "Shargash", p, 8, 0);
                }
            }
        }

        // TwinStar 3d/3d
        cycle = (((double) (dayno % 6)) + cf.hour - E2Param.twinRise) / 6.0;
        if (cycle < 0) {
            cycle += 1.0;
        }
        if (stars && 0 <= cycle && cycle <= 0.5) {
            // mouth location at rising and setting
            em = Transform.eastMouth(dayno, cf.hour - 6 * cycle);
            dm = Transform.dodgeMouth(3 + dayno, cf.hour - 6 * cycle);
            p = Transform.southpath(view, cycle * 2.0 * Math.PI, dm, em);
            if (p.x > 0 && p.y > 0) {
                g.setColor(Color.yellow);
                g.fillOval(p.x - 4, p.y, 4, 4);
                g.setColor(Color.white);
                g.fillOval(p.x, p.y + 4, 4, 4);
                if (names) {
                    Utils.label(gc, "TwinStars", p, 8, 0);
                }
            }
        }

        // Artia 56d/56d but not in sacred time.
        // Interpret as season on, season off
        if (cf.week < 40) {
            final int parity = cf.year % 2;
            long qdayno = (cf.week * 7 + cf.day + 280 * parity);
            cycle = (((double) (qdayno % 112)) + cf.hour - E2Param.artiaRise) / 112.0;
            if (cycle < 0) {
                cycle += 1.0;
            }
            if (stars && 0 <= cycle && cycle <= 0.5) {
                // mouth location at rising and setting
                qdayno = cf.year * 294;
                double riseday = cf.week * 7 + cf.day + cf.hour - cycle * 112.0;
                double setday = riseday + 56.0;

                if (riseday < 0) {
                    riseday -= 14.0;
                }
                em = Transform.eastMouth(qdayno, riseday);

                if (setday > 280.0) {
                    setday += 14.0;
                }
                dm = Transform.dodgeMouth(qdayno, setday);
                p = Transform.southpath(view, cycle * 2.0 * Math.PI, dm, em);
                if (p.x > 0 && p.y > 0) {
                    g.setColor(Color.red);
                    g.fillOval(p.x - 2, p.y - 2, 4, 4);
                    if (names) {
                    Utils.label(gc, "Artia", p, 4, 0);
                    }
                }
            }
        }

        // Quasi-fixed quantities

        if (EphemerisII.cbframe.isSelected()) // Buserian's frame
        {
            drawFrame(g);
        }


        // Zenith
        if (stars) {
            final double pA = 0.15 * Math.PI;
            final double pB = 4.0;
            double sx = Math.sin(pA) * Math.sin(pB);
            double sy = Math.sin(pA) * Math.cos(pB);
            final double sz = Math.cos(pA);

            // apply offset viewing position
            sx -= view.offset * Math.cos(view.bearing);
            sy -= view.offset * Math.sin(view.bearing);

            xy = Transform.getAltAz(view, sx, sy, sz);
            g.setColor(Color.lightGray);
            g.fillOval(xy.x - 2, xy.y - 2, 4, 4);
            if (names) {
                Utils.label(gc, "Zenith", xy, 4, 0);
            }
        }


        // Stormgate
        if (stars && ( // visible only for 24 hours about launch
                (cf.week % 2 == 1 && cf.day == 0 && cf.hour < 0.5) ||
                (cf.week % 2 == 0 && cf.day == 6 && cf.hour > 0.5))) {
            xy = primitive(0.35 * Math.PI, 0.5);
            g.setColor(Color.lightGray);
            if (xy.y > 0 && xy.x > 0) {
                g.fillOval(xy.x - 2, xy.y - 2, 4, 4);
                if (names) {
                    Utils.label(gc, "Stormgate", xy, 4, 0);
                }
            }
        }


        // Special effects
        // Orlanth's ring : visible week on, week off
        if (stars) {
            cycle = (cf.week * 7 + cf.day) % 14 + cf.hour;
            final double start2 = 6.0 + 23.0 / 24.0 + 13.0 / 1440.0;
            cycle -= start2;
            if (cycle < 0) {
                cycle += 14;
            }
            final double top = 7 + 1.0 / 24.0;
            if (cycle >= 0 && cycle <= top) {
                final double polarAngle = 0.05 * Math.PI * (7.0 - cycle);
                final double polarBearing = 0.5 - (294.0 / 293.0) * 2.0 * Math.PI * cycle;

                xy = primitive(polarAngle, polarBearing);

                // period is 16/7 hours = 16/24*7 = 2/21 days
                final double period = 2.0 / 21.0;
                final double rotations = cycle / period;

                if (xy.y > 0 && xy.x > 0) {
                    g.setColor(Color.orange);
                    if (EphemerisII.cbring.isSelected()) {
                        final int size2 = Math.max(1, (int) Math.round(view.halfSide / 240.0));
                        g.fillOval(xy.x - size2 / 2, xy.y - size2 / 2, size2, size2);
                        g.drawLine(xy.x, xy.y, xy.x, xy.y);
                    } else {
                        for (int i = 0; i < 8 && i <= 16.0 * rotations; ++i) {
                            if (7 == i) {
                                g.setColor(Color.green);
                            }
                            if (cycle > 7) {
                                final double del = (cycle - 7.0) * 168;
                                if (i < del) {
                                    continue;
                                }
                            }
                            final double da = -polarBearing + Math.PI * (rotations * 2 - i / 4.0);
                            final int dx = (int) Math.rint(10.0 * Math.cos(da));
                            final int dy = (int) Math.rint(10.0 * Math.sin(da));
                            g.fillOval(xy.x - 2 + dx, xy.y - 2 + dy, 4, 4);
                        }
                    }
                    if (names) {
                        Utils.label(gc, "Orlanth's Ring", xy, 10, 0);
                    }
                }
            }
        }

        // Red Moon - visible at day
        if (cf.year >= 1247 && cf.year <= 1725) {
            drawMoon(g);
        }

    } // end paint

    /**
     * The instantiator must have some handle to the size
     * of the area where output is to be sent.
     * @param d Dimension to draw to
     */
    public synchronized void setSize(final Dimension d) {
        size = d;
    }

    public synchronized Dimension getSize() {
        return size;
    }

    public synchronized void drawOrbiter(final Graphics g) {
        double days = (294 * (cf.year - 1725) + cf.week * 7) % 28 + cf.day + cf.hour; // how far into 28 day cycle
        double orbits = days * 29.0 / 28.0;		          // begins at observed zenith
        while (orbits > 0.5) {
            orbits -= 1.0;
        }
        final double cycle = orbits + 0.25;
        if (0 <= cycle && cycle <= 0.5) {
            final Point xy = Transform.sunpath(view, cycle * Math.PI * 2.0);
            final int x = xy.x;
            final int y = xy.y;

            g.setColor(Color.lightGray);
            g.fillOval(x - 6, y - 6, 12, 12);
            g.setColor(Color.black);
            days = 28 - days;
            if (days < 14) {
                g.fillArc(x - 7, y - 7, 14, 14, 90, -180);
            } else {
                g.fillArc(x - 7, y - 7, 14, 14, 90, 180);
            }

            if (Math.abs(days - 14) > 7) {
                g.setColor(Color.lightGray);
            }
            if (days > 14) {
                days -= 14;
            }
            int width = (int) Math.rint(days - 7);
            if (width < 0) {
                width = -width;
            }
            g.fillOval(x - width, y - 7, 2 * width, 14);

            g.setColor(Color.lightGray);
            if (EphemerisII.cbnames.isSelected()) {
                Utils.label(gc, "White Orbiter", xy, 14, 0);
            }
        }
    }

    public synchronized void drawMoon(final Graphics g) {
        final double myOffset = cf.getOffset(); // distance from centre/20,000km
        double mx = myOffset * Math.cos(view.bearing);
        double my = myOffset * Math.sin(view.bearing);
        final double moony = 0.150 / 20.0;
        final double moonx = 3.5 / 20.0;

        mx -= moonx;
        my -= moony; // cratercenter coordinates

        // at day 3.75 of the week, moon is full at 30deg N of E

        final double norm = Math.sqrt(mx * mx + my * my);
        final double nx = mx / norm;
        final double ny = my / norm;
        mx = -Math.cos(0.35 * Math.PI) * mx / norm;
        my = -Math.cos(0.35 * Math.PI) * my / norm;
        final double mz = Math.sin(0.35 * Math.PI);

        double ang = 30 + Math.atan2(nx, ny) * 180.0 / Math.PI;
        double doy = (cf.day + cf.hour - 3.75);
        if (doy < 0) {
            doy += 7.0;
        }
        ang -= 360 * doy / 7.0;
        ang -= 180.0;


        final Point xy = Transform.getAltAz(view, mx, my, mz);
        final int moonsize = EphemerisII.cbring.isSelected() ? Math.max(1, (int) Math.round(view.halfSide / 180.0))
                : 12;
        g.setColor(Color.red);
        g.drawLine(xy.x, xy.y, xy.x, xy.y);
        g.fillOval(xy.x - moonsize / 2, xy.y - moonsize / 2, moonsize, moonsize);
        while (ang < 0) {
            ang += 360.0;
        }
        while (ang > 360) {
            ang -= 360;
        }
        int x = xy.x;
        int y = xy.y;
        ang = 360 - ang;
        if (ang > 360) {
            ang -= 360;
        }

        double tang = ang;
        if (tang > 180) {
            tang -= 180;
        }
        final double frac = (Math.abs(tang - 90) < 1) ? 100 : 90.0 / (tang - 90.0);

        if (view.up) {
            int i, j;
            for (i = 0; i < moonsize; ++i) {
                x = -moonsize / 2 + i;
                for (j = 0; j < moonsize; ++j) {
                    g.setColor(Color.black);
                    y = -moonsize / 2 + j;
                    if (x * x + y * y >= moonsize * moonsize / 4.0) {
                        continue;
                    }

                    double cross = nx * x + ny * y;
                    if (ang < 180) {
                        cross *= -1;
                    }

                    if (cross < 0) {
                        g.drawLine(x + xy.x, y + xy.y,
                                x + xy.x, y + xy.y);
                    }

                    if (Math.abs(ang - 180) > 90) {
                        g.setColor(Color.red);
                    }
                    if (Math.abs(tang - 90) > 1) {
                        final double dot = nx * y - ny * x;
                        cross *= frac;
                        if (cross * cross + dot * dot < moonsize * moonsize / 4.0) {
                            g.drawLine(x + xy.x, y + xy.y,
                                    x + xy.x, y + xy.y);
                        }
                    }
                }
            }
        } else {
            g.setColor(Color.black);
            if (ang < 180) {
                g.fillArc(x - moonsize / 2, y - moonsize / 2, moonsize, moonsize, 90, -180);
            } else {
                g.fillArc(x - moonsize / 2, y - moonsize / 2, moonsize, moonsize, 90, 180);
            }

            if (Math.abs(ang - 180) > 90) {
                g.setColor(Color.red);
            }
            if (ang > 180) {
                ang -= 180;
            }
            int width = (int) Math.floor(moonsize * (ang - 90) / 180.0);
            if (width < 0) {
                width = -width;
            }
            g.fillOval(x - width, y - moonsize / 2, 2 * width, moonsize);
        }
        if (EphemerisII.cbnames.isSelected()) {
                Utils.label(gc, "Red Moon", xy, moonsize, 0);
        }
    }
}

/* end of file EphemerisIIAnimation.java */

