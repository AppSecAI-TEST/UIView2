package com.hn.d.valley.sub.user;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.github.all.base.adapter.ViewGroupUtils;
import com.angcyo.uiview.github.all.base.adapter.adapter.cache.BaseCacheAdapter;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.RSubscriber;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.T_;
import com.angcyo.uiview.widget.RCheckGroup;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.bean.GroupDescBean;
import com.hn.d.valley.bean.UserDiscussListBean;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.bean.realm.UserInfoBean;
import com.hn.d.valley.main.message.groupchat.ReportNextUIView;
import com.hn.d.valley.service.SocialService;
import com.hn.d.valley.widget.HnLoading;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：举报界面
 * 创建人员：Robi
 * 创建时间：2017/02/13 15:24
 * 修改人员：Robi
 * 修改时间：2017/02/13 15:24
 * 修改备注：
 * Version: 1.0.0
 */
public class ReportUIView extends BaseContentUIView {
    RCheckGroup mContentLayout;

    UserDiscussListBean.DataListBean mDataBean;

    UserInfoBean mInfoBean;

    private GroupDescBean mGroupDesc;
    private UserInfoProvider.UserInfo userInfo;

    public ReportUIView(UserInfoBean infoBean) {
        mInfoBean = infoBean;
    }

    public ReportUIView(UserInfoProvider.UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public ReportUIView(UserDiscussListBean.DataListBean dataBean) {
        mDataBean = dataBean;
    }

    public ReportUIView(GroupDescBean groupDesc) {
        this.mGroupDesc = groupDesc;
    }

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_report);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mContentLayout = v(R.id.content_layout);
        click(R.id.submit_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick();
            }
        });
        ResUtil.setBgDrawable(mViewHolder.v(R.id.submit_view), SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity.getString(R.string.report))
                .setShowBackImageView(true);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        add(RRetrofit.create(SocialService.class)
                .getReportReason(Param.buildMap("type:" + getReportType()))
                .compose(Rx.transformerList(Tag.class))
                .subscribe(new RSubscriber<List<Tag>>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                        showNonetLayout(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onViewLoad();
                            }
                        });
                    }

                    @Override
                    public void onSucceed(List<Tag> bean) {
                        super.onSucceed(bean);
                        showContentLayout();
                        ViewGroupUtils.addViews(mContentLayout, new BaseCacheAdapter<Tag>(mActivity, bean) {
                            @Override
                            public View getView(ViewGroup parent, int pos, Tag data) {
                                final View inflate = mInflater.inflate(R.layout.item_report, parent, false);
                                ((TextView) inflate.findViewById(R.id.text_view)).setText(data.getContent());
                                inflate.setTag(data);
                                View check = inflate.findViewWithTag("check");
                                if (check instanceof ImageView) {
                                    ((ImageView) check).setColorFilter(SkinHelper.getSkin().getThemeSubColor());
                                }

                                return inflate;
                            }
                        });
                    }
                }));
    }

    /**
     * discuss-动态/评论/回复 group-群聊/个人
     */
    private String getReportType() {
        if (mInfoBean != null) {
            return "group";
        } else if (mDataBean != null) {
            return "discuss";
        } else if (mGroupDesc != null) {
            return "group";
        } else if (userInfo != null) {
            return "user";
        }
        return "";
    }

    /**
     * 举报
     */
    public void onSubmitClick() {
        final View checkView = mContentLayout.getCheckView();
        if (checkView == null) {
            T_.show(mActivity.getString(R.string.no_selector_report_tip));
        } else {
            Tag tag = (Tag) checkView.getTag();
            if (mInfoBean != null) {
                reportUser(mInfoBean.getUid(), tag);
            } else if (mDataBean != null) {
                reportDiscuss(tag);
            } else if (mGroupDesc != null) {
                startIView(new ReportNextUIView(tag, mGroupDesc.getGid()));
            } else if (userInfo != null) {
                reportUser(userInfo.getAccount(), tag);
            }
        }
    }


    /**
     * 举报用户
     */
    private void reportUser(String account, Tag tag) {
        add(RRetrofit.create(SocialService.class)
                .report(Param.buildMap("type:user", "item_id:" + account, "reason_id:" + tag.getId()))
                .compose(Rx.transformer(String.class))
                .subscribe(new RSubscriber<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        HnLoading.show(mParentILayout, false);
                    }

                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        T_.show(bean);
                        finishIView();
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        HnLoading.hide();
                    }

                }));
    }

    /**
     * 举报动态
     */
    private void reportDiscuss(Tag tag) {
        add(RRetrofit.create(SocialService.class)
                .report(Param.buildMap("type:discuss", "item_id:" + mDataBean.getDiscuss_id(), "reason_id:" + tag.getId()))
                .compose(Rx.transformer(String.class)).subscribe(new RSubscriber<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        HnLoading.show(mParentILayout, false);
                    }

                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        T_.show(bean);
                        finishIView();
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        HnLoading.hide();
                    }
                }));
    }
}
