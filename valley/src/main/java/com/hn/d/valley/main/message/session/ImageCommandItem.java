package com.hn.d.valley.main.message.session;

import android.content.Intent;
import android.view.View;

import com.angcyo.library.okhttp.Ok;
import com.angcyo.uiview.github.luban.Luban;
import com.angcyo.uiview.utils.file.FileUtil;
import com.angcyo.uiview.utils.media.BitmapDecoder;
import com.hn.d.valley.R;
import com.hn.d.valley.widget.HnLoading;
import com.lzy.imagepicker.ImagePickerHelper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rx.functions.Action0;
import rx.functions.Action1;

import static com.lzy.imagepicker.ImagePickerHelper.REQUEST_CODE;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 15:10
 * 修改人员：hewking
 * 修改时间：2017/05/18 15:10
 * 修改备注：
 * Version: 1.0.0
 */
public class ImageCommandItem extends CommandItemInfo{

    public ImageCommandItem(){
        this(R.drawable.zhaopian_xiaoxi, "图片");
    }

    public ImageCommandItem(int icoResId, String text) {
        super(icoResId, text);
    }

    @Override
    protected void onClick() {
        ImagePickerHelper.startImagePicker(getContainer().activity, false, false, 9);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 100 为imagepickerhleper 指定
        if (requestCode == REQUEST_CODE) {
            final ArrayList<String> images = ImagePickerHelper.getImages(getContainer().activity, requestCode, resultCode, data);
            if (images.isEmpty()) {
                return;
            }
            HnLoading.show(getContainer().mLayout);
            Luban.luban(getContainer().activity, images)
                    .subscribe(new Action1<ArrayList<String>>() {
                        @Override
                        public void call(ArrayList<String> strings) {
                            sendPictureAndGif(strings);
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
    }

    private void sendPictureAndGif(ArrayList<String> strings) {
        //发送图片和gif图
        boolean isGif = false;
        String path = strings.get(0);
        File file = new File(path);

        try {
            InputStream is = new FileInputStream(file);
            String imageType = Ok.ImageTypeUtil.getImageType(is);
            Ok.ImageType type = Ok.ImageType.of(imageType);
            if (type == Ok.ImageType.GIF) {
                isGif = true;
            }
            is.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        if (isGif) {
            IMMessage gifMessage =
                    MessageBuilder.createFileMessage(getContainer().account, getContainer().sessionType, file, FileUtil.getFileNameNoEx(path));
            Map<String,Object> remoteExtension =  new HashMap<>();
            String size = null;
            int[] bounds = null;
            bounds = BitmapDecoder.decodeBound(new File(path));
            if (bounds != null) {
                size = "{" + bounds[0] + "," + bounds[1] + "}";
            }

            remoteExtension.put("size",size);
            remoteExtension.put("extend_type","gifTypeImage");
            gifMessage.setRemoteExtension(remoteExtension);
            getContainer().proxy.sendMessage(gifMessage);

        } else {
            IMMessage imageMessage =
                    MessageBuilder.createImageMessage(getContainer().account, getContainer().sessionType, new File(strings.get(0)));
            getContainer().proxy.sendMessage(imageMessage);
        }
    }
}
