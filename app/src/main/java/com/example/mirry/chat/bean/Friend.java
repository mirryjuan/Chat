package com.example.mirry.chat.bean;

import com.example.mirry.chat.utils.PinyinUtil;

/**
 * Created by Mirry on 2017/3/8.
 */

public class Friend implements Comparable<Friend>{
    private String name;
    private String pinyin;

    public Friend(String name) {
        this.name = name;
        this.pinyin = PinyinUtil.getPinyin(name);
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Friend another) {
        return this.getPinyin().compareTo(another.getPinyin());
    }
}
