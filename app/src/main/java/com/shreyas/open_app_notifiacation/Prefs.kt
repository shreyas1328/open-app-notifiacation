package com.shreyas.open_app_notifiacation

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import kotlin.collections.ArrayList

class Prefs {
    companion object {

        val HOME = "com.huawei.android.launcher"
        val NULL = "null"

        val APPNAME = "MYAPPLICTION"
        val APP_KEY = "MYKEYING"
        val APP_KEY_NEW = "MYKEYINGNEW"
        val MODE = 0

        fun setAppValue(context: Context, app:String) {
            val prefs = context.getSharedPreferences(APPNAME,  MODE)
            val edit = prefs.edit()
            edit.putString(APP_KEY, app)
            edit.apply()
        }

        fun getAppValue(context: Context) : String {
            return context.getSharedPreferences(APPNAME, MODE).getString(APP_KEY, "null")!!
        }

        fun setNEWAppValue(context: Context, app:String) {
            val prefs = context.getSharedPreferences(APPNAME,  MODE)
            val edit = prefs.edit()
            edit.putString(APP_KEY_NEW, app)
            edit.apply()
        }

        fun getNEWAppValueLive(context: Context) : String {
            return context.getSharedPreferences(APPNAME, MODE).getString(APP_KEY_NEW, "null")!!
        }


            val exceptions = arrayListOf<String>("launcher", "android", "home")

        fun checkList(checking:String, array: ArrayList<String>): Boolean {
            for (item in array) {
                Log.d("test_999", "aasd: "+item+"   "+checking)
                if (checking.contains(item)){
                    return false
                }
            }
            return true
        }

        fun reset(context: Context, condition:String) {
            if (condition.contains(exceptions.get(0))) {
                setNEWAppValue(context, "null")
            }
        }

        fun showFlashMsg(context: Context, layout:Int, width:Int, height:Int, viewCode:Boolean) {

            try {
            val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val inflater =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(layout, null)
            val params: WindowManager.LayoutParams
            setView(view, wm, context, viewCode)
            if (Build.VERSION.SDK_INT >= 26) {
                params = WindowManager.LayoutParams(
                    width,
                    height,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
            } else {
                params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )
            }
            params.gravity = Gravity.CENTER or Gravity.CENTER
            params.x = 0
            params.y = 0
            Log.d("ttt", "Sad: " + params.gravity)
            wm.addView(view, params)


            }catch (e:Exception) {
                Log.d("work_123","Exception: "+e.message)
            }
        }

        private fun setView(
            view: View?,
            wm: WindowManager,
            context: Context,
            viewCode: Boolean
        ) {

            Log.d("work_123", "viewCode:   "+viewCode)
            if (viewCode) {
                val mBtnDismiss = view?.findViewById<Button>(R.id.btn_dismiss)

                mBtnDismiss?.setOnClickListener(View.OnClickListener {
                    wm.removeView(view)
                })

            }else {
                val imageSrc = arrayOf(R.drawable.spy_1, R.drawable.spy_2, R.drawable.spy_3).random()
                val mIvImage = view?.findViewById<ImageView>(R.id.iv_image)
                val mIvRetry = view?.findViewById<ImageView>(R.id.iv_retry)
                mIvImage!!.setImageDrawable(ContextCompat.getDrawable(context, imageSrc))
                val mIvClose = view?.findViewById<ImageView>(R.id.iv_close)
                mIvClose!!.setOnClickListener {
                    wm.removeView(view)
                }

                mIvRetry!!.setOnClickListener {
                    wm.removeView(view)
                    reShedule(context)
                }
            }



        }

        private fun reShedule(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("MYTASK")
            BReciver.scheduleTask(context)
        }
    }
}