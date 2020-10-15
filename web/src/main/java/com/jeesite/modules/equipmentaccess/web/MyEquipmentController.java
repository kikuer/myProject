/**
 * Copyright (c) 2013-Now http://jeesite.com All rights reserved.
 */
package com.jeesite.modules.equipmentaccess.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeesite.common.lang.DateUtils;
import com.jeesite.modules.equipmentaccess.client.ClientRunner;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jeesite.common.config.Global;
import com.jeesite.common.entity.Page;
import com.jeesite.common.web.BaseController;
import com.jeesite.modules.equipmentaccess.entity.MyEquipment;
import com.jeesite.modules.equipmentaccess.service.MyEquipmentService;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * opcua设备接入Controller
 * @author HIT
 * @version 2020-10-10
 */
@Controller
@RequestMapping(value = "${adminPath}/equipmentaccess/myEquipment")
public class MyEquipmentController extends BaseController {

	@Autowired
	private MyEquipmentService myEquipmentService;


	// 关键点！将每一个设备生成的client存储到Map，根据设备id获取client
	 public Map<String,OpcUaClient> clientMap = new HashMap<>();

	// 客户端实例，可能存在问题，每一个设备都对应一个客户端，因此不应该设置为全局变量
	// private OpcUaClient client = null;

	@Autowired
	private ClientRunner clientRunner;

	/**
	 * 获取数据
	 */
	@ModelAttribute
	public MyEquipment get(String equipmentId, boolean isNewRecord) {
		return myEquipmentService.get(equipmentId, isNewRecord);
	}
	
	/**
	 * 设备接入的查询列表
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:view")
	@RequestMapping(value = {"list", ""})
	public String list(MyEquipment myEquipment, Model model) {
		model.addAttribute("myEquipment", myEquipment);
		return "modules/equipmentaccess/myEquipmentList";
	}



	/**
	 * 设备控制的查询列表
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:view")
	@RequestMapping(value = {"list2", ""})
	public String list2(MyEquipment myEquipment, Model model) {
		model.addAttribute("myEquipment", myEquipment);
		return "modules/equipmentaccess/myEquipmentList2";
	}
	
	/**
	 * 查询列表数据,同时设定定时任务，每隔5分钟查询一次，是否没有在线；
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<MyEquipment> listData(MyEquipment myEquipment, HttpServletRequest request, HttpServletResponse response) {
		myEquipment.setPage(new Page<>(request, response));
		Page<MyEquipment> page = myEquipmentService.findPage(myEquipment);

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		// 参数：1、任务体 2、首次执行的延时时间
		//      3、任务执行间隔 4、间隔时间单位
		//设置六分钟检查一次
		service.scheduleAtFixedRate(()->{
			if (clientMap.size() == 0){
				List<MyEquipment> myEquipments = myEquipmentService.findAll();
				for (MyEquipment equipment : myEquipments) {
					myEquipmentService.modifyAccessStatus(equipment.getEquipmentId(),"离线");
				}

			}
		}, 0, 6, TimeUnit.MINUTES);

		return page;
	}




	/**
	 * 设备控制的查询列表，只查询在线的、已经支持控制的设备列表数据
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:view")
	@RequestMapping(value = "listData2")
	@ResponseBody
	public Page<MyEquipment> listData2(MyEquipment myEquipment, HttpServletRequest request, HttpServletResponse response) {
		myEquipment.setPage(new Page<>(request, response));
		Page<MyEquipment> page = myEquipmentService.findPage(myEquipment);
		List<MyEquipment> list = page.getList();
		List<MyEquipment> myList = new ArrayList<>();

		int listSize = list.size();
		//注意，这里list.size是动态变化的，因此不能写在循环体里面
		for (int i = 0; i < listSize; i++) {

			MyEquipment equipment = list.get(i);
			//查询已经在线的transport类型设备列表
			if (equipment.getEquipmentStatus().equals("在线") && equipment.getEquipmentType().equals("transport")){
				myList.add(equipment);
			}
		}

		page.setList(myList);

		return page;
	}






	/**
	 * 查看编辑表单
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:view")
	@RequestMapping(value = "form")
	public String form(MyEquipment myEquipment, Model model) {
		model.addAttribute("myEquipment", myEquipment);
		model.addAttribute("flag",0);
		return "modules/equipmentaccess/myEquipmentForm";
	}


	@RequiresPermissions("equipmentaccess:myEquipment:view")
	@RequestMapping(value = "update")
	public String update(MyEquipment myEquipment, Model model) {
		model.addAttribute("myEquipment", myEquipment);
		//当flag为1时候，更改表单的action，走另一个save，不要检查url是否重复
		model.addAttribute("flag",1);
		return "modules/equipmentaccess/myEquipmentForm";
	}

	/**
	 * 判断是否在线
	 *
	 */

