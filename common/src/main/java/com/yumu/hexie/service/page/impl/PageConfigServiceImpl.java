package com.yumu.hexie.service.page.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.yumu.hexie.integration.baidu.BaiduMapUtil;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.Banner;
import com.yumu.hexie.model.view.BannerRepository;
import com.yumu.hexie.model.view.PageConfigView;
import com.yumu.hexie.model.view.PageConfigViewRepository;
import com.yumu.hexie.service.page.PageConfigService;

@Service("pageConfigService")
public class PageConfigServiceImpl implements PageConfigService {

    @Inject
    private PageConfigViewRepository pageConfigViewRepository;
	@Inject
	private BannerRepository bannerRepository;

	@Override
	public List<Banner> queryBannerType(User user, int bannerType) {
		if(user.getProvinceId() != 0){
			return bannerRepository.queryByBannerTypeAndUser(user.getProvinceId(), user.getCityId(), user.getCountyId(), user.getXiaoquId(), bannerType);
		}else{
			return bannerRepository.queryByBannerTypeAndUser(19, user.getCityId(), user.getCountyId(), user.getXiaoquId(), bannerType);		
		}

	}
	
    @Override
    public String findByTempKey(String key) {
        PageConfigView v = pageConfigViewRepository.findByTempKey(key);
        if(v != null) {
            return v.getPageConfig();
        }
        return "";
    }


	@Override
	public List<Banner> queryBannerCoordinate(int bannerType, String coordinate) {
		// TODO Auto-generated method stub
		coordinate = BaiduMapUtil.findByCoordinateGetBaidu(coordinate);
		String name = BaiduMapUtil.findByBaiduGetCity(coordinate);
		List<Banner> list = bannerRepository.findByRegion(name, bannerType);
		if(list.isEmpty()||list.size()<=0) {//如果没有默认取上海市的
			list = bannerRepository.findByRegion("上海市", bannerType);
		}
		return list;
	}
	
	@Override
	public List<Banner> queryBannerName(int bannerType, String name) {
		// TODO Auto-generated method stub
		List<Banner> list = bannerRepository.findByRegion(name, bannerType);
		if(list.isEmpty()||list.size()<=0) {//如果没有默认取上海市的
			list = bannerRepository.findByRegion("上海市", bannerType);
		}
		return list;
	}
}
