import java.util.EventObject;

public class NetworkReceivedEvent extends EventObject
{
	public String message;	

	public NetworkReceivedEvent(Object source, String message)
	{
		super(source);

		this.message = message;
	}
}
