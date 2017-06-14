package com.hn.d.valley.sub.user;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RMaxAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseRecyclerUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.CommentListBean;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.realm.IcoInfoBean;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.emoji.EmojiRecyclerView;
import com.hn.d.valley.emoji.IEmoticonSelectedListener;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.service.DiscussService;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.sub.other.LikeUserRecyclerUIView;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func2;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：动态详情
 * 创建人员：Robi
 * 创建时间：2017/01/13 19:27
 * 修改人员：Robi
 * 修改时间：2017/01/13 19:27
 * 修改备注：
 * Version: 1.0.0
 */
@Deprecated
public class DynamicDetailUIView extends BaseRecyclerUIView<UserDiscussListBean.DataListBean,
        CommentListBean.DataListBean, String> implements UIIDialogImpl.OnDismissListener {

    //    @BindView(R.id.recycler_view)
    RRecyclerView mRecyclerView;
    //    @BindView(R.id.refresh_layout)
    HnRefreshLayout mRefreshLayout;
    //    @BindView(R.id.dynamic_root_layout)
    RSoftInputLayout mDynamicRootLayout;
    //    @BindView(R.id.emoji_recycler_view)
    EmojiRecyclerView mEmojiRecyclerView;
    //    @BindView(R.id.input_view)
    ExEditText mInputView;
    //    @BindView(R.id.emoji_control_layout)
    CheckBox mEmojiControlLayout;
    //    @BindView(R.id.send_view)
    TextView mSendView;
    /**
     * 动态id
     */
    private String discuss_id;
    /**
     * 点赞头像列表
     */
    private HnIcoRecyclerView mIcoRecyclerView;

    public DynamicDetailUIView(String discuss_id) {
        this.discuss_id = discuss_id;
    }

    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflater.inflate(R.layout.view_dynamic_detail, baseContentLayout);
//        inflate(R.layout.view_dynamic_detail);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mEmojiRecyclerView.setOnEmojiSelectListener(new IEmoticonSelectedListener() {
            @Override
            public void onEmojiSelected(String emoji) {
                final int selectionStart = mInputView.getSelectionStart();
                mInputView.getText().insert(selectionStart, emoji);
                MoonUtil.show(mActivity, mInputView, mInputView.getText().toString());
                mInputView.setSelection(selectionStart + emoji.length());
                mInputView.requestFocus();
            }

            @Override
            public void onStickerSelected(String categoryName, String stickerName) {

            }
        });
        mDynamicRootLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
                if (!isEmojiShow) {
                    mEmojiControlLayout.setChecked(false);
                }
            }
        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDynamicRootLayout.requestBackPressed();
                }
                return false;
            }
        });

        ResUtil.setBgDrawable(mSendView, ResUtil.generateRippleRoundMaskDrawable(density() * 3,
                Color.WHITE,
                SkinHelper.getSkin().getThemeDarkColor(),
                getColor(R.color.colorDisable),
                SkinHelper.getSkin().getThemeSubColor()
        ));

        SkinUtils.setExpressView(mEmojiControlLayout);
    }

    @Override
    public boolean onBackPressed() {
        if (mDynamicRootLayout == null) {
            return true;
        }
        return mDynamicRootLayout.requestBackPressed();
    }

    @Override
    public HnRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    @Override
    public RRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected RExBaseAdapter<UserDiscussListBean.DataListBean, CommentListBean.DataListBean, String> initRExBaseAdapter() {
        return new RExBaseAdapter<UserDiscussListBean.DataListBean, CommentListBean.DataListBean, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_HEADER) {
                    return R.layout.item_search_user_item_layout;
                }
                return R.layout.item_comment_layout;
            }

            @Override
            protected void onBindHeaderView(RBaseViewHolder holder, final int posInHeader, final UserDiscussListBean.DataListBean headerBean) {
                super.onBindHeaderView(holder, posInHeader, headerBean);
                UserDiscussItemControl.initItem(mSubscriptions, holder, headerBean, null, null, getILayout());
                holder.v(R.id.command_item_view).setVisibility(View.GONE);

                final View likeUserControlLayout = holder.v(R.id.like_users_layout);
                final View clickView = holder.v(R.id.click_view);
                likeUserControlLayout.setVisibility(View.GONE);

                //点赞人数列表
                clickView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new LikeUserRecyclerUIView(headerBean.getDiscuss_id()));
                    }
                });

