package ApiFreeze;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/freeze")
public class FreezeController {

	private final FreezeService freezeService;

	public FreezeController(FreezeService freezeService) {
		this.freezeService = freezeService;
	}

	// API to freeze the system
	@PostMapping("/freezeApi")
	public ResponseEntity<String> freezeSystem() {
		freezeService.setFreezeState(true); // Set freeze to true
		return ResponseEntity.ok("System is now frozen.");
	}

	// API to unfreeze the system
	@PostMapping("/unfreezeApi")
	public ResponseEntity<String> unfreezeSystem() {
		freezeService.setFreezeState(false); // Set freeze to false
		return ResponseEntity.ok("System is now unfrozen.");
	}

	@GetMapping("/freeze-status")
	public ResponseEntity<String> getFreezeStatus() {
		boolean freezeState = freezeService.getFreezeState();
		return ResponseEntity.ok(freezeState ? "System is frozen." : "System is not frozen.");
	}
}
