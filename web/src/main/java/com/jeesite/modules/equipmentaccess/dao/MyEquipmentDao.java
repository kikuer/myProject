/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.equipmentaccess.dao;

import com.jeesite.common.dao.CrudDao;
import com.jeesite.common.mybatis.annotation.MyBatisDao;
import com.jeesite.modules.equipmentaccess.entity.MyEquipment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * opcua设备接入DAO接口
 * @author HIT
 * @version 2020-10-10
 */
@MyBatisDao
public interface MyEquipmentDao extends CrudDao<MyEquipment> {
    void modifyAccessStatus(@Param("equipmentId")String equipmentId,@Param("status")String status);

    void updatOnlineTime(@Param("equipmentId") String equipmentId, @Param("currentTime")String currentTime);

    List<MyEquipment> findAll();

    String getAccessStatus(String equipmentId);
}