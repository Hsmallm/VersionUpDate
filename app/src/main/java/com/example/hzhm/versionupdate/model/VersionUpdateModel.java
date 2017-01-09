package com.example.hzhm.versionupdate.model;

/**
 * Created by hzhm on 2017/1/9.
 */

public class VersionUpdateModel {

    /**
     * result : {"allowLowestVersion":"1.0.0","createdon":1483086431000,"currentVersion":"4.2.2","description":"<p>sda&nbsp;<\/p>","enabled":"0","forceUpdate":"1","modifydon":1483415683000,"platformId":"695E61DCBE8A4F2BA256D3B76AD5D392","version":"4.2.2"}
     */

    public ResultBean result;

    public static class ResultBean {
        /**
         * allowLowestVersion : 1.0.0
         * createdon : 1483086431000
         * currentVersion : 4.2.2
         * description : <p>sda&nbsp;</p>
         * enabled : 0
         * forceUpdate : 1
         * modifydon : 1483415683000
         * platformId : 695E61DCBE8A4F2BA256D3B76AD5D392
         * version : 4.2.2
         */

        public String allowLowestVersion;//最低版本号
        public long createdon;
        public String currentVersion;//当前最新版本号（需要下载的版本号）
        public String description;
        public String enabled;
        public String forceUpdate;//是否强制更新（0-强制更新，1-一般更新，2-静默更新）
        public long modifydon;
        public String platformId;
        public String version;
    }
}
