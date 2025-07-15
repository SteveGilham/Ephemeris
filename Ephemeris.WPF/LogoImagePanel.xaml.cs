using System.Windows.Controls;
using System.Windows.Media.Imaging;

namespace Ephemeris.WPF
{
    public partial class LogoImagePanel : UserControl
    {
        public LogoImagePanel()
        {
            InitializeComponent();
            LogoImage.Source = TinesLogo.GetBitmapSource();
        }
    }
}