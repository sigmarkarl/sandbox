package org.simmi.client;

import org.simmi.shared.Blast;
import org.simmi.shared.Database;
import org.simmi.shared.Machine;
import org.simmi.shared.Sequences;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
	Sequences[] getSequences();
	String saveSequences(Sequences seqs);
	Blast[] getBlastResults();
	Database[] getDatabases();
	String saveDb(Database b);
	String saveBlast(Blast b);
	String deleteKey(String key);
	Machine[] getMachineInfo( String thismachine, int procs );
	String saveMachine(Machine m);
}
