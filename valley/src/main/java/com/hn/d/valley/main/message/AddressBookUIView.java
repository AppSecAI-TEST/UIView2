package com.hn.d.valley.main.message;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.angcyo.library.glide.GlideCircleTransform;
import com.angcyo.library.utils.L;
import com.angcyo.uiview.container.ILayout;
import com.angcyo.uiview.model.TitleBarPattern;
import com.angcyo.uiview.recycler.RBaseViewHolder;
import com.angcyo.uiview.recycler.RGroupItemDecoration;
import com.angcyo.uiview.recycler.adapter.RExBaseAdapter;
import com.angcyo.uiview.recycler.adapter.RModelAdapter;
import com.angcyo.uiview.utils.ContactsPickerHelper;
import com.angcyo.uiview.utils.RUtils;
import com.angcyo.uiview.widget.ExEditText;
import com.bumptech.glide.Glide;
import com.hn.d.valley.R;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.bean.ListModel;
import com.hn.d.valley.bean.PhoneUser;
import com.hn.d.valley.control.FriendsControl;
import com.hn.d.valley.main.friend.AbsContactItem;
import com.hn.d.valley.main.friend.DataResourceRepository;
import com.hn.d.valley.main.friend.FuncItem;
import com.hn.d.valley.main.friend.ItemTypes;
import com.hn.d.valley.main.friend.PhoneContactItem;
import com.hn.d.valley.main.message.query.TextQuery;
import com.hn.d.valley.sub.other.SingleRSubscriber;
import com.hn.d.valley.sub.other.SingleRecyclerUIView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
@SuppressWarnings("ALL")
public class AddressBookUIView extends SingleRecyclerUIView<AbsContactItem> {

    private AddressBookAdapter mAddressAdapter;

    private boolean mFirstLoad;

    @Override
    protected TitleBarPattern getTitleBar() {
        return super.getTitleBar().setTitleString("添加手机联系人");
    }

    public AddressBookUIView() {

    }

    @Override
    protected void onUILoadData(String page) {
        super.onUILoadData(page);
        startLoad();

    }

    @Override
    public void onViewShowFirst(Bundle bundle) {
        super.onViewShowFirst(bundle);
    }

