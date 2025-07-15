using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Threading;
using System.Linq;

namespace Ephemeris.WPF
{
    public partial class EphemerisIIControlPanel : UserControl, IE2TimeOfDay, ISlideListener
    {
        private string[] _cults = {
            "Voria", "Gorgorma", "Uleria", "Flamal", "Triolina", "Dormal", "Bagog",
            "[Summer Solstice]", "Invisible God", "Red Goddess", "Yelmalio",
            "Pamalt", "Babeester Gor", "Asrelia", "Earth Goddesses", "Lodril",
            "Maran Gor", "Lokarnos", "Kyger Litor, 7 Mothers",
            "Argan Argar", "Zorak Zoran", "Waha the Butcher", "Xiola Umbar", "Valind",
            "Magasta", "Subere", "Godunya", "Ty Kora Tek", "[Winter Solstice]", "Donandar",
            "Humakt", "Storm Bull", "Tsankth", "Orlanth", "Unholy Trio", "Issaries",
            "Lhankor Mhy"
        };

        private int[] _dates = {
            1, 3, 7, 23, 28, 37, 43,
            68, 80, 97, 110,
            116, 127, 134, 136, 138,
            139, 152, 175,
            177, 183, 189, 190, 193,
            196, 197, 201, 213, 215, 235,
            242, 258, 261, 263, 281, 286,
            287
        };

        public TextBox CalendarTextBox { get; set; } // Publicly exposed for EphemerisII.cs

        public int Year { get; private set; } = 1600;
        public int Week { get; private set; } = 0;
        public int Day { get; private set; } = 0;
        public int Season { get; private set; } = 8;

        public double Hour { get; private set; } = 0.75;
        private double _yrPart = 0.0;
        private double _baseHarmonic = 0.0;

        private bool _ticking = false;
        private DispatcherTimer _timer;

        private double _delta, _delta0 = 293.0 / 294.0, _freeDelta = 1.0 / 48.0;

        private static readonly string[] _dow = { "Freezeday", "Waterday", "Clayday", "Windsday", "Fireday", "Wildday", "Godsday" };
        private static readonly string[] _wos = { "Disorder", "Harmony", "Death", "Fertility", "Stasis", "Movement", "Illusion", "Truth", "Luck", "Fate" };
        private static readonly string[] _soy = { "Sea", "Fire", "Earth", "Dark", "Storm" };

        public EphemerisIIControlPanel()
        {
            InitializeComponent();

            // Add HolyDays to ListBox
            foreach (string cult in _cults)
            {
                HolyDaysListBox.Items.Add(cult);
            }
            HolyDaysListBox.SelectedIndex = 0;

            // Initialize Sliders and attach event handlers
            MinBar.SlideEvent += (s, e) => OnSlideEvent(s, e);
            HrBar.SlideEvent += (s, e) => OnSlideEvent(s, e);
            DowBar.SlideEvent += (s, e) => OnSlideEvent(s, e);
            WkBar.SlideEvent += (s, e) => OnSlideEvent(s, e);
            YrBar.SlideEvent += (s, e) => OnSlideEvent(s, e);
            CenBar.SlideEvent += (s, e) => OnSlideEvent(s, e);

            // Initialize RadioButtons for step interval
            CbSec.Checked += StepInterval_Checked;
            CbMin.Checked += StepInterval_Checked;
            CbTen.Checked += StepInterval_Checked;
            CbHr.Checked += StepInterval_Checked;
            CbDay.Checked += StepInterval_Checked;
            CbSday.Checked += StepInterval_Checked;
            CbSolar.Checked += StepInterval_Checked;
            CbFree.Checked += StepInterval_Checked;

            // Initialize RadioButtons for time format
            CbTest.Checked += TimeFormat_Checked;
            CbWest.Checked += TimeFormat_Checked;
            CbEast.Checked += TimeFormat_Checked;
            CbPamalt.Checked += TimeFormat_Checked;
            CbOrlanth.Checked += TimeFormat_Checked;
            CbPeloria.Checked += TimeFormat_Checked;

            // Initialize E2SelfButtons
            Midnight00hButton.Click += (s, e) => SetTimeOfDay(0);
            DawnButton.Click += (s, e) => SetTimeOfDay(1);
            NoonButton.Click += (s, e) => SetTimeOfDay(2);
            DuskButton.Click += (s, e) => SetTimeOfDay(3);
            Midnight24hButton.Click += (s, e) => SetTimeOfDay(4);

            // Set initial calendar text
            // CalendarTextBox.Text = "Freezeday 00:00:00     Disorder week Sea season 1600ST";

            SetLabel();

            _timer = new DispatcherTimer();
            _timer.Interval = TimeSpan.FromSeconds(1); // Tick every second
            _timer.Tick += (sender, e) => Tick();
        }

