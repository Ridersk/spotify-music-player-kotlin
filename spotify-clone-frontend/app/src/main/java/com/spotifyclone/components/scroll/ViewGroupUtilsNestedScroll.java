package com.spotifyclone.components.scroll;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

final class ViewGroupUtilsNestedScroll {
    private static final Matrix sMatrix = new Matrix();
    private static final RectF sRectF = new RectF();
    private static final Matrix sIdentity = new Matrix();
    private static final Rect sRect = new Rect();

    static boolean isPointInChildBounds(ViewGroup parent, View child, int x, int y) {
        getDescendantRect(parent, child, sRect);
        return sRect.contains(x, y);
    }

    private static void getDescendantRect(ViewGroup parent, View descendant, Rect out) {
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        offsetDescendantRect(parent, descendant, out);
    }

    private static void offsetDescendantRect(ViewGroup parent, View descendant, Rect rect) {
        sMatrix.set(sIdentity);
        offsetDescendantMatrix(parent, descendant, sMatrix);
        sRectF.set(rect);
        sMatrix.mapRect(sRectF);
        final int left = (int) (sRectF.left + 0.5f);
        final int top = (int) (sRectF.top + 0.5f);
        final int right = (int) (sRectF.right + 0.5f);
        final int bottom = (int) (sRectF.bottom + 0.5f);
        rect.set(left, top, right, bottom);
    }

    private static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
        final ViewParent parent = view.getParent();
        if (parent instanceof View && parent != target) {
            final View vp = (View) parent;
            offsetDescendantMatrix(target, vp, m);
            m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
        }
        m.preTranslate(view.getLeft(), view.getTop());
        if (!view.getMatrix().isIdentity()) {
            m.preConcat(view.getMatrix());
        }
    }

    private ViewGroupUtilsNestedScroll() {
    }
}
