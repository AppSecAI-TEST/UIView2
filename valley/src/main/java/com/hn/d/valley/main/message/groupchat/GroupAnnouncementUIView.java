package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseView;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
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

    @Override
    protected TitleBarPattern getTitleBar() {

        ArrayList<TitleBarPattern.TitleBarItem> titleBarItems = new ArrayList<>();
        titleBarItems.add(TitleBarPattern.TitleBarItem.build().setText("添加").setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(inputConfigCallback));
            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_group_announcement))
                .setRightItems(titleBarItems);
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        if (param != null) {
            gid = param.mBundle.getString(GroupMemberUIVIew.GID);
        }
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mBaseContentLayout.setBackgroundResource(R.color.chat_bg_color);
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);

        RRetrofit.create(GroupChatService.class)
                .announcementList(Param.buildMap("uid:" + UserCache.getUserAccount(),"gid:" + gid))
                .compose(Rx.transformerList(GroupAnnouncementBean.class))
                .subscribe(new BaseSingleSubscriber<List<GroupAnnouncementBean>>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                    }

                    @Override
                    public void onSucceed(List<GroupAnnouncementBean> beans) {
                        if (beans == null || beans.size() == 0) {
                            onUILoadDataEnd();
                        } else {
                            onUILoadDataEnd(beans);
                        }
                    }
                });
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
        protected void onBindDataView(RBaseViewHolder holder, int posInData, GroupAnnouncementBean dataBean) {
            super.onBindDataView(holder, posInData, dataBean);
            TextView tv_announce_desc = holder.tv(R.id.tv_announcement_desc);

            tv_announce_desc.setText(dataBean.getContent());

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
            return super.initTitleBar(titleBarPattern).setTitleString("发布群公告")
                    .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.finish),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String value = "empty";
                                    if (!mExEditText.isEmpty()) {
                                        value = mExEditText.string();
                                    }

                                    RRetrofit.create(GroupChatService.class)
                                            .setAnnouncement(Param.buildMap("uid:" + UserCache.getUserAccount(),"gid:" + gid,"content:" + value))
                                            .compose(Rx.transformer(String.class))
                                            .subscribe(new BaseSingleSubscriber<String>() {
                                                @Override
                                                public void onError(int code, String msg) {
                                                    super.onError(code, msg);
                                                }

                                                @Override
                                                public void onSucceed(String beans) {
                                                    onUILoadDataEnd();
                                                    finishIView(mIView);

                                                }
                                            });
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
}