        public double GetBaseHarmonic() { return _baseHarmonic; }

        public void GetSolarDelta(bool next)
        {
            double dayLength = E2Param.GetDayLength(GetBaseHarmonic());
            double dawn = (1.0 - dayLength) / 2.0;
            bool day = (Hour >= dawn) && ((1.0 - Hour) > dawn);
            double phase = 0;
            if (day) phase = (Hour - dawn) / dayLength;
            else if (Hour > 0.5) phase = (Hour - (1.0 - dawn)) / (1.0 - dayLength);
            else phase = (Hour + dawn) / (1.0 - dayLength);

            dayLength = E2Param.GetDayLength(
                E2Param.GetHarmonic(Week, next ? Day + 1 : Day - 1, Hour));
            dawn = (1.0 - dayLength) / 2.0;

            double newHour = Hour;
            if (day) newHour = phase * dayLength + dawn;
            else if (Hour > 0.5) newHour = phase * (1.0 - dayLength) + (1.0 - dawn);
            else newHour = phase * (1.0 - dayLength) - dawn;

            if (next) { _delta = 1.0 + newHour - Hour; }
            else { _delta = newHour - (Hour + 1.0); }
        }

        public void Tick()
        {
            if (!_ticking) return;

            if (CbSec.IsChecked == true) _delta = 1.0 / 86400.0;
            else if (CbMin.IsChecked == true) _delta = 1.0 / 1440.0;
            else if (CbTen.IsChecked == true) _delta = 1.0 / 144.0;
            else if (CbHr.IsChecked == true) _delta = 1.0 / 24.0;
            else if (CbDay.IsChecked == true) _delta = 1.0;
            else if (CbSday.IsChecked == true) _delta = 293.0 / 294.0; // Sidereal day approx
            else if (CbSolar.IsChecked == true) GetSolarDelta(!CbReverse.IsChecked == true);
            else if (CbFree.IsChecked == true) _delta = _freeDelta;
            else _delta = _delta0;

            if (CbReverse.IsChecked == true && CbSolar.IsChecked != true) Hour -= _delta;
            else Hour += _delta;
            SetTime();
        }

        public void SetTime()
        {
            if (Math.Abs(Hour) >= 1.0)
            {
                double ddays = Math.Floor(Math.Abs(Hour));
                int idays = (int)Math.Round(ddays);
                if (Hour > 0)
                {
                    Day += idays;
                    Hour -= idays;
                    while (Day >= 294) { Day -= 294; ++Year; }
                }
                else
                {
                    Day -= idays;
                    Hour += idays;
                    while (Day < 0) { Day += 294; --Year; }
                }
            }

            if (Hour >= 0.999995)
            {
                Hour -= 1;
                Day += 1;
            }
            else if (Hour < 0)
            {
                Day -= 1;
                Hour += 1;
            }

            int hr = (int)(24 * Hour);
            if ((24 * Hour) - hr > 0.9999) hr += 1;

            HrBar.Value = hr;
            int min = (int)(Hour * 1440.0 - 60 * hr);
            int seconds = (int)(Hour * 86400.0 - 3600 * hr - 60 * min);
            MinBar.Value = min;

            while (Day > 6)
            {
                Day -= 7;
                Week += 1;
            }
            while (Day < 0)
            {
                Day += 7;
                Week -= 1;
            }
            DowBar.Value = Day;

            while (Week > 41)
            {
                Year += 1;
                Week -= 42;
            }
            while (Week < 0)
            {
                Year -= 1;
                Week += 42;
            }
            WkBar.Value = Week;

            int yp = Year + 11000;
            int y100 = yp % 100;
            int c100 = (yp / 100);
            YrBar.Value = y100;
            CenBar.Value = c100;

            SetLabel();
        }