    private void startLoad() {

//        RRetrofit.create(ContactService.class)
//                .phoneUser(Param.buildMap("uid:" + uid, "page:" + page))
//                .compose(Rx.transformer(PhoneUserList.class))
//                .subscribe(new SingleRSubscriber<PhoneUserList>(this) {
//                    @Override
//                    protected void onResult(PhoneUserList bean) {
//                        if (bean == null || bean.getData_list().size() == 0) {
//                            onUILoadDataEnd();
//                        } else {
//                            for (PhoneUser b : bean.getData_list()) {
////                                b.setIs_attention(1);
//                            }
////                            onUILoadDataEnd(bean);
//                        }
//                    }
//                });

//        Observable.zip(RRetrofit.create(ContactService.class)
//                .phoneUser(Param.buildMap("uid:" + uid, "page:" + page))
//                .compose(Rx.transformer(PhoneUserList.class)), Observable.create(new Observable.OnSubscribe<List<AbsContactItem>>() {
//
//            @Override
//            public void call(Subscriber<? super List<AbsContactItem>> subscriber) {
//                subscriber.onStart();
//                List<AbsContactItem> contactItems = DataResourceRepository.getInstance().provide(ItemTypes.PHONECOTACT);
//                subscriber.onNext(contactItems);
//                subscriber.onCompleted();
//            }
//        }), new Func2<PhoneUserList, List<AbsContactItem>, List<WrapPhoneContactItem>> () {
//            @Override
//            public List<WrapPhoneContactItem> call(PhoneUserList phoneUserList, List<AbsContactItem> absContactItems) {
//                List<WrapPhoneContactItem> wrapPhoneContactItems = new ArrayList<>();
//
//                return null;
//            }
//        });


//        Observable.create(new Observable.OnSubscribe<List<AbsContactItem>>() {
//
//            @Override
//            public void call(Subscriber<? super List<AbsContactItem>> subscriber) {
//                subscriber.onStart();
//                List<AbsContactItem> contactItems = DataResourceRepository.getInstance().provide(ItemTypes.PHONECOTACT,null);
//                subscriber.onNext(contactItems);
//                subscriber.onCompleted();
//            }
//        })
//                .flatMap(new Func1<List<AbsContactItem>, Observable<PhoneUserList>>() {
//                    @Override
//                    public Observable<PhoneUserList> call(List<AbsContactItem> absContactItems) {
//
//                        String phones = buildJsonParam(absContactItems);
//
//                        return RRetrofit.create(ContactService.class)
//                                .phoneUser(Param.buildMap("uid:" + UserCache.getUserAccount(), "phones:" + phones,"phone_model:" + Build.MODEL,"device_id:" + Build.DEVICE))
//                                .compose(Rx.transformer(PhoneUserList.class));
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleRSubscriber<PhoneUserList>(this) {
//
//                    @Override
//                    public void onError(int code, String msg) {
//                        super.onError(code, msg);
//                    }
//
//                    @Override
//                    protected void onResult(PhoneUserList bean) {
//                        L.i(bean.getData_list());
//                    }
//                });


        Observable.create(new Observable.OnSubscribe<List<AbsContactItem>>() {

            @Override
            public void call(Subscriber<? super List<AbsContactItem>> subscriber) {
                L.i("AddressBookUIView : call " + Thread.currentThread().getName());
                subscriber.onStart();
                List<AbsContactItem> contactItems = DataResourceRepository.getInstance().provide(ItemTypes.PHONECOTACT,null);
                subscriber.onNext(contactItems);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleRSubscriber<List<AbsContactItem>>(this) {
                    @Override
                    protected void onResult(List<AbsContactItem> bean) {
                        L.i("AddressBookUIView : " + Thread.currentThread().getName());
//                        showContentLayout();
                        mAddressAdapter.resetData(bean);
//                        onUILoadDataFinish();
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
            jsonObject.put("phone",RUtils.connect(phones));
            jsonObject.put("name",RUtils.connect(names));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();

    }


    @Override
    protected RExBaseAdapter initRExBaseAdapter() {
        mAddressAdapter = new AddressBookAdapter(mActivity){
            @Override
            protected void onPreProvide() {
                mAllDatas.add(new FuncItem<>("搜索",new Action1<ILayout>() {
                    @Override
                    public void call(ILayout o) {
                        mParentILayout.startIView(new SearchUserUIView());
                    }
                }));
            }
        };
        return mAddressAdapter;
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        getRecyclerView().addItemDecoration(new RGroupItemDecoration(new FriendsControl.GroupItemCallBack(mActivity,mAddressAdapter)));
    }

    public class AddressBookAdapter extends RExBaseAdapter<String,AbsContactItem,String> {

        public AddressBookAdapter(Context context) {
            super(context);
            setModel(RModelAdapter.MODEL_MULTI);
        }

        protected void onPreProvide(){
        }

        public void resetData(List<AbsContactItem> datas) {

            mAllDatas.clear();
//            mAllDatas.removeIf(new Predicate<AbsContactItem>() {
//                @Override
//                public boolean test(AbsContactItem absContactItem) {
//                    return absContactItem instanceof PhoneContactItem;
//                }
//            });
            onPreProvide();
            mAllDatas.addAll(datas);
            FriendsControl.sort(mAllDatas);
//            notifyItemRangeChanged(1,datas.size());
            notifyDataSetChanged();
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            if (viewType == ItemTypes.FUNC) {
                return R.layout.item_contact_search;
            } else if (viewType == ItemTypes.PHONECOTACT) {
                return R.layout.item_firends_phonecontact_item;
            }
            return R.layout.item_firends_phonecontact_item;
        }

        @Override
        protected int getDataItemType(int posInData) {
            return mAllDatas.get(posInData).getItemType();
        }

        @Override
        protected void onBindDataView(RBaseViewHolder holder, int posInData, final AbsContactItem dataBean) {
            super.onBindDataView(holder, posInData, dataBean);

            if (holder.getViewType() == ItemTypes.FUNC) {

                if (!mFirstLoad) {
                    mFirstLoad = true;
                } else {
                    return;
                }

                final ExEditText mSearchInputView = holder.v(R.id.search_input_view);
                RxTextView.textChanges(mSearchInputView)
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
                                            List<AbsContactItem> items = DataResourceRepository.getInstance().provide(ItemTypes.PHONECOTACT, new TextQuery(mSearchInputView.getText().toString()));
                                            return  Observable.just(items);
//                                        }
                                    }
                                });
                            }
                        })
//                        .map(new Func1<CharSequence, List<AbsContactItem>>() {
//                            @Override
//                            public List<AbsContactItem> call(CharSequence charSequence) {
//                                if (TextUtils.isEmpty(charSequence)) {
//                                    return null;
//                                } else {
//                                    List<AbsContactItem> items = DataResourceRepository.getInstance().provide(ItemTypes.PHONECOTACT, new TextQuery(mSearchInputView.getText().toString()));
//                                    return  items;
//                                }
//                            }
//                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SingleRSubscriber<List<AbsContactItem>>(AddressBookUIView.this) {
                            @Override
                            protected void onResult(List<AbsContactItem> datas) {
                                if (datas == null || datas.size() == 0) {
                                    return;
                                }
                                resetData(datas);
                            }
                        });
//                        .subscribe(new Action1<List<AbsContactItem>>() {
//                            @Override
//                            public void call(List<AbsContactItem> datas) {
//                                if (datas == null || datas.size() == 0) {
//                                    return;
//                                }
//                                resetData(datas);
//                            }
//                        });
            }

