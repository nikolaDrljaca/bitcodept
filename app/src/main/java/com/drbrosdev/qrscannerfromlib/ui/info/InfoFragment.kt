package com.drbrosdev.qrscannerfromlib.ui.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import com.drbrosdev.qrscannerfromlib.BuildConfig
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentInfoBinding
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.google.android.material.transition.MaterialSharedAxis

class InfoFragment: Fragment(R.layout.fragment_info) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentInfoBinding.bind(view)

        updateWindowInsets(binding.root)

        binding.apply {
            val text = BuildConfig.VERSION_NAME
            tvVersion.text = "Version $text"

            ibGitNikola.setOnClickListener {
                val page = Uri.parse(getString(R.string.github_nikola))
                startActivity(Intent(Intent.ACTION_VIEW, page))
            }

            ibGitOgnjen.setOnClickListener {
                val page = Uri.parse(getString(R.string.github_ognjen))
                startActivity(Intent(Intent.ACTION_VIEW, page))
            }

            tvRateApp.setOnClickListener {
                val page = Uri.parse("https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                val intent = Intent(Intent.ACTION_VIEW, page)
                startActivity(intent)
            }

            tvPrivacy.setOnClickListener {
                val page = Uri.parse("https://nikoladrljaca.github.io/bitcodept/privacy-policy")
                val intent = Intent(Intent.ACTION_VIEW, page)
                startActivity(intent)
            }

            tvReportBug.setOnClickListener {
                val addresses = arrayOf("devnikoladr@gmail.com")
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, addresses)
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report))
                }
                startActivity(intent)
            }

            tvSupport.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                findNavController().navigate(R.id.action_infoFragment_to_supportFragment)
            }

            tvShare.setOnClickListener {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)
            }

            tvLicenses.setOnClickListener {
                findNavController().navigate(R.id.action_infoFragment_to_licensesFragment)
                reenterTransition = Fade()
            }

            imageViewAppIcon.apply {
                val shape = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(20f)
                    .build()
                shapeAppearanceModel = shape
            }
        }
    }
}