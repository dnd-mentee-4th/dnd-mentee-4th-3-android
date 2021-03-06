package com.thisteampl.jackpot.main.filtering

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.thisteampl.jackpot.R
import com.thisteampl.jackpot.main.projectController.ProjectGetElement
import com.thisteampl.jackpot.main.projectController.ProjectPostLatest
import com.thisteampl.jackpot.main.projectController.projectAPI
import com.thisteampl.jackpot.main.userController.CheckProfile
import com.thisteampl.jackpot.main.userController.UserRelatedFilteringGet
import com.thisteampl.jackpot.main.userController.UserRelatedFilteringPost
import com.thisteampl.jackpot.main.userController.userAPI
import com.thisteampl.jackpot.main.viewmore.RecentlyProjectViewMore
import kotlinx.android.synthetic.main.activity_filtering_search.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile_edit_change_emoji.view.*
import kotlinx.android.synthetic.main.activity_project_creation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilteringSearch : AppCompatActivity() {


    // 프로젝트 찾기
    // 1) 기술 스택
    private val projectfind_stackToolTechnologyStack = ArrayList<String>()

    // 2) 프로젝트 방식
    private var projectfind_onoffbtn = arrayOfNulls<Button>(2)
    private var projectfind_onofftext = "onoff"

    // 3) 지역
    private var projectfind_regiontext = "지역" // 지역 list 저장용

    // 4) 기간
    private var projectfind_durationbtn = ArrayList<String>()

    // 5) 관심분야
    private var projectfind_projectfieldbtn =ArrayList<String>()


    // 멤버찾기
    // 1) 포지션
    private val memberfind_selectpositionItems = ArrayList<String>()    // 포지션

    // 2) 개발언어

    private val stackTooldeveloper = ArrayList<String>() // 개발자 스택
    private val stackTooldesigner = ArrayList<String>()  // 디자이너 스택

    // 3) 프로젝트 방식
    private var memberfind_onoffbtn = arrayOfNulls<Button>(2)
    private var memberfind_onofftext = "onoff"

    // 4) 지역
    private var memberfind_regiontext = "지역" // 지역 list 저장용


    private var page: Int = 1
    var regions = listOf(
        "서울", "경기", "인천", "대전", "광주", "울산", "세종", "대구", "부산", "강원도",
        "충청북도", "충청남도", "전라북도", "전라남도", "경상남도", "경상북도", "제주도",
        "해외"
    )


    private var userapi = userAPI.create()
    private var projectapi = projectAPI.projectRetrofitService()

    // 사용자 포지션
    var user = ""


    var developer_btn:Boolean = false
    var designer_btn:Boolean = false
    var planner_btn:Boolean = false

    // 가독성 떨어짐
    // 개발자 툴 name
    var developercheck_index:Int = 0
    // 디자이너 툴 name
    var designercheck_index:Int = 0
    


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_search)

        filtersearch_minusbutton_button.setOnClickListener {
            finish()
        }

        // user가 개발자일 때, 디자이너일 때 (추 후 코드 변경되었으면)
        user = intent.getStringExtra("position")!!

        // 개발자일 때 개발자 툴만, 디자이너 일 때 디자이너 툴만
        if(user == "개발자" || user == "기획자"){
            filtersearch_projectstack_constraintLayout.visibility = View.VISIBLE
            filtersearch_projectstackdesigner_constraintLayout.visibility = View.GONE
        }else if(user == "디자이너"){
            filtersearch_projectstack_constraintLayout.visibility = View.GONE
            filtersearch_projectstackdesigner_constraintLayout.visibility = View.VISIBLE
        }else {
            ToastmakeTextPrint("로그인 정보가 없습니다.")
            finish()
        }

        // 첫 시작할 때
        findproject()
        searchfindbtn()

    }

    // 프로젝트 찾기, 멤버 찾기 버튼
    private fun searchfindbtn() {
        filtersearch_findproject_textview.setOnClickListener {
            filtersearch_write_recruitment_article_constraintlayout.visibility = View.VISIBLE
            filtersearch_page2_write_recruitment_article_constraintlayout.visibility = View.GONE
            filtersearch_findproject_textview.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorbrightly))
            filtersearch_findmember_textview.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colordarkly))

            // page 1
            page = 1
            findproject()
        }

        filtersearch_findmember_textview.setOnClickListener {

            filtersearch_write_recruitment_article_constraintlayout.visibility = View.GONE
            filtersearch_page2_write_recruitment_article_constraintlayout.visibility = View.VISIBLE
            filtersearch_findproject_textview.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colordarkly))
            filtersearch_findmember_textview.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorbrightly))

            // page 2
            page = 2
            findmember()
        }

        filtersearch_reset_button.setOnClickListener {

            if (page == 1) {
                onClearConstraintLayout(filtersearch_projectstackbutton_constraintLayout)
                onClearConstraintLayout(filtersearch_projectstackdesignerbutton_constraintLayout)
                onClearLinearLayout(filtersearch_projectonoff_linearlayout)
                onClearLinearLayout(filtersearch_month_linearlayout)
                onClearLinearLayout(filtersearch_field_linearlayout)
                onClearLinearLayout(filtersearch_field2_linearlayout)
                ToastmakeTextPrint("필터 초기화")
            }else if(page == 2){
                onClearConstraintLayout(filtersearch_page2_projectstackbutton_constraintLayout)
                onClearConstraintLayout(filtersearch_page2_projectstackdesignerbutton_constraintLayout)
                onClearLinearLayout(filtersearch_page2_position_linearlayout)
                onClearLinearLayout(filtersearch_page2_projectonoff_linearlayout)

            }


        }

        filtersearch_applyfilter_button.setOnClickListener {
            if (checkPageButton()) {
                val memberfind_stackToolTechnologyStack = ArrayList<String>() // 개발자, 디자이너 스택 통합

                val intent = Intent(this, FilteringSearchResults::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                if(page == 1){

                    for(i in 0..projectfind_stackToolTechnologyStack.size-1){
                        if(projectfind_stackToolTechnologyStack[i] == "Html/CSS"){
                            projectfind_stackToolTechnologyStack[i] = "Html_CSS"
                        }
                        if(projectfind_stackToolTechnologyStack[i] == "React.JS"){
                            projectfind_stackToolTechnologyStack[i] = "React_js"
                        }
                        if(projectfind_stackToolTechnologyStack[i] == "After Effects"){
                            projectfind_stackToolTechnologyStack[i] = "After_Effects"
                        }
                        if(projectfind_stackToolTechnologyStack[i] == "C++"){
                            projectfind_stackToolTechnologyStack[i] = "Cplus"
                        }
                        if(projectfind_stackToolTechnologyStack[i] == "FLASK") projectfind_stackToolTechnologyStack[i] = "Flask"
                        if(projectfind_stackToolTechnologyStack[i] == "PhotoShop") projectfind_stackToolTechnologyStack[i] = "PhotoShop"

                    }

                    for(i in 0..projectfind_projectfieldbtn.size-1){
                        if(projectfind_projectfieldbtn[i]=="예술/창작")
                            projectfind_projectfieldbtn[i] = "예술_창작"
                    }

                    // 2page를 위한 포지션
                    intent.putExtra("position",ArrayList<String>())

                    // 프로젝트 찾기
                    // 개발 툴
                    intent.putExtra("stackTool",projectfind_stackToolTechnologyStack)
                    // 프로젝트 방식
                    intent.putExtra("onoff",projectfind_onofftext)
                    // 지역
                    intent.putExtra("region",projectfind_regiontext)
                    // 기간
                    intent.putExtra("duration",projectfind_durationbtn)
                    // 관심분야
                    intent.putExtra("interest",projectfind_projectfieldbtn)
                }
                else if(page == 2){

                    memberfind_stackToolTechnologyStack.addAll(stackTooldeveloper)
                    memberfind_stackToolTechnologyStack.addAll(stackTooldesigner)


                    for(i in 0..memberfind_stackToolTechnologyStack.size-1){
                        if(memberfind_stackToolTechnologyStack[i] == "Html/CSS"){
                            memberfind_stackToolTechnologyStack[i] = "Html_CSS"
                        }

                        if(memberfind_stackToolTechnologyStack[i] == "React.JS"){
                            memberfind_stackToolTechnologyStack[i] = "React_js"
                        }
                        if(memberfind_stackToolTechnologyStack[i] == "After Effects"){
                            memberfind_stackToolTechnologyStack[i] = "After_Effects"
                        }
                        if(memberfind_stackToolTechnologyStack[i] == "C++"){
                            memberfind_stackToolTechnologyStack[i] = "Cplus"
                        }
                        if(memberfind_stackToolTechnologyStack[i] == "FLASK") memberfind_stackToolTechnologyStack[i] = "Flask"
                        if(memberfind_stackToolTechnologyStack[i] == "PhotoShop") memberfind_stackToolTechnologyStack[i] = "PhotoShop"


                    }


                    // 1page를 위한 기간
                    // 기간
                    intent.putExtra("duration",ArrayList<String>())
                    // 관심분야
                    intent.putExtra("interest",ArrayList<String>())
                    // 포지션
                    intent.putExtra("position",memberfind_selectpositionItems)
                    // 개발 툴
                    intent.putExtra("stackTool",memberfind_stackToolTechnologyStack)
                    // 프로젝트 방식
                    intent.putExtra("onoff",memberfind_onofftext)
                    // 지역
                    intent.putExtra("region",memberfind_regiontext)
                }

                intent.putExtra("page",page.toString())
                startActivity(intent)

            }

        }

    }


    private fun checkPageButton(): Boolean {
        if(page ==1){
            if (projectfind_stackToolTechnologyStack.size == 0) {
                ToastmakeTextPrint("툴 선택해주세요."); return false
            }
            if (projectfind_onofftext.equals("onoff")) {
                ToastmakeTextPrint("프로젝트 방식을 선택해주세요."); return false
            }
            if (projectfind_regiontext.equals("지역")&&projectfind_onofftext.equals("오프라인")) {
                ToastmakeTextPrint("지역을 입력해주세요."); return false
            }
            if (projectfind_durationbtn.size ==0) {
                ToastmakeTextPrint("프로젝트 예상 기간을 선택해주세요."); return false
            }
            if (projectfind_projectfieldbtn.size == 0) {
                ToastmakeTextPrint("분야를 선택해주세요."); return false
            }
        }else{
            if (memberfind_selectpositionItems.size == 0) {
                ToastmakeTextPrint("포지션을 선택해주세요."); return false
            }
            if (stackTooldeveloper.size ==0 && stackTooldesigner.size ==0 && memberfind_selectpositionItems.size != 0 ) {
                ToastmakeTextPrint("툴 선택해주세요."); return false
            }
            if (memberfind_onofftext.equals("onoff")){
                ToastmakeTextPrint("프로젝트 방식을 입력해주세요."); return false
            }
            if (memberfind_regiontext.equals("지역")&&memberfind_onofftext.equals("오프라인")) {
                ToastmakeTextPrint("지역을 입력해주세요."); return false
            }
        }

        return true
    }


    // 필터초기화, ConstraintLayout
    private fun onClearConstraintLayout(constraintlayout: ConstraintLayout) {

        for (i in 0 until constraintlayout.childCount) {
            val child: View = constraintlayout.getChildAt(i)
            // 해당 버튼에 효과 빼기
            if (child is Button) {
                child.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent
                )
                child.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonNoSelect
                    )
                )
            }
        }


        // 툴
        if(constraintlayout == filtersearch_projectstackbutton_constraintLayout
            || constraintlayout == filtersearch_projectstackdesignerbutton_constraintLayout
        ){
            // 스택
            projectfind_stackToolTechnologyStack.clear()

        }else if (constraintlayout == filtersearch_page2_projectstackbutton_constraintLayout
            || constraintlayout == filtersearch_page2_projectstackdesignerbutton_constraintLayout
        ){
            // 멤버찾기 기술 스택
            stackTooldeveloper.clear()
            stackTooldesigner.clear()
        }
    }

    // 필터초기화, LinearLayout
    private fun onClearLinearLayout(linearlayout: LinearLayout) {

        for (i in 0 until linearlayout.childCount) {
            val child: View = linearlayout.getChildAt(i)
            // 해당 버튼에 효과 빼기
            if (child is Button) {
                child.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent
                )
                child.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonNoSelect
                    )
                )
            }
        }

        // 프로젝트 방식, 지역
        if (linearlayout == filtersearch_projectonoff_linearlayout) {
            filtersearch_regions_textview.visibility = View.GONE
            filtersearch_regions_linearlayout.visibility = View.GONE
            projectcheckoffout = -1
            filtersearch_regions_spinner.text = "지역"
            projectfind_onofftext = "onoff"
        }// 기간
        else if(linearlayout==filtersearch_month_linearlayout){
            projectfind_durationbtn.clear()
        }// 지역
        else if(linearlayout==filtersearch_field_linearlayout
            || linearlayout == filtersearch_field2_linearlayout
        ){
            projectfind_projectfieldbtn.clear()
        }else if(linearlayout == filtersearch_page2_position_linearlayout){
            memberfind_selectpositionItems.clear()
            filtersearch_page2_projectstack_constraintLayout.visibility = View.GONE
            filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.GONE
            developercheck_index = 0
            designercheck_index = 0
            stackTooldeveloper.clear()
            stackTooldesigner.clear()

            for(idx in 0..10)developerbool[idx]=false
            for(idx in 0..11)designerbool[idx]=false

        }else if(linearlayout == filtersearch_page2_projectonoff_linearlayout){
            memberfind_onofftext = "onoff"
        }else if(linearlayout == filtersearch_page2_regions_linearlayout){
            memberfind_regiontext = "지역"
        }

    }


    // 프로젝트 찾기 화면
    private fun findproject() {

        if (user.equals("개발자")) {
            for (i in 0 until filtersearch_projectstackbutton_constraintLayout.childCount) {
                val child: View = filtersearch_projectstackbutton_constraintLayout.getChildAt(i)
                // 해당 버튼에 효과 주기
                if (child is Button) {
                    child.background = ContextCompat.getDrawable(
                        this@FilteringSearch,
                        R.drawable.radius_button_effect
                    )

                    child.setOnClickListener {
                        if (!projectfind_stackToolTechnologyStack.contains(child.text.toString())) {
                            child.background = ContextCompat.getDrawable(
                                this@FilteringSearch,
                                R.drawable.radius_background_transparent_select
                            )
                            child.setTextColor(
                                ContextCompat.getColor(
                                    this@FilteringSearch,
                                    R.color.colorButtonSelect
                                )
                            )
                            projectfind_stackToolTechnologyStack.add(child.text.toString())
                        } else {
                            child.background = ContextCompat.getDrawable(
                                this@FilteringSearch,
                                R.drawable.radius_button_effect
                            )
                            child.setTextColor(
                                ContextCompat.getColor(
                                    this@FilteringSearch,
                                    R.color.colorButtonNoSelect
                                )
                            )
                            projectfind_stackToolTechnologyStack.remove(child.text.toString())
                        }

                    }

                }


            }

        }else if(user.equals("디자이너")) {
            for (i in 0 until filtersearch_projectstackdesignerbutton_constraintLayout.childCount) {
                val child: View =
                    filtersearch_projectstackdesignerbutton_constraintLayout.getChildAt(i)
                // 해당 버튼에 효과 주기
                if (child is Button) {
                    child.background = ContextCompat.getDrawable(
                        this@FilteringSearch,
                        R.drawable.radius_button_effect
                    )

                    child.setOnClickListener {

                        if (!projectfind_stackToolTechnologyStack.contains(child.text.toString())) {
                            child.background = ContextCompat.getDrawable(
                                this@FilteringSearch,
                                R.drawable.radius_background_transparent_select
                            )
                            child.setTextColor(
                                ContextCompat.getColor(
                                    this@FilteringSearch,
                                    R.color.colorButtonSelect
                                )
                            )
                            projectfind_stackToolTechnologyStack.add(child.text.toString())
                        } else {
                            child.background = ContextCompat.getDrawable(
                                this@FilteringSearch,
                                R.drawable.radius_button_effect
                            )
                            child.setTextColor(
                                ContextCompat.getColor(
                                    this@FilteringSearch,
                                    R.color.colorButtonNoSelect
                                )
                            )
                            projectfind_stackToolTechnologyStack.remove(child.text.toString())
                        }

                    }

                }


            }
        }

        contentofproject()

    }


    var month = false
    var month2 = false
    var month3 = false

    var checkbool = Array(4){i->false}
    var checkbool2 = Array(4){i->false}
    val intentionbtn = arrayOfNulls<Button>(4)
    val intentionbtn2 = arrayOfNulls<Button>(4)

    var check = false

    private fun contentofproject() {

        if(check == false){
            intentionbtn[0] =filtersearch_selfdeveloper_textview
            intentionbtn[1] =filtersearch_hobby_textview
            intentionbtn[2] =filtersearch_economy_textview
            intentionbtn[3] =filtersearch_cook_textview

            intentionbtn2[0] =filtersearch_it_textview
            intentionbtn2[1] =filtersearch_rest_textview
            intentionbtn2[2] =filtersearch_health_textview
            intentionbtn2[3] =filtersearch_holiday_textview
            check = true

        }


        //프로젝트 방식
        filtersearch_regions_spinner.setItems(regions)
        filtersearch_regions_spinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            projectfind_regiontext = newItem
        }

        projectfind_onoffbtn[0] = findViewById(R.id.filtersearch_offline_button)
        projectfind_onoffbtn[1] = findViewById(R.id.filtersearch_online_button)
        projectfind_onoffbtn[0]?.setOnClickListener {


            projectfind_onoffbtn[0]?.let { it1 ->
                this.onClickProjectBtn(
                    it1,
                    0, 2
                )
            }
        }
        projectfind_onoffbtn[1]?.setOnClickListener {

           
            projectfind_onoffbtn[1]?.let { it2 ->
                this.onClickProjectBtn(
                    it2,
                    1, 2
                )
            }
        }


        // 프로젝트 예상기간
        filtersearch_month_button.setOnClickListener {
            if(month == false){
                filtersearch_month_button.background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent_select)
                filtersearch_month_button.setTextColor(ContextCompat.getColor(this@FilteringSearch, R.color.colorButtonSelect))
                month = true
                projectfind_durationbtn.add(filtersearch_month_button.text.toString())
            }
            else{
                filtersearch_month_button.background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent)
                filtersearch_month_button.setTextColor(ContextCompat.getColor(this@FilteringSearch, R.color.colorButtonNoSelect))
                month = false
                projectfind_durationbtn.remove(filtersearch_month_button.text.toString())
            }
        }

        filtersearch_month2_button.setOnClickListener {
            if(month2 == false){
                filtersearch_month2_button.background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent_select)
                filtersearch_month2_button.setTextColor(ContextCompat.getColor(this@FilteringSearch, R.color.colorButtonSelect))
                month2 = true
                projectfind_durationbtn.add(filtersearch_month2_button.text.toString())
            }
            else{
                filtersearch_month2_button.background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent)
                filtersearch_month2_button.setTextColor(ContextCompat.getColor(this@FilteringSearch, R.color.colorButtonNoSelect))
                month2 = false
                projectfind_durationbtn.remove(filtersearch_month2_button.text.toString())
            }
        }

        filtersearch_month3_button.setOnClickListener {
            if(month3 == false){
                filtersearch_month3_button.background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent_select)
                filtersearch_month3_button.setTextColor(ContextCompat.getColor(this@FilteringSearch, R.color.colorButtonSelect))
                month3 = true
                projectfind_durationbtn.add(filtersearch_month3_button.text.toString())
            }
            else{
                filtersearch_month3_button.background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent)
                filtersearch_month3_button.setTextColor(ContextCompat.getColor(this@FilteringSearch, R.color.colorButtonNoSelect))
                month3 = false
                projectfind_durationbtn.remove(filtersearch_month3_button.text.toString())
            }
        }



        // 관심분야
        for (i in 0 until filtersearch_field_linearlayout.childCount) {
            filtersearch_field_linearlayout[i].setOnClickListener {
                if(checkbool[i] == false){
                    filtersearch_field_linearlayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent_select)
                    intentionbtn[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonSelect))
                    checkbool[i] = true
                    projectfind_projectfieldbtn.add(intentionbtn[i]?.text.toString())
                }else{
                    filtersearch_field_linearlayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent)
                    intentionbtn[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))
                    checkbool[i] = false
                    projectfind_projectfieldbtn.remove(intentionbtn[i]?.text.toString())
                }
            }

        }

        for (i in 0 until filtersearch_field2_linearlayout.childCount) {
            filtersearch_field2_linearlayout[i].setOnClickListener {
                if(checkbool2[i] == false){
                    filtersearch_field2_linearlayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent_select)
                    intentionbtn2[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonSelect))
                    checkbool2[i] = true
                    projectfind_projectfieldbtn.add(intentionbtn2[i]?.text.toString())
                }else{
                    filtersearch_field2_linearlayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent)
                    intentionbtn2[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))
                    checkbool2[i] = false
                    projectfind_projectfieldbtn.remove(intentionbtn2[i]?.text.toString())
                }
            }

        }

    }

    // 멤버 찾기 화면
    private fun findmember() {
        membercontents()
    }


    var developerbool = Array(11){i->false}
    var designerbool = Array(12){i->false}
    var memberdevelpoer = arrayOfNulls<Button>(11)
    var memberdesigner = arrayOfNulls<Button>(12)


    var membercheckbool = Array(4){i->false}
    var membercheckbool2 = Array(4){i->false}
    val memberintentionbtn = arrayOfNulls<Button>(4)
    val memberintentionbtn2 = arrayOfNulls<Button>(4)

    var memberintentioncheck = false



    private fun membercontents() {

        if(memberintentioncheck  == false){



            memberdevelpoer[0] =filtersearch_page2_java_Button
            memberdevelpoer[1] =filtersearch_page2_cpluse_Button
            memberdevelpoer[2] =filtersearch_page2_python_Button
            memberdevelpoer[3] =filtersearch_page2_javascript_Button
            memberdevelpoer[4] =filtersearch_page2_django_Button
            memberdevelpoer[5] =filtersearch_page2_html_css_Button
            memberdevelpoer[6] =filtersearch_page2_swift_Button
            memberdevelpoer[7] =filtersearch_page2_kotlin_Button
            memberdevelpoer[8] =filtersearch_page2_spring_Button
            memberdevelpoer[9] =filtersearch_page2_flask_Button
            memberdevelpoer[10] =filtersearch_page2_reactjs_Button

            memberdesigner[0] = filtersearch_page2_photoshop_Button
            memberdesigner[1] = filtersearch_page2_illustrator_Button
            memberdesigner[2] = filtersearch_page2_xd_Button
            memberdesigner[3] = filtersearch_page2_sketch_Button
            memberdesigner[4] = filtersearch_page2_figma_Button
            memberdesigner[5] = filtersearch_page2_Principle_Button
            memberdesigner[6] = filtersearch_page2_protopie_Button
            memberdesigner[7] = filtersearch_page2_after_effects_Button
            memberdesigner[8] = filtersearch_page2_premiere_Button
            memberdesigner[9] = filtersearch_page2_Indesign_Button
            memberdesigner[10] = filtersearch_page2_c4d_Button
            memberdesigner[11] = filtersearch_page2_Zeplin_Button

            memberintentioncheck = true
        }


        // 포지션, 개발, 디자이너 툴
        // 개발자 버튼 클릭했을 때
        filtersearch_page2_developer_Button.setOnClickListener {

            filtersearch_page2_projectstack_constraintLayout.visibility = View.VISIBLE
            filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.GONE

            if(designercheck_index == 0){
                filtersearch_page2_designer_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent
                )
                filtersearch_page2_designer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))


                designer_btn = false
                memberfind_selectpositionItems.remove(filtersearch_page2_designer_Button.text.toString())
            }

            // 개발자 툴이 닫혀있을 때
            if(!developer_btn){
                filtersearch_page2_developer_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent_select
                )
                filtersearch_page2_developer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonSelect))

                filtersearch_page2_projectstack_constraintLayout.visibility = View.VISIBLE
                filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.GONE

                // 개발자 툴 오픈
                for (i in 0 until filtersearch_page2_projectstackbutton_constraintLayout.childCount) {
                    filtersearch_page2_projectstackbutton_constraintLayout[i].setOnClickListener {
                        if(developerbool[i] == false){
                            filtersearch_page2_projectstackbutton_constraintLayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent_select)
                            memberdevelpoer[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonSelect))
                            developerbool[i] = true
                            developercheck_index++
                            stackTooldeveloper.add(memberdevelpoer[i]?.text.toString())
                        }else{
                            filtersearch_page2_projectstackbutton_constraintLayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent)
                            memberdevelpoer[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))
                            developerbool[i] = false
                            developercheck_index--
                            stackTooldeveloper.remove(memberdevelpoer[i]?.text.toString())
                        }
                    }

                }
                memberfind_selectpositionItems.add(filtersearch_page2_developer_Button.text.toString())
                developer_btn = true
            }else if(developer_btn && developercheck_index == 0){
                // 개발자 버튼을 눌릴 때 : 개발자 툴에 아무런 버튼 입력 없을 때 버튼 효과 빼기
                filtersearch_page2_developer_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent
                )
                filtersearch_page2_developer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))

                memberfind_selectpositionItems.remove(filtersearch_page2_developer_Button.text.toString())
                developer_btn = false
                filtersearch_page2_projectstack_constraintLayout.visibility = View.GONE
                filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.GONE
            }

        }


        // 디자이너 버튼 클릭했을 때
        filtersearch_page2_designer_Button.setOnClickListener {
            filtersearch_page2_projectstack_constraintLayout.visibility = View.GONE
            filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.VISIBLE
            if (developercheck_index == 0) {
                filtersearch_page2_developer_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent
                )
                filtersearch_page2_developer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))

                developer_btn = false
                memberfind_selectpositionItems.remove(filtersearch_page2_developer_Button.text.toString())
            }


            // 디자이너 툴이 닫혀있을 때
            if(!designer_btn){
                filtersearch_page2_designer_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent_select
                )
                filtersearch_page2_designer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonSelect))

                filtersearch_page2_projectstack_constraintLayout.visibility = View.GONE
                filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.VISIBLE

                // 디자이너 툴 오픈

                for (i in 0 until filtersearch_page2_projectstackdesignerbutton_constraintLayout.childCount) {
                    filtersearch_page2_projectstackdesignerbutton_constraintLayout[i].setOnClickListener {
                        if(designerbool[i] == false){
                            filtersearch_page2_projectstackdesignerbutton_constraintLayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent_select)
                            memberdesigner[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonSelect))
                            designerbool[i] = true
                            designercheck_index++
                            stackTooldesigner.add(memberdesigner[i]?.text.toString())
                        }else{
                            filtersearch_page2_projectstackdesignerbutton_constraintLayout[i].background = ContextCompat.getDrawable(this@FilteringSearch, R.drawable.radius_background_transparent)
                            memberdesigner[i]?.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))
                            designerbool[i] = false
                            designercheck_index--
                            stackTooldesigner.remove(memberdesigner[i]?.text.toString())
                        }
                    }

                }

                designer_btn = true
                memberfind_selectpositionItems.add( filtersearch_page2_designer_Button.text.toString())
            }else if(designer_btn && designercheck_index == 0){
                // 디자이너 버튼을 눌릴 때 : 디자이너 툴에 아무런 버튼 입력 없을 때 버튼 효과 빼기
                filtersearch_page2_designer_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent
                )
                filtersearch_page2_designer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))

                designer_btn = false
                filtersearch_page2_projectstack_constraintLayout.visibility = View.GONE
                filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.GONE
                memberfind_selectpositionItems.remove( filtersearch_page2_designer_Button.text.toString())
            }

        }

        filtersearch_page2_planner_Button.setOnClickListener{
            filtersearch_page2_projectstack_constraintLayout.visibility = View.GONE
            filtersearch_page2_projectstackdesigner_constraintLayout.visibility = View.GONE

            // 개발자, 디자이너 툴 아무런 입력이 없을 때
            if(designercheck_index == 0 || developercheck_index == 0){

                if(developercheck_index == 0){
                    filtersearch_page2_developer_Button.background = ContextCompat.getDrawable(
                        this@FilteringSearch,
                        R.drawable.radius_background_transparent
                    )
                    filtersearch_page2_developer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))


                    developer_btn = false
                    memberfind_selectpositionItems.remove(filtersearch_page2_developer_Button.text.toString())


                }

                if(designercheck_index == 0){
                    filtersearch_page2_designer_Button.background = ContextCompat.getDrawable(
                        this@FilteringSearch,
                        R.drawable.radius_background_transparent
                    )
                    filtersearch_page2_designer_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))

                    designer_btn = false
                    memberfind_selectpositionItems.remove( filtersearch_page2_designer_Button.text.toString())

                }

            }


            // 기획자 버튼 닫혀있을 때
            if(!planner_btn){
                filtersearch_page2_planner_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent_select
                )
                filtersearch_page2_planner_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonSelect))

                planner_btn = true
                memberfind_selectpositionItems.add(filtersearch_page2_planner_Button.text.toString())


            }else{  // 기획자 버튼 열려있을 때
                filtersearch_page2_planner_Button.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent
                )
                filtersearch_page2_planner_Button.setTextColor(ContextCompat.getColor(this@FilteringSearch,R.color.colorButtonNoSelect))

                planner_btn = false
                memberfind_selectpositionItems.remove(filtersearch_page2_planner_Button.text.toString())
            }


        }


        // 프로젝트 방식

        filtersearch_page2_regions_spinner.setItems(regions)
        filtersearch_page2_regions_spinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            memberfind_regiontext = newItem
        }

        // 프로젝트 방식

        memberfind_onoffbtn[0] = findViewById(R.id.filtersearch_page2_offline_button)
        memberfind_onoffbtn[1] = findViewById(R.id.filtersearch_page2_online_button)
        memberfind_onoffbtn[0]?.setOnClickListener {

            memberfind_onoffbtn[0]?.let { it1 ->
                this.onClickMemberBtn(
                    it1,
                    0, 2
                )
            }
        }
        memberfind_onoffbtn[1]?.setOnClickListener {

            memberfind_onoffbtn[1]?.let { it2 ->
                this.onClickMemberBtn(
                    it2,
                    1, 2
                )
            }
        }



    }

    private fun ToastmakeTextPrint(word: String) {
        Toast.makeText(this, word, Toast.LENGTH_SHORT).show()
    }


    // FilteringSearch에서 사용된 코드 이용
    var projectcheckoffout: Int = -1
    var projectcheckoff:Int = -1

    private fun onClickProjectBtn(v: View, index: Int, btnsize: Int) {
        var id = v.id

        var btn = R.id.filtersearch_offline_button

        projectcheckoff = btnsize - 1

        // 프로젝트 방식
        if (id == R.id.filtersearch_offline_button || id == R.id.filtersearch_online_button) {


            // 선택된 상태라면 체크 취소하기
            if (index == projectcheckoffout) {

                // 오프라인 부분이면 지역부분 빼기
                if (index == 0) {
                    filtersearch_regions_textview.visibility = View.GONE
                    filtersearch_regions_linearlayout.visibility = View.GONE
                    filtersearch_regions_spinner.text = "지역"
                }
                projectfind_onoffbtn[index]?.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_button_effect
                )
                projectfind_onoffbtn[index]?.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonNoSelect
                    )
                )

                // 초기화
                projectfind_onofftext = "onoff"
                projectcheckoffout = -1
            } else {

                if (index == 0) {
                    filtersearch_regions_textview.visibility = View.VISIBLE
                    filtersearch_regions_linearlayout.visibility = View.VISIBLE

                } else {
                    filtersearch_regions_textview.visibility = View.GONE
                    filtersearch_regions_linearlayout.visibility = View.GONE
                    filtersearch_regions_spinner.text = "지역"
                }

                projectfind_onoffbtn[index]?.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent_select
                )
                projectfind_onoffbtn[index]?.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonSelect
                    )
                )


                projectfind_onoffbtn[projectcheckoff - index]?.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_button_effect
                )
                projectfind_onoffbtn[projectcheckoff - index]?.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonNoSelect
                    )
                )

                projectfind_onofftext = projectfind_onoffbtn[index]?.text.toString()
                projectcheckoffout = index
            }

        }


    }


    // FilteringSearch에서 사용된 코드 이용
    var membercheckoffout: Int = -1
    var membercheckoff:Int = -1


    private fun onClickMemberBtn(v: View, index: Int, btnsize: Int) {
        var id = v.id

        var btn = R.id.filtersearch_page2_offline_button

        membercheckoff = btnsize - 1
        // 프로젝트 방식
        if (id == R.id.filtersearch_page2_offline_button || id == R.id.filtersearch_page2_online_button) {


            // 선택된 상태라면 체크 취소하기
            if (index == membercheckoffout) {

                // 오프라인 부분이면 지역부분 빼기
                if (index == 0) {
                    filtersearch_page2_regions_textview.visibility = View.GONE
                    filtersearch_page2_regions_linearlayout.visibility = View.GONE
                    filtersearch_page2_regions_spinner.text = "지역"
                }
                memberfind_onoffbtn[index]?.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_button_effect
                )
                memberfind_onoffbtn[index]?.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonNoSelect
                    )
                )

                // 초기화
                memberfind_onofftext = "onoff"
                membercheckoffout = -1
            } else {

                if (index == 0) {
                    filtersearch_page2_regions_textview.visibility = View.VISIBLE
                    filtersearch_page2_regions_linearlayout.visibility = View.VISIBLE

                } else {
                    filtersearch_page2_regions_textview.visibility = View.GONE
                    filtersearch_page2_regions_linearlayout.visibility = View.GONE
                    filtersearch_page2_regions_spinner.text = "지역"
                }

                memberfind_onoffbtn[index]?.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_background_transparent_select
                )
                memberfind_onoffbtn[index]?.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonSelect
                    )
                )


                memberfind_onoffbtn[membercheckoff - index]?.background = ContextCompat.getDrawable(
                    this@FilteringSearch,
                    R.drawable.radius_button_effect
                )
                memberfind_onoffbtn[membercheckoff - index]?.setTextColor(
                    ContextCompat.getColor(
                        this@FilteringSearch,
                        R.color.colorButtonNoSelect
                    )
                )

                memberfind_onofftext = memberfind_onoffbtn[index]?.text.toString()
                membercheckoffout = index
            }

        }

    }
}
