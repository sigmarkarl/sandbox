package org.simmi.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("listeinkunn")
public interface EinkunnService extends RemoteService {
	List<EinkunnSerializable> einkunnServer(String name) throws IllegalArgumentException;
}
