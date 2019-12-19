package org.order.orderassistant_kakao.Side

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kakao.sdk.newtoneapi.*
import kotlinx.android.synthetic.main.activity_final.*
import kotlinx.android.synthetic.main.hamburger_final.*
import kotlinx.android.synthetic.main.side_final.*
import org.order.orderassistant_kakao.MainActivity
import org.order.orderassistant_kakao.R
import org.order.orderassistant_kakao.Token


class Side_Final : AppCompatActivity() {
    //TTS
    val TAG = "Kakao"
    var ttsClient : TextToSpeechClient? = null
    val NETWORK_STATE_CODE = 0

    //STT
    var sttClient:SpeechRecognizerClient?=null
    val REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE:Int=0
    var listener1:SpeechRecognizeListener?=null
    var first:String?=null
    var menu:String?=null
    var side:String?=null


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.side_final)
        sf_sttStart.isEnabled=true

        if(Token.side=="") {
            if (Token.menu == "맥치킨") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.chicken_mac))
            } else if (Token.menu == "상하이 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.chicken_shanghai))
            } else if (Token.menu == "빅맥") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_bigmac))
            } else if (Token.menu == "1955 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_1955))
            } else if (Token.menu == "불고기 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_bulgogi))
            } else if (Token.menu == "베이컨토마토디럭스") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_bacon))
            } else if (Token.menu == "치즈버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_cheese))
            } else if (Token.menu == "슈슈 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.shrimp_shushu))
            } else if (Token.menu == "슈비 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.shimp_shubi))
            }
        }else if(Token.menu==""){
            if(Token.side=="후렌치후라이"){
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.side_french))
            }else if(Token.side=="맥너겟"){
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.side_mac))
            }else if(Token.side=="해쉬브라운"){
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.side_hash))
            }else if(Token.side=="치킨텐더"){
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.side_chickentender))
            }
        }else{
            if (Token.menu == "맥치킨") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.chicken_mac))
            } else if (Token.menu == "상하이 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.chicken_shanghai))
            } else if (Token.menu == "빅맥") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_bigmac))
            } else if (Token.menu == "1955 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_1955))
            } else if (Token.menu == "불고기 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_bulgogi))
            } else if (Token.menu == "베이컨토마토디럭스") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_bacon))
            } else if (Token.menu == "치즈버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.meat_cheese))
            } else if (Token.menu == "슈슈 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.shrimp_shushu))
            } else if (Token.menu == "슈비 버거") {
                sf_sttStart.setBackgroundDrawable(resources.getDrawable(R.drawable.shimp_shubi))
            }
        }
        //후렌치후라이, 맥너겟, 해쉬브라운,치킨텐더

        var permission_network = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
        var permission_storage = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        if(permission_network != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission to recode denied")
            //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), NETWORK_STATE_CODE)
        } else {
            //음성인식과 음성합성 두개의 초기화 코드를 다 넣어 줘야 에러가 없다.(뭐 이래)
            SpeechRecognizerManager.getInstance().initializeLibrary(this)
            TextToSpeechManager.getInstance().initializeLibrary(this)


            //TTS 클라이언트 생성
            ttsClient = TextToSpeechClient.Builder()
                .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_2)     // 음성합성방식
                .setSpeechSpeed(1.0)            // 발음 속도(0.5~4.0)
                .setSpeechVoice(TextToSpeechClient.VOICE_WOMAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
                .setListener(object : TextToSpeechListener {
                    //아래 두개의 메소드만 구현해 주면 된다. 음성합성이 종료될 때 호출된다.
                    override fun onFinished() {
                        val intSentSize = ttsClient?.getSentDataSize()      //세션 중에 전송한 데이터 사이즈
                        val intRecvSize = ttsClient?.getReceivedDataSize()  //세션 중에 전송받은 데이터 사이즈

                        val strInacctiveText = "handleFinished() SentSize : $intSentSize  RecvSize : $intRecvSize"

                        Log.i(TAG, strInacctiveText)
                    }

                    override fun onError(code: Int, message: String?) {
                        Log.d(TAG, code.toString())
                    }
                })
                .build()

            /*
            //사이드 메뉴가 없으면 햄버거만 읽어주기
            if(sfinal_sttResult3.toString()==""){
                    ttsClient?.play(sfinal_sttResult2.getText().toString() + " " + sfinal_sttResult.getText().toString() + "주문 완료되었습니다! 감사합니다.")

            }else{
                //햄버거가 없으면 사이드 메뉴만 읽어주기
                if(sfinal_sttResult2.toString()==""){
                    ttsClient?.play(sfinal_sttResult3.getText().toString()+ " " + sfinal_sttResult.getText().toString() + "주문 완료되었습니다! 감사합니다.")
                }else {
                    ttsClient?.play(sfinal_sttResult2.getText().toString() + " " + sfinal_sttResult3.getText().toString() + " " + sfinal_sttResult.getText().toString() + "주문완료되었습니다! 감사합니다.")
                }
            }
             */
            //사이드 X, 햄버거 O
            if(Token.side==""){
                ttsClient?.play(Token.menu+" "+Token.first+"주문 완료되었습니다. 감사합니다~")

            }else{
                //사이드 O, 햄버거 X
                if(Token.menu==""){
                    ttsClient?.play(Token.side+" "+Token.first+"주문 완료되었습니다. 감사합니다~")

                }else{
                    ttsClient?.play(Token.menu+" "+Token.side+Token.first+"주문 완료되었습니다. 감사합니다~")
                }
            }

            sf_sttStart.setOnClickListener {
                var builder = SpeechRecognizerClient.Builder()
                    .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)
                sttClient = builder.build()


                sttClient?.setSpeechRecognizeListener(listener1)
                sttClient?.startRecording(false);
                val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))

                Toast.makeText(this, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();

            }

            listener1 = object : SpeechRecognizeListener {
                override fun onReady() {

                }

                override fun onFinished() {

                }

                override fun onPartialResult(partialResult: String?) {

                }

                override fun onBeginningOfSpeech() {

                }

                override fun onAudioLevel(audioLevel: Float) {

                }

                override fun onEndOfSpeech() {

                }

                override fun onError(errorCode: Int, errorMsg: String?) {

                }

                override fun onResults(results: Bundle?) {
                    val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    var builder: StringBuilder? = null

                    var texts =
                        results?.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS)
                    var confs =
                        results?.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES)


                    var i: Int? = 0

                    Log.d("text", "Result: " + texts);

                    if (texts != null) {
                        for (i in 0 until texts.size) {
                            builder?.append(texts!![i])
                            Log.d("text", texts[i])
                            //builder?.append(" (")
                            //builder?.append(confs.get(i).intValue())
                            //builder?.append(")\n")
                        }
                    }

                    var txt0="다시"
                    var txt1="처음"
                    if (txt0 in texts.toString()||txt1 in texts.toString()) {
                        Toast.makeText(applicationContext, "다시", Toast.LENGTH_LONG)
                            .show()
                        val intent =
                            Intent(applicationContext, MainActivity::class.java)
                        Token.setmenu("")
                        Token.setside("")
                        Token.setfirst("")
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        sf_bt_reset.setOnClickListener{
            val intent =Intent(applicationContext, MainActivity::class.java)
            Token.setmenu("")
            Token.setside("")
            Token.setfirst("")
            startActivity(intent)
            finish()
        }
        sf_bt_back.setOnClickListener{
            val intent =Intent(applicationContext, Sidemenu::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NETWORK_STATE_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        //TextToSpeechManager.getInstance().finalizeLibrary()
        //SpeechRecognizerManager.getInstance().finalizeLibrary();

    }
}
