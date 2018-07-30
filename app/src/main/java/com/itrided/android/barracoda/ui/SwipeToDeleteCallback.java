package com.itrided.android.barracoda.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.itrided.android.barracoda.R;

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final Drawable deleteIcon;
    private final int intrinsicWidth;
    private final int intrinsicHeight;
    private final ColorDrawable background;
    private final int backgroundColor;
    private final Paint clearPaint;

    public int getMovementFlags(@Nullable RecyclerView recyclerView, @Nullable RecyclerView.ViewHolder viewHolder) {
        if (viewHolder != null) {
            if (viewHolder.getAdapterPosition() == 10) {
                return 0;
            }
        }

        return super.getMovementFlags(recyclerView, viewHolder);
    }

    public boolean onMove(@Nullable RecyclerView recyclerView, @Nullable RecyclerView.ViewHolder viewHolder,
                          @Nullable RecyclerView.ViewHolder target) {
        return false;
    }

    public void onChildDraw(@Nullable Canvas c, @Nullable RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View itemView = viewHolder.itemView;
        final int itemHeight = itemView.getBottom() - itemView.getTop();
        final boolean isCanceled = dX == 0.0F && !isCurrentlyActive;

        if (isCanceled) {
            this.clearCanvas(c, (float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else {
            // Draw the delete background
            this.background.setColor(this.backgroundColor);
            this.background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            this.background.draw(c);

            // Calculate position of delete icon
            final int deleteIconTop = itemView.getTop() + (itemHeight - this.intrinsicHeight) / 2;
            final int deleteIconMargin = (itemHeight - this.intrinsicHeight) / 2;
            final int deleteIconLeft = itemView.getRight() - deleteIconMargin - this.intrinsicWidth;
            final int deleteIconRight = itemView.getRight() - deleteIconMargin;
            final int deleteIconBottom = deleteIconTop + this.intrinsicHeight;

            this.deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            this.deleteIcon.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private final void clearCanvas(Canvas c, float left, float top, float right, float bottom) {
        if (c != null) {
            c.drawRect(left, top, right, bottom, this.clearPaint);
        }

    }

    public SwipeToDeleteCallback(@NonNull Context context) {
        super(0, ItemTouchHelper.LEFT);
        final Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        this.clearPaint = clearPaint;
        this.deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        this.intrinsicWidth = deleteIcon.getIntrinsicWidth();
        this.intrinsicHeight = deleteIcon.getIntrinsicHeight();
        this.background = new ColorDrawable();
        this.backgroundColor = ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryLight, context.getTheme());
    }
}
