package com.hn.d.valley.main.avchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.hn.d.valley.R;
import com.hn.d.valley.activity.HnSplashActivity;
import com.hn.d.valley.activity.HnUIMainActivity;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.main.avchat.activity.AVChatActivity;


/**
 * 音视频聊天通知栏
 */
public class AVChatNotification {

    private Context context;

    private NotificationManager notificationManager;
    private Notification callingNotification;
    private Notification missCallNotification;
    private String account;
    private String displayName;
    private static final int CALLING_NOTIFY_ID = 111;
    private static final int MISS_CALL_NOTIFY_ID = 112;

    public AVChatNotification(Context context) {
        this.context = context;
    }

    public void init(String account) {
        this.account = account;
        this.displayName = NimUserInfoCache.getInstance().getUserDisplayName(account);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void buildCallingNotification() {
        if (callingNotification == null) {
            Intent localIntent = new Intent();
            localIntent.setClass(context, AVChatActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            String tickerText = String.format(context.getString(R.string.avchat_notification), displayName);
            int iconId = R.drawable.love_icon_2_n;

            PendingIntent pendingIntent = PendingIntent.getActivity(context, CALLING_NOTIFY_ID, localIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);
            callingNotification = makeNotification(pendingIntent, context.getString(R.string.avchat_call), tickerText, tickerText,
                    iconId, false, false);
        }
    }

    private void buildMissCallNotification() {
        if (missCallNotification == null) {
            Intent notifyIntent = new Intent(context, HnSplashActivity.class);
            Intent data = new Intent(context, HnUIMainActivity.class);
            data.putExtra(Extras.EXTRA_ACCOUNT, account);
            data.putExtra(Extras.EXTRA_FROM, Extras.EXTRA_FROM_NOTIFICATION);
            notifyIntent.putExtra(Extras.EXTRA_DATA, data);
            notifyIntent.putExtra(Extras.EXTRA_JUMP_P2P, true);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notifyIntent.setAction(Intent.ACTION_VIEW);
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, CALLING_NOTIFY_ID, notifyIntent, PendingIntent
                    .FLAG_UPDATE_CURRENT);

            String title = context.getString(R.string.avchat_no_pickup_call);
            String tickerText = NimUserInfoCache.getInstance().getUserDisplayName(account) + ": 【网络通话】";
            int iconId = R.drawable.love_icon_2_n;

            missCallNotification = makeNotification(pendingIntent, title, tickerText, tickerText, iconId, true, true);
        }
    }

    private Notification makeNotification(PendingIntent pendingIntent, String title, String content, String tickerText,
                                          int iconId, boolean ring, boolean vibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setTicker(tickerText)
                .setSmallIcon(iconId);
        int defaults = Notification.DEFAULT_LIGHTS;
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (ring) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        builder.setDefaults(defaults);

        return builder.build();
    }

    public void activeCallingNotification(boolean active) {
        if (notificationManager != null) {
            if (active) {
                buildCallingNotification();
                notificationManager.notify(CALLING_NOTIFY_ID, callingNotification);
            } else {
                notificationManager.cancel(CALLING_NOTIFY_ID);
            }
        }
    }

    public void activeMissCallNotification(boolean active) {
        if (notificationManager != null) {
            if (active) {
                buildMissCallNotification();
                notificationManager.notify(MISS_CALL_NOTIFY_ID, missCallNotification);
            } else {
                notificationManager.cancel(MISS_CALL_NOTIFY_ID);
            }
        }
    }

    public interface Extras {

        String EXTRA_JUMP_P2P = "EXTRA_JUMP_P2P";

        String EXTRA_DATA = "data";

        String EXTRA_FROM = "from";

        String EXTRA_FROM_NOTIFICATION = "from_notification";

        // 参数
        String EXTRA_ACCOUNT = "account";

    }
}
