package com.hn.d.valley.main.message.search;

import com.angcyo.uiview.mvp.presenter.IBasePresenter;
import com.angcyo.uiview.mvp.view.IBaseView;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.message.query.TextQuery;

import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/03/27 15:45
 * 修改人员：hewking
 * 修改时间：2017/03/27 15:45
 * 修改备注：
 * Version: 1.0.0
 */
public interface GlobalSearch {

    interface ISearchView extends IBaseView {

        void onSearchSuccess(List<AbsContactItem> items);

    }

    interface ISearchPresenter extends IBasePresenter<ISearchView> {

        void search(TextQuery textQuery, int... itemtypes);

    }

    interface ISearchDetailView extends IBaseView {

        void onSearchSuccess(List<AbsContactItem> items);

    }

    interface ISearchDetailPresenter extends IBasePresenter<ISearchDetailView> {
        void search(TextQuery textQuery);

    }

}
