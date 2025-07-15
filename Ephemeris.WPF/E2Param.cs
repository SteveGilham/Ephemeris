using System;

public static class E2Param
{
    public static int DomeRadius = 20;   // e6 meters
    public static bool Yuthu = false;  // sun overhead in yuthuppa midsummer
    public static double WinterTilt = -10.6; //deg
    public static double EquinoxTilt = 0;
    public static double SummerTilt = 9.0;

    public static double SummerDay = 0.1;       // deviation from 0.5 days
    public static double WinterDay = -1.06 / 9.0;

    public static bool Uleria = false; // sidereal period

    public static double ShargashRise = 0.0;
    public static double TwinRise = 0.0;
    public static double ArtiaRise = 0.0;

    public static double GetHarmonic(int week, int day, double hour)
    {
        double dday = (7.0 * week + day);
        dday += hour; // days since midnight on 1st day of year

        double cycle, harmonic;
        if (dday < 0.5) dday += 294;
        if (dday < 135.5)
        {
            cycle = (dday - 0.5) * Math.PI / 135.0;
            harmonic = Math.Sin(cycle);
        }
        else
        {
            cycle = (dday - 135.5) * Math.PI / 159.0;
            harmonic = -Math.Sin(cycle);
        }
        return harmonic;
    }

    public static double GetTilt(double harmonic) // deg
    {
        if (!Yuthu)
        {
            if (harmonic >= 0) return EquinoxTilt + (SummerTilt - EquinoxTilt) * harmonic;
            else return EquinoxTilt - (WinterTilt - EquinoxTilt) * harmonic;
        }
        else
        {
            double plus = Math.Asin(3.6 / DomeRadius) * 180.0 / Math.PI;
            if (harmonic >= 0) return plus * harmonic;
            else return plus * harmonic * 10.6 / 9;
        }
    }

    public static double GetDayLength(double harmonic) // days
    {
        if (harmonic >= 0) return 0.5 + SummerDay * harmonic;
        else return 0.5 - WinterDay * harmonic;
    }
}
