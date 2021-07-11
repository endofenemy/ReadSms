package com.test.readsms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.test.readsms.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val READ_SMS_REQUEST_CODE: Int = 101
    private var requestCount: Int = 0

    private val readSmsViewModel: ReadSmsViewModel by viewModels {
        ReadSmsModelFactory(ReadSmsRepository(activity?.contentResolver!!))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        with(binding) {
            viewModel = readSmsViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readSmsViewModel.phoneError.observe(viewLifecycleOwner, {
            binding.phoneTIL.error = it
        })
        readSmsViewModel.daysError.observe(viewLifecycleOwner, {
            binding.daysTIL.error = it
        })

        readSmsViewModel.buttonClick.observe(viewLifecycleOwner, {
            binding.phoneTIL.error = ""
            binding.daysTIL.error = ""
            checkRequestPermissions()
        })

        readSmsViewModel.smsList.observe(viewLifecycleOwner, {
            if (it.size > 0) {
                binding.result.text = "Number of messages found is ${it.size}"
            } else {
                binding.result.text = "Sorry, No messages found"
            }
        })
    }

    private fun checkRequestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_SMS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            readSmsViewModel.permissionGranted()
        } else {
            permissionsNotGranted()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_SMS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission Allowed
                readSmsViewModel.permissionGranted()
            } else {
                permissionsNotGranted()
            }
        }
    }

    private fun permissionsNotGranted() {
        if (requestCount < 2) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_SMS), READ_SMS_REQUEST_CODE
            )
            ++requestCount
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_SMS), READ_SMS_REQUEST_CODE
                )
            } else {
                // Permission is required.
                val snackbar = Snackbar.make(
                    requireView(),
                    "Read SMS Permission Required",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction("Go To Settings") {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    requireActivity().startActivity(intent)
                }
                snackbar.show()
            }
        }
    }


}
