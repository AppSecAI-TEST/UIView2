package com.hn.d.valley.sub.user.dialog;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.ClipboardUtils;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.view.DelayClick;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.bean.HotInfoListBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.control.ShareControl;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.control.VoiceStatusInfo;
import com.hn.d.valley.main.found.sub.InformationDetailUIView;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.message.attachment.DynamicDetailAttachment;
import com.hn.d.valley.main.message.attachment.DynamicDetailMsg;
import com.hn.d.valley.main.message.attachment.ShareNewsAttachment;
import com.hn.d.valley.main.message.attachment.ShareNewsMsg;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.user.DynamicType;
import com.hn.d.valley.sub.user.PublishDynamicUIView2;
import com.hn.d.valley.sub.user.ReportUIView;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

import rx.functions.Action3;
import rx.subscriptions.CompositeSubscription;

import static com.hn.d.valley.main.me.UserDetailUIView2.isMe;
import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：分享动态
 * 创建人员：Robi
 * 创建时间：2017/05/02 10:59
 * 修改人员：Robi
 * 修改时间：2017/05/02 10:59
 * 修改备注：
 * Version: 1.0.0
 */
public class DynamicShareDialog extends UIIDialogImpl {
    UserDiscussListBean.DataListBean mDataListBean;
    CompositeSubscription mSubscription;
    HotInfoListBean mHotInfoListBean;

    boolean canShare = true;

    public DynamicShareDialog(UserDiscussListBean.DataListBean dataListBean,
                              CompositeSubscription subscription) {
        mDataListBean = dataListBean;
        mSubscription = subscription;
    }

    public DynamicShareDialog(HotInfoListBean dataListBean,
                              CompositeSubscription subscription) {
        mHotInfoListBean = dataListBean;
        mSubscription = subscription;
    }

