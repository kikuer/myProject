/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.equipmentaccess.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeesite.common.entity.Page;
import com.jeesite.common.service.CrudService;
import com.jeesite.modules.equipmentaccess.entity.MyEquipment;
import com.jeesite.modules.equipmentaccess.dao.MyEquipmentDao;

/**
 * opcua设备接入Service
 * @author HIT
 * @version 2020-10-10
 */
@Service
@Transactional(readOnly=true)
public class MyEquipmentService extends CrudService<MyEquipmentDao, MyEquipment> {


	@Autowired
	private MyEquipmentDao myEquipmentDao;


	
	/**
	 * 获取单条数据
	 * @param myEquipment
	 * @return
	 */
	@Override
	public MyEquipment get(MyEquipment myEquipment) {
		return super.get(myEquipment);
	}
	
	/**
	 * 查询分页数据
	 * @param myEquipment 查询条件
	 * @param myEquipment.page 分页对象
	 * @return
	 */
	@Override
	public Page<MyEquipment> findPage(MyEquipment myEquipment) {
		return super.findPage(myEquipment);
	}
	
	/**
	 * 保存数据（插入或更新）
	 * @param myEquipment
	 */
	@Override
	@Transactional(readOnly=false)
	public void save(MyEquipment myEquipment) {
		super.save(myEquipment);
	}
	
	/**
	 * 更新状态
	 * @param myEquipment
	 */
	@Override
	@Transactional(readOnly=false)
	public void updateStatus(MyEquipment myEquipment) {
		super.updateStatus(myEquipment);
	}
	
	/**
	 * 删除数据
	 * @param myEquipment
	 */
	@Override
	@Transactional(readOnly=false)
	public void delete(MyEquipment myEquipment) {
		super.delete(myEquipment);
	}


	/**
	 * 更新设备在线状态
	 * @param id
	 * @param equipmentId
	 */

	@Transactional(readOnly=false)
	public void modifyAccessStatus(String equipmentId,String status) {
		myEquipmentDao.modifyAccessStatus(equipmentId,status);
	}

	/**
	 * 更新设备上线时间
	 * @param myEquipment
	 * @param id
	 */

	@Transactional(readOnly=false)
    public void updateOnlineTime(String equipmentId, String currentTime) {

		myEquipmentDao.updatOnlineTime(equipmentId,currentTime);


	}


	@Transactional(readOnly=false)
	public boolean findRepeatIpPort(String endPointUrl) {

		List<MyEquipment> list = myEquipmentDao.findAll();
		for (int i = list.size() - 1; i >= 0; i--) {
			String existUrl = (list.get(i).getEquipmentIp() +":"+ list.get(i).getEquipmentPort()).trim().toLowerCase();
			if (endPointUrl.trim().toLowerCase().substring(10).equals(existUrl)){
				return true;
			}
		}
		return false; //没有重复

	}

	@Transactional(readOnly=false)
	public List<MyEquipment> findAll() {

		List<MyEquipment> list = myEquipmentDao.findAll();
		return  list;

	}

	//查询在线状态
    public String getAccessStatus(String equipmentId) {
		return myEquipmentDao.getAccessStatus(equipmentId);
    }
}