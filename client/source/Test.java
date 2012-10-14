import java.util.ArrayList;

public class Test
{
	ArrayList<NetworkReceivedEventListenerInterface> listeners;

	public Test()
	{
		this.listeners = new ArrayList<NetworkReceivedEventListenerInterface>();
	}

	public void addEventListener(NetworkReceivedEventListenerInterface listener)
	{
		listeners.add(listener);
	}

	public void fireEvent()
	{
		NetworkReceivedEvent event = new NetworkReceivedEvent(this, "Hey Brah");

		for(int index = 0; index < listeners.size(); index++)
		{
			listeners.get(index).handleNetworkReceiveEvent(event);
		}
	}
}
