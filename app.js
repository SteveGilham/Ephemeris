

class StarDome {
    constructor() {
        this.host = [];
        this.radian = 1618.614;
        this.skew = 0.04445887;
        this.populate();
    }

    draw(ctx, angle, slide, radius, x0, y0, names, radialOffset, bearing, up, look) {
        for (let i = 0; i < this.host.length; i++) {
            this.host[i].draw(ctx, this.skew - angle, slide, radius, x0, y0, names, radialOffset, bearing, up, look);
        }
    }

    populate() {
        this.host.push(new DomeStar("Pole Star", 1, 1, 50, "Pole Star"));
        this.host.push(new DomeStar("Arraz", -425, 185, 35, "Arraz"));
        this.host.push(new DomeStar("Ourania", 355, -325, 20, ""));
        this.host.push(new DomeStar("Evandal", 446, -1285, 20, "Oropum"));
        this.host.push(new DomeStar("Everina", 655, -760, 20, "Rice"));
        this.host.push(new DomeStar("Conspirator", 1451, -969, 20, "Whisperers"));
        this.host.push(new DomeStar("Maw", 910, -70, 20, ""));
        this.host.push(new DomeStar("Eye", 1120, -189, 20, ""));
        this.host.push(new DomeStar("Neck", 895, 96, 20, ""));
        this.host.push(new DomeStar("Chest", 805, 305, 20, "Star Dragon"));
        this.host.push(new DomeStar("Wing", 835, 576, 20, ""));
        this.host.push(new DomeStar("Belly", 565, 665, 20, ""));
        this.host.push(new DomeStar("Tail", 459, 935, 20, ""));
        this.host.push(new DomeStar("Stinger", -6, 1070, 20, ""));
        this.host.push(new DomeStar("Tail", 219, 1085, 20, ""));
        this.host.push(new DomeStar("Seed", 129, 1565, 20, "Tree", 'green'));
        this.host.push(new DomeStar("Erkonus", -1445, 544, 20, "Erkonus"));
        this.host.push(new DomeStar("Dove", -1355, -611, 20, "Dove"));
        this.host.push(new DomeStar("Harp", -574, -850, 20, "Harp"));
        this.host.push(new DomeStar("Steward", -185, -850, 20, "Steward"));
        this.host.push(new DomeStar("(Tongue)", -49, -1600, 5, ""));
        this.host.push(new DomeStar("Eye", 71, -2005, 20, "Lorion"));
        this.host.push(new DomeStar("Tail", 0, -2500, 20, ""));
        this.host.push(new DomeStar("Leg", 866, -2185, 20, "Thunderer"));
        this.host.push(new DomeStar("Leg", 761, -2350, 20, ""));
        this.host.push(new DomeStar("Arm", 926, -2380, 20, ""));
        this.host.push(new DomeStar("Arm", 1166, -2154, 20, ""));
        this.host.push(new DomeStar("Varnaga", 1241, -1614, 20, "Crocodile"));
        this.host.push(new DomeStar("Vergenari", 1961, -1479, 20, "Sow"));
        this.host.push(new DomeStar("Bull", 2140, -819, 20, "Bull"));
        this.host.push(new DomeStar("Oasis", 2140, 51, 20, "Oasis"));
        this.host.push(new DomeStar("Thasus", 1780, 666, 20, "Thasus"));
        this.host.push(new DomeStar("Pincer", 1359, 1506, 20, ""));
        this.host.push(new DomeStar("Womb", 1269, 1716, 20, "Scorpion"));
        this.host.push(new DomeStar("Jewel Flower", 474, 1910, 20, "Flowers"));
        this.host.push(new DomeStar("Youth", 113, 2540, 20, "Youth"));
        this.host.push(new DomeStar("(Shafesora)", -1911, 1354, 5, "Marsh"));
        this.host.push(new DomeStar("Lion", -2285, 544, 20, "Lion"));
        this.host.push(new DomeStar("Swan", -1955, -206, 20, "Swan"));
        this.host.push(new DomeStar("Bow", -1459, -1331, 20, ""));
        this.host.push(new DomeStar("Heart", -1339, -1571, 20, "Hunter"));
        this.host.push(new DomeStar("Hip", -1459, -1661, 20, ""));
        this.host.push(new DomeStar("Knee", -1609, -1601, 20, ""));
        this.host.push(new DomeStar("Pot", -814, -1795, 20, "Pot"));
        this.host.push(new DomeStar("Fan", -1024, -2336, 20, "Fan"));
        this.host.push(new DomeStar("Eye", -94, -1930, 20, ""));
        this.host.push(new DomeStar("Marker", -94, -2290, 20, ""));
        this.host.push(new DomeStar("Chorus", -260, 410, 10, "Chorus"));
        this.host.push(new DomeStar("", -380, 320, 10, ""));
        this.host.push(new DomeStar("", -470, 290, 10, ""));
        this.host.push(new DomeStar("", -545, 65, 10, ""));
        this.host.push(new DomeStar("", -440, 95, 10, ""));
        this.host.push(new DomeStar("", -380, -10, 10, ""));
        this.host.push(new DomeStar("", -485, -40, 10, ""));
        this.host.push(new DomeStar("Cook", -110, -265, 10, "Cook"));
        this.host.push(new DomeStar("", -80, -340, 10, ""));
        this.host.push(new DomeStar("", -155, -340, 10, ""));
        this.host.push(new DomeStar("", -170, -400, 10, ""));
        this.host.push(new DomeStar("", -245, -415, 10, ""));
        this.host.push(new DomeStar("", -140, -445, 10, ""));
        this.host.push(new DomeStar("", 430, -400, 10, ""));
        this.host.push(new DomeStar("", 280, -400, 10, ""));
        this.host.push(new DomeStar("", 190, -340, 10, ""));
        this.host.push(new DomeStar("Officers", 190, 110, 10, "Officers"));
        this.host.push(new DomeStar("", 160, 245, 10, ""));
        this.host.push(new DomeStar("", 130, 440, 10, ""));
        this.host.push(new DomeStar("", 325, 50, 10, ""));
        this.host.push(new DomeStar("", 401, -1015, 10, ""));
        this.host.push(new DomeStar("", 596, -1450, 10, ""));
        this.host.push(new DomeStar("", 701, -1435, 10, ""));
        this.host.push(new DomeStar("", 446, -1540, 10, ""));
        this.host.push(new DomeStar("", 386, -1345, 10, ""));
        this.host.push(new DomeStar("", 566, -1150, 10, ""));
        this.host.push(new DomeStar("", 535, -775, 10, ""));
        this.host.push(new DomeStar("", 805, -700, 10, ""));
        this.host.push(new DomeStar("", 715, -520, 10, ""));
        this.host.push(new DomeStar("", 610, -535, 10, ""));
        this.host.push(new DomeStar("", 565, -475, 10, ""));
        this.host.push(new DomeStar("", 475, -535, 10, ""));
        this.host.push(new DomeStar("", 445, -685, 10, ""));
        this.host.push(new DomeStar("", 385, -595, 10, ""));
        this.host.push(new DomeStar("", 295, -535, 10, ""));
        this.host.push(new DomeStar("", 1180, -909, 10, ""));
        this.host.push(new DomeStar("", 1301, -999, 10, ""));
        this.host.push(new DomeStar("", 1225, -744, 10, ""));
        this.host.push(new DomeStar("", 1330, -849, 10, ""));
        this.host.push(new DomeStar("", 1600, -969, 10, ""));
        this.host.push(new DomeStar("", 865, 186, 10, ""));
        this.host.push(new DomeStar("", 820, 440, 10, ""));
        this.host.push(new DomeStar("", 970, 621, 10, ""));
        this.host.push(new DomeStar("", 970, 726, 10, ""));
        this.host.push(new DomeStar("", 939, 861, 10, ""));
        this.host.push(new DomeStar("", 774, 1055, 10, ""));
        this.host.push(new DomeStar("", 1030, 726, 10, ""));
        this.host.push(new DomeStar("", 564, 830, 10, ""));
        this.host.push(new DomeStar("", 460, 800, 10, ""));
        this.host.push(new DomeStar("", 159, 995, 10, ""));
        this.host.push(new DomeStar("", -81, 1655, 10, ""));
        this.host.push(new DomeStar("", 54, 1715, 10, ""));
        this.host.push(new DomeStar("", 204, 1790, 10, ""));
        this.host.push(new DomeStar("", 264, 1655, 10, ""));
        this.host.push(new DomeStar("", 114, 1925, 10, ""));
        this.host.push(new DomeStar("", 159, 2015, 10, ""));
        this.host.push(new DomeStar("Yoke", -590, 905, 10, "Yoke"));
        this.host.push(new DomeStar("", -546, 1010, 10, ""));
        this.host.push(new DomeStar("", -486, 995, 10, ""));
        this.host.push(new DomeStar("", -545, 845, 10, ""));
        this.host.push(new DomeStar("", -500, 860, 10, ""));
        this.host.push(new DomeStar("", -1370, 469, 10, ""));
        this.host.push(new DomeStar("", -1490, 394, 10, ""));
        this.host.push(new DomeStar("Hawk", -860, -101, 10, "Hawk"));
        this.host.push(new DomeStar("", -830, -146, 10, ""));
        this.host.push(new DomeStar("", -800, -220, 10, ""));
        this.host.push(new DomeStar("", -920, -146, 10, ""));
        this.host.push(new DomeStar("", -890, -221, 10, ""));
        this.host.push(new DomeStar("", -845, -280, 10, ""));
        this.host.push(new DomeStar("", -1355, -356, 10, ""));
        this.host.push(new DomeStar("", -1475, -716, 10, ""));
        this.host.push(new DomeStar("", -1310, -701, 10, ""));
        this.host.push(new DomeStar("Quail", -1399, -896, 10, "Quail"));
        this.host.push(new DomeStar("", -1294, -986, 10, ""));
        this.host.push(new DomeStar("", -1414, -1001, 10, ""));
        this.host.push(new DomeStar("", -1309, -1076, 10, ""));
        this.host.push(new DomeStar("", -709, -895, 10, ""));
        this.host.push(new DomeStar("", -649, -955, 10, ""));
        this.host.push(new DomeStar("", -334, -970, 10, ""));
        this.host.push(new DomeStar("", -289, -955, 10, ""));
        this.host.push(new DomeStar("", -214, -940, 10, ""));
        this.host.push(new DomeStar("", 56, -1390, 10, ""));
        this.host.push(new DomeStar("", 251, -1345, 10, ""));
        this.host.push(new DomeStar("", 41, -1225, 10, ""));
        this.host.push(new DomeStar("", 10, -1000, 10, ""));
        this.host.push(new DomeStar("", 145, -1000, 10, ""));
        this.host.push(new DomeStar("Fishes", 26, -1480, 10, "Fishes"));
        this.host.push(new DomeStar("", 101, -1600, 10, ""));
        this.host.push(new DomeStar("", 56, -1750, 10, ""));
        this.host.push(new DomeStar("", -64, -1735, 10, ""));
        this.host.push(new DomeStar("", 131, -2170, 10, ""));
        this.host.push(new DomeStar("", -109, -2155, 10, ""));
        this.host.push(new DomeStar("", 851, -2305, 10, ""));
        this.host.push(new DomeStar("", 956, -2259, 10, ""));
        this.host.push(new DomeStar("", 1046, -2214, 10, ""));
        this.host.push(new DomeStar("", 1316, -1689, 10, ""));
        this.host.push(new DomeStar("", 1286, -1539, 10, ""));
        this.host.push(new DomeStar("", 1346, -1479, 10, ""));
        this.host.push(new DomeStar("", 1091, -1584, 10, ""));
        this.host.push(new DomeStar("", 1181, -1389, 10, ""));
        this.host.push(new DomeStar("", 1946, -1659, 10, ""));
        this.host.push(new DomeStar("", 2126, -1374, 10, ""));
        this.host.push(new DomeStar("", 2155, -939, 10, ""));
        this.host.push(new DomeStar("", 2156, -1029, 10, ""));
        this.host.push(new DomeStar("", 2215, -714, 10, ""));
        this.host.push(new DomeStar("", 2260, -804, 10, ""));
        this.host.push(new DomeStar("", 1990, 531, 10, ""));
        this.host.push(new DomeStar("", 1554, 846, 10, ""));
        this.host.push(new DomeStar("", 1659, 1056, 10, ""));
        this.host.push(new DomeStar("", 1389, 1821, 10, ""));
        this.host.push(new DomeStar("", 1334, 2048, 10, ""));
        this.host.push(new DomeStar("", 1134, 2091, 10, ""));
        this.host.push(new DomeStar("", 1149, 1656, 10, ""));
        this.host.push(new DomeStar("", 999, 1716, 10, ""));
        this.host.push(new DomeStar("", 909, 1896, 10, ""));
        this.host.push(new DomeStar("", 609, 1970, 10, ""));
        this.host.push(new DomeStar("", 489, 2135, 10, ""));
        this.host.push(new DomeStar("", 98, 2675, 10, ""));
        this.host.push(new DomeStar("", 23, 2435, 10, ""));
        this.host.push(new DomeStar("", -51, 2345, 10, ""));
        this.host.push(new DomeStar("", 84, 2390, 10, ""));
        this.host.push(new DomeStar("", 189, 2465, 10, ""));
        this.host.push(new DomeStar("", 248, 2375, 10, ""));
        this.host.push(new DomeStar("", -456, 2045, 10, ""));
        this.host.push(new DomeStar("", -366, 1940, 10, ""));
        this.host.push(new DomeStar("Willows", -351, 1745, 10, "Willows"));
        this.host.push(new DomeStar("", -456, 1655, 10, ""));
        this.host.push(new DomeStar("", -276, 1670, 10, ""));
        this.host.push(new DomeStar("", -366, 1580, 10, ""));
        this.host.push(new DomeStar("Plough", -1101, 1864, 10, "Plough"));
        this.host.push(new DomeStar("", -1116, 1759, 10, ""));
        this.host.push(new DomeStar("", -1056, 1759, 10, ""));
        this.host.push(new DomeStar("", -981, 1819, 10, ""));
        this.host.push(new DomeStar("", -996, 1759, 10, ""));
        this.host.push(new DomeStar("", -1071, 1699, 10, ""));
        this.host.push(new DomeStar("Veridna", -1656, 2384, 10, "Veridna"));
        this.host.push(new DomeStar("", -1461, 2474, 10, ""));
        this.host.push(new DomeStar("", -1611, 2504, 10, ""));
        this.host.push(new DomeStar("", -1716, 2474, 10, ""));
        this.host.push(new DomeStar("", -1701, 2594, 10, ""));
        this.host.push(new DomeStar("", -1686, 2699, 10, ""));
        this.host.push(new DomeStar("", -2495, 574, 10, ""));
        this.host.push(new DomeStar("", -2390, 424, 10, ""));
        this.host.push(new DomeStar("", -2225, 454, 10, ""));
        this.host.push(new DomeStar("", -2165, 394, 10, ""));
        this.host.push(new DomeStar("", -2210, -71, 10, ""));
        this.host.push(new DomeStar("", -2120, -116, 10, ""));
        this.host.push(new DomeStar("", -2000, -41, 10, ""));
        this.host.push(new DomeStar("", -1880, -161, 10, ""));
        this.host.push(new DomeStar("", -1865, -206, 10, ""));
        this.host.push(new DomeStar("", -2030, -341, 10, ""));
        this.host.push(new DomeStar("Hag", -2615, -417, 10, "Hag"));
        this.host.push(new DomeStar("", -2705, -462, 10, ""));
        this.host.push(new DomeStar("", -2885, -447, 10, ""));
        this.host.push(new DomeStar("", -2825, -627, 10, ""));
        this.host.push(new DomeStar("", -2570, -762, 10, ""));
        this.host.push(new DomeStar("", -2614, -852, 10, ""));
        this.host.push(new DomeStar("", -2540, -822, 10, ""));
        this.host.push(new DomeStar("", -2464, -777, 10, ""));
        this.host.push(new DomeStar("Borna", -2674, -927, 10, "Borna"));
        this.host.push(new DomeStar("", -2404, -852, 10, ""));
        this.host.push(new DomeStar("", -2254, -1016, 10, ""));
        this.host.push(new DomeStar("", -2224, -1121, 10, ""));
        this.host.push(new DomeStar("Deer", -2254, -1211, 10, "Deer"));
        this.host.push(new DomeStar("", -2119, -1256, 10, ""));
        this.host.push(new DomeStar("", -2119, -1316, 10, ""));
        this.host.push(new DomeStar("Firestick", -2614, -1362, 10, "Firestick"));
        this.host.push(new DomeStar("", -2614, -1437, 10, ""));
        this.host.push(new DomeStar("", -2599, -1512, 10, ""));
        this.host.push(new DomeStar("", -2584, -1572, 10, ""));
        this.host.push(new DomeStar("", -2599, -1617, 10, ""));
        this.host.push(new DomeStar("", -2494, -1602, 10, ""));
        this.host.push(new DomeStar("", -1399, -1481, 10, ""));
        this.host.push(new DomeStar("", -1594, -1391, 10, ""));
        this.host.push(new DomeStar("", -1684, -1436, 10, ""));
        this.host.push(new DomeStar("", -1774, -1511, 10, ""));
        this.host.push(new DomeStar("", -2029, -1676, 10, ""));
        this.host.push(new DomeStar("Groundhog", -2029, -1766, 10, "Groundhog"));
        this.host.push(new DomeStar("", -1909, -2051, 10, ""));
        this.host.push(new DomeStar("", -1774, -2036, 10, ""));
        this.host.push(new DomeStar("Rabbits", -1744, -2126, 10, "Rabbits"));
        this.host.push(new DomeStar("", -1744, -2216, 10, ""));
        this.host.push(new DomeStar("Raven", -589, -2125, 10, "Raven"));
        this.host.push(new DomeStar("", -574, -2215, 10, ""));
        this.host.push(new DomeStar("", -649, -2230, 10, ""));
        this.host.push(new DomeStar("", -679, -2305, 10, ""));
        this.host.push(new DomeStar("", -529, -2260, 10, ""));
        this.host.push(new DomeStar("", -603, -2320, 10, ""));
        this.host.push(new DomeStar("Love Stars", -1835, 1647, -1, ""));
        this.host.push(new DomeStar("Love Stars", -1881, 1459, -1, ""));
        this.host.push(new DomeStar("Love Stars", -1967, 1552, -1, ""));
        this.host.push(new DomeStar("Love Stars", -2000, 1678, -1, ""));
        this.host.push(new DomeStar("Love Stars", -2013, 1859, -1, ""));
        this.host.push(new DomeStar("Love Stars", -2166, 1714, -1, ""));
        this.host.push(new DomeStar("Love Stars", -2222, 1981, -1, ""));
        this.host.push(new DomeStar("Love Stars", -2239, 1771, -1, ""));
        this.host.push(new DomeStar("Love Stars", -2403, 1862, -1, ""));
        this.host.push(new DomeStar("War Stars", -1991, 1003, -1, ""));
        this.host.push(new DomeStar("War Stars", -2091, 1069, -1, ""));
        this.host.push(new DomeStar("War Stars", -2125, 1125, -1, ""));
        this.host.push(new DomeStar("War Stars", -2231, 1021, -1, ""));
        this.host.push(new DomeStar("War Stars", -2289, 1201, -1, ""));
        this.host.push(new DomeStar("War Stars", -2437, 1398, -1, ""));
        this.host.push(new DomeStar("War Stars", -2484, 1173, -1, ""));
        this.host.push(new DomeStar("War Stars", -2511, 1279, -1, ""));
        this.host.push(new DomeStar("War Stars", -2708, 1325, -1, ""));
        this.host.push(new DomeStar("River", 53, 3035, -1, ""));
        this.host.push(new DomeStar("River", 83, 2765, -1, ""));
        this.host.push(new DomeStar("River", 98, 2450, -1, ""));
        this.host.push(new DomeStar("River", 99, 2225, -1, ""));
        this.host.push(new DomeStar("River", 99, 2045, -1, ""));
        this.host.push(new DomeStar("River", 129, 1790, -1, ""));
        this.host.push(new DomeStar("River", 189, 1520, -1, ""));
        this.host.push(new DomeStar("River", 219, 1340, -1, ""));
        this.host.push(new DomeStar("River", 354, 1100, -1, ""));
        this.host.push(new DomeStar("River", 534, 875, -1, ""));
        this.host.push(new DomeStar("River", 715, 650, -1, ""));
        this.host.push(new DomeStar("River", 880, 486, -1, ""));
        this.host.push(new DomeStar("River", 955, 261, -1, ""));
        this.host.push(new DomeStar("River", 1015, -115, -1, ""));
        this.host.push(new DomeStar("River", 910, -325, -1, ""));
        this.host.push(new DomeStar("River", 715, -610, -1, ""));
        this.host.push(new DomeStar("River", 415, -850, -1, ""));
        this.host.push(new DomeStar("River", 281, -1120, -1, ""));
        this.host.push(new DomeStar("River", 146, -1375, -1, ""));
        this.host.push(new DomeStar("River", 26, -1645, -1, ""));
        this.host.push(new DomeStar("River", -34, -1960, -1, ""));
        this.host.push(new DomeStar("River", -34, -2215, -1, ""));
        this.host.push(new DomeStar("River", -63, -2455, -1, ""));
        this.host.push(new DomeStar("River", -78, -2755, -1, ""));
        this.host.push(new DomeStar("River", -78, -2980, -1, ""));
        this.host.push(new DomeStar("Love Stars", -1923, 1619, 3, "Love Stars"));
        this.host.push(new DomeStar("War Stars", -2238, 1167, 3, "War Stars"));
        this.host.push(new DomeStar("Forest", -2770, 723, -2, ""));
        this.host.push(new DomeStar("Forest", -2305, 453, -2, ""));
        this.host.push(new DomeStar("Forest", -1915, 169, -2, ""));
        this.host.push(new DomeStar("Forest", -1645, -176, -2, ""));
        this.host.push(new DomeStar("Forest", -1390, -461, -2, ""));
        this.host.push(new DomeStar("Forest", -1180, -806, -2, ""));
        this.host.push(new DomeStar("Forest", -1164, -1286, -2, ""));
        this.host.push(new DomeStar("Forest", -1284, -1691, -2, ""));
        this.host.push(new DomeStar("Forest", -1389, -1991, -2, ""));
        this.host.push(new DomeStar("Forest", -1614, -2321, -2, ""));
        this.host.push(new DomeStar("Forest", -2800, 243, -2, ""));
        this.host.push(new DomeStar("Forest", -2845, -191, -2, ""));
        this.host.push(new DomeStar("Forest", -2785, -762, -2, ""));
        this.host.push(new DomeStar("Forest", -2679, -1181, -2, ""));
        this.host.push(new DomeStar("Forest", -2469, -1676, -2, ""));
        this.host.push(new DomeStar("Forest", -2139, -2006, -2, ""));
        this.host.push(new DomeStar("Forest", -1764, -1766, -2, ""));
        this.host.push(new DomeStar("Forest", -2350, 79, -2, ""));
        this.host.push(new DomeStar("Forest", -2335, -446, -2, ""));
        this.host.push(new DomeStar("Forest", -2200, -866, -2, ""));
        this.host.push(new DomeStar("Forest", -2034, -1271, -2, ""));
        this.host.push(new DomeStar("Forest", -1614, -1256, -2, ""));
        this.host.push(new DomeStar("Forest", -1795, -896, -2, ""));
        this.host.push(new DomeStar("Forest", -1765, -536, -2, ""));
        this.host.push(new DomeStar("Fields", -282, 2810, -3, ""));
        this.host.push(new DomeStar("Fields", -1361, 2539, -3, ""));
        this.host.push(new DomeStar("Fields", -1406, 1714, -3, ""));
        this.host.push(new DomeStar("Fields", -671, 2120, -3, ""));
        this.host.push(new DomeStar("Fields", -326, 1715, -3, ""));
        this.host.push(new DomeStar("Fields", -1436, 934, -3, ""));
        this.host.push(new DomeStar("Fields", -1315, 49, -3, ""));
        this.host.push(new DomeStar("Fields", -700, 80, -3, ""));
        this.host.push(new DomeStar("Fields", -1046, 1054, -3, ""));
        this.host.push(new DomeStar("Fields", -596, 1505, -3, ""));
        this.host.push(new DomeStar("Fields", -715, 665, -3, ""));
        this.host.push(new DomeStar("Fields", -730, -550, -3, ""));
        this.host.push(new DomeStar("Fields", -564, -1075, -3, ""));
        this.host.push(new DomeStar("Fields", -324, -1675, -3, ""));
        this.host.push(new DomeStar("Fields", -849, -2785, -3, ""));
        this.host.push(new DomeStar("Fields", -594, -2200, -3, ""));
        this.host.push(new DomeStar("City", -205, 228, -4, ""));
        this.host.push(new DomeStar("City", 18, 259, -4, ""));
        this.host.push(new DomeStar("City", 215, 150, -4, ""));
        this.host.push(new DomeStar("City", 247, -40, -4, ""));
        this.host.push(new DomeStar("City", 139, -203, -4, ""));
        this.host.push(new DomeStar("City", -39, -276, -4, ""));
        this.host.push(new DomeStar("City", -228, -153, -4, ""));
        this.host.push(new DomeStar("City", -281, 30, -4, ""));
    }
}

