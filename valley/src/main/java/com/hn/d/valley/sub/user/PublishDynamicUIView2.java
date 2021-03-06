package com.hn.d.valley.sub.user;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.UIParam;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RDragRecyclerView;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.Json;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.string.SingleTextWatcher;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RExTextView;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.angcyo.uiview.widget.SoftRelativeLayout;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.adapter.HnAddImageAdapter2;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Action;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.iview.RelayVideoLongClickListener;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.base.oss.OssHelper;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.event.UpdateDataEvent;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.control.PublishTaskRealm;
import com.hn.d.valley.control.TagsControl;
import com.hn.d.valley.control.TopControl;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.control.VideoStatusInfo;
import com.hn.d.valley.control.VoiceStatusInfo;
import com.hn.d.valley.emoji.IEmoticonSelectedListener;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.me.setting.BindPhoneUIView;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.message.redpacket.PayUIDialog;
import com.hn.d.valley.main.message.redpacket.ThirdPayUIDialog;
import com.hn.d.valley.main.message.session.EmojiLayoutControl;
import com.hn.d.valley.main.other.AmapUIView;
import com.hn.d.valley.main.wallet.SetPayPwdUIView;
import com.hn.d.valley.main.wallet.WalletAccount;
import com.hn.d.valley.main.wallet.WalletHelper;
import com.hn.d.valley.realm.RRealm;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.utils.HnGlide;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.utils.RAmap;
import com.hn.d.valley.utils.RBus;
import com.hn.d.valley.widget.HnExEditText;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnTopImageView;
import com.hn.d.valley.x5.X5WebUIView;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action3;

import static com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter.Options.DEFALUT_LIMIT;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：新的 发布动态
 * 创建人员：Robi
 * 创建时间：2017/05/02 16:21
 * 修改人员：Robi
 * 修改时间：2017/05/02 16:21
 * 修改备注：
 * Version: 1.0.0
 */
public class PublishDynamicUIView2 extends BaseContentUIView {

    private static boolean isShowTip = false;
    private static List<Tag> mLastSelectorTags = new ArrayList<>();//保存上一次选择标签(只在图文动态下有效)
    private static List<Tag> mLastVideoSelectorTags = new ArrayList<>();//保存上一次选择标签(只在视频动态下有效)
    Action0 mPublishAction;
    UserDiscussListBean.DataListBean mDataListBean;
    String mContent;
    ForwardInformationBean mForwardInformationBean;
    List<FriendBean> friendBean = new ArrayList<>();
    private HnAddImageAdapter2 mAddImageAdapter2;
    private List<Luban.ImageItem> photos = new ArrayList<>();
    private List<Tag> mSelectorTags = new ArrayList<>();
    private DynamicType mDynamicType;
    private AmapBean mTargetLocation;
    private RSoftInputLayout mSoftInputLayout;
    private HnExEditText mInputView;
    private EmojiLayoutControl mEmojiLayoutControl;
    private List<String> atUsers = new ArrayList<>();//@的用户
    private List<FriendBean> mFriendList = new ArrayList<>();
    private VideoStatusInfo mVideoStatusInfo;
    private HotInfoListBean mHotInfoListBean;
    private PublishTaskRealm mPublishTaskRealm;
    private List<String> visiableFriends = new ArrayList<>();//可见或不可见好友
    private DynamicVisiableLevelUIView.LevelType levelType = DynamicVisiableLevelUIView.LevelType.PUBLIC;// 可见类型 默认公开

    /**
     * 发布动态
     */
    public PublishDynamicUIView2(DynamicType dynamicType) {
        mDynamicType = dynamicType;
        if (mDynamicType == DynamicType.IMAGE) {
            mSelectorTags.addAll(mLastSelectorTags);
        }
    }

    /**
     * 转发动态
     */
    public PublishDynamicUIView2(UserDiscussListBean.DataListBean dataListBean) {
        mDataListBean = dataListBean;
        String mediaType = mDataListBean.getMedia_type();
        if ("3".equalsIgnoreCase(mediaType)) {
            mDynamicType = DynamicType.FORWARD_IMAGE;
            if (dataListBean.getMediaList().isEmpty()) {
                mDynamicType = DynamicType.FORWARD_TEXT;
            }
        } else if ("2".equalsIgnoreCase(mediaType)) {
            mDynamicType = DynamicType.FORWARD_VIDEO;
        } else if ("4".equalsIgnoreCase(mediaType)) {
            mDynamicType = DynamicType.FORWARD_VOICE;//允许转发语音, 星期三 2017-8-2
        } else if ("1".equalsIgnoreCase(mediaType)) {
            mDynamicType = DynamicType.FORWARD_TEXT;
        } else {
            mDynamicType = DynamicType.FORWARD_PACKET;
        }
    }

    /**
     * 发布视频
     */
    public PublishDynamicUIView2(VideoStatusInfo videoStatusInfo) {
        mVideoStatusInfo = videoStatusInfo;
        mDynamicType = DynamicType.VIDEO;
        mSelectorTags.addAll(mLastVideoSelectorTags);
    }


    /**
     * 转发资讯
     */
    public PublishDynamicUIView2(HotInfoListBean hotInfoListBean) {
        mHotInfoListBean = hotInfoListBean;
        mDynamicType = DynamicType.FORWARD_INFORMATION;

        mForwardInformationBean = new ForwardInformationBean();
        mForwardInformationBean.setAuthor(mHotInfoListBean.getAuthor());
        mForwardInformationBean.setLogo(mHotInfoListBean.getLogo());
        if (hotInfoListBean.isVideo()) {
            mForwardInformationBean.setMedia(mHotInfoListBean.getImgs() + "?" + mHotInfoListBean.getMedia());
        } else {
            mForwardInformationBean.setMedia(mHotInfoListBean.getImgs());
        }
        mForwardInformationBean.setMedia_type(mHotInfoListBean.getType());
        mForwardInformationBean.setNews_id(String.valueOf(mHotInfoListBean.getId()));
        mForwardInformationBean.setTitle(mHotInfoListBean.getTitle());
    }

