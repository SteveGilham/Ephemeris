using System.Collections.Generic;
using System.Windows.Media;

namespace Ephemeris.WPF
{
    public class StarDome
    {
        private List<DomeStar> _host = new List<DomeStar>();

        public const double Radian = 1618.614;
        private double _skew = 0.04445887;

        public void Draw(DrawingContext g, double angle, double slide, int radius,
            int x0, int y0, bool names,
            double radialOffset, double bearing,
            bool up, double look)
        {
            foreach (var star in _host)
            {
                star.Draw(g, _skew - angle, slide, radius,
                    x0, y0, names, radialOffset, bearing,
                    up, look);
            }
        }

        public string Locate(int ex, int ey, int rad, int cen,
            double angle, double slide, double radialOffset,
            double bearing, bool up, double look)
        {
            double r = 2500;
            int index = _host.Count;
            for (int i = 0; i < _host.Count; ++i)
            {
                double r1 = _host[i].Locate(ex, ey, rad, cen,
                    _skew - angle, slide,
                    radialOffset, bearing, up, look);
                if (r1 < r)
                {
                    index = i;
                    r = r1;
                }
            }
            if (index == _host.Count) return "*";
            return $"{index} {_host[index].Id()}";
        }

        public StarDome()
        {
            _host.Add(new DomeStar("Pole Star", 1, 1, 50, "Pole Star"));
            _host.Add(new DomeStar("Arraz", -425, 185, 35, "Arraz"));
            _host.Add(new DomeStar("Ourania", 355, -325, 20, ""));
            _host.Add(new DomeStar("Evandal", 446, -1285, 20, "Oropum"));
            _host.Add(new DomeStar("Everina", 655, -760, 20, "Rice"));
            _host.Add(new DomeStar("Conspirator", 1451, -969, 20, "Whisperers"));
            _host.Add(new DomeStar("Maw", 910, -70, 20, ""));
            _host.Add(new DomeStar("Eye", 1120, -189, 20, ""));
            _host.Add(new DomeStar("Neck", 895, 96, 20, ""));
            _host.Add(new DomeStar("Chest", 805, 305, 20, "Star Dragon"));
            _host.Add(new DomeStar("Wing", 835, 576, 20, ""));
            _host.Add(new DomeStar("Belly", 565, 665, 20, ""));
            _host.Add(new DomeStar("Tail", 459, 935, 20, ""));
            _host.Add(new DomeStar("Stinger", -6, 1070, 20, ""));
            _host.Add(new DomeStar("Tail", 219, 1085, 20, ""));
            _host.Add(new DomeStar("Seed", 129, 1565, 20, "Tree", Colors.Green));
            _host.Add(new DomeStar("Erkonus", -1445, 544, 20, "Erkonus"));
            _host.Add(new DomeStar("Dove", -1355, -611, 20, "Dove"));
            _host.Add(new DomeStar("Harp", -574, -850, 20, "Harp"));
            _host.Add(new DomeStar("Steward", -185, -850, 20, "Steward"));
            _host.Add(new DomeStar("(Tongue)", -49, -1600, 5, ""));
            _host.Add(new DomeStar("Eye", 71, -2005, 20, "Lorion"));
            _host.Add(new DomeStar("Tail", 0, -2500, 20, ""));
            _host.Add(new DomeStar("Leg", 866, -2185, 20, "Thunderer"));
            _host.Add(new DomeStar("Leg", 761, -2350, 20, ""));
            _host.Add(new DomeStar("Arm", 926, -2380, 20, ""));
            _host.Add(new DomeStar("Arm", 1166, -2154, 20, ""));
            _host.Add(new DomeStar("Varnaga", 1241, -1614, 20, "Crocodile"));
            _host.Add(new DomeStar("Vergenari", 1961, -1479, 20, "Sow"));
            _host.Add(new DomeStar("Bull", 2140, -819, 20, "Bull"));
            _host.Add(new DomeStar("Oasis", 2140, 51, 20, "Oasis"));
            _host.Add(new DomeStar("Thasus", 1780, 666, 20, "Thasus"));
            _host.Add(new DomeStar("Pincer", 1359, 1506, 20, ""));
            _host.Add(new DomeStar("Womb", 1269, 1716, 20, "Scorpion"));
            _host.Add(new DomeStar("Jewel Flower", 474, 1910, 20, "Flowers"));
            _host.Add(new DomeStar("Youth", 113, 2540, 20, "Youth"));
            _host.Add(new DomeStar("(Shafesora)", -1911, 1354, 5, "Marsh"));
            _host.Add(new DomeStar("Lion", -2285, 544, 20, "Lion"));
            _host.Add(new DomeStar("Swan", -1955, -206, 20, "Swan"));
            _host.Add(new DomeStar("Bow", -1459, -1331, 20, ""));
            _host.Add(new DomeStar("Heart", -1339, -1571, 20, "Hunter"));
            _host.Add(new DomeStar("Hip", -1459, -1661, 20, ""));
            _host.Add(new DomeStar("Knee", -1609, -1601, 20, ""));
            _host.Add(new DomeStar("Pot", -814, -1795, 20, "Pot"));
            _host.Add(new DomeStar("Fan", -1024, -2336, 20, "Fan"));
            _host.Add(new DomeStar("Eye", -94, -1930, 20, ""));
            _host.Add(new DomeStar("Marker", -94, -2290, 20, ""));
            //_host.Add(new DomeStar("Kalikos", -378, -2785, 20, "Kalikos", Colors.Red));
            _host.Add(new DomeStar("Chorus", -260, 410, 10, "Chorus"));
            _host.Add(new DomeStar("", -380, 320, 10, ""));
            _host.Add(new DomeStar("", -470, 290, 10, ""));
            _host.Add(new DomeStar("", -545, 65, 10, ""));
            _host.Add(new DomeStar("", -440, 95, 10, ""));
            _host.Add(new DomeStar("", -380, -10, 10, ""));
            _host.Add(new DomeStar("", -485, -40, 10, ""));
            _host.Add(new DomeStar("Cook", -110, -265, 10, "Cook"));
            _host.Add(new DomeStar("", -80, -340, 10, ""));
            _host.Add(new DomeStar("", -155, -340, 10, ""));
            _host.Add(new DomeStar("", -170, -400, 10, ""));
            _host.Add(new DomeStar("", -245, -415, 10, ""));
            _host.Add(new DomeStar("", -140, -445, 10, ""));
            _host.Add(new DomeStar("", 430, -400, 10, ""));
            _host.Add(new DomeStar("", 280, -400, 10, ""));
            _host.Add(new DomeStar("", 190, -340, 10, ""));
            _host.Add(new DomeStar("Officers", 190, 110, 10, "Officers"));
            _host.Add(new DomeStar("", 160, 245, 10, ""));
            _host.Add(new DomeStar("", 130, 440, 10, ""));
            _host.Add(new DomeStar("", 325, 50, 10, ""));
            _host.Add(new DomeStar("", 401, -1015, 10, ""));
            _host.Add(new DomeStar("", 596, -1450, 10, ""));
            _host.Add(new DomeStar("", 701, -1435, 10, ""));
            _host.Add(new DomeStar("", 446, -1540, 10, ""));
            _host.Add(new DomeStar("", 386, -1345, 10, ""));
            _host.Add(new DomeStar("", 566, -1150, 10, ""));
            _host.Add(new DomeStar("", 535, -775, 10, ""));
            _host.Add(new DomeStar("", 805, -700, 10, ""));
            _host.Add(new DomeStar("", 715, -520, 10, ""));
            _host.Add(new DomeStar("", 610, -535, 10, ""));
            _host.Add(new DomeStar("", 565, -475, 10, ""));
            _host.Add(new DomeStar("", 475, -535, 10, ""));
            _host.Add(new DomeStar("", 445, -685, 10, ""));
            _host.Add(new DomeStar("", 385, -595, 10, ""));
            _host.Add(new DomeStar("", 295, -535, 10, ""));
            _host.Add(new DomeStar("", 1180, -909, 10, ""));
            _host.Add(new DomeStar("", 1301, -999, 10, ""));
            _host.Add(new DomeStar("", 1225, -744, 10, ""));
            _host.Add(new DomeStar("", 1330, -849, 10, ""));
            _host.Add(new DomeStar("", 1600, -969, 10, ""));
            _host.Add(new DomeStar("", 865, 186, 10, ""));
            _host.Add(new DomeStar("", 820, 440, 10, ""));
            _host.Add(new DomeStar("", 970, 621, 10, ""));
            _host.Add(new DomeStar("", 970, 726, 10, ""));
            _host.Add(new DomeStar("", 939, 861, 10, ""));
            _host.Add(new DomeStar("", 774, 1055, 10, ""));
            _host.Add(new DomeStar("", 1030, 726, 10, ""));
            _host.Add(new DomeStar("", 564, 830, 10, ""));
            _host.Add(new DomeStar("", 460, 800, 10, ""));
            _host.Add(new DomeStar("", 159, 995, 10, ""));
            _host.Add(new DomeStar("", -81, 1655, 10, ""));
            _host.Add(new DomeStar("", 54, 1715, 10, ""));
            _host.Add(new DomeStar("", 204, 1790, 10, ""));
            _host.Add(new DomeStar("", 264, 1655, 10, ""));
            _host.Add(new DomeStar("", 114, 1925, 10, ""));
            _host.Add(new DomeStar("", 159, 2015, 10, ""));
            _host.Add(new DomeStar("Yoke", -590, 905, 10, "Yoke"));
            _host.Add(new DomeStar("", -546, 1010, 10, ""));
            _host.Add(new DomeStar("", -486, 995, 10, ""));
            _host.Add(new DomeStar("", -545, 845, 10, ""));
            _host.Add(new DomeStar("", -500, 860, 10, ""));
            _host.Add(new DomeStar("", -1370, 469, 10, ""));
            _host.Add(new DomeStar("", -1490, 394, 10, ""));
            _host.Add(new DomeStar("Hawk", -860, -101, 10, "Hawk"));
            _host.Add(new DomeStar("", -830, -146, 10, ""));
            _host.Add(new DomeStar("", -800, -220, 10, ""));
            _host.Add(new DomeStar("", -920, -146, 10, ""));
            _host.Add(new DomeStar("", -890, -221, 10, ""));
            _host.Add(new DomeStar("", -845, -280, 10, ""));
            _host.Add(new DomeStar("", -1355, -356, 10, ""));
            _host.Add(new DomeStar("", -1475, -716, 10, ""));
            _host.Add(new DomeStar("", -1310, -701, 10, ""));
            _host.Add(new DomeStar("Quail", -1399, -896, 10, "Quail"));
            _host.Add(new DomeStar("", -1294, -986, 10, ""));
            _host.Add(new DomeStar("", -1414, -1001, 10, ""));
            _host.Add(new DomeStar("", -1309, -1076, 10, ""));
            _host.Add(new DomeStar("", -709, -895, 10, ""));
            _host.Add(new DomeStar("", -649, -955, 10, ""));
            _host.Add(new DomeStar("", -334, -970, 10, ""));
            _host.Add(new DomeStar("", -289, -955, 10, ""));
            _host.Add(new DomeStar("", -214, -940, 10, ""));
            _host.Add(new DomeStar("", 56, -1390, 10, ""));
            _host.Add(new DomeStar("", 251, -1345, 10, ""));
            _host.Add(new DomeStar("", 41, -1225, 10, ""));
            _host.Add(new DomeStar("", 10, -1000, 10, ""));
            _host.Add(new DomeStar("", 145, -1000, 10, ""));
            _host.Add(new DomeStar("Fishes", 26, -1480, 10, "Fishes"));
            _host.Add(new DomeStar("", 101, -1600, 10, ""));
            _host.Add(new DomeStar("", 56, -1750, 10, ""));
            _host.Add(new DomeStar("", -64, -1735, 10, ""));
            _host.Add(new DomeStar("", 131, -2170, 10, ""));
            _host.Add(new DomeStar("", -109, -2155, 10, ""));
            _host.Add(new DomeStar("", 851, -2305, 10, ""));
            _host.Add(new DomeStar("", 956, -2259, 10, ""));
            _host.Add(new DomeStar("", 1046, -2214, 10, ""));
            _host.Add(new DomeStar("", 1316, -1689, 10, ""));
            _host.Add(new DomeStar("", 1286, -1539, 10, ""));
            _host.Add(new DomeStar("", 1346, -1479, 10, ""));
            _host.Add(new DomeStar("", 1091, -1584, 10, ""));
            _host.Add(new DomeStar("", 1181, -1389, 10, ""));
            _host.Add(new DomeStar("", 1946, -1659, 10, ""));
            _host.Add(new DomeStar("", 2126, -1374, 10, ""));
            _host.Add(new DomeStar("", 2155, -939, 10, ""));
            _host.Add(new DomeStar("", 2156, -1029, 10, ""));
            _host.Add(new DomeStar("", 2215, -714, 10, ""));
            _host.Add(new DomeStar("", 2260, -804, 10, ""));
            _host.Add(new DomeStar("", 1990, 531, 10, ""));
            _host.Add(new DomeStar("", 1554, 846, 10, ""));
            _host.Add(new DomeStar("", 1659, 1056, 10, ""));
            _host.Add(new DomeStar("", 1389, 1821, 10, ""));
            _host.Add(new DomeStar("", 1334, 2048, 10, ""));
            _host.Add(new DomeStar("", 1134, 2091, 10, ""));
            _host.Add(new DomeStar("", 1149, 1656, 10, ""));
            _host.Add(new DomeStar("", 999, 1716, 10, ""));
            _host.Add(new DomeStar("", 909, 1896, 10, ""));
            _host.Add(new DomeStar("", 609, 1970, 10, ""));
            _host.Add(new DomeStar("", 489, 2135, 10, ""));
            _host.Add(new DomeStar("", 98, 2675, 10, ""));
            _host.Add(new DomeStar("", 23, 2435, 10, ""));
            _host.Add(new DomeStar("", -51, 2345, 10, ""));
            _host.Add(new DomeStar("", 84, 2390, 10, ""));
            _host.Add(new DomeStar("", 189, 2465, 10, ""));
            _host.Add(new DomeStar("", 248, 2375, 10, ""));
            _host.Add(new DomeStar("", -456, 2045, 10, ""));
            _host.Add(new DomeStar("", -366, 1940, 10, ""));
            _host.Add(new DomeStar("Willows", -351, 1745, 10, "Willows"));
            _host.Add(new DomeStar("", -456, 1655, 10, ""));
            _host.Add(new DomeStar("", -276, 1670, 10, ""));
            _host.Add(new DomeStar("", -366, 1580, 10, ""));
            _host.Add(new DomeStar("Plough", -1101, 1864, 10, "Plough"));
            _host.Add(new DomeStar("", -1116, 1759, 10, ""));
            _host.Add(new DomeStar("", -1056, 1759, 10, ""));
            _host.Add(new DomeStar("", -981, 1819, 10, ""));
            _host.Add(new DomeStar("", -996, 1759, 10, ""));
            _host.Add(new DomeStar("", -1071, 1699, 10, ""));
            _host.Add(new DomeStar("Veridna", -1656, 2384, 10, "Veridna"));
            _host.Add(new DomeStar("", -1461, 2474, 10, ""));
            _host.Add(new DomeStar("", -1611, 2504, 10, ""));
            _host.Add(new DomeStar("", -1716, 2474, 10, ""));
            _host.Add(new DomeStar("", -1701, 2594, 10, ""));
            _host.Add(new DomeStar("", -1686, 2699, 10, ""));
            _host.Add(new DomeStar("", -2495, 574, 10, ""));
            _host.Add(new DomeStar("", -2390, 424, 10, ""));
            _host.Add(new DomeStar("", -2225, 454, 10, ""));
            _host.Add(new DomeStar("", -2165, 394, 10, ""));
            _host.Add(new DomeStar("", -2210, -71, 10, ""));
            _host.Add(new DomeStar("", -2120, -116, 10, ""));
            _host.Add(new DomeStar("", -2000, -41, 10, ""));
            _host.Add(new DomeStar("", -1880, -161, 10, ""));
            _host.Add(new DomeStar("", -1865, -206, 10, ""));
            _host.Add(new DomeStar("", -2030, -341, 10, ""));
            _host.Add(new DomeStar("Hag", -2615, -417, 10, "Hag"));
            _host.Add(new DomeStar("", -2705, -462, 10, ""));
            _host.Add(new DomeStar("", -2885, -447, 10, ""));
            _host.Add(new DomeStar("", -2825, -627, 10, ""));
            _host.Add(new DomeStar("", -2570, -762, 10, ""));
            _host.Add(new DomeStar("", -2614, -852, 10, ""));
            _host.Add(new DomeStar("", -2540, -822, 10, ""));
            _host.Add(new DomeStar("", -2464, -777, 10, ""));
            _host.Add(new DomeStar("Borna", -2674, -927, 10, "Borna"));
            _host.Add(new DomeStar("", -2404, -852, 10, ""));
            _host.Add(new DomeStar("", -2254, -1016, 10, ""));
            _host.Add(new DomeStar("", -2224, -1121, 10, ""));
            _host.Add(new DomeStar("Deer", -2254, -1211, 10, "Deer"));
            _host.Add(new DomeStar("", -2119, -1256, 10, ""));
            _host.Add(new DomeStar("", -2119, -1316, 10, ""));
            _host.Add(new DomeStar("Firestick", -2614, -1362, 10, "Firestick"));
            _host.Add(new DomeStar("", -2614, -1437, 10, ""));
            _host.Add(new DomeStar("", -2599, -1512, 10, ""));
            _host.Add(new DomeStar("", -2584, -1572, 10, ""));
            _host.Add(new DomeStar("", -2599, -1617, 10, ""));
            _host.Add(new DomeStar("", -2494, -1602, 10, ""));
            _host.Add(new DomeStar("", -1399, -1481, 10, ""));
            _host.Add(new DomeStar("", -1594, -1391, 10, ""));
            _host.Add(new DomeStar("", -1684, -1436, 10, ""));
            _host.Add(new DomeStar("", -1774, -1511, 10, ""));
            _host.Add(new DomeStar("", -2029, -1676, 10, ""));
            _host.Add(new DomeStar("Groundhog", -2029, -1766, 10, "Groundhog"));
            _host.Add(new DomeStar("", -1909, -2051, 10, ""));
            _host.Add(new DomeStar("", -1774, -2036, 10, ""));
            _host.Add(new DomeStar("Rabbits", -1744, -2126, 10, "Rabbits"));
            _host.Add(new DomeStar("", -1744, -2216, 10, ""));
            _host.Add(new DomeStar("Raven", -589, -2125, 10, "Raven"));
            _host.Add(new DomeStar("", -574, -2215, 10, ""));
            _host.Add(new DomeStar("", -649, -2230, 10, ""));
            _host.Add(new DomeStar("", -679, -2305, 10, ""));
            _host.Add(new DomeStar("", -529, -2260, 10, ""));
            _host.Add(new DomeStar("", -603, -2320, 10, ""));
            _host.Add(new DomeStar("Love Stars", -1835, 1647, -1, ""));
            _host.Add(new DomeStar("Love Stars", -1881, 1459, -1, ""));
            _host.Add(new DomeStar("Love Stars", -1967, 1552, -1, ""));
            _host.Add(new DomeStar("Love Stars", -2000, 1678, -1, ""));
            _host.Add(new DomeStar("Love Stars", -2013, 1859, -1, ""));
            _host.Add(new DomeStar("Love Stars", -2166, 1714, -1, ""));
            _host.Add(new DomeStar("Love Stars", -2222, 1981, -1, ""));
            _host.Add(new DomeStar("Love Stars", -2239, 1771, -1, ""));
            _host.Add(new DomeStar("Love Stars", -2403, 1862, -1, ""));
            _host.Add(new DomeStar("War Stars", -1991, 1003, -1, ""));
            _host.Add(new DomeStar("War Stars", -2091, 1069, -1, ""));
            _host.Add(new DomeStar("War Stars", -2125, 1125, -1, ""));
            _host.Add(new DomeStar("War Stars", -2231, 1021, -1, ""));
            _host.Add(new DomeStar("War Stars", -2289, 1201, -1, ""));
            _host.Add(new DomeStar("War Stars", -2437, 1398, -1, ""));
            _host.Add(new DomeStar("War Stars", -2484, 1173, -1, ""));
            _host.Add(new DomeStar("War Stars", -2511, 1279, -1, ""));
            _host.Add(new DomeStar("War Stars", -2708, 1325, -1, ""));
            _host.Add(new DomeStar("River", 53, 3035, -1, ""));
            _host.Add(new DomeStar("River", 83, 2765, -1, ""));
            _host.Add(new DomeStar("River", 98, 2450, -1, ""));
            _host.Add(new DomeStar("River", 99, 2225, -1, ""));
            _host.Add(new DomeStar("River", 99, 2045, -1, ""));
            _host.Add(new DomeStar("River", 129, 1790, -1, ""));
            _host.Add(new DomeStar("River", 189, 1520, -1, ""));
            _host.Add(new DomeStar("River", 219, 1340, -1, ""));
            _host.Add(new DomeStar("River", 354, 1100, -1, ""));
            _host.Add(new DomeStar("River", 534, 875, -1, ""));
            _host.Add(new DomeStar("River", 715, 650, -1, ""));
            _host.Add(new DomeStar("River", 880, 486, -1, ""));
            _host.Add(new DomeStar("River", 955, 261, -1, ""));
            _host.Add(new DomeStar("River", 1015, -115, -1, ""));
            _host.Add(new DomeStar("River", 910, -325, -1, ""));
            _host.Add(new DomeStar("River", 715, -610, -1, ""));
            _host.Add(new DomeStar("River", 415, -850, -1, ""));
            _host.Add(new DomeStar("River", 281, -1120, -1, ""));
            _host.Add(new DomeStar("River", 146, -1375, -1, ""));
            _host.Add(new DomeStar("River", 26, -1645, -1, ""));
            _host.Add(new DomeStar("River", -34, -1960, -1, ""));
            _host.Add(new DomeStar("River", -34, -2215, -1, ""));
            _host.Add(new DomeStar("River", -63, -2455, -1, ""));
            _host.Add(new DomeStar("River", -78, -2755, -1, ""));
            _host.Add(new DomeStar("River", -78, -2980, -1, ""));
            _host.Add(new DomeStar("Love Stars", -1923, 1619, 3, "Love Stars"));
            _host.Add(new DomeStar("War Stars", -2238, 1167, 3, "War Stars"));
            _host.Add(new DomeStar("Forest", -2770, 723, -2, ""));
            _host.Add(new DomeStar("Forest", -2305, 453, -2, ""));
            _host.Add(new DomeStar("Forest", -1915, 169, -2, ""));
            _host.Add(new DomeStar("Forest", -1645, -176, -2, ""));
            _host.Add(new DomeStar("Forest", -1390, -461, -2, ""));
            _host.Add(new DomeStar("Forest", -1180, -806, -2, ""));
            _host.Add(new DomeStar("Forest", -1164, -1286, -2, ""));
            _host.Add(new DomeStar("Forest", -1284, -1691, -2, ""));
            _host.Add(new DomeStar("Forest", -1389, -1991, -2, ""));
            _host.Add(new DomeStar("Forest", -1614, -2321, -2, ""));
            _host.Add(new DomeStar("Forest", -2800, 243, -2, ""));
            _host.Add(new DomeStar("Forest", -2845, -191, -2, ""));
            _host.Add(new DomeStar("Forest", -2785, -762, -2, ""));
            _host.Add(new DomeStar("Forest", -2679, -1181, -2, ""));
            _host.Add(new DomeStar("Forest", -2469, -1676, -2, ""));
            _host.Add(new DomeStar("Forest", -2139, -2006, -2, ""));
            _host.Add(new DomeStar("Forest", -1764, -1766, -2, ""));
            _host.Add(new DomeStar("Forest", -2350, 79, -2, ""));
            _host.Add(new DomeStar("Forest", -2335, -446, -2, ""));
            _host.Add(new DomeStar("Forest", -2200, -866, -2, ""));
            _host.Add(new DomeStar("Forest", -2034, -1271, -2, ""));
            _host.Add(new DomeStar("Forest", -1614, -1256, -2, ""));
            _host.Add(new DomeStar("Forest", -1795, -896, -2, ""));
            _host.Add(new DomeStar("Forest", -1765, -536, -2, ""));
            _host.Add(new DomeStar("Fields", -282, 2810, -3, ""));
            _host.Add(new DomeStar("Fields", -1361, 2539, -3, ""));
            _host.Add(new DomeStar("Fields", -1406, 1714, -3, ""));
            _host.Add(new DomeStar("Fields", -671, 2120, -3, ""));
            _host.Add(new DomeStar("Fields", -326, 1715, -3, ""));
            _host.Add(new DomeStar("Fields", -1436, 934, -3, ""));
            _host.Add(new DomeStar("Fields", -1315, 49, -3, ""));
            _host.Add(new DomeStar("Fields", -700, 80, -3, ""));
            _host.Add(new DomeStar("Fields", -1046, 1054, -3, ""));
            _host.Add(new DomeStar("Fields", -596, 1505, -3, ""));
            _host.Add(new DomeStar("Fields", -715, 665, -3, ""));
            _host.Add(new DomeStar("Fields", -730, -550, -3, ""));
            _host.Add(new DomeStar("Fields", -564, -1075, -3, ""));
            _host.Add(new DomeStar("Fields", -324, -1675, -3, ""));
            _host.Add(new DomeStar("Fields", -849, -2785, -3, ""));
            _host.Add(new DomeStar("Fields", -594, -2200, -3, ""));
            _host.Add(new DomeStar("City", -205, 228, -4, ""));
            _host.Add(new DomeStar("City", 18, 259, -4, ""));
            _host.Add(new DomeStar("City", 215, 150, -4, ""));
            _host.Add(new DomeStar("City", 247, -40, -4, ""));
            _host.Add(new DomeStar("City", 139, -203, -4, ""));
            _host.Add(new DomeStar("City", -39, -276, -4, ""));
            _host.Add(new DomeStar("City", -228, -153, -4, ""));
            _host.Add(new DomeStar("City", -281, 30, -4, ""));
        }
    }
}