package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.library.glide.GlideBlurTransformation;
import com.angcyo.uiview.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.Json;
import com.angcyo.uiview.widget.RImageView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonSyntaxException;
import com.hn.d.valley.R;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.main.found.sub.InformationDetailUIView;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.DynamicDetailAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailMsg;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.sub.user.DynamicDetailUIView2;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnVideoPlayView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHDynamicShareDetail extends MsgViewHolderBase {

    public MsgVHDynamicShareDetail(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder, data, position, isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_sharedynamic_layout;
    }

    @Override
    protected void inflateContentView() {


    }

    @Override
    protected void bindContentView() {

        HnGlideImageView imageView = (HnGlideImageView) findViewById(R.id.iv_item_head);
        TextView tv_pc_name = (TextView) findViewById(R.id.tv_pc_name);
        TextView msgPcLayout = (TextView) findViewById(R.id.tv_pc_desc);
        LinearLayout pc_layout = (LinearLayout) findViewById(R.id.msg_card_layout);
        RImageView iv_content = (RImageView) findViewById(R.id.iv_item_content);
        HnVideoPlayView videoPlayView = (HnVideoPlayView) findViewById(R.id.video_play_view);

//        contentContainer.setBackgroundResource(0);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pc_layout.getLayoutParams();
        if (isReceivedMessage()) {
//            params.setMarginStart(ScreenUtil.dip2px(10));
        } else {
//            params.setMarginEnd(ScreenUtil.dip2px(10));
        }

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        DynamicDetailAttachment shareDynaAttachment = (DynamicDetailAttachment) attachment;
        final DynamicDetailMsg detailMsg = shareDynaAttachment.getDynamicMsg();
        tv_pc_name.setText(detailMsg.getMsg());
        imageView.setImageUrl(detailMsg.getAvatar());
//        msgPcLayout.setText(detailMsg.getApnsText());
        msgPcLayout.setText(SpannableStringUtils.getBuilder(detailMsg.getApnsText())
                .append("的动态")
                .setForegroundColor(SkinHelper.getSkin().getThemeDarkColor())
                .create());

        String thumbUrl = detailMsg.getPicture();

        pc_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new DynamicDetailUIView2(detailMsg.getItem_id()));
            }
        });

        if (detailMsg.isImageMediaType()) {
            videoPlayView.setVisibility(View.INVISIBLE);

        } else if (detailMsg.isVoiceMediaType()) {
            videoPlayView.setVisibility(View.VISIBLE);
            videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE);
            DrawableRequestBuilder<Integer> builder = Glide.with(imageView.getContext())
                    .load(R.drawable.default_vociecover)
                    .placeholder(R.drawable.default_vociecover)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
                builder.bitmapTransform(new GlideBlurTransformation(imageView.getContext()))
                        .into(iv_content);
            return;

        } else if (detailMsg.isVideoMediaType()) {
            videoPlayView.setVisibility(View.VISIBLE);
            thumbUrl = detailMsg.getCover();
            videoPlayView.setPlayType(HnVideoPlayView.PlayType.VIDEO);

        } else if (detailMsg.isTextMediaType()) {
            videoPlayView.setVisibility(View.GONE);
            iv_content.setVisibility(View.GONE);
        } else if (detailMsg.isArticle()) {
            videoPlayView.setVisibility(View.GONE);
            iv_content.setImageResource(R.drawable.zixun_morentu);
            try{
                final HotInfoListBean bean;
                bean = Json.from(detailMsg.getCover(), HotInfoListBean.class);
                tv_pc_name.setText(bean.getTitle());
                pc_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mUIBaseView.startIView(new InformationDetailUIView(bean));
                    }
                });
            }catch (JsonSyntaxException | NullPointerException e) {
                pc_layout.setOnClickListener(null);
                tv_pc_name.setText(detailMsg.getMsg());
            }
            return;
        }

        Glide.with(context)
                .load(thumbUrl)
                .placeholder(R.drawable.zhanweitu_1)
                .into(iv_content);

    }

}
