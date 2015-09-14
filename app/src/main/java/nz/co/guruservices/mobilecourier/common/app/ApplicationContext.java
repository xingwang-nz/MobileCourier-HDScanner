package nz.co.guruservices.mobilecourier.common.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import nz.co.guruservices.mobilecourier.R;
import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class ApplicationContext
extends Application {

    private Properties properties;

    private String restBaseUrl;

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initProperties();
    }

    private void initProperties() {
        InputStream rawResource = null;
        try {
            rawResource = getResources().openRawResource(R.raw.app);
            properties = new Properties();
            properties.load(rawResource);
            restBaseUrl = properties.getProperty("rest.base.url");
        } catch (final Exception e) {
            Log.e("Application", "read propeties file failed " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (rawResource != null) {
                try {
                    rawResource.close();
                } catch (final IOException e) {
                }
            }
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * constructs full REST url by concatenate base url and sub rest resource path
     * 
     * @param subResourcePath
     * @return
     */
    public String constructRestUrl(String subResourcePath) {
        return restBaseUrl + "/" + subResourcePath;
    }
}
