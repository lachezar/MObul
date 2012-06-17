package org.mobul.helpers;

import java.util.List;

import org.mobul.OEListsHost;
import org.mobul.db.ObservationData.Model;


public class ServerSyncAsyncTask extends CommonAsyncTask {
	
	private OEListsHost tabHost;
	private List<Model> od;
	
	public ServerSyncAsyncTask(OEListsHost activity, List<Model> od) {
		super(activity);
		tabHost = activity;
		this.od = od;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (od != null && od.size() > 0) {
			tabHost.sendData(od);
		}
	}

}
