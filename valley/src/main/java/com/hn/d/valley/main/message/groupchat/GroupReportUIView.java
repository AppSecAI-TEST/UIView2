package com.hn.d.valley.main.message.groupchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.widget.ItemInfoLayout;
import com.hn.d.valley.R;
import com.hn.d.valley.sub.other.ItemRecyclerUIView;

import java.util.List;

/**
 * Created by hewking on 2017/3/21.
 */
public class GroupReportUIView extends ItemRecyclerUIView<ItemRecyclerUIView.ViewItemInfo>{

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("举报");
    }

    @Override
    protected int getItemLayoutId(int vType) {

        if (mRExBaseAdapter.isLast(vType)) {
            return R.layout.item_button_view;
        }

        if (vType == 0) {
            return R.layout.item_single_text_view;
        }

        return R.layout.item_radio_view;
    }

    @NonNull
    @Override
    protected RExBaseAdapter<String, ViewItemInfo, String> createRExBaseAdapter() {
        return new ReportModeAdapter(mActivity,mItemsList);
    }

    @Override
    protected void createItems(List<ViewItemInfo> items) {

        final int line = mActivity.getResources().getDimensionPixelSize(R.dimen.base_line);
        final int left = mActivity.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);


        items.add(ViewItemInfo.build(new ItemCallback() {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                holder.tv(R.id.text_view).setText("请选择举报原因");
                holder.tv(R.id.text_view).setTextColor(getResources().getColor(R.color.main_text_color));
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, final int posInData, ViewItemInfo dataBean) {
                initItem(holder,"骚扰辱骂", posInData);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                initItem(holder,"淫秽色情",posInData);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                initItem(holder,"血腥暴力", posInData);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                initItem(holder,"欺诈(酒托，话费托等诈骗行为)", posInData);
            }
        }));

        items.add(ViewItemInfo.build(new ItemLineCallback(left,line) {
            @Override
            public void onBindView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                initItem(holder,"违法行为(诈骗，违禁品，反动等)", posInData);
            }
        }));

        items.add(ViewItemInfo.build(new ItemOffsetCallback(3 * left) {
            @Override
            public void onBindView(final RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
                TextView textView = holder.v(R.id.text_view);
                    textView.setText("下一步");
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.itemView.setSelected(true);
                        }
                    });
                }
        }));
    }

    private void initItem(RBaseViewHolder holder,String desc, final int posInData) {
        ItemInfoLayout infoLayout = holder.v(R.id.item_info_layout);
        infoLayout.setItemText(desc);

        holder.v(R.id.radio_view).setEnabled(false);
        holder.v(R.id.radio_view).setClickable(false);
        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRExBaseAdapter.setSelectorPosition(posInData);
            }
        });
    }


    public  class ReportModeAdapter extends RExBaseAdapter<String,ViewItemInfo,String>{

        public ReportModeAdapter(Context context,List<ViewItemInfo> items) {
            super(context,items);
            setModel(RModelAdapter.MODEL_SINGLE);
        }

        @Override
        protected int getDataItemType(int posInData) {
            return GroupReportUIView.this.getDataItemType(posInData);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, ViewItemInfo dataBean) {
            GroupReportUIView.this.onBindDataView(holder, posInData, dataBean);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return GroupReportUIView.this.getItemLayoutId(viewType);
        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, final RBaseViewHolder holder, int position, ViewItemInfo bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            if (position > 0 && position < 6) {
                CompoundButton compoundButton = holder.v(R.id.radio_view);
                if (isSelector) {
                    compoundButton.setVisibility(View.VISIBLE);
                    compoundButton.setChecked(isSelector);
                } else {
                    compoundButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        protected boolean onUnSelectorPosition(RBaseViewHolder viewHolder, int position, boolean isSelector) {
            CompoundButton compoundButton = viewHolder.v(R.id.radio_view);
            compoundButton.setChecked(false);
            compoundButton.setVisibility(View.INVISIBLE);
            return true;
        }
    }
}
