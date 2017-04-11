package com.hn.d.valley.main.message.chat.viewholder;

import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.hn.d.valley.R;
import com.hn.d.valley.bean.realm.AmapBean;
import com.hn.d.valley.cache.NimUserInfoCache;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.main.message.chat.BaseMultiAdapter;
import com.hn.d.valley.main.message.chat.MsgViewHolderBase;
import com.hn.d.valley.main.other.AmapUIView;
import com.netease.nimlib.sdk.msg.attachment.LocationAttachment;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

/**
 * Created by hewking on 2017/4/9.
 */

public class MsgViewHolderLocation extends MsgViewHolderBase {

    public MsgViewHolderLocation(BaseMultiAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.msg_location_layout;
    }

    @Override
    protected void inflateContentView() {

    }

    @Override
    protected void bindContentView() {

        final LocationAttachment attachment = (LocationAttachment) message.getAttachment();
        TextView location = (TextView) findViewById(R.id.location_address_view);
        location.setText(attachment.getAddress());
        new LatLng(attachment.getLatitude(), attachment.getLongitude());
        final AmapBean amapBean = new AmapBean();
        amapBean.latitude = attachment.getLatitude();
        amapBean.longitude = attachment.getLongitude();
        amapBean.address = attachment.getAddress();
        contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String avatar = "";
                NimUserInfoCache userInfoCache = NimUserInfoCache.getInstance();

                if (isReceivedMessage()) {
                    if (userInfoCache != null) {
                        NimUserInfo userInfo = userInfoCache.getUserInfo(message.getFromAccount());
                        if (userInfo != null) {
                            avatar = userInfo.getAvatar();
                        }
                    }
                } else {
                    avatar = UserCache.instance().getAvatar();
                }
                mUIBaseView.startIView(new AmapUIView(null, amapBean, avatar, false));
            }
        });

    }

}
