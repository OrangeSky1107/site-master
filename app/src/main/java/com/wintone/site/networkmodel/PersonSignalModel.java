package com.wintone.site.networkmodel;

/**
 * create by ths on 2020/6/20
 */
public class PersonSignalModel {


    /**
     * code : 1000
     * message : 请求处理成功
     * result : {"constructionId":"da36f319e881b44102dc32ecdfb622be","constructionName":"王家大院","createDate":1592556813000,"cwrIskeypsn":"0","empName":"谭浩世","enterAndRetreatCondition":2,"faceUrl":"https://yztcos-1301448263.cos.ap-guangzhou.myqcloud.com/20200618/960165c798014167ad8f1927c2c0102a.jpg","id":"0670bcf49e0894f69bfdfeccf80d998a","idCode":"431021199411070032","idphotoScan":"https://yztcos-1301448263.cos.ap-guangzhou.myqcloud.com/20200618/fc903fb5e2324144b598f63e35d54d3e.jpg","idphotoScan2":"https://yztcos-1301448263.cos.ap-guangzhou.myqcloud.com/20200618/cf5c949624d5463d8ce3fd0d41773f85.jpg","ifContract":0,"isTeam":1,"isTrain":0,"isUpload":"1","jobName":"监理单位/装修监理工程师","projectId":"47768b8681aa5be8390c73377a16745d","projectName":"澳洲列表","quarantine":"0","startTime":"2020-06-18","teamId":"d3c724225563412cd49e9428afc4a453","teamName":"三支姐妹花"}
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
         * constructionId : da36f319e881b44102dc32ecdfb622be
         * constructionName : 王家大院
         * createDate : 1592556813000
         * cwrIskeypsn : 0
         * empName : 谭浩世
         * enterAndRetreatCondition : 2
         * faceUrl : https://yztcos-1301448263.cos.ap-guangzhou.myqcloud.com/20200618/960165c798014167ad8f1927c2c0102a.jpg
         * id : 0670bcf49e0894f69bfdfeccf80d998a
         * idCode : 431021199411070032
         * idphotoScan : https://yztcos-1301448263.cos.ap-guangzhou.myqcloud.com/20200618/fc903fb5e2324144b598f63e35d54d3e.jpg
         * idphotoScan2 : https://yztcos-1301448263.cos.ap-guangzhou.myqcloud.com/20200618/cf5c949624d5463d8ce3fd0d41773f85.jpg
         * ifContract : 0
         * isTeam : 1
         * isTrain : 0
         * isUpload : 1
         * jobName : 监理单位/装修监理工程师
         * projectId : 47768b8681aa5be8390c73377a16745d
         * projectName : 澳洲列表
         * quarantine : 0
         * startTime : 2020-06-18
         * teamId : d3c724225563412cd49e9428afc4a453
         * teamName : 三支姐妹花
         */

        private String constructionId;
        private String constructionName;
        private long createDate;
        private String cwrIskeypsn;
        private String empName;
        private int enterAndRetreatCondition;
        private String faceUrl;
        private String id;
        private String idCode;
        private String idphotoScan;
        private String idphotoScan2;
        private int ifContract;
        private int isTeam;
        private int isTrain;
        private String isUpload;
        private String jobName;
        private String projectId;
        private String projectName;
        private String quarantine;
        private String startTime;
        private String teamId;
        private String teamName;

        public String getConstructionId() {
            return constructionId;
        }

        public void setConstructionId(String constructionId) {
            this.constructionId = constructionId;
        }

        public String getConstructionName() {
            return constructionName;
        }

        public void setConstructionName(String constructionName) {
            this.constructionName = constructionName;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public String getCwrIskeypsn() {
            return cwrIskeypsn;
        }

        public void setCwrIskeypsn(String cwrIskeypsn) {
            this.cwrIskeypsn = cwrIskeypsn;
        }

        public String getEmpName() {
            return empName;
        }

        public void setEmpName(String empName) {
            this.empName = empName;
        }

        public int getEnterAndRetreatCondition() {
            return enterAndRetreatCondition;
        }

        public void setEnterAndRetreatCondition(int enterAndRetreatCondition) {
            this.enterAndRetreatCondition = enterAndRetreatCondition;
        }

        public String getFaceUrl() {
            return faceUrl;
        }

        public void setFaceUrl(String faceUrl) {
            this.faceUrl = faceUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIdCode() {
            return idCode;
        }

        public void setIdCode(String idCode) {
            this.idCode = idCode;
        }

        public String getIdphotoScan() {
            return idphotoScan;
        }

        public void setIdphotoScan(String idphotoScan) {
            this.idphotoScan = idphotoScan;
        }

        public String getIdphotoScan2() {
            return idphotoScan2;
        }

        public void setIdphotoScan2(String idphotoScan2) {
            this.idphotoScan2 = idphotoScan2;
        }

        public int getIfContract() {
            return ifContract;
        }

        public void setIfContract(int ifContract) {
            this.ifContract = ifContract;
        }

        public int getIsTeam() {
            return isTeam;
        }

        public void setIsTeam(int isTeam) {
            this.isTeam = isTeam;
        }

        public int getIsTrain() {
            return isTrain;
        }

        public void setIsTrain(int isTrain) {
            this.isTrain = isTrain;
        }

        public String getIsUpload() {
            return isUpload;
        }

        public void setIsUpload(String isUpload) {
            this.isUpload = isUpload;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(String jobName) {
            this.jobName = jobName;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getQuarantine() {
            return quarantine;
        }

        public void setQuarantine(String quarantine) {
            this.quarantine = quarantine;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }
    }
}
