/*
 Navicat Premium Data Transfer

 Source Server         : root
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : tiny_universe

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 09/07/2024 19:01:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '逗号隔开',
  `tag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` tinyint(1) NOT NULL COMMENT '(0到9) 1-9为文章 0为草稿',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '为1表示正常，为0表示删除？',
  `author` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `article_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 108 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (84, 1, '<p>你好你好你好你好你好你好你好</p>', '你好', '', '', '3', 0, '2024-07-02 18:48:37', '2024-07-02 18:48:37', 1, 'ailu');
INSERT INTO `article` VALUES (106, 1, '<ul><li style=\"text-align: start;\"></li></ul><h1 style=\"text-align: start;\">黑马程序员面试专题</h1><p style=\"text-indent: 2em; text-align: start;\"><br></p><h2 style=\"text-align: start;\">Redis</h2><h3 style=\"text-align: start;\">1.缓存穿透</h3><ul><li style=\"text-align: start;\">定义：查询一个不存在的数据，redis查询不到数据也就不会进行缓存，导致每次都查询数据库</li><li style=\"text-align: start;\">解决方案：如果查询的数据为空，在redis对应的key缓存空数据布隆过滤器，因为哈希冲突，所以可能误判，实现框架：Redisson、Guava。原理：存储bitmap（位图）数据：一个以bit为单位的数组，只存储二进制数据01存储：拿到key，通过多个hash函数计算多个hash值后，对应bitmap的多个索引位置，改为1读取：拿到key，使用相同的hash函数计算出对应的hash值，判断对应的位置是否都为1</li></ul><h3 style=\"text-align: start;\">2.缓存击穿</h3><ul><li style=\"text-align: start;\">定义：给一个key设置过期时间，当key过期时，恰好对这个key有大量并发请求，导致数据库压力过大（Redis重新建立缓存时间过长，在这段时间会查询数据库）</li><li style=\"text-align: start;\">解决方案:互斥锁（保证强一致性）：在Redis查询缓存未命中时，添加互斥锁，然后查询数据库并缓存数据，再写入缓存，释放锁逻辑过期+互斥锁（高效）：对热点key不设置过期时间，而是给对应的数据添加一个过期时间的字段。过程：在查询缓存时发现逻辑时间已经过期，此时添加互斥锁开启新子线程：查询数据库并缓存数据，再写入缓存，释放锁主线程和其他发起请求的线程不需要等待该子线程，而是直接返回数据（过期）</li></ul><h3 style=\"text-align: start;\">3.缓存雪崩</h3><ul><li style=\"text-align: start;\">定义：在同一时段大量的缓存key同时失效或者Redis服务宕机，导致数据库压力过大</li><li style=\"text-align: start;\">解决方案：key失效：给不同key的TTL的原值加上随机值服务宕机：Redis集群通用：降级限流（nginx或spring cloud gateway）,多级缓存</li></ul><p style=\"text-indent: 2em; text-align: start;\"><br></p><h3 style=\"text-align: start;\">4.双写一致性</h3><ul><li style=\"text-align: start;\">定义：当修改了数据库的数据也要同时更新缓存的数据</li><li style=\"text-align: start;\">问题：先删除缓存再操作数据库：线程1删除完缓存后，在数据库还没更新完时，线程2查询缓存未命中，查询数据库，写入缓存（旧），此时线程1数据库再更新完（新）**，导致不一致。先操作数据库再删除缓存：线程1查询缓存未命中，查询数据库，在写入缓存前切换到线程2，线程2更新数据库（新），删除缓存，在切换到线程1，写入缓存（旧），导致不一致</li><li style=\"text-align: start;\">高一致性实现方案：设定超时时间延迟双删：删除缓存，修改数据库，延迟一会再删除缓存，延迟的原因是等待数据库master同步到slave 读数据时添加共享锁：其他线程可共享读操作写数据时添加排他锁：阻塞其他线程读写操作</li><li style=\"text-align: start;\">弱一致性实现方案：异步通知：MQ：修改数据库时，发布消息到mq中，由消费者来更新缓存Canal：监听mysql的binlog（日志文件），无代码侵入</li></ul><p style=\"text-indent: 2em; text-align: start;\"><br></p><h3 style=\"text-align: start;\">5.Redis的数据持久性操作</h3><ul><li style=\"text-align: start;\">RDB：Redis DateBase，Redis数据快照，把内存中的数据都记录到磁盘中，当Redis服务重启后，从磁盘读取快照文件，恢复数据，</li></ul><ol><li style=\"text-align: start;\">执行原理： 通过bgsave命令，fork得到子进程，子进程拷贝主进程的页表，获取与物理内存的映射关系，即可生成RDB文件。fork采用的技术是copy-on-write，fork时将数据设置为read-only模式，此时如果用户进行写请求，将数据拷贝，主线程读写数据都依赖这个拷贝数据。</li></ol><ul><li style=\"text-align: start;\">AOF：Append Only File，追加文件，Redis处理的每个写命令都会记录在AOF文件，默认关闭，配置redis.conf的appendonly开启AOF，可appendsync指定命令记录频率 和 bgrewriteaof指定重写（合并命令）阈值。记录频率选择：always：立即记录每次的写命令everysec（一般）: 每s记录no：操作系统决定</li><li style=\"text-align: start;\">比较：</li></ul><p style=\"text-indent: 2em; text-align: start;\"><br></p><h3 style=\"text-align: start;\">6.Redis的过期策略</h3><ul><li style=\"text-align: start;\">惰性删除：在需要key时，才检查其是否过期，如果过期才删掉</li><li style=\"text-align: start;\">定期删除：每隔一段时间，对一些key进行检查，删除里面过期的keySLOW模式：定时任务FAST模式：执行频率不固定</li></ul><h3 style=\"text-align: start;\">7.数据淘汰策略</h3><ul><li style=\"text-align: start;\">定义：在Redis内存不够用时，对数据进行淘汰</li><li style=\"text-align: start;\">策略：noeviction：默认，不淘汰任何key，不允许添加新的数据volatile-ttl：TTL越小越先淘汰allkeys-random：全体key随机淘汰volatile-random: 对设置了TTL的key，随机淘汰allkeys-lru：基于LRU算法淘汰，LRU：最近最少使用，当前时间减去最后一次访问的时间，值越大淘汰优先级越高volatile-lru: 设置了TTL的key,基于LRU算法淘汰allkeys-lfu: 基于LFU算法淘汰，LFU：最少频率使用volatile-lfu: 设置了TTL的key,基于LFU算法淘汰</li><li style=\"text-align: start;\">使用：一般：allkeys-lru数据访问频率差距不大：allkeys-random有需要永久保存的数据：volatile-lru短时高频访问：allkeys-lfu或volatile-lfu</li><li style=\"text-align: start;\">面试题:数据库哟1000万数据，Redis只能存储20万数据，怎么保证Redis中的数据都是热点数据？答：使用Redis的allkeys-lru淘汰策略Redis的内存用完会发生什么？数据淘汰</li></ul><p style=\"text-indent: 2em; text-align: start;\"><br></p><h3 style=\"text-align: start;\">8.Redis，Redission的分布式锁</h3><ul><li style=\"text-align: start;\">定义：保证多个不同端口服务只有一个能访问共享资源的锁，Redis分布式锁为不可重入锁，Redission锁为为可重入锁：允许在已经持有锁的线程再次获取同一个锁而不导致死锁</li><li style=\"text-align: start;\">使用场景：定时任务，抢单，幂等性</li><li style=\"text-align: start;\">Redis分布式锁使用命令（setnx）：set key value NX EX ttl：获取锁，并设置锁的过期时间，避免服务宕机时锁没释放，导致死锁</li><li style=\"text-align: start;\">Redisson分布式锁执行流程：</li><li style=\"text-align: start;\">Redisson分布式锁（Redission锁是基于Redis锁+lua脚本实现的）使用代码</li><li style=\"text-align: start;\">Redission分布式锁实现可重入的实现原理：通过hash结构记录线程id和重入次数，获取锁的时候判断当前线程是否为获取锁的那个线程，如果是，则在原有的重入次数上加1，释放锁的时候也减一</li><li style=\"text-align: start;\">Redisson分布式锁无法实现主从一致：主从一致定义：master负责接收数据和把数据同步给slave，slave负责外部对数据的读取，当master宕机的时候，会选一个slave作为master解决方案：Redisson提供的RedLock（红锁）</li><li style=\"text-align: start;\">面试题：Redis分布式锁的使用场景：使用Redisson分布式锁，底层：setnx，lua脚本（原子性）Redisson分布式锁如何控制锁的有效时长：提供一个WatchDog（看门狗），线程获取锁成功后，WatchDog对该线程的锁续期</li></ul><h3 style=\"text-align: start;\">9.Redis集群方案</h3><ul><li style=\"text-align: start;\">主从（master,slave）复制：定义：搭建主从集群，实现读写分离，master负责接收数据和把数据同步给slave，slave负责外部对数据的读取全量同步： 首次主从同步增量同步：初次全量同步后</li><li style=\"text-align: start;\">哨兵（Sentinel）模式：定义：实现主从集群的自动故障恢复，保证高可用作用：监控：Sentinel不断监测主slave是否预期工作自动故障恢复：master故障，将一个slave提升为master，故障恢复也继续以该节点为master通知：当master转移时，将最新消息发送给Redis的客户端实现：原理：基于心跳机制监测服务，每隔1s向集群的每个实例发送ping服务主观下线：如果某个Sentinel节点发现Redis实例未在规定时间响应，则认为该实例主观下线客观下线：如果超过指定数量（quorum）的Sentinel认为该实例主观下线，则该实例客观下线哨兵选主规则：如果某个slave与master断开时间太长，就不会选为master依据设定的slave-priority的优先级slave的offset值（从master到从节点slave已成功复制的字节数），越大优先级越高slave节点的id越小，优先级越高脑裂问题：定义：由于网络原因，master与Sentinel被分配到了不同的网络分区，此时，哨兵监测不到master，即会选择一个slave作为master，即导致多个master。当网络恢复，原来的master会降级为slave，导致客户端写入的数据丢失解决方案：设置主节点所需的最少的从节点数量、缩短主从数据同步的延迟时间，达不到要求就拒绝客户端的请求面试题：怎么保证Redis的高并发高可用？哨兵模式：实现主从集群的自动故障恢复（监控、自动故障恢复、通知）你们使用redis是单点还是集群，哪种集群？主从（1主1从）+哨兵就可以了。单节点不超过10G内存，如果Redis内存不足则可以给不同服务分配独立的Redis主从节点</li><li style=\"text-align: start;\">分片集群：定义：定义多个主节点，每个主节点分配连续数量的哈希槽，主节点之间互相监测彼此心跳状态，客户端请求可以访问集群的任意一个主节点，最终都会被转发到正确的节点，而不需要Sentinel实现正确转发的原理：</li></ul><h3 style=\"text-align: start;\">10.Redis其他面试题</h3><ul><li style=\"text-align: start;\">Redis是单线程的，为什么那么快？Redis是纯内存操作避免上下文切换和线程安全问题使用IO多路复用模型，NIO（非阻塞IO）</li></ul><p style=\"text-indent: 2em; text-align: start;\"><br></p><h2 style=\"text-align: start;\">网络</h2><h3 style=\"text-align: start;\">1.网络模型</h3><ul><li style=\"text-align: start;\">BIO：进行IO请求时，线程被阻塞</li><li style=\"text-align: start;\">NIO：应用程序轮询发起 read 调用，直到数据准备好，但等待数据从内核空间拷贝到用户空间的这段时间里，线程依然是阻塞的。</li><li style=\"text-align: start;\">AIO：</li><li style=\"text-align: start;\">多路IO复用技术：</li></ul><p style=\"text-indent: 2em; text-align: start;\"><br></p><p style=\"text-indent: 2em; text-align: start;\"><br></p><p style=\"text-indent: 2em; text-align: start;\"><br></p><h2 style=\"text-align: start;\">Spring</h2><h3 style=\"text-align: start;\"><strong>1.Spring的单例Bean是线程安全的吗？</strong></h3><ul><li style=\"text-align: start;\">Bean是单例的，默认@Scope(\"singleton\")，可指定多例@Scope(\"prototype\")</li><li style=\"text-align: start;\">非线程安全，所以对于可被修改的成员变量需要考虑线程安全，而局部变量存在虚拟机栈，线程安全，被Spring管理的成员变量无状态（不可修改），线程安全</li></ul><h3 style=\"text-align: start;\">2.SpringAOP面向切面编程</h3><ul><li style=\"text-align: start;\">目的：将与业务无关，但对多个对象产生影响的公共代码抽取封装为一个模块，名为“切面”</li><li style=\"text-align: start;\">底层：JDK动态代理和CGLIB动态代理</li><li style=\"text-align: start;\">使用场景：操作日志，缓存（@Cacheable），事务处理（@Transactional）</li><li style=\"text-align: start;\">核心：\"通知\"+“切点表达式”的结合</li></ul><h3 style=\"text-align: start;\">3.Spring事务失效场景</h3><ul><li style=\"text-align: start;\">捕获异常：因为@Transactional是通过环绕通知+try-catch捕获到异常后进行回滚操作的，所以如果先捕获异常会导致@Transactional无法捕获到，即事务失效</li><li style=\"text-align: start;\">抛出（throws）受检查异常：Spring默认只会回滚不受检查异常，解决方案：@Transactional(rollbackFor=Exception.class)</li><li style=\"text-align: start;\">非public方法：不是public方法自然不会暴露给Spring</li></ul><h3 style=\"text-align: start;\">4.Spring的bean的生命周期</h3><ul><li style=\"text-align: start;\">BeanDefinition对象定义：Spring的IOC容器实例化时会将xml配置的bean信息封装成一个BeanDefinition，Spring据此创建bean对象。方法：getBeanClassName：获取bean的类名getInitMethodName：获取初始化方法名称getPropertyValues：获取bean的属性值getScope：获取作用域isLazyInit：判断是否延迟初始化</li><li style=\"text-align: start;\">生命周期通过BendDefinition获取bean的定义信息通过Bean的构造函数实例bean（创建）bean依赖注入成员变量（开始初始化）处理Aware接口（BeanNameAware,BeanFactoryAware,ApplicationContextAware）Bean的后置处理器BeanPostProcessor-前置Bean的初始化方法（initializingBean,init-method）Bean的后置处理器BeanPostProcessor-后置销毁bean</li><li style=\"text-align: start;\"></li></ul>', '黑马程序员面试', '', '', '3', 1, '2024-07-05 21:11:11', '2024-07-07 22:15:56', 1, 'ailu');

-- ----------------------------
-- Table structure for article_active
-- ----------------------------
DROP TABLE IF EXISTS `article_active`;
CREATE TABLE `article_active`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `love` bigint NOT NULL DEFAULT 0 COMMENT '点赞',
  `forward` bigint NOT NULL DEFAULT 0 COMMENT '转发量',
  `comment_count` bigint NOT NULL DEFAULT 0 COMMENT '评论',
  `collection_count` bigint NOT NULL DEFAULT 0 COMMENT '收藏',
  `watch` bigint NOT NULL DEFAULT 0 COMMENT '观看',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `article_id`(`article_id` ASC) USING BTREE,
  CONSTRAINT `article_active_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_active
-- ----------------------------

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`  (
  `article_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `tag_id` bigint NOT NULL,
  UNIQUE INDEX `article_tag_pk`(`article_id` ASC, `tag_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_tag
-- ----------------------------
INSERT INTO `article_tag` VALUES ('100', 4);
INSERT INTO `article_tag` VALUES ('101', 3);
INSERT INTO `article_tag` VALUES ('102', 3);
INSERT INTO `article_tag` VALUES ('103', 3);
INSERT INTO `article_tag` VALUES ('104', 3);
INSERT INTO `article_tag` VALUES ('105', 3);
INSERT INTO `article_tag` VALUES ('106', 3);
INSERT INTO `article_tag` VALUES ('107', 5);
INSERT INTO `article_tag` VALUES ('11', 3);
INSERT INTO `article_tag` VALUES ('12', 5);
INSERT INTO `article_tag` VALUES ('13', 4);
INSERT INTO `article_tag` VALUES ('14', 4);
INSERT INTO `article_tag` VALUES ('15', 4);
INSERT INTO `article_tag` VALUES ('16', 5);
INSERT INTO `article_tag` VALUES ('17', 5);
INSERT INTO `article_tag` VALUES ('18', 5);
INSERT INTO `article_tag` VALUES ('19', 5);
INSERT INTO `article_tag` VALUES ('20', 5);
INSERT INTO `article_tag` VALUES ('21', 5);
INSERT INTO `article_tag` VALUES ('22', 5);
INSERT INTO `article_tag` VALUES ('23', 14);
INSERT INTO `article_tag` VALUES ('24', 5);
INSERT INTO `article_tag` VALUES ('24', 8);
INSERT INTO `article_tag` VALUES ('25', 5);
INSERT INTO `article_tag` VALUES ('25', 8);
INSERT INTO `article_tag` VALUES ('27', 3);
INSERT INTO `article_tag` VALUES ('27', 4);
INSERT INTO `article_tag` VALUES ('28', 4);
INSERT INTO `article_tag` VALUES ('29', 3);
INSERT INTO `article_tag` VALUES ('30', 3);
INSERT INTO `article_tag` VALUES ('30', 4);
INSERT INTO `article_tag` VALUES ('30', 5);
INSERT INTO `article_tag` VALUES ('31', 4);
INSERT INTO `article_tag` VALUES ('32', 5);
INSERT INTO `article_tag` VALUES ('33', 3);
INSERT INTO `article_tag` VALUES ('34', 8);
INSERT INTO `article_tag` VALUES ('35', 5);
INSERT INTO `article_tag` VALUES ('36', 8);
INSERT INTO `article_tag` VALUES ('37', 8);
INSERT INTO `article_tag` VALUES ('38', 8);
INSERT INTO `article_tag` VALUES ('39', 8);
INSERT INTO `article_tag` VALUES ('40', 4);
INSERT INTO `article_tag` VALUES ('41', 5);
INSERT INTO `article_tag` VALUES ('42', 14);
INSERT INTO `article_tag` VALUES ('43', 7);
INSERT INTO `article_tag` VALUES ('44', 5);
INSERT INTO `article_tag` VALUES ('45', 5);
INSERT INTO `article_tag` VALUES ('46', 5);
INSERT INTO `article_tag` VALUES ('47', 5);
INSERT INTO `article_tag` VALUES ('47', 8);
INSERT INTO `article_tag` VALUES ('48', 5);
INSERT INTO `article_tag` VALUES ('48', 8);
INSERT INTO `article_tag` VALUES ('49', 5);
INSERT INTO `article_tag` VALUES ('49', 8);
INSERT INTO `article_tag` VALUES ('50', 11);
INSERT INTO `article_tag` VALUES ('51', 6);
INSERT INTO `article_tag` VALUES ('52', 3);
INSERT INTO `article_tag` VALUES ('53', 5);
INSERT INTO `article_tag` VALUES ('54', 11);
INSERT INTO `article_tag` VALUES ('55', 3);
INSERT INTO `article_tag` VALUES ('56', 5);
INSERT INTO `article_tag` VALUES ('57', 5);
INSERT INTO `article_tag` VALUES ('58', 5);
INSERT INTO `article_tag` VALUES ('59', 5);
INSERT INTO `article_tag` VALUES ('60', 5);
INSERT INTO `article_tag` VALUES ('61', 5);
INSERT INTO `article_tag` VALUES ('65', 5);
INSERT INTO `article_tag` VALUES ('66', 5);
INSERT INTO `article_tag` VALUES ('67', 5);
INSERT INTO `article_tag` VALUES ('68', 5);
INSERT INTO `article_tag` VALUES ('69', 5);
INSERT INTO `article_tag` VALUES ('70', 3);
INSERT INTO `article_tag` VALUES ('71', 3);
INSERT INTO `article_tag` VALUES ('72', 3);
INSERT INTO `article_tag` VALUES ('73', 3);
INSERT INTO `article_tag` VALUES ('8', 3);
INSERT INTO `article_tag` VALUES ('83', 3);
INSERT INTO `article_tag` VALUES ('84', 3);
INSERT INTO `article_tag` VALUES ('9', 3);
INSERT INTO `article_tag` VALUES ('9', 4);
INSERT INTO `article_tag` VALUES ('91', 5);
INSERT INTO `article_tag` VALUES ('93', 3);
INSERT INTO `article_tag` VALUES ('93', 4);
INSERT INTO `article_tag` VALUES ('93', 5);
INSERT INTO `article_tag` VALUES ('93', 6);
INSERT INTO `article_tag` VALUES ('95', 8);
INSERT INTO `article_tag` VALUES ('97', 3);
INSERT INTO `article_tag` VALUES ('98', 5);
INSERT INTO `article_tag` VALUES ('99', 3);

-- ----------------------------
-- Table structure for chat
-- ----------------------------
DROP TABLE IF EXISTS `chat`;
CREATE TABLE `chat`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat
-- ----------------------------

-- ----------------------------
-- Table structure for collection
-- ----------------------------
DROP TABLE IF EXISTS `collection`;
CREATE TABLE `collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` tinyint NOT NULL COMMENT '1收藏夹 0收藏内容',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collection
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `article_id` bigint NOT NULL,
  `parent_id` binary(1) NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `love` bigint NOT NULL DEFAULT 0,
  `update_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for log
-- ----------------------------
DROP TABLE IF EXISTS `log`;
CREATE TABLE `log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `status` int NULL DEFAULT NULL COMMENT '状态',
  `ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求URL',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误消息',
  `method` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方法名称',
  `request_method` enum('GET','POST','PUT','DELETE','HEAD','OPTIONS','TRACE','PATCH') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方式',
  `cost_time` bigint NULL DEFAULT NULL COMMENT '消耗时间（毫秒）',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '位置',
  `operation_type` int NULL DEFAULT NULL COMMENT '操作类型',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `operator_type` int NULL DEFAULT NULL COMMENT '操作员类型',
  `json_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'JSON结果',
  `oper_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作参数',
  `oper_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '日志实体表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of log
-- ----------------------------
INSERT INTO `log` VALUES (1, 0, '127.0.0.1', '/user/updateMsg', NULL, 'com.ailu.server.controller.user.UserController.updateMsg()', 'PUT', 111, '内网IP', 2, '个人信息', 0, '{\"code\":1}', '{\"avatar\":\"http://127.0.0.1:9000/public/2024/06/30/eae089034f534ed096bf335515a1bb62.png\",\"birthday\":\"2024-05-28\",\"description\":\"该用户太懒，甚至连自我介绍都没有\",\"id\":1,\"sex\":1,\"updateTime\":\"2024-07-08 16:56:06.744\",\"username\":\"ailu\"}', '2024-07-08 16:56:07');

-- ----------------------------
-- Table structure for setting
-- ----------------------------
DROP TABLE IF EXISTS `setting`;
CREATE TABLE `setting`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `background` varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'http://127.0.0.1:9000/public/红色.svg,http://127.0.0.1:9000/public/黄色.svg,http://127.0.0.1:9000/public/蓝色.svg,http://127.0.0.1:9000/public/绿色.svg',
  `is_public` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1公开 0不公开',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `setting_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of setting
-- ----------------------------
INSERT INTO `setting` VALUES (1, 1, 'http://127.0.0.1:9000/public/2024/06/19/3f219872633848dcafec363eb122e92f.jpg,http://127.0.0.1:9000/public/2024/06/19/12d7610a44054ef4b6ad799647bd51b2.jpg,http://127.0.0.1:9000/public/2024/06/19/bf0ef344ee55445f94cb083d83198e58.jpg,http://127.0.0.1:9000/public/2024/06/30/0ce22a7001ff415abfaa1135451ad0a4.png', 1);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `count` bigint NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (3, '闲聊', '2024-06-16 20:46:02', '2024-06-16 20:46:02', 24);
INSERT INTO `tag` VALUES (4, '游戏', '2024-06-16 20:46:29', '2024-06-16 20:46:30', 11);
INSERT INTO `tag` VALUES (5, '音乐', '2024-06-16 20:46:38', '2024-06-16 20:46:38', 36);
INSERT INTO `tag` VALUES (6, '电影', '2024-06-16 20:46:49', '2024-06-16 20:46:50', 2);
INSERT INTO `tag` VALUES (7, '生活', '2024-06-16 20:46:54', '2024-06-16 20:46:55', 1);
INSERT INTO `tag` VALUES (8, '艺术', '2024-06-16 20:47:11', '2024-06-16 20:47:12', 11);
INSERT INTO `tag` VALUES (9, '编程', '2024-06-16 20:47:17', '2024-06-16 20:47:18', 0);
INSERT INTO `tag` VALUES (10, '美食', '2024-06-16 20:47:29', '2024-06-16 20:47:30', 0);
INSERT INTO `tag` VALUES (11, '教育', '2024-06-16 20:48:13', '2024-06-16 20:48:13', 2);
INSERT INTO `tag` VALUES (12, '科技', '2024-06-16 20:48:27', '2024-06-16 20:48:28', 0);
INSERT INTO `tag` VALUES (13, '健康', '2024-06-16 20:48:38', '2024-06-16 20:48:39', 0);
INSERT INTO `tag` VALUES (14, '综合', '2024-06-16 20:51:28', '2024-06-16 20:51:29', 2);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sex` tinyint(1) NOT NULL DEFAULT 2 COMMENT '1:男  0:女 2:未知',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '该用户太懒，甚至连自我介绍都没有',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1-9:正常  0:被封禁',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `birthday` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `test_index1`(`status` ASC) USING BTREE,
  INDEX `test_index2`(`sex` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'ailu', '$2a$10$TgFS70a1RuqKasTfMIPOueZ/fresCdNY5L1ITEAHUDkB3Lb4Mq0zO', 1, 'http://127.0.0.1:9000/public/2024/06/30/eae089034f534ed096bf335515a1bb62.png', '2568315961@qq.com', '该用户太懒，甚至连自我介绍都没有', 1, '2024-06-04 17:19:05', '2024-07-08 16:56:07', '2024-05-28');

SET FOREIGN_KEY_CHECKS = 1;
