package com.drbrosdev.qrscannerfromlib.ui.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentInfoBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.infoDevHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.infoItem
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import org.koin.android.ext.android.inject

class InfoFragment: Fragment(R.layout.fragment_info) {
    private val binding by viewBinding(FragmentInfoBinding::bind)
    private val reviewManager: ReviewManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        binding.apply {
            recyclerView.setItemSpacingPx(12)
            recyclerView.withModels {
                infoDevHeader {
                    spanSizeOverride { totalSpanCount, _, _ ->
                        totalSpanCount
                    }
                    id("dev_header")
                    versionText("BitCodept 5.1")
                    onNikolaGhClick { openDevLink(getString(R.string.github_nikola)) }
                    onNikolaLnClick { openDevLink(getString(R.string.linkedin_nikola)) }
                    onOgnjenGhClick { openDevLink(getString(R.string.github_ognjen)) }
                    onOgnjenLnClick { openDevLink(getString(R.string.linkedin_ognjen)) }
                }

                InfoItem.items.forEach { infoItem ->
                    infoItem {
                        id(infoItem.id)
                        infoItem(infoItem)
                        onClick { handleInfoItemClick(infoItem) }
                    }
                }
            }
        }
    }

    private fun openDevLink(link: String) {
        val page = Uri.parse(link)
        startActivity(Intent(Intent.ACTION_VIEW, page))
    }

    private fun handleInfoItemClick(item: InfoItem) {
        when(item) {
            is InfoItem.Bug -> {
                val addresses = arrayOf("devnikoladr@gmail.com")
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, addresses)
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.bug_report))
                }
                startActivity(intent)
            }
            is InfoItem.OpenSourceCode -> {
                exitTransition = Fade()
                reenterTransition = Fade()
                findNavController().navigate(InfoFragmentDirections.toLicensesFragment())
            }
            is InfoItem.PrivacyPolicy -> {
                val page = Uri.parse("https://github.com/nikolaDrljaca/bitcodept/blob/main/privacy.md")
                val intent = Intent(Intent.ACTION_VIEW, page)
                startActivity(intent)
            }
            is InfoItem.Share -> {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
                    type = "text/plain"
                }
                val intent = Intent.createChooser(shareIntent, null)
                startActivity(intent)
            }
            is InfoItem.Star -> {
                val request = reviewManager.requestReviewFlow()
                request.addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        //show something?
                    }
                }
            }
            is InfoItem.Support -> {
//                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
//                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
//                findNavController().navigate(InfoFragmentDirections.toSupportFragment())
                Toast.makeText(requireContext(), "Coming soon.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
