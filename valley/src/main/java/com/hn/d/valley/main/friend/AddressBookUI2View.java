package com.hn.d.valley.main.friend;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.angcyo.github.utilcode.utils.StringUtils;
import com.angcyo.library.glide.GlideCircleTransform;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ContentLayout;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.dialog.UIBottomItemDialog;
import com.angcyo.uiview.dialog.UIDialog;
import com.angcyo.uiview.dialog.UIItemDialog;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.net.RException;
import com.angcyo.uiview.net.RRetrofit;
import com.angcyo.uiview.net.Rx;
import com.angcyo.uiview.net.rsa.RSA;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.rsen.RefreshLayout;
import com.angcyo.uiview.utils.ContactsPickerHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.utils.TimeUtil;
import com.angcyo.uiview.utils.string.StringUtil;
import com.angcyo.uiview.widget.ExEditText;
import com.bumptech.glide.Glide;
import com.github.promeg.pinyinhelper.Pinyin;
import com.hn.d.valley.R;
import com.hn.d.valley.base.BaseUIView;
import com.hn.d.valley.base.Param;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.base.rx.BaseSingleSubscriber;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.bean.PhoneUser;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.me.SkinManagerUIView;
import com.hn.d.valley.main.message.query.ContactSearch;
import com.hn.d.valley.main.message.query.TextQuery;
import com.hn.d.valley.service.ContactService;
import com.hn.d.valley.service.UserService;
import com.hn.d.valley.skin.SkinUtils;
import com.hn.d.valley.widget.HnFollowImageView;
import com.hn.d.valley.widget.HnGlideImageView;
import com.hn.d.valley.widget.HnLoading;
import com.hn.d.valley.widget.HnRefreshLayout;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by hewking on 2017/3/22.
 */
public class AddressBookUI2View extends BaseUIView implements RefreshLayout.OnRefreshListener {

    RecyclerView rv_phoneusers;
    ExEditText et_search;
    HnRefreshLayout refreshLayout;
    private AddressBookAdapter mAddressAdapter;
    private List<AbsContactItem> mContacts;

