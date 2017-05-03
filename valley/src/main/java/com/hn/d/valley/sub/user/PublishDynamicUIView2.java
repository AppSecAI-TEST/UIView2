package com.hn.d.valley.sub.user;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.utils.string.SingleTextWatcher;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.angcyo.uiview.widget.SoftRelativeLayout;
import com.angcyo.uiview.widget.viewpager.TextIndicator;
import com.hn.d.valley.BuildConfig;
import com.hn.d.valley.R;
import com.hn.d.valley.adapter.HnAddImageAdapter2;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.iview.ImagePagerUIView;
import com.hn.d.valley.base.iview.VideoPlayUIView;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.PublishControl;
import com.hn.d.valley.control.TagsControl;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.message.EmojiLayoutControl;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.main.other.AmapUIView;
import com.hn.d.valley.utils.HnGlide;
import com.hn.d.valley.utils.Image;
import com.hn.d.valley.utils.PhotoPager;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnTopImageView;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

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
    Action0 mPublishAction;
    private HnAddImageAdapter2 mAddImageAdapter2;
    private ArrayList<Luban.ImageItem> photos;
    private List<Tag> mSelectorTags = new ArrayList<>();
    private DynamicType mDynamicType;
    private AmapBean mTargetLocation;
    private RSoftInputLayout mSoftInputLayout;
    private ExEditText mInputView;
    private EmojiLayoutControl mEmojiLayoutControl;
    private List<String> atUsers = new ArrayList<>();//@的用户
    private List<FriendBean> mFriendList = new ArrayList<>();
    private PublishDynamicUIView.VideoStatusInfo mVideoStatusInfo;

    public PublishDynamicUIView2(DynamicType dynamicType) {
        mDynamicType = dynamicType;
    }

    /**
     * 发布视频
     */
    public PublishDynamicUIView2(PublishDynamicUIView.VideoStatusInfo videoStatusInfo) {
        mVideoStatusInfo = videoStatusInfo;
        mDynamicType = DynamicType.VIDEO;
    }

    public PublishDynamicUIView2 setPublishAction(Action0 publishAction) {
        mPublishAction = publishAction;
        return this;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_publish_dynamic2);
    }

    @Override
    public boolean onBackPressed() {
        return mSoftInputLayout.requestBackPressed();
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(null == null ? mActivity.getString(R.string.publish_dynamic) : mActivity.getString(R.string.forward_dynamic))
                .setShowBackImageView(true)
                .addRightItem(TitleBarPattern.TitleBarItem.build(R.drawable.send_forward_dynamic_n, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mInputView.isEmpty()) {
                            mInputView.error();
                            return;
                        }

                        if (mDynamicType == DynamicType.IMAGE && mAddImageAdapter2.getAllDatas().size() == 0) {
                            T_.error(getString(R.string.image_empty_tip));
                            return;
                        }

                        onPublish();
                    }
                }));
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
                public void onEnd() {
                    super.onEnd();
                    HnLoading.hide();
                }
            });
        }
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        //默认选中的标签
        initTags();
        initTagsView();

        initAddressView();

        initVisibleView();

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
        final RRecyclerView recyclerView = mViewHolder.v(R.id.recycler_view);
        mAddImageAdapter2 = new HnAddImageAdapter2(mActivity);
        mAddImageAdapter2.setMaxItemCount(9);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                mAddImageAdapter2.setItemHeight(recyclerView.getMeasuredWidth() / 3);
            }
        });
        recyclerView.addItemDecoration(new RBaseItemDecoration((int) ResUtil.dpToPx(mActivity.getResources(), 6), Color.TRANSPARENT));
        mAddImageAdapter2.setAddListener(new HnAddImageAdapter2.OnAddListener() {

            @Override
            public void onAddClick(View view, int position, Luban.ImageItem item) {
                ImagePickerHelper.startImagePicker(mActivity, true, false, false, true, 9);
            }

            @Override
            public void onItemClick(View view, int position, Luban.ImageItem item) {
                ImagePagerUIView.start(mOtherILayout, view,
                        PhotoPager.getImageItems2(mAddImageAdapter2.getAllDatas()), position);
            }

            @Override
            public void onItemLongClick(View view, int position, Luban.ImageItem item) {
                mAddImageAdapter2.setDeleteModel(recyclerView);
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
                        initTagsView();
                    }
                }, mSelectorTags).setIsVideo(mDynamicType == DynamicType.VIDEO));
            }
        });

        //位置选择
        mViewHolder.v(R.id.address_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            ImageView videoThumbView = mViewHolder.v(R.id.video_thumb_view);
            HnGlide.displayFile(videoThumbView, mVideoStatusInfo.videoThumbPath);

            TextView videoTimeView = mViewHolder.v(R.id.video_time_view);
            UserDiscussItemControl.initVideoTimeView(videoTimeView, mVideoStatusInfo.videoPath);

            videoThumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new VideoPlayUIView(mVideoStatusInfo.videoThumbPath, mVideoStatusInfo.videoPath));
                }
            });
        }

        //转发
    }

    private void initVisibleView() {
        TextView visibleView = mViewHolder.tv(R.id.visible_view);
        visibleView.setText("公开");
    }

    private void initAddressView() {
        TextView addressView = mViewHolder.tv(R.id.address_view);
        if (mTargetLocation == null) {
            addressView.setText(R.string.where_are_you);
        } else {
            addressView.setText(mTargetLocation.title);
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
        for (Tag t : TagsControl.getAllTags()) {
            if (mDynamicType == DynamicType.IMAGE) {
                if (TextUtils.equals(t.getName(), getString(R.string.life))) {
                    mSelectorTags.add(t);
                }
            } else if (mDynamicType == DynamicType.VIDEO) {
                if (TextUtils.equals(t.getName(), getString(R.string.video))) {
                    mSelectorTags.add(t);
                }
            } else if (mDynamicType == DynamicType.VOICE) {
                if (TextUtils.equals(t.getName(), getString(R.string.voice))) {
                    mSelectorTags.add(t);
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
                } else {
                    imageView.setImageResource(R.drawable.expression_comments_n);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSoftInputLayout.isEmojiShow()) {
                    mSoftInputLayout.showSoftInput(mInputView);
                } else {
                    mSoftInputLayout.showEmojiLayout();
                }
            }
        });

        mEmojiLayoutControl = new EmojiLayoutControl(mViewHolder, new EmojiLayoutControl.OnEmojiSelectListener() {
            @Override
            public void onEmojiText(String emoji) {
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
        });
    }

    private void initControlLayout() {

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
        final HnTopImageView hnTopImageView = mViewHolder.v(R.id.ico_top);
        hnTopImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hnTopImageView.setTop(!hnTopImageView.isTop());

                if (hnTopImageView.isTop() && !isShowTip) {
                    UIDialog.build().setDialogTitle(mActivity.getString(R.string.tip))
                            .setDialogContent(mActivity.getString(R.string.dynamic_top_tip))
                            .setCancelText("").setOkText(mActivity.getString(R.string.known))
                            .setCanCanceledOnOutside(false)
                            .showDialog(PublishDynamicUIView2.this);
                    if (!BuildConfig.DEBUG) {
                        isShowTip = true;
                    }
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
            if (!string.contains(friendBean.getDefaultMark())) {
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
                                mInputView.addMention(friendBean.getDefaultMark());
                            }
                        }
                    }
                });
    }


    /**
     * 点击发布动态
     */
    private void onPublish() {
        //创建发布任务, 在后台进行发布
        HnTopImageView hnTopImageView = mViewHolder.v(R.id.ico_top);
        CheckBox allowDownloadView = mViewHolder.v(R.id.allow_box_view);

        if (mDynamicType.getType() < 6) {
            //小于6,表示是发布动态
            PublishControl.PublishTask publishTask = null;
            if (mDynamicType == DynamicType.IMAGE) {
                publishTask = new PublishControl.PublishTask(photos);
            } else if (mDynamicType == DynamicType.VIDEO) {
                publishTask = new PublishControl.PublishTask(mVideoStatusInfo);
            }
            publishTask.setSelectorTags(mSelectorTags)
                    .setTop(hnTopImageView.isTop())
                    .setShareLocation(mTargetLocation != null)
                    .setAddress(getAddress())
                    .setLat(getLatitude())
                    .setLng(getLongitude())
                    .setAllow_download(allowDownloadView.isChecked() ? 1 : 0)
                    .setContent(mInputView.fixMentionString(new ExEditText.getIdFromUserName() {
                        @Override
                        public String userId(String userName) {
                            for (FriendBean bean : mFriendList) {
                                if (TextUtils.equals(userName, bean.getDefaultMark())) {
                                    return bean.getUid();
                                }
                            }
                            return "";
                        }
                    }))
            ;
            PublishControl.instance().addTask(publishTask);

            finishIView();
            if (mPublishAction != null) {
                mPublishAction.call();
            }
        } else {
            //转发动态, 不需要再后台进行
        }
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

    /**
     * 发布动态的类型
     */
    public enum DynamicType {
        TEXT(1)/*纯文本*/, VIDEO(2)/*视频*/, IMAGE(3)/*图文*/, VOICE(4)/*语音*/, PACKET(5)/*红包*/,
        FORWARD_TEXT(6)/*转发纯文本*/, FORWARD_VIDEO(7)/*转发视频*/, FORWARD_IMAGE(8)/*转发图文*/, FORWARD_VOICE(9)/*转发语音*/, FORWARD_PACKET(10)/*转发红包*/;

        int type;

        DynamicType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }

}
