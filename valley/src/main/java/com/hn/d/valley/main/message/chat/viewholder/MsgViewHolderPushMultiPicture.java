package com.hn.d.valley.main.message.chat.viewholder;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RRecyclerView;
import com.angcyo.uiview.recycler.adapter.RBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hn.d.valley.R;
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

public class MsgViewHolderPushMultiPicture extends MsgViewHolderBase {

    static int TOP_TYPE = 0;
    static int NORMOL_TYPE = 1;

    private RRecyclerView mRecyclerView;

    public MsgViewHolderPushMultiPicture(BaseMultiAdapter<RBaseViewHolder> adapter) {
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
    }

    @Override
    protected void bindContentView() {
        Map<String, Object> extension = message.getRemoteExtension();
        final ArrayList<HashMap<String, String>> dataList = (ArrayList<HashMap<String, String>>) extension.get("data_list");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setAdapter(new RBaseAdapter<HashMap<String, String>>(context, dataList) {
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
            protected void onBindView(RBaseViewHolder holder, int position, final HashMap<String, String> bean) {

                ImageView imageView = holder.imgV(R.id.image_view);
                TextView desc = holder.tv(R.id.tv_desc);

                desc.setText(bean.get("title"));
                Glide.with(context)
                        .load(bean.get("thumb"))
                        .placeholder(com.angcyo.uiview.R.drawable.default_image)
                        .error(com.angcyo.uiview.R.drawable.default_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUIBaseView.startIView(new X5WebUIView(bean.get("link")));
                    }
                });
            }

        });

    }
}
