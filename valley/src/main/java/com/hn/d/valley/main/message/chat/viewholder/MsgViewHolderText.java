package com.hn.d.valley.main.message.chat.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.utils.HtmlFrom;
import com.hn.d.valley.utils.Regex;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.regex.Pattern;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderText extends MsgViewHolderBase {

    TextView contentView;
    LinearLayout linkLayout;
    TextView shareContent;
    HnGlideImageView shareImage;

    public MsgViewHolderText(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder,data,position,isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_text_layout;
    }

    @Override
    protected void inflateContentView() {
        contentView = (TextView) findViewById(R.id.msg_text_view);
        linkLayout = (LinearLayout) findViewById(R.id.link_layout);
        shareContent = (TextView) findViewById(R.id.share_cotent);
        shareImage = (HnGlideImageView) findViewById(R.id.share_image);
    }

    @Override
    protected void bindContentView() {


        final String msg = message.getContent();
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        String regex = Regex.URL_PATTERN2;
        Pattern pattern = Pattern.compile(regex);
        if (pattern.matcher(msg.toLowerCase().trim()).matches()) {

            LinkBeanWrap bean = (LinkBeanWrap) contentContainer.getTag();
            if (bean != null) {

                if (getViewHolder().getLayoutPosition() != bean.position){
                    return;
                }

                boolean isFetch = bean.isError;

                if(!isFetch) {
                    return;
                }
                if (bean.linkBean != null) {
                  processResult(bean.linkBean);
                } else {
                    linkLayout.setVisibility(View.GONE);
                    MoonUtil.show(context, contentView, message.getContent());
                }
            } else {
                requestLink(msg);
            }

        } else {
            linkLayout.setVisibility(View.GONE);
            MoonUtil.show(context, contentView, message.getContent());
        }

    }

    private void requestLink(String msg) {
        HtmlFrom.getPageAsyc(msg, new RequestCallback<HtmlFrom.LinkBean>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(HtmlFrom.LinkBean linkBean) {
                processResult(linkBean);
                contentContainer.setTag(new LinkBeanWrap(linkBean,false,getViewHolder().getLayoutPosition()));
            }

            @Override
            public void onError(String msg) {
//                contentContainer.setTag(2,false);
                contentContainer.setTag(new LinkBeanWrap(null,true,getViewHolder().getLayoutPosition()));
                linkLayout.setVisibility(View.GONE);
                MoonUtil.show(context, contentView, message.getContent());
            }
        });
    }

    public static class LinkBeanWrap{
        HtmlFrom.LinkBean linkBean;
        boolean isError = false;
        int position;

        public LinkBeanWrap(HtmlFrom.LinkBean linkBean, boolean isError,int position) {
            this.linkBean = linkBean;
            this.isError = isError;
            this.position = position;
        }
    }

    private void processResult(HtmlFrom.LinkBean linkBean) {
        shareContent.setText(linkBean.getTitle());
        shareImage.setImageUrl(linkBean.getImg());
        linkLayout.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);

        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new X5WebUIView(message.getContent()));
            }
        });
    }
}
