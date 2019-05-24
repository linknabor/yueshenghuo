package com.yumu.hexie.service.page;

import java.util.List;

import com.yumu.hexie.model.user.User;
import com.yumu.hexie.model.view.Banner;

public interface PageConfigService {
	public List<Banner> queryBannerType(User user, int bannerType);
	public List<Banner> queryBannerCoordinate(int bannerType,String coordinate);
	public List<Banner> queryBannerName(int bannerType, String name);
    public String findByTempKey(String key);
}
