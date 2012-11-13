struct HudFastPathEvent {
	1: string fpData
}

service HudFastPathService {
	HudFastPathEvent executeFastPath(1: HudFastPathEvent event)
}


