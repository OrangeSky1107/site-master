package com.wintone.site.networkmodel;

import java.util.List;

/**
 * create by ths on 2020/6/18
 */
public class DictionariesModel {


    /**
     * code : 1000
     * message : 请求处理成功
     * result : [{"category":"WORK_TYPE","createDate":1592216454000,"groupTitle":"工种","hot":2,"id":"101","tag":"BF7A27AED374BE16D707E41A631FBE34","title":"监理单位/装修监理工程师","type":1},{"category":"WORK_TYPE","createDate":1592216454000,"groupTitle":"工种","hot":1,"id":"100","tag":"BE86667205808221FAD7ED510E2A8004","title":"监理单位/项目总监","type":1},{"category":"WORK_TYPE","createDate":1592216454000,"groupTitle":"工种","id":"102","tag":"8E5240C22F6EAB2826E93C624777884E","title":"监理单位/资料员","type":1},{"category":"WORK_TYPE","createDate":1592216454000,"groupTitle":"工种","id":"103","tag":"67FC4D87567E9BE70A2EA1D82A1990CC","title":"监理单位/总监代表","type":1},{"category":"WORK_TYPE","createDate":1592216454000,"groupTitle":"工种","id":"104","tag":"D72BE9338AB8E2B9C294FAF3A51882DD","title":"监理单位/总监理工程师","type":1},{"category":"WORK_TYPE","createDate":1592216529000,"groupTitle":"工种","id":"105","tag":"DB606185B62C45419451DA527DA3373C","title":"监理员","type":1},{"category":"WORK_TYPE","createDate":1592216553000,"groupTitle":"工种","id":"106","tag":"B15FF883099164EA641E3C70DE336C02","title":"建设单位/安全分管领导","type":1},{"category":"WORK_TYPE","createDate":1592216553000,"groupTitle":"工种","id":"107","tag":"EBA01D81B1B8551122D0DB811076C7EC","title":"建设单位/分管领导","type":1},{"category":"WORK_TYPE","createDate":1592216553000,"groupTitle":"工种","id":"108","tag":"2EDBA3227927BC68698AB191D7CCA5E5","title":"建设单位/集团安全分管领导","type":1},{"category":"WORK_TYPE","createDate":1592216553000,"groupTitle":"工种","id":"109","tag":"DE894C48A040992E6A84723A579FE3A2","title":"建设单位/集团安全管理部门领导","type":1}]
     */

    private int code;
    private String message;
    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * category : WORK_TYPE
         * createDate : 1592216454000
         * groupTitle : 工种
         * hot : 2
         * id : 101
         * tag : BF7A27AED374BE16D707E41A631FBE34
         * title : 监理单位/装修监理工程师
         * type : 1
         */

        private String category;
        private long createDate;
        private String groupTitle;
        private int hot;
        private String id;
        private String tag;
        private String title;
        private int type;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public String getGroupTitle() {
            return groupTitle;
        }

        public void setGroupTitle(String groupTitle) {
            this.groupTitle = groupTitle;
        }

        public int getHot() {
            return hot;
        }

        public void setHot(int hot) {
            this.hot = hot;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
