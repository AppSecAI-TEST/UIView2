package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angcyo.library.facebook.RFresco;
import com.angcyo.uiview.utils.media.BitmapDecoder;
import com.angcyo.uiview.utils.media.ImageUtil;
import com.angcyo.uiview.utils.string.StringUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.AttachmentProgress;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;

import static com.hn.d.valley.main.message.ChatAdapter.setImageSize;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderVideo extends MsgViewHolderBase {

    protected View progressCover;
    protected TextView progressLabel;
    protected ProgressBar progressBar;

    SimpleDraweeView draweeView;

    private float lastPercent;


    private AbortableFuture downloadFuture;
    private boolean downloading;

    public MsgViewHolderVideo(BaseMultiAdapter adapter) {
        super(adapter);
    }


    @Override
    protected int getContentResId() {
        return R.layout.msg_video_layout;
    }

    @Override
    protected void inflateContentView() {

        progressBar = (ProgressBar) findViewById(R.id.msg_item_thumb_progress_bar); // 覆盖掉
        progressCover = findViewById(R.id.msg_item_thumb_progress_cover);
        progressLabel = (TextView) findViewById(R.id.msg_item_thumb_progress_text);
        draweeView = (SimpleDraweeView) findViewById(R.id.msg_image_view);
    }

    @Override
    protected void bindContentView() {
        final ImageView clickView = (ImageView) findViewById(R.id.click_view);

        final VideoAttachment msgAttachment = (VideoAttachment) message.getAttachment();
        final String path = msgAttachment.getPath();
        final String thumbPath = msgAttachment.getThumbPath();

        contentContainer.setBackgroundResource(0);

        if (!TextUtils.isEmpty(thumbPath)) {
            loadThumbnailImage(thumbPath);
        } else if (!TextUtils.isEmpty(path)) {
            loadThumbnailImage(thumbFromSourceFile(path));
        } else {
            loadThumbnailImage(null);
            if (message.getAttachStatus() == AttachStatusEnum.transferred
                    || message.getAttachStatus() == AttachStatusEnum.def) {
                downloadAttachment(message);
            }
        }
//        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(path)) {
//            downloadAttachment(message);
//        } else {
//            if (!TextUtils.isEmpty(thumbPath)) {
//                setImageSize(draweeView, thumbPath);
//            }
//        }
//
//        String thumb = msgAttachment.getThumbPathForSave();
//        String s = BitmapDecoder.extractThumbnail(msgAttachment.getPath(), thumb) ? thumb : thumb;
//        setImageSize(draweeView, s);
//        if (isReceivedMessage(message)) {
//            clickView.setImageResource(R.drawable.bubble_box_left_selector2);
//        } else {
//            clickView.setImageResource(R.drawable.bubble_box_right_selector2);
//        }
//        Glide.with(context)
//                .load(s)
//                .placeholder(com.angcyo.uiview.R.drawable.default_image)
//                .error(com.angcyo.uiview.R.drawable.default_image)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .centerCrop()
//                .into(draweeView);

//        String url = "/storage/emulated/0/com.hn.d.valley/nim/image/a74c10e3bbcf5b4313a56de822535814.png";

        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReceivedMessage()) {
                    download(message);
                } else {
//                    mUIBaseView.startIView(new VideoPlayUIView(((VideoAttachment) message.getAttachment()).getPath()
//                            , draweeView.getDrawable(), new int[]{msgAttachment.getWidth(),msgAttachment.getHeight()}));
                    download(message);
                }

            }
        });

        refreshStatus();

    }

    private void loadThumbnailImage(String thumbPath) {
        setImageSize(draweeView,thumbPath);
        if (thumbPath != null) {
            if (isReceivedMessage()) {
                RFresco.mask(context, draweeView, R.drawable.bubble_box_left, thumbPath, true);
            }else {
                RFresco.mask(context,draweeView,R.drawable.bubble_box_right_n2,thumbPath,true);
            }

//            Glide.with(context)
//                    .load(thumbPath)
//                    .placeholder(com.angcyo.uiview.R.drawable.default_image)
//                    .error(com.angcyo.uiview.R.drawable.default_image)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .centerCrop()
//                    .into(draweeView);

        } else {
//            RFresco.mask(context, draweeView, R.drawable.bubble_box_right_n2, thumbPath, true);
        }
    }

    private String thumbFromSourceFile(String path) {
        VideoAttachment attachment = (VideoAttachment) message.getAttachment();
        String thumb = attachment.getThumbPathForSave();
        return BitmapDecoder.extractThumbnail(path, thumb) ? thumb : null;
    }

    private void download(IMMessage message) {

        if (!isVideoHasDownloaded(message)) {
            // async download original image
            onDownloadStart(message);
            downloadFuture = NIMClient.getService(MsgService.class).downloadAttachment(message, false);
            downloading = true;
            registerObservers(true);

        } else {
            onDownloadSuccess(message);
        }
    }

    private void onDownloadStart(IMMessage message) {
        showStatusView(View.VISIBLE);
        progressLabel.setText(((VideoAttachment) message.getAttachment()).getSize() + "");
    }

    private void showStatusView(int visible) {
        progressCover.setVisibility(visible);
        progressBar.setVisibility(visible);
        progressLabel.setVisibility(visible);
    }

    private boolean isVideoHasDownloaded(final IMMessage message) {
        if (message.getAttachStatus() == AttachStatusEnum.transferred &&
                !TextUtils.isEmpty(((VideoAttachment) message.getAttachment()).getPath())) {
            return true;
        }

        return false;
    }


    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeAttachmentProgress(attachmentProgressObserver, register);
    }

    private Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage msg) {
            if (!msg.isTheSame(message)) {
                return;
            }

            if (msg.getAttachStatus() == AttachStatusEnum.transferred && isVideoHasDownloaded(msg)) {
                onDownloadSuccess(msg);
            } else if (msg.getAttachStatus() == AttachStatusEnum.fail) {
                onDownloadFailed();
            }
        }
    };

    private void onDownloadFailed() {
        downloadFuture = null;
        showStatusView(View.GONE);
    }

    private void stopDownload() {
        if (downloadFuture != null) {
            downloadFuture.abort();
            downloadFuture = null;

            downloading = false;
        }
    }

    private void onDownloadSuccess(final IMMessage message) {
        downloadFuture = null;
        showStatusView(View.GONE);
        playVideo();
    }

    private void playVideo() {
        final VideoAttachment msgAttachment = (VideoAttachment) message.getAttachment();
        mUIBaseView.startIView(new VideoPlayUIView(((VideoAttachment) message.getAttachment()).getPath()
                , draweeView.getDrawable(), new int[]{msgAttachment.getWidth(),msgAttachment.getHeight()}));
    }

    private Observer<AttachmentProgress> attachmentProgressObserver = new Observer<AttachmentProgress>() {
        @Override
        public void onEvent(AttachmentProgress p) {

            if (!p.getUuid().equals(message.getUuid())) {
                return;
            }

            long total = p.getTotal();
            long progress = p.getTransferred();
            float percent = (float) progress / (float) total;
            if (percent > 1.0) {
                // 消息中标识的文件大小有误，小于实际大小
                percent = (float) 1.0;
                progress = total;
            }
            if (percent - lastPercent >= 0.10) {
                lastPercent = percent;
                progressLabel.setText(StringUtil.getPercentString(progress / total));
            } else {
                if (lastPercent == 0.0) {
                    lastPercent = percent;
                    progressLabel.setText(StringUtil.getPercentString(progress / total));
                }
                if (percent == 1.0 && lastPercent != 1.0) {
                    lastPercent = percent;
                    progressLabel.setText(StringUtil.getPercentString(progress / total));
                }
            }
        }
    };

    private void refreshStatus() {
        FileAttachment attachment = (FileAttachment) message.getAttachment();
        if (TextUtils.isEmpty(attachment.getPath()) && TextUtils.isEmpty(attachment.getThumbPath())) {
            if (message.getAttachStatus() == AttachStatusEnum.fail || message.getStatus() == MsgStatusEnum.fail) {
                statusFailView.setVisibility(View.VISIBLE);
            } else {
                statusFailView.setVisibility(View.GONE);
            }
        }

        if (message.getStatus() == MsgStatusEnum.sending
                || (isReceivedMessage() && message.getAttachStatus() == AttachStatusEnum.transferring)) {
            showStatusView(View.VISIBLE);
            progressLabel.setText(StringUtil.getPercentString(getMsgAdapter().getProgress(message)));
        } else {
           showStatusView(View.GONE);
        }
    }

    private void setImageSize(ImageView imageView,String thumbPath) {
        int[] bounds = null;
        if (thumbPath != null) {
            bounds = BitmapDecoder.decodeBound(new File(thumbPath));
        }
        if (bounds == null) {
            if (message.getMsgType() == MsgTypeEnum.image) {
                ImageAttachment attachment = (ImageAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            } else if (message.getMsgType() == MsgTypeEnum.video) {
                VideoAttachment attachment = (VideoAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            }
        }

        if (bounds != null) {
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1]);
            ViewGroup.LayoutParams maskParams = imageView.getLayoutParams();
            maskParams.width = imageSize.width;
            maskParams.height = imageSize.height;
            imageView.setLayoutParams(maskParams);
        }
    }
}
