package com.hn.d.valley.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.angcyo.uiview.utils.media.BitmapDecoder;
import com.angcyo.uiview.utils.media.ImageUtil;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hn.d.valley.R;
import java.io.IOException;

public class MsgThumbImageView extends android.support.v7.widget.AppCompatImageView {

    private Drawable mask; // blend mask drawable

    public MsgThumbImageView(Context context) {
        super(context);
    }

    public MsgThumbImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MsgThumbImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private boolean hasLoaded = false;

    private static final Paint paintMask = createMaskPaint();

    private static final Paint createMaskPaint() {
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mask != null) {
            // bounds
            int width = getWidth();
            int height = getHeight();

            // create blend layer
            canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);

            //
            // mask
            //
            if (mask != null) {
                mask.setBounds(0, 0, width, height);
                mask.draw(canvas);
            }

            //
            // source
            //
            {
                canvas.saveLayer(0, 0, width, height, paintMask, Canvas.ALL_SAVE_FLAG);
                super.onDraw(canvas);
                canvas.restore();
            }

            // apply blend layer
            canvas.restore();
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    public void loadAsPath(String pathName, int width, int height, int maskId) {
        setBlendDrawable(maskId);
        setImageBitmap(BitmapDecoder.decodeSampled(pathName, width, height));
    }

    public void loadAsPath(boolean isOriginal, final String path, final String tag, final int width, final int height, final int maskId) {
        if (TextUtils.isEmpty(path)) {
            setTag(null);
            loadAsResource(R.drawable.default_image, maskId);
            return;
        }

        if (!isOriginal || getTag() == null || !getTag().equals(tag)) {
            hasLoaded = false; // 由于ViewHolder复用，使得tag发生变化，必须重新加载
        }
        setTag(tag); // 解决ViewHolder复用问题

        // async load
        if (!hasLoaded) {
            // load default image first
            loadAsResource(R.drawable.default_image, maskId);
            final ImageUtil.ImageSize imageSize = new ImageUtil.ImageSize(width, height);

            BitmapTypeRequest<String> builder = Glide.with(getContext())
                    .load(path)
                    .asBitmap();
            if (getDrawable() != null) {
                builder.placeholder(getDrawable());
            }
            builder.into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (getTag() != null && getTag().equals(tag) && !hasLoaded) {
                        setImageBitmap(resource);
                        hasLoaded = true;
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    // 视频缩略图后缀.mp4等导致ImageLoader解码失败
                    loadBmpAsync(path, path, imageSize, tag);
                }
            });
        }
    }

    private void loadBmpAsync(final String imageUri, final String path, final ImageUtil.ImageSize imageSize, final String tag) {

        new AsyncTask<String, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String[] params) {
                return BitmapDecoder.decodeSampled(path, imageSize.width, imageSize.height);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null && (getTag() != null && getTag().equals(tag) && !hasLoaded)) {
                    setImageBitmap(bitmap);
                    hasLoaded = true;
                }
            }
        }.execute();

    }

    private Bitmap decodeBmpAndSave(String path, ImageUtil.ImageSize imageSize, String imageUri) {
        Bitmap bitmap = BitmapDecoder.decodeSampled(path, imageSize.width, imageSize.height);
        return bitmap;
    }

    public void loadAsResource(int resId, int maskId) {
        setBlendDrawable(maskId);
        setImageResource(resId);
    }

    private void setBlendDrawable(int maskId) {
        mask = maskId != 0 ? getResources().getDrawable(maskId) : null;
    }
}
