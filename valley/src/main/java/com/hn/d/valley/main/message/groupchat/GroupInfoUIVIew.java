package com.hn.d.valley.main.message.groupchat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import com.angcyo.library.utils.Anim;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.GroupDescBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.InputUIView;
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

    private GroupDescBean mGroupDescBean;

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

        if (mRExBaseAdapter.isLast(viewType)) {
            return R.layout.item_button_view;
        }

        if (viewType == 4 || viewType == 5) {
            return R.layout.item_switch_view;
        }
        return R.layout.item_info_layout;
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        add(RRetrofit.create(GroupChatService.class)
                .groupInfo(Param.buildMap("uid:" + UserCache.getUserAccount(),"yx_gid:" + mSessionId))
                .compose(Rx.transformer(GroupDescBean.class))
                .subscribe(new BaseSingleSubscriber<GroupDescBean>() {
                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadData();
                            }
                        });
                    }

                    @Override
                    public void onSucceed(GroupDescBean bean) {
                        if (bean == null) {
//                            onUILoadDataEnd();
                        } else {
                            showContentLayout();
                            mGroupDescBean = bean;
                            mRExBaseAdapter.notifyDataSetChanged();
                        }
                    }
                }));
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
                bindGroupName(holder);
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
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText("置顶聊天");
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                ItemInfoLayout itemInfoLayout = holder.v(R.id.item_info_layout);
                SwitchCompat switchCompat = holder.v(R.id.switch_view);
                itemInfoLayout.setItemText("消息免打扰");
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
                infoLayout.setItemText("退出登录");

            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(3 * left) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                textView.setText("退出");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dissolveGroup();
                    }
                });
            }
        }));

    }

    private void dissolveGroup() {
        add(RRetrofit.create(GroupChatService.class)
                .dissolve(Param.buildMap("uid:" + UserCache.getUserAccount()
                        ,"gid:" + mGroupDescBean.getGid()))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        T_.info("解散成功");
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));
    }

    private void bindGroupMemberInfo(RBaseViewHolder holder) {
        GroupMemberModel.getInstanse().init(holder,mActivity,this,mSessionId);
    }

    private void bindGroupName(RBaseViewHolder holder) {
        final ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText("群聊名称");
        if (mGroupDescBean == null) {
            return;
        }

        boolean isRawname = mGroupDescBean.getDefaultName().equals("");
        final String currentName = isRawname?mGroupDescBean.getDefaultName():mGroupDescBean.getName();
        infoLayout.setItemDarkText(currentName);
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(InputUIView.build(new InputUIView.InputConfigCallback() {
                    @Override
                    public TitleBarPattern initTitleBar(TitleBarPattern titleBarPattern) {
                        return super.initTitleBar(titleBarPattern)
                                .setTitleString(mActivity.getString(R.string.modify_name_title))
                                .addRightItem(TitleBarPattern.TitleBarItem.build(mActivity.getResources().getString(R.string.save)
                                        , new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (mExEditText.isEmpty()) {
                                                    Anim.band(mExEditText);
                                                    return;
                                                }
                                                final String name = mExEditText.string();
                                                infoLayout.setItemDarkText(name);
                                                editGroupName(name);
                                                finishIView(mIView);
                                            }
                                        }));
                    }

                    @Override
                    public void initInputView(RBaseViewHolder holder, ExEditText editText, ViewItemInfo bean) {
                        super.initInputView(holder, editText, bean);
                        editText.setMaxLength(mActivity.getResources().getInteger(R.integer.name_count));
                        editText.setHint(R.string.input_name_hint);
                        if (mGroupDescBean != null ) {
                            setInputText(currentName);
                        }
                    }
                }));
            }
        });

    }

    private void editGroupName(String name) {
        if (mGroupDescBean == null ) {
            return;
        }
        add(RRetrofit.create(GroupChatService.class)
                .editGroupName(Param.buildMap("uid:" + UserCache.getUserAccount()
                        ,"gid:" + mGroupDescBean.getGid(),"name:" + name))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }
}
