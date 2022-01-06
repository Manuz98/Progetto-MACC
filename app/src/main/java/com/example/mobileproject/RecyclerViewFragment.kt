package com.example.mobileproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.Common.Common
import com.example.mobileproject.Model.HospitalModel
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * A simple [Fragment] subclass.
 * Use the [RecyclerViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecyclerViewFragment : Fragment(), RecyclerViewAdapter.ClickListener {

    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var dbref: FirebaseFirestore
    private lateinit var hospitalArrayList: ArrayList<HospitalModel>


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
        val view = inflater.inflate(R.layout.fragment_recycler_view, container, false)
        (activity as MainActivity).nav_view.setCheckedItem(R.id.nav_book)
        hospitalArrayList = arrayListOf()
        initRecyclerView(view)
        getHospitalData()
        return view
    }

    private fun getHospitalData() {
        dbref = FirebaseFirestore.getInstance()
        dbref.collection("AllHospital").addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                   Log.e("Firebase error", error.message.toString())
                   return
                }
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        var hospital: HospitalModel = dc.document.toObject(HospitalModel::class.java)
                        hospital.hospitalId = dc.document.id
                        hospitalArrayList.add(hospital)
                    }
                }

                adapter.notifyDataSetChanged()
            }

        })

    }

    private fun initRecyclerView(view : View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = RecyclerViewAdapter(hospitalArrayList, this)
        recyclerView.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecyclerViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecyclerViewFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onItemClick(hospitalModel: HospitalModel) {
        Common.currentHospital = hospitalModel
        val recyclerViewTimeFragment = RecyclerViewTimeFragment()
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frameLayout, recyclerViewTimeFragment)
        transaction.commit()
    }
}