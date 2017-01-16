package com.hn.d.valley.control;

import android.view.View;

import com.angcyo.library.facebook.DraweeViewUtil;
import com.angcyo.uiview.utils.RUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hn.d.valley.R;

import java.util.List;

import rx.functions.Action1;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/16 18:55
 * 修改人员：Robi
 * 修改时间：2017/01/16 18:55
 * 修改备注：
 * Version: 1.0.0
 */
public class MediaTypeControl {
    public static void initMedia(String media, String media_type, SimpleDraweeView mediaImageView,
                                 final Action1<List<String>> onMediaClick) {
        if ("1".equalsIgnoreCase(media_type)) {
            //文本类型
            mediaImageView.setVisibility(View.GONE);
            return;
        }

        final List<String> medias = RUtils.split(media);
        if (medias.isEmpty()) {
            mediaImageView.setVisibility(View.GONE);
        } else {
            if ("3".equalsIgnoreCase(media_type)) {
                //图片
                mediaImageView.setVisibility(View.VISIBLE);
                Object tag = mediaImageView.getTag();
                if (tag == null || !tag.toString().equalsIgnoreCase(medias.get(0))) {
                    mediaImageView.setTag(medias.get(0));
                    DraweeViewUtil.resize(mediaImageView, medias.get(0));
                }

                //点击预览全部图片
                if (onMediaClick != null) {
                    mediaImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onMediaClick.call(medias);
                        }
                    });
                }

            } else if ("2".equalsIgnoreCase(media_type)) {
                //视频
                mediaImageView.setVisibility(View.VISIBLE);
                DraweeViewUtil.setDraweeViewRes(mediaImageView, R.drawable.video_release);
            }
        }
    }
}