	public  boolean isOnline(MyEquipment myEquipment) {
		OpcUaClient client = clientMap.get(myEquipment.getEquipmentId());
		if (client == null) {
			return false;
		}
		return true;
	}

	/**
	 * 保存数据，更新状态为离线
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:edit")
	@PostMapping(value = "save")
	@ResponseBody
	public String save(@Validated MyEquipment myEquipment) {


		String equipmentIp = myEquipment.getEquipmentIp();
		String equipmentPort = myEquipment.getEquipmentPort();
		// opc.tcp://localhost:4840
		String endPointUrl = "opc.tcp://" + equipmentIp + ":" + equipmentPort;

		//ip+port不允许重复，需要进行判断
		if (myEquipmentService.findRepeatIpPort(endPointUrl)){
			return renderResult(Global.TRUE, text(endPointUrl.substring(10) +"地址重复！"));
		}
		myEquipmentService.save(myEquipment);

		//更新当前设备状态
		String status = "离线";
		myEquipmentService.modifyAccessStatus(myEquipment.getEquipmentId(),status);
		return renderResult(Global.TRUE, text("保存opcua设备成功！"));
	}

    /**
     * 更新数据
     * @param myEquipment
     * @return
     */

    @RequiresPermissions("equipmentaccess:myEquipment:edit")
    @PostMapping(value = "save2")
    @ResponseBody
    public String save2(@Validated MyEquipment myEquipment) {

        myEquipmentService.save(myEquipment);
        return renderResult(Global.TRUE, text("保存opcua设备成功！"));
    }
	
