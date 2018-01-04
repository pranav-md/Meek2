package com.meek;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by User on 01-Jan-18.
 */

public class BottomSheetHandling extends MapsActivity {
       void showBottomSheet() {

       }
    public static Animator createRevealWithDelay(View view, int centerX,int centerY, float startRadius, float endRadius) {
        Animator delayAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, startRadius);
        delayAnimator.setDuration(1000);
        Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(delayAnimator, revealAnimator);
        return set;
    }
}
