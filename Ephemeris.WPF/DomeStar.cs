using System;
using System.Windows;
using System.Windows.Media;

namespace Ephemeris.WPF
{
    public class DomeStar
    {
        private double _x, _y, _mag;
        private string _name, _marker;
        private Color _color;
        private double _radius;
        private int _diameter;
        private Point[] _scatter;
        private double _r, _theta;
        private E2Doublet[] _polar;
        private static int _nfaint = 12;

        public DomeStar(string name, int x, int y, int mag, string marker, Color color)
        {
            _name = name;
            _x = x;
            _y = y;
            _mag = mag;
            _marker = marker;
            _color = color;

            if (_mag > 0)
            {
                _radius = Math.Sqrt(_mag);
                _diameter = (int)Math.Round(_radius);
                _radius /= 2;
                _r = Math.Sqrt((_x * _x) + (_y * _y)) / StarDome.Radian;
                _theta = Math.Atan2(_y, _x);
            }
            else
            {
                E2RandomFaintStar ran = new E2RandomFaintStar((x << 16) + (y & 0xFFFF));
                _scatter = new Point[_nfaint];
                _polar = new E2Doublet[_nfaint];
                for (int i = 0; i < _nfaint; ++i)
                {
                    _scatter[i] = ran.Next();
                    _polar[i] = new E2Doublet();
                    double xx = _scatter[i].X + _x;
                    double yy = _scatter[i].Y + _y;
                    _polar[i].R = Math.Sqrt((xx * xx) + (yy * yy)) / StarDome.Radian;
                    _polar[i].T = Math.Atan2(yy, xx);
                }
            }
        }

        public DomeStar(string name, int x, int y, int mag, string marker)
            : this(name, x, y, mag, marker, (mag < 0) ? Colors.Cyan : Colors.White) { }

        public void Draw(DrawingContext g, double spin,
             double slide, int radius, int x0, int y0, bool names,
             double radialOffset, double bearing,
             bool up, double look)
        {
            SolidColorBrush brush = new SolidColorBrush(_color);
            Pen pen = new Pen(brush, 1);

            int px, py, nx = 0, ny = 0;
            bool drawName = names;

            if (_mag > 0)
            {
                double th = -_theta - spin;
                double x = Math.Cos(th) * Math.Sin(_r);
                double y = Math.Sin(th) * Math.Sin(_r);
                double z = Math.Cos(_r);

                double ty = y;
                double tx = x * Math.Cos(slide) + z * Math.Sin(slide);
                double tz = -x * Math.Sin(slide) + z * Math.Cos(slide);

                if (tz < 0) return;

                tx -= radialOffset * Math.Cos(bearing);
                ty -= radialOffset * Math.Sin(bearing);

                if (up)
                {
                    double azimuth = Math.Atan2(tx, ty);
                    double altitude = Math.Atan2(Math.Sqrt(tx * tx + ty * ty), tz);
                    altitude /= (Math.PI / 2.0);
                    nx = px = x0 + (int)Math.Round(radius * altitude * Math.Cos(azimuth));
                    ny = py = y0 - (int)Math.Round(radius * altitude * Math.Sin(azimuth));
                    g.DrawEllipse(brush, pen, new Point(px, py), _diameter / 2.0, _diameter / 2.0);
                }
                else
                {
                    double c = Math.Cos(look);
                    double s = Math.Sin(look);
                    x = tx * c + ty * s;
                    y = tx * s - ty * c;
                    z = tz;

                    if (x < 0) return;
                    double azimuth = Math.Atan2(y, z) + Math.PI / 2.0;
                    double altitude = Math.Atan2(Math.Sqrt(y * y + z * z), x);
                    altitude /= (Math.PI / 2.0);
                    nx = px = x0 - (int)Math.Round(radius * altitude * Math.Cos(azimuth));
                    ny = py = y0 - (int)Math.Round(radius * altitude * Math.Sin(azimuth));
                    g.DrawEllipse(brush, pen, new Point(px, py), _diameter / 2.0, _diameter / 2.0);
                }
            }
            else
            {
                drawName = false;
                for (int i = 0; i < _nfaint; ++i)
                {
                    double th = -_polar[i].T - spin;
                    double x = Math.Cos(th) * Math.Sin(_polar[i].R);
                    double y = Math.Sin(th) * Math.Sin(_polar[i].R);
                    double z = Math.Cos(_polar[i].R);

                    double ty = y;
                    double tx = x * Math.Cos(slide) + z * Math.Sin(slide);
                    double tz = -x * Math.Sin(slide) + z * Math.Cos(slide);

                    if (tz < 0) continue;

                    tx -= radialOffset * Math.Cos(bearing);
                    ty -= radialOffset * Math.Sin(bearing);

                    if (up)
                    {
                        drawName = names;
                        double azimuth = Math.Atan2(tx, ty);
                        double altitude = Math.Atan2(Math.Sqrt(tx * tx + ty * ty), tz);
                        altitude /= (Math.PI / 2.0);
                        nx = px = x0 + (int)Math.Round(radius * altitude * Math.Cos(azimuth));
                        ny = py = y0 - (int)Math.Round(radius * altitude * Math.Sin(azimuth));
                        g.DrawEllipse(brush, pen, new Point(px, py), 0.5, 0.5); // Draw a point
                    }
                    else
                    {
                        double c = Math.Cos(look);
                        double s = Math.Sin(look);
                        x = tx * c + ty * s;
                        y = tx * s - ty * c;
                        z = tz;

                        if (x < 0) continue;
                        drawName = names;
                        double azimuth = Math.Atan2(y, z) + Math.PI / 2.0;
                        double altitude = Math.Atan2(Math.Sqrt(y * y + z * z), x);
                        altitude /= (Math.PI / 2.0);
                        nx = px = x0 - (int)Math.Round(radius * altitude * Math.Cos(azimuth));
                        ny = py = y0 - (int)Math.Round(radius * altitude * Math.Sin(azimuth));
                        g.DrawEllipse(brush, pen, new Point(px, py), 0.5, 0.5); // Draw a point
                    }
                }
            }

            if (drawName && !string.IsNullOrEmpty(_marker))
            {
                FormattedText formattedText = new FormattedText(
                    _marker,
                    System.Globalization.CultureInfo.CurrentCulture,
                    FlowDirection.LeftToRight,
                    new Typeface("Arial"), // You might want to make this configurable
                    12, // Font size
                    new SolidColorBrush(Colors.Cyan) // Text color
                );
                g.DrawText(formattedText, new Point(nx, ny));
            }
        }

