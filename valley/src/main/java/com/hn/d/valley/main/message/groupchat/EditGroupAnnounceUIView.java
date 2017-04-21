package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RTextView;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupAnnounceDetailBean;
import com.hn.d.valley.bean.GroupAnnouncementBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnEmojiTextView;
import com.hn.d.valley.widget.HnEmptyRefreshLayout;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class EditGroupAnnounceUIView extends BaseUIView {


    @BindView(R.id.iv_item_head)
    HnGlideImageView ivItemHead;
    @BindView(R.id.tv_friend_name)
    TextView tvFriendName;
    @BindView(R.id.tv_func_desc)
    TextView tvFuncDesc;
    @BindView(R.id.item_root_layout)
    RelativeLayout itemRootLayout;
    @BindView(R.id.tv_announcement_desc)
    RTextView tvAnnouncementDesc;
    @BindView(R.id.refresh_layout)
    HnEmptyRefreshLayout refreshLayout;
    @BindView(R.id.command_item_view)
    RTextView commandItemView;

    private String gid;
    private String an_id;
    private boolean isAdmin;

    EditGroupAnnounceUIView(String gid, String an_id,boolean isAdmin) {
        this.gid = gid;
        this.an_id = an_id;
        this.isAdmin = isAdmin;
    }

    @Override
    protected TitleBarPattern getTitleBar() {

        ArrayList<TitleBarPattern.TitleBarItem> titleBarItems = new ArrayList<>();
        titleBarItems.add(TitleBarPattern.TitleBarItem.build().setRes(R.drawable.delete_search).setVisibility(View.GONE).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnnounce();
            }
        }));

        return super.getTitleBar().setShowBackImageView(true).setRightItems(titleBarItems);
    }



    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);

        loadData();

    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        if(isAdmin) {
            getUITitleBarContainer().showRightItem(0);
            commandItemView.setVisibility(View.VISIBLE);
        }

        initView();
    }

    private void initView() {

        commandItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(inputConfigCallback));
            }
        });

    }

    private InputUIView.InputConfigCallback inputConfigCallback = new InputUIView.InputConfigCallback() {
        @Override
        public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
            return super.initTitleBar(titleBarPattern).setTitleString(mActivity.getString(R.string.text_edit_group_announce))
                    .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.finish),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String value = "empty";
                                    if (!mExEditText.isEmpty()) {
                                        value = mExEditText.string();
                                    }

                                    add(RRetrofit.create(GroupChatService.class)
                                            .setAnnouncement(Param.buildMap("an_id:" + an_id,"gid:" + gid,"content:" + value))
                                            .compose(Rx.transformer(String.class))
                                            .subscribe(new BaseSingleSubscriber<String>() {
                                                @Override
                                                public void onError(int code, String msg) {
                                                    super.onError(code, msg);
                                                }

                                                @Override
                                                public void onSucceed(String beans) {
                                                    loadData();
                                                    finishIView(mIView);

                                                }
                                            }));
                                }
                            }));
        }

        @Override
        public void initInputView(RBaseViewHolder holder, ExEditText editText, ItemRecyclerUIView.ViewItemInfo bean) {
            super.initInputView(holder, editText, bean);
            editText.setSingleLine(false);
            editText.setMaxLines(5);
            int maxCount = mActivity.getResources().getInteger(R.integer.signature_count);
            editText.setMaxLength(maxCount);
            editText.setGravity(Gravity.TOP);
            UI.setViewHeight(editText, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));
            final TextIndicator textIndicator = holder.v(R.id.single_text_indicator_view);
            textIndicator.setVisibility(View.VISIBLE);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    textIndicator.setCurrentCount(s.length());
                }
            });
        }
    };

    @Override
    public void onSkinChanged(ISkin skin) {
        super.onSkinChanged(skin);
        if (commandItemView != null) {
            ResUtil.setBgDrawable(commandItemView, skin.getThemeMaskBackgroundSelector());
        }
    }

    private void deleteAnnounce() {
        add(RRetrofit.create(GroupChatService.class)
                .removeAnnouncement(Param.buildMap("gid:" + gid,"an_id:" + an_id))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(String beans) {
                        T_.show(mActivity.getString(R.string.text_delete_success));
                        finishIView();
                    }
                }));
    }


    private void loadData() {

        add(RRetrofit.create(GroupChatService.class)
                .announcementDetail(Param.buildMap("gid:" + gid,"an_id:" + an_id))
                .compose(Rx.transformer(GroupAnnounceDetailBean.class))
                .subscribe(new BaseSingleSubscriber<GroupAnnounceDetailBean>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(GroupAnnounceDetailBean beans) {
                        showContentLayout();
                        processResult(beans);

                    }
                }));

    }

    private void processResult(GroupAnnounceDetailBean beans) {
        if (beans == null) {
            return;
        }

        tvFriendName.setText(beans.getUsername());
        tvFuncDesc.setText(TimeUtil.getTimeShowString(Long.parseLong(beans.getCreated()) * 1000, false));

        tvAnnouncementDesc.setText(beans.getContent());
        ivItemHead.setImageUrl(beans.getAvatar());
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_edit_announce);
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return super.getDefaultLayoutState();
    }
}
