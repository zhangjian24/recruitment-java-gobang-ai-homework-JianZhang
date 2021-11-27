package com.jianzhang.gobang;

import com.jianzhang.dto.PositionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 五子棋逻辑类
 */
public class Gobang {

    private static final Logger LOGGER = LoggerFactory.getLogger(Gobang.class);

    /**
     * 用户落子标记
     */
    private static final int USER_FLAG = 1;

    /**
     * AI落子标记
     */
    private static final int AI_FLAG = -1;

    /**
     * 胜利条件，连子数
     */
    private static final int WIN_LENGTH = 5;

    /**
     * 棋盘长度最大限制
     */
    private static final int MAX_WIDTH = 1000;

    /**
     * 棋盘大小
     */
    private int width;

    /**
     * 棋盘数组
     */
    private int[][] arr;

    public Gobang() {
    }

    public Gobang(int width) {
        if (width < WIN_LENGTH || width > MAX_WIDTH) {
            throw new RuntimeException("invalid width");
        }
        this.width = width;
        this.arr = new int[this.width][this.width];
    }

    /**
     * 用户落子
     *
     * @param x
     * @param y
     * @return 是否胜利
     */
    public synchronized boolean placeUserPosition(int x, int y) {
        if (x < 0 || x >= this.width) {
            throw new RuntimeException("invalid x");
        }
        if (y < 0 || y >= this.width) {
            throw new RuntimeException("invalid y");
        }
        if (arr[x][y] != 0) {
            throw new RuntimeException("position not empty");
        }
        arr[x][y] = USER_FLAG;

        return sureWin(USER_FLAG);
    }

    /**
     * AI落子 是否胜利
     *
     * @param x
     * @param y
     * @return
     */
    public synchronized boolean placeAIPosition(int x, int y) {
        if (x < 0 || x >= this.width) {
            throw new RuntimeException("invalid x");
        }
        if (y < 0 || y >= this.width) {
            throw new RuntimeException("invalid y");
        }
        if (arr[x][y] != 0) {
            throw new RuntimeException("position not empty");
        }
        arr[x][y] = AI_FLAG;

        return sureWin(AI_FLAG);
    }

