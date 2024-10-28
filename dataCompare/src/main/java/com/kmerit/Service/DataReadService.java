package com.kmerit.Service;

import com.kmerit.entity.DataCompareType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface DataReadService {

    Map<String, Object> readData(DataCompareType type);

}