        private void SetLabel()
        {
            Year = (CenBar.Value - 110) * 100 + YrBar.Value;
            Day = DowBar.Value;
            Week = WkBar.Value;

            Hour = ((double)HrBar.Value) / 24.0;
            Hour += ((double)MinBar.Value) / 1440.0;
            // Seconds are not directly controlled by a slider, so use the internal 'seconds' field
            // Hour += ((double)seconds) / 86400.0; 

            if (Week > 41) Week = 41; // Clamp week value
            WkBar.SetValues(Week, 5, 0, 41);

            SetHarmonic();
            SetLocal();
        }

        public void SetLocal()
        {
            double dawn = (1.0 - E2Param.GetDayLength(_baseHarmonic)) / 2.0;
            double dusk = (1.0 + E2Param.GetDayLength(_baseHarmonic)) / 2.0;
            double part, h24;
            int hr, min, sec;
                        string hh, mm, ss, text = string.Empty;

            if (CbTest.IsChecked == true)
            {
                h24 = Hour * 24.0;
                hr = (int)h24;
                min = (int)((h24 - hr) * 60.0);
                sec = (int)(((h24 - hr) * 60.0 - min) * 60.0);
                hh = hr.ToString("00");
                mm = min.ToString("00");
                ss = sec.ToString("00");

                text = $"{hh}:{mm}:{ss}    {_dow[Day]}    ";

                if (Week < 5 * Season)
                {
                    text += $"{_wos[Week % Season]} week, {_soy[Week / Season]} season {Year}ST    ";
                }
                else
                {
                    int w = Week - 5 * Season + 1;
                    text += $"Sacred Week {w} {Year}ST    ";
                }
                double dday = (7.0 * Week + Day) + Hour;
                text += $"Day {dday:F3} tilt {E2Param.GetTilt(_baseHarmonic):F3}    ";

                double dd = E2Param.GetDayLength(_baseHarmonic) * 24.0;
                hr = (int)dd;
                min = (int)((dd - hr) * 60.0);
                text += $"day len/24h = {hr}h{min}m";
            }
            else if (CbWest.IsChecked == true)
            {
                if (Hour < dawn)
                {
                    part = 8.0 * ((Hour / dawn) + 1.0);
                    hr = (int)part;
                    min = (int)((part - hr) * 64.0);
                    mm = min.ToString("00");
                    text = $"Night hour {hr}:{mm}";
                }
                else if (Hour < dusk)
                {
                    part = 16.0 * (Hour - dawn) / (dusk - dawn);
                    hr = (int)part;
                    min = (int)((part - hr) * 64.0);
                    mm = min.ToString("00");
                    text = $"Day hour {hr}:{mm}";
                }
                else
                {
                    part = 8.0 * (Hour - dusk) / (1.0 - dusk);
                    hr = (int)part;
                    min = (int)((part - hr) * 64.0);
                    mm = min.ToString("00");
                    text = $"Night hour {hr}:{mm}";
                }
                int dd = Day + 7 * Week;
                text += $"    Day {dd} {Year}ST";
            }
            else if (CbEast.IsChecked == true)
            {
                h24 = Hour * 24.0;
                hr = (int)h24;
                min = (int)((h24 - hr) * 60.0);
                sec = (int)(((h24 - hr) * 60.0 - min) * 60.0);
                hh = hr.ToString("00");
                mm = min.ToString("00");
                ss = sec.ToString("00");

                text = $"{hh}:{mm}:{ss}    ";
                int num = Day + 1;

                text += $"{num}-day    The week of ";
                switch (Week)
                {
                    case 0: text += "Wise Passivity"; break;
                    case 1: text += "Tranquil Composure"; break;
                    case 2: text += "Lucid Stillness"; break;
                    case 3: text += "Taciturn Solemnity"; break;
                    case 4: text += "Fortunate Incapacity"; break;
                    case 5: text += "Profound Solitude"; break;
                    case 6: text += "Futile Annihilation"; break;

                    case 7: text += "Erudite Obfuscation"; break;
                    case 8: text += "Concealed Truths"; break;
                    case 9: text += "Privy Trust"; break;
                    case 10: text += "Inner Knowledge"; break;
                    case 11: text += "Constrained Discretion"; break;
                    case 12: text += "Esoteric Reality"; break;
                    case 13: text += "Lurking Ambuscade"; break;

                    case 14: text += "Naked Essence"; break;
                    case 15: text += "the Fervid Soul"; break;
                    case 16: text += "Cheery Exhilaration"; break;
                    case 17: text += "Vitality"; break;
                    case 18: text += "Absolute Innascibility"; break;
                    case 19: text += "Pleasant Torpor"; break;
                    case 20: text += "the Journey's End"; break;

                    case 21: text += "Practiced Sagacity"; break;
                    case 22: text += "Adroit Readiness"; break;
                    case 23: text += "Conscious Insight"; break;
                    case 24: text += "Ingenious Success"; break;
                    case 25: text += "Exquisite Sensation"; break;
                    case 26: text += "Poignant Memory"; break;
                    case 27: text += "Dull Oblivion"; break;

                    case 28: text += "Assured Credence"; break;
                    case 29: text += "Seeking Comprehension"; break;
                    case 30: text += "Intelligent Incredulity"; break;
                    case 31: text += "Sufficient Omniscience"; break;
                    case 32: text += "Hesitant Cognizance"; break;
                    case 33: text += "Mature Nescience"; break;
                    case 34: text += "Mindless Dolour"; break;

                    case 35: text += "Exuberant Creation"; break;
                    case 36: text += "Portentous Gloom"; break;
                    case 37: text += "the Unpathed Waters"; break;
                    case 38: text += "the Living Glebe"; break;
                    case 39: text += "Effulgent Radiance"; break;
                    case 40: text += "Novel Tempestuousness"; break;
                    case 41: text += "Universal Ruin"; break;
                }
                text += "    The month of ";
                switch (Week / 7)
                {
                    case 0: text += "Silence"; break;
                    case 1: text += "Secrets"; break;
                    case 2: text += "Being"; break;
                    case 3: text += "Experience"; break;
                    case 4: text += "Thought"; break;
                    case 5: text += "Spirit"; break;
                }
                text += $"    {Year}ST";
            }
            else if (CbPamalt.IsChecked == true)
            {
                double del = (dusk - dawn) / 4.0;
                double del2 = (1.0 - dusk) / 2.0;
                if (Hour < (dawn / 2)) text = "Late Night";
                else if (Hour < dawn) text = "Dawning";
                else if (Hour < dawn + del) text = "Early Morning";
                else if (Hour < dawn + 2 * del) text = "Early Day";
                else if (Hour < dawn + 3 * del) text = "Early Eve";
                else if (Hour < dusk) text = "Late Eve";
                else if (Hour < dusk + del2) text = "Gloaming";
                else text = "Early Night";
                int dd = Day + 7 * Week;
                text += $"    Day {dd} {Year}ST";
            }
            else if (CbOrlanth.IsChecked == true)
            {
                if (Hour < dawn)
                {
                    part = 6.0 * ((Hour / dawn) + 1.0);
                    hr = (int)part;
                    min = (int)((part - hr) * 60.0);
                    mm = min.ToString("00");
                    text = $"Night hour {hr}:{mm}";
                }
                else if (Hour < dusk)
                {
                    part = 12.0 * (Hour - dawn) / (dusk - dawn);
                    hr = (int)part;
                    min = (int)((part - hr) * 60.0);
                    mm = min.ToString("00");
                    text = $"Day hour {hr}:{mm}";
                }
                else
                {
                    part = 6.0 * (Hour - dusk) / (1.0 - dusk);
                    hr = (int)part;
                    min = (int)((part - hr) * 60.0);
                    mm = min.ToString("00");
                    text = $"Night hour {hr}:{mm}";
                }
                text += $"    {_dow[Day]}    ";

                if (Week < 5 * Season)
                {
                    text += $"{_wos[Week % Season]} week, {_soy[Week / Season]} season {Year}ST";
                }
                else
                {
                    int w = Week - 5 * Season + 1;
                    text += $"Sacred Week {w} {Year}ST";
                }
            }
            else if (CbPeloria.IsChecked == true)
            {
                if (Hour < dawn)
                {
                    part = 5.0 * ((Hour / dawn) + 1.0);
                    hr = (int)part;
                    min = (int)((part - hr) * 100.0);
                    mm = min.ToString("00");
                    text = $"Night hour {hr}.{mm}";
                }
                else if (Hour < dusk)
                {
                    part = 15.0 * (Hour - dawn) / (dusk - dawn);
                    hr = (int)part;
                    min = (int)((part - hr) * 100.0);
                    mm = min.ToString("00");
                    text = $"Day hour {hr}.{mm}";
                }
                else
                {
                    part = 5.0 * (Hour - dusk) / (1.0 - dusk);
                    hr = (int)part;
                    min = (int)((part - hr) * 100.0);
                    mm = min.ToString("00");
                    text = $"Night hour {hr}.{mm}";
                }
                int dd = Day + 7 * Week;
                text += $"    Day {dd} {Year}ST";
            }
            CalendarTextBox.Text = text;
        }

