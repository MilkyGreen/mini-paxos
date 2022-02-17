package com.milkygreen.paxos.core;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个key的一个版本的值
 * 基本上是个Acceptor，加了一个锁对象
 */
public class Version {

    private Acceptor acceptor;

    public ReentrantLock lock = new ReentrantLock();

    public Acceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(Acceptor acceptor) {
        this.acceptor = acceptor;
    }
}
