package com.milkygreen.paxos.core;

/**
 *  一次paxos的唯一标识
 *  在paxos中，一个key的一个版本只能被写入一次，因此key+version可以确定唯一的一个paxos
 */
public class PaxosInstanceId {

    private String key;

    private Long version;

    public PaxosInstanceId() {
    }

    public PaxosInstanceId(String key, Long version) {
        this.key = key;
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
