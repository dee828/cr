package com.example.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.business.entity.Train;
import org.apache.ibatis.annotations.CacheNamespace;

@CacheNamespace
public interface TrainMapper extends BaseMapper<Train> {
}
