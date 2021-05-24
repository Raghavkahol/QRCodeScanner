package com.example.scanner.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.OnRebindCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.scanner.PermissionUtils
import com.example.scanner.R
import com.example.scanner.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val args: HomeFragmentArgs by navArgs()

    @Inject
    lateinit var homeViewModelFactory: HomeViewModelFactory

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModel.provideFactory(homeViewModelFactory, args.sessionData)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home,
            container,
            false
        ).apply {
            viewModel = homeViewModel
            lifecycleOwner = this@HomeFragment

            activity?.let {
                PermissionUtils.checkForPermission(
                    it, arrayOf(
                        Manifest.permission.CAMERA,
                    )
                )
            }

            scanNowButton.setOnClickListener {
                handlePermissionAndLaunchScanner(it)
            }

            stopSession.setOnClickListener {
                handlePermissionAndLaunchScanner(it)
            }

            resetSession.setOnClickListener {
                homeViewModel.resetSession()
            }
        }
        return binding.root
    }

    private fun handlePermissionAndLaunchScanner(view: View) {
        if(checkForCameraPermission()) {
            navigateToScannerScreen(view)
        } else {
            askForCameraPermission()
        }
    }

    private fun checkForCameraPermission(): Boolean {
        return activity?.let { ContextCompat.checkSelfPermission(it,
            Manifest.permission.CAMERA) } == PackageManager.PERMISSION_GRANTED
    }

    private fun navigateToScannerScreen(view :View) {
        val direction = HomeFragmentDirections
            .actionHomeFragmentToScannerFragment()
        view.findNavController().navigate(direction)
    }

    private fun askForCameraPermission() {
        activity?.let { activity ->
            PermissionUtils.checkForPermission(
                activity,  arrayOf(
                    Manifest.permission.CAMERA,
                ))
        }
    }
}