package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupAnnouncementBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.InputUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/3/20.
 */
public class GroupAnnouncementUIView  extends SingleRecyclerUIView<GroupAnnouncementBean>{

    private String gid;
    private Boolean isAdmin;

    @Override
    protected TitleBarPattern getTitleBar() {

        ArrayList<TitleBarPattern.TitleBarItem> titleBarItems = new ArrayList<>();
        titleBarItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_add)).setVisibility(View.GONE).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(inputConfigCallback));
            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_group_announcement))
                .setRightItems(titleBarItems);
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
        loadData();
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        if (isAdmin) {
            getUITitleBarContainer().showRightItem(0);
        }
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        if (param != null) {
            gid = param.mBundle.getString(GroupMemberUIVIew.GID);
            isAdmin = param.mBundle.getBoolean(GroupMemberUIVIew.IS_ADMIN);
        }
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mBaseContentLayout.setBackgroundResource(R.color.chat_bg_color);
    }

    @Override
    protected void onEmptyData(boolean isEmpty) {
        super.onEmptyData(isEmpty);
        initOverEmptyLayout("暂无群公告", R.drawable.image_nothing);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        add(RRetrofit.create(GroupChatService.class)
                .announcementList(Param.buildMap("uid:" + UserCache.getUserAccount(),"gid:" + gid))
                .compose(Rx.transformerList(GroupAnnouncementBean.class))
                .subscribe(new BaseSingleSubscriber<List<GroupAnnouncementBean>>() {

                    @Override
                    public void onSucceed(List<GroupAnnouncementBean> beans) {
                        if (beans == null || beans.size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(beans);
                            onUILoadDataFinish();
                        }
                    }
                }));
    }

    @Override
    protected RExBaseAdapter<String, GroupAnnouncementBean, String> initRExBaseAdapter() {
        return new AnnouncementAdapter(mActivity);
    }

    public class AnnouncementAdapter extends RExBaseAdapter<String,GroupAnnouncementBean,String> {

        public AnnouncementAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_group_announcement;
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, final GroupAnnouncementBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);
            TextView tv_time = holder.tv(R.id.tv_time_view);
            TextView tv_announce_desc = holder.tv(R.id.tv_announcement_desc);
            View content = holder.v(R.id.rl_content);

            tv_announce_desc.setText(dataBean.getContent());
            tv_time.setText(TimeUtil.getTimeShowString(Long.parseLong(dataBean.getCreated()) * 1000, false));

            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new EditGroupAnnounceUIView(gid,dataBean.getAn_id(),isAdmin));
                }
            });
        }
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    private InputUIView.InputConfigCallback inputConfigCallback = new InputUIView.InputConfigCallback() {
        @Override
        public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
            return super.initTitleBar(titleBarPattern).setTitleString(mActivity.getString(R.string.text_publish_group_annonce))
                    .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.publish),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String value = "empty";
                                    if (mExEditText.isEmpty()) {
                                        T_.error(getString(R.string.input_group_announcement_hint));
                                        return;
                                    } else {
                                        value = mExEditText.string();
                                    }

                                    final String finalValue = value;
                                    UIDialog.build()
                                            .setDialogContent(mActivity.getString(R.string.text_send_announce_tip))
                                            .setOkText(getString(R.string.ok))
                                            .setCancelText(getString(R.string.cancel))
                                            .setOkListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    add(RRetrofit.create(GroupChatService.class)
                                                            .setAnnouncement(Param.buildMap("uid:" + UserCache.getUserAccount(),"gid:" + gid,"content:" + finalValue))
                                                            .compose(Rx.transformer(String.class))
                                                            .subscribe(new BaseSingleSubscriber<String>() {
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
    protected RecyclerView.ItemDecoration initItemDecoration() {
        return super.initItemDecoration();
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }
}
