using System.Windows;
using System.Windows.Media;

namespace Ephemeris.WPF
{
    public interface IAnimated
    {
        void Paint(DrawingContext g);
        void SetSize(Size d);
    }
}