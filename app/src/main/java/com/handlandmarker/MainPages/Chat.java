package com.handlandmarker.MainPages;

import android.widget.ImageView;

public class Chat {

        ImageView dp;
        private String name, msg, time;
        private int count;

        public Chat() {

        }
        public Chat(ImageView iv,String name, String msg, String time, int count) {
            this.dp =iv;
            this.name = name;
            this.msg = msg;
            this.time = time;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

    public ImageView getDp() {
        return dp;
    }

    public void setDp(ImageView dp) {
        this.dp = dp;
    }
}
