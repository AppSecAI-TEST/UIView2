package com.hn.d.valley.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.recycler.RBaseAdapter;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.widget.RCheckGroup;
import com.hn.d.valley.R;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/29 16:52
 * 修改人员：Robi
 * 修改时间：2016/12/29 16:52
 * 修改备注：
 * Version: 1.0.0
 */
public class HnTestActivity extends AppCompatActivity {

    public static void launcher(Activity activity) {
        Intent intent = new Intent(activity, HnTestActivity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.base_tran_to_left_enter, R.anim.base_tran_to_left_exit);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sex_filter);

        RCheckGroup checkGroup = (RCheckGroup) findViewById(R.id.check_group_view);
        checkGroup.setOnCheckChangedListener(new RCheckGroup.OnCheckChangedListener() {
            @Override
            public void onChecked(View fromm, View to) {
                if (fromm == null) {
                    L.e("onChecked fromm:" + null + " ->to:" + to.getId());
                } else {
                    L.e("onChecked fromm:" + fromm.getId() + " ->to:" + to.getId());
                }
            }

            @Override
            public void onReChecked(View view) {
                L.e("onReChecked:" + view.getId());
            }
        });

//        TextureMapView mapView = view(R.id.map_view);
//        mapView.onCreate(savedInstanceState);

//        final RRecyclerView rRecyclerView = view(R.id.recycler_view);
//        final EditText editText = view(R.id.input_view);
//        final TestAdapter adapter = new TestAdapter(this);
//        rRecyclerView.setAdapter(adapter);
//        final InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

//        view(R.id.show).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editText.requestFocus();
//                manager.showSoftInput(editText, 0);
//            }
//        });
//        view(R.id.hide).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                manager.hideSoftInputFromWindow(rRecyclerView.getWindowToken(), 0);
//            }
//        });
//        view(R.id.scroll).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((LinearLayoutManager) rRecyclerView.getLayoutManager()).scrollToPositionWithOffset(adapter.getItemCount() - 1, 0);
//            }
//        });
//        view(R.id.scroll2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rRecyclerView.smoothScrollToPosition(3);
//            }
//        });

    }

    public <T extends View> T view(int id) {
        return (T) findViewById(id);
    }

    class TestAdapter extends RBaseAdapter<String> {

        public TestAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        @Override
        protected View createContentView(ViewGroup parent, int viewType) {
            TextView textView = new TextView(HnTestActivity.this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(-1, 300));
            return textView;
        }

        @Override
        protected void onBindView(RBaseViewHolder holder, int position, String bean) {
            ((TextView) holder.itemView).setText("--> " + position);
        }
    }
}
