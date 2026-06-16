package be.alkaram.mosquee.repository;

import be.alkaram.mosquee.model.ConfigSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigSiteRepository extends JpaRepository<ConfigSite, Long> {
}