//                holder.v(R.id.bottom_line_view).setVisibility(View.VISIBLE);

                List<LikeUserInfoBean> like_users = headerBean.getLike_users();
                final TextView userCountView = holder.tv(R.id.like_user_count_view);

                mIcoRecyclerView = holder.v(R.id.like_user_recycler_view);

                int oldSize = 0;
                if (like_users != null && !like_users.isEmpty()) {
                    oldSize = like_users.size();
                    likeUserControlLayout.setVisibility(View.VISIBLE);
                    userCountView.setText(oldSize + "");

                    List<IcoInfoBean> infos = new ArrayList<>();
                    for (LikeUserInfoBean infoBean : like_users) {
                        infos.add(new IcoInfoBean(infoBean.getUid(), infoBean.getAvatar()));
                    }
                    mIcoRecyclerView.getMaxAdapter().resetData(infos);
                }
                //点赞
                UserDiscussItemControl.bindLikeItemView(mSubscriptions, holder, headerBean, new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (mIcoRecyclerView != null) {
                            RMaxAdapter<IcoInfoBean> maxAdapter = mIcoRecyclerView.getMaxAdapter();
                            IcoInfoBean IcoInfoBean = new IcoInfoBean(UserCache.getUserAccount(),
                                    UserCache.getUserAvatar());
                            int itemRawCount;
                            if (aBoolean) {
                                maxAdapter.addLastItem(IcoInfoBean);
                                itemRawCount = maxAdapter.getItemRawCount();
                                userCountView.setText(itemRawCount + "");
                            } else {
                                maxAdapter.deleteItem(IcoInfoBean);
                                itemRawCount = maxAdapter.getItemRawCount();
                                userCountView.setText(itemRawCount + "");
                            }

                            likeUserControlLayout.setVisibility(itemRawCount > 0 ? View.VISIBLE : View.GONE);
                        }
                    }
                }, true);

                View userDeleteView = holder.v(R.id.user_delete_view);
                userDeleteView.setVisibility(View.GONE);
                if (TextUtils.equals(headerBean.getUid(), UserCache.getUserAccount())) {
                    userDeleteView.setVisibility(View.VISIBLE);
                    userDeleteView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //删除动态
                            add(RRetrofit.create(DiscussService.class)
                                    .delete(Param.buildMap("discuss_id:" + headerBean.getDiscuss_id()))
                                    .compose(Rx.transformer(String.class))
                                    .doOnSubscribe(new Action0() {
                                        @Override
                                        public void call() {
                                            HnLoading.show(mILayout).addDismissListener(DynamicDetailUIView.this);
                                        }
                                    })
                                    .subscribe(new RSubscriber<String>() {
                                        @Override
                                        public void onSucceed(String bean) {
                                            T_.ok(bean);
                                            HnLoading.hide();
                                            finishIView();
                                        }

                                        @Override
                                        public void onError(int code, String msg) {
                                            super.onError(code, msg);
                                            T_.error(msg);
                                        }

                                        @Override
                                        public void onEnd() {
                                            super.onEnd();
                                            HnLoading.hide();
                                        }
                                    }));
                        }
                    });
                }
            }

            @Override
            protected void onBindDataView(final RBaseViewHolder holder, int posInData, final CommentListBean.DataListBean dataBean) {
                super.onBindDataView(holder, posInData, dataBean);
                holder.fillView(dataBean, true);
                View deleteView = holder.v(R.id.delete_view);
                if (TextUtils.equals(dataBean.getUid(), UserCache.getUserAccount())) {
                    deleteView.setVisibility(View.VISIBLE);
                    deleteView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteItem(dataBean);
                            add(RRetrofit.create(SocialService.class)
                                    .removeComment(Param.buildMap("type:comment",
                                            "item_id:" + dataBean.getComment_id()))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new RSubscriber<String>() {
                                        @Override
                                        public void onError(int code, String msg) {
                                            super.onError(code, msg);
                                            T_.error(msg);
                                        }
                                    }));
                        }
                    });
                } else {
                    deleteView.setVisibility(View.GONE);
                }
                final TextView contentView = holder.v(R.id.content);
                final TextView seeAllView = holder.v(R.id.see_all_view);
                seeAllView.setVisibility(View.GONE);
                seeAllView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentView.setMaxLines(Integer.MAX_VALUE);
                        seeAllView.setVisibility(View.GONE);
                    }
                });
                contentView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Layout layout = contentView.getLayout();
                        if (layout != null) {
                            int lines = layout.getLineCount();
                            if (lines > 0) {
                                if (layout.getEllipsisCount(lines - 1) > 0) {
                                    seeAllView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }, Constant.DEBOUNCE_TIME);
                DynamicCommentControl.bindLikeItemView(mSubscriptions, holder, dataBean, null);

                holder.v(R.id.avatar).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new UserDetailUIView2(dataBean.getUid()));
                    }
                });
            }
        };
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString(mActivity.getString(R.string.dynamic_detail))
                .setFloating(false)
                .setTitleHide(false)
                .setTitleBarBGColor(SkinHelper.getSkin().getThemeColor());
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected boolean hasScrollListener() {
        return false;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        loadData();
    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        Observable
                .zip(
                        RRetrofit.create(DiscussService.class)
                                .detail(Param.buildMap("discuss_id:" + discuss_id, "uid:" + UserCache.getUserAccount()))
                                .compose(Rx.transformer(UserDiscussListBean.DataListBean.class))
                                .asObservable(),
                        RRetrofit.create(SocialService.class)
                                .commentList(Param.buildMap("page:" + page, "item_id:" + discuss_id, "type:discuss", "uid:" + UserCache.getUserAccount()))
                                .compose(Rx.transformer(CommentListBean.class))
                                .asObservable(),
                        new Func2<UserDiscussListBean.DataListBean, CommentListBean, String>() {
                            @Override
                            public String call(UserDiscussListBean.DataListBean dataListBean, CommentListBean dataListBean2) {
                                showContentLayout();
                                if (dataListBean != null) {
                                    List<UserDiscussListBean.DataListBean> headList = new ArrayList<>();
                                    headList.add(dataListBean);
                                    mRExBaseAdapter.resetHeaderData(headList);
                                }
                                if (dataListBean2 != null) {
                                    onUILoadDataEnd(dataListBean2.getData_list());
                                } else {
                                    onUILoadDataEnd();
                                }
                                return null;
                            }
                        }
                )
                .compose(Rx.<String>transformer())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadView();
                    }
                })
                .subscribe(new RSubscriber<String>() {
                    @Override
                    public void onEnd() {
                        super.onEnd();
                        hideLoadView();
                        onUILoadDataFinish();
                    }
                });
    }

    /**
     * 发送评价
     */
//    @OnClick({R.id.send_view})
    public void onSendClick(View view) {
        add(RRetrofit.create(SocialService.class)
                .comment(Param.buildMap("type:discuss", "item_id:" + discuss_id, "content:" + mInputView.string()))
                .compose(Rx.transformer(String.class))
                .subscribe(new RSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        loadData();
                        try {
                            mRecyclerView.smoothScrollToPosition(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        T_.error(msg);
                    }
                }));
        mInputView.setText("");
    }

    /**
     * 输入框文本变化
     */
//    @OnTextChanged(R.id.input_view)
    public void onInputTextChanged(Editable editable) {
        mSendView.setEnabled(!TextUtils.isEmpty(editable));
    }

    /**
     * 表情切换
     */
//    @OnClick({R.id.emoji_control_layout})
    public void onEmojiControlClick(CompoundButton view) {
        if (view.isChecked()) {
            mDynamicRootLayout.showEmojiLayout();
        } else {
            mDynamicRootLayout.hideEmojiLayout();
        }
    }

    @Override
    public void onDismiss() {
        onCancel();
    }

}
