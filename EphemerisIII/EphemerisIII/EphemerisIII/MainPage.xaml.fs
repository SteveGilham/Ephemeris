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

    let license =
      this.FindName("License") :?> TextBlock

    license.Text <- License.LICENSE_TEXT

    let minBar =
      this.FindName("MinBar") :?> NumericUpDown

    let hrBar =
      this.FindName("HrBar") :?> NumericUpDown

    let dowBar =
      this.FindName("DoWBar") :?> NumericUpDown

    let wkBar =
      this.FindName("WkBar") :?> NumericUpDown

    let yrBar =
      this.FindName("YrBar") :?> NumericUpDown

    let centBar =
      this.FindName("CentBar") :?> NumericUpDown

    let statusBar =
      this.FindName("StatusBar") :?> Label

    let holyDays =
      this.FindName("HolyDays") :?> ComboBox

    let mutable suspend = false

    let round: float -> int = Math.Round >> int

    let toTime () : Time =
      { Year = round (yrBar.Value) + 100 * round (centBar.Value)
        Day = round (dowBar.Value) + 7 * round (wkBar.Value) - 1
        Hour = round (hrBar.Value)
        Minute = round (minBar.Value)
        Second = 0 }

    let update _ =
      ModelTime.Now <- toTime ()
      statusBar.Content <- ModelTime.Now.ToString()

      if not suspend then
        holyDays.SelectedIndex <-
          match
            List.tryFindIndex (fun x -> x = (1 + ModelTime.Now.Day)) Names.holyDates
          with
          | Some n -> n
          | _ -> 0

    minBar.ValueChanged.Add update
    hrBar.ValueChanged.Add update
    wkBar.ValueChanged.Add update
    dowBar.ValueChanged.Add update
    yrBar.ValueChanged.Add update
    centBar.ValueChanged.Add update

    holyDays.SelectionChanged.Add(fun _ ->
      if holyDays.SelectedIndex > 0 then
        let day =
          Names.holyDates[holyDays.SelectedIndex] - 1

        try
          suspend <- true
          wkBar.Value <- float (day / 7)
          dowBar.Value <- float (day % 7)
        finally
          suspend <- false)

    update ()