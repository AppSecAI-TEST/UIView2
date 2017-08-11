package com.hn.d.valley.main.message.chat.viewholder;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.angcyo.library.utils.L;
import com.angcyo.uidemo.layout.demo.view.HnCardView;
import com.angcyo.uidemo.layout.demo.view.HnDiceView;
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
import java.util.UUID;

import static com.hn.d.valley.main.message.chat.ChatUIView2.msgService;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHExpression extends MsgViewHolderBase {

//    private boolean animrunning;
    ImageView draweeView;
    HnDiceView diceLayout;
    HnCardView pokerLayout;

    public MsgVHExpression(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_chartlet_layout;
    }

    @Override
    protected void inflateContentView() {
        L.d("MsgVHExpression  inflateContentView; ");
//        if (draweeView == null) {
            draweeView = (ImageView) findViewById(R.id.msg_image_view);
//        }

//        if (diceLayout == null) {
            diceLayout = (HnDiceView) findViewById(R.id.diceLayout);

//        }

//        if (pokerLayout == null) {
            pokerLayout = (HnCardView) findViewById(R.id.pokerlayout);
//        }
//        DiceView diceView = (DiceView) findViewById(R.id.diceView);

    }

    @Override
    protected void bindContentView() {
        contentContainer.setBackground(null);

        CustomExpressionAttachment expressionAttachment = (CustomExpressionAttachment) message.getAttachment();
        if (expressionAttachment == null) {
            return;
        }

        CustomExpressionMsg expressionMsg = expressionAttachment.getExpressionMsg();
        L.d("MsgVHExpression  bindContentView; ");

        if (expressionMsg.getType() == 3) {
            // 骰子
            // sending 状态取消
            statusSendingView.setVisibility(View.GONE);
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
                final String[] split = dicecount.split(",");
                final int[] counts = new int[split.length];
                int i = 0;
                for (String count : split) {
                    counts[i++] = Integer.valueOf(count);
                }
                L.d("MsgVHExpression dice ; " + dicecount);
                Map<String, Object> localExtension = message.getLocalExtension();
                if (localExtension == null) {
                    localExtension = new HashMap<>();
                    localExtension.put("read", true);
                    message.setLocalExtension(localExtension);
                    msgService().updateIMMessage(message);
                    // 动画启动
//                    diceLayout.setFirst(true);
//                    diceLayout.init(split.length, counts,true);
                    diceLayout.startRoll(UUID.randomUUID().toString(),counts);
//                    animrunning = true;
//                    view.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            animrunning = false;
//                        }
//                    },1000);
                } else {
//                    if(animrunning) {
//                        diceLayout.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                diceLayout.init(split.length, counts,false);
//                            }
//                        },1000);
//                    } else {
//                        diceLayout.init(split.length, counts,false);
                        diceLayout.setTargetDice(counts);
//                    }
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
                pokerLayout.setTargetCards(pokercount);

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
