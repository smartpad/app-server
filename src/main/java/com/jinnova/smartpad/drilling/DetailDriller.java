package com.jinnova.smartpad.drilling;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

interface DetailDriller {
	
	DrillResult drill(int clusterId, String targetId, String targetSyscat, List<String> segments,
			BigDecimal lon, BigDecimal lat/*, int page, int size*/) throws SQLException;
}
