package cash.super_.platform.repository;

import cash.super_.platform.model.parkinglot.Marketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceRepository extends JpaRepository<Marketplace, Long> {

    boolean existsDistinctByIdAndAppVersionLessThanEqual(Long marketplaceId, Double appVersion);
}
