package com.hn.d.valley.start;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.utils.ScreenUtil;
import com.angcyo.uiview.utils.T_;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.widget.HnLoading;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by hewking on 2017/2/27.
 */

public class RecommendUserUIView extends BaseContentUIView{

    @BindView(R.id.tv_focus)
    TextView tv_focus;
    @BindView(R.id.recycler_view)
    RRecyclerView rRecyclerView;

    private RBaseViewHolder mViewHolder;
    private RecommendUserAdapter mUserAdapter;

    @Override
    protected void inflateContentLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_start_recommenduser);
    }

    @Override
    public void loadContentView(View rootView) {
        super.loadContentView(rootView);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        init();
    }

    private void init() {
        ViewGroup.LayoutParams layoutParams =  rRecyclerView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth;
        int itemHeight = layoutParams.width / 3;
        L.i("init itemHeight : " + itemHeight);

        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(10);
        mUserAdapter = new RecommendUserAdapter(mActivity,itemHeight);
        rRecyclerView.addItemDecoration(itemDecoration);
        //禁止RecyclerView 上下拖动阴影
        rRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rRecyclerView.setLayoutManager(new GridLayoutManager(mActivity,3));
        rRecyclerView.setAdapter(mUserAdapter);
    }

    @Override
    public void onViewCreate() {
        super.onViewCreate();
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
    }

    @OnClick(R.id.tv_focus)
    public void focusRecommendUser(){
        HnLoading.show(mILayout);
        tv_focus.postDelayed(new Runnable() {
            @Override
            public void run() {
                HnLoading.hide();
            }
        },3000);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText("跳过").setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T_.show("跳过");
            }
        }));

        return super.getTitleBar().setTitleString("推荐").setRightItems(rightItems);
    }

    public static class RecommendUserAdapter extends RBaseAdapter<LikeUserInfoBean> {

        private int itemHeight;

        private static final int DEFAULT_MAX_COUNT = 9;

        public RecommendUserAdapter(Context context,int itemHeight) {
            super(context);
            this.itemHeight = itemHeight;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_focus_user_layout;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, final int position, final LikeUserInfoBean likeUserInfoBean) {
            if (likeUserInfoBean == null) {
                return;
            }

            if(itemHeight != 0) {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = itemHeight;
                holder.itemView.setLayoutParams(layoutParams);
            }
            L.i("itemHeight : " + itemHeight);

            final ImageView imageView = holder.imgV(R.id.image_view);
            final ImageView deleteView = holder.imgV(R.id.delete_view);
            deleteView.setVisibility(View.VISIBLE);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.zhanweitu_1);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteView.getVisibility() == View.VISIBLE) {
                        deleteView.setVisibility(View.GONE);
                    }else {
                        deleteView.setVisibility(View.VISIBLE);
                    }
                }
            });

            imageView.post(new Runnable() {
                @Override
                public void run() {
                    if (likeUserInfoBean != null
                            && !TextUtils.isEmpty(likeUserInfoBean.getAvatar())) {
                        Glide.with(mContext)
                                .load(likeUserInfoBean.getAvatar())
                                .placeholder(R.drawable.zhanweitu_1)
                                .into(imageView);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
//            return DEFAULT_MAX_COUNT;
            return mAllDatas.size() > DEFAULT_MAX_COUNT ? DEFAULT_MAX_COUNT : mAllDatas.size();
        }
    }

    @Override
    public boolean canTryCaptureView() {
        return true;
    }
}
