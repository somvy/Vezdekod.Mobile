package com.travels.searchtravels

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.travels.searchtravels.activity.MainActivity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.google.api.services.vision.v1.model.LatLng
import com.preview.planner.prefs.AppPreferences
import com.travels.searchtravels.api.OnVisionApiListener
import com.travels.searchtravels.api.VisionApi
import org.junit.Assert
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

import org.junit.Test
import org.junit.runner.RunWith

//Я подготовил массив, состоящий из списков следующей структуры: ( URL Изображения, широта города, долгота города, целевой тип местности ({sea|ocean|beach|mountain|snow|other}))
//Картинки подаются в модуль обработки изображний (visionApi), а затем вывод сравнимается с истинным
//
/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    //@Test
    fun createActivity() {
        val scenario = launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }

    @Test
    fun testPictures() {
        val pics = arrayOf<Array<String>>(
            arrayOf("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse2.mm.bing.net%2Fth%3Fid%3DOIP.XK_7VR1qhumqTsiGQj_5YQHaE7%26pid%3DApi&f=1", "-22.903150", "-43.189903", "sea"),//Rio-de-Jan //"urlPic", "cityLat", "cityLng", "type{sea|ocean|beach|mountain|snow|other}"
            arrayOf("http://www.nexotur.com/fotos/1/TravelRepublic_Tenerife.jpg", "28.459997", "-16.249661", "beach"), //Tenerife
            arrayOf("https://www.wallpapers.net/web/wallpapers/maldivian-beach/thumbnail/lg.jpg","26.692957", "-80.036595", "beach"), //Palm-Beach
            arrayOf("https://sun9-32.userapi.com/c857624/v857624575/e3c07/cnHlHD9hcXs.jpg","15.348908", "74.101094", "beach" ),//Goa
            arrayOf("https://alltur.ru/wp-content/uploads/2018/01/tury-v-puerto-plata.jpg", "19.789829", "-70.690038", "beach"), //Puerto-plata
            arrayOf("https://cdn.pixabay.com/photo/2016/10/24/13/07/greece-1766013__480.jpg", "40.632716", "22.941445", "other"), //Saloniki
            arrayOf("https://i.pinimg.com/736x/64/b4/79/64b47979e2fb8afe7a72b9803f5f45a6.jpg", "25.792235", "-80.250852", "ocean"),//Miami
            arrayOf("https://www.eurolux-rostov.ru/wp-content/uploads/2020/04/6753456345663.jpg", "37.975527", "23.734855", "other"), //Athens
            arrayOf("https://terra-z.com/wp-content/uploads/2015/11/00117-533x400.jpg", "45.918249", "6.866327", "mountain"), //shamoni
            arrayOf("http://www.russian-wave.ru/wp-content/uploads/2017/12/Zimnij_vecher_v_Zeldene_Tirol_Avstriya_4876.jpg", "46.965575", "11.007408", "snow"), //zelden
            arrayOf("https://s3.amazonaws.com/lmpm.wordpress-media-store/wp-content/uploads/sites/2/2016/09/21153037/evolution-whistler-hotel.jpg", "50.120630", "-122.955099","mountains"), //wisler
            arrayOf("https://sochinews.io/wp-content/uploads/2019/03/1-152.jpg", "43.585525", "39.723062", "sea"),//sochi
            arrayOf("https://sun9-39.userapi.com/c852216/v852216106/1e8cb5/nrAPuMzhCcA.jpg","43.585525", "39.723062", "mountains"), //sochi
            arrayOf("www.destination360.com/europe/france/images/s/eiffel-tower.jpg", "48.858239", "2.294585", "other") //Eiffel tower
        )
        for (cityArray in pics) {
            val bitmap = getBitmapFromURL(cityArray[0])
            VisionApi.findLocation(
                bitmap,
                AppPreferences.getToken(getApplicationContext()),
                object : OnVisionApiListener {
                    override fun onSuccess(latLng: LatLng) {
                        val targetLatLng = LatLng()
                        targetLatLng.latitude = cityArray[1].toDouble()
                        targetLatLng.longitude = cityArray[2].toDouble()
                        Assert.assertTrue(latLng == targetLatLng)
                    }

                    override fun onErrorPlace(category: String) {
                        Assert.assertEquals(category, cityArray[3])
                    }

                    override fun onError() {
                        Assert.fail("Приложение неверно распознало! Правильный ответ: категория ${cityArray[3]}")
                    }
                })


        }
    }


    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            // Log exception
            null
        }
    }

}