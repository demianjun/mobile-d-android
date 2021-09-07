package op.gg.joinus.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import op.gg.joinus.R
import op.gg.joinus.databinding.FragmentOnboarding1Binding
import op.gg.joinus.util.joinLog

class OnboardingFragment1 : Fragment() {

    private lateinit var binding: FragmentOnboarding1Binding
    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_onboarding_1, container, false)
        binding.btnConfirm.setOnClickListener {
            (activity as OnboardingActivity).replaceFragment(1)
        }
        val age = resources.getStringArray(R.array.planets_array)
        val adapter = ArrayAdapter(mContext, android.R.layout.simple_spinner_item, age)
        binding.spinnerAge.adapter = adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        joinLog(TAG, "onResume")
    }

    companion object {
        private const val TAG = "OnboardingFragment1"
    }
}