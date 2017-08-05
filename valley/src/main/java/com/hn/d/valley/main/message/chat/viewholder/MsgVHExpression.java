package com.hn.d.valley.main.message.chat.viewholder;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hn.d.valley.R;
import com.hn.d.valley.emoji.StickerManager;
import com.hn.d.valley.main.message.attachment.CustomExpressionAttachment;
import com.hn.d.valley.main.message.attachment.CustomExpressionMsg;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.widget.DiceLayout;
import com.hn.d.valley.widget.PokerLayout;
import com.netease.nimlib.sdk.msg.constant.StickerEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHExpression extends MsgViewHolderBase {

    public MsgVHExpression(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_chartlet_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {
        ImageView draweeView = (ImageView) findViewById(R.id.msg_image_view);
//        DiceView diceView = (DiceView) findViewById(R.id.diceView);

        DiceLayout diceLayout = (DiceLayout) findViewById(R.id.diceLayout);
        PokerLayout pokerLayout = (PokerLayout) findViewById(R.id.pokerlayout);

        contentContainer.setBackground(null);

        CustomExpressionAttachment expressionAttachment = (CustomExpressionAttachment) message.getAttachment();
        if (expressionAttachment == null) {
            return;
        }

        CustomExpressionMsg expressionMsg = expressionAttachment.getExpressionMsg();

        if (expressionMsg.getType() == 3) {
            // 骰子
            draweeView.setVisibility(View.GONE);
            pokerLayout.setVisibility(View.GONE);
            diceLayout.setVisibility(View.VISIBLE);

            String extend = expressionMsg.getExtend();
            try {
                if (TextUtils.isEmpty(extend)) {
                    return;
                }
                JSONObject object = new JSONObject(extend);
                String dicecount = object.optString("dicecount");
                String[] split = dicecount.split(",");
                int[] counts = new int[split.length];
                int i = 0;
                for (String count : split) {
                    counts[i++] = Integer.valueOf(count);
                }

                Map<String, Object> localExtension = message.getLocalExtension();
                if (localExtension == null) {
                    localExtension = new HashMap<>();
                    localExtension.put("read", true);
                    message.setLocalExtension(localExtension);
                    msgService().updateIMMessage(message);
                    // 动画启动
                    diceLayout.init(split.length, counts,true);
                } else {
                    diceLayout.init(split.length, counts,false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (expressionMsg.getType() == 4) {
            // 扑克牌
            draweeView.setVisibility(View.GONE);
            diceLayout.setVisibility(View.GONE);
            pokerLayout.setVisibility(View.VISIBLE);

            String extend = expressionMsg.getExtend();
            if (TextUtils.isEmpty(extend)) {
                return;
            }
            try {
                JSONObject object = new JSONObject(extend);
                String pokercount = object.optString("pokercount");
                pokerLayout.init(pokercount);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            diceLayout.setVisibility(View.GONE);
            draweeView.setVisibility(View.VISIBLE);
            pokerLayout.setVisibility(View.GONE);

            String chartlet = expressionMsg.getMsg();
            String category = StickerEnum.Companion.typeOfValue(expressionMsg.getType()).getType();
            Glide.with(context)
                    .load(Uri.parse(StickerManager.getInstance().getStickerBitmapUri(category, chartlet)))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(draweeView);
        }
    }


}
