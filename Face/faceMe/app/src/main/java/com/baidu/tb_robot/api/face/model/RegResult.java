/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.tb_robot.api.face.model;

import java.util.List;

@SuppressWarnings("unused")
public class RegResult {

    /**
     * result_num : 1
     * result : [{"location":{"left":44,"top":85,"width":195,"height":191},"face_probability":0.86922705173492,"rotation_angle":3,"yaw":-21.878089904785,"pitch":-1.8928043842316,"roll":-5.4075946807861,"age":33.811832427979,"beauty":19.49462890625,"expression":0,"expression_probablity":0.99472212791443,"faceshape":[{"type":"square","probability":0.073435798287392},{"type":"triangle","probability":4.1542074177414E-4},{"type":"oval","probability":0.56098282337189},{"type":"heart","probability":0.037038408219814},{"type":"round","probability":0.32812759280205}],"glasses":1,"glasses_probability":0.6368208527565}]
     * log_id : 2387488391122617
     */

    private int result_num;
    private long log_id;
    /**
     * location : {"left":44,"top":85,"width":195,"height":191}
     * face_probability : 0.86922705173492
     * rotation_angle : 3
     * yaw : -21.878089904785
     * pitch : -1.8928043842316
     * roll : -5.4075946807861
     * age : 33.811832427979
     * beauty : 19.49462890625
     * expression : 0
     * expression_probablity : 0.99472212791443
     * faceshape : [{"type":"square","probability":0.073435798287392},{"type":"triangle","probability":4.1542074177414E-4},{"type":"oval","probability":0.56098282337189},{"type":"heart","probability":0.037038408219814},{"type":"round","probability":0.32812759280205}]
     * glasses : 1
     * glasses_probability : 0.6368208527565
     */

    private List<ResultBean> result;

    public int getResult_num() {
        return result_num;
    }

    public void setResult_num(int result_num) {
        this.result_num = result_num;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * left : 44
         * top : 85
         * width : 195
         * height : 191
         */

        private LocationBean location;
        private double face_probability;
        private int rotation_angle;
        private double yaw;
        private double pitch;
        private double roll;
        private double age;
        private double beauty;
        private int expression;
        private double expression_probablity;
        private int glasses;
        private String gender;
        private double glasses_probability;

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        /**
         * type : square
         * probability : 0.073435798287392
         */

        private List<FaceshapeBean> faceshape;

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public double getFace_probability() {
            return face_probability;
        }

        public void setFace_probability(double face_probability) {
            this.face_probability = face_probability;
        }

        public int getRotation_angle() {
            return rotation_angle;
        }

        public void setRotation_angle(int rotation_angle) {
            this.rotation_angle = rotation_angle;
        }

        public double getYaw() {
            return yaw;
        }

        public void setYaw(double yaw) {
            this.yaw = yaw;
        }

        public double getPitch() {
            return pitch;
        }

        public void setPitch(double pitch) {
            this.pitch = pitch;
        }

        public double getRoll() {
            return roll;
        }

        public void setRoll(double roll) {
            this.roll = roll;
        }

        public double getAge() {
            return age;
        }

        public void setAge(double age) {
            this.age = age;
        }

        public double getBeauty() {
            return beauty;
        }

        public void setBeauty(double beauty) {
            this.beauty = beauty;
        }

        public int getExpression() {
            return expression;
        }

        public void setExpression(int expression) {
            this.expression = expression;
        }

        public double getExpression_probablity() {
            return expression_probablity;
        }

        public void setExpression_probablity(double expression_probablity) {
            this.expression_probablity = expression_probablity;
        }

        public int getGlasses() {
            return glasses;
        }

        public void setGlasses(int glasses) {
            this.glasses = glasses;
        }

        public double getGlasses_probability() {
            return glasses_probability;
        }

        public void setGlasses_probability(double glasses_probability) {
            this.glasses_probability = glasses_probability;
        }

        public List<FaceshapeBean> getFaceshape() {
            return faceshape;
        }

        public void setFaceshape(List<FaceshapeBean> faceshape) {
            this.faceshape = faceshape;
        }

        public static class LocationBean {
            private int left;
            private int top;
            private int width;
            private int height;

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }

        public static class FaceshapeBean {
            private String type;
            private double probability;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public double getProbability() {
                return probability;
            }

            public void setProbability(double probability) {
                this.probability = probability;
            }
        }
    }
}

