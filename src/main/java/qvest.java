import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.*;
import com.google.gson.reflect.*;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

public class qvest {

    public static Map<String,Object> jsonToMap(String str){
        Map<String,Object> map = new Gson().fromJson(str,new
                TypeToken<HashMap<String,Object>> () {}.getType());
        return map;
    }


    public static void  main(String[] args) {
        //версия компилятора 9
        String API_KEY = "17d895a616d62449fa5959bcede0c3c0";
        String LOCATION = "Irkutsk,RU";
        String Lat="52.2978";
        String Lon="104.30";
        String urlTest="https://api.openweathermap.org/data/2.5/onecall?lat="+Lat+"&lon="+Lon+"&exclude=current,minutely,hourly&units=metric&appid="+API_KEY;
        try{

            StringBuilder result = new StringBuilder();
            URL url = new URL(urlTest);
            URLConnection conn = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader (conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null){
                result.append(line);
            }
            rd.close();
            Map<String, Object > respMap = jsonToMap (result.toString());
            String data=respMap.get("daily").toString();
            String y=data.replaceFirst("\\[","");
            String[] daylyData= y.split("\\{dt=");

            ArrayList<String> FiveDays=new ArrayList();
            for (int i = 1 ;i <6; i++) {
                FiveDays.add(daylyData[i]);
            }

            BigDecimal lowestDifrentTemperature=new BigDecimal("100");
            BigDecimal nighTempr;
            BigDecimal mornTempr;
            BigDecimal promTemp=new BigDecimal("0");
            int dayNumber=0;

            String prom="";
            double promPressure;
            double maxPressure=0;
            for (int i = 0; i < FiveDays.size(); i++) {
                prom=FiveDays.get(i);
                promPressure=Double.parseDouble(
                        StringUtils.substringBetween(prom, "pressure=", ","));
                if (maxPressure< promPressure)
                    maxPressure=promPressure;
                nighTempr=new BigDecimal(
                        StringUtils.substringBetween(prom, "night=", ","));
                mornTempr=new BigDecimal(
                        StringUtils.substringBetween(prom, "morn=", "}"));

                if (mornTempr.compareTo(nighTempr)==1)
                    promTemp=mornTempr.subtract(nighTempr);
                else
                    promTemp=nighTempr.subtract(mornTempr);

                if (promTemp.compareTo(lowestDifrentTemperature)==-1){
                    lowestDifrentTemperature=promTemp;
                    dayNumber=i;
                }
            }
            Date date = new Date();
            DateTime dt = new DateTime(date);
            dt = dt.plusDays(dayNumber);
            date =dt.toDate();
            System.out.println("максимальное давление в течении 5 дней = "+maxPressure);
            System.out.printf("%1$s %2$td %2$tB %2$tY разница температур  = "+lowestDifrentTemperature, "Дата:", date);


        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
}
