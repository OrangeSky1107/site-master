package com.wintone.site.networkmodel;

import java.util.List;

/**
 * create by ths on 2020/6/18
 */
public class DictionariesModel {


    /**
     * code : 1000
     * message : 请求处理成功
     * result : {"current":1,"orders":[],"pages":212,"records":[{"category":"WORK_TYPE","createDate":1592216454000,"groupTitle":"工种","hot":1,"id":"100","tag":"BE86667205808221FAD7ED510E2A8004","title":"监理单位/项目总监","type":1}],"searchCount":true,"size":1,"total":212}
     */

    private int code;
    private String message;
    private ResultBean result;

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

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * current : 1
         * orders : []
         * pages : 212
         * records : [{"category":"WORK_TYPE","createDate":1592216454000,"groupTitle":"工种","hot":1,"id":"100","tag":"BE86667205808221FAD7ED510E2A8004","title":"监理单位/项目总监","type":1}]
         * searchCount : true
         * size : 1
         * total : 212
         */

        private int current;
        private int pages;
        private boolean searchCount;
        private int size;
        private int total;
        private List<?> orders;
        private List<RecordsBean> records;

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public boolean isSearchCount() {
            return searchCount;
        }

        public void setSearchCount(boolean searchCount) {
            this.searchCount = searchCount;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<?> getOrders() {
            return orders;
        }

        public void setOrders(List<?> orders) {
            this.orders = orders;
        }

        public List<RecordsBean> getRecords() {
            return records;
        }

        public void setRecords(List<RecordsBean> records) {
            this.records = records;
        }

        public static class RecordsBean {
            /**
             * category : WORK_TYPE
             * createDate : 1592216454000
             * groupTitle : 工种
             * hot : 1
             * id : 100
             * tag : BE86667205808221FAD7ED510E2A8004
             * title : 监理单位/项目总监
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
}
