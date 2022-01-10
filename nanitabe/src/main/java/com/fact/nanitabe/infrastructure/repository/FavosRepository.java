package com.fact.nanitabe.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.fact.nanitabe.infrastructure.entity.Favos;

public interface FavosRepository extends JpaRepository<Favos, Integer> {

	// FavosテーブルからuserIdとstoreIdが一致するレコードのカウント
	@Query("SELECT COUNT(f) FROM Favos f WHERE f.userId = :userId AND storeId = :storeId")
	int countFavo(Integer userId, String storeId);
	
	// favos1テーブルからuserIdとstoreIdが一致するレコードの削除(お気に入り削除)
	@Modifying
	@Query("DELETE FROM Favos f WHERE f.userId = :userId AND f.storeId = :storeId")
	void deleteByUserIdAndStoreId(Integer userId, String storeId);
	
	// お気に入りリストを取得する
	@Query("SELECT f FROM Favos f INNER JOIN f.user WHERE f.user.userId = :userId")
	List<Favos> getFavoList(Integer userId) throws Exception;
}
