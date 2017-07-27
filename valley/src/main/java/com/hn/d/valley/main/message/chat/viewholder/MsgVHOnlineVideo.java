package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

    //view state
    private boolean hasOnShow;

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
            loadThumbnailImage(thumbPath,true,msg);
        } else {
            Log.e("receive"," no image");
            loadThumbnailImage(null,false,msg);
        }

        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new VideoPlayUIView(thumbPath,path));
            }
        });

    }

    private void loadThumbnailImage(String thumbPath,boolean isOriginal,OnlineVideoForwardMsg videoForwardMsg) {
        L.i(thumbPath);
        setImageSize(draweeView,videoForwardMsg);
        if (!TextUtils.isEmpty(thumbPath)) {
            if (isReceivedMessage()) {
                draweeView.loadAsPath(isOriginal,thumbPath,message.getUuid(), ImageUtil.getImageMaxEdge(),ImageUtil.getImageMaxEdge(),R.drawable.nim_message_item_round_bg);
            }else {
                draweeView.loadAsPath(isOriginal,thumbPath,message.getUuid(), ImageUtil.getImageMaxEdge(),ImageUtil.getImageMaxEdge(),R.drawable.nim_message_item_round_bg);
            }
        }
    }


    private void setImageSize(ImageView imageView,OnlineVideoForwardMsg msg) {
        int[] bounds = {msg.getWidth(),msg.getHeight()};
        if (bounds != null) {
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1]);
            ViewGroup.LayoutParams maskParams = imageView.getLayoutParams();
            maskParams.width = imageSize.width;
            maskParams.height = imageSize.height;
            imageView.setLayoutParams(maskParams);
        }
    }

    @Override
    public void onViewHide() {
        hasOnShow = false;
    }

    @Override
    public void onViewShow() {
        hasOnShow = true;
    }
}
