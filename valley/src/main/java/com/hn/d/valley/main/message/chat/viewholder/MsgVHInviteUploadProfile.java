package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.library.glide.GlideBlurTransformation;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.widget.RImageView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hn.d.valley.R;
import com.hn.d.valley.main.me.setting.EditInfoUIView;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailMsg;
import com.hn.d.valley.main.message.attachment.InviteUploadMsg;
import com.hn.d.valley.main.message.attachment.InviteUploadProfileAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.sub.user.DynamicDetailUIView2;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnVideoPlayView;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHInviteUploadProfile extends MsgViewHolderBase {

    public MsgVHInviteUploadProfile(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder, data, position, isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_text_layout;
    }

    @Override
    protected void inflateContentView() {
        TextView contentView = (TextView) findViewById(R.id.msg_text_view);

        InviteUploadProfileAttachment attachment = (InviteUploadProfileAttachment) message.getAttachment();
        InviteUploadMsg uploadMsg = attachment.getInviteUploadMsg();

        contentView.setText(SpannableStringUtils.getBuilder(uploadMsg.getUsername())
            .setForegroundColor(SkinHelper.getSkin().getThemeSubColor())
                .append(uploadMsg.getMsg())
                .append("上传照片吧!")
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        mUIBaseView.startIView(new EditInfoUIView(null,null));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(SkinHelper.getSkin().getThemeSubColor());
                        ds.setUnderlineText(false);
                        ds.clearShadowLayer();
                    }

                })
            .create());
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void bindContentView() {



    }

}
