package com.hn.d.valley.main.me;

import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.widget.RImageCheckView;
import com.angcyo.uiview.widget.RImageView;
import com.angcyo.uiview.widget.RNineImageLayout;
import com.angcyo.uiview.widget.RTextView;
import com.hn.d.valley.R;
import com.hn.d.valley.control.DraftControl;
import com.hn.d.valley.control.PublishTaskRealm;
import com.hn.d.valley.control.UserDiscussItemControl;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.hn.d.valley.sub.user.DynamicType;
import com.hn.d.valley.widget.HnExTextView;
import com.hn.d.valley.widget.HnPlayTimeView;
import com.hn.d.valley.widget.HnVideoPlayView;

import java.util.List;

import io.realm.RealmResults;

import static com.hn.d.valley.control.UserDiscussItemControl.getVideoTime;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的草稿箱
 * 创建人员：Robi
 * 创建时间：2017/05/16 15:59
 * 修改人员：Robi
 * 修改时间：2017/05/16 15:59
 * 修改备注：
 * Version: 1.0.0
 */
public class DraftManagerUIView extends SingleRecyclerUIView<PublishTaskRealm> {

    boolean isInEditMode = false;

    @NonNull
    @Override
    protected LayoutState getDefaultLayoutState() {
        return LayoutState.LOAD;
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected int getItemDecorationColor() {
        return getColor(R.color.chat_bg_color);
    }

    @Override
    protected int getItemDecorationHeight() {
        return getDimensionPixelOffset(R.dimen.base_hdpi);
    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar()
                .setTitleString(mActivity, R.string.my_draft_tip)
                .addRightItem(TitleBarPattern.buildText(getString(R.string.base_edit), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView textView = (TextView) v;
                        if (isInEditMode) {
                            textView.setText(R.string.base_edit);
                        } else {
                            textView.setText(R.string.cancel);
                        }
                        isInEditMode = !isInEditMode;

                        changeEditMode();
                    }
                }).setVisibility(View.GONE))
                ;
    }

    private void changeEditMode() {
        mRExBaseAdapter.notifyDataSetChanged();
        mViewHolder.v(R.id.control_layout).setVisibility(isInEditMode ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void inflateRecyclerRootLayout(RelativeLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_draft);
    }

    @Override
    public RefreshLayout getRefreshLayout() {
        return mViewHolder.v(R.id.refresh_layout);
    }

    @Override
    public RRecyclerView getRecyclerView() {
        return mViewHolder.v(R.id.recycler_view);
    }

    @Override
    protected RExBaseAdapter<String, PublishTaskRealm, String> initRExBaseAdapter() {
        return new RExBaseAdapter<String, PublishTaskRealm, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return R.layout.item_draft;
            }

            @Override
            protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, PublishTaskRealm bean) {
                RImageCheckView checkView = holder.v(R.id.check_view);
                checkView.setChecked(isSelector);
            }

            @Override
            protected void onBindDataView(RBaseViewHolder holder, int posInData, PublishTaskRealm dataBean) {
                holder.v(R.id.check_view).setVisibility(isInEditMode ? View.VISIBLE : View.GONE);

                RTextView textView = holder.v(R.id.time_view);
                View mediaControlLayout = holder.v(R.id.media_control_layout);

                mediaControlLayout.setVisibility(View.VISIBLE);

                if (DynamicType.isImage(dataBean.getType())) {
                    textView.setLeftIco(R.drawable.tuwen_chaogaoxiang);
                } else if (DynamicType.isVideo(dataBean.getType())) {
                    textView.setLeftIco(R.drawable.shiping_chaogaoxiang);
                } else if (DynamicType.isVoice(dataBean.getType())) {
                    textView.setLeftIco(R.drawable.luyin_chaogaoxiang);
                } else {
                    mediaControlLayout.setVisibility(View.GONE);
                    textView.setLeftIco(R.drawable.wenzi_chaogaoxiang);
                }
                textView.setText(dataBean.getTimeString());

                //内容
                HnExTextView contentView = holder.v(R.id.content_view);
                contentView.setMaxShowLine(3);
                contentView.setText(dataBean.getShowContent());

                //媒体控制
                initMediaLayout(holder, dataBean);

                //重发
                holder.v(R.id.retry_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                //重新编辑
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
    }

    private void initMediaLayout(RBaseViewHolder holder, PublishTaskRealm dataBean) {
        RNineImageLayout mediaImageTypeView = holder.v(R.id.media_image_view);
        TextView videoTimeView = holder.v(R.id.video_time_view);
        TextView bottomVideoTimeView = holder.v(R.id.bottom_video_time_view);

        View voiceTipView = holder.v(R.id.voice_tip_view);
        View bottomVoiceTipView = holder.v(R.id.bottom_voice_tip_view);
        final HnVideoPlayView videoPlayView = holder.v(R.id.video_play_view);
        bottomVoiceTipView.setVisibility(View.GONE);
        bottomVideoTimeView.setVisibility(View.GONE);

        String mediaType = dataBean.getType();

        //固定大小
        int size = (int) (density() * 100);
        final int[] mediaSize = new int[]{size, size};

        if (DynamicType.isImage(mediaType)) {
            //图片类型
            mediaImageTypeView.setVisibility(View.VISIBLE);
            videoTimeView.setVisibility(View.INVISIBLE);
            videoPlayView.setVisibility(View.INVISIBLE);
            voiceTipView.setVisibility(View.INVISIBLE);
            mediaImageTypeView.setDrawMask(false);
            mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                @Override
                public int[] getWidthHeight(int imageSize) {
                    return mediaSize;
                }

                @Override
                public void displayImage(final ImageView imageView, String url, int width, int height) {
                    UserDiscussItemControl.displayImage(imageView, url, width, height, true);
                }

                @Override
                public void onImageItemClick(ImageView imageView, List<String> urlList, List<RImageView> imageList, int index) {
                    //点击预览全部图片
                }
            });
            mediaImageTypeView.setImage(dataBean.getPhotos2().get(0).thumbPath);
        } else if (DynamicType.isVideo(mediaType)) {
            //视频类型
            mediaImageTypeView.setVisibility(View.VISIBLE);
            videoTimeView.setVisibility(View.VISIBLE);
            videoPlayView.setVisibility(View.VISIBLE);
            voiceTipView.setVisibility(View.INVISIBLE);
            videoPlayView.setPlayType(HnVideoPlayView.PlayType.VIDEO);
            mediaImageTypeView.setDrawMask(false);
            //DraweeViewUtil.setDraweeViewRes(mediaImageTypeView, R.drawable.video_release);

            final String thumbUrl = dataBean.getVideoStatusInfo().getVideoThumbPath();
            String videoUrl = dataBean.getVideoStatusInfo().getVideoPath();
            videoTimeView.setText(getVideoTime(videoUrl));
            mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                @Override
                public int[] getWidthHeight(int imageSize) {
                    return mediaSize;
                }

                @Override
                public void displayImage(ImageView imageView, String url, int width, int height) {
                    UserDiscussItemControl.displayImage(imageView, url, width, height, true);
                }

                @Override
                public void onImageItemClick(ImageView imageView, List<String> urlList, List<RImageView> imageList, int index) {
                }
            });
            mediaImageTypeView.setImage(thumbUrl);
        } else if (DynamicType.isVoice(mediaType)) {
            //语音类型
            videoPlayView.setVisibility(View.VISIBLE);
            videoTimeView.setVisibility(View.VISIBLE);
            voiceTipView.setVisibility(View.VISIBLE);
            mediaImageTypeView.setDrawMask(true);

            String thumbUrl = dataBean.getVoiceStatusInfo().getVoiceImagePath();
            String videoUrl = dataBean.getVoiceStatusInfo().getVoicePath();
            videoTimeView.setText(getVideoTime(videoUrl));

            //语音播放时长的展示
            ((HnPlayTimeView) videoTimeView).setPlayTime(-1);

            //详情里面, 左下角显示播放按钮
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoPlayView.getLayoutParams();
            //其他界面居中显示
            params.gravity = Gravity.CENTER;
            videoPlayView.setPlayType(HnVideoPlayView.PlayType.VOICE_HOME);

            videoTimeView.setVisibility(View.GONE);
            voiceTipView.setVisibility(View.GONE);
            bottomVoiceTipView.setVisibility(View.VISIBLE);
            bottomVideoTimeView.setVisibility(View.VISIBLE);
            bottomVideoTimeView.setText(videoTimeView.getText());
            videoPlayView.setLayoutParams(params);

            mediaImageTypeView.setNineImageConfig(new RNineImageLayout.NineImageConfig() {
                @Override
                public int[] getWidthHeight(int imageSize) {
                    return mediaSize;
                }

                @Override
                public void displayImage(ImageView imageView, String url, int width, int height) {
                    UserDiscussItemControl.displayVoiceImage(imageView, url, width, height, true);
                }

                @Override
                public void onImageItemClick(ImageView imageView, List<String> urlList, List<RImageView> imageList, int index) {
                    //T_.info(videoUrl);
                }
            });
            mediaImageTypeView.setImage(thumbUrl);
        } else {
            mediaImageTypeView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();
        mRExBaseAdapter.setModel(RModelAdapter.MODEL_MULTI);

        mViewHolder.cV(R.id.selector_all).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRExBaseAdapter.setSelectorAll(getRecyclerView(), R.id.check_view);
                } else {
                    mRExBaseAdapter.unselectorAll(getRecyclerView(), R.id.check_view);
                }
            }
        });
    }

    @Override
    protected void onUILoadData(String page) {
        showLoadView();
        DraftControl.getDraft(new DraftControl.OnDraftListener() {
            @Override
            protected void onDraft(RealmResults<PublishTaskRealm> taskRealms) {
                onUILoadDataFinish();
                hideLoadView();
                if (taskRealms.isEmpty()) {
                    showEmptyLayout();
                } else {
                    showContentLayout();
                    getUITitleBarContainer().showRightItem(0);
                    mRExBaseAdapter.resetAllData(taskRealms);
                }
            }
        });
    }
}
