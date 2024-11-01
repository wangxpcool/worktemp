package com.kmerit.Service.iml.Comparator;

import com.kmerit.Service.ComparatorFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Service
public class BigDecimalComparator implements ComparatorFactory {

    @Override
    public Boolean compare(Object obj1, Object obj2, String format) {
        if (obj1 == null && obj2 == null) {
            return true; // 两个都是null
        }
        if (obj1 == null || obj2 == null) {
            return false; // 其中一个为null
        }
        //todo 格式化
        DecimalFormat decimalFormat = new DecimalFormat(format);
        String amount1 = decimalFormat.format((BigDecimal) obj1);
        String amount2 = decimalFormat.format((BigDecimal) obj2);
        return amount1.compareTo(amount2) == 0; // 使用compareTo方法进行比较
    }

}
