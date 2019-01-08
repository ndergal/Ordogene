package org.ordogene.api;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.ordogene.file.utils.ApiJsonResponse;
import org.ordogene.file.utils.Const;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.util.FormatUtil;

/**
 * This class is useful to check if the server is running
 * 
 * @author darwinners team
 *
 */
@RestController
public class HelloController {

	private final static SystemInfo si = new SystemInfo();

	/**
	 * @return "Ordogene Project"
	 */
	@RequestMapping("/")
	public String index() {
		return "Ordogene Project";
	}

	@RequestMapping("/resources")
	public ResponseEntity<ApiJsonResponse> resources() {
		StringBuilder sb = new StringBuilder();

		HardwareAbstractionLayer hal = si.getHardware();
		// CPu information
		CentralProcessor processor = hal.getProcessor();
		// cpu brand/model
		sb.append(hal.getProcessor() + " CPU(s):").append('\n');
		// cpu load
		sb.append(String.format("CPU load: %.1f%% %n", processor.getSystemCpuLoad() * 100));

		// per core CPU
		sb.append("CPU load per processor: ");
		double[] load = processor.getProcessorCpuLoadBetweenTicks();
		for (double avg : load) {
			sb.append(String.format(" %.1f%%", avg * 100));
		}
		// free memory available on the machine
		sb.append("\n\nFree memory on the system : " + FormatUtil.formatBytes(hal.getMemory().getAvailable()) + "/"
				+ FormatUtil.formatBytes(hal.getMemory().getTotal()));
		/* Total amount of free memory available to the JVM */
		sb.append("\nFree memory in the JVM : " + FormatUtil.formatBytes(Runtime.getRuntime().freeMemory()));

		/* Total memory currently in use by the JVM */
		sb.append("\nTotal memory actually allocated to the JVM : "
				+ FormatUtil.formatBytes(Runtime.getRuntime().totalMemory()));

		/* This will return Long.MAX_VALUE if there is no preset limit */
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		sb.append("\nMaximum memory allocated to the JVM : "
				+ (maxMemory == Long.MAX_VALUE ? "no limit" : FormatUtil.formatBytes(maxMemory)));

		sb.append("\nSwap used: " + FormatUtil.formatBytes(hal.getMemory().getSwapUsed()) + "/"
				+ FormatUtil.formatBytes(hal.getMemory().getSwapTotal())).append('\n');

		// free space on the ApplicationPath's drive
		Path appliPath = Paths.get(Const.getConst().get("ApplicationPath"));
		sb.append("\nFree space on the calculation result folder :  "
				+ FormatUtil.formatBytes(appliPath.toFile().getFreeSpace()));
		sb.append('\n');
		String encodedString = Base64.getEncoder().encodeToString(sb.toString().getBytes());
		ApiJsonResponse res = new ApiJsonResponse("", 0, null, null, encodedString);
		return new ResponseEntity<ApiJsonResponse>(res, HttpStatus.OK);

	}

}