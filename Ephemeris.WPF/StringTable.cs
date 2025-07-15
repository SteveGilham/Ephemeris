namespace Ephemeris.WPF
{
    public static class StringTable
    {
        // BasicFrame
        public const string FILE = "File";
        public const string HELP = "Help";
        public const string EXIT = "Exit";
        public const string ABOUT = "About";
        public const string CLOSE = "Close";
        public const string LICTEXT = "License text";

        // replace a %S with another string
        public static string Substitute(string whole, string part)
        {
            int i = whole.IndexOf("%S");
            if (i == -1) return whole;
            else if (i == 0) return part + whole.Substring(2);
            else return whole.Substring(0, i) + part + whole.Substring(i + 2);
        }
    }
}