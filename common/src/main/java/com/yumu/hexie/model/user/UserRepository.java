package com.yumu.hexie.model.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
	
	public List<User> findByOpenid(String openid);
	public List<User> findByShareCode(String shareCode);
	
	@Modifying
	@Transactional
	@Query(value = "update user set wuyeId = ?1 where id = ?2 ", nativeQuery = true)
	public int updateUserWuyeId(String wuyeId, long id);
}
