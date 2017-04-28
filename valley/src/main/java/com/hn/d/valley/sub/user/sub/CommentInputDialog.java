package com.hn.d.valley.sub.user.sub;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.base.UIBaseRxView;
import com.angcyo.uiview.base.UIIDialogImpl;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;
import com.angcyo.uiview.utils.string.SingleTextWatcher;
import com.angcyo.uiview.widget.ExEditText;
import com.angcyo.uiview.widget.RSoftInputLayout;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.FriendBean;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.ContactItem;
import com.hn.d.valley.main.message.EmojiLayoutControl;
import com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter;
import com.hn.d.valley.main.message.groupchat.ContactSelectUIVIew;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action3;

import static com.hn.d.valley.main.message.groupchat.BaseContactSelectAdapter.Options.DEFALUT_LIMIT;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：评论输入对话框
 * 创建人员：Robi
 * 创建时间：2017/04/28 11:46
 * 修改人员：Robi
 * 修改时间：2017/04/28 11:46
 * 修改备注：
 * Version: 1.0.0
 */
public class CommentInputDialog extends UIIDialogImpl {
    InputConfig mInputConfig;
    private EmojiLayoutControl mEmojiLayoutControl;
    private String mImagePath = "";//选择的图片
    private List<String> atUsers = new ArrayList<>();//@的用户
    private List<FriendBean> mFriendList = new ArrayList<>();
    private RSoftInputLayout mSoftInputLayout;
    private ExEditText mInputView;

    public CommentInputDialog(InputConfig inputConfig) {
        mInputConfig = inputConfig;
    }

    @Override
    public boolean canCanceledOnOutside() {
        return false;
    }

    @Override
    protected View inflateDialogView(RelativeLayout dialogRootLayout, LayoutInflater inflater) {
        return inflate(R.layout.dialog_comment_input_layout);
    }

    @Override
    public boolean onBackPressed() {
        return mSoftInputLayout.requestBackPressed();
    }

