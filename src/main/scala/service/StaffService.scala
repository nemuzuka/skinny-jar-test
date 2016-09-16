package service

import model.Staff
import scalikejdbc.DBSession

/**
  * Staffに関するService.
  */
trait StaffService {

  /**
    * staff登録.
    * @param name 名称
    * @param session Session
    * @return 生成ID
    */
  def createStaff(name:String)(implicit session:DBSession) = {
    Staff.create(Staff(id = -1L, staffName = name))
  }

  /***
    * Staff取得.
    * @param id ID
    * @return 該当データ(存在しない場合、None)
    */
  def getStaff(id:Long)(implicit session:DBSession) = {
    Staff.findById(id)
  }
}

object StaffService extends StaffService