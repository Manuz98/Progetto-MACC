package com.example.mobileproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.HospitalModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * A simple [Fragment] subclass.
 * Use the [RecyclerViewRegionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecyclerViewRegionsFragment : Fragment(), RecyclerViewRegionsAdapter.ClickListener {

    private lateinit var adapter: RecyclerViewRegionsAdapter
    private lateinit var regionArrayList: ArrayList<String>

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
        val view= inflater.inflate(R.layout.fragment_recycler_view_regions, container, false)
        (activity as MainActivity).nav_view.setCheckedItem(R.id.nav_news)
        regionArrayList = arrayListOf(
            "Abruzzo",
            "Basilicata",
            "Calabria",
            "Campania",
            "Emilia-Romagna",
            "Friuli Venezia Giulia",
            "Lazio",
            "Liguria",
            "Lombardia",
            "Marche",
            "Molise",
            "Piemonte",
            "Puglia",
            "Sardegna",
            "Sicilia",
            "Toscana",
            "Trentino-Alto Adige",
            "Umbria",
            "Valle d'Aosta",
            "Veneto"
        )
        initRecyclerView(view)
        return view
    }

    private fun initRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerViewRegionsAdapter(regionArrayList,this)
        recyclerView.adapter = adapter
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecyclerViewRegionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecyclerViewRegionsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onItemClick(region: String) {
        Common.currentRegion = region
        Log.d("CURRENT REGION", Common.currentRegion)
        //Da implementare
        /*
        val nextpage = NextPageFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frameLayout, nextpage)
        transaction.commit()
        */
    }
}