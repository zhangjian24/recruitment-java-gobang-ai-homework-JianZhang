package com.jianzhang.service;

import java.util.Map;

public interface GameService {

    Integer WITH = 20;

    Integer WIN_WITH = 5;

    String USER = "user";

    String AI = "AI";

    String NONE = "none";

    Integer YES = 1;

    Integer NO = 0;

    /**
     * 新一轮棋局
     *
     * @return
     */
    Map newGame();

    /**
     * 放置棋子
     *
     * @param gameId
     * @param x
     * @param y
     * @return
     */
    Map placePosition(Long gameId, Integer x, Integer y);

    /**
     * 棋局列表
     *
     * @return
     */
    Map gameList();

    /**
     * 棋局详情
     *
     * @param gameId
     * @return
     */
    Map gameInfo(Long gameId);
}
