package com.hn.d.valley.sub.user.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.github.utilcode.utils.ClipboardUtils;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.control.ShareControl;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.sub.user.DynamicType;
import com.hn.d.valley.sub.user.ReportUIView;

import rx.subscriptions.CompositeSubscription;

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

    public DynamicShareDialog(UserDiscussListBean.DataListBean dataListBean,
                              CompositeSubscription subscription) {
        mDataListBean = dataListBean;
        mSubscription = subscription;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.dialog_dynamic_share);
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();

        //关注
        mViewHolder.v(R.id.follow_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                ClipboardUtils.copyText(mDataListBean.getContent());
                T_.show(getString(R.string.copy_tip));
            }
        });

        //取消
        mViewHolder.v(R.id.cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        //分享
        String mediaType = mDataListBean.getMedia_type();
        String shareDes = getString(R.string.share);
        if (DynamicType.isImage(mediaType)) {
            shareDes = getString(R.string.share_image);
        } else if (DynamicType.isText(mediaType)) {
            shareDes = getString(R.string.share_text);
        } else if (DynamicType.isVideo(mediaType)) {
            shareDes = getString(R.string.share_video);
        } else if (DynamicType.isVoice(mediaType)) {
            shareDes = getString(R.string.share_voice);
        }
        ShareControl.shareDynamicControl(mActivity, mViewHolder,
                mDataListBean.getUser_info().getAvatar(),
                mDataListBean.getDiscuss_id(),
                getString(R.string.share_dynamic_format, mDataListBean.getUser_info().getUsername()), shareDes);
    }

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
}
