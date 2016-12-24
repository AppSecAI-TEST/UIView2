package com.hn.d.valley.start.mvp;

import com.angcyo.uiview.mvp.presenter.IBasePresenter;
import com.angcyo.uiview.mvp.view.IBaseView;
import com.hn.d.valley.base.Bean;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/14 9:08
 * 修改人员：Robi
 * 修改时间：2016/12/14 9:08
 * 修改备注：
 * Version: 1.0.0
 */
public interface Start {

    interface IRegisterView extends IBaseView {

    }

    interface IRegister2View<O, B extends Bean<O>> extends IBaseView {
        void onRegisterSuccess(B bean);
    }

    interface ILoginView<O, B extends Bean<O>> extends IBaseView {
        void onLoginSuccess(B bean);
    }

    interface IRegisterPresenter extends IBasePresenter<IRegisterView> {
        /**
         * 获取手机验证码
         */
        void getVerifyCode(String phone);
    }

    interface IRegister2Presenter extends IBasePresenter<IRegister2View> {
        /**
         * username	是	string	用户名/昵称【2-20位】
         * pwd	是	string	密码【rsa加密】
         * phone	是	string	手机号【11位】
         * avatar	是	string	头像
         * sex	是	int	性别【1-男 2-女】
         * code	是	string	验证码【6位数字】
         */
        void register(String username, String pwd, String phone, String avatar,
                      String sex, String code);
    }

    interface ILoginPresenter extends IBasePresenter<ILoginView> {
        /**
         * phone	否	string	手机号【第三方登录想此字段为空】
         * pwd	是	string	密码【rsa加密】
         * open_id	否	string	第三方唯一id【第三方登录必填】
         * open_type	否	string	第三方类型【1-qq,2-微信】【第三方登录必填】
         * open_nick	否	string	昵称【第三方登录必填】
         * open_avatar	否	string	头像【第三方登录必填】
         * open_sex	否	int	性别【1-男，2-女，0-保密】【第三方登录必填】
         * push_device_id	是	string	jpush对应的registration_id
         * os_version	是	string	操作系统版本号【如ISO10.1.1】
         */
        void login(String phone, String pwd, String open_id, String open_type, String open_nick,
                   String open_avatar, String open_sex);
    }

}
