package com.milkygreen.paxos.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟一个k-v存储的server服务
 */
public class KVServer {

    /**
     * 记录了每个key的所有版本列表
     * 第一层：key-版本map
     * 第二层：版本-值
     */
    public static Map<String,Map<Long,Version>> map = new HashMap<String, Map<Long, Version>>();


    public void Prepare(){

    }

    public void accept(){

    }



}
