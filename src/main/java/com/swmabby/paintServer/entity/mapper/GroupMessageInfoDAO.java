package com.swmabby.paintServer.entity.mapper;

import com.swmabby.paintServer.entity.model.GroupMessageInfo;
import com.swmabby.paintServer.entity.model.GroupMessageInfoExample;
import java.util.List;

public interface GroupMessageInfoDAO {
    long countByExample(GroupMessageInfoExample example);

    int deleteByExample(GroupMessageInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(GroupMessageInfo record);

    int insertSelective(GroupMessageInfo record);

    List<GroupMessageInfo> selectByExample(GroupMessageInfoExample example);

    GroupMessageInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") GroupMessageInfo record, @Param("example") GroupMessageInfoExample example);

    int updateByExample(@Param("record") GroupMessageInfo record, @Param("example") GroupMessageInfoExample example);

    int updateByPrimaryKeySelective(GroupMessageInfo record);

    int updateByPrimaryKey(GroupMessageInfo record);
}