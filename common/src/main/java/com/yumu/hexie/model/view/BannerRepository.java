package com.yumu.hexie.model.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.yumu.hexie.model.ModelConstant;

public interface BannerRepository extends JpaRepository<Banner, Long> {

	@Query("from Banner m where m.status="+ModelConstant.BANNER_STATUS_VALID+" and ((m.regionType=0) "
			+ "or (m.regionType=1 and m.regionId=?1) "
			+ "or (m.regionType=2 and m.regionId=?2) "
			+ "or (m.regionType=3 and m.regionId=?3) "
			+ "or (m.regionType=4 and m.regionId=?4)) "
			+ "and m.bannerType=?5 "
			+ "order by m.sortNo asc,m.id desc ")
	public List<Banner> queryByBannerTypeAndUser(long provinceId,long cityId,long countyId,long xiaoquId, int bannerType);
	
	@Query(value="SELECT b.* FROM banner b JOIN region r ON b.regionId = r.id WHERE r.name = ?1 AND b.bannerType = ?2 AND b.status ="+ModelConstant.BANNER_STATUS_VALID,nativeQuery = true)
	public List<Banner> findByRegion(String name, int bannerType);
	
	@Query(value="SELECT b.* FROM banner b JOIN region r ON b.regionId = r.parentId WHERE r.name = ?1 AND b.bannerType = ?2 AND b.status ="+ModelConstant.BANNER_STATUS_VALID,nativeQuery = true)
	public List<Banner> findByRegionfather(String name, int bannerType);
}
