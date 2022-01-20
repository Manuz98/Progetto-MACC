package com.example.mobileproject

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.mobileproject.Common.Common
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_region_info.*
/**
 * A simple [Fragment] subclass.
 * Use the [RegionInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegionInfoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_region_info, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        region_name.text = Common.currentRegion
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
            //proxy to the server
            var proxy : Proxy = Proxy()
            var result= proxy.request()
            val list: List<String> = listOf(*result.split("*").toTypedArray())
            Log.i("POLL", list[0])
            var denominazione_regione =list[0]
            var nuovi_positivi = list[1]
            var dimessi_guariti = list[2]
            var deceduti = list[3]
            var totale_casi = list[4]
            var current_hour=list[5]
            var today=list[6]
            val text=txt_positives
            text.text = nuovi_positivi
            val text1=txt_discharged
            text1.text = dimessi_guariti
            val text2=txt_deceased
            text2.text = deceduti
            val text3=txt_cases
            text3.text = totale_casi
            val text4=last_update
            text4.text = today +" "+current_hour

            Log.i("RESULT", result)
            btn_update_info.setOnClickListener{
                if(txt_positives.text == "n/A"){
                    Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show()
                    var result4= proxy.request()
                    Log.i("CREATE", result4)
                    val list2: List<String> = listOf(*result4.split("*").toTypedArray())
                    Log.i("POLL", list2[0])
                    var denominazione_regione =list2[0]
                    var nuovi_positivi = list2[1]
                    var dimessi_guariti = list2[2]
                    var deceduti = list2[3]
                    var totale_casi = list2[4]
                    var current_hour=list2[5]
                    var today=list2[6]
                    val text=txt_positives
                    text.text = nuovi_positivi
                    val text1=txt_discharged
                    text1.text = dimessi_guariti
                    val text2=txt_deceased
                    text2.text = deceduti
                    val text3=txt_cases
                    text3.text = totale_casi
                    val text4=last_update
                    text4.text = today +" "+current_hour
                }
                else{
                    Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show()
                    var result2= proxy.update()
                    Log.i("UPDATE", result2)
                    val list: List<String> = listOf(*result2.split("*").toTypedArray())
                    Log.i("POLL", list[0])
                    var denominazione_regione =list[0]
                    var nuovi_positivi = list[1]
                    var dimessi_guariti = list[2]
                    var deceduti = list[3]
                    var totale_casi = list[4]
                    var current_hour=list[5]
                    var today=list[6]
                    val text=txt_positives
                    text.text = nuovi_positivi
                    val text1=txt_discharged
                    text1.text = dimessi_guariti
                    val text2=txt_deceased
                    text2.text = deceduti
                    val text3=txt_cases
                    text3.text = totale_casi
                    val text4=last_update
                    text4.text = today +" "+current_hour
                }
            }

            btn_delete_info.setOnClickListener{
                Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show()
                var result3= proxy.delete()
                Log.i("DELETE", result3)
                val text=txt_positives
                text.text = "n/A"
                val text1=txt_discharged
                text1.text = "n/A"
                val text2=txt_deceased
                text2.text = "n/A"
                val text3=txt_cases
                text3.text = "n/A"
                val text4=last_update
                text4.text = "n/A"
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegionInfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegionInfoFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}