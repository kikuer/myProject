/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.equipmentaccess.entity;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import com.jeesite.common.entity.DataEntity;
import com.jeesite.common.mybatis.annotation.Column;
import com.jeesite.common.mybatis.annotation.Table;
import com.jeesite.common.mybatis.mapper.query.QueryType;

/**
 * opcua设备接入Entity
 * @author HIT
 * @version 2020-10-10
 */
@Table(name="my_equipment", alias="a", columns={
		@Column(name="equipment_id", attrName="equipmentId", label="设备id", isPK=true),
		@Column(name="equipment_name", attrName="equipmentName", label="设备名称", queryType=QueryType.LIKE),
		@Column(name="equipment_affliation", attrName="equipmentAffliation", label="归属机构"),
		@Column(name="equipment_type", attrName="equipmentType", label="设备类型"),
		@Column(name="equipment_standard", attrName="equipmentStandard", label="设备规格"),
		@Column(name="equipment_description", attrName="equipmentDescription", label="设备描述"),
		@Column(name="equipment_ip", attrName="equipmentIp", label="设备ip"),
		@Column(name="equipment_port", attrName="equipmentPort", label="通信端口"),
		@Column(name="equipment_status", attrName="equipmentStatus", label="在线状态"),
		@Column(name="equipment_access_time", attrName="equipmentAccessTime", label="最近上线时间"),
	}, orderBy="a.equipment_id DESC"
)
public class MyEquipment extends DataEntity<MyEquipment> {
	
	private static final long serialVersionUID = 1L;
	private String equipmentId;		// 设备id
	private String equipmentName;		// 设备名称
	private String equipmentAffliation;		// 归属机构
	private String equipmentType;		// 设备类型
	private String equipmentStandard;		// 设备规格
	private String equipmentDescription;		// 设备描述
	private String equipmentIp;		// 设备ip
	private String equipmentPort;		// 通信端口
	private String equipmentStatus;		// 在线状态
	private String equipmentAccessTime;		// 最近上线时间
	
	public MyEquipment() {
		this(null);
	}

	public MyEquipment(String id){
		super(id);
	}
	
	public String getEquipmentId() {
		return equipmentId;
	}

	public void setEquipmentId(String equipmentId) {
		this.equipmentId = equipmentId;
	}
	
	@NotBlank(message="设备名称不能为空")
	@Length(min=0, max=100, message="设备名称长度不能超过 100 个字符")
	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}
	
	@Length(min=0, max=255, message="归属机构长度不能超过 255 个字符")
	public String getEquipmentAffliation() {
		return equipmentAffliation;
	}

	public void setEquipmentAffliation(String equipmentAffliation) {
		this.equipmentAffliation = equipmentAffliation;
	}
	
	@Length(min=0, max=255, message="设备类型长度不能超过 255 个字符")
	public String getEquipmentType() {
		return equipmentType;
	}

	public void setEquipmentType(String equipmentType) {
		this.equipmentType = equipmentType;
	}
	
	@Length(min=0, max=100, message="设备规格长度不能超过 100 个字符")
	public String getEquipmentStandard() {
		return equipmentStandard;
	}

	public void setEquipmentStandard(String equipmentStandard) {
		this.equipmentStandard = equipmentStandard;
	}
	
	public String getEquipmentDescription() {
		return equipmentDescription;
	}

	public void setEquipmentDescription(String equipmentDescription) {
		this.equipmentDescription = equipmentDescription;
	}
	
	@NotBlank(message="设备ip不能为空")
	@Length(min=0, max=255, message="设备ip长度不能超过 255 个字符")
	public String getEquipmentIp() {
		return equipmentIp;
	}

	public void setEquipmentIp(String equipmentIp) {
		this.equipmentIp = equipmentIp;
	}
	
	@NotBlank(message="通信端口不能为空")
	@Length(min=0, max=255, message="通信端口长度不能超过 255 个字符")
	public String getEquipmentPort() {
		return equipmentPort;
	}

	public void setEquipmentPort(String equipmentPort) {
		this.equipmentPort = equipmentPort;
	}
	
	public String getEquipmentStatus() {
		return equipmentStatus;
	}

	public void setEquipmentStatus(String equipmentStatus) {
		this.equipmentStatus = equipmentStatus;
	}
	
	public String getEquipmentAccessTime() {
		return equipmentAccessTime;
	}

	public void setEquipmentAccessTime(String equipmentAccessTime) {
		this.equipmentAccessTime = equipmentAccessTime;
	}


}