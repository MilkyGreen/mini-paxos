package com.milkygreen.paxos.core;

/**
 * 请求发起者/客户端
 * 发起一次paxos写入
 */
public class Proposer {

    /**
     * paxos实例id
     */
    private PaxosInstanceId id;

    /**
     * 试图写入的值
     */
    private Long val;

    /**
     * 投票号码。
     * 每次写入成功，需要增加编号然后重试
     */
    private BallotNum bal;

    /**
     * 阶段1，写前读。如果有更高的ballot，就要提升ballot，重新再来。
     * 如果没有更高的ballot但是有key的这个ver有值了，需要用这个值来请求第二阶段（帮助其他的proposer修复数据）
     * （应答数量必须是多数派）
     */
    public void Phase1(){

    }

    /**
     *
     * 将自己的val或者上一阶段查到的val，发给s所有server，多数派返回成功就算成功。
     * 如果有服务端返回更高的Ballot，说明别人抢先写入了，自己需要提升ballot，重新从阶段1开始。（这次阶段1很可能会拿到别人写的值，
     * 需要帮他「修复」一下。修复成功后，再提升ballot和ver，执行自己真正的写入）
     *
     */
    public void Phase2(){

    }

    public Proposer() {
    }

    public Proposer(PaxosInstanceId id, Long val, BallotNum bal) {
        this.id = id;
        this.val = val;
        this.bal = bal;
    }


    public PaxosInstanceId getId() {
        return id;
    }

    public void setId(PaxosInstanceId id) {
        this.id = id;
    }

    public Long getVal() {
        return val;
    }

    public void setVal(Long val) {
        this.val = val;
    }

    public BallotNum getBal() {
        return bal;
    }

    public void setBal(BallotNum bal) {
        this.bal = bal;
    }
}