class DomeStar {
    constructor(name, x, y, mag, marker, col = 'white') {
        this.name = name;
        this.x = x;
        this.y = y;
        this.mag = mag;
        this.marker = marker;
        this.col = col;
        this.r = Math.sqrt((this.x * this.x + this.y * this.y)) / 1618.614;
        this.theta = Math.atan2(this.y, this.x);
        this.diam = Math.rint(Math.sqrt(this.mag));
    }

    draw(ctx, spin, slide, radius, x0, y0, names, radialOffset, bearing, up, look) {
        let th = -this.theta - spin;
        let x = Math.cos(th) * Math.sin(this.r);
        let y = Math.sin(th) * Math.sin(this.r);
        let z = Math.cos(this.r);

        let ty = y;
        let tx = x * Math.cos(slide) + z * Math.sin(slide);
        let tz = -x * Math.sin(slide) + z * Math.cos(slide);

        if (tz < 0) return;

        tx -= radialOffset * Math.cos(bearing);
        ty -= radialOffset * Math.sin(bearing);

        let px, py;

        if (up) {
            let azimuth = Math.atan2(tx, ty);
            let altitude = Math.atan2(Math.sqrt(tx * tx + ty * ty), tz);
            altitude /= (Math.PI / 2.0);
            px = x0 + Math.rint(radius * altitude * Math.cos(azimuth));
            py = y0 - Math.rint(radius * altitude * Math.sin(azimuth));
        } else {
            let c = Math.cos(look);
            let s = Math.sin(look);
            x = tx * c + ty * s;
            y = tx * s - ty * c;
            z = tz;
            if (x < 0) return;
            let azimuth = Math.atan2(y, z) + Math.PI / 2.0;
            let altitude = Math.atan2(Math.sqrt(y * y + z * z), x);
            altitude /= (Math.PI / 2.0);
            px = x0 - Math.rint(radius * altitude * Math.cos(azimuth));
            py = y0 - Math.rint(radius * altitude * Math.sin(azimuth));
        }

        ctx.fillStyle = this.col;
        ctx.beginPath();
        ctx.arc(px, py, this.diam / 2, 0, 2 * Math.PI);
        ctx.fill();

        if (names && this.marker.length > 0) {
            ctx.fillStyle = 'cyan';
            ctx.fillText(this.marker, px + 5, py);
        }
    }
}

