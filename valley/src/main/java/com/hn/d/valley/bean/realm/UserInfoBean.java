package com.hn.d.valley.bean.realm;

import android.content.res.Resources;
import android.text.TextUtils;

import com.angcyo.uiview.github.pickerview.DateDialog;
import com.angcyo.uiview.utils.file.FileUtil;
import com.hn.d.valley.R;
import com.hn.d.valley.cache.UserCache;
import com.hn.d.valley.widget.HnIcoRecyclerView;
import com.orhanobut.hawk.Hawk;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

import static com.angcyo.uiview.github.pickerview.DateDialog.Date_FORMAT;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/01/06 8:35
 * 修改人员：Robi
 * 修改时间：2017/01/06 8:35
 * 修改备注：
 * Version: 1.0.0
 */
public class UserInfoBean extends RealmObject {

    public static final String KEY_NEW_VISITOR = "key_new_visitor";
    public static final String KEY_NEW_NOTIFICATION = "key_new_notification";
    public int is_login_protect;
    public String test;

    boolean new_visitor;//是否有新的访客
    boolean new_notification;//是否有新的动态通知


    /**
     * is_attention : 1
     * is_contact : 0
     * contact_remark :
     * is_star : 0
     * is_blacklist : 0
     * fans_count : 1
     * attention_count : 0
     * discuss_count : 0
     * collect_count : 0
     * uid : 50001
     * status : 1
     * username : 幽灵
     * true_name :
     * phone : 18770090776
     * avatar : http://static.bzsns.cn/pic/M00/00/2B/CixiMlbVVumAIJEGAAAetFQEzXc84.JPEG
     * photos :
     * coins : 0
     * grade : 1
     * sex : 0
     * company :
     * province_id : 0
     * city_id : 0
     * county_id : 0
     * province_name :
     * city_name :
     * county_name :
     * is_auth : 0
     * auth_type : 0
     * auth_desc :
     * introduce :
     * job :
     * industry :
     * signature :
     * is_set_password : 1
     * is_login_protect : 1
     */

    private int is_attention;
    private int is_contact;
    private String contact_remark;
    private int is_star;
    private int is_blacklist;
    private int fans_count;
    private int attention_count;
    private int discuss_count;
    private int collect_count;
    private String uid;
    private String status;
    private String username;
    private String true_name;
    private String phone;
    private String avatar;
    private String photos;
    private String coins;
    private String grade;
    private String sex;
    private String company;
    private String province_id;
    private String city_id;
    private String county_id;
    private String province_name;
    private String city_name;
    private String county_name;
    private String is_auth;
    private String auth_type;
    private String auth_desc;
    private String introduce;
    private String job;
    private String industry;
    private String signature;
    private int is_set_password;
    private RealmList<NewestDiscussPicBean> newest_discuss_pic;
    /**
     * look_fans : 1
     * look_my_discuss : 1
     * look_his_discuss : 1
     * created : 1480930827
     * voice_introduce :
     */

    private int look_fans;
    private int look_my_discuss;
    private int look_his_discuss;
    private String created;
    private String voice_introduce;
    /**
     * birthday : 2017-02-21
     */

    private String birthday;
    /**
     * discuss_pic_count : 27
     */

    private int discuss_pic_count;
    /**
     * website : 个人网址
     * cover : http://klg-circleimg.oss-cn-shenzhen.aliyuncs.com/50017/141489630474_s_828.0x620.0.jpg
     */

    private String website;
    private String cover;
    /**
     * newest_discuss_pic : []
     * relation : {"count":2,"list":[{"avatar":"http://circleimg.klgwl.com/77500371484917281.776834","uid":"50037"},{"avatar":"http://avatorimg.klgwl.com/15019298316_1485330266.033782","uid":"60021"}]}
     */

    private RelationBean relation;


    /**
     * 是否有新的访客
     */
    public boolean isNew_visitor() {
        return new_visitor || Hawk.get(KEY_NEW_VISITOR, false);
    }

    public void setNew_visitor(boolean new_visitor) {
        this.new_visitor = new_visitor;
        Hawk.put(KEY_NEW_VISITOR, new_visitor);
    }

    public boolean isNew_notification() {
        return new_notification || Hawk.get(KEY_NEW_NOTIFICATION, false);
    }

    public void setNew_notification(boolean new_notification) {
        this.new_notification = new_notification;
        Hawk.put(KEY_NEW_NOTIFICATION, new_notification);
    }

    public int getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(int is_attention) {
        this.is_attention = is_attention;
    }

    public int getIs_contact() {
        return is_contact;
    }

    public void setIs_contact(int is_contact) {
        this.is_contact = is_contact;
    }

    public String getContact_remark() {
        return contact_remark;
    }

    public void setContact_remark(String contact_remark) {
        this.contact_remark = contact_remark;
    }

    public int getIs_star() {
        return is_star;
    }

    public void setIs_star(int is_star) {
        this.is_star = is_star;
    }

    public int getIs_blacklist() {
        return is_blacklist;
    }

    public void setIs_blacklist(int is_blacklist) {
        this.is_blacklist = is_blacklist;
    }

    public int getFans_count() {
        return fans_count;
    }

    public void setFans_count(int fans_count) {
        this.fans_count = fans_count;
    }

