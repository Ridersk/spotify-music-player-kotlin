package com.spotifyclone.components.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.spotifyclone.R;

import org.jetbrains.annotations.NotNull;

class CustomBehaviorNestedScroll extends CoordinatorLayout.Behavior<NestedScrollView> {

    @Override
    public boolean layoutDependsOn(
            @NotNull CoordinatorLayout parent, @NotNull NestedScrollView child, View dependency) {
        return dependency.getId() == R.id.toolbarContainer;
    }

    public CustomBehaviorNestedScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(
            CoordinatorLayout parent, @NotNull NestedScrollView child, int layoutDirection) {
        final View titlePanel = parent.findViewById(R.id.titlePanel);
        final View cardContainer = child.findViewById(R.id.cardContainer);
        final View cardView = child.findViewById(R.id.cardView);
        final MaxHeightRecyclerView rv = child.findViewById(R.id.recyclerList);
        final int toolbarContainerHeight = parent.getDependencies(child).get(0).getHeight();
        final int fabHalfHeight = child.findViewById(R.id.btnFloat).getHeight() / 2;
        final int rvMaxHeight = child.getHeight() - fabHalfHeight;
        final int titlePanelBottomPos = titlePanel.getBottom();

        parent.onLayoutChild(child, layoutDirection);
        rv.setMaxHeight(rvMaxHeight);
        setTopMargin(titlePanel, toolbarContainerHeight);
        setPaddingTop(cardContainer,  titlePanelBottomPos - toolbarContainerHeight);
        setTopMargin(cardView, fabHalfHeight);
        ViewCompat.offsetTopAndBottom(child, toolbarContainerHeight);
        setPaddingBottom(rv, toolbarContainerHeight);
        return true;
    }

    private static void setTopMargin(View v, int topMargin) {
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        if (lp.topMargin != topMargin) {
            lp.topMargin = topMargin;
            v.setLayoutParams(lp);
        }
    }

    private static void setPaddingTop(View v, int top) {
        if (v.getPaddingTop() != top) {
            v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
        }
    }

    private static void setPaddingBottom(View v, int bottom) {
        if (v.getPaddingBottom() != bottom) {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(
            @NotNull CoordinatorLayout parent, @NotNull NestedScrollView child, MotionEvent ev) {
        return ev.getActionMasked() == MotionEvent.ACTION_DOWN
                && isTouchInChildBounds(parent, child, ev)
                && !isTouchInChildBounds(parent, child.findViewById(R.id.cardView), ev)
                && !isTouchInChildBounds(parent, child.findViewById(R.id.btnFloat), ev);
    }

    private static boolean isTouchInChildBounds(
            ViewGroup parent, View child, MotionEvent ev) {
        return ViewGroupUtilsNestedScroll.isPointInChildBounds(
                parent, child, (int) ev.getX(), (int) ev.getY());
    }
}
