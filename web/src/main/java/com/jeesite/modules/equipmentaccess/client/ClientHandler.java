/**
 * Created by Jellyleo on 2019年12月12日
 * Copyright © 2019 jellyleo.com 
 * All rights reserved. 
 */
package com.jeesite.modules.equipmentaccess.client;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

/**
 * @ClassName: ClientHandler
 * @Description: 客户端处理
 */
@Slf4j
@Service
public class ClientHandler {

	// 客户端实例
	private OpcUaClient client = null;

	@Autowired
	private ClientRunner clientRunner;

	/**
	 * 
	 * @MethodName: connect
	 * @Description: connect
	 * @throws Exception
	 */
//	public String connect() throws Exception {
//
//		if (client != null) {
//			return "客户端已创建";
//		}
//
//		client = clientRunner.run();
//
//		if (client == null) {
//			return "客户端配置实例化失败";
//		}
//
//		// 创建连接
//		client.connect().get();
//		return "创建连接成功";
//	}

	/**
	 * @MethodName: disconnect
	 * @Description: 断开连接
	 * @return
	 * @throws Exception
	 */
	public String disconnect() throws Exception {

		if (client == null) {
			return "连接已断开";
		}

		// 断开连接
		clientRunner.getFuture().complete(client);
		client = null;
		return "断开连接成功";
	}




	//浏览节点
	public String browse() {
		if (client == null) {
			return "找不到客户端，操作失败";
		}


		StringBuilder sb = new StringBuilder();


		try {
			List<Node> nodes = client.getAddressSpace().browse(Identifiers.RootFolder).get();
			for(Node node:nodes){
				List<Node> nodes1 = client.getAddressSpace().browseNode(node).get();
				for (int i = 0; i < nodes1.size(); i++) {
					Node node1 = nodes1.get(i);
					Object value = node1.readNodeId().get().getValue().getValue();

					System.out.println(nodes1.get(i).getBrowseName().get().getName() + " === "+value.toString());
				}
//				sb.append("Node= " + node.getBrowseName().get().getName() +"\n    ");
//				System.out.println("Node= " + node.getBrowseName().get().getName());

			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return sb.toString();

	}

	//读取节点值
	public String readValue() throws Exception {


		if (client != null) {
			return "客户端已创建";
		}

//		client = clientRunner.run();

		if (client == null) {
			return "客户端配置实例化失败";
		}

		// 创建连接
		client.connect().get();


		if (client == null){
			return "找不到客户端，操作失败";
		}
		DataValue value = null;


			NodeId nodeId1 = new NodeId(2, "\"OverheadFlow\"");
			NodeId nodeId2 = new NodeId(2, 5);
			NodeId nodeId3 = new NodeId(2, 4);
			NodeId nodeId4 = new NodeId(2, 3);
			NodeId nodeId5 = new NodeId(2, 1);
			NodeId nodeId6 = new NodeId(2, 2);
		ArrayList<NodeId> nodeIds = new ArrayList<>();
		nodeIds.add(nodeId1);
		nodeIds.add(nodeId2);
		nodeIds.add(nodeId3);
		nodeIds.add(nodeId4);
		nodeIds.add(nodeId5);
		nodeIds.add(nodeId6);

		try {
			for (NodeId nodeId : nodeIds) {
				Object value1 = client.readValue(0.0, TimestampsToReturn.Both, nodeId).get().getValue().getValue();
				if (value1 == null) continue;
				System.out.println(value1.toString());
			}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}



		return "读取";


	}

	//写入节点值
	public String writeValue(int value){
		//创建连接
        try {
            if (client != null) {
                return "客户端已创建";
            }
            // client = clientRunner.run();

            if (client == null) {
                return "客户端配置实例化失败";
            }

            // 创建连接
            client.connect().get();


            if (client == null){
                return "找不到客户端，操作失败";
            }

            NodeId nodeId = new NodeId(2, 3);
            Variant variant = new Variant(value);
            DataValue dataValue = new DataValue(variant, null, null);
            StatusCode statusCode = client.writeValue(nodeId, dataValue).get();

            System.out.println(statusCode.isGood());

        }catch (Exception e){
            e.printStackTrace();
        }

        return "写入";

	}


	//订阅变量
    //OPC UA提供了创建变量监控和订阅的方式来监控对应变量的变化。
    public String createSubscribe() throws Exception{
        //创建连接

            if (client != null) {
                return "客户端已创建";
            }

           // client = clientRunner.run();

            if (client == null) {
                return "客户端配置实例化失败";
            }

            // 创建连接
            client.connect().get();


            if (client == null){
                return "找不到客户端，操作失败";
            }
        //创建发布间隔1000ms的订阅对象
            UaSubscription subscription = client.getSubscriptionManager()
                    .createSubscription(1000.0).get();

       //创建订阅的变量
        NodeId nodeId = new NodeId(2, 2);
        ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, null);
        //创建监控的参数
        MonitoringParameters parameters = new MonitoringParameters(
                //用来标识每个创建的监控项，所以对于不同的监控变量这个值必须不同，并且唯一
                //可以采用递增的方式来设置这个值，或者在多线程环境下使用具有原子性的数据类型来设置该值。
                uint(1),
                1000.0,
                null,
                uint(10),
                true

        );
        //创建监控项请求
        //该请求最后用于创建订阅。
        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId,
                                                 MonitoringMode.Reporting, parameters);
        ArrayList<MonitoredItemCreateRequest> requests = new ArrayList<>();
        requests.add(request);

        //创建监控项，并且给出变量值改变时的回调函数
        List<UaMonitoredItem> items = subscription.createMonitoredItems(
                TimestampsToReturn.Both,
                requests,
                (item,id)->{
                    item.setValueConsumer((item2, value)->{
                        System.out.println("nodeid :"+item2.getReadValueId().getNodeId());
                        System.out.println("value :"+value.getValue().getValue());
                    });
                }
        ).get();



        return "订阅";
        }


}
