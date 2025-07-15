using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Input;
using System.Globalization;

namespace Ephemeris.WPF
{
    public class EphemerisIIAnimation : Control, IAnimated
    {
        private Size _size;
        private int _side, _halfSide;
        private double _spinAngle, _tiltVector;
        private StarDome _sky = new StarDome();
        private EphemerisIIControlPanel _controlPanel; // Placeholder
        private bool _fontData = false;
        private Typeface _defaultTypeface, _nTypeface;
        private double _nFontHeight;
        private double[] _nFontWidths = new double[4];
        private char[] _cardinal = { 'N', 'E', 'W', 'S' };
        private DrawingContext _drawingContext; // Equivalent to Java's Graphics context
        private bool _up;
        private double _look;
        private double _offset, _bearing;
        private EphemerisIIFrame _frame; // Placeholder
        private bool _start = true;

        public EphemerisIIAnimation()
        {
            // Set a transparent background to allow custom drawing
            Background = Brushes.Transparent;
            // Enable mouse events
            MouseLeftButtonDown += OnMouseLeftButtonDown;
            MouseMove += OnMouseMove;
        }

        public void Associate(EphemerisIIControlPanel controlPanel) { _controlPanel = controlPanel; }
        public void Associate(EphemerisIIFrame frame) { _frame = frame; }

        public void SetAngles(double spin, double tilt)
        {
            _spinAngle = spin;
            _tiltVector = tilt;
        }

        private Point SunPath(double angle)
        {
            double y = -Math.Cos(angle) - _offset * Math.Sin(_bearing);
            double x = Math.Sin(angle) * Math.Sin(_tiltVector) - _offset * Math.Cos(_bearing);
            double z = Math.Sin(angle) * Math.Cos(_tiltVector);
            return GetAltAz(x, y, z);
        }

        private Point SouthPath(double angle, double dm, double em)
        {
            double northpoint = (dm + Math.PI - em) / 2.0;
            double subtend = northpoint - dm;

            double ty = Math.Cos(subtend);
            double tx = -Math.Sin(subtend) * Math.Cos(angle);
            double tz = Math.Sin(subtend) * Math.Sin(angle);

            double y = ty * Math.Sin(northpoint) - tx * Math.Cos(northpoint) - _offset * Math.Sin(_bearing);
            double x = ty * Math.Cos(northpoint) + tx * Math.Sin(northpoint) - _offset * Math.Cos(_bearing);
            return GetAltAz(y, x, tz);
        }

        private Point GetAltAz(double x, double y, double z)
        {
            double altitude, azimuth;
            int xx = 1;
            if (_up)
            {
                azimuth = Math.Atan2(x, y);
                altitude = Math.Atan2(Math.Sqrt(x * x + y * y), z);
                altitude /= (Math.PI / 2.0);
            }
            else
            {
                double c = Math.Cos(_look);
                double s = Math.Sin(_look);
                double tx = x * c + y * s;
                double ty = x * s - y * c;
                double tz = z;

                azimuth = Math.Atan2(ty, tz) + Math.PI / 2.0;
                altitude = Math.Atan2(Math.Sqrt(ty * ty + tz * tz), tx);
                altitude /= (Math.PI / 2.0);
                if (tx < 0) return new Point(-1, -1);
                xx = -1;
            }
            return new Point(
                _side / 2 + xx * (int)Math.Round(_halfSide * altitude * Math.Cos(azimuth)),
                _side / 2 - (int)Math.Round(_halfSide * altitude * Math.Sin(azimuth)));
        }

        private Point Primitive(double pA, double pB)
        {
            double sx = Math.Sin(pA) * Math.Sin(pB);
            double sy = Math.Sin(pA) * Math.Cos(pB);
            double sz = Math.Cos(pA);

            double ty = sy;
            double tx = sx * Math.Cos(_tiltVector) + sz * Math.Sin(_tiltVector);
            double tz = -sx * Math.Sin(_tiltVector) + sz * Math.Cos(_tiltVector);

            tx -= _offset * Math.Cos(_bearing);
            ty -= _offset * Math.Sin(_bearing);

            return GetAltAz(tx, ty, tz);
        }

        private Point SpinPath(double angle, double twist)
        {
            double tx = -Math.Cos(angle);
            double ty = 0;
            double tz = Math.Sin(angle);

            double y = ty;
            double x = tx * Math.Cos(_tiltVector) - tz * Math.Sin(_tiltVector);
            double z = tx * Math.Sin(_tiltVector) + tz * Math.Cos(_tiltVector);

            x = tx * Math.Cos(twist) + ty * Math.Sin(twist);
            y = -tx * Math.Sin(twist) + ty * Math.Cos(twist);
            z = tz;

            ty = y;
            tx = x * Math.Cos(_tiltVector) + z * Math.Sin(_tiltVector);
            tz = -x * Math.Sin(_tiltVector) + z * Math.Cos(_tiltVector);

            tx -= _offset * Math.Cos(_bearing);
            ty -= _offset * Math.Sin(_bearing);
            return GetAltAz(tx, ty, tz);
        }