    public DynamicShareDialog setCanShare(boolean canShare) {
        this.canShare = canShare;
        return this;
    }

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.dialog_dynamic_share);
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();

        //标题
        //mViewHolder.tv(R.id.title_view).setText(getString(R.string.dynamic_share_, mDataListBean.getAuthor()));


        //取消
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        //动态分享
        if (mDataListBean != null) {
            //分享动态
            mViewHolder.v(R.id.klg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDialog();
                    if (!canShare) {
                        T_.error(getString(R.string.cant_share_tip));
                        return;
                    }
                    if (mDataListBean.isForwardInformation()) {
                        mParentILayout.startIView(new PublishDynamicUIView2(HotInfoListBean.from(mDataListBean.getOriginal_info())));
                    } else if (TextUtils.isEmpty(mDataListBean.uuid)) {
                        mParentILayout.startIView(new PublishDynamicUIView2(mDataListBean));
                    }
                }
            });

            //关注/取消关注
            initAttentionView();
            //收藏/取消收藏
            initCollectView();

            //举报
            mViewHolder.v(R.id.report_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDialog();
                    mParentILayout.startIView(new ReportUIView(mDataListBean));
                }
            });

            //复制
            mViewHolder.v(R.id.copy_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDialog();
                    if (!canShare) {
                        T_.error(getString(R.string.cant_share_tip));
                        return;
                    }
                    ClipboardUtils.copyText("http://wap.klgwl.com/discuss/detail?item_id=" + mDataListBean.getDiscuss_id());
                    T_.show(getString(R.string.copy_tip));
                }
            });

            //分享
            String mediaType = mDataListBean.getMedia_type();
            String shareDes = getString(R.string.share);
            String thumbUrl = mDataListBean.getUser_info().getAvatar();
            List<String> mediaList = mDataListBean.getMediaList();
            if (DynamicType.isImage(mediaType)) {
                shareDes = getString(R.string.share_image);
                if (mediaList.size() > 0) {
                    thumbUrl = mediaList.get(0);
                }
            } else if (DynamicType.isText(mediaType)) {
                shareDes = getString(R.string.share_text);
            } else if (DynamicType.isVideo(mediaType)) {
                shareDes = getString(R.string.share_video);
                if (mediaList.size() > 0) {
                    String s = UserDiscussItemControl.getVideoParams(mediaList.get(0))[0];
                    if (!TextUtils.isEmpty(s)) {
                        thumbUrl = s;
                    }

                }
            } else if (DynamicType.isVoice(mediaType)) {
                shareDes = getString(R.string.share_voice);
                if (mediaList.size() > 0) {
                    String s = UserDiscussItemControl.getVideoParams(mediaList.get(0))[0];
                    if (!TextUtils.isEmpty(s) && !VoiceStatusInfo.NOPIC.equalsIgnoreCase(s)) {
                        thumbUrl = s;
                    }
                }
            }
            ShareControl.shareDynamicControl(mActivity, mViewHolder,
                    thumbUrl,
                    mDataListBean.getDiscuss_id(),
                    getString(R.string.share_dynamic_format, mDataListBean.getUser_info().getUsername()),
                    shareDes, this, canShare);

            //分享恐龙谷
            mViewHolder.click(R.id.share_klg, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!canShare) {
                        finishDialog();
                        T_.error(getString(R.string.cant_share_tip));
                        return;
                    }
                    getILayout().finishIView(DynamicShareDialog.class);
                    ContactSelectUIVIew.start(mParentILayout, new BaseContactSelectAdapter.Options()
                            , null, true, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                                @Override
                                public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {

                                    requestCallback.onSuccess("");

                                    SessionTypeEnum type = SessionTypeEnum.P2P;
                                    if (uiBaseDataView == null) {
                                        type = SessionTypeEnum.Team;
                                    }
                                    for (AbsContactItem item : absContactItems) {
                                        ContactItem contactItem = (ContactItem) item;
                                        FriendBean friendBean = contactItem.getFriendBean();
                                        DynamicDetailMsg detailMsg = DynamicDetailMsg.create(mDataListBean);
                                        DynamicDetailAttachment attachment = new DynamicDetailAttachment(detailMsg);
                                        IMMessage message = MessageBuilder.createCustomMessage(friendBean.getUid(), type, friendBean.getIntroduce(), attachment);
                                        msgService().sendMessage(message, false);
                                    }
                                    T_.ok(getString(R.string.share_success));

                                }
                            });
                }
            });
        }

        //资讯分享
        if (mHotInfoListBean != null) {

            final String detailUrl = InformationDetailUIView.Companion.getDetailShareUrl(mHotInfoListBean.getId());

            //复制
            mViewHolder.v(R.id.copy_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDialog();
                    if (!canShare) {
                        T_.error(getString(R.string.cant_share_tip));
                        return;
                    }
                    ClipboardUtils.copyText(detailUrl);
                    T_.show(getString(R.string.copy_tip));
                }
            });

            //举报
            mViewHolder.v(R.id.follow_view).setVisibility(View.INVISIBLE);

            mViewHolder.v(R.id.report_view).setVisibility(View.INVISIBLE);
            mViewHolder.v(R.id.report_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishDialog();
                    //mParentILayout.startIView(new ReportUIView(mDataListBean));
                }
            });

            //分享
            String mediaType = mHotInfoListBean.getType();
            String shareDes = mHotInfoListBean.getTitle();
            String thumbIco = mHotInfoListBean.getLogo();
            List<String> mediaList = mHotInfoListBean.getImgsList();

            if ("picture".equalsIgnoreCase(mediaType)) {
                if (mediaList.size() > 0) {
                    thumbIco = mediaList.get(0);
                }
            } else if ("article".equalsIgnoreCase(mediaType)) {
                if (mediaList.size() > 0) {
                    thumbIco = mediaList.get(0);
                }
            } else if ("video".equalsIgnoreCase(mediaType)) {
                if (mediaList.size() > 0) {
                    thumbIco = mediaList.get(0);
                }
                String videoUrl = mHotInfoListBean.getMedia();
            } else if ("voice".equalsIgnoreCase(mediaType)) {
                if (mediaList.size() > 0) {
                    String s = UserDiscussItemControl.getVideoParams(mediaList.get(0))[0];
                    if (!TextUtils.isEmpty(s) && !VoiceStatusInfo.NOPIC.equalsIgnoreCase(s)) {
                        thumbIco = s;
                    }
                }
            }

            ShareControl.shareHotInfoControl(mActivity, mViewHolder,
                    thumbIco,
                    detailUrl,
                    mHotInfoListBean.getAuthor(),
                    shareDes, this, canShare);

            //分享恐龙谷
            mViewHolder.click(R.id.share_klg, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getILayout().finishIView(DynamicShareDialog.class);
                    ContactSelectUIVIew.start(mParentILayout, new BaseContactSelectAdapter.Options()
                            , null, true, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                                @Override
                                public void call(UIBaseRxView uiBaseDataView, List<AbsContactItem> absContactItems, RequestCallback requestCallback) {

                                    requestCallback.onSuccess("");

                                    SessionTypeEnum type = SessionTypeEnum.P2P;
                                    // 注释暂时 size > 1 判断 team
//                                    if (absContactItems.size() > 1) {
//                                        type = SessionTypeEnum.Team;
//                                    }
                                    // 选择群 uibasedataview 为 null
                                    if (uiBaseDataView == null) {
                                        type = SessionTypeEnum.Team;
                                    }

                                    for (AbsContactItem item : absContactItems) {
                                        ContactItem contactItem = (ContactItem) item;
                                        FriendBean friendBean = contactItem.getFriendBean();
//                                        DynamicDetailMsg detailMsg = DynamicDetailMsg.create(mHotInfoListBean);
//                                        DynamicDetailAttachment attachment = new DynamicDetailAttachment(detailMsg);
                                        ShareNewsMsg msg = ShareNewsMsg.create(mHotInfoListBean);
                                        ShareNewsAttachment attachment = new ShareNewsAttachment(msg);
                                        IMMessage message = MessageBuilder.createCustomMessage(friendBean.getUid(), type, friendBean.getIntroduce(), attachment);
                                        msgService().sendMessage(message, false);
                                    }

                                    T_.ok(getString(R.string.share_success));

                                }
                            });
                }
            });
        }
    }

    /**
     * 收藏,取消收藏
     */
    private void initCollectView() {
        RTextView textView = mViewHolder.v(R.id.collect_view);
        if (mDataListBean.getIs_collect() == 1) {
            textView.setTopIco(R.drawable.share_shouchang_s);
            textView.setText(getString(R.string.cancel_fav));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSubscription.add(RRetrofit.create(SocialService.class)
                            .unCollect(Param.buildMap("type:discuss", "item_id:" + mDataListBean.getDiscuss_id()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String bean) {
                                    super.onSucceed(bean);
                                    T_.show(bean);
                                    mDataListBean.setIs_collect(0);
                                    initCollectView();
                                }
                            }));
                }
            });
        } else {
            textView.setTopIco(R.drawable.share_shouchang_n);
            textView.setText(getString(R.string.collect));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSubscription.add(RRetrofit.create(SocialService.class)
                            .collect(Param.buildMap("type:discuss", "item_id:" + mDataListBean.getDiscuss_id()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {
                                @Override
                                public void onSucceed(String bean) {
                                    super.onSucceed(bean);
                                    T_.show(bean);
                                    mDataListBean.setIs_collect(1);
                                    initCollectView();
                                }
                            }));
                }
            });
        }
    }

    /**
     * 关注/取消关注
     */
    private void initAttentionView() {
        TextView textView = mViewHolder.v(R.id.follow_view);

        if (isMe(mDataListBean.getUid())) {
            textView.setVisibility(View.GONE);
        }

        if (mDataListBean.getUser_info().getIs_attention() == 1) {
            //已关注
            textView.setText("取消关注");

            mViewHolder.delayClick(R.id.follow_view, new DelayClick() {
                @Override
                public void onRClick(View view) {
                    finishDialog();
                    mDataListBean.getUser_info().setIs_attention(0);
                    mSubscription.add(RRetrofit.create(UserService.class)
                            .unAttention(Param.buildMap("to_uid:" + mDataListBean.getUid()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onSucceed(String bean) {
                                    T_.show(getString(R.string.handle_success));
                                }
                            }));
                }
            });
        } else {
            //未关注
            textView.setText("关注");

            mViewHolder.delayClick(R.id.follow_view, new DelayClick() {
                @Override
                public void onRClick(View view) {
                    finishDialog();
                    mDataListBean.getUser_info().setIs_attention(1);
                    mSubscription.add(RRetrofit.create(UserService.class)
                            .attention(Param.buildMap("to_uid:" + mDataListBean.getUid()))
                            .compose(Rx.transformer(String.class))
                            .subscribe(new BaseSingleSubscriber<String>() {

                                @Override
                                public void onSucceed(String bean) {
                                    T_.show(getString(R.string.handle_success));
                                }
                            }));
                }
            });
        }
    }
}
