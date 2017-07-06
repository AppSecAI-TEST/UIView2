package com.hn.d.valley.start;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.ScreenUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseContentUIView;
import com.hn.d.valley.bean.RecommendUserBean;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnLoading;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hewking on 2017/2/27.
 */

public class RecommendUserUIView extends BaseContentUIView {

    TextView tv_focus;
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
        click(R.id.tv_focus, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusRecommendUser();
            }
        });
        tv_focus = v(R.id.tv_focus);
        rRecyclerView = v(R.id.recycler_view);
        init();
    }

    private void init() {
        ViewGroup.LayoutParams layoutParams = rRecyclerView.getLayoutParams();
        layoutParams.width = ScreenUtil.screenWidth;
        int itemHeight = layoutParams.width / 3;
        L.i("init itemHeight : " + itemHeight);

        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(10);
        mUserAdapter = new RecommendUserAdapter(mActivity, itemHeight, rRecyclerView);
        rRecyclerView.addItemDecoration(itemDecoration);
        //禁止RecyclerView 上下拖动阴影
        rRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rRecyclerView.setAdapter(mUserAdapter);
    }

    @Override
    public void onViewCreate(View rootView) {
        super.onViewCreate(rootView);
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
    }

    @Override
    public void onViewShow(Bundle bundle) {
        super.onViewShow(bundle);
    }

    public void focusRecommendUser() {
        HnLoading.show(mILayout);
        tv_focus.postDelayed(new Runnable() {
            @Override
            public void run() {
                HnLoading.hide();
            }
        }, 3000);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        ArrayList<TitleBarPattern.TitleBarItem> rightItems = new ArrayList<>();
        rightItems.add(TitleBarPattern.TitleBarItem.build().setText(mActivity.getString(R.string.text_jump)).setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));

        return super.getTitleBar().setTitleString(mActivity.getString(R.string.text_recommend)).setRightItems(rightItems);
    }

    @Override
    public boolean canTryCaptureView() {
        return true;
    }

    public interface OnUserSelectListener {
        void onSelect(boolean boo);
    }

    public static class RecommendUserAdapter extends RModelAdapter<RecommendUserBean> {

        private static final int DEFAULT_MAX_COUNT = 9;

        private int itemHeight;

        private RRecyclerView mRecyclerView;

        private OnUserSelectListener mSelectListener;

        public RecommendUserAdapter(Context context, int itemHeight, RRecyclerView recyclerView) {
            super(context);
            this.itemHeight = itemHeight;
            this.mRecyclerView = recyclerView;
            setModel(RModelAdapter.MODEL_MULTI);

        }

        public void setSelectListener(OnUserSelectListener listener) {
            this.mSelectListener = listener;
        }

        @Override
        public void resetData(List<RecommendUserBean> datas) {
            super.resetData(datas);
            // 选中所有
            setSelectorAll(mRecyclerView, R.id.delete_view);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_focus_user_layout;
        }

        @Override
        protected void onBindCommonView(RBaseViewHolder holder, int position, final RecommendUserBean likeUserInfoBean) {
            if (likeUserInfoBean == null) {
                return;
            }
            if (itemHeight != 0) {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                layoutParams.height = itemHeight;
                holder.itemView.setLayoutParams(layoutParams);
            }
            L.i("itemHeight : " + itemHeight);

            final HnGlideImageView imageView = (HnGlideImageView) holder.imgV(R.id.image_view);
            final TextView username = holder.tv(R.id.tv_username);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.default_avatar);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    if (likeUserInfoBean != null
                            && !TextUtils.isEmpty(likeUserInfoBean.getAvatar())) {
                        imageView.setImageThumbUrl(likeUserInfoBean.getAvatar());
                        username.setText(likeUserInfoBean.getUsername());
                    }
                }
            });
        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, final int position, RecommendUserBean bean) {
            final CompoundButton deleteView = holder.cV(R.id.delete_view);
            deleteView.setChecked(isSelector);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectorPosition(position, deleteView);
                    if (mSelectListener != null) {
                        // getAllSelector() == 0 为没有一个选中
                        mSelectListener.onSelect(getAllSelector().size() != 0);
                    }
                }
            };

            deleteView.setOnClickListener(clickListener);
            holder.itemView.setOnClickListener(clickListener);
        }

        @Override
        protected void onBindNormalView(RBaseViewHolder holder, int position, RecommendUserBean bean) {

        }

        @Override
        public int getItemCount() {
//            return DEFAULT_MAX_COUNT;
            return mAllDatas.size() > DEFAULT_MAX_COUNT ? DEFAULT_MAX_COUNT : mAllDatas.size();
        }
    }
}
