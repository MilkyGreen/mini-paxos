package com.milkygreen.paxos.core;

/**
 * 投票号码
 *
 * 包含一个单调递增的数字round，和全局唯一的proposerId（发起者id）
 */
public class BallotNum {

    // 轮次号码，每次递增
    private Long round;

    // 发起客户端的id
    private Long proposerId;

    public BallotNum() {
    }

    public BallotNum(Long round, Long proposerId) {
        this.round = round;
        this.proposerId = proposerId;
    }

    public Long getRound() {
        return round;
    }

    public void setRound(Long round) {
        this.round = round;
    }

    public Long getProposerId() {
        return proposerId;
    }

    public void setProposerId(Long proposerId) {
        this.proposerId = proposerId;
    }
}
