package com.drbrosdev.qrscannerfromlib.ui.gratitude

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentGratitudeBinding
import com.drbrosdev.qrscannerfromlib.util.getColor
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.google.android.material.transition.MaterialSharedAxis

class GratitudeFragment: Fragment(R.layout.fragment_gratitude) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentGratitudeBinding.bind(view)
        updateWindowInsets(binding.root)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        binding.textViewMessage.text = generateGratitudeMessage()

        val statusBarColorAnimator = ValueAnimator.ofArgb(getColor(R.color.background), getColor(R.color.candy_blue))
            .apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener {
                    requireActivity().window.statusBarColor = it.animatedValue as Int
                }
                startDelay = 300
            }

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.circle_reveal_anim)
            .apply {
                duration = 700
                startOffset = 300
                interpolator = AccelerateDecelerateInterpolator()
            }

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) = Unit
            override fun onAnimationEnd(p0: Animation?) {
                binding.constraintLayout.setBackgroundColor(getColor(R.color.candy_blue))
                binding.lottieAnim.playAnimation()
            }

            override fun onAnimationRepeat(p0: Animation?) = Unit
        })

        binding.viewCircle.startAnimation(animation)
            .also { statusBarColorAnimator.start() }

        binding.lottieAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) = Unit
            override fun onAnimationCancel(p0: Animator?) = Unit
            override fun onAnimationRepeat(p0: Animator?) = Unit
            override fun onAnimationEnd(p0: Animator?) {
                binding.buttonGoHome.fadeTo(true)
                binding.textViewMessage.fadeTo(true)
            }
        })

        binding.buttonGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_gratitudeFragment_to_homeFragment)
        }
    }
}