	/**
	 * 删除设备，删除之前断开连接，并删除client
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(MyEquipment myEquipment) {
		//删除设备之前要断开设备的连接
		//根据设备id获取client
		OpcUaClient client = clientMap.get(myEquipment.getEquipmentId());
		//client为空则已经断开
		if (client != null){
			// 断开连接，并且将client置空存入到map中
			clientRunner.getFuture().complete(client);
			client = null;
			clientMap.put(myEquipment.getEquipmentId(),client);
		}

		myEquipmentService.delete(myEquipment);

		return renderResult(Global.TRUE, text("删除opcua设备成功！"));
	}


	/**
	 *
	 * 连接设备，连接完成后将client存入map中
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:edit")
	@RequestMapping(value = "connect")
	@ResponseBody
	public String connect( MyEquipment myEquipment){


		String equipmentIp = myEquipment.getEquipmentIp();
		String equipmentPort = myEquipment.getEquipmentPort();
		// opc.tcp://localhost:4840
		String endPointUrl = "opc.tcp://" + equipmentIp + ":" + equipmentPort;
		//判断是否已经连接,已经连接则返回
		OpcUaClient client = clientMap.get(myEquipment.getEquipmentId());
		if (client == null) {
			try {
				client = clientRunner.run(endPointUrl);
			}catch (Exception e){
				return renderResult(Global.TRUE, text("设备连接失败，请检查相应设置后重试!"));
			}
		}else {
			return renderResult(Global.TRUE, text("设备已连接!"));
		}

		// 创建连接，连接后将client存到map中
		try {
			client.connect().get();
			clientMap.put(myEquipment.getEquipmentId(),client);
		} catch (Exception e) {
			return renderResult(Global.TRUE, text("连接设备失败！"));
		}
		//更新当前设备状态
		String status = "在线";
		myEquipmentService.modifyAccessStatus(myEquipment.getEquipmentId(),status);
		//更新最近上线时间
		String currentTime = DateUtils.formatDateTime(new Date());
		myEquipmentService.updateOnlineTime(myEquipment.getEquipmentId(),currentTime);
		return renderResult(Global.TRUE,text("连接设备成功"));

	}

	/**
	 * 断开连接，设置状态为离线
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:edit")
	@PostMapping(value = "disconnect")
	@ResponseBody
	public String disconnect(MyEquipment myEquipment){

		//根据设备id获取client
		OpcUaClient client = clientMap.get(myEquipment.getEquipmentId());

		//client为空则已经断开
		if (client == null){
			return renderResult(Global.TRUE, text("设备已经断开连接!"));
		}else {
			// 断开连接，并且将client置空存入到map中
			clientRunner.getFuture().complete(client);
			client = null;
			clientMap.put(myEquipment.getEquipmentId(),client);
			//更新当前设备状态
			String status = "离线";
			myEquipmentService.modifyAccessStatus(myEquipment.getEquipmentId(),status);
			return renderResult(Global.TRUE, text("断开连接成功!"));
		}

	}

	/**
	 * 读取单个设备的数据
	 */
	@RequiresPermissions("equipmentaccess:myEquipment:edit")
	@RequestMapping(value = "readValue")
	public String readValue(MyEquipment myEquipment,Model model){

		//如果没有连接，提示连接设备
		String status = myEquipmentService.getAccessStatus(myEquipment.getEquipmentId());
		if (status.equals("离线")){
			return "请先连接设备";
		}
		//拿到对应设备的client
		OpcUaClient client = clientMap.get(myEquipment.getEquipmentId());


		try {
			//nodes   Objects，Types，View
			List<Node> nodes = client.getAddressSpace().browse(Identifiers.RootFolder).get();
			//拿到Objects
			Node serverLastNode = nodes.get(0);

			//list中有server、tank
			List<Node> serverNode = client.getAddressSpace().browseNode(serverLastNode).get();

			//拿到opcuaServer的名称
			Node myServerNode = serverNode.get(1);
			model.addAttribute("opcUAServerName",myServerNode.getDisplayName().get().getText());

			//获取opcuaServer的节点属性名，到前端展示
			List<Node> myVaribles = client.getAddressSpace().browseNode(myServerNode).get();

			//生成属性名attr0 - attrmmmmmmm
			String[] attr = new String[myVaribles.size()];

			//生成属性名对应的值的名称 attrNum0 - attNummmmmmmm
			String[] attrNum = new String[myVaribles.size()];
			for (int i=0; i<attr.length; i++){
				attr[i] = "attr" + String.valueOf(i);
				attrNum[i] = "attrNum" + String.valueOf(i);
			}

			for (int i=0; i<myVaribles.size(); i++){
				model.addAttribute(attr[i],myVaribles.get(i).getDisplayName().get().getText());

				Object value =  client.readValue(0.0, TimestampsToReturn.Both, myVaribles.get(i).getNodeId().get()).get().getValue().getValue();
				model.addAttribute(attrNum[i],value.toString());
			}
			for (Node myVarible : myVaribles) {

				System.out.println(myVarible.getDisplayName().get().getText());
				Object value1 = client.readValue(0.0, TimestampsToReturn.Both, myVarible.getNodeId().get()).get().getValue().getValue();
				System.out.println(value1.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("myEquipmentName",myEquipment.getEquipmentName());
		model.addAttribute("myEquipment", myEquipment);
		//当flag为1时候，更改表单的action，走另一个save，不要检查url是否重复
		model.addAttribute("flag",1);
		return "modules/equipmentaccess/myEquipmentData";
	}






}
