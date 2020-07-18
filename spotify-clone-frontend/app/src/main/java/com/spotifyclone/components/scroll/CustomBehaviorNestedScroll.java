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
import com.spotifyclone.tools.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

public class CustomBehaviorNestedScroll extends CoordinatorLayout.Behavior<NestedScrollView> {

    public static final int MARGIN_TO_LIMIT = 50;

    public CustomBehaviorNestedScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(
            @NotNull CoordinatorLayout parent, @NotNull NestedScrollView child, View dependency) {
        return dependency.getId() == R.id.toolbarContainer;
    }

    @Override
    public boolean onLayoutChild(
            CoordinatorLayout parent, @NotNull NestedScrollView child, int layoutDirection) {
        final View titlePanel = parent.findViewById(R.id.titlePanel);
        final View cardContainer = child.findViewById(R.id.cardContainer);
        final View cardView = child.findViewById(R.id.cardView);
        final View btnFloat = child.findViewById(R.id.btnFloat);
        final MaxHeightRecyclerView recyclerView = child.findViewById(R.id.recyclerList);
        final int toolbarContainerHeight = parent.getDependencies(child).get(0).getHeight();
        final int fabHalfHeight = btnFloat.getHeight() / 2;
        final int rvMaxHeight = child.getHeight() - fabHalfHeight;
        final int titlePanelBottomPos = titlePanel.getBottom();

        parent.onLayoutChild(child, layoutDirection);
        recyclerView.setMaxHeight(rvMaxHeight);
        ViewUtils.Companion.setTopMargin(titlePanel, toolbarContainerHeight);
        ViewUtils.Companion.setPaddingTop(cardContainer, titlePanelBottomPos - toolbarContainerHeight);
        ViewUtils.Companion.setTopMargin(cardView, fabHalfHeight);
        ViewUtils.Companion.setPaddingBottom(recyclerView, toolbarContainerHeight + MARGIN_TO_LIMIT);
        ViewCompat.offsetTopAndBottom(btnFloat, toolbarContainerHeight + MARGIN_TO_LIMIT);
        ViewCompat.offsetTopAndBottom(cardView, toolbarContainerHeight + MARGIN_TO_LIMIT);
        return true;
    }

    public static CustomBehaviorNestedScroll getBehavior(@NotNull NestedScrollView child) {
        if (child.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams param = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            CoordinatorLayout.Behavior behavior = param.getBehavior();
            if (behavior instanceof CustomBehaviorNestedScroll) {
                return (CustomBehaviorNestedScroll) behavior;
            }
        }
        return null;
    }

    public void refresh(@NotNull NestedScrollView child) {
        if (child.getParent() instanceof CoordinatorLayout) {
            refresh((CoordinatorLayout) child.getParent(), child);
        }
    }

    private void refresh(@NotNull CoordinatorLayout parent, @NotNull NestedScrollView child) {
        // Work around to force recalculate the layouts margin and tops
        final View titlePanel = parent.findViewById(R.id.titlePanel);
        final View cardContainer = child.findViewById(R.id.cardContainer);
        final View cardView = child.findViewById(R.id.cardView);
        final View btnFloat = child.findViewById(R.id.btnFloat);
        final MaxHeightRecyclerView recyclerView = child.findViewById(R.id.recyclerList);

        ViewUtils.Companion.

                setTopMargin(titlePanel, 0);
        ViewUtils.Companion.setPaddingTop(cardContainer, 0);
        ViewUtils.Companion.setTopMargin(cardView, 0);
        ViewUtils.Companion.setPaddingBottom(recyclerView, 0);
        ViewCompat.offsetTopAndBottom(btnFloat, 0);
        ViewCompat.offsetTopAndBottom(cardView, 0);
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
