package android.usuario.driverrating.GPS;

import android.os.AsyncTask;
import android.usuario.driverrating.IniciarClassificacao;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jorge on 09/03/2017.
 */

public class OverpassReader {
    private IOverpassReader listener;
    private XmlPullParserFactory xmlPullParserFactory;
    private XmlPullParser parser;
    private OverpassInfo overpassInfo;


    public OverpassReader(IniciarClassificacao listener) {
        this.listener=listener;
        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(false);
            parser = xmlPullParserFactory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        overpassInfo=new OverpassInfo();
    }
    public void read(double latitude, double longitude, double buffer) {
        // compute the bounding box
        double lat1,lon1,lat2,lon2;
        double earthRadius=6371000;
        double radiusOnLatitude = Math.cos(Math.toRadians(latitude)) * earthRadius;
        double deltaLatitude = buffer / earthRadius;
        double deltaLongitude = buffer / radiusOnLatitude;

        lat1=latitude-Math.toDegrees(deltaLatitude);
        lon1=longitude-Math.toDegrees(deltaLongitude);
        lat2=latitude+Math.toDegrees(deltaLatitude);
        lon2=longitude+Math.toDegrees(deltaLongitude);

        String xmlPath = "http://www.overpass-api.de/api/xapi?*[maxspeed=*][bbox="+lon1+","+lat1+","+lon2+","+lat2+"]";
        BackgroundAsyncTask backgroundAsyncTask = new BackgroundAsyncTask();
        backgroundAsyncTask.execute(xmlPath);
    }

    private class BackgroundAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String ...params) {
            URL url = null;
            String returnedResult = "";
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                parser.setInput(is, null);
                returnedResult = getLoadedXmlValues(parser);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            listener.overpassupdate(overpassInfo);
        }
        private String getLoadedXmlValues(XmlPullParser parser) throws XmlPullParserException, IOException {
            int eventType = parser.getEventType();
            String name = null;
            Entity mEntity = new Entity();
            while (eventType != parser.END_DOCUMENT){
                if(eventType == parser.START_TAG ){
                    name = parser.getName();
                    if (name.equals("tag")) {
                        if (parser.getAttributeValue(0).equals("highway")) {
                            mEntity.highway = parser.getAttributeValue(1);
                            overpassInfo.setHighway(mEntity.highway);
                        }
                        if (parser.getAttributeValue(0).equals("lanes")) {
                            mEntity.lanes = parser.getAttributeValue(1);
                            overpassInfo.setLanes(mEntity.lanes);
                        }
                        if (parser.getAttributeValue(0).equals("maxspeed")) {
                            mEntity.maxspeed = parser.getAttributeValue(1);
                            overpassInfo.setMaxspeed(mEntity.maxspeed);
                        }
                        if (parser.getAttributeValue(0).equals("name")) {
                            mEntity.name = parser.getAttributeValue(1);
                            overpassInfo.setName(mEntity.name);
                        }
                        if (parser.getAttributeValue(0).equals("surface")) {
                            mEntity.surface = parser.getAttributeValue(1);
                            overpassInfo.setSurface(mEntity.surface);
                        }
                    }
                }
                try {
                    eventType = parser.next();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "highway:"+mEntity.highway + "\n" +
                    "lanes:"+mEntity.lanes + "\n" +
                    "maxspeed:"+mEntity.maxspeed + "\n" +
                    "name:"+mEntity.name + "\n" +
                    "surface:"+mEntity.surface + "\n";

        }
        public class Entity{
            public String highway;
            public String lanes;
            public String maxspeed;
            public String name;
            public String surface;
        }
    }

}
