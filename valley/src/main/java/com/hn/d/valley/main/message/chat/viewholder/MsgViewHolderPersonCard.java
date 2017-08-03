package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.RTextView;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.main.friend.PersonCardUIDialog;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.message.attachment.CustomAttachment;
import com.hn.d.valley.main.message.attachment.CustomAttachmentType;
import com.hn.d.valley.main.message.attachment.PersonalCard;
import com.hn.d.valley.main.message.attachment.PersonalCardAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import rx.functions.Action0;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderPersonCard extends MsgViewHolderBase {

    private RTextView chars_count;
    private HnGenderView grade;
    private ImageView auth_iview;
    private RTextView fans_count;

    public MsgViewHolderPersonCard(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder, data, position, isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_personalcard_layout;
    }

    @Override
    protected void inflateContentView() {


    }

    @Override
    protected void bindContentView() {

        HnGlideImageView imageView = (HnGlideImageView) findViewById(R.id.iv_thumb);
        TextView tv_pc_name = (TextView) findViewById(R.id.username);
        chars_count = (RTextView) findViewById(R.id.chars_count);
        auth_iview = (ImageView) findViewById(R.id.auth_iview);
        fans_count = (RTextView) findViewById(R.id.fans_count);
        grade = (HnGenderView) findViewById(R.id.grade);
        LinearLayout ll_user_detail = (LinearLayout) findViewById(R.id.ll_user_detail);
        ll_user_detail.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

//        LinearLayout pc_layout = (LinearLayout) findViewById(R.id.msg_card_layout);
//        contentContainer.setBackgroundResource(0);
//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) pc_layout.getLayoutParams();
//        if (isReceivedMessage()) {
//            params.setMarginStart(ScreenUtil.dip2px(10));
//        } else {
//            params.setMarginEnd(ScreenUtil.dip2px(10));
//        }

        CustomAttachment attachment = (CustomAttachment) message.getAttachment();
        if (attachment == null) {
            return;
        }
        switch (attachment.getType()) {
            case CustomAttachmentType.PersonalCard:
                PersonalCardAttachment pcAttachment = (PersonalCardAttachment) attachment;
                final PersonalCard from = PersonalCardAttachment.from(pcAttachment.toJson(true));
                tv_pc_name.setText(from.getUsername());
//                imageView.setImageUrl(from.getAvatar());
                Glide.with(context)
                        .load(from.getAvatar())
                        .placeholder(R.drawable.zhanweitu_1)
                        .into(imageView);

                contentContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUIBaseView.startIView(new UserDetailUIView2(from.getUid()));
                    }
                });

                loadData(from.getUid());

                break;
            default:
                break;

        }

    }

    private void loadData(String uid) {
        RRetrofit.create(UserService.class)
                .userInfo(Param.buildMap("to_uid:" + uid))
                .compose(Rx.transformer(UserInfoBean.class))
                .subscribe(new RSubscriber<UserInfoBean>() {
                    @Override
                    public void onSucceed(UserInfoBean bean) {
                        super.onSucceed(bean);
                        fillView(bean);
                    }
                });
    }

    private void fillView(UserInfoBean bean) {
        fans_count.setText(bean.getFans_count() + "");
        chars_count.setText(bean.getCharm());
        grade.setGender(bean.getSex(), bean.getGrade(),bean.getConstellation(),bean.getCharm());
    }

}
