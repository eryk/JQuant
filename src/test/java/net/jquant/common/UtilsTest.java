package net.jquant.common;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class UtilsTest {
    @Test
    public void isToday() throws Exception {
        DateTime dt = new DateTime();
        Assert.assertTrue(Utils.isToday(dt.toDate()));
        dt = dt.plusDays(1);
        Assert.assertFalse(Utils.isToday(dt.toDate()));
        dt = dt.plusDays(-1);
        Assert.assertTrue(Utils.isToday(dt.toDate()));
        dt = dt.plusDays(-1);
        Assert.assertFalse(Utils.isToday(dt.toDate()));
    }

}