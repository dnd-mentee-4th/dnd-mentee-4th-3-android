package com.thisteampl.jackpot.main.user

import androidx.room.Entity
import androidx.room.PrimaryKey

//import java.util.*

/*
    Room에서의 개념
    1. Entity : 디비 테이블
    2. PrimaryKey : 데이터 구분을 위한 키, autoGenerate 되며 - 1부터 만들어진다.
    Room을 이용해 User 테이블 생성이 된다.
    서버에서 User의 정보를 받아와서 저장하는 식.
 */

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) var idx: Long, // autoGenerate 알아서 id를 1씩 증가시켜준다.
    val id: String,
    var password: String,
    val name: String, // 0 : 현재, 과거 1 : 미래 2 : 메모 (time 0, 1은 이제 같은 의미, 2인지 아닌지가 중요)
    var contact: String, // True: 고정, False : 안함
    var region: Int,
    var job: Int,
    //var Stack: ???
    val career: Int,
    var introduce: String
)
