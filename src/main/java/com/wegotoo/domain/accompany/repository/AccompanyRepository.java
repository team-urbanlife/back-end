package com.wegotoo.domain.accompany.repository;

import com.wegotoo.domain.accompany.Accompany;
import com.wegotoo.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccompanyRepository extends JpaRepository<Accompany, Long>, AccompanyRepositoryCustom {

    @Query("SELECT a FROM Accompany a JOIN FETCH a.user WHERE a.id = :id")
    Optional<Accompany> findByIdWithUser(@Param("id") Long id);

    Optional<Accompany> findByIdAndUser(Long accompanyId, User user);
}
