package com.hn.d.valley.main.message.chat.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseItemDecoration;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hn.d.valley.R;
import com.hn.d.valley.main.message.attachment.HotSpotInfo;
import com.hn.d.valley.main.message.attachment.HotSpotInfoAttachment;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.x5.X5WebUIView;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgVHHotSpotInfo extends MsgViewHolderBase {

    static int TOP_TYPE = 0;
    static int NORMOL_TYPE = 1;

    private RRecyclerView mRecyclerView;
    private RelativeLayout mItemlayout;

    public MsgVHHotSpotInfo(BaseMultiAdapter<RBaseViewHolder> adapter) {
        super(adapter);
    }

    @Override
    public void convert(RBaseViewHolder holder, IMMessage data, int position, boolean isScrolling) {
        super.convert(holder, data, position, isScrolling);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_item_push_list;
    }

    @Override
    protected void inflateContentView() {
        mRecyclerView = (RRecyclerView) findViewById(R.id.rv_msg_push);
        mItemlayout = (RelativeLayout) findViewById(R.id.item_layout);
    }

    @Override
    protected void bindContentView() {
        HotSpotInfoAttachment hotSpotInfoAttachment = (HotSpotInfoAttachment) message.getAttachment();
        if (hotSpotInfoAttachment == null)  {
            return;
        }

//        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mItemlayout.getLayoutParams();
//        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
//        mItemlayout.setLayoutParams(params);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new RBaseItemDecoration());
        mRecyclerView.setAdapter(new RBaseAdapter<HotSpotInfo.NewsBean>(context, hotSpotInfoAttachment.getHotSpotInfo().getNews()) {
            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TOP_TYPE) {
                    return R.layout.msg_item_push_picture_text_layout;
                } else if (viewType == NORMOL_TYPE) {
                    return R.layout.msg_item_push_multi_imagelayout;
                }
                return R.layout.msg_item_push_multi_imagelayout;
            }

            @Override
            public int getItemType(int position) {
                return position == 0 ? TOP_TYPE : NORMOL_TYPE;
            }

            @Override
            protected void onBindView(RBaseViewHolder holder, int position, HotSpotInfo.NewsBean news) {

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                holder.itemView.setLayoutParams(params);

                ImageView imageView = holder.imgV(R.id.image_view);
                TextView desc = holder.tv(R.id.tv_desc);

                String[] imgs = news.getImgs().split(";");

                desc.setText(news.getTitle());
                Glide.with(context)
                        .load(imgs[0])
                        .placeholder(com.angcyo.uiview.R.drawable.default_image)
                        .error(com.angcyo.uiview.R.drawable.default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mUIBaseView.startIView(new X5WebUIView(bean.get("link")));
                    }
                });
            }
        });
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    protected boolean isShowBubble() {
        return false;
    }
}
