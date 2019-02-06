package uk.me.desiderio.fabmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Custom ViewGroup that animates its children to show {@link FloatingActionButton} submenu
 * <p>
 * View defines two attributes:
 * - background colour for the screen showing when the menu is open
 * - button to open and close menu
 */
public class FloatingActionMenu extends LinearLayout implements View.OnClickListener {

    private static final String TAG = FloatingActionMenu.class.getSimpleName();

    private FloatingActionButton mainButton;
    private Animation closeAnimation;
    private Animation openAnimation;
    private List<View> views;

    private OnItemClickListener listener;

    private int mainButtonId;
    private int backgroundColour;

    private boolean isOpen;

    public FloatingActionMenu(@NonNull Context context) {
        super(context);
        initAttributes(context, null);
        initView();
    }

    public FloatingActionMenu(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        initView();
    }


    public FloatingActionMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
        initView();
    }

    public FloatingActionMenu(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs);
        initView();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void initView() {
        views = new ArrayList<>();
        setOrientation(VERTICAL);
        initAnimations();
    }

    private void initAttributes(@NonNull Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionMenu);
        mainButtonId = a.getResourceId(R.styleable.FloatingActionMenu_main_fab, 0);
        backgroundColour = a.getColor(R.styleable.FloatingActionMenu_background_colour,
                                      ContextCompat.getColor(getContext(),
                                                             R.color.fabDefaultBackground));

        if (mainButtonId == 0) {
            throw new IllegalArgumentException("Missing Resource: 'main_fab' has to be provided " +
                                                       "in the xml file pointing to button to act" +
                                                       " as main button");
        }

        a.recycle();
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        child.setOnClickListener(this);

        if (child.getId() == mainButtonId) {
            mainButton = (FloatingActionButton) child;
        } else {
            // hides view by running close animation
            child.startAnimation(closeAnimation);
            views.add(child);
        }
        Log.d(TAG, views.size() + "child added " + child.getId());
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        child.setOnClickListener(null);
        views.remove(child);
    }

    private boolean isSubMenuButton(View v) {
        return views.contains(v);
    }

    @Override
    public void onClick(View v) {
        if (shouldToggleMenuState(v)) {
            isOpen = !isOpen;

            if (isOpen) {
                setBackgroundColor(backgroundColour);
                openMenu();
            } else {
                setBackgroundColor(0);
                closeMenu();
            }
        }

        shoulPropagateClick(v);
    }

    /**
     * menu should:
     * - open when selecting main button
     * - close when selecting any of the items including outside the menu
     */
    private boolean shouldToggleMenuState(View v) {
        return v == mainButton || isOpen;
    }

    /**
     * only submenu button will propate click event
     */
    private void shoulPropagateClick(View v) {
        if (isSubMenuButton(v)) {
            if (listener != null) {
                listener.onFloatingMenuItemClick(v);
            }
        }
    }

    private void initAnimations() {
        openAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.menu_open);
        closeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.menu_close);
    }

    private void openMenu() {
        setOnClickListener(this);
        startMenuAnimation(openAnimation);
        shouldEnableMiniButtons(true);
    }

    private void closeMenu() {
        setOnClickListener(null);
        startMenuAnimation(closeAnimation);
        shouldEnableMiniButtons(false);
    }

    private void shouldEnableMiniButtons(boolean shouldEnable) {
        views.stream().forEach(view -> view.setEnabled(shouldEnable));
    }

    private void startMenuAnimation(Animation animation) {
        for (View view : views) {
            view.setAnimation(animation);
        }
        animation.start();
    }

    public interface OnItemClickListener {
        void onFloatingMenuItemClick(View v);
    }
}
