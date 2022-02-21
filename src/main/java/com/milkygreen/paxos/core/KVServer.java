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

    /**
     * 查询一个key的一个版本
     * @param id
     * @return
     */
    public synchronized Version getLockedVersion(PaxosInstanceId id){
        String key = id.getKey();
        Long ver = id.getVersion();

        if(!map.containsKey(key)){
            map.put(key,new HashMap<>());
        }
        Map<Long, Version> versions = map.get(key);
        if(!versions.containsKey(ver)){
            versions.put(ver,new Version());
        }

        Version version = versions.get(ver);
        version.lock.lock();
        return version;
    }


    /**
     * 处理写前读方法
     * @param proposer
     * @return
     */
    public Acceptor Prepare(Proposer proposer){
        Version lockedVersion = null;
        try {
            // 获取要处理的key版本
            lockedVersion = getLockedVersion(proposer.getId());

            Acceptor acceptor = lockedVersion.getAcceptor();
            if(acceptor == null){ // 还没被写入过，将LastBal设置为proposer的
                acceptor = new Acceptor();
                acceptor.setLastBal(proposer.getBal());
            }else{
                // 写入过且记录的LastBal小于proposer的，记录proposer的bal。否则什么也不做。
                if(proposer.getBal().greaterThen(acceptor.getLastBal())){
                    acceptor.setLastBal(proposer.getBal());
                }
            }
            return acceptor;
        } finally {
            lockedVersion.lock.unlock();
        }
    }

    /**
     * 写入方法
     * @param proposer
     * @return
     */
    public Acceptor accept(Proposer proposer){
        Version lockedVersion = null;
        try {
            lockedVersion = getLockedVersion(proposer.getId());
            Acceptor acceptor = lockedVersion.getAcceptor();
            // 只接收bal大于等于lockedVersion里面lastBal的写入请求，其他的一概不管
            if(proposer.getBal().greaterThen(acceptor.getLastBal())){
                // 更新这个key版本的相应值
                acceptor.setVal(proposer.getVal());
                acceptor.setLastBal(proposer.getBal());
                acceptor.setValBal(proposer.getBal());
            }
            return acceptor;
        } finally {
            lockedVersion.lock.unlock();
        }
    }





}
