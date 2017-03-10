package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/3/9.
 */
public class MultiImageView extends ImageView {

    public MultiImageView(Context context) {
        super(context);
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Shape shape = Shape.NONE;


    int rectCorners = 100;

    private List<Bitmap> bitmaps = new ArrayList<>();
    private Path path = new Path();
    private RectF rect = new RectF();
    private MultiDrawable multiDrawable;

    public void addImage(Bitmap  bitmap) {
        bitmaps.add(bitmap);
        refresh();
    }

    public void clear() {
        bitmaps.clear();
        refresh();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refresh();
    }

    private void refresh(){
        multiDrawable = new MultiDrawable(bitmaps);
        setImageDrawable(multiDrawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas != null) {
            if(getDrawable() != null) {
                // if shape not set - just draw
                if (shape != Shape.NONE) {
                    path.reset();

                    rect.set(0f,0f,getWidth(),getHeight());
                    if (shape == Shape.RECTANGLE) {
                        path.addRoundRect(rect,rectCorners,rectCorners,Path.Direction.CW);
                    } else {
                        path.addOval(rect,Path.Direction.CW);
                    }

                    // clip with shape
                    canvas.clipPath(path);
                }
                super.onDraw(canvas);
            }
        }

    }

    //Types of shape
    enum  Shape {
        CIRCLE, RECTANGLE, NONE
    }

    class MultiDrawable extends Drawable{
        MultiDrawable(List<Bitmap> bitmaps) {

        }

        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private ArrayList<PhotoItem> items;

        private void init() {
            items.clear();
            Rect bounds = getBounds();
            if (bitmaps.size() == 1) {
                Bitmap bitmap = scaleCenterCrop(bitmaps.get(0),bounds.width(),bounds.height());
                items.add(new PhotoItem(bitmap,new Rect(0,0,getBounds().width(),getBounds().height())));
            } else if (bitmaps.size() == 2) {
                Bitmap bitmap1 = scaleCenterCrop(bitmaps.get(0),bounds.width(),bounds.height());
                Bitmap bitmap2 = scaleCenterCrop(bitmaps.get(1),bounds.width(),bounds.height());
                items.add(new PhotoItem(bitmap1,new Rect(0,0,bounds.width() / 2,bounds.height())));
                items.add(new PhotoItem(bitmap2,new Rect(bounds.width() / 2 , 0 , bounds.width(),bounds.height())));
            }else if (bitmaps.size() == 3) {
                Bitmap bitmap1 = scaleCenterCrop(bitmaps.get(0), bounds.width(), bounds.height() / 2);
                Bitmap bitmap2 = scaleCenterCrop(bitmaps.get(1), bounds.width() / 2, bounds.height() / 2);
                Bitmap bitmap3 = scaleCenterCrop(bitmaps.get(2), bounds.width() / 2, bounds.height() / 2);
                items.add(new PhotoItem(bitmap1, new Rect(0, 0, bounds.width() / 2, bounds.height())));
                items.add(new PhotoItem(bitmap2, new Rect(bounds.width() / 2, 0, bounds.width(), bounds.height() / 2)));
                items.add(new PhotoItem(bitmap3, new Rect(bounds.width() / 2, bounds.height() / 2, bounds.width(), bounds.height())));
            }
            if (bitmaps.size() == 4) {
                Bitmap bitmap1 = scaleCenterCrop(bitmaps.get(0), bounds.width() / 2, bounds.height() / 2);
                Bitmap bitmap2 = scaleCenterCrop(bitmaps.get(1), bounds.width() / 2, bounds.height() / 2);
                Bitmap bitmap3 = scaleCenterCrop(bitmaps.get(2), bounds.width() / 2, bounds.height() / 2);
                Bitmap bitmap4 = scaleCenterCrop(bitmaps.get(3), bounds.width() / 2, bounds.height() / 2);
                items.add(new PhotoItem(bitmap1, new Rect(0, 0, bounds.width() / 2, bounds.height() / 2)));
                items.add(new PhotoItem(bitmap2, new Rect(0, bounds.height() / 2, bounds.width() / 2, bounds.height())));
                items.add(new PhotoItem(bitmap3, new Rect(bounds.width() / 2, 0, bounds.width(), bounds.height() / 2)));
                items.add(new PhotoItem(bitmap4, new Rect(bounds.width() / 2, bounds.height() / 2, bounds.width(), bounds.height())));
            }
        }

        private Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
            return ThumbnailUtils.extractThumbnail(source,newWidth,newHeight);
        }


        @Override
        public void draw(Canvas canvas) {
            if (canvas != null) {
                for(PhotoItem item : items) {
                    canvas.drawBitmap(item.bitmap,getBounds(),item.position,paint);
                }
            }
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            init();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }


}



    class PhotoItem{
        Rect position;
        Bitmap bitmap;

        public PhotoItem( Bitmap bitmap,Rect position) {
            this.position = position;
            this.bitmap = bitmap;
        }
    }

