package com.mulesoft.endpoint;


import com.mulesoft.exception.ServiceFaultException;
import com.mulesoft.repository.TshirtRepository;
import org.mulesoft.tshirt_service.ListInventory;
import org.mulesoft.tshirt_service.ListInventoryResponse;
import org.mulesoft.tshirt_service.OrderTshirt;
import org.mulesoft.tshirt_service.OrderTshirtResponse;
import org.mulesoft.tshirt_service.TrackOrder;
import org.mulesoft.tshirt_service.TrackOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class TshirtEndpoint {
	private static final String NAMESPACE_URI = "http://mulesoft.org/tshirt-service";

  private static final Logger log = LoggerFactory.getLogger(TshirtEndpoint.class);

	private TshirtRepository tshirtRepository;

	@Autowired
	public TshirtEndpoint(TshirtRepository tshirtRepository) {
		this.tshirtRepository = tshirtRepository;
	}


	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ListInventory")
	@ResponsePayload
	public ListInventoryResponse listInventory(@RequestPayload ListInventory request) {
		ListInventoryResponse response = new ListInventoryResponse();
		response.getInventory().addAll(tshirtRepository.findAllInventories());
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "OrderTshirt")
	@ResponsePayload
	public OrderTshirtResponse orderTshirt(@RequestPayload OrderTshirt orderTshirt) {
		OrderTshirtResponse response = new OrderTshirtResponse();
    long orderId = tshirtRepository.orderTshirt(orderTshirt);
    if(orderId==0){
      log.error("Email already exists");
      throw new ServiceFaultException("Order already exists for email: " + orderTshirt.getEmail());
    }
		response.setOrderId(String.valueOf(orderId));
		return response;
	}

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "TrackOrder")
  @ResponsePayload
  public TrackOrderResponse trackOrder(@RequestPayload TrackOrder trackOrder) {
	  TrackOrderResponse trackOrderResponse = tshirtRepository.trackOrder(trackOrder);
	  if(trackOrderResponse==null){
      log.error("Order doesn't exist");
      throw new ServiceFaultException("No order found for email: " + trackOrder.getEmail());
    }
   return tshirtRepository.trackOrder(trackOrder);
  }
}
