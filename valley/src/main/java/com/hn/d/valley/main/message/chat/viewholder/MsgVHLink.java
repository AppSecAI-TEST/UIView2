package com.hn.d.valley.main.message.chat.viewholder;

import android.support.v4.widget.TextViewCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.utils.Json;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.MoonUtil;
import com.hn.d.valley.main.message.attachment.LinkMsgAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.ChatUIView2;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.message.groupchat.RequestCallback;
import com.hn.d.valley.utils.HtmlFrom;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import rx.functions.Action1;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHLink extends MsgViewHolderBase {

    RelativeLayout linkLayout;
    TextView shareContent;
    ImageView shareImage;
    TextView tv_link_content;

    public MsgVHLink(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder,data,position,isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_text_link_layout;
    }

    @Override
    protected void inflateContentView() {
        linkLayout = (RelativeLayout) findViewById(R.id.link_layout);
        shareContent = (TextView) findViewById(R.id.share_cotent);
        shareImage = (ImageView) findViewById(R.id.share_image);
        tv_link_content = (TextView) findViewById(R.id.tv_link_content);
    }

    @Override
    protected void bindContentView() {
        final String msg = message.getContent();
        if (TextUtils.isEmpty(msg)) {
            return;
        }

//        LinkMsgAttachment attachment = (LinkMsgAttachment) message.getAttachment();
        Map<String, Object> localExtension = message.getLocalExtension();
        if (localExtension == null) {
            return;
        }
        String content = (String) localExtension.get("link");
        HtmlFrom.LinkBean linkBean = Json.from(content,HtmlFrom.LinkBean.class);

        processResult(linkBean);
    }

    public static boolean isLinkMsg(IMMessage msg) {
        if (msg.getMsgType() != MsgTypeEnum.text) {
            return false;
        }
        Pattern pattern = Patterns.WEB_URL;
        return pattern.matcher(msg.getContent().toLowerCase().trim()).matches();
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
//                MoonUtil.show(context, contentView, message.getContent());
            }
        });
    }

    public static void request(final IMMessage message , final Action1<IMMessage> action) {
        HtmlFrom.getPageAsyc(message.getContent(), new RequestCallback<HtmlFrom.LinkBean>() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(HtmlFrom.LinkBean linkBean) {
                message.setAttachment(new LinkMsgAttachment(linkBean));
                //持久化 attachment 到数据库
//                ChatUIView2.msgService().updateIMMessageStatus(message);
                Map<String, Object> localExtension = message.getLocalExtension();
                if (localExtension == null) {
                    localExtension = new HashMap<>();
                    localExtension.put("link", Json.to(linkBean));
                    message.setLocalExtension(localExtension);
                    msgService().updateIMMessage(message);
                }

                action.call(message);
            }

            @Override
            public void onError(String msg) {
                action.call(message);
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

        Glide.with(context)
                .load(linkBean.getImg())
                .placeholder(R.drawable.rc_ic_def_rich_content)
                .into(shareImage);

//        contentView.setVisibility(View.GONE);
        tv_link_content.setText(linkBean.getContent());
        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUIBaseView.startIView(new X5WebUIView(message.getContent()));
            }
        });
    }
}
