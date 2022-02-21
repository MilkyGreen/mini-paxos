package com.milkygreen.paxos.core;

import java.util.ArrayList;
import java.util.List;

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
    private Integer val;

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
    /**
     *
     * @param servers 服务端集合
     * @param quorum 多数要求
     * @return
     */
    public Acceptor Phase1(List<KVServer> servers,int quorum){
        List<Acceptor> replies = new ArrayList<>();

        // 这里模拟向服务端发送请求
        for (KVServer server : servers) {
            Acceptor prepare = server.Prepare(this);
            replies.add(prepare);
        }
        int ok = 0;
        BallotNum higherBal = this.bal;
        Acceptor maxVoted = new Acceptor();
        maxVoted.setValBal(this.bal);
        for (Acceptor reply : replies) {

            // 是否有更高的bal
            if(!this.getBal().greaterThen(reply.getLastBal())){
                if(reply.getLastBal().greaterThen(higherBal)){
                    higherBal = reply.getLastBal();
                }
                continue;
            }

            // 记录返回的bal最高的值，如果有的话，Phase2需要拿这个值来请求，而不是自己的。（想要写自己的值需要再从头来一遍完整的paxos过程）
            if(reply.getValBal() != null){
                if(maxVoted.getValBal() == null || reply.getValBal().greaterThen(maxVoted.getValBal())){
                    maxVoted = reply;
                }
            }
            ok++;
            if(ok == quorum){
                // 达到多数派了，返回已经被接受的值（可能为空）
                return maxVoted;
            }
        }
        // 未达到多数派正确返回，说明有人在并发的请求，返回最高的bal,供调用放升级之后重试
        Acceptor acceptor = new Acceptor();
        acceptor.setLastBal(higherBal);
        return acceptor;
    }

    /**
     * 阶段2
     * 将自己的val或者上一阶段查到的val，发给所有server，多数派返回成功就算成功。
     * 如果没有达到多数派，且有服务端返回更高的Ballot，说明别人抢先写入了，自己需要提升ballot，重新从阶段1开始。（这次阶段1很可能会拿到别人写的值，
     * 需要帮他「修复」一下。修复成功后，再提升ballot和ver，执行自己真正的写入）
     *
     */
    /**
     *
     * @param servers 服务端集合
     * @param quorum 多数要求
     * @return
     */
    public BallotNum Phase2(List<KVServer> servers,int quorum){
        List<Acceptor> replies = new ArrayList<>();

        // 这里模拟向所有服务端发送请求
        for (KVServer server : servers) {
            Acceptor prepare = server.accept(this);
            replies.add(prepare);
        }

        int ok = 0;
        BallotNum higherBal = this.bal;
        // 循环检查服务端的返回
        for (Acceptor reply : replies) {
            // 是否有更高的bal
            if(!this.getBal().greaterThen(reply.getLastBal())){
                if(reply.getLastBal().greaterThen(higherBal)){
                    higherBal = reply.getLastBal();
                }
                continue;
            }
            ok++;
            if(ok == quorum){ // 多数返回了成功，代表写入成功了
                return null;
            }
        }
        // 没到达多数派，返回见到的更高的bal，供上层升级
        return higherBal;
    }

    public Proposer() {
    }

    public Proposer(PaxosInstanceId id, Integer val, BallotNum bal) {
        this.id = id;
        this.val = val;
        this.bal = bal;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public PaxosInstanceId getId() {
        return id;
    }

    public void setId(PaxosInstanceId id) {
        this.id = id;
    }

    public BallotNum getBal() {
        return bal;
    }

    public void setBal(BallotNum bal) {
        this.bal = bal;
    }
}
