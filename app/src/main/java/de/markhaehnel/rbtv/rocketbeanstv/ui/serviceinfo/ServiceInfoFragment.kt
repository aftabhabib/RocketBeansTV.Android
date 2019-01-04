package de.markhaehnel.rbtv.rocketbeanstv.ui.serviceinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import de.markhaehnel.rbtv.rocketbeanstv.AppExecutors
import de.markhaehnel.rbtv.rocketbeanstv.R
import de.markhaehnel.rbtv.rocketbeanstv.binding.FragmentDataBindingComponent
import de.markhaehnel.rbtv.rocketbeanstv.databinding.FragmentServiceInfoBinding
import de.markhaehnel.rbtv.rocketbeanstv.di.Injectable
import de.markhaehnel.rbtv.rocketbeanstv.ui.common.RetryCallback
import de.markhaehnel.rbtv.rocketbeanstv.util.autoCleared
import kotlinx.android.synthetic.main.fragment_service_info.*
import javax.inject.Inject
import kotlin.math.roundToInt

class ServiceInfoFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentServiceInfoBinding>()

    private lateinit var serviceInfoViewModel: ServiceInfoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dataBinding = DataBindingUtil.inflate<FragmentServiceInfoBinding>(
            inflater,
            R.layout.fragment_service_info,
            container,
            false,
            dataBindingComponent
        )

        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                serviceInfoViewModel.retry()
            }
        }

        binding = dataBinding
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        serviceInfoViewModel = ViewModelProviders.of(this, viewModelFactory).get(ServiceInfoViewModel::class.java)
        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.serviceInfo = serviceInfoViewModel.serviceInfo

        initServiceInfo()
    }

    fun initServiceInfo() {
        serviceInfoViewModel.serviceInfo.observe(viewLifecycleOwner, Observer { serviceInfo ->
            if (serviceInfo.data != null) {
                progressBar.apply {
                    isIndeterminate = false
                    progress = serviceInfo.data.service.streamInfo.showInfo.progress.roundToInt()
                }
            } else {
                progressBar.isIndeterminate = true
            }
        })
    }
}
