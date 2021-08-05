package com.fypmoney.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fypmoney.R;
import com.fypmoney.connectivity.retrofit.Allapi
import com.fypmoney.connectivity.retrofit.ApiClient1
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.model.YourTaskModel
import com.fypmoney.model.yourTaskModal.TaskRequest
import com.fypmoney.model.yourTaskModal.YourTaskResponse
import com.fypmoney.util.SharedPrefUtils
import com.fypmoney.view.adapter.YourTaskStaggeredAdapter
import com.fypmoney.viewhelper.GridItemDecoration
import kotlinx.android.synthetic.main.view_chores.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class YourTaskFragment : Fragment() ,
    AcceptRejectTaskFragment.OnBottomSheetClickListener{
    var test : TextView? =null
    var recyclerViewTasks:RecyclerView ?=null
    companion object {
        fun newInstance() = YourTaskFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_your_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerViewTasks = view.findViewById(R.id.recycler_view)
        recyclerViewTasks!!.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerViewTasks!!.addItemDecoration(GridItemDecoration(10, 2))


    }





    override fun onBottomSheetButtonClick() {

    }


}