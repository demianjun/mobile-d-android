package op.gg.joinus.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import op.gg.joinus.R
import op.gg.joinus.databinding.DialogCheckMatchingBinding
import op.gg.joinus.databinding.FragmentHomeBinding
import op.gg.joinus.model.RoomCreate
import op.gg.joinus.model.RoomInfo
import op.gg.joinus.model.RoomStartDate
import op.gg.joinus.model.UserInfo
import op.gg.joinus.network.RetrofitClient
import op.gg.joinus.util.joinLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var roomListAdapter:HomeRoomListAdapter

    //private val roomList:MutableList<String> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)

        initView()
        initListener()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStart() {
        setToolbar()
        super.onStart()
    }

    override fun onStop(){
        returnToolbar()
        super.onStop()
    }

    private fun returnToolbar(){
        val toolbar = (activity as MainActivity).getBinding().toolbarMain
        toolbar.navigationIcon = null
        toolbar.setNavigationOnClickListener {  }
        toolbar.menu.clear()
        (activity as MainActivity).getBinding().toolbarMainTitle.text =""
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setToolbar(){
        val toolbar = (activity as MainActivity).getBinding().toolbarMain
        toolbar.inflateMenu(R.menu.menu_home)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.item_home_filter-> {
                    val homFilterFragment = HomeFilterFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main,homFilterFragment)
                        .addToBackStack(null)
                        .commit()
                }
                R.id.item_home_search->{

                }
            }
            return@setOnMenuItemClickListener true
        }
    }



    private fun initView(){
        //set RecyclerView LayoutManger
        val layoutManager = LinearLayoutManager(context)
        binding.rcHomeMatching.layoutManager = layoutManager

        //set RecyclerView Adapter
        roomListAdapter = HomeRoomListAdapter(requireContext())
        binding.rcHomeMatching.adapter = roomListAdapter

        //set layoutNoMatchingRoom
        if (roomListAdapter.itemCount != 0){
            binding.layoutNoMatchingRoom.visibility = View.GONE
        }

        //set List
        getRoomList()
    }

    private fun initListener(){
        //set Adapter click listener
        roomListAdapter.setOnItemClickListner(object:HomeRoomListAdapter.OnItemClickListener{
            override fun onItemClick(item: HomeRoomListItem) {
                val roomJoinFragment = RoomJoinFragment(item)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main,roomJoinFragment)
                    .addToBackStack(null)
                    .commit()
            }
        })
        //set swipeRefreshLayout listener
        binding.srlHomeMatching.setOnRefreshListener {
            getRoomList()
        }
        //set floating add button listener
        binding.fabAddMatching.setOnClickListener{
            val addMatchingFragment = AddMatchingFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main,addMatchingFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    //get RoomList from server
    private fun getRoomList(){
        binding.srlHomeMatching.isRefreshing = true
        val retrofit = RetrofitClient.getInstance()
        val api = retrofit.buildRetrofit()
        val callGetRoomList = api.getRoom()
        callGetRoomList.enqueue(object: Callback<List<RoomInfo>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<RoomInfo>>, response: Response<List<RoomInfo>>) {
                if(!response.isSuccessful){
                    joinLog("response err",response.body().toString())
                    binding.srlHomeMatching.isRefreshing = false
                }
                joinLog("response",response.body().toString())
                val tempList = mutableListOf<HomeRoomListItem>()
                for (i in response.body()!!){
                    tempList.add(HomeRoomListItem(i))
                }
                roomListAdapter.itemList = tempList.toList()
                roomListAdapter.notifyDataSetChanged()
                if (roomListAdapter.itemCount != 0){
                    binding.layoutNoMatchingRoom.visibility = View.GONE
                }
                else{
                    binding.layoutNoMatchingRoom.visibility = View.VISIBLE
                }
                binding.srlHomeMatching.isRefreshing = false
            }
            override fun onFailure(call: Call<List<RoomInfo>>, t: Throwable) {
                joinLog("response fail",t.toString())
                binding.srlHomeMatching.isRefreshing = false
            }
        })
    }

}