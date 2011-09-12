package org.simmi.client;

import org.simmi.shared.Blast;
import org.simmi.shared.Database;
import org.simmi.shared.Machine;
import org.simmi.shared.Sequences;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getSequences(AsyncCallback<Sequences[]> asyncCallback);
	void saveSequences(Sequences seqs, AsyncCallback<String> asyncCallback);
	void getBlastResults(AsyncCallback<Blast[]> asyncCallback);
	void getDatabases(AsyncCallback<Database[]> asyncCallback);
	void saveDb(Database b, AsyncCallback<String> asyncCallback);
	void saveBlast(Blast b, AsyncCallback<String> asyncCallback);
	void deleteKey(String key, AsyncCallback<String> asyncCallback);
	void getMachineInfo(String thismachine, int procs, AsyncCallback<Machine[]> asyncCallback);
	void saveMachine(Machine m, AsyncCallback<String> asyncCallback);
}
