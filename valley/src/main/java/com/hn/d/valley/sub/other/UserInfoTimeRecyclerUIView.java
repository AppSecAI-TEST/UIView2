package com.hn.d.valley.sub.other;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RExItemDecoration;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupAdapter;
import com.angcyo.uiview.recycler.adapter.RGroupData;
import com.angcyo.uiview.utils.TimeUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.LikeUserInfoBean;
import com.hn.d.valley.main.me.UserDetailUIView2;
import com.hn.d.valley.widget.HnGenderView;
import com.hn.d.valley.widget.HnGlideImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 用户信息列表界面
 * Created by angcyo on 2017-01-15.
 */

public abstract class UserInfoTimeRecyclerUIView extends
        SingleRecyclerUIView<UserInfoTimeRecyclerUIView.UserInfoGroup> {

    private RGroupAdapter<String, UserInfoGroup, String> mGroupAdapter;

    /**
     * 返回小时:分钟
     */
    public static String formatHHMM(long date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return simpleDateFormat.format(new Date(date));
    }

    @Override
    public int getDefaultBackgroundColor() {
        return getColor(R.color.chat_bg_color);
    }

    @Override
    protected boolean hasDecoration() {
        return false;
    }

    @Override
    protected boolean isLoadInViewPager() {
        return false;
    }

    @Override
    protected RExBaseAdapter<String, UserInfoGroup, String> initRExBaseAdapter() {
        mGroupAdapter = new RGroupAdapter<String, UserInfoGroup, String>(mActivity) {
            @Override
            protected int getItemLayoutId(int viewType) {
                if (viewType == TYPE_GROUP_HEAD) {
                    return R.layout.item_single_main_text_view;
                } else if (viewType == TYPE_GROUP_DATA) {
                    return R.layout.item_user_info_time;
                }
                return super.getItemLayoutId(viewType);
            }
        };
        return mGroupAdapter;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        final int top = getDimensionPixelOffset(R.dimen.base_xhdpi);
        final int line = getDimensionPixelOffset(R.dimen.base_line);

        mRecyclerView.addItemDecoration(
                new RExItemDecoration(new RExItemDecoration.SingleItemCallback() {

                    @Override
                    public void getItemOffsets2(Rect outRect, int position, int edge) {
                        if (position == 0) {
                        } else if (mGroupAdapter.isInGroup(position)) {
                            outRect.top = top;
                        } else {
                            outRect.top = line;
                        }
                    }

                    @Override
                    public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
                        paint.setColor(Color.WHITE);
                        if (mGroupAdapter.isInGroup(position)) {
                        } else if (mGroupAdapter.isInGroup(position - 1)) {
                            offsetRect.set(0, itemView.getTop() - offsetRect.top,
                                    top, itemView.getTop());
                            canvas.drawRect(offsetRect, paint);
                        } else {
                            offsetRect.set(0, itemView.getTop() - offsetRect.top,
                                    (int) (density() * 70), itemView.getTop());
                            canvas.drawRect(offsetRect, paint);
                        }
                    }
                }));
    }

    /**
     * 接口返回之后, 通过此方法 设置数据,自动完成其他操作
     */
    public void setUserInfos(List<LikeUserInfoBean> list) {

        //先要判断之前最后一条数据的日期, 决定是否需要插入
        UserInfoGroup lastGroup = null;
        if (page != 1) {
            List<UserInfoGroup> allDatas = mRExBaseAdapter.getAllDatas();
            if (allDatas.size() > 0) {
                lastGroup = allDatas.get(allDatas.size() - 1);
            }
        }
        List<UserInfoGroup> userInfoGroups = getUserInfoGroups(list);

        if (lastGroup == null) {
            mRExBaseAdapter.resetAllData(userInfoGroups);
        } else {
            if (userInfoGroups.size() > 0) {
                UserInfoGroup firstGroup = userInfoGroups.get(0);
                if (lastGroup.year == firstGroup.year &&
                        lastGroup.month == firstGroup.month &&
                        lastGroup.day == firstGroup.day) {
                    lastGroup.appendDatas(mGroupAdapter, firstGroup.getAllDatas());
                    userInfoGroups.remove(0);
                }
                mRExBaseAdapter.appendData(userInfoGroups);
            }
        }

        if (mRExBaseAdapter.getItemCount() > 0) {
            showContentLayout();
        } else {
            showEmptyLayout();
        }

        if (1 == page && list.size() >= Constant.DEFAULT_PAGE_DATA_COUNT) {
            mRExBaseAdapter.setEnableLoadMore(true);
        }

        onUILoadDataFinish();
    }

    protected List<UserInfoGroup> getUserInfoGroups(List<LikeUserInfoBean> bean) {
        int lastYear = -1, lastMonth = -1, lastDay = -1;
        int nowYear = -1, nowMonth = -1, nowDay = -1;//现在的年月份

        List<UserInfoGroup> groups = new ArrayList<>();

        String now = TimeUtil.getDateTimeString(System.currentTimeMillis(), "yyyy/MM/dd");
        String[] nowSplit = now.split("/");
        if (nowSplit.length == 3) {
            nowYear = Integer.valueOf(nowSplit[0]);
            nowMonth = Integer.valueOf(nowSplit[1]);
            nowDay = Integer.valueOf(nowSplit[2]);
        }

        UserInfoGroup group = null;

        List<LikeUserInfoBean> users = new ArrayList<>();
        for (int i = 0; i < bean.size(); i++) {
            LikeUserInfoBean infoBean = bean.get(i);
            String formatDateTime = TimeUtil.getDateTimeString(Long.parseLong(infoBean.getCreated()) * 1000, "yyyy/MM/dd");

            String[] split = formatDateTime.split("/");
            if (split.length == 3) {
                if (i == 0) {
                    lastYear = Integer.valueOf(split[0]);
                    lastMonth = Integer.valueOf(split[1]);
                    lastDay = Integer.valueOf(split[2]);

                    users.add(infoBean);
                    group = new UserInfoGroup(users,
                            createTime(lastYear, lastMonth, lastDay, nowYear, nowMonth, nowDay))
                            .setYear(lastYear).setMonth(lastMonth).setDay(lastDay);
                } else {
                    int year = Integer.valueOf(split[0]);
                    int month = Integer.valueOf(split[1]);
                    int day = Integer.valueOf(split[2]);

                    if (lastYear == year && lastMonth == month && lastDay == day) {
                        users.add(infoBean);
                    } else {
                        groups.add(group);
                        lastYear = year;
                        lastMonth = month;
                        lastDay = day;

                        users = new ArrayList<>();
                        users.add(infoBean);
                        group = new UserInfoGroup(users,
                                createTime(lastYear, lastMonth, lastDay, nowYear, nowMonth, nowDay))
                                .setYear(lastYear).setMonth(lastMonth).setDay(lastDay);
                    }
                }
            }
        }
        if (group != null) {
            groups.add(group);
        }
        return groups;
    }

    private String createTime(int lastYear, int lastMonth, int lastDay,
                              int nowYear, int nowMonth, int nowDay) {
        String time;
        if (lastYear == nowYear && lastMonth == nowMonth && lastDay == nowDay) {
            time = getString(R.string.today);
        } else if (lastYear == nowYear && lastMonth == nowMonth && lastDay + 1 == nowDay) {
            time = getString(R.string.yesterday);
        } else if (lastYear == nowYear) {
            time = getString(R.string.time_format, lastMonth, lastDay);
        } else {
            time = getString(R.string.full_time_format, lastYear, lastMonth, lastDay);
        }
        return time;
    }

    protected class UserInfoGroup extends RGroupData<LikeUserInfoBean> {

        public int year;
        public int month;
        public int day;
        private String time;

        public UserInfoGroup(List<LikeUserInfoBean> allDatas, String time) {
            super(allDatas);
            this.time = time;
        }

        public UserInfoGroup setYear(int year) {
            this.year = year;
            return this;
        }

        public UserInfoGroup setMonth(int month) {
            this.month = month;
            return this;
        }

        public UserInfoGroup setDay(int day) {
            this.day = day;
            return this;
        }

        @Override
        protected void onBindGroupView(RBaseViewHolder holder, int position, int indexInGroup) {
            super.onBindGroupView(holder, position, indexInGroup);
            holder.tv(R.id.text_view).setText(time);
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int position, int indexInData) {
            super.onBindDataView(holder, position, indexInData);
            final LikeUserInfoBean likeUserInfoBean = getAllDatas().get(indexInData);
            holder.itemView.setBackgroundColor(Color.WHITE);

            HnGlideImageView imageView = holder.v(R.id.image_view);
            imageView.setImageThumbUrl(likeUserInfoBean.getAvatar());

            holder.tv(R.id.username).setText(likeUserInfoBean.getUsername());
            HnGenderView genderView = holder.v(R.id.grade_view);
            genderView.setGender(likeUserInfoBean.getSex(), likeUserInfoBean.getGrade());

            holder.tv(R.id.time_view).setText(formatHHMM(Long.valueOf(likeUserInfoBean.getCreated()) * 1000l));

            holder.v(R.id.item_root_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIView(new UserDetailUIView2(likeUserInfoBean.getUid()));
                }
            });
        }
    }
}
