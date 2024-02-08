namespace EphemerisIII

type Time =
  { Year: int
    Day: int
    Hour: int
    Minute: int
    Second: int }
  override this.ToString() =
    let season = 8
    let dow = Names.dow[this.Day % 7]
    let week = this.Day / 7

    let text =
      if (week < 5 * season) then
        Names.wos[week % season]
        + " week, "
        + Names.soy[week / season]
        + " season"
      else
        let w = week - 5 * season + 1
        "Sacred Week " + sprintf "%d" w

    sprintf
      "%02d:%02d:%02d    %s, %s %dST"
      this.Hour
      this.Minute
      this.Second
      dow
      text
      this.Year

type ModelTime() =

  static member val Now =
    { Year = 1600
      Day = 1
      Hour = 0
      Minute = 0
      Second = 0 } with get, set