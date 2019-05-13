package com.ydd.consumer;

import java.util.List;

public class DeviceResponse {

    /**
     * code : 0
     * message : OK
     * data : [{"id":"KitchenClient.2343b23c-4b81-466d-a56e-854196edcf2f","name":"面点","ip":"192.168.1.210","kindIds":["DishesKind.223c5983-d34c-4784-bcec-7d4094ee5779"],"positionType":1,"printerId":2,"statePrinter":true,"allPrint":0,"widthType":2}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : KitchenClient.2343b23c-4b81-466d-a56e-854196edcf2f
         * name : 面点
         * ip : 192.168.1.210
         * kindIds : ["DishesKind.223c5983-d34c-4784-bcec-7d4094ee5779"]
         * positionType : 1
         * printerId : 2
         * statePrinter : true
         * allPrint : 0
         * widthType : 2
         */

        private String id;
        private String name;
        private String ip;
        private int positionType;
        private int printerId;
        private boolean statePrinter;
        private int allPrint;
        private int widthType;
        private List<String> kindIds;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPositionType() {
            return positionType;
        }

        public void setPositionType(int positionType) {
            this.positionType = positionType;
        }

        public int getPrinterId() {
            return printerId;
        }

        public void setPrinterId(int printerId) {
            this.printerId = printerId;
        }

        public boolean isStatePrinter() {
            return statePrinter;
        }

        public void setStatePrinter(boolean statePrinter) {
            this.statePrinter = statePrinter;
        }

        public int getAllPrint() {
            return allPrint;
        }

        public void setAllPrint(int allPrint) {
            this.allPrint = allPrint;
        }

        public int getWidthType() {
            return widthType;
        }

        public void setWidthType(int widthType) {
            this.widthType = widthType;
        }

        public List<String> getKindIds() {
            return kindIds;
        }

        public void setKindIds(List<String> kindIds) {
            this.kindIds = kindIds;
        }
    }
}
