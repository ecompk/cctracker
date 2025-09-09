package com.infusiblecoder.cryptotracker.features.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.infusiblecoder.cryptotracker.databinding.IntroFragmentLayoutBinding

class IntroFragment : Fragment() {
    private var animationRes: Int = 0
    private lateinit var headerTitle: String
    private lateinit var headerSubtitle: String
    private var showbutton: Boolean = false
    private var page: Int = 0

    private var _binding: IntroFragmentLayoutBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TITLE = "title"
        private const val SUB_TITLE = "subtitle"
        private const val ANIMATION = "animation"
        private const val SHOW_BUTTON = "showbutton"
        private const val PAGE = "page"

        fun newInstance(animation: Int, headerTitle: String, headerSubtitle: String, page: Int, showbutton: Boolean): IntroFragment {
            val frag = IntroFragment()
            val b = Bundle()
            b.putInt(ANIMATION, animation)
            b.putString(TITLE, headerTitle)
            b.putString(SUB_TITLE, headerSubtitle)
            b.putBoolean(SHOW_BUTTON, showbutton)
            b.putInt(PAGE, page)
            frag.arguments = b
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            animationRes = it.getInt(ANIMATION)
            headerTitle = it.getString(TITLE) ?: ""
            headerSubtitle = it.getString(SUB_TITLE) ?: ""
            showbutton = it.getBoolean(SHOW_BUTTON)
            page = it.getInt(PAGE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = IntroFragmentLayoutBinding.inflate(inflater, container, false)

        // Set the current page index as the View's tag (useful in the PageTransformer)
        binding.root.tag = page

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            animationView.setAnimation(animationRes)
            if (page == 1) {
                animationView.speed = 1.5f
            }
            tvTitle.text = headerTitle
            tvSubTitle.text = headerSubtitle
            if (showbutton) {
                btnChooseCurrency.visibility = View.VISIBLE
            } else {
                btnChooseCurrency.visibility = View.GONE
            }

            btnChooseCurrency.setOnClickListener {
                (activity as? LaunchActivity)?.openCurrencyPicker()
            }
        }
    }

    fun showLoadingScreen() {
        binding.apply {
            contentGroup.visibility = View.GONE
            btnChooseCurrency.visibility = View.GONE
            pbLoading.visibility = View.VISIBLE
            tvLoading.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
