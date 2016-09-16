package model

import skinny.orm._
import scalikejdbc._

/**
  * 社員Entity.
  * @param id ID
  * @param staffName 社員名
  */
case class Staff(
  id: Long,
  staffName: String
)

/**
  * 実装クラス.
  */
object Staff extends SkinnyCRUDMapper[Staff] {
  override lazy val tableName = "staff"
  override lazy val defaultAlias = createAlias("s")

  override def extract(rs: WrappedResultSet, rn: ResultName[Staff]): Staff = new Staff(
    id = rs.get(rn.id),
    staffName = rs.get(rn.staffName)
  )

  /**
    * 登録.
    * @param entity 対象Entity
    * @param session Session
    * @return 生成ID
    */
  def create(entity: Staff)(implicit session: DBSession): Long = {
    Staff.createWithAttributes(
      'staffName -> entity.staffName
    )
  }

  /**
    * 更新.
    * @param entity 対象Entity
    * @param session Session
    * @return ID
    */
  def update(entity: Staff)(implicit session: DBSession): Long = {
    Staff.updateById(entity.id).withAttributes(
      'staffName -> entity.staffName
    )
  }

  /**
    * LIKE検索.
    * @param staffName 検索文字列(前方一致)
    * @param session Session
    * @return 該当データ
    */
  def findByStaffName(staffName: String)(implicit session: DBSession): Seq[Staff] = {
    val s = Staff.syntax("s")
    withSQL {
      select(s.result.*)
        .from(Staff as s)
        .where.like(s.staffName, LikeConditionEscapeUtil.beginsWith(staffName))
        .orderBy(s.id)
    }.map { rs =>
      Staff.extract(rs, s.resultName)
    }.list.apply
  }
}
