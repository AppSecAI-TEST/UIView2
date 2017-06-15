package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.ISkin;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RTextView;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupAnnounceDetailBean;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnEmptyRefreshLayout;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.ArrayList;

public class EditGroupAnnounceUIView extends BaseUIView {

    private HnGlideImageView ivItemHead;
    private TextView tvFriendName;
    private TextView tvFuncDesc;
    private RelativeLayout itemRootLayout;
    private RelativeLayout commandItem;
    private RTextView tvAnnouncementDesc;
    private HnEmptyRefreshLayout refreshLayout;
    private RTextView commandItemView;

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
        titleBarItems.add(TitleBarPattern.TitleBarItem.build()
                .setRes(R.drawable.delete_search)
                .setVisibility(View.GONE).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAnnounce();
            }
        }));

        return super.getTitleBar()
                .setTitleString(mActivity.getString(R.string.text_announce_detail))
                .setShowBackImageView(true)
                .setRightItems(titleBarItems);
    }



    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);

        loadData();

    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        ivItemHead = v(R.id.iv_item_head);
        tvFriendName = v(R.id.tv_friend_name);
        tvFuncDesc = v(R.id.tv_func_desc);
        itemRootLayout = v(R.id.item_root_layout);
        tvAnnouncementDesc = v(R.id.tv_announcement_desc);
        refreshLayout = v(R.id.refresh_layout);
        commandItemView = v(R.id.text_view);
        commandItem = v(R.id.command_item_view);

        if(isAdmin) {
            getUITitleBarContainer().showRightItem(0);
            commandItem.setVisibility(View.VISIBLE);
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

                                    final String finalValue = value;
                                    UIDialog.build()
                                            .setDialogContent(mActivity.getString(R.string.text_edit_announce_sure_tip))
                                            .setOkText(getString(R.string.ok))
                                            .setCancelText(getString(R.string.cancel))
                                            .setOkListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    add(RRetrofit.create(GroupChatService.class)
                                                            .setAnnouncement(Param.buildMap("an_id:" + an_id,"gid:" + gid,"content:" + finalValue))
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
                                            })
                                            .showDialog(mILayout);


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
            editText.setHint(R.string.text_please_edit_announce);
            int padding = ScreenUtil.dip2px(15);
            editText.setPadding(padding,padding,padding,padding);
            UI.setViewHeight(editText, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_150dpi));
            final TextIndicator textIndicator = holder.v(R.id.single_text_indicator_view);
            textIndicator.setMaxCount(maxCount);
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

        UIDialog.build()
                .setDialogContent("确定删除此公告?")
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                })
                .setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                })
                .showDialog(mILayout);


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
