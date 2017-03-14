package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.view.View;

import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.ChatUIView;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.List;

/**
 * Created by hewking on 2017/3/10.
 */
public class GroupInfoUIVIew extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    protected static final String KEY_SESSION_ID = "key_account";
    protected static final String KEY_SESSION_TYPE = "key_sessiontype";

    private String mSessionId;
    private SessionTypeEnum sessionType;

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("聊天信息");
    }

    public static void start(ILayout mLayout, String sessionId, SessionTypeEnum sessionType) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SESSION_ID, sessionId);
        bundle.putInt(KEY_SESSION_TYPE, sessionType.getValue());
        mLayout.startIView(new GroupInfoUIVIew(), new UIParam().setBundle(bundle).setLaunchMode(UIParam.SINGLE_TOP));
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);

    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        Bundle bundle = param.mBundle;
        if (bundle != null) {
            mSessionId = bundle.getString(KEY_SESSION_ID);
            sessionType = SessionTypeEnum.typeOfValue(bundle.getInt(KEY_SESSION_TYPE));
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.item_message_group_chatinfo;
        }
        return R.layout.item_info_layout;
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);

        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                bindGroupMemberInfo(holder);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("群聊名称");
                infoLayout.setItemDarkText("群公告测试群");
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("群二维码");
                infoLayout.setDarkDrawableRes(R.drawable.qr_code);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("群公告");
                infoLayout.setItemDarkText("群公告");
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("置顶聊天");
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("消息免打扰");
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("消息免打扰");
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("我在本群的昵称");
                infoLayout.setItemDarkText("哈哈");
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("聊天文件");
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("查找聊天记录");
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("清空聊天记录");
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
                infoLayout.setItemText("举报");
            }
        }));
    }

    private void bindGroupMemberInfo(RBaseViewHolder holder) {
        GroupMemberModel.getInstanse().init(holder,mActivity,this,mSessionId);
    }
}
