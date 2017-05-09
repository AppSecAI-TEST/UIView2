package com.hn.d.valley.main.message.redpacket;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.concurrent.TimeUnit;

import butterknife.BindInt;
import butterknife.BindView;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hn.d.valley.main.message.redpacket.Constants.CAN_BE_GRAB;
import static com.hn.d.valley.main.message.redpacket.Constants.EXPORE;
import static com.hn.d.valley.main.message.redpacket.Constants.LOOT_OUT;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/04/24 19:47
 * 修改人员：hewking
 * 修改时间：2017/04/24 19:47
 * 修改备注：
 * Version: 1.0.0
 */
public class OpenRedPacketUIDialog extends UIIDialogImpl {

    public static final String TAG = OpenRedPacketUIDialog.class.getSimpleName();

    @BindView(R.id.iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.iv_icon_head)
    HnGlideImageView ivIconHead;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_red_content)
    TextView tvRedContent;
    @BindView(R.id.base_dialog_root_layout)
    RelativeLayout baseDialogRootLayout;
    @BindView(R.id.iv_open)
    ImageView ivOpen;
    @BindView(R.id.tv_red_to_detail)
    TextView tvRedToDetail;

    private long redId;
    private String mSessionId;

    private int redpacketStatus;
    private GrabedRDDetail grabedRDDetail;

    private AnimatorSet mFrontAnimator;
    private AnimatorSet mBackAnimator;

    public OpenRedPacketUIDialog(int redpacketStatus, String sessionId, long redId) {
        this.redId = redId;
        this.mSessionId = sessionId;
        this.redpacketStatus = redpacketStatus;
    }

    public OpenRedPacketUIDialog(int redpacketStatus, long redId) {
        this(redpacketStatus, null, redId);
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_grab_layout, dialogRootLayout);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);

        RRetrofit.create(RedPacketService.class)
                .detail(Param.buildInfoMap("redid:" + redId))
                .compose(Rx.transformRedPacket(GrabedRDDetail.class))
                .subscribe(new BaseSingleSubscriber<GrabedRDDetail>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSucceed(GrabedRDDetail bean) {
                        if (bean == null) {

                        } else {
                            grabedRDDetail = bean;
                            ivIconHead.setImageUrl(bean.getAvatar());
                            tvUsername.setText(bean.getUsername());
                            tvTip.setText(R.string.text_send_you_a_packet);
                            if (redpacketStatus == CAN_BE_GRAB) {
                                tvRedContent.setText(bean.getContent());
                            }
                        }
                    }
                });

    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
            }
        });

        initAnimator();

        if (redpacketStatus == Constants.CAN_NOTE_GRAB
                || redpacketStatus == EXPORE
                || redpacketStatus == LOOT_OUT) {
            ivOpen.setVisibility(View.INVISIBLE);
//            tvRedContent.setVisibility(View.GONE);
            if (redpacketStatus == EXPORE) {
                tvRedContent.setText(R.string.text_red_packet_expore);
            }
            if (redpacketStatus == LOOT_OUT) {
                tvRedContent.setText(R.string.text_rp_already_grabed);
                tvRedToDetail.setVisibility(View.VISIBLE);
                tvRedToDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startIView(new GrabedRDResultUIView(redId));
                        finishDialog();
                    }
                });
            }
            return;
        }
        ivOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation(v);
                grabRedpacket();
            }
        });
    }


    /**
     * 1.初始化动画
     */
    private void initAnimator() {
        mFrontAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(mActivity,
                R.animator.anim_in);
        mBackAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(mActivity,
                R.animator.anim_out);
    }

    /**
     * 4.开启动画
     *
     * @param view
     */
    public void startAnimation(View view) {
        mFrontAnimator.setTarget(ivOpen);
//        mBackAnimator.setTarget(ivOpen);
        mFrontAnimator.start();
//        mBackAnimator.start();
    }

    private void grabRedpacket() {
        RRetrofit.create(RedPacketService.class)
                .status(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "redid:" + redId))
                .subscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, Integer>() {
                    @Override
                    public Integer call(ResponseBody responseBody) {
                        return GrabPacketHelper.parseResult(responseBody);
                    }
                })
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer code) {
                        if (Constants.CAN_BE_GRAB == code) {
                            return RRetrofit.create(RedPacketService.class)
                                    .grabbag(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "redid:" + redId))
                                    .compose(new Observable.Transformer<ResponseBody, Integer>() {
                                        @Override
                                        public Observable<Integer> call(Observable<ResponseBody> responseBodyObservable) {
                                            return responseBodyObservable.map(new Func1<ResponseBody, Integer>() {
                                                @Override
                                                public Integer call(ResponseBody responseBody) {
                                                    return GrabPacketHelper.parseResult(responseBody);
                                                }
                                            }).subscribeOn(Schedulers.io());
                                        }
                                    });
                        } else if (Constants.ALREADY_GRAB == code) {

                        }
                        return Observable.empty();
                    }
                }).flatMap(new Func1<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(Integer code) {
                if (Constants.IN_QUEUE == code) {
                    return Observable.interval(500, TimeUnit.MILLISECONDS)
                            .take(5)
                            .observeOn(Schedulers.io())
                            .flatMap(new Func1<Long, Observable<Integer>>() {
                                @Override
                                public Observable<Integer> call(Long aLong) {
                                    return RRetrofit.create(RedPacketService.class)
                                            .result(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "redid:" + redId))
                                            .compose(new Observable.Transformer<ResponseBody, Integer>() {
                                                @Override
                                                public Observable<Integer> call(Observable<ResponseBody> responseBodyObservable) {
                                                    return responseBodyObservable.map(new Func1<ResponseBody, Integer>() {
                                                        @Override
                                                        public Integer call(ResponseBody responseBody) {
                                                            return GrabPacketHelper.parseResult(responseBody);
                                                        }
                                                    }).subscribeOn(Schedulers.io());
                                                }
                                            });
                                }
                            }).takeUntil(new Func1<Integer, Boolean>() {
                                @Override
                                public Boolean call(Integer code) {
                                    L.i(TAG, "takeUntil " + code);
                                    return Constants.IN_QUEUE != code;
                                }
                            });
                }
                return Observable.empty();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<Integer>() {

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        finishDialog();
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onError(code, msg);
                        L.i(TAG, msg);
                        finishDialog();
                    }

                    @Override
                    public void onSucceed(Integer beans) {
                        L.i(TAG, beans);
                        if (mSessionId.equals(UserCache.getUserAccount())) {
                            startIView(new P2PStatusRPUIView(mSessionId, redId, true));
                        } else {
                            startIView(new GrabedRDResultUIView(redId));
                        }
                        finishDialog();
                    }
                });

    }


}
