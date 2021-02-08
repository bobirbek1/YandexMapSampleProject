package com.idrok.yandexmap.ui.saveDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.idrok.yandexmap.R
import com.idrok.yandexmap.database.MyRoomDatabase
import com.idrok.yandexmap.databinding.SaveDialogFragmentBinding
import com.idrok.yandexmap.ui.Variables

class SaveDialogFragment : DialogFragment() {

    private val TAG = "SaveDialogFragment"


    private lateinit var viewModel: SaveDialogViewModel
    private lateinit var binding: SaveDialogFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.save_dialog_fragment, container, false)
        setAllViews()
        return binding.root
    }

    private fun setAllViews() {

        if (dialog != null && dialog?.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }

        Variables.placeModel?.let { place ->
            binding.apply {
                etName.setText(place.displayName)
                btnCancel.setOnClickListener {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("ui", "destroy")
                    findNavController().popBackStack()
                }
                btnSave.setOnClickListener {
                    place.displayName = etName.text.toString()
                    place.time = System.currentTimeMillis()
                    viewModel.insertPLace(place)
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("ui", "destroy")
                    findNavController().popBackStack()
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        findNavController().previousBackStackEntry?.savedStateHandle?.set("ui", "destroy")
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val datasource = MyRoomDatabase.getDatabase(requireContext()).dao()
        val factory = SaveDialogViewModelFactory(datasource)
        viewModel = ViewModelProvider(this, factory).get(SaveDialogViewModel::class.java)
    }

}