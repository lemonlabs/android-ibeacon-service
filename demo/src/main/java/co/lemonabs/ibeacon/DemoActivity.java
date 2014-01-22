package co.lemonabs.ibeacon;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import java.util.Collection;

public class DemoActivity extends Activity implements IBeaconConsumer {

    private static final String TAG = "IBeaconDemo";

    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        iBeaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iBeaconManager.unBind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iBeaconManager.isBound(this)) iBeaconManager.setBackgroundMode(this, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIBeaconServiceConnect() {
        Log.d(TAG, "connected");

        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                if (iBeacons.size() > 0) {
                    int id = 0;
                    for (IBeacon beacon : iBeacons) {
                        Log.d(TAG, id + " " + beacon.getMinor() + "-" + beacon.getMajor() + " beacon is " + beacon.getAccuracy() + " meters away.");
                        id++;
                    }
                }
            }

        });

        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.d(TAG, "enter");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d(TAG, "exit");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d(TAG, "state " + state);
            }
        });

        try {
            iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            iBeaconManager.startMonitoringBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }
}
