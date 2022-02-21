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


    public Acceptor Prepare(Proposer proposer){
        Version lockedVersion = null;
        try {
            lockedVersion = getLockedVersion(proposer.getId());

            Acceptor acceptor = lockedVersion.getAcceptor();
            if(acceptor == null){
                acceptor = new Acceptor();
                acceptor.setLastBal(proposer.getBal());
            }else{
                if(proposer.getBal().greaterThen(acceptor.getLastBal())){
                    acceptor.setLastBal(proposer.getBal());
                }
            }
            return acceptor;
        } finally {
            lockedVersion.lock.unlock();
        }
    }

    public Acceptor accept(Proposer proposer){
        Version lockedVersion = null;
        try {
            lockedVersion = getLockedVersion(proposer.getId());
            Acceptor acceptor = lockedVersion.getAcceptor();
            if(proposer.getBal().greaterThen(acceptor.getLastBal())){
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
