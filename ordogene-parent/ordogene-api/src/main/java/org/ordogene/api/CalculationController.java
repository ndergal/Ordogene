package org.ordogene.api;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ordogene.file.Const;
import org.ordogene.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculationController {

	@Autowired
	FileService fs;

	private final Map<Integer, String> currentCalculation = new HashMap<>();
	private final Object token = new Object();

	@RequestMapping(value = "/{id}/calculations")
	@ResponseBody
	public ResponseEntity<String> getUserCalculations(@PathVariable String id) {

		if (id == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not exist");
		}

		boolean exist = fs.userExist(id);

		if (!exist) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not exist");
		} else {
			List<String> calculations = fs.getUserCalculations(id);
			StringBuilder sb = new StringBuilder();
			calculations.forEach(c -> sb.append(c).append('\n'));
			return ResponseEntity.ok().body(sb.toString());
		}
	}

	@RequestMapping(value = "/launchCalculation/{id}")
	@ResponseBody
	public ResponseEntity<String> launchCalculation(@PathVariable String id) {

		if (id == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(id + " does not exist");
		}

		boolean exist = fs.userExist(id);

		if (!exist) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(id + " does not exist");
		}

		int pid = 0;
		try {
			String command = "java -jar " + Const.getConst().get("JarAlgorithmPath");
			Process proc = Runtime.getRuntime().exec(command);
			Field f = proc.getClass().getDeclaredField("pid");
			f.setAccessible(true);
			pid = (int) f.get(proc);
			synchronized (token) {
				currentCalculation.put(pid, id);
			}
			asynchronousDeletePid(proc, pid, id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknow error... Sorry.");
		}
		return ResponseEntity.ok().body("" + pid);
	}

	private void asynchronousDeletePid(Process proc, int pid, String id) {

		Runnable waitAndDeletePid = new Runnable() {

			@Override
			public void run() {
				try {
					// int exitVal = proc.waitFor();
					int exitVal = proc.waitFor();
					synchronized (token) {
						if (currentCalculation.containsKey(pid))
							currentCalculation.remove(pid);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		Thread t = new Thread(waitAndDeletePid);
		t.setDaemon(true);
		t.start();
	}
}