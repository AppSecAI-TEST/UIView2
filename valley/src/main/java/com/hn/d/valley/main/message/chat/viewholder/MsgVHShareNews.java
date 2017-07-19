package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.library.glide.GlideBlurTransformation;
import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.Json;
import com.angcyo.uiview.widget.RImageView;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonSyntaxException;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.main.found.sub.InformationDetailUIView;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailMsg;
import com.hn.d.valley.main.message.attachment.ShareNewsAttachment;
import com.hn.d.valley.main.message.attachment.ShareNewsMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.sub.user.DynamicDetailUIView2;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnVideoPlayView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHShareNews extends MsgViewHolderBase {

    public MsgVHShareNews(BaseMultiAdapter adapter) {
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

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }

        ShareNewsAttachment shareNewsAttachment = (ShareNewsAttachment) attachment;
        final ShareNewsMsg detailMsg = shareNewsAttachment.getNewsMsg();
        tv_pc_name.setText(detailMsg.getTitle());
        imageView.setImageUrl(detailMsg.getLogo());
        msgPcLayout.setText(detailMsg.getAuthor());
//        msgPcLayout.setText(SpannableStringUtils.getBuilder(detailMsg.getAuthor())
//                .setForegroundColor(SkinHelper.getSkin().getThemeDarkColor())
//                .create());

            videoPlayView.setVisibility(View.GONE);
            iv_content.setImageResource(R.drawable.zixun_morentu);
            try{
                final HotInfoListBean bean;
//                bean = Json.from(detailMsg.get(), HotInfoListBean.class);
//                tv_pc_name.setText(bean.getTitle());
//                pc_layout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mUIBaseView.startIView(new InformationDetailUIView(bean));
//                    }
//                });
            }catch (JsonSyntaxException | NullPointerException e) {
                pc_layout.setOnClickListener(null);
                tv_pc_name.setText(detailMsg.getAuthor());
            }
            return;

    }

}
