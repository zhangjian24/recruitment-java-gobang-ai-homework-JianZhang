# 应用启动
需要java、maven 环境

```shell
mvn clean package
java -jar target/recruitment-java-gobang-ai-homework-JianZhang-1.0-SNAPSHOT.jar
```

#数据结构
棋局 `com.jianzhang.entity.Game`

```java
package com.jianzhang.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 棋局
 */
@Entity
public class Game {
    @Id
    @GeneratedValue
    private Long id;

    private Integer width;

    private String winner;

    private Integer complete;
}

```

落子记录 `com.jianzhang.entity.Position`
```java
package com.jianzhang.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 落子记录
 */
@Entity
public class Position {
    @Id
    @GeneratedValue
    private Long id;

    private Long gameId;

    @Column(name = "`from`")
    private String from;

    private Integer x;

    private Integer y;
}

```

# 逻辑

棋局处理逻辑 `com.jianzhang.gobang.Gobang`

## `sureWin` 方法
> boolean sureWin(int flag)

利用坐标，在横向、纵向、顺时针45°、逆时针45° 四个方向上寻找连续棋子的长度，找到大于等于5的就胜利。

(注：其实可以缩小搜索范围，最后落子的四个方向上前后5个位置范围内找就可以了)

## `estimateAI` 方法
> PositionInfo estimateAI()

先计算 双方 不同长度 `连续棋子` 周围 可放置的位置，见方法`Map<Integer, List<PositionInfo>> estimate(int flag)`;

再通过 `Map<PositionInfo, Long> calScore(Map<Integer, List<PositionInfo>> pos)` 计算所有可放置位置的 `推荐值`，推荐值计算公式如下:


$$
\sum_{0}^{4}k_i^2w^2 +wi-i^2+wj-j^2
$$

> k ：当前位置在四个方向上某个方向的邻近 连续棋子 长度
>
> w ：棋盘宽度
>
> (i，j) ：当前位置

k参与计算是为了让邻近长度越长、数量越多连续棋子的位置推荐值越高

i、j 参与计算是为了，让棋子尽量放在棋盘中间



最后应用以下策略：

1. 如果发现自己下子后可以获胜，则在对应位置落子;
2. 否则，当发现用户的棋有四个连在一起的，需要去落子堵住一头
3. 否则，当发现用户的棋有三个连在一起的，需要去落子堵住一头
4. 否则，在合适位置落子尽量去构建一个最长的连子

## 存在的问题

1. 判断胜利的搜索范围可以缩小
2. 没考虑 连续棋子 前后的 空位数，是否足够形成5子连续，比如：3连前后只有一个空位，最多形成4连，其价值就比较低