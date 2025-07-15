using System;
using System.Windows;
using System.Windows.Controls;

namespace Ephemeris.WPF
{
    public partial class E2TimePicker : Window, ISlideListener
    {
        private double _delta;

        public E2TimePicker(Window owner, double lastDelta)
        {
            InitializeComponent();
            Owner = owner;
            _delta = lastDelta;

            ScaleBar.SlideEvent += OnSlideEvent;

            // Set initial checked state based on lastDelta
            if (lastDelta < 1.0 / 24.0)
            {
                CbMin.IsChecked = true;
            }
            else if (lastDelta < 1)
            {
                CbHr.IsChecked = true;
            }
            else if (lastDelta < 294)
            {
                CbDay.IsChecked = true;
            }
            else
            {
                CbYr.IsChecked = true;
            }

            // Manually trigger the checked event for the initially selected radio button
            // to set the correct scalebar values and label.
            if (CbMin.IsChecked == true) TimeUnit_Checked(CbMin, null);
            else if (CbHr.IsChecked == true) TimeUnit_Checked(CbHr, null);
            else if (CbDay.IsChecked == true) TimeUnit_Checked(CbDay, null);
            else if (CbYr.IsChecked == true) TimeUnit_Checked(CbYr, null);
        }

        public double GetDelta() { return _delta; }

        private void Ok_Click(object sender, RoutedEventArgs e)
        {
            DialogResult = true;
        }

        public void OnSlideEvent(object sender, SlideEventArgs e)
        {
            UpdateDeltaAndLabel();
        }

        private void TimeUnit_Checked(object sender, RoutedEventArgs e)
        {
            RadioButton rb = sender as RadioButton;
            if (rb == null || rb.IsChecked != true) return;

            int val;
            if (rb == CbMin)
            {
                val = (int)Math.Round(1440 * _delta);
                ScaleBar.SetValues(Math.Min(val, 60), 5, 0, 60);
            }
            else if (rb == CbHr)
            {
                val = (int)Math.Round(24 * _delta);
                ScaleBar.SetValues(Math.Min(val, 24), 5, 0, 24);
            }
            else if (rb == CbDay)
            {
                val = (int)Math.Round(_delta);
                ScaleBar.SetValues(Math.Min(val, 294), 5, 0, 294);
            }
            else // CbYr
            {
                val = (int)Math.Round(_delta / 29.4);
                ScaleBar.SetValues(Math.Min(val, 100), 5, 0, 100);
            }
            UpdateDeltaAndLabel();
        }

        private void UpdateDeltaAndLabel()
        {
            if (CbMin.IsChecked == true)
            {
                _delta = ScaleBar.Value / 1440.0;
                ScaleLabel.Content = ScaleBar.Value.ToString();
            }
            else if (CbHr.IsChecked == true)
            {
                _delta = ScaleBar.Value / 24.0;
                ScaleLabel.Content = ScaleBar.Value.ToString();
            }
            else if (CbDay.IsChecked == true)
            {
                _delta = ScaleBar.Value;
                ScaleLabel.Content = ScaleBar.Value.ToString();
            }
            else // CbYr
            {
                _delta = ScaleBar.Value * 29.4;
                ScaleLabel.Content = (ScaleBar.Value / 10.0).ToString("F1");
            }
        }

        public void SlideEvent(Slider s)
        {
            OnSlideEvent(s, null);
        }
    }
}
