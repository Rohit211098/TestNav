package dev.rohitrawat.testnav

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Pair
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.facebook.drawee.backends.pipeline.Fresco
import dev.rohitrawat.testnav.ui.theme.TestNavTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class MainActivity : AddStickerPackActivity() {


    private  val stickerListLiveData  : MutableLiveData<ArrayList<StickerPack>> = MutableLiveData()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestNavTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()

                }
            }
        }

        stickerListLiveData.observe(this,{ stickes ->

            Log.e("APP", "outside coroutine fetching " + stickes[1].trayImageFile)

            addStickerPackToWhatsApp(stickes[0].identifier,stickes[0].name , packageManager);

        })

        Fresco.initialize(this)

        lifecycleScope.launch {
            testingIO(baseContext)
        }
    }


    private  fun testingIO(context: Context)  {
        CoroutineScope(Dispatchers.IO).launch {
            var stickerPackList: ArrayList<StickerPack>
            try {

                stickerPackList = StickerPackLoader.fetchStickerPacks(context)

                stickerListLiveData.postValue(stickerPackList)


                Log.e("APP", "Inside coroutine fetching " + stickerPackList[1].name)


//            if (stickerPackList.size == 0) {
//
//            }
//            for (stickerPack in stickerPackList) {
//                StickerPackValidator.verifyStickerPackValidity(context, stickerPack)
//            }

            } catch (e: Exception) {
                Log.e("EntryActivity", "error fetching sticker packs", e)
                Pair<String?, ArrayList<StickerPack>?>(e.message, null)
            }


        }
    }

}




@Composable
fun MainPage(name: String, modifier: Modifier = Modifier , navController: NavController) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(20.dp)
    ){
        Text(
            text = "Test $name!",
            modifier = modifier
        )
        Button(onClick = {
            navController.navigate(Screens.settingScreen.route)
                         },modifier.padding(top = 20.dp)) {
            Text(text = "Next Screen")
        }
    }

}

@Composable
fun SettingPage(text : String){
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()){
        Text(text = "Setting Page $text")
    }
}

@Composable
fun InfoPage(text : String){
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()){
        Text(text = "Setting Page $text")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestNavTheme {
        //MainPage("Android")
    }
}