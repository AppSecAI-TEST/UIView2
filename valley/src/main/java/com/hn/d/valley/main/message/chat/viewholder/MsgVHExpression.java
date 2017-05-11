package com.hn.d.valley.main.message.chat.viewholder;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.utils.media.ImageUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hn.d.valley.R;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.emoji.StickerManager;
import com.hn.d.valley.main.message.attachment.CustomExpressionAttachment;
import com.hn.d.valley.main.message.attachment.CustomExpressionMsg;
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

import static com.hn.d.valley.emoji.StickerManager.CATEGORY_EXPRESSION;
import static com.hn.d.valley.main.message.ChatAdapter.setImageSize;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHExpression extends MsgViewHolderBase {

    public MsgVHExpression(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_chartlet_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {
        ImageView draweeView = (ImageView) findViewById(R.id.msg_image_view);

        contentContainer.setBackground(null);

        CustomExpressionAttachment expressionAttachment = (CustomExpressionAttachment) message.getAttachment();
        if (expressionAttachment == null) {
            return;
        }

        CustomExpressionMsg expressionMsg = expressionAttachment.getExpressionMsg();
        String chartlet = expressionMsg.getMsg();

        Glide.with(context)
                .load(Uri.parse(StickerManager.getInstance().getStickerBitmapUri(CATEGORY_EXPRESSION
                        , chartlet)))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(draweeView);

    }


}
