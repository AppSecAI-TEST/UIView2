package com.hn.d.valley.base.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.angcyo.library.utils.L;
import com.angcyo.uiview.utils.Json;
import com.hn.d.valley.base.constant.Constant;
import com.hn.d.valley.activity.HnSplashActivity;
import com.orhanobut.hawk.Hawk;

import cn.jpush.android.api.JPushInterface;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/11/10 11:31
 * 修改人员：Robi
 * 修改时间：2016/11/10 11:31
 * 修改备注：
 * Version: 1.0.0
 */
public class JPushReceiver extends BroadcastReceiver {

    public static String mRegistrationId = "";

    private static Intent getMainIntent(Context context) {
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        launcher.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        launcher.setComponent(new ComponentName(context, HnSplashActivity.class));
        return launcher;
    }

    private static void handleMessage(Context context, Message message, String body) {
        String type = message.getType();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        L.e("极光 JPushReceiver 收到广播:" + action);
        if (action.equalsIgnoreCase("cn.jpush.android.intent.REGISTRATION")) {
            //极光注册广播
            Bundle bundle = intent.getExtras();
            mRegistrationId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);

            Hawk.put(Constant.JPUSH_ID, mRegistrationId);
        } else if (action.equalsIgnoreCase("cn.jpush.android.intent.NOTIFICATION_OPENED")) {
            context.startActivity(getMainIntent(context));


//            Intent main = new Intent(context, com.hn.zan.activity.HnSplashActivity.class);
//            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            main.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
//            context.startActivity(main);


//            Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage("com.hn.zan");
//            launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            context.startActivity(launchIntentForPackage);
        } else if (action.equalsIgnoreCase("cn.jpush.android.intent.MESSAGE_RECEIVED")) {
            //自定义的消息
            Bundle bundle = intent.getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);//标题
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);//消息
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);//扩展字段
            String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String file = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
            String id = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            String file3 = bundle.getString(JPushInterface.EXTRA_MSG_ID);

            try {
                L.e("极光 JPushReceiver extras:" + extras);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Message msg = Json.from(extras, Message.class);
                handleMessage(context, msg, message);
            } catch (Exception e) {
                e.printStackTrace();
//                Notify.show(context, (int) (Long.parseLong(id) % 10000), title, message, "珍暧妮:您收到一条消息",
//                        R.drawable.zhenaini_icon, getMainIntent(context));
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            String type = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
            String fileHtml = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
            String file = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            String file2 = bundle.getString(JPushInterface.EXTRA_MSG_ID);
        }
    }

    public static class Message {

        /**
         * mData : {"nick":"test"}
         * type : test
         */

        private DataBean data;
        private String type;
        /**
         * mData : {"anthor":{"id":"10458","nick":"18575677360","avatar":"def_user_icon.png","richlvl":"51","anchorlvl":"0","superadmin":"0","logintype":"userLogin","admin":0,"sex":"女","coin":"10000000000.00","livetitle":"test"},"notify":"269530435321594288","live_notice":"不知道为什么,就是想发个公告...."}
         * msg : 你的好友 联通用户 开始直播了 赶快带上小板凳来围观吧
         */

        private String msg;

        public DataBean getData() {
            return data;
        }

        public void setData(DataBean data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public static class DataBean {
            /**
             * anthor : {"id":"10458","nick":"18575677360","avatar":"def_user_icon.png","richlvl":"51","anchorlvl":"0","superadmin":"0","logintype":"userLogin","admin":0,"sex":"女","coin":"10000000000.00","livetitle":"test"}
             * notify : 269530435321594288
             * live_notice : 不知道为什么,就是想发个公告....
             */

            private AnthorBean anthor;
            private String notify;
            private String live_notice;

            /**
             * nick : test
             */

            private String nick;
            private String from;
            private String to;
            private String avatar;
            private String msg_id;
            /**
             * dialog_id : 97
             * time : 2016-11-28 14:47:32
             */

            private String dialog_id;
            private String time;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getMsg_id() {
                return msg_id;
            }

            public void setMsg_id(String msg_id) {
                this.msg_id = msg_id;
            }

            public String getTo() {
                return to;
            }

            public void setTo(String to) {
                this.to = to;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getNick() {
                return nick;
            }

            public void setNick(String nick) {
                this.nick = nick;
            }

            public String getDialog_id() {
                return dialog_id;
            }

            public void setDialog_id(String dialog_id) {
                this.dialog_id = dialog_id;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public AnthorBean getAnthor() {
                return anthor;
            }

            public void setAnthor(AnthorBean anthor) {
                this.anthor = anthor;
            }

            public String getNotify() {
                return notify;
            }

            public void setNotify(String notify) {
                this.notify = notify;
            }

            public String getLive_notice() {
                return live_notice;
            }

            public void setLive_notice(String live_notice) {
                this.live_notice = live_notice;
            }

            public static class AnthorBean {
                /**
                 * id : 10458
                 * nick : 18575677360
                 * avatar : def_user_icon.png
                 * richlvl : 51
                 * anchorlvl : 0
                 * superadmin : 0
                 * logintype : userLogin
                 * admin : 0
                 * sex : 女
                 * coin : 10000000000.00
                 * livetitle : test
                 */

                private String id;
                private String nick;
                private String avatar;
                private String richlvl;
                private String anchorlvl;
                private String superadmin;
                private String logintype;
                private int admin;
                private String sex;
                private String coin;
                private String livetitle;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getNick() {
                    return nick;
                }

                public void setNick(String nick) {
                    this.nick = nick;
                }

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }

                public String getRichlvl() {
                    return richlvl;
                }

                public void setRichlvl(String richlvl) {
                    this.richlvl = richlvl;
                }

                public String getAnchorlvl() {
                    return anchorlvl;
                }

                public void setAnchorlvl(String anchorlvl) {
                    this.anchorlvl = anchorlvl;
                }

                public String getSuperadmin() {
                    return superadmin;
                }

                public void setSuperadmin(String superadmin) {
                    this.superadmin = superadmin;
                }

                public String getLogintype() {
                    return logintype;
                }

                public void setLogintype(String logintype) {
                    this.logintype = logintype;
                }

                public int getAdmin() {
                    return admin;
                }

                public void setAdmin(int admin) {
                    this.admin = admin;
                }

                public String getSex() {
                    return sex;
                }

                public void setSex(String sex) {
                    this.sex = sex;
                }

                public String getCoin() {
                    return coin;
                }

                public void setCoin(String coin) {
                    this.coin = coin;
                }

                public String getLivetitle() {
                    return livetitle;
                }

                public void setLivetitle(String livetitle) {
                    this.livetitle = livetitle;
                }
            }
        }
    }

}
