package com.kmerit.Service;

import com.kmerit.entity.DataCompareType;
import com.kmerit.entity.DataSyncType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface DataReadService {

    List<Map<String, Object>> readData(DataSyncType type);

}
