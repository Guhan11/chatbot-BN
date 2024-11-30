package ApiFreeze;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class FreezeService {

	@Value("${freeze.state}")
	private boolean freezeState;

	private final ApplicationContext context;

	public FreezeService(ApplicationContext context) {
		this.context = context;
	}

	// Get the current freeze state
	public boolean getFreezeState() {
		return freezeState;
	}

	// Set the freeze state dynamically (called by API to toggle freeze)
	public void setFreezeState(boolean freezeState) {
		this.freezeState = freezeState;
	}
}
