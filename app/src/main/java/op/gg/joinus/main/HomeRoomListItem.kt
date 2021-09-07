package op.gg.joinus.main

import op.gg.joinus.model.RoomInfo
import op.gg.joinus.util.diffCalendar
import op.gg.joinus.util.joinLog
import java.io.IOException
import java.util.*

data class HomeRoomListItem (
    var room: RoomInfo
){
    lateinit var title:String
    lateinit var idNTime:String
    lateinit var matchTime:String
    lateinit var numPeople:String
    lateinit var imgGame:String
    init{
        with(this){

            // set title
            title = room.room_name

            //set idNTime
            idNTime = ""
            for (n in room.user_list){
                if (n.pk == room.leader_pk){
                    idNTime += n.nickname
                }
            }
            val diffDateStr = diffCalendar("",room.created_at)
            idNTime += if (diffDateStr == "1일"){
                " | 어제"
            } else{
                " | $diffDateStr 전"
            }

            //set matchTime
            if (room.is_start == 1){
                matchTime = "매칭 완료"
            }
            else{
                matchTime = if(diffCalendar(room.start_date,"") == ""){
                    "오늘 시작"
                }else{
                    diffCalendar(room.start_date,"") + " 뒤"
                }
            }

            //set numPeople
            numPeople =  room.now_people_cnt.toString() + "/" + room.people_number.toString()
            //set imgGame
            when(room.game_name){
                "league of legends"->{
                    imgGame = "data:image/jpeg;base64,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"
                }
                else ->{

                }
            }
        }
    }
}