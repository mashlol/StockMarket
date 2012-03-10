package start;
 
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
 
public class StockMarket extends JavaPlugin {
 
	private StockMarketCommandExecutor myExecutor;
	
	Logger log = Logger.getLogger("Minecraft"); //Define your logger
	StockMarketEventThread s;
	
	public void onDisable() {
		//log.info("Disabled stock market.");
		s.finish();
	}

	public void onEnable() {
		myExecutor = new StockMarketCommandExecutor(this);
		getCommand("sm").setExecutor(myExecutor);
		
		// FETCH FROM MYSQL THE LOOPTIME v
		int loopTime = 0;
		
		s = new StockMarketEventThread(loopTime);
		s.start();
	}
}