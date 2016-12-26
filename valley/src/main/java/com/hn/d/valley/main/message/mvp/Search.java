package com.hn.d.valley.main.message.mvp;

import com.angcyo.uiview.mvp.presenter.IBasePresenter;
import com.angcyo.uiview.mvp.view.IBaseView;
import com.hn.d.valley.base.Bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/26 9:11
 * 修改人员：Robi
 * 修改时间：2016/12/26 9:11
 * 修改备注：
 * Version: 1.0.0
 */
public interface Search {
    interface ISearchView<O, B extends Bean<O>> extends IBaseView {
        /**
         * 搜索成功
         *
         * @param bean 搜索成功的用户信息
         */
        void onSearchSucceed(B bean);
    }

    interface ISearchPresenter extends IBasePresenter<ISearchView> {
        /**
         * uid	是	int	用户id
         * key	是	string	手机或用户id
         */
        void onSearch(String uid, String key);
    }

}
