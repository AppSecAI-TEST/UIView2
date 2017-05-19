package com.hn.d.valley.main.message.session;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：hewking
 * 创建时间：2017/05/18 10:29
 * 修改人员：hewking
 * 修改时间：2017/05/18 10:29
 * 修改备注：
 * Version: 1.0.0
 */
public abstract class SessionCustomization implements Serializable{

    private boolean optionBtn = true;

    private boolean showInputPanel = true;

    public abstract List<CommandItemInfo> createItems();

    public boolean isShowInputPanel() {
        return showInputPanel;
    }

    public void setShowInputPanel(boolean showInputPanel) {
        this.showInputPanel = showInputPanel;
    }

    public boolean isOptionBtn() {
        return optionBtn;
    }

    public void setOptionBtn(boolean optionBtn) {
        this.optionBtn = optionBtn;
    }
}
