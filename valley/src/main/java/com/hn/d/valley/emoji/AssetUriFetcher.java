package com.hn.d.valley.emoji;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * A DataFetcher that uses the {@link AssetManager} to load data from a {@link Uri}
 * pointing to a local resource.
 *
 * The Uri can either be a relative path to the file within the asset, like "images/01.jpg", or a full asset Uri
 * such as "file://android_asset/images/01.jpg"
 */
public class AssetUriFetcher implements DataFetcher<InputStream> {

    private static final String TAG = "AssetUriFetcher";
    private final WeakReference<Context> contextRef;
    private final Uri uri;
    private InputStream data;

    /**
     * Opens an input stream for a uri pointing to a local asset. Only certain uris are supported
     *
     * @param context A context (this will be weakly referenced and the load will fail if the weak reference
     *                is cleared before {@link #loadData(Priority)}} is called.
     * @param uri     A Uri pointing to a local asset. This load will fail if the uri isn't openable by
     *                {@link ContentResolver#openInputStream(Uri)}
     * @see ContentResolver#openInputStream(Uri)
     */
    public AssetUriFetcher(Context context, Uri uri) {
        contextRef = new WeakReference<>(context);
        this.uri = uri;
    }

    @Override
    public final InputStream loadData(Priority priority) throws Exception {
        Context context = contextRef.get();
        if (context == null) {
            throw new NullPointerException("Context has been cleared in AssetUriFetcher uri: " + uri);
        }

        String relativePath = uri.toString();
        relativePath = relativePath.replace("file:///android_asset/", "");

        AssetManager am = context.getAssets();
        data = am.open(relativePath);
        return data;
    }

    @Override
    public void cleanup() {
        if (data != null) {
            try {
                data.close();
            } catch (IOException e) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "failed to close data", e);
                }
            }
        }
    }

    @Override
    public void cancel() {
        // Do nothing.
    }

    @Override
    public String getId() {
        return uri.toString();
    }
}