        private double SetHarmonic()
        {
            double dday = (7.0 * Week + Day);
            _yrPart = dday / 294.0;
            _baseHarmonic = E2Param.GetHarmonic(Week, Day, Hour);
            dday += Hour;
            return dday;
        }

        public double Spin()
        {
            return Math.PI * 2.0 *
                   ((Hour - 0.75) // daily motion
                    + _yrPart);    // annual motion
        }

        public double Slide()
        {
            return E2Param.GetTilt(_baseHarmonic) * Math.PI / 180.0;
        }

        public void SetTimeOfDay(int i)
        {
            switch (i)
            {
                case 0: Hour = 0.0; break;
                case 1:
                    Hour = 0.25;
                    SetHarmonic();
                    Hour = (1.0 - E2Param.GetDayLength(_baseHarmonic)) / 2.0;
                    break;
                case 2: Hour = 0.5; break;
                case 3:
                    Hour = 0.75;
                    SetHarmonic();
                    Hour = (1.0 + E2Param.GetDayLength(_baseHarmonic)) / 2.0;
                    break;
                case 4: Hour = 86399.0 / 86400.0; break;
            }
            SetTime();
        }

        public double GetOffset()
        {
            return LocatorPanel.R;
        }

        public double GetBearing()
        {
            return (Math.PI / 2.0) - LocatorPanel.Theta;
        }

