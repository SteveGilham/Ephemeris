using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Ephemeris.WPF
{
    public partial class E2LocatorPanel : UserControl
    {
        private double _r = 0.0;
        private double _theta = 0.0;

        private static readonly double DRAD = 20.0;
        private static readonly double INNR = 5.75;
        private static readonly double FRAC = INNR / DRAD;

        private int _ix, _iy, _iw, _ih;
        private EphemerisIIControlPanel _scaler; // Reference to the control panel

        public double R => _r;
        public double Theta => _theta;

        public E2LocatorPanel(EphemerisIIControlPanel owner)
        {
            InitializeComponent();
            _scaler = owner;

            // Set the image source
            InnerWorldMap.Source = InnerWorld.GetBitmapSource();

            // Add mouse event handlers
            MouseLeftButtonUp += E2LocatorPanel_MouseLeftButtonUp;
            SizeChanged += E2LocatorPanel_SizeChanged;
        }

        private void E2LocatorPanel_SizeChanged(object sender, SizeChangedEventArgs e)
        {
            UpdateImagePositionAndSize();
            InvalidateVisual(); // Redraw the control
        }

        private void UpdateImagePositionAndSize()
        {
            double lwi = ActualWidth;
            double lhi = ActualHeight;
            double x = lwi;
            double buf = 0;

            if (lhi < lwi)
            {
                x = lhi;
                buf = (lwi - lhi) / 2;
            }

            double factor = x / 206.0;
            _ix = (int)Math.Round(buf + x / 2 - 91.0 * factor);
            _iy = (int)Math.Round(x / 2 - 98.0 * factor);
            _iw = (int)Math.Round(194.0 * factor);
            _ih = (int)Math.Round(168.0 * factor);

            // Set the position and size of the image within the Grid
            // This assumes InnerWorldMap is directly inside a Grid or Canvas
            InnerWorldMap.Margin = new Thickness(_ix, _iy, 0, 0);
            InnerWorldMap.Width = _iw;
            InnerWorldMap.Height = _ih;
            InnerWorldMap.HorizontalAlignment = HorizontalAlignment.Left;
            InnerWorldMap.VerticalAlignment = VerticalAlignment.Top;
        }

        protected override void OnRender(DrawingContext drawingContext)
        {
            base.OnRender(drawingContext);

            // Draw the 3D effect rectangles around the image
            for (int i = 1; i < 4; ++i)
            {
                Rect rect = new Rect(_ix - i, _iy - i, _iw + 2 * i, _ih + 2 * i);
                drawingContext.DrawRectangle(null, new Pen(Brushes.LightGray, 1), rect);
            }

            // Draw the viewpoint
            double dWidth = ActualWidth;
            double dHeight = ActualHeight;
            double currentX = dWidth;
            double currentBuf = 0;

            if (dHeight < dWidth)
            {
                currentX = dHeight;
                currentBuf = (dWidth - dHeight) / 2;
            }

            double centre = (currentX - 2) / 2.0;
            double px = centre * (1.0 + _r * Math.Cos(_theta) / FRAC);
            double py = centre * (1.0 - _r * Math.Sin(_theta) / FRAC);

            int spx = (int)Math.Round(px);
            int spy = (int)Math.Round(py);

            drawingContext.DrawEllipse(Brushes.Yellow, new Pen(Brushes.Yellow, 1), new Point(currentBuf + spx, spy), 2.5, 2.5);
            drawingContext.DrawEllipse(Brushes.DarkGray, new Pen(Brushes.DarkGray, 1), new Point(currentBuf + spx, spy), 1.5, 1.5);
        }

        private void E2LocatorPanel_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            Point clickPoint = e.GetPosition(this);

            if (clickPoint.X < _ix || clickPoint.X > _ix + _iw ||
                clickPoint.Y < _iy || clickPoint.Y > _iy + _ih)
            {
                return;
            }

            double dWidth = ActualWidth;
            double dHeight = ActualHeight;
            double x = dWidth;
            double buf = 0;

            if (x > dHeight)
            {
                x = dHeight;
                buf = (dWidth - dHeight) / 2;
            }

            double centre = (x - 2) / 2.0;
            double px = (clickPoint.X - buf) - centre;
            double py = centre - clickPoint.Y;
            double tr = Math.Sqrt(px * px + py * py) / centre;

            _r = tr * FRAC;
            _theta = Math.Atan2(py, px);

            InvalidateVisual(); // Redraw the control to show the updated viewpoint
        }

        // ISlideListener implementation (if needed, otherwise remove)
        public void slideEvent(Slider s)
        {
            // Not directly used here, but might be called if this control were a slider listener.
        }
    }
}