class EphemerisII {
    constructor() {
        this.canvas = document.getElementById('ephemerisCanvas');
        this.ctx = this.canvas.getContext('2d');
        this.animation = new EphemerisIIAnimation(this.ctx);
        this.run();
    }

    run() {
        this.animation.paint();
        requestAnimationFrame(() => this.run());
    }
}



class E2Param {
    static domeRadius = 20;   // e6 meters
    static yuthu = false;  // sun overhead in yuthuppa midsummer
    static winterTilt = -10.6; //deg
    static equinoxTilt = 0;
    static summerTilt = 9.0;

    static summerDay = 0.1;       // deviation from 0.5 days
    static winterDay = -1.06/9.0;

    static uleria = false; // sidereal period

    static shargashRise = 0.0;
    static twinRise = 0.0;
    static artiaRise = 0.0;

    static getHarmonic(week, day, hour) {
        let dday = (7.0 * week + day);
        dday += hour; // days since midnight on 1st day of year
        // Spring equinox is dday == 0.5
        // Summer solstice is dday == 68.0, 9 deg N after 67.5 days
        // Autumn equinox is dday == 135.5 after 67.5 days
        // Winter solstice is dday == 215 10.6 deg S after 79.5 days
        // Spring equinox is dday == 294.5 after 79.5 days
        // We could fix up a smoother function some time

        let cycle, amplitude, harmonic;
        if (dday < 0.5) dday += 294;
        if (dday < 135.5) {
            cycle = (dday - 0.5) * Math.PI / 135.0;
            harmonic = Math.sin(cycle);
        }
        else {
            cycle = (dday - 135.5) * Math.PI / 159.0;
            harmonic = -Math.sin(cycle);
        }
        return harmonic;
    }

