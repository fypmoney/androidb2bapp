package com.fypmoney.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.fypmoney.R;
import com.fypmoney.database.entity.TaskEntity
import com.fypmoney.model.YourTaskModel
import com.fypmoney.view.adapter.YourTaskStaggeredAdapter
import com.fypmoney.viewhelper.GridItemDecoration
import com.fypmoney.viewmodel.ChoresViewModel

class YourTaskFragment : Fragment() ,
    AcceptRejectTaskFragment.OnBottomSheetClickListener{

    var taskDetailsData = ObservableField<TaskEntity>()
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
        val recyclerViewMovies = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerViewMovies.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerViewMovies.addItemDecoration(GridItemDecoration(10, 2))

        val movieListAdapter = YourTaskStaggeredAdapter(generateDummyData())
        recyclerViewMovies.adapter = movieListAdapter
        movieListAdapter.onItemClick = { items ->
            // do something with your item
            Log.d("TAG", items.Title+"")
            callAcceptRjectSheet(taskDetailsData.get())
        }
        //movieListAdapter.setMovieList(generateDummyData())
    }


    private fun generateDummyData(): List<YourTaskModel> {
        val listOfMovie = mutableListOf<YourTaskModel>()

        var movieModel = YourTaskModel(1, "Dad", "₹70", "Water the plants")
        listOfMovie.add(movieModel)

        movieModel = YourTaskModel(2, "Mom", "₹110", "Make your bed after getting up Make your bed after getting up Make your bed after getting up")
        listOfMovie.add(movieModel)

        movieModel = YourTaskModel(3, "Brother", "₹50", "Set the dinner table or clear it.")
        listOfMovie.add(movieModel)

        movieModel = YourTaskModel(4, "Sister", "₹40", "Clean bedroom")
        listOfMovie.add(movieModel)

        return listOfMovie
    }

    private fun callAcceptRjectSheet(taskEntity: TaskEntity?) {
        val bottomSheet =
            AcceptRejectTaskFragment(
                taskEntity, this
            )
        bottomSheet.dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.RED))
        activity?.let { bottomSheet.show(it.getSupportFragmentManager(), "AcceptRejectBottomSheet") }
    }

    override fun onBottomSheetButtonClick() {

    }
}