        private void OnMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            // This is a simplified hit test. A more robust solution would involve
            // iterating through drawn objects and checking for intersection.
            // For now, we'll just invalidate visual to trigger a redraw.
            InvalidateVisual();
        }

        private void OnMouseMove(object sender, MouseEventArgs e)
        {
            // Similar to OnMouseLeftButtonDown, this is a placeholder.
            // Actual hit testing and UI updates would go here.
            // For now, just invalidate visual if needed for dynamic updates.
            // InvalidateVisual();
        }

        private double _yuthu;

        private void Rescale(double[] v)
        {
            double l = Math.Sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);

            double b = v[1] * _yuthu / l;
            double k = Math.Sqrt(b * b + 1.0 - _yuthu * _yuthu) - b;

            v[0] = k * v[0] / l;
            v[1] = _yuthu + k * v[1] / l;
            v[2] = k * v[2] / l;
        }

        private void DrawFrame(DrawingContext g)
        {
            if (E2Param.Yuthu) _yuthu = Math.Sin(Math.PI / 20.0);
            else _yuthu = (3.6 / E2Param.DomeRadius);

            Point df1 = new Point(), dt1, df2 = new Point(), dt2, df3 = new Point(), dt3;
            g.DrawRectangle(null, new Pen(Brushes.LightGray, 1), new Rect(0, 0, ActualWidth, ActualHeight)); // Placeholder for background

            double poley = Math.Sin(_tiltVector) - _yuthu;
            double polez = Math.Cos(_tiltVector);
            double yudist = Math.Sqrt(poley * poley + polez * polez);
            double[] v = new double[3];

            poley /= yudist;
            polez /= yudist;

            double ny = polez;
            double nz = -poley;

            double s1 = Math.Sin(Math.PI / 10.0);
            double s2 = Math.Sin(7.0 * Math.PI / 20.0);
            double s3 = Math.Sin(Math.PI / 2.0);

            double c1 = Math.Cos(Math.PI / 10.0);
            double c2 = Math.Cos(7.0 * Math.PI / 20.0);
            double c3 = Math.Cos(Math.PI / 2.0);
            double offx = _offset * Math.Cos(_bearing);
            double offy = _offset * Math.Sin(_bearing);

            for (int i = 0; i < 65; ++i)
            {
                double angle = i * Math.PI / 32.0;
                double s = Math.Sin(angle);
                double c = Math.Cos(angle);

                // First ring
                v[0] = s1 * c;
                v[1] = poley * c1 + s1 * s * ny;
                v[2] = polez * c1 + s1 * s * nz;
                Rescale(v);
                dt1 = GetAltAz(v[1] - offx, v[0] - offy, v[2]);
                if (i > 0 && df1.X > 0 && df1.Y > 0 && dt1.X > 0 && dt1.Y > 0)
                {
                    g.DrawLine(new Pen(Brushes.LightGray, 1), df1, dt1);
                }
                df1 = dt1;

                // Second ring
                v[0] = s2 * c;
                v[1] = poley * c2 + s2 * s * ny;
                v[2] = polez * c2 + s2 * s * nz;
                Rescale(v);
                dt2 = GetAltAz(v[1] - offx, v[0] - offy, v[2]);
                if (i > 0 && df2.X > 0 && df2.Y > 0 && dt2.X > 0 && dt2.Y > 0)
                {
                    g.DrawLine(new Pen(Brushes.LightGray, 1), df2, dt2);
                }
                df2 = dt2;

                // Third ring
                v[0] = s3 * c;
                v[1] = poley * c3 + s3 * s * ny;
                v[2] = polez * c3 + s3 * s * nz;
                Rescale(v);
                dt3 = GetAltAz(v[1] - offx, v[0] - offy, v[2]);
                if (i > 0 && df3.X > 0 && df3.Y > 0 && dt3.X > 0 && dt3.Y > 0)
                {
                    g.DrawLine(new Pen(Brushes.LightGray, 1), df3, dt3);
                }
                df3 = dt3;
            }
        }

        protected override void OnRender(DrawingContext drawingContext)
        {
            base.OnRender(drawingContext);
            _drawingContext = drawingContext;

            // Placeholder for control panel and frame. Will be associated later.
            if (_controlPanel == null || _frame == null) return;

            _controlPanel.SetTime();

            if (!_fontData)
            {
                _defaultTypeface = new Typeface(new FontFamily("Arial"), FontStyles.Normal, FontWeights.Normal, FontStretches.Normal);
                _nTypeface = new Typeface(new FontFamily("Arial"), FontStyles.Normal, FontWeights.Bold, FontStretches.Normal);

                // Measure font metrics (approximate for WPF)
                FormattedText ft = new FormattedText("N", CultureInfo.CurrentCulture, FlowDirection.LeftToRight, _nTypeface, 24, Brushes.Black);
                _nFontHeight = ft.Height;
                _nFontWidths[0] = new FormattedText("N", CultureInfo.CurrentCulture, FlowDirection.LeftToRight, _nTypeface, 24, Brushes.Black).Width;
                _nFontWidths[1] = new FormattedText("E", CultureInfo.CurrentCulture, FlowDirection.LeftToRight, _nTypeface, 24, Brushes.Black).Width;
                _nFontWidths[2] = new FormattedText("W", CultureInfo.CurrentCulture, FlowDirection.LeftToRight, _nTypeface, 24, Brushes.Black).Width;
                _nFontWidths[3] = new FormattedText("S", CultureInfo.CurrentCulture, FlowDirection.LeftToRight, _nTypeface, 24, Brushes.Black).Width;

                _fontData = true;
            }

            // Draw background
            drawingContext.DrawRectangle(Brushes.Black, null, new Rect(0, 0, ActualWidth, ActualHeight));

            if (_start)
            {
                _controlPanel.Adjust();
                _start = false;
            }

            _up = _controlPanel.GetUp();
            _look = -_controlPanel.GetLook();

            _size = new Size(ActualWidth, ActualHeight);
            _side = (int)Math.Min(_size.Width, _size.Height);
            if (!_up) _side = (int)Math.Min(_size.Width, 2 * _size.Height);
            _halfSide = (int)(0.45 * _side);
            int margin = _side / 2 - _halfSide;
            int x1 = margin;
            int d1 = 2 * _halfSide;

            SetAngles(_controlPanel.Spin(), _controlPanel.Slide());

            bool day = false;
            double dayLength = E2Param.GetDayLength(_controlPanel.GetBaseHarmonic());
            double dawn = (1.0 - dayLength) / 2.0;

            int blue = 0;
            double twilength = 0.1 * dayLength;

            if (_controlPanel.Hour < (dawn - twilength)) { /* no change */ }
            else if (_controlPanel.Hour < dawn)
            {
                blue = (int)(255.0 * (1.0 + (_controlPanel.Hour - dawn) / twilength));
            }
            else if ((1.0 - _controlPanel.Hour) > dawn)
            {
                blue = 255;
                day = true;
            }
            else if ((1.0 - _controlPanel.Hour) > (dawn - twilength))
            {
                blue = (int)(255.0 * (1.0 + (1.0 - _controlPanel.Hour - dawn) / twilength));
            }

            if (blue > 0)
            {
                Color skyColor = Color.FromArgb(255, 0, 0, (byte)blue);
                if (_up) drawingContext.DrawEllipse(new SolidColorBrush(skyColor), null, new Point(x1 + d1 / 2, x1 + d1 / 2), d1 / 2, d1 / 2);
                else drawingContext.DrawGeometry(new SolidColorBrush(skyColor), null, Geometry.Parse($"M {x1},{x1 + d1 / 2} A {d1 / 2},{d1 / 2} 0 0 1 {x1 + d1},{x1 + d1 / 2} L {x1 + d1},{x1 + d1 / 2} L {x1},{x1 + d1 / 2} Z"));
            }

            bool stars = (blue < 127) || _frame.CbObscure.IsChecked == true; // Assuming IsChecked for CheckboxMenuItem

            // Draw horizon and cardinal points
            Pen greenPen = new Pen(Brushes.Green, 1);
            if (_up)
            {
                for (int i = 0; i < 5; ++i)
                {
                    drawingContext.DrawEllipse(null, greenPen, new Point(x1 + d1 / 2, x1 + d1 / 2), d1 / 2, d1 / 2);
                    x1--;
                    d1 += 2;
                }

                // Cardinal points
                DrawText(_cardinal[0].ToString(), _nTypeface, 24, Brushes.White, new Point((_side - _nFontWidths[0]) / 2, margin - _nFontHeight / 4));
                DrawText(_cardinal[1].ToString(), _nTypeface, 24, Brushes.Yellow, new Point(margin - 3 * _nFontWidths[1] / 2, (_side + _nFontHeight) / 2));
                DrawText(_cardinal[2].ToString(), _nTypeface, 24, Brushes.Red, new Point((_side - margin + _nFontWidths[2] / 2), (_side + _nFontHeight) / 2));
                DrawText(_cardinal[3].ToString(), _nTypeface, 24, Brushes.Green, new Point((_side - _nFontWidths[3]) / 2, (_side - margin + 3 * _nFontHeight / 4)));
            }
            else
            {
                for (int i = 0; i < 5; ++i)
                {
                    // DrawArc equivalent for WPF
                    PathGeometry arcGeometry = new PathGeometry();
                    PathFigure arcFigure = new PathFigure();
                    arcFigure.StartPoint = new Point(x1, x1 + d1 / 2);
                    arcFigure.Segments.Add(new ArcSegment(new Point(x1 + d1, x1 + d1 / 2), new Size(d1 / 2, d1 / 2), 0, false, SweepDirection.Clockwise, true));
                    arcGeometry.Figures.Add(arcFigure);
                    drawingContext.DrawGeometry(null, greenPen, arcGeometry);

                    drawingContext.DrawLine(greenPen, new Point(x1, x1 + d1 / 2), new Point(x1 + d1, x1 + d1 / 2));
                    x1--;
                    d1 += 2;
                }

                int span = 2 * d1;
                int offset = -(int)Math.Round(span * _look / (Math.PI * 2.0));
                int baseVal = offset - span;

                for (int go = 0; go < 3; ++go, baseVal += span)
                {
                    if (Math.Abs(baseVal) <= d1 / 2 + _nFontWidths[0])
                    {
                        DrawText(_cardinal[0].ToString(), _nTypeface, 24, Brushes.White, new Point(x1 + d1 / 2 - (baseVal) - _nFontWidths[0] / 2, x1 + d1 / 2 + _nFontHeight + 2));
                    }
                    if (Math.Abs(baseVal - d1 / 2) <= d1 / 2 + _nFontWidths[1])
                    {
                        DrawText(_cardinal[1].ToString(), _nTypeface, 24, Brushes.Yellow, new Point(x1 + d1 / 2 - (baseVal - d1 / 2) - _nFontWidths[1] / 2, x1 + d1 / 2 + _nFontHeight + 2));
                    }
                    if (Math.Abs(baseVal + d1 / 2) <= d1 / 2 + _nFontWidths[2])
                    {
                        DrawText(_cardinal[2].ToString(), _nTypeface, 24, Brushes.Red, new Point(x1 + d1 / 2 - (baseVal + d1 / 2) - _nFontWidths[2] / 2, x1 + d1 / 2 + _nFontHeight + 2));
                    }
                    if (Math.Abs(baseVal + d1) <= d1 / 2 + _nFontWidths[3])
                    {
                        DrawText(_cardinal[3].ToString(), _nTypeface, 24, Brushes.Green, new Point(x1 + d1 / 2 - (baseVal + d1) - _nFontWidths[3] / 2, x1 + d1 / 2 + _nFontHeight + 2));
                    }
                }
            }

            _offset = _controlPanel.GetOffset() * 20.0 / E2Param.DomeRadius;
            _bearing = -_controlPanel.GetBearing();

            bool names = _frame.CbNames.IsChecked == true; // Assuming IsChecked for CheckboxMenuItem
            if (stars) _sky.Draw(drawingContext, _spinAngle, _tiltVector, _halfSide,
                      _side / 2, _side / 2, names, _offset, _bearing, _up, _look);

            // Sunpath
            if (_frame.CbSunPath.IsChecked == true)
            {
                Point sunPathCurrentPoint = SunPath(0.0);
                for (int i = 1; i < 51; ++i)
                {
                    Point c = SunPath(i * Math.PI / 50.0);
                    if (sunPathCurrentPoint.X > 0 && c.X > 0 && sunPathCurrentPoint.Y > 0 && c.Y > 0)
                        drawingContext.DrawLine(new Pen(Brushes.Yellow, 1), sunPathCurrentPoint, c);
                    sunPathCurrentPoint = c;
                }
            }

            double hourAngle, h;
            Point xy;

            // Yelm & Lightfore
            if (day)
            {
                hourAngle = Math.PI * (_controlPanel.Hour - dawn) / dayLength;
                xy = SunPath(hourAngle);
                if (xy.X > 0 && xy.Y > 0)
                {
                    int sunsize = _controlPanel.CbRing.IsChecked == true ?
                        Math.Max(1, (int)Math.Round(_halfSide / 180.0))
                        : 12;
                    drawingContext.DrawEllipse(Brushes.Yellow, null, xy, sunsize / 2.0, sunsize / 2.0);
                    if (names) DrawText("Yelm", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 12, xy.Y));
                }
            }
            else
            {
                h = _controlPanel.Hour;
                if (h > 0.5)
                {
                    hourAngle = (h - (1.0 - dawn)) / (1.0 - dayLength);
                }
                else
                {
                    hourAngle = (h + dawn) / (1.0 - dayLength);
                }
                hourAngle *= Math.PI;
                xy = SunPath(hourAngle);
                if (xy.X > 0 && xy.Y > 0)
                {
                    drawingContext.DrawEllipse(Brushes.White, null, xy, 2, 2);
                    if (names) DrawText("Lightfore", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 4, xy.Y));
                }

                // Lightfore path
                if (_frame.CbLight.IsChecked == true)
                {
                    double k1 = (1.0 - dayLength) * Math.PI / 25.0;
                    double t0 = -2.0 * (1.0 - dayLength) * hourAngle - Math.PI / 2.0;

                    Point lightforePathCurrentPoint = SpinPath(0.0, t0);
                    drawingContext.DrawEllipse(Brushes.LightGray, null, lightforePathCurrentPoint, 2, 2);
                    for (int i = 1; i < 51; ++i)
                    {
                        Point c = SpinPath(i * Math.PI / 50.0, t0 + i * k1);
                        if (lightforePathCurrentPoint.X > 0 && lightforePathCurrentPoint.Y > 0 && c.X > 0 && c.Y > 0)
                            drawingContext.DrawLine(new Pen(Brushes.LightGray, 1), lightforePathCurrentPoint, c);
                        lightforePathCurrentPoint = c;
                    }
                }
            }

            double cycle;

            // Theya
            if (!day || _frame.CbObscure.IsChecked == true)
            {
                double fliptime = dawn + (1.0 - dawn) / 2.0; // Simplified from original
                double cf_hour = _controlPanel.Hour;
                if (cf_hour > 0.99999) cf_hour -= 1.0;
                if (cf_hour > fliptime) cf_hour = fliptime * 2.0 - cf_hour;
                cycle = (5 * (cf_hour - dawn) + 1.0) * Math.PI / 8;
                xy = SunPath(cycle);
                if (xy.X > 0 && xy.Y > 0 && cf_hour >= (dawn - 0.2))
                {
                    drawingContext.DrawEllipse(Brushes.White, null, xy, 2, 2);
                    if (names) DrawText("Theya", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 4, xy.Y));
                }
            }

            // Rausa
            if (!day || _frame.CbObscure.IsChecked == true)
            {
                double fliptime = dawn + (1.0 - dawn) / 2.0;
                double cf_hour = _controlPanel.Hour;
                if (cf_hour < 0.00001) cf_hour += 1.0;
                if (cf_hour < fliptime) cf_hour = fliptime * 2.0 - cf_hour;
                cycle = (1 + (5 * (cf_hour - 1) / 8)) * Math.PI;
                xy = SunPath(cycle);
                if (xy.X > 0 && xy.Y > 0 && cf_hour <= 1.00001)
                {
                    drawingContext.DrawEllipse(Brushes.Red, null, xy, 2, 2);
                    if (names) DrawText("Rausa", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 4, xy.Y));
                }
            }

            // Kalikos
            if (stars)
            {
                double upAngle = -1;
                if (_controlPanel.Hour < dawn) upAngle = (dawn - _controlPanel.Hour) * 5 * Math.PI / 8;
                else if (_controlPanel.Hour > (1.0 - dawn)) upAngle = (_controlPanel.Hour - 1.0 + dawn) * 5 * Math.PI / 8;
                if (upAngle > 0)
                {
                    double qy = -Math.Cos(upAngle) * Math.Cos(0.2) - _offset * Math.Sin(_bearing);
                    double qx = Math.Cos(upAngle) * Math.Sin(0.2) - _offset * Math.Cos(_bearing);
                    double qz = Math.Sin(upAngle);
                    xy = GetAltAz(-qy, qx, qz);
                    drawingContext.DrawEllipse(Brushes.White, null, xy, 2, 2);
                    if (names) DrawText("Kalikos", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 4, xy.Y));
                }
            }

            // Entekos/Moskalf
            long dayno = _controlPanel.Year * 294 + _controlPanel.Week * 7 + _controlPanel.Day;
            long entekosOffset = 5 * 294 + 135;
            cycle = 0.5 + (((double)((dayno - entekosOffset) % 62)) + _controlPanel.Hour - 0.75) / 62.0;
            if (cycle > 1.0) cycle -= 1.0;
            if (stars && cycle >= 0 && cycle <= 0.5)
            {
                xy = SunPath(cycle * 2.0 * Math.PI);
                if (xy.X > 0 && xy.Y > 0)
                {
                    drawingContext.DrawEllipse(Brushes.LightGray, null, xy, 3, 3);
                    if (names) DrawText("Entekos", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 6, xy.Y));
                }
            }

            // Lokarnos/Wagon
            long wagonOffset = 152;
            cycle = (((double)((dayno - wagonOffset) % 14)) + _controlPanel.Hour) / 14.0;
            if (cycle < 0) cycle += 1.0;
            if (stars && cycle >= 0 && cycle <= 0.5)
            {
                xy = SunPath(cycle * 2.0 * Math.PI);
                if (xy.X > 0 && xy.Y > 0)
                {
                    drawingContext.DrawEllipse(Brushes.LightGray, null, xy, 4, 4);
                    if (names) DrawText("Lokarnos", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 8, xy.Y));
                }
            }

            // Uleria/Mastakos
            if (!E2Param.Uleria)
            {
                cycle = (_controlPanel.Hour - 0.75);
            }
            else
            {
                cycle = _controlPanel.Week * 7 + _controlPanel.Day + _controlPanel.Hour - 135.75;
                cycle *= 294.0 / 293.0;
                cycle -= Math.Floor(cycle);
            }
            if (cycle < 0) cycle += 1.0;
            cycle *= 3.0;
            while (cycle > 1.0) cycle -= 1.0;
            if (cycle < 0) cycle += 1.0;
            cycle *= Math.PI;

            if (stars)
            {
                SolidColorBrush uleriaBrush = blue > 127 ? Brushes.Cyan : Brushes.Blue;
                xy = SunPath(cycle);
                if (xy.X > 0 && xy.Y > 0)
                {
                    drawingContext.DrawEllipse(uleriaBrush, null, xy, 2, 2);
                    if (names) DrawText("Uleria", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 4, xy.Y));
                }
            }

            // White Orbiter
            if (_controlPanel.Year > 1725)
            {
                DrawOrbiter(drawingContext);
            }

            // South path and south path planets
            double dm, em;
            Point southPathPoint;
            if (_frame.CbSouthPath.IsChecked == true)
            {
                em = EastMouth(dayno, _controlPanel.Hour);
                dm = DodgeMouth(dayno, _controlPanel.Hour);

                southPathPoint = SouthPath(0.0, dm, em);
                for (int i = 1; i < 51; ++i)
                {
                    Point c = SouthPath(i * Math.PI / 50.0, dm, em);
                    if (southPathPoint.X > 0 && c.X > 0 && southPathPoint.Y > 0 && c.Y > 0)
                        drawingContext.DrawLine(new Pen(Brushes.Red, 1), southPathPoint, c);
                    southPathPoint = c;
                }
            }

            // Shargash
            cycle = (((double)(dayno % 28)) + _controlPanel.Hour - E2Param.ShargashRise) / 28.0;
            if (cycle < 0) cycle += 1.0;
            if (cycle >= 0 && cycle <= 0.5)
            {
                em = EastMouth(dayno, _controlPanel.Hour - 28 * cycle);
                dm = DodgeMouth(14 + dayno, _controlPanel.Hour - 28 * cycle);
                Point shargashPoint = SouthPath(cycle * 2.0 * Math.PI, dm, em);
                if (shargashPoint.X > 0 && shargashPoint.Y > 0)
                {
                    drawingContext.DrawEllipse(Brushes.Red, null, shargashPoint, 4, 4);
                    if (names) DrawText("Shargash", _defaultTypeface, 12, Brushes.LightGray, new Point(shargashPoint.X + 8, shargashPoint.Y));
                }
            }

            // TwinStar
            cycle = (((double)(dayno % 6)) + _controlPanel.Hour - E2Param.TwinRise) / 6.0;
            if (cycle < 0) cycle += 1.0;
            if (stars && cycle >= 0 && cycle <= 0.5)
            {
                em = EastMouth(dayno, _controlPanel.Hour - 6 * cycle);
                dm = DodgeMouth(3 + dayno, _controlPanel.Hour - 6 * cycle);
                Point twinStarPoint = SouthPath(cycle * 2.0 * Math.PI, dm, em);
                if (twinStarPoint.X > 0 && twinStarPoint.Y > 0)
                {
                    drawingContext.DrawEllipse(Brushes.Yellow, null, new Point(twinStarPoint.X - 4, twinStarPoint.Y), 2, 2);
                    drawingContext.DrawEllipse(Brushes.White, null, new Point(twinStarPoint.X, twinStarPoint.Y + 4), 2, 2);
                    if (names) DrawText("TwinStars", _defaultTypeface, 12, Brushes.LightGray, new Point(twinStarPoint.X + 8, twinStarPoint.Y));
                }
            }

            // Artia
            if (_controlPanel.Week < 40)
            {
                int parity = _controlPanel.Year % 2;
                long qdayno = (_controlPanel.Week * 7 + _controlPanel.Day + 280 * parity);
                cycle = (((double)((qdayno % 112))) + _controlPanel.Hour - E2Param.ArtiaRise) / 112.0;
                if (cycle < 0) cycle += 1.0;
                if (stars && cycle >= 0 && cycle <= 0.5)
                {
                    qdayno = _controlPanel.Year * 294;
                    double riseday = _controlPanel.Week * 7 + _controlPanel.Day + _controlPanel.Hour - cycle * 112.0;
                    double setday = riseday + 56.0;

                    if (riseday < 0) riseday -= 14.0;
                    em = EastMouth(qdayno, riseday);

                    if (setday > 280.0) setday += 14.0;
                    dm = DodgeMouth(qdayno, setday);
                    Point artiaPoint = SouthPath(cycle * 2.0 * Math.PI, dm, em);
                    if (artiaPoint.X > 0 && artiaPoint.Y > 0)
                    {
                        drawingContext.DrawEllipse(Brushes.Red, null, artiaPoint, 2, 2);
                        if (names) DrawText("Artia", _defaultTypeface, 12, Brushes.LightGray, new Point(artiaPoint.X + 4, artiaPoint.Y));
                    }
                }
            }

            // Buserian's frame
            if (_frame.CbFrame.IsChecked == true)
            {
                DrawFrame(drawingContext);
            }

            // Zenith
            if (stars)
            {
                double pA = 0.15 * Math.PI;
                double pB = 4.0;
                double sx = Math.Sin(pA) * Math.Sin(pB);
                double sy = Math.Sin(pA) * Math.Cos(pB);
                double sz = Math.Cos(pA);

                sx -= _offset * Math.Cos(_bearing);
                sy -= _offset * Math.Sin(_bearing);

                xy = GetAltAz(sx, sy, sz);
                drawingContext.DrawEllipse(Brushes.LightGray, null, xy, 2, 2);
                if (names) DrawText("Zenith", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 4, xy.Y));
            }

            // Stormgate
            if (stars && ((_controlPanel.Week % 2 == 1 && _controlPanel.Day == 0 && _controlPanel.Hour < 0.5) ||
                          (_controlPanel.Week % 2 == 0 && _controlPanel.Day == 6 && _controlPanel.Hour > 0.5)))
            {
                xy = Primitive(0.35 * Math.PI, 0.5);
                if (xy.X > 0 && xy.Y > 0)
                {
                    drawingContext.DrawEllipse(Brushes.LightGray, null, xy, 2, 2);
                    if (names) DrawText("Stormgate", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 4, xy.Y));
                }
            }

            // Orlanth's ring
            if (stars)
            {
                cycle = (_controlPanel.Week * 7 + _controlPanel.Day) % 14 + _controlPanel.Hour;
                double start = 6.0 + 23.0 / 24.0 + 13.0 / 1440.0;
                cycle -= start;
                if (cycle < 0) cycle += 14;
                double top = 7 + 1.0 / 24.0;
                if (cycle >= 0 && cycle <= top)
                {
                    double polarAngle = 0.05 * Math.PI * (7.0 - cycle);
                    double polarBearing = 0.5 - (294.0 / 293.0) * 2.0 * Math.PI * cycle;

                    xy = Primitive(polarAngle, polarBearing);

                    double period = 2.0 / 21.0;
                    double rotations = cycle / period;

                    if (xy.X > 0 && xy.Y > 0)
                    {
                        if (_controlPanel.CbRing.IsChecked == true)
                        {
                            int size = Math.Max(1, (int)Math.Round(_halfSide / 240.0));
                            drawingContext.DrawEllipse(Brushes.Orange, null, xy, size / 2.0, size / 2.0);
                        }
                        else
                        {
                            for (int i = 0; i < 8 && i <= 16.0 * rotations; ++i)
                            {
                                SolidColorBrush ringBrush = Brushes.Orange;
                                if (7 == i) ringBrush = Brushes.Green;
                                if (cycle > 7)
                                {
                                    double del = (cycle - 7.0) * 168;
                                    if (i < del) continue;
                                }
                                double da = -polarBearing + Math.PI * (rotations * 2 - i / 4.0);
                                int dx = (int)Math.Round(10.0 * Math.Cos(da));
                                int dy = (int)Math.Round(10.0 * Math.Sin(da));
                                drawingContext.DrawEllipse(ringBrush, null, new Point(xy.X - 2 + dx, xy.Y - 2 + dy), 2, 2);
                            }
                        }
                        if (names) DrawText("Orlanth's Ring", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + 10, xy.Y));
                    }
                }
            }

            // Red Moon
            if (_controlPanel.Year >= 1247 && _controlPanel.Year <= 1725)
            {
                DrawMoon(drawingContext);
            }
        }

        public void Paint(DrawingContext g)
        {
            // This method is part of IAnimated, but drawing is done in OnRender for WPF controls.
            // We can call InvalidateVisual to trigger OnRender.
            InvalidateVisual();
        }

        public void SetSize(Size d)
        {
            _size = d;
            // Invalidate visual to trigger redraw with new size
            InvalidateVisual();
        }

        public Size GetSize()
        {
            return _size;
        }

        private double EastMouth(long dayno, double hour)
        {
            double baseDate = E2Param.ShargashRise - 7.0;
            double cycle = Math.PI * ((dayno % 28) + hour - baseDate) / 14.0;
            double angle = Math.PI / 18 * (Math.Sin(2 * cycle) + Math.Sin(cycle) - 0.85);
            return angle;
        }

        private double DodgeMouth(long dayno, double hour)
        {
            double cycle = 2.0 * Math.PI * ((dayno % 81) + hour) / 81.0;
            double angle = Math.Cos(cycle) + Math.Cos(2 * cycle) +
                                 Math.Cos(3 * cycle) + Math.Cos(5 * cycle) - 1.0;

            double nmax = 2.0 * Math.PI / 9.0;
            double smax = -Math.PI / 4.0;

            angle *= (smax - nmax) / -6.0;
            angle += (nmax + smax) / 2.0;

            return angle;
        }

        private void DrawText(string text, Typeface typeface, double fontSize, Brush brush, Point origin)
        {
            FormattedText formattedText = new FormattedText(
                text,
                CultureInfo.CurrentCulture,
                FlowDirection.LeftToRight,
                typeface,
                fontSize,
                brush
            );
            _drawingContext.DrawText(formattedText, origin);
        }

        public void DrawOrbiter(DrawingContext g)
        {
            double days = (294 * (_controlPanel.Year - 1725) + _controlPanel.Week * 7) % 28 + _controlPanel.Day + _controlPanel.Hour;
            double orbits = days * 29.0 / 28.0;
            while (orbits > 0.5) orbits -= 1.0;
            double cycle = orbits + 0.25;
            if (cycle >= 0 && cycle <= 0.5)
            {
                Point xy = SunPath(cycle * Math.PI * 2.0);
                int x = (int)xy.X;
                int y = (int)xy.Y;

                g.DrawEllipse(Brushes.LightGray, null, new Point(x, y), 6, 6);
                
                days = 28 - days;
                if (days < 14)
                    g.DrawGeometry(Brushes.Black, null, Geometry.Parse($"M {x},{y} A 7,7 0 0 0 {x},{y-7} A 7,7 0 0 0 {x},{y+7} Z")); // Simplified arc
                else
                    g.DrawGeometry(Brushes.Black, null, Geometry.Parse($"M {x},{y} A 7,7 0 0 1 {x},{y-7} A 7,7 0 0 1 {x},{y+7} Z")); // Simplified arc

                if (Math.Abs(days - 14) > 7) g.DrawEllipse(Brushes.LightGray, null, new Point(x, y), 7, 7); // Placeholder for fill
                if (days > 14) days -= 14;
                int width = (int)Math.Round(days - 7);
                if (width < 0) width = -width;
                g.DrawEllipse(Brushes.LightGray, null, new Point(x, y), width, 7);

                if (_frame.CbNames.IsChecked == true) DrawText("White Orbiter", _defaultTypeface, 12, Brushes.LightGray, new Point(x + 14, y));
            }
        }

        public void DrawMoon(DrawingContext g)
        {
            double myOffset = _controlPanel.GetOffset();
            double mx = myOffset * Math.Cos(_bearing);
            double my = myOffset * Math.Sin(_bearing);
            double moony = 0.150 / 20.0;
            double moonx = 3.5 / 20.0;

            mx -= moonx;
            my -= moony;

            double norm = Math.Sqrt(mx * mx + my * my);
            double nx = mx / norm;
            double ny = my / norm;
            mx = -Math.Cos(0.35 * Math.PI) * mx / norm;
            my = -Math.Cos(0.35 * Math.PI) * my / norm;
            double mz = Math.Sin(0.35 * Math.PI);

            double ang = 30 + Math.Atan2(nx, ny) * 180.0 / Math.PI;
            double doy = (_controlPanel.Day + _controlPanel.Hour - 3.75);
            if (doy < 0) doy += 7.0;
            ang -= 360 * doy / 7.0;
            ang -= 180.0;

            Point xy = GetAltAz(mx, my, mz);
            int moonsize = _controlPanel.CbRing.IsChecked == true ?
               Math.Max(1, (int)Math.Round(_halfSide / 180.0))
               : 12;
            g.DrawLine(new Pen(Brushes.Red, 1), xy, xy);
            g.DrawEllipse(Brushes.Red, null, xy, moonsize / 2.0, moonsize / 2.0);
            while (ang < 0) ang += 360.0;
            while (ang > 360) ang -= 360;
            int x = (int)xy.X;
            int y = (int)xy.Y;
            ang = 360 - ang;
            if (ang > 360) ang -= 360;

            double tang = ang;
            if (tang > 180) tang -= 180;
            double frac = (Math.Abs(tang - 90) < 1) ? 100 : 90.0 / (tang - 90.0);

            if (_up)
            {
                for (int i = 0; i < moonsize; ++i)
                {
                    x = -moonsize / 2 + i;
                    for (int j = 0; j < moonsize; ++j)
                    {
                        y = -moonsize / 2 + j;
                        if (x * x + y * y >= moonsize * moonsize / 4.0) continue;

                        double cross = nx * x + ny * y;
                        if (ang < 180) cross *= -1;

                        if (cross < 0) g.DrawLine(new Pen(Brushes.Black, 1), new Point(x + xy.X, y + xy.Y), new Point(x + xy.X, y + xy.Y));

                        if (Math.Abs(ang - 180) > 90) g.DrawLine(new Pen(Brushes.Red, 1), new Point(x + xy.X, y + xy.Y), new Point(x + xy.X, y + xy.Y));
                        if (Math.Abs(tang - 90) > 1)
                        {
                            double dot = nx * y - ny * x;
                            cross *= frac;
                            if (cross * cross + dot * dot < moonsize * moonsize / 4.0)
                            {
                                g.DrawLine(new Pen(Brushes.Red, 1), new Point(x + xy.X, y + xy.Y), new Point(x + xy.X, y + xy.Y));
                            }
                        }
                    }
                }
            }
            else
            {
                if (ang < 180)
                    g.DrawGeometry(Brushes.Black, null, Geometry.Parse($"M {x},{y} A {moonsize / 2},{moonsize / 2} 0 0 0 {x},{y - moonsize / 2} A {moonsize / 2},{moonsize / 2} 0 0 0 {x},{y + moonsize / 2} Z"));
                else
                    g.DrawGeometry(Brushes.Black, null, Geometry.Parse($"M {x},{y} A {moonsize / 2},{moonsize / 2} 0 0 1 {x},{y - moonsize / 2} A {moonsize / 2},{moonsize / 2} 0 0 1 {x},{y + moonsize / 2} Z"));

                if (Math.Abs(ang - 180) > 90) g.DrawEllipse(Brushes.Red, null, new Point(x, y), moonsize / 2.0, moonsize / 2.0);
                if (ang > 180) ang -= 180;
                int width = (int)Math.Floor(moonsize * (ang - 90) / 180.0);
                if (width < 0) width = -width;
                g.DrawEllipse(Brushes.Red, null, new Point(x, y), width, moonsize / 2.0);
            }
            if (_frame.CbNames.IsChecked == true) DrawText("Red moon", _defaultTypeface, 12, Brushes.LightGray, new Point(xy.X + moonsize, xy.Y));
        }
    }
}