    static getTilt(harmonic) // deg
    {
        if (!this.yuthu) {
            if (harmonic >= 0) return this.equinoxTilt + (this.summerTilt - this.equinoxTilt) * harmonic;
            else return this.equinoxTilt - (this.winterTilt - this.equinoxTilt) * harmonic;
        }
        else {
            let plus = Math.asin(3.6 / this.domeRadius) * 180.0 / Math.PI;
            if (harmonic >= 0) return plus * harmonic;
            else return plus * harmonic * 10.6 / 9;
        }
    }

    static getDayLength(harmonic) // days
    {
        if (harmonic >= 0) return 0.5 + this.summerDay * harmonic;
        else return 0.5 - this.winterDay * harmonic;
    }
}


class EphemerisIIAnimation {
    constructor(ctx) {
        this.ctx = ctx;
        this.size = { width: ctx.canvas.width, height: ctx.canvas.height };
        this.sky = new StarDome();
        this.spinAngle = 0.0;
        this.tiltVector = 0.0;
        this.year = 1600;
        this.week = 0;
        this.day = 0;
        this.hour = 0.75;
    }

    paint() {
        this.ctx.fillStyle = 'black';
        this.ctx.fillRect(0, 0, this.size.width, this.size.height);

        let up = true;
        let look = 0;
        let side = (this.size.width < this.size.height) ? this.size.width : this.size.height;
        let halfSide = 0.45 * side;
        let offset = 0;
        let bearing = 0;

        this.setAngles();

        this.sky.draw(this.ctx, this.spinAngle, this.tiltVector, halfSide, side / 2, side / 2, true, offset, bearing, up, look);
    }

    setAngles() {
        let dday = (7.0 * this.week + this.day);
        let yrpart = dday / 294.0;
        let baseHarmonic = E2Param.getHarmonic(this.week, this.day, this.hour);
        this.spinAngle = Math.PI * 2.0 * ((this.hour - 0.75) + yrpart);
        this.tiltVector = E2Param.getTilt(baseHarmonic) * Math.PI / 180.0;
    }
}


window.onload = () => {
    new EphemerisII();
};
