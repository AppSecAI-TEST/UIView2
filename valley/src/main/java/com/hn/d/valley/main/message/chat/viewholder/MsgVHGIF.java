package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.view.View;

import com.angcyo.uiview.utils.media.ImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.ChatControl2;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.widget.MsgThumbImageView;
import com.lzy.imagepicker.bean.ImageItem;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.List;

import static com.hn.d.valley.main.message.ChatAdapter.setImageSize;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHGIF extends MsgViewHolderBase {

    public MsgVHGIF(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_image_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

        final MsgThumbImageView draweeView = (MsgThumbImageView) findViewById(R.id.msg_image_view);
        final View clickView = findViewById(R.id.click_view);

        final FileAttachment msgAttachment = (FileAttachment) message.getAttachment();
        final String path = msgAttachment.getPath();
        final String thumbPath = msgAttachment.getThumbPath();

        String fileUri = "";
        boolean isFile;
        if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(path)) {
            isFile = false;
            fileUri = msgAttachment.getUrl();
            downloadAttachment(message,false);
        } else {
            isFile = true;
            if (!TextUtils.isEmpty(thumbPath)) {
                setImageSize(draweeView, clickView, message, thumbPath);
                fileUri = thumbPath;
            } else if (!TextUtils.isEmpty(path)) {
                setImageSize(draweeView, clickView, message, path);
                fileUri = path;
            }
        }
        Glide.with(context)
//                    .using(new AssetUriLoader(mContext))
                .load(fileUri)
//                    .asGif()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new GlideDrawableImageViewTarget(draweeView,1));

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChatControl2.Images images = getAllImageMessage(message);
                ImagePagerUIView.start(mUIBaseView.getILayout(), v, images.images, images.positon);
            }
        });

    }

    private ChatControl2.Images getAllImageMessage(IMMessage messageAnchor) {
        ChatControl2.Images imagesBean = new ChatControl2.Images();

        final List<IMMessage> allDatas = getAdapter().getAllDatas();
        ArrayList<ImageItem> images = new ArrayList<>();

        for (int i = 0; i < allDatas.size(); i++) {
            final IMMessage message = allDatas.get(i);
            if (message.getMsgType() == MsgTypeEnum.image) {
                FileAttachment msgAttachment = (FileAttachment) message.getAttachment();
                String path2 = msgAttachment.getPath();
                String thumbPath2 = msgAttachment.getThumbPath();

                if (messageAnchor.isTheSame(message)) {
                    imagesBean.positon = images.size();
                }

                final ImageItem imageItem = new ImageItem();
                imageItem.path = path2;
                imageItem.thumbPath = thumbPath2;
                imageItem.url = msgAttachment.getUrl();
                images.add(imageItem);
            }
        }

        imagesBean.images = images;
        return imagesBean;
    }
}
