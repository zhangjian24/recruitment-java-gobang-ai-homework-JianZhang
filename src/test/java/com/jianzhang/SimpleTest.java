package com.jianzhang;

import com.jianzhang.dto.PositionInfo;
import com.jianzhang.gobang.Gobang;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);

    /**
     * 重复
     *
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void test01_01() throws Exception {
        Gobang gobang = new Gobang(20);

        gobang.placeUserPosition(5, 5);
        gobang.placeAIPosition(5, 5);
    }

    /**
     * 越界
     *
     * @throws Exception
     */
    @Test(expected = RuntimeException.class)
    public void test01_02() throws Exception {
        Gobang gobang = new Gobang(20);

        gobang.placeUserPosition(5, 5);
        gobang.placeAIPosition(5, 21);
    }

    /**
     * 横向ooooo
     *
     * @throws Exception
     */
    @Test
    public void test02_01() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeUserPosition(5, 5));
        Assert.assertFalse(gobang.placeUserPosition(5, 6));
        Assert.assertFalse(gobang.placeUserPosition(5, 7));
        Assert.assertFalse(gobang.placeUserPosition(5, 8));
        Assert.assertTrue(gobang.placeUserPosition(5, 9));
    }

    /**
     * 横向oo#ooo
     *
     * @throws Exception
     */
    @Test
    public void test02_02() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeUserPosition(5, 5));
        Assert.assertFalse(gobang.placeUserPosition(5, 6));
        Assert.assertFalse(gobang.placeAIPosition(5, 7));
        Assert.assertFalse(gobang.placeUserPosition(5, 8));
        Assert.assertFalse(gobang.placeUserPosition(5, 9));
        Assert.assertFalse(gobang.placeUserPosition(5, 10));
    }

    /**
     * 纵向ooooo
     *
     * @throws Exception
     */
    @Test
    public void test02_03() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 6));
        Assert.assertFalse(gobang.placeAIPosition(6, 6));
        Assert.assertFalse(gobang.placeAIPosition(7, 6));
        Assert.assertFalse(gobang.placeAIPosition(8, 6));
        Assert.assertTrue(gobang.placeAIPosition(9, 6));
    }

    /**
     * 纵向oo#ooo
     *
     * @throws Exception
     */
    @Test
    public void test02_04() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 6));
        Assert.assertFalse(gobang.placeAIPosition(6, 6));
        Assert.assertFalse(gobang.placeUserPosition(7, 6));
        Assert.assertFalse(gobang.placeAIPosition(8, 6));
        Assert.assertFalse(gobang.placeAIPosition(9, 6));
        Assert.assertFalse(gobang.placeAIPosition(10, 6));
    }

    /**
     * 顺时针45° ooooo
     *
     * @throws Exception
     */
    @Test
    public void test02_05() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 5));
        Assert.assertFalse(gobang.placeAIPosition(4, 6));
        Assert.assertFalse(gobang.placeAIPosition(3, 7));
        Assert.assertFalse(gobang.placeAIPosition(2, 8));
        Assert.assertTrue(gobang.placeAIPosition(1, 9));
    }

    /**
     * 顺时针45° oo#ooo
     *
     * @throws Exception
     */
    @Test
    public void test02_06() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 5));
        Assert.assertFalse(gobang.placeAIPosition(4, 6));
        Assert.assertFalse(gobang.placeUserPosition(3, 7));
        Assert.assertFalse(gobang.placeAIPosition(2, 8));
        Assert.assertFalse(gobang.placeAIPosition(1, 9));
        Assert.assertFalse(gobang.placeAIPosition(0, 10));
    }

    /**
     * 逆时针45° ooooo
     *
     * @throws Exception
     */
    @Test
    public void test02_07() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 5));
        Assert.assertFalse(gobang.placeAIPosition(6, 6));
        Assert.assertFalse(gobang.placeAIPosition(7, 7));
        Assert.assertFalse(gobang.placeAIPosition(8, 8));
        Assert.assertTrue(gobang.placeAIPosition(9, 9));
    }

    /**
     * 逆时针45° oo#ooo
     *
     * @throws Exception
     */
    @Test
    public void test02_08() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 5));
        Assert.assertFalse(gobang.placeAIPosition(6, 6));
        Assert.assertFalse(gobang.placeUserPosition(7, 7));
        Assert.assertFalse(gobang.placeAIPosition(8, 8));
        Assert.assertFalse(gobang.placeAIPosition(9, 9));
        Assert.assertFalse(gobang.placeAIPosition(10, 10));
    }


    /**
     * 横向oo_oo，连续长度对应可放置的点
     *
     * @throws Exception
     */
    @Test
    public void test03_01() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 5));
        Assert.assertFalse(gobang.placeAIPosition(5, 6));
//        Assert.assertFalse(gobang.placeUserPosition(5, 7));
        Assert.assertFalse(gobang.placeAIPosition(5, 8));
        Assert.assertFalse(gobang.placeAIPosition(5, 9));

        PositionInfo positionInfo = gobang.estimateAI();
        System.out.println(positionInfo);
    }

    /**
     * 横向oo#oo，连续长度对应可放置的点
     *
     * @throws Exception
     */
    @Test
    public void test03_02() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 5));
        Assert.assertFalse(gobang.placeAIPosition(5, 6));
        Assert.assertFalse(gobang.placeUserPosition(5, 7));
        Assert.assertFalse(gobang.placeAIPosition(5, 8));
        Assert.assertFalse(gobang.placeAIPosition(5, 9));

        PositionInfo positionInfo = gobang.estimateAI();
        System.out.println(positionInfo);
    }

    /**
     * _____
     * _o#o_
     * __o__
     * _____
     * ___o_
     * 连续长度对应可放置的点
     *
     * @throws Exception
     */
    @Test
    public void test03_03() throws Exception {
        Gobang gobang = new Gobang(20);

        Assert.assertFalse(gobang.placeAIPosition(5, 5));
        Assert.assertFalse(gobang.placeUserPosition(5, 6));
        Assert.assertFalse(gobang.placeAIPosition(5, 7));
        Assert.assertFalse(gobang.placeAIPosition(6, 6));
        Assert.assertFalse(gobang.placeAIPosition(8, 7));

        PositionInfo positionInfo = gobang.estimateAI();
        System.out.println(positionInfo);
    }

}
