package com.hn.d.valley.bean;

/**
 * Created by hewking on 2017/3/20.
 */
public class CustomMessageBean {

    /**
     * message_id : 15381
     * type : 5
     * body : {"msg":"我是重中之重重中之重重中之重重中之重","extend_type":"add_contact","tip":"我是重中之重重中之重重中之重重中之重","uid":"50038","created":1489576689,"username":"重中之重重中之重重中之重重中之重","true_name":"这些吧","avatar":"http://circleimg.klgwl.com/8500381484620010.694439","sex":"1","is_auth":"1","auth_type":"1","job":"在线运营","industry":"印刷/包装/造纸","company":"这","signature":"咳咳咳KKK默默图TonyT图www","grade":"1"}
     * extend_type : add_contact
     * created : 1489576689
     * is_attention : 1
     * is_contact : 1
     */

    private String message_id;
    private String type;
    private BodyBean bodyBean;
    private String body;
    private String extend_type;

    public BodyBean getBodyBean() {
        return bodyBean;
    }

    public void setBodyBean(BodyBean bodyBean) {
        this.bodyBean = bodyBean;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    private String created;
    private int is_attention;
    private int is_contact;

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getExtend_type() {
        return extend_type;
    }

    public void setExtend_type(String extend_type) {
        this.extend_type = extend_type;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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



    public LikeUserInfoBean convert() {


        LikeUserInfoBean userInfo = new LikeUserInfoBean();

        userInfo.setAvatar(getBodyBean().getAvatar());
        userInfo.setUid(getBodyBean().getUid());
        userInfo.setUsername(getBodyBean().getUsername());
        userInfo.setAuth_type(getBodyBean().getAuth_type());
        userInfo.setIs_auth(getBodyBean().getIs_auth());
        userInfo.setCreated(created);
        userInfo.setSex(getBodyBean().getSex());
        userInfo.setGrade(getBodyBean().getGrade());
        userInfo.setIs_contact(is_contact);
        userInfo.setIs_attention(is_attention);
        userInfo.setSignature(getBodyBean().getSignature());


        return userInfo;


    }


    public static class BodyBean {
        /**
         * msg : 我是重中之重重中之重重中之重重中之重
         * extend_type : add_contact
         * tip : 我是重中之重重中之重重中之重重中之重
         * uid : 50038
         * created : 1489576689
         * username : 重中之重重中之重重中之重重中之重
         * true_name : 这些吧
         * avatar : http://circleimg.klgwl.com/8500381484620010.694439
         * sex : 1
         * is_auth : 1
         * auth_type : 1
         * job : 在线运营
         * industry : 印刷/包装/造纸
         * company : 这
         * signature : 咳咳咳KKK默默图TonyT图www
         * grade : 1
         */

        private String msg;
        private String extend_type;
        private String tip;
        private String uid;
        private int created;
        private String username;
        private String true_name;
        private String avatar;
        private String sex;
        private String is_auth;
        private String auth_type;
        private String job;
        private String industry;
        private String company;
        private String signature;
        private String grade;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getExtend_type() {
            return extend_type;
        }

        public void setExtend_type(String extend_type) {
            this.extend_type = extend_type;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public int getCreated() {
            return created;
        }

        public void setCreated(int created) {
            this.created = created;
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getIs_auth() {
            return is_auth;
        }

        public void setIs_auth(String is_auth) {
            this.is_auth = is_auth;
        }

        public String getAuth_type() {
            return auth_type;
        }

        public void setAuth_type(String auth_type) {
            this.auth_type = auth_type;
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

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }
    }
}
