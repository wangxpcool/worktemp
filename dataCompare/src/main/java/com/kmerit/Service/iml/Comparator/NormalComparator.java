package com.kmerit.Service.iml.Comparator;

import com.kmerit.Service.ComparatorFactory;
import org.springframework.stereotype.Service;

@Service
public class NormalComparator implements ComparatorFactory{

    @Override
    public Boolean compare(Object obj1, Object obj2,String format) {
        if (obj1 == null && obj2 == null) {
            return true; // 两个都是null
        }
        if (obj1 == null || obj2 == null) {
            return false; // 其中一个为null
        }
        return obj1.equals(obj2); // 调用equals方法进行比较
    }

}
