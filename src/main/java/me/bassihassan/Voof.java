package me.bassihassan;

import me.bassihassan.exception.VoofException;
import me.bassihassan.core.Callback;
import me.bassihassan.core.Unmarshaller;
import me.bassihassan.option.Options;
import org.apache.log4j.Logger;
import me.bassihassan.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class Voof {
    private static Logger logger = Logger.getLogger(Voof.class);

    public static <T> void fromFile(final String uri, Class<T> type, Callback<T> callback) {
        try {
            logger.info("start invoking fromFile(...) {}");
            Unmarshaller unmarshaller = init(uri, new Options());
            unmarshaller.unmarshaller(type, callback);
        } catch (IOException e) {
            logger.info("IOException => {}", e);
        }
    }

    public static <T> Collection<T> fromFile(final String uri, Class<T> type) {
        try {
            logger.info("start invoking fromFile(...) {}");
            Unmarshaller unmarshaller = init(uri, new Options());
            return unmarshaller.unmarshaller(type);
        } catch (IOException e) {
            throw new VoofException(e);
        }
    }

    private static Unmarshaller init(final String uri, final Options options) throws IOException {
        InputStream inputStream = Utils.uriToInputStream(uri);
        String extension = Utils.getExtention(uri);
        return new Unmarshaller(WorkbookFactory.constructWorkbook(extension, inputStream), options);
    }

}