    @Override
    protected void initDialogContentView() {
        super.initDialogContentView();
        mSoftInputLayout = mViewHolder.v(R.id.root_layout);
        mInputView = mViewHolder.v(R.id.input_view);

        mViewHolder.v(R.id.content_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finishDialog();
                mSoftInputLayout.requestBackPressed();
            }
        });

        //输入框
        ExEditText exEditText = mViewHolder.v(R.id.input_view);
        exEditText.setHint("说点什么吧");

        //控制按钮
        initControlLayout();
        //表情切换
        initEmojiLayout();

        //输入, 发送按钮
        initSendLayout();

        //自动弹出键盘

        mSoftInputLayout.showSoftInput(mInputView);

        if (mInputConfig != null) {
            mInputConfig.onInitDialogLayout(mViewHolder);
        }
    }

    private void initEmojiLayout() {
        final ImageView imageView = mViewHolder.v(R.id.ico_exp);

        mSoftInputLayout.addOnEmojiLayoutChangeListener(new RSoftInputLayout.OnEmojiLayoutChangeListener() {
            @Override
            public void onEmojiLayoutChange(boolean isEmojiShow, boolean isKeyboardShow, int height) {
                if (isEmojiShow) {
                    imageView.setImageResource(R.drawable.icon_keyboard);
                } else {
                    imageView.setImageResource(R.drawable.expression_comments_n);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSoftInputLayout.isEmojiShow()) {
                    mSoftInputLayout.showSoftInput(mInputView);
                } else {
                    mSoftInputLayout.showEmojiLayout();
                }
            }
        });

        mEmojiLayoutControl = new EmojiLayoutControl(mViewHolder, new EmojiLayoutControl.OnEmojiSelectListener() {
            @Override
            public void onEmojiText(String emoji) {
                if (emoji.equals("/DEL")) {
                    mInputView.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else {
                    final int selectionStart = mInputView.getSelectionStart();
                    mInputView.getText().insert(selectionStart, emoji);
                    MoonUtil.show(mActivity, mInputView, mInputView.getText().toString());
                    mInputView.setSelection(selectionStart + emoji.length());
                    mInputView.requestFocus();
                }
            }
        });
    }

    private void initSendLayout() {
        final TextView sendView = mViewHolder.v(R.id.send_view);
        ExEditText editText = mViewHolder.v(R.id.input_view);
        editText.addTextChangedListener(new SingleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                sendView.setEnabled(s.length() > 0);
            }
        });
        //输入@字符, 自动弹出选择联系人
        editText.setOnMentionInputListener(new ExEditText.OnMentionInputListener() {
            @Override
            public void onMentionCharacterInput() {
                selectorContact();
            }

            @Override
            public void onMentionTextChanged(List<String> allMention) {
//                for (String s : allMention) {
//                    L.e("call: onMentionTextChanged([allMention])-> " + s);
//                }
            }
        });

        ResUtil.setBgDrawable(sendView, ResUtil.generateRippleRoundMaskDrawable(density() * 3,
                Color.WHITE,
                SkinHelper.getSkin().getThemeDarkColor(),
                getColor(R.color.colorDisable),
                SkinHelper.getSkin().getThemeSubColor()
        ));

        /**发送*/
        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDialog();
                if (mInputConfig != null) {
                    mInputConfig.onSendClick(mImagePath, fixMentionString());
                }
            }
        });
    }

    /**
     * 替换@功能的文本信息
     */
    private String fixMentionString() {
        String string = mInputView.string();
        List<String> allMention = mInputView.getAllMention();
        for (String s : allMention) {
            string = string.replaceAll("@" + s, createStringWithUserName(s));
        }
        return string;
    }

    private String createStringWithUserName(String userName) {
        String id = "";
        for (FriendBean bean : mFriendList) {
            if (TextUtils.equals(bean.getDefaultMark(), userName)) {
                id = bean.getUid();
                break;
            }
        }
        return "<m>" + id + "</m>";
    }


    private void initControlLayout() {

        mViewHolder.v(R.id.ico_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePickerHelper.startImagePicker(mActivity, true, true, false, false, 1);
            }
        });
        mViewHolder.v(R.id.ico_at).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mInputView.insert("@");
                selectorContact();
            }
        });
        mViewHolder.v(R.id.ico_gif).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 选择联系人
     */
    private void selectorContact() {
        final ExEditText mInputView = mViewHolder.v(R.id.input_view);

        ContactSelectUIVIew.start(mILayout,
                new BaseContactSelectAdapter.Options(RModelAdapter.MODEL_MULTI, DEFALUT_LIMIT, true),
                atUsers, new Action3<UIBaseRxView, List<AbsContactItem>, RequestCallback>() {
                    @Override
                    public void call(UIBaseRxView uiBaseRxView,
                                     List<AbsContactItem> absContactItems,
                                     RequestCallback requestCallback) {
                        atUsers.clear();
                        mFriendList.clear();

                        requestCallback.onSuccess("");

                        for (AbsContactItem item : absContactItems) {
                            if (item instanceof ContactItem) {
                                FriendBean friendBean = ((ContactItem) item).getFriendBean();
                                atUsers.add(friendBean.getUid());
                                mFriendList.add(friendBean);
                                mInputView.addMention(friendBean.getDefaultMark());
                            }
                        }
                    }
                });
    }

    private void initPreviewLayout(String imagePath) {
        this.mImagePath = imagePath;

        View previewControlLayout = mViewHolder.v(R.id.preview_control_layout);
        if (TextUtils.isEmpty(imagePath)) {
            previewControlLayout.setVisibility(View.GONE);
        } else {
            previewControlLayout.setVisibility(View.VISIBLE);
            Glide.with(mActivity)
                    .load(imagePath)
                    .into(mViewHolder.imgV(R.id.preview_image_view));
        }

        mViewHolder.v(R.id.preview_delete_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPreviewLayout("");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ArrayList<String> images = ImagePickerHelper.getImages(mActivity, requestCode, resultCode, data);
        final boolean origin = ImagePickerHelper.isOrigin(requestCode, resultCode, data);
        if (images.isEmpty()) {
            return;
        }
        HnLoading.show(mILayout);
        Luban.luban(mActivity, images)
                .subscribe(new Action1<ArrayList<String>>() {
                    @Override
                    public void call(ArrayList<String> strings) {
                        initPreviewLayout(strings.get(0));
                        L.e("call: call([strings])-> " + strings.get(0));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HnLoading.hide();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        HnLoading.hide();
                    }
                });
    }

    public interface InputConfig {

        /**
         * 在此方法中, 可以初始化一些对话框的默认资源
         */
        void onInitDialogLayout(RBaseViewHolder viewHolder);

        /**
         * 当点击发送按钮之后,回调
         *
         * @param imagePath 选择的图片路径, 自行非空判断
         * @param content   输入框中的内容
         */
        void onSendClick(String imagePath, String content);
    }
}
