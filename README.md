# mini-paxos
一个最小版的 paxos 协议 Java 实现，帮助掌握 classic-paxos 原理与细节。

classic-paxos有下面几个核心要点：
1、多数派读写。
server端没有master，互相之间以相同的角色对外提供服务。
客户端的读写操作至少要获得超过半数服务端的正确返回才算成功，读取时必须取所有返回中版本最高的值。

2、一个版本一个值。
以简单的key-value存储为例，在paxos体系中，每一个key都有自己的版本，一个key的一个版本只能被写入一次。
客户端试图对已经写入过值的key的版本写入时（可能已经被其他客户端并发写入了），会返回失败，只能尝试升级版本重试。

3、写前读
客户算在写入一个key之前必须进行一次特殊的读取，成为写前读。服务端会记住最后一次进行写前读这个key的客户端，
后面只允许该客户端写入，并拒绝其他客户端的写入。这样是为了避免两个客户端同时进行多数派写入时的并发问题。

4、数据修复
客户端不论在写前读还是写入时，如果服务端返回改key已经有值了，则客户端必须暂时放弃自己要写入的值，将服务端返回的
值再执行一次写入，称为修复。这样可以帮助其他客户端在没写完就崩溃的情况下，替他完成一次多数派写入，完成数据一致性。
（当然也有可能人家根本没有崩溃，已经完整的写入了数据，但是也不影响）。

5、轮次
客户端的写入请求每次都需要带一个全局唯一且单调递增的轮次，用来区分谁更有权利写入数据。
写入失败之后需要升级轮次后再重试。

一个带冲突的写入流程可能是这样的：
客户端A先多数派读取key=name，返回的是：value=jim，version=1。
A想把name改成kim，发起了一个写前读操作，key=name,version=2，轮次=round1
过半的服务端返回了数据。
然后A发起了一个写入操作，key=name,value=kim,version=2，轮次=round1（要和写前读的轮次一样）
超过半数的服务端都正确返回了数据。
但是，其中有个别服务端拒绝了A的写入，因为已经存在了更高的轮次round2
说明已经有人在A写前读和写入的间隙快速的完成了一写前读，这时A只能轮次升级成round3再试，
这次round3的写前读直接返回说已经存在key=name，version=2的值，value=mike，说明别人已经抢先写入成功了，
这是A只能遵守规则，暂时先放弃自己的kim，把自己请求中的value也改成mike,替别人完成一次修复（哪怕可能不需要），执行写入请求（读前写就不用了因为该刚才执行过了）。
如果修复成功，A再拿自己的kim从头开始写入的流程...

流程有点繁琐，性能也不会太好，不过在大多数节点不挂的情况下，classic-paxos保证了数据的强一致性。
多数派读写：保证了数据在客户端层面的一致性，即只要是多数派写入成功的，所有人都可以通过多数派读看到最新值。
读前写和轮次，控制了并发写入问题，即同一时间一个key只能被写入一次。失败的客户端需要重试。
数据修复：确保了服务端副本之间的数据最终一致性。因为客户端可能会在写一半时崩溃（虽然不影响多数派读取），会造成节点之后的数据不一致，其他客户端会帮助修复这种问题，同步数据副本达到一致。
