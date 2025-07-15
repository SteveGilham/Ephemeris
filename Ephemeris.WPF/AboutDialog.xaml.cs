using System.Windows;

namespace Ephemeris.WPF
{
    public partial class AboutDialog : Window
    {
        public AboutDialog()
        {
            InitializeComponent();
            Title = StringTable.LICTEXT;
            LicenseText.Text = Licence.Text;
        }

        private void Close_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }
    }
}
