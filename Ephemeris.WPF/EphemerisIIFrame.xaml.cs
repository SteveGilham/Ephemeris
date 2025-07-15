using System.Windows;
using System.Windows.Controls;

namespace Ephemeris.WPF
{
    public partial class EphemerisIIFrame : Window
    {
        private EphemerisIIAnimation _animation;
        private E2WizBox _wizBox;

        public EphemerisIIFrame()
        {
            InitializeComponent();
            _wizBox = new E2WizBox();
            
            // Initialize and associate animation
            _animation = new EphemerisIIAnimation();
            _animation.Associate(this);
            AnimationContentControl.Content = _animation;

            // Set up event handlers for menu items
            Loaded += EphemerisIIFrame_Loaded;

            // Associate ControlPanel with Animation and pass CalendarTextBox
            ControlPanel.CalendarTextBox = CalendarTextBox;
            _animation.Associate(ControlPanel);
        }

        private void EphemerisIIFrame_Loaded(object sender, RoutedEventArgs e)
        {
            // This is where you might associate the control panel with the animation
            // once the control panel is also translated and available.
            // For now, it's a placeholder.
            // _animation.Associate(_controlPanel);
        }

        public void Associate(EphemerisIIAnimation a)
        {
            _animation = a;
            _animation.Associate(this);
        }

        private void Exit_Click(object sender, RoutedEventArgs e)
        {
            Application.Current.Shutdown();
        }

        private void About_Click(object sender, RoutedEventArgs e)
        {
            AboutDialog aboutDialog = new AboutDialog();
            aboutDialog.Owner = this;
            aboutDialog.ShowDialog();
        }

        private void WizardMenuItem_Checked(object sender, RoutedEventArgs e)
        {
            _wizBox.Show();
        }

        private void WizardMenuItem_Unchecked(object sender, RoutedEventArgs e)
        {
            _wizBox.Hide();
        }

        // Public properties to expose CheckBoxMenuItem states
        public MenuItem CbSunPathMenuItem => CbSunPath;
        public MenuItem CbSouthPathMenuItem => CbSouthPath;
        public MenuItem CbNamesMenuItem => CbNames;
        public MenuItem CbFrameMenuItem => CbFrame;
        public MenuItem CbLightMenuItem => CbLight;
        public MenuItem CbObscureMenuItem => CbObscure;
    }

    
}