            if (holder.getViewType() == ItemTypes.PHONECOTACT) {
                PhoneContactItem item = (PhoneContactItem) dataBean;

                TextView username = holder.tv(R.id.username);
                TextView signature = holder.tv(R.id.signature);
                username.setText(item.getContactsInfo().name);
                signature.setText("手机号:" + item.getContactsInfo().phone);

                Glide.with(mActivity)
                        .load(ContactsPickerHelper.getPhotoByte(mActivity, item.getContactsInfo().contactId))
                        .transform(new GlideCircleTransform(mActivity))
                        .placeholder(R.drawable.default_avatar)
                        .into(holder.imgV(R.id.image_view));
            }

        }

        @Override
        protected void onBindModelView(int model, boolean isSelector, RBaseViewHolder holder, int position, AbsContactItem bean) {
            super.onBindModelView(model, isSelector, holder, position, bean);
            if (holder.getViewType() == ItemTypes.PHONECOTACT) {

            }
        }

        @NonNull
        @Override
        protected RBaseViewHolder createBaseViewHolder(int viewType, View item) {
            return super.createBaseViewHolder(viewType, item);
        }
    }

    public class AddressBookViewHolder extends RBaseViewHolder{


        public AddressBookViewHolder(View itemView, int viewType) {
            super(itemView, viewType);

        }

        public void bind(View itemView){

        }
    }

    public class PhoneUserList extends ListModel<PhoneUser>{}

    public class WrapPhoneContactItem extends PhoneContactItem {

        PhoneUser phoneUser;

        public WrapPhoneContactItem(ContactsPickerHelper.ContactsInfo info,PhoneUser phoneUser) {
            super(info);
            this.phoneUser = phoneUser;
        }

        public PhoneUser getPhoneUser() {
            return phoneUser;
        }
    }



}