    public int getAttention_count() {
        return attention_count;
    }

    public void setAttention_count(int attention_count) {
        this.attention_count = attention_count;
    }

    public int getDiscuss_count() {
        return discuss_count;
    }

    public void setDiscuss_count(int discuss_count) {
        this.discuss_count = discuss_count;
    }

    public int getCollect_count() {
        return collect_count;
    }

    public void setCollect_count(int collect_count) {
        this.collect_count = collect_count;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTrue_name() {
        return true_name;
    }

    public void setTrue_name(String true_name) {
        this.true_name = true_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProvince_id() {
        return province_id;
    }

    public void setProvince_id(String province_id) {
        this.province_id = province_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCounty_id() {
        return county_id;
    }

    public void setCounty_id(String county_id) {
        this.county_id = county_id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCounty_name() {
        return county_name;
    }

    public void setCounty_name(String county_name) {
        this.county_name = county_name;
    }

    public String getIs_auth() {
        return is_auth;
    }

    public void setIs_auth(String is_auth) {
        this.is_auth = is_auth;
    }

    public boolean isAuth() {
        return "1".equalsIgnoreCase(getIs_auth());
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getAuth_desc() {
        return auth_desc;
    }

    public void setAuth_desc(String auth_desc) {
        this.auth_desc = auth_desc;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getIs_set_password() {
        return is_set_password;
    }

    public void setIs_set_password(int is_set_password) {
        this.is_set_password = is_set_password;
    }

    public int getIs_login_protect() {
        return is_login_protect;
    }

    public void setIs_login_protect(int is_login_protect) {
        this.is_login_protect = is_login_protect;
    }

    public RealmList<NewestDiscussPicBean> getNewest_discuss_pic() {
        return newest_discuss_pic;
    }

    public void setNewest_discuss_pic(RealmList<NewestDiscussPicBean> newest_discuss_pic) {
        this.newest_discuss_pic = newest_discuss_pic;
    }

    public int getLook_fans() {
        return look_fans;
    }

    public void setLook_fans(int look_fans) {
        this.look_fans = look_fans;
    }

    public int getLook_my_discuss() {
        return look_my_discuss;
    }

    public void setLook_my_discuss(int look_my_discuss) {
        this.look_my_discuss = look_my_discuss;
    }

    public int getLook_his_discuss() {
        return look_his_discuss;
    }

    public void setLook_his_discuss(int look_his_discuss) {
        this.look_his_discuss = look_his_discuss;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedTime() {
        String parse = null;
        try {
            parse = Date_FORMAT.format(new Date(Long.valueOf(created) * 1000l));
            return parse;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return created;
        }
    }

    public String getVoice_introduce() {
        return voice_introduce;
    }

    public void setVoice_introduce(String voice_introduce) {
        this.voice_introduce = voice_introduce;
    }

    public String getVoiceTime() {
        if (TextUtils.isEmpty(voice_introduce)) {
            return "0";
        } else {
            String[] voiceIntroduces;
            voiceIntroduces = voice_introduce.split("--");
            if (voiceIntroduces.length == 2) {
                return voiceIntroduces[1];
            } else {
                voiceIntroduces = voice_introduce.split("_t_");
                String introd1 = voiceIntroduces[1];
                String duration = FileUtil.getFileNameNoEx(introd1);

                if (voiceIntroduces.length == 2) {
                    return duration;
                }
            }
        }
        return "0";
    }

    public long getVoiceDuration() {
        return Long.parseLong(getVoiceTime().replaceAll("s", "").replaceAll("S", "").trim());
    }

    public String getVoiceUrl() {
        if (TextUtils.isEmpty(voice_introduce)) {
            return "";
        } else {
            String[] voiceIntroduces;
            voiceIntroduces = voice_introduce.split("--");
            if (voiceIntroduces.length == 2) {
                return voiceIntroduces[0];
            } else {
                return voice_introduce;
            }
        }
    }


    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday(Resources resources) {
        if (TextUtils.isEmpty(birthday)) {
            return resources.getString(R.string.secret);
        }
        return resources.getString(R.string.birthday_format, DateDialog.getBirthday(birthday));
    }

    public String getAddress() {
        return getProvince_name() + " " + getCity_name();
    }

    public int getDiscuss_pic_count() {
        return discuss_pic_count;
    }

    public void setDiscuss_pic_count(int discuss_pic_count) {
        this.discuss_pic_count = discuss_pic_count;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public boolean isMe() {
        return TextUtils.equals(uid, UserCache.getUserAccount());
    }

    public RelationBean getRelation() {
        return relation;
    }

    public void setRelation(RelationBean relation) {
        this.relation = relation;
    }

    public static class RelationBean {
        /**
         * count : 2
         * list : [{"avatar":"http://circleimg.klgwl.com/77500371484917281.776834","uid":"50037"},{"avatar":"http://avatorimg.klgwl.com/15019298316_1485330266.033782","uid":"60021"}]
         */

        private int count;
        private List<HnIcoRecyclerView.IcoInfo> list;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<HnIcoRecyclerView.IcoInfo> getList() {
            return list;
        }

        public void setList(List<HnIcoRecyclerView.IcoInfo> list) {
            this.list = list;
        }
    }
}
