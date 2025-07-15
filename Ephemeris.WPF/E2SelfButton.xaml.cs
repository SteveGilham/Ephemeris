using System.Windows.Controls;
using System.Windows;

namespace Ephemeris.WPF
{
    public partial class E2SelfButton : Button
    {
        private IE2TimeOfDay _core;
        private int _type;

        public E2SelfButton(string label, IE2TimeOfDay target, int type)
        {
            InitializeComponent();
            Content = label;
            _core = target;
            _type = type;
            Click += E2SelfButton_Click;
        }

        private void E2SelfButton_Click(object sender, RoutedEventArgs e)
        {
            _core.SetTimeOfDay(_type);
        }
    }
}