namespace EphemerisIII

open System
open System.Collections.Generic
open System.IO
open System.Linq
open System.Windows
open System.Windows.Controls

type MainPage() as this =
    inherit MainPageXaml()

    do
        this.InitializeComponent()

        let license = this.FindName("License") :?> TextBlock
        license.Text <- License.LICENSE_TEXT
