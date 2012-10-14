import java.util.ArrayList;

public class NetworkThread extends Thread
{
	private ArrayList<NetworkReceivedEventListenerInterface> receiveListeners;

	public NetworkThread()
	{
		receiveListeners = new ArrayList<NetworkReceivedEventListenerInterface>();
	}
	
	public void run()
	{

	}

	public void addReceiveListener(NetworkReceivedEventListenerInterface listener)
	{
		if(listener == null)
		{
			Log.writeError("Tried to add a null listener object to the Network Thread");
			return;
		}

		receiveListeners.add(listener);
	}

	public synchronized boolean sendToServer(Object object)
	{
		return false;
	}
}
