package com.kmerit.Service;

import org.springframework.stereotype.Service;

@Service
public interface ComparatorFactory {

//    Boolean compare(Object a,Object b,String format);
    <T> Boolean compare(T a, T b, String format);

}
