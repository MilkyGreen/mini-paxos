package com.milkygreen.paxos.core;

/**
 * 一次被接受的赋值。记录一个key+版本 当前的值。
 */
public class Acceptor {

    /**
     * 最近一次写前读的ballot号码。
     * 任何一个proposer想写入一个值，必须在写前进行一次「写前读」的操作，Acceptor会记录下这次读操作的BallotNum，
     * 后面只接受最新BallotNum 的写入。
     */
    private BallotNum lastBal;

    /**
     * 当前的值
     */
    private Integer val;

    /**
     * 当前值被写入时的BallotNum
     */
    private BallotNum valBal;

    public Acceptor() {
    }

    public Acceptor(BallotNum lastBal, Integer val, BallotNum valBal) {
        this.lastBal = lastBal;
        this.val = val;
        this.valBal = valBal;
    }

    public BallotNum getLastBal() {
        return lastBal;
    }

    public void setLastBal(BallotNum lastBal) {
        this.lastBal = lastBal;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public BallotNum getValBal() {
        return valBal;
    }

    public void setValBal(BallotNum valBal) {
        this.valBal = valBal;
    }
}
