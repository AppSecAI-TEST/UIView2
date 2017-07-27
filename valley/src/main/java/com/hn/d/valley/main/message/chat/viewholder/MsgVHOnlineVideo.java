package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.media.ImageUtil;
import com.angcyo.uiview.widget.RImageView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.main.message.attachment.OnlineVideoForwardAttachment;
import com.hn.d.valley.main.message.attachment.OnlineVideoForwardMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgUIObserver;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.widget.MsgThumbImageView;
import com.netease.nimlib.sdk.AbortableFuture;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHOnlineVideo extends MsgViewHolderBase implements MsgUIObserver{

    protected View progressCover;
    protected TextView progressLabel;
    protected ProgressBar progressBar;
    protected ImageView clickView;

    MsgThumbImageView draweeView;

    private float lastPercent;

    //view state
    private boolean hasOnShow;

    private AbortableFuture downloadFuture;
    private boolean downloading;

    public MsgVHOnlineVideo(BaseMultiAdapter adapter) {
        super(adapter);
        getMsgAdapter().registerUIObserver(this);
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
        draweeView = (MsgThumbImageView) findViewById(R.id.msg_image_view);
    }

    @Override
    protected void bindContentView() {
        clickView = (ImageView) findViewById(R.id.click_view);

        OnlineVideoForwardAttachment msgAttachment = (OnlineVideoForwardAttachment) message.getAttachment();
        OnlineVideoForwardMsg msg = msgAttachment.getVideoForwardMsg();
        final String path = msg.getVideoURL();
        final String thumbPath = msg.getCover();

        if (!TextUtils.isEmpty(thumbPath)) {
            loadThumbnailImage(thumbPath,true);
        } else {
            Log.e("receive"," no image");
            loadThumbnailImage(null,false);
        }

        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new VideoPlayUIView(((OnlineVideoForwardAttachment) message.getAttachment()).getVideoForwardMsg().getVideoURL()
                        , RImageView.copyDrawable(draweeView), new int[]{(int) (250 * ScreenUtil.density), (int) (150 * ScreenUtil.density)}));
            }
        });

    }

    private void loadThumbnailImage(String thumbPath,boolean isOriginal) {
        L.i(thumbPath);
        if (!TextUtils.isEmpty(thumbPath)) {
            if (isReceivedMessage()) {
                draweeView.loadAsPath(isOriginal,thumbPath,message.getUuid(), ImageUtil.getImageMaxEdge(),ImageUtil.getImageMaxEdge(),R.drawable.nim_message_item_round_bg);
            }else {
                draweeView.loadAsPath(isOriginal,thumbPath,message.getUuid(), ImageUtil.getImageMaxEdge(),ImageUtil.getImageMaxEdge(),R.drawable.nim_message_item_round_bg);
            }
        }
    }


    private void showStatusView(int visible) {
        progressCover.setVisibility(visible);
        progressBar.setVisibility(visible);
        progressLabel.setVisibility(visible);
        clickView.setVisibility(View.VISIBLE == visible ? View.GONE : View.VISIBLE);
    }


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

    private void playVideo() {
//        final VideoAttachment msgAttachment = (VideoAttachment) message.getAttachment();
//        mUIBaseView.startIView(new VideoPlayUIView(((VideoAttachment) message.getAttachment()).getPath()
//                , RImageView.copyDrawable(draweeView), new int[]{msgAttachment.getWidth(),msgAttachment.getHeight()}));
    }


    private void setImageSize(ImageView imageView,String thumbPath) {
//        int[] bounds = null;
//        if (thumbPath != null) {
//            bounds = BitmapDecoder.decodeBound(new File(thumbPath));
//        }
//        if (bounds == null) {
//            if (message.getMsgType() == MsgTypeEnum.image) {
//                ImageAttachment attachment = (ImageAttachment) message.getAttachment();
//                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
//            } else if (message.getMsgType() == MsgTypeEnum.video) {
//                VideoAttachment attachment = (VideoAttachment) message.getAttachment();
//                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
//            }
//        }
//
//        if (bounds != null) {
//            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1]);
//            ViewGroup.LayoutParams maskParams = imageView.getLayoutParams();
//            maskParams.width = imageSize.width;
//            maskParams.height = imageSize.height;
//            imageView.setLayoutParams(maskParams);
//        }
    }

    @Override
    public void onViewHide() {
//        stopDownload();
        hasOnShow = false;
        L.d("msgviewholdervideo onViewHide hide stopdownload " + hasOnShow);
    }

    @Override
    public void onViewShow() {
        hasOnShow = true;
        L.d("msgviewholdervideo onViewShow hide stopdownload " + hasOnShow);
    }
}
