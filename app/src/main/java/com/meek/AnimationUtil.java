package com.meek;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Property;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by User on 20-Jun-18.
 */

public class AnimationUtil {

    /**
     * Animates a marker from it's current position to the provided finalPosition
     *
     * @param marker        marker to animate
     * @param finalPosition the final position of the marker after the animation
     */
    public static void animateMarkerTo(final Marker marker, final LatLng finalPosition) {
        // Use the appropriate implementation per API Level
            animateMarkerToICS(marker, finalPosition);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static void animateMarkerToICS(Marker marker, LatLng finalPosition) {
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Linear();
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator
                .ofObject(marker, property, typeEvaluator, finalPosition);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * For other LatLngInterpolator interpolators, see https://gist.github.com/broady/6314689
     */
    interface LatLngInterpolator {

        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class Linear implements LatLngInterpolator {

            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;

                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
}