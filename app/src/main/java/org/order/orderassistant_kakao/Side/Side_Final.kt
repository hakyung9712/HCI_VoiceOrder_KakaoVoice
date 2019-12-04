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

        /*
        //이전 액티비티에서 값 받아오기
        val intent2 = intent
        first = intent2.extras!!.getString("first")
        menu = intent2.extras!!.getString("menu")
        side=intent2.extras!!.getString("side")
        sfinal_sttResult.setText(first)
        sfinal_sttResult2.setText(menu)
        sfinal_sttResult3.setText(side)

         */


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
            if(Token.side==""){
                ttsClient?.play(Token.menu+" "+Token.first+"주문 완료되었습니다. 감사합니다~")
            }else{
                if(Token.menu==""){
                    ttsClient?.play(Token.side+" "+Token.first+"주문 완료되었습니다. 감사합니다~")
                }else{
                    ttsClient?.play(Token.menu+" "+Token.side+Token.first+"주문 완료되었습니다. 감사합니다~")
                }
            }

            sfinal_sttStart.setOnClickListener {
                var builder = SpeechRecognizerClient.Builder()
                    .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WEB)
                sttClient = builder.build()


                sttClient?.setSpeechRecognizeListener(listener1)
                sttClient?.startRecording(true);
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
                        startActivity(intent)
                        finish()
                    }
/*
                    var txt1="네"
                    var txt2="아니"
                    var txt3="사이드"

                    if (txt1 in texts.toString()||txt3 in texts.toString()) {
                        Toast.makeText(applicationContext, "사이드 주문", Toast.LENGTH_LONG)
                            .show()
                        //val intent =
                        //    Intent(applicationContext, SecondActivity::class.java)
                        intent.putExtra("first", first)
                        intent.putExtra("menu", menu)
                        startActivity(intent)
                        finish()
                    } else if (txt2 in texts.toString()) {
                        Toast.makeText(applicationContext, "주문 끝", Toast.LENGTH_LONG)
                            .show()
                        /*
                        val intent =
                            Intent(applicationContext, SecondActivity::class.java)
                        intent.putExtra("first", first)
                        intent.putExtra("menu", menu)
                        startActivity(intent)
                        finish()
                        */
                        var end="주문이 완료되었어요. 감사합니다"
                        ttsClient?.play(end)
                    }else {
                        var oneMore="한번 더 말해주세요."
                        ttsClient?.play(oneMore)
                    }

                }

 */

                }
            }
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
