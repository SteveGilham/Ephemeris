using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Collections.Generic;

namespace Ephemeris.WPF
{
    public class Slider : Control
    {
        public static readonly DependencyProperty ValueProperty =
            DependencyProperty.Register("Value", typeof(int), typeof(Slider), new PropertyMetadata(0, OnValueChanged));

        public static readonly DependencyProperty MinimumProperty =
            DependencyProperty.Register("Minimum", typeof(int), typeof(Slider), new PropertyMetadata(0));

        public static readonly DependencyProperty MaximumProperty =
            DependencyProperty.Register("Maximum", typeof(int), typeof(Slider), new PropertyMetadata(100));

        public static readonly DependencyProperty VisibleRangeProperty =
            DependencyProperty.Register("VisibleRange", typeof(int), typeof(Slider), new PropertyMetadata(5));

        public int Value
        {
            get { return (int)GetValue(ValueProperty); }
            set { SetValue(ValueProperty, value); }
        }

        public int Minimum
        {
            get { return (int)GetValue(MinimumProperty); }
            set { SetValue(MinimumProperty, value); }
        }

        public int Maximum
        {
            get { return (int)GetValue(MaximumProperty); }
            set { SetValue(MaximumProperty, value); }
        }

        public int VisibleRange
        {
            get { return (int)GetValue(VisibleRangeProperty); }
            set { SetValue(VisibleRangeProperty, value); }
        }

        private bool _isDragging = false;
        private Point _lastMousePosition;

        public event EventHandler<SlideEventArgs> SlideEvent;

        public Slider()
        {
            Focusable = true;
            MouseLeftButtonDown += Slider_MouseLeftButtonDown;
            MouseMove += Slider_MouseMove;
            MouseLeftButtonUp += Slider_MouseLeftButtonUp;
        }

        private static void OnValueChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
        {
            Slider slider = (Slider)d;
            slider.InvalidateVisual(); // Redraw when value changes
            slider.SlideEvent?.Invoke(slider, new SlideEventArgs(slider));
        }

        public void SetValues(int value, int visible, int minimum, int maximum)
        {
            Value = value;
            VisibleRange = visible;
            Minimum = minimum;
            Maximum = maximum;
        }

        protected override void OnRender(DrawingContext drawingContext)
        {
            base.OnRender(drawingContext);

            // Background
            Rect backgroundRect = new Rect(0, 0, ActualWidth, ActualHeight);
            drawingContext.DrawRectangle(Background, null, backgroundRect);

            // Draw the track
            double tlx = 10;
            double tly = 5;
            double brx = ActualWidth - 10;
            double bry = 10;

            Pen darkPen = new Pen(new SolidColorBrush(Colors.DarkGray), 1);
            Pen brightPen = new Pen(new SolidColorBrush(Colors.LightGray), 1);

            // Track outline
            drawingContext.DrawLine(darkPen, new Point(tlx, tly), new Point(brx, tly));
            drawingContext.DrawLine(darkPen, new Point(tlx, tly + 1), new Point(brx - 1, tly + 1));
            drawingContext.DrawLine(darkPen, new Point(tlx, tly), new Point(tlx, bry));
            drawingContext.DrawLine(darkPen, new Point(tlx + 1, tly + 1), new Point(tlx + 1, bry - 1));

            drawingContext.DrawLine(brightPen, new Point(tlx, bry), new Point(brx, bry));
            drawingContext.DrawLine(brightPen, new Point(tlx + 1, bry - 1), new Point(brx, bry - 1));
            drawingContext.DrawLine(brightPen, new Point(brx, tly), new Point(brx, bry));
            drawingContext.DrawLine(brightPen, new Point(brx - 1, tly + 1), new Point(brx - 1, bry - 1));

            // Draw the thumb
            double perPixel = (double)(Maximum - Minimum);
            if (ActualWidth - 20 > 0)
            {
                perPixel /= (ActualWidth - 20);
            }
            else
            {
                perPixel = 1; // Avoid division by zero
            }

            double offset = (Value - Minimum) / perPixel;
            double thumbWidth = VisibleRange / perPixel;

            double thumbTlx = 10 + offset;
            double thumbBrx = thumbTlx + thumbWidth;
            double thumbTly = 2;
            double thumbBry = 13;

            // Ensure thumb stays within bounds
            if (thumbTlx < 10) thumbTlx = 10;
            if (thumbBrx > ActualWidth - 10) thumbBrx = ActualWidth - 10;

            Rect thumbRect = new Rect(thumbTlx, thumbTly, thumbBrx - thumbTlx, thumbBry - thumbTly);
            drawingContext.DrawRectangle(new SolidColorBrush(Colors.LightGray), null, thumbRect);

            // Thumb outline
            drawingContext.DrawLine(brightPen, new Point(thumbTlx, thumbTly), new Point(thumbBrx, thumbTly));
            drawingContext.DrawLine(brightPen, new Point(thumbTlx, thumbTly + 1), new Point(thumbBrx - 1, thumbTly + 1));
            drawingContext.DrawLine(brightPen, new Point(thumbTlx, thumbTly), new Point(thumbTlx, thumbBry));
            drawingContext.DrawLine(brightPen, new Point(thumbTlx + 1, thumbTly + 1), new Point(thumbTlx + 1, thumbBry - 1));

            drawingContext.DrawLine(darkPen, new Point(thumbTlx, thumbBry), new Point(thumbBrx, thumbBry));
            drawingContext.DrawLine(darkPen, new Point(thumbTlx + 1, thumbBry - 1), new Point(thumbBrx, thumbBry - 1));
            drawingContext.DrawLine(darkPen, new Point(thumbBrx, thumbTly), new Point(thumbBrx, thumbBry));
            drawingContext.DrawLine(darkPen, new Point(thumbBrx - 1, thumbTly + 1), new Point(thumbBrx - 1, thumbBry - 1));
        }

        private void Slider_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            _lastMousePosition = e.GetPosition(this);
            _isDragging = true;
            CaptureMouse();
            e.Handled = true;
        }

        private void Slider_MouseMove(object sender, MouseEventArgs e)
        {
            if (_isDragging)
            {
                Point currentMousePosition = e.GetPosition(this);
                double deltaX = currentMousePosition.X - _lastMousePosition.X;

                double perPixel = (double)(Maximum - Minimum);
                if (ActualWidth - 20 > 0)
                {
                    perPixel /= (ActualWidth - 20);
                }
                else
                {
                    perPixel = 1;
                }

                int newValue = Value + (int)(deltaX * perPixel);
                Value = Math.Max(Minimum, Math.Min(Maximum, newValue));
                _lastMousePosition = currentMousePosition;
                e.Handled = true;
            }
        }

        private void Slider_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            if (_isDragging)
            {
                _isDragging = false;
                ReleaseMouseCapture();
                e.Handled = true;
            }
        }
    }

    public class SlideEventArgs : EventArgs
    {
        public Slider Slider { get; }

        public SlideEventArgs(Slider slider)
        {
            Slider = slider;
        }
    }
}
