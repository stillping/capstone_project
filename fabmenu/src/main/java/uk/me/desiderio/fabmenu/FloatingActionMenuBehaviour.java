package uk.me.desiderio.fabmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class FloatingActionMenuBehaviour extends CoordinatorLayout.Behavior<FloatingActionMenu> {

    public FloatingActionMenuBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull FloatingActionMenu child, @NonNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FloatingActionMenu child, @NonNull View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY());
        child.setTranslationY(translationY);
        return false;
    }
}
