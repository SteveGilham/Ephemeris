using System;
using System.Windows;
using System.Windows.Controls;

namespace Ephemeris.WPF
{
    public partial class E2WizBox : Window, ISlideListener
    {
        public E2WizBox()
        {
            InitializeComponent();
            InitializeSliders();
            UpdateLabels();
        }

        private void InitializeSliders()
        {
            ScaleBar.SlideEvent += OnSlideEvent;
            STilt.SlideEvent += OnSlideEvent;
            ETilt.SlideEvent += OnSlideEvent;
            WTilt.SlideEvent += OnSlideEvent;
            SDay.SlideEvent += OnSlideEvent;
            WDay.SlideEvent += OnSlideEvent;
            SharDay.SlideEvent += OnSlideEvent;
            SharHr.SlideEvent += OnSlideEvent;
            TwinDay.SlideEvent += OnSlideEvent;
            TwinHr.SlideEvent += OnSlideEvent;
            ArtiaDay.SlideEvent += OnSlideEvent;
            ArtiaHr.SlideEvent += OnSlideEvent;
        }

        public void OnSlideEvent(object sender, SlideEventArgs e)
        {
            UpdateLabels();
        }

        public void SlideEvent(Slider s)
        {
            OnSlideEvent(s, null);
        }

        private string GetTiltString()
        {
            double size = 3.6 / ScaleBar.Value;
            double t = Math.Asin(size) * 180.0 / Math.PI;
            double t2 = 10.6 * t / 9;
            return $"{t:F1}/{t2:F1} degree tilt";
        }

        private void UpdateLabels()
        {
            ScaleLabel.Content = ScaleBar.Value.ToString();
            TiltedLabel.Content = GetTiltString();
            E2Param.DomeRadius = ScaleBar.Value;

            STiltLabel.Content = $"Summer tilt {STilt.Value} deg";
            ETiltLabel.Content = $"Equinox tilt {ETilt.Value} deg";
            WTiltLabel.Content = $"Winter tilt {WTilt.Value} deg";

            if (VariableControl.IsChecked == true)
            {
                E2Param.SummerTilt = STilt.Value;
                E2Param.WinterTilt = WTilt.Value;
                E2Param.EquinoxTilt = ETilt.Value;
            }

            double sDayFrac = SDay.Value * 0.01;
            if (VariableControl.IsChecked == true) E2Param.SummerDay = sDayFrac;
            SDayLabel.Content = $"Summer day length {(sDayFrac * 24 + 12):F1}h";

            double wDayFrac = WDay.Value * 0.01;
            if (VariableControl.IsChecked == true) E2Param.WinterDay = wDayFrac;
            WDayLabel.Content = $"Winter day length {(wDayFrac * 24 + 12):F1}h";

            E2Param.ShargashRise = SharDay.Value + SharHr.Value / 144.0;
            SharDayLabel.Content = $"Shargash rises day {SharDay.Value} year 1";
            int sharHrVal = SharHr.Value / 6;
            int sharMinVal = (SharHr.Value % 6) * 10;
            SharHrLabel.Content = $"at {sharHrVal}h {sharMinVal}m";

            E2Param.TwinRise = TwinDay.Value + TwinHr.Value / 144.0;
            TwinDayLabel.Content = $"Twinstar rises day {TwinDay.Value} year 1";
            int twinHrVal = TwinHr.Value / 6;
            int twinMinVal = (TwinHr.Value % 6) * 10;
            TwinHrLabel.Content = $"at {twinHrVal}h {twinMinVal}m";

            E2Param.ArtiaRise = ArtiaDay.Value + ArtiaHr.Value / 144.0;
            ArtiaDayLabel.Content = $"Artia rises day {ArtiaDay.Value} year 1";
            int artiaHrVal = ArtiaHr.Value / 6;
            int artiaMinVal = (ArtiaHr.Value % 6) * 10;
            ArtiaHrLabel.Content = $"at {artiaHrVal}h {artiaMinVal}m";
        }

        private void NineDegreeTilt_Checked(object sender, RoutedEventArgs e)
        {
            E2Param.Yuthu = YuthuOverhead.IsChecked == true;
            if (NineDegreeTilt.IsChecked == true)
            {
                E2Param.SummerTilt = 9.0;
                E2Param.WinterTilt = -10.6;
                E2Param.EquinoxTilt = 0;
                E2Param.SummerDay = 0.1;
                E2Param.WinterDay = -1.06 / 9.0;
            }
            UpdateLabels();
        }

        private void YuthuOverhead_Checked(object sender, RoutedEventArgs e)
        {
            E2Param.Yuthu = YuthuOverhead.IsChecked == true;
            UpdateLabels();
        }

        private void VariableControl_Checked(object sender, RoutedEventArgs e)
        {
            E2Param.Yuthu = YuthuOverhead.IsChecked == true;
            if (VariableControl.IsChecked == true)
            {
                E2Param.SummerTilt = STilt.Value;
                E2Param.WinterTilt = WTilt.Value;
                E2Param.EquinoxTilt = ETilt.Value;
                E2Param.SummerDay = SDay.Value * 0.01;
                E2Param.WinterDay = WDay.Value * 0.01;
            }
            UpdateLabels();
        }

        private void UleriaPeriod_Checked(object sender, RoutedEventArgs e)
        {
            E2Param.Uleria = true;
        }

        private void UleriaPeriod_Unchecked(object sender, RoutedEventArgs e)
        {
            E2Param.Uleria = false;
        }
    }
}