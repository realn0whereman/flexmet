struct HudFastPathEvent {
	1: string fpData
}

service HudFastPathService {
	HudFastPathEvent executeFastPath(1: HudFastPathEvent event)
}

struct HudPIQLQuery {
	1: string query
}

struct HudPIQLResponse {
	1: string response
}

service HudPIQLService {
	HudPIQLResponse executePIQLQuery(1: HudPIQLQuery)
}
