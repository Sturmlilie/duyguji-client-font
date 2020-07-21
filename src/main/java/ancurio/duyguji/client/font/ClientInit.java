package ancurio.duyguji.client.font;

import ancurio.duyguji.client.input.api.DuygujiLogger;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientInit implements ClientModInitializer {
    public static final String LOG_NAMESPACE = "duyguji:font";
    public static final Logger LOGGER = LogManager.getLogger(LOG_NAMESPACE);

    public final static DuygujiLogger commonLogger = new DuygujiLogger() {
        public void log(String str, Object ...arg) {
            LOGGER.info("["+ LOG_NAMESPACE+ "] " + str, arg);
        }
    };

    public static void log(String str, Object ...arg) {
        commonLogger.log(str, arg);
    }

    @Override
    public void onInitializeClient() {
    }
}
