package com.hn.d.valley.main.me;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.angcyo.uiview.utils.UI;
import com.angcyo.uiview.widget.ExEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hn.d.valley.R;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.oss.OssControl;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.service.AuthService;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：我的身份/身份认证
 * 创建人员：Robi
 * 创建时间：2017/03/10 10:39
 * 修改人员：Robi
 * 修改时间：2017/03/10 10:39
 * 修改备注：
 * Version: 1.0.0
 */
public class MyAuthNextUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo> {

    FileType mFileType;
    AuthInfo mAuthInfo;
    private RAddPhotoAdapter mAddPhotoAdapter;
    private ImageView mFrontImageView;
    private ImageView mReverseImageView;
    private ExEditText mEditText;

    private String frontPath, reversePath, frontUrl, reverseUrl;
    private boolean isCancel;

    public MyAuthNextUIView(AuthInfo authInfo) {
        mAuthInfo = authInfo;
    }

    @Override
    protected int getTitleResource() {
        return R.string.my_auth_title;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0 || viewType == 2 || viewType == 3) {
            return R.layout.item_single_text_view;
        } else if (viewType == 1) {
            return R.layout.item_single_input_view;
        } else if (viewType == 6) {
            return R.layout.item_button_view;
        } else if (viewType == 4) {
            //上传身份证
            return R.layout.item_auth_upload_card;
        } else if (viewType == 5) {
            //证明材料
            return R.layout.item_auth_upload_certify;

        }
        return R.layout.item_info_layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        if (images.isEmpty()) {
            return;
        }
        String filePath = images.get(0);
        switch (mFileType) {
            case CERTIFY:
                mAddPhotoAdapter.addLastItemSafe(filePath);
                break;
            case FRONT:
                displayImage(mFrontImageView, filePath);
                break;
            case REVERSE:
                displayImage(mReverseImageView, filePath);
                break;
        }
    }

    @Override
    public void onViewLoad() {
        super.onViewLoad();
        mAddPhotoAdapter = new RAddPhotoAdapter<String>(mActivity)
                .setDeleteModel(true)
                .setExcludeWidth(mBaseOffsetSize * 4)
                .setConfigCallback(new RAddPhotoAdapter.ConfigCallback() {
                    @Override
                    public void onDisplayImage(ImageView imageView, int position) {
                        displayImage(imageView, String.valueOf(mAddPhotoAdapter.getAllDatas().get(position)));
                    }

                    @Override
                    public void onAddClick(View view) {
                        mFileType = FileType.CERTIFY;
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
                if (viewAdapterPosition == 5) {
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
                holder.tv(R.id.text_view).setText(R.string.certification_tip);
                holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mEditText = holder.v(R.id.edit_text_view);
                mEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

                mEditText.setSingleLine(false);
                mEditText.setMaxLines(5);
                mEditText.setGravity(Gravity.TOP);
                UI.setViewHeight(mEditText, mActivity.getResources().getDimensionPixelOffset(R.dimen.base_100dpi));

                StringBuilder builder = new StringBuilder();
                builder.append(mAuthInfo.baseInfo.get(2));
                builder.append(" ");
                builder.append(mAuthInfo.baseInfo.get(3));
                builder.append(" ");
                builder.append(mAuthInfo.baseInfo.get(0));

                mEditText.setInputText(builder.toString());
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.itemView.setPadding(holder.itemView.getPaddingLeft(), 0, holder.itemView.getPaddingRight(), 0);
                holder.tv(R.id.text_view).setText(R.string.certification_tip2);
                holder.tv(R.id.text_view).setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimensionPixelOffset(R.dimen.default_text_little_size));
            }
        }));
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText(R.string.upload_info_tip);
                holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
            }
        }));


        //上传身份证
        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                mFrontImageView = holder.v(R.id.card_front_view);
                mFrontImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFileType = FileType.FRONT;
                        ImagePickerHelper.startImagePicker(mActivity, true, true, false, false, 1);
                    }
                });

                mReverseImageView = holder.v(R.id.card_reverse_view);
                mReverseImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFileType = FileType.REVERSE;
                        ImagePickerHelper.startImagePicker(mActivity, true, true, false, false, 1);
                    }
                });

            }
        }));

        //证明材料
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

        //下一步
        items.add(ViewItemInfo.build(new ItemOffsetCallback(mBaseOffsetSize * 2) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText(R.string.submit);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyAuthUIView.checkEmpty(mEditText)) {
                            return;
                        }

                        if (TextUtils.isEmpty(frontPath) || TextUtils.isEmpty(reversePath)) {
                            T_.error(getString(R.string.upload_card_tip2));
                            return;
                        }

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
        HnLoading.show(mOtherILayout).addDismissListener(new UIIDialogImpl.OnDismissListener() {
            @Override
            public void onDismiss() {
                isCancel = true;
            }
        });

        new OssControl(new OssControl.OnUploadListener() {
            @Override
            public void onUploadStart() {

            }

            @Override
            public void onUploadSucceed(List<String> list) {
                frontUrl = list.get(0);
                if (isCancel) {
                    return;
                }
                new OssControl(new OssControl.OnUploadListener() {
                    @Override
                    public void onUploadStart() {

                    }

                    @Override
                    public void onUploadSucceed(List<String> list) {
                        reverseUrl = list.get(0);
                        if (isCancel) {
                            return;
                        }
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

                    @Override
                    public void onUploadFailed(int code, String msg) {

                    }
                }).uploadCircleImg(reversePath);
            }

            @Override
            public void onUploadFailed(int code, String msg) {

            }
        }).uploadCircleImg(frontPath);
    }

    private void onApplyNext(List<String> list) {
        add(RRetrofit.create(AuthService.class)
                .apply(Param.buildMap("type:" + (mAuthInfo.mType.getId() + 1),
                        "true_name:" + mAuthInfo.baseInfo.get(0),
                        "id_card:" + mAuthInfo.baseInfo.get(1),
                        "company:" + mAuthInfo.baseInfo.get(2),
                        "job:" + mAuthInfo.baseInfo.get(3),
                        "introduce:" + mAuthInfo.introduce,
                        "website:" + mAuthInfo.website,
                        "card_front:" + frontUrl,
                        "card_back:" + reverseUrl,
                        "industry_id:" + mAuthInfo.industry_id,
                        "desc:" + mEditText.string(),
                        "proof:" + RUtils.connect(list)

                ))
                .compose(Rx.transformer(String.class))
                .subscribe(new BaseSingleSubscriber<String>() {
                    @Override
                    public void onSucceed(String bean) {
                        super.onSucceed(bean);
                        T_.show(bean);
                    }

                    @Override
                    public void onEnd() {
                        super.onEnd();
                        HnLoading.hide();
                    }
                })
        );
    }

    private void displayImage(ImageView imageView, String filePath) {
        Glide.with(imageView.getContext())
                .load(filePath)
                .placeholder(R.drawable.zhanweitu_1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    /**
     * 当前文件选择器,返回后要添加到那个位置
     */
    private enum FileType {
        FRONT, REVERSE, CERTIFY
    }

    /**
     * 需要认证的信息
     */
    public static class AuthInfo {
        List<String> baseInfo;
        String industry_id;//	否	int	职场名人认证时必填
        String introduce;//是	string	人物介绍
        String website;//否	string	个人链接

        MyAuthUIView.AuthType mType;//【1-职场名人，2-娱乐明星，3-体育人物，4-政府人员】

        public AuthInfo(List<String> baseInfo, String industry_id, String introduce,
                        String website, MyAuthUIView.AuthType type) {
            this.baseInfo = baseInfo;
            this.industry_id = industry_id;
            this.introduce = introduce;
            this.website = website;
            mType = type;
        }
    }
}