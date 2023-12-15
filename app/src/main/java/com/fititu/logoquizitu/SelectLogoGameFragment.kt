package com.fititu.logoquizitu

import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fititu.logoquizitu.Model.AppDatabase
import com.fititu.logoquizitu.Model.Dao.CompanyDao
import com.fititu.logoquizitu.Model.Entity.CompanyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SelectLogoGameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SelectLogoGameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var companyDao : CompanyDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_logo_game, container, false)
        companyDao = AppDatabase.getInstance(requireContext()).companyDao()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SelectLogoGameFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SelectLogoGameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init_game(view)
    }
    fun init_game(view : View){

        // get view components
        val nameText : TextView = view.findViewById(R.id.logo_to_be_guessed)
        val grid : GridLayout = view.findViewById(R.id.selectGrid)

        // choose random logo name
        val randomLogos: List<CompanyEntity> = runBlocking(Dispatchers.IO) {
            companyDao.getRandomLogos()
        }

        val to_be_guessed_idx = (0..7).random()

        // set nameText to the to be guessed logo name - the to_be_guessed_idx-th from list of random logos
        nameText.setText(randomLogos[to_be_guessed_idx].companyName)



        for(i in 0 until grid.childCount){
            var imbtn : ImageButton = grid.getChildAt(i) as ImageButton //view.findViewById<ImageButton>(R.id.imageButton1)

            Glide.with(requireContext())
                    .load(randomLogos[i].imgOriginal)
                    .into(imbtn)

        }
    }
}