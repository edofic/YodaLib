/*
 * Copyright 2012 Andraz Bajt
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edofic.yodalib.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

/**
 * wrapper for location services
 * takes parent context and updates one registered handler
 * handler must then poll current best location
 * updates are preformed only if new location is worthy
 * can use last known location to init with filtering by age
 * <p/>
 * based heavily on google's sample
 */
public class ManagedLocator {
    public static final long ONE_MINUTE = 60000;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    private LocationListener locListener;
    private LocationManager locMgr;
    private Location currentBest;

    /**
     * sets up new location listener to update data/ui
     *
     * @param context      parent context, needed for gps service
     * @param handler      handler to notify(empty message), which will then get data via getCurrentBest
     * @param interval     time minimal change interval in milliseconds
     * @param useCoarse    request location from network
     * @param useFine      request fine location from gps
     * @param useOldToInit uses last known location if newer than specified ms as first value(also updates the handler). put 0 to disable
     */
    public ManagedLocator(Context context, final Handler handler, long interval, boolean useCoarse, boolean useFine, long useOldToInit) {
        locListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            /**
             * handle location changes
             * on good location update it sends an empty message to the registered handler
             * @param location
             */
            @Override
            public void onLocationChanged(Location location) {
                if (isBetterLocation(location, currentBest))
                    currentBest = location;
                handler.sendEmptyMessage(0);
            }
        };

        //register for actual location updates
        locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (useFine) {
            locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 0, locListener);
        }
        if (useCoarse) {
            locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 0, locListener);
        }

        //lets try and use any previous known location
        if (useOldToInit > 0) {
            Location lastKnownLocation;
            if (useFine) {
                lastKnownLocation = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnownLocation != null && System.currentTimeMillis() - lastKnownLocation.getTime() <= useOldToInit) {
                    locListener.onLocationChanged(lastKnownLocation);
                }
            }
            if (useCoarse) {
                lastKnownLocation = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null && System.currentTimeMillis() - lastKnownLocation.getTime() <= useOldToInit) {
                    locListener.onLocationChanged(lastKnownLocation);
                }
            }
        }
    }

    /**
     * stops all activity
     * after this call the object is useless
     */
    public void stop() {
        locMgr.removeUpdates(locListener);
    }

    /**
     * current best location
     * to be called from registered handler
     *
     * @return current best location
     */
    public Location getCurrentBest() {
        return currentBest;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (location == null) {
            //null is never good
            return false;
        }
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > 2 * ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -2 * ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