    public AddressBookUI2View() {

    }

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setShowBackImageView(true)
                .setTitleString(mActivity.getString(R.string.text_add_phone_contacts));
    }

    @Override
    protected void inflateContentLayout(ContentLayout baseContentLayout, LayoutInflater inflater) {
        inflate(R.layout.view_contact_phonematch);
    }

    @Override
    protected void initOnShowContentLayout() {
        super.initOnShowContentLayout();

        rv_phoneusers = v(R.id.recycler_friend);
        et_search = v(R.id.search_input_view);
        refreshLayout = v(R.id.refresh_layout);

        initView();
    }

    private void initView() {

        refreshLayout.setRefreshDirection(RefreshLayout.TOP);
        refreshLayout.addOnRefreshListener(this);

        intiSearchView();
        initRExBaseAdapter();
        initRecyclerView();
    }

    private void intiSearchView() {
        RxTextView.textChanges(et_search)
                .debounce(Constant.DEBOUNCE_TIME_700, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .switchMap(new Func1<CharSequence, Observable<List<AbsContactItem>>>() {
                    @Override
                    public Observable<List<AbsContactItem>> call(CharSequence charSequence) {
                        return Observable.just(charSequence).flatMap(new Func1<CharSequence, Observable<List<AbsContactItem>>>() {
                            @Override
                            public Observable<List<AbsContactItem>> call(CharSequence charSequence) {
//                                        if (TextUtils.isEmpty(charSequence)) {
//                                            return null;
//                                        } else {
                                if (mContacts != null) {
                                    List<AbsContactItem> datas = new ArrayList<>();
                                    for (AbsContactItem mContact : mContacts) {
                                        PhoneContactItem item = (PhoneContactItem) mContact;
                                        boolean hit = ContactSearch.hitContactInfo(item.getContactsInfo(), new TextQuery(et_search.getText().toString()));
                                        if (!hit) {
                                            continue;
                                        }
                                        datas.add(item);
                                    }
                                    return Observable.just(datas);
                                } else {
                                    mContacts = DataResourceRepository.getInstance().provide(ItemTypes.PHONECOTACT, new TextQuery(et_search.getText().toString()));
                                    return Observable.just(mContacts);
                                }
//                                        }
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<List<AbsContactItem>>() {
                    @Override
                    public void onSucceed(List<AbsContactItem> datas) {
                        if (datas == null || datas.size() == 0) {
                            return;
                        }
                        mAddressAdapter.resetData(datas);
                    }
                });
    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                checkPermissionDialog();
                return;
            }
        }
        startLoad();
    }

    private void checkPermissionDialog() {
        UIDialog.build()
                .setDialogContent(getString(R.string.text_requet_contact_permission))
                .setOkText(getString(R.string.ok))
                .setCancelText(getString(R.string.cancel))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 获取权限
                        new RxPermissions(mActivity)
                                .request(Manifest.permission.READ_CONTACTS)
                                .subscribe(new BaseSingleSubscriber<Boolean>() {
                                    @Override
                                    public void onSucceed(Boolean bean) {
                                        super.onSucceed(bean);
                                        if (bean) {
                                            startLoad();
                                        } else {
                                            finishIView();
                                        }
                                    }
                                });
                    }
                })
                .setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finishIView();
                    }
                })
                .showDialog(mParentILayout);
    }

    private void startLoad() {
        Observable.create(new Observable.OnSubscribe<List<AbsContactItem>>() {
            @Override
            public void call(Subscriber<? super List<AbsContactItem>> subscriber) {
                L.i("AddressBookUIView : call " + Thread.currentThread().getName());
                subscriber.onStart();
                mContacts = DataResourceRepository.getInstance().provide(ItemTypes.PHONECOTACT, null);
                subscriber.onNext(mContacts);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSingleSubscriber<List<AbsContactItem>>() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        if (refreshLayout != null) {
                            refreshLayout.setRefreshEnd();
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadView();
                    }

                    @Override
                    public void onSucceed(List<AbsContactItem> bean) {
                        super.onSucceed(bean);
                        L.i("AddressBookUIView : " + Thread.currentThread().getName());
                        showContentLayout();
                        checkPhoneMatch(bean);
//                        mAddressAdapter.resetData(bean);
                    }

                });
    }

    private void checkPhoneMatch(final List<AbsContactItem> absContactItems) {
        if (absContactItems.size() == 0) {
            hideLoadView();
            return;
        }
        String phones = buildJsonParam(absContactItems);
        RRetrofit.create(ContactService.class)
                .phoneUser3(Param.buildMap("uid:" + UserCache.getUserAccount(), "phones:" + phones
                        , "phone_model:" + Build.MODEL, "device_id:" + Build.DEVICE))
                .compose(Rx.transformer(PhoneUserList.class))
                .subscribe(new BaseSingleSubscriber<PhoneUserList>() {
                    @Override
                    public void onSucceed(PhoneUserList bean) {
                        super.onSucceed(bean);
                        if (bean == null || bean.getData_list().size() == 0) {
//                            mAddressAdapter.resetData(absContactItems);
                        } else {
                            List<AbsContactItem> contactItems = new ArrayList<>(absContactItems.size());
                            List<PhoneUser> users = bean.getData_list();
                            for (Iterator<AbsContactItem> it = absContactItems.iterator(); it.hasNext(); ) {
                                PhoneContactItem item = (PhoneContactItem) it.next();
                                boolean flag = false;
                                for (PhoneUser user : users) {
                                    if (item.getContactsInfo().phone.equals(user.getPhone())) {
                                        contactItems.add(new WrapPhoneContactItem(item.getContactsInfo(), user));
                                        flag = true;
                                        break;
                                    }
                                }
                                if (!flag) {
                                    contactItems.add(item);
                                }
                            }
                            mContacts = contactItems;
                            mAddressAdapter.resetData(contactItems);
                        }
                    }

                    @Override
                    public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                        super.onEnd(isError, isNoNetwork, e);
                        hideLoadView();
                        if (isError) {
                            mAddressAdapter.resetData(absContactItems);
                        }
                    }

                });
    }

    private String buildJsonParam(List<AbsContactItem> contactItems) {

        List<String> phones = new ArrayList<>(contactItems.size());
        List<String> names = new ArrayList<>(contactItems.size());

        for (AbsContactItem item : contactItems) {
            PhoneContactItem pItem = (PhoneContactItem) item;
            phones.add(pItem.getContactsInfo().phone);
            names.add(pItem.getContactsInfo().name);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone", StringUtil.removeBlanks(RUtils.connect(phones)));
            jsonObject.put("name", StringUtil.removeBlanks(RUtils.connect(names)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();

    }


    protected RExBaseAdapter initRExBaseAdapter() {
        mAddressAdapter = new AddressBookAdapter(mActivity) {
            @Override
            protected void onPreProvide() {
                mAllDatas.add(new FuncItem<>("搜索", new Action1<ILayout>() {
                    @Override
                    public void call(ILayout o) {
                        mParentILayout.startIView(new SearchUserUIView());
                    }
                }));
            }
        };
        return mAddressAdapter;
    }

    protected void initRecyclerView() {
        rv_phoneusers.setLayoutManager(new LinearLayoutManager(mActivity));
        rv_phoneusers.setAdapter(mAddressAdapter);
        rv_phoneusers.addItemDecoration(new RGroupItemDecoration(new FriendsControl.GroupItemCallBack(mActivity, mAddressAdapter)));
    }

    @Override
    public void onRefresh(@RefreshLayout.Direction int direction) {
        if (RefreshLayout.TOP == direction) {
            startLoad();
        }
    }

    //是否是联系人
    protected boolean isContact(PhoneUser dataBean) {
        return dataBean.getIs_contact() == 1;
    }

    //是否已关注
    protected boolean isAttention(PhoneUser dataBean) {
        return dataBean.getIs_attention() == 1;
    }

    /**
     * @param value true 关注, false 未关注
     */
    protected boolean onSetDataBean(PhoneUser dataBean, boolean value) {
        //请根据界面设置对应关系值
        if (!value) {
            dataBean.setIs_contact(0);
        }
        dataBean.setIs_attention(value ? 1 : 0);
        return true;
    }

    private void buildMsg(PhoneContactItem item) {
        //                            NSString *spm = [NSString stringWithFormat:@"%@_invite_%@",[ToolObject getUid],[ToolObject returnNowDate]];
//                            NSString *shareUrl = [NSString stringWithFormat:@"wap.klgwl.com/user/register?spm=%@",[ToolObject encryptString:spm publicKey:RSAPUBLICKEY]];
//
//                            NSString *message = [NSString stringWithFormat:@"【恐龙谷】%@通过手机通讯录邀请你加入恐龙谷,快点击 %@ 注册吧。",[ToolObject getUserName],shareUrl];

        //未关注
        StringBuilder sb = new StringBuilder();
        sb.append(UserCache.getUserAccount())
                .append("_invite_")
                .append(TimeUtil.getNowDatetime());

        String encodeInfo = RSA.encode(Param.safe(sb)).replaceAll("/", "_a").replaceAll("\\+", "_b").replaceAll("=", "_c");
        sb = new StringBuilder();
        sb.append("【恐龙谷】")
                .append(UserCache.getUserAccount())
                .append("通过手机通讯录邀请你加入恐龙谷,快点击 ")
                .append("wap.klgwl.com/user/register?spm=")
                .append(encodeInfo)
                .append(" 下载吧");

        RUtils.sendSMS(mActivity, sb.toString(), item.getContactsInfo().phone);
    }

    public class AddressBookAdapter extends RExBaseAdapter<String, AbsContactItem, String> {

        public AddressBookAdapter(Context context) {
            super(context);
            setModel(RModelAdapter.MODEL_MULTI);
        }

        protected void onPreProvide() {
        }

        public void resetData(List<AbsContactItem> datas) {
            mAllDatas.clear();
            mAllDatas.addAll(datas);
            FriendsControl.sort(mAllDatas);
            notifyDataSetChanged();
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_firends_phonecontact_item;
        }

        @Override
        protected int getDataItemType(int posInData) {
            return mAllDatas.get(posInData).getItemType();
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, final int posInData, final AbsContactItem dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            TextView username = holder.tv(R.id.username);
            TextView signature = holder.tv(R.id.signature);
            HnGlideImageView image_view = holder.v(R.id.image_view);
            HnFollowImageView follow_image_view = holder.v(R.id.follow_image_view);

            if (holder.getViewType() == ItemTypes.PHONECOTACT) {

                if (dataBean instanceof WrapPhoneContactItem) {
                    //处理关注相关
                    WrapPhoneContactItem wrapContact = (WrapPhoneContactItem) dataBean;
                    final PhoneUser phoneUser = wrapContact.getPhoneUser();

                    username.setText(wrapContact.getContactsInfo().name);
                    signature.setText(String.format("恐龙谷:%s", phoneUser.getUsername()));
                    image_view.setImageUrl(phoneUser.getAvatar());

                    String titleString = "";
                    //未关注
                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSelectorPosition(posInData);
                            add(RRetrofit.create(UserService.class)
                                    .attention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + phoneUser.getUid()))
                                    .compose(Rx.transformer(String.class))
                                    .subscribe(new BaseSingleSubscriber<String>() {
                                        @Override
                                        public void onSucceed(String bean) {
                                            if (!onSetDataBean(phoneUser, true)) {
                                                phoneUser.setIs_attention(1);
                                            }
                                            setSelectorPosition(posInData);
                                        }


                                        @Override
                                        public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                                            super.onEnd(isError, isNoNetwork, e);
                                            if (isError) {
                                                setSelectorPosition(posInData);
                                            }
                                        }
                                    }));
                        }
                    };
                    follow_image_view.setOnClickListener(clickListener);

                    if (isContact(phoneUser) || isAttention(phoneUser)) {
                        final String finalTitleString = titleString;
                        clickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //取消关注
                                UIBottomItemDialog.build()
                                        .setTitleString(finalTitleString)
                                        .addItem(new UIItemDialog.ItemInfo(mContext.getResources().getString(R.string.base_ok), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                setSelectorPosition(posInData);
                                                add(RRetrofit.create(UserService.class)
                                                        .unAttention(Param.buildMap("uid:" + UserCache.getUserAccount(), "to_uid:" + phoneUser.getUid()))
                                                        .compose(Rx.transformer(String.class))
                                                        .subscribe(new BaseSingleSubscriber<String>() {

                                                            @Override
                                                            public void onSucceed(String bean) {
                                                                if (!onSetDataBean(phoneUser, false)) {
                                                                    phoneUser.setIs_attention(0);
                                                                }
                                                                setSelectorPosition(posInData);
                                                            }

                                                            @Override
                                                            public void onEnd(boolean isError, boolean isNoNetwork, RException e) {
                                                                super.onEnd(isError, isNoNetwork, e);
                                                                if (isError) {
                                                                    setSelectorPosition(posInData);
                                                                }
                                                            }
                                                        }));
                                            }
                                        })).showDialog(mILayout);
                            }
                        };
                        follow_image_view.setOnClickListener(clickListener);
                    }

                } else {
                    final PhoneContactItem item = (PhoneContactItem) dataBean;

                    username.setText(item.getContactsInfo().name);
                    signature.setText(String.format("手机号:%s", item.getContactsInfo().phone));

                    follow_image_view.setImageResource(R.drawable.quyaoqing);
                    follow_image_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buildMsg(item);
                        }
                    });

                    Glide.with(mActivity)
                            .load(ContactsPickerHelper.getPhotoByte(mActivity, item.getContactsInfo().contactId))
                            .transform(new GlideCircleTransform(mActivity))
                            .placeholder(R.drawable.default_avatar)
                            .into(holder.imgV(R.id.image_view));
                }

            }

        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, AbsContactItem bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            if (holder.getViewType() == ItemTypes.PHONECOTACT) {
                if (!(bean instanceof WrapPhoneContactItem)) {
                    return;
                }
                WrapPhoneContactItem wrapContact = (WrapPhoneContactItem) bean;
                final PhoneUser phoneUser = wrapContact.getPhoneUser();

                HnFollowImageView followView = holder.v(R.id.follow_image_view);
                followView.setLoadingModel(isSelector);
                followView.setLoadingModel(isSelector);
                if (isSelector) {
                    followView.setOnClickListener(null);
                } else {
                    //关注
                    if (isContact(phoneUser)) {
                        followView.setImageResource(R.drawable.huxiangguanzhu);
                    } else {
                        if (isAttention(phoneUser)) {
                            followView.setImageResource(R.drawable.focus_on);
                        } else {
                            switch (SkinUtils.getSkin()) {
                                case SkinManagerUIView.SKIN_BLUE:
                                    followView.setImageResource(R.drawable.follow_blue);
                                    break;
                                case SkinManagerUIView.SKIN_GREEN:
                                    followView.setImageResource(R.drawable.follow);
                                    break;
                                default:
                                    followView.setImageResource(R.drawable.follow_black);
                                    break;
                            }
                        }
                    }
                }
            }
        }

        @NonNull
        @Override
        protected RBaseViewHolder createBaseViewHolder(int viewType, View item) {
            return super.createBaseViewHolder(viewType, item);
        }
    }

    public class AddressBookViewHolder extends RBaseViewHolder {


        public AddressBookViewHolder(View itemView, int viewType) {
            super(itemView, viewType);

        }

        public void bind(View itemView) {

        }
    }

    public class PhoneUserList extends ListModel<PhoneUser> {
    }

    public class WrapPhoneContactItem extends PhoneContactItem {

        PhoneUser phoneUser;

        public WrapPhoneContactItem(ContactsPickerHelper.ContactsInfo info, PhoneUser phoneUser) {
            super(info);
            char ch = phoneUser.getUsername().charAt(0);
            String letter = StringUtils.getAsciiLeadingUp(ch);
            if (letter != null) {
                groupText = letter;
            } else if (Pinyin.isChinese(ch)) {
                groupText = String.valueOf(Pinyin.toPinyin(ch).toUpperCase().charAt(0));
            } else {
                groupText = "#";
            }
            this.phoneUser = phoneUser;
        }

        public PhoneUser getPhoneUser() {
            return phoneUser;
        }
    }


}
