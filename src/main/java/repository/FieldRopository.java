package repository;

import model.Field;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRopository extends JpaRepository<Field, Long> {
}
