using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;

namespace Ephemeris.WPF
{
    public partial class E2LookPanel : UserControl
    {
        public bool Up { get; private set; } = true;
        public double Bearing { get; private set; } = 0.0;
        private bool _scan = false;
        private double _feed = 0.0;

        public E2LookPanel()
        {
            InitializeComponent();
            MouseLeftButtonDown += OnMouseLeftButtonDown;
            MouseMove += OnMouseMove;
            MouseLeftButtonUp += OnMouseLeftButtonUp;
            MouseLeave += OnMouseLeave;
        }

        protected override void OnRender(DrawingContext drawingContext)
        {
            base.OnRender(drawingContext);

            double lwi = ActualWidth;
            double lhi = ActualHeight;
            double buf = 0;
            double x = lwi;

            if (lhi < lwi)
            {
                x = lhi;
                buf = (lwi - lhi) / 2;
            }

            double halfx = x / 2;
            double rad = x / 3;
            double inner = rad / 2;
            double cx = buf + halfx;
            double cy = halfx;

            // Draw background circle
            drawingContext.DrawEllipse(Brushes.LightGray, null, new Point(cx, cy), rad, rad);

            // Draw green circles
            drawingContext.DrawEllipse(null, new Pen(Brushes.Green, 1), new Point(cx, cy), rad, rad);
            drawingContext.DrawEllipse(null, new Pen(Brushes.Green, 1), new Point(cx, cy), rad - 1, rad - 1);

            // Draw arcs
            Pen brighterPen = new Pen(Brushes.LightGray, 1);
            Pen darkerPen = new Pen(Brushes.DarkGray, 1);

            // Arc 1 (brighter)
            PathGeometry arc1 = new PathGeometry();
            PathFigure arc1Figure = new PathFigure();
            arc1Figure.StartPoint = new Point(cx + (rad + 1) * Math.Cos(45 * Math.PI / 180), cy - (rad + 1) * Math.Sin(45 * Math.PI / 180));
            arc1Figure.Segments.Add(new ArcSegment(new Point(cx + (rad + 1) * Math.Cos(225 * Math.PI / 180), cy - (rad + 1) * Math.Sin(225 * Math.PI / 180)), new Size(rad + 1, rad + 1), 0, false, SweepDirection.Clockwise, true));
            arc1.Figures.Add(arc1Figure);
            drawingContext.DrawGeometry(null, brighterPen, arc1);

            // Arc 2 (brighter)
            PathGeometry arc2 = new PathGeometry();
            PathFigure arc2Figure = new PathFigure();
            arc2Figure.StartPoint = new Point(cx + (rad + 2) * Math.Cos(45 * Math.PI / 180), cy - (rad + 2) * Math.Sin(45 * Math.PI / 180));
            arc2Figure.Segments.Add(new ArcSegment(new Point(cx + (rad + 2) * Math.Cos(225 * Math.PI / 180), cy - (rad + 2) * Math.Sin(225 * Math.PI / 180)), new Size(rad + 2, rad + 2), 0, false, SweepDirection.Clockwise, true));
            arc2.Figures.Add(arc2Figure);
            drawingContext.DrawGeometry(null, brighterPen, arc2);

            // Arc 3 (darker)
            PathGeometry arc3 = new PathGeometry();
            PathFigure arc3Figure = new PathFigure();
            arc3Figure.StartPoint = new Point(cx + (rad + 1) * Math.Cos(45 * Math.PI / 180), cy - (rad + 1) * Math.Sin(45 * Math.PI / 180));
            arc3Figure.Segments.Add(new ArcSegment(new Point(cx + (rad + 1) * Math.Cos(225 * Math.PI / 180), cy - (rad + 1) * Math.Sin(225 * Math.PI / 180)), new Size(rad + 1, rad + 1), 0, false, SweepDirection.Counterclockwise, true));
            arc3.Figures.Add(arc3Figure);
            drawingContext.DrawGeometry(null, darkerPen, arc3);

            // Arc 4 (darker)
            PathGeometry arc4 = new PathGeometry();
            PathFigure arc4Figure = new PathFigure();
            arc4Figure.StartPoint = new Point(cx + (rad + 2) * Math.Cos(45 * Math.PI / 180), cy - (rad + 2) * Math.Sin(45 * Math.PI / 180));
            arc4Figure.Segments.Add(new ArcSegment(new Point(cx + (rad + 2) * Math.Cos(225 * Math.PI / 180), cy - (rad + 2) * Math.Sin(225 * Math.PI / 180)), new Size(rad + 2, rad + 2), 0, false, SweepDirection.Counterclockwise, true));
            arc4.Figures.Add(arc4Figure);
            drawingContext.DrawGeometry(null, darkerPen, arc4);

            // Draw inner blue circle and lines
            drawingContext.DrawEllipse(null, new Pen(Brushes.Blue, 1), new Point(cx, cy), inner, inner);
            drawingContext.DrawLine(new Pen(Brushes.Blue, 1), new Point(cx - rad, halfx), new Point(cx - inner, halfx));
            drawingContext.DrawLine(new Pen(Brushes.Blue, 1), new Point(cx + inner, halfx), new Point(cx + rad, halfx));
            drawingContext.DrawLine(new Pen(Brushes.Blue, 1), new Point(buf + halfx, cy - rad), new Point(buf + halfx, cy - inner));
            drawingContext.DrawLine(new Pen(Brushes.Blue, 1), new Point(buf + halfx, cy + inner), new Point(buf + halfx, cy + rad));

            // Draw viewpoint
            if (Up)
            {
                drawingContext.DrawEllipse(Brushes.Red, null, new Point(buf + halfx, halfx), 2.5, 2.5);
            }
            else
            {
                double px = rad * Math.Sin(Bearing);
                double py = rad * Math.Cos(Bearing);
                int spx = (int)Math.Round(cx + px);
                int spy = (int)Math.Round(cy - py);
                drawingContext.DrawEllipse(Brushes.Red, null, new Point(spx, spy), 2.5, 2.5);
                drawingContext.DrawLine(new Pen(Brushes.Red, 1), new Point(cx, cy), new Point(spx, spy));
            }

            // Draw scan indicator
            if (_scan)
            {
                drawingContext.DrawEllipse(Brushes.DarkGray, null, new Point(cx + rad * Math.Sin(_feed), cy - rad * Math.Cos(_feed)), 2.5, 2.5);
                drawingContext.DrawLine(new Pen(Brushes.DarkGray, 1), new Point(cx, cy), new Point(cx + rad * Math.Sin(_feed), cy - rad * Math.Cos(_feed)));

                string feedStr = (_feed * 180.0 / Math.PI).ToString("F1");
                if (feedStr.Length > 7) feedStr = feedStr.Substring(0, 7);
                FormattedText formattedText = new FormattedText(
                    feedStr,
                    System.Globalization.CultureInfo.CurrentCulture,
                    FlowDirection.LeftToRight,
                    new Typeface("Arial"),
                    12,
                    Brushes.DarkGray
                );
                drawingContext.DrawText(formattedText, new Point(cx, cy));
            }
        }

