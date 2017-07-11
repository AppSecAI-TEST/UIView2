package com.hn.d.valley.main.me.setting;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupDescBean;
import com.hn.d.valley.main.message.session.SessionHelper;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.widget.HnButton;
import com.hn.d.valley.widget.HnGlideImageView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;


/**
    扫描二维码加群
 */
public class QrCodeAddGroupUIView extends BaseContentUIView {

    private String groupProfile;

    private HnGlideImageView icon;
    private RTextView tv_group_name;
    private RTextView tv_group_member_num;
    private HnButton btn_add;

    public QrCodeAddGroupUIView(String result) {
        this.groupProfile = result;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_qrcode_add_group);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true).setTitleString(
                getString(com.hn.d.valley.R.string.text_add_group));
    }

    @Override
    public int getDefaultBackgroundColor() {
        return ContextCompat.getColor(mActivity,R.color.chat_bg_color);
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_qr_code;
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        L.d("groupprofile",groupProfile);

        icon = mViewHolder.v(R.id.user_ico_view);
        tv_group_name = mViewHolder.v(R.id.tv_group_name);
        tv_group_member_num = mViewHolder.v(R.id.tv_group_member_num);
        btn_add = mViewHolder.v(R.id.tv_add_group);

        String[] split = groupProfile.split(",");
        if (split.length < 3) {
            return;
        }

        add(RRetrofit.create(GroupChatService.class)
                        .groupInfo(Param.buildMap("uid:" + split[1], "yx_gid:" + split[2]))
                        .compose(Rx.transformer(GroupDescBean.class))
                        .subscribe(new BaseSingleSubscriber<GroupDescBean>() {

                            @Override
                            public void onSucceed(GroupDescBean bean) {
                                super.onSucceed(bean);
                                process(bean);
                            }
                        }));
    }

    private void process(final GroupDescBean t) {

        final String[] split = groupProfile.split(",");

        String iconUrl = t.getDefaultAvatar();
        int count = t.getMemberCount();
        String name = t.getTrueName();

        icon.setImageUrl(iconUrl);
        tv_group_member_num.setText(String.format("(%d)",count));
        tv_group_name.setText(name);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RRetrofit.create(GroupChatService.class)
                        .join(Param.buildMap("invitor:" + split[1],"gid:" + split[0]))
                        .compose(Rx.transformer(String.class))
                        .subscribe(new BaseSingleSubscriber<String>() {
                            @Override
                            public void onSucceed(String bean) {
                                super.onSucceed(bean);
                                SessionHelper.startTeamSession(mILayout,split[2], SessionTypeEnum.Team);
                                mILayout.finishIView(QrCodeAddGroupUIView.class);
                            }

                            @Override
                            public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                                super.onEnd(isError, isNoNetwork, e);
                            }
                        });
            }
        });
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

}
