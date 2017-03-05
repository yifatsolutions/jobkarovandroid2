package karov.shemi.oz;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class locLis implements LocationListener {
	public void onLocationChanged(Location location) {
		// Called when a new location is found by the network location provider.
		if (location != null) {
			//currentlocation=location;
		}
	}

    public void onStatusChanged(String provider, int status, Bundle extras) {}

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }
}
