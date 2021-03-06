package org.order.orderassistant_kakao

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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    //TTS
    val TAG = "Kakao"
    var ttsClient : TextToSpeechClient? = null
    val NETWORK_STATE_CODE = 0

    //STT
    var sttClient:SpeechRecognizerClient?=null
    val REQUEST_CODE_AUDIO_AND_WRITE_EXTERNAL_STORAGE:Int=0
    var listener1:SpeechRecognizeListener?=null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    .setSpeechSpeed(0.9)            // 발음 속도(0.5~4.0)
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

                var strText = "어서오세요~ 맥도날드 입니다.왼쪽 위에는 처음으로, 오른쪽위에는 뒤로, 하단에는 말하기 시작 버튼이 항상 있습니다. 하단 버튼을 누르고 매장 또는 포장 여부를 말해주세요."
                ttsClient?.play(strText)


                main_sttStart.setOnClickListener {
                    ttsClient?.stop()
                    val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vib.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))

                    var builder = SpeechRecognizerClient.Builder()
                        .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)
                    sttClient = builder.build()


                    sttClient?.setSpeechRecognizeListener(listener1)
                    sttClient?.startRecording(false)

                    Toast.makeText(this,"음성 인식을 시작합니다.",Toast.LENGTH_LONG).show()
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

                    @SuppressLint("NewApi")
                    override fun onResults(results: Bundle?) {
                        //
                        val vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vib.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))

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
                        var reset="처음"
                        var txt0="다시"
                        var txt1="매장"
                        var txt2="포장"
                        if (txt1 in texts.toString()) {
                            Token.setfirst("매장")
                            Toast.makeText(applicationContext, "매장 주문", Toast.LENGTH_LONG).show()
                            val intent =Intent(applicationContext, SecondActivity::class.java)
                            //intent.putExtra("first", "매장")
                            startActivity(intent)
                            finish()
                        } else if (txt2 in texts.toString()) {
                            Token.setfirst("포장")
                            Toast.makeText(applicationContext, "포장 주문", Toast.LENGTH_LONG).show()
                            val intent =
                                Intent(applicationContext, SecondActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else if (txt0 in texts.toString()||reset in texts.toString()) {
                            Toast.makeText(applicationContext, "다시", Toast.LENGTH_LONG)
                                .show()
                            val intent =
                                Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            var oneMore="한번 더 말해주세요."
                            ttsClient?.play(oneMore)
                        }

                    }
                }

        }

        main_bt_reset.setOnClickListener{
            val intent =
                Intent(applicationContext, MainActivity::class.java)
            Token.setmenu("")
            Token.setside("")
            Token.setfirst("")
            startActivity(intent)
            finish()
        }
        main_bt_back.setOnClickListener{
            val intent =
                Intent(applicationContext, MainActivity::class.java)
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
