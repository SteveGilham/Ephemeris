using System;
using System.Windows;

namespace Ephemeris.WPF
{
    public class E2RandomFaintStar : Random
    {
        public E2RandomFaintStar(int i) : base(i) { }

        public new Point Next()
        {
            double r = 300.0 * Math.Sqrt(NextDouble());
            double theta = 2.0 * Math.PI * NextDouble();
            return new Point(
                (int)Math.Round(r * Math.Cos(theta)),
                (int)Math.Round(r * Math.Sin(theta)));
        }
    }
}