        public double Locate(int ex, int ey, int rad, int cen,
            double spin, double slide, double radialOffset,
            double bearing, bool up, double look)
        {
            double azimuth;
            double altitude;
            int nx, ny;

            if (_mag > -3000)
            {
                double th = -_theta - spin;
                double x = Math.Cos(th) * Math.Sin(_r);
                double y = Math.Sin(th) * Math.Sin(_r);
                double z = Math.Cos(_r);

                double ty = y;
                double tx = x * Math.Cos(slide) + z * Math.Sin(slide);
                double tz = -x * Math.Sin(slide) + z * Math.Cos(slide);

                if (tz < 0) return 20000.0;

                tx -= radialOffset * Math.Cos(bearing);
                ty -= radialOffset * Math.Sin(bearing);

                if (up)
                {
                    azimuth = Math.Atan2(tx, ty);
                    altitude = Math.Atan2(Math.Sqrt(tx * tx + ty * ty), tz);
                    altitude /= (Math.PI / 2.0);
                    nx = cen + (int)Math.Round(rad * altitude * Math.Cos(azimuth));
                    ny = cen - (int)Math.Round(rad * altitude * Math.Sin(azimuth));
                }
                else
                {
                    double c = Math.Cos(look);
                    double s = Math.Sin(look);
                    x = tx * c + ty * s;
                    y = tx * s - ty * c;
                    z = tz;

                    if (x < 0) return 20000.0;
                    azimuth = Math.Atan2(y, z) + Math.PI / 2.0;
                    altitude = Math.Atan2(Math.Sqrt(y * y + z * z), x);
                    altitude /= (Math.PI / 2.0);
                    nx = cen - (int)Math.Round(rad * altitude * Math.Cos(azimuth));
                    ny = cen - (int)Math.Round(rad * altitude * Math.Sin(azimuth));
                }
                double dx = nx - ex;
                double dy = ny - ey;
                double crit = 5.0;
                if (_mag > 0) crit += _diameter;
                double margin = Math.Sqrt(dx * dx + dy * dy);
                return (margin < crit) ? margin : 20000.0;
            }
            else
            {
                for (int i = 0; i < _nfaint; ++i)
                {
                    double th = -_polar[i].T - spin;
                    double x = Math.Cos(th) * Math.Sin(_polar[i].R);
                    double y = Math.Sin(th) * Math.Sin(_polar[i].R);
                    double z = Math.Cos(_polar[i].R);

                    double ty = y;
                    double tx = x * Math.Cos(slide) + z * Math.Sin(slide);
                    double tz = -x * Math.Sin(slide) + z * Math.Cos(slide);

                    if (tz < 0) continue;

                    tx -= radialOffset * Math.Cos(bearing);
                    ty -= radialOffset * Math.Sin(bearing);

                    if (up)
                    {
                        azimuth = Math.Atan2(tx, ty);
                        altitude = Math.Atan2(Math.Sqrt(tx * tx + ty * ty), tz);
                        altitude /= (Math.PI / 2.0);
                    }
                    else
                    {
                        double c = Math.Cos(look);
                        double s = Math.Sin(look);
                        x = tx * c + ty * s;
                        y = tx * s - ty * c;
                        z = tz;

                        if (x < 0) continue;
                        azimuth = Math.Atan2(y, z) + Math.PI / 2.0;
                        altitude = Math.Atan2(Math.Sqrt(y * y + z * z), x);
                        altitude /= (Math.PI / 2.0);
                    }
                }
            }
            return 20000.0;
        }

        public string Id()
        {
            return $"\"{_name}\" {_x} {_y} {_mag} \"{_marker}\"";
        }
    }
}