    public PublishDynamicUIView2(PublishTaskRealm taskRealm) {
        mPublishTaskRealm = taskRealm;
        mContent = taskRealm.getShowContent2();
        mSelectorTags.addAll(taskRealm.getSelectorTags2());

        if (DynamicType.isVideo(taskRealm.getType())) {
            mDynamicType = DynamicType.VIDEO;
            mVideoStatusInfo = new VideoStatusInfo(taskRealm.getVideoStatusInfo().getVideoThumbPath(),
                    taskRealm.getVideoStatusInfo().getVideoPath());
        } else if (DynamicType.isImage(taskRealm.getType())) {
            mDynamicType = DynamicType.IMAGE;
            photos = taskRealm.getPhotos2();
        } else if (DynamicType.isText(taskRealm.getType())) {
            mDynamicType = DynamicType.TEXT;
        } else {
            mDynamicType = DynamicType.TEXT;
        }
    }

    public PublishDynamicUIView2 setPublishAction(Action0 publishAction) {
        mPublishAction = publishAction;
        return this;
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_publish_dynamic2);
    }

    @Override
    public boolean onBackPressed() {
        if (isForwardInformation()) {
            return true;
        }
        boolean backPressed = mSoftInputLayout.requestBackPressed();
        if (backPressed) {
            if (!mInputView.isEmpty() || mAddImageAdapter2.getAllDatas().size() > 0 || mVideoStatusInfo != null) {
//                UIItemDialog.build()
//                        .addItem(getString(R.string.save_draft), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                RRealm.exe(new Realm.Transaction() {
//                                    @Override
//                                    public void execute(Realm realm) {
//                                        if (mPublishTaskRealm != null) {
//                                            mPublishTaskRealm.deleteFromRealm();
//                                        }
//                                        PublishTaskRealm.save(getPublishTaskRealm());
//                                        finishIView(PublishDynamicUIView2.this, new UIParam(true, true, false));
//                                    }
//                                });
//                            }
//                        })
//                        .addItem(getString(R.string.no_save), new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                finishIView(PublishDynamicUIView2.this, new UIParam(true, true, false));
//                            }
//                        })
//                        .showDialog(mParentILayout);

                UIDialog.build().setDialogContent(getString(R.string.save_draft_tip))
                        .setOkText(getString(R.string.save))
                        .setCancelText(getString(R.string.no_save))
                        .setOkClick(new UIDialog.OnDialogClick() {
                            @Override
                            public void onDialogClick(UIDialog dialog, View clickView) {
                                RRealm.exe(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        if (mPublishTaskRealm != null) {
                                            mPublishTaskRealm.deleteFromRealm();
                                        }
                                        PublishTaskRealm.save(getPublishTaskRealm(""));
                                        finishIView(PublishDynamicUIView2.this, new UIParam(true, true, false));
                                    }
                                });
                            }
                        })
                        .setCancelClick(new UIDialog.OnDialogClick() {
                            @Override
                            public void onDialogClick(UIDialog dialog, View clickView) {
                                finishIView(PublishDynamicUIView2.this, new UIParam(true, true, false));
                            }
                        })
                        .showDialog(mParentILayout);
                return false;
            }
        }
        return backPressed;
    }

    private boolean isForwardInformation() {
        return mDynamicType == DynamicType.FORWARD_INFORMATION;
    }

    @Override
    protected String getTitleString() {
        if (isForwardInformation()) {
            return getString(R.string.forward_information);
        }
        return isForward() ? getString(R.string.forward_dynamic) : getString(R.string.publish_dynamic);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setShowBackImageView(true)
                .addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.send_forward_dynamic_n, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInputView.isEmpty()) {
                            if (isForward()) {
                                mInputView.setText(getContent(mInputView.string()));
                            } else {
//                                mInputView.error();
//                                return;
                                if (mDynamicType == DynamicType.IMAGE &&
                                        mAddImageAdapter2.getAllDatas().size() == 0) {
                                    T_.error(getString(R.string.publish_empty_tip));
                                    return;
                                }
                            }
                        }

                        checkDynamicType();
                        if (mDynamicType == DynamicType.VIDEO) {
                            final ExEditText hotMoneyView = mViewHolder.v(R.id.hot_money_view);
                            final ExEditText hotNumView = mViewHolder.v(R.id.hot_num_view);

                            if (isHotPackageLayoutExpand()) {
                                if (hotMoneyView.checkEmpty()) {
                                    scrollToBottom(hotMoneyView);
                                    return;
                                }
                                if (hotNumView.checkEmpty()) {
                                    scrollToBottom(hotNumView);
                                    return;
                                }
                                if (Float.valueOf(hotMoneyView.string()) < 0.01f) {
                                    T_.error("红包金额至少需要0.01元");
                                    scrollToBottom(hotMoneyView);
                                    return;
                                }
                                if (Float.valueOf(hotNumView.string()) < 1f) {
                                    T_.error("红包个数至少需要1个");
                                    scrollToBottom(hotNumView);
                                    return;
                                }
                                if (Float.valueOf(hotMoneyView.string()) < 0.01f * Integer.valueOf(hotNumView.string())) {
                                    T_.error("平均红包金额需要大于0.01元");
                                    return;
                                }
                                Action.publishAction();
                                // TODO: 2017/07/27 0027 调用发布红包接口
                                HnLoading.show(mILayout);
                                PublishControl.uploadDynamicVideo(getPublishTaskRealm(""), new Action1<String>() {
                                    @Override
                                    public void call(final String extend) {
                                        HnLoading.hide();
                                        if (WalletHelper.getInstance().getWalletAccount() == null) {
                                            WalletHelper.getInstance().fetchWallet(new RequestCallback<WalletAccount>() {
                                                @Override
                                                public void onStart() {

                                                }

                                                @Override
                                                public void onSuccess(WalletAccount o) {
                                                    if (o.hasPin()) {
                                                        sendRedbag(hotMoneyView, hotNumView, extend);
                                                    } else {
                                                        showPinDialog();
                                                    }
                                                }

                                                @Override
                                                public void onError(String msg) {

                                                }
                                            });
                                            return;
                                        }

                                        if (!WalletHelper.getInstance().getWalletAccount().hasPin()) {
                                            showPinDialog();
                                        } else {
                                            sendRedbag(hotMoneyView, hotNumView, extend);
                                        }
                                    }
                                });
                                return;
                            }
                        }

                        Action.publishAction();
                        onPublish("");
                    }
                }));
    }

    private void sendRedbag(ExEditText hotMoneyView, ExEditText hotNumView, String extend) {
        Float money = Float.valueOf(hotMoneyView.string()) * 100;
        int num = Integer.valueOf(hotNumView.string());
        PayUIDialog.Params params = new PayUIDialog.Params();
        params.setBalance(WalletHelper.getInstance().getWalletAccount().getMoney())
                .setMoney(money)
                .setTo_square("1")
                .setExtend(extend)
                .setRandom(1)
                .setType(1)
                .setContent("视频红包")
                .setNum(num);

        if (money > WalletHelper.getInstance().getWalletAccount().getMoney()) {
            T_.show(getString(R.string.text_balance_not_enough));
            params.enableBalance(false);
            startIView(new ThirdPayUIDialog(new Action1() {
                @Override
                public void call(Object o) {
                    finishIView();
                }
            }, params, ThirdPayUIDialog.ALIPAY, 1));
            return;
        }

        startIView(new PayUIDialog(new Action1() {
            @Override
            public void call(Object o) {
                if (mPublishAction != null) {
                    // 插入一条临时动态
//                    UserDiscussListBean.DataListBean dynamicContentBean = PublishControl.createBean(getPublishTaskRealm(""));
//                    PublishControl.instance().getDataListBeen().add(dynamicContentBean);
//                    mPublishAction.call();
//                    PublishControl.instance().getDataListBeen().remove(dynamicContentBean);
                    // 刷新视频红包动态
                    finishIView(PublishDynamicUIView2.this, new UIParam().setUnloadRunnable(new Runnable() {
                        @Override
                        public void run() {
                            RBus.post(Constant.TAG_UPDATE_CIRCLE, new UpdateDataEvent());
                        }
                    }));
                }
            }
        }, params));
    }

    private void showPinDialog() {
        UIDialog.build()
                .setDialogContent(mActivity.getString(R.string.text_no_set_pwd_please_set_pwd))
                .setOkText(mActivity.getString(R.string.text_set_pay_pwd))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 先判断是否绑定手机
                        if (!UserCache.instance().isBindPhone()) {
                            startIView(new BindPhoneUIView());
                            T_.show(mActivity.getString(R.string.text_unselected_phonenumber));
                        } else {
                            startIView(new SetPayPwdUIView(SetPayPwdUIView.SETPAYPWD));
                        }
                    }
                })
                .setCancelText(getString(R.string.cancel))
                .showDialog(mParentILayout);
    }

    protected void checkDynamicType() {
        if (mDynamicType == DynamicType.IMAGE && mAddImageAdapter2.getAllDatas().size() == 0) {
            mDynamicType = DynamicType.TEXT;
        } else if (mDynamicType == DynamicType.TEXT && mAddImageAdapter2.getAllDatas().size() > 1 /**加号要除掉*/) {
            mDynamicType = DynamicType.IMAGE;
            if (mInputView.isEmpty()) {
                mInputView.setText(R.string.publish_image);
            }
        } else if (mVideoStatusInfo != null) {
            mDynamicType = DynamicType.VIDEO;
        }

        int emptyRes;
        if (mDynamicType == DynamicType.TEXT) {
            emptyRes = R.string.publish_text;
        } else if (mDynamicType == DynamicType.IMAGE) {
            emptyRes = R.string.publish_image;
        } else if (mDynamicType == DynamicType.VIDEO) {
            emptyRes = R.string.publish_video;
        } else {
            emptyRes = R.string.publish_dynamic_tip;
        }

        if (mInputView.isEmpty()) {
            mInputView.setText(emptyRes);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Observable<ArrayList<Luban.ImageItem>> observable = Image.onActivityResult(mActivity, requestCode, resultCode, data);
        if (observable != null) {
            observable.subscribe(new BaseSingleSubscriber<ArrayList<Luban.ImageItem>>() {
                @Override
                public void onStart() {
                    super.onStart();
                    HnLoading.show(mILayout, false);
                }

                @Override
                public void onSucceed(ArrayList<Luban.ImageItem> strings) {
                    if (BuildConfig.DEBUG) {
                        Luban.logFileItems(mActivity, strings);
                    }
                    photos = strings;
                    mAddImageAdapter2.resetData(strings);
                }

                @Override
                public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                    super.onEnd(isError, isNoNetwork, e);
                    HnLoading.hide();
                }
            });
        }
    }

    @Override
    public void onViewCreate(View rootView, UIParam param) {
        super.onViewCreate(rootView, param);
        ImagePickerHelper.clearAllSelectedImages();
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        //默认选中的标签
        initTags();
        initTagsView();

        initAddressView();

        initVisibleView(mViewHolder.tv(R.id.visible_view2));
        initVisibleView(mViewHolder.tv(R.id.visible_view));

        mSoftInputLayout = mViewHolder.v(R.id.soft_input_layout);

        //自动隐藏键盘
        mBaseRootLayout.setOnInterceptTouchListener(new SoftRelativeLayout.OnInterceptTouchListener() {
            @Override
            public void onInterceptTouchDown(MotionEvent event) {
            }

            @Override
            public void onTouchDown(MotionEvent event) {
                mSoftInputLayout.requestBackPressed();
            }
        });

        //输入控件
        mInputView = mViewHolder.v(R.id.input_view);
        TextIndicator textIndicator = mViewHolder.v(R.id.single_text_indicator_view);
        textIndicator.initIndicator(getResources().getInteger(R.integer.dynamic_status_text_count), mInputView);
        initInputLayout();

        //表情切换
        initEmojiLayout();
        initControlLayout();

        //图片
        final RDragRecyclerView recyclerView = mViewHolder.v(R.id.recycler_view);
        recyclerView.setDragCallback(new RDragRecyclerView.OnDragCallback() {
            @Override
            public boolean canDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (mAddImageAdapter2.isDeleteModel()) {
                    if (viewHolder.getItemViewType() != HnAddImageAdapter2.TYPE_ADD_ITEM) {
                        return true;
                    }
                }
                return false;
            }
        });
        mAddImageAdapter2 = new HnAddImageAdapter2(mActivity) {
            @Override
            protected void onBindView(RBaseViewHolder holder, int position, Luban.ImageItem bean) {
                int px = (int) ResUtil.dpToPx(mActivity.getResources(), 6) / 2;
                holder.itemView.setPadding(px, px, px, px);
                super.onBindView(holder, position, bean);
            }
        };
        mAddImageAdapter2.setMaxItemCount(9);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                mAddImageAdapter2.setItemHeight(recyclerView.getMeasuredWidth() / 3);
            }
        });
        //recyclerView.addItemDecoration(new RBaseItemDecoration((int) ResUtil.dpToPx(mActivity.getResources(), 6), Color.TRANSPARENT));
        mAddImageAdapter2.setAddListener(new HnAddImageAdapter2.OnAddListener() {

            @Override
            public void onAddClick(View view, int position, Luban.ImageItem item) {
                ImagePickerHelper.startImagePicker(mActivity, true, false, false, true, 9);
            }

            @Override
            public void onItemClick(View view, int position, Luban.ImageItem item) {
                ImagePagerUIView.start(mParentILayout, view,
                        PhotoPager.getImageItems2(mAddImageAdapter2.getAllDatas()), position);
            }

            @Override
            public void onItemLongClick(View view, int position, Luban.ImageItem item) {
                mAddImageAdapter2.setDeleteModel(recyclerView, true);
            }

            @Override
            public void onDeleteClick(View view, int position, Luban.ImageItem item) {
                mAddImageAdapter2.getAllDatas().remove(position);
                ImagePickerHelper.deleteItemFromSelected(item.path);
                if (position == 8) {
                    mAddImageAdapter2.notifyItemChanged(position);
                } else {
                    mAddImageAdapter2.notifyItemRemoved(position);
                    mAddImageAdapter2.notifyItemRangeChanged(position, 9);
                }
            }
        });
        recyclerView.setAdapter(mAddImageAdapter2);

        //标签选择
        mViewHolder.v(R.id.tag_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new TagsUIView(new Action1<List<Tag>>() {
                    @Override
                    public void call(List<Tag> tags) {
                        mSelectorTags = tags;
                        if (mDynamicType == DynamicType.IMAGE) {
                            mLastSelectorTags.clear();
                            mLastSelectorTags.addAll(tags);
                        } else if (mDynamicType == DynamicType.VIDEO) {
                            mLastVideoSelectorTags.clear();
                            mLastVideoSelectorTags.addAll(tags);
                        }
                        initTagsView();
                    }
                }, mSelectorTags).setIsVideo(mDynamicType == DynamicType.VIDEO));
            }
        });

        //位置选择
        mViewHolder.v(R.id.address_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.checkPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        new Action1<Boolean>() {
                            @Override
                            public void call(Boolean aBoolean) {
                                if (aBoolean ){
                                    RAmap.startLocation(true);
                                } else {
                                    T_.error("对应权限没有打开.");
                                }
                            }
                            });

                startIView(new AmapUIView(new Action1<AmapBean>() {
                    @Override
                    public void call(AmapBean amapBean) {
                        if (amapBean != null) {
                            mTargetLocation = amapBean;
                            initAddressView();
                        }
                    }
                }, null, UserCache.getUserAvatar(), true));
            }
        });

        //视频
        View videoControlLayout = mViewHolder.v(R.id.video_control_layout);
        if (mDynamicType == DynamicType.VIDEO) {
            recyclerView.setVisibility(View.GONE);
            videoControlLayout.setVisibility(View.VISIBLE);

            final ImageView videoThumbView = mViewHolder.v(R.id.video_thumb_view);
            HnGlide.displayFile(videoThumbView, mVideoStatusInfo.getVideoThumbPath());

            TextView videoTimeView = mViewHolder.v(R.id.video_time_view);
            UserDiscussItemControl.initVideoTimeView(videoTimeView, mVideoStatusInfo.getVideoPath());

            videoThumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new VideoPlayUIView(mVideoStatusInfo.getVideoPath(),
                            mVideoStatusInfo.getVideoThumbPath(),
                            RImageView.copyDrawable(videoThumbView),
                            OssHelper.getWidthHeightWithUrl(mVideoStatusInfo.getVideoThumbPath()))
                            .resetViewLocation(videoThumbView)
                            .setOnLongPress(new RelayVideoLongClickListener(mParentILayout)));
                }
            });

            //红包规则
            mViewHolder.click(R.id.hot_package_help_view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new X5WebUIView("http://wap.klgwl.com/agreement/rule/package.html"));
                }
            });

            //展开/关闭,红包金额/数量
            mViewHolder.click(R.id.hot_package_extend_view, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View controlView = mViewHolder.v(R.id.hot_package_control_view);
                    if (!isHotPackageLayoutExpand()) {
                        //展开
                        ViewAnimator.animate(controlView)
                                .translationY(-controlView.getMeasuredHeight(), 0)
                                .onStart(new AnimationListener.Start() {
                                    @Override
                                    public void onStart() {
                                        controlView.setVisibility(View.VISIBLE);
                                    }
                                })
                                .start();

                        ViewAnimator.animate(v).rotation(0, 180).start();
                    } else {
                        //关闭
                        ViewAnimator.animate(controlView)
                                .translationY(0, -controlView.getMeasuredHeight())
                                .onStop(new AnimationListener.Stop() {
                                    @Override
                                    public void onStop() {
                                        controlView.setVisibility(View.INVISIBLE);
                                    }
                                })
                                .start();
                        ViewAnimator.animate(v).rotation(180, 0).start();
                    }
                }
            });

            //输入框焦点, 自动滚动
            final ExEditText hotMoneyView = mViewHolder.v(R.id.hot_money_view);
            final ExEditText hotNumView = mViewHolder.v(R.id.hot_num_view);

            hotMoneyView.addTextChangedListener(new SingleTextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    super.afterTextChanged(s);
                    int maxNumber = (int) (100 * hotMoneyView.getInputNumber());
                    if (maxNumber > 0) {
                        hotNumView.setMaxNumber(maxNumber);
                        hotNumView.setText(String.valueOf(maxNumber));
                    } else {
                        hotNumView.setText("");
                    }
                }
            });

            View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(final View v, boolean hasFocus) {
                    if (hasFocus) {
                        scrollToBottom((ExEditText) v);
                    }
                }
            };

            hotMoneyView.setOnFocusChangeListener(focusChangeListener);
            hotNumView.setOnFocusChangeListener(focusChangeListener);
        }

        //转发动态
        if (isForward()) {
            recyclerView.setVisibility(View.GONE);

            mViewHolder.v(R.id.tag_view).setVisibility(View.GONE);
            mViewHolder.v(R.id.allow_box_view).setVisibility(View.GONE);
            mViewHolder.v(R.id.visible_view2).setVisibility(View.VISIBLE);

            mViewHolder.v(R.id.selector_control_layout).setVisibility(View.GONE);
            mViewHolder.v(R.id.forward_control_layout).setVisibility(View.VISIBLE);

            HnGlideImageView imageView = mViewHolder.v(R.id.avatar);
            mViewHolder.tv(R.id.username).setTextColor(SkinHelper.getSkin().getThemeSubColor());
            RExTextView contentView = mViewHolder.v(R.id.content);
            contentView.setFoldString("");
            contentView.setMaxShowLine(2);

            if (isForwardInformation()) {
                //转发资讯
                String mediaType = mHotInfoListBean.getType();
                String shareDes = mHotInfoListBean.getTitle();
                String thumbIco = mHotInfoListBean.getLogo();
                List<String> mediaList = mHotInfoListBean.getImgsList();

                imageView.setContentDescription(getString(R.string.is_circle));
                if (mHotInfoListBean.isPicture()) {
                    if (mediaList.size() > 0) {
                        thumbIco = mediaList.get(0);
                        imageView.setContentDescription("");
                    }
                } else if (mHotInfoListBean.isText()) {
                    if (mediaList.size() > 0) {
                        thumbIco = mediaList.get(0);
                        imageView.setContentDescription("");
                    }
                } else if (mHotInfoListBean.isVideo()) {
                    if (mediaList.size() > 0) {
                        thumbIco = mediaList.get(0);
                        imageView.setContentDescription("");
                    }
                    String videoUrl = mHotInfoListBean.getMedia();
                    if (TextUtils.isEmpty(videoUrl)) {
                        //从动态列表里面转发的
                        thumbIco = mHotInfoListBean.getInformationVideoThumbUrl();
                        videoUrl = mHotInfoListBean.getInformationVideoUrl();
                    } else {
                        //从资讯详情转发的

                    }
                } else if ("voice".equalsIgnoreCase(mediaType)) {
                    if (mediaList.size() > 0) {
                        String s = UserDiscussItemControl.getVideoParams(mediaList.get(0))[0];
                        if (!TextUtils.isEmpty(s) && !VoiceStatusInfo.NOPIC.equalsIgnoreCase(s)) {
                            thumbIco = s;
                            imageView.setContentDescription("");
                        }
                    }
                }
                //头像, 图片, 视频缩略图
                imageView.setImageUrl(thumbIco, false);
                mViewHolder.tv(R.id.username).setText(mHotInfoListBean.getAuthor());

                MoonUtil.show(mActivity, contentView, shareDes);

            } else if ("0".equalsIgnoreCase(mDataListBean.getShare_original_item_id())) {
                //不是已经被转发过的动态
                if (mDynamicType == DynamicType.FORWARD_TEXT) {
                    imageView.setContentDescription(getString(R.string.is_circle));
                    imageView.setImageThumbUrl(mDataListBean.getUser_info().getAvatar());
                } else {
                    imageView.setImageUrl(mDataListBean.getMediaList().get(0));
                }

                mViewHolder.tv(R.id.username).setText(mDataListBean.getUser_info().getUsername());

                MoonUtil.show(mActivity, contentView, getContent(mDataListBean.getContent()));
            } else {
                //已经被转发过的动态
                if (mDynamicType == DynamicType.FORWARD_TEXT) {
                    imageView.setContentDescription(getString(R.string.is_circle));
                    imageView.setImageThumbUrl(mDataListBean.getOriginal_info().getAvatar());
                } else {
                    imageView.setImageUrl(mDataListBean.getOriginal_info().getMediaList().get(0));
                }

                mViewHolder.tv(R.id.username).setText(mDataListBean.getOriginal_info().getUsername());

                MoonUtil.show(mActivity, contentView, getContent(mDataListBean.getOriginal_info().getContent()));
            }
        }


        //重新编辑的动态
        if (mPublishTaskRealm != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mInputView.setInputText(mContent);
                    mInputView.showEmoji();
                    mInputView.requestFocus();
                    mAddImageAdapter2.resetData(photos);
                }
            });
        }
    }

    /**
     * 滚动到最底部, 方便键盘输入
     */
    private void scrollToBottom(final ExEditText v) {
        v.post(new Runnable() {
            @Override
            public void run() {
                v.setSelectionLast();
            }
        });

        postDelayed(new Runnable() {
            @Override
            public void run() {
                NestedScrollView scrollView = mViewHolder.v(R.id.scroll_view);
                scrollView.smoothScrollBy(0, scrollView.getChildAt(0).getMeasuredHeight());
            }
        }, 300);
    }

    /**
     * 红包布局是否展开, 展开表示需要发送红包
     */
    private boolean isHotPackageLayoutExpand() {
        return mViewHolder.v(R.id.hot_package_extend_view).getRotation() == 180;
    }

    private String getContent(String content) {
        if (TextUtils.isEmpty(content)) {
            if (mDynamicType == DynamicType.FORWARD_VIDEO) {
                content = getString(R.string.forward_video_tip);
            } else if (mDynamicType == DynamicType.FORWARD_VOICE) {
                content = getString(R.string.forward_voice_tip);
            } else if (mDynamicType == DynamicType.FORWARD_INFORMATION) {
                content = getString(R.string.forward_information);
            } else {
                content = getString(R.string.forward_tip);
            }
        }
        return content;
    }

    /**
     * 是否是转发的动态
     */
    private boolean isForward() {
        return mPublishTaskRealm == null && mDynamicType.getValue() >= 6;
    }

    private void initVisibleView(final TextView visibleView) {
//        final TextView visibleView = mViewHolder.tv(R.id.visible_view);
        visibleView.setText(levelType.getLevel());
        visibleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIView(new DynamicVisiableLevelUIView(PublishDynamicUIView2.this.levelType,
                        PublishDynamicUIView2.this.friendBean)
                        .setSelectionAction(new Action3<DynamicVisiableLevelUIView.LevelType, List<String>, List<FriendBean>>() {
                            @Override
                            public void call(DynamicVisiableLevelUIView.LevelType levelType, List<String> list, List<FriendBean> friendBeen) {
                                L.d(PublishDynamicUIView2.class.getSimpleName(), "type : " + levelType.name() + " friends : " + list.toString());
                                PublishDynamicUIView2.this.levelType = levelType;
                                PublishDynamicUIView2.this.visiableFriends = list;
                                PublishDynamicUIView2.this.friendBean = friendBeen;

                                visibleView.setText(levelType.getLevel());
                            }
                        }));
            }
        });
    }

    private void initAddressView() {
        TextView addressView = mViewHolder.tv(R.id.address_view);
        if (mTargetLocation == null) {
            addressView.setText(R.string.where_are_you);
        } else {
            if (TextUtils.isEmpty(mTargetLocation.title)) {
                addressView.setText(mTargetLocation.address);
            } else {
                addressView.setText(mTargetLocation.title);
            }
        }
    }

    private void initTagsView() {
        StringBuilder build = new StringBuilder();
        for (Tag t : mSelectorTags) {
            build.append(t.getName());
            build.append("、");
        }
        mViewHolder.tv(R.id.tag_view).setText(RUtils.safe(build));
    }

    private void initTags() {
        if (mPublishTaskRealm != null) {
            return;
        }
        for (Tag t : TagsControl.getAllTags()) {
            if (mDynamicType == DynamicType.IMAGE) {
                if (TextUtils.equals(t.getName(), getString(R.string.life))) {
                    if (mSelectorTags.isEmpty()) {
                        mSelectorTags.add(t);
                    }
                }
            } else if (mDynamicType == DynamicType.VIDEO) {
                if (TextUtils.equals(t.getName(), getString(R.string.video))) {
                    if (!mSelectorTags.contains(t)) {
                        mSelectorTags.add(t);
                    }
                }
            } else if (mDynamicType == DynamicType.VOICE) {
                if (TextUtils.equals(t.getName(), getString(R.string.voice))) {
                    if (!mSelectorTags.contains(t)) {
                        mSelectorTags.add(t);
                    }
                }
            }
        }
    }

    private void initEmojiLayout() {
        final ImageView imageView = mViewHolder.v(R.id.ico_exp);

        mSoftInputLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
                if (isEmojiShow) {
                    imageView.setImageResource(R.drawable.icon_keyboard);
                    mEmojiLayoutControl.emotNotifiDataSetChanged();
                } else {
                    imageView.setImageResource(R.drawable.biaoqing_fabudongtai_n);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show emoji
                mEmojiLayoutControl.showEmoji();

                if (mSoftInputLayout.isEmojiShow()) {
                    mSoftInputLayout.showSoftInput(mInputView);
                } else {
                    mSoftInputLayout.showEmojiLayout();
                }
            }
        });

        mEmojiLayoutControl = new EmojiLayoutControl(mViewHolder, new IEmoticonSelectedListener() {
            @Override
            public void onEmojiSelected(String emoji) {
                if (emoji.equals("/DEL")) {
                    mInputView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else {
                    final int selectionStart = mInputView.getSelectionStart();
                    mInputView.getText().insert(selectionStart, emoji);
                    MoonUtil.show(mActivity, mInputView, mInputView.getText().toString());
                    mInputView.setSelection(selectionStart + emoji.length());
                    mInputView.requestFocus();
                }
            }

            @Override
            public void onStickerSelected(String categoryName, String stickerName) {

            }
        });
    }

    private void initControlLayout() {

        if (isForwardInformation()) {
            //mViewHolder.v(R.id.ico_at).setVisibility(View.GONE);//也需要@功能
        }

        mViewHolder.v(R.id.ico_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerHelper.startImagePicker(mActivity, true, true, false, false, 1);
            }
        });
        mViewHolder.v(R.id.ico_at).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mInputView.insert("@");
                selectorContact();
            }
        });
        mViewHolder.v(R.id.ico_gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //置顶
        final HnTopImageView hnTopImageView = mViewHolder.v(R.id.ico_top);
        hnTopImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowTip) {
                    UIDialog.build().setDialogTitle(mActivity.getString(R.string.tip))
                            .setDialogContent(mActivity.getString(R.string.dynamic_top_tip))
                            .setCancelText("").setOkText(mActivity.getString(R.string.known))
                            .setCanCanceledOnOutside(false)
                            .showDialog(PublishDynamicUIView2.this);
                    if (!BuildConfig.DEBUG) {
                        isShowTip = true;
                    }
                }
                if (hnTopImageView.isTop()) {
                    hnTopImageView.setTop(false);
                } else {
                    TopControl.Companion.canTop().subscribe(new BaseSingleSubscriber<Boolean>() {
                        @Override
                        public void onSucceed(Boolean bean) {
                            super.onSucceed(bean);
                            if (bean) {
                                hnTopImageView.setTop(true);
                            } else {
                                UIDialog.build().setDialogTitle(mActivity.getString(R.string.tip))
                                        .setDialogContent(getString(R.string.max_top_tip, TopControl.Companion.getTopBean().getTotal() + ""))
                                        .setCancelText("").setOkText(mActivity.getString(R.string.known))
                                        .setCanCanceledOnOutside(false)
                                        .showDialog(PublishDynamicUIView2.this);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initInputLayout() {
        ExEditText editText = mViewHolder.v(R.id.input_view);
        editText.addTextChangedListener(new SingleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                checkAtUser();
            }
        });
        //输入@字符, 自动弹出选择联系人
        editText.setOnMentionInputListener(new ExEditText.OnMentionInputListener() {
            @Override
            public void onMentionCharacterInput() {
                selectorContact();
            }

            @Override
            public void onMentionTextChanged(List<String> allMention) {
//                for (String s : allMention) {
//                    L.e("call: onMentionTextChanged([allMention])-> " + s);
//                }
            }
        });
    }

    /**
     * 文本输入框删除@用户之后
     */
    private void checkAtUser() {
        String string = mInputView.string();
        for (int i = mFriendList.size() - 1; i >= 0; i--) {
            FriendBean friendBean = mFriendList.get(i);
            if (!string.contains(friendBean.getTrueName())) {
                mFriendList.remove(i);
                atUsers.remove(friendBean.getUid());
            }
        }
    }


    /**
     * 选择联系人
     */
    private void selectorContact() {
        final ExEditText mInputView = mViewHolder.v(R.id.input_view);

        ContactSelectUIVIew.start(mILayout,
                new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_MULTI, DEFALUT_LIMIT, true),
                atUsers, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseRxView,
                                     List<AbsContactItem> absContactItems,
                                     RequestCallback requestCallback) {
                        atUsers.clear();
                        mFriendList.clear();

                        requestCallback.onSuccess("");

                        for (AbsContactItem item : absContactItems) {
                            if (item instanceof ContactItem) {
                                FriendBean friendBean = ((ContactItem) item).getFriendBean();
                                atUsers.add(friendBean.getUid());
                                mFriendList.add(friendBean);
                                mInputView.addMention(friendBean.getTrueName());
                            }
                        }
                    }
                });
    }

    /**
     * 点击发布动态
     */
    private void onPublish(String package_id) {
        //创建发布任务, 在后台进行发布
        HnTopImageView hnTopImageView = mViewHolder.v(R.id.ico_top);
        CheckBox allowDownloadView = mViewHolder.v(R.id.allow_box_view);

        if (mDynamicType.getValue() < 6) {
            if (mPublishTaskRealm != null) {
                RRealm.exe(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mPublishTaskRealm.deleteFromRealm();
                    }
                });
            }

            //小于6,表示是发布动态
            PublishTaskRealm publishTask = getPublishTaskRealm(package_id);
            PublishControl.instance().addTask(publishTask, true);

            finishIView();
            if (mPublishAction != null) {
                mPublishAction.call();
            } else {
                PublishControl.instance().startPublish();
            }
        } else {
            //转发动态,转发资讯 ,不需要再后台进行
            HnLoading.show(mParentILayout);
            String item_id;
            if (isForwardInformation()) {
                item_id = String.valueOf(mHotInfoListBean.getId());
            } else {
                if ("0".equalsIgnoreCase(mDataListBean.getShare_original_item_id())) {
                    item_id = mDataListBean.getDiscuss_id();
                } else {
                    item_id = mDataListBean.getOriginal_info().getDiscuss_id();
                }
            }
            add(RRetrofit.create(SocialService.class)
                    .forward(Param.buildMap(
                            "type:" + (isForwardInformation() ? "news" : "discuss"),
                            "item_id:" + item_id,
                            "is_top:" + hnTopImageView.isTop(),
                            "content:" + getForwardContent(),
                            "scan_type:" + levelType.getId(),
                            "scan_user:" + RUtils.connect(visiableFriends))
                    )
                    .compose(Rx.transformer(String.class))
                    .subscribe(new BaseSingleSubscriber<String>() {
                        @Override
                        public void onSucceed(String s) {
                            T_.show(getString(R.string.forward_succeed));
                            finishIView();
                            RBus.post(Constant.TAG_UPDATE_CIRCLE, new UpdateDataEvent());
                        }

                        @Override
                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                            super.onEnd(isError, isNoNetwork, e);
                            HnLoading.hide();
                        }
                    }));
        }
    }

    /**
     * 获取转发内容
     */
    private String getForwardContent() {
//        if (isForwardInformation()) {
//            mForwardInformationBean.setContent(mInputView.string());
//            return Json.to(mForwardInformationBean);
//        }


        String mentionString = mInputView.fixMentionString(new ExEditText.getIdFromUserName() {
            @Override
            public String userId(String userName) {
                for (FriendBean bean : mFriendList) {
                    if (TextUtils.equals(userName, bean.getTrueName())) {
                        return bean.getUid();
                    }
                }
                return "";
            }
        });

        if (isForwardInformation()) {
            mForwardInformationBean.setContent(mentionString);
            return Json.to(mForwardInformationBean);
        } else {
            return mentionString;
        }
    }

    @NonNull
    private PublishTaskRealm getPublishTaskRealm(String package_id) {
        checkDynamicType();

        HnTopImageView hnTopImageView = mViewHolder.v(R.id.ico_top);
        CheckBox allowDownloadView = mViewHolder.v(R.id.allow_box_view);

        PublishTaskRealm publishTask = new PublishTaskRealm();
        if (mDynamicType == DynamicType.IMAGE) {
            publishTask = new PublishTaskRealm(photos);
        } else if (mDynamicType == DynamicType.VIDEO) {
            publishTask = new PublishTaskRealm(mVideoStatusInfo);
        } else if (mDynamicType == DynamicType.TEXT) {
            publishTask = new PublishTaskRealm();
        }
        publishTask.setSelectorTags2(mSelectorTags)
                .setTop(hnTopImageView.isTop())
                .setShareLocation(mTargetLocation != null)
                .setAddress(getAddress())
                .setLat(getLatitude())
                .setLng(getLongitude())
                .setAllow_download(allowDownloadView.isChecked() ? 1 : 0)
                .setScan_type(levelType.getId())
                .setScan_user(RUtils.connect(visiableFriends))
                .setShowContent(mInputView.fixShowMentionString(new ExEditText.getIdFromUserName() {
                    @Override
                    public String userId(String userName) {
                        for (FriendBean bean : mFriendList) {
                            if (TextUtils.equals(userName, bean.getTrueName())) {
                                return bean.getUid();
                            }
                        }
                        return "";
                    }
                }))
                .setShowContent2(mInputView.string())
                .setContent(mInputView.fixMentionString(new ExEditText.getIdFromUserName() {
                    @Override
                    public String userId(String userName) {
                        for (FriendBean bean : mFriendList) {
                            if (TextUtils.equals(userName, bean.getTrueName())) {
                                return bean.getUid();
                            }
                        }
                        return "";
                    }
                }))
                .setPackage_id(package_id)
        ;
        return publishTask;
    }

    private String getAddress() {
        if (mTargetLocation == null) {
            return "";
        }
        return mTargetLocation.address;
    }

    private String getLatitude() {
        if (mTargetLocation == null) {
            return "";
        }
        return String.valueOf(mTargetLocation.latitude);
    }

    private String getLongitude() {
        if (mTargetLocation == null) {
            return "";
        }
        return String.valueOf(mTargetLocation.longitude);
    }

    /**
     * 发布动态
     */
    private void publish() {
//        if (forwardDataBean == null) {
//            add(RRetrofit.create(DiscussService.class)
//                    .publish(Param.buildMap(
//                            "tags:" + RUtils.connect(mSelectorTags),
//                            "media_type:3",
//                            "media:" + RUtils.connect(mUploadMedias),
//                            "is_top:" + (mTopBox.isChecked() ? 1 : 0),
//                            "open_location:" + (mShareBox.isChecked() ? 1 : 0),
//                            "content:" + mInputView.string(),
//                            "address:" + getAddress(),
//                            "lng:" + getLongitude(),
//                            "lat:" + getLatitude()))
//                    .compose(Rx.transformer(String.class))
//                    .subscribe(new BaseSingleSubscriber<String>() {
//                        @Override
//                        public void onSucceed(String s) {
//                            T_.show(s);
//                            finishIView();
//                            RBus.post(Constant.TAG_UPDATE_CIRCLE, new UpdateDataEvent());
//                        }
//
//                        @Override
//                        public void onEnd() {
//                            super.onEnd();
//                            HnLoading.hide();
//                        }
//                    })
//            );
//        } else {
//            add(RRetrofit.create(SocialService.class)
//                    .forward(Param.buildMap(
//                            "tags:" + RUtils.connect(mSelectorTags),
//                            "media_type:3",
//                            "type:discuss",
//                            "item_id:" + forwardDataBean.getDiscuss_id(),
//                            "is_top:" + (mTopBox.isChecked() ? 1 : 0),
//                            "open_location:" + (mShareBox.isChecked() ? 1 : 0),
//                            "content:" + mInputView.string(),
//                            "address:" + getAddress(),
//                            "lng:" + getLongitude(),
//                            "lat:" + getLatitude()))
//                    .compose(Rx.transformer(String.class))
//                    .subscribe(new BaseSingleSubscriber<String>() {
//                        @Override
//                        public void onSucceed(String s) {
//                            T_.show(s);
//                            finishIView();
//                            RBus.post(Constant.TAG_UPDATE_CIRCLE, new UpdateDataEvent());
//                        }
//
//                        @Override
//                        public void onEnd() {
//                            super.onEnd();
//                            HnLoading.hide();
//                        }
//                    })
//            );
//        }
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
    }

    @Override
    public void onViewHide() {
        super.onViewHide();
    }

    /**
     * 转发资讯的bean
     */
    public static class ForwardInformationBean {

        /**
         * content : 转发资讯
         * title : 习近平今天会见了特兰普
         * news_id : 1002
         * media : http://circleimg.klgwl.com/11.jpg
         * media_type : 3
         * logo : http://circleimg.klgwl.com/11.jpg
         * author : 头条新闻
         */

        private String content;
        private String title;
        private String news_id;
        private String media;
        private String media_type;
        private String logo;
        private String author;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNews_id() {
            return news_id;
        }

        public void setNews_id(String news_id) {
            this.news_id = news_id;
        }

        public String getMedia() {
            return media;
        }

        public void setMedia(String media) {
            this.media = media;
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
    }
}
