package com.hn.d.valley.main.message.groupchat;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RAddPhotoAdapter;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.T_;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.realm.Tag;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.service.GroupChatService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

import static com.hn.d.valley.main.found.sub.HotInfoListUIView.displayImage;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/15 11:31
 * 修改人员：hewking
 * 修改时间：2017/05/15 11:31
 * 修改备注：
 * Version: 1.0.0
 */
public class ReportNextUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    private RAddPhotoAdapter mAddPhotoAdapter;

    private boolean isCancel;

    private Tag mTag;
    private String gid;

    public ReportNextUIView(Tag tag, String gid) {
        this.mTag = tag;
        this.gid = gid;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 1) {
            return R.layout.item_button_view;
        }
        return R.layout.item_report_upload_certify;
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mAddPhotoAdapter = new RAddPhotoAdapter<String>(mActivity)
                .setDeleteModel(true)
                .setMaxPhotoCount(1)
                .setExcludeWidth(mBaseOffsetSize * 4)
                .setConfigCallback(new RAddPhotoAdapter.ConfigCallback() {
                    @Override
                    public void onDisplayImage(ImageView imageView, int position) {
                        displayImage(imageView, String.valueOf(mAddPhotoAdapter.getAllDatas().get(position)));
                    }

                    @Override
                    public void onAddClick(View view) {
                        ImagePickerHelper.startImagePicker(mActivity, true, true, false, false, 1);
                    }

                    @Override
                    public boolean onDeleteClick(View view, int position) {
                        return false;
                    }
                });
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        getRecyclerView().addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                int viewAdapterPosition = params.getViewAdapterPosition();
                if (viewAdapterPosition == 0) {
                    RecyclerView.ViewHolder viewHolder = getRecyclerView().findViewHolderForAdapterPosition(viewAdapterPosition);
                    if (viewHolder != null) {
                        RRecyclerView recyclerView = ((RBaseViewHolder) viewHolder).reV(R.id.recycler_view);
                        recyclerView.setAdapter(mAddPhotoAdapter);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                RRecyclerView recyclerView = holder.reV(R.id.recycler_view);
                recyclerView.addItemDecoration(new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {
                    @Override
                    public void getItemOffsets(Rect outRect, int position) {
                        if (position > 0) {
                            outRect.left = mBaseOffsetSize;
                        }
                    }
                }));
                mAddPhotoAdapter.attachRecyclerView(recyclerView);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(mBaseOffsetSize * 2) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText(R.string.submit);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAddPhotoAdapter.getAllDatas().isEmpty()) {
                            T_.error(getString(R.string.upload_certify_tip));
                            return;
                        }
                        onApply();
                    }
                });
            }
        }));
    }

    /**
     * 开始提交
     */
    private void onApply() {
        isCancel = false;
        HnLoading.show(mParentILayout).addDismissListener(new UIIDialogImpl.OnDismissListener() {
            @Override
            public void onDismiss() {
                isCancel = true;
                onCancel();
            }
        });

        new OssControl(new OssControl.OnUploadListener() {
            @Override
            public void onUploadStart() {

            }

            @Override
            public void onUploadSucceed(List<String> list) {
                if (isCancel) {
                    return;
                }
                onApplyNext(list);
            }

            @Override
            public void onUploadFailed(int code, String msg) {

            }
        }).uploadCircleImg(mAddPhotoAdapter.getAllDatas());
    }

    private void onApplyNext(List<String> list) {
        add(RRetrofit.create(GroupChatService.class)
                .report(Param.buildMap("uid:" + UserCache.getUserAccount()
                        , "gid:" + gid,"reason_id:" + mTag.getId()
                        ,"imgs:" + RUtils.connect(list)))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        finishIView();
                        T_.info(mActivity.getString(R.string.text_resport_success));
                    }

                    @Override
                    public void onNoNetwork() {
                        super.onNoNetwork();
                    }
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        if (images.isEmpty()) {
            return;
        }
        String filePath = images.get(0);
        mAddPhotoAdapter.addLastItemSafe(filePath);

    }

}
