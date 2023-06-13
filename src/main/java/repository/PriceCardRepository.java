package repository;

import model.Chart;
import model.PriceCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceCardRepository extends JpaRepository<PriceCard, Long> {
}
