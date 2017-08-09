package com.hn.d.valley.main.message.redpacket;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.anim.RotateAnimation;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.view.DelayClick;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.UIBaseUIDialog;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.service.RedPacketService;
import com.hn.d.valley.widget.HnGlideImageView;

import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.hn.d.valley.main.message.redpacket.Constants.CAN_BE_GRAB;
import static com.hn.d.valley.main.message.redpacket.Constants.CAN_NOTE_GRAB;
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
public class OpenRedPacketUIDialog extends UIBaseUIDialog {

    public static final String TAG = OpenRedPacketUIDialog.class.getSimpleName();

    ImageView ivCancel;
    HnGlideImageView ivIconHead;
    TextView tvUsername;
    TextView tvTip;
    TextView tvRedContent;
    RelativeLayout baseDialogRootLayout;
    ImageView ivOpen;
    TextView tvRedToDetail;
    String discuss_id = "";
    private long redId;
    private String mSessionId;
    private int redpacketStatus;

    //    private AnimatorSet mFrontAnimator;
    private GrabedRDDetail grabedRDDetail;

    public OpenRedPacketUIDialog(int redpacketStatus, String sessionId, long redId) {
        this.redId = redId;
        this.mSessionId = sessionId;
        this.redpacketStatus = redpacketStatus;
    }

    public OpenRedPacketUIDialog(int redpacketStatus, long redId) {
        this(redpacketStatus, null, redId);
    }

    public static Observable<Integer> grabRedBag(long redId, String extend) {
        return RRetrofit.create(RedPacketService.class)
                .grabbag(Param.buildInfoMap("uid:" + UserCache.getUserAccount(), "redid:" + redId, "extend:" + extend))
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

    @Override
    protected View inflateDialogView(FrameLayout dialogRootLayout, LayoutInflater inflater) {
        setGravity(Gravity.CENTER);
        return inflater.inflate(R.layout.pay_grab_layout, dialogRootLayout);
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);

        add(RRetrofit.create(RedPacketService.class)
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
                            initData(bean);
                        }
                    }
                }));

    }

    private void initData(GrabedRDDetail bean) {
        grabedRDDetail = bean;
        ivIconHead.setImageUrl(bean.getAvatar());
        tvUsername.setText(bean.getUsername());
        tvTip.setText(R.string.text_send_you_a_packet);
        if (bean.getUid() == Integer.valueOf(UserCache.getUserAccount())) {
            tvTip.setText("");
            tvRedToDetail.setVisibility(View.VISIBLE);
            tvRedToDetail.setText("查看领取详情>");
            tvRedToDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toResultUIView();
                    finishDialog();
                }
            });
        }
        if (redpacketStatus == CAN_BE_GRAB) {
            tvRedContent.setText(bean.getContent());
        } else if (redpacketStatus == CAN_NOTE_GRAB) {
            tvTip.setText("");
        }
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
        ivCancel = v(R.id.iv_cancel);
        ivIconHead = v(R.id.iv_icon_head);
        tvUsername = v(R.id.tv_username);
        tvTip = v(R.id.tv_tip);
        tvRedContent = v(R.id.tv_red_content);
        baseDialogRootLayout = v(R.id.base_dialog_root_layout);
        tvRedToDetail = v(R.id.tv_red_to_detail);
        ivOpen = v(R.id.iv_open);


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
                        toResultUIView();
                        finishDialog();
                    }
                });
            }
            return;
        }
        ivOpen.setOnClickListener(new DelayClick() {
            @Override
            public void onRClick(View view) {
                startAnimation(view);
                grabRedpacket();
            }
        });
    }

    /**
     * 1.初始化动画
     */
    private void initAnimator() {
//        mFrontAnimator = (AnimatorSet) AnimatorInflater.loadAnimator(mActivity,
//                R.animator.anim_in);
    }

    /**
     * 4.开启动画
     *
     * @param view
     */
    public void startAnimation(View view) {
//        mFrontAnimator.setTarget(ivOpen);
//        mFrontAnimator.start();
        view.clearAnimation();
        view.startAnimation(new RotateAnimation(view.getMeasuredWidth() / 2, view.getMeasuredHeight() / 2));
    }

    public OpenRedPacketUIDialog setDiscuss_id(String discuss_id) {
        this.discuss_id = discuss_id;
        return this;
    }

    private void grabRedpacket() {
        add(RRetrofit.create(RedPacketService.class)
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
                            return grabRedBag(redId, discuss_id);
                        } else if (Constants.ALREADY_GRAB == code) {
                            return Observable.just(Constants.ALREADY_GRAB);
                        } else if (Constants.LOOT_OUT == code) {
                            return Observable.just(Constants.LOOT_OUT);
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
                } else if (Constants.ALREADY_GRAB == code) {
                    return Observable.just(Constants.ALREADY_GRAB);
                } else if (Constants.LOOT_OUT == code) {
                    return Observable.just(Constants.LOOT_OUT);
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
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        if (isError) {
                            L.i(TAG, e.getMsg());
                            finishDialog();
                        }
                    }

                    @Override
                    public void onSucceed(Integer beans) {
                        L.i(TAG, beans);
                        if (beans == Constants.ALREADY_GRAB) {
                            startIView(new OpenRedPacketUIDialog(Constants.LOOT_OUT, redId).setDiscuss_id(discuss_id));
                        }  else if (Constants.LOOT_OUT == beans) {
                            startIView(new OpenRedPacketUIDialog(Constants.LOOT_OUT, redId).setDiscuss_id(discuss_id));
                        }else {
                            toResultUIView();
                        }
//                        if (mSessionId.equals(UserCache.getUserAccount())) {
//                            replaceIView(new P2PStatusRPUIView(mSessionId, redId, true));
//                        } else {
//                        }
//                        finishDialog();
                    }
                }));

    }

    private void toResultUIView() {
        if (TextUtils.isEmpty(discuss_id)) {
            replaceIView(new GrabedRDResultUIView(redId));
        } else {
            replaceIView(new GrabedRDResultUIView(redId).setSqureRedbag(true));
        }
    }


}