        public void Adjust()
        {
            // In Java, this was holyDays.makeVisible(holyDays.getSelectedIndex());
            // In WPF, ListBox.ScrollIntoView(ListBox.SelectedItem) achieves similar.
            HolyDaysListBox.ScrollIntoView(HolyDaysListBox.SelectedItem);
        }

        public bool GetUp()
        {
            return LookAtPanel.Up;
        }

        public double GetLook()
        {
            return LookAtPanel.Bearing;
        }

        public void SlideEvent(Slider s)
        {
            // This method explicitly implements ISlideListener.SlideEvent
            // It calls the internal event handler logic.
            OnSlideEvent(s, new SlideEventArgs(s));
        }

        public void OnSlideEvent(object sender, SlideEventArgs e)
        {
            if (_ticking) return;
            // seconds = 0; // Not directly controlled by slider in WPF
            SetLabel();
        }

        private void HolyDaysListBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (_ticking) return;
            if (HolyDaysListBox.SelectedIndex >= 0)
            {
                int day = _dates[HolyDaysListBox.SelectedIndex] - 1;
                DowBar.Value = day % 7;
                WkBar.Value = day / 7;
                SetLabel();
            }
        }

        private void TickerButton_Click(object sender, RoutedEventArgs e)
        {
            _ticking = !_ticking;
            if (_ticking)
            {
                TickerButton.Content = "Stop";
                _timer.Start();
            }
            else
            {
                TickerButton.Content = "Start";
                _timer.Stop();
            }
        }

        private void FreehandButton_Click(object sender, RoutedEventArgs e)
        {
            E2TimePicker pick = new E2TimePicker(Window.GetWindow(this), _freeDelta);
            if (pick.ShowDialog() == true)
            {
                _freeDelta = pick.GetDelta();
                CbFree.IsChecked = true;
            }
        }

        private void StepInterval_Checked(object sender, RoutedEventArgs e)
        {
            // This method is called when any radio button in the StepInterval group is checked.
            // The actual delta value is set in the Tick() method based on the checked radio button.
        }

        private void TimeFormat_Checked(object sender, RoutedEventArgs e)
        {
            SetLocal();
        }
    }
}