    /**
     * 确定是否胜利
     *
     * @param flag 用户标记/AI标记
     * @return
     */
    private boolean sureWin(int flag) {
        LOGGER.debug("sureWin--->{}", flag);
        boolean isWin = false;
        //纵向
        for (int k = 0; !isWin && k < this.width; k++) {
            int continuous = 0;//连续长度
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int j = 0; !isWin && j < this.width; j++) {
                if (this.arr[k][j] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", k, j, continuous));
                } else {
                    continuous = 0;
                }
                if (continuous >= WIN_LENGTH) {
                    isWin = true;
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }
        if (isWin) {
            return true;
        }
        //横向
        LOGGER.debug("--->");
        for (int k = 0; !isWin && k < this.width; k++) {
            int continuous = 0;
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int i = 0; !isWin && i < this.width; i++) {
                if (this.arr[i][k] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", i, k, continuous));
                } else {
                    continuous = 0;
                }
                if (continuous >= WIN_LENGTH) {
                    isWin = true;
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }
        if (isWin) {
            return true;
        }
        //逆时针45°
        LOGGER.debug("--->");
        for (int k = 0; !isWin && k < this.width * 2; k++) {
            int continuous = 0;
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int i = 0; !isWin && i < this.width; i++) {
                if (k - i < 0 || k - i >= this.width) {
                    continue;
                }
                if (this.arr[i][k - i] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", i, k - i, continuous));
                } else {
                    continuous = 0;
                }
                if (continuous >= WIN_LENGTH) {
                    isWin = true;
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }
        if (isWin) {
            return true;
        }
        //顺时针45°
        LOGGER.debug("--->");
        for (int k = -this.width + 1; !isWin && k < this.width; k++) {
            int continuous = 0;
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int i = 0; !isWin && i < this.width; i++) {
                if (i + k < 0 || i + k >= this.width) {
                    continue;
                }
                if (this.arr[i][i + k] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", i, i + k, continuous));
                } else {
                    continuous = 0;
                }
                if (continuous >= WIN_LENGTH) {
                    isWin = true;
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }

        return isWin;
    }

    /**
     * 计算AI最佳落子位置
     *
     * @return
     */
    public synchronized PositionInfo estimateAI() {
//        如果发现自己下子后可以获胜，则在对应位置落子
        Map<Integer, List<PositionInfo>> estimateA = estimate(AI_FLAG);
        Map<PositionInfo, Long> scoreMapA = calScore(estimateA);
        if (Objects.nonNull(estimateA.get(WIN_LENGTH - 1))) {
            Optional<PositionInfo> best = estimateA.get(WIN_LENGTH - 1).stream().max(Comparator.comparing(scoreMapA::get));
            if (best.isPresent()) {
                return best.get();
            }
        }
//        否则，当发现用户的棋有四个连在一起的，需要去落子堵住一头
        Map<Integer, List<PositionInfo>> estimateU = estimate(USER_FLAG);
        Map<PositionInfo, Long> scoreMapU = calScore(estimateU);
        if (Objects.nonNull(estimateU.get(WIN_LENGTH - 1))) {
            Optional<PositionInfo> best = estimateU.get(WIN_LENGTH - 1).stream().max(Comparator.comparing(scoreMapU::get));
            if (best.isPresent()) {
                return best.get();
            }
        }
//        否则，当发现用户的棋有三个连在一起的，需要去落子堵住一头
        if (Objects.nonNull(estimateU.get(WIN_LENGTH - 2))) {
            Optional<PositionInfo> best = estimateU.get(WIN_LENGTH - 2).stream().max(Comparator.comparing(scoreMapU::get));
            if (best.isPresent()) {
                return best.get();
            }
        }

//        否则，在合适位置落子尽量去构建一个最长的连子
        return scoreMapA.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).map(Map.Entry::getKey).orElse(null);
    }

    /**
     * 计算已有连续点周围的落子点
     *
     * @param flag
     * @return
     */
    private Map<Integer, List<PositionInfo>> estimate(int flag) {
        LOGGER.debug("estimate--->{}", flag);
        Map<Integer, List<PositionInfo>> result = new HashMap<>();
        for (int k = 0; k < this.width; k++) {
            int continuous = 0;//连续长度
            PositionInfo startPos = null;
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int j = 0; j < this.width; j++) {
                if (this.arr[k][j] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", k, j, continuous));
                } else {
                    if (continuous > 0) {
                        List<PositionInfo> pos = result.get(continuous);
                        if (Objects.isNull(pos)) {
                            pos = new ArrayList<>();
                            result.put(continuous, pos);
                        }
                        if (Objects.nonNull(startPos)) {
                            pos.add(startPos);
                        }
                        startPos = null;

                        if (this.arr[k][j] == 0) {
                            pos.add(new PositionInfo(k, j));
                        }
                        continuous = 0;
                    }
                    if (this.arr[k][j] == 0) {
                        startPos = new PositionInfo(k, j);
                    } else {
                        startPos = null;
                    }
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }
        LOGGER.debug("--->");
        for (int k = 0; k < this.width; k++) {
            int continuous = 0;
            PositionInfo startPos = null;
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int i = 0; i < this.width; i++) {
                if (this.arr[i][k] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", i, k, continuous));
                } else {
                    if (continuous > 0) {
                        List<PositionInfo> pos = result.get(continuous);
                        if (Objects.isNull(pos)) {
                            pos = new ArrayList<>();
                            result.put(continuous, pos);
                        }
                        if (Objects.nonNull(startPos)) {
                            pos.add(startPos);
                        }
                        startPos = null;

                        if (this.arr[i][k] == 0) {
                            pos.add(new PositionInfo(i, k));
                        }
                        continuous = 0;
                    }
                    if (this.arr[i][k] == 0) {
                        startPos = new PositionInfo(i, k);
                    } else {
                        startPos = null;
                    }
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }
        LOGGER.debug("--->");
        for (int k = 0; k < this.width * 2; k++) {
            int continuous = 0;
            PositionInfo startPos = null;
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int i = 0; i < this.width; i++) {
                if (k - i < 0 || k - i >= this.width) {
                    continue;
                }
                if (this.arr[i][k - i] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", i, k - i, continuous));
                } else {
                    if (continuous > 0) {
                        List<PositionInfo> pos = result.get(continuous);
                        if (Objects.isNull(pos)) {
                            pos = new ArrayList<>();
                            result.put(continuous, pos);
                        }
                        if (Objects.nonNull(startPos)) {
                            pos.add(startPos);
                        }
                        startPos = null;

                        if (this.arr[i][k - i] == 0) {
                            pos.add(new PositionInfo(i, k - i));
                        }
                        continuous = 0;
                    }
                    if (this.arr[i][k - i] == 0) {
                        startPos = new PositionInfo(i, k - i);
                    } else {
                        startPos = null;
                    }
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }
        LOGGER.debug("--->");
        for (int k = -this.width + 1; k < this.width; k++) {
            int continuous = 0;
            PositionInfo startPos = null;
            StringJoiner lineJoiner = new StringJoiner(",");
            for (int i = 0; i < this.width; i++) {
                if (i + k < 0 || i + k >= this.width) {
                    continue;
                }
                if (this.arr[i][i + k] == flag) {
                    continuous++;
                    lineJoiner.add(String.format("(%s,%s)->%s", i, i + k, continuous));
                } else {
                    if (continuous > 0) {
                        List<PositionInfo> pos = result.get(continuous);
                        if (Objects.isNull(pos)) {
                            pos = new ArrayList<>();
                            result.put(continuous, pos);
                        }
                        if (Objects.nonNull(startPos)) {
                            pos.add(startPos);
                        }
                        startPos = null;

                        if (this.arr[i][i + k] == 0) {
                            pos.add(new PositionInfo(i, i + k));
                        }
                        continuous = 0;
                    }
                    if (this.arr[i][i + k] == 0) {
                        startPos = new PositionInfo(i, i + k);
                    } else {
                        startPos = null;
                    }
                }
            }
            if (lineJoiner.length() > 0) {
                LOGGER.debug(lineJoiner.toString());
            }
        }

        return result;
    }

    /**
     * 计算落子点的推荐值
     *
     * @param pos
     * @return
     */
    private Map<PositionInfo, Long> calScore(Map<Integer, List<PositionInfo>> pos) {
        Map<PositionInfo, Long> scoreMap = new HashMap<>();
        // TODO: 2021/11/26 考虑连成5子 所需 连续棋子前后的 空位数，比如：3连前后只有一个空位，其价值就比较低
        pos.forEach((k, v) -> {
            for (PositionInfo positionInfo : v) {
                Long score = scoreMap.get(positionInfo);
                if (Objects.isNull(score)) {
                    score = 0L;
                }
                score += (long) k * k * width * width;//连子推荐值，连子长度、个数越大越推荐
                scoreMap.put(positionInfo, score);
            }
        });

        //加上位置推荐值，中间推荐值高，四周低
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (arr[i][j] == 0) {
                    PositionInfo positionInfo = new PositionInfo(i, j);
                    if (!scoreMap.containsKey(positionInfo)) {
                        scoreMap.put(positionInfo, (long) (width * i - i * i + width * j - j * j));
                    } else {
                        Long score = scoreMap.get(positionInfo);
                        scoreMap.put(positionInfo, score + (long) (width * i - i * i + width * j - j * j));
                    }
                }
            }
        }

        return scoreMap;
    }
}
