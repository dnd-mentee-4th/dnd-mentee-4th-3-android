package com.thisteampl.jackpot.main.viewmore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.thisteampl.jackpot.R
import com.thisteampl.jackpot.main.notice.FieldMoreAdapter
import com.thisteampl.jackpot.main.projectController.ProjectGetElement
import com.thisteampl.jackpot.main.projectController.ProjectPostLatest
import com.thisteampl.jackpot.main.projectController.projectAPI
import com.thisteampl.jackpot.main.userController.userAPI
import kotlinx.android.synthetic.main.activity_field_more.*

import kotlinx.android.synthetic.main.activity_project_view_more.*
import kotlinx.android.synthetic.main.activity_recently_project_view_more.*

class RecentlyProjectViewMore : AppCompatActivity() {

    var projectapi = projectAPI.projectRetrofitService()
    var selection_result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recently_project_view_more)


        recentlyregitstered_backbutton_button.setOnClickListener {
            finish()
        }

        var rankinglist = listOf("최신순","인기순")
        recentlyregitstered_powerspinner.setItems(rankinglist)
        recentlyregitstered_powerspinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            selection_result = newItem

            setprojectListView()
        }

        setprojectListView()
    }


    private fun setprojectListView() {
        val mut = mutableListOf<String>()
        mut.add("")
        var filteringproject = ProjectPostLatest(
            mut, mut, 0, 100, "", "최신순", mut
        )


        if (selection_result.equals("최신순")) {
            filteringproject.sortType = "최신순"
        } else if (selection_result.equals("인기순")) {
            filteringproject.sortType = "인기순"
        }

        

        projectapi?.getProjectContents(filteringproject)
            ?.enqueue(object : retrofit2.Callback<ProjectGetElement> {
                override fun onFailure(
                    call: retrofit2.Call<ProjectGetElement>,
                    t: Throwable
                ) {
                    Log.d("tag : ", "onFailure" + t.localizedMessage)
                }

                override fun onResponse(
                    call: retrofit2.Call<ProjectGetElement>,
                    response: retrofit2.Response<ProjectGetElement>
                ) {
                    if (response.isSuccessful) {
//                        ToastmakeTextPrint("최근 등록된 프로젝트 검색 완료")

                        val adapter = RecentlyProjectViewMoreAdapter(
                            getApplicationContext(), response.body()!!.contents
                        )
                        recentlyregitstered_listview.adapter = adapter

                        Log.d("tag", "크기 : ${adapter.getCount()}")
                    } else {
                        ToastmakeTextPrint("최근 등록된 프로젝트 검색되지 않았습니다.")
                        Log.d("tag", "${response.code().toString()}")
                        Log.d("tag","분야 : ")
                    }

                }

            })


    }


    private fun ToastmakeTextPrint(word: String) {
        Toast.makeText(baseContext, word, Toast.LENGTH_SHORT).show()
    }

}







