$csv | % { 
	"  <circle x=`"$($_.x)`" y=`"$($_.y)`" r=`"$($_.size)`" fill=`"$($_.color)`" transform=`"translate($(3200+$_.x) $(3200+$_.y))`"/>" 
        "  <text x=`"$($_.x)`" y=`"$($_.y)`" fill=`"silver`" stroke=`"black`"  transform=`"translate(3200 3200)`">$($_.label)</text>"	
        "  <text x=`"$($_.x)`" y=`"$($_.y)`" fill=`"yellow`" stroke=`"black`"  transform=`"translate(3200 3200)`">$($_.name)</text>"	
	} | Set-Content StarDome.svg