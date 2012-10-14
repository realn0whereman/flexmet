import java.util.EventListener;

public interface NetworkReceivedEventListenerInterface extends EventListener
{
	public void handleNetworkReceiveEvent(NetworkReceivedEvent nre);
}
