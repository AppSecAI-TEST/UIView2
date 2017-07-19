package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.SpannableStringUtils;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.skin.SkinHelper;
import com.hn.d.valley.R;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.main.me.setting.EditInfoUIView;
import com.hn.d.valley.main.message.attachment.InviteUploadMsg;
import com.hn.d.valley.main.message.attachment.InviteUploadProfileAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

import rx.functions.Action0;

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
        return R.layout.msg_text_invite_layout;
    }

    @Override
    protected void inflateContentView() {
        final TextView tv_name = (TextView) findViewById(R.id.tv_name);
        final TextView tv_msg = (TextView) findViewById(R.id.tv_msg);
//        final TextView tv_tip = (TextView) findViewById(R.id.tv_tip);

        InviteUploadProfileAttachment attachment = (InviteUploadProfileAttachment) message.getAttachment();
        final InviteUploadMsg uploadMsg = attachment.getInviteUploadMsg();

        tv_name.setTextColor(SkinHelper.getSkin().getThemeSubColor());
//        tv_tip.setTextColor(SkinHelper.getSkin().getThemeSubColor());
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new UserDetailUIView2(uploadMsg.getUid()));
            }
        });
//
        tv_name.setText(uploadMsg.getUsername());
//        tv_msg.setText(uploadMsg.getMsg());
//        tv_tip.setText("上传照片吧!");
//        tv_tip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mUIBaseView.startIView(new EditInfoUIView(new ArrayList<String>(), new Action0() {
//                    @Override
//                    public void call() {
//
//                    }
//                }));
//            }
//        });

        tv_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new EditInfoUIView(new ArrayList<String>(), new Action0() {
                    @Override
                    public void call() {

                    }
                }));
            }
        });

        tv_msg.setText(SpannableStringUtils.getBuilder(uploadMsg.getMsg())
                .append("上传照片吧!")
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        mUIBaseView.startIView(new EditInfoUIView(new ArrayList<String>(), new Action0() {
                            @Override
                            public void call() {

                            }
                        }));
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
//        contentContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                contentView.performClick();
//            }
//        });

    }

    @Override
    protected void bindContentView() {


    }

}