        private void OnMouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            DoEvent(e, false, false, true, false);
        }

        private void OnMouseMove(object sender, MouseEventArgs e)
        {
            DoEvent(e, true, false, false, false);
        }

        private void OnMouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            // No specific action on mouse up in original Java code for this panel
            // DoEvent(e, false, false, false, false); // Or handle as needed
        }

        private void OnMouseLeave(object sender, MouseEventArgs e)
        {
            DoEvent(e, false, false, false, true);
        }

        private void DoEvent(MouseEventArgs eventArgs, bool move, bool drag, bool click, bool exit)
        {
            double lwi = ActualWidth;
            double lhi = ActualHeight;
            double buf = 0;
            double x = lwi;

            if (lhi < lwi)
            {
                x = lhi;
                buf = (lwi - lhi) / 2;
            }

            double halfx = x / 2;
            double rad = x / 3;
            double inner = rad / 2;
            double cx = buf + halfx;
            double cy = halfx;

            Point mousePosition = eventArgs.GetPosition(this);
            double px = mousePosition.X - cx;
            double py = cy - mousePosition.Y;
            double tr = Math.Sqrt(px * px + py * py);

            _scan = false;

            if (tr > rad || exit) { /* No action */ }
            else if (!click)
            {
                if (tr > inner)
                {
                    _scan = true;
                    _feed = Math.Atan2(px, py);
                }
            }
            else
            {
                Up = (tr < inner);
                if (!Up) Bearing = Math.Atan2(px, py);
            }
            InvalidateVisual();
        }
    }
}